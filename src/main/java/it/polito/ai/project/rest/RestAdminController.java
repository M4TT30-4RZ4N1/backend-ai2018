package it.polito.ai.project.rest;

import it.polito.ai.project.service.CustomerTransactionService;
import it.polito.ai.project.service.UserArchiveService;
import it.polito.ai.project.service.model.CustomerTransaction;
import it.polito.ai.project.service.model.TimedPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This class is related to the RestAdminController, which includes the methods that are available to the admin.
 */
@RestController
@RequestMapping("/admin")
public class RestAdminController {
    private final CustomerTransactionService transactionService;
    private final UserArchiveService userArchiveService;

    /**
     * This method allows to generate a RestAdminController
     * @param transactionService
     * @param userArchiveService
     */
    @Autowired
    public RestAdminController(CustomerTransactionService transactionService, UserArchiveService userArchiveService) {
        this.transactionService = transactionService;
        this.userArchiveService = userArchiveService;
    }

    /**
     * This method allows to retrieve all the positions.
     * @return  the list of TimedPositions
     */
    @RequestMapping(value = "/usersData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    List<TimedPosition> getAllPositions() {
        return userArchiveService.getAllPosition();
    }

    /**
     * This method allows to retrieve all the transactions.
     * @return  the list of CustomerTransactions
     */
    @RequestMapping(value = "/customersData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    List<CustomerTransaction> getAllTransactions() {
        return transactionService.getTransactions();
    }
}
