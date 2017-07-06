/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.individual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.CustomerConstants;
import aaa.main.modules.customer.actiontabs.OpportunityActionTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Opportunity Add Remove
 * @scenario
 * 1. Create Individual Customer
 * 2. Navigate to Opportunity tab
 * 3. Add new Opportunity
 * 4. From Action drop-down select "Close"
 * 5. Verify that Opportunity status is Closed
 * @details
 */
public class TestCustomerOpportunityAddRemove extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerOpportunityAddRemove() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        NavigationPage.toMainTab(CustomerSummaryTab.OPPORTUNITY.get());
        log.info("TEST: Add Opportunity for Customer # " + customerNumber);
        customer.addOpportunity().perform(tdCustomerIndividual.getTestData("Opportunity", "TestData"));
        OpportunityActionTab.tableOpportunity.verify.rowsCount(1);

        log.info("TEST: Remove Opportunity for Customer # " + customerNumber);
        customer.removeOpportunity().perform(1);
        OpportunityActionTab.tableOpportunity.getRow(1).getCell(CustomerConstants.CustomerOpportunityTable.STATUS).verify.value("Closed");
    }
}
