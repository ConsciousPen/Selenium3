/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.individual;

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

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Contact Details Add Remove new Product
 * @scenario
 * 1. Create Individual Customer
 * 2. Navigate to Contacts Relationship tab
 * 3. Add new Product
 * 4. Navigate to Contacts Relationship tab
 * 5. Click on AddNewContactsDetails button
 * 6. Verify that product is present in Product details table
 * 7. Remove product
 * 8. Navigate to Contacts Relationship tab
 * 9. Click on AddNewContactsDetails button
 * 10. Verify that product is not present in Product details table
 * @details
 */
public class TestCustomerContactDetailsNewProductAddRemove extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerContactDetailsNewProductAddRemove() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Add New Product Details for Customer # " + customerNumber);
        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        customer.addNewContactsDetails().perform(tdCustomerIndividual.getTestData("ContactsDetails", "TestData")
                .adjust(tdCustomerIndividual.getTestData("ContactsDetails", "Adjustment_NewProduct")));

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        CustomerSummaryPage.buttonAddNewContactsDetails.click();

        CustomerSummaryPage.tableNewProductDetails.getRow(1).getCell(CustomerConstants.CustomerNewProductDetailsTable.POLICY_TYPE).verify.value(tdCustomerIndividual.getTestData(
                "ContactsDetails", "Adjustment_NewProduct", CustomerMetaData.GeneralTab.class.getSimpleName()).getValue(
                CustomerMetaData.GeneralTab.POLICY_TYPE.getLabel()));

        log.info("TEST: Remove New Product Details for Customer # " + customerNumber);
        customer.removeNewProductDetails().perform(1);
        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        CustomerSummaryPage.buttonAddNewContactsDetails.click();
        CustomerSummaryPage.tableNewProductDetails.verify.present(false);

    }
}
