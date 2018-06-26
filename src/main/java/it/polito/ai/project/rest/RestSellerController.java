package it.polito.ai.project.rest;

import it.polito.ai.project.service.PositionService;
import it.polito.ai.project.service.UserArchiveService;
import it.polito.ai.project.service.model.TimedPosition;
import it.polito.ai.project.service.model.UserArchive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
public class RestSellerController {
    @Autowired
    private PositionService positionService;
    @Autowired
    private UserArchiveService userArchiveService;
    @RequestMapping(value="/archives/self", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    List<UserArchive> getOwnArchives() {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UserArchive> archives = userArchiveService.getOwnArchives(user);
        //rimuove il contenuto del file nella risposta poichè non necessario
        archives.forEach(a -> a.setContent(null));
        return archives;
    }
    @RequestMapping(value="/archives/purchased", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    List<String> getPurchasedArchives() {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //rimuove il contenuto del file nella risposta poichè non necessario
        return  userArchiveService.getPurchasedArchives(user);
    }
    @RequestMapping(value = "/positions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody
    void createdPositions(@RequestBody List<TimedPosition> positions) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        /* se nel content dell'archivio ci mettiamo il riferimento alle timed position, questo serve
        for(TimedPosition position : positions)
            positionService.addToDB(username, position);*/
        userArchiveService.addArchive(username, positions);
    }

    @RequestMapping(value="/archives/download", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    void downloadArchives(@RequestBody List<String> filenames, HttpServletResponse response) {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ByteArrayOutputStream zip = null;
        try {
            userArchiveService.createZip(user, filenames, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
            throw new AccessDeniedException("Server Error");
        }
        return;
    }

    @RequestMapping(value="/archives/delete", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    void deleteArchives(@RequestBody List<String> filenames) {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userArchiveService.deleteArchives(user, filenames);
        return;
    }


}
