/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.main.enums.CustomerConstants;
import aaa.main.metadata.CustomerMetaData.GeneralTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Customer Update Employment Participant
 * @scenario
 * 1. Create Non-Individual Customer
 * 2. Add Employment Participant
 * 3. Select added Participant and Perform Update Participant action
 * 4. Update First and Last names of Participant
 * 5. Verify that First and Last Names were changed for Participant in Employment table
 * @details
 */
public class TestCustomerParticipantEmploymentUpdate extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerParticipantEmploymentUpdate() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        customer.addParticipant().perform(tdCustomerNonIndividual.getTestData("AddParticipant", "TestData_Employment"));

        log.info("TEST: Update Participant for Customer # " + customerNumber);
        customer.updateParticipantEmployment().perform(tdSpecific.getTestData("TestData"), 1);
        CustomerSummaryPage.tableEmploymentCensus.getRow(1).getCell(CustomerConstants.CustomerEmploymentCensusTable.NAME).verify.contains(tdSpecific.getTestData("TestData",
                GeneralTab.class.getSimpleName()).getValue(GeneralTab.FIRST_NAME.getLabel()));
        CustomerSummaryPage.tableEmploymentCensus.getRow(1).getCell(CustomerConstants.CustomerEmploymentCensusTable.NAME).verify.contains(tdSpecific.getTestData("TestData",
                GeneralTab.class.getSimpleName()).getValue(GeneralTab.LAST_NAME.getLabel()));
    }
}
