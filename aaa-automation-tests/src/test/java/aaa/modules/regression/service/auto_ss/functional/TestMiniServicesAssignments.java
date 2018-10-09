package aaa.modules.regression.service.auto_ss.functional;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
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
	 * @author Maris Strazds
	 * @name Driver Assignment and Adding a driver. This test is similar to pas10484_ViewDriverAssignment, but also new driver is added
	 * 1. Create a policy with 2 driver and 1 vehicle
	 * 2. Create pended endorsement using DXP
	 * 3. Add new driver
	 * 4. For 3 driver and 1 vehicle check primary response
	 * 5. Add vehicle Using DXP
	 * 6. Hit driver assignment service to verify unassigned response. Verify that new driver is available for assignment.
	 * 7. PAS go to assign page and save
	 * 8. Hit driver assignment service to verify unassigned response. Verify that new driver is available for assignment.
	 * 9. Verify Occasional Status
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14477, PAS-10484"})
	public void pas14477_ViewDriverAssignment_NewDriver(@Optional("VA") String state) {

		pas14477_ViewDriverAssignment_NewDriver_Body(getPolicyType());
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
	 * @scenario Nr1
	 * 1. Create a policy with V1 and D2.
	 * 2. Hit view driver assignment service. Get all info.
	 * 3. Add one more vehicle V2.
	 * 4. Hit view vehicle service, get all info.
	 * 5. Hit DA update service, Rate after, check response.
	 * a) Update: V2-->D1, Check V2-->D1, V1-->D2
	 * b) Update: V1-->D1, D2, Check V1-->D1,D2, V2-->Unn
	 * c) Update: V2-->D2, V1-->D1, V2-->D2
	 * 6. Rate and Bind. Check if endorsement doesn't exist.
	 *
	 * @scenario Nr2
	 * 1. Create a policy with V2 and D4.
	 * 2. Hit view driver assignment service. Get all info.
	 * 3. Add one more vehicle V2.
	 * 4. Hit view vehicle service, get all info.
	 * 5. Hit DA update service, Rate after, check response.
	 * a) Update: V3-->D1, Check V1-->D2 V2-->D3,D4, V3-->D1, V4-->Unn
	 * b) Update: V4-->D2, Check V1-->Unn, V2-->D4,D3 V3-->D1, V4-->D2
	 * c) Update: V2-->D3, Check V1-->Unn, V2-->D3, V3-->D1, V4-->D2, D4-->Unn
	 * d) Update: V1-->D3, Check V1-->D3, V2-->Unn, V3-->D1, V4-->D2, D4-->Unn
	 * e) Update: V2-->D4, Check V1-->D3, V2-->D4, V3-->D1, V4-->D2
	 * 6. Rate and Bind. Check if endorsement doesn't exist.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13994", "PAS-14699", "PAS-15529"})
	public void pas13994_UpdateDriverAssignmentServiceRule1(@Optional("VA") String state) {

		pas13994_UpdateDriverAssignmentServiceRule1Body1(getPolicyType());
		pas15529_UpdateDriverAssignmentServiceRule1Body2(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Update driver assignment, rule V>D
	 * @scenario Nr1
	 * 1. Create a policy with V2 and D4.
	 * 2. Hit view driver assignment service. Get all info.
	 * 3. Add one more vehicle V2.
	 * 4. Hit view vehicle service, get all info.
	 * 5. Hit DA update service, Rate after, check response.
	 * a) Update: Update: V4-->D1, Check V1-->D1, V2-->D2, V3-->D1, V4-->D1
	 * b) Update V2-->D1 Check V1-->D1, V2-->D1, V3-->D1, V4-->D1
	 * c) Update add: V2-->D2, Check V1-->D1, V2-->D2, V3-->D1, V4-->D1
	 * 6. Rate and Bind. Check if endorsement doesn't exist.
	 * @scenario Nr2
	 * 1. Create a policy with V3 and D4.
	 * 2. Hit view driver assignment service. Get all info.
	 * 3. Add two more vehicles.
	 * 4. Hit view vehicle service, get all info.
	 * 5. Hit DA update service, Rate after, check response.
	 * a) Update V4-->D1 AND V5-->D2 (V1-->D1, V2-->D2, V3-->D3, V4-->D1, V5-->D2)
	 * b) Update D1-->All V, (V1-->D1, V2-->D1,  V3-->D1, V4-->D1, V5-->D1, D2,D3-->Unn)
	 * c) Update V1,V2-->D2, V3,V4,V5-->D3, (V1-->D2, V2-->D2,  V3-->D3, V4-->D3, V5-->D3, D1 -->Unn)
	 * d) Update V5-->D1, (V1-->D2, V2-->D2,  V3-->D3, V4-->D3, V5-->D1)
	 * 6. Rate and Bind. Check if endorsement doesn't exist.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13994", "PAS-14699", "PAS-15529"})
	public void pas13994_UpdateDriverAssignmentServiceRule2(@Optional("VA") String state) {

		pas13994_UpdateDriverAssignmentServiceRule2Body1(getPolicyType());
		pas13994_UpdateDriverAssignmentServiceRule2Body2(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Update driver assignment, rule V<D
	 * @scenario 1. Create a policy with 2V and 4D
	 * 2. Hit view driver assignment service. Get all info.
	 * 3. Add one more vehicle V3.
	 * 4. Hit view vehicle service, get all info.
	 * 5. Hit DA update service, Rate after, check response.
	 * a) Update: V3-->D1, Check V1-->D2, V2-->D3, D4, V3-->D1
	 * b) Update: V2-->D2, Check V1-->Unn, V2-->D2, V3-->D1, D3,D4-->Unn
	 * c) Update: V1-->D3,D4,D2 Check V2-->Unn, V3-->D1, V1-->D3,D4,D2
	 * d) Update: V2-->D3 Check V2-->D3, V3-->D1, V1-->D4,D2
	 * 6. Rate and Bind. Check if endorsement doesn't exist.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13994", "PAS-15529", "PAS-15253"})
	public void pas13994_UpdateDriverAssignmentServiceRule3(@Optional("VA") String state) {

		pas13994_UpdateDriverAssignmentServiceRule3Body(getPolicyType());
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-11684"})
	public void pas11684_DriverAssignmentExistsForState(@Optional("UT") String state) {
		assertSoftly(softly ->
				pas11684_DriverAssignmentExistsForStateBody(state, softly)
		);
	}

	/**
	 * @author Megha Gubbala
	 * @name Driver assignment and transaction history
	 * @scenario 1. Create a policy with 1V and 1D
	 * 2.create endorsement
	 * 3.Hit viewEndorsementChangeLog.
	 * 4Verify if transaction history shows change type added and driver assignment.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14539"})
	public void pas14539_transactionInfoDriverAssignment(@Optional("VA") String state) {

		pas14539_transactionInfoDriverAssignmentBody();
	}

	/**
	 * @author Megha Gubbala
	 * @name Driver assignment and transaction history
	 * @scenario 1. Create a policy with 2V and 2D
	 * 2.create endorsement
	 * 3.Hit viewEndorsementChangeLog.
	 * 4.Verify if transaction history shows change type added and driver assignment.
	 * 5.change the driver assignment
	 * 6. then run change log service again
	 * 7. verify new driver assignment added and existing is removed
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14539,PAS-16589"})
	public void pas14539_transactionInfoUpdateDriverAssignment(@Optional("VA") String state) {

		pas14539_transactionInfoUpdateDriverAssignmentBody(getPolicyType());
	}

	/**
	 * @author Megha Gubbala, Maris Strazds
	 * @name Remove driver assigned to trailer
	 * @scenario
	 * 1. Create a policy in PAS with Trailer, Motor Home and Golf Cart (Golf Cart applicable only for AZ) assigned to not FNI
	 * 2. Create an endorsement through service
	 * 3. Remove the driver that is assigned to Trailer, Motor Home and Golf Cart (Golf Cart applicable only for AZ)
	 * 4. Open Endorsement in Inquiry mode and validate that Trailer, Motor Home and Golf Cart (Golf Cart applicable only for AZ) are assigned to FNI
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15505"})
	public void pas15505_RemoveDriverAssignedToTrailerMotorHomeGolfCart(@Optional("NJ") String state) {
		pas15505_RemoveDriverAssignedToTrailerMotorHomeGolfCartBody(getPolicyType());
		//NOTE: This test works for all Auto SS states
	}
}
