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
import java.util.function.Supplier;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 *         Modified by @bappity for Outlook EWS
 */
public class OutlookEwsEmailSenderProvider implements EmailSenderProvider {
    private final String ewsUrl;
    private final Supplier<String> accessTokenSupplier;

    public OutlookEwsEmailSenderProvider(String ewsUrl, Supplier<String> accessTokenSupplier) {
        this.ewsUrl = ewsUrl;
        this.accessTokenSupplier = accessTokenSupplier;
    }

    @Override
    public void send(Map<String, String> config, String to, String subject, String textBody, String htmlBody)
            throws EmailException {
        String from = config.get("from");
        String fromDisplayName = config.get("fromDisplayName");
        String replyTo = config.get("replyTo");
        String replyToDisplayName = config.get("replyToDisplayName");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(ewsUrl);
            String accessToken = accessTokenSupplier.get();
            httpPost.setHeader("Authorization", "Bearer " + accessToken);
            httpPost.setHeader("Content-Type", "text/xml");

            // Set the XML payload for sending the email (EWS SOAP request)
            String xmlPayload = buildXmlPayload(from, fromDisplayName, replyTo, subject, textBody, htmlBody);
            httpPost.setEntity(new StringEntity(xmlPayload, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode >= 200 && statusCode < 300) {
                    // Email sent successfully
                } else {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    ServicesLogger.LOGGER.failedToSendEmail(
                            new Exception("EWS responded with status code " + statusCode + ": " + responseBody));
                    throw new EmailException("Failed to send email via EWS. Status Code: " + statusCode);
                }
            }
        } catch (Exception e) {
            ServicesLogger.LOGGER.failedToSendEmail(e);
            throw new EmailException("Failed to send email", e);
        }
    }

    private String buildXmlPayload(String from, String fromDisplayName, String replyTo, String subject, String textBody,
            String htmlBody) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                .append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ")
                .append("xmlns:m=\"http://schemas.microsoft.com/exchange/services/2006/messages\" ")
                .append("xmlns:t=\"http://schemas.microsoft.com/exchange/services/2006/types\" ")
                .append("xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">")
                .append("<soap:Header>")
                .append("<t:RequestServerVersion Version=\"Exchange2016\"/>")
                .append("</soap:Header>")
                .append("<soap:Body>")
                .append("<m:CreateItem MessageDisposition=\"SendAndSaveCopy\">")
                .append("<m:Items>")
                .append("<t:Message>")
                .append("<t:Subject>").append(escapeXml(subject)).append("</t:Subject>")
                .append("<t:Body BodyType=\"HTML\">").append(escapeXml(htmlBody)).append("</t:Body>")
                .append("<t:From>")
                .append("<t:Mailbox>")
                .append("<t:EmailAddress>").append(escapeXml(from)).append("</t:EmailAddress>")
                .append("<t:Name>").append(escapeXml(fromDisplayName)).append("</t:Name>")
                .append("</t:Mailbox>")
                .append("</t:From>")
                .append("<t:ToRecipients>")
                .append("<t:Mailbox>")
                .append("<t:EmailAddress>").append(escapeXml(replyTo)).append("</t:EmailAddress>")
                .append("</t:Mailbox>")
                .append("</t:ToRecipients>");

        if (replyTo != null && !replyTo.isEmpty()) {
            // Note: EWS does not support setting Reply-To directly. Consider adding
            // instructions in the email body or custom headers if necessary.
            sb.append("<t:AdditionalHeaders>")
                    .append("<t:Header>")
                    .append("<t:Name>Reply-To</t:Name>")
                    .append("<t:Value>").append(escapeXml(replyTo)).append("</t:Value>")
                    .append("</t:Header>")
                    .append("</t:AdditionalHeaders>");
        }

        sb.append("</t:Message>")
                .append("</m:Items>")
                .append("</m:CreateItem>")
                .append("</soap:Body>")
                .append("</soap:Envelope>");

        return sb.toString();
    }

    private String escapeXml(String input) {
        if (input == null)
            return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    @Override
    public void close() {
    }
}