package aaa.modules.regression.service.auto_ca.select.functional;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestMiniServicesCoveragesHelperCA;
import aaa.utils.StateList;
import toolkit.datax.TestData;
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
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-28579", "PAS-31509"})
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
	 * PAS-29261
	 * 6. Replace vehicles with keepCoverages=yes and verify that coverages that are expected to retain are retained and coverages that are not expected to retain are not retained
	 * PAS-15405
	 * 7. Replace vehicles with keepAssignments=yes/no and check result
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-26668", "PAS-29261", "PAS-15405"})
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
	 * @scenario
	 * Check ETEC in the same scenario
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15424", "PAS-30268", "PAS-15420"})
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
	 * @scenario
	 * Check ETEC in the same scenario
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15424", "PAS-15420"})
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
	 * @scenario
	 * Check ETEC in the same scenario
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15424", "PAS-15420"})
	public void pas15424_viewUpdateOEMCoverageCATC03(@Optional("CA") String state) {
		pas15424_viewUpdateOEMCoverageNewVehNoCompCollCABody(false, true);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15424", "PAS-15420"})
	public void pas15424_viewUpdateOEMCoverageCATC04(@Optional("CA") String state) {
		pas15424_viewUpdateOEMCoverageNewVehNoCompCollCABody(true, false);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15424", "PAS-30268", "PAS-15420"})
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
	 * @scenario
	 * Check ETEC in the same scenario
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
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15424", "PAS-15420"})
	public void pas15424_viewUpdateOEMCoverageCATC07(@Optional("CA") String state) {
		pas15424_viewUpdateOEMCoverageExistingVehicleNoCompCollTC06Body(true, false);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15424", "PAS-30268", "PAS-15420"})
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
	 * @scenario
	 * Check ETEC in the same scenario
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15424", "PAS-15420"})
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
	 * @author Maris Strazds
	 * @name View/Update Coverages - ETEC - CA Select
	 * @scenario
	 * 1. Create policy in PAS with so that all vehicles has other than 25/750 ETEC limit
	 * 2. Create endorsement through service
	 * 3. Add vehicle and check that ETEC Limit is defaulted to 25/750 wit correct available limits
	 * 4. Remove COLLDED/COMPDED, then put it back and check that ETEC is defaulted to 25/750 wit correct available limits
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15420"})
	public void pas15420_ETECWhenNotAllVehiclesHasDefaultLimit24750(@Optional("CA") String state) {
		pas15420_ETECWhenNotAllVehiclesHasDefaultLimit24750Body();
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

	/**
	 * @author Maris Strazds
	 * @name Update Coverage - ADB
	 * @scenario1
	 * 1. Create policy With multiple drivers
	 * 2. Verify ADB coverage showing only for AFR drivers.
	 * 3. Run View Premium service save the Premium
	 * 4. Update Coverage Service add adb coverage to the AFR driver verify Premium should increased
	 * 5. Update Coverage Service add adb coverage for 2 AFR driver verify Premium should increased
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-17642", "PAS-30778"})
	public void pas17642_UpdateCoverageADB_CA(@Optional("CA") String state) {
		pas17642_UpdateCoverageADBBody();
	}

	/**
	 * @author Maris Strazds
	 * @name Default COMP and COLL to default values when replacing vehicle with financed/leased vehicle
	 * @scenario
	 * 1. Create policy in PAS with owned vehicle without COMP and COLL
	 * 2. Create endorsement through service
	 * 3. Replace vehicle with Financed vehicle
	 * 4. Verify that COMP and COLL is defaulted to state default values
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-31098"})
	public void pas31098_noCollAndCompFNC(@Optional("CA") String state) {
		TestData testData = getPolicyDefaultTD();
		testData.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel()), "contains=No Coverage");
		pas31098_body(testData, "FNC");
	}

	/**
	 * @author Maris Strazds
	 * @name Default COMP and COLL to default values when replacing vehicle with financed/leased vehicle
	 * @scenario
	 * 1. Create policy in PAS with owned vehicle without COLL
	 * 2. Create endorsement through service
	 * 3. Replace vehicle with Financed vehicle
	 * 4. Verify that COMP and COLL is defaulted to state default values
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-31098"})
	public void pas31098_noCollFNC(@Optional("CA") String state) {
		TestData testData = getPolicyDefaultTD();
		testData.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel()), "contains=No Coverage");
		pas31098_body(testData, "FNC");
	}

	/**
	 * @author Maris Strazds
	 * @name Default COMP and COLL to default values when replacing vehicle with financed/leased vehicle
	 * @scenario
	 * 1. Create policy in PAS with owned vehicle without COMP and COLL
	 * 2. Create endorsement through service
	 * 3. Replace vehicle with Leased vehicle
	 * 4. Verify that COMP and COLL is defaulted to state default values
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-31098"})
	public void pas31098_noCollAndCompLSD(@Optional("CA") String state) {
		TestData testData = getPolicyDefaultTD();
		testData.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel()), "contains=No Coverage");
		pas31098_body(testData, "LSD");
	}

	/**
	 * @author Maris Strazds
	 * @name Default COMP and COLL to default values when replacing vehicle with financed/leased vehicle
	 * @scenario
	 * 1. Create policy in PAS with owned vehicle without COLL
	 * 2. Create endorsement through service
	 * 3. Replace vehicle with Leased vehicle
	 * 4. Verify that COMP and COLL is defaulted to state default values
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-31098"})
	public void pas31098_noCollLSD(@Optional("CA") String state) {
		TestData testData = getPolicyDefaultTD();
		testData.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel()), "contains=No Coverage");
		pas31098_body(testData, "LSD");
	}
}
