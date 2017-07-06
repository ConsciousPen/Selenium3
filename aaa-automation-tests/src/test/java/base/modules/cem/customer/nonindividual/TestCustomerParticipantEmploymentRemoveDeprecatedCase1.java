/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerTab;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.enums.CustomerConstants;
import aaa.main.modules.customer.actiontabs.EmployeeInfoTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Customer Remove Deprecated Employment Relationship (case 1)
 * @scenario
 * 1. Create Non-Individual Customer
 * 2. Perform Add Participant action
 * 3. Perform Remove Relationship for previously created Participant and check Keep History
 * 4. QuickSearch for removed Participant
 * 5. Start Update action
 * 6. Navigate to Employee Info tab
 * 7. Remove Deprecated Relationship
 * 8. Verify that Deprecated Relationship was removed
 * @details https://jira.exigeninsurance.com/browse/EISDEV-135337
 */
public class TestCustomerParticipantEmploymentRemoveDeprecatedCase1 extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerParticipantEmploymentRemoveDeprecatedCase1() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        customer.addParticipant().perform(tdCustomerNonIndividual.getTestData("AddParticipant", "TestData_Employment"));
        String participantId = CustomerSummaryPage.tableEmploymentCensus.getRow(1).getCell(CustomerConstants.CustomerEmploymentCensusTable.PARTICIPANT_ID).getValue();

        customer.removeParticipantEmployment().perform(tdCustomerNonIndividual.getTestData("RemoveParticipant", "TestData").adjust(
                tdCustomerNonIndividual.getTestData("RemoveParticipant", "Adjustment_KeepHistory")), 1);

        MainPage.QuickSearch.search(participantId);

        log.info("TEST: Remove Deprecated Participant on Update for Customer # " + participantId);
        customer.update().start();
        NavigationPage.toViewSubTab(CustomerTab.EMPLOYEE_INFO.get());

        EmployeeInfoTab.buttonRemoveRelationshipToSponsor.click();
        Page.dialogConfirmation.confirm();

        EmployeeInfoTab.labelEmployeeInformationExpanded.verify.present(false);
    }
}
