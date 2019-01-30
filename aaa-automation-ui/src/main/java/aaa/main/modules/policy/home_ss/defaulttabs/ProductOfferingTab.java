/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ss.defaulttabs;

import static aaa.toolkit.webdriver.customcontrols.ProductOfferingVariationControl.*;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.Tab;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.toolkit.webdriver.customcontrols.ProductOfferingVariationControl;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 *
 * @category Generated
 */
public class ProductOfferingTab extends Tab {
	public ProductOfferingTab() {
		super(HomeSSMetaData.ProductOfferingTab.class);
	}

	public Button addEndorsement = new Button(By.id("policyDataGatherForm:addEndorsements_footer"), Waiters.AJAX);
	//public Button btnCalculatePremium = new Button(By.id("policyDataGatherForm:actionButton_AAAHOComparisonButtonsComponent_footer"));
	public Button btnCalculatePremium = new Button(By.xpath("//input[@id='policyDataGatherForm:actionButton_AAAHOComparisonButtonsComponent_footer'or @id='policyDataGatherForm:calculatePremium_AAAHOComparisonButtonsComponent_footer']"));
	public Button btnAddAdditionalVariation = new Button(By.id("policyDataGatherForm:addAdditionalVariation"));
	public Table tableEndorsement = new Table(By.id("policyDataGatherForm:panel")).applyConfiguration("IncludedEndorsementsTable"); //"rowThatTemplate": "//div/table/tbody/tr[%s+1]",
	public Table tableHeritage = new Table(By.id("QuoteVariation_sections"));
	public Table tableLegacy = new Table(By.id("QuoteVariation2_sections"));
	public Table tablePrestige = new Table(By.id("QuoteVariation3_sections"));

	public Button buttonSelectHeritage = new Button(By.id("policyDataGatherForm:QuoteVariation_ActivateBtn"));
	public Button buttonSelectLegacy = new Button(By.id("policyDataGatherForm:QuoteVariation2_ActivateBtn"));
	public Button buttonSelectPrestige = new Button(By.id("policyDataGatherForm:QuoteVariation3_ActivateBtn"));

	@Override
	public Tab submitTab() {
		if (addEndorsement.isPresent() && addEndorsement.isVisible()) {
			addEndorsement.click();
		}
		return this;
	}

	public void calculatePremium() {
		btnCalculatePremium.click();
	}

	public static Dollar getTotalPremium(AssetDescriptor<ProductOfferingVariationControl> sectionAssetDescriptor) {
		return new Dollar(new ProductOfferingTab().getAssetList().getAsset(sectionAssetDescriptor).getAsset(TOTAL_PREMIUM).getValue());
	}

	public static Dollar getBasePremium(AssetDescriptor<ProductOfferingVariationControl> sectionAssetDescriptor) {
		return new Dollar(new ProductOfferingTab().getAssetList().getAsset(sectionAssetDescriptor).getAsset(BASE_PREMIUM).getValue());
	}

	public static Dollar getSubTotalPremium(AssetDescriptor<ProductOfferingVariationControl> sectionAssetDescriptor) {
		return new Dollar(new ProductOfferingTab().getAssetList().getAsset(sectionAssetDescriptor).getAsset(SUBTOTAL).getValue());
	}

	public static boolean isTotalPremiumCalculatedProperly(AssetDescriptor<ProductOfferingVariationControl> sectionAssetDescriptor) {
		return getBasePremium(sectionAssetDescriptor).add(getSubTotalPremium(sectionAssetDescriptor))
				.equals(getTotalPremium(sectionAssetDescriptor));
	}

	public static List<String> getIncludedEndorsementList(AssetDescriptor<ProductOfferingVariationControl> bundleName) {
		ProductOfferingTab poTab = new ProductOfferingTab();
		List<String> formIdList = new ArrayList<>();
		for (int i = 1; i <= poTab.tableEndorsement.getRowsCount(); i++) {
			Row row = poTab.tableEndorsement.getRow(i);
			if (row.getCell("Plan: " + bundleName.getLabel()).getValue().equals("Included") && !row.getCell("Endorsement").getValue().contains("only)")) {
				formIdList.add(row.getCell(1).getValue().split(" - ")[0]);
			}
		}
		return formIdList;
	}
}
