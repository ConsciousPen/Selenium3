/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.agencyvendor.agency.defaulttabs;

import org.openqa.selenium.By;

import aaa.admin.metadata.agencyvendor.AgencyMetaData;
import aaa.common.DefaultTab;
import toolkit.webdriver.controls.composite.table.Table;

public class ContactInfoTab extends DefaultTab {

    public static Table tableContacts = new Table(By.id("brokerAgencyInfoForm:personalContactsTable"));

    public ContactInfoTab() {
        super(AgencyMetaData.ContactInfoTab.class);
    }
}
