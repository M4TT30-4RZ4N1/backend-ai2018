package it.polito.ai.lab3.rest;

import it.polito.ai.lab3.service.CustomerTransactionService;
import it.polito.ai.lab3.service.PositionService;
import it.polito.ai.lab3.service.model.CustomerTransaction;
import it.polito.ai.lab3.service.model.TimedPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.wololo.geojson.Polygon;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customers")
public class RestCustomerController {
    @Autowired
    private CustomerTransactionService transactionService;
    @Autowired
    private PositionService positionService;
    @RequestMapping(value="/listPositions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    Integer getPositionInIntervalInPolygon(HttpSession session, RedirectAttributes redirect,
                                                @RequestBody Polygon polygon,
                                                @Param("after") Long after, @Param("before") Long before) {

        // this is used to redirect to the getPositions url and save data in the session
        List<TimedPosition> polygonPositions = positionService.getPositionInIntervalInPolygon(polygon, new Date(after), new Date(before));

        // save positions into session object for customer possible transaction
        session.setAttribute("positions",polygonPositions);

        // set redirect attribute
        redirect.addFlashAttribute("positions", polygonPositions);
        return polygonPositions.size();
    }

    // here there is the real method get that return the count on the object saved in session
    @RequestMapping(value="/getPositions", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    Integer getPositionInInterval(@ModelAttribute("positions") List<TimedPosition> polygonPositions) {
        return polygonPositions.size();
    }

    @RequestMapping(value = "/buyPositions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody
    void confirmTransaction(HttpSession session) {

        List<TimedPosition> positions = (List<TimedPosition>)session.getAttribute("positions");

        Map<String, Long> rawTransactions = positions.stream()
                                                    .map(p -> p.getUser())
                                                    .collect(Collectors.groupingBy(u -> u, Collectors.counting()));
        // get username
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        for(Map.Entry<String, Long> user_nPositions : rawTransactions.entrySet()){
                CustomerTransaction transaction = new CustomerTransaction();
                transaction.setCustomerId(user);
                transaction.setUserId(user_nPositions.getKey());
                transaction.setnPositions(user_nPositions.getValue().intValue());
                transaction.setPrice(1); // PRICE SET TO 1
                transactionService.addTransaction(transaction);
        }
    }

}
