package aaa.modules.regression.preconditions;

import org.testng.annotations.Test;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.modules.BaseTest;

public class JobAdding extends BaseTest{

	@Test(description = "Renewal job adding")
	public void renewalJobAdding() {
		adminApp().open();
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_SCHEDULER.get());

		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.RENEWAL_OFFER_GENERATION_PART_1);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.RENEWAL_OFFER_GENERATION_PART_2);
	}
}
