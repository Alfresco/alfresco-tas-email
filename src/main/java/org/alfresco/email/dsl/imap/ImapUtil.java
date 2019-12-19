package org.alfresco.email.dsl.imap;

import org.alfresco.email.ImapWrapper;
import org.alfresco.utility.LogFactory;
import org.alfresco.utility.Utility;
import org.alfresco.utility.model.ContentModel;
import org.slf4j.Logger;

import java.io.IOException;

import javax.mail.*;

public class ImapUtil {
    private ImapWrapper imapProtocol;
    private Logger LOG = LogFactory.getLogger();

    public ImapUtil(ImapWrapper imapProtocol)
    {
        this.imapProtocol = imapProtocol;
    }

    public Folder getCurrentFolder() throws Exception
    {
        String folderPath = imapProtocol.getLastResource();
        folderPath = Utility.removeLastSlash(folderPath);
        return getImapStore().getFolder(folderPath);
    }

    private Store getImapStore()
    {
        if (imapProtocol.getImapStore() == null)
            LOG.error("You must first authenticate to IMAP server. Use authenticateUser() method.");

        return imapProtocol.getImapStore();
    }

    /**
     * Returns IMAP folder for the provided ContentModel
     */
    public Folder getFolder(ContentModel contentModel) throws Exception
    {
        String folderPath = contentModel.getProtocolLocation();
        if(!folderPath.contains(imapProtocol.getPrefixSpace()))
        {
            folderPath = imapProtocol.getPrefixSpace() + Utility.removeLastSlash(folderPath);
        }
        return getImapStore().getFolder(folderPath);
    }

    /**
     * Returns message if it exists otherwise returns null
     */
    public Message getMessageBySubject(Message[] messages, String subject) throws Exception
    {
        for (Message message : messages)
            if (message.getSubject().equals(subject))
                return message;
        return null;
    }

    /**
     * Returns multipart message content as text
     */
    protected String getMessageContent(Message message) throws Exception
    {
        Multipart multipart = (Multipart) message.getContent();
        String content = "";
        for (int i = 0; i < multipart.getCount(); i++) 
        {
            BodyPart part = multipart.getBodyPart(i);
            content += part.getContent().toString();
        }
        if (content == "")
        {
            IOException ioe = new IOException("No content");
            throw ioe;
        }
        return content;
    }

    /**
     * Returns the object name
     *
     * e.g. getObjectName("/documentLibrary/file.txt") will return file.txt
     */
    public String getObjectName(String path)
    {
        String[] tokens = path.split("/");
        return tokens[tokens.length - 1];
    }

    public String getCurrentSiteName() throws Exception
    {
        return getCurrentFolder().getFullName().split("Sites/")[1].split("/")[0];
    }

    public String getMessageFlagName(Flags.Flag flag)
    {
        String flagName = "";

        if (flag.equals(Flags.Flag.ANSWERED))
            flagName = "ANSWERED";

        if (flag.equals(Flags.Flag.DELETED))
            flagName = "DELETED";

        if (flag.equals(Flags.Flag.DRAFT))
            flagName = "DRAFT";

        if (flag.equals(Flags.Flag.FLAGGED))
            flagName = "FLAGGED";

        if (flag.equals(Flags.Flag.RECENT))
            flagName = "RECENT";

        if (flag.equals(Flags.Flag.SEEN))
            flagName = "SEEN";

        return flagName;
    }
}
