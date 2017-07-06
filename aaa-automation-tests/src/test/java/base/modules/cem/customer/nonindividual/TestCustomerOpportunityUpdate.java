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
import aaa.main.modules.customer.actiontabs.OpportunityActionTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Opportunity update
 * @scenario
 * 1. Create Non Individual Customer
 * 2. Navigate to Opportunity tab
 * 3. Add new Opportunity
 * 4. Select action Update change something
 * 5. Verify that is changed
 * @details
 */
public class TestCustomerOpportunityUpdate extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerOpportunityUpdate() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        NavigationPage.toMainTab(CustomerSummaryTab.OPPORTUNITY.get());
        log.info("TEST: Add Opportunity for Customer # " + customerNumber);
        customer.addOpportunity().perform(tdCustomerNonIndividual.getTestData("Opportunity", "TestData"));

        OpportunityActionTab.tableOpportunity.getRow(1).getCell(CustomerConstants.CustomerOpportunityTable.LIKELIHOOD).verify.value(
                tdCustomerNonIndividual.getTestData("Opportunity").getTestData("TestData").getTestData(
                        OpportunityActionTab.class.getSimpleName()).getValue(CustomerMetaData.OpportunityActionTab.LIKELIHOOD.getLabel()));

        log.info("TEST: Update Opportunity for Customer # " + customerNumber);
        customer.updateOpportunity().perform(tdCustomerNonIndividual.getTestData("Opportunity", "Adjustment_Likehool"));

        OpportunityActionTab.tableOpportunity.getRow(1).getCell(CustomerConstants.CustomerOpportunityTable.LIKELIHOOD).verify.value(
                tdCustomerNonIndividual.getTestData("Opportunity").getTestData("Adjustment_Likehool").getTestData(
                        OpportunityActionTab.class.getSimpleName()).getValue(CustomerMetaData.OpportunityActionTab.LIKELIHOOD.getLabel()));
    }
}
