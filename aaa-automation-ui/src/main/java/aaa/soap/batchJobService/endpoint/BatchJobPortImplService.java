package aaa.soap.batchJobService.endpoint;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.*;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.http.HTTPConduitConfigurer;
import com.exigen.ipb.eisa.base.app.CSAAApplicationFactory;
import aaa.soap.AAAHTTPConfigurer;

@WebServiceClient(name = "BatchJobPortImplService", targetNamespace = "http://endpoint.batchjobws.aaa.exigen.com/", wsdlLocation = "http://eqxaaawas01.eqxdev.exigengroup.com:9082/aaa-admin/services/BatchJobService.wsdl")
public class BatchJobPortImplService
		extends Service {

	private final static URL BATCHJOBPORTIMPLSERVICE_WSDL_LOCATION;
	private final static WebServiceException BATCHJOBPORTIMPLSERVICE_EXCEPTION;
	private final static QName BATCHJOBPORTIMPLSERVICE_QNAME = new QName("http://endpoint.batchjobws.aaa.exigen.com/", "BatchJobPortImplService");

	static {
		URL url = null;
		WebServiceException e = null;
		try {
			url = new URL(CSAAApplicationFactory.get().adminApp().getUrl().replace("/admin", "").concat("/services/BatchJobService?wsdl"));
		} catch (MalformedURLException ex) {
			e = new WebServiceException(ex);
		}
		Bus bus = BusFactory.getThreadDefaultBus();
		HTTPConduitConfigurer conf = new AAAHTTPConfigurer("qa", "qa");
		bus.setExtension(conf, HTTPConduitConfigurer.class);
		BATCHJOBPORTIMPLSERVICE_WSDL_LOCATION = url;
		BATCHJOBPORTIMPLSERVICE_EXCEPTION = e;
	}

	public BatchJobPortImplService() {
		super(__getWsdlLocation(), BATCHJOBPORTIMPLSERVICE_QNAME);
	}

	public BatchJobPortImplService(WebServiceFeature... features) {
		super(__getWsdlLocation(), BATCHJOBPORTIMPLSERVICE_QNAME, features);
	}

	public BatchJobPortImplService(URL wsdlLocation) {
		super(wsdlLocation, BATCHJOBPORTIMPLSERVICE_QNAME);
	}

	public BatchJobPortImplService(URL wsdlLocation, WebServiceFeature... features) {
		super(wsdlLocation, BATCHJOBPORTIMPLSERVICE_QNAME, features);
	}

	public BatchJobPortImplService(URL wsdlLocation, QName serviceName) {
		super(wsdlLocation, serviceName);
	}

	public BatchJobPortImplService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
		super(wsdlLocation, serviceName, features);
	}

	/**
	 *
	 * @return
	 *     returns BatchJobPort
	 */
	@WebEndpoint(name = "BatchJobPortImplPort")
	public BatchJobPort getBatchJobPortImplPort() {
		return super.getPort(new QName("http://endpoint.batchjobws.aaa.exigen.com/", "BatchJobPortImplPort"), BatchJobPort.class);
	}

	/**
	 *
	 * @param features
	 *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
	 * @return
	 *     returns BatchJobPort
	 */
	@WebEndpoint(name = "BatchJobPortImplPort")
	public BatchJobPort getBatchJobPortImplPort(WebServiceFeature... features) {
		return super.getPort(new QName("http://endpoint.batchjobws.aaa.exigen.com/", "BatchJobPortImplPort"), BatchJobPort.class, features);
	}

	private static URL __getWsdlLocation() {
		if (BATCHJOBPORTIMPLSERVICE_EXCEPTION != null) {
			throw BATCHJOBPORTIMPLSERVICE_EXCEPTION;
		}
		return BATCHJOBPORTIMPLSERVICE_WSDL_LOCATION;
	}

}
