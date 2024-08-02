package bappity.keycloak.provider.email.aws;

import org.keycloak.Config;
import org.keycloak.email.EmailSenderProvider;
import org.keycloak.email.EmailSenderProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ServerInfoAwareProviderFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 * Modified by @bappity for Outlook EWS
 */
public class OutlookEwsEmailSenderProviderFactory implements EmailSenderProviderFactory, ServerInfoAwareProviderFactory {

    private final Map<String, String> configMap = new HashMap<>();
    private SesClient ses;

    @Override
    public EmailSenderProvider create(KeycloakSession session) {
        return new OutlookEwsEmailSenderProvider(ses);
    }

    @Override
    public void init(Config.Scope config) {
        String configRegion = config.get("region");
        if (configRegion != null) {
            configMap.put("region", configRegion);
            Region region = Region.of(configRegion);
            ses = SesClient.builder().region(region).build();
        } else {
            ses = SesClient.create();
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
