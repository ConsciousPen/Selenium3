/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Relationship Add Remove
 * @scenario
 * 1. Create Non Individual Customer
 * 2. Navigate to Contact Relationship tab
 * 3. Add Relationship
 * 4. Navigate to Contact Relationship tab
 * 5. Verify that Relationship is present in relationship results table
 * 6. Remove Relationship
 * 7. Navigate to Contact Relationship tab
 * 8. Verify that Relationship is not present in relationship results table
 * @details
 */
public class TestCustomerRelationshipAddRemove extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerRelationshipAddRemove() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("TEST: Add Relationship for Customer # " + customerNumber);
        customer.addRelationshipContact().perform(tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_Relationship"));

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        CustomerSummaryPage.tableRelationshipResult.verify.rowsCount(1);
        log.info("TEST: Remove Relationship for Customer # " + customerNumber);
        customer.removeRelationshipContact().perform();

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        CustomerSummaryPage.tableRelationshipResult.verify.present(false);
    }
}
