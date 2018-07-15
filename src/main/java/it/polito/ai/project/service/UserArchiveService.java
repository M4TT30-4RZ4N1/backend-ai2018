package it.polito.ai.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.ai.project.service.model.ClientInteraction.PositionResult;
import it.polito.ai.project.service.model.ClientInteraction.SearchResult;
import it.polito.ai.project.service.model.ClientInteraction.TimestampResult;
import it.polito.ai.project.service.model.ClientInteraction.UserResult;
import it.polito.ai.project.service.model.CustomException.EmptyArchiveException;
import it.polito.ai.project.service.model.CustomerTransaction;
import it.polito.ai.project.service.model.TimedPosition;
import it.polito.ai.project.service.model.UserArchive;
import it.polito.ai.project.service.repositories.CustomerTransactionRepository;
import it.polito.ai.project.service.repositories.UserArchiveRepository;
import it.polito.ai.project.service.repositories.UserArchiveRepositoryImpl;
import it.polito.ai.project.service.validator.Validator;
import javassist.NotFoundException;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.wololo.geojson.Polygon;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This class is related to the User archive service.
 */
@Component
public class UserArchiveService {
    private final UserArchiveRepository archiveRepository;
    private final CustomerTransactionRepository transactionRepository;
    private final UserArchiveRepositoryImpl userArchiveRepositoryImpl;
    private Validator validator;
    private TimedPosition first = null;

    /**
     * This method allows to create a new user archive service.
     * @param archiveRepository
     * @param transactionRepository
     * @param userArchiveRepositoryImpl
     */
    @Autowired
    public UserArchiveService(UserArchiveRepository archiveRepository, CustomerTransactionRepository transactionRepository, UserArchiveRepositoryImpl userArchiveRepositoryImpl) {
        this.validator = new Validator();
        this.archiveRepository = archiveRepository;
        this.transactionRepository = transactionRepository;
        this.userArchiveRepositoryImpl = userArchiveRepositoryImpl;
    }

    /**
     * This method allows to add a new user archive.
     * @param archive new user archive
     */
    @PreAuthorize("hasRole( 'USER' )")
    public void addArchive(UserArchive archive){
        List<TimedPosition> content = validate(archive.getOwner(), archive.getContent());
        if(content.size() == 0) return;
        UserArchive newArchive = new UserArchive(archive.getOwner(), archive.getFilename(), archive.getCounter(), archive.isDeleted(), content);
        archiveRepository.insert(newArchive);
    }

    /**
     * This method allows to update a user archive.
     * @param archive user archive to be updated
     */
    @PreAuthorize("hasRole( 'USER' )")
    public void updateArchive(UserArchive archive){
        // we assume that the content of the archive is valid
        archiveRepository.save(archive);
    }

    /**
     * This method allows to add a new user archive.
     * @param username username of the user
     * @param rawContent list of TimedPosition
     * @return the user archive
     */
    @PreAuthorize("hasRole( 'USER' )")
    public UserArchive addArchive(String username, List<TimedPosition> rawContent){
        String filename = username+"_"+(new Date().getTime())+"_"+UUID.randomUUID().toString().replace("-", "")+".json";
        List<TimedPosition> content = validate(username, rawContent);
        if(content.size() == 0) throw new EmptyArchiveException("There were no valid position in the archive");
        UserArchive archive = new UserArchive(username, filename, 0, false, content);
        archiveRepository.insert(archive);
        return archive;
    }

    /**
     * This method allows to validate the sequence of TimedPosition.
     * @param username username of the user
     * @param rawContent list of TimedPosition
     */
    private List<TimedPosition> validate(String username, List<TimedPosition> rawContent) {
        List<TimedPosition> content = new ArrayList<>();
        first = userArchiveRepositoryImpl.findLastPosition(username);
        rawContent.forEach(p -> {
            if(first == null){
                if(validator.validateFirst(p)){
                    content.add(p);
                    first = p;
                }
            }else{
                if(validator.validateSequence(first, p)){
                    content.add(p);
                    first = p;
                }
            }
        });
        return content;
    }

    /**
     * This method allows to retrieve a list of archives (without including the content), based on the username.
     * @param user username of the user
     * @return list of user archives
     */
    public List<UserArchive> getOwnArchivesWithoutContent(String user) {
        List<UserArchive> res = new ArrayList<>(archiveRepository.findByOwnerAndDeletedIsFalseAndExcludeContentAndExcludeId(user));
        System.out.println("Archivi caricati: " + res);
        return res;
    }

    /**
     * This method allows to retrieve a list of archives (without including the content), based on the username and other parameters.
     * @param user username of the user
     * @param pagenumber
     * @param size
     * @return list of user archives
     */
    public List<UserArchive> getOwnArchivesWithoutContent(String user,int pagenumber,int size) {
        Pageable page=PageRequest.of(pagenumber,size);
        List<UserArchive> res = new ArrayList<>(archiveRepository.findByOwnerAndDeletedIsFalseAndExcludeContentAndExcludeId(user,page));
        System.out.println("Archivi caricati: " + res);
        return res;
    }

    /**
     * This method allows to retrieve a list of archives (including the content), based on the username.
     * @param user username of the user
     * @return list of user archives
     */
    private List<UserArchive> getOwnArchives(String user) {
        List<UserArchive> res = new ArrayList<>(archiveRepository.findByOwnerAndDeletedIsFalse(user));
        System.out.println("Archivi caricati: " + res);
        return res;
    }

    /**
     * This method allows to retrieve a list of purchased archives (including the content), based on the username.
     * @param user username of the user
     * @return list of user archives
     */
    public List<UserArchive> getPurchasedArchives(String user) {
        List<CustomerTransaction> tmp = new ArrayList<>(transactionRepository.findByCustomerId(user));
        List<UserArchive> res = tmp.stream().map(customerTransaction -> archiveRepository.findByFilenameAndExcludeContentAndExcludeIdAndExcludeCounterAndExcludeDelete(customerTransaction.getFilename()))
                .collect(Collectors.toList());
        System.out.println("Archivi acquistati: " + res);
        return res;
    }

    /**
     * This method allows to retrieve a list of purchased archives (including the content), based on the username and other parameters.
     * @param user username of the user
     * @param page
     * @param size
     * @return list of user archives
     */
    public List<UserArchive> getPurchasedArchives(String user,int page,int size) {
        List<CustomerTransaction> tmp = new ArrayList<>(transactionRepository.findByCustomerId(user,PageRequest.of(page,size)));
        List<UserArchive> res = tmp.stream().map(customerTransaction -> archiveRepository.findByFilenameAndExcludeContentAndExcludeIdAndExcludeCounterAndExcludeDelete(customerTransaction.getFilename()))
                .collect(Collectors.toList());
        System.out.println("Archivi acquistati: " + res);
        return res;
    }

    /**
     * This method allows to retrieve a user archive, based on the filename.
     * @param filename name of the file
     * @return user archive
     */
    private UserArchive findArchiveByFilename(String filename) {
        UserArchive res=archiveRepository.findByFilename(filename);
        return res;
    }

    /**
     * This method allows to retrieve a user archive, based on the filename (not deleted).
     * @param filename name of the file
     * @return user archive
     */
    public UserArchive findArchiveByFilenameAndDeletedIsFalse(String filename) {
        UserArchive res = archiveRepository.findByFilenameAndDeletedIsFalse(filename);
        return res;
    }

    /**
     * This method allows to create a new zip.
     * @param user the username
     * @param filenames name of the files
     * @param outputStream the output stream
     * @return a ZipOutputStream
     */
    public ZipOutputStream createZip(String user, List<String> filenames, ServletOutputStream outputStream) throws IOException {
        // I check whether the archives that the user wants to download belong to his collection
        List<String> archiveCollection = getOwnArchives(user).stream().map(UserArchive::getFilename).collect(Collectors.toList());
        archiveCollection.addAll(getPurchasedArchives(user).stream().map(UserArchive::getFilename).collect(Collectors.toList()));
        System.out.println("Elenco file da zippare: " + filenames);
        System.out.println("Elenco file posseduti: " + archiveCollection);
        filenames.forEach( (f)->{
                    if(!archiveCollection.contains(f))
                        throw new AccessDeniedException("Invalid operation, archive " + f + " not owned");
                }
        );
        // from now on, we can assume that the required files belong to the user, so the operation can continue

        ObjectMapper mapper = new ObjectMapper();
        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        filenames.stream().map(this::findArchiveByFilename).forEach(
                archive->{
                    try {
                        String s = mapper.writeValueAsString(archive.getContent());
                        ZipEntry entry = new ZipEntry(archive.getFilename());
                     /* use more Entries to add more files
                     and use closeEntry() to close each file entry */
                        zipOutputStream.putNextEntry(entry);
                        zipOutputStream.write(s.getBytes());
                        zipOutputStream.closeEntry();
                    }catch (IOException e){
                        throw new RuntimeException("Unable to create the zip archive");
                    }
                }
        );
        zipOutputStream.finish();
        zipOutputStream.close();
        outputStream.close();
        return zipOutputStream;
    }

    /**
     * This method allows to delete a list of archives.
     * @param user the username
     * @param filenames name of the files
     */
    public void deleteArchives(String user, List<String> filenames){
        // before deleting the files I check whether they belong to the user
        List<String> archiveCollection = getOwnArchivesWithoutContent(user).stream().map(UserArchive::getFilename).collect(Collectors.toList());
        filenames.forEach( (f)->{
            if(!archiveCollection.contains(f))
                throw new AccessDeniedException("Invalid operation, archive " + f + " not owned");
            }
        );
        // from now on, we can assume that the required files belong to the user, so the operation can continue

        filenames.stream().map(this::findArchiveByFilename).forEach(
                        archive->{
                            archive.setDeleted(true);
                            archiveRepository.save(archive);
                        });
    }

    /**
     * This method allows to delete an archive.
     * @param user the username
     * @param f name of the file
     */
    public void deleteArchive(String user, String f){
        // before deleting the file I check whether it belongs to the user
        List<String> archiveCollection = getOwnArchivesWithoutContent(user).stream().map(UserArchive::getFilename).collect(Collectors.toList());
        if(!archiveCollection.contains(f)){
             throw new AccessDeniedException("Invalid operation, archive " + f + " not owned");
        }
        UserArchive archive=this.findArchiveByFilename(f);
        archive.setDeleted(true);
        archiveRepository.save(archive);
    }

    /**
     * This method allows to download an archive
     * @param user the username
     * @param filename name of the file
     * @return the list of TimedPosition
     */
    public List<TimedPosition> downloadArchive(String user, String filename) throws NotFoundException {
        UserArchive archive=archiveRepository.findByFilename(filename);
        if(archive==null){
            throw new NotFoundException("Invalid operation, archive " + filename + " not owned");
        }
        if(archive.getOwner().equals(user)&&!archive.isDeleted()){
            return archive.getContent();
        }else if(transactionRepository.findByCustomerIdAndFilename(user,filename).size()>0){
            return  archive.getContent();
        }
        throw new AccessDeniedException("Invalid operation, archive " + filename + " not owned");
    }

    /**
     * This method allows to get a list of user archives based on a filter.
     * @param polygon the polygon filter
     * @param after start timestamp filter
     * @param before end timestamp filter
     * @param users users filter
     * @return the list of user archives
     */
    public List<UserArchive> getUserArchiveWithPositionInIntervalInPolygonInUserList(Polygon polygon, Date after, Date before, List<String> users){
        return userArchiveRepositoryImpl.getPositionInIntervalInPolygonInUserList(polygon, after.getTime(), before.getTime(), users);
    }

    /**
     * This method allows to get an approximate result based on a filter.
     * @param polygon the polygon filter
     * @param after start timestamp filter
     * @param before end timestamp filter
     * @param users users filter
     * @return the search result
     */
    public SearchResult getApproximatePositionInIntervalInPolygonInUserList(Polygon polygon, Date after, Date before, List<String> users){
        List<UserArchive> res = new ArrayList<>(userArchiveRepositoryImpl.getPositionInIntervalInPolygonInUserList(polygon, after.getTime(), before.getTime(), users));
        SearchResult searchResult=new SearchResult();
        res.forEach(userarchive->{
            userarchive.getContent().forEach(
                    content->{
                        searchResult.byTimestamp.add(new TimestampResult(userarchive.getOwner(), DateUtils.setSeconds(new Date(content.timestamp*1000), 0).getTime()/1000));
                        content.trimPrecsion();
                        searchResult.byPosition.add(new PositionResult(userarchive.getOwner(), content.point));
                        searchResult.byUser.add(new UserResult(userarchive.getOwner()));
                    }
            );
        });
        searchResult.byTimestamp=searchResult.byTimestamp.stream().distinct().sorted(Comparator.comparingLong(TimestampResult::getTimestamp)).collect(Collectors.toList());
        searchResult.byPosition=searchResult.byPosition.stream().distinct().collect(Collectors.toList());
        searchResult.byUser=searchResult.byUser.stream().distinct().sorted(Comparator.comparing(UserResult::getUser)).collect(Collectors.toList());
        searchResult.byTotal=res.size();
        Collections.shuffle(searchResult.byPosition);
        return searchResult;
    }

    /**
     * This method allows to get a list of user archives based on a filter.
     * @param polygon the polygon filter
     * @param after start timestamp filter
     * @param before end timestamp filter
     * @param users users filter
     * @return the list of user archives
     */
    public List<UserArchive> getSearchArchive(Polygon polygon, Date after, Date before, List<String> users){
        List<UserArchive> res = new ArrayList<>(userArchiveRepositoryImpl.getArchiveWithPositionInIntervalInPolygonInUserList(polygon, after.getTime(), before.getTime(), users));
        return res;
    }

    /**
     * This method allows to get a list of TimedPositions.
     * @return the list of TimedPosition
     */
    public List<TimedPosition> getAllPosition(){
        return  this.archiveRepository.findAll().stream().map(UserArchive::getContent).flatMap(List::stream).collect(Collectors.toList());
    }
}
