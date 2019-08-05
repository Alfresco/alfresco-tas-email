package org.alfresco.email.dsl;

import static org.alfresco.email.dsl.JmxUtil.IMAP_CONFIG;
import static org.alfresco.email.dsl.JmxUtil.INBOUND_SMTP_CONFIG;
import static org.alfresco.email.dsl.JmxUtil.OUTBOUND_SMTP_CONFIG;
import static org.alfresco.utility.report.log.Step.STEP;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.email.EmailProperties;

public class ServerConfiguration
{
    private static Map<String, String> inboundSmtpSettings;
    private static Map<String, String> outboundSmtpSettings;
    private static Map<String, String> imapSettings;

    public static void save(JmxUtil jmxUtil, EmailProperties emailProperties) throws Exception
    {
        synchronized (ServerConfiguration.class)
        {
            if (inboundSmtpSettings == null)
            {
                STEP("SERVER: Save Configuration");

                inboundSmtpSettings = new HashMap<>();
                inboundSmtpSettings.put("email.server.enabled", jmxUtil.getSmtpServerConfigurationStatus());
                inboundSmtpSettings.put("email.server.auth.enabled", "true");
                inboundSmtpSettings.put("email.server.port", String.valueOf(jmxUtil.getSmtpServerPort()));
                inboundSmtpSettings.put("email.inbound.unknownUser", jmxUtil.getSmtpUnknownUser());
                inboundSmtpSettings.put("email.handler.folder.overwriteDuplicates", jmxUtil.getSmtpOverwriteDuplicates());
                inboundSmtpSettings.put("email.server.connections.max", String.valueOf(100));
                inboundSmtpSettings.put("email.server.allowed.senders", jmxUtil.getSmtpAllowedSenders());
                inboundSmtpSettings.put("email.server.blocked.senders", jmxUtil.getSmtpBlockedSenders());
                inboundSmtpSettings.put("email.inbound.emailContributorsAuthority", jmxUtil.getSmtpEmailAuthenticationGroup());
                inboundSmtpSettings.put("email.server.domain", jmxUtil.getSmtpEmailServerDomain());
                inboundSmtpSettings.put("email.server.enableTLS", jmxUtil.getSmtpTlsConfigurationStatus());
                inboundSmtpSettings.put("email.server.hideTLS", jmxUtil.getSmtpTlsHiddenConfigurationStatus());
                inboundSmtpSettings.put("email.server.requireTLS", jmxUtil.getSmtpRequireTlsConfigurationStatus());

                outboundSmtpSettings = new HashMap<>();
                outboundSmtpSettings.put("mail.encoding", emailProperties.getMailEncoding());
                outboundSmtpSettings.put("mail.from.default", emailProperties.getMailFromDefault());
                outboundSmtpSettings.put("mail.from.enabled", String.valueOf(emailProperties.isMailFromEnabled()));
                outboundSmtpSettings.put("mail.host", emailProperties.getMailHost());
                outboundSmtpSettings.put("mail.port", String.valueOf(emailProperties.getMailPort()));
                outboundSmtpSettings.put("mail.protocol", emailProperties.getMailProtocol());
                outboundSmtpSettings.put("mail.smtp.auth", String.valueOf(emailProperties.isMailSmtpAuth()));
                outboundSmtpSettings.put("mail.smtp.debug", String.valueOf(emailProperties.isMailSmtpDebug()));
                outboundSmtpSettings.put("mail.smtp.starttls.enable", String.valueOf(emailProperties.isMailSmtpStartTTLSEnable()));
                outboundSmtpSettings.put("mail.smtp.timeout", String.valueOf(emailProperties.getMailSmtpTimeout()));
                outboundSmtpSettings.put("mail.smtps.auth", String.valueOf(emailProperties.isMailSmtpsAuth()));
                outboundSmtpSettings.put("mail.smtps.starttls.enable", String.valueOf(emailProperties.isMailSmtpsStartTTLSEnable()));
                outboundSmtpSettings.put("mail.username", emailProperties.getMailUsername());
                outboundSmtpSettings.put("mail.testmessage.send", "false");
                outboundSmtpSettings.put("mail.testmessage.subject", jmxUtil.getMailTestMessageSubject());
                outboundSmtpSettings.put("mail.testmessage.text", jmxUtil.getMailTestMessageText());
                outboundSmtpSettings.put("mail.testmessage.to", jmxUtil.getMailTestMessageTo());

                imapSettings = new HashMap<>();
                imapSettings.put("imap.server.imap.enabled", "true");
                imapSettings.put("imap.server.imaps.enabled", "false");
                imapSettings.put("imap.server.port", String.valueOf(emailProperties.getImapPort()));
                imapSettings.put("imap.server.enabled", jmxUtil.getImapServerConfigurationStatus());
                imapSettings.put("imap.server.imap.enabled", jmxUtil.getImapProtocolConfigurationStatus());
                imapSettings.put("imap.server.imaps.enabled", jmxUtil.getImapsServerConfigurationStatus());
                imapSettings.put("imap.server.imaps.port", String.valueOf(jmxUtil.getImapsServerPort()));
                imapSettings.put("imap.mail.from.default", jmxUtil.getImapFromDefaultAddress());
                imapSettings.put("imap.mail.to.default", jmxUtil.getImapToDefaultAddress());

                restore(jmxUtil);
            }
        }
    }

    public static void restore(JmxUtil jmxUtil) throws Exception
    {
        synchronized (ServerConfiguration.class)
        {
            STEP("SERVER: Restore Configuration");

            for (Map.Entry<String, String> entry : inboundSmtpSettings.entrySet())
                if (!jmxUtil.jmx.readProperty(INBOUND_SMTP_CONFIG, entry.getKey()).toString().equals(entry.getValue()))
                    jmxUtil.jmx.writeProperty(INBOUND_SMTP_CONFIG, entry.getKey(), entry.getValue());

            for (Map.Entry<String, String> entry : outboundSmtpSettings.entrySet())
                if (!jmxUtil.jmx.readProperty(OUTBOUND_SMTP_CONFIG, entry.getKey()).toString().equals(entry.getValue()))
                    jmxUtil.jmx.writeProperty(OUTBOUND_SMTP_CONFIG, entry.getKey(), entry.getValue());

            for (Map.Entry<String, String> entry : imapSettings.entrySet())
                if (!jmxUtil.jmx.readProperty(IMAP_CONFIG, entry.getKey()).toString().equals(entry.getValue()))
                    jmxUtil.jmx.writeProperty(IMAP_CONFIG, entry.getKey(), entry.getValue());
        }
    }
}
