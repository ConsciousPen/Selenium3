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
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTime;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer New Relationship Add Remove
 * @scenario
 * 1. Create Non Individual Customer and add Relationship
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
        TestData dateUpdate = tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_RelationshipWithBusinessStarted").resolveLinks();
        DateTime newDate = new DateTime(dateUpdate.getValue(RelationshipTab.class.getSimpleName(), CustomerMetaData.RelationshipTab.DATE_BUSINESS_STARTED.getLabel()), DateTime.MM_DD_YYYY);
        dateUpdate.adjust(TestData.makeKeyPath(RelationshipTab.class.getSimpleName(), CustomerMetaData.RelationshipTab.DATE_BUSINESS_STARTED.getLabel()), newDate.toString());

        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_Relationship").resolveLinks()));

        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Add Relationship for Customer # " + customerNumber);
        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        customer.addNewRelationshipContacts().perform(dateUpdate);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        RelationshipTab.linkRelationshipsTogglePanel.click();

        RelationshipTab.labelBusinessStarted.verify.value(newDate.toString());

        log.info("TEST: Remove New Relationship for Customer # " + customerNumber);
        customer.removeNewRelationshipContacts().perform(1);
        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        RelationshipTab.labelBusinessStarted.verify.present(false);
    }
}
