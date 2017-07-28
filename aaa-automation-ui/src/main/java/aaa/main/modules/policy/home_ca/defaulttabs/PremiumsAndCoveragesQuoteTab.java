/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ca.defaulttabs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.common.Tab;
import aaa.main.metadata.policy.HomeCaMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
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
		super(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.class);
	}

	// public Button btnCalculatePremium = new
	// Button(By.id("policyDataGatherForm:premiumRecalcCov"), Waiters.AJAX);
	public Button btnContinue = new Button(By.id("policyDataGatherForm:next_footer"), Waiters.AJAX);

	@Override
	public Tab fillTab(TestData td) {
		super.fillTab(convertValue(td));
		if (!td.getTestData(getMetaKey()).containsKey(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.CALCULATE_PREMIUM_BUTTON.getLabel()))
			calculatePremium();
		return this;
	}

	public Button btnCalculatePremium() {
		return getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.CALCULATE_PREMIUM_BUTTON.getLabel(), Button.class);
	}

	public void calculatePremium() {
		btnCalculatePremium().click();
	}

	private TestData convertValue(TestData td) {
		TestData tdCoverages = td.getTestData(getAssetList().getName());
		for (String key : tdCoverages.getKeys()) {
			String value = tdCoverages.getValue(key);
			if (value!=null && value.contains("|")) {
				Pattern p = Pattern.compile("^([^\\|]+)\\|([^$]+)");
				Matcher m = p.matcher(value);
				m.find();
				Double percent = Double.parseDouble(m.group(1));
				Dollar coverage = new Dollar(getAssetList().getAsset((m.group(2)), StaticElement.class).getValue());
				value = coverage.getPercentage(percent).toPlaingString();
				td.adjust(TestData.makeKeyPath(getAssetList().getName(), key), value);
			}
		}
		return td;
	}

	@Override
	public Tab submitTab() {
		btnContinue.click();
		return this;
	}
}
