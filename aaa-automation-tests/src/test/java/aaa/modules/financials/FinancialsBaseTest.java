package aaa.modules.financials;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.products.HomeSSConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static toolkit.verification.CustomAssertions.assertThat;

public class FinancialsBaseTest extends FinancialsTestDataFactory {

	protected static final String METHOD_CASH = "TestData_Cash";
	protected static final String METHOD_CHECK = "TestData_Check";
	protected static final String CITY = "city";
	protected static final String STATE = "state";
	protected static final String COUNTY = "county";
	protected static final String TOTAL = "total";

	protected String createFinancialPolicy() {
		return createFinancialPolicy(getPolicyTD());
	}

	protected String createFinancialPolicy(TestData td) {
		String policyNum = createPolicy(td);
		ALL_POLICIES.add(policyNum);
		return policyNum;
	}

	protected Dollar payTotalAmountDue(){
		// Open Billing account and Pay min due for the renewal
		if (!BillingSummaryPage.tablePaymentsOtherTransactions.isPresent()) {
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		}
		Dollar due = new Dollar(BillingSummaryPage.getTotalDue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), due);
		return due;
	}
	protected Dollar payMinAmountDue(String paymentMethod) {
		// Open Billing account and Pay min due for the renewal
		if (!BillingSummaryPage.tablePaymentsOtherTransactions.isPresent()) {
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		}
		Dollar due = new Dollar(BillingSummaryPage.getMinimumDue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", paymentMethod), due);
		return due;
	}

	protected void cancelPolicy(String policyNumber) {
		if (!PolicySummaryPage.labelPolicyStatus.isPresent()) {
			SearchPage.openPolicy(policyNumber);
		}
		cancelPolicy(TimeSetterUtil.getInstance().getCurrentTime());
	}

	protected void cancelPolicy(LocalDateTime cxDate) {
		policy.cancel().perform(getCancellationTD(cxDate));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
	}

	protected void performReinstatement(String policyNumber) {
		if (!PolicySummaryPage.labelPolicyStatus.isPresent()) {
			SearchPage.openPolicy(policyNumber);
		}
		policy.reinstate().perform(getReinstatementTD());
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	protected void performReinstatementWithLapse(LocalDateTime effDate, String policyNumber) {
		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(effDate.plusMonths(1).minusDays(20).with(DateTimeUtils.closestPastWorkingDay));
		JobUtils.executeJob(Jobs.changeCancellationPendingPoliciesStatus);
		TimeSetterUtil.getInstance().nextPhase(effDate.plusDays(20));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.reinstate().perform(getReinstatementTD());
		if (Page.dialogConfirmation.buttonYes.isPresent()) {
			Page.dialogConfirmation.buttonYes.click();
		}
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	protected void performAPEndorsement(String policyNumber) {
		performAPEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime());
	}

	protected void performAPEndorsement(String policyNumber, LocalDateTime effDate) {
		policy.endorse().perform(getEndorsementTD(effDate));
		policy.getDefaultView().fill(getAddPremiumTD());
		SearchPage.openPolicy(policyNumber);
	}

	protected Dollar performRPEndorsement(String policyNumber, LocalDateTime effDate) {
		policy.endorse().perform(getEndorsementTD(effDate));
		policy.getDefaultView().fill(getReducePremiumTD());
		Dollar reducedPrem = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.ENDORSEMENT);
		SearchPage.openPolicy(policyNumber);
		return reducedPrem;
	}

	protected void performNonPremBearingEndorsement(String policyNumber) {
		performNonPremBearingEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime());
	}

	protected void performNonPremBearingEndorsement(String policyNumber, LocalDateTime effDate) {
		policy.endorse().perform(getEndorsementTD(effDate));
		policy.getDefaultView().fill(getNPBEndorsementTD());
		SearchPage.openPolicy(policyNumber);
	}

	protected Dollar rollBackEndorsement(String policyNumber) {
		SearchPage.openPolicy(policyNumber);
		policy.rollBackEndorsement().perform(getPolicyTD("EndorsementRollBack", "TestData"));
		Dollar rollBackAmount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.ROLL_BACK_ENDORSEMENT);
		SearchPage.openPolicy(policyNumber);
		return rollBackAmount;
	}

	protected Dollar getBillingAmountByType(String type, String subtype) {
		if (!BillingSummaryPage.tablePaymentsOtherTransactions.isPresent()) {
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		}
		Map<String, String> query = new HashMap<>();
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE, type);
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, subtype);
		return new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(query).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue()).abs();
	}

	protected void waiveFeeByDateAndType(LocalDateTime txDate, String feeType) {
		Map<String, String> query = new HashMap<>();
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, txDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE, BillingConstants.PaymentsAndOtherTransactionType.FEE);
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, feeType);
		BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(query)
				.getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(BillingConstants.PaymentsAndOtherTransactionAction.WAIVE).click();
		BillingSummaryPage.dialogConfirmation.confirm();
	}

	protected void advanceTimeAndOpenPolicy(LocalDateTime date, String policyNumber) {
		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(date);
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
	}

	protected Map<String, Dollar> getTaxAmountsFromVRD(String policyNumber) {
		Map<String, Dollar> taxes = new HashMap<>();
		if (!PolicySummaryPage.labelPolicyStatus.isPresent()) {
			SearchPage.openPolicy(policyNumber);
		}
		policy.policyInquiry().start();

		// Auto policies
		if (getPolicyType().isAutoPolicy()) {
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			PremiumAndCoveragesTab.RatingDetailsView.open();
			taxes.put(STATE, new Dollar(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRowContains(3, "Premium Surcharge").getCell(4).getValue()));
			if (getState().equals(Constants.States.KY)) {
				taxes.put(CITY, new Dollar(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRowContains(3, "City Tax").getCell(4).getValue()));
				taxes.put(COUNTY, new Dollar(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRowContains(3, "County Tax").getCell(4).getValue()));
			}
			PremiumAndCoveragesTab.RatingDetailsView.close();
			new PremiumAndCoveragesTab().cancel();

		// PUP policies
		} else if (getPolicyType().equals(PolicyType.PUP)) {
			NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
			NavigationPage.toViewSubTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
			taxes.put(STATE, new Dollar(PremiumAndCoveragesQuoteTab.tableTaxes
					.getRowContains(HomeSSConstants.StateAndLocalTaxesTable.DESCRIPTION, HomeSSConstants.StateAndLocalTaxesTable.PREMIUM_SURCHARGE)
					.getCell(HomeSSConstants.StateAndLocalTaxesTable.TERM_PREMIUM)));
			if (getState().equals(Constants.States.KY)) {
				taxes.put(CITY, new Dollar(PremiumAndCoveragesQuoteTab.tableTaxes
						.getRowContains(HomeSSConstants.StateAndLocalTaxesTable.DESCRIPTION, HomeSSConstants.StateAndLocalTaxesTable.CITY_TAX)
						.getCell(HomeSSConstants.StateAndLocalTaxesTable.TERM_PREMIUM)));
				taxes.put(COUNTY, new Dollar(PremiumAndCoveragesQuoteTab.tableTaxes
						.getRowContains(HomeSSConstants.StateAndLocalTaxesTable.DESCRIPTION, HomeSSConstants.StateAndLocalTaxesTable.COUNTY_TAX)
						.getCell(HomeSSConstants.StateAndLocalTaxesTable.TERM_PREMIUM)));
			}
			new PremiumAndCoveragesQuoteTab().cancel();

		// HO policies
		} else {
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
			NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
			taxes.put(STATE, new Dollar(PremiumsAndCoveragesQuoteTab.tableTaxes
					.getRowContains(HomeSSConstants.StateAndLocalTaxesTable.DESCRIPTION, HomeSSConstants.StateAndLocalTaxesTable.PREMIUM_SURCHARGE)
					.getCell(HomeSSConstants.StateAndLocalTaxesTable.TERM_PREMIUM)));
			if (getState().equals(Constants.States.KY)) {
				taxes.put(CITY, new Dollar(PremiumsAndCoveragesQuoteTab.tableTaxes
						.getRowContains(HomeSSConstants.StateAndLocalTaxesTable.DESCRIPTION, HomeSSConstants.StateAndLocalTaxesTable.CITY_TAX)
						.getCell(HomeSSConstants.StateAndLocalTaxesTable.TERM_PREMIUM)));
				taxes.put(COUNTY, new Dollar(PremiumsAndCoveragesQuoteTab.tableTaxes
						.getRowContains(HomeSSConstants.StateAndLocalTaxesTable.DESCRIPTION, HomeSSConstants.StateAndLocalTaxesTable.COUNTY_TAX)
						.getCell(HomeSSConstants.StateAndLocalTaxesTable.TERM_PREMIUM)));
			}
			new PremiumsAndCoveragesQuoteTab().cancel();
		}
		taxes.put(TOTAL, taxes.get(STATE).add(taxes.get(CITY)).add(taxes.get(COUNTY)));
		return taxes;
	}

}
