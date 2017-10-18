package aaa.helpers.listeners;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestResult;
import toolkit.exceptions.IstfException;
import toolkit.utils.teststoragex.listeners.TestngTestListener2;
import toolkit.utils.teststoragex.models.Attachment;
import toolkit.webdriver.BrowserController;

public class AaaTestListener extends TestngTestListener2 {

	@Override
	public void onTestFailure(ITestResult result) {
		log.info("Test failure date/time: " + TimeSetterUtil.getInstance().getCurrentTime());
		hideFooter();
		super.onTestFailure(result);
		showFooter();
	}

	protected void hideFooter() {
		try {
			BrowserController.get().executeScript("$('#headerForm').hide();");
			BrowserController.get().executeScript("try{document.styleSheets[0].insertRule('body:after,footer:after{display:none!important}',0);}catch(e){}");
		} catch (IstfException | WebDriverException e) {
			log.debug("Error execute script for hide footer: ", e);
		}
	}

	protected void showFooter() {
		try {
			BrowserController.get().executeScript("$('#headerForm').show();");
		} catch (IstfException | WebDriverException e) {
			log.debug("Error execute script for show footer: ", e);
		}
	}

	@Override
	protected void createAuxAttachments(ITestResult result){
		if (result.getTestContext().getAttribute("attachment") != null) {
			createAttachment(result, result.getTestContext().getAttribute("attachment").toString(), Attachment.Type.OTHER);
		}

		if (!result.isSuccess()) {
			String appLogPath = new AppLogGrabber().grabAppLog(result);
			if (appLogPath != null) {
				createAttachment(result, appLogPath, Attachment.Type.APP_LOG);
			}
		}
	}
}
