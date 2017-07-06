/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Customer Add/Remove Employment Participant
 * @scenario
 * 1. Create Non-Individual Customer
 * 2. Perform Add Participant action
 * 3. Verify that Participant appeared in Employment table in Census tab
 * 4. Remove previously created Participant
 * 5. Verify that Employment table is not present
 * @details
 */
public class TestCustomerParticipantEmploymentAddRemove extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerParticipantEmploymentAddRemove() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Add Participant for Customer # " + customerNumber);
        customer.addParticipant().perform(tdCustomerNonIndividual.getTestData("AddParticipant", "TestData_Employment"));
        CustomerSummaryPage.tableEmploymentCensus.verify.rowsCount(1);

        log.info("TEST: Remove Participant for Customer # " + customerNumber);
        customer.removeParticipantEmployment().perform(tdCustomerNonIndividual.getTestData("RemoveParticipant", "TestData"), 1);
        CustomerSummaryPage.tableEmploymentCensus.verify.present(false);
    }
}
