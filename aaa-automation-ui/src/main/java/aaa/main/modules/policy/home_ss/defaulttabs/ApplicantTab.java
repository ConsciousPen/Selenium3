/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ss.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.main.metadata.policy.HomeSSMetaData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class ApplicantTab extends Tab {
    public ApplicantTab() {
        super(HomeSSMetaData.ApplicantTab.class);
    }

    public Button btnContinue = new Button(By.id("policyDataGatherForm:next_footer"), Waiters.AJAX);
    public Table tblInsuredList = new Table(By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListAAAHOOtherOrPriorPolicyComponent']//table"));

    @Override
    public Tab submitTab() {
        btnContinue.click();
        //TODO OSI: Workaround, sometimes not navigating to next tab from first attempt
        if (this.getAgentInfoAssetList().getAsset(HomeSSMetaData.ApplicantTab.AgentInfo.AGENT_OF_RECORD).isPresent()) {
            btnContinue.click();
        }
        //TODO: OSI: Workaround, agency/location/agent configuration broke after agency move in PAM, whcih causes Retrieve Channel ID service not to work properly
        //TODO: Will open a defect for EM team
/*	    if(this.getAgentInfoAssetList().getAsset(HomeSSMetaData.ApplicantTab.AgentInfo.AGENCY_LOCATION).getValue().isEmpty()){
            this.getAgentInfoAssetList().getAsset(HomeSSMetaData.ApplicantTab.AgentInfo.AGENCY_LOCATION).setValue("index=1");
            btnContinue.click();
        }*/
        return this;
    }

    public AssetList getNamedInsuredAssetList() {
        return getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), AssetList.class);
    }

    public AssetList getAAAMembershipAssetList() {
        return getAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), AssetList.class);
    }

    public AssetList getDwellingAddressAssetList() {
        return getAssetList().getAsset(HomeSSMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(), AssetList.class);
    }

    public AssetList getMailingAddressAssetList() {
        return getAssetList().getAsset(HomeSSMetaData.ApplicantTab.MAILING_ADDRESS.getLabel(), AssetList.class);
    }

    public AssetList getNamedInsuredInfoAssetList() {
        return getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED_INFORMATION.getLabel(), AssetList.class);
    }

    public AssetList getOtherAAAPoliciesAssetList() {
        return getAssetList().getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel(), AssetList.class);
    }

    public AssetList getAgentInfoAssetList() {
        return getAssetList().getAsset(HomeSSMetaData.ApplicantTab.AGENT_INFORMATION.getLabel(), AssetList.class);
    }
}
