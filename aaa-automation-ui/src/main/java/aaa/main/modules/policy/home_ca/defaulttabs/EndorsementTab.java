/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ca.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.abstract_tabs.PropertyEndorsementsTab;
import aaa.toolkit.webdriver.customcontrols.endorsements.HomeCAEndorsementsMultiAssetList;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class EndorsementTab extends PropertyEndorsementsTab {
    public EndorsementTab() {
        super(HomeCaMetaData.EndorsementTab.class);

	    tblIncludedEndorsements = new Table(By.xpath("//div[@id='policyDataGatherForm:selectedObjects_AAAHoPolicyEndorsementFormManager:content']/div/table"));
	    tblOptionalEndorsements = new Table(By.xpath("//table[@id='policyDataGatherForm:availableTable_AAAHoPolicyEndorsementFormManager']"));

	    TBL_SUB_ENDORSEMENTS_BY_FORMID_TEMPLATE = "//div[@id='policyDataGatherForm:selectedObjects_AAAHoPolicyEndorsementFormManager:content']/descendant::table[1]/tbody/tr[count(../tr[td[position()=1 and normalize-space(.)='%s']]/preceding-sibling::tr)+3]/descendant::table[1]";
	    LNK_ADD_SUB_ENDORSEMENT_BY_FORMID_TEMPLATE = "//div[@id='policyDataGatherForm:selectedObjects_AAAHoPolicyEndorsementFormManager:content']/descendant::table[1]/tbody/tr[count(../tr[td[position()=1 and normalize-space(.)='%s']]/preceding-sibling::tr)+2]//a[.='Add']";

	    btnSaveForm = new Button(By.id("policyDataGatherForm:editObjectSaveBtnAAAHoPolicyEndorsementFormManager"));
	    btnCancelForm = new Button(By.xpath("//input[@id='policyDataGatherForm:editObjectCancelEliminateBtn_AAAHoPolicyEndorsementFormManager' or @id='policyDataGatherForm:editObjectCancelBtn_AAAHoPolicyEndorsementFormManager']"));
    }
    
    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }

	public HomeCAEndorsementsMultiAssetList getFormAssetList(AssetDescriptor endorsementAsset) {
		return (HomeCAEndorsementsMultiAssetList) getAssetList().getAsset(endorsementAsset);
	}
}
