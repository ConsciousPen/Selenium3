/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.pages.MainPage;
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
public class TestCustomerParticipantStudentRemoveDeprecatedCase2 extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerParticipantStudentRemoveDeprecatedCase2() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        customer.addParticipant().perform(tdCustomerNonIndividual.getTestData("AddParticipant", "TestData_Student"));
        String participantId = CustomerSummaryPage.tableStudentCensus.getRow(1).getCell(CustomerConstants.CustomerStudentCensusTable.PARTICIPANT_ID).getValue();
        String participantName = CustomerSummaryPage.labelCustomerName.getValue();

        customer.updateParticipantStudent().start(1);
        CustomerSummaryPage.labelStudentInformationExpanded.verify.value(
                "Student Information for " + customerNumber + " " + participantName + " Relationship");

        EmployeeInfoTab.buttonRemoveRelationshipToSponsor.click();
        new SponsorParticipantRelationshipAssociationRemovalActionTab().fillTab(
                tdCustomerNonIndividual.getTestData("RemoveParticipant", "Adjustment_KeepHistory"));
        CustomerSummaryPage.buttonOkKeepHistory.click();
        CustomerSummaryPage.labelStudentInformationCollapsed.verify.value(
                "Student Information for " + customerNumber + " " + participantName
                        + " Relationship (Deprecated: Participant was removed from group)");

        log.info("TEST: Remove Deprecated Participant on Update for Customer # " + participantId);
        EmployeeInfoTab.buttonRemoveRelationshipToSponsor.click();
        Page.dialogConfirmation.confirm();
        EmployeeInfoTab.buttonSaveAndExit.click();

        MainPage.QuickSearch.search(participantId);

        customer.update().start();

        CustomerSummaryPage.labelStudentInformationExpanded.verify.present(false);
    }
}
