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
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.policy.abstract_tabs.Purchase;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import static toolkit.verification.CustomAssertions.assertThat;

public class TestInvoiceWithNoDownPaymentTemplate extends PolicyBaseTest {

	private final Dollar zeroDollars = new Dollar(0.00);
	private String policyNumber;
	private LocalDateTime dueDate;

    protected void pas9001_testInvoiceWithNoDownPaymentNB() {

        Purchase purchaseTab;
        if (getPolicyType().isCaProduct()) {
            purchaseTab = new PurchaseTab();
        } else {
            purchaseTab = new aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab();
        }

        // Create Customer
        mainApp().open();
        createCustomerIndividual();

        // Create policy with updated min deposit on Purchase tab
        getPolicyType().get().initiate();
        getPolicyType().get().getDefaultView().fillUpTo(getPolicyDefaultTD(), purchaseTab.getClass());
        purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.CHANGE_MINIMUM_DOWNPAYMENT).setValue(true);
        purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.MINIMUM_REQUIRED_DOWNPAYMENT).setValue("10.00");
        purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.REASON_FOR_CHANGING).setValue("index=1");
		purchaseTab.getAssetList().getAsset(PurchaseMetaData.PurchaseTab.PAYMENT_METHOD_CASH).setValue("10.00");
        purchaseTab.submitTab();
		setPolicyInfo();

		// Min Due and off cycle billing validation
		validateMinDueAndOffCycleBillingInvoice();

    }

    protected void pas9001_testInvoiceWithNoDownPaymentEndorsement() {

        Tab premiumAndCoveragesQuoteTab;
        Tab bindTab;
        if (getPolicyType().isCaProduct()) {
            premiumAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
            bindTab = new BindTab();
        } else {
            premiumAndCoveragesQuoteTab = new aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab();
            bindTab = new aaa.main.modules.policy.home_ss.defaulttabs.BindTab();
        }

        // Create customer and policy
        mainApp().open();
        createCustomerIndividual();
        getPolicyType().get().createPolicy(getPolicyDefaultTD());
		setPolicyInfo();

        // Create a premium-bearing endorsement (increase) at effective date plus 5 days
        TimeSetterUtil.getInstance().confirmDateIsAfter(PolicySummaryPage.getEffectiveDate().plusDays(5));
        reopenPolicy(policyNumber);
        getPolicyType().get().endorse().perform(getStateTestData(testDataManager.policy.get(getPolicyType()).getTestData("Endorsement"), "TestData"));
        navigateToPremiumAndCoveragesQuoteTab();
        premiumAndCoveragesQuoteTab.getAssetList().getAsset(getDeductible()).setValueByIndex(0);
        premiumAndCoveragesQuoteTab.getAssetList().getAsset(getCalculatePremiumButton()).click();
        navigateToBindTab();
        bindTab.submitTab();

        // Min Due and off cycle billing validation
		validateMinDueAndOffCycleBillingInvoice();

    }

    private void navigateToPremiumAndCoveragesQuoteTab() {
        if (getPolicyType().isCaProduct()) {
            NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
            NavigationPage.toViewSubTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        } else {
            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
            NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        }
    }

    private void navigateToBindTab() {
        if (getPolicyType().isCaProduct()) {
            NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
        } else {
            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        }
    }

    private AssetDescriptor<ComboBox> getDeductible() {
        if (getPolicyType().isCaProduct()) {
            return HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE;
        }
        return HomeSSMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE;
    }

    private AssetDescriptor<JavaScriptButton> getCalculatePremiumButton() {
        if (getPolicyType().isCaProduct()) {
            return HomeCaMetaData.PremiumsAndCoveragesQuoteTab.CALCULATE_PREMIUM_BUTTON;
        }
        return HomeSSMetaData.PremiumsAndCoveragesQuoteTab.CALCULATE_PREMIUM;
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
		TimeSetterUtil.getInstance().nextPhase(dueDate.minusDays(20));
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
