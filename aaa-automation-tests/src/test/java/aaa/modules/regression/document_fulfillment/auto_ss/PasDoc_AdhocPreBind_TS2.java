package aaa.modules.regression.document_fulfillment.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum.AutoSSTab;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.verification.CustomSoftAssertions;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;

public class PasDoc_AdhocPreBind_TS2 extends AutoSSBaseTest {
	
	@Parameters({"state"})
	@StateList(statesExcept = States.CA)
	@Test(groups = {Groups.DOCGEN, Groups.HIGH})
	public void TestScenario2_Quote(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(getPolicyTD(), DocumentsAndBindTab.class);
		
		DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	
		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_APPLICATION)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_QUOTE)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTOPAY_AUTHORIZATION_FORM)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
		
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent(false);
		});
		
		NavigationPage.toViewTab(AutoSSTab.DRIVER.get());
		policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_1"), DriverTab.class, PremiumAndCoveragesTab.class, true);
		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.NAMED_DRIVER_EXCLUSION)).isPresent();
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.CRITICAL_INFORMATION_FOR_TEENAGE_DRIVERS_AND_THEIR_PARENTS)).isPresent();
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AAA_USAGE_BASED_INSURANCE_PROGRAM_TERMS_AND_CONDITIONS)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AAA_WITH_SMARTTRECK_ACKNOWLEDGEMENT)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.ACP_SMARTTRECK_SUBSCRIPTION_TERMS)).isPresent();
			
			//Required to Bind section
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.NAMED_DRIVER_EXCLUSION)).isPresent();
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS)).isPresent();
		});
		
		documentsAndBindTab.saveAndExit();
		log.info("TEST: Created quote# " + PolicySummaryPage.getPolicyNumber());
	}

}
