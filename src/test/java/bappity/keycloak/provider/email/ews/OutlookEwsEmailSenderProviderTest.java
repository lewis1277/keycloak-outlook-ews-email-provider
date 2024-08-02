// package bappity.keycloak.provider.email.ews;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.keycloak.email.EmailException;
// import org.keycloak.models.UserModel;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.apache.http.HttpResponse;
// import org.apache.http.client.methods.CloseableHttpResponse;
// import org.apache.http.client.methods.HttpPost;
// import org.apache.http.impl.client.CloseableHttpClient;
// import org.apache.http.impl.client.HttpClients;
// import org.apache.http.entity.StringEntity;
// import org.apache.http.util.EntityUtils;

// import java.nio.charset.StandardCharsets;
// import java.util.HashMap;
// import java.util.Map;

// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// class OutlookEwsEmailSenderProviderTest {

//     private OutlookEwsEmailSenderProvider provider;

//     @Mock
//     private UserModel user;

//     @Mock
//     private CloseableHttpClient httpClient;

//     @Mock
//     private CloseableHttpResponse httpResponse;

//     @BeforeEach
//     void before() throws Exception {
//         MockitoAnnotations.initMocks(this);

//         // Mock HttpClient setup
//         httpClient = mock(CloseableHttpClient.class);
//         httpResponse = mock(CloseableHttpResponse.class);
//         when(httpResponse.getEntity()).thenReturn(new org.apache.http.entity.StringEntity("<Response>Success</Response>", StandardCharsets.UTF_8));
//         when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);

//         // Initialize provider with mock HTTP client
//         provider = new OutlookEwsEmailSenderProvider("https://outlook.office365.com/EWS/Exchange.asmx", "mock-access-token");
//     }

//     @Test
//     void testSend() throws Exception {
//         Map<String, String> config = new HashMap<>();
//         config.put("from", "john@example.com");
//         when(user.getEmail()).thenReturn("user@example.com");

//         // Call the method to test
//         provider.send(config, "user@example.com", "Subject", "Text Body", "Html Body");

//         // Verify interactions
//         verify(httpClient).execute(any(HttpPost.class));
//     }

//     @Test
//     void testMissingFromAddress() {
//         Map<String, String> config = new HashMap<>();
//         provider = new OutlookEwsEmailSenderProvider("https://outlook.office365.com/EWS/Exchange.asmx", "mock-access-token");

//         EmailException exception = assertThrows(EmailException.class,
//             () -> provider.send(config, "user@example.com", "Subject", "Text Body", "Html Body"));

//         assertTrue(exception.getMessage().contains("from"));
//     }
// }