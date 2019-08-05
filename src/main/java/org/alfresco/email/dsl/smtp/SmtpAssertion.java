package org.alfresco.email.dsl.smtp;

import static org.alfresco.utility.report.log.Step.STEP;

import org.alfresco.email.SmtpWrapper;
import org.alfresco.utility.dsl.DSLAssertion;
import org.testng.Assert;

public class SmtpAssertion extends DSLAssertion<SmtpWrapper>
{
    public SmtpAssertion(SmtpWrapper smtpWrapper)
    {
        super(smtpWrapper);
    }
    
    public SmtpWrapper smtpIsConnected()
    {
        STEP("SMTP: Assert that SMTP is connected");
        Assert.assertTrue(getProtocol().isConnected(), "SMTP authentication was not successful");
        return getProtocol();
    }
    
    public SmtpWrapper smtpIsNotConnected()
    {
        STEP("SMTP: Assert that SMTP is not connected");
        Assert.assertFalse(getProtocol().isConnected(), "SMTP authentication was successful");
        return getProtocol();
    }
}
