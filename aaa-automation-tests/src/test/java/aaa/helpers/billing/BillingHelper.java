/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.billing;

import java.util.List;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.PolicyConstants;
import aaa.main.modules.billing.account.defaulttabs.BillingAccountTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.utils.datetime.DateTime;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;

public final class BillingHelper {

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

	public static final String STRATEGY_FEE_1_NAME = "FEE1/1";
	public static final String STRATEGY_FEE_2_NAME = "FEE2/2";
	public static final String STRATEGY_TAX_1_NAME = "TAX1/1";

	private BillingHelper() {
	}

	public static Dollar[] calculateInstallments(int installmentsCount, Dollar totalDueAmount) {
		Dollar[] installments = new Dollar[2];
		installments[1] = totalDueAmount.divide(installmentsCount);
		installments[0] = totalDueAmount.subtract(installments[1].multiply(installmentsCount - 1));
		return installments;
	}

	public static int getInstallementNumber() {
		/*
		 * https://blueprint.eisgroup.com/?ArtifactId=2936934 Rules: • 1st
		 * installment due date can't be earlier, then Policy Effective Date or
		 * Policy Issue Date in a case of backdated Policy. • Last installment
		 * due date can't be later or equal to Policy Expiration date. • Gap
		 * between installments specified in 'Installment Frequency' parameter
		 * of selected Payment Plan UM2956809: Admin: 'Add / Update Payment
		 * Plan'  • Maximum Number of Installments limited by 'Number of
		 * installments' parameter of selected Payment Plan UM2956809: Admin:
		 * 'Add / Update Payment Plan'  • In a case of Due Day Type = Month
		 * Days: 'Installment Frequency' parameter means month gap. • In a case
		 * of Due Day Type = Week Day: 'Installment Frequency' parameter means
		 * week gap. • Installments' Due Amounts =  Policy Premium / Number of
		 * generated of installments.
		 */

		int iCounter = 1;

		DateTime policyEffectiveDate = new DateTime(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.EFFECTIVE_DATE).getValue(), DateTime.MM_DD_YYYY);
		DateTime policyExpirationDate = new DateTime(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.EXPIRATION_DATE).getValue(), DateTime.MM_DD_YYYY);
		DateTime dateToday = new DateTime(TimeSetterUtil.getInstance().getCurrentTime().toString(DateTime.MM_DD_YYYY));

		// if todays day is bigger than effective date, than get today as first
		// installment date
		DateTime installmentDateFirst, installementDateLast;
		int dayToday = Integer.parseInt(dateToday.toString().split("/")[1]);
		int dayEffective = Integer.parseInt(policyEffectiveDate.toString().split("/")[1]);
		if (dayToday > dayEffective) {
			installmentDateFirst = new DateTime(policyEffectiveDate.addDays(dayToday - dayEffective).toString(DateTime.MM_DD_YYYY), DateTime.MM_DD_YYYY);
		} else {
			// else take next month with todays day
			installmentDateFirst = policyEffectiveDate;
		}
		installementDateLast = policyExpirationDate.subtractMonths(1);

		// count months between First and Last dates
		for (;; iCounter++) {
			DateTime period = installmentDateFirst.addMonths(iCounter);
			if ((period.equals(installementDateLast)) || (period.equals(installementDateLast.addDays(dayToday - dayEffective)))) {
				iCounter++;
				break;
			}
		}

		return iCounter;
	}

	public static void verifyAdvancedAllocationForTaxesAndFees(List<String> listTaxesAndFees) {
		String rowLocator = "//form[@id='advAllocationForm']//tr[td[.='%s']]";

		for (String tf : listTaxesAndFees) {
			new Dollar(new TextBox(By.xpath(String.format(rowLocator, tf) + "/td[2]/input[@type='text']")).getValue()).verify.moreThan(BillingHelper.DZERO);
			new Dollar(new StaticElement(By.xpath(String.format(rowLocator, tf) + "/td[3]")).getValue()).verify.moreThan(BillingHelper.DZERO);
			new Dollar(new StaticElement(By.xpath(String.format(rowLocator, tf) + "/td[4]")).getValue()).verify.moreThan(BillingHelper.DZERO);
		}
	}

	public static Dollar[] getInstallmentsWithTaxAndFee(int installmentsCount, String policyNumber, Dollar totalDueAmount) {
		Dollar[] installments = new Dollar[2];

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(BillingConstants.BillingPaymentsAndOtherTransactionsTable.POLICY, policyNumber).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE).controls.links.getFirst().click();

		String fieldsToCalculateXpath = "(//input[@disabled and not (@id = 'premiumTransactionsForm:grossPremium')])";

		int amountsCount = BrowserController.get().driver().findElements(By.xpath(fieldsToCalculateXpath)).size();

		Dollar roundPremiumAndTaxes = new Dollar(0);
		for (int i = 1; i <= amountsCount; i++) {
			roundPremiumAndTaxes = (new Dollar(new StaticElement(By.xpath(fieldsToCalculateXpath + "[" + i + "]")).getAttribute("value"))).divide(installmentsCount).add(roundPremiumAndTaxes);
		}

		installments[1] = roundPremiumAndTaxes;
		installments[0] = new Dollar(totalDueAmount).subtract(installments[1].multiply(installmentsCount - 1));

		BillingAccountTab.buttonBack.click();
		return installments;
	}
}
