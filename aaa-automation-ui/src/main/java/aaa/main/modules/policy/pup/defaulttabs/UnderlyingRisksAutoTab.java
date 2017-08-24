/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.policy.pup.defaulttabs;

import org.openqa.selenium.By;

import aaa.common.Tab;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class UnderlyingRisksAutoTab extends Tab {
	public static Table tableAutomobiles = new Table(By.xpath("//table[tbody[@id='policyDataGatherForm:dataGatherView_ListPupAutomobile_data']]"));
	
    public UnderlyingRisksAutoTab() {
        super(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.class);
    }

    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }
    
    @Override
    public Tab fillTab(TestData td) {
        assetList.fill(td);
        return this;
    }
    
    public MultiInstanceAfterAssetList getDriversAssetList() {
    	return getAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.DRIVERS.getLabel(), MultiInstanceAfterAssetList.class);
	}
    public MultiInstanceAfterAssetList getAutomobilesAssetList() {
    	return getAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.AUTOMOBILES.getLabel(), MultiInstanceAfterAssetList.class);
	}
    public MultiInstanceAfterAssetList getMotorcyclesAssetList() {
    	return getAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.MOTORCYCLES.getLabel(), MultiInstanceAfterAssetList.class);
	}
    public MultiInstanceAfterAssetList getMotorHomesAssetList() {
    	return getAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.MOTOR_HOMES.getLabel(), MultiInstanceAfterAssetList.class);
	}
}
