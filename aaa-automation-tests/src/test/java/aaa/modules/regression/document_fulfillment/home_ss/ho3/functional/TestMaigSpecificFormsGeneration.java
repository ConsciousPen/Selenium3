package aaa.modules.regression.document_fulfillment.home_ss.ho3.functional;

import static aaa.modules.regression.sales.auto_ss.functional.preconditions.TestEValueMembershipProcessPreConditions.RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_CHECK;
import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.document_fulfillment.template.functional.TestMaigSpecificFormsGenerationTemplate;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

public class TestMaigSpecificFormsGeneration extends TestMaigSpecificFormsGenerationTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	//@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-2674"})
	public void pas2674_SpecificConversionPacketGenerationForNJ(@Optional("NJ") String state) throws NoSuchFieldException {
		verifyConversionFormsSequence(adjustWithAdditionalInterest(getConversionPolicyDefaultTD()));
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	//@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "retrieveMembershipSummaryEndpointCheck")
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-2674"})
	public void pas2674_SpecificConversionPacketGenerationForOtherStates(@Optional("DE") String state) throws NoSuchFieldException {
		// CW, DE, VA
		verifyConversionFormsSequence(adjustWithMortgageeData(getConversionPolicyDefaultTD()));
	}

	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"PAS-2674"})
	public void pas9816_SpecificBillingPacketGenerationForOtherStates(@Optional("DE") String state) throws NoSuchFieldException {
		// CW, DE, VA
		verifyBillingFormsSequence(getConversionPolicyDefaultTD().adjust(TestData.makeKeyPath("PremiumsAndCoveragesQuoteTab","Payment plan"),"Monthly (Renewal)").resolveLinks());
	}

	@Test(description = "Check membership endpoint", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void retrieveMembershipSummaryEndpointCheck() {
		assertThat(DBService.get().getValue(RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_CHECK).get()).contains(PropertyProvider.getProperty(CustomTestProperties.APP_HOST));
	}

}
