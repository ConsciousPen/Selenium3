/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.pages.MainPage;
import aaa.main.enums.CustomerConstants;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Customer Remove Membership Participant but Keep History
 * @scenario
 * 1. Create Non-Individual Customer
 * 2. Perform Add Participant action
 * 3. Perform Remove Relationship for Participant and check Keep History
 * 4. QuickSearch for removed Participant
 * 5. Start Update Action
 * 6. Verify that Membership Information is still there.
 * @details
 */
public class TestCustomerParticipantMembershipRemoveKeepHistory extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerParticipantMembershipRemoveKeepHistory() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        customer.addParticipant().perform(tdCustomerNonIndividual.getTestData("AddParticipant", "TestData_Membership"));
        String participantId = CustomerSummaryPage.tableMembershipCensus.getRow(1).getCell(CustomerConstants.CustomerMembershipCensusTable.PARTICIPANT_ID).getValue();

        log.info("TEST: Remove Participant but Keep History for  # " + participantId);
        customer.removeParticipantMembership().perform(tdCustomerNonIndividual.getTestData("RemoveParticipant", "TestData").adjust(
                tdCustomerNonIndividual.getTestData("RemoveParticipant", "Adjustment_KeepHistory")), 1);

        String customerName = CustomerSummaryPage.labelCustomerName.getValue();

        MainPage.QuickSearch.search(participantId);

        customer.update().start();

        CustomerSummaryPage.labelMembershipInformationExpanded.verify.value(
                "Membership Information for " + customerNumber + " " + customerName
                        + " Relationship (Deprecated: Participant was removed from group)");
    }
}
