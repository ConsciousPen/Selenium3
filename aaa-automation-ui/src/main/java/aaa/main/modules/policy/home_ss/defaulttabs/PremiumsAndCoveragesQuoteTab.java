/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ss.defaulttabs;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import aaa.toolkit.webdriver.customcontrols.RatingDetailsTable;
import org.openqa.selenium.By;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of a specific tab in a workspace. Tab classes from the default
 * workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB
 * LABEL>ActionTab (to prevent duplication). Modify this class if tab filling
 * procedure has to be customized, extra asset list to be added, custom testdata
 * key to be defined, etc.
 *
 * @category GeneratedPremiumsAndCoveragesQuoteTab
 */
public class PremiumsAndCoveragesQuoteTab extends PropertyQuoteTab {

	public PremiumsAndCoveragesQuoteTab() {
		super(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class);
	}

	public JavaScriptButton btnCalculatePremium() {
		return getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.CALCULATE_PREMIUM.getLabel(), JavaScriptButton.class);
	}

	public static Table tablefeesSummary = new Table(By.id("policyDataGatherForm:feesSummaryTable"));
	public static Table tableInstallmentFeeDetails = new Table(By.id("policyDataGatherForm:installmentFeeDetailsTable"));
	public static Table tableDiscounts = new Table(By.xpath("//table[@id='policyDataGatherForm:discountInfoTable']"));
	public static Table tableTaxes = new Table(By.id("policyDataGatherForm:taxSurchargeSummaryTable"));
	public static RatingDetailsTable tableCappedPolicyPremium = new RatingDetailsTable("//div[@id='cappingDetailsPopupPanel:vehicleCapPanel_body']//table");
	public static Table autoPaySetupSavingMessage = new Table(By.id("policyDataGatherForm:installmentFeeAmountSavedPanel"));
	public static Table tableCoverages = new Table(By.xpath("//table[@id='policyDataGatherForm:coverageSummaryTable']"));

	public static Button buttonViewCappingDetails = new Button(By.id("policyDataGatherForm:viewCappingDetails_Link_1"), Waiters.AJAX);

	public static Link linkPaymentPlan = new Link(By.id("policyDataGatherForm:paymentPlansTogglePanel:header"), Waiters.AJAX);
	public static Table tablePaymentPlans = new Table(By.id("policyDataGatherForm:paymentPlansTable"));
	public static Table tableUnrestrictedPaymentPlans = new Table(By.id("policyDataGatherForm:unrestrictedPaymentPlansTable"));
	public static StaticElement labelPaymentPlanRestriction = new StaticElement(By.xpath("//*[@id='policyDataGatherForm:paymentPlansTogglePanel:content']/table[2]/tbody"));
	public static StaticElement labelInstallmentFees = new StaticElement(By.xpath("(//*[@id='policyDataGatherForm:paymentPlansTogglePanel:content']/table)[last()]/tbody"));

	public static Link linkViewApplicableFeeSchedule = new Link(By.id("policyDataGatherForm:installmentFeeDetails"), Waiters.AJAX);
	public static Link linkViewCappingDetails = new Link(By.id("policyDataGatherForm:cappingHODetailsPopup"), Waiters.AJAX);
	public static Link linkViewPropertyQuote = new Link(By.id("policyDataGatherForm:viewHomeQuote_Link"), Waiters.AJAX);

	@Override
	public void calculatePremium() {

		if (!btnCalculatePremium().isPresent()) {
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
			NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		}
		try {
			hideHeader();
			btnCalculatePremium().click();
			showHeader();
		} catch (Exception e) {
			hideHeader();
			btnCalculatePremium().click();
			showHeader();
		}
	}

	public boolean isDiscountApplied(String discount) {
		Map<String, String> query = new HashMap<>();
		query.put("Discounts Applied", discount);
		return !tableDiscounts.getRowsThatContain(query).isEmpty();
	}

	public void openViewRatingDetails() {
		getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.VIEW_RATING_DETAILS.getLabel()).getWebElement().click();
	}
}
