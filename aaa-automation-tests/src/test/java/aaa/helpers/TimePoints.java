package aaa.helpers;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

public class TimePoints {

	public static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";
	protected Logger log = LoggerFactory.getLogger(TimePoints.class);
	protected TestData td;

	public TimePoints(TestData td) {
		this.td = td;
	}

	public LocalDateTime getTimepoint(LocalDateTime date, TimepointsList timePointName, Boolean applyShift) {
		LocalDateTime returnDate = date;
		List<String> timepoint = td.getList(timePointName.get());
		if (timepoint.size() == 1) {
			timepoint.add("NONE");
		}
		if (timepoint.size() > 2) {
			throw new IllegalArgumentException("Wrong timepoint entry, please check testdata");
		}
		returnDate = returnDate.plusDays(Integer.parseInt(timepoint.get(0)));
		if (applyShift && (returnDate.getDayOfWeek() == DayOfWeek.SATURDAY || returnDate.getDayOfWeek() == DayOfWeek.SATURDAY)) {
			switch (timepoint.get(1).toUpperCase()) {
				case "PREVIOUS":
					returnDate = returnDate.with(DateTimeUtils.previousWorkingDay);
					break;
				case "NEXT":
					returnDate = returnDate.with(DateTimeUtils.nextWorkingDay);
					break;
				case "NONE":
					break;
				default:
					break;
			}
		}

		return returnDate;
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

	public LocalDateTime getUpdatePolicyStatusDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.UPDATE_POLICY_STATUS, true);
	}

	public LocalDateTime getCancellationDate(LocalDateTime date) {
		return getTimepoint(getCancellationNoticeDate(date), TimepointsList.CANCELLATION, true);
	}

	public LocalDateTime getCancellationNoticeDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.CANCELLATION_NOTICE, true);
	}

	public LocalDateTime getRenewCustomerDeclineDate(LocalDateTime date) {
		return getTimepoint(date, TimepointsList.RENEW_CUSTOMER_DECLINE, true);// .addHours(1);
	}

	public LocalDateTime getEarnedPremiumBillFirst(LocalDateTime date) {
		return getTimepoint(getCancellationDate(date), TimepointsList.EARNED_PREMIUM_BILL_FIRST, true);
	}

	public LocalDateTime getEarnedPremiumBillSecond(LocalDateTime date) {
		return getTimepoint(getCancellationDate(date), TimepointsList.EARNED_PREMIUM_BILL_SECOND, true);
	}

	public LocalDateTime getEarnedPremiumBillThird(LocalDateTime date) {
		return getTimepoint(getCancellationDate(date), TimepointsList.EARNED_PREMIUM_BILL_THIRD, true);
	}

	public LocalDateTime getEarnedPremiumWriteOff(LocalDateTime date) {
		return getTimepoint(getCancellationDate(date), TimepointsList.EARNED_PREMIUM_WRITE_OFF, true);
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

	public enum TimepointsList {
		RENEW_GENERATE_IMAGE("Renew generate image"), //
		RENEW_CHECK_UW_RULES("Renew check uw rules"), //
		RENEW_REPORTS("Renew reports"), //
		RENEW_GENERATE_MSG("Renew generate message"), //
		RENEW_GENERATE_PREVIEW("Renew generate preview"), //
		RENEW_GENERATE_OFFER("Renew generate offer"), //
		BILL_GENERATION("Bill generation"), //
		OFFCYCLE_BILL_GENERATION("Offcycle bill generation"), //
		BILL_PAYMENT("Bill payment"), //
		UPDATE_POLICY_STATUS("Update policy status"), //
		CANCELLATION("Cancellation"), //
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
