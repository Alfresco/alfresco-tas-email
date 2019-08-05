package org.alfresco.email.dsl.imap;

import static org.alfresco.utility.report.log.Step.STEP;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.*;

import org.alfresco.email.ImapWrapper;
import org.alfresco.utility.TasProperties;
import org.alfresco.utility.dsl.DSLAssertion;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.testng.Assert;

public class ImapAssertion extends DSLAssertion<ImapWrapper>
{
    @SuppressWarnings("unused")
    private TasProperties tasProperties;

    public ImapAssertion(ImapWrapper imapProtocol, TasProperties tasProperties)
    {
        super(imapProtocol);
        this.tasProperties = tasProperties;
    }

    /**
     * Verify total number of messages (files) from a folder in IMAP client
     * 
     * @param messageCount expected number of messages
     * @return current wrapper
     * @throws Exception
     */
    public ImapWrapper countMessagesIs(int messageCount) throws Exception
    {
        Folder folder = getProtocol().withImapUtil().getCurrentFolder();
        if (!folder.isOpen())
            folder.open(Folder.READ_ONLY);
        STEP(String.format("IMAP: Assert folder '%s' has %s messages", folder.getFullName(), messageCount));
        Assert.assertEquals(folder.getMessageCount(), messageCount, String.format("Messages found in [%s]", folder.getFullName()));
        return getProtocol();
    }

    /**
     * Verify a folder has new unread messages (files) in IMAP client
     * 
     * @return
     * @throws Exception
     */
    public ImapWrapper hasNewMessages() throws Exception
    {
        Folder folder = getProtocol().withImapUtil().getCurrentFolder();
        STEP(String.format("IMAP: Assert folder '%s' has new messages", folder.getFullName()));
        Assert.assertTrue(folder.hasNewMessages(), String.format("New Message found in [%s]", folder.getFullName()));
        return getProtocol();
    }

    /**
     * Verify user is connected through IMAP client to Alfresco repository
     * 
     * @return current wrapper
     */
    public ImapWrapper userIsConnected()
    {
        STEP(String.format("IMAP: Assert user %s is connected in IMAP", getProtocol().getTestUser().getUsername()));
        Assert.assertTrue(getProtocol().getImapStore().isConnected(), String.format("%s is now connected via IMAP", getProtocol().getTestUser().toString()));
        return getProtocol();
    }

    /**
     * Verify user is NOT connected through IMAP client to Alfresco repository
     * 
     * @return current wrapper
     */
    public ImapWrapper userIsNotConnected()
    {
        STEP(String.format("IMAP: Assert user %s is NOT connected in IMAP", getProtocol().getTestUser().getUsername()));
        Assert.assertFalse(getProtocol().getImapStore().isConnected(), String.format("%s is NOT connected via IMAP", getProtocol().getTestUser().toString()));
        return getProtocol();
    }
    
     /**
     * Verify current folder exists in IMAP
     *
     * @return current wrapper
     * @throws Exception
     */
    public ImapWrapper existsInImap() throws Exception
    {
        Folder currentFolder = getProtocol().withImapUtil().getCurrentFolder();
        STEP(String.format("IMAP: Assert that folder '%s' exists in IMAP", getProtocol().getLastResource()));
        Assert.assertNotNull(currentFolder.getParent().getFolder(currentFolder.getName()),
                String.format("Folder '%s' does not exist in IMAP", currentFolder.getName()));
        return getProtocol();
    }

    /**
     * Verify current folder contains message
     *
     * @param fileModels expected message to exist
     * @return current wrapper
     * @throws Exception
     */
    public ImapWrapper containsMessages(FileModel... fileModels) throws Exception
    {
        Folder currentFolder = getProtocol().withImapUtil().getCurrentFolder();
        if (!currentFolder.isOpen())
            currentFolder.open(Folder.READ_ONLY);
        for (FileModel fileModel : fileModels)
        {
            String contentModelName = getProtocol().withImapUtil().getObjectName(fileModel.getName());
            STEP(String.format("IMAP: Assert that folder '%s' contains message '%s'", getProtocol().getLastResource(), contentModelName));
            Message message = getProtocol().withImapUtil().getMessageBySubject(currentFolder.getMessages(), contentModelName);
            Assert.assertNotNull(message, String.format("Message '%s' does not exist in folder '%s'", contentModelName, currentFolder.getName()));
        }

        return getProtocol();
    }

    /**
     * Verify file content has contains document name, title, description, creator, created date,
     * modifier, modified date, size, content folder link, content url link, download url
     *
     * @return current wrapper
     * @throws Exception
     */
    public ImapWrapper fileContentIsDisplayed() throws Exception
    {
        String contentModelName = getProtocol().withImapUtil().getObjectName(getProtocol().getLastResource());
        STEP(String.format("IMAP: Assert that message '%s' content is displayed", contentModelName));
        Folder currentFolder = getProtocol().withImapUtil().getCurrentFolder().getParent();
        if (!currentFolder.isOpen())
            currentFolder.open(Folder.READ_ONLY);
        Message message = getProtocol().withImapUtil().getMessageBySubject(currentFolder.getMessages(), contentModelName);
        if(message == null)
            throw new MessagingException(String.format("No message with subject %s has been found", contentModelName));
        String messageContent = getProtocol().withImapUtil().getMessageContent(message);

        Assert.assertTrue(messageContent.contains("Document name"), "File content does not contain 'Document name'");
        Assert.assertTrue(messageContent.contains("Title"), "File content does not contain 'Title'");
        Assert.assertTrue(messageContent.contains("Description"), "File content does not contain 'Description'");
        Assert.assertTrue(messageContent.contains("Creator"), "File content does not contain 'Creator'");
        Assert.assertTrue(messageContent.contains("Created"), "File content does not contain 'Created'");
        Assert.assertTrue(messageContent.contains("Modifier"), "File content does not contain 'Modifier'");
        Assert.assertTrue(messageContent.contains("Modified"), "File content does not contain 'Modified'");
        Assert.assertTrue(messageContent.contains("Size"), "File content does not contain 'Size'");
        Assert.assertTrue(messageContent.contains("CONTENT LINKS"), "File content does not contain 'CONTENT LINKS'");
        Assert.assertTrue(messageContent.contains("Content folder"), "File content does not contain 'Content folder'");
        Assert.assertTrue(messageContent.contains("Content URL"), "File content does not contain 'Content URL'");
        Assert.assertTrue(messageContent.contains("Download URL"), "File content does not contain 'Download URL'");

        return getProtocol();
    }

    /**
     * Verify file content for document name, title, description, content folder link, content url link and download url
     *
     * @param fileModel message to verify
     * @return current wrapper
     * @throws Exception
     */
    public ImapWrapper messageContentMatchesFileModelData(FileModel fileModel) throws Exception
    {
        String value = null;
        String contentModelName = getProtocol().withImapUtil().getObjectName(fileModel.getName());
        Folder currentFolder = getProtocol().withImapUtil().getCurrentFolder().getParent();
        if (!currentFolder.isOpen())
            currentFolder.open(Folder.READ_ONLY);
        Message message = getProtocol().withImapUtil().getMessageBySubject(currentFolder.getMessages(), contentModelName);
        if(message == null)
            throw new MessagingException(String.format("No message with subject %s has been found", contentModelName));
        String messageContent = getProtocol().withImapUtil().getMessageContent(message).replaceAll(" ", "");

        Assert.assertTrue(messageContent.contains("Documentname:" + contentModelName), "File content Title is incorrect");

        if (fileModel.getTitle() == null)
            value = "NONE";
        else
            value = fileModel.getTitle();

        Assert.assertTrue(messageContent.contains("Title:" + value), "File content Title is incorrect");

        if (fileModel.getDescription() == null)
            value = "NONE";
        else
            value = fileModel.getDescription();

        Assert.assertTrue(messageContent.contains("Description:" + value), "File content Description is incorrect");

        Pattern pattern = Pattern.compile("Contentfolder:.*" + String.format("share/page/site/IMAPsite%s", currentFolder.getFullName().split("IMAPsite")[1]).toLowerCase());
        Matcher matcher = pattern.matcher(messageContent);
        Assert.assertTrue(matcher.find(), "File content 'Content folder' is incorrect");

        pattern = Pattern.compile("ContentURL:.*" +
                String.format("share/proxy/alfresco/api/node/content/workspace/SpacesStore/%s/%s", fileModel.getNodeRef().split(";")[0], contentModelName));
        matcher = pattern.matcher(messageContent);
        Assert.assertTrue(matcher.find(), "File content 'Content URL' is incorrect");

        Assert.assertTrue(messageContent.contains(String.format("share/proxy/alfresco/api/node/content/workspace/SpacesStore/%s/%s?a=true",
                fileModel.getNodeRef().split(";")[0], contentModelName)), "File content 'Download URL' is incorrect");

        return getProtocol();
    }

    /**
     * Verify current working directory matches contentModel
     *
     * @param contentModel
     * @return current wrapper
     * @throws Exception
     */
    public ImapWrapper currentDirectoryIs(ContentModel contentModel) throws Exception
    {
        STEP(String.format("IMAP: Assert that current directory is '%s'", contentModel.getProtocolLocation()));
        Assert.assertEquals(getProtocol().getCurrentSpace(), contentModel.getProtocolLocation(), "Current directory is not the correct one");
        return getProtocol();
    }

    /**
     * Verify if specified folders exists in the current folder
     * 
     * @param folderModels list of searched folders
     * @return
     * @throws Exception
     */
    public ImapWrapper contains(FolderModel... folderModels) throws Exception
    {
        List<FolderModel> foldersList = getProtocol().getFolders();
        for (FolderModel folder: folderModels)
        {
            STEP(String.format("IMAP: Assert that '%s' folder contains folder '%s'", getProtocol().getLastResource(), folder.getName()));
            Assert.assertTrue(isFolderInList(folder.getName(), foldersList));
        }
        return getProtocol();
    }

    /**
     * Verify if specified folders (Imap site folders) exists in the current folder
     * 
     * @param siteModels
     * @return
     * @throws Exception
     */
    public ImapWrapper contains(SiteModel... siteModels) throws Exception
    {
        List<FolderModel> foldersList = getProtocol().getFolders();
        for (SiteModel site : siteModels)
        {
            STEP(String.format("IMAP: Assert that '%s' folder contains folder '%s'", getProtocol().getLastResource(), site.getId()));
            Assert.assertTrue(isFolderInList(site.getId(), foldersList));
        }
        return getProtocol();
    }

    private boolean isFolderInList(String folderName, List<FolderModel> folders)
    {
        for (ContentModel folder : folders)
        {
            if (folderName.equals(folder.getName()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Verify current folder does not contain message
     *
     * @param fileModels expected message to not be present
     * @return current wrapper
     * @throws Exception
     */
    public ImapWrapper doesNotContainMessages(FileModel... fileModels) throws Exception
    {
        Folder currentFolder = getProtocol().withImapUtil().getCurrentFolder();
        if (!currentFolder.isOpen())
            currentFolder.open(Folder.READ_ONLY);
        for (FileModel fileModel : fileModels)
        {
            String contentModelName = getProtocol().withImapUtil().getObjectName(fileModel.getName());
            STEP(String.format("IMAP: Assert that folder '%s' does not contain message '%s'", getProtocol().getLastResource(), contentModelName));
            Message message = getProtocol().withImapUtil().getMessageBySubject(currentFolder.getMessages(), contentModelName);
            Assert.assertNull(message, String.format("Message '%s' exists in folder '%s'", contentModelName, currentFolder.getName()));
        }
        return getProtocol();
    }

    /**
     * Verify if an Imap message has the specified flags
     * 
     * @param flags flags to be checked
     * @return
     * @throws Exception
     */
    public ImapWrapper messageContainsFlags(Flags.Flag... flags) throws Exception
    {
        String flagName = "";
        String fileName = getProtocol().withImapUtil().getObjectName(getProtocol().getLastResource());
        Folder currentFolder = getProtocol().withImapUtil().getCurrentFolder().getParent();
        if (!currentFolder.isOpen())
            currentFolder.open(Folder.READ_ONLY);
        Message message = getProtocol().withImapUtil().getMessageBySubject(currentFolder.getMessages(), fileName);
        if(message == null)
            throw new MessagingException(String.format("No message with subject %s has been found", fileName));
        for (Flags.Flag flag : flags) {
            flagName = getProtocol().withImapUtil().getMessageFlagName(flag);
            STEP(String.format("IMAP: Assert that message '%s' has flag '%s' set", fileName, flagName));
            Assert.assertTrue(message.getFlags().contains(flag), String.format("Message '%s' does not have flag '%s' set", fileName, flagName));
        }
        return getProtocol();
    }

    /**
     * Verify if an Imap message doesn't have the specified flags
     * 
     * @param flags flags to be checked
     * @return
     * @throws Exception
     */
    public ImapWrapper messageDoesNotContainFlags(Flags.Flag... flags) throws Exception
    {
        String flagName = "";
        String fileName = getProtocol().withImapUtil().getObjectName(getProtocol().getLastResource());
        Folder currentFolder = getProtocol().withImapUtil().getCurrentFolder().getParent();
        if (!currentFolder.isOpen())
            currentFolder.open(Folder.READ_ONLY);
        Message message = getProtocol().withImapUtil().getMessageBySubject(currentFolder.getMessages(), fileName);
        if(message == null)
            throw new MessagingException(String.format("No message with subject %s has been found", fileName));
        for (Flags.Flag flag : flags) {
            flagName = getProtocol().withImapUtil().getMessageFlagName(flag);
            STEP(String.format("IMAP: Assert that message '%s' does not have flag '%s' set", fileName, flagName));
            Assert.assertFalse(message.getFlags().contains(flag), String.format("Message '%s' has flag '%s' set", fileName, flagName));
        }
        return getProtocol();
    }
    
    /**
     * Verify search results contain the message correspondent to the expected file
     *
     * @param files expected file(s) to be present
     * @return current wrapper
     * @throws Exception
     */
    public ImapWrapper resultsContainMessage(FileModel... files) throws MessagingException
    {
        for (FileModel file : files)
        {
            STEP(String.format("IMAP: Assert that message '%s' is present in the search results", file.getName()));
            Assert.assertTrue(isMessagePresent(file), String.format("Message with subject '%s' is not present in the list of search results", file.getName()));
        }
        return getProtocol();
    }
    
    /**
     * Verify search results do NOT contain the message correspondent to the expected file
     *
     * @param files expected file(s) NOT to be present
     * @return current wrapper
     * @throws Exception
     */    
    public ImapWrapper resultsDoNotContainMessage(FileModel... files) throws MessagingException
    {
        for (FileModel file : files)
        {
            STEP(String.format("IMAP: Assert that message '%s' is NOT present in the search results", file.getName()));
            Assert.assertFalse(isMessagePresent(file), String.format("Message with subject '%s' is present in the list of search results", file.getName()));
        }
        return getProtocol();
    }
    
    private boolean isMessagePresent(FileModel file) throws MessagingException
    {
        for (Message message : getProtocol().usingSearchResults())
            if (message.getSubject().equals(String.format("%s.%s", file.getName(), file.getFileType().extension)))
                return true;
        return false;
    }

    /**
     * Verify if the message sender contains {@link ImapAssertion#messageSenderIs(String)}
     *
     * @param sender
     * @return current wrapper
     * @throws Exception
     */
    public ImapWrapper messageSenderNameIs(String subject, String sender) throws Exception
    {
        STEP(String.format("IMAP: Assert that sender name of message with subject '%s' sender is '%s'", subject, sender));
        Folder currentFolder = getProtocol().withImapUtil().getCurrentFolder();
        if (!currentFolder.isOpen())
            currentFolder.open(Folder.READ_ONLY);
        Message message = getProtocol().withImapUtil().getMessageBySubject(currentFolder.getMessages(), subject);
        if(message == null)
            throw new MessagingException(String.format("No message with subject %s has been found", subject));
        String messageFrom = message.getFrom()[0].toString();
        Assert.assertEquals(messageFrom.split("@")[0], sender, "Message does not have the expected sender name");
        return getProtocol();
    }

    /**
     * Verify if the sender of message with subject is exactly {@link ImapAssertion#messageSenderIs(String, String)}
     *
     * @param subject subject of the message
     * @param sender sender of the message
     * @return current wrapper
     * @throws Exception
     */
    public ImapWrapper messageSenderIs(String subject, String sender) throws Exception
    {
        STEP(String.format("IMAP: Assert that message '%s' sender is '%s'", subject, sender));
        Folder currentFolder = getProtocol().withImapUtil().getCurrentFolder();
        if (!currentFolder.isOpen())
            currentFolder.open(Folder.READ_ONLY);
        Message message = getProtocol().withImapUtil().getMessageBySubject(currentFolder.getMessages(), subject);
        if(message == null)
            throw new MessagingException(String.format("No message with subject %s has been found", subject));
        String messageFrom = message.getFrom()[0].toString();
        Assert.assertEquals(messageFrom, sender, "Message does not have the expected sender");
        return getProtocol();
    }
    
    /**
     * Verify if the message subject is exactly {@link ImapAssertion#messageSubjectIs(String)}
     *
     * @param sender
     * @return current wrapper
     * @throws Exception
     */
    public ImapWrapper messageSubjectIs(String subject) throws Exception
    {
        STEP(String.format("IMAP: Assert that message with subject '%s' is found", subject));
        Folder currentFolder = getProtocol().withImapUtil().getCurrentFolder();
        if (!currentFolder.isOpen())
            currentFolder.open(Folder.READ_ONLY);
        Assert.assertNotNull(getProtocol().withImapUtil().getMessageBySubject(currentFolder.getMessages(), subject), String.format("No message with subject %s has been found", subject));
        return getProtocol();
    }

    /**
     * Verify if current folder doesn't contain the specified folders
     * 
     * @param folderModels children folders
     * @return
     * @throws Exception
     */
    public ImapWrapper doesNotContain(FolderModel... folderModels) throws Exception
    {
        List<FolderModel> foldersList = getProtocol().getFolders();
        for (FolderModel folder: folderModels)
        {
            STEP(String.format("IMAP: Assert that '%s' folder does not contain folder '%s'", getProtocol().getLastResource(), folder.getName()));
            Assert.assertFalse(isFolderInList(folder.getName(), foldersList));
        }
        return getProtocol();
    }

    /**
     * Verify if current folder is subscribed
     * 
     * @return
     * @throws Exception
     */
    public ImapWrapper isSubscribed() throws Exception
    {
        STEP(String.format("IMAP: Assert that folder '%s' is subscribed", getProtocol().getLastResource()));
        Folder folder = getProtocol().withImapUtil().getCurrentFolder();
        Assert.assertTrue(folder.isSubscribed(), String.format("Folder '%s' is not subscribed", getProtocol().getLastResource()));
        return getProtocol();
    }

    /**
     * Verify if current folder is not subscribed
     * 
     * @return
     * @throws Exception
     */
    public ImapWrapper isNotSubscribed() throws Exception
    {
        STEP(String.format("IMAP: Assert that folder '%s' is not subscribed", getProtocol().getLastResource()));
        Folder folder = getProtocol().withImapUtil().getCurrentFolder();
        Assert.assertFalse(folder.isSubscribed(), String.format("Folder '%s' is subscribed", getProtocol().getLastResource()));
        return getProtocol();
    }
}
