// package bappity.keycloak.provider.email.ews;

// import com.microsoft.aad.msal4j.*;
// import org.junit.jupiter.api.Disabled;
// import org.junit.jupiter.api.Test;
// import org.keycloak.email.EmailException;
// import org.keycloak.models.UserModel;
// import org.apache.http.HttpResponse;
// import org.apache.http.client.methods.CloseableHttpResponse;
// import org.apache.http.client.methods.HttpPost;
// import org.apache.http.impl.client.CloseableHttpClient;
// import org.apache.http.impl.client.HttpClients;
// import org.apache.http.util.EntityUtils;

// import java.net.URI;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Set;
// import java.util.concurrent.CompletableFuture;

// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.when;

// class OutlookEwsEmailSenderProviderIT {

//     private static String getOAuthAccessToken(String tenantId, String clientId, String clientSecret) throws Exception {
//         ConfidentialClientApplication app = ConfidentialClientApplication.builder(
//                 clientId,
//                 ClientCredentialFactory.createFromSecret(clientSecret))
//                 .authority("https://login.microsoftonline.com/" + tenantId)
//                 .build();

//         ClientCredentialParameters parameters = ClientCredentialParameters.builder(
//                 Set.of("https://outlook.office365.com/.default"))
//                 .build();

//         CompletableFuture<IAuthenticationResult> future = app.acquireToken(parameters);
//         return future.get().accessToken();
//     }

//     @Test
//     @Disabled("Run only manually")
//     void testSend() throws Exception {
//         // OAuth token and EWS URL
//         String accessToken = getOAuthAccessToken("your-tenant-id", "your-app-id", "your-client-secret");
//         String ewsUrl = "https://outlook.office365.com/EWS/Exchange.asmx";

//         // Send email using Apache HttpClient
//         try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//             HttpPost httpPost = new HttpPost(ewsUrl);
//             httpPost.setHeader("Authorization", "Bearer " + accessToken);
//             httpPost.setHeader("Content-Type", "text/xml");

//             // Set the XML payload for sending the email (e.g., EWS SOAP request)
//             String xmlPayload = "<Your SOAP Request Here>";
//             httpPost.setEntity(new org.apache.http.entity.StringEntity(xmlPayload, "UTF-8"));

//             try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
//                 String responseBody = EntityUtils.toString(response.getEntity());
//                 System.out.println("Response: " + responseBody);
//             }
//         } catch (Exception e) {
//             System.err.println("Failed to send email: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }
// }