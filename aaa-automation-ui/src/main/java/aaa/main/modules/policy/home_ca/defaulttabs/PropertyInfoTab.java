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
public class PropertyInfoTab extends Tab {
    public PropertyInfoTab() {
        super(HomeCaMetaData.PropertyInfoTab.class);
    }
    
    public Table tblAdditionalAddressessList = new Table(By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListAAAHOAdditionalAddress']//table"));
    public Table tblDetachedStructuresList = new Table(By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListAAAHODetachedStructuresInfoComponent']//table"));
    public Table tblPetsOrAnimalsList = new Table(By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListAAADwellAnimalInfoComponent']//table"));
    public Table tblClaimsList = new Table(By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListAAAHOLossInfo']//table"));
    
    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }
}
