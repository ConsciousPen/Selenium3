/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.CustomerConstants;
import aaa.main.modules.customer.actiontabs.OpportunityActionTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Opportunity Bam Messages
 * @scenario
 * 1. Create Non Individual Customer
 * 2. Navigate to Opportunity tab
 * 3. Add new Opportunity
 * 4. Update Opportunity
 * 5. Close Opportunity
 * 6. Open ActivitiesAndUserNotes
 * 7. Verify that all Opportunity BAM Messages is present in table
 * @details
 */
public class TestCustomerOpportunityBamMessages extends CustomerBaseTest {
    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerOpportunityBamMessages() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Add Opportunity for Customer # " + customerNumber);
        NavigationPage.toMainTab(CustomerSummaryTab.OPPORTUNITY.get());
        customer.addOpportunity().perform(tdCustomerNonIndividual.getTestData("Opportunity", "TestData"));

        log.info("TEST: Update Opportunity for Customer # " + customerNumber);
        customer.updateOpportunity().perform(tdCustomerNonIndividual.getTestData("Opportunity", "Adjustment_Likehool"));

        log.info("TEST: Close Opportunity for Customer # " + customerNumber);
        customer.removeOpportunity().perform(1);

        OpportunityActionTab.tableOpportunity.getRow(1).getCell(CustomerConstants.CustomerOpportunityTable.ID).controls.links.getFirst().click();

        CustomAssert.enableSoftMode();
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist("Update Opportunity. Status change to Closed");
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist("Close Opportunity. Reason: No interest");
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist("Preview Opportunity Details");
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist("Update Opportunity. No Status change");
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist("Preview Opportunity Details");
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist("Create Opportunity with Status: Draft");
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }
}
