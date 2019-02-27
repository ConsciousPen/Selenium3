/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ca.defaulttabs;

import aaa.common.Tab;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import aaa.toolkit.webdriver.customcontrols.dialog.AddressValidationDialog;
import org.openqa.selenium.By;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AssetList;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class MortgageesTab extends Tab {
	public static StaticElement mortgageeClauseHelpText = new StaticElement(By.id("policyDataGatherForm:sedit_AAAHOMortgageeInfo_legalMortgageeName_helpText"));

    public MortgageesTab() {
        super(HomeCaMetaData.MortgageesTab.class);
    }

    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }

	public MultiInstanceAfterAssetList getMortgageeInfoAssetList() {
		return getAssetList().getAsset(HomeCaMetaData.MortgageesTab.MORTGAGEE_INFORMATION.getLabel(), MultiInstanceAfterAssetList.class);
	}
    public AssetList getLegalPropetyAddressAssetList() {
    	return getAssetList().getAsset(HomeCaMetaData.MortgageesTab.LEGAL_PROPERTY_ADDRESS.getLabel(), AssetList.class);
	}
    public AssetList getAdditionalInsuredAssetList() {
    	return getAssetList().getAsset(HomeCaMetaData.MortgageesTab.ADDITIONAL_INSURED.getLabel(), AssetList.class);
	}
    public AssetList getAdditionalInterestAssetList() {
    	return getAssetList().getAsset(HomeCaMetaData.MortgageesTab.ADDITIONAL_INTEREST.getLabel(), AssetList.class);
	}
    public AssetList getThirdPartyDesigneeAssetList() {
    	return getAssetList().getAsset(HomeCaMetaData.MortgageesTab.THIRD_PARTY_DESIGNEE.getLabel(), AssetList.class);
	}
	public AddressValidationDialog getValidateAddressDialogAssetList() {
		return getAssetList().getAsset(HomeCaMetaData.MortgageesTab.MORTGAGEE_INFORMATION).getAsset(HomeSSMetaData.MortgageesTab.MortgageeInformation.VALIDATE_ADDRESS_DIALOG);
	}

}
