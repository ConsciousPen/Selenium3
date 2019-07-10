package aaa.modules.regression.document_fulfillment.auto_ss;

import static aaa.main.enums.DocGenEnum.Documents.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum.AutoSSTab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.docgen.impl.PasDocImpl;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.xml.model.pasdoc.DataElement;
import aaa.helpers.xml.model.pasdoc.Document;
import aaa.helpers.xml.model.pasdoc.DocumentGenerationRequest;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.DocGenEnum.EventName;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class PasDoc_OnlineBatch extends AutoSSBaseTest {

	/**
	 * OnlineBatch Scenario 1 - POLICY_ISSUE: AHAUXX, AHNBXX, AA02AZ + AAAEAZ2, AA10XX
	 * <p>	 <b>(a)</b> 1. Create policy with:
	 * <p>		- Policy Type = Standard, 
	 * <p>		- Driver with chargeable activity, 
	 * <p>		- No Excluded Drivers, 
	 * <p>		- No Drivers with Financial Responsibility = Yes, 
	 * <p>		- Vehicle with Existing Damage = None, 
	 * <p>		- No Vehicle Type = Golf Cart, 
	 * <p>		- Uninsured and Underinsured Coverages = recommended, 
	 * <p>		- No 'Not signed' documents in 'Required to Bind', 
	 * <p>		- No AutoPay. 
	 * <p>		2. Verify the forms are generated: AHAUXX (Driver chargeable activity), AHNBXX, AA02AZ + AAAEAZ2, AA10XX. 
	 * <p>
	 * <p>	 <b>(b)</b> 1. Create policy with ALL types of vehicles.
	 * <p>		2. Verify form AA10XX is generated. 
	 * <p>
	 * <p>	 <b>(c)</b> 1. Create policy with 6 or more Vehicles.
	 * <p>		2. Verify form AA10XX is generated. 
	 *
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario1(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();		
		//Scenario 1a: Driver with chargeable activity
		TestData td_driverWithActivity = getPolicyTD().adjust(getTestSpecificTD("TestData_DriverWithActivity").resolveLinks());
		String policy_driverWithActivity = createPolicy(td_driverWithActivity);
		log.info("PAS DOC: Scenario 1: Policy with driver with chargeable activity created: " + policy_driverWithActivity);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_driverWithActivity, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2);

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

	/**
	 * OnlineBatch Scenario 2: POLICY_ISSUE: AHAUXX
	 * <p> 1. Create Policy with: 
	 * <p>		- Policy Type = Standard, 
	 * <p>		- On General tab: select 'Override Prefilled Current Carrier?' = Yes and select 'Agent Entered BI Limits' >= $500,000 
	 * <p>		- On Rating Detail Report: override Insurance Score. 
	 * <p> 2. Check that AHAUXX is NOT generated. 
	 *
	 * @param state
	 */
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

	/**
	 * OnlineBatch Scenario 3 - POLICY_ISSUE: AA43AZ 
	 * <p>  <b>(a)</b> 1. Create policy with 1 Excluded Driver.
	 * <p>	2. Check forms are generated: AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AA43AZ. 
	 * <p> 	3. Check document AA02AZ contains form  AA43AZ. 
	 * <p> 
	 * <p>  <b>(b)</b> 1. Create policy with 2 Excluded Drivers.
	 * <p> 	2. Check document AA43AZ is generated. 
	 * <p> 	3. Check document AA02AZ contains form AA43AZ. 
	 *
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario3(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		//Scenario 3a: Add Excluded Driver
		TestData td_excludedDriver = getPolicyTD().adjust(getTestSpecificTD("TestData_ExcludedDriver").resolveLinks());
		String policy_excludedDriver = createPolicy(td_excludedDriver);
		log.info("PAS DOC: Scenario 3: Policy with excluded driver created: " + policy_excludedDriver);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_excludedDriver, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AA43AZ);
		verifyAA02AZcontainsForm(policy_excludedDriver, "AA43AZ", true);

		//Scenario 3b: Add 2 Excluded Drivers
		TestData td_2excludedDrivers = getPolicyTD().adjust(getTestSpecificTD("TestData_2ExcludedDrivers").resolveLinks());
		String policy_2excludedDrivers = createPolicy(td_2excludedDrivers);
		log.info("PAS DOC: Scenario 3b: Policy with 2 excluded drivers created: " + policy_2excludedDrivers);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_2excludedDrivers, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AA43AZ);
		verifyAA02AZcontainsForm(policy_2excludedDrivers, "AA43AZ", true);
	}

	/**
	 * OnlineBatch Scenario 4 - POLICY_ISSUE: AASR22 
	 * <p>  <b>(a)</b> 1. Create policy with Driver with Financial Responsibility = Yes.
	 * <p>		2. Verify only these forms are generated: AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AASR22. 
	 * <p>	
	 * <p>  <b>(b)</b> 1. Create policy with 2 Drivers with Financial Responsibility = Yes.
	 * <p>		2. Verify 2 AASR22 forms are generated.
	 *
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario4(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		//Scenario 4a: Add Driver with Financial Responsibility = Yes
		TestData td_financialDriver = getPolicyTD().adjust(getTestSpecificTD("TestData_FinancialDriver").resolveLinks());
		String policy_financialDriver = createPolicy(td_financialDriver);
		log.info("PAS DOC: Scenario 4: Policy with financial driver created: " + policy_financialDriver);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_financialDriver, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_financialDriver, AASR22);
		assertThat(countDocuments(policy_financialDriver, null, AASR22)).isEqualTo(1);

		//Scenario 4b: 2 Drivers with Financial Responsibility = Yes
		TestData td_2financialDrivers = getPolicyTD().adjust(getTestSpecificTD("TestData_2FinancialDrivers").resolveLinks());
		String policy_2financialDrivers = createPolicyWith2FinancialDrivers(td_2financialDrivers);
		log.info("PAS DOC: Scenario 4b: Policy with 2 financial drivers created: " + policy_2financialDrivers);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_2financialDrivers, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_2financialDrivers, AASR22);
		assertThat(countDocuments(policy_2financialDrivers, null, AASR22)).isEqualTo(2);
	}

	/**
	 * OnlineBatch Scenario 5 - POLICY_ISSUE: AA59XX
	 * <p>	 <b>(a)</b> 1. Policy created with 1 Vehicle with Existing Damage (NOT None).
	 * <p>		2. Verify forms are generated: AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AA59XX. 
	 *
	 * <p>	 <b>(b)</b> 1. Policy created with 2 Vehicles with Existing Damage (NOT None).
	 * <p>		2. Verify 2 forms AA59XX are generated.
	 *
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario5(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		//Scenario 5a: Vehicle with Existing Damage
		TestData td_vehicleWithDamage = getPolicyTD().adjust(getTestSpecificTD("TestData_VehicleWithDamage").resolveLinks());
		String policy_vehicleWithDamage = createPolicy(td_vehicleWithDamage);
		log.info("PAS DOC: Scenario 5: Policy with vehicle with damage created: " + policy_vehicleWithDamage);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_vehicleWithDamage, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AA59XX);
		assertThat(countDocuments(policy_vehicleWithDamage, null, AA59XX)).isEqualTo(1);

		//Scenario 5b: 2 Vehicles with existing damage
		TestData td_2vehiclesWithDamage = getPolicyTD().adjust(getTestSpecificTD("TestData_2VehiclesWithDamage").resolveLinks());
		String policy_2vehiclesWithDamage = createPolicy(td_2vehiclesWithDamage);
		log.info("PAS DOC: Scenario 5b: Policy with 2 vehicles with damage created: " + policy_2vehiclesWithDamage);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_2vehiclesWithDamage, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AA59XX);
		assertThat(countDocuments(policy_2vehiclesWithDamage, null, AA59XX)).isEqualTo(2);
	}

	/**
	 * OnlineBatch Scenario 6 - POLICY_ISSUE: AAGCAZ 
	 * <p>	 <b>(a)</b> 1. Create policy with Vehicle Type = Golf Cart.
	 * <p>		2. Verify these forms are generated: AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AAGCAZ. 
	 * <p>
	 * <p>	 <b>(b)</b> 1. Create policy with 2 Vehicles with Type = Golf Cart.
	 * <p>		2. Verify only one AAGCAZ form is generated. 
	 *
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario6(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		//Scenario 6a: add Vehicle Type = Golf Cart
		TestData td_golfCart = getPolicyTD().adjust(getTestSpecificTD("TestData_GolfCart").resolveLinks());
		String policy_golfCart = createPolicy(td_golfCart);
		log.info("PAS DOC: Scenario 6: Policy with Golf Cart created: " + policy_golfCart);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_golfCart, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AAGCAZ);

		//Scenario 6b: add 2 Vehicles with Type = Golf Cart
		TestData td_2golfCarts = getPolicyTD().adjust(getTestSpecificTD("TestData_2GolfCarts").resolveLinks());
		String policy_2golfCarts = createPolicy(td_2golfCarts);
		log.info("PAS DOC: Scenario 6b: Policy with 2 Golf Carts created: " + policy_2golfCarts);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_2golfCarts, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AAGCAZ);
	}

	/**
	 * OnlineBatch Scenario 7 - POLICY_ISSUE: AA52AZ 
	 * <p>	 <b>(a)</b> 1. Create policy with Uninsured Motorists Bodily Injury limits (UM) < BI Liability Limits.
	 * <p>		2. Verify forms are generated: AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AA52AZ. 
	 * <p>		3. Verify AA52AZ form is mentioned in AA02AZ document. 
	 * <p>
	 * <p>	 <b>(b)</b> 1. Create policy with Underinsured Motorists Bodily Injury limits (UIM) < BI Liability Limits.
	 * <p>		2. Verify form is generated: AA52AZ. 
	 * <p>		3. Verify AA52AZ form is mentioned in AA02AZ document. 
	 * <p>
	 * <p>	 <b>(c)</b> 1. Create policy with both UM and UIM limits < BI Liability limits.
	 * <p>		2. Verify form is generated: AA52AZ. 
	 * <p>		3. Verify AA52AZ form is mentioned in AA02AZ document. 
	 *
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario7(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		//Scenario 7a: Uninsured Motorists Bodily Injury limits (UM) < BI Liability Limits
		TestData td_UMLessThanBI = getPolicyTD().adjust(getTestSpecificTD("TestData_UMLessThanBI").resolveLinks());
		String policy_UMLessThanBI = createPolicy(td_UMLessThanBI);
		log.info("PAS DOC: Scenario 7: Policy with Uninsured Motorist BI limits (UM) < BI limits created: " + policy_UMLessThanBI);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_UMLessThanBI, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AA52AZ);
		verifyAA02AZcontainsForm(policy_UMLessThanBI, "AA52AZ", true);

		//Scenario 7b: Underinsured Motorists Bodily Injury limits (UIM) < BI Liability Limits
		TestData td_UIMLessThanBI = getPolicyTD().adjust(getTestSpecificTD("TestData_UIMLessThanBI").resolveLinks());
		String policy_UIMLessThanBI = createPolicy(td_UIMLessThanBI);
		log.info("PAS DOC: Scenario 7: Policy with Underinsured Motorist BI limits (UIM) < BI limits created: " + policy_UIMLessThanBI);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_UIMLessThanBI, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AA52AZ);
		verifyAA02AZcontainsForm(policy_UIMLessThanBI, "AA52AZ", true);

		//Scenario 7c: both UM and UIM limits < BI Liability limits
		TestData td_UMandUIMLessThanBI = getPolicyTD().adjust(getTestSpecificTD("TestData_UMandUIMLessThanBI").resolveLinks());
		String policy_UMandUIMLessThanBI = createPolicy(td_UMandUIMLessThanBI);
		log.info("PAS DOC: Scenario 7: Policy with both UM and UIM limits < BI limits created: " + policy_UMandUIMLessThanBI);
		DocGenHelper.verifyDocumentsGenerated(true, false, policy_UMandUIMLessThanBI, AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AA52AZ);
		verifyAA02AZcontainsForm(policy_UMandUIMLessThanBI, "AA52AZ", true);
	}

	/**
	 * OnlineBatch Scenario 8 - POLICY_ISSUE: AARFIXX
	 * <p>	 <b>(a)</b> 1. Create policy with 1 document 'Not signed' in 'Required to Bind'.
	 * <p>		2. Verify forms are generated: AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AARFIXX. 
	 * <p>
	 * <p>	 <b>(b)</b> 1. Create policy with 2 documents 'Not signed' in 'Required to Bind'.
	 * <p>		2. Verify forms are generated: AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AARFIXX. 
	 *
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario8(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		//Scenario 8a: Set 'Not signed' for documents in 'Required to Bind'
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

	/**
	 * OnlineBatch Scenario 9 - POLICY_ISSUE: AH35XX
	 * <p>	1. Create policy with selected payment plan = Monthly and activate AutoPay. 
	 * <p>	2. Verify forms are generated: AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AH35XX. 
	 *
	 * @param state
	 */
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

	/**
	 * OnlineBatch Scenario 10 - POLICY_ISSUE: AA41XX
	 * <p>	1. Create policy with Policy Type = Non-Owner. 
	 * <p>	2. Verify forms are generated: AHAUXX, AHNBXX, AA02AZ, AA10XX, AAAEAZ2, AA41XX. 
	 * <p>  3. Verify AA41XX form is mentioned in AA02AZ document. 
	 *
	 * @param state
	 */
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
		verifyAA02AZcontainsForm(policyNumber, "AA41XX", true);
	}

	/**
	 * OnlineBatch Scenario 11 - ENDORSEMENT_ISSUE: AA02AZ, AA10XX 
	 * <p>	 <b>(a)</b> 1. Create standard policy.
	 * <p>		2. Start Endorsement action and add Vehicle with Type NOT Trailer.  
	 * <p>		3. Verify the following forms are generated: AA02AZ (without AAAEAZ2), AA10XX. 
	 * <p>
	 * <p>	 <b>(b)</b> 1. Create standard policy.
	 * <p>		2. Start Endorsement action and add Vehicle with Type = Trailer. 
	 * <p>		3. Verify AA10XX is NOT generated.
	 *
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario11(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		
		//Scenario 11a: Purchase Endorsement: add a Vehicle (Type not Trailer)
		createPolicy();
		TestData td_addVehicle = getTestSpecificTD("TestData_Endorsement_AddVehicle").adjust(getPolicyTD("Endorsement", "TestData"));
		policy.endorse().performAndFill(td_addVehicle);
		String policy_addVehicle = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 11: Endorsement with added Vehicle (Type not Trailer) created: " + policy_addVehicle);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_addVehicle, EventName.ENDORSEMENT_ISSUE, AA02AZ, AA10XX);

		//Scenario 11b: Purchase Endorsement: add a Vehicle with Type = Trailer
		createPolicy();
		TestData td_addTrailer = getTestSpecificTD("TestData_Endorsement_AddTrailer").adjust(getPolicyTD("Endorsement", "TestData"));
		policy.endorse().performAndFill(td_addTrailer);
		String policy_addTrailer = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 11b: Endorsement with added Trailer created: " + policy_addTrailer);
		PasDocImpl.verifyDocumentsGenerated(null, false, false, policy_addTrailer, EventName.ENDORSEMENT_ISSUE, AA10XX);
	}

	/**
	 * OnlineBatch Scenario 12 - ENDORSEMENT_ISSUE: AA52AZ
	 * <p>	 <b>(a)</b> 1. Create policy with: both UM and UIM limits = BI Liability limits.
	 * <p>		2. Purchase endorsement: set UM and UIM limits < BI limits. 
	 * <p>		3. Verify the following forms are generated: AA02AZ  (without AAAEAZ2), AA52AZ. AA52AZ form is mentioned in AA02AZ document. 
	 * <p>
	 * <p>	 <b>(b)</b> 1. Create policy with: both UM and UIM limits = BI Liability limits.
	 * <p>		2. Purchase endorsement: set UIM limits < BI limits. 
	 * <p>		3. Verify form AA52AZ is generated and AA52AZ form is mentioned in AA02AZ document. 
	 * <p>
	 * <p>	 <b>(c)</b> 1. Create policy with: both UM and UIM limits = BI Liability limits.
	 * <p>		2. Purchase endorsement: set UM limits < BI limits. 
	 * <p>		3. Verify form AA52AZ is generated and AA52AZ form is mentioned in AA02AZ document. 
	 * <p>
	 * <p>	 <b>(d)</b> - not covered.
	 * <p> 
	 * <p>	 <b>(e)</b>	1. Create policy with:  both UM and UIM limits < BI limits (AA52AZ form is generated).
	 * <p>		2. Purchase endorsement: set both UM and UIM limits = BI limits. 
	 * <p>		3. Verify form AA52AZ is NOT generated and AA52AZ form is NOT mentioned in AA02AZ document.
	 *
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario12(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_endorsement = getPolicyTD("Endorsement", "TestData");

		//Scenario 12a: Endorsement: set UM and UIM limits < BI limits
		createPolicy();
		TestData td_UMandUIMlessThanBI = getTestSpecificTD("TestData_Endorsement_UMandUIMLessThanBI").adjust(td_endorsement);
		policy.endorse().performAndFill(td_UMandUIMlessThanBI);
		String policy_UMandUIMlessThanBI = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 12a: Endorsement with UM and UIM limits < BI limits created: " + policy_UMandUIMlessThanBI);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_UMandUIMlessThanBI, EventName.ENDORSEMENT_ISSUE, AA02AZ, AA52AZ);
		verifyAA02AZcontainsForm(policy_UMandUIMlessThanBI, EventName.ENDORSEMENT_ISSUE, "AA52AZ", true);

		//Scenario 12b: Endorsement: set UIM limits < BI limits
		createPolicy();
		TestData td_UIMlessThanBI = getTestSpecificTD("TestData_Endorsement_UIMLessThanBI").adjust(td_endorsement);
		policy.endorse().performAndFill(td_UIMlessThanBI);
		String policy_UIMlessThanBI = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 12b: Endorsement with UIM limit < BI limits created: " + policy_UIMlessThanBI);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_UIMlessThanBI, EventName.ENDORSEMENT_ISSUE, AA02AZ, AA52AZ);
		verifyAA02AZcontainsForm(policy_UIMlessThanBI, EventName.ENDORSEMENT_ISSUE, "AA52AZ", true);

		//Scenario 12c: Endorsement: set UM limits < BI limits
		createPolicy();
		TestData td_UMlessThanBI = getTestSpecificTD("TestData_Endorsement_UMLessThanBI").adjust(td_endorsement);
		policy.endorse().performAndFill(td_UMlessThanBI);
		String policy_UMlessThanBI = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 12b: Endorsement with UM limit < BI limits created: " + policy_UMlessThanBI);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_UMlessThanBI, EventName.ENDORSEMENT_ISSUE, AA02AZ, AA52AZ);
		verifyAA02AZcontainsForm(policy_UMlessThanBI, EventName.ENDORSEMENT_ISSUE, "AA52AZ", true);

		//Scenario 12e: Endorsement: set both UM and UIM limits = BI limits
		createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData_UMandUIMLessThanBI").resolveLinks()));
		TestData td_UMandUIMequalToBI = getTestSpecificTD("TestData_Endorsement_UMandUIMEqualToBI").adjust(td_endorsement);
		policy.endorse().performAndFill(td_UMandUIMequalToBI);
		String policy_UMandUIMequalToBI = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 12e: Endorsement with UM and UIM limits = BI limits created: " + policy_UMandUIMequalToBI);
		PasDocImpl.verifyDocumentsGenerated(null, false, false, policy_UMandUIMequalToBI, EventName.ENDORSEMENT_ISSUE, AA52AZ);
		verifyAA02AZcontainsForm(policy_UMandUIMequalToBI, EventName.ENDORSEMENT_ISSUE, "AA52AZ", false);
	}

	/**
	 * OnlineBatch Scenario 13 - ENDORSEMENT_ISSUE: AA43AZ
	 * <p>	 <b>(a)</b> 1. Create policy with: No Excluded Drivers.
	 * <p>		2. Purchase endorsement: add Excluded Driver. 
	 * <p>		3. Verify the following forms are generated: AA02AZ (without AAAEAZ2), AA43AZ. 
	 * <p>		AA43AZ form is mentioned in AA02AZ document. Excluded Driver is mentioned in AA02AZ document. 
	 * <p>
	 * <p>	 <b>(b)</b> 1. Create policy with: 2 Not Excluded Drivers.
	 * <p>		2. Purchase endorsement: change Driver2 Type to Excluded. 
	 * <p>		3. Verify the following forms are generated: AA02AZ (without AAAEAZ2), AA43AZ. 
	 * <p>		AA43AZ form is mentioned in AA02AZ document. Excluded Driver is mentioned in AA02AZ document. 
	 * <p>
	 * <p>	 <b>(c)</b> - not covered.
	 * <p>	 <b>(d)</b> - not covered.
	 * <p>
	 * <p>	 <b>(e)</b> 1. Create policy with: Excluded Driver.
	 * <p>		2. Purchase endorsement: remove Excluded Driver. 
	 * <p>		3. Verify form AA43AZ is NOT generated, AA43AZ is NOT mentioned in AA02AZ document.
	 *
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario13(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		//Scenario 13a: Endorsement: add Excluded Driver
		createPolicy();
		TestData td_addExcludedDriver = getTestSpecificTD("TestData_Endorsement_AddExcludedDriver").adjust(getPolicyTD("Endorsement", "TestData"));
		policy.endorse().performAndFill(td_addExcludedDriver);
		String policy_addExcludedDriver = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 13a: Endorsement: add Excluded Driver: " + policy_addExcludedDriver);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_addExcludedDriver, EventName.ENDORSEMENT_ISSUE, AA02AZ, AA43AZ);
		verifyAA02AZcontainsForm(policy_addExcludedDriver, EventName.ENDORSEMENT_ISSUE, "AA43AZ", true);
		verifyAA02AZcontainsForm(policy_addExcludedDriver, EventName.ENDORSEMENT_ISSUE, "AAAEAZ2", false);

		//Scenario 13b: Endorsement: change Driver2 Type to Excluded
		createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData_2_Drivers").resolveLinks()));
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.DRIVER.get());
		DriverTab.viewDriver(2);
		new DriverTab().fillTab(getTestSpecificTD("TestData_Endorsement_setExcludedDriver2"));
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().fillTab(getTestSpecificTD("TestData_Endorsement_setExcludedDriver2"));
		new DocumentsAndBindTab().submitTab();
		String policy_endosre_setExcludedDriver2 = PolicySummaryPage.getPolicyNumber();
		log.info("Scenario 13b: Endorsement: change Driver2 Type to Excluded: " + policy_endosre_setExcludedDriver2);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_endosre_setExcludedDriver2, EventName.ENDORSEMENT_ISSUE, AA02AZ, AA43AZ);
		verifyAA02AZcontainsForm(policy_endosre_setExcludedDriver2, EventName.ENDORSEMENT_ISSUE, "AA43AZ", true);
		verifyAA02AZcontainsForm(policy_endosre_setExcludedDriver2, EventName.ENDORSEMENT_ISSUE, "AAAEAZ2", false);

		//Scenario 13e: Endorsement: remove Excluded Driver
		createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData_ExcludedDriver").resolveLinks()));
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.DRIVER.get());
		new DriverTab().removeDriver(2);
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		String policy_removeExcludedDriver = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 13e: Endorsement: remove Excluded Driver: " + policy_removeExcludedDriver);
		PasDocImpl.verifyDocumentsGenerated(null, false, false, policy_removeExcludedDriver, EventName.ENDORSEMENT_ISSUE, AA43AZ);
		verifyAA02AZcontainsForm(policy_removeExcludedDriver, EventName.ENDORSEMENT_ISSUE, "AA43AZ", false);
	}

	/**
	 * OnlineBatch Scenario 14 - ENDORSEMENT_ISSUE: AA41XX
	 * <p>	1. Create policy with Policy Type = Standard. 
	 * <p>	2. Purchase endorsement with: set Policy Type = Non-Owner. 
	 * <p>	3. Verify the following forms are generated: AA02AZ (without AAAEAZ2), AA10XX, AA41XX. Form AA41XX is mentioned in AA02AZ document.
	 *
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario14(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		//Scenario 14: Endorsement: set Policy Type = Non-Owner
		TestData td_endorse_nano = getTestSpecificTD("TestData_Endorsement_Nano").adjust(getPolicyTD("Endorsement", "TestData"));
		policy.endorse().performAndFill(td_endorse_nano);
		String policy_endorse_nano = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 14: Endorsement: set Policy Type = Non-Owner: " + policy_endorse_nano);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_endorse_nano, EventName.ENDORSEMENT_ISSUE, AA02AZ, AA10XX, AA41XX);
		verifyAA02AZcontainsForm(policy_endorse_nano, EventName.ENDORSEMENT_ISSUE, "AA41XX", true);
	}

	/**
	 * OnlineBatch Scenario 15 - ENDORSEMENT_ISSUE: AA59XX
	 * <p>	 <b>(a)</b> 1. Create policy with: Vehicle with Existing Damage = None.
	 * <p>		2. Purchase endorsement with: set Existing Damage not None. 
	 * <p>		3. Verify the following forms are generated: AA02AZ (without AAAEAZ2), AA59XX. 
	 * <p>
	 * <p>	 <b>(b)</b> 1. Create policy with: Vehicle with Existing Damage = None.
	 * <p>		2. Purchase endorsement: add Vehicle with Existing Damage not None. 
	 * <p>		3. Verify the following forms are generated: AA02AZ (without AAAEAZ2), AA10XX, AA59XX. 
	 * <p>
	 * <p>	 <b>(c)</b> 1. Create policy with: Vehicle1 and Vehicle2 with Existing Damage = None.
	 * <p>		2. Purchase endorsement: for both Vehicle set Existing Damage not None. 
	 * <p>		3. Verify the following forms are generated: AA02AZ (without AAAEAZ2), AA59XX for Vehicle1, AA59XX for Vehicle2. 
	 * <p>
	 * <p>	 <b>(d)</b> - not covered.
	 * <p>
	 * <p>	 <b>(e)</b> 1. Create policy: Vehicle with Existing Damage not None.
	 * <p>		2. Purchase endorsement with: set Existing Damage = None. 
	 * <p>		3. Verify form AA59XX is NOT generated and AA59XX form is NOT mentioned in AA02AZ document. 
	 *
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario15(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		//Scenario 15a: Endorsement: set Existing Damage not None
		createPolicy();
		TestData td_endorse_setDamage = getTestSpecificTD("TestData_Endorsement_setDamage").adjust(getPolicyTD("Endorsement", "TestData"));
		policy.endorse().performAndFill(td_endorse_setDamage);
		String policy_endorse_setDamage = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 15a: Endorsement: set Existing Damage not None: " + policy_endorse_setDamage);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_endorse_setDamage, EventName.ENDORSEMENT_ISSUE, AA02AZ, AA59XX);
		verifyAA02AZcontainsForm(policy_endorse_setDamage, EventName.ENDORSEMENT_ISSUE, "AAAEAZ2", false);

		//Scenario 15b: Endorsement: add Vehicle with Existing Damage not None
		createPolicy();
		TestData td_endorse_addDamageVehicle = getTestSpecificTD("TestData_Endorsement_addDamageVehicle").adjust(getPolicyTD("Endorsement", "TestData"));
		policy.endorse().performAndFill(td_endorse_addDamageVehicle);
		String policy_endorse_addDamageVehicle = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 15b: Endorsement: add Vehicle with Existing Damage not None: " + policy_endorse_addDamageVehicle);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_endorse_addDamageVehicle, EventName.ENDORSEMENT_ISSUE, AA02AZ, AA10XX, AA59XX);
		verifyAA02AZcontainsForm(policy_endorse_addDamageVehicle, EventName.ENDORSEMENT_ISSUE, "AAAEAZ2", false);

		//Scenario 15c: Endorsement: for both Vehicle set Existing Damage not None
		createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData_2_Vehicles").resolveLinks()));
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.VEHICLE.get());
		new VehicleTab().getAssetList().getAsset(AutoSSMetaData.VehicleTab.EXISTING_DAMAGE).setValue("Other Body Damages");
		VehicleTab.tableVehicleList.selectRow(2);
		new VehicleTab().getAssetList().getAsset(AutoSSMetaData.VehicleTab.EXISTING_DAMAGE).setValue("Other Body Damages");
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		String policy_endorse_set2VehiclesDamage = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 15c: Endorsement: for both Vehicle set Existing Damage not None: " + policy_endorse_set2VehiclesDamage);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_endorse_set2VehiclesDamage, EventName.ENDORSEMENT_ISSUE, AA02AZ, AA59XX);
		assertThat(countDocuments(policy_endorse_set2VehiclesDamage, EventName.ENDORSEMENT_ISSUE, AA59XX)).isEqualTo(2);

		//Scenario 15e: Endorsement: set Existing Damage = None
		createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData_RegularWithDamage").resolveLinks()));
		TestData td_endorse_removeDamage = getTestSpecificTD("TestData_Endorsement_removeDamage").adjust(getPolicyTD("Endorsement", "TestData"));
		policy.endorse().performAndFill(td_endorse_removeDamage);
		String policy_endorse_removeDamage = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 15e: Endorsement: set Existing Damage = None: " + policy_endorse_removeDamage);
		PasDocImpl.verifyDocumentsGenerated(null, false, false, policy_endorse_removeDamage, EventName.ENDORSEMENT_ISSUE, AA59XX);
		verifyAA02AZcontainsForm(policy_endorse_removeDamage, EventName.ENDORSEMENT_ISSUE, "AA59XX", false);
	}

	/**
	 * OnlineBatch Scenario 16 - ENDORSEMENT_ISSUE: AAGCAZ
	 * <p>	 <b>(a)</b> 1. Create policy with: No Vehicle with Type = Golf Cart.
	 * <p>		2. Purchase endorsement: add Vehicle with Type = Golf Cart. 
	 * <p>		3. The following forms are generated: AA02AZ (without AAAEAZ2), AA10XX, AAGCAZ. Form AAGCAZ is mentioned in AA02AZ document.
	 * <p>
	 * <p>	 <b>(b)</b> - not covered.
	 * <p>	 <b>(c)</b> - not covered.
	 * <p>
	 * <p>	 <b>(d)</b> 1. Create policy with Vehicle1 with Type = Golf Cart.
	 * <p>		2. Purchase endorsement: remove Golf Cart. 
	 * <p>		3. Verify AAGCAZ form is NOT generated and AAGCAZ form is NOT mentioned in AA02AZ document. 
	 *
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario16(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		//Scenario 16a: Endorsement: add Vehicle with Type = Golf Cart
		TestData td_endorse_addGolfCart = getTestSpecificTD("TestData_Endorsement_addGolfCart").adjust(getPolicyTD("Endorsement", "TestData"));
		policy.endorse().performAndFill(td_endorse_addGolfCart);
		String policy_endorse_addGolfCart = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 16a: Endorsement: add Vehicle with Type = Golf Cart: " + policy_endorse_addGolfCart);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_endorse_addGolfCart, EventName.ENDORSEMENT_ISSUE, AA02AZ, AA10XX, AAGCAZ);
		verifyAA02AZcontainsForm(policy_endorse_addGolfCart, EventName.ENDORSEMENT_ISSUE, "AAGCAZ", true);

		//Scenario 16d: Endorsement: remove Golf Cart
		createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData_GolfCart").resolveLinks()));
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.removeRow(2);
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		String policy_endorse_removeGolfCart = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 16a: Endorsement: add Vehicle with Type = Golf Cart: " + policy_endorse_removeGolfCart);
		PasDocImpl.verifyDocumentsGenerated(null, false, false, policy_endorse_removeGolfCart, EventName.ENDORSEMENT_ISSUE, AAGCAZ);
		verifyAA02AZcontainsForm(policy_endorse_removeGolfCart, EventName.ENDORSEMENT_ISSUE, "AAGCAZ", false);
	}

	/**
	 * OnlineBatch Scenario 17 - ENDORSEMENT_ISSUE: AASR22
	 * <p>	 <b>(a)</b> 1. Create policy with: Drivers with Financial Responsibility = No.
	 * <p>		2. Purchase endorsement: change Financial Responsibility to Yes. 
	 * <p>		3. Verify the following forms are generated: AA02AZ (without AAAEAZ2), AASR22. 
	 * <p>	 <b>(b)</b> - not covered.
	 * <p>	 <b>(c)</b> 1. Create policy: 2 Drivers with Financial Responsibility = No.
	 * <p>		2. Purchase endorsement: change Financial Responsibility to Yes for Driver1. 
	 * <p>		3. Verify the following forms are generated: AA02AZ (without AAAEAZ2), AASR22 for Driver1. 
	 * <p>	 <b>(d)</b> - not covered,
	 * <p>	 <b>(e)</b> - not covered.
	 * 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario17(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();

		//Scenario 17a: Endorsement: change Financial Responsibility to Yes
		createPolicy();
		TestData td_endorse_addFinDriver = getTestSpecificTD("TestData_Endorsement_addFinDriver").adjust(getPolicyTD("Endorsement", "TestData"));
		policy.endorse().performAndFill(td_endorse_addFinDriver);
		String policy_endorse_addFinDriver = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 17a: Endorsement: change Financial Responsibility to Yes: " + policy_endorse_addFinDriver);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_endorse_addFinDriver, EventName.ENDORSEMENT_ISSUE, AA02AZ);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_endorse_addFinDriver, EventName.ENDORSEMENT_ISSUE, AASR22);
		verifyAA02AZcontainsForm(policy_endorse_addFinDriver, EventName.ENDORSEMENT_ISSUE, "AAAEAZ2", false);

		//Scenario 17c: Endorsement: change Financial Responsibility to Yes for Driver1
		createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData_2_Drivers").resolveLinks()));
		TestData td_endorse_addFinResponsib = getTestSpecificTD("TestData_Endorsement_addFinResponsibility").adjust(getPolicyTD("Endorsement", "TestData"));
		policy.endorse().performAndFill(td_endorse_addFinResponsib);
		String policy_endorse_addFinResposib = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 17c: Endorsement: change Financial Responsibility to Yes for Driver1: " + policy_endorse_addFinResposib);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_endorse_addFinResposib, EventName.ENDORSEMENT_ISSUE, AA02AZ);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_endorse_addFinResposib, EventName.ENDORSEMENT_ISSUE, AASR22);
		verifyAA02AZcontainsForm(policy_endorse_addFinResposib, EventName.ENDORSEMENT_ISSUE, "AAAEAZ2", false);
		assertThat(countDocuments(policy_endorse_addFinResposib, EventName.ENDORSEMENT_ISSUE, AASR22)).isEqualTo(1);
	}

	/**
	 * OnlineBatch Scenario 18 - ENDORSEMENT_ISSUE: AASR26
	 * <p>	 <b>(a)</b> 1. Create policy: Driver1 with Financial Responsibility = Yes.
	 * <p>		2. Purchase endorsement: change Financial Responsibility to No. 
	 * <p>		3. Verify the following forms are generated: AA02AZ (without AAAEAZ2), AASR26.  
	 * <p>	 <b>(b)</b> - not covered,
	 * <p>	 <b>(c)</b> - not covered.
	 * <p>	 <b>(d)</b> 1. Create policy: Driver1 and Driver2 with Financial Responsibility = Yes.
	 * <p>		2. Purchase endorsement: change Financial Responsibility to No for both Drivers. 
	 * <p>		3. Verify The following forms are generated: AA02AZ (without AAAEAZ2), AASR26 for Driver1, AASR26 for Driver2.  
	 * <p>	 <b>(e)</b> - not covered.
	 * <p>	 <b>(f)</b> 1. Create policy: Driver1 and Driver2 with Financial Responsibility = Yes.
	 * <p>		2. Purchase Endorsement: change Financial Responsibility to No for Driver1, remove Driver2. 
	 * <p>		3. Verify The following forms are generated: AA02AZ (without AAAEAZ2), AASR26 for Driver1, AASR26 for Driver2.
	 *
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario18(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();

		//Scenario 18a: Endorsement: change Financial Responsibility to No
		createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData_FinancialDriver").resolveLinks()));
		TestData td_endorse_removeFinDriver = getTestSpecificTD("TestData_Endorsement_removeFinDriver").adjust(getPolicyTD("Endorsement", "TestData"));
		policy.endorse().performAndFill(td_endorse_removeFinDriver);
		String policy_endorse_removeFinDriver = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 18a: Endorsement: change Financial Responsibility to No: " + policy_endorse_removeFinDriver);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_endorse_removeFinDriver, EventName.ENDORSEMENT_ISSUE, AA02AZ);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_endorse_removeFinDriver, EventName.ENDORSEMENT_ISSUE, AASR26);
		verifyAA02AZcontainsForm(policy_endorse_removeFinDriver, EventName.ENDORSEMENT_ISSUE, "AAAEAZ2", false);

		//Scenario 18d: Endorsement: change Financial Responsibility to No for both Drivers
		TestData td_2financialDrivers = getPolicyTD().adjust(getTestSpecificTD("TestData_2FinancialDrivers").resolveLinks());
		createPolicyWith2FinancialDrivers(td_2financialDrivers);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.DRIVER.get());
		new DriverTab().getAssetList().getAsset(AutoSSMetaData.DriverTab.FINANCIAL_RESPONSIBILITY_FILING_NEEDED).setValue("No");
		DriverTab.viewDriver(2);
		new DriverTab().getAssetList().getAsset(AutoSSMetaData.DriverTab.FINANCIAL_RESPONSIBILITY_FILING_NEEDED).setValue("No");
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		String policy_endorse_change2FinDrivers = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 18d: Endorsement: change Financial Responsibility to No for both Drivers: " + policy_endorse_change2FinDrivers);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_endorse_change2FinDrivers, EventName.ENDORSEMENT_ISSUE, AA02AZ);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_endorse_change2FinDrivers, EventName.ENDORSEMENT_ISSUE, AASR26);
		verifyAA02AZcontainsForm(policy_endorse_change2FinDrivers, EventName.ENDORSEMENT_ISSUE, "AAAEAZ2", false);
		assertThat(countDocuments(policy_endorse_change2FinDrivers, EventName.ENDORSEMENT_ISSUE, AASR26)).isEqualTo(2);

		//Scenario 18f: Endorsement: change Financial Responsibility to No for Driver1 and remove Driver2
		createPolicyWith2FinancialDrivers(td_2financialDrivers);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.DRIVER.get());
		new DriverTab().getAssetList().getAsset(AutoSSMetaData.DriverTab.FINANCIAL_RESPONSIBILITY_FILING_NEEDED).setValue("No");
		DriverTab.tableDriverList.removeRow(2);
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		String policy_endorse_remove2FinDrivers = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 18f: Endorsement: change Financial Responsibility to No for Driver1 and remove Driver2: " + policy_endorse_remove2FinDrivers);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_endorse_remove2FinDrivers, EventName.ENDORSEMENT_ISSUE, AA02AZ);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_endorse_remove2FinDrivers, EventName.ENDORSEMENT_ISSUE, AASR26);
		verifyAA02AZcontainsForm(policy_endorse_remove2FinDrivers, EventName.ENDORSEMENT_ISSUE, "AAAEAZ2", false);
		assertThat(countDocuments(policy_endorse_remove2FinDrivers, EventName.ENDORSEMENT_ISSUE, AASR26)).isEqualTo(2);
	}

	/**
	 * OnlineBatch Scenario 19 - ENDORSEMENT_ISSUE: AAPDXX
	 * <p>	 <b>(a)</b> 1. Create policy with regular Driver1.
	 * <p>		2. Purchase Endorsement: add Driver2 with License Type = Learner's Permit.
	 * <p>		3. Verify The following forms are generated: AA02AZ (without AAAEAZ2), AAPDXX for Driver2.
	 * <p>
	 * <p>	 <b>(b)</b> 1. Create policy: Driver1 with License Type not Learner's Permit.
	 * <p>		2. Purchase Endorsement: add 2 Drivers with License Type = Learner's Permit. 
	 * <p>		3. Verify The following forms are generated: AA02AZ (without AAAEAZ2), AAPDXX for Driver2 and Driver3. 
	 * <p>
	 * <p>	 <b>(c)</b> 1. Create policy with Driver2 with License Type not Learner's Permit.
	 * <p>		2. Purchase Endorsement: change Driver2 License Type to Learner's Permit. 
	 * <p>		3. Verify form AAPDXX is NOT generated. 
	 * <p>	 <b>(d)</b> - not covered.
	 * 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario19(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		//Scenario 19a: Endorsement: add Driver2 with License Type = Learner's Permit
		createPolicy();
		TestData td_endorse_addPermitDriver = getTestSpecificTD("TestData_Endorsement_addPermitDriver").adjust(getPolicyTD("Endorsement", "TestData"));
		policy.endorse().performAndFill(td_endorse_addPermitDriver);
		String policy_endorse_addPermitDriver = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 19a: Endorsement: add Driver2 with License Type = Learner's Permit: " + policy_endorse_addPermitDriver);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_endorse_addPermitDriver, EventName.ENDORSEMENT_ISSUE, AA02AZ, AAPDXX);
		verifyAA02AZcontainsForm(policy_endorse_addPermitDriver, EventName.ENDORSEMENT_ISSUE, "AAAEAZ2", false);

		//Scenario 19b: Endorsement: add Driver2 and Driver3, both with License Type = Learner's Permit
		createPolicy();
		TestData td_endorse_add2PermitDrivers = getTestSpecificTD("TestData_Endorsement_add2PermitDrivers").adjust(getPolicyTD("Endorsement", "TestData"));
		policy.endorse().performAndFill(td_endorse_add2PermitDrivers);
		String policy_endorse_add2PermitDrivers = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 19b: Endorsement: add 2 drivers with License Type = Learner's Permit: " + policy_endorse_add2PermitDrivers);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_endorse_add2PermitDrivers, EventName.ENDORSEMENT_ISSUE, AA02AZ, AAPDXX);
		verifyAA02AZcontainsForm(policy_endorse_add2PermitDrivers, EventName.ENDORSEMENT_ISSUE, "AAAEAZ2", false);
		assertThat(countDocuments(policy_endorse_add2PermitDrivers, EventName.ENDORSEMENT_ISSUE, AAPDXX)).isEqualTo(2);

		//Scenario 19c: Endorsement: change Driver2 License Type to Learner's Permit
		createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData_2_Drivers").resolveLinks()));
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.DRIVER.get());
		DriverTab.viewDriver(2);
		new DriverTab().getAssetList().getAsset(AutoSSMetaData.DriverTab.LICENSE_TYPE).setValue("Learner's Permit");
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
		String policy_endorse_setPermitDriver = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 19c: Endorsement: change Driver2 License Type to Learner's Permit: " + policy_endorse_setPermitDriver);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_endorse_setPermitDriver, EventName.ENDORSEMENT_ISSUE, AAPDXX);
	}

	/**
	 * OnlineBatch Scenario 20 - ENDORSEMENT_ISSUE: AHDRXX 
	 * <p>	 <b>(a)</b> 1. Create policy1 with Membership Discount:
	 * <p>		- set Current AAA Member = Yes, 
	 * <p>		- set Membership Number = 9920702826992070 (so on 'Rating Detail Report' Membership Status = Lapsed). 
	 * <p>		2. Shift time to NB + 15 days and execute 'MembershipValidation' batch jobs. 
	 * <p>		3. Shift time to NB + 30 days and execute 'MembershipValidation' batch jobs. 
	 * <p>		4. Verify Automated endorsement is issued. Membership Discount is removed. Forms are generated: AA02AZ (without AAAEAZ2), AHDRXX.
	 * <p>	 <b>(b)</b> - not covered.
	 * 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH, Groups.TIMEPOINT})
	public void testScenario20(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_membershipLapsed = getPolicyTD().adjust(getTestSpecificTD("TestData_MembershipLapsed").resolveLinks());
		RatingDetailReportsTab reportsTab = new RatingDetailReportsTab();
		PremiumAndCoveragesTab premiumTab = new PremiumAndCoveragesTab();
		ErrorTab errorTab = new ErrorTab();

		policy.initiate();
		policy.getDefaultView().fillUpTo(td_membershipLapsed, RatingDetailReportsTab.class, true);
		String membershipReportStatus = reportsTab.getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1).getCell(
				AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.STATUS.getLabel()).getValue();
		assertThat(membershipReportStatus).as("Invalid membership report status: " + membershipReportStatus).isEqualTo("Lapsed");
		reportsTab.submitTab();
		policy.getDefaultView().fillFromTo(td_membershipLapsed, VehicleTab.class, PremiumAndCoveragesTab.class, true);
		premiumTab.submitTab();
		if (errorTab.isVisible()) {
			errorTab.overrideAllErrors();
			errorTab.buttonOverride.click();
			premiumTab.submitTab();
		}
		policy.getDefaultView().fillFromTo(td_membershipLapsed, DriverActivityReportsTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();

		String policy1 = PolicySummaryPage.getPolicyNumber();
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		log.info("PAS DOC: Scenario 20a: Policy with Membership Status = Lasped is created: " + policy1);

		TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(15));
		JobUtils.executeJob(BatchJob.membershipValidationJob);
		TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(30));
		JobUtils.executeJob(BatchJob.membershipValidationJob);
		mainApp().open();
		SearchPage.openPolicy(policy1);
		PolicySummaryPage.TransactionHistory.open();
		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(PolicyConstants.PolicyTransactionHistoryTable.TYPE).getValue()).isEqualTo("Endorsement");
		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(PolicyConstants.PolicyTransactionHistoryTable.REASON).getValue()).contains("Discount");
		PolicySummaryPage.TransactionHistory.close();

		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy1, EventName.ENDORSEMENT_ISSUE, AA02AZ, AHDRXX);

	}
	
	/*
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
	*/
	
	private String createPolicyWith2FinancialDrivers(TestData td_2finDrivers) {
		policy.initiate();
		policy.getDefaultView().fillUpTo(td_2finDrivers, DocumentsAndBindTab.class, true);
		DocumentsAndBindTab docsAndBindTab = new DocumentsAndBindTab();
		docsAndBindTab.submitTab();
		if (docsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.CASE_NUMBER).isPresent()) {
			docsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.CASE_NUMBER).setValue("12346");
			docsAndBindTab.submitTab();
		}
		new PurchaseTab().fillTab(td_2finDrivers);
		new PurchaseTab().submitTab();			
		return PolicySummaryPage.getPolicyNumber();
	}
	
	private void verifyAA02AZcontainsForm(String policyNumber, String form, boolean expectedPresent) {
		verifyAA02AZcontainsForm(policyNumber, null, form, expectedPresent);
	}

	/**
	 * The method verifies that document AA02AZ contains or not (depends on  <b>expectedPresent</b>) the  <b>form</b>
	 * @param policyNumber
	 * @param eventName
	 * @param form
	 * @param expectedPresent
	 */
	private void verifyAA02AZcontainsForm(String policyNumber, DocGenEnum.EventName eventName, String form, boolean expectedPresent) {
		DocumentGenerationRequest doc = PasDocImpl.getDocumentRequest(policyNumber, eventName, AA02AZ);
		Document AA02AZ = doc.getDocuments().stream().filter(c -> "AA02AZ".equals(c.getTemplateId())).findFirst().get();
		boolean isFormPresent = false;
		for (DataElement dataElement: AA02AZ.getAdditionalData().getDataElement()) {
			log.info("DataElement is " + dataElement.getValue());
			if (dataElement.getValue().contains(form)) {
				isFormPresent = true;
			}
		}

		String err_msg = "Document AA02AZ does not contain form ";
		if (!expectedPresent) {
			err_msg = "Document AA02AZ contains form ";
		}

		assertThat(isFormPresent).as(err_msg + form).isEqualTo(expectedPresent);
	}

	/**
	 * The method counts  <b>form</b> in generated xml for  <b>policyNumber</b> and  <b>eventName</b>
	 * @param policyNumber
	 * @param eventName
	 * @param document
	 * @return
	 */
	private int countDocuments(String policyNumber, DocGenEnum.EventName eventName, DocGenEnum.Documents document) {
		DocumentGenerationRequest docGenReq = PasDocImpl.getDocumentRequest(policyNumber, eventName, document);
		Document doc = docGenReq.getDocuments().stream().filter(c -> document.getIdInXml().equals(c.getTemplateId())).findFirst().get();
		List<String> dataElementList = new ArrayList<>();
		for (DataElement dataElement: doc.getAdditionalData().getDataElement()) {
			dataElementList.add(dataElement.getName());
		}
		log.info("Count of documents: " + dataElementList.size());
		return dataElementList.size();
	}
	
}
