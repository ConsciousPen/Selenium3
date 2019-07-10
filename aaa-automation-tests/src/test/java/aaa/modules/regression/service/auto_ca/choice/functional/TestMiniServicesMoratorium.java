package aaa.modules.regression.service.auto_ca.choice.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestMiniServicesMoratoriumHelper;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestMiniServicesMoratorium extends TestMiniServicesMoratoriumHelper {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}

	/**
	 * @author Nauris Ivanans
	 * @name Moratoriums - Garaging Address on the Policy/Adding/Updating a Vehicle
	 * @scenario1 1. Create backdate policy (-2d).
	 * 2. Hit start endorsement info service. Check the response.
	 * 3. Create endorsement outside of PAS.
	 * 4. Add and update vehicle with different garage address
	 * 5. Add another vehicle, but zip code should be under the moratorium. Check the response.
	 * 6. Rate.
	 * 7. Check the response after update and after rate service
	 * 8. Delete endorsement.
	 *
	 * @scenario2 1. Create new endorsement (Backdate -2d)
	 * 2. Add vehicle, garage address not different.
	 * 3. Add another vehicle, garage address which gonna be under the moratorium after two days.
	 * 4. Check the response, if error is not displaying.
	 * 5. Rate and bind endorsement.
	 * 6. Hit start endorsement info service (today). Check the response.
	 */
	@Override
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_CHOICE, testCaseId = {"PAS-22549"})
	@StateList(states = {Constants.States.CA})
	public void pas21466_GarageAddressUnderTheMoratorium(@Optional("CA") String state) {
		super.pas21466_GarageAddressUnderTheMoratorium(state);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Moratoriums - Garaging Address on the Policy/Adding/Updating a Vehicle
	 * @scenario1 1. Create AZ backdate policy (-5d). Residential address should be under the moratorium.
	 * 2. Hit start endorsement info service. Check the response. (-5d)
	 * and check for today date, when moratorium rule should exist, and future date.
	 * 3. Create endorsement outside of PAS (-5d).
	 * 4. Update existing vehicle garage address (not under moratorium)
	 * 5. Bind and rate endorsement.
	 * 6. Create new endorsement outside of PAS (today).
	 * 7. Add and update vehicle with different garage address, CA - under the moratorium.
	 * 8. Check the response. And try rate.
	 * 9. Update new vehicle again, with CA address not under the moratorium.
	 * 10. Check the response. Rate and Bind.
	 * 11. Update new vehicle again, with AZ address not under the moratorium.
	 * 12. Rate and Bind.
	 */
	@Override
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@StateList(states = {Constants.States.CA})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_CHOICE, testCaseId = {"PAS-22549"})
	public void pas21466_GarageAddressUnderTheMoratoriumPart2(@Optional("CA") String state) {
		super.pas21466_GarageAddressUnderTheMoratoriumPart2(state);
	}
}
