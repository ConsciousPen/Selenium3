/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.CustomerMetaData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;

public class EmployeeInfoTab extends ActionTab {
    public static StaticElement labelEmployeeInformationExpanded = new StaticElement(By.id("crmForm:participantHeaderContentExpandedEmployeeInfo_0"));
    public static StaticElement labelEmployeeInformationCollapsed = new StaticElement(By.id("crmForm:participantHeaderContentCollapsedEmployeeInfo_0"));
    public static Button buttonRemoveRelationshipToSponsor = new Button(By.xpath("//a[text() = 'Remove Relationship to Sponsor']"));

    public EmployeeInfoTab() {
        super(CustomerMetaData.EmployeeInfoTab.class);
    }

    @Override
    public Tab submitTab() {
        buttonDone.click();
        return this;
    }
}
