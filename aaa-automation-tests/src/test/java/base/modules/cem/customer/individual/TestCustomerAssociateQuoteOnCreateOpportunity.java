/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.individual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.AppMainTabs;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.CustomerConstants;
import aaa.main.modules.customer.actiontabs.OpportunityActionTab;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Associate Quote On Create Opportunity
 * @scenario
 * 1. Create Individual Customer
 * 2. Create quote
 * 3. Navigate to Customer tab
 * 4. Click on Opportunity tab
 * 5. Add Associate quote to Opportunity
 * 6. Verify that, Opportunity status is Quoted
 * 7. Remove associated quote
 * 8. Verify that Opportunity status is Draft
 * @details
 */
public class TestCustomerAssociateQuoteOnCreateOpportunity extends CustomerBaseTest {

    private PolicyType type = PolicyType.HOME_SS;
    private IPolicy policy = type.get();
    private TestData tdPolicy = testDataManager.policy.get(type);

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerAssociateQuoteOnCreateOpportunity() {
        mainApp().open();

        customer.create(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        NavigationPage.toMainTab(AppMainTabs.QUOTE.get());

        policy.createQuote(tdPolicy.getTestData("DataGather", "TestData"));

        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        NavigationPage.toMainTab(AppMainTabs.CUSTOMER.get());

        NavigationPage.toMainTab(CustomerSummaryTab.OPPORTUNITY.get());
        log.info("TEST: Add Associate Quote to Opportunity tab " + quoteNumber);
        customer.addAssociateQuoteOnOpportunity().perform(tdCustomerIndividual.getTestData("Opportunity", "TestData"), quoteNumber);

        OpportunityActionTab.tableOpportunity.verify.rowsCount(1);
        OpportunityActionTab.tableOpportunity.getRow(1).getCell(CustomerConstants.CustomerOpportunityTable.STATUS).verify.value("Quoted");

        log.info("STEP: Remove Associated Quote from Opportunity tab " + quoteNumber);
        customer.removeAssociateQuoteOnOpportunity().perform(1);
        OpportunityActionTab.tableOpportunity.getRow(1).getCell(CustomerConstants.CustomerOpportunityTable.STATUS).verify.value("Draft");
    }
}
