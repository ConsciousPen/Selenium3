package aaa.modules.regression.sales.home_ss.dp3.functional;

import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.EndorsementForms;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.regression.sales.template.functional.TestEndorsementsTabAbstract;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.POLICY_ISSUE;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER;

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
	 * 4. Fill everything up until 'Premium & Coverages' tab -> 'Endorsement' subtab
	 * 5. Check that 'Earthquake' endorsement is available in 'Optional Endorsements' (DS 04 69)
	 * 6. Add 'Earthquake' endorsement
	 * 7. Check that Edit link is available
	 * 8. Change something in endorsement and verify that it is saved
	 * 9. Check that 'Loss Assessment Coverage For Earthquake' endorsement is available in 'Optional Endorsements' (DS 04 68)
	 * 10. Add 'Loss Assessment Coverage For Earthquake' endorsement
	 * 11. Check that Edit link is available
	 * 12. Change something in endorsement and verify that it is saved
	 * 13. Check that Remove link is available
	 * 14. Remove endorsements and check that they are removed
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement check for privileged user")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17479, PAS-17489")
	public void testEarthquakeEndorsement_Privileged_NewBusiness(@Optional("OK") String state) {
		initiateNewBusinessTx(false);

		addEndorsementForm(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId());
		//DS 04 68 should be only available then DS 04 69 is added
		addEndorsementForm(EndorsementForms.HomeSSEndorsementForms.DS_04_68.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_68.getFormId());

		pas17039_checkEndorsementFunctionality();
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for privileged user
	 * @scenario
	 * 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Create Policy
	 * 4. Initiate Endorsement
	 * 5. Fill everything up until 'Premium & Coverages' tab -> 'Endorsement' subtab
	 * 6. Check that 'Earthquake' endorsement is available in 'Optional Endorsements' (DS 04 69)
	 * 7. Add 'Earthquake' endorsement
	 * 8. Check that Edit link is available
	 * 9. Change something in endorsement and verify that it is saved
	 * 10. Check that 'Loss Assessment Coverage For Earthquake' endorsement is available in 'Optional Endorsements' (DS 04 68)
	 * 11. Add 'Loss Assessment Coverage For Earthquake'' endorsement
	 * 12. Check that Edit link is available
	 * 13. Change something in endorsement and verify that it is saved
	 * 14. Check that Remove link is available
	 * 15. Remove endorsements and check that they are removed
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement check for privileged user")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17479, PAS-17489")
	public void testEarthquakeEndorsement_Privileged_Endorsement(@Optional("OK") String state) {
		createPolicy(false);
		initiateEndorsementTx();

		addEndorsementForm(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId());
		//DS 04 68 should be only available then DS 04 69 is added
		addEndorsementForm(EndorsementForms.HomeSSEndorsementForms.DS_04_68.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_68.getFormId());

		pas17039_checkEndorsementFunctionality();
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for privileged user
	 * @scenario
	 * 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Create Policy
	 * 4. Initiate Renewal
	 * 5. Fill everything up until 'Premium & Coverages' tab -> 'Endorsement' subtab
	 * 6. Check that 'Earthquake' endorsement is available in 'Optional Endorsements' (DS 04 69)
	 * 7. Add 'Earthquake' endorsement
	 * 8. Check that Edit link is available
	 * 9. Change something in endorsement and verify that it is saved
	 * 10. Check that 'Loss Assessment Coverage For Earthquake' endorsement is available in 'Optional Endorsements' (DS 04 68)
	 * 11. Add 'Earthquake' endorsement
	 * 12. Check that Edit link is available
	 * 13. Change something in endorsement and verify that it is saved
	 * 14. Check that Remove link is available
	 * 15. Remove endorsements and check that they are removed
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement check for privileged user")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17479, PAS-17489")
	public void testEarthquakeEndorsement_Privileged_Renewal(@Optional("OK") String state) {
		createPolicy(false);
		initiateRenewalTx();

		addEndorsementForm(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId());
		//DS 04 68 should be only available then DS 04 69 is added
		addEndorsementForm(EndorsementForms.HomeSSEndorsementForms.DS_04_68.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_68.getFormId());

		pas17039_checkEndorsementFunctionality();
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for NON privileged user
	 * @scenario
	 * 1. Login app with user who DOES NOT HAVE 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Initiate Quote creation
	 * 4. FIll everything up until 'Premium & Coverages' tab -> 'Endorsement' subtab
	 * 5. Check that 'Earthquake' endorsement is NOT available in 'Optional Endorsements' (DS 04 69)
	 * 6. Check that 'Earthquake' endorsement is NOT available in 'Included Endorsements' (DS 04 69)
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement check for non privileged user")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17484")
	public void testEarthquakeEndorsement_NonPrivileged_NewBusinessTx(@Optional("OK") String state) {
		initiateNewBusinessTx_NonPrivileged("F35");
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
	 * 5. Add 'Earthquake' and 'Loss Assessment Coverage For Earthquake' endorsements
	 * 6. Save & Exit
	 * 7. Login app with user who DOES NOT HAVE 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 8. Navigate to 'Premium & Coverages' tab -> 'Endorsement' of created policy
	 * 9. Check that 'Earthquake' endorsement is available in 'Included Endorsements' (DS 04 69)
	 * 10. Check that 'Loss Assessment Coverage For Earthquake' endorsement is available in 'Included Endorsements' (DS 04 68)
	 * 11. Check that Edit link is available for both endorsements
	 * 12. Change something in endorsements and verify that it is saved
	 * 13. Check that Remove link is available
	 * 14. Remove endorsements and check that it is removed
	 * 15. Check that 'Earthquake' and 'Loss Assessment Coverage For Earthquake' endorsement is NOT available in 'Optional Endorsements'
	 * 16. Bind Endorsement
	 * 17. Initiate 2nd Endorsement
	 * 18. Navigate to 'Premium & Coverages' tab -> 'Endorsement' of created policy
	 * 19. Check that 'Earthquake' and 'Loss Assessment Coverage For Earthquake' endorsement is NOT available in 'Optional Endorsements'
	 * 20. Check that 'Earthquake' and 'Loss Assessment Coverage For Earthquake' endorsement is NOT available in 'Included Endorsements'
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement check for non privileged user")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17484")
	public void testEarthquakeEndorsement_NonPrivileged_NewBusinessTx_AlreadyHadEndorsement(@Optional("OK") String state) {
		initiateNewBusinessTx_NonPrivileged_AlreadyHadEndorsement("F35",
				EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_68.getFormId());

		pas17039_checkEndorsementFunctionality();
		checkEndorsementIsNotAvailableInOptionalEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_68.getName());

		finishNewBusinessTx();

		//2nd Endorsement -> check that 'Earthquake' endorsement doesn't exist in Endorsement tab
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
	 * 3. Create Policy with 'Earthquake' and 'Loss Assessment Coverage For Earthquake' endorsements added
	 * 4. Login app with user who DOES NOT HAVE 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 5. Initiate Endorsement Transactions
	 * 6. Navigate to 'Premium & Coverages' tab -> 'Endorsement' of created policy
	 * 7. Check that 'Earthquake' endorsement is available in 'Included Endorsements' (DS 04 69)
	 * 8. Check that 'Loss Assessment Coverage For Earthquake' endorsement is available in 'Included Endorsements' (DS 04 68)
	 * 9. Check that Edit link is available
	 * 10. Change something in endorsements and verify that it is saved
	 * 11. Check that Remove links is available
	 * 12. Remove endorsements and check that they are removed
	 * 13. Check that 'Earthquake' and Loss Assessment Coverage For Earthquake' endorsement is NOT available in 'Optional Endorsements'
	 * 14. Bind Endorsement
	 * 15. Initiate 2nd Endorsement
	 * 16. Navigate to 'Premium & Coverages' tab -> 'Endorsement' of created policy
	 * 17. Check that 'Earthquake' and 'Loss Assessment Coverage For Earthquake' endorsement is NOT available in 'Optional Endorsements'
	 * 18. Check that 'Earthquake' and 'Loss Assessment Coverage For Earthquake' endorsement is NOT available in 'Included Endorsements'
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement check for non privileged user")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17484, PAS-17498")
	public void testEarthquakeEndorsement_NonPrivileged_EndorsementTx_AlreadyHadEndorsement(@Optional("OK") String state) {
		String policyNumber = createPolicyWithEndorsement(false,
				EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_68.getFormId());

		openAppNonPrivilegedUser("F35");
		SearchPage.openPolicy(policyNumber);
		initiateEndorsementTx();

		pas17039_checkEndorsementFunctionality();
		checkEndorsementIsNotAvailableInOptionalEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_68.getName());
		checkEndorsementIsNotAvailableInOptionalEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName());

		finishRenewalOrEndorsementTx(false);

		//2nd Endorsement -> check that 'Earthquake' endorsement doesn't exist in Endorsement tab
		initiateEndorsementTx();
		checkEndorsementIsNotAvailableInIncludedEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_68.getName());
		checkEndorsementIsNotAvailableInOptionalEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_68.getName());
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
	 * 3. Create Policy with 'Earthquake' and 'Loss Assessment Coverage For Earthquake' endorsement added
	 * 4. Login app with user who DOES NOT HAVE 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 5. Initiate Renewal Transactions
	 * 6. Navigate to 'Premium & Coverages' tab -> 'Endorsement' of created policy
	 * 7. Check that 'Earthquake' endorsement is available in 'Included Endorsements'
	 * 8. Check that Edit link is available
	 * 9. Change something in endorsement and verify that it is saved
	 * 10. Check that Remove link is available
	 * 11. Remove endorsement and check that it is removed
	 * 12. Check that 'Earthquake' and 'Loss Assessment Coverage For Earthquake' endorsement is NOT available in 'Optional Endorsements'
	 * 13. Save & Exit
	 * 14. Initiate new Renewal version
	 * 15. Navigate to 'Premium & Coverages' tab -> 'Endorsement' of created policy
	 * 16. Check that 'Earthquake' and 'Loss Assessment Coverage For Earthquake' endorsement is NOT available in 'Optional Endorsements'
	 * 17. Check that 'Earthquake' and 'Loss Assessment Coverage For Earthquake' endorsement is NOT available in 'Included Endorsements'
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement check for non privileged user")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17484")
	public void testEarthquakeEndorsement_NonPrivileged_RenewalTx_AlreadyHadEndorsement(@Optional("OK") String state) {
		String policyNumber = createPolicyWithEndorsement(false,
				EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_68.getFormId());

		initiateRenewalTx();
		new PremiumsAndCoveragesQuoteTab().saveAndExit();

		openAppNonPrivilegedUser("F35");
		SearchPage.openPolicy(policyNumber);
		navigateToRenewalPremiumAndCoveragesTab();

		pas17039_checkEndorsementFunctionality();
		checkEndorsementIsNotAvailableInOptionalEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_68.getName());

		finishRenewalOrEndorsementTx(false);

		//2nd Renewal tx -> check that 'X' endorsement doesn't exist in Endorsement tab
		navigateToRenewalPremiumAndCoveragesTab();
		checkEndorsementIsNotAvailableInIncludedEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_68.getName());
		checkEndorsementIsNotAvailableInOptionalEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_68.getName());
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for NON privileged user
	 * @scenario
	 * 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Create Policy with 'Earthquake' and 'Loss Assessment Coverage For Earthquake' endorsement added
	 * 4. Check that Document generation triggered after Policy Creation
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement document trigger")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17498")
	public void testEarthquakeEndorsement_checkDocGenTrigger_NewBusiness(@Optional("OK") String state) {
		String policyNumber = createPolicyWithEndorsement(false,
				EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_68.getFormId());

		//Check that Document generation triggered after Policy Creation
		checkDocGenTriggered(policyNumber, POLICY_ISSUE, DocGenEnum.Documents.DS0468.getIdInXml(),
				DocGenEnum.Documents.DS0469.getIdInXml());
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for NON privileged user
	 * @scenario
	 * 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Create Policy
	 * 4. Initiate Endorsement
	 * 5. Add 'Earthquake' and 'Loss Assessment Coverage For Earthquake'
	 * 6. Bind Endorsement
	 * 7. Check that Document generation triggered after Endorsement
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement document trigger")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17498")
	public void testEarthquakeEndorsement_checkDocGenTrigger_Endorsement(@Optional("OK") String state) {
		String policyNumber = createPolicy(false);
		initiateEndorsementTx();
		addEndorsementForm(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId());
		addEndorsementForm(EndorsementForms.HomeSSEndorsementForms.DS_04_68.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_68.getFormId());

		finishRenewalOrEndorsementTx(true);

		//Check that Document generation triggered after Policy Endorsement
		checkDocGenTriggered(policyNumber, ENDORSEMENT_ISSUE, DocGenEnum.Documents.DS0468.getIdInXml(),
				DocGenEnum.Documents.DS0469.getIdInXml());
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for NON privileged user
	 * @scenario
	 * 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Create Policy
	 * 4. Switch Time to R-35
	 * 5. Create Renewal in 'Proposed' status via job
	 * 6. Open Renewal
	 * 7. Add 'Earthquake' and 'Loss Assessment Coverage For Earthquake'
	 * 8. Purchase Renewal
	 * 9. Check that Document generation triggered after Renewal
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement document trigger")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17498")
	public void testEarthquakeEndorsement_checkDocGenTrigger_Renewal(@Optional("OK") String state) {
		String policyNumber = createPolicy(false);
		createProposedRenewal();

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		navigateToRenewalPremiumAndCoveragesTab();
		addEndorsementForm(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId());
		addEndorsementForm(EndorsementForms.HomeSSEndorsementForms.DS_04_68.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_68.getFormId());

		finishRenewalOrEndorsementTx(true);

		//Check that Document generation triggered after Policy Endorsement
		checkDocGenTriggered(policyNumber, RENEWAL_OFFER, DocGenEnum.Documents.DS0468.getIdInXml(),
				DocGenEnum.Documents.DS0469.getIdInXml());
	}
}