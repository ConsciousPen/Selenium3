package aaa.helpers.rest.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.annotation.Nonnull;
import com.fasterxml.jackson.annotation.JsonProperty;
import aaa.config.CsaaTestProperties;
import toolkit.config.PropertyProvider;

/**
 * Get OAuth2 token request sent to PingFederate to acquire new authentication token.
 */
public class GetOAuth2TokenRequest {

    @JsonProperty("client_id")
    private final String clientId;

    @JsonProperty("client_secret")
    private final String clientSecret;

    @JsonProperty("grant_type")
    private final String grantType;

    protected GetOAuth2TokenRequest(@Nonnull String clientId, @Nonnull String clientSecret, @Nonnull String grantType) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.grantType = grantType;
    }

    public static GetOAuth2TokenRequest create() {
		return new GetOAuth2TokenRequest(PropertyProvider.getProperty(CsaaTestProperties.DXP_CLIENT_ID), PropertyProvider.getProperty(CsaaTestProperties.DXP_CLIENT_SECRET), PropertyProvider.getProperty(CsaaTestProperties.DXP_GRANT_TYPE));
    }

    public String asUrlEncoded() {
        try {
            return "client_id=" + URLEncoder.encode(clientId, "UTF-8")
                    + "&client_secret=" + URLEncoder.encode(clientSecret, "UTF-8")
                    + "&grant_type=" +  URLEncoder.encode(grantType, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

}
