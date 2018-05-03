package it.polito.ai.lab3.rest;

import it.polito.ai.lab3.service.CustomerTransactionService;
import it.polito.ai.lab3.service.PositionService;
import it.polito.ai.lab3.service.model.CustomerTransaction;
import it.polito.ai.lab3.service.model.TimedPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    @RequestMapping(value="/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    List<TimedPosition> getPositionInIntervalInPolygon(@PathVariable Long userId, @Param("after") Long after, @Param("before") Long before) {
        return positionService.getPositionInIntervalInPolygon(new Date(after), new Date(before));
    }
    @RequestMapping(value = "/{userId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody
    void confirmTransaction(@PathVariable Long userId, @RequestBody List<TimedPosition> positions) {
        Map<Long, Long> rawTransactions = positions.stream()
                                                    .map(p -> p.getUserId())
                                                    .collect(Collectors.groupingBy(u -> u, Collectors.counting()));
        for(Map.Entry<Long, Long> user_nPositions : rawTransactions.entrySet()){
                CustomerTransaction transaction = new CustomerTransaction();
                transaction.setCustomerId(userId);
                transaction.setUserId(user_nPositions.getKey());
                transaction.setnPositions(user_nPositions.getValue().intValue());
                transactionService.addTransaction(transaction);
        }
    }

}
