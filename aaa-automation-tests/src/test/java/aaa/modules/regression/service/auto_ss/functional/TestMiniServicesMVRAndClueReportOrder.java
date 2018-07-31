package aaa.modules.regression.service.auto_ss.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestMiniServicesMVRAndClueReportOrderHelper;
import toolkit.utils.TestInfo;

public class TestMiniServicesMVRAndClueReportOrder extends TestMiniServicesMVRAndClueReportOrderHelper {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	/**
	 * @author Maris Strazds
	 * @name Test Report Ordering for Endorsement (Named Insured)
	 * @scenario
	 * 1. Create a policy in PAS
	 * 2. Create an endorsement through service
	 * 3. Add Driver 1 (Named Insured) through service with MVR status =  Hit - Activity Found, CLUE status = processing complete, results clear
	 * 4. Run the Report Order Service for MVR/CLUE
	 * 5. Open the Endorsement in PAS, navigate to "Driver Activity Reports" tab and validate that MVR/CLUE reports have been ordered successfully with no errors
	 * 6. Validate that I receive the report response
	 *          AND it is viewable in PAS (pdf)
	 *          AND it is reconciled in PAS
	 *          AND a positive response is provided
	 * 7. Rate and bind the policy
	 * 8. Rate and Bind
	 * 9. Create an endorsement through service
	 * 10. Add Driver 2 (Named Insured) through service with MVR status =  Clear, CLUE status = processing complete, with results information
	 * 11. Repeat steps 4 - 8
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15077"})
	public void pas15077_orderReports_endorsement(@Optional("VA") String state) {

		pas15077_orderReports_endorsementBody(getPolicyType());
	}

	/**
	 * @author Maris Strazds
	 * @name Test Report Ordering for Endorsement (not a Named Insured)
	 * @scenario
	 * 1. Create a policy in PAS
	 * 2. Create an endorsement through service
	 * 3. Add Driver 1 (not a Named Insured) through service with MVR status =  Hit - Activity Found, CLUE status = processing complete, results clear
	 * 4. Run the Report Order Service for MVR/CLUE
	 * 5. Open the Endorsement in PAS, navigate to "Driver Activity Reports" tab and validate that MVR/CLUE reports have been ordered successfully with no errors
	 * 6. Validate that I receive the report response
	 *          AND it is viewable in PAS (pdf)
	 *          AND it is reconciled in PAS
	 *          AND a positive response is provided
	 * 7. Rate and bind the policy
	 * 8. Rate and Bind
	 * 9. Create an endorsement through service
	 * 10. Add Driver 2 (not Named Insured) through service with MVR status =  Clear, CLUE status = processing complete, with results information
	 * 11. Repeat steps 4 - 8
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-16694"})
	public void pas16694_orderReports_not_Named_Insured_endorsement(@Optional("") String state) {

		pas16694_orderReports_not_Named_Insured_endorsementBody(getPolicyType());
	}


	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15374"})
	public void pas15374_driverWithMajorViolations(@Optional("AZ") String state) {

		pas15374_driverWithMajorViolationsBody(getPolicyType());
	}
}

