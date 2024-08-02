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

public class OutlookEwsEmailSenderProviderFactory implements EmailSenderProviderFactory, ServerInfoAwareProviderFactory {
    private final Map<String, String> configMap = new HashMap<>();
    private String ewsUrl;
    private String accessToken;

    @Override
    public EmailSenderProvider create(KeycloakSession session) {
        return new OutlookEwsEmailSenderProvider(ewsUrl, accessToken);
    }

    @Override
    public void init(Config.Scope config) {
        String clientId = config.get("clientId");
        String clientSecret = config.get("clientSecret");
        String tenantId = config.get("tenantId");
        ewsUrl = config.get("ewsUrl");

        if (clientId != null && clientSecret != null && tenantId != null && ewsUrl != null) {
            configMap.put("ewsUrl", ewsUrl);

            try {
                accessToken = getOAuthAccessToken(tenantId, clientId, clientSecret);
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize OAuth token", e);
            }
        } else {
            throw new RuntimeException("Missing configuration for Office365 EWS");
        }
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

    private static String getOAuthAccessToken(String tenantId, String clientId, String clientSecret) throws Exception {
        ConfidentialClientApplication app = ConfidentialClientApplication.builder(
                clientId,
                ClientCredentialFactory.createFromSecret(clientSecret))
                .authority("https://login.microsoftonline.com/" + tenantId)
                .build();

        ClientCredentialParameters parameters = ClientCredentialParameters.builder(
                Set.of("https://outlook.office365.com/.default"))
                .build();

        CompletableFuture<IAuthenticationResult> future = app.acquireToken(parameters);
        return future.get().accessToken();
    }
}