package aaa.modules.preconditions;

import static aaa.modules.regression.sales.auto_ss.functional.preconditions.EvalueInsertSetupPreConditions.*;
import static toolkit.verification.CustomAssertions.assertThat;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;
import aaa.config.CsaaTestProperties;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.VehicleQueries;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.modules.BaseTest;
import toolkit.config.PropertyProvider;
import toolkit.db.DBService;

public class ScorpionsPreconditions extends BaseTest {

	private String propertyAppHost = PropertyProvider.getProperty(CsaaTestProperties.APP_HOST);
	private String propertyAppStubURLTemplate = PropertyProvider.getProperty(CsaaTestProperties.APP_STUB_URL_TEMPLATE);

	@Test(groups = {Groups.FUNCTIONAL, Groups.PRECONDITION}, description = "Renewal job adding")
	public void renewalJobAdding() {
		JobUtils.createJob(BatchJob.Renewal_Offer_Generation_Part1);
		JobUtils.createJob(BatchJob.Renewal_Offer_Generation_Part2);
	}

	@Test(groups = {Groups.FUNCTIONAL, Groups.PRECONDITION},description = "Enable vin refresh")
	public static void enableVinRefresh() {
		int result = DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_DISPLAYVALUE_BY_CODE, "true"));
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
