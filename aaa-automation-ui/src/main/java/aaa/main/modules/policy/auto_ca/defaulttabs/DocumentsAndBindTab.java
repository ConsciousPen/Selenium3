/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ca.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.common.components.Dialog;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Implementation of a specific tab in a workspace. Tab classes from the default
 * workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB
 * LABEL>ActionTab (to prevent duplication). Modify this class if tab filling
 * procedure has to be customized, extra asset list to be added, custom testdata
 * key to be defined, etc.
 *
 * @category Generated
 */
public class DocumentsAndBindTab extends Tab {
	public DocumentsAndBindTab() {
		super(AutoCaMetaData.DocumentsAndBindTab.class);
	}

	public static Button btnPurchase = new Button(By.xpath(".//input[contains(@id, 'policyDataGatherForm:moveToBilling')]"), Waiters.AJAX);
	public static Dialog confirmPurchase = new Dialog("//div[@id='policyDataGatherForm:confirmPurchaseDialog_container']");
	public static Dialog confirmEndorsementPurchase = new Dialog("//div[@id='policyDataGatherForm:ConfirmDialogA_container']");

	public AssetList getDocumentsForPrintingAssetList() {
		return getAssetList().getAsset(AutoCaMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING.getLabel(), AssetList.class);
	}

	public AssetList getRequiredToBindAssetList() {
		return getAssetList().getAsset(AutoCaMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(), AssetList.class);
	}

	public AssetList getRequiredToIssueAssetList() {
		return getAssetList().getAsset(AutoCaMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel(), AssetList.class);
	}

	public AssetList getVehicleInformationAssetList() {
		return getAssetList().getAsset(AutoCaMetaData.DocumentsAndBindTab.VEHICLE_INFORMATION.getLabel(), AssetList.class);
	}

	@Override
	public Tab submitTab() {
		btnPurchase.click();
		ErrorTab errorTab = new ErrorTab();
		if (errorTab.isVisible() && errorTab.getErrorCodesList().contains(ErrorEnum.Errors.ERROR_AAA_AUTO_CA_MEM_LASTNAME.getMessage())) {
			errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_AUTO_CA_MEM_LASTNAME);
			errorTab.override();
			btnPurchase.click();
		}
		confirmPurchase();
		return this;
	}

	protected void confirmPurchase() {
		if (confirmPurchase.isPresent() && confirmPurchase.isVisible()) {
			confirmPurchase.confirm();
		} else if (confirmEndorsementPurchase.isPresent() && confirmEndorsementPurchase.isVisible()) {
			confirmEndorsementPurchase.confirm();
		}
	}
}
