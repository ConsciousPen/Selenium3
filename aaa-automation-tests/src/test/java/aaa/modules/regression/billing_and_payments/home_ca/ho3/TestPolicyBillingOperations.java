package aaa.modules.regression.billing_and_payments.home_ca.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.billing_and_payments.template.PolicyBillingOperations;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestPolicyBillingOperations extends PolicyBillingOperations {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_CA_HO3;
    }

    /**
     * @author Jurij Kuznecov
     * <b> Test CAH Policy Manual Fee Adjustment </b>
     * <p> Steps:
     * <p> 1.  Create new or open existent Customer
     * <p> 2.  Create a new HO3 policy
     * <p> 3.  Make a positive fee
     * <p> 4.  Check fee transaction appears in "Payments&Other Transactions"
     * <p> 5.  Check total amount due is increased on fee amount
     * <p> 6.  Check minimum due doesn't change
     * <p> 7.  Waive the fee transaction
     * <p> 8.  Check waive transaction appears in "Payments&Other Transactions"
     * <p> 9.  Check waive link isn't present in the fee transaction row
     * <p> 10. Check total amount due is decreased by fee amount
     * <p> 11. Check minimum due doesn't change
     */

    @Parameters({"state"})
    @StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_CA_HO3)
    public void testManualFeeAdjustment(@Optional("CA") String state) {
        testManualFeeAdjustment();
    }

    /**
     * @author Jurij Kuznecov
     * <b> Test CAH Policy Manual Refund </b>
     * <p> Steps:
     * <p> 1.  Create new or open existent Customer
     * <p> 2.  Create a new HO3 policy
     * <p> 3.  Make a payment 1000$
     * <p> 4.  Check for an error message if Refund Amount is empty
     * <p> 5.  Check for an error message if Refund Amount > Total Paid Amount
     * <p> 6.  Check for an error message if Refund Amount is "0"
     * <p> 7.  Make a refund of 1000$
     * <p> 8.  Check presence of the refund transaction in Pending transactions on billing tab
     * <p> 9.  Check that System creates an Approval task for the Refund transaction
     * <p> 10. Approve the refund transaction
     * <p> 11. Check presence of the refund transaction in Payments & Other Transactions on billing tab
     * <p> 12. Check Total Paid Amount value after refunding
     */

    @Parameters({"state"})
    @StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_CA_HO3)
    public void testManualRefund(@Optional("CA") String state) {
        testManualRefund();
    }

    /**
     * @author Jurij Kuznecov
     * <b> Test CAH Policy Manual Write-Off </b>
     * <p> Steps:
     * <p> 1.  Create new or open existent Customer
     * <p> 2.  Create a new HO3 policy
     * <p> 3.  Write Off 100$
     * <p> 4.  Check write-off transaction appears in "Payments and other transactions" section on billing tab
     * <p> 5.  Reversal Write Off 100$
     * <p> 6.  Check reversal write-off transaction appears in "Payments and other transactions" section on billing tab
     * <p> 7.  Check Total Due value after write-off/reversal write-off
     * <p>  // Manual Adjustment(Advanced Allocations)
     * <p> 8.  Enter payments amounts > Sub Total in advanced allocation dialog
     * <p> 9.  Check error appears
     * <p> 10. Make a positive adjustment using advanced allocation dialog
     * <p> 11. Check that System defaults 'Total Amount' with the value entered by user in 'Amount' field on 'Other Transactions' tab
     * <p> 12. Check that System defaults 'Product Sub total' with the value entered by user in 'Amount' field on 'Other Transactions' tab
     * <p> 13. Check positive adjustment transaction appears in "Payments and other transactions" section on billing tab
     * <p> 14. Check Total Due value is increased
     * <p> 15. Check Minimum Due Amount doesn't change
     * <p> 16. Make a negative adjustment using advanced allocation dialog
     * <p> 17. Check negative adjustment transaction appears in "Payments and other transactions" section on billing tab
     * <p> 18. Check Total Due value is decreased
     * <p> 19. Check Minimum Due Amount doesn't change
     */

    @Parameters({"state"})
    @StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_CA_HO3)
    public void testManualWriteOff(@Optional("CA") String state) {
        testManualWriteOff();
    }

    /**
     * @author Jurij Kuznecov
     * <b> Test CAH Policy Manual Returned Payments </b>
     * <p> Steps:
     * <p> 1.  Create a policy
     * <p> 2.  Add 4 payment methods(Cash, Check, Credit Card, EFT)
     * <p> 3.  Make 5 different payments(Cash, Check, Credit Card, EFT)
     * <p> 4.  Decline Credit Card payment, with reason "NSF fee - with restriction"
     * <p> 5.  Check Payment Decline Transaction appears in Payment and Other Transaction section
     * <p> 6.  Check Fee Transaction appears in Payment and Other Transaction section
     * <p> 7.  Check status of the payment transaction changes to "Decline"
     * <p> 8.  Check Total Due is increased
     * <p> 9.  Decline EFT payment, with reason "NSF fee - without restriction"
     * <p> 10. Check Payment Decline Transaction appears in Payment and Other Transaction section
     * <p> 11. Check Fee Transaction appears in Payment and Other Transaction section
     * <p> 12. Check status of the payment transaction changes to "Decline"
     * <p> 13. Check Total Due is increased
     * <p> 14. Decline Check payment, with reason "No Fee + No Restriction"
     * <p> 15. Check Payment Decline Transaction appears in Payment and Other Transaction section
     * <p> 16. Check status of the payment transaction changes to "Decline"
     * <p> 17. Check Total Due is increased
     * <p> 18. Decline Check payment, with reason "No Fee + No Restriction + No Letter"
     * <p> 19. Check Payment Decline Transaction appears in Payment and Other Transaction section
     * <p> 20. Check status of the payment transaction changes to "Decline"
     * <p> 21. Check Total Due is increased
     * <p> 22. Decline Cash payment.
     * <p> 23. Check Payment Decline Transaction appears in Payment and Other Transaction section.
     * <p> 24. Check status of the payment transaction changes to "Decline"
     * <p> 25. Check Total Due is increased.
     * <p> 26. Make a payment = Deposit amount + 2 * Fee Amount
     * <p>     (We add Fee Amount to compensate Fees due that was generated before)
     * <p> 27. Decline Deposit payment.
     * <p> 28. Check original installments dues stays the same(As it was on step 25)
     * <p> 29. Check that Prepaid is decreased.
     */

    @Parameters({"state"})
    @StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_CA_HO3)
    public void testManualReturnedPayments(@Optional("CA") String state) {
        testManualReturnedPayments();
    }
}
