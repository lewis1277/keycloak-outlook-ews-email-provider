package bappity.keycloak.provider.email.aws;

import org.keycloak.Config;
import org.keycloak.email.EmailSenderProvider;
import org.keycloak.email.EmailSenderProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ServerInfoAwareProviderFactory;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 * Modified by @bappity for Outlook EWS
 */
public class OutlookEwsEmailSenderProviderFactory implements EmailSenderProviderFactory, ServerInfoAwareProviderFactory {
    private final Map<String, String> configMap = new HashMap<>();
    private ExchangeService exchangeService;

    @Override
    public EmailSenderProvider create(KeycloakSession session) {
        return new OutlookEwsEmailSenderProvider(exchangeService);
    }

    @Override
    public void init(Config.Scope config) {
        String email = config.get("email");
        String password = config.get("password");
        String url = config.get("ewsUrl");

        if (email != null && password != null && url != null) {
            configMap.put("email", email);
            configMap.put("ewsUrl", url);

            try {
                exchangeService = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
                ExchangeCredentials credentials = new WebCredentials(email, password);
                exchangeService.setCredentials(credentials);
                exchangeService.setUrl(new URI(url));
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize Exchange service", e);
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
}
