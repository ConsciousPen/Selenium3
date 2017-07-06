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
 * @name Test Customer Update Membership Participant
 * @scenario
 * 1. Create Non-Individual Customer
 * 2. Add Membership Participant
 * 3. Select added Participant and Perform Update Participant action
 * 4. Update First and Last names of Participant
 * 5. Verify that First and Last Names were changed for Participant in Membership table
 * @details
 */
public class TestCustomerParticipantMembershipUpdate extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerParticipantMembershipUpdate() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        customer.addParticipant().perform(tdCustomerNonIndividual.getTestData("AddParticipant", "TestData_Membership"));

        log.info("TEST: Update Participant for Customer # " + customerNumber);
        customer.updateParticipantMembership().perform(tdSpecific.getTestData("TestData"), 1);
        CustomerSummaryPage.tableMembershipCensus.getRow(1).getCell(CustomerConstants.CustomerMembershipCensusTable.NAME).verify.contains(tdSpecific.getTestData("TestData",
                GeneralTab.class.getSimpleName()).getValue(GeneralTab.FIRST_NAME.getLabel()));
        CustomerSummaryPage.tableMembershipCensus.getRow(1).getCell(CustomerConstants.CustomerMembershipCensusTable.NAME).verify.contains(tdSpecific.getTestData("TestData",
                GeneralTab.class.getSimpleName()).getValue(GeneralTab.LAST_NAME.getLabel()));
    }
}
