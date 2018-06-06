package aaa.modules.regression.sales.template.functional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.policy.abstract_tabs.Purchase;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import static toolkit.verification.CustomAssertions.assertThat;

public abstract class TestOffCycleBillNoInstallmentDateAbstract extends PolicyBaseTest {

	private final Dollar zeroDollars = new Dollar(0.00);
	private String policyNumber;
	private LocalDateTime dueDate;

	protected abstract Purchase getPurchaseTab();
	protected abstract Tab getPremiumAndCoveragesTab();
	protected abstract Tab getBindTab();
	protected abstract void navigateToPremiumAndCoveragesTab();
	protected abstract void navigateToBindTab();
	protected abstract void adjustPremiumBearingValue();
	protected abstract AssetDescriptor<JavaScriptButton> getCalculatePremiumButton();


    protected void pas9001_testInvoiceWithNoDownPaymentNB() {

        // Create Customer
        mainApp().open();
        createCustomerIndividual();

        // Create policy with updated min deposit on Purchase tab
        getPolicyType().get().initiate();
        getPolicyType().get().getDefaultView().fillUpTo(getPolicyDefaultTD(), getPurchaseTab().getClass());
		getPurchaseTab().getAssetList().getAsset(PurchaseMetaData.PurchaseTab.CHANGE_MINIMUM_DOWNPAYMENT).setValue(true);
		getPurchaseTab().getAssetList().getAsset(PurchaseMetaData.PurchaseTab.MINIMUM_REQUIRED_DOWNPAYMENT).setValue("0.00");
		getPurchaseTab().getAssetList().getAsset(PurchaseMetaData.PurchaseTab.REASON_FOR_CHANGING).setValue("index=1");
		getPurchaseTab().getAssetList().getAsset(PurchaseMetaData.PurchaseTab.PAYMENT_METHOD_CASH).setValue(Purchase.remainingBalanceDueToday.getValue());
		getPurchaseTab().submitTab();
		setPolicyInfo();

		// Min Due and off cycle billing validation
		validateMinDueAndOffCycleBillingInvoice();

    }

    protected void pas9001_testInvoiceWithNoDownPaymentEndorsement() {

        // Create customer and policy
        mainApp().open();
        createCustomerIndividual();
        getPolicyType().get().createPolicy(getPolicyDefaultTD());
		setPolicyInfo();

        // Create a premium-bearing endorsement (increase) at effective date plus 5 days
        TimeSetterUtil.getInstance().nextPhase(PolicySummaryPage.getEffectiveDate().plusDays(5));
        reopenPolicy(policyNumber);
        getPolicyType().get().endorse().perform(getStateTestData(testDataManager.policy.get(getPolicyType()).getTestData("Endorsement"), "TestData"));
        navigateToPremiumAndCoveragesTab();
        adjustPremiumBearingValue();
		getPremiumAndCoveragesTab().getAssetList().getAsset(getCalculatePremiumButton()).click();
        navigateToBindTab();
        getBindTab().submitTab();

        // Min Due and off cycle billing validation
		validateMinDueAndOffCycleBillingInvoice();

    }

    private void reopenPolicy(String policyNumber) {
        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
    }

    private void setPolicyInfo() {
		policyNumber = PolicySummaryPage.getPolicyNumber();
		dueDate = PolicySummaryPage.getEffectiveDate().plusMonths(1);
	}

    private void validateMinDueAndOffCycleBillingInvoice() {
		// Verify min due
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		assertThat(BillingSummaryPage.getMinimumDue()).isEqualTo(zeroDollars);

		// Move to DD-20, run off cycle billing job, and refresh policy
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getOffcycleBillGenerationDate(dueDate));

		JobUtils.executeJob(Jobs.offCycleBillingInvoiceAsyncJob);
		reopenPolicy(policyNumber);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

		// Verify off cycle invoice is generated
		Dollar minDue = new Dollar(BillingSummaryPage.tableBillsStatements.getRow(1).getCell("Minimum Due").getValue());
		assertThat(BillingSummaryPage.tableBillsStatements.getRows().size()).isEqualTo(1);
		assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell("Due Date").getValue()).isEqualTo(dueDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
		assertThat(minDue).isNotEqualTo(zeroDollars);
		assertThat(new Dollar(BillingSummaryPage.tableBillsStatements.getRow(1).getCell("Total Due").getValue())).isEqualTo(minDue);

	}

}
