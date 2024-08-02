package bappity.keycloak.provider.email.aws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.email.EmailException;
import org.keycloak.models.UserModel;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 * Modified by @bappity for Outlook EWS
 */
class OutlookEwsEmailSenderProviderTest {

    private OutlookEwsEmailSenderProvider provider;

    @Mock
    private UserModel user;
    @Mock
    private SesClient ses;

    @BeforeEach
    void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testSend() throws EmailException {
        Map<String, String> config = new HashMap<>();
        config.put("from", "john@example.com");

        when(user.getEmail()).thenReturn("user@example.com");

        provider = new OutlookEwsEmailSenderProvider(ses);
        provider.send(config, user, "Subject", "Text Body", "Html Body");

        verify(ses).sendEmail(any(SendEmailRequest.class));
    }

    @Test
    void testMissingFromAddress() {
        Map<String, String> config = new HashMap<>();
        provider = new OutlookEwsEmailSenderProvider(ses);

        Throwable exception = assertThrows(EmailException.class,
            () -> provider.send(config, user, "Subject", "Text Body", "Html Body"));

        assertTrue(exception.getMessage().contains("from"));
    }
}
