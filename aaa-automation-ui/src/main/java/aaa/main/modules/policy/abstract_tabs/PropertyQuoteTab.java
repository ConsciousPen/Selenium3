/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.abstract_tabs;

import aaa.common.Tab;
import aaa.common.components.Dialog;
import aaa.main.metadata.policy.HomeSSMetaData;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.openqa.selenium.By;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

import java.text.DecimalFormat;

/**
 * Implementation of a specific tab in a workspace. Tab classes from the default
 * workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB
 * LABEL>ActionTab (to prevent duplication). Modify this class if tab filling
 * procedure has to be customized, extra asset list to be added, custom testdata
 * key to be defined, etc.
 *
 * @category Generated
 */
public abstract class PropertyQuoteTab extends Tab {

	public static Table tableEndorsementForms = new Table(By.id("policyDataGatherForm:formSummaryTable"));
	public static Button btnOverridePremium = new Button(By.xpath("//a[@id='policyDataGatherForm:overridePremiumLinkHo' or @id='policyDataGatherForm:overridePremiumLinkPup']"));
	public static Button btnCalculatePremium = new Button(By.id("policyDataGatherForm:premiumRecalcCov"), Waiters.AJAX);
	public static Table tablePremiumSummary = new Table(By.id("policyDataGatherForm:riskItemPremiumInfoTable"));
	public static Table tableTotalPremiumSummary = new Table(By.id("policyDataGatherForm:totalSummaryTable"));
	public static Table tableTotalDwellingSummary = new Table(By.id("policyDataGatherForm:dwellingSummaryTable"));
	public static Table tableDiscounts = new Table(By.id("policyDataGatherForm:discountInfoTable"));
	public static Link linkViewRatingDetails = new Link(By.id("policyDataGatherForm:ratingHODetailsPopup"), Waiters.AJAX);
	//	public static Table tableOverrideValues = new Table(By.xpath("//div[@id='coverage_information']/table[class='width100']"));
	public static TextBox textBoxOverrideFlatAmount = new TextBox(By.xpath("//input[@id='premiumOverrideInfoFormAAAHOPremiumOverride:deltaPremiumAmt' or @id='premiumOverrideInfoFormAAAPUPPremiumOverride:deltaPremiumAmt']"), Waiters.AJAX);
	public static TextBox textBoxOverridePercentageAmount = new TextBox(By.xpath("//input[@id='premiumOverrideInfoFormAAAHOPremiumOverride:percentageAmt' or @id='premiumOverrideInfoFormAAAPUPPremiumOverride:percentageAmt']"), Waiters.AJAX);
	public static Dialog dialogOverridePremium = new Dialog(By.xpath("//form[@id='premiumOverrideInfoFormAAAHOPremiumOverride' or @id='premiumOverrideInfoFormAAAPUPPremiumOverride']"));
	public static Dialog dialogOverrideConfirmation = new Dialog(By.id("overrideModalConfirmationDialog_container"));
	public static StaticElement lblOverridenPremium = new StaticElement(
			By.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOPreCovInfoMsg_body' or @id='policyDataGatherForm:componentView_AAAPUPPreCovInfoMsg_body']/div[@class='buttonsBlockInline width100 noSurround neutral_background buttons alignLeft']"));
	public static StaticElement lblErrorMessage = new StaticElement(By.xpath("//span[@class='error_message']"));
	public static Button btnContinue = new Button(By.id("policyDataGatherForm:nextButton_footer"), Waiters.AJAX);
	public static Table tablePremiumOverrideadjustment = new Table(By.id("premiumOverrideInfoFormAAAPUPPremiumOverride:adjustment_info"));

	protected PropertyQuoteTab(Class<? extends MetaData> mdClass) {
		super(mdClass);
	}

	public static Dollar getPolicyTermPremium() {
		return new Dollar(tableTotalPremiumSummary.getRow(1).getCell(tableTotalPremiumSummary.getColumnsCount()).getValue());
	}

	public static Dollar getPolicyDwellingPremium() {
		return new Dollar(tableTotalDwellingSummary.getRow(1).getCell(tableTotalPremiumSummary.getColumnsCount()).getValue());
	}

	public static Dollar getEndorsedPolicyTermPremium() {
		return new Dollar(tableTotalPremiumSummary.getRow(1).getCell(tableTotalPremiumSummary.getColumnsCount() - 2).getValue());
	}

	public static Dollar getPreEndorsementPremium() {
		return new Dollar(tableTotalPremiumSummary.getRow(1).getCell(tableTotalPremiumSummary.getColumnsCount() - 3).getValue());
	}
	public static Dollar getOverridenPremiumFlatAmount() {
		return new Dollar(textBoxOverrideFlatAmount.getValue());
	}

	public static int getOverridenPremiumPercentageAmount() {
		DecimalFormat df = new DecimalFormat("#.##");
		return (new Double(df.format(new Double(textBoxOverridePercentageAmount.getValue())))).intValue();
	}

	public static Dollar calculatedOverrideFlatAmount() {
		return new Dollar(getPolicyDwellingPremium().getPercentage(getOverridenPremiumPercentageAmount()));
	}

	public static int calculatedOverridePercentageAmount() {
		DecimalFormat df = new DecimalFormat("#.##");
		return (new Double(df.format((new Double(getOverridenPremiumFlatAmount().toPlaingString()) /
				(new Double(getPolicyDwellingPremium().toPlaingString())) * 100)))).intValue();
	}

	public static Dollar getFinalTermPremium() {
		return new Dollar(tablePremiumOverrideadjustment.getRow(3).getCell(2).getValue());
	}

	@Override
	public Tab fillTab(TestData td) {
		super.fillTab(convertValue(td));
		if (!td.getTestData(getMetaKey()).containsKey(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.CALCULATE_PREMIUM.getLabel())) {
			calculatePremium();
		}
		return this;
	}

	public Tab fillTab(TestData td, boolean calculatePremium) {
		super.fillTab(convertValue(td));
		if (calculatePremium)
			calculatePremium();
		return this;
	}

	@Override
	public Tab submitTab() {
		buttonNext.click();
		return this;
	}

	public abstract void calculatePremium();

	protected TestData convertValue(TestData td) {
		String returnValue = td.getTestData(getAssetList().getName()).getValue(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C.getLabel());
		if (returnValue != null && returnValue.contains("|")) {
			returnValue = getPercentForValue(returnValue);
			td.adjust(TestData.makeKeyPath(getAssetList().getName(), HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C.getLabel()), returnValue);
		}
		return td;
	}

	protected String getPercentForValue(String returnValue) {
		String[] percentAndCoverage = returnValue.split("\\|");
		Double percent = Double.parseDouble(percentAndCoverage[0]);
		Dollar coverage = new Dollar(getAssetList().getAsset(percentAndCoverage[1].replaceAll("\\$.*", "").trim()).getValue());
		return coverage.getPercentage(percent).toPlaingString();
	}

	public static class RatingDetailsView {
		public static RatingDetailsTable propertyInformation = new RatingDetailsTable("//table[@id='horatingDetailsPopupForm_1:ratingDetailsTable']");
		public static RatingDetailsTable discounts = new RatingDetailsTable("//table[@id='horatingDetailsPopupForm_6:ratingDetailsTable']");
		public static RatingDetailsTable values = new RatingDetailsTable("//table[@id='horatingDetailsPopupForm_5:ratingDetailsTable']");
		public static Button btn_Ok = new Button(By.id("ratingDetailsPopupButton:ratingDetailsPopupCancel"), Waiters.AJAX);

		public static void open() {
			linkViewRatingDetails.click();
		}

		public static void close() {
			btn_Ok.click();
		}

	}

	public static class RatingDetailsTable {
		private final String LOCATOR_TEMPLATE = "//td[.='%s']/following-sibling::td[1]";
		private String locator;
		private final String LABEL_LOCATOR_TEMPLATE = "//td[.='%s']";

		public RatingDetailsTable(String tableLocator) {
			this.locator = tableLocator;
		}

		public String getValueByKey(String key) {
			String label = this.locator + String.format(LOCATOR_TEMPLATE, key);
			return new StaticElement(By.xpath(label)).getValue();
		}

		public StaticElement getLabel(String key) {
			return new StaticElement(By.xpath(this.locator + String.format(LABEL_LOCATOR_TEMPLATE, key)));
		}
	}

}
