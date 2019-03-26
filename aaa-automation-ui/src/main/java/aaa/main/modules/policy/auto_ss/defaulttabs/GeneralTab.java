/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss.defaulttabs;

import aaa.common.components.OtherAAAProductsSearchTableElement;
import aaa.common.components.OtherAAAProductsTableElement;
import aaa.toolkit.webdriver.customcontrols.NoSectionsMultiAssetList;
import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.toolkit.webdriver.customcontrols.FillableTable;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import aaa.toolkit.webdriver.customcontrols.dialog.AddressValidationDialog;
import aaa.toolkit.webdriver.customcontrols.dialog.SelectSearchDialog;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel(), MultiInstanceAfterAssetList.class);
	}

	public AddressValidationDialog getValidateAddressDialogAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.VALIDATE_ADDRESS_DIALOG);
	}

	public AssetList getAAAMembershipAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel(), AssetList.class);
	}

	public AssetList getOtherAAAProductOwnedAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.OTHER_AAA_PRODUCTS_OWNED_ASSET_LIST.getLabel(), AssetList.class);
	}

	public Table getOtherAAAProductTable() {
		return getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIST_OF_PRODUCTS_OWNED.getLabel(), FillableTable.class).getTable();
	}

	public AssetList getListOfProductsRowsAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.LIST_OF_PRODUCT_ROWS.getLabel(), AutoSSMetaData.GeneralTab.LIST_OF_PRODUCT_ROWS.getControlClass());
	}

	public Table getManualSearchResultTable() {
		return getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.SearchOtherAAAProducts.RESULT_TABLE.getLabel(), FillableTable.class).getTable();
	}

	public SelectSearchDialog getSearchOtherAAAProducts() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.SEARCH_OTHER_AAA_PRODUCTS.getLabel(), SelectSearchDialog.class);
	}

	public AssetList getSearchOtherAAAProductsAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.GeneralTab.SEARCH_OTHER_AAA_PRODUCTS.getLabel(), AssetList.class);
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

	public InquiryAssetList getPolicyInfoInquiryAssetList() {
		return getInquiryAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), InquiryAssetList.class);
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
