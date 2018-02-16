package aaa.modules.preconditions;

import static aaa.modules.regression.sales.auto_ss.functional.preconditions.EvalueInsertSetupPreConditions.*;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.modules.BaseTest;
import toolkit.db.DBService;

public class ScorpionsPreconditions extends BaseTest {
	/* Vin refresh enable/disable queries */
	private String SELECT_LOOKUP_ROW_FROM_AAAROLLOUTELIGIBILITYLOOKUP_BY_CODE = "select * from LOOKUPVALUE where LOOKUPLIST_ID in"
			+ "(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME = 'AAARolloutEligibilityLookup') and code = 'vinRefresh'";

	private String UPDATE_DISPLAYVALUE_BY_CODE = "UPDATE LOOKUPVALUE SET DISPLAYVALUE = '%1$s' WHERE LOOKUPLIST_ID in (SELECT ID FROM LOOKUPLIST "
			+ "WHERE LOOKUPNAME = 'AAARolloutEligibilityLookup') and code = 'vinRefresh'";


	@Test(description = "Renewal job adding")
	public void renewalJobAdding() {
		adminApp().open();
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_SCHEDULER.get());

		assertThat(GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.RENEWAL_OFFER_GENERATION_PART_1)).isEqualTo(true);
		assertThat(GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.RENEWAL_OFFER_GENERATION_PART_2)).isEqualTo(true);
	}

	@Test(description = "Enable vin refresh")
	public void enableVinRefresh() {
		int result = DBService.get().executeUpdate(String.format(UPDATE_DISPLAYVALUE_BY_CODE, "true"));
		assertThat(result).isGreaterThan(0);
	}

	@Test(description = "Precondition set doc generation endpoints")
	public static void docGenStubEndpointInsert() {
		int result = 0;
		List<String> queries = Arrays.asList(DOC_GEN_WEB_CLIENT,AAA_RETRIEVE_AGREEMENT_WEB_CLIENT,AAA_RETRIEVE_DOCUMENT_WEB_CLIENT);
		for(String query : queries){
			result = DBService.get().executeUpdate(query);
			assertThat(result).isGreaterThan(0);
		}
	}

}