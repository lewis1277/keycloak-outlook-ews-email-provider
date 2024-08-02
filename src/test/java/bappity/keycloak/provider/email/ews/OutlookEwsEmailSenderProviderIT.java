package bappity.keycloak.provider.email.aws;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.keycloak.email.EmailException;
import org.keycloak.models.UserModel;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 * Modified by @bappity for Outlook EWS
 */
class OutlookEwsEmailSenderProviderIT {

    @Test
    @Disabled("Run only manually")
    void testSend() throws Exception {
        // Set up the ExchangeService
        ExchangeService exchangeService = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
        
        // Replace these with your actual Office365 credentials and EWS URL
        String email = "your-email@your-domain.com";
        String password = "your-password";
        String ewsUrl = "https://outlook.office365.com/EWS/Exchange.asmx";

        ExchangeCredentials credentials = new WebCredentials(email, password);
        exchangeService.setCredentials(credentials);
        exchangeService.setUrl(new URI(ewsUrl));

        Map<String, String> config = new HashMap<>();
        config.put("from", email);
        config.put("fromDisplayName", "Keycloak Test");

        UserModel user = mock(UserModel.class);
        when(user.getEmail()).thenReturn("recipient@example.com");

        OutlookEwsEmailSenderProvider provider = new OutlookEwsEmailSenderProvider(exchangeService);

        try {
            provider.send(config, user, "Test", "Hello", "<h1>Hello</h1>");
            System.out.println("Email sent successfully");
        } catch (EmailException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
