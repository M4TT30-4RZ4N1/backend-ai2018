package it.polito.ai.lab3.rest;

import it.polito.ai.lab3.service.CustomerTransactionService;
import it.polito.ai.lab3.service.PositionService;
import it.polito.ai.lab3.service.model.CustomerTransaction;
import it.polito.ai.lab3.service.model.TimedPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
    @RequestMapping(value="/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    List<TimedPosition> getPositionInIntervalInPolygon(@Param("after") Long after, @Param("before") Long before) {
        return positionService.getPositionInIntervalInPolygon(new Date(after), new Date(before));
    }
    @RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody
    void confirmTransaction(@RequestBody List<TimedPosition> positions) {
        Map<String, Long> rawTransactions = positions.stream()
                                                    .map(p -> p.getUser())
                                                    .collect(Collectors.groupingBy(u -> u, Collectors.counting()));
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        for(Map.Entry<String, Long> user_nPositions : rawTransactions.entrySet()){
                CustomerTransaction transaction = new CustomerTransaction();
                transaction.setCustomerId(user.getUsername());
                transaction.setUserId(user_nPositions.getKey());
                transaction.setnPositions(user_nPositions.getValue().intValue());
                transactionService.addTransaction(transaction);
        }
    }

}
