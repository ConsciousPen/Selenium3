package aaa.modules.regression.billing_and_payments.home_ca.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.billing_and_payments.template.PolicyBillingOperations;

public class TestPolicyBillingOperations extends PolicyBillingOperations {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_CA_HO3;
    }

    /**
     * @author Jurij Kuznecov
     * @name Test CAH Policy Manual Fee Adjustment
     * @scenario 
     * 1.  Create new or open existent Customer
     * 2.  Create a new HO3 policy
     * 3.  Make a positive fee
     * 4.  Check fee transaction appears in "Payments&Other Transactions"
     * 5.  Check total amount due is increased on fee amount
     * 6.  Check minimum due doesn't change
     * 7.  Waive the fee transaction 
     * 8.  Check waive transaction appears in "Payments&Other Transactions"
     * 9.  Check waive link isn't present in the fee transaction row 
     * 10. Check total amount due is decreased by fee amount
     * 11. Check minimum due doesn't change
     */

    @Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_CA_HO3)
    public void testManualFeeAdjustment(@Optional("CA") String state) {
        super.testManualFeeAdjustment();
    }

    /**
     * @author Jurij Kuznecov
     * @name Test CAH Policy Manual Refund
     * @scenario 
     * 1.  Create new or open existent Customer
     * 2.  Create a new HO3 policy
     * 3.  Make a payment 1000$
     * 4.  Check for an error message if Refund Amount is empty
     * 5.  Check for an error message if Refund Amount > Total Paid Amount
     * 6.  Check for an error message if Refund Amount is "0"
     * 7.  Make a refund of 1000$
     * 8.  Check presence of the refund transaction in Pending transactions on billing tab
     * 9.  Check that System creates an Approval task for the Refund transaction
     * 10. Approve the refund transaction
     * 11. Check presence of the refund transaction in Payments & Other Transactions on billing tab
     * 12. Check Total Paid Amount value after refunding
     */

    @Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_CA_HO3)
    public void testManualRefund(@Optional("CA") String state) {
        super.testManualRefund();
    }

    /**
     * @author Jurij Kuznecov
     * @name Test CAH Policy Manual Write-Off
     * @scenario
     * 1.  Create new or open existent Customer
     * 2.  Create a new HO3 policy
     * 3.  Write Off 100$
     * 4.  Check write-off transaction appears in "Payments and other transactions" section on billing tab
     * 5.  Reversal Write Off 100$ 
     * 6.  Check reversal write-off transaction appears in "Payments and other transactions" section on billing tab
     * 7.  Check Total Due value after write-off/reversal write-off
     *  // Manual Adjustment(Advanced Allocations)
     * 8.  Enter payments amounts > Sub Total in advanced allocation dialog
     * 9.  Check error appears
     * 10. Make a positive adjustment using advanced allocation dialog
     * 11. Check that System defaults 'Total Amount' with the value entered by user in 'Amount' field on 'Other Transactions' tab
     * 12. Check that System defaults 'Product Sub total' with the value entered by user in 'Amount' field on 'Other Transactions' tab
     * 13. Check positive adjustment transaction appears in "Payments and other transactions" section on billing tab
     * 14. Check Total Due value is increased
     * 15. Check Minimum Due Amount doesn't change
     * 16. Make a negative adjustment using advanced allocation dialog
     * 17. Check negative adjustment transaction appears in "Payments and other transactions" section on billing tab
     * 18. Check Total Due value is decreased
     * 19. Check Minimum Due Amount doesn't change
     */

    @Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_CA_HO3)
    public void testManualWriteOff(@Optional("CA") String state) {
        super.testManualWriteOff();
    }

    /**
     * @author Jurij Kuznecov
     * @name Test CAH Policy Manual Returned Payments
     * @scenario
     * 1.  Create a policy
     * 2.  Add 4 payment methods(Cash, Check, Credit Card, EFT)
     * 3.  Make 5 different payments(Cash, Check, Credit Card, EFT)
     * 4.  Decline Credit Card payment, with reason "NSF fee - with restriction"
     * 5.  Check Payment Decline Transaction appears in Payment and Other Transaction section
     * 6.  Check Fee Transaction appears in Payment and Other Transaction section
     * 7.  Check status of the payment transaction changes to "Decline"
     * 8.  Check Total Due is increased
     * 9.  Decline EFT payment, with reason "NSF fee - without restriction"
     * 10. Check Payment Decline Transaction appears in Payment and Other Transaction section
     * 11. Check Fee Transaction appears in Payment and Other Transaction section
     * 12. Check status of the payment transaction changes to "Decline"
     * 13. Check Total Due is increased
     * 14. Decline Check payment, with reason "No Fee + No Restriction"
     * 15. Check Payment Decline Transaction appears in Payment and Other Transaction section
     * 16. Check status of the payment transaction changes to "Decline"
     * 17. Check Total Due is increased
     * 18. Decline Check payment, with reason "No Fee + No Restriction + No Letter"
     * 19. Check Payment Decline Transaction appears in Payment and Other Transaction section
     * 20. Check status of the payment transaction changes to "Decline"
     * 21. Check Total Due is increased
     * 22. Decline Cash payment.
     * 23. Check Payment Decline Transaction appears in Payment and Other Transaction section.
     * 24. Check status of the payment transaction changes to "Decline"
     * 25. Check Total Due is increased.
     * 26. Make a payment = Deposit amount + 2 * Fee Amount 
     *     (We add Fee Amount to compensate Fees due that was generated before)
     * 27. Decline Deposit payment.
     * 28. Check original installments dues stays the same(As it was on step 25)
     * 29. Check that Prepaid is decreased.
     */

    @Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_CA_HO3)
    public void testManualReturnedPayments(@Optional("CA") String state) {
        super.testManualReturnedPayments();
    }
}
