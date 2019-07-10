package aaa.helpers.jobs;

import static com.exigen.ipb.eisa.base.config.CustomTestProperties.SOAP_BATCHJOB_ENDPOINT;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Supplier;
import javax.xml.ws.WebServiceException;
import com.exigen.ipb.eisa.base.app.CSAAApplicationFactory;
import toolkit.config.PropertyProvider;

public class BatchJobURLSupplier implements Supplier<URL> {

	@Override
	public URL get() {
		try {
			return new URL(PropertyProvider.getProperty(SOAP_BATCHJOB_ENDPOINT, buildServiceURL()));
		} catch (MalformedURLException | URISyntaxException ex) {
			throw new WebServiceException(ex);
		}
	}

	private static String buildServiceURL() throws URISyntaxException, MalformedURLException {
		return CSAAApplicationFactory.get().adminApp().getServiceUrl().concat("/services/BatchJobTrigger?wsdl");
	}
}