package aaa.modules.regression.sales.auto_ss.functional;

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
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Dominykas Razgunas
 * @name Test Create Auto SS policy
 * @scenario
 * 1. Create Customer
 * 2. Create Auto policy
 * 3. Verify policy status is active
 * @details
 */
public class TestNewDriverLicenseNumberFormats extends AutoSSBaseTest {

	@Parameters({"state"})
	@StateList(statesExcept = {States.CA})
	@Test(groups = {Groups.HIGH, Groups.FUNCTIONAL}, description = "Test newly added Driver License number formats MO")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-23888")
	public void pas23888_NewLicenceNumberFormatALandMO(@Optional("") String state) {

		String licenseNumberMO = "123F321654";
		String licenseNumberAL = "87456321";
		// TestData for MO license policy
		TestData policyTD1 = getPolicyTD().adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(),
				AutoSSMetaData.DriverTab.LICENSE_STATE.getLabel()), "MO")
				.adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(),
						AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()), licenseNumberMO);
		//TestData for AL license policy
		TestData policyTD2 = getPolicyTD().adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(),
				AutoSSMetaData.DriverTab.LICENSE_STATE.getLabel()), "AL")
				.adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(),
						AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()), licenseNumberAL);

		mainApp().open();
		createCustomerIndividual();
		createPolicy(policyTD1);

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		new DriverTab().getAssetList().getAsset(AutoSSMetaData.DriverTab.LICENSE_STATE).setValue("AL");
		new DriverTab().getAssetList().getAsset(AutoSSMetaData.DriverTab.LICENSE_NUMBER).setValue(licenseNumberAL);
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		createPolicy(policyTD2);

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		new DriverTab().getAssetList().getAsset(AutoSSMetaData.DriverTab.LICENSE_STATE).setValue("MO");
		new DriverTab().getAssetList().getAsset(AutoSSMetaData.DriverTab.LICENSE_NUMBER).setValue(licenseNumberMO);
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}
}