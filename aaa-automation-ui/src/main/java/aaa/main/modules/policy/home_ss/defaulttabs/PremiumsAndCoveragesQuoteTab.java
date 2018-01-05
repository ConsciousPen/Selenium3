/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ss.defaulttabs;
import org.openqa.selenium.By;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
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
public class PremiumsAndCoveragesQuoteTab extends PropertyQuoteTab {

	public PremiumsAndCoveragesQuoteTab() {
		super(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class);
	}

	public Button btnCalculatePremium() {
		return getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.CALCULATE_PREMIUM.getLabel(), Button.class);
	}

	public static Table tablefeesSummary = new Table(By.id("policyDataGatherForm:feesSummaryTable"));
	public static Table tableInstallmentFeeDetails = new Table(By.id("policyDataGatherForm:installmentFeeDetailsTable"));
	public static Table autoPaySetupSavingMessage = new Table (By.id("policyDataGatherForm:installmentFeeAmountSavedPanel"));
	public static Table tableCappedPolicyPremium = new Table (By.xpath("//div[@id='cappingDetailsPopupPanel:vehicleCapPanel_body']//table"));

	public static Link linkPaymentPlan = new Link(By.id("policyDataGatherForm:paymentPlansTogglePanel:header"), Waiters.AJAX);
	public static Link linkViewApplicableFeeSchedule = new Link(By.id("policyDataGatherForm:installmentFeeDetails"), Waiters.AJAX);
	public static Link linkViewCappingDetails = new Link(By.id("policyDataGatherForm:cappingHODetailsPopup"), Waiters.AJAX);

	@Override
	public void calculatePremium() {
		if (!btnCalculatePremium().isPresent()) {
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
			NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		}
		btnCalculatePremium().click();
	}
}
