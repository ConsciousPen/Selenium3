package aaa.modules.regression.sales.home_ss.ho6.functional;

import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.EndorsementForms;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
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

	private String parentEndorsementFormId = EndorsementForms.HomeSSEndorsementForms.HS_04_54.getFormId();
	private String subEndorsementFormId = EndorsementForms.HomeSSEndorsementForms.HS_04_36.getFormId();

	private String parentEndorsementDocGenId = DocGenEnum.Documents.HS0454.getIdInXml();
	private String childEndorsementDocGenId = DocGenEnum.Documents.HS0436.getIdInXml();

	@Override
	protected PolicyType getPolicyType() { return PolicyType.HOME_SS_HO6; }

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for privileged user
	 * @scenario
	 * 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Initiate Quote creation
	 * 4. Fill everything up until 'Premium & Coverages' tab -> 'Endorsement' subtab
	 * 5. Check that 'Earthquake' endorsement is available in 'Optional Endorsements' (HS 04 54)
	 * 6. Add 'Earthquake' endorsement
	 * 7. Check that Edit link is available
	 * 8. Change something in endorsement and verify that it is saved
	 * 9. Check that 'Loss Assessment Coverage For Earthquake' endorsement is available in 'Optional Endorsements' (HS 04 36)
	 * 10. Add 'Loss Assessment Coverage For Earthquake' endorsement
	 * 11. Check that Edit link is available
	 * 12. Change something in endorsement and verify that it is saved
	 * 13. Check that Remove link is available
	 * 14. Remove endorsements and check that they are removed
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement check for privileged user")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-17479, PAS-17489")
	public void testEarthquakeEndorsement_Privileged_NewBusiness(@Optional("OK") String state) {
		createQuoteAndFillUpTo(EndorsementTab.class);

		addEndorsementForm(parentEndorsementFormId);
		//HS 04 36 should be only available then HS 04 54 is added
		addEndorsementForm(subEndorsementFormId);

		checkEndorsementFunctionality(subEndorsementFormId, parentEndorsementFormId);
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
	 * 6. Check that 'Earthquake' endorsement is available in 'Optional Endorsements' (HS 04 54)
	 * 7. Add 'Earthquake' endorsement
	 * 8. Check that Edit link is available
	 * 9. Change something in endorsement and verify that it is saved
	 * 10. Check that 'Loss Assessment Coverage For Earthquake' endorsement is available in 'Optional Endorsements' (HS 04 36)
	 * 11. Add 'Loss Assessment Coverage For Earthquake'' endorsement
	 * 12. Check that Edit link is available
	 * 13. Change something in endorsement and verify that it is saved
	 * 14. Check that Remove link is available
	 * 15. Remove endorsements and check that they are removed
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement check for privileged user")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-17479, PAS-17489")
	public void testEarthquakeEndorsement_Privileged_Endorsement(@Optional("OK") String state) {
		openAppAndCreatePolicy();
		initiateEndorsementTx();

		addEndorsementForm(parentEndorsementFormId);
		//HS 04 36 should be only available then HS 04 54 is added
		addEndorsementForm(subEndorsementFormId);

		checkEndorsementFunctionality(subEndorsementFormId, parentEndorsementFormId);
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
	 * 6. Check that 'Earthquake' endorsement is available in 'Optional Endorsements' (HS 04 54)
	 * 7. Add 'Earthquake' endorsement
	 * 8. Check that Edit link is available
	 * 9. Change something in endorsement and verify that it is saved
	 * 10. Check that 'Loss Assessment Coverage For Earthquake' endorsement is available in 'Optional Endorsements' (HS 04 36)
	 * 11. Add 'Earthquake' endorsement
	 * 12. Check that Edit link is available
	 * 13. Change something in endorsement and verify that it is saved
	 * 14. Check that Remove link is available
	 * 15. Remove endorsements and check that they are removed
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement check for privileged user")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-17479, PAS-17489")
	public void testEarthquakeEndorsement_Privileged_Renewal(@Optional("OK") String state) {
		openAppAndCreatePolicy();
		initiateRenewalTx();

		addEndorsementForm(parentEndorsementFormId);
		//HS 04 36 should be only available then HS 04 54 is added
		addEndorsementForm(subEndorsementFormId);

		checkEndorsementFunctionality(subEndorsementFormId, parentEndorsementFormId);
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for NON privileged user
	 * @scenario
	 * 1. Login app with user who DOES NOT HAVE 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Initiate Quote creation
	 * 4. FIll everything up until 'Premium & Coverages' tab -> 'Endorsement' subtab
	 * 5. Check that 'Earthquake' endorsement is NOT available in 'Optional Endorsements' (HS 04 54)
	 * 6. Check that 'Earthquake' endorsement is NOT available in 'Included Endorsements' (HS 04 54)
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement check for non privileged user")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-17484")
	public void testEarthquakeEndorsement_NonPrivileged_NewBusinessTx(@Optional("OK") String state) {
		initiateNewBusinessTx_NonPrivileged("F35");
		checkEndorsementIsNotAvailableInOptionalEndorsements(parentEndorsementFormId);
		checkEndorsementIsNotAvailableInIncludedEndorsements(parentEndorsementFormId);
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
	 * 9. Check that 'Earthquake' endorsement is available in 'Included Endorsements' (HS 04 54)
	 * 10. Check that 'Loss Assessment Coverage For Earthquake' endorsement is available in 'Included Endorsements' (HS 04 36)
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
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-17484")
	public void testEarthquakeEndorsement_NonPrivileged_NewBusinessTx_AlreadyHadEndorsement(@Optional("OK") String state) {
		initiateNewBusinessTx_NonPrivileged_AlreadyHadEndorsement("F35", parentEndorsementFormId, subEndorsementFormId);

		checkEndorsementFunctionality(subEndorsementFormId, parentEndorsementFormId);
		checkEndorsementIsNotAvailableInOptionalEndorsements(parentEndorsementFormId, subEndorsementFormId);

		finishNewBusinessTx();

		//2nd Endorsement -> check that 'Earthquake' endorsement doesn't exist in Endorsement tab
		initiateEndorsementTx();
		checkEndorsementIsNotAvailableInIncludedEndorsements(parentEndorsementFormId);
		checkEndorsementIsNotAvailableInOptionalEndorsements(parentEndorsementFormId);
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
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-17484")
	public void testEarthquakeEndorsement_NonPrivileged_EndorsementTx(@Optional("OK") String state) {
		String policyNumber = openAppAndCreatePolicy();
		closeOpenAppNonPrivilegedUser("F35");
		SearchPage.openPolicy(policyNumber);
		initiateEndorsementTx();
		checkEndorsementIsNotAvailableInOptionalEndorsements(parentEndorsementFormId);
		checkEndorsementIsNotAvailableInIncludedEndorsements(parentEndorsementFormId);
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
	 * 7. Check that 'Earthquake' endorsement is available in 'Included Endorsements' (HS 04 54)
	 * 8. Check that 'Loss Assessment Coverage For Earthquake' endorsement is available in 'Included Endorsements' (HS 04 36)
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
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-17484, PAS-17498")
	public void testEarthquakeEndorsement_NonPrivileged_EndorsementTx_AlreadyHadEndorsement(@Optional("OK") String state) {
		String policyNumber = createPolicyWithEndorsement(parentEndorsementFormId, subEndorsementFormId);

		closeOpenAppNonPrivilegedUser("F35");
		SearchPage.openPolicy(policyNumber);
		initiateEndorsementTx();

		checkEndorsementFunctionality(subEndorsementFormId, parentEndorsementFormId);
		checkEndorsementIsNotAvailableInOptionalEndorsements(subEndorsementFormId, parentEndorsementFormId);

		finishRenewalOrEndorsementTx(false);

		//2nd Endorsement -> check that 'Earthquake' endorsement doesn't exist in Endorsement tab
		initiateEndorsementTx();
		checkEndorsementIsNotAvailableInIncludedEndorsements(parentEndorsementFormId, subEndorsementFormId);
		checkEndorsementIsNotAvailableInOptionalEndorsements(parentEndorsementFormId, subEndorsementFormId);
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
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-17484")
	public void testEarthquakeEndorsement_NonPrivileged_RenewalTx(@Optional("OK") String state) {
		String policyNumber = openAppAndCreatePolicy();
		initiateRenewalTx();
		new PremiumsAndCoveragesQuoteTab().saveAndExit();

		closeOpenAppNonPrivilegedUser("A30");
		SearchPage.openPolicy(policyNumber);
		navigateToRenewalPremiumAndCoveragesTab();
		checkEndorsementIsNotAvailableInOptionalEndorsements(parentEndorsementFormId);
		checkEndorsementIsNotAvailableInIncludedEndorsements(parentEndorsementFormId);
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
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-17484")
	public void testEarthquakeEndorsement_NonPrivileged_RenewalTx_AlreadyHadEndorsement(@Optional("OK") String state) {
		String policyNumber = createPolicyWithEndorsement(parentEndorsementFormId, subEndorsementFormId);

		initiateRenewalTx();
		new PremiumsAndCoveragesQuoteTab().saveAndExit();

		closeOpenAppNonPrivilegedUser("F35");
		SearchPage.openPolicy(policyNumber);
		navigateToRenewalPremiumAndCoveragesTab();

		checkEndorsementFunctionality(subEndorsementFormId, parentEndorsementFormId);
		checkEndorsementIsNotAvailableInOptionalEndorsements(parentEndorsementFormId, subEndorsementFormId);

		finishRenewalOrEndorsementTx(false);

		//2nd Renewal tx -> check that 'X' endorsement doesn't exist in Endorsement tab
		navigateToRenewalPremiumAndCoveragesTab();
		checkEndorsementIsNotAvailableInIncludedEndorsements(parentEndorsementFormId, subEndorsementFormId);
		checkEndorsementIsNotAvailableInOptionalEndorsements(parentEndorsementFormId, subEndorsementFormId);
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
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-17498")
	public void testEarthquakeEndorsement_checkDocGenTrigger_NewBusiness(@Optional("OK") String state) {
		String policyNumber = createPolicyWithEndorsement(parentEndorsementFormId, subEndorsementFormId);

		//Check that Document generation triggered after Policy Creation
		checkDocGenTriggered(policyNumber, POLICY_ISSUE, parentEndorsementDocGenId, childEndorsementDocGenId);
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
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-17498")
	public void testEarthquakeEndorsement_checkDocGenTrigger_Endorsement(@Optional("OK") String state) {
		String policyNumber = openAppAndCreatePolicy();
		initiateEndorsementTx();
		addEndorsementForm(parentEndorsementFormId);
		addEndorsementForm(subEndorsementFormId);

		finishRenewalOrEndorsementTx(true);

		//Check that Document generation triggered after Policy Endorsement
		checkDocGenTriggered(policyNumber, ENDORSEMENT_ISSUE, parentEndorsementDocGenId, childEndorsementDocGenId);
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
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-17498")
	public void testEarthquakeEndorsement_checkDocGenTrigger_Renewal(@Optional("OK") String state) {
		String policyNumber =  openAppAndCreatePolicy();
		moveTimeAndRunRenewJobs(getTimePoints().getRenewOfferGenerationDate(PolicySummaryPage.getExpirationDate()));

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		navigateToRenewalPremiumAndCoveragesTab();
		addEndorsementForm(parentEndorsementFormId);
		addEndorsementForm(subEndorsementFormId);

		finishRenewalOrEndorsementTx(true);

		//Check that Document generation triggered after Policy Endorsement
		checkDocGenTriggered(policyNumber, RENEWAL_OFFER, parentEndorsementDocGenId, childEndorsementDocGenId);
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for NON privileged user
	 * @scenario
	 * 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Initiate Quote Creation
	 * 4. Fill everything up until 'Premium & Coverages' tab -> 'Quote' subtab
	 * 5. Calculate Premium
	 * 6. Navigate to 'Premium & Coverages' tab -> 'Endorsement' subtab
	 * 7. Add 'Earthquake' and 'Loss Assessment Coverage For Earthquake' endorsements
	 * 8. Navigate to 'Premium & Coverages' tab -> 'Quote' subtab
	 * 9. Calculate Premium
	 * 10. Check that Premium has increased
	 * 11. Check that Endorsements are displayed in "Endorsement Forms" section
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement premium change check")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-17494")
	public void testEarthquakeEndorsement_checkPremium_NewBusinessTx(@Optional("OK") String state) {
		createQuoteAndFillUpTo(EndorsementTab.class);
		checkEndorsementsIncreasesPremium(parentEndorsementFormId, subEndorsementFormId);
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for NON privileged user
	 * @scenario
	 * 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Create Policy
	 * 4. Initiate Endorsement
	 * 5. Fill everything up until 'Premium & Coverages' tab -> 'Quote' subtab
	 * 6. Calculate Premium
	 * 7. Navigate to 'Premium & Coverages' tab -> 'Endorsement' subtab
	 * 8. Add 'Earthquake' and 'Loss Assessment Coverage For Earthquake' endorsements
	 * 9. Navigate to 'Premium & Coverages' tab -> 'Quote' subtab
	 * 10. Calculate Premium
	 * 11. Check that Premium has increased
	 * 12. Check that Endorsements are displayed in "Endorsement Forms" section
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement premium change check")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-17494")
	public void testEarthquakeEndorsement_checkPremium_EndorsementTx(@Optional("OK") String state) {
		openAppAndCreatePolicy();
		initiateEndorsementTx();
		checkEndorsementsIncreasesPremium(parentEndorsementFormId, subEndorsementFormId);
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for NON privileged user
	 * @scenario
	 * 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Create Policy
	 * 4. Initiate Renewal
	 * 5. Fill everything up until 'Premium & Coverages' tab -> 'Quote' subtab
	 * 6. Calculate Premium
	 * 7. Navigate to 'Premium & Coverages' tab -> 'Endorsement' subtab
	 * 8. Add 'Earthquake' and 'Loss Assessment Coverage For Earthquake' endorsements
	 * 9. Navigate to 'Premium & Coverages' tab -> 'Quote' subtab
	 * 10. Calculate Premium
	 * 11. Check that Premium has increased
	 * 12. Check that Endorsements are displayed in "Endorsement Forms" section
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "OK Earthquake endorsement premium change check")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-17494")
	public void testEarthquakeEndorsement_checkPremium_RenewaltTx(@Optional("OK") String state) {
		openAppAndCreatePolicy();
		initiateRenewalTx();
		checkEndorsementsIncreasesPremium(parentEndorsementFormId, subEndorsementFormId);
	}
}