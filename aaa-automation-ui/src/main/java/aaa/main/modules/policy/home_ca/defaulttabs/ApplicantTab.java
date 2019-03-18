/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ca.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class ApplicantTab extends Tab {
    public ApplicantTab() {
        super(HomeCaMetaData.ApplicantTab.class);
    }
    
    public Table tblNamedInsuredsList = new Table(By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListAAAHONamedInsured']//table"));
    public Table tblOtherActiveAAAPoliciesList = new Table(By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListAAAHOOtherOrPriorPolicyComponent']//table"));

    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }
    
    public MultiInstanceAfterAssetList getNamedInsuredAssetList() {
    	return getAssetList().getAsset(HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), MultiInstanceAfterAssetList.class);
	}
    public AssetList getAAAMembershipAssetList() {
    	return getAssetList().getAsset(HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), AssetList.class);
	}
    public AssetList getDwellingAddressAssetList() {
    	return getAssetList().getAsset(HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(), AssetList.class);
	}
    public AssetList getPreviousDwellingAddressAssetList() {
    	return getAssetList().getAsset(HomeCaMetaData.ApplicantTab.PREVIOUS_DWELLING_ADDRESS.getLabel(), AssetList.class);
	}
    public AssetList getMailingAddressAssetList() {
    	return getAssetList().getAsset(HomeCaMetaData.ApplicantTab.MAILING_ADDRESS.getLabel(), AssetList.class);
	}
    public AssetList getNamedInsuredInfoAssetList() {
    	return getAssetList().getAsset(HomeCaMetaData.ApplicantTab.NAMED_INSURED_INFORMATION.getLabel(), AssetList.class);
	}
    public MultiInstanceAfterAssetList getOtherAAAPoliciesAssetList() {
    	return getAssetList().getAsset(HomeCaMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel(), MultiInstanceAfterAssetList.class);
	}
    public AssetList getAgentInfoAssetList() {
    	return getAssetList().getAsset(HomeCaMetaData.ApplicantTab.AGENT_INFORMATION.getLabel(), AssetList.class);
	}
}
