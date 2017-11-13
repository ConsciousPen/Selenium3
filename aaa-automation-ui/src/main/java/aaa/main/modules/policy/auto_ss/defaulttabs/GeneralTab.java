/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.policy.AutoSSMetaData;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class GeneralTab extends Tab {

    public static Table tblInsuredList = new Table(By.xpath("//div[@id='policyDataGatherForm:componentView_Insured_body']//table"));

    public GeneralTab() {
        super(AutoSSMetaData.GeneralTab.class);
    }

    public MultiAssetList getNamedInsuredInfoAssetList() {
        return getAssetList().getAsset(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel(), MultiAssetList.class);
    }

    public AssetList getValidateAddressDialogAssetList() {
        return getAssetList().getAsset(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel(), AssetList.class)
                .getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.VALIDATE_ADDRESS_DIALOG.getLabel(), AssetList.class);
    }

    public AssetList getAAAProductOwnedAssetList() {
        return getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AssetList.class);
    }

    public AssetList getCurrentCarrierInfoAssetList() {
        return getAssetList().getAsset(AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(), AssetList.class);
    }

    public AssetList getPolicyInfoAssetList() {
        return getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AssetList.class);
    }

    public AssetList getContactInfoAssetList() {
        return getAssetList().getAsset(AutoSSMetaData.GeneralTab.CONTACT_INFORMATION.getLabel(), AssetList.class);
    }

    @Override
    public Tab submitTab() {
        //BUG PAS-6060 Membership Last name is cleared when changing Agent Entered BI Limits
        getAAAProductOwnedAssetList().getAsset("Last name", TextBox.class).setValue("test");
        buttonNext.click();
        return this;
    }

    public void removeInsured(int index) {
        if (tblInsuredList.isPresent() && tblInsuredList.getRow(index).isPresent()) {
            tblInsuredList.getRow(index).getCell(4).controls.links.get("Remove").click(Waiters.AJAX);
            Page.dialogConfirmation.confirm();
        }
    }

    public void viewInsured(int index) {
        if (tblInsuredList.isPresent() && tblInsuredList.getRow(index).isPresent()) {
            tblInsuredList.getRow(index).getCell(4).controls.links.get("View/Edit").click(Waiters.AJAX);
        }
    }
}
