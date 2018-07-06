package it.polito.ai.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.ai.project.service.model.CustomException.EmptyArchiveException;
import it.polito.ai.project.service.model.CustomerTransaction;
import it.polito.ai.project.service.model.TimedPosition;
import it.polito.ai.project.service.model.UserArchive;
import it.polito.ai.project.service.repositories.CustomerTransactionRepository;
import it.polito.ai.project.service.repositories.UserArchiveRepository;
import it.polito.ai.project.service.repositories.UserArchiveRepositoryImpl;
import it.polito.ai.project.service.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class UserArchiveService {
    private final UserArchiveRepository archiveRepository;
    private final CustomerTransactionRepository transactionRepository;
    private final UserArchiveRepositoryImpl userArchiveRepositoryImpl;
    private Validator validator;
    private TimedPosition first = null;

    @Autowired
    public UserArchiveService(UserArchiveRepository archiveRepository, CustomerTransactionRepository transactionRepository, UserArchiveRepositoryImpl userArchiveRepositoryImpl) {
        this.validator = new Validator();
        this.archiveRepository = archiveRepository;
        this.transactionRepository = transactionRepository;
        this.userArchiveRepositoryImpl = userArchiveRepositoryImpl;
    }

    @PreAuthorize("hasRole( 'USER' )")
    public void addArchive(UserArchive archive){
        List<TimedPosition> content = validate(archive.getOwner(), archive.getContent());
        if(content.size() == 0) return;
        UserArchive newArchive = new UserArchive(archive.getOwner(), archive.getFilename(), archive.getCounter(), archive.isDeleted(), content);
        archiveRepository.insert(newArchive);
    }

    @PreAuthorize("hasRole( 'USER' )")
    public void addArchive(String username, List<TimedPosition> rawContent){
        String filename = UUID.randomUUID().toString().replace("-", "");
        List<TimedPosition> content = validate(username, rawContent);
        if(content.size() == 0) throw new EmptyArchiveException("There were no valid position in the archive");
        UserArchive archive = new UserArchive(username, filename, 0, false, content);
        archiveRepository.insert(archive);
    }

    private List<TimedPosition> validate(String username, List<TimedPosition> rawContent) {
        //TODO validare la lista di timedPositions recuperando l'ultima posizione uploadata dal db
        List<TimedPosition> content = new ArrayList<>();
        first = userArchiveRepositoryImpl.findLastPosition(username);
        rawContent.forEach(p -> {
            if(p.getUser() != null && !p.getUser().equals(username))
                throw new RuntimeException("The user specified in the positions differs from the session user");
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

    public List<UserArchive> getOwnArchivesWithoutContent(String user) {
        List<UserArchive> res = new ArrayList<>(archiveRepository.findByOwnerAndDeletedIsFalseAndExcludeContentAndExcludeId(user));
        //controllare se l'interfaccia spring fa il suo dovere, altrimenti creare un implementazione come per le posizioni
        System.out.println("Archivi caricati: " + res);
        return res;
    }
    private List<UserArchive> getOwnArchives(String user) {
        List<UserArchive> res = new ArrayList<>(archiveRepository.findByOwnerAndDeletedIsFalse(user));
        //controllare se l'interfaccia spring fa il suo dovere, altrimenti creare un implementazione come per le posizioni
        System.out.println("Archivi caricati: " + res);
        return res;
    }
    public List<UserArchive> getPurchasedArchives(String user) {
        List<CustomerTransaction> tmp = new ArrayList<>(transactionRepository.findByCustomerId(user));
        List<UserArchive> res = tmp.stream().map(customerTransaction -> archiveRepository.findByFilenameAndExcludeContentAndExcludeIdAndExcludeCounterAndExcludeDelete(customerTransaction.getFilename()))
                .collect(Collectors.toList());
        //controllare se l'interfaccia spring fa il suo dovere, altrimenti creare un implementazione come per le posizioni
        System.out.println("Archivi acquistati: " + res);
        return res;
    }

    private UserArchive findArchiveByFilename(String filename) {
        UserArchive res=archiveRepository.findByFilename(filename);
        //controllare se l'interfaccia spring fa il suo dovere, altrimenti creare un implementazione come per le posizioni
        System.out.println("Archivo acquistati: " + res);
        return res;
    }

    public ZipOutputStream createZip(String user, List<String> filenames, ServletOutputStream outputStream) throws IOException {
        // controllo che i file che l'utente vuole scaricare, facciano effettivamente parte della sua collezione
        List<String> archiveCollection = getOwnArchives(user).stream().map(UserArchive::getFilename).collect(Collectors.toList());
        archiveCollection.addAll(getPurchasedArchives(user).stream().map(UserArchive::getFilename).collect(Collectors.toList()));
        System.out.println("Elenco file da zippare: " + archiveCollection);
        filenames.forEach( (f)->{
                    if(!archiveCollection.contains(f))
                        throw new AccessDeniedException("Invalid operation, archive " + f + " not owned");
                }
        );
        // da qui in poi si può assumere che i file richiesti sono di proprietà e quindi l'operazione può continuare

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
        return zipOutputStream;
    }

    public void deleteArchives(String user, List<String> filenames){
        // controllo che i file che l'utente vuole eliminare, siano suoi
        List<String> archiveCollection = getOwnArchivesWithoutContent(user).stream().map(UserArchive::getFilename).collect(Collectors.toList());
        filenames.forEach( (f)->{
            if(!archiveCollection.contains(f))
                throw new AccessDeniedException("Invalid operation, archive " + f + " not owned");
            }
        );
        // da qui in poi si può assumere che i file richiesti sono di proprietà e quindi l'operazione può continuare
        filenames.stream().map(this::findArchiveByFilename).forEach(
                        archive->{
                            archive.setDeleted(true);
                            archiveRepository.save(archive);
                        });
    }
    public void deleteArchive(String user, String f){
        // controllo che i file che l'utente vuole eliminare, siano suoi
        List<String> archiveCollection = getOwnArchivesWithoutContent(user).stream().map(UserArchive::getFilename).collect(Collectors.toList());
        if(!archiveCollection.contains(f)){
             throw new AccessDeniedException("Invalid operation, archive " + f + " not owned");
        }
        UserArchive archive=this.findArchiveByFilename(f);
        archive.setDeleted(true);
        archiveRepository.save(archive);
    }

    public List<TimedPosition> downloadArchive(String user, String filename) {
        UserArchive archive=archiveRepository.findByFilename(filename);
        if(archive.getOwner().equals(user)&&!archive.isDeleted()){
            return archive.getContent();
        }else if(transactionRepository.findByCustomerIdAndFilename(user,filename).size()>0){
            return  archive.getContent();
        }
        throw new AccessDeniedException("Invalid operation, archive " + filename + " not owned");
    }
}
