package it.polito.ai.lab3.rest;

import it.polito.ai.lab3.service.PositionService;
import it.polito.ai.lab3.service.model.TimedPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/users")
public class RestUserController {
    @Autowired
    private PositionService positionService;
    @RequestMapping(value="/{userId}/positions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    List<TimedPosition> getAll(@PathVariable Long userId) {
        return positionService.getPositions(userId);
    }
    @RequestMapping(value="/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    List<TimedPosition> getPositionInInterval(@PathVariable Long userId, @Param("after") Long after, @Param("before") Long before) {
        return positionService.getPositionInInterval(userId, new Date(after), new Date(before));
    }
    @RequestMapping(value = "/{userId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody
    void get(@PathVariable Long userId, @RequestBody List<TimedPosition> positions) {
        for(TimedPosition position : positions)
            positionService.addToDB(userId, position);
    }

}
