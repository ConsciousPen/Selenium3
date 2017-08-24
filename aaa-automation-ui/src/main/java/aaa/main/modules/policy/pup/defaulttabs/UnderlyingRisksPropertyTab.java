/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.policy.pup.defaulttabs;

import org.openqa.selenium.By;

import aaa.common.Tab;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class UnderlyingRisksPropertyTab extends Tab {
	public static Table tableAdditionalResidences = new Table(By.xpath("//table[tbody[@id='policyDataGatherForm:dataGatherView_ListPupAdditionalDwelling_data']]"));
	
    public UnderlyingRisksPropertyTab() {
        super(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.class);
    }

  /*  @Override
    public Tab fillTab(TestData td) {
        if (td.containsKey(getMetaKey()) && !td.getTestDataList(getMetaKey()).get(0).getKeys().isEmpty()) {
            super.getAssetList().fill(td);
        }

        return this;
    }*/

    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }
    
    public MultiInstanceAfterAssetList getAdditionalResidenciesAssetList() {
    	return getAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.ADDITIONAL_RESIDENCIES.getLabel(), MultiInstanceAfterAssetList.class);
	}
    public MultiInstanceAfterAssetList getBusinessOrFarmingCoverageAssetList() {
    	return getAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.BUSINESS_OR_FARMING_COVERAGE.getLabel(), MultiInstanceAfterAssetList.class);
	}
    public MultiInstanceAfterAssetList getPetsOrAnimalsInfoAssetList() {
    	return getAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.PETS_OR_ANIMAL_INFORMATION.getLabel(), MultiInstanceAfterAssetList.class);
	}
    public MultiInstanceAfterAssetList getRecreationalEquipmentInfoAssetList() {
    	return getAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.RECREATIONAL_EQUIPMENT_INFORMATION.getLabel(), MultiInstanceAfterAssetList.class);
	}
}
