/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ca.defaulttabs;

import org.openqa.selenium.By;

import aaa.common.Tab;
import aaa.main.metadata.policy.AutoCaMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class PremiumAndCoveragesTab extends Tab {
	
    public static Button buttonCalculatePremium = new Button(By.id("policyDataGatherForm:premiumRecalc"));
    public static StaticElement labelProductInquiry = new StaticElement(By.xpath("//span[@id='policyDataGatherForm:sedit_AAAProductOverride_policyFormCd']"));
    
    //-- old controls
    public static Table tablePremiumSummary = new Table(By.id("policyDataGatherForm:riskItemPremiumInfoTable"));
    public static Button buttonCommissionOverride = new Button(By.id("policyDataGatherForm:commissionOverrideButton"));
    public Button btnContinue = new Button(By.id("policyDataGatherForm:nextButton_footer"), Waiters.AJAX);
    
    //--
    public PremiumAndCoveragesTab() {
        super(AutoCaMetaData.PremiumAndCoveragesTab.class);
    }

    @Override
    public Tab fillTab(TestData td) {
        super.fillTab(td);
        buttonCalculatePremium.click();
        return this;
    }
    
    @Override
    public Tab submitTab() {
    	btnContinue.click();
        return this;
    }
}


