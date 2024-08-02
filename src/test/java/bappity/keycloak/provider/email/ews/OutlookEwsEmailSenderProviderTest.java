package bappity.keycloak.provider.email.aws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.email.EmailException;
import org.keycloak.models.UserModel;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 * Modified by @bappity for Outlook EWS
 */
class OutlookEwsEmailSenderProviderTest {

    private OutlookEwsEmailSenderProvider provider;

    @Mock
    private UserModel user;

    @Mock
    private ExchangeService exchangeService;

    @Mock
    private EmailMessage emailMessage;

    @BeforeEach
    void before() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(exchangeService.createItem(EmailMessage.class)).thenReturn(emailMessage);
    }

    @Test
    void testSend() throws Exception {
        Map<String, String> config = new HashMap<>();
        config.put("from", "john@example.com");
        when(user.getEmail()).thenReturn("user@example.com");

        provider = new OutlookEwsEmailSenderProvider(exchangeService);
        provider.send(config, user, "Subject", "Text Body", "Html Body");

        verify(emailMessage).setSubject("Subject");
        verify(emailMessage).setBody(any(MessageBody.class));
        verify(emailMessage).getToRecipients();
        verify(emailMessage).setFrom(any(EmailAddress.class));
        verify(emailMessage).send();
    }

    @Test
    void testMissingFromAddress() {
        Map<String, String> config = new HashMap<>();
        provider = new OutlookEwsEmailSenderProvider(exchangeService);

        EmailException exception = assertThrows(EmailException.class,
            () -> provider.send(config, user, "Subject", "Text Body", "Html Body"));

        assertTrue(exception.getMessage().contains("from"));
    }
}
