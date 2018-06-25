package it.polito.ai.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.ai.project.service.model.CustomerTransaction;
import it.polito.ai.project.service.model.TimedPosition;
import it.polito.ai.project.service.model.UserArchive;
import it.polito.ai.project.service.repositories.CustomerTransactionRepository;
import it.polito.ai.project.service.repositories.UserArchiveRepository;
import it.polito.ai.project.service.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class UserArchiveService {
    @Autowired
    private UserArchiveRepository archiveRepository;
    @Autowired
    CustomerTransactionRepository transactionRepository;
    private Validator validator;
    private TimedPosition first = null;

    public UserArchiveService() {
        this.validator = new Validator();
    }

    @PreAuthorize("hasRole( 'USER' )")
    public void addArchive(UserArchive archive){
        archiveRepository.insert(archive);
    }

    public void addArchive(String username, List<TimedPosition> rawContent){
        String filename = UUID.randomUUID().toString().replace("-", "");
        List<TimedPosition> content = validate(rawContent);
        UserArchive archive = new UserArchive(username, filename, 0, false, content);
        archiveRepository.insert(archive);
    }

    private List<TimedPosition> validate(List<TimedPosition> rawContent) {
        //TODO validare la lista di timedPositions recuperando l'ultima posizione uploadata dal db
        return rawContent;
    }

    public List<UserArchive> getOwnArchives(String user) {
        List<UserArchive> res = new ArrayList<>();
        res.addAll(archiveRepository.findByOwnerAndDeletedIsFalse(user));
        //controllare se l'interfaccia spring fa il suo dovere, altrimenti creare un implementazione come per le posizioni
        System.out.println(res);
        return res;
    }

    public List<String> getPurchasedArchives(String user) {
        List<CustomerTransaction> tmp = new ArrayList<>();
        tmp.addAll(transactionRepository.findByCustomerId(user));
        List<String> res = tmp.stream().map(t -> t.getFilename()).distinct().collect(Collectors.toList());
        //controllare se l'interfaccia spring fa il suo dovere, altrimenti creare un implementazione come per le posizioni
        System.out.println(res);
        return res;
    }

    public List<UserArchive> findArchiveByFilename(String filename) {
        List<UserArchive> res = new ArrayList<>();
        res.addAll(archiveRepository.findByFilename(filename));
        //controllare se l'interfaccia spring fa il suo dovere, altrimenti creare un implementazione come per le posizioni
        System.out.println(res);
        return res;
    }

    public FileSystemResource createZip(String user, List<String> filenames) throws IOException {
        // controllo che i file che l'utente vuole scaricare, facciano effettivamente parte della sua collezione
        List<String> archiveCollection = getOwnArchives(user).stream().map(a -> a.getFilename()).collect(Collectors.toList());
        archiveCollection.addAll(getPurchasedArchives(user));
        for(String f : filenames){
            if(!archiveCollection.contains(f))
                throw new AccessDeniedException("Invalid filename");
        }
        // da qui in poi si può assumere che i file richiesti sono di proprietà e quindi l'operazione può continuare
        String zipFilename = "test.zip";
        File zip = new File("./"+zipFilename);
        ObjectMapper mapper = new ObjectMapper();
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zip));
        for(String f : filenames) {
            for(UserArchive archive : findArchiveByFilename(f)){
                String s = mapper.writeValueAsString(archive.getContent());
                ZipEntry entry = new ZipEntry(f);
                 /* use more Entries to add more files
                 and use closeEntry() to close each file entry */
                zipOutputStream.putNextEntry(entry);
                zipOutputStream.write(s.getBytes());
                zipOutputStream.closeEntry();
            }
        }
        return new FileSystemResource(zip);
    }

    public void deleteArchives(String user, List<String> filenames){
        // controllo che i file che l'utente vuole eliminare, siano suoi
        List<String> archiveCollection = getOwnArchives(user).stream().map(a -> a.getFilename()).collect(Collectors.toList());
        for(String f : filenames){
            if(!archiveCollection.contains(f))
                throw new AccessDeniedException("Invalid filename");
        }
        // da qui in poi si può assumere che i file richiesti sono di proprietà e quindi l'operazione può continuare
        for(String f : filenames) {
            for(UserArchive archive : findArchiveByFilename(f)){
                archive.setDeleted(true);
                archiveRepository.save(archive);
            }
        }
    }
}
