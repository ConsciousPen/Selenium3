package aaa.modules.regression.document_fulfillment.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import static aaa.main.enums.DocGenEnum.Documents.*;

public class PasDoc_OnlineBatch extends AutoSSBaseTest {
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario1(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();		
		//Scenario 1: Driver with chargeable activity
		TestData td_driverWithActivity = getPolicyTD().adjust(getTestSpecificTD("TestData_DriverWithActivity").resolveLinks());
		String policy_driverWithActivity = createPolicy(td_driverWithActivity);
		log.info("PAS DOC: Scenario 1: Policy with driver with chargeable activity created: " + policy_driverWithActivity);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_driverWithActivity, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2); //AAAEAZ2
		
		//Scenario 1b: Add all types of vehicles
		TestData td_allTypeVehicles = getPolicyTD().adjust(getTestSpecificTD("TestData_AllTypeVehicles").resolveLinks());
		String policy_allTypeVehicles = createPolicy(td_allTypeVehicles);
		log.info("PAS DOC: Scenario 1B: Policy with all types of vehicles created: " + policy_allTypeVehicles);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_allTypeVehicles, AA10XX); 
		
		//Scenario 1c: Add 6 vehicles or more
		TestData td_6vehicles = getPolicyTD().adjust(getTestSpecificTD("TestData_6_Vehicles").resolveLinks());
		String policy_6vehicles = createPolicy(td_6vehicles);
		log.info("PAS DOC: Scenario 1C: Policy with 6 vehicles created: " + policy_6vehicles);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_6vehicles, AA10XX); 		
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario2(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_overrideScore = getPolicyTD().adjust(getTestSpecificTD("TestData_OverrideInsuranceScore").resolveLinks());
		String policy_overrideScore = createPolicy(td_overrideScore);
		log.info("PAS DOC: Scenario 2: Policy with overrided insurance score created: " + policy_overrideScore);
		DocGenHelper.verifyDocumentsGenerated(false, false, policy_overrideScore, AHAUXX);
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario3(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		//Scenario 3: Add Excluded Driver
		TestData td_excludedDriver = getPolicyTD().adjust(getTestSpecificTD("TestData_ExcludedDriver").resolveLinks());
		String policy_excludedDriver = createPolicy(td_excludedDriver);
		log.info("PAS DOC: Scenario 3: Policy with excluded driver created: " + policy_excludedDriver);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_excludedDriver, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AA43AZ);
		
		//Scenario 3b: Add 2 Excluded Drivers
		TestData td_2excludedDrivers = getPolicyTD().adjust(getTestSpecificTD("TestData_2ExcludedDrivers").resolveLinks());
		String policy_2excludedDrivers = createPolicy(td_2excludedDrivers);
		log.info("PAS DOC: Scenario 3b: Policy with 2 excluded drivers created: " + policy_2excludedDrivers);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_excludedDriver, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AA43AZ);		
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario4(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		//Scenario 4: Add Driver with Financial Responsibility = Yes
		TestData td_financialDriver = getPolicyTD().adjust(getTestSpecificTD("TestData_FinancialDriver").resolveLinks());
		String policy_financialDriver = createPolicy(td_financialDriver);
		log.info("PAS DOC: Scenario 4: Policy with financial driver created: " + policy_financialDriver);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_financialDriver, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AASR22); //AASR22
		
		//Scenario 4b: 2 Drivers with Financial Responsibility = Yes
		TestData td_2financialDrivers = getPolicyTD().adjust(getTestSpecificTD("TestData_2FinancialDrivers").resolveLinks());
		//String policy_2financialDrivers = createPolicy(td_2financialDrivers);
		policy.initiate();
		policy.getDefaultView().fillUpTo(td_2financialDrivers, DocumentsAndBindTab.class, true);
		DocumentsAndBindTab docsAndBindTab = new DocumentsAndBindTab();
		docsAndBindTab.submitTab();
		if (docsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.CASE_NUMBER).isPresent()) {
			docsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.CASE_NUMBER).setValue("12346");
			docsAndBindTab.submitTab();
		}
		new PurchaseTab().fillTab(td_2financialDrivers);
		new PurchaseTab().submitTab();			
		String policy_2financialDrivers = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 4b: Policy with 2 financial drivers created: " + policy_2financialDrivers);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_2financialDrivers, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AASR22); //AASR22
	}

	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario5(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		//Scenario 5: Vehicle with Existing Damage
		TestData td_vehicleWithDamage = getPolicyTD().adjust(getTestSpecificTD("TestData_VehicleWithDamage").resolveLinks());
		String policy_vehicleWithDamage = createPolicy(td_vehicleWithDamage);
		log.info("PAS DOC: Scenario 5: Policy with vehicle with damage created: " + policy_vehicleWithDamage);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_vehicleWithDamage, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AA59XX); 
		
		//Scenario 5b: 2 Vehicles with existing damage
		TestData td_2vehiclesWithDamage = getPolicyTD().adjust(getTestSpecificTD("TestData_2VehiclesWithDamage").resolveLinks()); 
		String policy_2vehiclesWithDamage = createPolicy(td_2vehiclesWithDamage);
		log.info("PAS DOC: Scenario 5b: Policy with 2 vehicles with damage created: " + policy_2vehiclesWithDamage);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_2vehiclesWithDamage, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AA59XX); 
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario6(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		//Scenario 6: 
		TestData td_golfCart = getPolicyTD().adjust(getTestSpecificTD("TestData_GolfCart").resolveLinks());
		String policy_golfCart = createPolicy(td_golfCart);
		log.info("PAS DOC: Scenario 6: Policy with Golf Cart created: " + policy_golfCart);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_golfCart, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AAGCAZ);
		
		//Scenario 6b: 
		TestData td_2golfCarts = getPolicyTD().adjust(getTestSpecificTD("TestData_2GolfCarts").resolveLinks());
		String policy_2golfCarts = createPolicy(td_2golfCarts);
		log.info("PAS DOC: Scenario 6b: Policy with 2 Golf Carts created: " + policy_2golfCarts);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_2golfCarts, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AAGCAZ);
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario7(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		//Scenario 7: Uninsured Motorists Bodily Injury limits (UM) < BI Liability Limits
		TestData td_UMLessThanBI = getPolicyTD().adjust(getTestSpecificTD("TestData_UMLessThanBI").resolveLinks());
		String policy_UMLessThanBI = createPolicy(td_UMLessThanBI);
		log.info("PAS DOC: Scenario 7: Policy with Uninsured Motorist BI limits (UM) < BI limits created: " + policy_UMLessThanBI);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_UMLessThanBI, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AA52AZ); 
		
		//Scenario 7b: Underinsured Motorists Bodily Injury limits (UIM) < BI Liability Limits
		TestData td_UIMLessThanBI = getPolicyTD().adjust(getTestSpecificTD("TestData_UIMLessThanBI").resolveLinks());
		String policy_UIMLessThanBI = createPolicy(td_UIMLessThanBI);
		log.info("PAS DOC: Scenario 7: Policy with Underinsured Motorist BI limits (UIM) < BI limits created: " + policy_UIMLessThanBI);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_UIMLessThanBI, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AA52AZ); 
		
		//Scenario 7c: both UM and UIM limits < BI Liability limits
		TestData td_UMandUIMLessThanBI = getPolicyTD().adjust(getTestSpecificTD("TestData_UMandUIMLessThanBI").resolveLinks());
		String policy_UMandUIMLessThanBI = createPolicy(td_UMandUIMLessThanBI);
		log.info("PAS DOC: Scenario 7: Policy with both UM and UIM limits < BI limits created: " + policy_UMandUIMLessThanBI);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_UMandUIMLessThanBI, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AA52AZ); 
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario8(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		//Scenario 8: Set 'Not signed' for documents in 'Required to Bind'
		TestData td_docNotSigned = getPolicyTD().adjust(getTestSpecificTD("TestData_DocNotSigned").resolveLinks());
		policy.initiate();
		policy.getDefaultView().fillUpTo(td_docNotSigned, ErrorTab.class, true);
		new ErrorTab().submitTab();
		new PurchaseTab().fillTab(td_docNotSigned);
		new PurchaseTab().submitTab();
		String policy_docNotSigned = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 8: Policy with documents 'Not Signed' created: " + policy_docNotSigned);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_docNotSigned, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AARFIXX);
		
		//Scenario 8b: Two 'Not Signed' documents in 'Required to Bind'
		TestData td_2docsNotSigned = getPolicyTD().adjust(getTestSpecificTD("TestData_2DocsNotSigned").resolveLinks());
		policy.initiate();
		policy.getDefaultView().fillUpTo(td_2docsNotSigned, ErrorTab.class, true);
		new ErrorTab().submitTab();
		new PurchaseTab().fillTab(td_2docsNotSigned);
		new PurchaseTab().submitTab();
		String policy_2docsNotSigned = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 8: Policy with documents 'Not Signed' created: " + policy_2docsNotSigned);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_2docsNotSigned, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AARFIXX);
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario9(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		//Scenario 9: Select payment plan = Monthly and activate AutoPay
		TestData td_enabledAutoPay = getPolicyTD().adjust(getTestSpecificTD("TestData_EnabledAutoPay").resolveLinks());
		String policy_enabledAutoPay = createPolicy(td_enabledAutoPay);
		log.info("PAS DOC: Scenario 9: Policy with enabled AutoPay created: " + policy_enabledAutoPay);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_enabledAutoPay, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AH35XX);
	}

	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario10(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		//Scenario 10: Policy Type is Non-Owner
		TestData td_nano = getPolicyTD().adjust(getTestSpecificTD("TestData_Nano").resolveLinks());
		String policyNumber = createPolicy(td_nano);
		log.info("PAS DOC: Scenario 10: Non-Owner policy created" + policyNumber);
		DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AA41XX); 
	}

	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testPolicyCreationFull(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_full = getPolicyTD().adjust(getTestSpecificTD("TestData_Full").resolveLinks());
		createPolicy(td_full);
		log.info("TEST: Standard Full: Created policy# " + PolicySummaryPage.getPolicyNumber());
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testQuoteCreation(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_quote = getPolicyTD().adjust(getTestSpecificTD("TestData_Quote").resolveLinks());
		policy.initiate();
		policy.getDefaultView().fillUpTo(td_quote, DocumentsAndBindTab.class, true);
		new DocumentsAndBindTab().saveAndExit();
		log.info("TEST: Standard Quote: Created quote# " + PolicySummaryPage.getPolicyNumber());
	}
	
}