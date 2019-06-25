/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ca.defaulttabs;

import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.toolkit.webdriver.customcontrols.AdvancedTable;
import aaa.toolkit.webdriver.customcontrols.FillableTable;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import aaa.toolkit.webdriver.customcontrols.NoSectionsMultiAssetList;
import aaa.toolkit.webdriver.customcontrols.dialog.AddressValidationDialog;
import aaa.toolkit.webdriver.customcontrols.dialog.SelectSearchDialog;
import org.openqa.selenium.By;
import toolkit.webdriver.controls.composite.assets.AssetList;
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
		super(AutoCaMetaData.GeneralTab.class);
	}

	public MultiInstanceAfterAssetList getNamedInsuredInfoAssetList() {
		return getAssetList().getAsset(AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel(), MultiInstanceAfterAssetList.class);
	}

	public AddressValidationDialog getValidateAddressDialogAssetList() {
		return getAssetList().getAsset(AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION).getAsset(AutoCaMetaData.GeneralTab.NamedInsuredInformation.VALIDATE_ADDRESS_DIALOG);
	}

	public AssetList getListOfProductsRowsAssetList() {
		return getAssetList().getAsset(AutoCaMetaData.GeneralTab.LIST_OF_PRODUCT_ROWS.getLabel(), AutoCaMetaData.GeneralTab.LIST_OF_PRODUCT_ROWS.getControlClass());
	}

	public AssetList getAAAProductOwnedAssetList() {
		return getAssetList().getAsset(AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AssetList.class);
	}

	public Table getOtherAAAProductTable() {
		return getOtherAAAProductOwnedAssetList().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.LIST_OF_PRODUCTS_OWNED.getLabel(), FillableTable.class).getTable();
	}

	public SelectSearchDialog getSearchOtherAAAProducts() {
		return getOtherAAAProductOwnedAssetList().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_OTHER_AAA_PRODUCTS.getLabel(), SelectSearchDialog.class);
	}

	public AssetList getAAAMembershipAssetList() {
		return getAssetList().getAsset(AutoCaMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel(), AssetList.class);
	}


	public NoSectionsMultiAssetList getOtherAAAProductOwnedAssetList() {
		return getAssetList().getAsset(AutoCaMetaData.GeneralTab.OTHER_AAA_PRODUCTS_OWNED.getLabel(), NoSectionsMultiAssetList .class);
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

	@Override
	public Tab submitTab() {
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
