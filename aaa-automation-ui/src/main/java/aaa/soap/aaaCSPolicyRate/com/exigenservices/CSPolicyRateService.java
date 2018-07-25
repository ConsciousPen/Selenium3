package aaa.soap.aaaCSPolicyRate.com.exigenservices;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.CXFBusFactory;
import org.apache.cxf.transport.http.HTTPConduitConfigurer;
import org.mortbay.log.Log;
import aaa.soap.AAAHTTPConfigurer;
import toolkit.config.PropertyProvider;

/**
 * This class was generated by Apache CXF 3.2.4
 * 2018-04-16T20:33:48.545-07:00
 * Generated source version: 3.2.4
 *
 */
@WebServiceClient(name = "CSPolicyRateService",
                  wsdlLocation = "http://nvdxpas2agl006:9095/aaa-admin/services/aaaCSPolicyRate?wsdl",
                  targetNamespace = "http://exigenservices.com/ipb/policy/integration")
public class CSPolicyRateService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://exigenservices.com/ipb/policy/integration", "CSPolicyRateService");
    public final static QName CSPolicyRatePort = new QName("http://exigenservices.com/ipb/policy/integration", "CSPolicyRatePort");
    static {
        URL url = null;
        try {
            url = new URL(String.format("http://%1$s%2$s/aaa-admin/services/aaaCSPolicyRate?wsdl", PropertyProvider.getProperty("app.host"), PropertyProvider.getProperty("app.ad.urltemplate").substring(0,5)));
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(CSPolicyRateService.class.getName())
                .log(java.util.logging.Level.INFO,
                     "Can not initialize the default wsdl from {0}", "http://%1$s%2$s/aaa-admin/services/aaaCSPolicyRate?wsdl");
        }
        Bus bus = CXFBusFactory.getThreadDefaultBus();
        HTTPConduitConfigurer conf = new AAAHTTPConfigurer("qa", "qa");
        Log.info("Logged as : qa");
        bus.setExtension(conf, HTTPConduitConfigurer.class);
        WSDL_LOCATION = url;
    }

    public CSPolicyRateService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public CSPolicyRateService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public CSPolicyRateService() {
        super(WSDL_LOCATION, SERVICE);
    }

    public CSPolicyRateService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    public CSPolicyRateService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public CSPolicyRateService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }




    /**
     *
     * @return
     *     returns CSPolicyRatePort
     */
    @WebEndpoint(name = "CSPolicyRatePort")
    public CSPolicyRatePort getCSPolicyRatePort() {
        return super.getPort(CSPolicyRatePort, CSPolicyRatePort.class);
    }

    /**
     *
     * @param features
     *     A list of {@link WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns CSPolicyRatePort
     */
    @WebEndpoint(name = "CSPolicyRatePort")
    public CSPolicyRatePort getCSPolicyRatePort(WebServiceFeature... features) {
        return super.getPort(CSPolicyRatePort, CSPolicyRatePort.class, features);
    }

}
