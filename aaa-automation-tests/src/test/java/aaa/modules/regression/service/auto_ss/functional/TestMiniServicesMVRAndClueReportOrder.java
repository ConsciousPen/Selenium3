package aaa.modules.regression.service.auto_ss.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestMiniServicesMVRAndClueReportOrderHelper;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestMiniServicesMVRAndClueReportOrder extends TestMiniServicesMVRAndClueReportOrderHelper {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	/**
	 * @author Maris Strazds
	 * @name Test Report Ordering for Endorsement (Named Insured)
	 * @scenario 1. Create a policy in PAS
	 * 2. Create an endorsement through service
	 * 3. Add Driver 1 (Named Insured) through service with MVR status =  Hit - Activity Found, CLUE status = processing complete, results clear
	 * 4. Run the Report Order Service for MVR/CLUE
	 * 5. Open the Endorsement in PAS, navigate to "Driver Activity Reports" tab and validate that MVR/CLUE reports have been ordered successfully with no errors
	 * 6. Validate that I receive the report response
	 * 		AND it is viewable in PAS (pdf)
	 * 		AND it is reconciled in PAS
	 * 		AND a positive response is provided
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
	 * @scenario 1. Create a policy in PAS
	 * 2. Create an endorsement through service
	 * 3. Add Driver 1 (not a Named Insured) through service with MVR status =  Hit - Activity Found, CLUE status = processing complete, results clear
	 * 4. Run the Report Order Service for MVR/CLUE
	 * 5. Open the Endorsement in PAS, navigate to "Driver Activity Reports" tab and validate that MVR/CLUE reports have been ordered successfully with no errors
	 * 6. Validate that I receive the report response
	 * 		AND it is viewable in PAS (pdf)
	 * 		AND it is reconciled in PAS
	 * 		AND a positive response is provided
	 * 7. Rate and bind the policy
	 * 8. Rate and Bind
	 * 9. Create an endorsement through service
	 * 10. Add Driver 2 (not Named Insured) through service with MVR status =  Clear, CLUE status = processing complete, with results information
	 * 11. Repeat steps 4 - 8
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-16694"})
	public void pas16694_orderReports_not_Named_Insured_endorsement(@Optional("VA") String state) {

		pas16694_orderReports_not_Named_Insured_endorsementBody(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite
	 * @name More than two minor violations, rule 200095
	 * @scenario 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Add driver with 3minor violations <=36months
	 * 4. Rate policy. Order reports for new driver.
	 * 5. Check response. Rate.
	 * 6. Try Bind, check response.
	 * 7. Delete endorsement.
	 * Repeat 1-7 steps with new added drivers:
	 * 1) 2minor violations <=36months.
	 * 2) 3minor violations, 1 is outdated >36months.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15384"})
	public void pas15384_moreThanTwoMinorViolationsError(@Optional("VA") String state) {

		pas15384_moreThanTwoMinorViolationsErrorBody("VA");
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-17676"})
	public void pas17676_moreThanTwoMinorViolationsError(@Optional("MD") String state) {

		pas15384_moreThanTwoMinorViolationsErrorBody("MD");
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Drivers with Narcotics, drug or felony convictions - 200005
	 * @scenario 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Add driver with One Felony
	 * 4. Rate policy. Order reports for new driver.
	 * 5. Check response. Rate.
	 * 6. Try Bind, check response.
	 * 7. Go to PAS, issue endorsement.
	 * 8. Create endorse, update driver from previous endorsement.
	 * 9. Try to rate and bind endorsement.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15371", "PAS-17648"})
	public void pas15371_driversWithNarcoticsDrugOrFelonyConvictionsError(@Optional("VA") String state) {

		pas15371_driversWithNarcoticsDrugOrFelonyConvictionsErrorBody();
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Drivers with more than 20 Points - 200004
	 * @scenario 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Add driver with more than 20 points, <33 months
	 * 4. Rate policy. Order reports for new driver.
	 * 5. Check response. Rate.
	 * 6. Try Bind, check response.
	 * 7. Delete endorsement.
	 * Repeat 1-7 steps with new added drivers:
	 * 1)Driver with 20 points (less than 20 in 33months)
	 * 2)Driver with less than 20 points.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15370"})
	public void pas15370_driverWithMoreThanTwentyPointsError(@Optional("VA") String state) {

		pas15370_driverWithMoreThanTwentyPointsErrorBody();
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Drivers with more than 20 Points - 200004
	 * @scenario 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Add driver with 4 incidents, <36 months
	 * 4. Rate policy. Order reports for new driver.
	 * 5. Check response. Rate.
	 * 6. Try Bind, check response.
	 * 7. Delete endorsement.
	 * Repeat 1-6 steps with new added driver:
	 * 1)Driver with 4 incidents, one is outdated.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15385"})
	public void pas15385_driverWithFourOrMoreIncidentsError(@Optional("VA") String state) {

		pas15385_driverWithFourOrMoreIncidentsErrorBody();
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Driver under the age of 21 years with a DUI is unacceptable
	 * @scenario 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Add driver with: Alcohol-Related Violation
	 * Driving under the Influence of Alcohol
	 * 4. Order reports for new driver.
	 * 5. Check response. Rate.
	 * 6. Try Bind, check response.
	 * 7. Delete endorsement.
	 * Repeat 1-6 steps with new added driver which have:
	 * Violation: DUI, DUD, TLQ,
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15375"})
	public void pas15375_duiIsUnacceptableForDriverUnderTheAgeError(@Optional("VA") String state) {

		pas15375_duiIsUnacceptableForDriverUnderTheAgeErrorBody();
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Drivers with a major violation -<=33 months - 200009
	 * @scenario 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Add driver with one major violation.
	 * 4. Order reports for new driver.
	 * 5. Check response. Rate.
	 * 6. Try Bind, check response.
	 * 7. Delete endorsement.
	 * Repeat with drivers, which have:
	 * FEL, HOM, DR, FLE, HAR, LTS, NGD,
	 * RKD, SUS violations.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15374"})
	public void pas15374_driverWithMajorViolationsError(@Optional("VA") String state) {

		pas15374_driverWithMajorViolationsErrorBody();
	}

	/**
	 * @author Megha Gubbala
	 * @name driver With One Or More Fault Accidents
	 * @scenario 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Add driver with: Accident fault Violation
	 * 4. Order reports for new driver.
	 * 5. Check response. Rate.
	 * 6. Try Bind, check response.
	 * 7. Delete endorsement.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15383"})
	public void pas15383_driverWithOneOrMoreFaultAccidents(@Optional("VA") String state) {

		pas15383_driverWithOneOrMoreFaultAccidentsErrorBody();
	}

	/**
	 * @author Megha Gubbala
	 * @name Report Information and the Conviction Date and driver reports
	 * @scenario 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Add driver with: Accident fault Violation
	 * 4. Verify response on DXP in License status and conviction date as correct
	 * 5. Check Pas And verify if the dates are matching to pas.
	 * 6. Verify driver activity and verify status there.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15369", "PAS-17924"})
	public void pas15369_reportOrderAndDriver(@Optional("VA") String state) {

		pas15369_reportOrderAndDriverBody();
	}

	/**
	 * @author Megha Gubbala
	 * @name Driver Details and the MVR
	 * @scenario 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Add driver with: Name mismatch.
	 * 4. Order reports for new driver.
	 * 5. Check response. to verify Error for name mismatch.
	 * 6. Add one more driver with gender and DOB mismatch.
	 * 7. Verify errors for gender and DOB.
	 * 8. update the driver and correct DOB and Gender.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15372"})
	public void pas15372_driverDetailsAndMvrRulesThatProvided(@Optional("AZ") String state) {

		pas15372_driverDetailsAndMvrRulesThatProvidedBody();
	}

	//Scenario2 For CO
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15369", "PAS-17924"})
	public void pas15369_reportOrderAndDriverCO(@Optional("CO") String state) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		pas15369_reportOrderAndDriverOtherStateBody(policyNumber);
	}

	//Scenario3 For NY
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15369", "PAS-17924"})
	public void pas15369_reportOrderAndDriverNY(@Optional("NY") String state) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		pas15369_reportOrderAndDriverOtherStateBody(policyNumber);
	}

	//Scenario4 For NJ
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15369", "PAS-17924"})
	public void pas15369_reportOrderAndDriverNJ(@Optional("NJ") String state) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		pas15369_reportOrderAndDriverOtherStateBody(policyNumber);
	}

	//Scenario5 For PA
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15369", "PAS-17924"})
	public void pas15369_reportOrderAndDriverPA(@Optional("PA") String state) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		pas15369_reportOrderAndDriverOtherStateBody(policyNumber);
	}

	/**
	 * @author Megha Gubbala
	 * @name Driver Details and the MVR
	 * @scenario 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Add driver with: 3 or more minor violations for speeding
	 * 4. Order reports for new driver.
	 * 5. Check response. to verify Error 200103_C
	 * 6. rate and bind verify rule on bind
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15376"})
	public void pas15376_3OrMoreMinorOrSpeedingViolations(@Optional("AZ") String state) {

		pas15376_3OrMoreMinorOrSpeedingViolationsBody();
	}
}

