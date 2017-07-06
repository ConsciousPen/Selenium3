/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.mywork.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.MyWorkMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class AssignTaskToActionTab extends ActionTab {
    public static Button buttonSearch = new Button(By.id("taskAssignForm:searchButton"));
    public static Table tableSearchResults = new Table(By.xpath("//div[@id='taskAssignForm:userSearchResults']//table[.//tbody[@id='taskAssignForm:userSearchResults_data']]"));

    public static Button buttonAssign = new Button(By.id("taskAssignForm:submitAssign"));
    public static Button buttonCancel = new Button(By.id("taskAssignForm:cancelAssign"));

    public AssignTaskToActionTab() {
        super(MyWorkMetaData.AssignTaskToActionTab.class);
    }

    @Override
    public Tab fillTab(TestData td) {
        if (td.containsKey(getMetaKey()) && !td.getTestData(getMetaKey()).getKeys().isEmpty()) {

            TestData tdTemp = td.getTestData(getMetaKey());
            String keyQueue = MyWorkMetaData.AssignTaskToActionTab.QUEUE.getLabel();
            String keyStayInQueue = MyWorkMetaData.AssignTaskToActionTab.STAY_IN_ORIGINAL_QUEUE.getLabel();
            if (tdTemp.containsKey(keyQueue)) {

                getAssetList().getControl(keyStayInQueue, CheckBox.class).setValue(false);
                Link select = new Link(By.xpath("//div[@id='taskAssignForm:availableQueues']/div[contains(@class, 'ui-selectonemenu-trigger')]/span"));
                select.click();

                String temp = tdTemp.getValue(keyQueue);
                if (temp.equals("")) {
                    temp = "&nbsp;";
                }

                Link lnkToSelect = new Link(By.xpath("//div[@id='taskAssignForm:availableQueues_panel']//li[@data-label='" + temp + "']"));
                lnkToSelect.click();

                tdTemp.mask(keyStayInQueue, keyQueue);
                ((AssetList) getAssetList()).setValue(tdTemp);

                if (temp.equals("&nbsp;")) {
                    buttonSearch.click();
                    new Link(tableSearchResults, By.xpath(".//tr[1]/td[1]")).click();
                }
            } else {
                ((AssetList) getAssetList()).setValue(tdTemp);
                buttonSearch.click();
                new Link(tableSearchResults, By.xpath(".//tr[1]/td[1]")).click();
            }
        }
        return this;
    }
}
