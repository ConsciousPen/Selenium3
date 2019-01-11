package aaa.modules.regression.sales.auto_ca.choice.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Dominykas Razgunas
 * @name Test newly added Driver License number formats MO/AL
 * @scenario
 * 1. Create Customer
 * 2. Create Auto policy with new DL formats
 * 3. Endorse Policy and change DL from MO to AL new format also
 * 4. Bind Policy
 * 5. Create new policy with AL new format
 * 6. Endorse policy and change AL to MO format
 * 7. Bind Policy
 * @details
 */
public class TestNewDriverLicenseNumberFormats extends AutoCaSelectBaseTest {

	@Parameters({"state"})
	@StateList(states = {States.CA})
	@Test(groups = {Groups.HIGH, Groups.FUNCTIONAL}, description = "Test newly added Driver License number formats MO")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-23888")
	public void pas23888_NewLicenceNumberFormatMO(@Optional("") String state) {

		String licenseNumberMO = "123F321654";
		String licenseNumberAL = "87456321";
		// TestData for MO license policy
		TestData policyTD1 = getPolicyTD().adjust(TestData.makeKeyPath(AutoCaMetaData.DriverTab.class.getSimpleName(),
				AutoCaMetaData.DriverTab.LICENSE_STATE.getLabel()), "MO")
				.adjust(TestData.makeKeyPath(AutoCaMetaData.DriverTab.class.getSimpleName(),
						AutoCaMetaData.DriverTab.LICENSE_NUMBER.getLabel()), licenseNumberMO);

		assertDriverLicenseNumberFormat(policyTD1, "AL", licenseNumberAL);
	}

	@Test(groups = {Groups.HIGH, Groups.FUNCTIONAL}, description = "Test newly added Driver License number formats AL")
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-23888")
	public void pas23888_NewLicenceNumberFormatAL(@Optional("") String state) {

		String licenseNumberMO = "123F321654";
		String licenseNumberAL = "87456321";

		//TestData for AL license policy
		TestData policyTD2 = getPolicyTD().adjust(TestData.makeKeyPath(AutoCaMetaData.DriverTab.class.getSimpleName(),
				AutoCaMetaData.DriverTab.LICENSE_STATE.getLabel()), "AL")
				.adjust(TestData.makeKeyPath(AutoCaMetaData.DriverTab.class.getSimpleName(),
						AutoCaMetaData.DriverTab.LICENSE_NUMBER.getLabel()), licenseNumberAL);

		assertDriverLicenseNumberFormat(policyTD2, "MO", licenseNumberMO);
	}

	private void assertDriverLicenseNumberFormat(TestData policyTD, String stateChange, String licenseNumber){
		mainApp().open();
		createCustomerIndividual();
		createPolicy(policyTD);

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
		new DriverTab().getAssetList().getAsset(AutoCaMetaData.DriverTab.LICENSE_STATE).setValue(stateChange);
		new DriverTab().getAssetList().getAsset(AutoCaMetaData.DriverTab.LICENSE_NUMBER).setValue(licenseNumber);
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}
}