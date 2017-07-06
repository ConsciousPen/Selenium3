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
 * @name Test Customer Relationship Add Remove while creating
 * @scenario
 * 1. Create Non Individual Customer and relationship
 * 2. Navigate to Contact Relationship tab
 * 3. Remove Relationship
 * 7. Navigate to Contact Relationship tab
 * 8. Verify that Relationship is not present in relationship results table
 * @details
 */
public class TestCustomerRelationshipAddRemoveWhileCreating extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerRelationshipAddRemoveWhileCreating() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_Relationship").resolveLinks()));

        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("TEST: Remove Relationship for Customer # " + customerNumber);
        customer.removeRelationshipContact().perform();

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        CustomerSummaryPage.tableRelationshipResult.verify.present(false);
    }
}
