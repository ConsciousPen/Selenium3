/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.common.components;

import java.util.Map;

import org.openqa.selenium.By;

import aaa.common.enums.DialogSearchEnum.SearchBy;
import aaa.common.metadata.SearchMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.AbstractEditableStringElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.collection.Controls;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class DialogSearch {

    public static final String DIALOG_SEARCH = "DialogSearch";

    public AssetList assetListDialogSearch;

    private String locator;

    public Button buttonSearch;
    public Button buttonClear;
    public Button buttonSelect;
    public Button buttonCancel;

    public StaticElement labelHeader;
    public StaticElement labelMessage;

    public Table tableSearchResults;

    public Controls controls;

    public DialogSearch(String dialogLocator) {
        locator = dialogLocator;
        controls = new Controls(By.xpath(dialogLocator));

        assetListDialogSearch = new AssetList(By.xpath(dialogLocator), SearchMetaData.DialogSearch.class);

        buttonSearch = new Button(By.xpath(dialogLocator + "//input[@value = 'Search']"));
        buttonClear = new Button(By.xpath(dialogLocator + "//input[@value = 'Clear']"));
        buttonSelect = new Button(By.xpath(dialogLocator + "//input[@value = 'Select']"));
        buttonCancel = new Button(By.xpath(dialogLocator + "//input[@value = 'Cancel']"));

        labelHeader = new StaticElement(By.xpath(dialogLocator + "//div[contains(@id, '_header_content')]"));
        labelMessage = new StaticElement(By.xpath(dialogLocator + "//div[contains(@id, '_content_scroller')]//span[contains(@id, 'Message') or @class='textBold']"));

        tableSearchResults = new Table(By.xpath(dialogLocator + "//table[@id='policySearch:policySearchResults' or @id='adjusterSearchForm:results_internal' "
                + "or @id='brokerSearchFromcrmCustomerBrokerCd:body_brokerSearchResultscrmCustomerBrokerCd' or @id='brokerSearchFromtarget:body_brokerSearchResultstarget' "
                + "or @id='brokerSearchFromProducerInfo:body_brokerSearchResultsProducerInfo' or @id='assignGroupInfoForm:searchResultTable' "
                + "or @id='selectGroupForm:body_groupSearchTable' or @id='selectAgenciesForm:body_resultAgenciesTable']"));
    }

    public void search(SearchBy searchBy, String searchString) {
        for (String key : assetListDialogSearch.getAssetNames()) {
            if (key.contains(searchBy.get())) {
                ((AbstractEditableStringElement) assetListDialogSearch.getControl(key)).setValue(searchString);
                break;
            }
        }

        buttonSearch.click();
    }

    public void search(Map<String, String> searchCriteria) {
        for (String key : assetListDialogSearch.getAssetNames()) {
            if (searchCriteria.containsKey(key)) {
                ((AbstractEditableStringElement) assetListDialogSearch.getControl(key)).setValue(searchCriteria.get(key));
            }
        }
        buttonSearch.click();
    }

    public void search(TestData dt) {
        assetListDialogSearch.setValue(dt);
        buttonSearch.click();
    }

    public boolean isPresent() {
        return new StaticElement(By.xpath(locator)).isPresent();
    }

    public boolean isVisible() {
        return new StaticElement(By.xpath(locator)).isVisible();
    }
}
