package aaa.soap;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transport.http.HTTPConduitConfigurer;

public class AAAHTTPConfigurer implements HTTPConduitConfigurer {
    //todo request public access to the ETCSAHTTPConduitConfigurer
    private final String username;
    private final String password;

    public AAAHTTPConfigurer(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void configure(String name, String address, HTTPConduit c) {
        AuthorizationPolicy ap = new AuthorizationPolicy();
        ap.setUserName(username);
        ap.setPassword(password);
        c.setAuthorization(ap);
    }
}
