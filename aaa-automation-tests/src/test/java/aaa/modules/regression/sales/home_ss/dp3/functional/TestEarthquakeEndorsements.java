package aaa.modules.regression.sales.home_ss.dp3.functional;

import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.EndorsementForms;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.regression.sales.template.functional.TestEndorsementsTabAbstract;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.OK)
public class TestEarthquakeEndorsements extends TestEndorsementsTabAbstract {

	@Override
	protected PolicyType getPolicyType() { return PolicyType.HOME_SS_DP3; }

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for privileged user
	 * @scenario
	 * 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Initiate Quote creation
	 * 4. FIll everything up until 'Premium & Coverages' tab -> 'Endorsement' subtab
	 * 5. Check that 'Earthquake' endorsement is available in 'Optional Endorsements'
	 * 6. Add 'Earthquake' endorsement
	 * 7. Check that Edit link is available
	 * 8. Change something in endorsement and verify that it is saved
	 * 9. Check that Remove link is available
	 * 10. Remove endorsement and check that it is removed
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement check for privileged user")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17479")
	public void testEarthquakeEndorsement_Privileged_NewBusiness(@Optional("OK") String state) {
		initiateNewBusinessTx(false);
		testEndorsementForms(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId());
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for privileged user
	 * @scenario
	 * 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Create Policy
	 * 4. Initiate Endorsement
	 * 5. Navigate to 'Premium & Coverages' tab -> 'Endorsement' subtab
	 * 6. Check that 'Earthquake' endorsement is available in 'Optional Endorsements'
	 * 7. Add 'Earthquake' endorsement
	 * 8. Check that Edit link is available
	 * 9. Change something in endorsement and verify that it is saved
	 * 10. Check that Remove link is available
	 * 11. Remove endorsement and check that it is removed
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement check for privileged user")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17479")
	public void testEarthquakeEndorsement_Privileged_Endorsement(@Optional("OK") String state) {
		createPolicy(false);
		initiateEndorsementTx();
		testEndorsementForms(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId());
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for privileged user
	 * @scenario
	 * 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Create Policy
	 * 4. Initiate Renewal
	 * 5. Navigate to 'Premium & Coverages' tab -> 'Endorsement' subtab
	 * 6. Check that 'Earthquake' endorsement is available in 'Optional Endorsements'
	 * 7. Add 'Earthquake' endorsement
	 * 8. Check that Edit link is available
	 * 9. Change something in endorsement and verify that it is saved
	 * 10. Check that Remove link is available
	 * 11. Remove endorsement and check that it is removed
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement check for privileged user")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17479")
	public void testEarthquakeEndorsement_Privileged_Renewal(@Optional("OK") String state) {
		createPolicy(false);
		initiateRenewalTx();
		testEndorsementForms(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId());

	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for NON privileged user
	 * @scenario
	 * 1. Login app with user who DOES NOT HAVE 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Initiate Quote creation
	 * 4. FIll everything up until 'Premium & Coverages' tab -> 'Endorsement' subtab
	 * 5. Check that 'Earthquake' endorsement is NOT available in 'Optional Endorsements'
	 * 6. Check that 'Earthquake' endorsement is NOT available in 'Included Endorsements'
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement check for non privileged user")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17484")
	public void testEarthquakeEndorsement_NonPrivileged_NewBusinessTx(@Optional("OK") String state) {
		initiateNewBusinessTx_NonPrivileged("F35",false);
		checkEndorsementIsNotAvailableInOptionalEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName());
		checkEndorsementIsNotAvailableInIncludedEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName());
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for NON privileged user
	 * @scenario
	 * 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Initiate Quote creation
	 * 4. FIll everything up until 'Premium & Coverages' tab -> 'Endorsement' subtab
	 * 5. Add 'Earthquake' endorsement
	 * 6. Save & Exit
	 * 7. Login app with user who DOES NOT HAVE 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 8. Navigate to 'Premium & Coverages' tab -> 'Endorsement' of created policy
	 * 9. Check that 'Earthquake' endorsement is available in 'Included Endorsements'
	 * 10. Check that Edit link is available
	 * 11. Change something in endorsement and verify that it is saved
	 * 12. Check that Remove link is available
	 * 13. Remove endorsement and check that it is removed
	 * 14. Check that 'Earthquake' endorsement is NOT available in 'Optional Endorsements'
	 * 15. Bind Endorsement
	 * 16. Initiate 2nd Endorsement
	 * 17. Navigate to 'Premium & Coverages' tab -> 'Endorsement' of created policy
	 * 18. Check that 'Earthquake' endorsement is NOT available in 'Optional Endorsements'
	 * 19. Check that 'Earthquake' endorsement is NOT available in 'Included Endorsements'
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement check for non privileged user")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17484")
	public void testEarthquakeEndorsement_NonPrivileged_NewBusinessTx_AlreadyHadEndorsement(@Optional("OK") String state) {
		initiateNewBusinessTx_NonPrivileged_AlreadyHadEndorsement("F35",
				EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId());
		testEndorsementForms_NonPrivileged_AlreadyHadEndorsement(false, EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId());
		finishNewBusinessTx();

		//2nd Endorsement -> check that 'X' endorsement doesn't exist in Endorsement tab
		initiateEndorsementTx();
		checkEndorsementIsNotAvailableInIncludedEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName());
		checkEndorsementIsNotAvailableInOptionalEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName());
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for NON privileged user
	 * @scenario
	 * 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Create Policy
	 * 4. Login app with user who DOES NOT HAVE 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 5. Initiate Endorsement Transaction
	 * 6. Navigate to 'Premium & Coverages' tab -> 'Endorsement' of created policy
	 * 7. Check that 'Earthquake' endorsement is NOT available in 'Optional Endorsements'
	 * 8. Check that 'Earthquake' endorsement is NOT available in 'Included Endorsements'
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement check for non privileged user")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17484")
	public void testEarthquakeEndorsement_NonPrivileged_EndorsementTx(@Optional("OK") String state) {
		String policyNumber = createPolicy(false);
		openAppNonPrivilegedUser("F35");
		SearchPage.openPolicy(policyNumber);
		initiateEndorsementTx();
		checkEndorsementIsNotAvailableInOptionalEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName());
		checkEndorsementIsNotAvailableInIncludedEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName());
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for NON privileged user
	 * @scenario
	 * 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Create Policy with 'Earthquake' endorsement added
	 * 4. Login app with user who DOES NOT HAVE 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 5. Initiate Endorsement Transactions
	 * 6. Navigate to 'Premium & Coverages' tab -> 'Endorsement' of created policy
	 * 7. Check that 'Earthquake' endorsement is available in 'Included Endorsements'
	 * 8. Check that Edit link is available
	 * 9. Change something in endorsement and verify that it is saved
	 * 10. Check that Remove link is available
	 * 11. Remove endorsement and check that it is removed
	 * 12. Check that 'Earthquake' endorsement is NOT available in 'Optional Endorsements'
	 * 13. Bind Endorsement
	 * 14. Initiate 2nd Endorsement
	 * 15. Navigate to 'Premium & Coverages' tab -> 'Endorsement' of created policy
	 * 16. Check that 'Earthquake' endorsement is NOT available in 'Optional Endorsements'
	 * 17. Check that 'Earthquake' endorsement is NOT available in 'Included Endorsements'
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement check for non privileged user")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17484")
	public void testEarthquakeEndorsement_NonPrivileged_EndorsementTx_AlreadyHadEndorsement(@Optional("OK") String state) {
		String policyNumber = createPolicyWithEndorsement(false, EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId());
		openAppNonPrivilegedUser("F35");
		SearchPage.openPolicy(policyNumber);
		initiateEndorsementTx();
		testEndorsementForms_NonPrivileged_AlreadyHadEndorsement(false, EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId());
		finishRenewalOrEndorsementTx();

		//2nd Endorsement -> check that 'X' endorsement doesn't exist in Endorsement tab
		initiateEndorsementTx();
		checkEndorsementIsNotAvailableInIncludedEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName());
		checkEndorsementIsNotAvailableInOptionalEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName());
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for NON privileged user
	 * @scenario
	 * 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Create Policy
	 * 4. Login app with user who DOES NOT HAVE 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 5. Initiate Renewal Transactions
	 * 6. Navigate to 'Premium & Coverages' tab -> 'Endorsement' of created policy
	 * 7. Check that 'Earthquake' endorsement is NOT available in 'Optional Endorsements'
	 * 8. Check that 'Earthquake' endorsement is NOT available in 'Included Endorsements'
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement check for non privileged user")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17484")
	public void testEarthquakeEndorsement_NonPrivileged_RenewalTx(@Optional("OK") String state) {
		String policyNumber = createPolicy(false);
		initiateRenewalTx();
		new PremiumsAndCoveragesQuoteTab().saveAndExit();

		openAppNonPrivilegedUser("A30");
		SearchPage.openPolicy(policyNumber);
		navigateToRenewalPremiumAndCoveragesTab();
		checkEndorsementIsNotAvailableInOptionalEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName());
		checkEndorsementIsNotAvailableInIncludedEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName());
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for NON privileged user
	 * @scenario
	 * 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Create Policy with 'Earthquake' endorsement added
	 * 4. Login app with user who DOES NOT HAVE 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 5. Initiate Renewal Transactions
	 * 6. Navigate to 'Premium & Coverages' tab -> 'Endorsement' of created policy
	 * 7. Check that 'Earthquake' endorsement is available in 'Included Endorsements'
	 * 8. Check that Edit link is available
	 * 9. Change something in endorsement and verify that it is saved
	 * 10. Check that Remove link is available
	 * 11. Remove endorsement and check that it is removed
	 * 12. Check that 'Earthquake' endorsement is NOT available in 'Optional Endorsements'
	 * 13. Propose Renewal
	 * 14. Initiate 2nd Renewal
	 * 15. Navigate to 'Premium & Coverages' tab -> 'Endorsement' of created policy
	 * 16. Check that 'Earthquake' endorsement is NOT available in 'Optional Endorsements'
	 * 17. Check that 'Earthquake' endorsement is NOT available in 'Included Endorsements'
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement check for non privileged user")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17484")
	public void testEarthquakeEndorsement_NonPrivileged_RenewalTx_AlreadyHadEndorsement(@Optional("OK") String state) {
		String policyNumber = createPolicyWithEndorsement(false, EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId());
		initiateRenewalTx();
		new PremiumsAndCoveragesQuoteTab().saveAndExit();

		openAppNonPrivilegedUser("F35");
		SearchPage.openPolicy(policyNumber);
		navigateToRenewalPremiumAndCoveragesTab();
		testEndorsementForms_NonPrivileged_AlreadyHadEndorsement(true, EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId());
		finishRenewalOrEndorsementTx();

		//2nd Renewal tx -> check that 'X' endorsement doesn't exist in Endorsement tab
		navigateToRenewalPremiumAndCoveragesTab();
		checkEndorsementIsNotAvailableInIncludedEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName());
		checkEndorsementIsNotAvailableInOptionalEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName());
	}
}