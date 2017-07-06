/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerTab;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.CustomerConstants;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Customer Remove Employment Participant and do not Keep History
 * @scenario
 * 1. Create Non-Individual Customer
 * 2. Perform Add Participant action
 * 3. Perform Remove Relationship for Participant and do not check Keep History
 * 4. QuickSearch for removed Participant
 * 5. Start Update Action
 * 6. Verify that Employee Info tab is not present
 * @details https://jira.exigeninsurance.com/browse/EISDEV-135335
 */
public class TestCustomerParticipantEmploymentRemoveNoHistory extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerParticipantEmploymentRemoveNoHistory() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        customer.addParticipant().perform(tdCustomerNonIndividual.getTestData("AddParticipant", "TestData_Employment"));
        String participantId = CustomerSummaryPage.tableEmploymentCensus.getRow(1).getCell(CustomerConstants.CustomerEmploymentCensusTable.PARTICIPANT_ID).getValue();

        log.info("TEST: Remove Participant and do not Keep History for  # " + participantId);
        customer.removeParticipantEmployment().perform(tdCustomerNonIndividual.getTestData("RemoveParticipant", "TestData"), 1);

        MainPage.QuickSearch.search(participantId);

        customer.update().start();

        NavigationPage.Verify.viewTabPresent(CustomerTab.EMPLOYEE_INFO.toString(), false);
    }
}
