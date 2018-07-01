package it.polito.ai.project.rest;

import it.polito.ai.project.service.CustomerTransactionService;
import it.polito.ai.project.service.PositionService;
import it.polito.ai.project.service.model.CustomerTransaction;
import it.polito.ai.project.service.model.TimedPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class RestAdminController {
    private final PositionService positionService;
    private final CustomerTransactionService transactionService;

    @Autowired
    public RestAdminController(PositionService positionService, CustomerTransactionService transactionService) {
        this.positionService = positionService;
        this.transactionService = transactionService;
    }

    @RequestMapping(value = "/usersData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    List<TimedPosition> getAllPositions() {
        return positionService.getPositions();
    }
    @RequestMapping(value = "/customersData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    List<CustomerTransaction> getAllTransactions() {
        return transactionService.getTransactions();
    }
}
