/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.defaulttabs.RelationshipTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Relationship update
 * @scenario
 * 1. Create Non Individual Customer
 * 2. Navigate to Contact Relationship tab
 * 3. Add Relationship
 * 4. Navigate to Contact Relationship tab
 * 5. Update Relationship
 * 6. Navigate to Contact Relationship tab
 * 7. Click Edit This Relationship button
 * 8. Verify that Relationship is changed
 * @details
 */
public class TestCustomerRelationshipUpdate extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerRelationshipUpdate() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("TEST: Add Relationship for Customer # " + customerNumber);
        customer.addRelationshipContact().perform(tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_Relationship"));

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("TEST: Update Relationship for Customer # " + customerNumber);
        customer.updateRelationshipContact().perform(tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_RelationshipUpdate"));

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        CustomerSummaryPage.buttonEditRelationship.click();

        RelationshipTab.comboBoxRelationshipRole.verify.value(
                tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_RelationshipUpdate").getTestData(
                        CustomerMetaData.RelationshipTab.class.getSimpleName()).getValue(
                        CustomerMetaData.RelationshipTab.RELATIONSHIP_TO_CUSTOMER.getLabel()));
    }
}
