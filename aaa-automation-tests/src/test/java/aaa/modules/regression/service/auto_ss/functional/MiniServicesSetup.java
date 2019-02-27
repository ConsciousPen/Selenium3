package aaa.modules.regression.service.auto_ss.functional;

import static aaa.modules.regression.sales.auto_ss.functional.preconditions.EvalueInsertSetupPreConditions.PROPERTY_CONFIGURER_ENTITY_INSERT;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.config.CsaaTestProperties;
import aaa.helpers.constants.Groups;
import aaa.helpers.listeners.AaaTestListener;
import aaa.modules.regression.service.auto_ss.functional.preconditions.MiniServicesSetupPreconditions;
import toolkit.config.PropertyProvider;
import toolkit.db.DBService;
import toolkit.verification.CustomSoftAssertions;

@Listeners({AaaTestListener.class})
public class MiniServicesSetup extends MiniServicesSetupPreconditions {

	@Test(description = "Precondition", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void miniServicesEndorsementDeleteDelayConfigInsert() {
		DBService.get().executeUpdate(MiniServicesSetupPreconditions.AAA_CUSTOMER_ENDORSEMENT_DAYS_CONFIG_INSERT);
		DBService.get().executeUpdate(MiniServicesSetupPreconditions.AAA_CUSTOMER_ENDORSMENT_DAYS_DEFAULD_CONFIG_UPDATE);
	}

	@Test(description = "Precondition", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void premiumTaxRateUri() {
		DBService.get().executeUpdate(MiniServicesSetupPreconditions.AAA_PREMIUM_TAX_RATE_URI_UPDATE);
	}

	@Test(description = "Precondition", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void myPolicyUserEnableUpdate() {
		DBService.get().executeUpdate(MiniServicesSetupPreconditions.MY_POLICY_USER_ENABLE_UPDATE);
	}

	/**
	 * Disabled, because MyPolicy user became System User
	 */
	@Test(enabled = false, description = "Precondition", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void myPolicyUserAddAllPrivilegesUpdate() {
		if (Boolean.valueOf(PropertyProvider.getProperty(CsaaTestProperties.SCRUM_ENVS_SSH)).equals(true)) {
			DBService.get().executeUpdate(MiniServicesSetupPreconditions.MY_POLICY_USER_ADD_ALL_PRIVILEGES_UPDATE);
		}
	}

	@Test(enabled = false, description = "Precondition adding MyPolicy as a user for Digital", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void myPolicyUserAddedConfigInsert() {
		DBService.get().executeUpdate(String.format(PROPERTY_CONFIGURER_ENTITY_INSERT, "gn3zhyt", "MyPolicy user", "aaaDigitalValidationService.pasDxpUser", "MyPolicy"));
	}

	@Test(enabled = false, description = "Precondition removing default config for Inelligible States", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void deleteIneligibleForMyPolicyStatesConfig() {
		DBService.get().executeUpdate(DELETE_INELIGIBLE_FOR_MY_POLICY_STATES_CONFIG);
	}

	@Test(description = "Precondition", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void miniServicesEndorsementInsertConfigDetermineEndorsementTypeAvailable() {
		DBService.get().executeUpdate(MiniServicesSetupPreconditions.AAA_LOOKUP_CONFIG_INSERT_UPDATE_DRIVER);
		DBService.get().executeUpdate(MiniServicesSetupPreconditions.AAA_LOOKUP_CONFIG_INSERT_UPDATE_VEHICLE);
		DBService.get().executeUpdate(MiniServicesSetupPreconditions.AAA_LOOKUP_CONFIG_INSERT_UPDATE_COVERAGES);
	}

	@Test(description = "Enabling 'canChange' for UMBI and UMPD for VA on test environment. This is turned off by default till atleast 19.5", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void enableUMBIAndUMPDForVA() {
		String state = Constants.States.VA;
		//check that UMBI and UMPD is disabled by default for VA
		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(DBService.get().executeUpdate(String.format(AAA_LOOKUP_CONFIG_GET_CANCHANGE_FOR_STATE_COVERAGE, state, "UMBI")))
					.as("Is (and should) 'canChange' enabled for UMBI and UMPD by default for VA? If so, please remove this precondition method as it is no more needed.").isEqualTo(0);
			softly.assertThat(DBService.get().executeUpdate(String.format(AAA_LOOKUP_CONFIG_GET_CANCHANGE_FOR_STATE_COVERAGE, state, "UMPD")))
					.as("Is (and should) 'canChange' enabled for UMBI and UMPD by default for VA? If so, please remove this precondition method as it is no more needed.").isEqualTo(0);
		});

		//enable UMBI and UMPD for VA
		DBService.get().executeUpdate(String.format(AAA_LOOKUP_CONFIG_ENABLE_CANCHANGE_FOR_STATE_COVERAGE, state, "UMBI"));
		DBService.get().executeUpdate(String.format(AAA_LOOKUP_CONFIG_ENABLE_CANCHANGE_FOR_STATE_COVERAGE, state, "UMPD"));
	}
}
