package org.alfresco.email.dsl;

import static org.alfresco.utility.report.log.Step.STEP;

import java.util.Arrays;

import org.alfresco.utility.dsl.DSLProtocol;
import org.alfresco.utility.network.Jmx;
import org.alfresco.utility.network.JmxClient;
import org.alfresco.utility.network.JmxJolokiaProxyClient;

/**
 * DSL for interacting with JMX (using direct JMX call see {@link JmxClient} or {@link JmxJolokiaProxyClient}
 */
public class JmxUtil
{
    @SuppressWarnings("unused")
    private DSLProtocol<?> protocol;
    protected Jmx jmx;

    protected static String INBOUND_SMTP_CONFIG = "Alfresco:Type=Configuration,Category=email,id1=inbound";
    protected static String OUTBOUND_SMTP_CONFIG = "Alfresco:Type=Configuration,Category=email,id1=outbound";
    protected static String IMAP_CONFIG = "Alfresco:Type=Configuration,Category=imap,id1=default";

    public JmxUtil(DSLProtocol<?> protocol, Jmx jmx)
    {
        this.protocol = protocol;
        this.jmx = jmx;
    }

    public String getSmtpServerConfigurationStatus() throws Exception
    {
        STEP("Inbound SMTP: Get Server Configuration Status");
        return jmx.readProperty(INBOUND_SMTP_CONFIG, "email.server.enabled").toString();
    }

    public void enableSmtpEmailServer() throws Exception
    {
        STEP("Inbound SMTP: Enable Email Server");
        jmx.writeProperty(INBOUND_SMTP_CONFIG, "email.server.enabled", Boolean.TRUE.toString());
    }

    public void disableSmtpEmailServer() throws Exception
    {
        STEP("Inbound SMTP: Disable Email Server");
        jmx.writeProperty(INBOUND_SMTP_CONFIG, "email.server.enabled", Boolean.FALSE.toString());
    }

    public void enableSmtpAuthentication() throws Exception
    {
        STEP("Inbound SMTP: Enable SMTP Authentication");
        jmx.writeProperty(INBOUND_SMTP_CONFIG, "email.server.auth.enabled", Boolean.TRUE.toString());
    }

    public void disableSmtpAuthentication() throws Exception
    {
        STEP("Inbound SMTP: Disable SMTP Authentication");
        jmx.writeProperty(INBOUND_SMTP_CONFIG, "email.server.auth.enabled", Boolean.FALSE.toString());
    }

    public String getSmtpAuthenticationConfigurationStatus() throws Exception
    {
        STEP("Inbound SMTP: Get SMTP Authentication Configuration Status");
        return jmx.readProperty(INBOUND_SMTP_CONFIG, "email.server.auth.enabled").toString();
    }

    public void updateSmtpServerPort(int newPort) throws Exception
    {
        updateSmtpServerPort(String.valueOf(newPort));
    }
    
    public void updateSmtpServerPort(String newPort) throws Exception
    {
        STEP(String.format("Inbound SMTP: Update Server Port to '%s'", newPort));
        jmx.writeProperty(INBOUND_SMTP_CONFIG, "email.server.port", newPort);
    }

    public int getSmtpServerPort() throws Exception
    {
        STEP("Inbound SMTP: Get Server Port");
        return Integer.parseInt(jmx.readProperty(INBOUND_SMTP_CONFIG, "email.server.port").toString());
    }

    public void updateSmtpUnknownUser(String newUnknownUser) throws Exception
    {
        STEP(String.format("Inbound SMTP: Update Server Unknown User to '%s'", newUnknownUser));
        jmx.writeProperty(INBOUND_SMTP_CONFIG, "email.inbound.unknownUser", newUnknownUser);
    }

    public String getSmtpUnknownUser() throws Exception
    {
        STEP("Inbound SMTP: Get Server Unknown User");
        return jmx.readProperty(INBOUND_SMTP_CONFIG, "email.inbound.unknownUser").toString();
    }

    public void enableSmtpOverwriteDuplicates() throws Exception
    {
        STEP("Inbound SMTP: Enable Server Overwrite Duplicates");
        jmx.writeProperty(INBOUND_SMTP_CONFIG, "email.handler.folder.overwriteDuplicates", Boolean.TRUE.toString());
    }

    public void disableSmtpOverwriteDuplicates() throws Exception
    {
        STEP("Inbound SMTP: Disable Server Overwrite Duplicates");
        jmx.writeProperty(INBOUND_SMTP_CONFIG, "email.handler.folder.overwriteDuplicates", Boolean.FALSE.toString());
    }

    public String getSmtpOverwriteDuplicates() throws Exception
    {
        STEP("Inbound SMTP: Get Server Overwrite Duplicates Configuration Status");
        return jmx.readProperty(INBOUND_SMTP_CONFIG, "email.handler.folder.overwriteDuplicates").toString();
    }

    public void updateSmtpMaximumServerConnections(int newConnectionsNumber) throws Exception
    {
        STEP(String.format("Inbound SMTP: Update Maximum Server Connections to '%s'", newConnectionsNumber));
        jmx.writeProperty(INBOUND_SMTP_CONFIG, "email.server.connections.max", String.valueOf(newConnectionsNumber));
    }

    public int getSmtpMaximumServerConnections() throws Exception
    {
        STEP("Inbound SMTP: Get Maximum Server Connections");
        return Integer.parseInt(jmx.readProperty(INBOUND_SMTP_CONFIG, "email.server.connections.max").toString());
    }

    public void updateSmtpAllowedSenders(String... newAllowedSenders) throws Exception
    {
        String allowedSenders = Arrays.asList(newAllowedSenders).toString();
        allowedSenders = allowedSenders.substring(1, allowedSenders.length() - 1);
        STEP(String.format("Inbound SMTP: Update Server Allowed Senders to '%s'", allowedSenders));
        jmx.writeProperty(INBOUND_SMTP_CONFIG, "email.server.allowed.senders", allowedSenders);
    }

    public String getSmtpAllowedSenders() throws Exception
    {
        STEP("Inbound SMTP: Get Server Allowed Senders");
        return jmx.readProperty(INBOUND_SMTP_CONFIG, "email.server.allowed.senders").toString();
    }

    public void updateSmtpBlockedSenders(String... newBlockedSenders) throws Exception
    {
        String blockedSenders = Arrays.asList(newBlockedSenders).toString();
        blockedSenders = blockedSenders.substring(1, blockedSenders.length() - 1);
        STEP(String.format("Inbound SMTP: Update Server Blocked Senders to '%s'", blockedSenders));
        jmx.writeProperty(INBOUND_SMTP_CONFIG, "email.server.blocked.senders", blockedSenders);
    }

    public String getSmtpBlockedSenders() throws Exception
    {
        STEP("Inbound SMTP: Get Server Blocked Senders");
        return jmx.readProperty(INBOUND_SMTP_CONFIG, "email.server.blocked.senders").toString();
    }

    public void updateSmtpEmailAuthenticationGroup(String newEmailContributorsAuthority) throws Exception
    {
        STEP(String.format("Inbound SMTP: Update Email Authentication Group to '%s'", newEmailContributorsAuthority));
        jmx.writeProperty(INBOUND_SMTP_CONFIG, "email.inbound.emailContributorsAuthority", newEmailContributorsAuthority);
    }

    public String getSmtpEmailAuthenticationGroup() throws Exception
    {
        STEP("Inbound SMTP: Get Email Authentication Group");
        return jmx.readProperty(INBOUND_SMTP_CONFIG, "email.inbound.emailContributorsAuthority").toString();
    }

    public void updateSmtpEmailServerDomain(String newDomain) throws Exception
    {
        STEP(String.format("Inbound SMTP: Update Email Server Domain to '%s'", newDomain));
        jmx.writeProperty(INBOUND_SMTP_CONFIG, "email.server.domain", newDomain);
    }

    public String getSmtpEmailServerDomain() throws Exception
    {
        STEP("Inbound SMTP: Get Email Server Domain");
        return jmx.readProperty(INBOUND_SMTP_CONFIG, "email.server.domain").toString();
    }

    public void enableSmtpTls() throws Exception
    {
        STEP("Inbound SMTP: Enable Server TLS");
        jmx.writeProperty(INBOUND_SMTP_CONFIG, "email.server.enableTLS", Boolean.TRUE.toString());
    }

    public void disableSmtpTls() throws Exception
    {
        STEP("Inbound SMTP: Disable Server TLS");
        jmx.writeProperty(INBOUND_SMTP_CONFIG, "email.server.enableTLS", Boolean.FALSE.toString());
    }

    public String getSmtpTlsConfigurationStatus() throws Exception
    {
        STEP("Inbound SMTP: Get Server TLS Configuration Status");
        return jmx.readProperty(INBOUND_SMTP_CONFIG, "email.server.enableTLS").toString();
    }

    public void enableSmtpTlsHidden() throws Exception
    {
        STEP("Inbound SMTP: Enable Server TLS Hidden");
        jmx.writeProperty(INBOUND_SMTP_CONFIG, "email.server.hideTLS", Boolean.TRUE.toString());
    }

    public void disableSmtpTlsHidden() throws Exception
    {
        STEP("Inbound SMTP: Disable Server TLS Hidden");
        jmx.writeProperty(INBOUND_SMTP_CONFIG, "email.server.hideTLS", Boolean.FALSE.toString());
    }

    public String getSmtpTlsHiddenConfigurationStatus() throws Exception
    {
        STEP("Inbound SMTP: Get Server TLS Hidden Configuration Status");
        return jmx.readProperty(INBOUND_SMTP_CONFIG, "email.server.hideTLS").toString();
    }

    public void enableSmtpRequireTls() throws Exception
    {
        STEP("Inbound SMTP: Enable Server Require TLS");
        jmx.writeProperty(INBOUND_SMTP_CONFIG, "email.server.requireTLS", Boolean.TRUE.toString());
    }

    public void disableSmtpRequireTls() throws Exception
    {
        STEP("Inbound SMTP: Disable Server Require TLS");
        jmx.writeProperty(INBOUND_SMTP_CONFIG, "email.server.requireTLS", Boolean.FALSE.toString());
    }

    public String getSmtpRequireTlsConfigurationStatus() throws Exception
    {
        STEP("Inbound SMTP: Get Server Require TLS Configuration Status");
        return jmx.readProperty(INBOUND_SMTP_CONFIG, "email.server.requireTLS").toString();
    }

    public String getImapProtocolConfigurationStatus() throws Exception
    {
        STEP("IMAP: Get IMAP Protocol Configuration Status");
        return jmx.readProperty(IMAP_CONFIG, "imap.server.imap.enabled").toString();
    }
    
    public String getImapServerConfigurationStatus() throws Exception
    {
        STEP("IMAP: Get Server Configuration Status");
        return jmx.readProperty(IMAP_CONFIG, "imap.server.enabled").toString();
    }
    
    public void enableImapServer() throws Exception
    {
        STEP("IMAP: Enable Email Server");
        jmx.writeProperty(IMAP_CONFIG, "imap.server.enabled", Boolean.TRUE.toString());
    }

    public void disableImapServer() throws Exception
    {
        STEP("IMAP: Disable Email Server");
        jmx.writeProperty(IMAP_CONFIG, "imap.server.enabled", Boolean.FALSE.toString());
    }

    public void enableImapProtocol() throws Exception
    {
        STEP("IMAP: Enable IMAP Protocol");
        jmx.writeProperty(IMAP_CONFIG, "imap.server.imap.enabled", Boolean.TRUE.toString());
    }

    public void disableImapProtocol() throws Exception
    {
        STEP("IMAP: Disable Email Server");
        jmx.writeProperty(IMAP_CONFIG, "imap.server.imap.enabled", Boolean.FALSE.toString());
    }

    public void updateImapServerPort(int newPort) throws Exception
    {
        STEP(String.format("IMAP: Update Server Port to '%s'", newPort));
        jmx.writeProperty(IMAP_CONFIG, "imap.server.port", String.valueOf(newPort));
    }

    public int getImapServerPort() throws Exception
    {
        STEP("IMAP: Get Server Port");
        return Integer.parseInt(jmx.readProperty(IMAP_CONFIG, "imap.server.port").toString());
    }

    public String getImapsServerConfigurationStatus() throws Exception
    {
        STEP("IMAPS: Get Server Configuration Status");
        return jmx.readProperty(IMAP_CONFIG, "imap.server.imaps.enabled").toString();
    }

    public void enableImapsServer() throws Exception
    {
        STEP("IMAPS: Enable Email Server");
        jmx.writeProperty(IMAP_CONFIG, "imap.server.imaps.enabled", Boolean.TRUE.toString());
    }

    public void disableImapsServer() throws Exception
    {
        STEP("IMAPS: Disable Email Server");
        jmx.writeProperty(IMAP_CONFIG, "imap.server.imaps.enabled", Boolean.FALSE.toString());
    }

    public void updateImapsServerPort(int newPort) throws Exception
    {
        STEP(String.format("IMAPS: Update Server Port to '%s'", newPort));
        jmx.writeProperty(IMAP_CONFIG, "imap.server.imaps.port", String.valueOf(newPort));
    }

    public int getImapsServerPort() throws Exception
    {
        STEP("IMAPS: Get Server Port");
        return Integer.parseInt(jmx.readProperty(IMAP_CONFIG, "imap.server.imaps.port").toString());
    }

    public void updateImapFromDefaultAddress(String newFrom) throws Exception
    {
        STEP(String.format("IMAP: Update Mail From Default Address to '%s'", newFrom));
        jmx.writeProperty(IMAP_CONFIG, "imap.mail.from.default", newFrom);
    }

    public String getImapFromDefaultAddress() throws Exception
    {
        STEP("IMAP: Get Mail From Default Address");
        return jmx.readProperty(IMAP_CONFIG, "imap.mail.from.default").toString();
    }

    public void updateImapToDefaultAddress(String newTo) throws Exception
    {
        STEP(String.format("IMAP: Update Mail To Default Address to '%s'", newTo));
        jmx.writeProperty(IMAP_CONFIG, "imap.mail.to.default", newTo);
    }

    public String getImapToDefaultAddress() throws Exception
    {
        STEP("IMAP: Get Mail To Default Address");
        return jmx.readProperty(IMAP_CONFIG, "imap.mail.to.default").toString();
    }

    public String getMailEncoding() throws Exception
    {
        STEP("Outbound SMTP: Get Mail Encoding");
        return jmx.readProperty(OUTBOUND_SMTP_CONFIG, "mail.encoding").toString();
    }

    public void updateMailEncoding(String newEncoding) throws Exception
    {
        STEP(String.format("Outbound SMTP: Update Mail Encoding to '%s'", newEncoding));
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.encoding", newEncoding);
    }

    public String getMailFromDefault() throws Exception
    {
        STEP("Outbound SMTP: Get Default Sender's Address");
        return jmx.readProperty(OUTBOUND_SMTP_CONFIG, "mail.from.default").toString();
    }

    public void updateMailFromDefault(String newFromDefault) throws Exception
    {
        STEP(String.format("Outbound SMTP: Update Default Sender's Address to '%s'", newFromDefault));
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.from.default", newFromDefault);
    }

    public String getMailFromConfigurationStatus() throws Exception
    {
        STEP("Outbound SMTP: Get Editable Sender Address Status");
        return jmx.readProperty(OUTBOUND_SMTP_CONFIG, "mail.from.enabled").toString();
    }

    public void enableMailFrom() throws Exception
    {
        STEP("Outbound SMTP: Enable Editable Sender Address");
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.from.enabled", Boolean.TRUE.toString());
    }

    public void disableMailFrom() throws Exception
    {
        STEP("Outbound SMTP: Disable Editable Sender Address");
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.from.enabled", Boolean.FALSE.toString());
    }

    public String getMailHost() throws Exception
    {
        STEP("Outbound SMTP: Get Mail Host");
        return jmx.readProperty(OUTBOUND_SMTP_CONFIG, "mail.host").toString();
    }

    public void updateMailHost(String newHost) throws Exception
    {
        STEP(String.format("Outbound SMTP: Update Mail Host to '%s'", newHost));
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.host", newHost);
    }

    public String getMailPassword() throws Exception
    {
        STEP("Outbound SMTP: Get Mail Password");
        return jmx.readProperty(OUTBOUND_SMTP_CONFIG, "mail.password").toString();
    }

    public void updateMailPassword(String newPassword) throws Exception
    {
        STEP(String.format("Outbound SMTP: Update Mail Password to '%s'", newPassword));
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.password", newPassword);
    }

    public int getMailPort() throws Exception
    {
        STEP("Outbound SMTP: Get Mail Port");
        return Integer.parseInt(jmx.readProperty(OUTBOUND_SMTP_CONFIG, "mail.port").toString());
    }

    public void updateMailPort(int newPort) throws Exception
    {
        STEP(String.format("Outbound SMTP: Update Mail Port to '%s'", newPort));
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.port", String.valueOf(newPort));
    }

    public void updateMailPort(String newPort) throws Exception
    {
        STEP(String.format("Outbound SMTP: Update Mail Port to '%s'", newPort));
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.port", newPort);
    }

    public String getMailProtocol() throws Exception
    {
        STEP("Outbound SMTP: Get Mail Protocol");
        return jmx.readProperty(OUTBOUND_SMTP_CONFIG, "mail.protocol").toString();
    }

    public void updateMailProtocol(String newProtocol) throws Exception
    {
        STEP(String.format("Outbound SMTP: Update Mail Protocol to '%s'", newProtocol));
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.protocol", newProtocol);
    }

    public String getMailSMTPAuthConfigurationStatus() throws Exception
    {
        STEP("Outbound SMTP: Get Mail SMTP Auth Configuration Status");
        return jmx.readProperty(OUTBOUND_SMTP_CONFIG, "mail.smtp.auth").toString();
    }

    public void enableMailSMTPAuth() throws Exception
    {
        STEP("Outbound SMTP: Enable Mail SMTP Auth");
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.smtp.auth", Boolean.TRUE.toString());
    }

    public void disableMailSMTPAuth() throws Exception
    {
        STEP("Outbound SMTP: Disable Mail SMTP Auth");
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.smtp.auth", Boolean.FALSE.toString());
    }

    public String getMailDebugConfigurationStatus() throws Exception
    {
        STEP("Outbound SMTP: Get Mail SMTP Debug Configuration Status");
        return jmx.readProperty(OUTBOUND_SMTP_CONFIG, "mail.smtp.debug").toString();
    }

    public void enableMailDebug() throws Exception
    {
        STEP("Outbound SMTP: Enable Mail SMTP Debug");
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.smtp.debug", Boolean.TRUE.toString());
    }

    public void disableMailDebug() throws Exception
    {
        STEP("Outbound SMTP: Disable Mail SMTP Debug");
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.smtp.debug", Boolean.FALSE.toString());
    }

    public String getMailSMTPStartTTLSConfigurationStatus() throws Exception
    {
        STEP("Outbound SMTP: Get Mail SMTP StartTTLS Configuration Status");
        return jmx.readProperty(OUTBOUND_SMTP_CONFIG, "mail.smtp.starttls.enable").toString();
    }

    public void enableMailSMTPStartTTLS() throws Exception
    {
        STEP("Outbound SMTP: Enable Mail SMTP StartTTLS");
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.smtp.starttls.enable", Boolean.TRUE.toString());
    }

    public void disableMailSMTPStartTTLS() throws Exception
    {
        STEP("Outbound SMTP: Disable Mail SMTP StartTTLS");
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.smtp.starttls.enable", Boolean.FALSE.toString());
    }

    public long getMailTimeout() throws Exception
    {
        STEP("Outbound SMTP: Get Mail SMTP Timeout");
        return Long.parseLong(jmx.readProperty(OUTBOUND_SMTP_CONFIG, "mail.smtp.timeout").toString());
    }

    public void updateMailTimeout(long newTimeout) throws Exception
    {
        STEP(String.format("Outbound SMTP: Update Mail SMTP Timeout to '%s'", newTimeout));
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.smtp.timeout", String.valueOf(newTimeout));
    }

    public String getMailSMTPSAuthConfigurationStatus() throws Exception
    {
        STEP("Outbound SMTP: Get Mail SMTPS Auth Configuration Status");
        return jmx.readProperty(OUTBOUND_SMTP_CONFIG, "mail.smtps.auth").toString();
    }

    public void enableMailSMTPSAuth() throws Exception
    {
        STEP("Outbound SMTP: Enable Mail SMTPS Auth");
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.smtps.auth", Boolean.TRUE.toString());
    }

    public void disableMailSMTPSAuth() throws Exception
    {
        STEP("Outbound SMTP: Disable Mail SMTPS Auth");
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.smtps.auth", Boolean.FALSE.toString());
    }

    public String getMailSMTPSStartTTLSConfigurationStatus() throws Exception
    {
        STEP("Outbound SMTP: Get Mail SMTPS StartTTLS Configuration Status");
        return jmx.readProperty(OUTBOUND_SMTP_CONFIG, "mail.smtps.starttls.enable").toString();
    }

    public void enableMailSMTPSStartTTLS() throws Exception
    {
        STEP("Outbound SMTP: Enable Mail SMTPS StartTTLS");
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.smtps.starttls.enable", Boolean.TRUE.toString());
    }

    public void disableMailSMTPSStartTTLS() throws Exception
    {
        STEP("Outbound SMTP: Disable Mail SMTPS StartTTLS");
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.smtps.starttls.enable", Boolean.FALSE.toString());
    }

    public String getMailTestMessageSendConfigurationStatus() throws Exception
    {
        STEP("Outbound SMTP: Get Mail Test Message Send Configuration Status");
        return jmx.readProperty(OUTBOUND_SMTP_CONFIG, "mail.testmessage.send").toString();
    }

    public void enableMailTestMessageSend() throws Exception
    {
        STEP("Outbound SMTP: Enable Mail Test Message Send");
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.testmessage.send", Boolean.TRUE.toString());
    }

    public void disableMailTestMessageSend() throws Exception
    {
        STEP("Outbound SMTP: Disable Mail Test Message Send");
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.testmessage.send", Boolean.FALSE.toString());
    }

    public String getMailTestMessageSubject() throws Exception
    {
        STEP("Outbound SMTP: Get Mail Test Message Subject");
        return jmx.readProperty(OUTBOUND_SMTP_CONFIG, "mail.testmessage.subject").toString();
    }

    public void updateMailTestMessageSubject(String newSubject) throws Exception
    {
        STEP(String.format("Outbound SMTP: Update Mail Test Message Subject to '%s'", newSubject));
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.testmessage.subject", newSubject);
    }

    public String getMailTestMessageText() throws Exception
    {
        STEP("Outbound SMTP: Get Mail Test Message Text");
        return jmx.readProperty(OUTBOUND_SMTP_CONFIG, "mail.testmessage.text").toString();
    }

    public void updateMailTestMessageText(String newText) throws Exception
    {
        STEP(String.format("Outbound SMTP: Update Mail Test Message Text to '%s'", newText));
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.testmessage.text", newText);
    }

    public String getMailTestMessageTo() throws Exception
    {
        STEP("Outbound SMTP: Get Mail Test Message TO");
        return jmx.readProperty(OUTBOUND_SMTP_CONFIG, "mail.testmessage.to").toString();
    }

    public void updateMailTestMessageTo(String newTo) throws Exception
    {
        STEP(String.format("Outbound SMTP: Update Mail Test Message TO to '%s'", newTo));
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.testmessage.to", newTo);
    }

    public String getMailUsername() throws Exception
    {
        STEP("Outbound SMTP: Get Mail Username");
        return jmx.readProperty(OUTBOUND_SMTP_CONFIG, "mail.username").toString();
    }

    public void updateMailUsername(String newUsername) throws Exception
    {
        STEP(String.format("Outbound SMTP: Update Mail Username to '%s'", newUsername));
        jmx.writeProperty(OUTBOUND_SMTP_CONFIG, "mail.username", newUsername);
    }

    public boolean getSystemUsagesConfigurationStatus() throws Exception
    {
        return Boolean.parseBoolean(jmx.readProperty("Alfresco:Name=GlobalProperties", "system.usages.enabled").toString());
    }

    public boolean getNotificationEmailSiteInviteConfigurationStatus() throws Exception
    {
        return Boolean.parseBoolean(jmx.readProperty("Alfresco:Name=GlobalProperties", "notification.email.siteinvite").toString());
    }
}
