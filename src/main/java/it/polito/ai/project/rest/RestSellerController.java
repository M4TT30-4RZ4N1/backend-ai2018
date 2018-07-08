package it.polito.ai.project.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import it.polito.ai.project.service.UserArchiveService;
import it.polito.ai.project.service.model.TimedPosition;
import it.polito.ai.project.service.model.UserArchive;
import it.polito.ai.project.service.model.UserArchives;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

/**
 * This class is related to the RestSellerController, which includes the methods that are available to the seller.
 */
@RestController
@RequestMapping("/user")
public class RestSellerController {
    private final UserArchiveService userArchiveService;

    /**
     * This method allows to generate a RestSellerController.
     * @param userArchiveService
     */
    @Autowired
    public RestSellerController(UserArchiveService userArchiveService) {
        this.userArchiveService = userArchiveService;
    }
    @ApiOperation(value = "View a list of available products", response = UserArchives.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value="/archives", method = RequestMethod.GET, params = {"ownership","page","size"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    UserArchives getArchivesHateos(@ApiParam(value = "Select self archive, purchased archive or all",allowableValues = "self,purchased,all",defaultValue = "all")
                                  @RequestParam(value = "ownership",defaultValue = "all") String ownership,
                                        @ApiParam(value = "Select the page of results",defaultValue = "0")
                                  @RequestParam(value = "page",defaultValue = "0") int page,
                                        @ApiParam(value = "Select the size of the page, pay attention to the fact that if you select all in ownership the result archive may be double sized"
                                                ,defaultValue = "10")
                                  @RequestParam(value = "size",defaultValue = "10") int size) {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UserArchive> resp=new ArrayList<UserArchive>();
        if(ownership.equals("all")||ownership.equals("self")){
            resp.addAll(userArchiveService.getOwnArchivesWithoutContent(user,page,size));
        }
        if(ownership.equals("all")||ownership.equals("purchased")){
            resp.addAll(userArchiveService.getPurchasedArchives(user,page,size));
        }
        //resp.forEach(a -> a.setContent(null));
        List<Resource<UserArchive>> hateoasArchive= resp.stream().map(this::createUserArchiveResoure).collect(Collectors.toList());
        UserArchives archives=new UserArchives(hateoasArchive);
        archives.add(linkTo(methodOn(RestSellerController.class).getArchivesHateos(ownership, page, size)).withSelfRel());
        if(page-1>=0) {
            archives.add(linkTo(methodOn(RestSellerController.class).getArchivesHateos(ownership, page - 1, size)).withRel("previous"));
        }
        if(resp.size()>=size) {
            archives.add(linkTo(methodOn(RestSellerController.class).getArchivesHateos(ownership, page + 1, size)).withRel("next"));
        }
        return archives;
    }

    /**
     * This method allows to generate a new UserArchive.
     * @param archive
     */
    private Resource<UserArchive> createUserArchiveResoure(UserArchive archive){
        return new Resource<UserArchive>(archive,linkTo(methodOn(RestSellerController.class).downloadArchive(null, archive.getFilename())).withSelfRel());
    }

    /**
     * This method allows to download an archive.
     * @param response
     * @param filename
     * @return the list of TimedPositions of the selected archive
     */
    @RequestMapping(value="/archives/{filename}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    List<TimedPosition> downloadArchive(HttpServletResponse response, @PathVariable("filename") String filename) {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return userArchiveService.downloadArchive(user,filename);
        } catch (NotFoundException e) {
            throw new ResourceNotFoundException("Archive not found");
        }
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

    /**
     * This method allows to generate a new UserArchive and to upload it.
     * @param file
     * @return the user archive
     */
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "File created with metadata"),
            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
    })
    @RequestMapping(value = "/upload", method = RequestMethod.POST,produces = "application/json")
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody
    Resource<UserArchive> createArchiveByUpload(@ApiParam("File with list of timed positions")@RequestParam("file") MultipartFile file) {
        //if(!file.getContentType().equals("application/json"))
           // throw new UnsupportedMediaTypeStatusException("File format not supported");
        try {
            String json = new String(file.getBytes());
            ObjectMapper objectMapper = new ObjectMapper();
            List<TimedPosition> positions = objectMapper.readValue(json, new TypeReference<List<TimedPosition>>(){});
            String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            /* se nel content dell'archivio ci mettiamo il riferimento alle timed position, questo serve
            for(TimedPosition position : positions)
                positionService.addToDB(username, position);*/
            UserArchive archive=userArchiveService.addArchive(username, positions);
            archive.setContent(null);
            return this.createUserArchiveResoure(archive);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "File created with metadata"),
            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
            @ApiResponse(code = 403, message = "The access to the resource you were trying to reach is forbidden"),
    })
    @RequestMapping(value = "/archives", method = RequestMethod.POST, consumes = {"application/json"},produces = "application/json")
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody
    Resource<UserArchive> createArchive(@ApiParam("List of timed positions") @RequestBody List<TimedPosition> positions) {
        try {
            String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserArchive archive=userArchiveService.addArchive(username, positions);
            archive.setContent(null);
            return this.createUserArchiveResoure(archive);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }


    /**
     * This method allows to download a list of zip archives.
     * @param filenames
     * @param response
     */
    @RequestMapping(value="/zip/archives", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    void downloadZipArchives(@RequestBody List<String> filenames, HttpServletResponse response) {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            userArchiveService.createZip(user, filenames, response.getOutputStream());
            response.setContentType("application/zip");
            response.setHeader("Content-disposition", "attachment; filename=archive.zip");
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Server Error");
        }
    }

    /**
     * This method allows to download a zip archive.
     * @param filename
     * @param response
     */
    @RequestMapping(value="/zip/archives/{filename}", method = RequestMethod.GET,produces="application/zip")
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    void downloadZipArchive(@ApiParam("The name of file that will be downloaded")@PathVariable("filename") String filename, HttpServletResponse response) {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> filenames = new ArrayList<>();
        filenames.add(filename);
        try {
            userArchiveService.createZip(user, filenames, response.getOutputStream());
            response.setContentType("application/zip");
            response.setHeader("Content-disposition", "attachment; filename=archive.zip");
            response.flushBuffer();
        } catch (AccessDeniedException e) {
            e.printStackTrace();
            throw new AccessDeniedException(e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected Error");
        }
    }

    /**
     * This method allows to delete a list of zip archives.
     * @param filenames
     */
    @RequestMapping(value="/archives", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    void deleteArchives(@ApiParam("The name of files that will be deleted")@RequestBody List<String> filenames) {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userArchiveService.deleteArchives(user, filenames);
    }

    /**
     * This method allows to delete a zip archive.
     * @param filename
     */
    @RequestMapping(value="/archives/{filename}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    void deleteArchive(@ApiParam("The name of file that will be deleted")@PathVariable("filename") String filename) {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userArchiveService.deleteArchive(user, filename);
    }


}
