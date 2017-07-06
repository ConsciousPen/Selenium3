/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.CustomerMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.composite.assets.AssetList;

// TODO move assetlist initialization and adding configuration to constructor.
public class SponsorParticipantRelationshipAssociationRemovalActionTab extends ActionTab {

    public static Button buttonRelationshipAssociationRemove = new Button(By.xpath("//*[contains(@id, 'crmForm:relationshipAssociationRemovalConfirmationPopup_btnOK_keepHistory')]"));

    public SponsorParticipantRelationshipAssociationRemovalActionTab() {
        super(CustomerMetaData.SponsorParticipantRelationshipAssociationRemovalActionTab.class);

    }

    @Override
    public Tab fillTab(TestData td) {
        if (td.containsKey(getMetaKey())) {
            assetList = new AssetList(By.xpath("//*[contains(@id, 'crmForm:relationshipAssociationRemovalConfirmationPopup_container')]"), metaDataClass);
            assetList.config.applyConfiguration("ParticipantKeepHistory");
            assetList.fill(td);
        }
        return this;
    }

    @Override
    public Tab submitTab() {
        if (assetList.getControl(CustomerMetaData.SponsorParticipantRelationshipAssociationRemovalActionTab.KEEP_RELATIONSHIP_HISTORY.getLabel(), CheckBox.class).getValue()) {
            buttonRelationshipAssociationRemove.click();
        } else {
            Page.dialogConfirmation.confirm();
        }
        return this;
    }
}
