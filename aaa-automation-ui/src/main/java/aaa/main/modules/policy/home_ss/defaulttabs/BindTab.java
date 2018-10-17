/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ss.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.common.components.Dialog;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.AssetList;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class BindTab extends Tab {
	public Button btnPurchase = new Button(
			By.xpath("//input[@id='policyDataGatherForm:overridenActionButton_PurchaseAction_footer' or @id='policyDataGatherForm:actionButton_moveToBilling_RenewalSave_footer' or @id='policyDataGatherForm:actionButton_moveToBilling_EndorsementPurchase_footer' or @id='policyDataGatherForm:actionButton_moveToBilling_RenewalPropose_footer' or @id='policyDataGatherForm:actionButton_moveToBilling_EndorsementBind_footer' or @id='policyDataGatherForm:actionButton_PurchaseAction_footer']"));
	public Dialog confirmPurchase = new Dialog("//div[@id='policyDataGatherForm:confirmPurchaseDialog_container']");
	public Dialog confirmEndorsementPurchase = new Dialog("//div[@id='policyDataGatherForm:ConfirmDialogA_container']");
	public Dialog confirmRenewPurchase = new Dialog("//div[@id='policyDataGatherForm:ConfirmDialog-1_container']");

	public BindTab() {
		super(HomeSSMetaData.BindTab.class);
	}

	public AssetList getPaperlessPreferencesAssetList() {
		return getAssetList().getAsset(HomeSSMetaData.BindTab.PAPERLESS_PREFERENCES.getLabel(), AssetList.class);
	}

	public AssetList getDocumentPrintingDetailsAssetList() {
		return getAssetList().getAsset(HomeSSMetaData.BindTab.DOCUMENT_PRINTING_DETAILS.getLabel(), AssetList.class);
	}

	@Override
	public Tab submitTab() {
		return submitTab(false);
	}

	public Tab submitTab(boolean errorExpected) {
		btnPurchase.click();
		confirmPurchase();
		ErrorTab errorTab = new ErrorTab();
		if (!errorExpected && errorTab.isVisible() && errorTab.getErrorCodesList().contains(ErrorEnum.Errors.ERROR_AAA_HO_SS_MEM_LASTNAME.getCode())) {
			errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_HO_SS_MEM_LASTNAME);
			errorTab.override();
			btnPurchase.click();
			confirmPurchase();
		}
		return this;
	}

	public void confirmPurchase() {
		if (confirmEndorsementPurchase.isPresent() && confirmEndorsementPurchase.isVisible()) {
			confirmEndorsementPurchase.confirm();
		} else if (confirmRenewPurchase.isPresent() && confirmRenewPurchase.isVisible()) {
			confirmRenewPurchase.confirm();
		} else if (confirmPurchase.isPresent() && confirmPurchase.isVisible()) {
			confirmPurchase.confirm();
		}
	}
}
