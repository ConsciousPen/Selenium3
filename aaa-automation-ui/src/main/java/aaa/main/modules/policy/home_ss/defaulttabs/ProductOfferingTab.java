/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ss.defaulttabs;

import aaa.toolkit.webdriver.customcontrols.ProductOfferingVariationControl;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.openqa.selenium.By;

import aaa.common.Tab;
import aaa.main.metadata.policy.HomeSSMetaData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

import static aaa.toolkit.webdriver.customcontrols.ProductOfferingVariationControl.BASE_PREMIUM;
import static aaa.toolkit.webdriver.customcontrols.ProductOfferingVariationControl.SUBTOTAL;
import static aaa.toolkit.webdriver.customcontrols.ProductOfferingVariationControl.TOTAL_PREMIUM;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class ProductOfferingTab extends Tab {
    public ProductOfferingTab() {
        super(HomeSSMetaData.ProductOfferingTab.class);
    }
    public Button addEndorsement = new Button(By.id("policyDataGatherForm:addEndorsements_footer"), Waiters.AJAX);
    public Button btnCalculatePremium = new Button(By.id("policyDataGatherForm:premiumRecalcCov_footer"));
    public Button btnAddAdditionalVariation = new Button(By.id("policyDataGatherForm:addAdditionalVariation"));
    public Table tableEndorsement = new Table(By.id("policyDataGatherForm:panel"));
    public Table tableHeritage = new Table(By.id("QuoteVariation_sections"));
    public Table tableLegacy = new Table(By.id("QuoteVariation2_sections"));
    public Table tablePrestige = new Table(By.id("QuoteVariation3_sections"));


    @Override
    public Tab submitTab() {
        if (addEndorsement.isPresent() && addEndorsement.isVisible()) {
            addEndorsement.click();
        }
        return this;
    }

    public void calculatePremiums(){
        btnCalculatePremium.click();
    }

    public static Dollar getTotalPremium(AssetDescriptor<ProductOfferingVariationControl> sectionAssetDescriptor){
        return new Dollar (new ProductOfferingTab().getAssetList().getAsset(sectionAssetDescriptor).getAsset(TOTAL_PREMIUM).getValue());
    }

    public static Dollar getBasePremium(AssetDescriptor<ProductOfferingVariationControl> sectionAssetDescriptor){
        return new Dollar (new ProductOfferingTab().getAssetList().getAsset(sectionAssetDescriptor).getAsset(BASE_PREMIUM).getValue());
    }

    public static Dollar getSubTotalPremium(AssetDescriptor<ProductOfferingVariationControl> sectionAssetDescriptor){
        return new Dollar (new ProductOfferingTab().getAssetList().getAsset(sectionAssetDescriptor).getAsset(SUBTOTAL).getValue());
    }

    public static Boolean isTotalPremiumCalculatedProperly(AssetDescriptor<ProductOfferingVariationControl> sectionAssetDescriptor){
        return new Boolean(getBasePremium(sectionAssetDescriptor).add(getSubTotalPremium(sectionAssetDescriptor))
                .equals(getTotalPremium(sectionAssetDescriptor)));
    }
}