package aaa.helpers;

import java.net.URL;
import com.exigen.ipb.eisa.base.app.CSAAApplicationFactory;
import com.exigen.ipb.eisa.utils.EISAppHelper;
import toolkit.config.ClassConfigurator;
import toolkit.utils.http.HttpExecutor;
import toolkit.utils.http.HttpHelper;
import toolkit.utils.http.HttpRequest;
import toolkit.utils.http.HttpResponse;

public class CSAAAppHelper extends EISAppHelper {

	@ClassConfigurator.Configurable
	private static String applicationProtocol = "http://";
	@ClassConfigurator.Configurable
	private static String regexBuildInfo = "(<title>.*?Build: )(.*?)(\\)<\\/title>)";
	@ClassConfigurator.Configurable
	private static String locatorContainer = "//body";
	@ClassConfigurator.Configurable
	private static String locatorApplicationException = "//form[@id='loginForm' or @id='errorForm']/table/tbody/tr/td|.//*[@id = 'errorTitleText']";
	@ClassConfigurator.Configurable
	private static String locatorEISException = "//form[@id='errorsForm']//tbody[contains(@id, 'errorsForm')]//td[not(.='')]";
	@ClassConfigurator.Configurable
	private static String locatorFormException =
			".//div[contains(@class, 'contentFrame')]/span/ul[not(contains(@class, 'tabs'))]|.//div[@class='error-container']//span[text() != '']|.//span[contains(@class, 'rf-msg-det')]|.//div[@id='contents']//tbody[contains(@id,'tbody_element')]//tr[.//td[contains(text(),'Error')]]";

	static {
		ClassConfigurator configurator = new ClassConfigurator(CSAAAppHelper.class);
		configurator.applyConfiguration();
	}

	@Override
	public String getBuildInfo() {
		String buildInfo = null;
		String host = CSAAApplicationFactory.get().mainApp().formatUrl();

		try {
			HttpRequest request = new HttpRequest();
			request.setUrl(new URL(host));
			HttpResponse response = HttpExecutor.sendRequest(request);
			buildInfo = HttpHelper.find(response.getContent(), regexBuildInfo, 2);
		} catch (Exception e) {
			buildInfo = "N/A";
		}
		LOGGER.info("Application URL: " + host);
		LOGGER.info("Build num : " + buildInfo);
		return buildInfo;
	}
}
