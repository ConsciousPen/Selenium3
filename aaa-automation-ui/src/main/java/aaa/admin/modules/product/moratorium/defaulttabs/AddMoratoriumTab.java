/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.product.moratorium.defaulttabs;

import static aaa.admin.metadata.product.MoratoriumMetaData.AddMoratoriumTab.ADD_RULE_SECTION;
import static aaa.admin.metadata.product.MoratoriumMetaData.AddMoratoriumTab.AddRuleSection.ADD_ADDITIONAL_EXPOSURE_CAP_SECTION;
import org.openqa.selenium.By;
import aaa.admin.metadata.product.MoratoriumMetaData;
import aaa.common.Tab;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.table.Table;

public class AddMoratoriumTab extends Tab {
	public static Button buttonAddRule = new Button(By.id("moratoriumRegistryForm:addRuleBtn_footer"));
	public static Button buttonAddAdditionalExposureCap = new Button(By.id("moratoriumRegistryForm:exposureCapAddBtn"));
	public static Button buttonSaveRule = new Button(By.id("moratoriumRegistryForm:okBtnRule"));
	public static Table tableCatastropheDetails = new Table(By.id("moratoriumRegistryForm:body_catastropheTable"));

	public static Link linkDelete = new Link(By.xpath("//a[.='Delete']"));

	public AddMoratoriumTab() {
		super(MoratoriumMetaData.AddMoratoriumTab.class);
	}

	@Override
	public Tab fillTab(TestData td) {
		if (td.containsKey(AddMoratoriumTab.class.getSimpleName()) && td.getTestData(AddMoratoriumTab.class.getSimpleName()).getKeys().contains(ADD_RULE_SECTION.getLabel())) {
			if (td.getTestData(AddMoratoriumTab.class.getSimpleName(), MoratoriumMetaData.AddMoratoriumTab.AddRuleSection.class.getSimpleName()).getKeys().contains("Delete Rule")) {
				linkDelete.click();
			}
			buttonAddRule.click();
			if (td.getTestData(AddMoratoriumTab.class.getSimpleName()).containsKey(MoratoriumMetaData.AddMoratoriumTab.AddRuleSection.class.getSimpleName()) && td.getTestData(AddMoratoriumTab.class.getSimpleName(), MoratoriumMetaData.AddMoratoriumTab.AddRuleSection.class.getSimpleName()).getKeys().contains(ADD_ADDITIONAL_EXPOSURE_CAP_SECTION.getLabel())) {
				buttonAddAdditionalExposureCap.click();
			}
			assetList.fill(td);
			buttonSaveRule.click();
			return this;
		}
		assetList.fill(td);
		return this;
	}
}
