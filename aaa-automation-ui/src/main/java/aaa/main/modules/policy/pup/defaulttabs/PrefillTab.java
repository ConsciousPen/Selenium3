/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.policy.pup.defaulttabs;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;

import aaa.common.Tab;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.toolkit.webdriver.customcontrols.dialog.SingleSelectSearchDialog;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;
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
public class PrefillTab extends Tab {
	public PrefillTab() {
		super(PersonalUmbrellaMetaData.PrefillTab.class);
	}

	public StaticElement lblAdditionalPolicyWarningMessage = new StaticElement(By.xpath("//span[@id='policyDataGatherForm:prerateWarningMessage']"));
	public Table tblInsuredsList = new Table(By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListAAAInsured']//table"));
	public Table tblActivePoliciesList = new Table(By.xpath("//table[@id='policyDataGatherForm:pupPolicyPrefillTable']"));
	public Button buttonAddPolicy = new Button(By.id("policyDataGatherForm:addPupPrefill"));
	public Button buttonRemovePolicy = new Button(By.id("policyDataGatherForm:removeManualPrefill"));
	public SingleSelectSearchDialog searchDialog = new SingleSelectSearchDialog(By.xpath(".//div[@id='pupPolicySearchPopup_container']"), PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.ActiveUnderlyingPoliciesSearch.class);
	
	@Override
	public Tab submitTab() {
		buttonNext.click();
		return this;
	}

	/*
	 * @Override public Tab fillTab(TestData td) { assetList.fill(td);
	 * 
	 * return this; }
	 */

	/*public Table tblActivePoliciesList() {
		return assetList.getAsset(PersonalUmbrellaMetaData.PrefillTab.ACTIVE_UNDERLYING_POLICIES.getLabel(), AdditionalPoliciesMultiAssetList.class).getAsset(PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.POLICIES_LIST_TABLE.getLabel(), Table.class);
	}*/

	public Row getNamedInsuredRow(int rowNum) {
		return tblInsuredsList.getRow(rowNum);
	}

	public Link getNamedInsuredListChangeLink(int row) {
		return getNamedInsuredRow(row).getCell(tblInsuredsList.getColumnsCount()).controls.links.get("View Details", Waiters.AJAX);
	}

	public Row getAdditionalPoliciesRow(int rowNum) {
		return tblActivePoliciesList.getRow(rowNum);
	}

	public Link getAdditionalPoliciesRemoveLink(int rowNum) {
		return getAdditionalPoliciesRow(rowNum).getCell(8).controls.links.get("Remove", Waiters.AJAX);
	}

	public Link getAdditionalPoliciesPrefillLink(int rowNum) {
		return getAdditionalPoliciesRow(rowNum).getCell(8).controls.links.get("Order Prefill", Waiters.AJAX);
	}

	public Link getAdditionalPoliciesChangeLink(int rowNum) {
		return getAdditionalPoliciesRow(rowNum).getCell(8).controls.links.get("View/Edit", Waiters.AJAX);
	}
	
	public TestData adjustWithRealPolicies(TestData td, Map<String, String> policies){
		String pathToList = TestData.makeKeyPath(getMetaKey(), PersonalUmbrellaMetaData.PrefillTab.ACTIVE_UNDERLYING_POLICIES.getLabel());
		String pathToValue = PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel();
		String modifiedValueKey = PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.ActiveUnderlyingPoliciesSearch.POLICY_NUMBER.getLabel();
		List<TestData> tdl = td.getTestData(getMetaKey()).getTestDataList(PersonalUmbrellaMetaData.PrefillTab.ACTIVE_UNDERLYING_POLICIES.getLabel());
		for(TestData tempTD : tdl){
				String key = tempTD.getTestData(pathToValue).getValue(modifiedValueKey);
			if(policies.containsKey(key)) {
				tempTD.adjust(TestData.makeKeyPath(pathToValue, modifiedValueKey), policies.get(key));
			}
		}
		td.adjust(pathToList, tdl);
		return td;
	}

}
