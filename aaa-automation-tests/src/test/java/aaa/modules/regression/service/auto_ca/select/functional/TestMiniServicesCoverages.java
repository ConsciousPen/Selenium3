package aaa.modules.regression.service.auto_ca.select.functional;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestMiniServicesCoveragesHelperCA;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestMiniServicesCoverages extends TestMiniServicesCoveragesHelperCA {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	/**
	 * @author Maris Strazds
	 * @name CA View Policy Level Coverages
	 * @scenario
	 * 1.Create policy in PAS
	 * 2. Create endorsement through DXP
	 * 3. Run View Endorsement coverages
	 * 4. Verify Policy Level Coverages
	 */
	@Parameters({"state"})
	@StateList(states = Constants.States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15412"})
	public void pas15412_viewCAPolicyLevelCoverages(@Optional("CA") String state) {
		pas15412_viewCAPolicyLevelCoveragesBody();
	}

	/**
	 * @author Maris Strazds
	 * @name CA View Policy Level Coverages
	 * @scenario
	 * 1.Create policy in PAS
	 * 2. Create endorsement through DXP
	 * 3. Update Policy Level Coverages And Check
	 */
	@Parameters({"state"})
	@StateList(states = Constants.States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-28579"})
	public void pas28579_updatePolicyLevelCoveragesCA(@Optional("CA") String state) {
		pas28579_updatePolicyLevelCoveragesCABody();
	}

	/**
	 * @author Maris Strazds
	 * @name View Coverages - RideSharing Coverage - CA
	 * @scenario
	 * 1. Create policy in PAS with different vehicle types (and Multiple regular vehicles)
	 * 2. Create endorsement through service
	 * 3. Add vehicle through service
	 * 4. Run viewEndorsementCoverages service
	 * 5. Verify RideSharing coverage (available only for Regular and Antique Vehicles)
	 * NOTE: not possible to check case when rideSharing coverage = yes, as not possible to create Endorsement through service for policies with RideSharing Coverage
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15423"})
	public void pas15423_rideSharingCoverageCA(@Optional("CA") String state) {
		pas15423_rideSharingCoverageCABody();
	}

	/**
	 * @author Maris Strazds
	 * @name View Coverages Service - CA Select Policies - Vehicle Level Coverages
	 * @scenario
	 * 1. Create policy in PAS
	 * 2. Create endorsement through service
	 * 3. Add vehicle through service
	 * 4. Run viewEndorsementCoverages service
	 * 5. Verify Vehicle Level Coverages
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-26668"})
	public void pas26668_viewVehicleLevelCoveragesCA(@Optional("CA") String state) {
		pas26668_viewVehicleLevelCoveragesCABody();
	}

	/**
	 * @author Maris Strazds
	 * @name View/Update Coverages - OEM - CA Select
	 * @scenario
	 * 1. Create policy in PAS
	 * 2. Create endorsement through service
	 * 3. Add vehicle less than 10y old
	 * 4. Apply OEM and check
	 * 5. Remove COM and/or COLL and check that OEM is removed
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15424", "PAS-30268"})
	public void pas15424_viewUpdateOEMCoverageCATC01(@Optional("CA") String state) {
		pas15424_viewUpdateOEMCoverageCATC01Body();
	}

	/**
	 * @author Maris Strazds
	 * @name View/Update Coverages - OEM - CA Select
	 * @scenario
	 * 1. Create policy in PAS with vehicle more than 10y old vehicle
	 * 2. Create endorsement through service
	 * 3. Run viewEndorsmentCoverages service and Verify that vehicle older than 10y doesn't have OEM available (existing vehicle)
	 * 4. Add vehicle more than 10y old
	 * 5. Run viewEndorsmentCoverages service and Verify that vehicle older than 10y doesn't have OEM available (newly added vehicle)
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15424"})
	public void pas15424_viewUpdateOEMCoverageVehOlderThan10yCATC02(@Optional("CA") String state) {
		pas15424_viewUpdateOEMCoverageVehOlderThan10yCATC02Body();
	}

	/**
	 * @author Maris Strazds
	 * @name View/Update Coverages - OEM - CA Select
	 * @scenario
	 * 1. Create policy in PAS
	 * 2. Create endorsement through service
	 * 3. Add vehicle Less than 10y old
	 * 4. Check that it has OEM available
	 * 5. Remove COMPDED and/or COLLDED and verify that OEM is not available anymore
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15424"})
	public void pas15424_viewUpdateOEMCoverageCATC03(@Optional("CA") String state) {
		pas15424_viewUpdateOEMCoverageNewVehNoCompCollCABody(false, true);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15424"})
	public void pas15424_viewUpdateOEMCoverageCATC04(@Optional("CA") String state) {
		pas15424_viewUpdateOEMCoverageNewVehNoCompCollCABody(true, false);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15424", "PAS-30268"})
	public void pas15424_viewUpdateOEMCoverageCATC05(@Optional("CA") String state) {
		pas15424_viewUpdateOEMCoverageNewVehNoCompCollCABody(true, true);
	}

	/**
	 * @author Maris Strazds
	 * @name View/Update Coverages - OEM - CA Select
	 * @scenario
	 * 1. Create policy in PAS with vehicle Less than 10y old
	 * 2. Create endorsement through service
	 * 3. Check that it has OEM available
	 * 4. Remove COMPDED and/or COLLDED and verify that OEM is not available anymore
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15424"})
	public void pas15424_viewUpdateOEMCoverageCATC06(@Optional("CA") String state) {
		pas15424_viewUpdateOEMCoverageExistingVehicleNoCompCollTC06Body(false, true);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15424"})
	public void pas15424_viewUpdateOEMCoverageCATC07(@Optional("CA") String state) {
		pas15424_viewUpdateOEMCoverageExistingVehicleNoCompCollTC06Body(true, false);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15424", "PAS-30268"})
	public void pas15424_viewUpdateOEMCoverageCATC08(@Optional("CA") String state) {
		pas15424_viewUpdateOEMCoverageExistingVehicleNoCompCollTC06Body(true, true);
	}

	/**
	 * @author Maris Strazds
	 * @name View/Update Coverages - OEM - CA Select
	 * @scenario
	 * 1. Create policy in PAS with vehicle without COMPDED and/or COLLDED (and no OEM)
	 * 2. Create endorsement through service
	 * 3. Run viewEndorsement coverages and verify thaat OEM is not available as has not applied COMPDED and/or COLLDED
	 * 4. Apply COMPDED and/or COLLDED and check that OEM is available
	 * 5. Apply OEM
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15424"})
	public void pas15424_viewUpdateOEMCoverageCATC09(@Optional("CA") String state) {
		pas15424_viewUpdateOEMCoverageCATC09Body();
	}

	/**
	 * @author Maris Strazds
	 * @name View/Update Coverages - OEM - CA Select
	 * @scenario
	 * 1. Create policy in PAS with less than 10y old vehicle without OEM applied (but with COMPDED and COLLDED applied)
	 * 2. Create endorsement through service
	 * 3. Check that OEM is available
	 * 4. Applie OEM
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15424"})
	public void pas15424_viewUpdateOEMCoverageLessThan10yNoOEMCATC010(@Optional("CA") String state) {
		pas15424_viewUpdateOEMCoverageLessThan10yNoOEMCATC010Body();
	}

	/**
	 * @author Megha Gubbala, Maris Strazds
	 * @name Verify Policy and Vehicle level coverages Order and Driver level coverages order
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-19057"})
	public void pas19057_OrderOfCoverageCATC01(@Optional("CA") String state) {
		assertSoftly(softly -> {
			pas19057_OrderOfCoverageBodyCA(softly, true);
		});
	}

	/**
	 * @author Megha Gubbala, Maris Strazds
	 * @name Verify Policy and Vehicle level coverages Order and Driver level coverages order
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-19057"})
	public void pas19057_OrderOfCoverageCATC02(@Optional("CA") String state) {
		assertSoftly(softly -> {
			pas19057_OrderOfCoverageBodyCA(softly, false);
		});
	}
}
