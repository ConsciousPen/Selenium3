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
public class GeneralTab extends Tab {
    public GeneralTab() {
        super(PersonalUmbrellaMetaData.GeneralTab.class);
    }

    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }
    
    public AssetList getPolicyInfoAssetList() {
    	return getAssetList().getControl(PersonalUmbrellaMetaData.GeneralTab.POLICY_INFO.getLabel(), AssetList.class);
	}
    public AssetList getAAAMembershipAssetList() {
    	return getAssetList().getControl(PersonalUmbrellaMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel(), AssetList.class);
	}
    public AssetList getDwellingAddressAssetList() {
    	return getAssetList().getControl(PersonalUmbrellaMetaData.GeneralTab.DWELLING_ADDRESS.getLabel(), AssetList.class);
	}
    public AssetList getMailingAddressAssetList() {
    	return getAssetList().getControl(PersonalUmbrellaMetaData.GeneralTab.MAILING_ADDRESS.getLabel(), AssetList.class);
	}
    public AssetList getThirdPartyDesigneeAssetList() {
    	return getAssetList().getControl(PersonalUmbrellaMetaData.GeneralTab.THIRD_PARTY_DESIGNEE.getLabel(), AssetList.class);
	}
    public AssetList getNamedInsuredContactsAssetList() {
    	return getAssetList().getControl(PersonalUmbrellaMetaData.GeneralTab.NAMED_INSURED_CONTACT_INFORMATION.getLabel(), AssetList.class);
	}
}
