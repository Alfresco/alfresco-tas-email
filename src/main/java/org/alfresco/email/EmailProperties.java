package org.alfresco.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:default.properties")
@PropertySource(value = "classpath:${environment}.properties", ignoreResourceNotFound = true)
public class EmailProperties
{
    @Value("${imap.server}")
    private String imapServer;

    @Value("${imap.port:143}")
    private int imapPort;

    /*
     * The IMAPS secure port
     */
    @Value("${imaps.port:993}")
    private int imapSecurePort;

    @Value("${smtp.server}")
    private String smtpServer;

    @Value("${smtp.port:25}")
    private int smtpPort;
    
    @Value("${smtp.authentication.enabled}")
    private boolean isSmtpAuthEnabled;
    
    @Value("${smtp.tsl.enabled}")
    private boolean isSmtpTSLEnabled;

    @Value("${mail.encoding}")
    private String mailEncoding;

    @Value("${mail.from.default}")
    private String mailFromDefault;

    @Value("${mail.from.enabled}")
    private boolean mailFromEnabled;

    @Value("${mail.host}")
    private String mailHost;

    @Value("${mail.password}")
    private String mailPassword;

    @Value("${mail.port}")
    private int mailPort;

    @Value("${mail.protocol}")
    private String mailProtocol;

    @Value("${mail.smtp.auth}")
    private boolean mailSmtpAuth;

    @Value("${mail.smtp.debug}")
    private boolean mailSmtpDebug;

    @Value("${mail.smtp.starttls.enable}")
    private boolean mailSmtpStartTTLSEnable;

    @Value("${mail.smtp.timeout}")
    private long mailSmtpTimeout;

    @Value("${mail.smtps.auth}")
    private boolean mailSmtpsAuth;

    @Value("${mail.smtps.starttls.enable}")
    private boolean mailSmtpsStartTTLSEnable;

    @Value("${mail.username}")
    private String mailUsername;

    @Value("${jmx.useJolokiaAgent}")
    private boolean useJolokiaAgent;
    
    public boolean isSmtpAuthEnabled()
    {
        return isSmtpAuthEnabled;
    }
    
    public boolean isSmtpTSLEnabled()
    {
        return isSmtpTSLEnabled;
    }

    public String getImapServer()
    {
        return imapServer;
    }

    public int getImapPort()
    {
        return imapPort;
    }

    public int getImapSecurePort()
    {
        return imapSecurePort;
    }

    public String getSmtpServer()
    {
        return smtpServer;
    }

    public int getSmtpPort()
    {
        return smtpPort;
    }
    
    public void setSmtpPort(int smtpPort)
    {
        this.smtpPort = smtpPort;
    }

    public String getMailEncoding() {
        return mailEncoding;
    }

    public String getMailFromDefault() {
        return mailFromDefault;
    }

    public boolean isMailFromEnabled() {
        return mailFromEnabled;
    }

    public String getMailHost() {
        return mailHost;
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public int getMailPort() {
        return mailPort;
    }

    public String getMailProtocol() {
        return mailProtocol;
    }

    public boolean isMailSmtpAuth() {
        return mailSmtpAuth;
    }

    public boolean isMailSmtpDebug() {
        return mailSmtpDebug;
    }

    public boolean isMailSmtpStartTTLSEnable() {
        return mailSmtpStartTTLSEnable;
    }

    public long getMailSmtpTimeout() {
        return mailSmtpTimeout;
    }

    public boolean isMailSmtpsAuth() {
        return mailSmtpsAuth;
    }

    public boolean isMailSmtpsStartTTLSEnable() {
        return mailSmtpsStartTTLSEnable;
    }

    public String getMailUsername() {
        return mailUsername;
    }

    public boolean getUseJolokiaAgent()
    {
        return useJolokiaAgent;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
    {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
