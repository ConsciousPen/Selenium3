package aaa.modules.regression.sales.home_ss.ho4.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestZeroClaimsDiscountTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(statesExcept = Constants.States.CA)
public class TestZeroClaimsDiscount extends TestZeroClaimsDiscountTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO4;
	}

	/**
	 * @author Josh Carpenter
	 * @name Test Zero Claims Discount for SS HO4 Quotes
	 * @scenario
	 * 1. Create customer
	 * 2. Initiate SS HO4 quote
	 * 3. Fill up to P & C tab with default data (No claims present on Property Info Tab)
	 * 4. Validate the zero claims discount is present and capture the premium amount
	 * 5. Navigate to Property Info tab and add a claim:
	 * 		a. Date of Loss = 1 year ago today
	 * 		b. Cause of Loss = Liability
	 * 		c. Amount of Loss = $1,001
	 * 		d. Claim Status = Closed
	 * 		e. AAA Claim = Yes
	 * 		f. Catastrophe = No
	 * 6. Navigate to P & C tab, calculate premium
	 * 7. Validate no zero claims discount is present and premium has increased
	 * 8. Change claim to 'Open' and validate discount/premium is added/decreased
	 * 9. Change claim to back to 'Closed' and validate discount/premium is removed/increased
	 * 10. Change claim to 'Subrogation' and validate discount/premium is added/decreased
	 * 11. Change claim to back to 'Closed' and validate discount/premium is removed/increased
	 * 12. Change claim Date to more than 3 years ago and validate discount/premium is added/decreased
	 * 13. Change date back to 1 year ago and validate discount/premium is removed/increased
	 * 14. Change claim amount to less than $1000 and validate discount/premium is added/decreased
	 * @details
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Test Zero Claims Discount for SS HO4 Quote")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4, testCaseId = "PAS-9088")
	public void pas9088_testZeroClaimsDiscountHO4Quote(@Optional("") String state) {
		pas9088_testZeroClaimsDiscountQuote();
	}

	/**
	 * @author Josh Carpenter
	 * @name Test Zero Claims Discount for SS HO4 Renewals
	 * @scenario
	 * 1. Create customer
	 * 2. Create SS HO4 Policy
	 * 3. Create renewal image and navigate to P & C tab and calculate premium
	 * 4. Validate the zero claims discount is present and capture the premium amount
	 * 5. Navigate to Property Info tab and add a claim:
	 * 		a. Date of Loss = 1 year ago today
	 * 		b. Cause of Loss = Liability
	 * 		c. Amount of Loss = $1,001
	 * 		d. Claim Status = Closed
	 * 		e. AAA Claim = Yes
	 * 		f. Catastrophe = No
	 * 6. Navigate to P & C tab, calculate premium
	 * 7. Validate no zero claims discount is present and premium has increased
	 * 8. Change claim to 'Open' and validate discount/premium is added/decreased
	 * 9. Change claim to back to 'Closed' and validate discount/premium is removed/increased
	 * 10. Change claim to 'Subrogation' and validate discount/premium is added/decreased
	 * 11. Change claim to back to 'Closed' and validate discount/premium is removed/increased
	 * 12. Change claim Date to more than 3 years ago and validate discount/premium is added/decreased
	 * 13. Change date back to 1 year ago and validate discount/premium is removed/increased
	 * 14. Change claim amount to less than $1000 and validate discount/premium is added/decreased
	 * @details
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Test Zero Claims Discount for SS HO4 Renewals")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4, testCaseId = "PAS-9088")
	public void pas9088_testZeroClaimsDiscountHO4Renewal(@Optional("") String state) {
		pas9088_testZeroClaimsDiscountRenewal();
	}
}

