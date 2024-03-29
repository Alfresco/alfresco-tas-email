package org.alfresco.email.dsl.smtp;

import static org.alfresco.utility.report.log.Step.STEP;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import org.alfresco.email.SmtpWrapper;
import org.alfresco.utility.Utility;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.File;

public class ComposeMessage
{
    private Message message;
    private BodyPart bodyPart;
    private Multipart multipart;
    @SuppressWarnings("unused")
    private Session session;
    private SmtpWrapper smtpProtocol;
    private Transport transport;

    public ComposeMessage(SmtpWrapper smtpProtocol, Session session)
    {
        this.session = session;
        this.smtpProtocol = smtpProtocol;
        this.transport = smtpProtocol.getTransport();
        message = new MimeMessage(session);
        bodyPart = new MimeBodyPart();
        multipart = new MimeMultipart();
    }

    /**
     * commaSeparatedRecipients = "a1@test.com, a2@test.com")
     * 
     * @param commaSeparatedRecipients
     * @return
     * @throws Exception
     */
    public ComposeMessage withRecipients(String commaSeparatedRecipients) throws Exception
    {
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(commaSeparatedRecipients));
        return this;
    }

    public ComposeMessage withSubject(String subject) throws Exception
    {
        message.setSubject(subject);
        return this;
    }

    public ComposeMessage withBody(String body) throws Exception
    {
        bodyPart.setText(body);
        multipart.addBodyPart(bodyPart);
        return this;
    }

    public ComposeMessage withAttachments(File... files) throws Exception
    {
        for (File file : files)
        {
            Utility.checkObjectIsInitialized(file, "file");
            try
            {
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(file);
                mimeBodyPart.setDataHandler(new DataHandler(source));
                mimeBodyPart.setFileName(file.getName());
                multipart.addBodyPart(mimeBodyPart);
            }
            catch (MessagingException e)
            {
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    public SmtpWrapper sendMail() throws Exception
    {
        Utility.checkObjectIsInitialized(transport, "transport");
        message.setFrom(new InternetAddress(smtpProtocol.getCurrentUser().getUsername()));
        
        STEP(String.format("SMTP: Sending mail with following properties: FROM: %s, TO: %s, BODY: %s", 
                ToStringBuilder.reflectionToString(message.getFrom(), ToStringStyle.MULTI_LINE_STYLE), 
                ToStringBuilder.reflectionToString(message.getAllRecipients(), ToStringStyle.MULTI_LINE_STYLE),
                bodyPart.getContent()));

        message.setContent(multipart);
        
        transport.sendMessage(message, message.getAllRecipients());
        if (smtpProtocol.getCurrentSpace() != null)
            smtpProtocol.setLastResource(Utility.buildPath(smtpProtocol.getCurrentSpace(), message.getSubject()));
        return smtpProtocol;
    }
}
