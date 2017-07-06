/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.CustomerConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Contact Details "WebUrl" Add Remove
 * @scenario
 * 1. Create Non Individual Customer
 * 2. Navigate to Contacts Relationship tab
 * 3. Add new Contact details "WebUrl"
 * 4. Navigate to Contacts Relationship tab
 * 5. Verify that contact details is present in Customer Contact table
 * 6. Remove Contact details
 * 7. Navigate to Contacts Relationship tab
 * 8. Verify that contact details is not present in Customer Contact table
 * @details
 */
public class TestCustomerContactDetailsWebUrlAddRemove extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerContactDetailsWebUrlAddRemove() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Add New Web url for Customer # " + customerNumber);
        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        customer.addNewContactsDetails().perform(tdCustomerNonIndividual.getTestData("ContactsDetails", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("ContactsDetails", "Adjustment_NewWebUrl")));

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        CustomerSummaryPage.tableCustomerContacts.getRow(2).getCell(CustomerConstants.CustomerContactsTable.CONTACT_TYPE).verify.value(tdCustomerNonIndividual.getTestData(
                "ContactsDetails", "Adjustment_NewWebUrl", CustomerMetaData.GeneralTab.class.getSimpleName()).getValue(
                CustomerMetaData.GeneralTab.WEB_URL_TYPE.getLabel()));

        log.info("TEST: Remove Web url for Customer # " + customerNumber);
        customer.removeNewContactsDetails().perform(2);
        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        CustomAssert.assertFalse(CustomerSummaryPage.tableCustomerContacts.getColumn(4).getValue().contains(tdCustomerNonIndividual.getTestData(
                "ContactsDetails", "Adjustment_NewWebUrl", CustomerMetaData.GeneralTab.class.getSimpleName()).getValue(
                CustomerMetaData.GeneralTab.WEB_URL_TYPE.getLabel())));
    }
}
