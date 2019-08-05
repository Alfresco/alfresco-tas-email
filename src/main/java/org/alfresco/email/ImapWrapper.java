package org.alfresco.email;

import static org.alfresco.utility.Utility.checkObjectIsInitialized;
import static org.alfresco.utility.report.log.Step.STEP;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SearchTerm;

import org.alfresco.dataprep.ContentActions;
import org.alfresco.email.dsl.JmxUtil;
import org.alfresco.email.dsl.MessageFlags;
import org.alfresco.email.dsl.imap.ImapAssertion;
import org.alfresco.email.dsl.imap.ImapUtil;
import org.alfresco.utility.LogFactory;
import org.alfresco.utility.TasProperties;
import org.alfresco.utility.Utility;
import org.alfresco.utility.dsl.DSLContentModelAction;
import org.alfresco.utility.dsl.DSLFolder;
import org.alfresco.utility.dsl.DSLProtocol;
import org.alfresco.utility.exception.TestConfigurationException;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(value = "prototype")
public class ImapWrapper extends DSLProtocol<ImapWrapper> implements DSLContentModelAction<ImapWrapper>, DSLFolder<ImapWrapper>
{
    static Logger LOG = LogFactory.getLogger();

    @Autowired
    TasProperties tasProperties;

    @Autowired
    EmailProperties emailProperties;

    @Autowired
    ContentActions contentActions;

    private Session mailSession = null;
    private Store imapStore = null;
    private List<Message> searchResults = null;

    public Store getImapStore()
    {
        return imapStore;
    }

    @Override
    public ImapAssertion assertThat()
    {
        return new ImapAssertion(this, tasProperties);
    }

    /**
     * @return JMX DSL for this wrapper
     */
    public JmxUtil withJMX()
    {
        return new JmxUtil(this, jmxBuilder.getJmxClient());
    }

    /**
     * @return utilities that are used by CMIS
     */
    public ImapUtil withImapUtil()
    {
        return new ImapUtil(this);
    }

    @Override
    public ImapWrapper authenticateUser(UserModel userModel) throws Exception
    {
        return authenticateUser(userModel, emailProperties.getImapServer(), emailProperties.getImapPort());
    }
    
    @Override
    public ImapWrapper disconnect() throws Exception
    {
        STEP(String.format("IMAP: Disconnect"));
        checkObjectIsInitialized(getImapStore(), "imapStore");
        getImapStore().close();
        return this;
    }

    @Override
    public String getPrefixSpace()
    {
        return "Alfresco IMAP";
    }

    @Override
    public String getRootPath() throws TestConfigurationException
    {
        return "";
    }

    @Override
    public String getSitesPath() throws TestConfigurationException
    {
        return String.format("%s/%s", getPrefixSpace(), "Sites");
    }

    @Override
    public String getUserHomesPath() throws TestConfigurationException
    {
        return String.format("%s/%s", getPrefixSpace(), "User Homes");
    }

    @Override
    public String getDataDictionaryPath() throws TestConfigurationException
    {
        return String.format("%s/%s", getPrefixSpace(), "Data Dictionary");
    }

    @Override
    protected String getProtocolJMXConfigurationStatus() throws Exception
    {
        return withJMX().getImapServerConfigurationStatus();
    }

    public ImapWrapper usingDataDictionary() throws Exception
    {
        String path = getDataDictionaryPath();
        setCurrentSpace(path);
        return this;
    }

    public ImapWrapper usingPath(String path) throws Exception
    {
        ContentModel folderModel = new FolderModel(path);
        folderModel.setProtocolLocation(buildPath(getCurrentSpace(), folderModel.getName()));
        setCurrentSpace(folderModel.getProtocolLocation());
        return this;
    }
    
    public ImapWrapper usingSites() throws Exception
    {
        setCurrentSpace(getSitesPath());
        return this;
    }

    @Override
    public ImapWrapper usingSite(SiteModel siteModel) throws Exception
    {
        return usingSite(siteModel.getId());
    }

    @Override
    public ImapWrapper usingUserHome(String username) throws Exception
    {
        checkObjectIsInitialized(username, "username");
        setCurrentSpace(buildUserHomePath(username));
        return this;
    }

    @Override
    public ImapWrapper usingUserHome() throws Exception
    {
        checkObjectIsInitialized(getTestUser().getUsername(), "username");
        STEP(String.format("IMAP: Navigate to '%s'", buildUserHomePath(getTestUser().getUsername())));
        setCurrentSpace(buildUserHomePath(getTestUser().getUsername()));
        return this;
    }

    public ImapWrapper usingUserHomeRoot() throws Exception
    {
        STEP(String.format("IMAP: Navigate to '%s'", getUserHomesPath()));
        setCurrentSpace(getUserHomesPath());
        return this;
    }
    
    public ImapWrapper usingAlfrescoImap() throws Exception
    {
        STEP(String.format("IMAP: Navigate to '%s'", getPrefixSpace()));
        setCurrentSpace(getPrefixSpace());
        return this;
    }

    public ImapWrapper usingSiteWikiContainer(SiteModel siteModel) throws Exception
    {
        String path = Utility.buildPath(getPrefixSpace(), "Sites", siteModel.getId(), "wiki");
        STEP(String.format("IMAP: Navigate to '%s'", path));
        setCurrentSpace(path);
        return this;
    }


    public ImapWrapper usingSiteLinksContainer(SiteModel siteModel) throws Exception
    {
        String path = Utility.buildPath(getPrefixSpace(), "Sites", siteModel.getId(), "links");
        STEP(String.format("IMAP: Navigate to '%s'", path));
        setCurrentSpace(path);
        return this;
    }

    public ImapWrapper usingSiteCalendarContainer(SiteModel siteModel) throws Exception
    {
        String path = Utility.buildPath(getPrefixSpace(), "Sites", siteModel.getId(), "calendar");
        STEP(String.format("IMAP: Navigate to '%s'", path));
        setCurrentSpace(path);
        return this;
    }

    @Override
    public ImapWrapper createFolder(FolderModel folderModel) throws Exception
    {
        STEP(String.format("IMAP: Create folder '%s'", folderModel.getName()));
        String folderPath = buildPath(getCurrentSpace(), folderModel.getName());
        Folder newFolder = getImapStore().getFolder(folderPath);
        newFolder.create(Folder.HOLDS_FOLDERS);
        setLastResource(newFolder.getFullName());
        folderModel.setProtocolLocation(folderPath);
        folderModel.setCmisLocation(getLastResourceWithoutPrefix());
        folderModel.setNodeRef(contentService.getNodeRefByPath(getTestUser().getUsername(), getTestUser().getPassword(), getLastResourceWithoutPrefix()));
        return this;
    }

    /**
     * Delete the folder. Folder must be closed in order to be deleted.
     */
    public ImapWrapper delete() throws Exception
    {
        Folder delFolder = withImapUtil().getCurrentFolder();
        STEP(String.format("IMAP: Delete folder '%s'", delFolder.getFullName()));
        if (delFolder.isOpen())
        {
            STEP(String.format("IMAP: Folder status is opened and the delete can not be performed! Closing the folder..."));
            delFolder.close(true);
        }
        delFolder.delete(true);
        try
        {
            delFolder.delete(true);
        }
        catch (FolderNotFoundException e)
        {
            LOG.info("IMAP: The folder has been deleted!");
        }
        catch (Exception e)
        {
            LOG.warn("IMAP: The folder has not been deleted!");
        }
        dataContent.waitUntilContentIsDeleted(getLastResourceWithoutPrefix());
        return this;
    }

    /**
     * Attempts to delete an open folder
     *
     * Note: this will always fail since a folder must be closed in order to be deleted
     */
    public ImapWrapper attemptToDeleteOpenFolder() throws Exception
    {
        Folder delFolder = withImapUtil().getCurrentFolder();
        STEP(String.format("IMAP: Attempt to delete folder '%s' that is open", delFolder.getFullName()));
        delFolder.open(Folder.READ_WRITE);
        delFolder.delete(true);
        return this;
    }

    @Override
    public List<FolderModel> getFolders() throws Exception
    {
        Folder folder = null;

        if (getCurrentSpace().isEmpty())
        {
            folder = getRootFolder();
        }
        else
        {
            folder = getFolder();
        }
        return getFolderModelsFromFolders(folder.list());
    }

    @Override
    public ImapWrapper rename(String newName) throws Exception
    {
        Folder currentFolder = withImapUtil().getCurrentFolder();
        if (currentFolder.isOpen())
            currentFolder.close(true);
        Folder newFolder = currentFolder.getParent().getFolder(newName);
        currentFolder.renameTo(newFolder);
        setLastResource(newFolder.getFullName());
        return this;
    }

    @Override
    public ImapWrapper update(String content) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ImapWrapper copyTo(ContentModel destination) throws Exception
    {
        Folder currentFolder = withImapUtil().getCurrentFolder();
        Folder destinationFolder = withImapUtil().getFolder(destination);

        FolderModel theCopy = new FolderModel(currentFolder.getName());
        usingResource(destination).createFolder(theCopy);
        destination = theCopy;
        setLastResource(currentFolder.getFullName());
        copyMessagesTo(destination);

        currentFolder.open(Folder.READ_ONLY);

        if (currentFolder.list().length != 0)
        {
            for (Folder folder : currentFolder.list())
            {
                setLastResource(folder.getFullName());
                copyTo(theCopy);
            }
        }
        
        setLastResource(destinationFolder.getFullName());
        return this;
    }

    @Override
    public ImapWrapper moveTo(ContentModel destination) throws Exception
    {
        STEP(String.format("IMAP: Move to folder '%s'", withImapUtil().getFolder(destination).getFullName()));
        String currentResource = getLastResource();
        copyTo(destination);
        setLastResource(currentResource);
        delete();
        setLastResource(withImapUtil().getFolder(destination).getFullName());
        return this;
    }

    /**
     * Delete specified message(s) (content) by given name. A message in IMAP client can be identified by the name of the content.
     * Parent folder must be closed at the end in order message to be deleted.
     * 
     * @param contentNames name of the content(s)
     * @return current wrapper
     * @throws Exception
     */
    public ImapWrapper deleteMessage(String... contentNames) throws Exception
    {
        Folder folder = getImapStore().getFolder(getCurrentSpace());
        folder.open(Folder.READ_WRITE);
        List<Message> messages = Arrays.asList(folder.getMessages());

        for (String contentName : contentNames )
        {
            LOG.info("Deleting message with subject: {} ", contentName);
            for (Message message : messages)
                if (message.getSubject().equals(contentName))
                {
                    message.setFlag(Flags.Flag.DELETED, true);
                    LOG.info("Marked DELETE for message {} ", message.getSubject());
                }
        }

        folder.close(true);
        folder.open(Folder.READ_WRITE);

        for (String contentName : contentNames)
            dataContent.waitUntilContentIsDeleted(getLastResourceWithoutPrefix() + "/" + contentName);

        return this;
    }

    /**
     * Delete current message. Parent folder must be closed at the end in order message to be deleted.
     * 
     * @return current wrapper
     * @throws Exception
     */
    public ImapWrapper deleteMessage() throws Exception
    {
        checkObjectIsInitialized(getCurrentUser(), "userName");
        checkObjectIsInitialized(getCurrentSpace(), "contentName");
        File file = new File(getLastResource());
        String fileName = file.getName();
        STEP(String.format("IMAP: Delete message with subject '%s'", fileName));
        
        Folder folder = getImapStore().getFolder(Utility.getParentPath(getCurrentSpace()));
        folder.open(Folder.READ_WRITE);
        List<Message> messages = Arrays.asList(folder.getMessages());
        for (Message message : messages)
        {
            if (message.getSubject().equals(fileName))
            {
                message.setFlag(Flags.Flag.DELETED, true);
                LOG.info("Marked DELETE for message {}", message.getSubject());
                folder.close(true);
                folder.open(Folder.READ_WRITE);
                dataContent.waitUntilContentIsDeleted(getLastResourceWithoutPrefix());
                break;
            }
        }
        setCurrentSpace(folder.getFullName());
        return this;
    }

    @Override
    public ImapWrapper usingSite(String siteId) throws Exception
    {
        checkObjectIsInitialized(siteId, "SiteID");
        STEP(String.format("IMAP: Navigate to site '%s/documentlibrary/'", siteId));
        setCurrentSpace(buildSiteDocumentLibraryPath(siteId, ""));
        return this;
    }

    @Override
    public ImapWrapper usingResource(ContentModel contentModel) throws Exception
    {
        checkObjectIsInitialized(contentModel.getName(), "contentName");
        if (contentModel.getProtocolLocation() == null || !contentModel.getProtocolLocation().startsWith(getPrefixSpace()))
        {
            contentModel.setProtocolLocation(Utility.buildPath(getPrefixSpace(), contentModel.getCmisLocation().substring(1)));
        }
        STEP(String.format("IMAP: Navigate to '%s'", contentModel.getProtocolLocation()));
        setCurrentSpace(contentModel.getProtocolLocation());
        return this;
    }
	
    private Folder getRootFolder() throws Exception
    {
        return getImapStore().getDefaultFolder();
    }

    private Folder getFolder() throws Exception
    {
        return getImapStore().getFolder(Utility.removeLastSlash(getCurrentSpace()));
    }	
   
    /**
     * Helper method that returns list of FolderModel from mail folder
     */
    private List<FolderModel> getFolderModelsFromFolders(Folder[] folders)
    {
        List<FolderModel> folderModels = new ArrayList<>();
        for (Folder folder : folders)
            folderModels.add(new FolderModel(folder.getName()));
        return folderModels;
    }

    public ImapWrapper copyMessageTo(ContentModel destinationContentModel) throws Exception
    {
        File file = new File(getLastResource());
        STEP(String.format("IMAP: Copy message with subject '%s' to '%s'", file.getName(), destinationContentModel.getName()));
        Folder folder = getImapStore().getFolder(Utility.removeLastSlash(getLastResource())).getParent();
        Folder destination = withImapUtil().getFolder(destinationContentModel);
        folder.open(Folder.READ_ONLY);
        destination.open(Folder.READ_WRITE);
        Message message = withImapUtil().getMessageBySubject(folder.getMessages(), file.getName());
        Message[] messages = {};
        if(message == null)
        {
            throw new MessagingException("There are no messages to be copied");
        }
        else
        {
            messages = new Message[]{message};
        }
        destination.appendMessages(messages);
        if (destinationContentModel.getProtocolLocation().startsWith(getPrefixSpace()))
            setLastResource(destinationContentModel.getProtocolLocation());
        else
            setLastResource(getPrefixSpace() + destinationContentModel.getProtocolLocation());
        return this;
    }

    /**
     * Copy all messages from current folder to destination folder 
     * 
     * @param destinationContentModel destination folder
     * @return
     * @throws Exception
     */
    public ImapWrapper copyMessagesTo(ContentModel destinationContentModel) throws Exception
    {
        STEP(String.format("IMAP: Copy messages to '%s'", destinationContentModel.getName()));
        Folder folder = getImapStore().getFolder(Utility.removeLastSlash(getLastResource()));
        Folder destination = withImapUtil().getFolder(destinationContentModel);
        folder.open(Folder.READ_ONLY);
        destination.open(Folder.READ_WRITE);
        destination.appendMessages(folder.getMessages());
        setLastResource(getPrefixSpace() + destinationContentModel.getProtocolLocation());
        return this;
    }
    
    /**
     * Rename message to the name of new content
     * 
     * @param newContent
     * @return
     * @throws Exception
     */
    public ImapWrapper renameMessageTo(ContentModel newContent) throws Exception
    {
        String fileName = withImapUtil().getObjectName(getLastResource());
        STEP(String.format("IMAP: Rename message '%s' to '%s'", fileName, newContent.getName()));
        String currentSiteName = withImapUtil().getCurrentSiteName();
        contentActions.renameContent(this.getCurrentUser().getUsername(), this.getCurrentUser().getPassword(), currentSiteName, fileName, newContent.getName());
        setLastResource(newContent.getProtocolLocation());
        return this;
    }

    /**
     * Move all messages from current folder to destination folder 
     * 
     * @param destinationContentModel destination folder
     * @return
     * @throws Exception
     */
    public ImapWrapper moveMessageTo(ContentModel destinationContentModel) throws Exception
    {
        File file = new File(getLastResource());
        STEP(String.format("IMAP: Move message '%s' to '%s'", file.getName(), destinationContentModel.getName()));
        Folder folder = getImapStore().getFolder(Utility.removeLastSlash(getLastResource())).getParent();
        Folder destination = withImapUtil().getFolder(destinationContentModel);
        folder.open(Folder.READ_ONLY);
        destination.open(Folder.READ_WRITE);
        Message message = withImapUtil().getMessageBySubject(folder.getMessages(), file.getName());
        if(message == null)
            throw new MessagingException("There is no message to be moved");
        Message[] messages = new Message[]{message};
        destination.appendMessages(messages);
        setCurrentSpace(folder.getFullName());
        deleteMessage(file.getName());
        setLastResource(getPrefixSpace() + destinationContentModel.getProtocolLocation());
        return this;
    }

    public ImapWrapper authenticateUser(UserModel userModel, String host) throws Exception
    {
        return authenticateUser(userModel, host, emailProperties.getImapPort());
    }

    public ImapWrapper authenticateUser(UserModel userModel, int port) throws Exception
    {
        return authenticateUser(userModel, emailProperties.getImapServer(), port);
    }

    public ImapWrapper authenticateUser(UserModel userModel, String host, int port) throws Exception
    {
        STEP(String.format("IMAP: Connect with %s/%s using port %d and host %s", userModel.getUsername(), userModel.getPassword(), port, host));
        Properties prop = new Properties();
        prop.put("host", host);
        prop.put("port", port);
        LOG.info("IMAP Server [{}], port [{}]", host, port);

        mailSession = Session.getInstance(prop);

        imapStore = mailSession.getStore(TestGroup.IMAP);
        try
        {
            getImapStore().connect(host, port, userModel.getUsername(), userModel.getPassword());
        }
        catch (MessagingException authEx)
        {
            LOG.info("User failed to connect to IMAP Server [{}], port [{}]", host, port);
            throw new TestConfigurationException(String.format("User failed to connect to IMAP server %s", authEx.getMessage()));
        }
        setTestUser(userModel);
        return this;
    }
       
    /**
     * Starts process of working with message flags of current content (file)
     * 
     * @return
     * @throws Exception
     */
    public MessageFlags withMessage() throws Exception
    {
        String fileName = withImapUtil().getObjectName(getLastResource());
        Folder currentFolder = withImapUtil().getCurrentFolder().getParent();
        if (!currentFolder.isOpen())
            currentFolder.open(Folder.READ_WRITE);
        return new MessageFlags(this, withImapUtil().getMessageBySubject(currentFolder.getMessages(), fileName));
    }

    /**
     * Search subject message for searched term that can contains wild cards
     * 
     * @param searchedTerm
     * @return
     * @throws Exception
     */
    public ImapWrapper searchSubjectWithWildcardsFor(String searchedTerm) throws Exception
    {
        Folder folder = getFolder();

        STEP(String.format("IMAP: Search for term '%s' in current folder '%s'", searchedTerm, folder.getName()));
        folder.open(Folder.READ_WRITE);

        @SuppressWarnings("serial")
        SearchTerm term = new SearchTerm() {
            @Override
            public boolean match(Message message) {
                try {
                    if (message.getSubject().matches(searchedTerm)) {
                        return true;
                    }
                } catch (MessagingException ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        };


        searchResults = Arrays.asList(folder.search(term));
        return this;
    }
    
    /**
     * Search subject message for searched term
     * 
     * @param searchedTerm
     * @return
     * @throws Exception
     */
    public ImapWrapper searchSubjectFor(String searchedTerm) throws Exception
    {        
        Folder folder = getFolder();
    
        STEP(String.format("IMAP: Search for term '%s' in current folder '%s'", searchedTerm, folder.getName()));
        folder.open(Folder.READ_WRITE);

        @SuppressWarnings("serial")
        SearchTerm term = new SearchTerm() {
            @Override
            public boolean match(Message message) {
                try {
                    if (message.getSubject().contains(searchedTerm)) {
                        return true;
                    }
                } catch (MessagingException ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        };
        
        searchResults = Arrays.asList(folder.search(term));
        return this;
    }
    
    /**
     * Gets all search results
     * 
     * @return list of returned messaged by searched
     */
    public List<Message> usingSearchResults()
    {
        return this.searchResults;
    }

    /**
     * Subscribe the current folder
     * 
     * @return
     * @throws Exception
     */
    public ImapWrapper subscribe() throws Exception
    {
        Folder folder = getFolder();
        folder.setSubscribed(true);
        return this;
    }

    /**
     * Unsubscribe the current folder
     * 
     * @return
     * @throws Exception
     */
    public ImapWrapper unsubscribe() throws Exception
    {
        Folder folder = getFolder();
        folder.setSubscribed(false);
        return this;
    }

    /**
     * Set the currentSpace to /Sites/siteId
     */
    public ImapWrapper usingSiteRoot(SiteModel siteModel) throws Exception
    {
        checkObjectIsInitialized(siteModel.getId(), "SiteID");
        STEP(String.format("IMAP: Navigate to site '%s/'", siteModel.getId()));
        setCurrentSpace(buildPath(getSitesPath(), siteModel.getId()));
        return this;
    }
}
