package aaa.helpers.listeners;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.ITestResult;
import toolkit.utils.teststoragex.listeners.TestngTestListener2;

public class AaaTestListener extends TestngTestListener2 {

	@Override
	public void onTestFailure(ITestResult result) {
		log.info("Test failure date/time: " + TimeSetterUtil.getInstance().getCurrentTime());
		super.onTestFailure(result);
	}
}
