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
import aaa.main.modules.customer.actiontabs.SponsorParticipantRelationshipAssociationRemovalActionTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Customer Remove Deprecated Student Relationship (case 2)
 * @scenario
 * 1. Create Non-Individual Customer
 * 2. Perform Add Participant action
 * 3. Start Update Participant action for previously created Participant
 * 4. Verify that Student Information is present
 * 5. Remove Relationship to Sponsor and check Keep History
 * 6. Verify that Deprecated Student Information is present
 * 7. Remove Relationship to Sponsor
 * 8. QuickSearch for removed Participant
 * 9. Start Update action
 * 10. Verify that Student Information is not present
 * @details https://jira.exigeninsurance.com/browse/EISDEV-135337
 */
public class TestCustomerParticipantEmploymentRemoveDeprecatedCase2 extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerParticipantEmploymentRemoveDeprecatedCase2() {
        String messageInformation = "Employee Information for %s %s Relationship";
        String messageInformationDeprecated = "Employee Information for %s %s Relationship (Deprecated: Participant was removed from group)";

        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        customer.addParticipant().perform(tdCustomerNonIndividual.getTestData("AddParticipant", "TestData_Employment"));
        String participantId = CustomerSummaryPage.tableEmploymentCensus.getRow(1).getCell(CustomerConstants.CustomerEmploymentCensusTable.PARTICIPANT_ID).getValue();
        String participantName = CustomerSummaryPage.labelCustomerName.getValue();

        customer.updateParticipantEmployment().start(1);
        EmployeeInfoTab.labelEmployeeInformationExpanded.verify.value(String.format(messageInformation, customerNumber, participantName));

        EmployeeInfoTab.buttonRemoveRelationshipToSponsor.click();
        new SponsorParticipantRelationshipAssociationRemovalActionTab().fillTab(tdCustomerNonIndividual.getTestData("RemoveParticipant", "Adjustment_KeepHistory"));
        CustomerSummaryPage.buttonOkKeepHistory.click();
        EmployeeInfoTab.labelEmployeeInformationCollapsed.verify.value(String.format(messageInformationDeprecated, customerNumber, participantName));

        log.info("TEST: Remove Deprecated Participant on Update for Customer # " + participantId);
        EmployeeInfoTab.buttonRemoveRelationshipToSponsor.click();
        Page.dialogConfirmation.confirm();
        EmployeeInfoTab.buttonSaveAndExit.click();

        MainPage.QuickSearch.search(participantId);

        customer.update().start();

        NavigationPage.Verify.viewTabPresent(CustomerTab.EMPLOYEE_INFO.toString(), false);
    }
}
