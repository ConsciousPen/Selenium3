package aaa.modules.regression.billing_and_payments.auto_ss.functional;

import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.config.CsaaTestProperties;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.rest.wiremock.HelperWireMockStub;
import aaa.helpers.rest.wiremock.dto.LastPaymentTemplateData;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.billing_and_payments.auto_ss.functional.preconditions.TestRefundProcessPreConditions;
import aaa.modules.regression.billing_and_payments.helpers.RefundProcessHelper;
import aaa.modules.regression.billing_and_payments.template.functional.TestRefundProcessTemplate;
import aaa.modules.regression.service.helper.HelperWireMockLastPaymentMethod;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static aaa.helpers.rest.wiremock.dto.LastPaymentTemplateData.EligibilityStatusEnum.NON_REFUNDABLE;
import static aaa.helpers.rest.wiremock.dto.LastPaymentTemplateData.PaymentMethodEnum.EFT;
import static aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT;
import static aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE;
import static aaa.modules.regression.sales.auto_ss.functional.preconditions.EvalueInsertSetupPreConditions.APP_STUB_URL;
import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public class TestRefundProcess extends PolicyBaseTest implements TestRefundProcessPreConditions {

	private static final String APP_HOST = PropertyProvider.getProperty(CsaaTestProperties.APP_HOST);
	private static final String MESSAGE_CREDIT_CARD = "Credit Card Visa-4113 expiring 01/22";
	private static final String MESSAGE_DEBIT_CARD = "Debit Card MasterCard-4444 expiring 05/20";
	private static final String MESSAGE_ACH = "Checking/Savings (ACH) #1542";
	private static final String AMOUNT_CREDIT_CARD = "10";
	private static final String AMOUNT_DEBIT_CARD = "22";
	private static final String AMOUNT_ACH = "33";
	private static final String AMOUNT_CHECK = "10.01";
	private static final String PAYMENT_METHOD_CREDIT_CARD = "Credit card";
	private static final String PAYMENT_METHOD_DEBIT_CARD = "Debit card";
	private static final String PAYMENT_METHOD_ACH = "ACH";
	private static final String PAYMENT_METHOD_CHECK = "Check";
	private static final String APPROVED_REFUND_AMOUNT = "499.99";
	private static final String PENDING_REFUND_AMOUNT = "500";
	private static final String BILLING_PAYMENT_METHOD_CHECK = "PaymentDetailsCheque";
	private static final String BILLING_PAYMENT_METHOD_CARD = "AAAPaymentDetailsPCICreditCard";
	private static final String BILLING_PAYMENT_METHOD_ACH = "AAAPaymentDetailsEFT";
	private final List<HelperWireMockStub> requestIdList = new LinkedList<>();
	private BillingAccount billingAccount = new BillingAccount();
	private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
	private RefundProcessHelper refundProcessHelper = new RefundProcessHelper();
	private HelperWireMockLastPaymentMethod helperWireMockLastPaymentMethod = new HelperWireMockLastPaymentMethod();
	private TestRefundProcessTemplate testRefundProcessTemplate = new TestRefundProcessTemplate(PolicyType.AUTO_SS);

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Test(description = "Precondition for TestRefundProcess tests", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public void precondJobAdding() {
		testRefundProcessTemplate.precondJobAdding();
	}

	@Test(description = "Precondition for TestRefundProcess tests", groups = {Groups.FUNCTIONAL, Groups.PRECONDITION})
	public static void pendingRefundPaymentMethodConfigCheck() {
		assertThat(DBService.get().getValue(PENDING_REFUND_PAYMENT_METHOD_CONFIG_CHECK))
				.as("The configuration is missing, run pendingRefundConfigurationUpdate and restart the env.").isEqualTo("pendingRefund");
	}

	public static void eRefundLastPaymentMethodConfigCheck() {
		assertThat(DBService.get().getValue(String.format(LAST_PAYMENT_METHOD_STUB_END_POINT_CHECK, APP_HOST)).orElse(""))
				.as("eRefund stub point is set incorrect, please run LAST_PAYMENT_METHOD_STUB_POINT_UPDATE").contains(APP_HOST);
		assertThat(DBService.get().getValue(String.format(AUTHENTICATION_STUB_END_POINT_CHECK, APP_HOST, APP_STUB_URL)).orElse(""))
				.as("Authentication stub point is set incorrect, please run AUTHENTICATION_STUB_POINT_UPDATE").contains(APP_HOST);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-2186", "PAS-1936", "PAS-7057"})
	public void pas2186_ManualRefundUnissuedVoidedCheck(@org.testng.annotations.Optional("VA") String state) {
		String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "10.01";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(refundAmount), "Approved");
		policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		// PAS-2186, PAS-1940, PAS-7858, PAS-352
		refundProcessHelper.unissuedManualRefundGeneration(Optional.empty(), billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0, false);
		refundProcessHelper.unissuedRefundVerification(billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0);
		refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CHECK, "ManualRefund", BILLING_PAYMENT_METHOD_CHECK, false, false);

		// PAS-1939
		refundProcessHelper.voidedManualRefundGeneration(refund);
		refundProcessHelper.voidedRefundVerification(true, billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 1, false);
		// PAS-7057
		refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CHECK, "RefundPaymentVoided", null, true, false);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-2719", "PAS-1939", "PAS-7057"})
	public void pas2719_ManualRefundUnissuedVoidedCreditCard(@org.testng.annotations.Optional("VA") String state) throws IllegalAccessException {
		String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "10";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestCC = helperWireMockLastPaymentMethod.getHelperWireMockStubCC(policyNumber, AMOUNT_CREDIT_CARD);

		try {
			// PAS-2719, PAS-1940, PAS-6210, PAS-7858, PAS-352, PAS-3619
			refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_CREDIT_CARD), billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 0, false);
			refundProcessHelper.unissuedRefundVerification(billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 0);
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CREDIT_CARD, "ManualRefund", BILLING_PAYMENT_METHOD_CARD, false, false);

			// PAS-1939
			refundProcessHelper.voidedManualRefundGeneration(refund);
			refundProcessHelper.voidedRefundVerification(true, billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 1, false);
			// PAS-7057
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CREDIT_CARD, "RefundPaymentVoided", null, true, false);
		} finally {
			stubRequestCC.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-2719", "PAS-1939", "PAS-7057"})
	public void pas2719_ManualRefundUnissuedVoidedDebitCard(@org.testng.annotations.Optional("AZ") String state) throws IllegalAccessException {
		String refundDate1 = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "22";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate1, "Refund", "Manual Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestDC = helperWireMockLastPaymentMethod.getHelperWireMockStubDC(policyNumber, AMOUNT_DEBIT_CARD);

		try {
			// PAS-2719, PAS-1940, PAS-6210, PAS-7858, PAS-352, PAS-3619
			refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_DEBIT_CARD), billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 0, false);
			refundProcessHelper.unissuedRefundVerification(billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 0);
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_DEBIT_CARD, "ManualRefund", BILLING_PAYMENT_METHOD_CARD, false, false);

			// PAS-1939
			refundProcessHelper.voidedManualRefundGeneration(refund);
			refundProcessHelper.voidedRefundVerification(true, billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 1, false);
			// PAS-7057
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_DEBIT_CARD, "RefundPaymentVoided", null, true, false);
		} finally {
			stubRequestDC.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-2719", "PAS-1939", "PAS-7057"})
	public void pas2719_ManualRefundUnissuedVoidedACH(@org.testng.annotations.Optional("MD") String state) throws IllegalAccessException {

		String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "33";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestACH = helperWireMockLastPaymentMethod.getHelperWireMockStubACH(policyNumber, AMOUNT_ACH);

		try {
			// PAS-2719, PAS-1940, PAS-6210, PAS-7858, PAS-352, PAS-3619
			refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_ACH), billingAccountNumber, MESSAGE_ACH, refund, false, 0, false);
			refundProcessHelper.unissuedRefundVerification(billingAccountNumber, MESSAGE_ACH, refund, false, 0);
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_ACH, "ManualRefund", BILLING_PAYMENT_METHOD_ACH, false, false);

			// PAS-1939
			refundProcessHelper.voidedManualRefundGeneration(refund);
			refundProcessHelper.voidedRefundVerification(true, billingAccountNumber, MESSAGE_ACH, refund, false, 1, false);
			// PAS-7057
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_ACH, "RefundPaymentVoided", null, true, false);
		} finally {
			stubRequestACH.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-7063", "PAS-1939", "PAS-7231"})
	public void pas7231_AutomatedRefundUnissuedVoidedCheck(@org.testng.annotations.Optional("VA") String state) {
		LocalDateTime refundTimePoint = getTimePoints().getRefundDate(TimeSetterUtil.getInstance().getCurrentTime());
		String refundDate = refundTimePoint.format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "10.01";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		// PAS-7063, PAS-7858, PAS-453
		refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refundTimePoint, refund, false);
		refundProcessHelper.unissuedRefundVerification(billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0);
		refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CHECK, "AutomatedRefund", BILLING_PAYMENT_METHOD_CHECK, false, false);

		// PAS-1939
		refundProcessHelper.voidedManualRefundGeneration(refund);
		refundProcessHelper.voidedRefundVerification(true, billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 1, false);
		// PAS-7231
		refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CHECK, "RefundPaymentVoided", null, true, false);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-7063", "PAS-1939", "PAS-7231"})
	public void pas7231_AutomatedRefundUnissuedVoidedCreditCard(@org.testng.annotations.Optional("VA") String state) throws IllegalAccessException {
		LocalDateTime refundTimePoint = getTimePoints().getRefundDate(TimeSetterUtil.getInstance().getCurrentTime());
		String refundDate = refundTimePoint.format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "10";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestCC = helperWireMockLastPaymentMethod.getHelperWireMockStubCC(policyNumber, AMOUNT_CREDIT_CARD);

		try {
			// PAS-7063, PAS-7858, PAS-453
			refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refundTimePoint, refund, false);
			refundProcessHelper.unissuedRefundVerification(billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 0);
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CREDIT_CARD, "AutomatedRefund", BILLING_PAYMENT_METHOD_CARD, false, false);

			// PAS-1939
			refundProcessHelper.voidedManualRefundGeneration(refund);
			refundProcessHelper.voidedRefundVerification(true, billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 1, false);
			// PAS-7231
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CREDIT_CARD, "RefundPaymentVoided", null, true, false);
		} finally {
			stubRequestCC.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-7063", "PAS-1939", "PAS-7231"})
	public void pas7231_AutomatedRefundUnissuedVoidedDebitCard(@org.testng.annotations.Optional("AZ") String state) throws IllegalAccessException {
		LocalDateTime refundTimePoint = getTimePoints().getRefundDate(TimeSetterUtil.getInstance().getCurrentTime());
		String refundDate = refundTimePoint.format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "21.99";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestDC = helperWireMockLastPaymentMethod.getHelperWireMockStubDC(policyNumber, AMOUNT_DEBIT_CARD);

		try {
			// PAS-7063, PAS-7858, PAS-453
			refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refundTimePoint, refund, false);
			refundProcessHelper.unissuedRefundVerification(billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 0);
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "AutomatedRefund", BILLING_PAYMENT_METHOD_CARD, false, false);

			// PAS-1939
			refundProcessHelper.voidedManualRefundGeneration(refund);
			refundProcessHelper.voidedRefundVerification(true, billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 1, false);
			// PAS-7231
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "RefundPaymentVoided", null, true, false);
		} finally {
			stubRequestDC.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-7063", "PAS-1936", "PAS-7231"})
	public void pas7063_AutomatedRefundUnissuedVoidedACH(@org.testng.annotations.Optional("MD") String state) throws IllegalAccessException {
		LocalDateTime refundTimePoint = getTimePoints().getRefundDate(TimeSetterUtil.getInstance().getCurrentTime());
		String refundDate = refundTimePoint.format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "30.01";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
		HelperWireMockStub stubRequestACH = helperWireMockLastPaymentMethod.getHelperWireMockStubACH(policyNumber, AMOUNT_ACH);

		try {
			// PAS-7063, PAS-7858, PAS-453
			refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refundTimePoint, refund, false);
			refundProcessHelper.unissuedRefundVerification(billingAccountNumber, MESSAGE_ACH, refund, false, 0);
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "AutomatedRefund", BILLING_PAYMENT_METHOD_ACH, false, false);

			// PAS-1939
			refundProcessHelper.voidedManualRefundGeneration(refund);
			refundProcessHelper.voidedRefundVerification(true, billingAccountNumber, MESSAGE_ACH, refund, false, 1, false);
			// PAS-7231
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "RefundPaymentVoided", null, true, false);
		} finally {
			stubRequestACH.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-1939", "PAS-6152", "PAS-2732", "PAS-450"})
	public void pas1939_ManualRefundUnissuedIssuedVoidedCheck(@org.testng.annotations.Optional("VA") String state) throws IOException {
		String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "10.01";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		refundProcessHelper.unissuedManualRefundGeneration(Optional.empty(), billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0, false);

		// PAS-1939
		refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);
		refundProcessHelper.issuedRefundVerification(billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0);
		// PAS-6152
		refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CHECK, "ManualRefund", BILLING_PAYMENT_METHOD_CHECK, false, false);

		refundProcessHelper.refundRecordInFileCheck(getPolicyType(), policyNumber, "M", "CHCK", "4WUIC", "N", "VA", refundAmount, "test@gmail.com", "Y");

		// PAS-2732
		refundProcessHelper.voidedAutomatedRefundGeneration(true, PAYMENT_METHOD_CHECK, billingAccountNumber, policyNumber);
		refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0, false);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-1939", "PAS-6152", "PAS-2732", "PAS-450"})
	public void pas1939_ManualRefundUnissuedIssuedVoidedCreditCard(@org.testng.annotations.Optional("VA") String state) throws IOException, IllegalAccessException {
		String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "10.00";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestCC = helperWireMockLastPaymentMethod.getHelperWireMockStubCC(policyNumber, AMOUNT_CREDIT_CARD);

		try {
			refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_CREDIT_CARD), billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 0, false);

			// PAS-1939
			refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);
			refundProcessHelper.issuedRefundVerification(billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 0);
			// PAS-6152
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CREDIT_CARD, "ManualRefund", BILLING_PAYMENT_METHOD_CARD, false, false);

			refundProcessHelper.refundRecordInFileCheck(getPolicyType(), policyNumber, "M", "Card", "4WUIC", "N", "VA", refundAmount, "test@gmail.com", "Y");

			// PAS-2732
			refundProcessHelper.voidedAutomatedRefundGeneration(true, PAYMENT_METHOD_CREDIT_CARD, billingAccountNumber, policyNumber);
			refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 2, false);
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CREDIT_CARD, "RefundPaymentVoided", null, true, true);
		} finally {
			stubRequestCC.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-1939", "PAS-6152", "PAS-2732", "PAS-450"})
	public void pas1936_ManualRefundUnissuedIssuedVoidedDebitCard(@org.testng.annotations.Optional("AZ") String state) throws IOException, IllegalAccessException {
		String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "21.99";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestDC = helperWireMockLastPaymentMethod.getHelperWireMockStubDC(policyNumber, AMOUNT_DEBIT_CARD);

		try {
			refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_DEBIT_CARD), billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 0, false);

			// PAS-1939
			refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);
			refundProcessHelper.issuedRefundVerification(billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 0);
			// PAS-6152
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "ManualRefund", BILLING_PAYMENT_METHOD_CARD, false, false);

			refundProcessHelper.refundRecordInFileCheck(getPolicyType(), policyNumber, "M", "Card", "4WUIC", "N", "AZ", refundAmount, "test@gmail.com", "Y");

			// PAS-2732
			refundProcessHelper.voidedAutomatedRefundGeneration(true, PAYMENT_METHOD_DEBIT_CARD, billingAccountNumber, policyNumber);
			refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 2, false);
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "RefundPaymentVoided", null, true, true);
		} finally {
			stubRequestDC.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-1939", "PAS-6152", "PAS-2732", "PAS-450"})
	public void pas1936_ManualRefundUnissuedIssuedVoidedACH(@org.testng.annotations.Optional("MD") String state) throws IOException, IllegalAccessException {
		String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "30.01";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestACH = helperWireMockLastPaymentMethod.getHelperWireMockStubACH(policyNumber, AMOUNT_ACH);
		requestIdList.add(stubRequestACH);

		try {
			refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_ACH), billingAccountNumber, MESSAGE_ACH, refund, false, 0, false);

			// PAS-1939
			refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);
			refundProcessHelper.issuedRefundVerification(billingAccountNumber, MESSAGE_ACH, refund, false, 0);
			// PAS-6152
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "ManualRefund", BILLING_PAYMENT_METHOD_ACH, false, false);

			refundProcessHelper.refundRecordInFileCheck(getPolicyType(), policyNumber, "M", "ACH", "4WUIC", "N", "MD", refundAmount, "test@gmail.com", "Y");

			// PAS-2732
			refundProcessHelper.voidedAutomatedRefundGeneration(true, PAYMENT_METHOD_ACH, billingAccountNumber, policyNumber);
			refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_ACH, refund, false, 2, false);
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "RefundPaymentVoided", null, true, true);
		} finally {
			stubRequestACH.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-6144", "PAS-7193", "PAS-6415"})
	public void pas6415_AutomatedRefundUnissuedIssuedVoidedCheck(@org.testng.annotations.Optional("VA") String state) throws IllegalAccessException {
		LocalDateTime refundTimePoint = getTimePoints().getRefundDate(TimeSetterUtil.getInstance().getCurrentTime());
		String refundDate = refundTimePoint.format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "10.01";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
		HelperWireMockStub stubRequestCC = helperWireMockLastPaymentMethod.getHelperWireMockStubCC(policyNumber, AMOUNT_CREDIT_CARD);
		requestIdList.add(stubRequestCC);

		try {
			refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refundTimePoint, refund, false);

			// PAS-453, PAS-6144
			refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);
			refundProcessHelper.issuedRefundVerification(billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0);
			// PAS-7193
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CHECK, "AutomatedRefund", BILLING_PAYMENT_METHOD_CHECK, false, false);

			// PAS-6415
			refundProcessHelper.voidedAutomatedRefundGeneration(false, PAYMENT_METHOD_CHECK, billingAccountNumber, policyNumber);
			refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0, false);
		} finally {
			stubRequestCC.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-6144", "PAS-7193", "PAS-6415"})
	public void pas6415_AutomatedRefundUnissuedIssuedVoidedCreditCard(@org.testng.annotations.Optional("VA") String state) throws IllegalAccessException {
		LocalDateTime refundTimePoint = getTimePoints().getRefundDate(TimeSetterUtil.getInstance().getCurrentTime());
		String refundDate = refundTimePoint.format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "10.00";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestCC = helperWireMockLastPaymentMethod.getHelperWireMockStubCC(policyNumber, AMOUNT_CREDIT_CARD);

		try {
			refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refundTimePoint, refund, false);

			// PAS-453, PAS-6144
			refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);
			refundProcessHelper.issuedRefundVerification(billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 0);
			// PAS-7193
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CREDIT_CARD, "AutomatedRefund", BILLING_PAYMENT_METHOD_CARD, false, true);

			// PAS-6415
			refundProcessHelper.voidedAutomatedRefundGeneration(false, PAYMENT_METHOD_CREDIT_CARD, billingAccountNumber, policyNumber);
			refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 2, false);
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CREDIT_CARD, "RefundPaymentVoided", null, true, true);
		} finally {
			stubRequestCC.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-6144", "PAS-7193", "PAS-6415"})
	public void pas6415_AutomatedRefundUnissuedIssuedVoidedDebitCard(@org.testng.annotations.Optional("AZ") String state) throws IllegalAccessException {
		LocalDateTime refundTimePoint = getTimePoints().getRefundDate(TimeSetterUtil.getInstance().getCurrentTime());
		String refundDate = refundTimePoint.format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "21.99";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestDC = helperWireMockLastPaymentMethod.getHelperWireMockStubDC(policyNumber, AMOUNT_DEBIT_CARD);

		try {
			refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refundTimePoint, refund, false);

			// PAS-453, PAS-6144
			refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);
			refundProcessHelper.issuedRefundVerification(billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 0);
			// PAS-7193
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "AutomatedRefund", BILLING_PAYMENT_METHOD_CARD, false, false);

			// PAS-6415
			refundProcessHelper.voidedAutomatedRefundGeneration(false, PAYMENT_METHOD_DEBIT_CARD, billingAccountNumber, policyNumber);
			refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 2, false);
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "RefundPaymentVoided", null, true, true);
		} finally {
			stubRequestDC.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-6144", "PAS-7193", "PAS-6415"})
	public void pas6415_AutomatedRefundUnissuedIssuedVoidedACH(@org.testng.annotations.Optional("MD") String state) throws IllegalAccessException {
		LocalDateTime refundTimePoint = getTimePoints().getRefundDate(TimeSetterUtil.getInstance().getCurrentTime());
		String refundDate = refundTimePoint.format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "30.01";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestACH = helperWireMockLastPaymentMethod.getHelperWireMockStubACH(policyNumber, AMOUNT_ACH);

		try {
			refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refundTimePoint, refund, false);

			// PAS-453, PAS-6144
			refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);
			refundProcessHelper.issuedRefundVerification(billingAccountNumber, MESSAGE_ACH, refund, false, 0);
			// PAS-7193
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "AutomatedRefund", BILLING_PAYMENT_METHOD_ACH, false, false);

			// PAS-6415
			refundProcessHelper.voidedAutomatedRefundGeneration(false, PAYMENT_METHOD_ACH, billingAccountNumber, policyNumber);
			refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_ACH, refund, false, 2, false);
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "RefundPaymentVoided", null, true, true);
		} finally {
			stubRequestACH.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-2727")
	public void pas2727_ManualRefundUnissuedIssuedProcessedCheck(@org.testng.annotations.Optional("VA") String state) {
		String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "10.01";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		refundProcessHelper.unissuedManualRefundGeneration(Optional.empty(), billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0, false);

		refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

		// PAS-2727
		refundProcessHelper.processedRefundGeneration(true, PAYMENT_METHOD_CHECK, billingAccountNumber, policyNumber);
		refundProcessHelper.processedRefundVerification(billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0);
		refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CHECK, "ManualRefund", BILLING_PAYMENT_METHOD_CHECK, false, false);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, priority = 1)
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-2728")
	public void pas2728_ManualRefundUnissuedIssuedProcessedCreditCard(@org.testng.annotations.Optional("VA") String state) throws IllegalAccessException {
		String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "10.00";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestCC = helperWireMockLastPaymentMethod.getHelperWireMockStubCC(policyNumber, AMOUNT_CREDIT_CARD);

		try {
			refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_CREDIT_CARD), billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 0, false);

			refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

			// PAS-2728
			refundProcessHelper.processedRefundGeneration(true, PAYMENT_METHOD_CREDIT_CARD, billingAccountNumber, policyNumber);
			refundProcessHelper.processedRefundVerification(billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 0);
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CREDIT_CARD, "ManualRefund", BILLING_PAYMENT_METHOD_CARD, false, false);
		} finally {
			stubRequestCC.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-2728")
	public void pas2728_ManualRefundUnissuedIssuedProcessedDebitCard(@org.testng.annotations.Optional("AZ") String state) throws IllegalAccessException {
		String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "21.99";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestDC = helperWireMockLastPaymentMethod.getHelperWireMockStubDC(policyNumber, AMOUNT_DEBIT_CARD);

		try {
			refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_DEBIT_CARD), billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 0, false);

			refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

			// PAS-2728
			refundProcessHelper.processedRefundGeneration(true, PAYMENT_METHOD_DEBIT_CARD, billingAccountNumber, policyNumber);
			refundProcessHelper.processedRefundVerification(billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 0);
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "ManualRefund", BILLING_PAYMENT_METHOD_CARD, false, false);
		} finally {
			stubRequestDC.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-2728")
	public void pas2728_ManualRefundUnissuedIssuedProcessedACH(@org.testng.annotations.Optional("MD") String state) throws IllegalAccessException {
		String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "30.01";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestACH = helperWireMockLastPaymentMethod.getHelperWireMockStubACH(policyNumber, AMOUNT_ACH);

		try {
			refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_ACH), billingAccountNumber, MESSAGE_ACH, refund, false, 0, false);

			refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

			// PAS-2728
			refundProcessHelper.processedRefundGeneration(true, PAYMENT_METHOD_ACH, billingAccountNumber, policyNumber);
			refundProcessHelper.processedRefundVerification(billingAccountNumber, MESSAGE_ACH, refund, false, 0);
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "ManualRefund", BILLING_PAYMENT_METHOD_ACH, false, false);
		} finally {
			stubRequestACH.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-4251")
	public void pas4251_AutomatedRefundUnissuedIssuedProcessedCheck(@org.testng.annotations.Optional("VA") String state) {
		LocalDateTime refundTimePoint = getTimePoints().getRefundDate(TimeSetterUtil.getInstance().getCurrentTime());
		String refundDate = refundTimePoint.format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "10.01";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refundTimePoint, refund, false);

		refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

		// PAS-4251
		refundProcessHelper.processedRefundGeneration(false, PAYMENT_METHOD_CHECK, billingAccountNumber, policyNumber);
		refundProcessHelper.processedRefundVerification(billingAccountNumber, PAYMENT_METHOD_CHECK, refund, true, 0);
		refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CHECK, "AutomatedRefund", BILLING_PAYMENT_METHOD_CHECK, false, false);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-6144")
	public void pas6144_AutomatedRefundUnissuedIssuedProcessedCreditCard(@org.testng.annotations.Optional("VA") String state) throws IllegalAccessException {
		LocalDateTime refundTimePoint = getTimePoints().getRefundDate(TimeSetterUtil.getInstance().getCurrentTime());
		String refundDate = refundTimePoint.format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "10.00";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestCC = helperWireMockLastPaymentMethod.getHelperWireMockStubCC(policyNumber, AMOUNT_CREDIT_CARD);

		try {
			refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refundTimePoint, refund, false);

			refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

			// PAS-6144, PAS-453
			refundProcessHelper.processedRefundGeneration(false, PAYMENT_METHOD_CREDIT_CARD, billingAccountNumber, policyNumber);
			refundProcessHelper.processedRefundVerification(billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 0);
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, AMOUNT_CREDIT_CARD, "AutomatedRefund", BILLING_PAYMENT_METHOD_CARD, false, false);
		} finally {
			stubRequestCC.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-6144")
	public void pas6144_AutomatedRefundUnissuedIssuedProcessedDebitCard(@org.testng.annotations.Optional("AZ") String state) throws IllegalAccessException {
		LocalDateTime refundTimePoint = getTimePoints().getRefundDate(TimeSetterUtil.getInstance().getCurrentTime());
		String refundDate = refundTimePoint.format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "21.99";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestDC = helperWireMockLastPaymentMethod.getHelperWireMockStubDC(policyNumber, AMOUNT_DEBIT_CARD);

		try {
			refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refundTimePoint, refund, false);

			refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

			// PAS-6144, PAS-453
			refundProcessHelper.processedRefundGeneration(false, PAYMENT_METHOD_DEBIT_CARD, billingAccountNumber, policyNumber);
			refundProcessHelper.processedRefundVerification(billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 0);
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "AutomatedRefund", BILLING_PAYMENT_METHOD_CARD, false, false);
		} finally {
			stubRequestDC.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-6144")
	public void pas6144_AutomatedRefundUnissuedIssuedProcessedACH(@org.testng.annotations.Optional("MD") String state) throws IllegalAccessException {
		LocalDateTime refundTimePoint = getTimePoints().getRefundDate(TimeSetterUtil.getInstance().getCurrentTime());
		String refundDate = refundTimePoint.format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "30.01";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestACH = helperWireMockLastPaymentMethod.getHelperWireMockStubACH(policyNumber, AMOUNT_ACH);

		try {
			refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refundTimePoint, refund, false);

			refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

			// PAS-6144, PAS-453
			refundProcessHelper.processedRefundGeneration(false, PAYMENT_METHOD_ACH, billingAccountNumber, policyNumber);
			refundProcessHelper.processedRefundVerification(billingAccountNumber, MESSAGE_ACH, refund, false, 0);
			refundProcessHelper.getSubLedgerInformation(billingAccountNumber, refund.get(AMOUNT).replace("$", ""), "AutomatedRefund", BILLING_PAYMENT_METHOD_ACH, false, false);
		} finally {
			stubRequestACH.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-455", "PAS-456"})
	public void pas455_ManualRefundVoidedWithAllocationCreditCard(@org.testng.annotations.Optional("VA") String state) throws IllegalAccessException {
		String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "9.00";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestCC = helperWireMockLastPaymentMethod.getHelperWireMockStubCC(policyNumber, AMOUNT_CREDIT_CARD);

		try {
			refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_CREDIT_CARD), billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 0, true);

			refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

			// PAS-455, PAS-456
			refundProcessHelper.voidedAutomatedRefundGeneration(false, PAYMENT_METHOD_CREDIT_CARD, billingAccountNumber, policyNumber);
			refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 2, true);
		} finally {
			stubRequestCC.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, priority = 1)
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-455", "PAS-456"})
	public void pas5743_EnterTooMuchAndGetMessage(@org.testng.annotations.Optional("VA") String state) {
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		assertSoftly(softly -> {
			try {
				HelperWireMockStub stubRequestCC = helperWireMockLastPaymentMethod.getHelperWireMockStubCC(policyNumber, AMOUNT_CREDIT_CARD);
				errorExceedingAmountCheck(MESSAGE_CREDIT_CARD, AMOUNT_CREDIT_CARD, softly);
				stubRequestCC.cleanUp();

				HelperWireMockStub stubRequestDC = helperWireMockLastPaymentMethod.getHelperWireMockStubDC(policyNumber, AMOUNT_DEBIT_CARD);
				errorExceedingAmountCheck(MESSAGE_DEBIT_CARD, AMOUNT_DEBIT_CARD, softly);
				stubRequestDC.cleanUp();

				HelperWireMockStub stubRequestACH = helperWireMockLastPaymentMethod.getHelperWireMockStubACH(policyNumber, AMOUNT_ACH);
				errorExceedingAmountCheck(MESSAGE_ACH, AMOUNT_ACH, softly);
				stubRequestACH.cleanUp();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		});
	}

	private void errorExceedingAmountCheck(String messagePaymentMethod, String amountPaymentMethod, ETCSCoreSoftAssertions softly) {
		billingAccount.refund().start();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).setValue(messagePaymentMethod);
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).setValue(new Dollar(amountPaymentMethod).add(0.01).toString());
		softly.assertThat(acceptPaymentActionTab.getAssetList().getWarning(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel()).getValue())
				.isEqualTo("The amount you entered exceeds the maximum amount for this payment method.");
		acceptPaymentActionTab.submitTab();
		softly.assertThat(acceptPaymentActionTab.getAssetList().getWarning(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel()).getValue())
				.isEqualTo("The amount you entered exceeds the maximum amount for this payment method.");
		acceptPaymentActionTab.cancel();
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-455", "PAS-456"})
	public void pas455_ManualRefundVoidedWithAllocationDebitCard(@org.testng.annotations.Optional("AZ") String state) throws IllegalAccessException {
		String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "21.00";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestDC = helperWireMockLastPaymentMethod.getHelperWireMockStubDC(policyNumber, AMOUNT_DEBIT_CARD);

		try {
			refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_DEBIT_CARD), billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 0, true);

			refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

			// PAS-455, PAS-456
			refundProcessHelper.voidedAutomatedRefundGeneration(false, PAYMENT_METHOD_DEBIT_CARD, billingAccountNumber, policyNumber);
			refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 2, true);
		} finally {
			stubRequestDC.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-455", "PAS-456"})
	public void pas455_ManualRefundVoidedWithAllocationACH(@org.testng.annotations.Optional("MD") String state) throws IllegalAccessException {
		String refundDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "33.00";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Manual Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestACH = helperWireMockLastPaymentMethod.getHelperWireMockStubACH(policyNumber, AMOUNT_ACH);

		try {
			refundProcessHelper.unissuedManualRefundGeneration(Optional.of(AMOUNT_ACH), billingAccountNumber, MESSAGE_ACH, refund, false, 0, true);

			refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

			// PAS-455, PAS-456
			refundProcessHelper.voidedAutomatedRefundGeneration(false, PAYMENT_METHOD_ACH, billingAccountNumber, policyNumber);
			refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_ACH, refund, false, 2, true);
		} finally {
			stubRequestACH.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-456")
	public void pas456_AutomatedRefundVoidedWithAllocationCreditCard(@org.testng.annotations.Optional("VA") String state) throws IllegalAccessException {
		LocalDateTime refundTimePoint = getTimePoints().getRefundDate(TimeSetterUtil.getInstance().getCurrentTime());
		String refundDate = refundTimePoint.format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "9.00";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestCC = helperWireMockLastPaymentMethod.getHelperWireMockStubCC(policyNumber, AMOUNT_CREDIT_CARD);

		try {
			refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refundTimePoint, refund, true);

			refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

			// PAS-456
			refundProcessHelper.voidedAutomatedRefundGeneration(false, PAYMENT_METHOD_CREDIT_CARD, billingAccountNumber, policyNumber);
			refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_CREDIT_CARD, refund, false, 2, true);
		} finally {
			stubRequestCC.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-456")
	public void pas456_AutomatedRefundVoidedWithAllocationDebitCard(@org.testng.annotations.Optional("AZ") String state) throws IllegalAccessException {
		LocalDateTime refundTimePoint = getTimePoints().getRefundDate(TimeSetterUtil.getInstance().getCurrentTime());
		String refundDate = refundTimePoint.format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "21.00";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestDC = helperWireMockLastPaymentMethod.getHelperWireMockStubDC(policyNumber, AMOUNT_DEBIT_CARD);

		try {
			refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refundTimePoint, refund, true);
			refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

			// PAS-456
			refundProcessHelper.voidedAutomatedRefundGeneration(false, PAYMENT_METHOD_DEBIT_CARD, billingAccountNumber, policyNumber);
			refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_DEBIT_CARD, refund, false, 2, true);
		} finally {
			stubRequestDC.cleanUp();
		}
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-456")
	public void pas456_AutomatedRefundVoidedWithAllocationACH(@org.testng.annotations.Optional("MD") String state) throws IllegalAccessException {
		LocalDateTime refundTimePoint = getTimePoints().getRefundDate(TimeSetterUtil.getInstance().getCurrentTime());
		String refundDate = refundTimePoint.format(DateTimeUtils.MM_DD_YYYY);
		String refundAmount = "33.00";
		Map<String, String> refund = refundProcessHelper.getRefundMap(refundDate, "Refund", "Automated Refund", new Dollar(refundAmount), "Approved");
		String policyNumber = policyCreation();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String billingAccountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();

		HelperWireMockStub stubRequestACH = helperWireMockLastPaymentMethod.getHelperWireMockStubACH(policyNumber, AMOUNT_ACH);

		try {
			refundProcessHelper.unissuedAutomatedRefundGeneration(policyNumber, refundTimePoint, refund, true);

			refundProcessHelper.issuedAutomatedRefundGeneration(policyNumber);

			// PAS-456
			refundProcessHelper.voidedAutomatedRefundGeneration(false, PAYMENT_METHOD_ACH, billingAccountNumber, policyNumber);
			refundProcessHelper.voidedRefundVerification(false, billingAccountNumber, MESSAGE_ACH, refund, false, 2, true);
		} finally {
			stubRequestACH.cleanUp();
		}
	}

	// *
	// * See test method for details

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-7298"})
	public void pas7298_pendingManualRefundsCheck(@org.testng.annotations.Optional("VA") String state) {

		String paymentMethod = "Check";

		policyCreation();

		refundProcessHelper.pas7298_pendingManualRefunds(PENDING_REFUND_AMOUNT, APPROVED_REFUND_AMOUNT, paymentMethod);
	}

	// *
	// * See test method for details

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-7298"})
	public void pas7298_pendingManualRefundsCC(@org.testng.annotations.Optional("VA") String state) throws IllegalAccessException {
		testRefundProcessTemplate.pas7298_pendingManualRefundsCC(getState());
	}

	// *
	// * See test method for details

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-7298"})
	public void pas7298_pendingManualRefundsDC(@org.testng.annotations.Optional("AZ") String state) throws IllegalAccessException {

		String paymentMethod = "contains=Debit Card";

		String policyNumber = policyCreation();
		HelperWireMockStub stubRequestDC = helperWireMockLastPaymentMethod.getHelperWireMockStubDC(policyNumber, PENDING_REFUND_AMOUNT);

		try {
			refundProcessHelper.pas7298_pendingManualRefunds(PENDING_REFUND_AMOUNT, APPROVED_REFUND_AMOUNT, paymentMethod);
		} finally {
			stubRequestDC.cleanUp();
		}
	}

	// *
	// * See test method for details

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-7298"})
	public void pas7298_pendingManualRefundsACH(@org.testng.annotations.Optional("MD") String state) throws IllegalAccessException {
		testRefundProcessTemplate.pas7298_pendingManualRefundsACH(getState());
	}

	/**
	 * See test method for details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-7298"})
	public void pas7298_pendingAutomatedRefundsCheck(@org.testng.annotations.Optional("VA") String state) {

		String paymentMethod = "Check";

		String policyNumber = policyCreation();

		refundProcessHelper.pas7298_pendingAutomatedRefunds(policyNumber, APPROVED_REFUND_AMOUNT, PENDING_REFUND_AMOUNT, paymentMethod, getTimePoints());
	}

	// *
	// * See test method for details

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-7298"})
	public void pas7298_pendingAutomatedRefundsCC(@org.testng.annotations.Optional("VA") String state) throws IllegalAccessException {
		testRefundProcessTemplate.pas7298_pendingAutomatedRefundsCC(getState());
	}

	// *
	// * See test method for details

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-7298"})
	public void pas7298_pendingAutomatedRefundsDC(@org.testng.annotations.Optional("AZ") String state) throws IllegalAccessException {

		String paymentMethod = "Debit Card";

		String policyNumber = policyCreation();

		HelperWireMockStub stubRequestDC = helperWireMockLastPaymentMethod.getHelperWireMockStubDC(policyNumber, PENDING_REFUND_AMOUNT);
		requestIdList.add(stubRequestDC);

		try {
			refundProcessHelper.pas7298_pendingAutomatedRefunds(policyNumber, APPROVED_REFUND_AMOUNT, PENDING_REFUND_AMOUNT, paymentMethod, getTimePoints());
		} finally {
			stubRequestDC.cleanUp();
		}
	}

	// *
	// * See test method for details

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-7298"})
	public void pas7298_pendingAutomatedRefundsACH(@org.testng.annotations.Optional("MD") String state) throws IllegalAccessException {
		testRefundProcessTemplate.pas7298_pendingAutomatedRefundsACH(getState());
	}

	/** Not used, because wiremock stub currently doesn't support error response fro LastPaymentMethod
	 /* @author Megha Gubbala
	 /* @name Refund Message when only method is check
	 /* @scenario 1. Create new policy for DC
	 /* 2. go to manual refund
	 /* 3. see the message (Only check refund is currently available)
	 /* 4.see the message for CT (No payment method available for electronic refund)
	 /* @details
	 */
	@Parameters({"state"})
	@Test(enabled = false, groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-1952")
	public void pas1952_MessageWhenOnlyMethodIsCheck(@org.testng.annotations.Optional("DC") String state) {
		eRefundLastPaymentMethodConfigCheck();
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		log.info("policyNumber: {}", policyNumber);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		billingAccount.refund().start();
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.MESSAGE_WHEN_ONLY_PAYMENT_METHOD_CHECK))
				.hasValue("Only check refund is currently available.");
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-1952")
	public void pas1952_MessageWhenOnlyMethodIsCheckNoElectronicRefund(@org.testng.annotations.Optional("CT") String state) {
		eRefundLastPaymentMethodConfigCheck();
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		log.info("policyNumber: {}", policyNumber);

		LastPaymentTemplateData dataACH = LastPaymentTemplateData.create(policyNumber, "100", NON_REFUNDABLE, "Refund Method is mandatory for Manual Refund", EFT, null, null, "1234", null);
		HelperWireMockStub stubRequestACH = HelperWireMockStub.create("last-payment-200", dataACH).mock();
		requestIdList.add(stubRequestACH);

		try {
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
			billingAccount.refund().start();
			assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.MESSAGE_WHEN_ONLY_PAYMENT_METHOD_CHECK))
					.hasValue("No payment method available for electronic refund.");
		} finally {
			stubRequestACH.cleanUp();
		}
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Voided Refund file format for Manual Check Refund
	 * @scenario 1. Create new policy for VA
	 * 2. create a manual refund
	 * 3. process the refund to get it to status Issued
	 * 4. Run aaaRefundCancellationAsyncJob
	 * 5. check file format of the generated file
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-2213"})
	public void pas2213_VoidedManualRefundFileFormatCheck(@org.testng.annotations.Optional("VA") String state) throws IOException {
		String refundAmount = "66.00";

		String policyNumber = policyCreation();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		billingAccount.refund().manualRefundPerform("Check", refundAmount);
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE)).hasValue("Refund");

		JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		refundProcessHelper.approvedRefundVoid();

		mainApp().open();
		SearchPage.openBilling(policyNumber);
		BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains("Type", "Refund").getCell(TYPE).controls.links.get("Refund").click();
		String transactionID = acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_ID.getLabel(), StaticElement.class).getValue();
		JobUtils.executeJob(Jobs.aaaRefundCancellationAsyncJob);
		refundProcessHelper.refundVoidRecordInFileCheck(policyNumber, transactionID, "PA", "4WUIC", refundAmount);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Voided Refund file format for Manual Check Refund
	 * @scenario 1. Create new policy for VA
	 * 2. create an overpayment and automated refund
	 * 3. process the refund to get it to status Issued
	 * 4. Run aaaRefundCancellationAsyncJob
	 * 5. check file format of the generated file
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = {"PAS-2213"})
	public void pas2213_VoidedAutomatedRefundFileFormatCheck(@org.testng.annotations.Optional("VA") String state) throws IOException {
		String refundAmount = "66.00";

		String policyNumber = policyCreation();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Dollar totalDue1 = BillingSummaryPage.getTotalDue();
		TestData tdBilling = testDataManager.billingAccount;
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue1.add(new Dollar(refundAmount)));
		LocalDateTime refundDate = getTimePoints().getRefundDate(DateTimeUtils.getCurrentDateTime());
		TimeSetterUtil.getInstance().nextPhase(refundDate);
		JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);

		JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		refundProcessHelper.approvedRefundVoid();

		mainApp().open();
		SearchPage.openBilling(policyNumber);
		BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains("Type", "Refund").getCell(TYPE).controls.links.get("Refund").click();
		String transactionID = acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_ID.getLabel(), StaticElement.class).getValue();
		JobUtils.executeJob(Jobs.aaaRefundCancellationAsyncJob);
		refundProcessHelper.refundVoidRecordInFileCheck(policyNumber, transactionID, "PA", "4WUIC", refundAmount);
	}

	private String policyCreation() {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		log.info("policyNumber: {}", policyNumber);
		return policyNumber;
	}

	@AfterSuite(alwaysRun = true)
	private void deleteMultipleLastPaymentRequests() {
		testRefundProcessTemplate.deleteMultiplePaperlessPreferencesRequests();
	}

}
