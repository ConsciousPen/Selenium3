package aaa.modules.regression.finance.template;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoUnit.DAYS;
import static toolkit.verification.CustomAssertions.assertThat;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.Job;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.LedgerHelper;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssertions;

public abstract class FinanceOperations extends PolicyBaseTest {

	private static final String CANCELLATION = "cancellation";
	private static final BigDecimal TOLERANCE_AMOUNT = new BigDecimal(7);
	private static final String ZERO = "0";

	BillingAccount billingAccount = new BillingAccount();
	TestData tdBilling = testDataManager.billingAccount;

	/**
	 * @author Reda Kazlauskiene
	 * @name Test Escheatment transaction creation
	 * @scenario 1. Create Annual Policy
	 * 2. Pay $25 more than full with check
	 * 3. Refund 25$ with check - run *aaaRefundGenerationAsyncJob*
	 * 4. Run *aaaRefundDisbursementAsyncJob* to make refund status to issued
	 * 5. Turn time for more than a year of Refund
	 * 6. Run Esheatment async job at the beginning of the month:  *aaaEscheatmentProcessAsyncJob*
	 * 7. Navigate to BA
	 */

	protected String createEscheatmentTransaction() {
		String policyNumber = openAppAndCreatePolicy();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Check"), new Dollar(25));

		LocalDateTime paymentDate = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime refundDate = getTimePoints().getRefundDate(paymentDate);
		TimeSetterUtil.getInstance().nextPhase(refundDate);
		JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);
		JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getStartTime().plusMonths(13));

		JobUtils.executeJob(Jobs.aaaEscheatmentProcessAsyncJob);

		return policyNumber;
	}

	/**
	 * @author Reda Kazlauskiene
	 * @name Run earnedPremiumPostingAsyncTaskGenerationJob and shift to next month until provided date
	 */
	protected LocalDateTime runEPJobUntil(LocalDateTime jobDate, LocalDateTime until, Job jobName) {
		while (!until.isBefore(jobDate)) {
			TimeSetterUtil.getInstance().nextPhase(jobDate);
			JobUtils.executeJob(jobName);
			jobDate = jobDate.plusMonths(1).withDayOfMonth(1);
		}
		return jobDate;
	}

	/**
	 * @author Reda Kazlauskiene
	 * @name Create Endorsement with specific TestDate and Effective date
	 */
	protected void createEndorsement(LocalDateTime effectiveDate, String testDataName) {
		policy.createEndorsement(getTestSpecificTD(testDataName)
				.adjust(getPolicyTD("Endorsement", "TestData")).resolveLinks()
				.adjust("EndorsementActionTab|Endorsement Date",
						effectiveDate.format(DateTimeUtils.MM_DD_YYYY)));
	}

	protected void createEndorsement(int daysToEffective, String testDataName) {
		createEndorsement(TimeSetterUtil.getInstance().getCurrentTime().plusDays(daysToEffective), testDataName);
	}

	/**
	 * @author Maksim Piatrouski
	 * @name Roll Back Endorsement with specific Effective date
	 */
	protected void rollBackEndorsement(LocalDateTime effectiveDate) {
		policy.rollBackEndorsement().perform(getPolicyTD("EndorsementRollBack", "TestData")
				.adjust("RollBackEndorsementActionTab|Endorsement Roll Back Date",
						effectiveDate.format(DateTimeUtils.MM_DD_YYYY)));
	}

	public static void main(String[] args) {
		FinanceOperations financeOperations = new FinanceOperations() {};
		financeOperations.test();
	}

	/**
	 * @author Maksim Piatrouski
	 * @name Cancel Policy with specific Effective date
	 */
	protected void cancelPolicy(LocalDateTime effectiveDate, PolicyType policyType) {
		if (policyType.equals(PolicyType.HOME_CA_HO3)) {
			policy.cancel().perform(getPolicyTD("Cancellation", "TestData")
					.adjust("CancelActionTab|Cancellation effective date",
							effectiveDate.format(DateTimeUtils.MM_DD_YYYY)));
			return;
		}
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData")
				.adjust("CancellationActionTab|Cancel Date",
						effectiveDate.format(DateTimeUtils.MM_DD_YYYY)));
	}

	/**
	 * @author Maksim Piatrouski
	 * @name Reinstate Policy with specific Effective date
	 */
	protected void reinstatePolicy(LocalDateTime effectiveDate) {
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData")
				.adjust("ReinstatementActionTab|Reinstate Date",
						effectiveDate.format(DateTimeUtils.MM_DD_YYYY)));
	}

	protected void reinstatePolicy(int daysToEffective) {
		reinstatePolicy(TimeSetterUtil.getInstance().getCurrentTime().plusDays(daysToEffective));
	}

	protected void cancelPolicy(int daysToEffective, PolicyType policyType) {
		cancelPolicy(TimeSetterUtil.getInstance().getCurrentTime().plusDays(daysToEffective), policyType);
	}

	/**
	 * @author Reda Kazlauskiene
	 * @name Renew policy
	 */
	protected void renewalImageGeneration(LocalDateTime policyExpirationDate, String policyNum) {
		LocalDateTime renewDateImage = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateImage);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicyHelper.verifyAutomatedRenewalGenerated(renewDateImage);
	}

	protected void renewalPreviewGeneration(LocalDateTime policyExpirationDate, String policyNum) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		searchForPolicy(policyNum);
		CustomAssertions.assertThat(PolicySummaryPage.buttonRenewals).isEnabled();

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	protected void renewalOfferGeneration(LocalDateTime policyExpirationDate, String policyNum) {
		LocalDateTime renewDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateOffer);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		searchForPolicy(policyNum);

		CustomAssertions.assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
	}

	protected void generateRenewalBill(LocalDateTime policyExpirationDate, LocalDateTime policyEffectiveDate, String policyNum) {
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	protected void payRenewalBill(LocalDateTime policyExpirationDate, String policyNum) {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(policyExpirationDate);
		if (getState().equals(Constants.States.CA)) {
			billDueDate = policyExpirationDate; //avoid switch to Monday, Renewal bill should be payed before policyStatusUpdateJob
		}
		TimeSetterUtil.getInstance().nextPhase(billDueDate);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(policyExpirationDate, BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().verifyManualPaymentAccepted(DateTimeUtils.getCurrentDateTime(), minDue.negate());
	}

	protected void updatePolicyStatus(LocalDateTime policyExpirationDate, LocalDateTime policyEffectiveDate, String policyNum) {
		LocalDateTime updateStatusDate = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(updateStatusDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	protected void validateEPCalculations(String policyNumber, List<TxType> txTypes, LocalDateTime effectiveDate, LocalDateTime expirationDate) {
		List<TxWithTermPremium> txsWithPremium = createTxsWithPremiums(policyNumber, txTypes);
		validateEPCalculationsFromTransactions(policyNumber, txsWithPremium, effectiveDate.toLocalDate(), expirationDate.toLocalDate());
	}

	protected List<TxWithTermPremium> createTxsWithPremiums(String policyNumber, List<TxType> txTypes) {
		List<TxWithTermPremium> txsWithPremium = new ArrayList<>();
		List<Map<String, String>> termAndActualPremiums = LedgerHelper.getTermAndActualPremiums(policyNumber);
		assertThat(txTypes.size()).as("Provided transaction type list do not match actual transactions").isEqualTo(termAndActualPremiums.size());
		for (int i = 0; i < termAndActualPremiums.size(); i++) {
			Map<String, String> termAndActualPremium = termAndActualPremiums.get(i);
			TxType txType = txTypes.get(i);
			String persistedType = termAndActualPremium.get(LedgerHelper.TXTYPE);
			String termPremium = CANCELLATION.equals(persistedType) ? ZERO : termAndActualPremium.get(LedgerHelper.TERM_PREMIUM);
			String actualPremium = termAndActualPremium.get(LedgerHelper.ACTUAL_PREMIUM);
			LocalDate txDate = LocalDate.parse(termAndActualPremium.get(LedgerHelper.TRANSACTION_DATE), LedgerHelper.DATE_TIME_FORMATTER);
			LocalDate txEffectiveDate = LocalDate.parse(termAndActualPremium.get(LedgerHelper.TRANSACTION_EFFECTIVE_DATE), LedgerHelper.DATE_TIME_FORMATTER);
			txsWithPremium.add(new TxWithTermPremium(txType, termPremium, actualPremium, txDate, txEffectiveDate));
		}
		return txsWithPremium;
	}

	protected void validateEPCalculationsFromTransactions(String policyNumber, List<TxWithTermPremium> txsWithPremium, LocalDate effectiveDate, LocalDate expirationDate) {

		List<LocalDate> monthlyTimePoints = new ArrayList<>(13);
		LocalDate monthlyTimePoint = effectiveDate;
		while (!expirationDate.isBefore(monthlyTimePoint)) {
			monthlyTimePoints.add(monthlyTimePoint);
			try {
				monthlyTimePoint = monthlyTimePoint.plusMonths(1).with(DAY_OF_MONTH, effectiveDate.getDayOfMonth());
			} catch (DateTimeException dte) {
				monthlyTimePoint = monthlyTimePoint.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
			}
		}

		List<PeriodFactor> periodFactors = new ArrayList<>();
		for (LocalDate timePoint : monthlyTimePoints) {
			LocalDate jobDate = timePoint.plusMonths(1).with(DAY_OF_MONTH, 1);
			LocalDate start = timePoint.equals(effectiveDate) ? effectiveDate : timePoint.with(TemporalAdjusters.firstDayOfMonth());
			LocalDate end = timePoint.equals(expirationDate) ? expirationDate.minusDays(1) : timePoint.with(TemporalAdjusters.lastDayOfMonth());
			periodFactors.add(new PeriodFactor(jobDate, start, end, effectiveDate, expirationDate));
		}

		Map<TxWithTermPremium, Map<LocalDate, BigDecimal>> calculatedEarnedPremiums = new LinkedHashMap<>();
		for (TxWithTermPremium txWithPremium : txsWithPremium) {
			List<PeriodFactor> periodFactorsFrom = periodFactors.subList(getTxPeriodIndex(periodFactors, txWithPremium), periodFactors.size());
			switch (txWithPremium.getTxType()) {
				case ISSUE:
					Map<LocalDate, BigDecimal> ep = calculateEPForNormal(txWithPremium, effectiveDate, expirationDate, periodFactorsFrom);
					calculatedEarnedPremiums.put(txWithPremium, ep);
					break;
				case ENDORSE:
					Map<LocalDate, BigDecimal> epForEndorsement = calculateEpForEndorsement(
							txWithPremium, txsWithPremium, effectiveDate, expirationDate, periodFactorsFrom);
					calculatedEarnedPremiums.put(txWithPremium, epForEndorsement);
					break;
				case CANCEL:
					calculateEpForOosCancel(
							txWithPremium, txsWithPremium, effectiveDate, expirationDate, periodFactorsFrom, calculatedEarnedPremiums);
					break;
				case REINSTATE:
					calculateEpForReinstatement(
							txWithPremium, txsWithPremium, effectiveDate, expirationDate, periodFactorsFrom, calculatedEarnedPremiums);
					break;
				case OOS_ENDORSE:
					Map<LocalDate, BigDecimal> epForOosEndorsement = calculateEpForEndorsement(
							txWithPremium, txsWithPremium, effectiveDate, expirationDate, periodFactorsFrom);
					calculatedEarnedPremiums.put(txWithPremium, epForOosEndorsement);
					break;
				case ROLL_ON:
					calculateEpForRollOn(
							txWithPremium, txsWithPremium, effectiveDate, expirationDate, periodFactorsFrom, calculatedEarnedPremiums);
					break;
				case OOS_CANCEL:
					calculateEpForOosCancel(
							txWithPremium, txsWithPremium, effectiveDate, expirationDate, periodFactorsFrom, calculatedEarnedPremiums);
					break;
				case REINSTATE_LAPSE:
					Map<LocalDate, BigDecimal> epForReinstateLapse = calculateEpForEndorsement(
							txWithPremium, txsWithPremium, effectiveDate, expirationDate, periodFactorsFrom);
					calculatedEarnedPremiums.put(txWithPremium, epForReinstateLapse);
					break;
				case ROLL_ON_CANCEL:
					calculateEpForRollOn(
							txWithPremium, txsWithPremium, effectiveDate, expirationDate, periodFactorsFrom, calculatedEarnedPremiums);
					break;
				case ROLL_ON_REINSTATE:
					calculateEpForRollOn(
							txWithPremium, txsWithPremium, effectiveDate, expirationDate, periodFactorsFrom, calculatedEarnedPremiums);
					break;
				case ROLL_BACK:
					calculateEPForRollBack(txWithPremium, txsWithPremium, effectiveDate, expirationDate, periodFactorsFrom, calculatedEarnedPremiums);
					break;
			}
		}
		Map<LocalDate, BigDecimal> finalEp = new LinkedHashMap<>();
		for (int i = 0; i < txsWithPremium.size(); i++) {
			TxWithTermPremium currentTx = txsWithPremium.get(i);
			Map<LocalDate, BigDecimal> ep = calculatedEarnedPremiums.get(currentTx);
			ArrayList<LocalDate> jobDates = new ArrayList<>(ep.keySet());

			if (i + 1 == txsWithPremium.size()) {
				for (LocalDate jobDate : jobDates) {
					if (jobDate.isBefore(currentTx.getTxDate())) {
						continue;
					}
					finalEp.put(jobDate, ep.get(jobDate));
				}
				break;
			}

			TxWithTermPremium nextTx = txsWithPremium.get(i + 1);
			for (LocalDate jobDate : jobDates) {
				if (jobDate.isAfter(nextTx.getTxDate()) || finalEp.containsKey(jobDate)) {
					continue;
				}
				finalEp.put(jobDate, ep.get(jobDate));
			}
		}

		Map<LocalDate, BigDecimal> epFromDb = LedgerHelper.getMonthlyEarnedPremiumAmounts(policyNumber);
		for (PeriodFactor periodFactor : periodFactors) {
			BigDecimal postedEP = epFromDb.get(periodFactor.getJobDate());
			if (postedEP == null) {
				epFromDb.put(periodFactor.getJobDate(), BigDecimal.ZERO);
			}
		}

		txsWithPremium.forEach(tx -> log.info(tx.toString()));
		log.info("Model calculations");
		finalEp.entrySet().forEach(entry -> log.info(entry.toString()));
		log.info("Actual posted EP");
		epFromDb.entrySet().forEach(entry -> log.info(entry.toString()));

		for (PeriodFactor factor : periodFactors) {
			LocalDate epDate = factor.getJobDate();
			BigDecimal modelAmt = finalEp.get(epDate);
			BigDecimal postedAmt = epFromDb.get(epDate);
			assertThat(modelAmt.subtract(postedAmt).abs().compareTo(TOLERANCE_AMOUNT) < 1)
					.as(String.format("Earned premium posted on %s did not meet the tolerance amount.\n" +
									"Expected result: %s\n" +
									"Actual result: %s\n" +
									"Tolerance amount: %s",
							epDate, modelAmt, postedAmt, TOLERANCE_AMOUNT))
					.isTrue();
		}
	}

	private void test() {
		List<TxWithTermPremium> premiums = new ArrayList<>();

		premiums.add(new TxWithTermPremium(TxType.ISSUE, 1344, 1344, LocalDate.of(2018, 11, 8), LocalDate.of(2018, 11, 8)));
		premiums.add(new TxWithTermPremium(TxType.ENDORSE, 1219, 1239, LocalDate.of(2019, 1, 9), LocalDate.of(2019, 1, 8)));
		premiums.add(new TxWithTermPremium(TxType.ENDORSE, 1376, 1344, LocalDate.of(2019, 3, 11), LocalDate.of(2019, 3, 10)));
		premiums.add(new TxWithTermPremium(TxType.OOS_ENDORSE, 1265, 1273, LocalDate.of(2019, 5, 14), LocalDate.of(2019, 2, 8)));
		premiums.add(new TxWithTermPremium(TxType.ROLL_ON, 1376, 1347, LocalDate.of(2019, 5, 14), LocalDate.of(2019, 3, 10)));
		validateEPCalculationsFromTransactions("asd", premiums, LocalDate.of(2018, 11, 8), LocalDate.of(2019, 11, 8));

		/*premiums.add(new TxWithTermPremium(TxType.ISSUE, new BigDecimal(405), LocalDate.of(2018, 8, 28), LocalDate.of(2018, 8, 28)));
		premiums.add(new TxWithTermPremium(TxType.ENDORSE, new BigDecimal(333), LocalDate.of(2018, 10, 29), LocalDate.of(2018, 10, 28)));
		premiums.add(new TxWithTermPremium(TxType.ENDORSE, new BigDecimal(450), LocalDate.of(2018, 12, 29), LocalDate.of(2018, 12, 28)));
		premiums.add(new TxWithTermPremium(TxType.OOS_ENDORSE, new BigDecimal(333), LocalDate.of(2019, 3, 2), LocalDate.of(2018, 11, 28)));
		premiums.add(new TxWithTermPremium(TxType.ROLL_ON, new BigDecimal(450), LocalDate.of(2019, 3, 2), LocalDate.of(2018, 12, 28)));
		validateEPCalculationsFromTransactions(premiums, LocalDate.of(2018, 8, 28), LocalDate.of(2019, 8, 28));

		/*premiums.add(new TxWithTermPremium(TxType.ISSUE, 2565, 2565, LocalDate.of(2018, 10, 15), LocalDate.of(2018, 10, 15)));
		premiums.add(new TxWithTermPremium(TxType.ENDORSE, 3761, 2873, LocalDate.of(2019, 7, 15), LocalDate.of(2019, 7, 13)));
		premiums.add(new TxWithTermPremium(TxType.ENDORSE, 3761, 2873, LocalDate.of(2019, 7, 15), LocalDate.of(2019, 7, 13)));
		premiums.add(new TxWithTermPremium(TxType.OOS_ENDORSE, 1448, 2242, LocalDate.of(2019, 8, 15), LocalDate.of(2019, 7, 1)));
		premiums.add(new TxWithTermPremium(TxType.ROLL_ON, 2434, 2496, LocalDate.of(2019, 8, 15), LocalDate.of(2019, 7, 13)));
		premiums.add(new TxWithTermPremium(TxType.ROLL_ON, 2434, 2496, LocalDate.of(2019, 8, 15), LocalDate.of(2019, 7, 13)));
		validateEPCalculationsFromTransactions(premiums, LocalDate.of(2018, 10, 15), LocalDate.of(2019, 10, 15));*/

        /*premiums.add(new TxWithTermPremium(TxType.ISSUE, 397, 397, LocalDate.of(2018, 8, 28), LocalDate.of(2018, 8, 28)));
        premiums.add(new TxWithTermPremium(TxType.ENDORSE, 401, 399.67, LocalDate.of(2018, 12, 29), LocalDate.of(2018, 12, 28)));
        premiums.add(new TxWithTermPremium(TxType.OOS_CANCEL, 0, 33.72, LocalDate.of(2019, 2, 2), LocalDate.of(2018, 9, 28)));
        premiums.add(new TxWithTermPremium(TxType.REINSTATE, 329, 334.78, LocalDate.of(2019, 3, 2), LocalDate.of(2018, 9, 28)));
        validateEPCalculationsFromTransactions("asd", premiums, LocalDate.of(2018, 8, 28), LocalDate.of(2019, 8, 28));*/

        /*premiums.add(new TxWithTermPremium(TxType.ISSUE, 1344, 1344, LocalDate.of(2018, 11, 7), LocalDate.of(2018, 11, 7)));
        premiums.add(new TxWithTermPremium(TxType.ENDORSE, 1493, 1467, LocalDate.of(2019, 1, 8), LocalDate.of(2019, 1, 7)));
        premiums.add(new TxWithTermPremium(TxType.ENDORSE, 1180, 1233, LocalDate.of(2019, 2, 8), LocalDate.of(2019, 2, 7)));
        premiums.add(new TxWithTermPremium(TxType.ENDORSE, 1211, 1239, LocalDate.of(2019, 9, 8), LocalDate.of(2019, 9, 7)));
        premiums.add(new TxWithTermPremium(TxType.ROLL_BACK, 1344, 1344, LocalDate.of(2019, 9, 11), LocalDate.of(2018, 11, 7)));
        validateEPCalculationsFromTransactions("asd", premiums, LocalDate.of(2018, 11, 7), LocalDate.of(2019, 11, 7));*/

	}

	private void calculateEPForRollBack(TxWithTermPremium currentTx, List<TxWithTermPremium> txsWithPremium, LocalDate effectiveDate, LocalDate expirationDate, List<PeriodFactor> periodFactorsFrom, Map<TxWithTermPremium, Map<LocalDate, BigDecimal>> calculatedEarnedPremiums) {
		Map<LocalDate, BigDecimal> ep = calculateEpForEndorsement(currentTx, txsWithPremium, effectiveDate, expirationDate, periodFactorsFrom);
		calculatedEarnedPremiums.put(currentTx, ep);
		addDifferenceBetweenActualAndWouldBe(currentTx, txsWithPremium, periodFactorsFrom, calculatedEarnedPremiums);
	}

	private void calculateEpForReinstatement(TxWithTermPremium currentTx, List<TxWithTermPremium> txsWithPremium, LocalDate effectiveDate, LocalDate expirationDate, List<PeriodFactor> periodFactorsFrom, Map<TxWithTermPremium, Map<LocalDate, BigDecimal>> calculatedEarnedPremiums) {
		Map<LocalDate, BigDecimal> ep = calculateEPForNormal(currentTx, effectiveDate, expirationDate, periodFactorsFrom);
		calculatedEarnedPremiums.put(currentTx, ep);
		addDifferenceBetweenActualAndWouldBe(currentTx, txsWithPremium, periodFactorsFrom, calculatedEarnedPremiums);
	}

	private void calculateEpForOosCancel(TxWithTermPremium currentTx, List<TxWithTermPremium> txsWithPremium, LocalDate effectiveDate, LocalDate expirationDate, List<PeriodFactor> periodFactors, Map<TxWithTermPremium, Map<LocalDate, BigDecimal>> calculatedEarnedPremiums) {
		Map<LocalDate, BigDecimal> ep = calculateEPForNormal(currentTx, effectiveDate, expirationDate, periodFactors);
		calculatedEarnedPremiums.put(currentTx, ep);
		PeriodFactor effectivePeriodFactor = getPeriodFactorUpToEffective(currentTx, periodFactors);
		PeriodFactor periodFactor = getPeriodFactorUpTo(currentTx, periodFactors);
		BigDecimal deviation = calculateStandardDeviation(currentTx, txsWithPremium, effectivePeriodFactor);
		ep.put(periodFactor.getJobDate(), ep.get(periodFactor.getJobDate()).add(deviation));
		addDifferenceBetweenActualAndWouldBe(currentTx, txsWithPremium, periodFactors, calculatedEarnedPremiums);
	}

	private void calculateEpForRollOn(TxWithTermPremium currentTx, List<TxWithTermPremium> txsWithPremium, LocalDate effectiveDate, LocalDate expirationDate, List<PeriodFactor> periodFactors, Map<TxWithTermPremium, Map<LocalDate, BigDecimal>> calculatedEarnedPremiums) {
		Map<LocalDate, BigDecimal> epForOos = calculateEpForEndorsement(currentTx, txsWithPremium, effectiveDate, expirationDate, periodFactors);
		calculatedEarnedPremiums.put(currentTx, epForOos);
		addDifferenceBetweenActualAndWouldBe(currentTx, txsWithPremium, periodFactors, calculatedEarnedPremiums);
	}

	private void addDifferenceBetweenActualAndWouldBe(TxWithTermPremium currentTx, List<TxWithTermPremium> txsWithPremium, List<PeriodFactor> periodFactors, Map<TxWithTermPremium, Map<LocalDate, BigDecimal>> calculatedEarnedPremiums) {
		List<TxWithTermPremium> txsUpToCurrent = new ArrayList<>(txsWithPremium.subList(0, txsWithPremium.indexOf(currentTx)));
		txsUpToCurrent.add(currentTx);
		List<TxWithTermPremium> txsBefore = txsUpToCurrent.stream().filter(tx -> tx.getTxDate().withDayOfMonth(1).isBefore(currentTx.getTxDate().withDayOfMonth(1))).collect(Collectors.toList());
		List<TxWithTermPremium> txsWithCurrent = txsUpToCurrent.stream().filter(tx -> !tx.getTxDate().isAfter(currentTx.getTxDate())).collect(Collectors.toList());
		BigDecimal actualTotal = getActualTotal(currentTx, calculatedEarnedPremiums, txsBefore);
		BigDecimal wouldBeTotal = getWouldBeTotal(currentTx, periodFactors, calculatedEarnedPremiums, txsWithCurrent);
		BigDecimal oosDelta = wouldBeTotal.subtract(actualTotal);
		LocalDate jobDateUpTo = getPeriodFactorUpTo(currentTx, periodFactors).getJobDate();
		Map<LocalDate, BigDecimal> ep = calculatedEarnedPremiums.get(currentTx);
		ep.put(jobDateUpTo, ep.get(jobDateUpTo).add(oosDelta));
	}

	private BigDecimal getActualTotal(TxWithTermPremium currentTx, Map<TxWithTermPremium, Map<LocalDate, BigDecimal>> calculatedEarnedPremiums, List<TxWithTermPremium> transactions) {
		Map<LocalDate, BigDecimal> actualPremiums = new LinkedHashMap<>();
		for (int i = 0; i < transactions.size(); i++) {
			TxWithTermPremium processedTx = transactions.get(i);
			Map<LocalDate, BigDecimal> ep = calculatedEarnedPremiums.get(processedTx);
			TxWithTermPremium nextTx = (i + 1) < transactions.size() ? transactions.get(i + 1) : null;

			ep.entrySet().stream()
					.filter(entry -> (nextTx == null || entry.getKey().isBefore(nextTx.getTxDate().plusMonths(1).withDayOfMonth(1)))
							&& entry.getKey().isBefore(currentTx.getTxDate().plusMonths(1).withDayOfMonth(1)))
					.forEach(entry -> actualPremiums.putIfAbsent(entry.getKey(), entry.getValue()));
		}

		return actualPremiums.entrySet().stream().map(entry -> entry.getValue()).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private BigDecimal getWouldBeTotal(TxWithTermPremium currentTx, List<PeriodFactor> periodFactors, Map<TxWithTermPremium, Map<LocalDate, BigDecimal>> calculatedEarnedPremiums, List<TxWithTermPremium> transactions) {
		List<Integer> indexes = new ArrayList<>();
		for (int i = 0; i < transactions.size(); i++) {
			TxWithTermPremium processedTx = transactions.get(i);
			if ((i + 1) == transactions.size()) {
				indexes.add(i);
				break;
			}
			TxWithTermPremium nextTx = transactions.get(i + 1);
			if (processedTx.getTxEffectiveDate().isBefore(nextTx.getTxEffectiveDate())) {
				indexes.add(i);
			}
		}
		List<Map<LocalDate, BigDecimal>> filteredEps = indexes.stream().map(transactions::get).map(calculatedEarnedPremiums::get).collect(Collectors.toList());
		Map<LocalDate, BigDecimal> wouldBeAmounts = new LinkedHashMap<>();
		for (Map<LocalDate, BigDecimal> filteredEp : filteredEps) {
			wouldBeAmounts.putAll(filteredEp);
		}
		LocalDate jobDate = getPeriodFactorUpTo(currentTx, periodFactors).getJobDate();
		return wouldBeAmounts.entrySet().stream().filter(entry -> entry.getKey().isBefore(jobDate)).map(entry -> entry.getValue()).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private PeriodFactor getPeriodFactorUpTo(TxWithTermPremium currentTx, List<PeriodFactor> periodFactors) {
		for (PeriodFactor periodFactor : periodFactors) {
			if (periodFactor.isDateInPeriod(currentTx.getTxDate())) {
				return periodFactor;
			}
		}
		throw new IllegalArgumentException("Current tx is out of bounds for all periods");
	}

	private PeriodFactor getPeriodFactorUpToEffective(TxWithTermPremium currentTx, List<PeriodFactor> periodFactors) {
		for (PeriodFactor periodFactor : periodFactors) {
			if (periodFactor.isDateInPeriod(currentTx.getTxEffectiveDate())) {
				return periodFactor;
			}
		}
		throw new IllegalArgumentException("Current tx is out of bounds for all periods");
	}

	private int getTxPeriodIndex(List<PeriodFactor> periodFactors, TxWithTermPremium txWithPremium) {
		LocalDate txEffectiveDate = txWithPremium.getTxEffectiveDate();
		int index = -1;
		for (PeriodFactor periodFactor : periodFactors) {
			if (periodFactor.isDateInPeriod(txEffectiveDate)) {
				index = periodFactors.indexOf(periodFactor);
				break;
			}
		}
		if (index == -1) {
			throw new IllegalArgumentException("Transaction effective date is in none reporting periods");
		}
		return index;
	}

	private Map<LocalDate, BigDecimal> calculateEpForEndorsement(TxWithTermPremium tx, List<TxWithTermPremium> txsWithPremium, LocalDate effective, LocalDate expiration, List<PeriodFactor> periodFactors) {
		Map<LocalDate, BigDecimal> results = new LinkedHashMap<>();
		results.put(periodFactors.get(0).getJobDate(), calculateStandardDeviation(tx, txsWithPremium, periodFactors.get(0)));
		results.putAll(calculateEPForNormal(tx, effective, expiration, periodFactors.subList(1, periodFactors.size())));
		return results;
	}

	private BigDecimal calculateStandardDeviation(TxWithTermPremium tx, List<TxWithTermPremium> txsWithPremium, PeriodFactor periodFactor) {
		TxWithTermPremium effectiveTxBefore = getEffectiveTxBefore(tx, txsWithPremium);
		BigDecimal startPrem = effectiveTxBefore.getTermPremium().multiply(periodFactor.getStartFactor());
		BigDecimal endPrem = tx.getTermPremium().multiply(periodFactor.getEndFactor());
		BigDecimal actualStartPrem = effectiveTxBefore.getActualPremium().subtract(startPrem);
		BigDecimal actualEndPrem = tx.getActualPremium().subtract(endPrem);
		return actualEndPrem.subtract(actualStartPrem).setScale(2, ROUND_HALF_UP);
	}

	private Map<LocalDate, BigDecimal> calculateEPForNormal(TxWithTermPremium tx, LocalDate effective, LocalDate expiration, List<PeriodFactor> periodFactors) {
		Map<LocalDate, BigDecimal> results = new LinkedHashMap<>();
		for (PeriodFactor period : periodFactors) {
			BigDecimal ep = getStandardEP(tx, effective, expiration, period);
			results.put(period.getJobDate(), ep);
		}
		return results;
	}

	private BigDecimal getStandardEP(TxWithTermPremium tx, LocalDate effective, LocalDate expiration, PeriodFactor period) {
		return new BigDecimal(period.getDuration())
				.divide(new BigDecimal(DAYS.between(effective, expiration)), 16, ROUND_HALF_UP)
				.multiply(tx.getTermPremium()).setScale(2, ROUND_HALF_UP);
	}

	private TxWithTermPremium getEffectiveTxBefore(TxWithTermPremium currentTx, List<TxWithTermPremium> transactions) {
		int index = transactions.indexOf(currentTx);
		if (index == 0) {
			return null;
		}
		for (int i = index; i >= 0; i--) {
			TxWithTermPremium tx = transactions.get(i);
			if (tx.getTxEffectiveDate().withDayOfMonth(1).isBefore(currentTx.getTxEffectiveDate().withDayOfMonth(1))) {
				return tx;
			}
		}
		return transactions.get(0);
	}

	protected enum TxType {
		ISSUE(true, false),
		ENDORSE(true, true),
		CANCEL(false, true),
		REINSTATE(true, false),
		OOS_ENDORSE(true, true),
		ROLL_ON(true, true),
		OOS_CANCEL(false, true),
		REINSTATE_LAPSE(true, false),
		ROLL_ON_CANCEL(false, true),
		ROLL_ON_REINSTATE(true, false),
		ROLL_BACK(true, true);

		private boolean added;
		private boolean returned;

		TxType(boolean added, boolean returned) {
			this.added = added;
			this.returned = returned;
		}

		public boolean isAdded() {
			return added;
		}

		public boolean isReturned() {
			return returned;
		}
	}

	protected class TxWithTermPremium {
		private TxType txType;
		private BigDecimal termPremium;
		private BigDecimal actualPremium;
		private LocalDate txDate;
		private LocalDate txEffectiveDate;

		public TxWithTermPremium(TxType txType, Object termPremium, Object actualPremium, LocalDate txDate, LocalDate txEffectiveDate) {
			this.txType = txType;
			this.termPremium = LedgerHelper.toBigDecimal(termPremium);
			this.actualPremium = LedgerHelper.toBigDecimal(actualPremium);
			this.txDate = txDate;
			this.txEffectiveDate = txEffectiveDate;
		}

		public TxType getTxType() {
			return txType;
		}

		public BigDecimal getTermPremium() {
			return termPremium;
		}

		public LocalDate getTxDate() {
			return txDate;
		}

		public LocalDate getTxEffectiveDate() {
			return txEffectiveDate;
		}

		public BigDecimal getActualPremium() {
			return actualPremium;
		}

		public void setActualPremium(BigDecimal actualPremium) {
			this.actualPremium = actualPremium;
		}

		@Override
		public String toString() {
			return String.format("Transaction: %s with transaction date: %s and effective date: %s - term premium: %s and actual premium: %s",
					txType, txDate, txEffectiveDate, termPremium, actualPremium);
		}
	}

	private class PeriodFactor {
		private LocalDate jobDate;
		private LocalDate periodStart;
		private LocalDate periodEnd;

		private BigDecimal startFactor;
		private BigDecimal endFactor;

		public PeriodFactor(LocalDate jobDate, LocalDate periodStart, LocalDate periodEnd, LocalDate effective, LocalDate expiration) {
			this.jobDate = jobDate;
			this.periodStart = periodStart;
			this.periodEnd = periodEnd;

			long term = DAYS.between(effective, expiration);
			long startDuration = DAYS.between(periodStart, expiration);
			this.startFactor = new BigDecimal(startDuration).divide(new BigDecimal(term), 16, ROUND_HALF_UP);
			long endDuration = DAYS.between(periodEnd, expiration) - 1;
			this.endFactor = new BigDecimal(endDuration).divide(new BigDecimal(term), 16, ROUND_HALF_UP);
		}

		public LocalDate getJobDate() {
			return jobDate;
		}

		public LocalDate getPeriodStart() {
			return periodStart;
		}

		public LocalDate getPeriodEnd() {
			return periodEnd;
		}

		public long getDuration() {
			long between = DAYS.between(periodStart, periodEnd) + 1;
			return between >= 0 ? between : 0;
		}

		public BigDecimal getStartFactor() {
			return startFactor;
		}

		public BigDecimal getEndFactor() {
			return endFactor;
		}

		@Override
		public String toString() {
			return String.format("Period: [%s, %s], Factor: [%s, %s]", periodStart, periodEnd, startFactor, endFactor);
		}

		public boolean isDateInPeriod(LocalDate date) {
			return !date.isBefore(periodStart) && !date.isAfter(periodEnd);
		}
	}
}
