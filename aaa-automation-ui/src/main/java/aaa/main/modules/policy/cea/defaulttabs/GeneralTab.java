/*Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.policy.cea.defaulttabs;

import org.openqa.selenium.By;

import aaa.common.Tab;
import aaa.main.metadata.policy.CaliforniaEarthquakeMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class GeneralTab extends Tab {
    public GeneralTab() {
        super(CaliforniaEarthquakeMetaData.GeneralTab.class);
    }
    public Button btnContinue = new Button(By.id("policyDataGatherForm:next_footer"), Waiters.AJAX);

    @Override
    public Tab submitTab() {
    	btnContinue.click();
        return this;
    }
    
    public TestData adjustWithRealPolicy(TestData td, String expectedValue){
    	return td.adjust(TestData.makeKeyPath(getMetaKey(), CaliforniaEarthquakeMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), CaliforniaEarthquakeMetaData.GeneralTab.PolicyInformation.POLICY_NUMBER.getLabel()), expectedValue);
	}
    
    public AssetList getPolicyInfoAssetList() {
    	return getAssetList().getControl(CaliforniaEarthquakeMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AssetList.class);
	}
    public AssetList getCeaPolicyInfoAssetList() {
    	return getAssetList().getControl(CaliforniaEarthquakeMetaData.GeneralTab.CEA_POLICY_INFORMATION.getLabel(), AssetList.class);
	}
    public AssetList getDwellingAddressAssetList() {
    	return getAssetList().getControl(CaliforniaEarthquakeMetaData.GeneralTab.DWELLING_ADDRESS.getLabel(), AssetList.class);
	}
    public AssetList getMailingAddressAssetList() {
    	return getAssetList().getControl(CaliforniaEarthquakeMetaData.GeneralTab.MAILING_ADDRESS.getLabel(), AssetList.class);
	}
    public AssetList getNamedInsuredAssetList() {
    	return getAssetList().getControl(CaliforniaEarthquakeMetaData.GeneralTab.NAMED_INSURED.getLabel(), AssetList.class);
	}
    public AssetList getNamedInsuredContactInfoAssetList() {
    	return getAssetList().getControl(CaliforniaEarthquakeMetaData.GeneralTab.NAMED_INSURED_CONTACT_INFORMATION.getLabel(), AssetList.class);
	}
    public AssetList getAgencyInfoAssetList() {
    	return getAssetList().getControl(CaliforniaEarthquakeMetaData.GeneralTab.AGENCY_INFORMATION.getLabel(), AssetList.class);
	}
}
