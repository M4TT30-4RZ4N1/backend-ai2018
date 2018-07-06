package it.polito.ai.project.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import it.polito.ai.project.service.PositionService;
import it.polito.ai.project.service.UserArchiveService;
import it.polito.ai.project.service.model.TimedPosition;
import it.polito.ai.project.service.model.UserArchive;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;


@RestController
@RequestMapping("/user")
public class RestSellerController {
    private final UserArchiveService userArchiveService;

    @Autowired
    public RestSellerController(UserArchiveService userArchiveService) {
        this.userArchiveService = userArchiveService;
    }

    @RequestMapping(value="/archives", method = RequestMethod.GET, params = {"ownership"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    ResponseEntity<?> getArchivesHateos(@ApiParam(value = "Select self archive, purchased archive or all",allowableValues = "self,purchased,all",defaultValue = "all")
                                  @RequestParam(value = "ownership",defaultValue = "all") String ownership) {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UserArchive> resp=new ArrayList<UserArchive>();
        if(ownership.equals("all") || ownership.equals("self")){
            resp.addAll(userArchiveService.getOwnArchivesWithoutContent(user));
        }
        if(ownership.equals("all") || ownership.equals("purchased")){
            resp.addAll(userArchiveService.getPurchasedArchives(user));
        }
        resp.forEach(a -> a.setContent(null));
        List<Resource<UserArchive>> hateosArchives = resp.stream()
                                                        .map(a -> new Resource<>(a, getArchiveLinks(a.getFilename(), ownership)))
                                                        .collect(Collectors.toList());
        return ResponseEntity
                    .ok()
                    .body(hateosArchives);
    }

    private List<Link> getArchiveLinks(String filename, String ownership) {

        return Arrays.asList(linkTo(methodOn(RestSellerController.class).downloadArchive(null,filename)).withSelfRel(),
                                linkTo(methodOn(RestSellerController.class).getArchivesHateos(ownership)).withRel("archives"));
    }

    @RequestMapping(value="/archives/{filename}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    List<TimedPosition> downloadArchive(HttpServletResponse response, @PathVariable("filename") String filename) {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userArchiveService.downloadArchive(user,filename);
    }
    /*@RequestMapping(value = "/archives", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody
    void createArchive(@ApiParam(value = "List of valid Timed Position provided by user")@RequestBody List<TimedPosition> positions) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // se nel content dell'archivio ci mettiamo il riferimento alle timed position, questo serve
        //for(TimedPosition position : positions)
         //   positionService.addToDB(username, position);
        userArchiveService.addArchive(username, positions);
    }
*/
    @RequestMapping(value = "/archives", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody
    ResponseEntity<?> createArchive(@RequestParam("file") MultipartFile file) {
        if(!file.getContentType().equals("application/json"))
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File format not supported");
        try {
            String json = new String(file.getBytes());
            ObjectMapper objectMapper = new ObjectMapper();
            List<TimedPosition> positions = objectMapper.readValue(json, new TypeReference<List<TimedPosition>>(){});
            String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            /* se nel content dell'archivio ci mettiamo il riferimento alle timed position, questo serve
            for(TimedPosition position : positions)
                positionService.addToDB(username, position);*/
            userArchiveService.addArchive(username, positions);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }

    @RequestMapping(value="/ziparchive", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    void downloadArchives(@RequestBody List<String> filenames, HttpServletResponse response) {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ByteArrayOutputStream zip = null;
        try {
            userArchiveService.createZip(user, filenames, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
            throw new AccessDeniedException("Server Error");
        }
    }

    @RequestMapping(value="/archives", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    void deleteArchives(@ApiParam("The name of files that will be deleted")@RequestBody List<String> filenames) {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userArchiveService.deleteArchives(user, filenames);
    }
    @RequestMapping(value="/archives/{filename}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    void deleteArchive(@ApiParam("The name of file that will be deleted")@PathVariable("filename") String filename) {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userArchiveService.deleteArchive(user, filename);
    }


}
