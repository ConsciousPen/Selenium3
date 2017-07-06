/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ca.defaulttabs;

import org.openqa.selenium.By;

import aaa.common.Tab;
import aaa.main.metadata.policy.HomeCaMetaData;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class EndorsementTab extends Tab {
    public EndorsementTab() {
        super(HomeCaMetaData.EndorsementTab.class);
    }
    
    public Table tblIncludedAndSelectedEndorsements = new Table(By.xpath("//div[@id='policyDataGatherForm:selectedObjects_AAAHoPolicyEndorsementFormManager:content']//table"));
    public Table tblOptionalEndorsements = new Table(By.xpath("//table[@id='policyDataGatherForm:availableTable_AAAHoPolicyEndorsementFormManager']"));
    
    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }
}
