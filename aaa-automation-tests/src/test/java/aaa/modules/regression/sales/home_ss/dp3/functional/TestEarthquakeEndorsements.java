package aaa.modules.regression.sales.home_ss.dp3.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.EndorsementForms;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestEndorsementsTabTemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.OK)
public class TestEarthquakeEndorsements extends TestEndorsementsTabTemplate {

	private String parentEndorsementFormId = EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId();
	private String subEndorsementFormId = EndorsementForms.HomeSSEndorsementForms.DS_04_68.getFormId();

	private String parentEndorsementDocGenId = DocGenEnum.Documents.DS0469.getIdInXml();
	private String subEndorsementDocGenId = DocGenEnum.Documents.DS0468.getIdInXml();

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
	public void pas17479_pas17489_Privileged_NewBusiness(@Optional("OK") String state) {
		newBusinessTx_privileged(true, parentEndorsementFormId, subEndorsementFormId);
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
	public void pas17479_pas17489_Privileged_Endorsement(@Optional("OK") String state) {
		endorsementTx_privileged(true, parentEndorsementFormId, subEndorsementFormId);
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
	public void pas17479_pas17489_Privileged_Renewal(@Optional("OK") String state) {
		renewalTx_privileged(true, parentEndorsementFormId, subEndorsementFormId);
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
	public void pas17484_NonPrivileged_NewBusinessTx(@Optional("OK") String state) {
		newBusinessTx_NonPrivileged(parentEndorsementFormId);
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
	public void pas17484_NonPrivileged_NewBusinessTx_AlreadyHadEndorsement(@Optional("OK") String state) {
		newBusinessTx_NonPrivileged_AlreadyHadEndorsement(parentEndorsementFormId, subEndorsementFormId);
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
	public void pas17484_NonPrivileged_EndorsementTx(@Optional("OK") String state) {
		endorsementTx_NonPrivileged(parentEndorsementFormId);
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
	public void pas17484_pas17498_NonPrivileged_EndorsementTx_AlreadyHadEndorsement(@Optional("OK") String state) {
		endorsementTx_NonPrivileged_AlreadyHadEndorsement(parentEndorsementFormId, subEndorsementFormId);
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
	public void pas17484_NonPrivileged_RenewalTx(@Optional("OK") String state) {
		renewalTx_NonPrivileged(parentEndorsementFormId);
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
	public void pas17484_NonPrivileged_RenewalTx_AlreadyHadEndorsement(@Optional("OK") String state) {
		renewalTx_NonPrivileged_AlreadyHadEndorsement(parentEndorsementFormId, subEndorsementFormId);
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
	public void pas17494_checkDocGenTrigger_NewBusiness(@Optional("OK") String state) {
		checkDocGenTrigger_NewBusinessTx(parentEndorsementFormId, subEndorsementFormId, parentEndorsementDocGenId, subEndorsementDocGenId);
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
	public void pas17498_checkDocGenTrigger_Endorsement(@Optional("OK") String state) {
		checkDocGenTrigger_EndorsementTx(parentEndorsementFormId, subEndorsementFormId, parentEndorsementDocGenId, subEndorsementDocGenId);
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "OK Earthquake endorsement document trigger")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17498")
	public void pas17498_checkDocGenTrigger_Renewal(@Optional("OK") String state) {
		checkDocGenTrigger_RenewalTx(parentEndorsementFormId, subEndorsementFormId, parentEndorsementDocGenId, subEndorsementDocGenId);
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
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17494")
	public void pas17494_checkPremium_NewBusinessTx(@Optional("OK") String state) {
		checkPremium_NewBusinessTx(parentEndorsementFormId, subEndorsementFormId);
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
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17494")
	public void pas17494_checkPremium_EndorsementTx(@Optional("OK") String state) {
		checkPremium_EndorsementTx(parentEndorsementFormId, subEndorsementFormId);
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
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-17494")
	public void pas17494_checkPremium_RenewalTx(@Optional("OK") String state) {
		checkPremium_RenewalTx(parentEndorsementFormId, subEndorsementFormId);
	}
}