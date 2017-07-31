package aaa.modules.e2e.templates;

import java.time.LocalDateTime;
import java.util.List;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.common.enums.NavigationEnum;
import aaa.common.enums.SearchEnum.SearchBy;
import aaa.common.enums.SearchEnum.SearchFor;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants.BillingBillsAndStatmentsTable;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;

public class Scenario1 extends BaseTest {
	
//	protected IPolicy policy;
	protected TestData tdPolicy;
	
	protected String policyNumber;
	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;
	protected Dollar policyPremium;
	
	protected String billingAccNum;
	protected List<LocalDateTime> installmentDueDates;
	protected Dollar billAmount;
	
	public void TC01_createPolicy(TestData policyCreationTD) {
//		policy = getPolicyType().get();
		
		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy(policyCreationTD);
		
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyPremium = PolicySummaryPage.getTotalPremiumSummary();
		
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		billingAccNum = BillingSummaryPage.labelBillingAccountNumber.getValue();
		installmentDueDates = BillingHelper.getInstallmentDueDates();
	}
	
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Generate_First_Bill() {
		generateAndCheckBill(installmentDueDates.get(0));
		billAmount = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(0), BillingBillsAndStatmentsTable.MINIMUM_DUE));
	}
	
	
	
	private void generateAndCheckBill(LocalDateTime installmentDate) {
		LocalDateTime billDate = getTimePoints().getBillGenerationDate(installmentDate);
		TimeSetterUtil.getInstance().nextPhase(billDate);
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.search(SearchFor.BILLING, SearchBy.BILLING_ACCOUNT, billingAccNum);
		BillingHelper.verifyBillGenerated(installmentDate, getTimePoints().getBillGenerationDate(installmentDate));
		BillingHelper.verifyFeeTransactionGenerated(billDate);
	}

}
