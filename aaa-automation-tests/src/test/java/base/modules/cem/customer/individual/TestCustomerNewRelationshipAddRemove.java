/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.individual;

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
 * @name Test Customer New Relationship Add Remove
 * @scenario
 * 1. Create Individual Customer and add Relationship
 * 2. Navigate to Contact Relationship tab
 * 3. Add new Relationship
 * 4. Navigate to Contact Relationship tab
 * 5. Verify that tax value is present
 * 6. Remove new Relationship
 * 7. Navigate to Contact Relationship tab
 * 8. Verify that tax value is not present
 * @details
 */
public class TestCustomerNewRelationshipAddRemove extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerNewRelationshipAddRemove() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData")
                .adjust(tdCustomerIndividual.getTestData("DataGather", "Adjustment_Relationship").resolveLinks()));

        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Add Relationship for Customer # " + customerNumber);
        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        customer.addNewRelationshipContacts().perform(tdCustomerIndividual.getTestData("DataGather", "Adjustment_RelationshipWithTax"));

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        RelationshipTab.linkRelationshipsTogglePanel.click();

        String taxIdentificationValue = tdCustomerIndividual.getTestData("DataGather").getTestData(
                "Adjustment_RelationshipWithTax").getTestData(RelationshipTab.class.getSimpleName()).getValue(
                CustomerMetaData.RelationshipTab.SSN_TAX_IDENTIFICATION.getLabel());

        RelationshipTab.labelTaxIdentification.verify.value(taxIdentificationValue);

        log.info("TEST: Remove New Relationship for Customer # " + customerNumber);
        customer.removeNewRelationshipContacts().perform(1);
        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        RelationshipTab.labelTaxIdentification.verify.present(false);
    }
}
