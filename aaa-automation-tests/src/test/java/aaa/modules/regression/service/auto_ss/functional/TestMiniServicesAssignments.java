package aaa.modules.regression.service.auto_ss.functional;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestMiniServicesAssignmentsHelper;
import toolkit.utils.TestInfo;

public class TestMiniServicesAssignments extends TestMiniServicesAssignmentsHelper {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	/**
	 * @author Megha Gubbala
	 * Create a policy with 2 driver and 2 vehicle
	 * Create pended endorsement using DXP
	 * For 2 driver and 1 vehicle check primary response
	 * Add vehicle Using DXP
	 * Hit driver assignement service to verify unassigned response
	 * Pas go to assign page and save
	 * Hit driver assignement service to verify unassigned response
	 * Verify Occasional Satatus
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-10484"})
	public void pas10484_ViewDriverAssignment(@Optional("VA") String state) {

		pas10484_ViewDriverAssignmentService(getPolicyType());
	}

	/**
	 * @author Megha Gubbala
	 * Create a policy with 1 driver and 1 vehicle
	 * Create pended endorsement using DXP
	 * Add vehicle Using DXP
	 * Hit driver assignement service to verify Response
	 * Pas go to assign page and get information
	 * Hit driver assignement service to verify 1 driver is assigned to both vehicle
	 * Verify primary for first vehicle and ocasional for 2nd vehicle
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-11633"})
	public void pas11633_ViewDriverAssignmentAutoAssign(@Optional("VA") String state) {

		pas11633_ViewDriverAssignmentAutoAssignService(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Update driver assignment, rule D=V
	 * @scenario 1. Create a policy with V1 and D1, D2.
	 * 2. Hit view driver assignment service. Get all info.
	 * 3. Add V2.
	 * 4. Hit DA update service.
	 * a) V2-->D1 and check response (V2-->D1, V1-->D2)
	 * b) Update , V1-->D1 (V1-->D1, V2-->Unn)
	 * c) Update V2-->D2 (V1-->D1, V2-->D2)
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13994"})
	public void pas13994_UpdateDriverAssignmentServiceRule1(@Optional("VA") String state) {

		pas13994_UpdateDriverAssignmentServiceRule1Body(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Update driver assignment, rule V>D
	 * @scenario 1. Create a policy with 3V and 2D
	 * 2. Hit view driver assignment service. Get all info.
	 * 3. Add V4.
	 * 4. Hit view vehicle service, get all info.
	 * 4. Hit DA update service:
	 * a) V4-->D1 (D1-->V1,V3,V4, D2-->V2)
	 * b) V4-->D2 (D1-->V1,V3 D2-->V2,V4)
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13994"})
	public void pas13994_UpdateDriverAssignmentServiceRule2(@Optional("VA") String state) {

		pas13994_UpdateDriverAssignmentServiceRule2Body1(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Update driver assignment, rule V<D
	 * @scenario 1. Create a policy with 2V and 4D
	 * 2. Hit view driver assignment service. Get all info.
	 * 3. Add one more vehicle V3.
	 * 4. Hit view vehicle service, get all info.
	 * 4. Hit DA update service:
	 * a) V3-->D1 (V1-->D2, V2-->D3,D4, V3-->D1)
	 * b) V2-->D2 (V1-->D2, V2-->D3,D4,D2 V3-->D1)
	 * c) V2-->D1 (V1-->Unn, V2-->D3,D4,D2,D1 V3-->Unn)
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13994"})
	public void pas13994_UpdateDriverAssignmentServiceRule3(@Optional("VA") String state) {

	//	pas13994_UpdateDriverAssignmentServiceRule3Body(getPolicyType());
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-11684"})
	public void pas11684_DriverAssignmentExistsForState(@Optional("UT") String state) {
		assertSoftly(softly ->
				pas11684_DriverAssignmentExistsForStateBody(state, softly)
		);
	}
}


