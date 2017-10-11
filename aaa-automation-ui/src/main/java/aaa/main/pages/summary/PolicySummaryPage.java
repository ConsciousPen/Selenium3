/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.pages.summary;

import aaa.common.Tab;
import aaa.common.components.Dialog;
import aaa.main.enums.PolicyConstants;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.openqa.selenium.By;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;

import java.time.LocalDateTime;

public class PolicySummaryPage extends SummaryPage {

	public static StaticElement labelPolicyNumber = new StaticElement(By.id("productContextInfoForm:policyDetail_policyNumTxt"));
	public static StaticElement labelPolicyStatus = new StaticElement(By.id("productContextInfoForm:policyDetail_policyStatusCdText"));
	public static StaticElement labelPolicyEffectiveDate = new StaticElement(By.id("productContextInfoForm:policyDetail_policyStatusCdText_txEffectiveDate"));
	public static StaticElement labelPolicyBillingAccount = new StaticElement(By.id("productContextInfoForm:policyDetail_policyBilling"));
	public static StaticElement labelProductName = new StaticElement(By.id("productContextInfoForm:policyDetail_productCdText"));
	public static StaticElement labelPendedEndorsement = new StaticElement(By.id("productContextInfoForm:lnkPendedEndorsements"));
	public static StaticElement labelRenewals = new StaticElement(By.id("productContextInfoForm:lnkRenewals"));
	public static StaticElement labelCancelNotice = new StaticElement(By.id("productContextInfoForm:cancelNoticeFlag"));
	public static StaticElement labelDoNotRenew = new StaticElement(By.id("productContextInfoForm:doNotRenewFlag"));
	public static StaticElement labelTermIncludesLapsePeriod = new StaticElement(By.id("productContextInfoForm:lapseExistsFlag"));
	public static StaticElement labelManualRenew = new StaticElement(By.id("productContextInfoForm:manualRenewFlag"));
	public static StaticElement labelPremiumWaived = new StaticElement(By.id("productContextInfoForm:premiumWaivedFlag"));
	public static StaticElement labelLapseExist = new StaticElement(By.id("productContextInfoForm:lapseExistsFlag"));

	public static Button buttonTransactionHistory = new Button(By.id("productContextInfoForm:lnkTransactionHistory"));
	public static Button buttonPendedEndorsement = new Button(By.id("productContextInfoForm:lnkPendedEndorsements"));
	public static Button buttonRenewals = new Button(By.id("productContextInfoForm:lnkRenewals"));
	public static Button buttonPolicyOverview = new Button(By.id("historyForm:backToSummary"));
	public static Button buttonQuoteOverview = new Button(By.id("quoteVersionHistoryForm:backToSummary"));
	public static Button buttonAddCertificatePolicy = new Button(By.xpath("//*[@id='productConsolidatedViewForm:createChildRoot_Certificate' "
		+ "or @id='productConsolidatedViewForm:createChildRoot_certificate']"));
	public static Button buttonQuoteVersionHistory = new Button(By.id("productContextInfoForm:lnkQuoteHistoryHistory"));
	public static Button buttonCreateVersion = new Button(By.id("topCreateQuoteVersionLink"));
	public static Button buttonCompare = new Button(By.id("quoteVersionHistoryForm:compareSelectedVersions_footer"));
	public static Button buttonCompareVersions = new Button(By.id("historyForm:compareVersions_footer"));
	public static Button buttonTasks = new Button(By.xpath("//*[contains(@id,'tasksList') and text()='Tasks']"));
	public static Button buttonProceed = new Button(By.xpath("//button[.//span[@class='ui-button-text ui-c' and text()='Proceed'] or @value='Proceed']"));

	public static Link linkPolicy = new Link(By.id("productContextInfoForm:policyDetail_policyNumLnk"));

	public static ComboBox comboboxPolicyTerm = new ComboBox(By.id("historyForm:transactionsFilter"));

	public static Table tableTransactionHistory = new Table(By.xpath("//table[@id='historyForm:body_historyTable' or @id='quoteVersionHistoryForm:body_historyTable']"));
	public static Table tableReinsuranceStatus = new Table(By.id("productConsolidatedViewForm:body_scolumn_CLReinsuranceConsolidatedView"));
	public static Table tableEndorsements = new Table(By.id("endorsementForm:endorsementList"));
	public static Table tableRenewals = new Table(By.id("renewalForm:renewals_list_table"));
	public static Table tableSelectQuote = new Table(By.id("quotePageContents:body_quote_list_table"));
	public static Table tableSelectPolicy = new Table(By.id("policyForm:body_policy_list_table_holder"));
	public static Table tablePremiumAudit = new Table(By.xpath(".//div[@id='productConsolidatedViewForm:scolumn_CLPolicyPremiumAuditConsolidatedView']//table"));
	public static Table tablePolicyDrivers = new Table(By.id("productConsolidatedViewForm:body_scolumn_DriverView"));
	public static Table tablePolicyVehicles = new Table(By.id("productConsolidatedViewForm:body_scolumn_VehicleAndGaragingInformationMVO"));
	public static Table tableCoverageSummary = new Table(By.id("productConsolidatedViewForm:body_scolumn_GroupCoverageSplitComponent"));
	public static Table tablePolicyList = new Table(By.id("policyForm:body_policy_list_table_holder"));
	public static Table tableCertificatePolicies = new Table(By.id("productConsolidatedViewForm:rootInstancesTable_certificate"));
	public static Table tableGeneralInformation = new Table(By.xpath("//div[@id='scolumn_GeneralInformation' " + "or @id='productConsolidatedViewForm:consolidatedInfoPanelPolicy' "
		+ "or @id='productConsolidatedViewForm:consolidatedInfoPanelGeneralInfo']//table"));
	public static Table tableDifferences = new Table(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']/table"));
	public static Table tableInsuredInformation = new Table(By.xpath("//div[@id='productConsolidatedViewForm:consolidatedInfoPanelPreconfigInsured_body' "
		+ "or @id='productConsolidatedViewForm:scolumn_InsuredInformation' " + "or @id='scolumn_AAAHONamedInsured'" + "or @id='scolumn_AAAHONamedInsuredInformation'"
		+ "or @id='scolumn_InsuredInformationMVO'" + "or @id='productConsolidatedViewForm:consolidatedInfoPanelAAAPupNamedInsuredInfo_body']//table"));
	public static Table tableCoveragePremiumSummary = new Table(By.id("policyDataGatherForm:policySummary_ListCLGLPremOpsProdCoveragePremiumSummary"));
	public static Table tableCompare = new Table(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']/table[@role='treegrid']"));
	public static Table tableOtherUnderlyingRisks = new Table(By.xpath("//div[@id='productConsolidatedViewForm:pupUnderlyingRiskPanel_body'//table"));
	public static Table tableTotalPremiumSummaryProperty = new Table(By.xpath("//table[@id='productConsolidatedViewForm:totalSummaryTable' "
		+ "or @id='productConsolidatedViewForm:pupCoverageDetail' " + "or @id='productConsolidatedViewForm:pupTableTotalPremium']"));
	public static Table tablePupPropertyInformation = new Table(By.id("productConsolidatedViewForm:body_scolumn_PupConsolidatedPropertyInfo"));
	// cea
	public static Table tablePaymentSummary = new Table(By.xpath("//table[@id='productConsolidatedViewForm:billing_transactions_active']"));
	public static Table tableTransactionSummary = new Table(By.xpath("//table[@id='productConsolidatedViewForm:billing_transactions_active2']"));

	public static Table tableAppliedDiscountsPolicy = new Table(By.xpath("//table[@id='productConsolidatedViewForm:policyDiscountsTable']"));
	public static Table tableAppliedDiscountsDriver = new Table(By.xpath("//table[@id='productConsolidatedViewForm:driverDiscountsTable']"));
	public static Table tableAppliedDiscountsVehicle = new Table(By.xpath("//table[@id='productConsolidatedViewForm:vehicleDiscountsTable']"));

	public static Dialog dialogRemoveSuspense = new Dialog("//div[@id='validateActionPopup_container']");

	public static Dollar getTotalPremiumSummaryForProperty() {
		return new Dollar(tableTotalPremiumSummaryProperty.getRow(1).getCell(2).getValue());
	}

	public static Dollar getProposedRenewalPremium() {
		buttonRenewals.click();
		Dollar renewalPremium = new Dollar(tableRenewals.getRow(1).getCell(PolicyConstants.PolicyRenewalsTable.PREMIUM).getValue());
		Tab.buttonBack.click();

		return renewalPremium;
	}

	public static LocalDateTime getExpirationDate() {
		return TimeSetterUtil.getInstance().parse(tableGeneralInformation.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.EXPIRATION_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);
	}

	public static LocalDateTime getEffectiveDate() {
		LocalDateTime dateEffective;
		if (!tableGeneralInformation.isPresent() || tableGeneralInformation.isPresent() && !tableGeneralInformation.getHeader().getValue().contains("Effective Date")) {
			dateEffective = TimeSetterUtil.getInstance().parse(labelPolicyEffectiveDate.getValue(), DateTimeUtils.MM_DD_YYYY);
		} else {
			dateEffective = TimeSetterUtil.getInstance().parse(tableGeneralInformation.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.EFFECTIVE_DATE).getValue(),
				DateTimeUtils.MM_DD_YYYY);
		}

		return dateEffective;
	}

	public static String getPolicyNumber() {
		String policyNumber = labelPolicyNumber.getValue();
		return policyNumber;
	}

	public static void verifyCancelNoticeFlagPresent() {
		labelCancelNotice.verify.present("'Cancel Notice' flag is present");
		labelCancelNotice.verify.value("Cancel Notice");
	}

	public static void verifyCancelNoticeFlagNotPresent() {
		labelCancelNotice.verify.present("'Cancel Notice' flag is absent", false);
	}

	public static void verifyLapseExistFlagPresent() {
		PolicySummaryPage.labelLapseExist.verify.present("Lapse period flag is present");
		PolicySummaryPage.labelLapseExist.verify.value("Term includes a lapse period");
	}

	public static void verifyDoNotRenewFlagPresent() {
		labelDoNotRenew.verify.present("'Do Not Renew' flag is present");
		labelDoNotRenew.verify.value("Do Not Renew");
	}

	public static void verifyManualRenewFlagPresent() {
		labelManualRenew.verify.present("'Manual Renew' flag is present");
		labelManualRenew.verify.value("Manual Renew");
	}

	public static class TransactionHistory {
		public static LocalDateTime getEffectiveDate() {
			return getEffectiveDate(1);
		}

		public static Dollar getTranPremium() {
			return getTranPremium(1);
		}

		public static Dollar getEndingPremium() {
			return getEndingPremium(1);
		}

		public static String getReason() {
			return getReason(1);
		}

		public static String getType() {
			return getType(1);
		}

		public static void open() {
			buttonTransactionHistory.click();
		}

		public static void close() {
			Tab.buttonTopCancel.click();
		}

		public static Link provideLinkExpandComparisonTree(int i) {
			return new Link(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']//tr[@id='comparisonTreeForm:comparisonTree_node_" + i + "']"
				+ "/td[1]/span[contains(@class, 'ui-treetable-toggler')]"));
		}

		public static LocalDateTime readEffectiveDate(int row) {
			return TimeSetterUtil.getInstance().parse(tableTransactionHistory.getRow(row).getCell(PolicyConstants.PolicyTransactionHistoryTable.EFFECTIVE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);
		}

		public static LocalDateTime readEffectiveDate(String nameType) {
			return TimeSetterUtil.getInstance().parse(
				tableTransactionHistory.getRow(PolicyConstants.PolicyTransactionHistoryTable.TYPE, nameType).getCell(PolicyConstants.PolicyTransactionHistoryTable.EFFECTIVE_DATE).getValue(),
				DateTimeUtils.MM_DD_YYYY);
		}

		public static LocalDateTime readTransactionDate(String nameType) {
			return TimeSetterUtil.getInstance().parse(
				tableTransactionHistory.getRow(PolicyConstants.PolicyTransactionHistoryTable.TYPE, nameType).getCell(PolicyConstants.PolicyTransactionHistoryTable.TRANSACTION_DATE).getValue(),
				DateTimeUtils.MM_DD_YYYY);
		}

		public static Dollar readEndingPremium(int rowNumber) {
			return new Dollar(tableTransactionHistory.getRow(rowNumber).getCell(PolicyConstants.PolicyTransactionHistoryTable.ENDING_PREMIUM).getValue());
		}

		public static Dollar readTranPremium(int rowNumber) {
			return new Dollar(tableTransactionHistory.getRow(rowNumber).getCell(PolicyConstants.PolicyTransactionHistoryTable.TRAN_PREMIUM).getValue());
		}

		public static String readReason(int rowNumber) {
			return tableTransactionHistory.getRow(rowNumber).getCell(PolicyConstants.PolicyTransactionHistoryTable.REASON).getHintValue();
		}

		public static String readType(int rowNumber) {
			return tableTransactionHistory.getRow(rowNumber).getCell(PolicyConstants.PolicyTransactionHistoryTable.TYPE).getValue();
		}

		public static LocalDateTime getTransactionDate(String nameType) {
			open();
			LocalDateTime date = readTransactionDate(nameType);
			close();

			return date;
		}

		public static LocalDateTime getEffectiveDate(int row) {
			open();
			LocalDateTime date = readEffectiveDate(row);
			close();

			return date;
		}

		public static Dollar getEndingPremium(int row) {
			open();
			Dollar value = readEndingPremium(row);
			close();

			return value;
		}

		public static Dollar getTranPremium(int row) {
			open();
			Dollar value = readTranPremium(row);
			close();

			return value;
		}

		public static String getReason(int row) {
			open();
			String content = readReason(row);
			close();

			return content;
		}

		public static String getType(int row) {
			open();
			String content = readType(row);
			close();

			return content;
		}
	}
}
