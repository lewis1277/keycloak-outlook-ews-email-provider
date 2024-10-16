package bappity.keycloak.provider.email.ews;

import com.microsoft.aad.msal4j.*;
import org.keycloak.Config;
import org.keycloak.email.EmailSenderProvider;
import org.keycloak.email.EmailSenderProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ServerInfoAwareProviderFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class OutlookEwsEmailSenderProviderFactory
        implements EmailSenderProviderFactory, ServerInfoAwareProviderFactory {
    private final Map<String, String> configMap = new HashMap<>();
    private String ewsUrl;
    private String clientId;
    private String clientSecret;
    private String tenantId;
    private ConfidentialClientApplication app;
    private volatile String accessToken;
    private volatile long tokenExpiryTime; // Epoch time in milliseconds

    @Override
    public EmailSenderProvider create(KeycloakSession session) {
        return new OutlookEwsEmailSenderProvider(ewsUrl, () -> {
            try {
                return getAccessToken();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return accessToken;
        });
    }

    @Override
    public void init(Config.Scope config) {
        clientId = config.get("clientId");
        clientSecret = config.get("clientSecret");
        tenantId = config.get("tenantId");
        ewsUrl = config.get("ewsUrl");

        if (clientId != null && clientSecret != null && tenantId != null && ewsUrl != null) {
            configMap.put("ewsUrl", ewsUrl);

            try {
                app = ConfidentialClientApplication.builder(
                        clientId,
                        ClientCredentialFactory.createFromSecret(clientSecret))
                        .authority("https://login.microsoftonline.com/" + tenantId)
                        .build();
                acquireAccessToken();
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize OAuth token", e);
            }
        } else {
            throw new RuntimeException("Missing configuration for Office365 EWS");
        }
    }

    private synchronized void acquireAccessToken() throws Exception {
        ClientCredentialParameters parameters = ClientCredentialParameters.builder(
                Set.of("https://outlook.office365.com/.default"))
                .build();

        CompletableFuture<IAuthenticationResult> future = app.acquireToken(parameters);
        IAuthenticationResult result = future.get();
        this.accessToken = result.accessToken();
        this.tokenExpiryTime = result.expiresOnDate().getTime() - 60000; // Refresh 1 minute before expiry
    }

    private String getAccessToken() throws Exception {
        if (System.currentTimeMillis() > tokenExpiryTime) {
            acquireAccessToken();
        }
        return accessToken;
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return "outlook-ews";
    }

    @Override
    public Map<String, String> getOperationalInfo() {
        return configMap;
    }
}