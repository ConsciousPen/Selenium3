package aaa.modules.regression.service.home_ca.ho3;

import static org.junit.Assert.assertTrue;
import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum.AppMainTabs;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionStatus;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionSubtypeReason;
import aaa.main.enums.PolicyConstants.PolicyTransactionHistoryTable;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.ProductConstants.TransactionHistoryType;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions;
import aaa.main.modules.policy.home_ca.actiontabs.CancelActionTab;
import aaa.main.modules.policy.home_ca.actiontabs.ReinstatementActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;

public class TestPolicyCancelReinstateUWReason extends HomeCaHO3BaseTest {

    /**
      * @author Jurij Kuznecov
      * @name Test CAH Policy Cancel Reinstate UW Reason
      * @scenario 
      * 1.  Create new or open existent Customer
      * 2.  Create a new HO3 policy
      * 3.  Start policy Cancellation process
      * 4.  Verify that fields 'Cancellation effective date', 'Cancellation reason' and 'Description' are presented on 'Cancellation' tab
      * 5.  Verify that default value in 'Cancellation effective date' field is today + 1 day
      * 6.  Leave the cancellation date and cancellation reason fields empty, click Ok and verify that error message is appears
      * 7.  Change the cancellation date to other than policy effective date and verify that error message is appears
      * 8.  Change the cancellation date to policy effective date and verify that policy status is 'Policy Cancelled'
      * 9.  Click transaction history link and verify that cancellation transaction appears in transaction history
      * 10. Navigate to Billing page and verify that Transaction is created in Payments & Other Transactions section 
      * 11. Navigate to Policy Summary page and check that policy consolidated screen contains an alert with cancellation reason
      * 12. Start policy Reinstatement process and verify fields 'Cancellation effective date' and 'Reinstate date'
      * 13. Leave the reinstate date field empty, click Ok and verify that error message is appears
      * 14. Fill in the 'Reinstate date' field with the date 30 days later than policy cancellation effective date and verify that error message is appears
      * 15. Change the reinstate date to policy cancellation effective date and verify that policy status is 'Policy Active' and policy effective date = policy cancellation date
      * 16. Click transaction history link and verify that reinstate transaction appears in transaction history
      * 17. Navigate to Billing page and verify that no reinstatement fee is applied
      */

    @Parameters({"state"})
    @StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.HOME_CA_HO3)
    public void testPolicyCancelReinstateUWReason(@Optional("CA") String state) {
        CancelActionTab cancelActionTab = new CancelActionTab();
        ReinstatementActionTab reinstatementActionTab = new ReinstatementActionTab();

        String expectedWarningCancellationDateRequired = "'Cancellation effective date' is required";
        String expectedWarningReason = "'Cancellation reason' is required";
        String expectedWarningDateEqual = "Rescission date should be equal to policy effective date";
        String expectedWarningReinstateDate = "'Reinstate date' is required";
        String expectedWarningReinstateDateIncorrect =
                "Policy is not eligible for reinstatement due to the reinstate date is greater than 30 days from the cancellation date. The policy may qualify to be rewritten.";
        String cancellationReason = "New Business Rescission - Underwriting Material Misrepresentation";
        String performer = "QA QA user";
        HashMap<String, String> query;

        mainApp().open();
        String policyNumber = getCopiedPolicy();

        String policyEffectiveDate = PolicySummaryPage.labelPolicyEffectiveDate.getValue();
        Dollar premium = new Dollar(PolicySummaryPage.getTotalPremiumSummaryForProperty());

        //  3.  Start policy cancellation process
        new HomeCaPolicyActions.Cancel().start();

        //  4.  Verify that fields 'Cancellation effective date', 'Cancellation reason' and 'Description' are presented on 'Cancellation' tab
        verifyFieldsPresentAndEnabled(cancelActionTab.getAssetList(), true, HomeCaMetaData.CancelActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel(),
                HomeCaMetaData.CancelActionTab.CANCELLATION_REASON.getLabel(),
                HomeCaMetaData.CancelActionTab.DESCRIPTION.getLabel());

        //  5.  Verify that default value in 'Cancellation effective date' field is today + 1 day
        assertThat(cancelActionTab.getAssetList().getAsset(HomeCaMetaData.CancelActionTab.CANCELLATION_EFFECTIVE_DATE)).valueContains(DateTimeUtils.getCurrentDateTime().plusDays(1)
                .format(DateTimeUtils.MM_DD_YYYY));

        //  6.  Leave the cancellation date and cancellation reason fields empty and click Ok
        cancelActionTab.getAssetList().getAsset(HomeCaMetaData.CancelActionTab.CANCELLATION_EFFECTIVE_DATE).setValue("");
        CancelActionTab.buttonOk.click();
        assertThat(cancelActionTab.getAssetList().getWarning(HomeCaMetaData.CancelActionTab.CANCELLATION_EFFECTIVE_DATE)).valueContains(expectedWarningCancellationDateRequired);
        assertThat(cancelActionTab.getAssetList().getWarning(HomeCaMetaData.CancelActionTab.CANCELLATION_REASON)).valueContains(expectedWarningReason);

        //  7.  Change the cancellation date to other than policy effective date and verify that error message is appears
        cancelActionTab.fillTab(getPolicyTD("Cancellation", "TestData_Plus3Days").adjust(
                TestData.makeKeyPath(HomeCaMetaData.CancelActionTab.class.getSimpleName(), HomeCaMetaData.CancelActionTab.CANCELLATION_REASON.getLabel()), cancellationReason));
        CancelActionTab.buttonOk.click();
        assertThat(cancelActionTab.getAssetList().getWarning(HomeCaMetaData.CancelActionTab.CANCELLATION_EFFECTIVE_DATE)).valueContains(expectedWarningDateEqual);

        //  8.  Change the cancellation date to policy effective date and verify that policy status is 'Policy Cancelled'
        cancelActionTab.getAssetList().getAsset(HomeCaMetaData.CancelActionTab.CANCELLATION_EFFECTIVE_DATE).setValue(policyEffectiveDate);
        CancelActionTab.buttonOk.click();
        Page.dialogConfirmation.confirm();
        PolicySummaryPage.labelPolicyStatus.getValue();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

        //  9.  Click transaction history link and verify that cancellation transaction appears in transaction history
        PolicySummaryPage.buttonTransactionHistory.click();
        query = new HashMap<>();
        query.put(PolicyTransactionHistoryTable.TYPE, TransactionHistoryType.CANCELLATION);
        query.put(PolicyTransactionHistoryTable.TRANSACTION_DATE, policyEffectiveDate);
        query.put(PolicyTransactionHistoryTable.EFFECTIVE_DATE, policyEffectiveDate);
        query.put(PolicyTransactionHistoryTable.REASON, cancellationReason);
        query.put(PolicyTransactionHistoryTable.TRAN_PREMIUM, premium.negate().toString());
        query.put(PolicyTransactionHistoryTable.ENDING_PREMIUM, new Dollar(0).toString());
        query.put(PolicyTransactionHistoryTable.PERFORMER, performer);
        assertThat(PolicySummaryPage.tableTransactionHistory.getRowContains(query)).exists();

        // 10. Navigate to Billing tab and verify that Transaction is created in Payments & Other Transactions section
        NavigationPage.toMainTab(AppMainTabs.BILLING.get());
        query = new HashMap<>();
        query.put(BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, policyEffectiveDate);
        query.put(BillingPaymentsAndOtherTransactionsTable.EFF_DATE, policyEffectiveDate);
        query.put(BillingPaymentsAndOtherTransactionsTable.POLICY, policyNumber);
        query.put(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, cancellationReason);
        query.put(BillingPaymentsAndOtherTransactionsTable.AMOUNT, premium.negate().toString());
        query.put(BillingPaymentsAndOtherTransactionsTable.STATUS, PaymentsAndOtherTransactionStatus.APPLIED);
        assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(query)).exists();

        // 11. Navigate to Policy Summary page and check that policy consolidated screen contains an alert with cancellation reason
        BillingSummaryPage.openPolicy(1);
        if (NotesAndAlertsSummaryPage.alert2.isPresent()) {
        	assertTrue(NotesAndAlertsSummaryPage.alert.getValue().contains(cancellationReason) || NotesAndAlertsSummaryPage.alert2.getValue().contains(cancellationReason));
            //assertThat(NotesAndAlertsSummaryPage.alert2).valueContains(cancellationReason);
        } else {
            assertThat(NotesAndAlertsSummaryPage.alert).valueContains(cancellationReason);
        }

        // 12. Start policy Reinstatement process and verify fields 'Cancellation effective date' and 'Reinstate date'
        new HomeCaPolicyActions.Reinstate().start();
        verifyFieldsPresentAndEnabled(reinstatementActionTab.getAssetList(), false, HomeCaMetaData.ReinstatementActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel());
        assertThat(reinstatementActionTab.getAssetList().getAsset(HomeCaMetaData.ReinstatementActionTab.CANCELLATION_EFFECTIVE_DATE)).valueContains(policyEffectiveDate);
        verifyFieldsPresentAndEnabled(reinstatementActionTab.getAssetList(), true, HomeCaMetaData.ReinstatementActionTab.REINSTATE_DATE.getLabel());
        assertThat(reinstatementActionTab.getAssetList().getAsset(HomeCaMetaData.ReinstatementActionTab.REINSTATE_DATE)).valueContains(DateTimeUtils.getCurrentDateTime().plusDays(1)
                .format(DateTimeUtils.MM_DD_YYYY));

        // 13. Leave the reinstate date field empty, click Ok and verify that error message is appears
        reinstatementActionTab.getAssetList().getAsset(HomeCaMetaData.ReinstatementActionTab.REINSTATE_DATE).setValue("");
        ReinstatementActionTab.buttonOk.click();
        assertThat(reinstatementActionTab.getAssetList().getWarning(HomeCaMetaData.ReinstatementActionTab.REINSTATE_DATE)).valueContains(expectedWarningReinstateDate);

        // 14. Fill in the 'Reinstate date' field with the date 30 days later than policy cancellation effective date and verify that error message is appears
        reinstatementActionTab.getAssetList().getAsset(HomeCaMetaData.ReinstatementActionTab.REINSTATE_DATE).setValue(DateTimeUtils.getCurrentDateTime().plusDays(31).format(DateTimeUtils.MM_DD_YYYY));
        assertThat(reinstatementActionTab.getAssetList().getWarning(HomeCaMetaData.ReinstatementActionTab.REINSTATE_DATE)).valueContains(expectedWarningReinstateDateIncorrect);

        // 15. Change the reinstate date to policy cancellation effective date and verify that policy status is 'Policy Active' and policy effective date = policy cancellation date
        reinstatementActionTab.getAssetList().getAsset(HomeCaMetaData.ReinstatementActionTab.REINSTATE_DATE).setValue(policyEffectiveDate);
        ReinstatementActionTab.buttonOk.click();
        Page.dialogConfirmation.confirm();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        assertThat(PolicySummaryPage.labelPolicyEffectiveDate).valueContains(policyEffectiveDate);

        // 16. Click transaction history link and verify that reinstate transaction appears in transaction history
        PolicySummaryPage.buttonTransactionHistory.click();
        query = new HashMap<>();
        query.put(PolicyTransactionHistoryTable.TYPE, TransactionHistoryType.REINSTATEMENT);
        query.put(PolicyTransactionHistoryTable.TRANSACTION_DATE, policyEffectiveDate);
        query.put(PolicyTransactionHistoryTable.EFFECTIVE_DATE, policyEffectiveDate);
        query.put(PolicyTransactionHistoryTable.TRAN_PREMIUM, premium.toString());
        query.put(PolicyTransactionHistoryTable.ENDING_PREMIUM, premium.toString());
        query.put(PolicyTransactionHistoryTable.PERFORMER, performer);
        assertThat(PolicySummaryPage.tableTransactionHistory.getRowContains(query)).exists();

        // 17. Navigate to Billing page and verify that no reinstatement fee is applied
        NavigationPage.toMainTab(AppMainTabs.BILLING.get());
        query = new HashMap<>();
        query.put(BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, policyEffectiveDate);
        query.put(BillingPaymentsAndOtherTransactionsTable.EFF_DATE, policyEffectiveDate);
        query.put(BillingPaymentsAndOtherTransactionsTable.POLICY, policyNumber);
        query.put(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT);
        query.put(BillingPaymentsAndOtherTransactionsTable.AMOUNT, premium.toString());
        query.put(BillingPaymentsAndOtherTransactionsTable.STATUS, PaymentsAndOtherTransactionStatus.APPLIED);
        assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(query)).exists();
    }

    private void verifyFieldsPresentAndEnabled(AbstractContainer<?, ?> assetList, boolean isEnabled, String... fields) {
        for (String field : fields) {
            assertThat(assetList.getAsset(field)).isEnabled(isEnabled);
        }
    }
}
