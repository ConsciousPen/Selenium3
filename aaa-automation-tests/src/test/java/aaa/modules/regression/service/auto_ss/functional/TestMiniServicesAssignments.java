package aaa.modules.regression.service.auto_ss.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestMiniServicesAssignmentsHelper;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;

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

		pas10484_ViewDriverAssignmentService();
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

		pas14477_ViewDriverAssignment_NewDriver_Body();
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

		pas11633_ViewDriverAssignmentAutoAssignService();
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

		pas13994_UpdateDriverAssignmentServiceRule3Body();
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
	 * 4.Verify if transaction history shows change type added and driver assignment.
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
	 * 5.Change the driver assignment
	 * 6.Then run change log service again
	 * 7.Verify new driver assignment added and existing is removed
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14539,PAS-16589"})
	public void pas14539_transactionInfoUpdateDriverAssignment(@Optional("VA") String state) {

		pas14539_transactionInfoUpdateDriverAssignmentBody();
	}

	/**
	 * @author Megha Gubbala, Maris Strazds
	 * @name Remove driver assigned to trailer, Motor Home, Golf Cart
	 * @scenario
	 * 1. Create a policy in PAS with Trailer, Motor Home and Golf Cart (Golf Cart applicable only for AZ) assigned to not FNI
	 * 2. Create an endorsement through service
	 * 3. Remove the driver that is assigned to Trailer, Motor Home and Golf Cart (Golf Cart applicable only for AZ)
	 * 4. Open Endorsement in Inquiry mode and validate that Trailer, Motor Home and Golf Cart (Golf Cart applicable only for AZ) are assigned to FNI
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15540", "PAS-15505"})
	public void pas15540_RemoveDriverAssignedToTrailer(@Optional("VA") String state) {
		pas15540_RemoveDriverAssignedToTrailerBody(getPolicyType());
		//NOTE: This test works for all Auto SS states
	}

	/**
	 * @author Jovita Pukenaite
	 * @name View driver assignment service after Remove/Add/Replace (Rule V = D)
	 * @scenario 1. Create a policy with 3V and 3D
	 * D1-->V1, D2-->V2, D3-->D3
	 * 2. Create endorsement outside of PAS
	 * 3. Remove V1, V2, D3
	 * 4. Check DA: D1-->V3, D2-->V3 and rate.
	 * 5. Delete old endorsement, create new one.
	 * 6. Delete D2, D3
	 * 7. Check DA: D3-->V1, V2, V3 and rate.
	 * Prepare for other TC when we have 2V and 2D
	 * D1-->V1, D2-->V2
	 * 8. +D3 and +V3
	 * 9. Check DA: D1-->V1, D2-->V2, D3, V3-->Unn
	 * 10. Update: D3-->V3 and rate after.
	 * 11. Delete old endorsement, create new one.
	 * 12. Replace V1.
	 * 13. Check DA: D1-->V1, D2-->V2.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21199"})
	public void pas21199_ViewDriverAssignmentAddRemoveActionsRule1(@Optional("VA") String state) {

		pas21199_ViewDriverAssignmentAddRemoveActionsRule1Body();
	}

	/**
	 * @author Jovita Pukenaite
	 * @name View driver assignment service after Remove/Add/Replace (Rule V > D)
	 * @scenario 1. Create policy with 4V and 2D
	 * V1-->D1, V2-->D1, V3-->D2, V4-->D2
	 * 1. Create endorsement outside of PAS
	 * 2. Remove V2
	 * 3. Check DA: D1-->V1, D2-->V3,V4 and rate.
	 * 4. Delete old endorsement, create new one.
	 * 5. Remove D2
	 * 6. Check DA: D1-->All V and rate.
	 * 7. Delete old endorsement, create new one.
	 * 8. Replace V1, delete V2, add D2
	 * 9. D1-->V1, D2-->V3,V4, D3-->Unn
	 * 10. Update D3-->V4 and rate.
	 * Prepare for next TC.
	 * D1-->V1,V2,V3, D2-->V4,V5,V6
	 * 1. Create endorsement outside of PAS
	 * 2. Remove D2, V2, V3
	 * 3. Check DA: D1-->V1,V4,V5,V6
	 * 4. Rate and Bind.
	 *
	 * @scenario Part2
	 * 1. Create a policy with V2 and D1
	 * 2. Create endorsement outside of PAS
	 * 3. Replace V1, V2, and after add V3
	 * 4. Check DA: D1-->V1,V2,V3 and rate.
	 * 5. Delete old endorsement, create new one.
	 * 6. Add V3 and D2, remove V1
	 * 7. Check DA: D1-->V2,V3; D2-->Unn
	 * 8. Update: D2-->V3, rate and bind.
	 *
	 * @scenario Part3
	 * 1. Create policy with 3D and 4V
	 * V1-->D1, V2-->D2, V3-->D3, V4-->D1
	 * 2. Create endorsement outside of PAS.
	 * 3. Remove V1
	 * 4. Check DA: V2-->D2, V3-->D3, V4-->D1 and rate.
	 * 5. Delete old endorsement, create new one.
	 * 6. Remove V2
	 * 7. Check DA: V1-->D1, V3-->D3, V4-->D1, D2-->Unn
	 * 8. Update D2-->V3, and rate
	 * 9. Delete old endorsement, create new one.
	 * 10. Remove V3
	 * 11. V1-->D1, V2-->D2, V4-->D1, D3-->Unn
	 * 12. Update D3-->V2, and rate
	 * 13. Delete old endorsement, create new one.
	 * 14. Remove V4
	 * 15. Check DA: V1-->D1, V2-->D2, V3-->D3 rate and bind.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21199"})
	public void pas21199_ViewDriverAssignmentAddRemoveActionsRule3(@Optional("VA") String state) {

		pas21199_ViewDriverAssignmentAddRemoveActionsRule3Part1Body();
		pas21199_ViewDriverAssignmentAddRemoveActionsRule3Part2Body();
		pas21199_ViewDriverAssignmentAddRemoveActionsRule3Part3Body();
	}

	/**
	 * @author Jovita Pukenaite
	 * @name View driver assignment service after Remove/Add/Replace (Rule V < D)
	 * @scenario 1. Create policy with 4D and 3V
	 * D1-->V1, D2-->V2, D3-->V2, D4-->V3
	 * 2. Remove D2
	 * 3. Check DA: D1-->V1, D3-->V2, D4-->V3 and rate.
	 * 4. Delete old endorsement, create new one.
	 * 5. Delete V2 and after D3
	 * 6. Check DA: D1-->V1, D2-->Unn, D4-->V3
	 * 7. Update D2-->V1 and rate.
	 * 8. Delete old endorsement, create new one.
	 * 9. Remove V2 after V3
	 * 10. Check DA: V1-->All D
	 * 11. Bind and rate.
	 *
	 * @scenario Part2
	 * 1. Create a policy with V2 and D3
	 * 2. Remove D3 add V3
	 * 3. Check DA: D1-->V1, D2-->V2, V3-->Unn
	 * 4. Update D2-->V3 and rate.
	 * Prepare for the next TC
	 * 1. Policy with 2V and 5D
	 * V1-->D1,D2,D3, V2-->D4,D5
	 * 2. Create endorsement outside of PAS.
	 * 3. Remove D4 and D5
	 * 4. Check DA: V1-->D2,D3 V2-->D5
	 * 5. Bind and rate.
	 *
	 * @scenario Part3
	 * 1. Create policy with 2D and 1V
	 * D1-->V1, D2-->V1
	 * 2. Create endorsement outside of PAS.
	 * 3. Add D3, add D4, add  V2
	 * 4. Check DA: D1-->V1, D2-->V1, D3-->V1, D4-->V1, V2-->Unn
	 * 5. Update D2-->V2, rate.
	 * 6. Delete old endorsement, create new one.
	 * 7. Delete D2 and add V2
	 * 8. Check DA: D1-->V1,V2
	 * 9. Bind and rate.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21199"})
	public void pas21199_ViewDriverAssignmentAddRemoveActionsRule2(@Optional("VA") String state) {

		pas21199_ViewDriverAssignmentAddRemoveActionsRule2Part1Body();
		pas21199_ViewDriverAssignmentAddRemoveActionsRule2Part2Body();
		pas21199_ViewDriverAssignmentAddRemoveActionsRule2Part3Body();
	}
}
