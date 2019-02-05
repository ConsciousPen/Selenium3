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
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;

public class PasDoc_AdhocPreBind extends AutoSSBaseTest {

	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.HIGH})
	public void testScenario2(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(getPolicyTD(), DocumentsAndBindTab.class);
		
		DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();

		CustomSoftAssertions.assertSoftly(softly -> {
			//Available for Printing
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_APPLICATION)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_QUOTE)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTOPAY_AUTHORIZATION_FORM)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
			//Required to Bind
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent(false);
		});
		
		NavigationPage.toViewTab(AutoSSTab.DRIVER.get());
		policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_1"), DriverTab.class, DriverActivityReportsTab.class, true);
		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			//Available for Printing
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

		policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_1"), DocumentsAndBindTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();
		
		log.info("TEST: Policy created #" + PolicySummaryPage.getPolicyNumber());
		
		//TODO Policy Inquiry
		policy.policyInquiry().start();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(DocumentsAndBindTab.availableForPrinting_AutoInsuranceApplication).isPresent();
			softly.assertThat(DocumentsAndBindTab.requiredToBind_AutoInsuranceApplication).isPresent();
		});
		documentsAndBindTab.cancel();
		
		//Endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		
		CustomSoftAssertions.assertSoftly(softly -> {
			//Available for Printing
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
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
			
			//Required to Bind section
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.NAMED_DRIVER_EXCLUSION)).isPresent();
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS)).isPresent();
		});
		
		NavigationPage.toViewTab(AutoSSTab.DRIVER.get());
		DriverTab.tableDriverList.removeRow(3);
		DriverTab.tableDriverList.removeRow(2);
		
		NavigationPage.toViewTab(AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.removeRow(2);
		
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().fillTab(getTestSpecificTD("TestData_Endorsement"));
		new PremiumAndCoveragesTab().calculatePremium();
		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			//Available for Printing
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.NAMED_DRIVER_EXCLUSION)).isPresent(false);			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.CRITICAL_INFORMATION_FOR_TEENAGE_DRIVERS_AND_THEIR_PARENTS)).isPresent(false);
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AAA_USAGE_BASED_INSURANCE_PROGRAM_TERMS_AND_CONDITIONS)).isPresent(false);
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AAA_WITH_SMARTTRECK_ACKNOWLEDGEMENT)).isPresent(false);
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.ACP_SMARTTRECK_SUBSCRIPTION_TERMS)).isPresent(false);
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
			
			//Required to Bind section
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.NAMED_DRIVER_EXCLUSION)).isPresent(false);
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent(false);
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS)).isPresent(false);
		});
		documentsAndBindTab.submitTab();
		log.info("TEST: Endorsement created for policy #" + PolicySummaryPage.getPolicyNumber());
		
		//Renewal
		policy.renew().perform();
		NavigationPage.toViewTab(AutoSSTab.DRIVER.get());
		policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_1"), DriverTab.class, PremiumAndCoveragesTab.class, true);
		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			//Available for Printing
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
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
			
			//Required to Bind section
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.NAMED_DRIVER_EXCLUSION)).isPresent();
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS)).isPresent();
		});
		documentsAndBindTab.fillTab(getTestSpecificTD("TestData_Renewal"));
		DocumentsAndBindTab.btnPurchase.click();
		DocumentsAndBindTab.confirmRenewal.confirm();
		ErrorTab errorTab = new ErrorTab();
		errorTab.overrideAllErrors();
		errorTab.submitTab();
		
		log.info("TEST: Renewal created for policy #" + PolicySummaryPage.getPolicyNumber());
		
	}	
	
}
