package org.alfresco.email;

import static org.alfresco.utility.report.log.Step.STEP;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;

import org.alfresco.email.dsl.JmxUtil;
import org.alfresco.email.dsl.smtp.ComposeMessage;
import org.alfresco.email.dsl.smtp.SmtpAssertion;
import org.alfresco.utility.dsl.DSLProtocol;
import org.alfresco.utility.exception.TestConfigurationException;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(value = "prototype")
public class SmtpWrapper extends DSLProtocol<SmtpWrapper>
{
    @Autowired
    EmailProperties emailProperties;
    private Transport transport;
    private Session session = null;

    public Transport getTransport()
    {
        return transport;
    }
    
    public Session getSession()
    {
        return session;
    }
    
    @Override
    protected String getProtocolJMXConfigurationStatus() throws Exception
    {
        return withJMX().getSmtpServerConfigurationStatus();
    }

    @Override
    public SmtpWrapper authenticateUser(UserModel userModel) throws Exception
    {
        return authenticateUser(userModel, emailProperties.getSmtpPort());
    }
    
    public SmtpWrapper authenticateUser(UserModel userModel, String smtpPort) throws Exception
    {
        Properties props = new Properties();
        STEP(String.format("SMTP: Authenticate with %s/%s on using port %d", userModel.getUsername(), userModel.getPassword(), smtpPort));

        props.put("mail.smtp.host", emailProperties.getSmtpServer());
        props.put("mail.smtp.port", smtpPort);

        if (emailProperties.isSmtpTSLEnabled())
        {
            LOG.info("TSL Enabled for SMTP..");
            props.put("mail.smtp.starttls.enable", "true");
        }

        if (emailProperties.isSmtpAuthEnabled())
        {
            props.put("mail.smtp.auth", "true");
            LOG.info("Authentication enabled based on property settings, connecting with {} to SMTP email server", userModel.toString());
            new Authenticator()
            {
            };
            session = Session.getInstance(props, new jakarta.mail.Authenticator()
            {
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(userModel.getUsername(), userModel.getPassword());
                }
            });

            transport = session.getTransport("smtp");
            transport.connect(userModel.getUsername(), userModel.getPassword());
        }
        else
        {
            STEP(String.format("SMTP: Authentication disabled based on property settings, connecting anonymous to SMTP email server"));
            session = Session.getInstance(props);
            transport = session.getTransport("smtp");
            UserModel annonymous = new UserModel("anonymous", "");
            transport.connect(annonymous.getUsername(), annonymous.getPassword());
        }

        setTestUser(userModel);
        return this;
    }

    public SmtpWrapper authenticateUser(UserModel userModel, int smtpPort) throws Exception
    {
        Properties props = new Properties();
        STEP(String.format("SMTP: Authenticate with %s/%s on using port %d", userModel.getUsername(), userModel.getPassword(), smtpPort));

        props.put("mail.smtp.host", emailProperties.getSmtpServer());
        props.put("mail.smtp.port", smtpPort);

        if (emailProperties.isSmtpTSLEnabled())
        {
            LOG.info("TSL Enabled for SMTP..");
            props.put("mail.smtp.starttls.enable", "true");
        }

        if (emailProperties.isSmtpAuthEnabled())
        {
            props.put("mail.smtp.auth", "true");
            LOG.info("Authentication enabled based on property settings, connecting with {} to SMTP email server", userModel.toString());
            new Authenticator()
            {
            };
            session = Session.getInstance(props, new jakarta.mail.Authenticator()
            {
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(userModel.getUsername(), userModel.getPassword());
                }
            });

            transport = session.getTransport("smtp");
            transport.connect(userModel.getUsername(), userModel.getPassword());
        }
        else
        {
            STEP(String.format("SMTP: Authentication disabled based on property settings, connecting anonymous to SMTP email server"));
            session = Session.getInstance(props);
            transport = session.getTransport("smtp");
            UserModel annonymous = new UserModel("anonymous", "");
            transport.connect(annonymous.getUsername(), annonymous.getPassword());
        }

        setTestUser(userModel);
        return this;
    }

    @Override
    public SmtpWrapper disconnect() throws Exception
    {
        STEP("SMTP: Disconnect SMTP Client");
        getTransport().close();        
        return this;
    }

    @Override
    public SmtpWrapper usingSite(String siteId) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SmtpWrapper usingSite(SiteModel siteModel) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SmtpWrapper usingUserHome(String username) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SmtpWrapper usingUserHome() throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getRootPath() throws TestConfigurationException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getSitesPath() throws TestConfigurationException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getUserHomesPath() throws TestConfigurationException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDataDictionaryPath() throws TestConfigurationException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPrefixSpace()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SmtpWrapper usingResource(ContentModel model) throws Exception
    {
        if (model.getProtocolLocation() != null)
            setCurrentSpace(model.getProtocolLocation());
        else
            setCurrentSpace(null);
        return this;
    }

    @Override
    public SmtpAssertion assertThat()
    {
        return new SmtpAssertion(this);
    }

    /**
     * @return JMX DSL for this wrapper
     */
    public JmxUtil withJMX()
    {
        return new JmxUtil(this, jmxBuilder.getJmxClient());
    }

    public boolean isConnected()
    {       
        return transport.isConnected();
    }
    
    public ComposeMessage composeMessage()
    {
        return new ComposeMessage(this, session);
    }
}
