/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.customer.individual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.AppMainTabs;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Start New Quote For Opportunity Create Update Preview
 * @scenario
 * 1. Create Individual Customer
 * 2. Navigate to Opportunity tab
 * 3. Click start new quote button in opportunity tab
 * 4. Quote is created
 * 5. Back to opportunity tab
 * 6. Click on created opportunity record and click update
 * 7. Click start new quote button
 * 8. Quote is created
 * 9. Back to opportunity tab
 * 10. Click on created opportunity record
 * 11. Click start new quote button
 * 12. Quote is created
 * @details
 */
public class TestCustomerStartNewQuoteForOpportunityCreateUpdatePreview extends CustomerBaseTest {

    private PolicyType type = PolicyType.HOME_SS;
    private IPolicy policy = type.get();
    private TestData tdPolicy = testDataManager.policy.get(type);

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerStartNewQuoteForOpportunityCreateUpdatePreview() {
        mainApp().open();

        customer.create(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        NavigationPage.toMainTab(CustomerSummaryTab.OPPORTUNITY.get());
        log.info("TEST: Start new quote in Opportunity Create, Update, Preview");
        customer.startNewQuoteInOpportunity().perform(tdCustomerIndividual.getTestData("Opportunity", "TestData"));

        policy.createQuote(tdPolicy.getTestData("DataGather", "TestData"));

        NavigationPage.toMainTab(AppMainTabs.CUSTOMER.get());
        NavigationPage.toMainTab(CustomerSummaryTab.OPPORTUNITY.get());

        log.info("STEP: start new quote in Opportunity update");
        customer.startNewQuoteInOpportunityUpdate().perform(1);

        policy.createQuote(tdPolicy.getTestData("DataGather", "TestData"));

        NavigationPage.toMainTab(AppMainTabs.CUSTOMER.get());
        NavigationPage.toMainTab(CustomerSummaryTab.OPPORTUNITY.get());

        log.info("STEP: start new quote in Opportunity preview");
        customer.startNewQuoteInOpportunityPreview().perform(1);

        policy.createQuote(tdPolicy.getTestData("DataGather", "TestData"));
    }
}
