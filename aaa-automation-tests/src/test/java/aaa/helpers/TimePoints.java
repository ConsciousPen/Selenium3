package aaa.helpers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.main.modules.policy.PolicyType;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

public class TimePoints {

	public static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";
	protected Logger log = LoggerFactory.getLogger(TimePoints.class);
	protected TestData td;
	protected PolicyType policyType;
	protected String state;

	public TimePoints(TestData td) {
		new TimePoints(td, null,null);
	}

	public TimePoints(TestData td, PolicyType policyType, String state) {
		this.td = td;
		this.policyType = policyType;
		this.state = state;
	}

	public LocalDateTime getTimepoint(LocalDateTime date, TimepointsList timePointName, Boolean applyShift) {
		List<String> timepoint = td.getList(timePointName.get());
		if (timepoint.size() == 1) {
			timepoint.add("NONE");
		}
		if (timepoint.size() > 2) {
			throw new IllegalArgumentException("Wrong timepoint entry, please check testdata");
		}
		LocalDateTime returnDate = parseDate(date, timepoint.get(0));
		if (applyShift) {
			switch (timepoint.get(1).toUpperCase()) {
				case "PREVIOUS":
					returnDate = returnDate.with(DateTimeUtils.closestPastWorkingDay);
					break;
				case "NEXT":
					returnDate = returnDate.with(DateTimeUtils.closestFutureWorkingDay);
					break;
				case "NONE":
					break;
				default:
					break;
			}
		}

		return returnDate;
	}

	private LocalDateTime parseDate(LocalDateTime date, String timepoint) {
		LocalDateTime returnDate = date;
		Matcher m = Pattern.compile("([-+]?)(\\d{1,3})(\\w?)").matcher(timepoint);

		while (m.find()) {
			int signum = m.group(1).isEmpty() || m.group(1).equals("+") ? 1 : -1;
			long val = Long.parseLong(m.group(2));
			String strUnit = m.group(3).isEmpty() ? "d": m.group(3);
			TemporalUnit unit;
			switch (strUnit) {
				case "m": unit = ChronoUnit.MINUTES; break;
				case "H": unit = ChronoUnit.HOURS; break;
				case "d": unit = ChronoUnit.DAYS; break;
				case "M": unit = ChronoUnit.MONTHS; break;
				case "y": unit = ChronoUnit.YEARS; break;
				default:
					throw new IllegalArgumentException("Cannot parse " + m.group(3) + " in " + timepoint + " as temporal unit");
			}
			returnDate = returnDate.plus(val * signum, unit);
		}
		return returnDate;
	}

	private boolean isWorkingDay(LocalDateTime date, TimepointsList timePointName) {
		LocalDateTime d = getTimepoint(date, timePointName, false);
		// analogue of DateTimeUtils.isWorkingDay() which is private
		return d.equals(d.with(DateTimeUtils.closestFutureWorkingDay));
	}

	public LocalDateTime getBillDueDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.BILL_PAYMENT, true);
	}

	public LocalDateTime getBillGenerationDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.BILL_GENERATION, true);
	}

	public LocalDateTime getOffcycleBillGenerationDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.OFFCYCLE_BILL_GENERATION, true);
	}

	public LocalDateTime getRenewImageGenerationDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.RENEW_GENERATE_IMAGE, true);
	}

	public LocalDateTime getRenewMsgGenerationDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.RENEW_GENERATE_MSG, true);
	}

	public LocalDateTime getRenewCheckUWRules(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.RENEW_CHECK_UW_RULES, true);
	}

	public LocalDateTime getRenewReportsDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.RENEW_REPORTS, true);
	}

	public LocalDateTime getRenewPreviewGenerationDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.RENEW_GENERATE_PREVIEW, true);
	}

	public LocalDateTime getRenewOfferGenerationDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.RENEW_GENERATE_OFFER, true);
	}

	public LocalDateTime getPreRenewalLetterGenerationDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.PRERENEWAL_LETTER, true);
	}

	public LocalDateTime getMortgageeBillFirstRenewalReminder(LocalDateTime date)  {
		return getTimepoint(date, TimepointsList.MORTGAGEE_BILL_FIRST_RENEW_REMINDER_NOTICE, true);
	}

	public LocalDateTime getMortgageeBillFinalExpirationNotice(LocalDateTime date)  {
		return getTimepoint(date, TimepointsList.MORTGAGEE_BILL_FINAL_EXP_NOTICE, true);
	}

	public LocalDateTime getUpdatePolicyStatusDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.UPDATE_POLICY_STATUS, true);
	}

	public LocalDateTime getCancellationDate(LocalDateTime date) {
		return getCancellationDate(date, this.policyType, this.state);
	}

	public LocalDateTime getCancellationDate(LocalDateTime date, PolicyType policyType, String state) {
		if (PolicyType.AUTO_SS.equals(policyType) && Constants.States.PA.equals(state) && !isWorkingDay(date, TimepointsList.CANCELLATION_NOTICE)) {
			// According to 160-140PA requirement for Auto SS, PA product we should add 18 days instead of 15 if cancellation notice falls on non-working day
			return getTimepoint(getCancellationNoticeDate(date), TimepointsList.CANCELLATION_IF_CN_FALLS_ON_NWD, true);
		} else if (PolicyType.AUTO_CA_SELECT.equals(policyType)) {
			// Cancellation date for Auto CA policy should be calculated from cancellation notice before shift, see QC defect 44998
			LocalDateTime cancellationNoticeWithoutShiftDate = getTimepoint(date, TimepointsList.CANCELLATION_NOTICE, false);
			return getTimepoint(cancellationNoticeWithoutShiftDate, TimepointsList.CANCELLATION, true);
		}
		return getTimepoint(getCancellationNoticeDate(date), TimepointsList.CANCELLATION, true);
	}

	public LocalDateTime getCancellationTransactionDate(LocalDateTime date) {
		return getCancellationTransactionDate(date, this.policyType, this.state);
	}

	public LocalDateTime getCancellationTransactionDate(LocalDateTime date, PolicyType policyType, String state) {
		if (PolicyType.AUTO_SS.equals(policyType) && Constants.States.PA.equals(state)  && !isWorkingDay(date, TimepointsList.CANCELLATION_NOTICE)) {
			// According to 160-140PA requirement for Auto SS, PA product we should add 18 days instead of 15 if cancellation notice falls on non-working day
			return getTimepoint(getCancellationNoticeDate(date), TimepointsList.CANCELLATION_IF_CN_FALLS_ON_NWD, false);
		} else if (PolicyType.AUTO_CA_SELECT.equals(policyType)) {
			// Cancellation date for Auto CA policy should be calculated from cancellation notice before shift, see QC defect 44998
			LocalDateTime cancellationNoticeWithoutShiftDate = getTimepoint(date, TimepointsList.CANCELLATION_NOTICE, false);
			return getTimepoint(cancellationNoticeWithoutShiftDate, TimepointsList.CANCELLATION, false);
		}
		return getTimepoint(getCancellationNoticeDate(date), TimepointsList.CANCELLATION, false);
	}

	public LocalDateTime getCancellationNoticeDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.CANCELLATION_NOTICE, true);
	}

	public LocalDateTime getRenewCustomerDeclineDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.RENEW_CUSTOMER_DECLINE, true);// .addHours(1);
	}

	public LocalDateTime getEarnedPremiumBillFirst(LocalDateTime date) {
		return getEarnedPremiumBillFirst(date, this.policyType, this.state);
	}

	public LocalDateTime getEarnedPremiumBillFirst(LocalDateTime date, PolicyType policyType, String state) {
		return getTimepoint(getCancellationDate(date, policyType, state), TimepointsList.EARNED_PREMIUM_BILL_FIRST, true);
	}

	public LocalDateTime getEarnedPremiumBillSecond(LocalDateTime date) {
		return getEarnedPremiumBillSecond(date, this.policyType, this.state);
	}

	public LocalDateTime getEarnedPremiumBillSecond(LocalDateTime date, PolicyType policyType, String state) {
		return getTimepoint(getCancellationDate(date, policyType, state), TimepointsList.EARNED_PREMIUM_BILL_SECOND, true);
	}

	public LocalDateTime getEarnedPremiumBillThird(LocalDateTime date) {
		return getEarnedPremiumBillThird(date, this.policyType, this.state);
	}

	public LocalDateTime getEarnedPremiumBillThirdManualCancelltion(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.EARNED_PREMIUM_BILL_THIRD, true);
	}

	public LocalDateTime getEarnedPremiumBillThird(LocalDateTime date, PolicyType policyType, String state) {
		return getTimepoint(getCancellationDate(date, policyType, state), TimepointsList.EARNED_PREMIUM_BILL_THIRD, true);
	}

	public LocalDateTime getEarnedPremiumWriteOff(LocalDateTime date) {
		return getEarnedPremiumWriteOff(date, this.policyType, this.state);
	}

	public LocalDateTime getEarnedPremiumWriteOffManualCancellation(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.EARNED_PREMIUM_WRITE_OFF, true);
	}

	public LocalDateTime getEarnedPremiumWriteOff(LocalDateTime date, PolicyType policyType, String state) {
		// updated according to https://csaaig.atlassian.net/browse/PAS-10214
		//return getTimepoint(getCancellationDate(date, policyType, state), TimepointsList.EARNED_PREMIUM_WRITE_OFF, true); 
		if (PolicyType.AUTO_SS.equals(policyType) || PolicyType.AUTO_CA_SELECT.equals(policyType) || PolicyType.PUP.equals(policyType) && Constants.States.CA.equals(state)) {
			return getTimepoint(getCancellationTransactionDate(date, policyType, state), TimepointsList.EARNED_PREMIUM_WRITE_OFF, true);
		} else {
			return getTimepoint(getCancellationDate(date, policyType, state), TimepointsList.EARNED_PREMIUM_WRITE_OFF, true);
		}
	}

	public LocalDateTime getPayLapsedRenewShort(LocalDateTime date) {
		return getTimepoint(getRenewCustomerDeclineDate(date), TimepointsList.PAY_LAPSED_RENEW_SHORT, true);
	}

	public LocalDateTime getPayLapsedRenewLong(LocalDateTime date) {
		return getTimepoint(getRenewCustomerDeclineDate(date), TimepointsList.PAY_LAPSED_RENEW_LONG, true);
	}

	public LocalDateTime getEndorsePolicyBeforeOfferGen(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.ENDORSE_POLICY_BEFORE_OFFER_GEN, true);
	}

	public LocalDateTime getMembershipRenewBatchOrder(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.MEMBERSHIP_RENEW_BATCH_ORDER, true);
	}

	public LocalDateTime getRenewalCancellationDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.RENEW_CANCEL_NOTICE, true);
	}

	public LocalDateTime getPolicyReinstateDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.POLICY_REINSTATEMENT_DATE, true);
	}

	public LocalDateTime getUserCancelNoticeDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.USER_CANCEL_NOTICE, true);
	}

	public LocalDateTime getReinstatementDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.REINSTATEMENT, true);
	}

	public LocalDateTime getManualCancellationDate(LocalDateTime date) {
		return getTimepoint(getCancellationNoticeDate(date), TimepointsList.MANUAL_CANCELLATION, true);
	}

	public LocalDateTime getRefundDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.REFUND, true);
	}

	public LocalDateTime getInsuranceRenewalReminderDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.INSURANCE_RENEWAL_REMINDER, true);
	}

	public LocalDateTime getConversionEffectiveDate() {
		return getEffectiveDateForTimePoint(TimepointsList.RENEW_GENERATE_PREVIEW);
	}

	/**
	 * Retrieves effective date in future based assuming that today will be specified timepoint
	 *
	 * @param timePoint {@link TimepointsList}
	 */
	public LocalDateTime getEffectiveDateForTimePoint(TimepointsList timePoint) {
		List<String> timepoint = td.getList(timePoint.get());
		return TimeSetterUtil.getInstance().getPhaseStartTime().with(DateTimeUtils.closestPastWorkingDay).minusDays(Integer.parseInt(timepoint.get(0)));
	}

	/**
	 * Retrieves effective date based on incoming params
	 *
	 * @param date      {@link LocalDateTime}
	 * @param timePoint {@link TimepointsList}
	 */
	public LocalDateTime getEffectiveDateForTimePoint(LocalDateTime date, TimepointsList timePoint) {
		List<String> timepoint = td.getList(timePoint.get());
		return date.with(DateTimeUtils.closestPastWorkingDay).minusDays(Integer.parseInt(timepoint.get(0)));
	}

	public enum TimepointsList {
		RENEW_GENERATE_IMAGE("Renew generate image"), //
		RENEW_CHECK_UW_RULES("Renew check uw rules"), //
		RENEW_REPORTS("Renew reports"), //
		RENEW_GENERATE_MSG("Renew generate message"), //
		RENEW_GENERATE_PREVIEW("Renew generate preview"), //
		RENEW_GENERATE_OFFER("Renew generate offer"), //
		PRERENEWAL_LETTER("Pre-renewal letter"), //
		MORTGAGEE_BILL_FIRST_RENEW_REMINDER_NOTICE("Mortgagee Bill First Renewal"), //
		MORTGAGEE_BILL_FINAL_EXP_NOTICE("Mortgagee Bill Final Exp"), //
		BILL_GENERATION("Bill generation"), //
		OFFCYCLE_BILL_GENERATION("Offcycle bill generation"), //
		BILL_PAYMENT("Bill payment"), //
		UPDATE_POLICY_STATUS("Update policy status"), //
		CANCELLATION("Cancellation"), //
		CANCELLATION_IF_CN_FALLS_ON_NWD("Cancellation if CN falls on non-working day"), // Applicable for Auto SS, PA state only
		CANCELLATION_NOTICE("Cancellation notice"), //
		RENEW_CUSTOMER_DECLINE("Renew customer decline"), //
		PAY_LAPSED_RENEW_SHORT("Pay Lapsed Renew short"), //
		PAY_LAPSED_RENEW_LONG("Pay Lapsed Renew long"), //
		EARNED_PREMIUM_BILL_FIRST("Earned premium bill first"), //
		EARNED_PREMIUM_BILL_SECOND("Earned premium bill second"), //
		EARNED_PREMIUM_BILL_THIRD("Earned premium bill third"), //
		EARNED_PREMIUM_WRITE_OFF("Earned premium write off"), //
		ENDORSE_POLICY_BEFORE_OFFER_GEN("Endorse Policy Before Offer Gen"), //
		MEMBERSHIP_RENEW_BATCH_ORDER("Membership Renew Batch Order"), //
		RENEW_CANCEL_NOTICE("Renew Cancel Notice"), //
		POLICY_REINSTATEMENT_DATE("Policy Reinstatement Date"), //
		USER_CANCEL_NOTICE("User Cancel Notice"), //
		REINSTATEMENT("Reinstatement"), //
		MANUAL_CANCELLATION("Manual cancellation"), //
		REFUND("Refund"), //
		INSURANCE_RENEWAL_REMINDER("Insurance renewal reminder"), //
		;
		String id;

		TimepointsList(String id) {
			this.id = id;
		}

		public String get() {
			return id;
		}
	}
}
