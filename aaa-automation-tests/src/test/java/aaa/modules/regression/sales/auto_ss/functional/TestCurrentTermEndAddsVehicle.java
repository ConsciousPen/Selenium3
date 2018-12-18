package aaa.modules.regression.sales.auto_ss.functional;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestCurrentTermEndAddsVehicleSSTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;
import toolkit.verification.ETCSCoreSoftAssertions;

@StateList(states = Constants.States.AZ)
public class TestCurrentTermEndAddsVehicle extends TestCurrentTermEndAddsVehicleSSTemplate {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	/**
	 * * @author Kiruthika Rajendran/Sarunas Jaraminas
	 *
	 * @name Current Term End Adds Vehicle:
	 * Make refresh correct for current and renewal terms
	 * @scenario 1
	 * 1. Create Auto SS Quote with two vehicles: First Vehicle - VIN MATCHED, Second Vehicle - VIN MATCHED
	 * 2. Make policy status - Proposed
	 * 3. Initiate Endorsement
	 * 4. Update VIN number for first Vehicle to VIN NOT MATCHED
	 * 5. Add third Vehicle
	 * 6. Calculate Premium and bind the endorsement
	 * 7. Open the last renewal inscription in 'Transaction history'
	 * Expected Result:
	 * The First Vehicle - updated according to 4th step VIN details
	 * The second Vehicle - NOT updated will not change/not refresh
	 * The third Vehicle - displayed new data according to version
	 * @details
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.AZ})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-16150")
	//PAS-16150 and PAS-14532 carry same scenarios
	public void pas14532_refreshForCurrentAndRenewalTermsVinNotMatched(@Optional("AZ") String state) {
		pas14532_refreshForCurrentAndRenewalTerms(NOT_MATCHED);
	}

	/**
	 * * @author Kiruthika Rajendran
	 *
	 * @name Current Term End Adds Vehicle:
	 * Make refresh correct for current and renewal terms
	 * @scenario 2
	 * 1. Create Auto SS Quote with two vehicles: First Vehicle - VIN MATCHED, Second Vehicle - VIN MATCHED
	 * 2. Make policy status - Proposed
	 * 3. Initiate Endorsement
	 * 4. Update VIN number for second Vehicle to VIN MATCHED
	 * 5. Add third Vehicle
	 * 6. Calculate Premium and bind the endorsement
	 * 7. Open the last renewal inscription in 'Transaction history'
	 * Expected Result:
	 * The First Vehicle - updated according to 4th step VIN details
	 * The second Vehicle - NOT updated will not change/not refresh
	 * The third Vehicle - displayed new data according to version
	 * @details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-16150")
	//PAS-16150 and PAS-14532 carry same scenarios
	public void pas14532_refreshForCurrentAndRenewalTermsVinMatched(@Optional("AZ") String state) {
		pas14532_refreshForCurrentAndRenewalTerms(MATCHED);
	}

	/**
	 * * @author Kiruthika Rajendran
	 *
	 * @name Current Term End Adds Vehicle:
	 * Make refresh correct for current and renewal terms
	 * @scenario 3
	 * 1. Create Auto SS Quote with two vehicles: First Vehicle - VIN MATCHED, Second Vehicle - VIN NOT MATCHED
	 * 2. Make policy status - Proposed
	 * 3. Initiate Endorsement
	 * 4. Check if new VIN stub exist.
	 * 5. Update y/m/m/s/s for a Vehicle details.
	 * 6. Add third Vehicle
	 * 7. Calculate Premium and bind the endorsement
	 * 8. Open the last renewal inscription in 'Transaction history'
	 * Expected Result:
	 * The First Vehicle - NOT updated will not change/not refresh;
	 * The second Vehicle - updated according to 4th step VIN details;
	 * The third Vehicle - displayed new data according to version;
	 * @details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-16150")
	//PAS-16150 and PAS-14532 carry same scenarios
	public void pas14532_refreshForCurrentAndRenewalTermsVinStubUpdate(@Optional("AZ") String state) {
		pas14532_refreshForCurrentAndRenewalTerms(STUB);
	}

	public void pas14532_refreshForCurrentAndRenewalTerms(String scenario) {
		pas14532_refreshForCurrentAndRenewalTerms_initiateEndorsement(scenario);
		// bind endorsement with the updated test data of three vehicles
		pas14532_refreshForCurrentAndRenewalTerms_bindEndorsement(scenario);
		//7. Verify Latest Renewal Version has correct vehicle details
		viewRatingDetails();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		if (scenario.equals(NOT_MATCHED)) { //Assertion for scenario 1
			// The First Vehicle - Displays Updated/refreshed data according to version;
			doSoftAssertions(softly, 2, "HYUNDAI MOTOR", "12", "17");
			doSoftAssertions(softly, 3, "TOYOTA MOTOR", "20", "30");
			//corresponding to PAS-18969 data should not be refreshed for VAN vehicles. 
			//doSoftAssertions(softly, 4, "FORD MOTOR", "25", "37");
			doSoftAssertions(softly, 4, "FORD", "13", "13");
		} else if (scenario.equals(MATCHED)) { //Assertion for scenario 2
			// The second Vehicle - NOT updated will not change/not refresh;
			doSoftAssertions(softly, 2, "KIA MOTOR", "20", "10");
			doSoftAssertions(softly, 3, "TOYOTA MOTOR", "20", "30");
			//doSoftAssertions(softly, 4, "FORD MOTOR", "25", "37");
			doSoftAssertions(softly, 4, "FORD", "13", "13");
		} else if (scenario.equals(STUB)) { //Assertion for scenario 3
			// The third Vehicle - displayed updated/refreshed data according to version;
			doSoftAssertions(softly, 2, "KIA MOTOR", "20", "10");
			doSoftAssertions(softly, 3, "BMW MOTOR", "12", "12");
			//doSoftAssertions(softly, 4, "FORD MOTOR", "25", "37");
			doSoftAssertions(softly, 4, "FORD", "13", "13");
		}
		softly.close();
		closeRatingDetails();
	}


	/**
	 * * @author Kiruthika Rajendran
	 *
	 * @name Current Term End Adds MSRP Vehicle:
	 * Make refresh correct for current and renewal terms
	 * @scenario
	 * 1. Create Auto SS Quote with two vehicles: First MSRP Vehicle - VIN NOT MATCH, Second MSRP Vehicle - VIN NOT MATCHED
	 * 2. Make policy status - Proposed
	 * 3. Initiate Endorsement
	 * 4. Update the Stated amount of first Vehicle
	 * 5. Add third MSRP Vehicle
	 * 6. Calculate Premium and bind the endorsement
	 * 7. Open the last renewal inscription in 'Transaction history'
	 * Expected Result:
	 * The First Vehicle - COMP/COLL symbol has to be updated for new version
	 * The second Vehicle - COMP/COLL symbol has to retain the same value
	 * The third Vehicle - COMP/COLL symbol has to be updated for new version
	 * @details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-16522")

	public void pas16522_refreshForMSRPVehicleCurrentAndRenewalTerms(@Optional("AZ") String state) {
		pas16522_refreshForMSRPVehicleCurrentAndRenewalTerms();
	}

	public void pas16522_refreshForMSRPVehicleCurrentAndRenewalTerms() {
		pas16522_refreshForMSRPVehicleCurrentAndRenewalTerms_initiateEndorsement();

		pas16522_refreshForMSRPVehicleCurrentAndRenewalTerms_bindEndorsement();
		viewRatingDetails();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		doSoftAssertions(softly, 2, "TESLA", "318", "334");
		doSoftAssertions(softly, 3, "PORSCHE", "169", "169");
		doSoftAssertions(softly, 4, "AUDI", "295", "223");
		softly.close();
		closeRatingDetails();
	}

	@AfterClass(alwaysRun = true)
	protected void resetDefault() {
		cleanup();
	}
}
