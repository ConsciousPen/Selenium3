/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ca.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

	public static Table tableEndorsementForms = new Table(By.id("policyDataGatherForm:formSummaryTable"));
	public static Button btnContinue = new Button(By.id("policyDataGatherForm:next_footer"), Waiters.AJAX);
	public static Link linkViewPropertyQuote = new Link(By.id("policyDataGatherForm:viewHomeQuoteCA_Link"), Waiters.AJAX);

	public PremiumsAndCoveragesQuoteTab() {
		super(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.class);
	}

    public JavaScriptButton btnCalculatePremium() {
        return getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.CALCULATE_PREMIUM_BUTTON.getLabel(), JavaScriptButton.class);
    }

	@Override
	public void calculatePremium() {
		if (!btnCalculatePremium().isPresent()) {
			NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
			NavigationPage.toViewSubTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
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

	@Override
	protected TestData convertValue(TestData td) {
		TestData tdCoverages = td.getTestData(getAssetList().getName());
		for (String key : tdCoverages.getKeys()) {
			String value = tdCoverages.getValue(key);
			if (value != null && value.contains("|")) {
				value = getPercentForValue(value);
				td.adjust(TestData.makeKeyPath(getAssetList().getName(), key), value);
			}
		}
		return td;
	}

	/**
	 * Determines if discount is present on P&C Quote Tab.
	 * @param discount Discount Text, as it appears in the UI.
	 * @return
	 */
	public boolean isDiscountApplied(String discount) {
		Map<String, String> query = new HashMap<>();
		query.put("Discounts applied", discount);
		return !tableDiscounts.getRowsThatContain(query).isEmpty();
	}
}
