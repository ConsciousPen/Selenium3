package aaa.modules.financials;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.By;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.products.HomeSSConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AdvancedAllocationsActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;

public class FinancialsBaseTest extends FinancialsTestDataFactory {

	protected static final String METHOD_CASH = "TestData_Cash";
	protected static final String METHOD_CHECK = "TestData_Check";
	protected static final String CITY = "city";
	protected static final String STATE = "state";
	protected static final String COUNTY = "county";
	protected static final String TOTAL = "total";
	private AdvancedAllocationsActionTab advancedAllocationsActionTab = new AdvancedAllocationsActionTab();

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
		Dollar due = BillingSummaryPage.getTotalDue();
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), due);
		return due;
	}

	protected Dollar payTotalAmountDueWithDatedCheck(LocalDateTime date) {
		// Open Billing account and Pay min due for the renewal
		if (!BillingSummaryPage.tablePaymentsOtherTransactions.isPresent()) {
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		}
		Dollar due = new Dollar(BillingSummaryPage.getTotalDue());
		TestData payment = testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Check")
				.adjust(TestData.makeKeyPath(BillingAccountMetaData.AcceptPaymentActionTab.class.getSimpleName(),
                        BillingAccountMetaData.AcceptPaymentActionTab.CHECK_DATE.getLabel()), formatDateToString(date));
		new BillingAccount().acceptPayment().perform(payment, due);
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
		cancelPolicy(policyNumber, TimeSetterUtil.getInstance().getCurrentTime());
	}

	protected void cancelPolicy(String policyNumber, LocalDateTime cxDate) {
		if (!PolicySummaryPage.labelPolicyStatus.isPresent()) {
			SearchPage.openPolicy(policyNumber);
		}
		policy.cancel().perform(getCancellationTD(cxDate));
	}

	protected void performReinstatement(String policyNumber) {
		if (!PolicySummaryPage.labelPolicyStatus.isPresent()) {
			SearchPage.openPolicy(policyNumber);
		}
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
		if (!PolicySummaryPage.labelPolicyStatus.isPresent()) {
			SearchPage.openPolicy(policyNumber);
		}
		policy.endorse().perform(getEndorsementTD(effDate));
		policy.getDefaultView().fill(getAddPremiumTD());
		SearchPage.openPolicy(policyNumber);
	}

	protected Dollar performRPEndorsement(String policyNumber, LocalDateTime effDate) {
		if (!PolicySummaryPage.labelPolicyStatus.isPresent()) {
			SearchPage.openPolicy(policyNumber);
		}
		policy.endorse().perform(getEndorsementTD(effDate));
		policy.getDefaultView().fill(getReducePremiumTD());
		return getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.ENDORSEMENT);
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

	protected LocalDateTime getCancellationEffectiveDate() {
		if (getPolicyType().isAutoPolicy()) {
			return TimeSetterUtil.getInstance().parse(PolicySummaryPage.tableGeneralInformation.getRow(1)
					.getCell(PolicyConstants.PolicyGeneralInformationTable.CANCELLATION_EFF_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);
		}
		return TimeSetterUtil.getInstance().parse(PolicySummaryPage.tableGeneralInformation.getRow(1)
				.getCell(PolicyConstants.PolicyGeneralInformationTable.CANCELLATION_EFFECTIVE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);
	}

	protected Dollar getBillingAmountByType(String type, String subtype) {
		return getBillingAmountByType(type, subtype, null);
	}

	protected Dollar getBillingAmountByType(String type, String subtype, LocalDateTime effDate) {
		Map<String, String> query = createQueryForPaymentsAndOtherTransactions(type, subtype, effDate);
		return new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(query).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue()).abs();
	}

	private Map<String, String> createQueryForPaymentsAndOtherTransactions(String type, String subtype, LocalDateTime effDate) {
		if (!BillingSummaryPage.tablePaymentsOtherTransactions.isPresent()) {
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		}
		Map<String, String> query = new HashMap<>();
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE, type);
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, subtype);
		if (effDate != null) {
			query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.EFF_DATE, formatDateToString(effDate));
		}
		return query;
	}

	protected int getTransactionIndexByType(String type, String subtype) {
		return getTransactionIndexByType(type, subtype, null);
	}

	protected int getTransactionIndexByType(String type, String subtype, LocalDateTime effDate) {
		Map<String, String> query = createQueryForPaymentsAndOtherTransactions(type, subtype, effDate);
		return BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(query).getIndex() - 1;
	}

	protected void waiveFeeByDateAndType(LocalDateTime txDate, String feeType) {
		Map<String, String> query = new HashMap<>();
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, formatDateToString(txDate));
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE, BillingConstants.PaymentsAndOtherTransactionType.FEE);
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, feeType);
		BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(query)
				.getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(BillingConstants.PaymentsAndOtherTransactionAction.WAIVE).click();
		BillingSummaryPage.dialogConfirmation.confirm();
	}

	protected void voidRefundPayment(String refundType) {
		clickBillingTransactionActionLink(BillingConstants.PaymentsAndOtherTransactionType.REFUND, refundType, BillingConstants.PaymentsAndOtherTransactionAction.VOID);
	}

	protected boolean isTaxState() {
		return getState().equals(Constants.States.KY) || getState().equals(Constants.States.WV);
	}

	protected Map<String, Dollar> getAllocationsFromTransaction(String type, String subtype) {
		return getAllocationsFromTransaction(type, subtype, null);
	}

	protected Map<String, Dollar> getAllocationsFromTransaction(String type, String subtype, LocalDateTime txDate) {
		if (!BillingSummaryPage.tablePaymentsOtherTransactions.isPresent()) {
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		}
		Map<String, String> query = new HashMap<>();
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE, type);
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, subtype);
		if (txDate != null) {
			query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, formatDateToString(txDate));
		}
		BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(query).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE).click();
		advancedAllocationsActionTab.linkAdvancedAllocation.click();

		return collectAllocationAmounts();
	}

	protected Map<String, Dollar> collectAllocationAmounts() {
		Map<String, Dollar> allocations = new HashMap<>();
		Table tableAdvancedAllocationsNetAndTaxes0 = new Table(By.xpath("//input[contains(@id, 'advAllocationForm:netPremiumAmount_0')]//ancestor::table[1]"));
		Table tablePolicyInfo0 = new Table(By.xpath("//input[contains(@id, 'advAllocationForm:subTotalAmount_0')]//preceding::table[1]"));
		Table tablePolicyInfo1 = new Table(By.xpath("//input[contains(@id, 'advAllocationForm:subTotalAmount_1')]//preceding::table[1]"));
		String effectiveDate0 = extractEffectiveDate(tablePolicyInfo0);
		String effectiveDate1 = extractEffectiveDate(tablePolicyInfo1);

		Dollar netPremium0 = extractNetPremium(tableAdvancedAllocationsNetAndTaxes0);
		allocations.put("Net Premium" + effectiveDate0, netPremium0);
		allocations.put("Net Premium", netPremium0);
		allocations.put("Taxes" + effectiveDate0, collectAllocationAmountsFrom(tableAdvancedAllocationsNetAndTaxes0).subtract(allocations.get("Net Premium" + effectiveDate0)));
		allocations.put("Taxes", allocations.get("Taxes" + effectiveDate0));

		Table tableAdvancedAllocationsFees0 = new Table(By.xpath("//input[contains(@id, 'advAllocationForm:feeAmount_0')]//ancestor::table[1]"));
		Dollar totalAmount = collectAllocationAmountsFrom(tableAdvancedAllocationsFees0);
		if (!totalAmount.isZero()) {
			allocations.put("Fees", totalAmount);
		}
		if (effectiveDate1 != null) {
			Table tableAdvancedAllocationsNetAndTaxes1 = new Table(By.xpath("//input[contains(@id, 'advAllocationForm:netPremiumAmount_1')]//ancestor::table[1]"));
			Dollar netPremium1 = extractNetPremium(tableAdvancedAllocationsNetAndTaxes1);
			allocations.put("Net Premium" + effectiveDate1, netPremium1);
			allocations.put("Net Premium", netPremium0.add(netPremium1));
			allocations.put("Taxes" + effectiveDate1, collectAllocationAmountsFrom(tableAdvancedAllocationsNetAndTaxes1).subtract(allocations.get("Net Premium" + effectiveDate1)));
			allocations.put("Taxes", allocations.get("Taxes" + effectiveDate0).add(allocations.get("Taxes" + effectiveDate1)));
			Table tableAdvancedAllocationsFees1 = new Table(By.xpath("//input[contains(@id, 'advAllocationForm:feeAmount_1')]//ancestor::table[1]"));
			totalAmount = totalAmount.add(collectAllocationAmountsFrom(tableAdvancedAllocationsFees1));
			if (!totalAmount.isZero()) {
				allocations.put("Fees", totalAmount);
			}
		}

		Tab.buttonBack.click();
		Tab.buttonBack.click();
		return allocations;
	}

	private Dollar extractNetPremium(Table tableAdvancedAllocationsNetAndTaxes) {
		Dollar netPremium = BillingHelper.DZERO;
		for (Row row : tableAdvancedAllocationsNetAndTaxes.getRows()) {
			if (row.getCell(1).getValue().equals("Net Premium")) {
				netPremium = netPremium.add(new Dollar(row.getCell(2).controls.textBoxes.getFirst().getValue()));
			}
		}
		return netPremium;
	}

	private String extractEffectiveDate(Table tablePolicyInfo) {
		if (tablePolicyInfo.isPresent()) {
			return tablePolicyInfo.getRow(1).getCell(1)
					.getWebElement().findElement(By.className("timezone")).getText();
		}
		return null;
	}

	private Dollar collectAllocationAmountsFrom(Table tableAdvancedAllocations) {
		Dollar totalAmount = BillingHelper.DZERO;
		if (!tableAdvancedAllocations.getRows().isEmpty()) {
			for (Row row : tableAdvancedAllocations.getRows()) {
				totalAmount= totalAmount.add(new Dollar(row.getCell(2).controls.textBoxes.getFirst().getValue()));
			}
		}
		return totalAmount;
	}

	protected Map<String, Dollar> getTaxAmountsForPolicy(String policyNumber) {
		Map<String, Dollar> taxes = new HashMap<>();
		String hoPolicyStateTaxDescription;
		if (getState().equals(Constants.States.WV)) {
			hoPolicyStateTaxDescription = HomeSSConstants.StateAndLocalTaxesTable.STATE_TAX;
		} else {
			hoPolicyStateTaxDescription = HomeSSConstants.StateAndLocalTaxesTable.PREMIUM_SURCHARGE;
		}

		if (!PolicySummaryPage.labelPolicyStatus.isPresent()) {
			SearchPage.openPolicy(policyNumber);
		}
		policy.policyInquiry().start();

		// Auto policies
		if (getPolicyType().isAutoPolicy()) {
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			if (getState().equals(Constants.States.KY)) {
				taxes.put(STATE, new Dollar(PremiumAndCoveragesTab.tableStateAndLocalTaxesSummaryDetailed
						.getRowContains(PolicyConstants.PolicyTaxesPremiumSurchargeDetailsTable.STATE_LOCAL_TAXES_PREMIUM_SURCHARGES, PolicyConstants.PolicyTaxesPremiumSurchargeDetailsTable.PREMIUM_SURCHARGE)
						.getCell(PolicyConstants.PolicyTaxesPremiumSurchargeDetailsTable.TERM_PREMIUM).getValue()));
				taxes.put(CITY, new Dollar(PremiumAndCoveragesTab.tableStateAndLocalTaxesSummaryDetailed
						.getRowContains(PolicyConstants.PolicyTaxesPremiumSurchargeDetailsTable.STATE_LOCAL_TAXES_PREMIUM_SURCHARGES, PolicyConstants.PolicyTaxesPremiumSurchargeDetailsTable.CITY_TAX)
						.getCell(PolicyConstants.PolicyTaxesPremiumSurchargeDetailsTable.TERM_PREMIUM).getValue()));
				taxes.put(COUNTY, new Dollar(PremiumAndCoveragesTab.tableStateAndLocalTaxesSummaryDetailed
						.getRowContains(PolicyConstants.PolicyTaxesPremiumSurchargeDetailsTable.STATE_LOCAL_TAXES_PREMIUM_SURCHARGES, PolicyConstants.PolicyTaxesPremiumSurchargeDetailsTable.COUNTY_TAX)
						.getCell(PolicyConstants.PolicyTaxesPremiumSurchargeDetailsTable.TERM_PREMIUM).getValue()));
			} else {
				taxes.put(STATE, new Dollar(PremiumAndCoveragesTab.tableStateAndLocalTaxesSummaryDetailed
						.getRowContains(PolicyConstants.PolicyTaxesPremiumSurchargeDetailsTable.STATE_LOCAL_TAX_PREMIUM_SURCHARGES, PolicyConstants.PolicyTaxesPremiumSurchargeDetailsTable.PREMIUM_SURCHARGE)
						.getCell(PolicyConstants.PolicyTaxesPremiumSurchargeDetailsTable.TERM_PREMIUM).getValue()));
			}
			new PremiumAndCoveragesTab().cancel();

		// PUP policies
		} else if (getPolicyType().equals(PolicyType.PUP)) {
			NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
			NavigationPage.toViewSubTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
			taxes.put(STATE, new Dollar(PremiumAndCoveragesQuoteTab.tableTaxes
					.getRowContains(HomeSSConstants.StateAndLocalTaxesTable.DESCRIPTION, hoPolicyStateTaxDescription)
					.getCell(HomeSSConstants.StateAndLocalTaxesTable.TERM_PREMIUM).getValue()));
			if (getState().equals(Constants.States.KY)) {
				taxes.put(CITY, new Dollar(PremiumAndCoveragesQuoteTab.tableTaxes
						.getRowContains(HomeSSConstants.StateAndLocalTaxesTable.DESCRIPTION, HomeSSConstants.StateAndLocalTaxesTable.CITY_TAX)
						.getCell(HomeSSConstants.StateAndLocalTaxesTable.TERM_PREMIUM).getValue()));
				taxes.put(COUNTY, new Dollar(PremiumAndCoveragesQuoteTab.tableTaxes
						.getRowContains(HomeSSConstants.StateAndLocalTaxesTable.DESCRIPTION, HomeSSConstants.StateAndLocalTaxesTable.COUNTY_TAX)
						.getCell(HomeSSConstants.StateAndLocalTaxesTable.TERM_PREMIUM).getValue()));
			}
			new PremiumAndCoveragesQuoteTab().cancel();

		// HO policies
		} else {
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
			NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
			taxes.put(STATE, new Dollar(PremiumsAndCoveragesQuoteTab.tableTaxes
					.getRowContains(HomeSSConstants.StateAndLocalTaxesTable.DESCRIPTION, hoPolicyStateTaxDescription)
					.getCell(HomeSSConstants.StateAndLocalTaxesTable.TERM_PREMIUM).getValue()));
			if (getState().equals(Constants.States.KY)) {
				taxes.put(CITY, new Dollar(PremiumsAndCoveragesQuoteTab.tableTaxes
						.getRowContains(HomeSSConstants.StateAndLocalTaxesTable.DESCRIPTION, HomeSSConstants.StateAndLocalTaxesTable.CITY_TAX)
						.getCell(HomeSSConstants.StateAndLocalTaxesTable.TERM_PREMIUM).getValue()));
				taxes.put(COUNTY, new Dollar(PremiumsAndCoveragesQuoteTab.tableTaxes
						.getRowContains(HomeSSConstants.StateAndLocalTaxesTable.DESCRIPTION, HomeSSConstants.StateAndLocalTaxesTable.COUNTY_TAX)
						.getCell(HomeSSConstants.StateAndLocalTaxesTable.TERM_PREMIUM).getValue()));
			}
			new PremiumsAndCoveragesQuoteTab().cancel();
		}

		if (getState().equals(Constants.States.KY)) {
			taxes.put(TOTAL, taxes.get(STATE).add(taxes.get(CITY)).add(taxes.get(COUNTY)));
		} else {
			taxes.put(TOTAL, taxes.get(STATE));
		}
		return taxes;
	}

	private void clickBillingTransactionActionLink(String type, String subType, String linkText) {
		Map<String, String> query = new HashMap<>();
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE, type);
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, subType);
		BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(query)
				.getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(linkText).click();
		BillingSummaryPage.dialogConfirmation.confirm();
	}

}
