package aaa.modules.regression.finance.template;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.jobs.Job;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.modules.policy.PolicyBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

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

import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoUnit.DAYS;

public abstract class FinanceOperations extends PolicyBaseTest {

	BillingAccount billingAccount = new BillingAccount();
	TestData tdBilling = testDataManager.billingAccount;

	protected static List<Map<String, String>> getCalculationsFromDB(String policyNumber) {
		String query = "select TXDATE, "
				+ "sum(case when entrytype='DEBIT' then entryamt else -entryamt end) "
				+ "from LEDGERENTRY where PRODUCTNUMBER = '%s' "
				+ "and PERIODTYPE = 'MONTHLY' "
				+ "and LEDGERACCOUNTNO = '1015' "
				+ "group by TXDATE "
				+ "ORDER BY TXDATE";

		return DBService.get().getRows(String.format(query, policyNumber));
	}

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
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();

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
		while (until.isAfter(jobDate)) {
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
		policy.endorse().performAndFill(getTestSpecificTD(testDataName)
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

	/**
	 * @author Maksim Piatrouski
	 * @name Cancel Policy with specific Effective date
	 */
	protected void cancelPolicy(LocalDateTime effectiveDate) {
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData")
				.adjust("CancellationActionTab|Cancel Date",
						effectiveDate.format(DateTimeUtils.MM_DD_YYYY)));
	}

	protected void cancelPolicy(int daysToEffective) {
		cancelPolicy(TimeSetterUtil.getInstance().getCurrentTime().plusDays(daysToEffective));
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

    public static void main(String[] args) {
		/*List<Integer> list = new ArrayList<>(Arrays.asList(0,1,2,3,4,5));
		List<Integer> integers = list.subList(2, list.size());*/

        FinanceOperations financeOperations = new FinanceOperations() {};
        financeOperations.test();
    }

    private void test() {
        List<TxWithTermPremium> premiums = new ArrayList<>();

		/*premiums.add(new TxWithTermPremium(TxType.ISSUE, new BigDecimal(7738), LocalDate.of(2018, 10, 15), LocalDate.of(2018, 10, 15)));
		premiums.add(new TxWithTermPremium(TxType.ENDORSE, new BigDecimal(7738), LocalDate.of(2018, 11, 15), LocalDate.of(2018, 11, 15)));
		premiums.add(new TxWithTermPremium(TxType.ENDORSE, new BigDecimal(5442), LocalDate.of(2019, 7, 15), LocalDate.of(2019, 6, 15)));
		premiums.add(new TxWithTermPremium(TxType.OOS_ENDORSE, new BigDecimal(5442), LocalDate.of(2019, 7, 15), LocalDate.of(2019, 4, 16)));
		premiums.add(new TxWithTermPremium(TxType.ROLL_ON, new BigDecimal(5442), LocalDate.of(2019, 7, 15), LocalDate.of(2019, 6, 15)));
		validateLedger(premiums, LocalDate.of(2018, 10, 15), LocalDate.of(2019, 10, 15));*/

		/*premiums.add(new TxWithTermPremium(TxType.ISSUE, new BigDecimal(405), LocalDate.of(2018, 8, 28), LocalDate.of(2018, 8, 28)));
		premiums.add(new TxWithTermPremium(TxType.ENDORSE, new BigDecimal(333), LocalDate.of(2018, 10, 29), LocalDate.of(2018, 10, 28)));
		premiums.add(new TxWithTermPremium(TxType.ENDORSE, new BigDecimal(450), LocalDate.of(2018, 12, 29), LocalDate.of(2018, 12, 28)));
		premiums.add(new TxWithTermPremium(TxType.OOS_ENDORSE, new BigDecimal(333), LocalDate.of(2019, 3, 2), LocalDate.of(2018, 11, 28)));
		premiums.add(new TxWithTermPremium(TxType.ROLL_ON, new BigDecimal(450), LocalDate.of(2019, 3, 2), LocalDate.of(2018, 12, 28)));
		validateLedger(premiums, LocalDate.of(2018, 8, 28), LocalDate.of(2019, 8, 28));*/

		/*premiums.add(new TxWithTermPremium(TxType.ISSUE, 2565, 2565, LocalDate.of(2018, 10, 15), LocalDate.of(2018, 10, 15)));
		premiums.add(new TxWithTermPremium(TxType.ENDORSE, 3761, 2873, LocalDate.of(2019, 7, 15), LocalDate.of(2019, 7, 13)));
		premiums.add(new TxWithTermPremium(TxType.ENDORSE, 3761, 2873, LocalDate.of(2019, 7, 15), LocalDate.of(2019, 7, 13)));
		premiums.add(new TxWithTermPremium(TxType.OOS_ENDORSE, 1448, 2242, LocalDate.of(2019, 8, 15), LocalDate.of(2019, 7, 1)));
		premiums.add(new TxWithTermPremium(TxType.ROLL_ON, 2434, 2496, LocalDate.of(2019, 8, 15), LocalDate.of(2019, 7, 13)));
		premiums.add(new TxWithTermPremium(TxType.ROLL_ON, 2434, 2496, LocalDate.of(2019, 8, 15), LocalDate.of(2019, 7, 13)));
		validateLedger(premiums, LocalDate.of(2018, 10, 15), LocalDate.of(2019, 10, 15));*/

        premiums.add(new TxWithTermPremium(TxType.ISSUE, 397, 397, LocalDate.of(2018, 8, 28), LocalDate.of(2018, 8, 28)));
        premiums.add(new TxWithTermPremium(TxType.ENDORSE, 401, 399.67, LocalDate.of(2018, 12, 29), LocalDate.of(2018, 12, 28)));
        premiums.add(new TxWithTermPremium(TxType.OOS_CANCEL, 0, 33.72, LocalDate.of(2019, 2, 2), LocalDate.of(2018, 9, 28)));
        premiums.add(new TxWithTermPremium(TxType.REINSTATE, 397, 397, LocalDate.of(2019, 3, 2), LocalDate.of(2018, 9, 28)));
        validateLedger(premiums, LocalDate.of(2018, 8, 28), LocalDate.of(2019, 8, 28));
    }

    protected void validateLedger(List<TxWithTermPremium> txsWithPremium, LocalDate effectiveDate, LocalDate expirationDate) {
//		calculateActualPremiums(txsWithPremium, effectiveDate, expirationDate);

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
                    Map<LocalDate, BigDecimal> epForCancel = calculateEpForEndorsement(
                            txWithPremium, txsWithPremium, effectiveDate, expirationDate, periodFactorsFrom);
                    calculatedEarnedPremiums.put(txWithPremium, epForCancel);
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
                    break;
                case ROLL_ON_CANCEL:
                    break;
                case ROLL_ON_REINSTATE:
                    break;
                case ROLL_BACK:
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
                    break;
                }
                finalEp.put(jobDate, ep.get(jobDate));
            }
        }
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
        List<TxWithTermPremium> txsBefore = txsUpToCurrent.stream().filter(tx -> tx.getTxDate().isBefore(currentTx.getTxDate())).collect(Collectors.toList());
        List<TxWithTermPremium> txsWithCurrent = txsUpToCurrent.stream().filter(tx -> !tx.getTxDate().isAfter(currentTx.getTxDate())).collect(Collectors.toList());
        BigDecimal actualTotal = getReportedTotal(currentTx, periodFactors, calculatedEarnedPremiums, txsBefore);
        BigDecimal wouldBeTotal = getReportedTotal(currentTx, periodFactors, calculatedEarnedPremiums, txsWithCurrent);
        BigDecimal oosDelta = wouldBeTotal.subtract(actualTotal);
        LocalDate jobDateUpTo = getPeriodFactorUpTo(currentTx, periodFactors).getJobDate();
        Map<LocalDate, BigDecimal> ep = calculatedEarnedPremiums.get(currentTx);
        ep.put(jobDateUpTo, ep.get(jobDateUpTo).add(oosDelta));
    }

    private BigDecimal getReportedTotal(TxWithTermPremium currentTx, List<PeriodFactor> periodFactors, Map<TxWithTermPremium, Map<LocalDate, BigDecimal>> calculatedEarnedPremiums, List<TxWithTermPremium> transactions) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < transactions.size(); i++) {
            if ((i + 1) == transactions.size()) {
                indexes.add(i);
                break;
            }
            TxWithTermPremium processedTx = transactions.get(i);
            TxWithTermPremium nextTx = transactions.get(i + 1);
            if (!processedTx.getTxEffectiveDate().isBefore(nextTx.getTxEffectiveDate())) {
                continue;
            }
            indexes.add(i);
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

/*	private void calculateActualPremiums(List<TxWithTermPremium> txsWithPremium, LocalDate effectiveDate, LocalDate expirationDate) {
		BigDecimal termDays = new BigDecimal(DAYS.between(effectiveDate, expirationDate));
		for (TxWithTermPremium tx : txsWithPremium) {
			TxWithTermPremium effectiveTxBefore = getEffectiveTxBefore(tx, txsWithPremium);
			BigDecimal unearnedDays = new BigDecimal(DAYS.between(tx.getTxEffectiveDate(), expirationDate));
			BigDecimal factor = unearnedDays.divide(termDays, 16, ROUND_HALF_UP);
			tx.setFactor(factor);
			if (effectiveTxBefore != null) {
				tx.setEarned(effectiveTxBefore.getFactor().subtract(factor).multiply(effectiveTxBefore.getTermPremium()).add(effectiveTxBefore.getEarned()));
				tx.setReturned(tx.getTxType().isReturned() ? effectiveTxBefore.getTermPremium().multiply(tx.getFactor(), new MathContext(16, RoundingMode.HALF_UP)).negate() : new BigDecimal(0));
			}
			tx.setAdded(tx.getTxType().isAdded() ? tx.getTermPremium().multiply(tx.getFactor(), new MathContext(16, RoundingMode.HALF_UP)) : new BigDecimal(0));
			boolean isOOS = TxType.OOS_CANCEL.equals(tx.getTxType()) || TxType.OOS_ENDORSE.equals(tx.getTxType());
			tx.setReversed(isOOS ? calculatePreviousChange(tx, txsWithPremium).negate() : new BigDecimal(0));
			tx.setChange(tx.getAdded().add(tx.getReturned()).add(tx.getReversed()));
			int index = txsWithPremium.indexOf(tx);
			TxWithTermPremium previousTx = index != 0 ? txsWithPremium.get(index - 1) : null;
			BigDecimal previousActual = previousTx != null ? previousTx.getActualPremium() : new BigDecimal(0);
			tx.setActualPremium(previousActual.add(tx.getChange()).setScale(2, ROUND_HALF_UP));
		}
	}

	private BigDecimal calculatePreviousChange(TxWithTermPremium currentTx, List<TxWithTermPremium> txsWithPremium) {
		BigDecimal totalChange = new BigDecimal(0);
		for (int i = 1; i < txsWithPremium.indexOf(currentTx); i++) {
			TxWithTermPremium tx = txsWithPremium.get(i);
			if (tx.getTxEffectiveDate().isAfter(currentTx.getTxEffectiveDate())){
				totalChange = totalChange.add(tx.getChange());
			}
		}
		return totalChange;
	}*/

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
            BigDecimal ep = new BigDecimal(period.getDuration())
                    .divide(new BigDecimal(DAYS.between(effective, expiration)), 16, ROUND_HALF_UP)
                    .multiply(tx.getTermPremium()).setScale(2, ROUND_HALF_UP);
            results.put(period.getJobDate(), ep);
        }
        return results;
    }

    private TxWithTermPremium getEffectiveTxBefore(TxWithTermPremium currentTx, List<TxWithTermPremium> transactions) {
        int index = transactions.indexOf(currentTx);
        if (index == 0) {
            return null;
        }
        for (int i = index; i >= 0  ; i--) {
            TxWithTermPremium tx = transactions.get(i);
            if (tx.getTxEffectiveDate().withDayOfMonth(1).isBefore(currentTx.getTxEffectiveDate().withDayOfMonth(1))) {
                return tx;
            }
        }
        return null;
    }

    private BigDecimal toBigDecimal(Object sum) {
        try {
            return new BigDecimal(String.valueOf(sum).replace(" ", "").replace("$", "").replace(",", "").replace("(", "-").replace(")", ""));
        } catch (NumberFormatException nfe){
            throw new IstfException(String.format("Value '%s' can't be converted to financial value", sum), nfe);
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

        public boolean isDateInPeriod(LocalDate date) {
            return !date.isBefore(periodStart) && !date.isAfter(periodEnd);
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
    }

    protected class TxWithTermPremium {
        private TxType txType;
        private BigDecimal termPremium;
        private BigDecimal actualPremium;
        private LocalDate txDate;
        private LocalDate txEffectiveDate;

        public TxWithTermPremium(TxType txType, Object termPremium, Object actualPremium,LocalDate txDate, LocalDate txEffectiveDate) {
            this.txType = txType;
            this.termPremium = toBigDecimal(termPremium);
            this.actualPremium = toBigDecimal(actualPremium);
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
}
