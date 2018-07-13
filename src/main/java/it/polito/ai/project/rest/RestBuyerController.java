package it.polito.ai.project.rest;

import it.polito.ai.project.security.RepositoryUserDetailsService;
import it.polito.ai.project.service.CustomerTransactionService;
import it.polito.ai.project.service.UserArchiveService;
import it.polito.ai.project.service.model.ClientInteraction.ArchiveTransaction;
import it.polito.ai.project.service.model.ClientInteraction.FilterQuery;
import it.polito.ai.project.service.model.ClientInteraction.SearchResult;
import it.polito.ai.project.service.model.CustomerTransaction;
import it.polito.ai.project.service.model.UserArchive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.wololo.geojson.Polygon;

import java.util.*;

/**
 * This class is related to the RestBuyerController, which includes the methods that are available to the buyer.
 */
@RestController
@RequestMapping("/customer")
public class RestBuyerController {
    private final CustomerTransactionService transactionService;
    private final UserArchiveService userArchiveService;
    private final RepositoryUserDetailsService repositoryUserDetailsService;
    /**
     * This method allows to generate a RestBuyerController.
     * @param transactionService the service which is related to the transactions on Mongo
     * @param userArchiveService the service which is related to the archives on Mongo
     * @param repositoryUserDetailsService the service which is related to the user details on Mongo
     */
    @Autowired
    public RestBuyerController(CustomerTransactionService transactionService, UserArchiveService userArchiveService, RepositoryUserDetailsService repositoryUserDetailsService) {
        this.transactionService = transactionService;
        this.userArchiveService = userArchiveService;
        this.repositoryUserDetailsService = repositoryUserDetailsService;
    }
    @RequestMapping(value="/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    List<String> getUsers(){
        return repositoryUserDetailsService.getAllUsername();
    }

    /**
     * This method allows to retrieve all the positions belonging to a certain time interval and included in the specified polygon.
     * @param filters the filter is referred to user and polygon
     * @param after the start timestamp for the research
     * @param before the end timestamp for the research
     * @return  the list of positions detected
     */
    @RequestMapping(value="/search", method = RequestMethod.POST, params = {"after","before"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    SearchResult getPositionInIntervalInPolygon(@RequestBody FilterQuery filters,
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
     * This method allows to retrieve the archive which contain all the positions belonging to a certain time interval and included in the specified polygon.
     * @param filters the filter is referred to user and polygon
     * @param after the start timestamp for the research
     * @param before the end timestamp for the research
     * @return  the list of positions detected
     */
    @RequestMapping(value = "/buy", method = RequestMethod.POST, params = {"after","before"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    List<ArchiveTransaction> getTransactions(@RequestBody FilterQuery filters,
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

    /**
     * This method allows to confirm the transaction.
     * @param archiveToBought the archives for which a new transaction will be registered
     */
    @RequestMapping(value = "/buy/confirm", method = RequestMethod.POST, consumes = {"application/json"})
    @ResponseStatus(value = HttpStatus.CREATED)
    public void confirmTransactions(@RequestBody List<ArchiveTransaction> archiveToBought){
        double totalPrice = 100;
        double positionPrice = totalPrice/archiveToBought.size();
        String customer = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        archiveToBought.stream().filter(archiveTransaction -> !archiveTransaction.isPurchased()).forEach(a ->{
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
