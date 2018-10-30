package aaa.helpers.rest.dtoDxp;

/**
 * Application context DTO used to map and modify X-ApplicationContext header param in requests and responses.
 */
public class ApplicationContext {

    public String application;
    public String address;
    public String correlationId;
    public String sessionId;
}