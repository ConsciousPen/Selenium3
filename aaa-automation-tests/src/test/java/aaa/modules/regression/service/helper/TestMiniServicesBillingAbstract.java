package aaa.modules.regression.service.helper;

import static aaa.main.enums.BillingConstants.BillingInstallmentScheduleTable.*;

import java.time.*;
import java.time.format.DateTimeFormatter;

import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.modules.regression.service.helper.dtoDxp.AccountDetails;
import aaa.modules.regression.service.helper.dtoDxp.Bill;
import aaa.modules.regression.service.helper.dtoDxp.Installment;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.SoftAssertions;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
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
	protected void pas13663_CurrentBillServiceBody(SoftAssertions softly, String policyNumber) {
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



	protected void currentBillServiceCheck(SoftAssertions softly, String policyNumber) {
		mainApp().open();
		SearchPage.openBilling(policyNumber);

		if (BillingSummaryPage.tableBillsStatements.getRowsCount() > 0) {
			LocalDateTime dueDateUi = TimeSetterUtil.getInstance().parse(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BILL_DUE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);
			String amountDueUI = new Dollar(BillingSummaryPage.getMinimumDue()).toPlaingString();
			String amountPastDueUi = new Dollar(BillingSummaryPage.getPastDue()).toPlaingString();
			Bill currentBillResponse2 = HelperCommon.currentBillService(policyNumber);
			softly.assertThat(currentBillResponse2.dueDate).isEqualTo(dueDateUi.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));
			softly.assertThat(currentBillResponse2.amountDue).isEqualTo(amountDueUI);
			softly.assertThat(currentBillResponse2.amountPastDue).isEqualTo(amountPastDueUi);
		} else {
			Bill currentBillResponse2 = HelperCommon.currentBillService(policyNumber);
			if (!currentBillResponse2.dueDate.equals(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("YYYY-MM-dd")))) {
				softly.assertThat(currentBillResponse2.dueDate).isEqualTo(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));
			}
			softly.assertThat(currentBillResponse2.amountDue).isEqualTo("0.00");
			softly.assertThat(currentBillResponse2.amountPastDue).isEqualTo("0.00");
		}
	}

	protected String installmentsServiceCheck(SoftAssertions softly, String policyNumber) {
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		int countInstallments = BillingSummaryPage.tableInstallmentSchedule.getRowsCount();

		Installment[] billingInstallmentsResponse = HelperCommon.billingInstallmentsInfo(policyNumber);
		for (int i = 1; i <= countInstallments; i++) {
			String typeUi = BillingSummaryPage.tableInstallmentSchedule.getRow(i).getCell(DESCRIPTION).getValue();
			String amountUi = new Dollar(BillingSummaryPage.tableInstallmentSchedule.getRow(i).getCell(SCHEDULE_DUE_AMOUNT).getValue()).toPlaingString().replace("$", "");
			String statusCdUi = BillingSummaryPage.tableInstallmentSchedule.getRow(i).getCell(BILLED_STATUS).getValue().replace("Unbilled","pending").replace("Billed","billed");
			String billedAmountUi = getBilledAmountFromInstallmentSchedule(i, BILLED_AMOUNT).toPlaingString().replace("$", "");

			Installment installment = billingInstallmentsResponse[i-1];
			softly.assertThat(installment.type).isEqualTo(typeUi);
			softly.assertThat(installment.amount).isEqualTo(amountUi);
			softly.assertThat(dateOnly(installment.dueDate)).isEqualTo((getDateFromInstallmentSchedule(i, INSTALLMENT_DUE_DATE)));
			softly.assertThat(installment.statusCd).isEqualTo(statusCdUi);
			softly.assertThat(dateOnly(installment.billGenerationDate)).isEqualTo(getDateFromInstallmentSchedule(i, BILL_GENERATION_DATE));
			softly.assertThat(dateOnly(installment.billDueDate)).isEqualTo(getDateFromInstallmentSchedule(i, BILL_DUE_DATE));
			softly.assertThat(installment.billedAmount).isEqualTo(billedAmountUi);
		}
		return getDateFromInstallmentSchedule(countInstallments, BILL_DUE_DATE);
	}

	protected void currentAccountInfoServiceCheck(SoftAssertions softly, String policyNumber, String latestBillDueDate) {
		mainApp().open();
		SearchPage.openBilling(policyNumber);

		String accountNumberUi = BillingSummaryPage.labelBillingAccountNumber.getValue();
		String amountMinDueUi = new Dollar(BillingSummaryPage.getMinimumDue()).toPlaingString();
		String amountPastDueUi = new Dollar(BillingSummaryPage.getPastDue()).toPlaingString();
		String amountTotalPaidUi = new Dollar(BillingSummaryPage.getTotalPaid()).toPlaingString();
		String billableAmountUi = new Dollar(BillingSummaryPage.getBillableAmount()).toPlaingString();

		AccountDetails billingAccountInfoResponse = HelperCommon.billingAccountInfoService(policyNumber);
		softly.assertThat(billingAccountInfoResponse.accountNumber).isEqualTo(accountNumberUi);
		softly.assertThat(billingAccountInfoResponse.minimumDue).isEqualTo(amountMinDueUi);
		softly.assertThat(billingAccountInfoResponse.pastDue).isEqualTo(amountPastDueUi);
		softly.assertThat(billingAccountInfoResponse.totalPaid).isEqualTo(amountTotalPaidUi);
		softly.assertThat(billingAccountInfoResponse.billableAmount).isEqualTo(billableAmountUi);
		softly.assertThat(dateOnly(billingAccountInfoResponse.latestInvoiceDueDate)).isEqualTo(latestBillDueDate);
	}

	private String dateOnly(String s) {
		if ( s != null ) {
			s = s.substring(0, "YYYY-MM-dd".length());
			return s;
		}
		return s;
	}

	private String getDateFromInstallmentSchedule(int rowIndex, String cellName) {
		String dateAsString = BillingSummaryPage.tableInstallmentSchedule.getRow(rowIndex).getCell(cellName).getValue();
		if (StringUtils.isNotEmpty(dateAsString)) {
			LocalDateTime localDate  = TimeSetterUtil.getInstance().parse(dateAsString, DateTimeUtils.MM_DD_YYYY);
			return localDate.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));
		}
		return null;
	}

	private Dollar getBilledAmountFromInstallmentSchedule(int rowIndex, String cellName) {
		String amountAsString = BillingSummaryPage.tableInstallmentSchedule.getRow(rowIndex)
				.getCell(cellName).getValue();
		if (StringUtils.isNotEmpty(amountAsString)) {
			return new Dollar(amountAsString);
		}
		return new Dollar(0);
	}
}