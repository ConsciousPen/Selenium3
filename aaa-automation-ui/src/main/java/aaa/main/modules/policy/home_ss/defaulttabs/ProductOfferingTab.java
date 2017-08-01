/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ss.defaulttabs;

import org.openqa.selenium.By;

import aaa.common.Tab;
import aaa.main.metadata.policy.HomeSSMetaData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class ProductOfferingTab extends Tab {
    public ProductOfferingTab() {
        super(HomeSSMetaData.ProductOfferingTab.class);
    }
    public Button addEndorsement = new Button(By.id("policyDataGatherForm:addEndorsements_footer"), Waiters.AJAX);
    public Button btnCalculatePremium = new Button(By.id("policyDataGatherForm:premiumRecalcCov_footer"));
    public Button btnAddAdditionalVariation = new Button(By.id("policyDataGatherForm:addAdditionalVariation"));

    @Override
    public Tab submitTab() {
        if (addEndorsement.isPresent() && addEndorsement.isVisible()) {
            addEndorsement.click();
        }
        return this;
    }
}
