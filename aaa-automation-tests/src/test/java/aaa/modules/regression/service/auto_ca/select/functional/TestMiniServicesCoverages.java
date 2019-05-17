package aaa.modules.regression.service.auto_ca.select.functional;

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
}
