package it.polito.ai.project.service.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class OpaqueTransaction {
    public int getnPositions() {
        return nPositions;
    }

    public void setnPositions(int nPositions) {
        this.nPositions = nPositions;
    }

    public String getOpaque_transaction() {
        return opaque_transaction;
    }

    public void setOpaque_transaction(String opaque_transaction) {
        this.opaque_transaction = opaque_transaction;
    }

    private int nPositions;
    private String opaque_transaction;


    public OpaqueTransaction() {

    }
    public static byte[] encrypt(byte[] bytesClear, String strKey) throws Exception{

        try {
            SecretKeySpec skeyspec=new SecretKeySpec(strKey.getBytes(),"Blowfish");
            Cipher cipher=Cipher.getInstance("Blowfish");
            cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
            byte[] encrypted=cipher.doFinal(bytesClear);
            return encrypted;

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }
    public static byte[] decrypt(byte[] bytesEncrypted, String strKey) throws Exception{
        try {
            SecretKeySpec skeyspec=new SecretKeySpec(strKey.getBytes(),"Blowfish");
            Cipher cipher=Cipher.getInstance("Blowfish");
            cipher.init(Cipher.DECRYPT_MODE, skeyspec);
            byte[] decrypted=cipher.doFinal(bytesEncrypted);
            return decrypted;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }
    public OpaqueTransaction(List<TimedPosition> polygonPositions) throws Exception {
        this.nPositions=polygonPositions.size();
        ObjectMapper mapper = new ObjectMapper();
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String pwd="123";//"super" + user + "secret";
        byte[] encrypted = encrypt(mapper.writeValueAsBytes(polygonPositions),pwd);
        this.opaque_transaction= Base64.getEncoder().encodeToString(encrypted);
    }
    public  List<TimedPosition> decode() throws Exception {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ObjectMapper mapper = new ObjectMapper();
        byte[] encrypted = Base64.getDecoder().decode(this.opaque_transaction);
        String pwd="123";//"super" + user + "secret";
        return mapper.readValue(decrypt(encrypted,pwd),new TypeReference<List<TimedPosition>>(){});
    }
}
