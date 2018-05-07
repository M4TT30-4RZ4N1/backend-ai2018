package it.polito.ai.lab3.rest;

import it.polito.ai.lab3.service.PositionService;
import it.polito.ai.lab3.service.model.TimedPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/users")
public class RestUserController {
    @Autowired
    private PositionService positionService;
    @RequestMapping(value="/positions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    List<TimedPosition> getPositionInInterval(@Param("after") Long after, @Param("before") Long before) {
        if(after == null || before == null) throw new RuntimeException("Missing parameters");
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return positionService.getPositionInInterval(user.getUsername(), new Date(after), new Date(before));
    }
    @RequestMapping(value = "/positions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody
    void createdPositions(@RequestBody List<TimedPosition> positions) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        for(TimedPosition position : positions)
            positionService.addToDB(user.getUsername(), position);
    }

}
