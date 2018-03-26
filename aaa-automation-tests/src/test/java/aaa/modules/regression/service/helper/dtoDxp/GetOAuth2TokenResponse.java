package aaa.modules.regression.service.helper.dtoDxp;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

/**
 * Get OAuth 2 token response containing issued authentication token information that is sent back from PingFederate.
 */
public class GetOAuth2TokenResponse {

    @JsonProperty("access_token")
    public String accessToken;

    @JsonProperty("expires_in")
    public int expiresIn;

    @JsonProperty("error")
    public String error;

    public boolean isValid() {
        return StringUtils.isEmpty(error) && StringUtils.isNotEmpty(accessToken) && expiresIn > 0;
    }
}
