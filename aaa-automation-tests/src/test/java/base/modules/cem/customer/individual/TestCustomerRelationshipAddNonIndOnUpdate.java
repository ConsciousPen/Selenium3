/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.customer.individual;

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
import toolkit.webdriver.controls.RadioGroup;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Relationship Add Non Ind On Update
 * @scenario
 * 1. Create Individual Customer
 * 2. Navigate contact relationship tab
 * 3. Select action Update from drop-down
 * 4. Add new relationship with type Non-Individual
 * 4. Click button Edit this relationship
 * 5. Verify that Type is Non-Individual
 * @details
 */
public class TestCustomerRelationshipAddNonIndOnUpdate extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerRelationshipAddNonIndOnUpdate() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("TEST: Add Relationship for Customer # " + customerNumber);
        customer.update().perform(tdCustomerIndividual.getTestData("Update", "TestData")
                .adjust(tdCustomerIndividual.getTestData("DataGather", "Adjustment_RelationshipWithNonIndividualType")));

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        CustomerSummaryPage.buttonEditRelationship.click();

        new RelationshipTab().getAssetList().getControl(CustomerMetaData.RelationshipTab.TYPE.getLabel(), RadioGroup.class).verify.value(
                tdCustomerIndividual.getTestData("DataGather", "Adjustment_RelationshipWithNonIndividualType").getTestData(
                        CustomerMetaData.RelationshipTab.class.getSimpleName()).getValue(CustomerMetaData.RelationshipTab.TYPE.getLabel()));
    }
}
