/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.billing;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.main.enums.BillingConstants.BillingInstallmentScheduleTable;
import aaa.main.pages.summary.BillingSummaryPage;
import toolkit.webdriver.controls.composite.table.Row;

public final class BillingHelper {

	//TODO remove after "base" test adaptation
	public static final String ZERO = new Dollar(0).toString();
	public static final Dollar DZERO = new Dollar(0);
	public static final Dollar INSTALLMENT_FEE = new Dollar(5);
	public static final Dollar INSTALLMENT_FEE_X2 = INSTALLMENT_FEE.multiply(2);
	public static final Dollar INSTALLMENT_FEE_X3 = INSTALLMENT_FEE.multiply(3);
	public static final Dollar INSTALLMENT_FEE2 = new Dollar(10);
	public static final Dollar PAYMENT_DECLINE_FEE = new Dollar(7);
	public static final Dollar CANCEL_NOTICE_FEE = new Dollar(10);
	public static final Dollar CANCELLATION_FEE = new Dollar(15);
	public static final Dollar REINSTATEMENT_FEE_WITHOUT_LAPSE = new Dollar(20);
	public static final Dollar REINSTATEMENT_FEE_WITH_LAPSE = new Dollar(25);
	public static final Dollar OFFER_WITHOUT_LAPSE = new Dollar(30);
	public static final Dollar OFFER_WITH_LAPSE = new Dollar(35);
	public static final int DAYS_CANCEL_NOTICE = 5;
	public static final int DAYS_CANCELLATION = 18;
	public static final int DAYS_REINSTATEMENT_WITHOUT_LAPSE = 2;
	public static final int DAYS_REINSTATEMENT_WITH_LAPSE = 7;
	public static final int DAYS_WRITE_OFF = 6;
	public static final int DAYS_RENEW_STRATEGY = 60;
	public static final int DAYS_ENDORSEMENT_STRATEGY = 305;
	public static final int DAYS_RENEW_WITHOUT_LAPSE = 10;
	public static final int DAYS_RENEW_WITH_LAPSE = 25;
	public static final int DAYS_OFF_CYCLE = 6;

	public static final DateTimeFormatter MM_DD_YYYY = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	
	public static List<LocalDate> getInstallmentDueDates() {
		ArrayList<LocalDate> dates = new ArrayList<LocalDate>();
		for (Row row : BillingSummaryPage.tableInstallmentSchedule.getRows()) {
			dates.add(LocalDate.parse(row.getCell(BillingInstallmentScheduleTable.INSTALLMENT_DUE_DATE).getValue(), MM_DD_YYYY));
		}
		return dates;
	}
}
