/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ca.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.toolkit.webdriver.customcontrols.AdvancedTable;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class GeneralTab extends Tab {

	public static AdvancedTable tableInsuredList = new AdvancedTable(By.id("policyDataGatherForm:dataGatherView_ListInsured"));

    public GeneralTab() {
        super(AutoCaMetaData.GeneralTab.class);
    }

    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }

	public AssetList getNamedInsuredInfoAssetList() {
    	return getAssetList().getAsset(AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel(), AssetList.class);
	}
    public AssetList getValidateAddressDialogAssetList() {
    	return getAssetList().getAsset(AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel(), AssetList.class)
    			.getAsset(AutoCaMetaData.GeneralTab.NamedInsuredInformation.VALIDATE_ADDRESS_DIALOG.getLabel(), AssetList.class);
	}
    public AssetList getAAAProductOwnedAssetList() {
    	return getAssetList().getAsset(AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AssetList.class);
	}
    public AssetList getCurrentCarrierInfoAssetList() {
    	return getAssetList().getAsset(AutoCaMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(), AssetList.class);
	}
    public AssetList getPolicyInfoAssetList() {
    	return getAssetList().getAsset(AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AssetList.class);
	}
    public AssetList getThirdPartyDesigneeInfoAssetList() {
    	return getAssetList().getAsset(AutoCaMetaData.GeneralTab.THIRD_PARTY_DESIGNEE_INFORMATION.getLabel(), AssetList.class);
	}

	public AssetList getContactInfoAssetList() {
		return getAssetList().getAsset(AutoCaMetaData.GeneralTab.CONTACT_INFORMATION.getLabel(), AssetList.class);
	}

	/*
	public void removeInsured(int index) {
		if (tblInsuredList.isPresent() && tblInsuredList.getRow(index).isPresent()) {
			tblInsuredList.getRow(index).getCell(4).controls.links.get("Remove").click(Waiters.AJAX);
			Page.dialogConfirmation.confirm();
		}
	}
	*/
	public void viewInsured(int index) {
		if (tableInsuredList.isPresent() && tableInsuredList.getRow(index).isPresent()) {
			tableInsuredList.getRow(index).getCell(4).controls.links.get("View/Edit").click(Waiters.AJAX);
		}
	}
}
