package aaa.modules.regression.service.helper;

/**
 * Methods enum used by REST services.
 * Contains some methods not supported by current ws-rs-api v.2.0.
 * Created by gn3zhyt on 7/17/2018.
 */
public enum RequestMethod {
    GET("GET"),
    POST("POST"),
    DELETE("DELETE"),
    PATCH("PATCH");

    private final String requestMethodName;

    RequestMethod(String requestMethodName) {
        this.requestMethodName = requestMethodName;
    }

    public String getMethodName() {
        return requestMethodName;
    }
}
