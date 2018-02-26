package aaa.modules.preconditions;

import static aaa.modules.regression.sales.auto_ss.functional.preconditions.EvalueInsertSetupPreConditions.*;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.constants.Groups;
import aaa.modules.BaseTest;
import toolkit.config.PropertyProvider;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

public class ScorpionsPreconditions extends BaseTest {
	/* Vin refresh enable/disable queries */
	private String SELECT_LOOKUP_ROW_FROM_AAAROLLOUTELIGIBILITYLOOKUP_BY_CODE = "select * from LOOKUPVALUE where LOOKUPLIST_ID in"
			+ "(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME = 'AAARolloutEligibilityLookup') and code = 'vinRefresh'";

	private String UPDATE_DISPLAYVALUE_BY_CODE = "UPDATE LOOKUPVALUE SET DISPLAYVALUE = '%1$s' WHERE LOOKUPLIST_ID in (SELECT ID FROM LOOKUPLIST "
			+ "WHERE LOOKUPNAME = 'AAARolloutEligibilityLookup') and code = 'vinRefresh'";


	@Test(groups = {Groups.PRECONDITION}, description = "Renewal job adding")
	@TestInfo()
	public void renewalJobAdding() {
		adminApp().open();
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_SCHEDULER.get());

		assertThat(GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.RENEWAL_OFFER_GENERATION_PART_1)).isEqualTo(true);
		assertThat(GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.RENEWAL_OFFER_GENERATION_PART_2)).isEqualTo(true);
	}

	@Test(groups = {Groups.PRECONDITION},description = "Enable vin refresh")
	public void enableVinRefresh() {
		int result = DBService.get().executeUpdate(String.format(UPDATE_DISPLAYVALUE_BY_CODE, "true"));
		assertThat(result).isGreaterThan(0);
	}

	@Test(groups = {Groups.PRECONDITION},description = "Precondition set doc generation endpoints")
	public static void docGenStubEndpointInsert() {
		int result = 0;
		List<String> queries = Arrays.asList(DOC_GEN_WEB_CLIENT,AAA_RETRIEVE_AGREEMENT_WEB_CLIENT,AAA_RETRIEVE_DOCUMENT_WEB_CLIENT);
		for(String query : queries){
			result = DBService.get().executeUpdate(query);
			assertThat(result).isGreaterThan(0);
		}
	}

	@Test(description = "Precondition for to be able to Add Payment methods, Payment Central is stubbed", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void paymentCentralStubEndPointUpdate() {
		DBService.get().executeUpdate(String.format(PAYMENT_CENTRAL_STUB_ENDPOINT_UPDATE, PropertyProvider.getProperty(CustomTestProperties.APP_HOST), PropertyProvider.getProperty("app.stub.urltemplate")));
	}

	private static final String PAYMENT_CENTRAL_CONFIG_CHECK = "select value from PROPERTYCONFIGURERENTITY where propertyname in('aaaBillingAccountUpdateActionBean.ccStorateEndpointURL','aaaPurchaseScreenActionBean.ccStorateEndpointURL','aaaBillingActionBean.ccStorateEndpointURL')";

	@Test(description = "Precondition updating Membership Summary Endpoint to Stub", groups = {Groups.PRECONDITION})
	public static void updateMembershipSummaryStubEndpoint() {
		DBService.get().executeUpdate(String.format(UPDATE_MEMBERSHIP_SUMMARY_STUB_POINT, APP_HOST, APP_STUB_URL));
	}

	@Test(description = "Preconditions")
	private void paymentCentralConfigCheck() {
		assertThat(DBService.get().getValue(PAYMENT_CENTRAL_CONFIG_CHECK).get()).contains(PropertyProvider.getProperty(CustomTestProperties.APP_HOST));
	}
}
