package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.Purchase;
import aaa.main.modules.policy.pup.defaulttabs.ErrorTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public abstract class TestOffCycleBillNoInstallmentDateAbstract extends PolicyBaseTest {

	private final Dollar zeroDollars = new Dollar(0.00);
	private String policyNumber;
	private LocalDateTime dueDate;
	private LocalDateTime effDate;

	protected abstract Purchase getPurchaseTab();

	protected abstract Tab getBindTab();

	protected abstract void navigateToPremiumAndCoveragesTab();

	protected abstract void navigateToBindTab();

	protected abstract void adjustPremiumBearingValue();

	protected abstract void calculatePremium();

	protected void pas9001_testOffCycleBillNoDownPayment_NB() {

		// Create Customer
		mainApp().open();
		createCustomerIndividual();

		// Create policy with updated min deposit on Purchase tab
		TestData td = getPolicyDefaultTD();
		getPolicyType().get().initiate();
		getPolicyType().get().getDefaultView().fillUpTo(td, getPurchaseTab().getClass());
		getPurchaseTab().getAssetList().getAsset(PurchaseMetaData.PurchaseTab.CHANGE_MINIMUM_DOWNPAYMENT).setValue(true);
		getPurchaseTab().getAssetList().getAsset(PurchaseMetaData.PurchaseTab.MINIMUM_REQUIRED_DOWNPAYMENT).setValue("0.00");
		getPurchaseTab().getAssetList().getAsset(PurchaseMetaData.PurchaseTab.REASON_FOR_CHANGING).setValue("index=1");
		getPurchaseTab().submitTab();
		setPolicyInfo();

		// Min Due and off cycle billing validation
		validateMinDueAndOffCycleBillingInvoice();

	}

	protected void pas9001_testOffCycleBillPremiumBearingEndorsement() {

		// Create customer and policy
		mainApp().open();
		createCustomerIndividual();
		getPolicyType().get().createPolicy(getPolicyDefaultTD());
		setPolicyInfo();

		// Create a premium-bearing endorsement (increase) at effective date plus 5 days
		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(effDate.plusDays(5));
		reopenPolicy(policyNumber);
		getPolicyType().get().endorse().perform(getStateTestData(testDataManager.policy.get(getPolicyType()).getTestData("Endorsement"), "TestData"));
		navigateToPremiumAndCoveragesTab();
		adjustPremiumBearingValue();
		calculatePremium();
		navigateToBindTab();
		getBindTab().submitTab();

		// Override UW rule for PUP policy
		if (getPolicyType().equals(PolicyType.PUP)) {
			ErrorTab errorTab = new ErrorTab();
			if (getState().equals(Constants.States.CA)) {
				errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_PUP_SS7121080_CA);
			} else {
				errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_PUP_SS7121080);
			}
			errorTab.override();
			getBindTab().submitTab();
		}

		// Min Due and off cycle billing validation
		validateMinDueAndOffCycleBillingInvoice();

	}

	private void reopenPolicy(String policyNumber) {
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
	}

	private void setPolicyInfo() {
		policyNumber = PolicySummaryPage.getPolicyNumber();
		effDate = PolicySummaryPage.getEffectiveDate();
		dueDate = effDate.plusMonths(1);
	}

	private void validateMinDueAndOffCycleBillingInvoice() {
		// Verify min due
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		assertThat(BillingSummaryPage.getMinimumDue()).isEqualTo(zeroDollars);

		// Move to DD-20, run off cycle billing job, and refresh policy
		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getOffcycleBillGenerationDate(dueDate));
		JobUtils.executeJob(BatchJob.offCycleBillingInvoiceAsyncJob);
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
