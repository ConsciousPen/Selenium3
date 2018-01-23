package aaa.modules.regression.queries.preconditions;

import static aaa.modules.regression.queries.LookupQueries.UPDATE_DISPLAYVALUE_BY_CODE;
import static aaa.modules.regression.sales.auto_ss.functional.preconditions.EvalueInsertSetupPreConditions.*;
import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.Test;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.modules.BaseTest;
import toolkit.db.DBService;

public class ScorpionsPreconditions extends BaseTest{

	@Test(description = "Renewal job adding")
	public void renewalJobAdding() {
		adminApp().open();
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_SCHEDULER.get());

		assertThat(GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.RENEWAL_OFFER_GENERATION_PART_1)).isEqualTo(true);
		assertThat(GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.RENEWAL_OFFER_GENERATION_PART_2)).isEqualTo(true);
	}

	@Test(description = "Enable vin refresh")
	public void enableVinRefresh(){
		DBService.get().executeUpdate(String.format(UPDATE_DISPLAYVALUE_BY_CODE, "TRUE"));
	}

	@Test(description = "Precondition set doc generation endpoints")
	public static void docGenStubEndpointInsert() {
		DBService.get().executeUpdate(DOC_GEN_WEB_CLIENT);
		DBService.get().executeUpdate(AAA_RETRIEVE_AGREEMENT_WEB_CLIENT);
		DBService.get().executeUpdate(AAA_RETRIEVE_DOCUMENT_WEB_CLIENT);
	}
}
