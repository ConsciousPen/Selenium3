/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.main.metadata.CustomerMetaData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.table.Table;

public class CommunicationActionTab extends ActionTab {
    public static Table tableCommunications = new Table(By.id("communicationsSearchForm:searchTable")).applyConfiguration("Communication");
    public static Table tableCommunicationInfoDetails = new Table(By.id("communicationsSearchForm:searchTable:0:details:0:communicationInfoDetails"));
    public static Button buttonStartNewCommunication = new Button(By.xpath("//a[.='Start New Communication']"));

    public CommunicationActionTab() {
        super(CustomerMetaData.CommunicationActionTab.class);
    }
}
