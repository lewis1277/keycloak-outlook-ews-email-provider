package bappity.keycloak.provider.email.ews;

import org.keycloak.email.EmailException;
import org.keycloak.email.EmailSenderProvider;
import org.keycloak.models.UserModel;
import org.keycloak.services.ServicesLogger;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 * Modified by @bappity for Outlook EWS
 */
public class OutlookEwsEmailSenderProvider implements EmailSenderProvider {
    private final String ewsUrl;
    private final String accessToken;

    OutlookEwsEmailSenderProvider(String ewsUrl, String accessToken) {
        this.ewsUrl = ewsUrl;
        this.accessToken = accessToken;
    }

    @Override
    public void send(Map<String, String> config, String to, String subject, String textBody, String htmlBody) throws EmailException {
        String from = config.get("from");
        String fromDisplayName = config.get("fromDisplayName");
        String replyTo = config.get("replyTo");
        String replyToDisplayName = config.get("replyToDisplayName");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(ewsUrl);
            httpPost.setHeader("Authorization", "Bearer " + accessToken);
            httpPost.setHeader("Content-Type", "text/xml");

            // Set the XML payload for sending the email (e.g., EWS SOAP request)
            String xmlPayload = buildXmlPayload(from, fromDisplayName, replyTo, subject, textBody, htmlBody);
            httpPost.setEntity(new StringEntity(xmlPayload, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("Response: " + responseBody);
            }
        } catch (Exception e) {
            ServicesLogger.LOGGER.failedToSendEmail(e);
            throw new EmailException("Failed to send email", e);
        }
    }

    private String buildXmlPayload(String from, String fromDisplayName, String replyTo, String subject, String textBody, String htmlBody) {
        // Construct your EWS SOAP request XML payload here
        return "<Your SOAP Request Here>";
    }

    @Override
    public void close() {
    }
}