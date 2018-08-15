package aaa.modules.regression.service.helper;

import static aaa.main.enums.BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.service.helper.dtoDxp.Bill;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public abstract class TestMiniServicesBillingAbstract extends PolicyBaseTest {

	private BillingAccount billingAccount = new BillingAccount();
	private TestData tdBilling = testDataManager.billingAccount;

	protected abstract String getGeneralTab();

	protected abstract String getPremiumAndCoverageTab();

	protected abstract String getDocumentsAndBindTab();

	protected abstract String getVehicleTab();

	protected abstract Tab getGeneralTabElement();

	protected abstract Tab getPremiumAndCoverageTabElement();

	protected abstract Tab getDocumentsAndBindTabElement();

	protected abstract Tab getVehicleTabElement();

	protected abstract AssetDescriptor<JavaScriptButton> getCalculatePremium();

	/**
	 * @author Oleg Stasyuk
	 * @name Test Current Bill Service for non-Annual
	 * @scenario 1. Create a policy
	 * 2. run the current bill service
	 * 3. check zero balances
	 * 4. generate the bill1
	 * 5. check Due Date, Min Due, Past Due (0) are returned same as are there in UI
	 * 6. partially pay the bill
	 * 7. check Due Date, Min Due, Past Due (0) are returned same as are there in UI
	 * 8. generate the bill2
	 * 9. check Due Date, Min Due, Past Due (non-0) are returned same as are there in UI
	 * 10. pay policy total due
	 * 11. check Due Date, Min Due, Past Due (non-0) are returned same as are there in UI
	 * @details
	 */
	protected void pas13663_CurrentBillServiceBody(ETCSCoreSoftAssertions softly, String policyNumber) {
		currentBillServiceCheck(softly, policyNumber);

		SearchPage.openBilling(policyNumber);
		LocalDateTime dd1 = BillingSummaryPage.getInstallmentDueDate(2);
		LocalDateTime dd2 = BillingSummaryPage.getInstallmentDueDate(3);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(dd1));
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);

		currentBillServiceCheck(softly, policyNumber);

		Dollar minimumDue = BillingSummaryPage.getMinimumDue();
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), minimumDue.subtract(new Dollar("11.11")));

		currentBillServiceCheck(softly, policyNumber);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(dd2));
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);

		currentBillServiceCheck(softly, policyNumber);

		mainApp().open();
		SearchPage.openBilling(policyNumber);

		Dollar totalDue = BillingSummaryPage.getTotalDue();
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue);

		currentBillServiceCheck(softly, policyNumber);
	}

	protected void currentBillServiceCheck(ETCSCoreSoftAssertions softly, String policyNumber) {
		mainApp().open();
		SearchPage.openBilling(policyNumber);

		if (BillingSummaryPage.tableBillsStatements.getRowsCount() > 0) {
			LocalDateTime dueDateUi = TimeSetterUtil.getInstance().parse(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(DUE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);
			String amountDueUI = new Dollar(BillingSummaryPage.getMinimumDue()).toPlaingString();
			String amountPastDueUi = new Dollar(BillingSummaryPage.getPastDue()).toPlaingString();
			Bill currentBillResponse2 = HelperCommon.currentBillService(policyNumber);
			softly.assertThat(currentBillResponse2.dueDate).isEqualTo(dueDateUi.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));
			softly.assertThat(currentBillResponse2.amountDue).isEqualTo(amountDueUI);
			softly.assertThat(currentBillResponse2.amountPastDue).isEqualTo(amountPastDueUi);
		} else {
			Bill currentBillResponse2 = HelperCommon.currentBillService(policyNumber);
			if(!currentBillResponse2.dueDate.equals(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("YYYY-MM-dd")))) {
				softly.assertThat(currentBillResponse2.dueDate).isEqualTo(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));
			}
			softly.assertThat(currentBillResponse2.amountDue).isEqualTo("0.00");
			softly.assertThat(currentBillResponse2.amountPastDue).isEqualTo("0.00");
		}
	}
}
