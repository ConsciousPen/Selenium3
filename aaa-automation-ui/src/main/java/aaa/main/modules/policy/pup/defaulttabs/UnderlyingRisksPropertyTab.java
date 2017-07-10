/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.policy.pup.defaulttabs;

import aaa.common.Tab;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import toolkit.webdriver.controls.composite.assets.AssetList;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class UnderlyingRisksPropertyTab extends Tab {
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
    
    public AssetList getAdditionalResidenciesAssetList() {
    	return getAssetList().getControl(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.ADDITIONAL_RESIDENCIES.getLabel(), AssetList.class);
	}
    public AssetList getBusinessOrFarmingCoverageAssetList() {
    	return getAssetList().getControl(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.BUSINESS_OR_FARMING_COVERAGE.getLabel(), AssetList.class);
	}
    public AssetList getPetsOrAnimalsInfoAssetList() {
    	return getAssetList().getControl(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.PETS_OR_ANIMAL_INFORMATION.getLabel(), AssetList.class);
	}
    public AssetList getRecreationalEquipmentInfoAssetList() {
    	return getAssetList().getControl(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.RECREATIONAL_EQUIPMENT_INFORMATION.getLabel(), AssetList.class);
	}
}
