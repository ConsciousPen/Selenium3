package aaa.modules.regression.sales.home_ss.ho6.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestUpdateWildfireScoreTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.UT)
public class TestOverrideWildfireScore extends TestUpdateWildfireScoreTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO6;
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test Update Wildfire Score HO6 - No privilege, NB
	 * @scenario
	 * 1. Sign in with unprivileged User.
	 * 2. Create Customer.
	 * 3. Initiate Home Owners Quote.
	 * 4. Fill Policy up to Property Info Tab.
	 * 5. Assert That Wildfire Score field is disabled (not editable).
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Test Update Wildfire Score HO6 - No privilege, NB")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-12922")
	public void pas12922_UpdateWildfireScoreNoPrivilegeNB(@Optional("UT") String state) {

		pas12922_UpdateWildfireScoreNoPrivilegeNB();
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test Update Wildfire Score HO6 - No Privilege, Endorsement
	 * @scenario
	 * 1. Sign in with unprivileged User.
	 * 2. Create Customer.
	 * 3. Create Home Owners Policy.
	 * 4. Endorse Policy.
	 * 5. Navigate to Property Info Tab.
	 * 6. Assert That Wildfire Score field is disabled (not editable).
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Test Update Wildfire Score HO6 - No Privilege, Endorsement")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-12922")
	public void pas12922_UpdateWildfireScoreNoPrivilegeEndorsement(@Optional("UT") String state) {

		pas12922_UpdateWildfireScoreNoPrivilegeEndorsement();
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test Update Wildfire Score HO6 - NB
	 * @scenario
	 * 1. Sign in with privileged User.
	 * 2. Create Customer.
	 * 3. Initiate Home Owners Quote.
	 * 4. Fill Quote up to Reports Tab.
	 * 5. Save Wildfire Score Value.
	 * 6. Submit Tab.
	 * 7. Fill Property Info Tab.
	 * 8. Assert That Wildfire Score field is enabled (editable).
	 * 9. Update Wildfire Score Value.
	 * 10. Navigate To reports Tab.
	 * 11. Assert That Wildfire Score Value did not change.
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Test Update Wildfire Score HO6 - NB")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-12922")
	public void pas12922_UpdateWildfireScoreNB(@Optional("UT") String state) {

		pas12922_UpdateWildfireScoreNB();
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test Update Wildfire Score HO6 - Endorsement
	 * @scenario
	 * 1. Sign in with privileged User.
	 * 2. Create Customer.
	 * 3. Create Home Owners Policy. Endorse Policy.
	 * 4. Navigate to Reports Tab.
	 * 5. Save Wildfire Score Value.
	 * 6. Submit Tab.
	 * 7. Navigate to Property Info Tab.
	 * 8. Assert That Wildfire Score field is enabled (editable).
	 * 9. Update Wildfire Score Value.
	 * 10. Navigate To reports Tab.
	 * 11. Assert That Wildfire Score Value did not change.
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Test Update Wildfire Score HO6 - Endorsement")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-12922")
	public void pas12922_UpdateWildfireScoreEndorsement(@Optional("UT") String state) {

		pas12922_UpdateWildfireScoreEndorsement();
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test Update Wildfire Score HO6 - Renewal
	 * @scenario
	 * 1. Sign in with privileged User.
	 * 2. Create Customer.
	 * 3. Create Home Owners Policy. Renew Policy.
	 * 4. Navigate to Reports Tab.
	 * 5. Save Wildfire Score Value.
	 * 6. Submit Tab.
	 * 7. Navigate to Property Info Tab.
	 * 8. Assert That Wildfire Score field is enabled (editable).
	 * 9. Update Wildfire Score Value.
	 * 10. Navigate To reports Tab.
	 * 11. Assert That Wildfire Score Value did not change.
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Test Update Wildfire Score HO6 - Renewal")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-12922")
	public void pas12922_UpdateWildfireScoreRenewal(@Optional("UT") String state) {

		pas12922_UpdateWildfireScoreRenewal();
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test Update Wildfire Score HO6 - Renewal Manual Entry
	 * @scenario
	 * 1. Sign in with privileged User.
	 * 2. Create Customer.
	 * 3. Initiate Home Owners Renewal Entry.
	 * 4. Fill Quote up to Reports Tab.
	 * 5. Save Wildfire Score Value.
	 * 6. Submit Tab.
	 * 7. Fill Property Info Tab.
	 * 8. Assert That Wildfire Score field is enabled (editable).
	 * 9. Update Wildfire Score Value.
	 * 10. Navigate To reports Tab.
	 * 11. Assert That Wildfire Score Value did not change.
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Test Update Wildfire Score HO6 - Renewal Manual Entry")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-12922")
	public void pas12922_UpdateWildfireScoreManualEntry(@Optional("UT") String state) {

		pas12922_UpdateWildfireScoreManualEntry();
	}

}