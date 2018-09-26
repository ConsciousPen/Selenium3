/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.common.components.Dialog;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AssetList;

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
		super(AutoSSMetaData.DocumentsAndBindTab.class);
	}

	public static Button btnGenerateDocuments = new Button(By.id("policyDataGatherForm:generate_link"));
	public static Button btnPurchase = new Button(By.xpath(".//input[@id='policyDataGatherForm:overridenActionButton_PurchaseAction_footer' or @id='policyDataGatherForm:actionButton_PurchaseAction_footer']"));
	public static Dialog confirmPurchase = new Dialog("//div[@id='policyDataGatherForm:confirmPurchaseDialog_container']");
	public static Dialog confirmEndorsementPurchase = new Dialog("//div[@id='policyDataGatherForm:ConfirmDialogA_container']");
	public static Dialog confirmRenewal = new Dialog("//div[@id='policyDataGatherForm:ConfirmDialog-1_content']");
	public static StaticElement helpIconPaperlessPreferences = new StaticElement(By.xpath("//label[@id='policyDataGatherForm:paperlessPreferences_enrolledInPaperless_helpText']"));

	public AssetList getDocumentsForPrintingAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING.getLabel(), AssetList.class);
	}

	public AssetList getRequiredToBindAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(), AssetList.class);
	}

	public AssetList getRequiredToIssueAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel(), AssetList.class);
	}

	public AssetList getEnterRecipientEmailAddressDialogAssetList() {
		return getDocumentsForPrintingAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.ENTER_RECIPIENT_EMAIL_ADDRESS_DIALOG.getLabel(), AssetList.class);
	}

	@Override
	public Tab submitTab() {
		return submitTab(false);
	}

	public Tab submitTab(boolean errorExpected) {
		btnPurchase.click();
		confirmPurchase();
		ErrorTab errorTab = new ErrorTab();
		if (!errorExpected && errorTab.isVisible() && errorTab.getErrorCodesList().contains(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME.getCode())) {
			errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME);
			errorTab.override();
			btnPurchase.click();
			confirmPurchase();
		}
		return this;
	}

	protected void confirmPurchase() {
		if (confirmPurchase.isPresent() && confirmPurchase.isVisible()) {
			confirmPurchase.confirm();
		} else if (confirmEndorsementPurchase.isPresent() && confirmEndorsementPurchase.isVisible()) {
			confirmEndorsementPurchase.confirm();
		} else if (confirmRenewal.isPresent() && confirmRenewal.isVisible()) {
			confirmRenewal.confirm();
		}
	}

	public AssetList getPaperlessPreferencesAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PAPERLESS_PREFERENCES.getLabel(), AssetList.class);
	}

	public AssetList getDocumentPrintingDetailsAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENT_PRINTING_DETAILS.getLabel(), AssetList.class);
	}

	public AssetList getGeneralInformationAssetList() {
		return getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION.getLabel(), AssetList.class);
	}
}
