package aaa.modules.regression.billing_and_payments.auto_ca.select.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.billing_and_payments.template.functional.TestPremiumAndMinDueAfterRPTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestPremiumAndMinDueAfterRP extends TestPremiumAndMinDueAfterRPTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}


    /**
     * @author Reda Kazlauskiene
	 * <b> Test CA Renewal Premium and Minimum Due to be accurately updated after an Return Premium endorsement </b>
	 * <p> Steps:
	 * <p> 1. Create new Customer;
	 * <p> 2. Create CA Auto Select Policy: Monthly or Annual payment plan
	 * <p> 3. Create Renewal proposal at R-35
	 * <p> 4. Create RP Endorsement for CURRENT TERM by reducing coverages
	 * <p> 5. Navigate to Billing Account and review changes
	 * <p> 6. Verify that the credit balance is not transferred automatically
	 * <p> 7. Verify that first Offer is declined and New offer is created
	 *
     */
    @Parameters({"state"})
    @StateList(states = {Constants.States.CA})
    @Test(groups = { Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT })
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_CA_SELECT, testCaseId = "PAS-13762")
    public void testPremiumAndMinDueAfterRPForCurrentTerm(@Optional("CA") String state) {
		checkMinDueForCurrentTerm();
    }

    /**
     * @author Reda Kazlauskiene
	 * <b> Test CA Renewal Premium and Minimum Due to be accurately updated after an Return Premium endorsement </b>
	 * <p> Steps:
	 * <p> 1. Create new Customer;
	 * <p> 2. Create CA Auto Select Policy: Monthly or Annual payment plan
	 * <p> 3. Create Renewal proposal at R-35
	 * <p> 4. Create RP Endorsement for RENEWAL TERM by reducing coverages
	 * <p> 5. Navigate to Billing Account and review changes
	 * <p> 6. Verify that first Offer is declined and New offer is created
	 *
     */
    @Parameters({"state"})
    @StateList(states = {Constants.States.CA})
    @Test(groups = { Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT })
    @TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_CA_SELECT, testCaseId = "PAS-13762")
    public void testPremiumAndMinDueAfterRPForRenewalTerm(@Optional("CA") String state) {
		checkMinDueForRenewalTerm();
    }

}
