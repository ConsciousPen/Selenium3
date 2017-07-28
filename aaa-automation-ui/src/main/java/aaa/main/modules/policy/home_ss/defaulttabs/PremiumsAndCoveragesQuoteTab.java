/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ss.defaulttabs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.common.Tab;
import aaa.main.metadata.policy.HomeSSMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Implementation of a specific tab in a workspace. Tab classes from the default
 * workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB
 * LABEL>ActionTab (to prevent duplication). Modify this class if tab filling
 * procedure has to be customized, extra asset list to be added, custom testdata
 * key to be defined, etc.
 * 
 * @category Generated
 */
public class PremiumsAndCoveragesQuoteTab extends Tab {

	public PremiumsAndCoveragesQuoteTab() {
		super(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class);
	}

	public static Button  btnCalculatePremium = new Button(By.id("policyDataGatherForm:premiumRecalcCov"), Waiters.AJAX);
	public static Table tablePremiumSummary = new Table(By.id("policyDataGatherForm:riskItemPremiumInfoTable"));
	public static Table tableTotalPremiumSummary = new Table(By.id("policyDataGatherForm:totalSummaryTable"));
	public static Table tableDiscounts = new Table(By.id("policyDataGatherForm:discountInfoTable"));
	public Button btnContinue = new Button(By.id("policyDataGatherForm:nextButton_footer"), Waiters.AJAX);
	
	public static Link linkViewRatingDetails = new Link(By.id("policyDataGatherForm:ratingHODetailsPopup"), Waiters.AJAX);

	@Override
	public Tab fillTab(TestData td) {
		super.fillTab(convertValue(td));
		calculatePremium();
		return this;
	}

	public void calculatePremium() {
		btnCalculatePremium.click();
	}

	private TestData convertValue(TestData td) {
		String returnValue = td.getTestData(getAssetList().getName()).getValue(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C.getLabel());
		if (returnValue!=null && returnValue.contains("|")) {
			Pattern p = Pattern.compile("^([^\\|]+)\\|([^$]+)");
			Matcher m = p.matcher(returnValue);
			m.find();
			Double percent = Double.parseDouble(m.group(1));
			Dollar coverage = new Dollar(getAssetList().getAsset((m.group(2)), StaticElement.class).getValue());
			returnValue = coverage.getPercentage(percent).toPlaingString();
			td.adjust(TestData.makeKeyPath(getAssetList().getName(), HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C.getLabel()), returnValue);
		}
		return td;
	}

	@Override
	public Tab submitTab() {
		btnContinue.click();
		return this;
	}
	
	public static Dollar getPolicyTermPremium(){
		return new Dollar(tableTotalPremiumSummary.getRow(1).getCell(tableTotalPremiumSummary.getColumnsCount()).getValue());
		//return new Dollar(tblTotalPremiumSummary.getRow(1).getCell(tblTotalPremiumSummary.getColumnsCount()-2).getValue());
	}
	
	public static Dollar getEndorsedPolicyTermPremium(){
		return new Dollar(tableTotalPremiumSummary.getRow(1).getCell(tableTotalPremiumSummary.getColumnsCount()-2).getValue());
	}
	
	public static class RatingDetailsView {
			
		public static RatingDetailsTable discounts = new RatingDetailsTable("//table[@id='horatingDetailsPopupForm_6:ratingDetailsTable']");
		public static Button btn_Ok = new Button(By.id("ratingDetailsPopupButton:ratingDetailsPopupCancel"), Waiters.AJAX);	
		
		public static void open() {
			linkViewRatingDetails.click();
		}
		
		public static void close() {
			btn_Ok.click();
		}
		
	}
	
	public static class RatingDetailsTable {
		private String locator;
		private final String LOCATOR_TEMPLATE = "//td[.='%s']/following-sibling::td[1]";
		//private final String LABEL_LOCATOR_TEMPLATE = "//td[.='%s']";
		
		public RatingDetailsTable(String tableLocator) {
			this.locator = tableLocator;
		}
		
		public String getValueByKey(String key) {
			String label = this.locator + String.format(LOCATOR_TEMPLATE, key);
			return new StaticElement(By.xpath(label)).getValue();
		}
	}

}
