package aaa.modules.regression.conversions.home_ss.ho6.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.EndorsementForms;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestEndorsementsTabAbstract;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.OK)
public class TestEarthquakeEndorsements extends TestEndorsementsTabAbstract {

	@Override
	protected PolicyType getPolicyType() { return PolicyType.HOME_SS_HO6; }

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for privileged user
	 * @scenario
	 * 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Do 'New Renewal Entry' action (initiate Conversion Policy)
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
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-17479")
	public void testEarthquakeEndorsement_Privileged_NewBusiness(@Optional("") String state) {
		initiateNewBusinessTx(true);
		testEndorsementForms(EndorsementForms.HomeSSEndorsementForms.HS_04_54.getName(),
				EndorsementForms.HomeSSEndorsementForms.HS_04_54.getFormId());
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for privileged user
	 * @scenario
	 * 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Create Conversion Policy
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
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-17479")
	public void testEarthquakeEndorsement_Privileged_Endorsement(@Optional("") String state) {
		createPolicy(true);
		initiateEndorsementTx();
		testEndorsementForms(EndorsementForms.HomeSSEndorsementForms.HS_04_54.getName(),
				EndorsementForms.HomeSSEndorsementForms.HS_04_54.getFormId());
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test earthquake endorsement form for privileged user
	 * @scenario
	 * 1. Login app with user who has 'Add/Remove OKEarthquake Endorsement' privilege (PAS-16030)
	 * 2. Create Customer
	 * 3. Create Conversion Policy
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
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-17479")
	public void testEarthquakeEndorsement_Privileged_Renewal(@Optional("") String state) {
		createPolicy(true);
		initiateRenewalTx();
		testEndorsementForms(EndorsementForms.HomeSSEndorsementForms.HS_04_54.getName(),
				EndorsementForms.HomeSSEndorsementForms.HS_04_54.getFormId());
	}
}