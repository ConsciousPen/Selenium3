/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.customer.defaulttabs.GeneralTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Create New Contacts Version
 * @scenario
 * 1. Create Non Individual Customer with address
 * 2. [Take Action]=Inquiry
 * 3. Verify no [Timeline] links/buttons are NOT displayed in customer profile
 * 4. Update any customer data
 * 5. Save Updates
 * 6. [Take Action]=Inquiry
 * 7. Verify [Timeline] links/buttons are displayed for those sections/table entities that have been changed
 * @details
 */
public class TestCustomerCreateNewContactsVersion extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerCreateNewContactsVersion() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("STEP: Inquiry Customer # " + customerNumber);
        customer.inquiry().perform();

        CustomerSummaryPage.buttonTimeLine.verify.present(false);

        GeneralTab.buttonTopCancel.click();

        log.info("TEST: Update Contacts Details for Customer and created new version # " + customerNumber);
        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        customer.updateContactsDetails().perform(tdCustomerNonIndividual.getTestData("ContactsDetails", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("ContactsDetails", "Adjustment_UpdateAddressType")), 1);

        log.info("STEP: Inquiry Customer # " + customerNumber);
        customer.inquiry().perform();

        CustomerSummaryPage.buttonTimeLine.verify.present();
    }
}
