package aaa.modules.regression.service.helper;

import static aaa.main.enums.BillingConstants.BillingInstallmentScheduleTable.*;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.helpers.rest.dtoDxp.*;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public abstract class TestMiniServicesBillingAbstract extends PolicyBaseTest {

	private BillingAccount billingAccount = new BillingAccount();
	private TestData tdBilling = testDataManager.billingAccount;
	private HelperMiniServices helperMiniServices = new HelperMiniServices();

	protected abstract String getGeneralTab();

	protected abstract String getPremiumAndCoverageTab();

	protected abstract String getDocumentsAndBindTab();

	protected abstract String getVehicleTab();

	protected abstract Tab getGeneralTabElement();

	protected abstract Tab getPremiumAndCoverageTabElement();

	protected abstract Tab getDocumentsAndBindTabElement();

	protected abstract AssetDescriptor<JavaScriptButton> getCalculatePremium();


	protected void pas16982_ViewInstallmentScheduleServiceBody(String policyNumber) {

		assertSoftly(softly -> {
			String lastDueDate = installmentsServiceCheck(softly, policyNumber);
			currentAccountInfoServiceCheck(softly, policyNumber, lastDueDate);
		});

		//create endorsement outside of PAS
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		AddDriverRequest addDriverRequest = DXPRequestFactory.createAddDriverRequest("Spouse", "Driver", "Smith", "1979-02-13", "III");
		DriversDto addDriverRequestService = HelperCommon.addDriver(policyNumber, addDriverRequest, DriversDto.class);
		UpdateDriverRequest updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest("female", "D32329585", 16, "AZ", "CH", "MSS");
		HelperCommon.updateDriver(policyNumber, addDriverRequestService.oid, updateDriverRequest);

		//Order reports through service
		HelperCommon.orderReports(policyNumber, addDriverRequestService.oid, OrderReportsResponse.class, 200);

		helperMiniServices.endorsementRateAndBind(policyNumber);

		assertSoftly(softly -> {
			String lastDueDate2 = installmentsServiceCheck(softly, policyNumber);
			currentAccountInfoServiceCheck(softly, policyNumber, lastDueDate2);
		});
	}


	protected String installmentsServiceCheck(ETCSCoreSoftAssertions softly, String policyNumber) {
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

	protected void currentAccountInfoServiceCheck(ETCSCoreSoftAssertions softly, String policyNumber, String latestBillDueDate) {
		mainApp().open();
		SearchPage.openBilling(policyNumber);

		String accountNumberUi = BillingSummaryPage.labelBillingAccountNumber.getValue();
		String amountMinDueUi = new Dollar(BillingSummaryPage.getMinimumDue()).toPlaingString();
		String amountPastDueUi = new Dollar(BillingSummaryPage.getPastDue()).toPlaingString();
		String amountTotalDueUi = new Dollar(BillingSummaryPage.getTotalDue()).toPlaingString();
		String amountTotalPaidUi = new Dollar(BillingSummaryPage.getTotalPaid()).toPlaingString();
		String billableAmountUi = new Dollar(BillingSummaryPage.getBillableAmount()).toPlaingString();

		AccountDetails billingAccountInfoResponse = HelperCommon.billingAccountInfoService(policyNumber);
		softly.assertThat(billingAccountInfoResponse.accountNumber).isEqualTo(accountNumberUi);
		softly.assertThat(billingAccountInfoResponse.minimumDue).isEqualTo(amountMinDueUi);
		softly.assertThat(billingAccountInfoResponse.pastDue).isEqualTo(amountPastDueUi);
		softly.assertThat(billingAccountInfoResponse.totalDue).isEqualTo(amountTotalDueUi);
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