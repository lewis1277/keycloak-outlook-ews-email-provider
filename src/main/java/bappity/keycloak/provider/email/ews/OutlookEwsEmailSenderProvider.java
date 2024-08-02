package bappity.keycloak.provider.email.aws;

import org.keycloak.email.EmailException;
import org.keycloak.email.EmailSenderProvider;
import org.keycloak.models.UserModel;
import org.keycloak.services.ServicesLogger;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;

import javax.mail.internet.InternetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 * Modified by @bappity for Outlook EWS
 */
public class OutlookEwsEmailSenderProvider implements EmailSenderProvider {
    private final ExchangeService exchangeService;

    OutlookEwsEmailSenderProvider(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @Override
    public void send(Map<String, String> config, UserModel user, String subject, String textBody, String htmlBody) throws EmailException {
        String from = config.get("from");
        String fromDisplayName = config.get("fromDisplayName");
        String replyTo = config.get("replyTo");
        String replyToDisplayName = config.get("replyToDisplayName");

        try {
            if (from == null || from.isEmpty()) {
                throw new Exception("Missing 'from' email address.");
            }

            EmailMessage message = new EmailMessage(exchangeService);
            message.setSubject(subject);
            message.setBody(MessageBody.getMessageBodyFromText(htmlBody));
            message.getToRecipients().add(user.getEmail());
            message.setFrom(new EmailAddress(from, fromDisplayName));

            if (replyTo != null && !replyTo.isEmpty()) {
                message.setReplyTo(Collections.singletonList(new EmailAddress(replyTo, replyToDisplayName)));
            }

            message.send();
        } catch (Exception e) {
            ServicesLogger.LOGGER.failedToSendEmail(e);
            throw new EmailException(e);
        }
    }

    private InternetAddress toInternetAddress(String email, String displayName) throws Exception {
        if (email == null || "".equals(email.trim())) {
            throw new EmailException("Please provide a valid address", null);
        }
        if (displayName == null || "".equals(displayName.trim())) {
            return new InternetAddress(email);
        }
        return new InternetAddress(email, displayName, StandardCharsets.UTF_8.toString());
    }

    @Override
    public void close() {
    }
}
