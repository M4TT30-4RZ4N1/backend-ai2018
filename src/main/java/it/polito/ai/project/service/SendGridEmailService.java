package it.polito.ai.project.service;

import com.sendgrid.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendGridEmailService{
    private SendGrid sendGridClient;
    @Autowired
    public SendGridEmailService(SendGrid sendGridClient) {
        this.sendGridClient = sendGridClient;
    }

    /**
     * This method allows to send an Email through html.
     * @param from sender email
     * @param to receiver email
     * @param subject subject of the email
     * @param body content of the email
     */
    public void sendHTML(String from, String to, String subject, String body) {
        Response response = sendEmail(from, to, subject, new Content("text/html", body));
        System.out.println("Status Code: " + response.getStatusCode() + ", Body: " + response.getBody() + ", Headers: "
                + response.getHeaders());
    }
    /**
     * This method allows to send an Email
     * @param from sender email
     * @param to receiver email
     * @param subject subject of the email
     * @param content content of the email
     * @return the response
     */
    private Response sendEmail(String from, String to, String subject, Content content) {
        Mail mail = new Mail(new Email(from), subject, new Email(to), content);
        mail.setReplyTo(new Email("admin@marsmarketplace.ga"));
        Request request = new Request();
        Response response = null;
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            response=this.sendGridClient.api(request);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return response;
    }
}