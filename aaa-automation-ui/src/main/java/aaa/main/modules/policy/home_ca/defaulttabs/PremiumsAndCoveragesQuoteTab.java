/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ca.defaulttabs;

import org.openqa.selenium.By;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.table.Table;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;

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

    public PremiumsAndCoveragesQuoteTab() {
        super(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.class);
    }

    public Button btnCalculatePremium() {
        return getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.CALCULATE_PREMIUM_BUTTON.getLabel(), Button.class);
    }

    @Override
    public void calculatePremium() {
        btnCalculatePremium().click();
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
}
