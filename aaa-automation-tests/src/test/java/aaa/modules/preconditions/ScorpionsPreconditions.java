package aaa.modules.preconditions;

import static toolkit.verification.CustomAssertions.assertThat;
import static aaa.modules.regression.sales.auto_ss.functional.preconditions.EvalueInsertSetupPreConditions.*;
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

public class ScorpionsPreconditions extends BaseTest {
	/* Vin refresh enable/disable queries */
	private static final String SELECT_LOOKUP_ROW_FROM_AAAROLLOUTELIGIBILITYLOOKUP_BY_CODE = "select * from LOOKUPVALUE where LOOKUPLIST_ID in"
			+ "(SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME = 'AAARolloutEligibilityLookup') and code = 'vinRefresh'";

	private static final String UPDATE_DISPLAYVALUE_BY_CODE = "UPDATE LOOKUPVALUE SET DISPLAYVALUE = '%1$s' WHERE LOOKUPLIST_ID in (SELECT ID FROM LOOKUPLIST "
			+ "WHERE LOOKUPNAME = 'AAARolloutEligibilityLookup') and code = 'vinRefresh'";

	private static final String PAYMENT_CENTRAL_CONFIG_CHECK = "select value from PROPERTYCONFIGURERENTITY where propertyname in('aaaBillingAccountUpdateActionBean.ccStorateEndpointURL','aaaPurchaseScreenActionBean.ccStorateEndpointURL','aaaBillingActionBean.ccStorateEndpointURL')";

	private String propertyAppHost = PropertyProvider.getProperty(CustomTestProperties.APP_HOST);
	private String propertyAppStubURLTemplate = PropertyProvider.getProperty(CustomTestProperties.APP_STUB_URL_TEMPLATE);

	@Test(groups = {Groups.FUNCTIONAL, Groups.PRECONDITION}, description = "Renewal job adding")
	public void renewalJobAdding() {
		adminApp().open();
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_SCHEDULER.get());

		assertThat(GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.RENEWAL_OFFER_GENERATION_PART_1)).isEqualTo(true);
		assertThat(GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.RENEWAL_OFFER_GENERATION_PART_2)).isEqualTo(true);
	}

	@Test(groups = {Groups.FUNCTIONAL, Groups.PRECONDITION},description = "Enable vin refresh")
	public void enableVinRefresh() {
		int result = DBService.get().executeUpdate(String.format(UPDATE_DISPLAYVALUE_BY_CODE, "true"));
		assertThat(result).isGreaterThan(0);
	}

	@Test(groups = {Groups.FUNCTIONAL, Groups.PRECONDITION},description = "Precondition set doc generation endpoints")
	public static void docGenStubEndpointInsert() {
		int result;
		List<String> queries = Arrays.asList(DOC_GEN_WEB_CLIENT,AAA_RETRIEVE_AGREEMENT_WEB_CLIENT,AAA_RETRIEVE_DOCUMENT_WEB_CLIENT);
		for(String query : queries){
			result = DBService.get().executeUpdate(query);
			assertThat(result).isGreaterThan(0);
		}
	}


	@Test(description = "Precondition for to be able to Add Payment methods, Payment Central is stubbed", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public void paymentCentralStubEndPointUpdate() {
		DBService.get().executeUpdate(String.format(PAYMENT_CENTRAL_STUB_ENDPOINT_UPDATE, propertyAppHost, propertyAppStubURLTemplate));
	}

	//http://sit-soaservices.tent.trt.csaa.pri:42000/1.1/RetrieveMembershipSummary
	@Test(description = "Precondition updating Membership Summary Endpoint to Stub", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public void updateMembershipSummaryStubEndpoint() {
		DBService.get().executeUpdate(String.format(RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_UPDATE, propertyAppHost, propertyAppStubURLTemplate));
	}

}
