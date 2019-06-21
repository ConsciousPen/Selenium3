package aaa.modules.regression.document_fulfillment.auto_ss;

import static aaa.main.enums.DocGenEnum.Documents.*;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum.AutoSSTab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.docgen.impl.PasDocImpl;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.DocGenEnum.EventName;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssertions;
import toolkit.verification.CustomSoftAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.waiters.Waiters;

public class PasDoc_AdhocPreBind extends AutoSSBaseTest {

	/**
	 * <p>  <b>Adhoc preBind Scenario 1: PreBind page UI</b>
	 * <p>  <b>Adhoc preBind Scenario 2: Forms availability: Quote - adding, Endorse - removing, Renew - adding</b>
	 * <p>  <b>Precondition:</b>
	 * <p>		Quote is created with data: 
	 * <p>		- Policy Type = Standard, 
	 * <p>		- No Excluded Driver, 
	 * <p>		- Vehicle is NOT enrolled in UBI, 
	 * <p>		- Uninsured and Underinsured Coverages = BI.
	 * <p>  <b>Steps:</b>
	 * <p> 1.1. Open 'Document & Bind' tab and check on UI: "Generate Documents" and "Generate eSignature Documents" buttons 
	 * <p> are shown and enabled under the "Documents Available for Printing" section
	 * <p> 2.1. On 'Documents & Bind' tab verify the following forms are present and enabled in 'Available for Printing' section:
	 * <p>		- Auto Insurance Quote (AAIQAZ), 
	 * <p>		- Auto Insurance Application (AA11AZ), 
	 * <p>		- AutoPay Authorization Form (AHAPXX), 
	 * <p>		- Uninsured and Underinsured Motorist Coverage Selection (AA52AZ). 
	 * <p> 2.2. Check the following forms are present in 'Required to Bind' section: 
	 * <p> 		- Auto Insurance Application (AA11AZ)
	 * <p> 2.3. Add Excluded Diver, go to 'Documents & Bind' tab and verify Named Driver Exclusion (AA43AZ) form 
	 * <p>		is shown in both 'Documents Available for Printing' and 'Required to Bind' sections. 
	 * <p> 2.4. Add Driver with Age = 16 (teenage driver), go to 'Documents & Bind' tab and verify  
	 * <p>		Critical Information for Teenage Drivers and Their Parents (AATSXX) form is shown only in 'Documents Available for Printing' section. 
	 * <p> 2.5.  Set Uninsured and Underinsured Coverages < BI, go to 'Documents & Bind' tab and  verify
	 * <p>		Uninsured and Underinsured Motorist Coverage Selection (AA52AZ) form added to 'Required to Bind' section. 
	 * <p> 2.6. Add Vehicle enrolled in UBI, go to 'Documents & Bind' tab and verify 'Documents Available for Printing' section contains forms: 
	 * <p>		- AAA Usage Based Insurance Program Terms and Conditions (AAUBI), 
	 * <p>		- ACP SMARTtrek Subscription Terms and Conditions (ACPUBI), 
	 * <p>		- AAA Insurance with SMARTtrek Acknowledgement of T&Cs and Privacy policies (AAUBI1). 
	 * <p>		and 'Required to Bind' section contains:
	 * <p>		- AAA Insurance with SMARTtrek Acknowledgement of T&Cs and Privacy policies (AAUBI1).
	 * <p> 2.7. Issue Policy, Start 'Inquiry' action, Go to 'Documents & Bind' tab: 
	 * <p>		The same values as were on Quote before Purchasing except: Auto Insurance Quote (AAIQAZ) should NOT be shown. 
	 * <p> 2.8. Start 'Endorsement' action, calculate premium, go to 'Documents & Bind' tab and verify 
	 * <p>		Auto Insurance Quote (AAIQAZ) is not shown, the other forms are the same.
	 * <p> 2.9. Remove Excluded Driver and verify Named Driver Exclusion (AA43AZ) is NOT shown. 
	 * <p> 2.10. Remove Teenage Driver and verify Critical Information for Teenage Drivers and Their Parents (AATSXX) is NOT shown. 
	 * <p> 2.11. Remove Vehicle enrolled in UBI and verify 3 UBI forms are NOT shown: 
	 * <p>		- AAA Usage Based Insurance Program Terms and Conditions (AAUBI), 
	 * <p>		- ACP SMARTtrek Subscription Terms and Conditions (ACPUBI), 
	 * <p>		- AAA with SMARTtrek Acknowledgement of T&Cs and Privacy Policies (AAUBI1).
	 * <p> 2.12. Set Uninsured and Underinsured Coverages = BI and verify 
	 * <p>		Uninsured and Underinsured Motorist Coverage Selection (AA52AZ) is shown only in 'Available for Printing' section. 
	 * <p> 2.13. Purchase Endorsement. 
	 * <p> 2.14. Start 'Renewal' action.
	 * <p> 2.15. Add Excluded Diver and verify Named Driver Exclusion (AA43AZ) form 
	 * <p>		is shown in both 'Documents Available for Printing' and 'Required to Bind' sections. 
	 * <p> 2.16. Add Driver with Age = 16, go to 'Documents & Bind' tab and verify 
	 * <p>		Critical Information for Teenage Drivers and Their Parents (AATSXX) form is shown only in 'Documents Available for Printing' section. 
	 * <p> 2.17. Set Uninsured and Underinsured Coverages < BI, Go to 'Documents & Bind' tab and verify 
	 * <p>		Uninsured and Underinsured Motorist Coverage Selection (AA52AZ) form added to 'Required to Bind' section. 
	 * <p> 2.18. Add Vehicle enrolled in UBI, go to 'Documents & Bind' tab and verify 'Documents Available for Printing' section contains forms:
	 * <p>		- AAA Usage Based Insurance Program Terms and Conditions (AAUBI), 
	 * <p>		- ACP SMARTtrek Subscription Terms and Conditions (ACPUBI), 
	 * <p>		- AAA Insurance with SMARTtrek Acknowledgement of T&Cs and Privacy policies (AAUBI1), 
	 * <p>		and 'Required to Bind' section contains:
	 * <p>		- AAA Insurance with SMARTtrek Acknowledgement of T&Cs and Privacy policies (AAUBI1).
	 * <p> 2.19. Purchase Renewal. 
	 * <p>
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario1_2(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		
		//1.1, 2.1, 2.2
		policy.initiate();
		policy.getDefaultView().fillUpTo(getPolicyTD(), DocumentsAndBindTab.class);		
		DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();

		CustomSoftAssertions.assertSoftly(softly -> {
			//Verify documents in Available for Printing section
			//2.1
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_APPLICATION)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_QUOTE)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTOPAY_AUTHORIZATION_FORM)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
			//Verify enabled buttons
			//1.1
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.BTN_GENERATE_DOCUMENTS)).isEnabled();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.BTN_GENERATE_ESIGNATURE_DOCUMENTS)).isEnabled();
			//Verify documents in Required to Bind section
			//2.2
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent(false);
		});
		documentsGeneration();

		TestData td_sc2 = getTestSpecificTD("TestData_SC2");
		if(!DocGenHelper.isPasDocEnabled(getState(), getPolicyType())) {
			td_sc2.adjust(getTestSpecificTD("TestData_SC2_NO_PASDOC").resolveLinks());
		}
		
		//2.3 - 2.6
		fillFromDriverToDocumentsAndBindTab(td_sc2);		
		CustomSoftAssertions.assertSoftly(softly -> {
			verifyDriverDocsPresent(softly);
			verifyVehicleDocsPresent(softly);
			verifyCoverageDocPresent(softly);
		});
		documentsGeneration();

		//2.7 (only issue policy is covered, verification in Inquiry mode is not covered in test)
		policy.getDefaultView().fillFromTo(td_sc2, DocumentsAndBindTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();		
		log.info("TEST: Policy created #" + PolicySummaryPage.getPolicyNumber());
		
		//Endorsement
		//2.8
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_APPLICATION)).isPresent();
			if(DocGenHelper.isPasDocEnabled(getState(), getPolicyType())) {
				softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
						AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_QUOTE)).isPresent(false);
			}
			verifyDriverDocsPresent(softly);
			verifyVehicleDocsPresent(softly);
			verifyCoverageDocPresent(softly);
		});
		documentsGeneration();
		
		//2.9 - 2.12
		removeAdditionalData(getTestSpecificTD("TestData_Endorsement_SC2"));		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			verifyDriverDocsPresent(false, softly);
			verifyVehicleDocsPresent(false, softly);
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();		
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent(false);
		});
		documentsGeneration();
		
		//2.13
		documentsAndBindTab.submitTab();
		log.info("TEST: Endorsement created for policy #" + PolicySummaryPage.getPolicyNumber());
		
		//Renewal
		//2.14
		policy.renew().perform();	
		//2.15 - 2.18
		fillAdditionalData(td_sc2);		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			verifyDriverDocsPresent(softly);
			verifyVehicleDocsPresent(softly);
			verifyCoverageDocPresent(softly);
		});
		documentsGeneration();
		
		//2.19
		documentsAndBindTab.fillTab(getTestSpecificTD("TestData_Renewal_SC2"));
		DocumentsAndBindTab.btnPurchase.click();
		DocumentsAndBindTab.confirmRenewal.confirm();
		ErrorTab errorTab = new ErrorTab();
		errorTab.overrideAllErrors();
		errorTab.submitTab();
		
		log.info("TEST: Renewal created for policy #" + PolicySummaryPage.getPolicyNumber());	
	}	

	/**
	 * <p>  <b>Adhoc preBind Scenario 3: Forms availability NANO</b>
	 * <p>  <b>Precondition:</b>
	 * <p>		Quote create with Policy Type = Non-Owner. 
	 * <p> 3.1. Go to 'Documents & Bind' tab and verify the following forms are present and available in 'Available for Printing' section:
	 * <p>		- Auto Insurance Quote (AAIQAZ), 
	 * <p>		- Auto Insurance Application (AA11AZ), 
	 * <p>		- AutoPay Authorization Form (AHAPXX),  
	 * <p>		- Uninsured and Underinsured Motorist Coverage Selection (AA52AZ) and Non-Owner Automobile Endorsement (AA41XX) forms are shown 
	 * <p>		in both 'Available for Printing' and 'Required to Bind' sections. 
	 * <p> 3.2. Issue Policy. (Verifications in Inquiry mode are not covered)
	 * <p> 3.3. Start 'Endorsement' action, calculate premium, go to 'Documents & Bind' tab and verify 
	 * <p>		the following forms are present and available for selection in 'Available for Printing' section: 
	 * <p>		- Auto Insurance Application (AA11AZ), 
	 * <p>		- AutoPay Authorization Form (AHAPXX), 
	 * <p>		- Uninsured and Underinsured Motorist Coverage Selection (AA52AZ) and Non-Owner Automobile Endorsement (AA41XX) forms are shown 
	 * <p>		in both 'Available for Printing' and 'Required to Bind' sections. 
	 * <p>		Purchase Endorsement. 
	 * <p> 3.4. Start 'Renewal' action, calculate premium, go to 'Documents & Bind' tab and verify the results are the same as on step 3.3.
	 * <p>
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario3(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		TestData td = getPolicyTD().adjust(getTestSpecificTD("TestData_SC3").resolveLinks());
		policy.getDefaultView().fillUpTo(td, DocumentsAndBindTab.class); 
		//3.1
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
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.NON_OWNER_AUTOMOBILE_ENDORSEMENT)).isPresent();
			//Required To Bind
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.NON_OWNER_AUTOMOBILE_ENDORSEMENT)).isPresent();
		});
		documentsGeneration();
		
		//3.2
		policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_SC3"), DocumentsAndBindTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();		
		log.info("TEST: Policy created #" + PolicySummaryPage.getPolicyNumber());
		
		//Endorsement
		//3.3
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			//Available for Printing
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_APPLICATION)).isPresent();
			if (DocGenHelper.isPasDocEnabled(getState(), getPolicyType())) {
				softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
						AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_QUOTE)).isPresent(false);
			}
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTOPAY_AUTHORIZATION_FORM)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.NON_OWNER_AUTOMOBILE_ENDORSEMENT)).isPresent();
			//Required To Bind
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.NON_OWNER_AUTOMOBILE_ENDORSEMENT)).isPresent();
		});
		documentsGeneration();
		documentsAndBindTab.submitTab();
		log.info("TEST: Endorsement created for policy #" + PolicySummaryPage.getPolicyNumber());
		
		//Renewal
		//3.4
		policy.renew().perform();
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			//Available for Printing
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_APPLICATION)).isPresent();
			if (DocGenHelper.isPasDocEnabled(getState(), getPolicyType())) {
				softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
						AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_QUOTE)).isPresent(false);
			}
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTOPAY_AUTHORIZATION_FORM)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.NON_OWNER_AUTOMOBILE_ENDORSEMENT)).isPresent();
			//Required To Bind
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.NON_OWNER_AUTOMOBILE_ENDORSEMENT)).isPresent();
		});
		documentsGeneration();
		documentsAndBindTab.submitTab();
		log.info("TEST: Renewal created for policy #" + PolicySummaryPage.getPolicyNumber());
	}
	
	/**
	 * <p>  <b>Adhoc preBind Scenario 4: Forms availability: Endorse - adding, Renew - removing</b>
	 * <p>  <b>Precondition:</b>
	 * <p>		Policy is issued: 
	 * <p>		- Policy Type = Standard, 
	 * <p>		- No Excluded Driver, 
	 * <p>		- Vehicle is NOT enrolled in UBI,  
	 * <p>		- Uninsured and Underinsured Coverages = BI. 
	 * <p>  <b>Steps:</b>
	 * <p> 4.1. Start 'Endorsement' action. 
	 * <p> 4.2. Add Excluded Diver, go to 'Documents & Bind' tab and verify form 
	 * <p>		Named Driver Exclusion (AA43AZ) is shown in both 'Documents Available for Printing' and 'Required to Bind' sections. 
	 * <p> 4.3. Add Driver with Age = 16, go to 'Documents & Bind' tab and verify 
	 * <p>		Critical Information for Teenage Drivers and Their Parents (AATSXX) is shown only in 'Documents Available for Printing' section.  		
	 * <p> 4.4. Set Uninsured and Underinsured Coverages < BI, go to 'Documents & Bind' tab and verify  
	 * <p> 		Uninsured and Underinsured Motorist Coverage Selection (AA52AZ) added to 'Required to Bind' section.
	 * <p> 4.5. Add Vehicle enrolled in UBI, go to 'Documents & Bind' tab and verify 'Documents Available for Printing' section contains forms:
	 * <p>		- AAA Usage Based Insurance Program Terms and Conditions (AAUBI), 
	 * <p>		- ACP SMARTtrek Subscription Terms and Conditions (ACPUBI), 
	 * <p>		- AAA Insurance with SMARTtrek Acknowledgement of T&Cs and Privacy policies (AAUBI1), 
	 * <p>		and 'Required to Bind' section contains: 
	 * <p> 		- AAA Insurance with SMARTtrek Acknowledgement of T&Cs and Privacy policies (AAUBI1). 
	 * <p> 4.6. Purchase Endorsement. 
	 * <p> 4.7. Start 'Renewal' action. 
	 * <p>		Remove Excluded Driver and verify form Named Driver Exclusion (AA43AZ) is NOT shown. 
	 * <p> 4.8. Remove Teenage Driver and verify form Critical Information for Teenage Drivers and Their Parents (AATSXX) is NOT shown. 
	 * <p> 4.9. Remove Vehicle enrolled in UBI and verify 3 UBI forms are NOT shown: 
	 * <p>		- AAA Usage Based Insurance Program Terms and Conditions (AAUBI), 
	 * <p>		- ACP SMARTtrek Subscription Terms and Conditions (ACPUBI), 
	 * <p>		- AAA Insurance with SMARTtrek Acknowledgement of T&Cs and Privacy Policies (AAUBI1). 
	 * <p> 4.10. Set Uninsured and Underinsured Coverages = BI and verify 
	 * <p>		Uninsured and Underinsured Motorist Coverage Selection (AA52AZ) is shown only in 'Available for Printing' section. 
	 * <p> 4.11. Purchase Renewal. 		 
	 * <p> 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario4(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		log.info("TEST: Policy created #" + PolicySummaryPage.getPolicyNumber());
		
		TestData td_sc4 = getTestSpecificTD("TestData_SC2");
		if(!DocGenHelper.isPasDocEnabled(getState(), getPolicyType())) {
			td_sc4.adjust(getTestSpecificTD("TestData_SC2_NO_PASDOC").resolveLinks());
		}
		
		//Endorsement
		//4.1
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		
		//4.2 - 4.5
		fillAdditionalData(td_sc4);
		DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			verifyDriverDocsPresent(softly);			
			verifyVehicleDocsPresent(softly);
			verifyCoverageDocPresent(softly);
		});
		documentsGeneration();
		//4.6
		documentsAndBindTab.fillTab(getTestSpecificTD("TestData_Renewal_SC2"));
		//documentsAndBindTab.submitTab();
		DocumentsAndBindTab.btnPurchase.click();
		DocumentsAndBindTab.confirmEndorsementPurchase.confirm();
		ErrorTab errorTab = new ErrorTab();
		errorTab.overrideAllErrors();
		errorTab.submitTab();
		log.info("TEST: Endorsement created for policy #" + PolicySummaryPage.getPolicyNumber());
		
		//Renewal
		//4.7
		policy.renew().perform();
		//4.7 - 4.10
		removeAdditionalData(getTestSpecificTD("TestData_Endorsement_SC2"));		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			verifyDriverDocsPresent(false, softly);			
			verifyVehicleDocsPresent(false, softly);
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent(false);
		});
		documentsGeneration();
		//4.11
		documentsAndBindTab.submitTab();
		log.info("TEST: Renewal created for policy #" + PolicySummaryPage.getPolicyNumber());		
	}
	
	/**
	 * <p>  <b>Adhoc preBind Scenario 5: Generate eSignature Documents: quote, endorsement, renewal</b>
	 * <p>
	 * <p>  <b>Precondition:</b>
	 * <p>		Quote created with data: 
	 * <p>		- Policy Type = Standard, 
	 * <p>		- Excluded Driver is added, 
	 * <p>		- Driver with Age = 16 (teenage driver) is added, 
	 * <p>		- Vehicle enrolled in UBI, 
	 * <p>		- Uninsured and Underinsured Coverages < than BI recommended.
	 * <p>  <b>Steps:</b>
	 * <p> 5.1. Go to 'Documents & Bind' tab, select all documents in "Documents Available for Printing" section (9 forms). 
	 * <p>		Click "Generate Documents" and verify all selected documents are generated.
	 * <p> 5.2. Select Auto Insurance Quote (AAIQAZ), click "Generate Documents" and verify only selected document is generated. 
	 * <p> 5.3. Select Auto Insurance Application (AA11AZ), click "Generate Documents" 
	 * <p>		and verify selected document is generated with Personal Information Privacy Notice (AHPNXX).
	 * <p> 5.4. Select AutoPay Authorization Form (AHAPXX), click "Generate Documents" and verify only selected document is generated. 
	 * <p> 5.5. Select Uninsured and Underinsured Motorist Coverage Selection (AA52AZ), click "Generate Documents" 
	 * <p>		and verify only selected document is generated. 
	 * <p> 5.6. Select Named Driver Exclusion (AA43AZ), click "Generate Documents" and verify only selected document is generated. 
	 * <p> 5.7. Select Critical Information for Teenage Drivers and Their Parents (AATSXX), click "Generate Documents" 
	 * <p>		and verify only selected document is generated. 
	 * <p> 5.8. Select AAA Usage Based Insurance Program Terms and Conditions (AAUBI), click "Generate Documents" 
	 * <p>		and verify only selected document is generated + AAPNUBI.
	 * <p> 5.9. Select ACP SMARTtrek Subscription Terms and Conditions (ACPUBI), click "Generate Documents" 
	 * <p>		and verify only selected document is generated + ACPPNUBI. 
	 * <p> 5.10. Select AAA Insurance with SMARTtrek Acknowledgement of T&Cs and Privacy Policies (AAUBI1), click "Generate Documents" 
	 * <p> 		and verify only selected document is generated.
	 * <p> 5.11. Issue Policy. 
	 * <p> 5.12. Start "Endorsement" action, calculate premium, go to 'Documents & Bind' tab. Repeat steps 5.1, 5.3-5.10 
	 * <p>		and verify the same results as in steps 5.1, 5.3-5.10. 
	 * <p> 		Issue Endorsement. 
	 * <p> 5.13. Start "Renewal" action, calculate premium, go to 'Documents & Bind' tab. 
	 * 		Repeat steps 5.1, 5.3-5.10 and verify the same results as in steps 5.1, 5.3-5.10. Issue Renewal.
	 * 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario5(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc5;
		if(DocGenHelper.isPasDocEnabled(getState(), getPolicyType())) {
			td_sc5 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC5").resolveLinks());
		}
		else {
			td_sc5 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC5_NO_PASDOC").resolveLinks());
		}		
		policy.initiate();
		policy.getDefaultView().fillUpTo(td_sc5, DocumentsAndBindTab.class, true);
		DocumentsAndBindTab docsAndBindTab = new DocumentsAndBindTab();
		docsAndBindTab.saveAndExit();
		String quoteNumber = PolicySummaryPage.getPolicyNumber();
		log.info("TEST: Quote created #" + quoteNumber);
		
		//5.1
		policy.dataGather().start();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		//Verify generation all docs
		generateAndVerifyDoc(getTestSpecificTD("TestData_GenAllDocs_Quote"), quoteNumber, AAIQAZ, AA11AZ, AHAPXX, AA52AZ, AA43AZ, AATSXX, AAUBI, ACPPNUBI, AAUBI1);	
		//Verify docs generation
		//5.2
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AAIQAZ"), quoteNumber, AAIQAZ);
		
		docsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_QUOTE).setValue("No");
		//5.3
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA11XX"), quoteNumber, AA11AZ);
		//5.4
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AHAPXX"), quoteNumber, AHAPXX);
		//5.5
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA52AZ"), quoteNumber, AA52AZ);
		//5.6
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA43AZ"), quoteNumber, AA43AZ);
		//5.7
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AATSXX"), quoteNumber, AATSXX);
		//5.8
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AAUBI"), quoteNumber, AAUBI);
		//5.9
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_ACPUBI"), quoteNumber, ACPPNUBI);
		//5.10
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AAUBI1"), quoteNumber, AAUBI1);
		//5.1b
		//log.info("Distribution Channel is: " + getDistributionChannel(quoteNumber));
		//5.11
		docsAndBindTab.submitTab();
		new PurchaseTab().fillTab(getPolicyTD());
		new PurchaseTab().submitTab();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		log.info("TEST: Policy created #" + policyNumber);
		
		//5.12
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();	
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		//Verify generation all docs
		generateAndVerifyDoc(getTestSpecificTD("TestData_GenAllDocs"), quoteNumber, AA11AZ, AHAPXX, AA52AZ, AA43AZ, AATSXX, AAUBI, ACPPNUBI, AAUBI1);	
		//Verify docs generation
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA11XX"), policyNumber, AA11AZ);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AHAPXX"), policyNumber, AHAPXX);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA52AZ"), policyNumber, AA52AZ);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA43AZ"), policyNumber, AA43AZ);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AATSXX"), policyNumber, AATSXX);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AAUBI"), policyNumber, AAUBI);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_ACPUBI"), policyNumber, ACPPNUBI);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AAUBI1"), policyNumber, AAUBI1);
		//log.info("Distribution Channel is: " + getDistributionChannel(policyNumber));
		docsAndBindTab.submitTab();
		log.info("TEST: Endorsement created for policy#" + PolicySummaryPage.getPolicyNumber());
		
		//5.13
		policy.renew().start();
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		//Verify generation all docs
		generateAndVerifyDoc(getTestSpecificTD("TestData_GenAllDocs"), quoteNumber, AA11AZ, AHAPXX, AA52AZ, AA43AZ, AATSXX, AAUBI, ACPPNUBI, AAUBI1);	
		//Verify docs generation
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA11XX"), policyNumber, AA11AZ);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AHAPXX"), policyNumber, AHAPXX);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA52AZ"), policyNumber, AA52AZ);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA43AZ"), policyNumber, AA43AZ);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AATSXX"), policyNumber, AATSXX);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AAUBI"), policyNumber, AAUBI);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_ACPUBI"), policyNumber, ACPPNUBI);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AAUBI1"), policyNumber, AAUBI1);
		//log.info("Distribution Channel is: " + getDistributionChannel(policyNumber));
		docsAndBindTab.submitTab();
		log.info("TEST: Renewal created for policy#" + PolicySummaryPage.getPolicyNumber());		
	}
	
	/**
	 * <p>  <b>Adhoc preBind Scenario 6: Generate Documents NANO</b>
	 * <p>  <b>Precondition:</b>
	 * <p>		Quote created with Policy Type = Non-Owner. 
	 * <p>  <b>Steps:</b>
	 * <p> 6.1. Go to 'Documents & Bind' tab, select Non-Owner Automobile Endorsement (AA41XX) in "Documents Available for Printing" section. 
	 * <p>		Click "Generate Documents" and verify that new tab with the documents is opened and only selected document is generated. 
	 * <p>		Issue Policy. 
	 * <p> 6.2. Start "Endorsement" action, calculate premium, go to 'Documents & Bind' tab. 
	 * <p>		Repeat step 6.1 and verify the result is the same as in step 6.1. 
	 * <p>		Issue Endorsement. 
	 * <p> 6.3. Start "Renewal" action, calculate premium, Go to 'Documents & Bind' tab. 
	 * <p>		Repeat step 6.1 and verify the result is the same as in step 6.1. 
	 * <p>
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario6(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc6 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC3").resolveLinks());	
		
		policy.initiate();
		policy.getDefaultView().fillUpTo(td_sc6, DocumentsAndBindTab.class, true);
		DocumentsAndBindTab docsAndBindTab = new DocumentsAndBindTab();
		docsAndBindTab.saveAndExit();
		String quoteNumber = PolicySummaryPage.getPolicyNumber();
		log.info("TEST: Quote created #" + PolicySummaryPage.getPolicyNumber());
		
		//6.1
		policy.dataGather().start();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		docsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_QUOTE).setValue("No");
		
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA41XX"), quoteNumber, AA41XX);		
		//log.info("Distribution Channel is: " + getDistributionChannel(quoteNumber));
		docsAndBindTab.submitTab();
		new PurchaseTab().fillTab(getPolicyTD());
		new PurchaseTab().submitTab();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		log.info("TEST: Policy created #" + policyNumber);
		
		//6.2
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();	
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA41XX"), policyNumber, AA41XX);
		//log.info("Distribution Channel is: " + getDistributionChannel(policyNumber));
		docsAndBindTab.submitTab();
		log.info("TEST: Endorsement created for policy#" + PolicySummaryPage.getPolicyNumber());
		
		//6.3
		policy.renew().start();
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA41XX"), policyNumber, AA41XX);
		//log.info("Distribution Channel is: " + getDistributionChannel(policyNumber));
		docsAndBindTab.submitTab();
		log.info("TEST: Renewal created for policy#" + PolicySummaryPage.getPolicyNumber());		
	}
	
	/**
	 * <p>  <b>Adhoc preBind Scenario 7: Generate eSignature Documents": quote, endorsement, renewal</b>
	 * <p>  <b>Precondition:</b>
	 * <p>		Quote created with data: 
	 * <p>		- Policy Type = Standard, 
	 * <p>		- Excluded Driver is added, 
	 * <p>		- Driver with Age = 16 (teenage driver) is added, 
	 * <p>		- Vehicle enrolled in UBI, 
	 * <p>		- Uninsured and Underinsured Coverages < than BI recommended. 
	 * <p>  <b>Steps:</b>
	 * <p> 7.1. Go to 'Documents & Bind' tab, select all documents in "Documents Available for Printing" section (9 forms), 
	 * <p> 		click "Generate eSignature Documents" and verify the popup with "Recipient Email Address" field is opened. 
	 * <p> 7.2. Select Auto Insurance Quote (AAIQAZ) and verify "Generate eSignature Documents" is disabled 
	 * <p>		(because AAIQAZ form is not eSignable). 
	 * <p> 7.3. Select Auto Insurance Application (AA11AZ), click "Generate eSignature Documents"
	 * <p>		and verify eSignature link is sent. 
	 * <p> 7.4. Select AutoPay Authorization Form (AHAPXX), click "Generate eSignature Documents" 
	 * <p>		and verify eSignature link is sent. 
	 * <p> 7.5. Select Uninsured and Underinsured Motorist Coverage Selection (AA52AZ), click "Generate eSignature Documents" 
	 * <p>		and verify eSignature link is sent. 
	 * <p> 7.6. Select Named Driver Exclusion (AA43AZ), click "Generate eSignature Documents"  
	 * <p>		and verify eSignature link is sent. 
	 * <p> 7.7. Select Critical Information for Teenage Drivers and Their Parents (AATSXX) and verify  
	 * <p>		"Generate eSignature Documents" is disabled (because AATSXX form is not eSignable). 
	 * <p> 7.8. Select AAA Usage Based Insurance Program Terms and Conditions (AAUBI) and verify 
	 * <p>		"Generate eSignature Documents" is disabled (because AAUBI form is not eSignable). 
	 * <p> 7.9. Select ACP SMARTtrek Subscription Terms and Conditions (ACPUBI) and verify  
	 * <p>		"Generate eSignature Documents" is disabled (because ACPUBI form is not eSignable). 
	 * <p> 7.10. Select AAA Insurance with SMARTtrek Acknowledgement of T&Cs and Privacy Policies (AAUBI1), click "Generate eSignature Documents"  
	 * <p>		and verify eSignature link is sent. 
	 * <p> 7.11. Issue Policy. 
	 * <p> 7.12. Start "Endorsement" action, calculate premium, go to 'Documents & Bind' tab. 
	 * <p>		Repeat steps 7.1, 7.3-7.10 and verify the same results as in steps 7.1 (8 forms), 7.3-7.10.
	 * <p>		Issue Endorsement. 
	 * <p> 7.13. Start "Renewal" action, calculate Premium, go to 'Documents & Bind' tab. 
	 * <p>		Repeat steps 7.1, 7.3-7.10 and verify the same results as in steps 7.1 (8 forms), 7.3-7.10.
	 * <p>
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario7(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();		
		TestData td_sc7;
		if(DocGenHelper.isPasDocEnabled(getState(), getPolicyType())) {
			td_sc7 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC5").resolveLinks());
		}
		else {
			td_sc7 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC5_NO_PASDOC").resolveLinks());
		}		
		//TestData td_sc7 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC5").resolveLinks());
		policy.initiate();
		policy.getDefaultView().fillUpTo(td_sc7, DocumentsAndBindTab.class, true);
		DocumentsAndBindTab docsAndBindTab = new DocumentsAndBindTab();
		docsAndBindTab.saveAndExit();
		log.info("TEST: Quote created #" + PolicySummaryPage.getPolicyNumber());
		
		//7.1
		policy.dataGather().start();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		//Verify generation all docs
		generateESignatureDocs(getTestSpecificTD("TestData_GenAllDocs"), true);		
		
		//Verify generation of separate doc
		docsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_QUOTE).setValue("No");
		//7.2
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAIQAZ"), false);
		//7.3
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA11XX"), true);
		//7.4
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AHAPXX"), true);
		//7.5
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA52AZ"), true);
		//7.6
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA43AZ"), true);
		//7.7
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AATSXX"), false);
		//7.8
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAUBI"), false);
		//7.9
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_ACPUBI"), false);
		//7.10
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAUBI1"), true);
		//7.11
		docsAndBindTab.submitTab();
		new PurchaseTab().fillTab(getPolicyTD());
		new PurchaseTab().submitTab();
		log.info("TEST: Policy created #" + PolicySummaryPage.getPolicyNumber());
		
		//7.12
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();	
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		//Verify generation all docs
		generateESignatureDocs(getTestSpecificTD("TestData_GenAllDocs"), true);		
		//Verify generation of separate doc
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA11XX"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AHAPXX"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA52AZ"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA43AZ"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AATSXX"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAUBI"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_ACPUBI"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAUBI1"), true);		
		docsAndBindTab.submitTab();
		log.info("TEST: Endorsement created for policy#" + PolicySummaryPage.getPolicyNumber());
		
		//7.13
		policy.renew().start();
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		//Verify generation all docs
		generateESignatureDocs(getTestSpecificTD("TestData_GenAllDocs"), true);		
		//Verify generation of separate doc
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA11XX"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AHAPXX"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA52AZ"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA43AZ"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AATSXX"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAUBI"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_ACPUBI"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAUBI1"), true);
		docsAndBindTab.submitTab();
		log.info("TEST: Renewal created for policy#" + PolicySummaryPage.getPolicyNumber());	
	}
	
	/**
	 * <p>  <b>Adhoc preBind Scenario 8: Generate eSignature Documents NANO</b>
	 * <p>  <b>Precondition:</b>
	 * <p>		Quote created with Policy Type = Non-Owner. 
	 * <p>  <b>Steps:</b>
	 * <p> 8.1. Go to 'Documents & Bind' tab, select Non-Owner Automobile Endorsement (AA41XX) in "Documents Available for Printing" section, 
	 * <p>		click "Generate eSignature Documents" and verify the pop-up with "Recipient Email Address" field is opened. 
	 * <p>		Issue Policy. 
	 * <p> 8.2. Start "Endorsement" action, calculate premium, go to 'Documents & Bind' tab. 
	 * <p>		Repeat steps 8.1 and verify results are the same as in steps 8.1. 
	 * <p>		Issue Endorsement. 
	 * <p> 8.3. Start "Renewal" action, calculate premium, go to 'Documents & Bind' tab. 
	 * <p>		Repeat steps 8.1 and verify results are the same as in steps 8.1.
	 * <p>
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario8(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc8 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC3").resolveLinks());			
		policy.initiate();
		policy.getDefaultView().fillUpTo(td_sc8, DocumentsAndBindTab.class, true);
		DocumentsAndBindTab docsAndBindTab = new DocumentsAndBindTab();
		docsAndBindTab.saveAndExit();
		log.info("TEST: Quote created #" + PolicySummaryPage.getPolicyNumber());
		
		//8.1
		policy.dataGather().start();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA41XX"), true);
		docsAndBindTab.submitTab();
		new PurchaseTab().fillTab(getPolicyTD());
		new PurchaseTab().submitTab();
		log.info("TEST: Policy created #" + PolicySummaryPage.getPolicyNumber());
		
		//8.2
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();	
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA41XX"), true);
		docsAndBindTab.submitTab();
		log.info("TEST: Endorsement created for policy#" + PolicySummaryPage.getPolicyNumber());
		
		//8.3
		policy.renew().start();
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA41XX"), true);
		docsAndBindTab.submitTab();
		log.info("TEST: Renewal created for policy#" + PolicySummaryPage.getPolicyNumber());
	}
	
	/**
	 * The method clicks on Generate Documents button under 'Available For Printing' section
	 */
	private void documentsGeneration() {
		DocumentsAndBindTab.btnGenerateDocuments.click();
		WebDriverHelper.switchToDefault();
	}
	
	/**
	 * The method select documents on Document And Bind tab, generate selected documents and verify selected documents are generated
	 * @param td_doc 		Documents should be selected on Documents And Bind tab
	 * @param policyNum		Policy number
	 * @param documents		Documents (forms) should be generated
	 */
	private void generateAndVerifyDoc(TestData td_doc, String policyNum, DocGenEnum.Documents... documents) {
		DocumentsAndBindTab docsAndBindTab = new DocumentsAndBindTab();
		docsAndBindTab.fillTab(td_doc);
		DocumentsAndBindTab.btnGenerateDocuments.click();
		WebDriverHelper.switchToDefault();
		if(DocGenHelper.isPasDocEnabled(getState(), getPolicyType())) {
			PasDocImpl.verifyDocumentsGenerated(null, true, false, policyNum, EventName.ADHOC_DOC_GENERATE, documents);
		} else {
			DocGenHelper.verifyDocumentsGenerated(true, false, policyNum, documents);		
		}
	}
	
	/**
	 * The method select documents and generate eSignature documents if 'Generate eSignature Documents' button is enabled. 
	 * @param td_doc		Documents should be selected on Documents And Bind tab
	 * @param isActiveBtn	Flag shows enabled or disabled 'Generate eSignature Documents' button
	 */
	private void generateESignatureDocs(TestData td_doc, boolean isActiveBtn) {
		DocumentsAndBindTab docsAndBindTab = new DocumentsAndBindTab();
		docsAndBindTab.fillTab(td_doc);
		CustomAssertions.assertThat(docsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.BTN_GENERATE_ESIGNATURE_DOCUMENTS)).isPresent(isActiveBtn);
		if (isActiveBtn) {
			DocumentsAndBindTab.btnGenerateESignaturaDocuments.click(Waiters.AJAX);
			CustomAssertions.assertThat(docsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.ENTER_RECIPIENT_EMAIL_ADDRESS_DIALOG)).isPresent();
			docsAndBindTab.getEnterRecipientEmailAddressDialogAssetList().getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EnterRecipientEmailAddressDialog.RECIPIENT_EMAIL_ADDRESS).setValue("test@email.com");
			docsAndBindTab.getEnterRecipientEmailAddressDialogAssetList().getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EnterRecipientEmailAddressDialog.BTN_OK).click();		
			Page.dialogConfirmation.buttonOk.click();
		}
	}
	
	private void fillAdditionalData(TestData td) {
		NavigationPage.toViewTab(AutoSSTab.DRIVER.get());
		policy.getDefaultView().fillFromTo(td, DriverTab.class, PremiumAndCoveragesTab.class, true);
	}
	
	private void fillFromDriverToDocumentsAndBindTab(TestData td) {
		NavigationPage.toViewTab(AutoSSTab.DRIVER.get());
		policy.getDefaultView().fillUpTo(td, DocumentsAndBindTab.class);
	}
	
	private void removeAdditionalData(TestData td) {
		NavigationPage.toViewTab(AutoSSTab.DRIVER.get());
		DriverTab.tableDriverList.removeRow(3);
		DriverTab.tableDriverList.removeRow(2);
		
		NavigationPage.toViewTab(AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.removeRow(2);
		
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().fillTab(td);
		new PremiumAndCoveragesTab().calculatePremium();
	}
	
	/**
	 * The method verifies the documents: 'Named Driver Exclusion' (AA43AZ) and 
	 * 'Critical Information for Teenage Drivers and Their Parents' (AATSXX) 
	 * are present or absent on Documents And Bind tab
	 * @param value
	 * @param softly
	 */
	private void verifyDriverDocsPresent(boolean value, ETCSCoreSoftAssertions softly) {
		//Documents in Available For Printing section
		softly.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.NAMED_DRIVER_EXCLUSION_ELECTION)).isPresent(value);			
		softly.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.CRITICAL_INFORMATION_FOR_TEENAGE_DRIVERS_AND_THEIR_PARENTS)).isPresent(value);
		//Document in Required to Bind section
		softly.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.NAMED_DRIVER_EXCLUSION_ELECTION)).isPresent(value);
	}
	
	/**
	 * The method verifies the documents: 'Named Driver Exclusion' (AA43AZ) and 
	 * 'Critical Information for Teenage Drivers and Their Parents' (AATSXX) are present on Documents And Bind tab
	 * @param softly
	 */
	private void verifyDriverDocsPresent(ETCSCoreSoftAssertions softly) {
		verifyDriverDocsPresent(true, softly);
	}
	
	/**
	 * The method verifies the documents related to Enrolled in UBI Vehicle are present or absent 
	 * on both 'Available For Printing' and 'Required To Bind' sections 
	 * @param value
	 * @param softly
	 */
	private void verifyVehicleDocsPresent(boolean value, ETCSCoreSoftAssertions softly) {
		//Documents in Available for Printing section
		softly.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AAA_USAGE_BASED_INSURANCE_PROGRAM_TERMS_AND_CONDITIONS)).isPresent(value);
		softly.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT)).isPresent(value);
		softly.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.ACP_SMARTTRECK_SUBSCRIPTION_TERMS)).isPresent(value);
		//Document in Required to Bind section
		softly.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS)).isPresent(value);
	}
	
	/**
	 * The method verifies the documents related to Enrolled in UBI Vehicle are present
	 * on both 'Available For Printing' and 'Required To Bind' sections
	 * @param softly
	 */
	private void verifyVehicleDocsPresent(ETCSCoreSoftAssertions softly) {
		verifyVehicleDocsPresent(true, softly);
	}
	
	/**
	 * The method verifies the document 'Uninsured and Underinsured Motorist Coverage Selection' (AA52AZ)
	 * is present in both 'Available For Printing' and 'Required To Bind' sections
	 * @param softly
	 */
	private void verifyCoverageDocPresent(ETCSCoreSoftAssertions softly) {
		softly.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
		
		softly.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
	}
	
}
