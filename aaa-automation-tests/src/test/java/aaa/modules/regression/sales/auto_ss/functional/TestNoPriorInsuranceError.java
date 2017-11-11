package aaa.modules.regression.sales.auto_ss.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.regression.sales.template.functional.TestNoPriorInsuranceErrorTemplate;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestNoPriorInsuranceError extends TestNoPriorInsuranceErrorTemplate {

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-3805 New Business DE: No Prior Insurance Message
	 * PAS-4244 New Business DE: No Prior Insurance Error
	 * @name Test No Prior Insurance Error and Message presence
	 * @scenario 0. Create customer
	 * 1. Initiate Auto SS quote creation and make it ready for purchase
	 * 2. Go to the GeneralTab and Change current carrier section to trigger error
	 * 3. Verify warning message presence
	 * 4. Navigate to Driver Activity Reports tab
	 * 5. Verify Error message presence
	 * 6. Return current carrier section to the default state
	 * 7. Purchase Reports at the DAR tab
	 * 8. Go to the GeneralTab and Change current carrier section to trigger error
	 * 9. Calculate Premium and purchase quote
	 * 10. Verify Error message presence
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-4244")
	public void testErrorMessagePresenceDE(@Optional("DE") String state) {
		super.verifyNoPriorInsuranceErrorDARTab();
	}

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-3805 New Business NJ: No Prior Insurance Message
	 * PAS-4244 New Business NJ: No Prior Insurance Error
	 * @name Test No Prior Insurance Error and Message presence
	 * @scenario 0. Create customer
	 * 1. Initiate Auto SS quote creation and make it ready for purchase
	 * 2. Go to the GeneralTab and Change current carrier section to trigger error
	 * 3. Verify warning message presence
	 * 4. Navigate to Driver Activity Reports tab
	 * 5. Verify Error message presence
	 * 6. Return current carrier section to the default state
	 * 7. Purchase Reports at the DAR tab
	 * 8. Go to the GeneralTab and Change current carrier section to trigger error
	 * 9. Calculate Premium and purchase quote
	 * 10. Verify Error message presence
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-4244")
	public void testErrorMessagePresenceNJ(@Optional("NJ") String state) {
		super.verifyNoPriorInsuranceErrorDARTab();
	}

}
