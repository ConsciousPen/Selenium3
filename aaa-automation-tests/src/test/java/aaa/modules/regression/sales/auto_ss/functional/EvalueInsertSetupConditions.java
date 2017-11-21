package aaa.modules.regression.sales.auto_ss.functional;

import org.testng.annotations.Test;
import aaa.modules.regression.sales.auto_ss.functional.PreConditions.EvalueInsertPreConditions;
import toolkit.db.DBService;

import java.util.Arrays;
import java.util.List;

public class EvalueInsertSetupConditions implements EvalueInsertPreConditions {

	@Test(description = "Precondition updating Payperless Preferences Endpoint to a Stub")
	public static void paperlessPreferencesStubEndpointUpdate() {
		DBService.get().executeUpdate(String.format(PAPERLESS_PREFERENCE_API_SERVICE_UPDATE, APP_HOST));
	}

	@Test(description = "Precondition updating Membership Summary Endpoint to Stub")
	public static void retrieveMembershipSummaryStubEndpointUpdate() {
		DBService.get().executeUpdate(String.format(RETRIEVE_MEMBERSHIP_SUMMARY_STUB_POINT_UPDATE, APP_HOST));
	}

	@Test(description = "Precondition for AHDRXX form generation")
	public static void ahdrxxConfigCheckUpdate() {
		if (!DBService.get().getValue(AHDRXX_CONFIG_CHECK).isPresent()) {
			DBService.get().executeUpdate(AHDRXX_CONFIG_INSERT);
		}
	}

	@Test(description = "Precondition for AHDEXX form generation")
	public static void ahdexxConfigCheckUpdate() {
		if (!DBService.get().getValue(AHDEXX_CONFIG_CHECK).isPresent()) {
			DBService.get().executeUpdate(AHDEXX_CONFIG_INSERT);
		}
	}

	@Test(description = "Precondition for eValue related Document Generation, different endpoint than Master or PAS13")
	public static void eValueDocGenStubEndpointInsert() {
		DBService.get().executeUpdate(DOC_GEN_WEB_CLIENT);
		DBService.get().executeUpdate(AAA_RETRIEVE_AGREEMENT_WEB_CLIENT);
		DBService.get().executeUpdate(AAA_RETRIEVE_DOCUMENT_WEB_CLIENT);
	}

	@Test(description = "Precondition for enabling eValue Configuration for States with Paperless Preferences stubbed")
	public static void eValueConfigInsert() {
		List<String> configForStates = Arrays.asList("VA"  //for Paperless Preferences = Yes
				, "MD"  //for Paperless Preferences = Pending
				, "DC"); //for Paperless Preferences = No
		//PA should not have eValue or Paperless Preferences Configuration
		for (String configForState : configForStates) {
			insertConfigForRegularStates(configForState);
		}

		List<String> configForStatesLimits = Arrays.asList(
				"MD"
				, "DC");
		for (String configForStatesLimit : configForStatesLimits) {
			insertConfigForLimitsRegularStates(configForStatesLimit);
		}
	}

	@Test(description = "Precondition for Current and Prior BI Limits configurations")
	public static void eValuePriorBiCurrentBiConfigUpdateInsert() {
		DBService.get().executeUpdate(EVALUE_PRIOR_BI_CONFIG_INSERT);
		DBService.get().executeUpdate(EVALUE_CURRENT_BI_CONFIG_INSERT);
	}

	private static void insertConfigForRegularStates(String state) {
		DBService.get().executeUpdate(String.format(EVALUE_CONFIGURATION_PER_STATE_INSERT, state));
		DBService.get().executeUpdate(String.format(PAPERLESS_PREFRENCES_CONFIGURATION_PER_STATE_INSERT, state));
	}

	private static void insertConfigForLimitsRegularStates(String state) {
		DBService.get().executeUpdate(String.format(EVALUE_CURRENT_BI_LIMIT_CONFIGURATION_INSERT, state));
		DBService.get().executeUpdate(String.format(EVALUE_PRIOR_BI_LIMIT_CONFIGURATION_INSERT, state));
	}

	@Test(description = "Precondition for eValue Channel and Territory configurations")
	public static void eValueTerritoryChannelForVAConfigUpdate() {
		DBService.get().executeUpdate(EVALUE_TERRITORY_CHANNEL_FOR_VA_CONFIG_UPDATE);
	}

	@Test(description = "Precondition for eValue Membership Eligibility configurations")
	public static void eValueMembershipEligibilityConfigInsert() {
		DBService.get().executeUpdate(EVALUE_MEMBERSHIP_ELIGIBILITY_CONFIG_INSERT);
	}

	@Test(description = "Precondition Refund/Payment handling, turning on pcDisbursementEngine related functionality")
	public static void refundDocumentGenerationConfigInsert() {
		DBService.get().executeUpdate(REFUND_DOCUMENT_GENERATION_CONFIGURATION_INSERT_SQL);
	}

	@Test(description = "Precondition for to be able to Add Payment methods, Payment Central is stubbed")
	public static void paymentCentralStubEndPointUpdate() {
		DBService.get().executeUpdate(String.format(PAYMENT_CENTRAL_STUB_ENDPOINT_UPDATE, APP_HOST));
	}

}
