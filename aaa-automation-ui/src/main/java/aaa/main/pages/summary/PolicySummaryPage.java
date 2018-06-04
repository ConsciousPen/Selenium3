/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.pages.summary;

import static aaa.main.enums.PolicyConstants.PolicyVehiclesTable.MAKE;
import static aaa.main.enums.PolicyConstants.PolicyVehiclesTable.MODEL;
import static aaa.main.enums.PolicyConstants.PolicyVehiclesTable.YEAR;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.components.Dialog;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.PolicyConstants;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;

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
	public static StaticElement labelManualRenew = new StaticElement(By.id("productContextInfoForm:manualRenewFlag"));
	public static StaticElement labelPremiumWaived = new StaticElement(By.id("productContextInfoForm:premiumWaivedFlag"));
	public static StaticElement labelLapseExist = new StaticElement(By.id("productContextInfoForm:lapseExistsFlag"));

	public static Button buttonBackFromErrorPage = new Button(By.id("errorsForm:backToPreviousConsolidatedView"));
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
	public static Button buttonRenewalQuoteVersion = new Button(By.id("productContextInfoForm:stubRenewalQuoteVersions"));

	public static Link linkPolicy = new Link(By.id("productContextInfoForm:policyDetail_policyNumLnk"));

	public static ComboBox comboboxPolicyTerm = new ComboBox(By.id("historyForm:transactionsFilter"));

	public static Table tableTransactionHistory = new Table(By.xpath("//table[@id='historyForm:body_historyTable' or @id='quoteVersionHistoryForm:body_historyTable']")).applyConfiguration("CustomHint");
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
	public static Table tableTotalStateTaxesProperty = new Table(By.xpath("//table[@id='productConsolidatedViewForm:stateLocalTaxes']"));
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
	
	public static Dollar getTotalStateTaxesForProperty() {
		return new Dollar(tableTotalStateTaxesProperty.getRow(1).getCell(2).getValue());
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
		if (labelPolicyNumber.isPresent()) {
			return labelPolicyNumber.getValue();
		}
		return linkPolicy.getValue();
	}

    /**
     * Returns the text at the given row/column in the Auto Coverages Summary Table
     * Row 1 is the first vehicle, such as '#1, 2011, CHEVROLET, EXPRESS VAN'
     * Column 1 is 'Vehicle'
     */
	public static String getAutoCoveragesSummaryTextAt(int row, int column) {
        return getAutoCoveragesSummaryTables().get(row).getRow(1).getCell(column).getValue();
    }

    /**
     * Returns entire Auto Coverages Summary section as test data
     */
    public static TestData getAutoCoveragesSummaryTestData() {
        List<Table> coveragesTables = getAutoCoveragesSummaryTables();
        int numTables = coveragesTables.size();
        Map<String, Object> coverageDataList = new LinkedHashMap<>();

        Row labels = coveragesTables.get(0).getRow(1);
        String limit = labels.getCell(3).getValue();
        String deductible = labels.getCell(4).getValue();
        String premium = labels.getCell(5).getValue();

        for (int tableRow = 1; tableRow < numTables; tableRow++) {
            String firstValue = coveragesTables.get(tableRow).getRow(1).getCell(1).getValue();
            if (firstValue.startsWith("#")) {
                String thisVehicle = firstValue;
                Map<String, Object> coverages = new LinkedHashMap<>();
                tableRow++;
                do {
                    Map<String, String> limitsPremiumsData = new LinkedHashMap<>();
                    limitsPremiumsData.put(limit, coveragesTables.get(tableRow).getRow(1).getCell(3).getValue());
                    limitsPremiumsData.put(deductible, coveragesTables.get(tableRow).getRow(1).getCell(4).getValue());
                    limitsPremiumsData.put(premium, coveragesTables.get(tableRow).getRow(1).getCell(5).getValue());
                    coverages.put(coveragesTables.get(tableRow).getRow(1).getCell(2).getValue(), limitsPremiumsData);
                    firstValue = coveragesTables.get(++tableRow).getRow(1).getCell(1).getValue();
                } while (firstValue.isEmpty());
                coverageDataList.put(thisVehicle, coverages);
            }
            coverageDataList.put(firstValue, coveragesTables.get(tableRow).getRow(1).getCell(2).getValue());
        }
        return new SimpleDataProvider(coverageDataList);
    }

    private static List<Table> getAutoCoveragesSummaryTables() {
        int numTables = BrowserController.get().driver().findElements(By.xpath(".//div[@id='productConsolidatedViewForm:consolidatedInfoPanelCoveragesConsView_body']//table//table")).size();
        return IntStream.range(1, numTables + 1).mapToObj(i -> new Table(By.xpath(".//div[@id='productConsolidatedViewForm:consolidatedInfoPanelCoveragesConsView_body']//table//table[" + i + "]")))
                .collect(Collectors.toList());
    }

	public static void verifyCancelNoticeFlagPresent() {
		labelCancelNotice.verify.present("'Cancel Notice' flag is present");
		labelCancelNotice.verify.value("Cancel Notice");
	}

	public static void verifyCancelNoticeFlagNotPresent() {
		labelCancelNotice.verify.present("'Cancel Notice' flag is absent", false);
	}

	public static void verifyLapseExistFlagPresent() {
		labelLapseExist.verify.present("Lapse period flag is present");
		labelLapseExist.verify.value("Term includes a lapse period");
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

	public static void lastTransactionHistoryOpen() {
		if (buttonTransactionHistory.isPresent()) {
			buttonTransactionHistory.click();
		}
		tableTransactionHistory.getRow(1).getCell(2).controls.links.get(1).click();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
	}

	public static void transactionHistoryRecordCountCheck(String policyNumber, int rowCount, String value) {
		buttonTransactionHistory.click();
		CustomAssert.assertEquals(tableTransactionHistory.getRowsCount(), rowCount);
		String valueShort = "";
		if (!StringUtils.isEmpty(value)) {
			valueShort = value.substring(0, 20);
			assertThat(tableTransactionHistory.getRow(1).getCell("Reason").getHintValue()).contains(value);
		}
		assertThat(tableTransactionHistory.getRow(1).getCell("Reason").getValue()).contains(valueShort);
		/*not needed, because  getHint value already works
		String transactionHistoryQuery = "select * from(\n"
				+ "select pt.TXREASONTEXT\n"
				+ "from PolicyTransaction pt\n"
				+ "where POLICYID in \n"
				+ "        (select id from POLICYSUMMARY \n"
				+ "        where POLICYNUMBER = '%s')\n"
				+ "    order by pt.TXDATE desc)\n"
				+ "    where rownum=1";
		assertThat(DBService.get().getValue(String.format(transactionHistoryQuery, policyNumber)).orElse(StringUtils.EMPTY)).isEqualTo(value);*/
	}

	public static String getVehicleInfo(int rowNum) {
		String yearVeh = tablePolicyVehicles.getRow(rowNum).getCell(YEAR).getValue();
		String makeVeh = tablePolicyVehicles.getRow(rowNum).getCell(MAKE).getValue();
		String modelVeh = tablePolicyVehicles.getRow(rowNum).getCell(MODEL).getValue();
		return yearVeh + " " + makeVeh + " " + modelVeh;
	}
}
