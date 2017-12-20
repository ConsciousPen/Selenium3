package aaa.modules.cft;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.utils.TestInfo;
import aaa.helpers.constants.Groups;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

public class TestCFTMonthEnd extends ControlledFinancialBaseTest {

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void generateCFTFilesForEachMonth(@Optional(StringUtils.EMPTY) String state) {
		// execute cft jobs for upcoming 14th month
		for (int i = 0; i < 14; i++) {
			TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusMonths(1).withDayOfMonth(1).withMinute(1));
			runCFTJobs();
		}
	}

}
