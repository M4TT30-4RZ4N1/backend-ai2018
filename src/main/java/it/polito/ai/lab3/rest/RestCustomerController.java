package it.polito.ai.lab3.rest;

import it.polito.ai.lab3.service.CustomerTransactionService;
import it.polito.ai.lab3.service.PositionService;
import it.polito.ai.lab3.service.model.CustomerTransaction;
import it.polito.ai.lab3.service.model.OpaqueTransaction;
import it.polito.ai.lab3.service.model.TimedPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
    @RequestMapping(value="/searchPositions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    OpaqueTransaction getPositionInIntervalInPolygon(HttpSession session, RedirectAttributes redirect,
                                                @RequestBody Polygon polygon,
                                                @Param("after") Long after, @Param("before") Long before) throws Exception {

        // this is used to redirect to the getPositions url and save data in the session
        List<TimedPosition> polygonPositions = positionService.getPositionInIntervalInPolygon(polygon, new Date(after), new Date(before));

        // save positions into session object for customer possible transaction
  //      session.setAttribute("positions",polygonPositions);

        // set redirect attribute

     //   redirect.addFlashAttribute("positions", polygonPositions);
        return new OpaqueTransaction(polygonPositions);
    }

    @RequestMapping(value = "/buyPositions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody
    List<TimedPosition> confirmTransaction(HttpSession session,@RequestBody OpaqueTransaction opaqueTransaction) throws Exception {



        List<TimedPosition> positions = opaqueTransaction.decode();//(List<TimedPosition>)session.getAttribute("positions");
        double totalPrice = 100;
        double positionPrice = totalPrice/positions.size();
        Map<String, Long> rawTransactions = positions.stream()
                                                    .map(TimedPosition::getUser)
                                                    .collect(Collectors.groupingBy(u -> u, Collectors.counting()));
        // get username
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        for(Map.Entry<String, Long> user_nPositions : rawTransactions.entrySet()){
                int nPositions = user_nPositions.getValue().intValue();
                CustomerTransaction transaction = new CustomerTransaction();
                transaction.setCustomerId(user);
                transaction.setUserId(user_nPositions.getKey());
                transaction.setnPositions(nPositions);
                transaction.setPrice(nPositions*positionPrice); // PRICE SET TO 1
                transactionService.addTransaction(transaction);
        }
        return positions;
    }

}
