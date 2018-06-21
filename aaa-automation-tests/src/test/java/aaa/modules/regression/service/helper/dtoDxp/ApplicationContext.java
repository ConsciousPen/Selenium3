package aaa.modules.regression.service.helper.dtoDxp;

/**
 * Application context DTO used to map and modify X-ApplicationContext header param in requests and responses.
 */
public class ApplicationContext {

    public String userId;
    public String application;
    public String sourceApplication;
    public String address;
    public String correlationId;
    public String sessionId;
}