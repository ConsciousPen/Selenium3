/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.CustomerMetaData.RelationshipTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import base.modules.cem.account.AccountBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTime;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Bam Messages
 * @scenario
 * 1. Create Non Individual Customer
 * 2. Customer Relationship added
 * 3. Customer Relationship updated
 * 4. Customer Relationship removed
 * 5. Customer Contact Details (Address) added
 * 6. Customer Contact Details (Address) updated
 * 7. Customer Contact Details (Address) removed
 * 8. Customer Contact Details (Phone) added
 * 9. Customer Contact Details (Phone) updated
 * 10. Customer Contact Details (Phone) removed
 * 11. Customer Contact Details (Chat) added
 * 12. Customer Contact Details (Chat) updated
 * 13. Customer Contact Details (Chat) removed
 * 14. Customer Contact Details (WebURL) added
 * 15. Customer Contact Details (WebURL) updated
 * 16. Customer Contact Details (WebURL) removed
 * 17. Customer Contact Details (SocialNet) added
 * 18. Customer Contact Details (SocialNet) updated
 * 19. Customer Contact Details (SocialNet) removed
 * 20. Customer Contact Details (Email) added
 * 21. Customer Contact Details (Email) updated
 * 22. Customer Contact Details (Email) removed
 * 23. Verify that all BAM Messages is present in table
 * @details
 */
public class TestCustomerBamMessages extends AccountBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerBamMessages() {
        TestData dateUpdate = tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_RelationshipWithBusinessStarted").resolveLinks();
        DateTime newDate = new DateTime(dateUpdate.getValue(RelationshipTab.class.getSimpleName(), RelationshipTab.DATE_BUSINESS_STARTED.getLabel()), DateTime.MM_DD_YYYY);
        dateUpdate.adjust(TestData.makeKeyPath(RelationshipTab.class.getSimpleName(), RelationshipTab.DATE_BUSINESS_STARTED.getLabel()), newDate.toString());

        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Create Customer " + customerNumber);

        log.info("STEP: Update for Customer # " + customerNumber);
        customer.update().perform(tdCustomerNonIndividual.getTestData("TestCustomerUpdate", "TestData"));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionByRegex(1, "Update Customer General data for Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Add Relationship Contact for Customer #" + customerNumber);
        customer.addNewRelationshipContacts().perform(dateUpdate);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionByRegex(1, "Add Relationship .+ to Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Update Relationship Contact for Customer #" + customerNumber);
        customer.updateRelationshipContact().perform(tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_RelationshipUpdate"));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionByRegex(1, "Update Relationship .+ for Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Remove Relationship Contact for Customer #" + customerNumber);
        customer.removeRelationshipContact().perform();
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionByRegex(1, "Remove Relationship .+ from Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Add new Contacts Details for Customer #" + customerNumber);
        customer.addNewContactsDetails().perform(tdCustomerNonIndividual.getTestData("ContactsDetails", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("ContactsDetails", "Adjustment_NewAddress")));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Add Customer Address Contact EISGROUP, As, CA, 12345 to Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Update new Contacts Details for Customer #" + customerNumber);
        customer.updateContactsDetails().perform(tdCustomerNonIndividual.getTestData("ContactsDetails", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("ContactsDetails", "Adjustment_UpdateAddressType")), 2);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Update Customer Address Contact EISGROUP, As, CA, 12345  for Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Remove new Contacts Details for Customer #" + customerNumber);
        customer.removeNewContactsDetails().perform(2);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Remove Customer Address Contact EISGROUP, As, CA, 12345 from Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Add new Contacts Details for Customer #" + customerNumber);
        customer.addNewContactsDetails().perform(tdCustomerNonIndividual.getTestData("ContactsDetails", "TestData"));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Add Customer Phone Contact (012) 345-6789 to Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Update new Contacts Details for Customer #" + customerNumber);
        customer.updateContactsDetails().perform(tdCustomerNonIndividual.getTestData("ContactsDetails", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("ContactsDetails", "Adjustment_UpdatePhoneNumber")), 2);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Update Customer Phone Contact (012) 345-6789 for Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Remove new Contacts Details for Customer #" + customerNumber);
        customer.removeNewContactsDetails().perform(2);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Remove Customer Phone Contact (987) 654-3210 from Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Add new Contacts Details for Customer #" + customerNumber);
        customer.addNewContactsDetails().perform(tdCustomerNonIndividual.getTestData("ContactsDetails", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("ContactsDetails", "Adjustment_NewChat")));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Add Customer Chat Contact 1 (Skype) to Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Update new Contacts Details for Customer #" + customerNumber);
        customer.updateContactsDetails().perform(tdCustomerNonIndividual.getTestData("ContactsDetails", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("ContactsDetails", "Adjustment_UpdateChatId")), 2);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Update Customer Chat Contact 1 (Skype) for Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Remove new Contacts Details for Customer #" + customerNumber);
        customer.removeNewContactsDetails().perform(2);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Remove Customer Chat Contact 2 (Skype) from Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Add new Contacts Details for Customer #" + customerNumber);
        customer.addNewContactsDetails().perform(tdCustomerNonIndividual.getTestData("ContactsDetails", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("ContactsDetails", "Adjustment_NewWebUrl")));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionByRegex(1, "Add Customer WebURL .+ to Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Update new Contacts Details for Customer #" + customerNumber);
        customer.updateContactsDetails().perform(tdCustomerNonIndividual.getTestData("ContactsDetails", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("ContactsDetails", "Adjustment_UpdateWebUrl")), 2);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionByRegex(1, "Update Customer WebURL .+ for Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Remove new Contacts Details for Customer #" + customerNumber);
        customer.removeNewContactsDetails().perform(2);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionByRegex(1, "Remove Customer WebURL .+ from Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Add new Contacts Details for Customer #" + customerNumber);
        customer.addNewContactsDetails().perform(tdCustomerNonIndividual.getTestData("ContactsDetails", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("ContactsDetails", "Adjustment_NewSocialNet")));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionByRegex(1, "Add Customer SocialNet .+ to Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Update new Contacts Details for Customer #" + customerNumber);
        customer.updateContactsDetails().perform(tdCustomerNonIndividual.getTestData("ContactsDetails", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("ContactsDetails", "Adjustment_UpdateSocialNetId")), 2);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionByRegex(1, "Update Customer SocialNet .+ for Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Remove new Contacts Details for Customer #" + customerNumber);
        customer.removeNewContactsDetails().perform(2);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionByRegex(1, "Remove Customer SocialNet .+ from Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Add new Contacts Details for Customer #" + customerNumber);
        customer.addNewContactsDetails().perform(tdCustomerNonIndividual.getTestData("ContactsDetails", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("ContactsDetails", "Adjustment_NewEmail")));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionByRegex(1, "Add Customer Email Contact .+ to Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Update new Contacts Details for Customer #" + customerNumber);
        customer.updateContactsDetails().perform(tdCustomerNonIndividual.getTestData("ContactsDetails", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("ContactsDetails", "Adjustment_UpdateEmailAddress")), 2);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionByRegex(1, "Update Customer Email Contact .+ for Customer " + customerNumber);

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        log.info("STEP: Remove new Contacts Details for Customer #" + customerNumber);
        customer.removeNewContactsDetails().perform(2);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionByRegex(1, "Remove Customer Email Contact .+ from Customer " + customerNumber);
    }
}
