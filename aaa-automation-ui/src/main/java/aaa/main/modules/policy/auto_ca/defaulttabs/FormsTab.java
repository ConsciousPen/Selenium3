/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ca.defaulttabs;

import org.openqa.selenium.By;

import toolkit.webdriver.controls.composite.table.Table;
import aaa.common.Tab;
import aaa.main.metadata.policy.AutoCaMetaData;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class FormsTab extends Tab {
	public static Table tableSelectedPolicyForms = new Table(By.id("policyDataGatherForm:body_selectedManageableComponentsTable_PolicyEndorsementFormsManager"));
	
    public FormsTab() {
        super(AutoCaMetaData.FormsTab.class);
    }

    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }
    
  
    
  
}
