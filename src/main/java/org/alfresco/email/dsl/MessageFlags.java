package org.alfresco.email.dsl;

import static org.alfresco.utility.report.log.Step.STEP;

import org.alfresco.email.ImapWrapper;

import jakarta.mail.Flags;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;

/**
 * DSL pertaining only to {@link jakarta.mail.Message} flags
 */
public class MessageFlags
{
    private ImapWrapper imapWrapper;
    private Message message;

    public MessageFlags(ImapWrapper imapWrapper, Message message)
    {
        this.imapWrapper = imapWrapper;
        this.message = message;
    }

    public ImapWrapper updateFlags()
    {
        return imapWrapper;
    }

    public MessageFlags setAnsweredFlag() throws MessagingException
    {
        STEP(String.format("IMAP: Add 'ANSWERED' flag to message '%s'", message.getSubject()));
        this.message.setFlag(Flags.Flag.ANSWERED, true);
        return this;
    }

    public MessageFlags removeAnsweredFlag() throws MessagingException
    {
        STEP(String.format("IMAP: Remove 'ANSWERED' flag from message '%s'", message.getSubject()));
        this.message.setFlag(Flags.Flag.ANSWERED, false);
        return this;
    }

    public MessageFlags setDeletedFlag() throws MessagingException
    {
        STEP(String.format("IMAP: Add 'DELETED' flag to message '%s'", message.getSubject()));
        this.message.setFlag(Flags.Flag.DELETED, true);
        return this;
    }

    public MessageFlags removeDeletedFlag() throws MessagingException
    {
        STEP(String.format("IMAP: Remove 'DELETED' flag from message '%s'", message.getSubject()));
        this.message.setFlag(Flags.Flag.DELETED, false);
        return this;
    }

    public MessageFlags setDraftFlag() throws MessagingException
    {
        STEP(String.format("IMAP: Add 'DRAFT' flag to message '%s'", message.getSubject()));
        this.message.setFlag(Flags.Flag.DRAFT, true);
        return this;
    }

    public MessageFlags removeDraftFlag() throws MessagingException
    {
        STEP(String.format("IMAP: Remove 'DRAFT' flag from message '%s'", message.getSubject()));
        this.message.setFlag(Flags.Flag.DRAFT, false);
        return this;
    }

    public MessageFlags setRecentFlag() throws MessagingException
    {
        STEP(String.format("IMAP: Add 'RECENT' flag to message '%s'", message.getSubject()));
        this.message.setFlag(Flags.Flag.RECENT, true);
        return this;
    }

    public MessageFlags removeRecentFlag() throws MessagingException
    {
        STEP(String.format("IMAP: Remove 'RECENT' flag from message '%s'", message.getSubject()));
        this.message.setFlag(Flags.Flag.RECENT, false);
        return this;
    }

    public MessageFlags setSeenFlag() throws MessagingException
    {
        STEP(String.format("IMAP: Add 'SEEN' flag to message '%s'", message.getSubject()));
        this.message.setFlag(Flags.Flag.SEEN, true);
        return this;
    }

    public MessageFlags removeSeenFlag() throws MessagingException
    {
        STEP(String.format("IMAP: Remove 'SEEN' flag from message '%s'", message.getSubject()));
        this.message.setFlag(Flags.Flag.SEEN, false);
        return this;
    }

    public MessageFlags setFlags(Flags.Flag... flagsList)  throws MessagingException
    {
        Flags flags = new Flags();
        for (int i = 0; i < flagsList.length; i++)
        {
            STEP(String.format("IMAP: Add '%s' flag to message '%s'",
                    imapWrapper.withImapUtil().getMessageFlagName(flagsList[i]), message.getSubject()));
            flags.add(flagsList[i]);
        }
        this.message.setFlags(flags, true);
        return this;
    }

    public MessageFlags removeFlags(Flags.Flag... flagsList)  throws MessagingException
    {
        Flags flags = new Flags();
        for (int i = 0; i < flagsList.length; i++)
        {
            STEP(String.format("IMAP: Remove '%s' flag from message '%s'",
                    imapWrapper.withImapUtil().getMessageFlagName(flagsList[i]), message.getSubject()));
            flags.add(flagsList[i]);
        }
        this.message.setFlags(flags, false);
        return this;
    }
}
