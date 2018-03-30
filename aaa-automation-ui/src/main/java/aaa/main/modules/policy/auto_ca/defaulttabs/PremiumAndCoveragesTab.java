/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ca.defaulttabs;

import org.openqa.selenium.By;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.AutoCaMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 *
 * @category Generated
 */
public class PremiumAndCoveragesTab extends Tab {

	public static Button buttonCalculatePremium = new Button(By.id("policyDataGatherForm:premiumRecalc"));
	public static StaticElement labelProductInquiry = new StaticElement(By.xpath("//span[@id='policyDataGatherForm:sedit_AAAProductOverride_policyFormCd']"));
	public static Link buttonViewRatingDetails = new Link(By.id("policyDataGatherForm:viewRatingDetails_Link"));
	public static Button buttonRatingDetailsOk = new Button(By.id("ratingDetailsPopupButton:ratingDetailsPopupCancel"));
	public static Table tableRatingDetailsQuoteInfo = new Table(By.id("ratingDetailsPopupForm:policy_summary"));
	public static Table tableRatingDetailsVehicles = new Table(By.id("ratingDetailsPopupForm:vehicle_summary"));
	public static Table tableDiscounts = new Table(By.id("policyDataGatherForm:discountSurchargeSummaryTable"));

	// -- old controls
	public static Table tablePremiumSummary = new Table(By.id("policyDataGatherForm:AAAPremiumSummary"));
	public static Button buttonCommissionOverride = new Button(By.id("policyDataGatherForm:commissionOverrideButton"));
	public Button btnContinue = new Button(By.id("policyDataGatherForm:nextButton_footer"), Waiters.AJAX);

	// --
	public PremiumAndCoveragesTab() {
		super(AutoCaMetaData.PremiumAndCoveragesTab.class);
	}

	public static Dollar getPolicyTermPremium() {
		return new Dollar(tablePremiumSummary.getRow(1).getCell(4).getValue());
	}

	public static void calculatePremium() {
		if (!buttonCalculatePremium.isPresent()) {
			NavigationPage.toViewSubTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		}
		buttonCalculatePremium.click();
	}

	@Override
	public Tab fillTab(TestData td) {
		super.fillTab(td);
		if (td.getTestData(getMetaKey()) != null && !td.getTestData(getMetaKey()).containsKey(AutoCaMetaData.PremiumAndCoveragesTab.CALCULATE_PREMIUM.getLabel())) {
			hideHeader();
			buttonCalculatePremium.click();
			showHeader();
		}
		return this;
	}

	@Override
	public Tab submitTab() {
		btnContinue.click();
		return this;
	}
}
