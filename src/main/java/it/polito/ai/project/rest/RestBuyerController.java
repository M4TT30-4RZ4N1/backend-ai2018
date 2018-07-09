package it.polito.ai.project.rest;

import it.polito.ai.project.service.CustomerTransactionService;
import it.polito.ai.project.service.UserArchiveService;
import it.polito.ai.project.service.model.ClientInteraction.ArchiveTransaction;
import it.polito.ai.project.service.model.ClientInteraction.FilterQuery;
import it.polito.ai.project.service.model.ClientInteraction.SearchResult;
import it.polito.ai.project.service.model.CustomerTransaction;
import it.polito.ai.project.service.model.TimedPosition;
import it.polito.ai.project.service.model.UserArchive;
import javassist.tools.web.BadHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.wololo.geojson.Polygon;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is related to the RestBuyerController, which includes the methods that are available to the buyer.
 */
@RestController
@RequestMapping("/customer")
public class RestBuyerController {
    private final CustomerTransactionService transactionService;
    private final UserArchiveService userArchiveService;
    /**
     * This method allows to generate a RestBuyerController.
     * @param transactionService
     * @param userArchiveService
     */
    @Autowired
    public RestBuyerController(CustomerTransactionService transactionService, UserArchiveService userArchiveService) {
        this.transactionService = transactionService;
        this.userArchiveService = userArchiveService;
    }

    /**
     * This method allows to retrieve all the positions belonging to a certain time interval and included in the specified polygon.
     * @param session
     * @param redirect
     * @param filters
     * @param after
     * @param before
     * @return  the list of positions detected
     */
    @RequestMapping(value="/search", method = RequestMethod.POST, params = {"after","before"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    SearchResult getPositionInIntervalInPolygon(HttpSession session, RedirectAttributes redirect,
                                                @RequestBody FilterQuery filters,
                                                @Param("after") Long after, @Param("before") Long before) throws Exception {
        if(after == null || before == null){
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"Invalid parameters");
        }
        // this is used to redirect to the getPositions url and save data in the session
        Polygon polygon = filters.getGeoFilter();
        List<String> users = filters.getUsersFilter();
        SearchResult polygonPositions = new SearchResult();
        if(polygon.getCoordinates().length != 0){
            polygonPositions = userArchiveService.getApproximatePositionInIntervalInPolygonInUserList(polygon, new Date(after), new Date(before), users);
        }
        return polygonPositions;
    }

    /**
     * This method allows to confirm the transaction and to retrieve all the positions belonging to a certain time interval and included in the specified polygon.
     * @param session
     * @param filters
     * @param after
     * @param before
     * @return  the list of positions detected
     */
    @RequestMapping(value = "/buy", method = RequestMethod.POST, params = {"after","before"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    List<ArchiveTransaction> getTransactions(HttpSession session, @RequestBody FilterQuery filters,
                                             @Param("after") Long after, @Param("before") Long before) throws Exception {

        if(after == null || before == null){
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"Invalid parameters");
        }
        Polygon polygon = filters.getGeoFilter();
        List<String> users = filters.getUsersFilter();
        List<UserArchive> archives;
        if(polygon.getCoordinates().length != 0){
            archives = userArchiveService.getSearchArchive(polygon, new Date(after), new Date(before), users);
        }
        else{
            return new ArrayList<>();
        }
        List<ArchiveTransaction> result = new ArrayList<>();
        String customer = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        archives.forEach(a ->{
            List<CustomerTransaction> transactions = transactionService.getTransactionByCustomerAndFilename(customer, a.getFilename());
            if(transactions.size() > 0 || a.getOwner().equals(customer)){
                result.add(new ArchiveTransaction(a.getFilename(), true));
            }else{
                result.add(new ArchiveTransaction(a.getFilename(), false));
            }
        });
        return result;
    }

    @RequestMapping(value = "/buy/confirm", method = RequestMethod.POST, consumes = {"application/json"})
    @ResponseStatus(value = HttpStatus.CREATED)
    public void confirmTransactions(@RequestBody List<ArchiveTransaction> archiveToBought){
        double totalPrice = 100;
        double positionPrice = totalPrice/archiveToBought.size();
        String customer = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        archiveToBought.forEach(a ->{
            List<CustomerTransaction> transactions = transactionService.getTransactionByCustomerAndFilename(customer, a.getFilename());
            if(transactions.size() == 0){
                UserArchive archive = userArchiveService.findArchiveByFilenameAndDeletedIsFalse(a.getFilename());
                archive.setCounter(archive.getCounter()+1);
                //System.out.println("Updating archive: " + archive);
                userArchiveService.updateArchive(archive);
                CustomerTransaction transaction = new CustomerTransaction();
                transaction.setCustomerId(customer);
                transaction.setUserId(archive.getOwner());
                transaction.setFilename(a.getFilename());
                transaction.setPrice(positionPrice); // PRICE SET TO 1
                //System.out.println("Saving new transaction: " + transaction);
                transactionService.addTransaction(transaction);
            }
        });
        return;
    }
}
