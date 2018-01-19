package aaa.helpers.listeners;

import org.testng.ITestResult;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.utils.teststoragex.listeners.TestngTestListener2;
import toolkit.utils.teststoragex.models.Attachment;

public class AaaTestListener extends TestngTestListener2 {

	@Override
	public void onTestFailure(ITestResult result) {
		log.info("Test failure date/time: " + TimeSetterUtil.getInstance().getCurrentTime());
		super.onTestFailure(result);
	}

	@Override
	protected void createAuxAttachments(ITestResult result) {
		if (result.getTestContext().getAttribute("attachment") != null) {
			createAttachment(result, result.getTestContext().getAttribute("attachment").toString(), Attachment.Type.OTHER);
		}
		String appLogPath = new AppLogGrabber().grabAppLog(result);
		if (appLogPath != null) {
			createAttachment(result, appLogPath, Attachment.Type.APP_LOG);
		}
	}
}
