/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.customer;

import java.util.Arrays;
import aaa.common.Tab;
import aaa.main.modules.customer.views.*;
import org.openqa.selenium.By;

import aaa.common.AbstractAction;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum.CustomerTab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.enums.CustomerConstants;
import aaa.main.modules.customer.actiontabs.CommunicationActionTab;
import aaa.main.modules.customer.actiontabs.OpportunityActionTab;
import aaa.main.modules.customer.defaulttabs.BusinessEntityTab;
import aaa.main.modules.customer.defaulttabs.CustomerTypeTab;
import aaa.main.modules.customer.defaulttabs.DivisionsTab;
import aaa.main.modules.customer.defaulttabs.GeneralTab;
import aaa.main.modules.customer.defaulttabs.RelationshipTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * Set of abstract classes describing all actions available for the product entities of each type.
 * Modify this class if the set of actions for a particular product entity has to be changed.
 * @category Generated
 */
public final class CustomerActions {
	private CustomerActions() {}

    public static class AddCommunication extends AbstractAction {
        @Override
        public String getName() {
            return "Add Communication";
        }

        @Override
        public Workspace getView() {
            return new CommunicationView();
        }

        @Override
        public AbstractAction start() {
            CustomerSummaryPage.buttonStartNewCommunication.click();
            return this;
        }

        @Override
        public AbstractAction submit() {
            CommunicationActionTab.buttonSave.click();
            return this;
        }
    }

    public static class AddCommunicationThread extends AbstractAction {
        @Override
        public String getName() {
            return "Add Communication Thread";
        }

        @Override
        public Workspace getView() {
            return new CommunicationView();
        }

        public AbstractAction start(int rowNumber) {
            CommunicationActionTab.tableCommunications.getRow(rowNumber).getCell(CustomerConstants.CustomerCommunicationsTable.ACTION).controls.buttons.get("Thread").click();
            return this;
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with only testData is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            CommunicationActionTab.buttonSave.click();
            return this;
        }
    }

    public static class RemoveCommunication extends AbstractAction {
        @Override
        public String getName() {
            return "Remove Communication";
        }

        @Override
        public Workspace getView() {
            return new CommunicationView();
        }

        public AbstractAction start(int rowNumber) {
            CommunicationActionTab.tableCommunications.getRow(rowNumber).getCell(CustomerConstants.CustomerCommunicationsTable.ACTION).controls.buttons.get("Delete").click();
            Page.dialogConfirmation.confirm();
            return this;
        }

        public AbstractAction perform(int rowNumber) {
            start(rowNumber);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class UpdateCommunication extends AbstractAction {
        @Override
        public String getName() {
            return "Update Communication";
        }

        @Override
        public Workspace getView() {
            return new CommunicationView();
        }

        public AbstractAction start(int rowNumber) {
            CommunicationActionTab.tableCommunications.getRow(rowNumber).getCell(CustomerConstants.CustomerCommunicationsTable.ACTION).controls.buttons.get("Update").click();
            return this;
        }

        public AbstractAction perform(int rowNumber, TestData td) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method without rowNumber is not supported for this action. Use perform(int rowNumber, TestData td) instead.");
        }

        @Override
        public AbstractAction submit() {
            CommunicationActionTab.buttonSave.click();
            return this;
        }
    }

    public static class DeleteCustomer extends AbstractAction {
        @Override
        public String getName() {
            return "Delete Customer";
        }

        @Override
        public Workspace getView() {
            return new DefaultView();
        }

        public AbstractAction perform() {
            start();
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform() instead.");
        }

        @Override
        public AbstractAction submit() {
            Page.dialogConfirmation.confirm();
            if (Page.dialogConfirmation.isPresent()) {
                Page.dialogConfirmation.confirm();
            }
            return this;
        }
    }

    public static class AddOpportunity extends AbstractAction {
        @Override
        public String getName() {
            return "Add Opportunity";
        }

        @Override
        public Workspace getView() {
            return new OpportunityView();
        }

        @Override
        public AbstractAction start() {
            OpportunityActionTab.buttonCreateNewOpportunity.click();
            OpportunityActionTab.buttonAddOpportunity.click();
            return this;
        }

        @Override
        public AbstractAction submit() {
            OpportunityActionTab.buttonDone.click();
            OpportunityActionTab.buttonDone.click();
            return this;
        }
    }

    public static class UpdateOpportunity extends AbstractAction {
        @Override
        public String getName() {
            return "Update Opportunity";
        }

        @Override
        public Workspace getView() {
            return new OpportunityView();
        }

        @Override
        public AbstractAction start() {
            OpportunityActionTab.tableOpportunity.getRow(1).getCell(CustomerConstants.CustomerOpportunityTable.ID).controls.links.getFirst().click();
            NavigationPage.setActionAndGo("Update");
            return this;
        }

        @Override
        public AbstractAction submit() {
            OpportunityActionTab.buttonDone.click();
            OpportunityActionTab.buttonDone.click();
            return this;
        }
    }

    public static class MergeCustomer extends  AbstractAction {

        @Override
        public String getName() { return "Merge Customers"; }

        @Override
        public Workspace getView() {
            return new MergeView();
        }


        public AbstractAction perform(TestData td) {
            //TODO
            start();
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction submit() {
            new Link(By.id("customerSearchForMergeForm:searchResultTable:0:selectCustomerLink")).click();
            new Button(By.id("mergeCustomersForm:mergeBtn_footer")).click();
            new Button(By.id("topSaveAndExitLink")).click();
            return this;
        }
    }

    public static class RemoveOpportunity extends AbstractAction {
        @Override
        public String getName() {
            return "Remove Opportunity";
        }

        @Override
        public Workspace getView() {
            return new OpportunityView();
        }

        public AbstractAction start(int rowNumber) {
            OpportunityActionTab.tableOpportunity.getRow(rowNumber).getCell(CustomerConstants.CustomerOpportunityTable.ID).controls.links.getFirst().click();
            NavigationPage.setActionAndGo("Close");
            Page.dialogConfirmation.confirm();
            return this;
        }

        public AbstractAction perform(int rowNumber) {
            start(rowNumber);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            OpportunityActionTab.buttonDone.click();
            return this;
        }
    }

    public static class Update extends AbstractAction {
        @Override
        public String getName() {
            return "Update";
        }

        @Override
        public Workspace getView() {
            return new UpdateView();
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class AddCustomerAdditionalNames extends AbstractAction{
        @Override
        public String getName() {
            return "Update";
        }

        @Override
        public Workspace getView() {
            return new UpdateView();
        }

        public AbstractAction perform(TestData td) {
            start();
            new GeneralTab().fillTab(td);
            return this;
        }
    }

    public static class RemoveRelationshipContact extends AbstractAction {
        @Override
        public String getName() {
            return "Remove Relationship Contact";
        }

        @Override
        public Workspace getView() {
            return new RelationshipContactView();
        }

        @Override
        public AbstractAction start() {
            CustomerSummaryPage.buttonEditRelationship.click();
            CustomerSummaryPage.linkRemoveThisRelationship.click();
            Page.dialogConfirmation.confirm();
            Page.dialogConfirmation.buttonYes.click();
            return this;
        }

        public AbstractAction perform() {
            start();
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform() instead.");
        }

        @Override
        public AbstractAction submit() {
            RelationshipTab.buttonDone.click();
            return this;
        }
    }

    public static class AddRelationshipContact extends AbstractAction {
        @Override
        public String getName() {
            return "Add Relationship Contact";
        }

        @Override
        public Workspace getView() {
            return new RelationshipContactView();
        }

        @Override
        public AbstractAction start() {
            CustomerSummaryPage.linkAddNewRelationshipContact.click();
            return this;
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class UpdateRelationshipContact extends AbstractAction {
        @Override
        public String getName() {
            return "Update Relationship Contact";
        }

        @Override
        public Workspace getView() {
            return new RelationshipContactView();
        }

        @Override
        public AbstractAction start() {
            CustomerSummaryPage.linkEditThisRelationship.click();
            return this;
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class ScheduledUpdate extends AbstractAction {
        @Override
        public String getName() {
            return "Scheduled Update";
        }

        @Override
        public Workspace getView() {
            return new ScheduledUpdateView();
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class Inquiry extends AbstractAction {
        @Override
        public String getName() {
            return "Inquiry";
        }

        @Override
        public Workspace getView() {
            return new InquiryView();
        }

        public AbstractAction perform() {
            start();
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform() instead.");
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class DeletePendingUpdates extends AbstractAction {
        @Override
        public String getName() {
            return "Delete Pending Updates";
        }

        @Override
        public Workspace getView() {
            return new DeletePendingUpdatesView();
        }

        public AbstractAction perform() {
            start();
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform() instead.");
        }

        @Override
        public AbstractAction submit() {
            Page.dialogConfirmation.confirm();
            return this;
        }
    }

    public static class AddNewContactsDetails extends AbstractAction {
        @Override
        public String getName() {
            return "Add New Contacts Details";
        }

        @Override
        public Workspace getView() {
            return new DetailsContactView();
        }

        @Override
        public AbstractAction start() {
            CustomerSummaryPage.buttonAddNewContactsDetails.click();
            return this;
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class RemoveNewContactsDetails extends AbstractAction {
        @Override
        public String getName() {
            return "Remove New Contacts Details";
        }

        @Override
        public Workspace getView() {
            return new DetailsContactView();
        }

        public AbstractAction start(int rowNumber) {
            CustomerSummaryPage.buttonAddNewContactsDetails.click();
            CustomerSummaryPage.tableCustomerContacts.getRow(rowNumber).getCell(CustomerConstants.CustomerContactsTable.ACTION).controls.buttons.get("Remove").click();
            Page.dialogConfirmation.confirm();
            GeneralTab.buttonNext.click();
            BusinessEntityTab.buttonNext.click();
            return this;
        }

        public AbstractAction perform(int rowNumber) {
            start(rowNumber);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            RelationshipTab.buttonDone.click();
            return this;
        }
    }

    public static class AddNewRelationshipContacts extends AbstractAction {
        @Override
        public String getName() {
            return "Add New Relationship Contact";
        }

        @Override
        public Workspace getView() {
            return new RelationshipContactView();
        }

        @Override
        public AbstractAction start() {
            CustomerSummaryPage.linkAddNewRelationshipContact.click();
            return this;
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class RemoveNewRelationshipContacts extends AbstractAction {
        @Override
        public String getName() {
            return "Remove Relationship Contact";
        }

        @Override
        public Workspace getView() {
            return new RelationshipContactView();
        }

        public AbstractAction start(int rowNumber) {
            new Link(By.xpath("//div[@id='crmForm:customerRelationshipsTogglePanel:content']//*[@id= 'crmForm:editRelationship_" + rowNumber + "']")).click();
            new Link(By.xpath("//*[@id='crmForm:removeRelationshipBtn_" + rowNumber + "']")).click();
            Page.dialogConfirmation.confirm();
            new Button(By.xpath("//input[@id='crmForm:yes_deleteLeadPopup_" + rowNumber + "']")).click();
            return this;
        }

        public AbstractAction perform(int rowNumber) {
            start(rowNumber);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            RelationshipTab.buttonDone.click();
            return this;
        }
    }

    public static class AddAgency extends AbstractAction {
        @Override
        public String getName() {
            return "Add Agency";
        }

        @Override
        public Workspace getView() {
            return new AddAgencyView();
        }

        @Override
        public AbstractAction start() {
            CustomerSummaryPage.buttonAddAgency.click();
            return this;
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class RemoveAgency extends AbstractAction {
        @Override
        public String getName() {
            return "Remove Agency";
        }

        @Override
        public Workspace getView() {
            return new RemoveAgencyView();
        }

        public AbstractAction start(int rowNumber) {
            CustomerSummaryPage.buttonAddAgency.click();
            new Button(By.xpath("//a[@id='crmForm:removeAssignmentBtn_" + rowNumber + "']")).click();
            return this;
        }

        public AbstractAction perform(int rowNumber, TestData td) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method without rowNumber is not supported for this action. Use perform(int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class RemoveBusinessEntity extends AbstractAction {
        @Override
        public String getName() {
            return "Remove This Business Entity";
        }

        @Override
        public Workspace getView() {
            return new DefaultView();
        }

        @Override
        public AbstractAction start() {
            NavigationPage.setActionAndGo("Update");
            CustomerTypeTab.buttonNext.click();
            CustomerSummaryPage.linkRemoveThisBusinessEntity.click();
            BusinessEntityTab.buttonNext.click();
            return this;
        }

        public AbstractAction perform() {
            start();
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform() instead.");
        }

        @Override
        public AbstractAction submit() {
            RelationshipTab.buttonDone.click();
            return this;
        }
    }

    public static class AddParticipant extends AbstractAction {
        @Override
        public String getName() {
            return "Add Participant";
        }

        @Override
        public Workspace getView() {
            return new AddParticipantView();
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class RemoveParticipantEmployment extends AbstractAction {
        @Override
        public String getName() {
            return "Remove Relationship";
        }

        @Override
        public Workspace getView() {
            return new RemoveParticipantView();
        }

        public AbstractAction start(int rowNumber) {
            CustomerSummaryPage.tableEmploymentCensus.getColumn(1).getCell(rowNumber).controls.checkBoxes.getFirst().setValue(true);
            new ComboBox(By.id("EMP_crmForm:censusSelect")).setValue(getName());
            new Button(By.id("EMP_crmForm:Go")).click();
            return this;
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method without rowNumber is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class RemoveParticipantMembership extends AbstractAction {
        @Override
        public String getName() {
            return "Remove Relationship";
        }

        @Override
        public Workspace getView() {
            return new RemoveParticipantView();
        }

        public AbstractAction start(int rowNumber) {
            CustomerSummaryPage.tableMembershipCensus.getColumn(1).getCell(rowNumber).controls.checkBoxes.getFirst().setValue(true);
            new ComboBox(By.id("MEM_crmForm:censusSelect")).setValue(getName());
            new Button(By.id("MEM_crmForm:Go")).click();
            return this;
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method without rowNumber is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class RemoveParticipantStudent extends AbstractAction {
        @Override
        public String getName() {
            return "Remove Relationship";
        }

        @Override
        public Workspace getView() {
            return new RemoveParticipantView();
        }

        public AbstractAction start(int rowNumber) {
            CustomerSummaryPage.tableStudentCensus.getColumn(1).getCell(rowNumber).controls.checkBoxes.getFirst().setValue(true);
            new ComboBox(By.id("STU_crmForm:censusSelect")).setValue(getName());
            new Button(By.id("STU_crmForm:Go")).click();
            return this;
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method without rowNumber is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class UpdateParticipantEmployment extends AbstractAction {
        @Override
        public String getName() {
            return "Update Participant";
        }

        @Override
        public Workspace getView() {
            return new UpdateParticipantView();
        }

        public AbstractAction start(int rowNumber) {
            CustomerSummaryPage.tableEmploymentCensus.getColumn(1).getCell(rowNumber).controls.checkBoxes.getFirst().setValue(true);
            new ComboBox(By.id("EMP_crmForm:censusSelect")).setValue(getName());
            new Button(By.id("EMP_crmForm:Go")).click();
            return this;
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            NavigationPage.toViewSubTab(CustomerTab.GENERAL.get());
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method without rowNumber is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class UpdateParticipantMembership extends AbstractAction {
        @Override
        public String getName() {
            return "Update Participant";
        }

        @Override
        public Workspace getView() {
            return new UpdateParticipantView();
        }

        public AbstractAction start(int rowNumber) {
            CustomerSummaryPage.tableMembershipCensus.getColumn(1).getCell(rowNumber).controls.checkBoxes.getFirst().setValue(true);
            new ComboBox(By.id("MEM_crmForm:censusSelect")).setValue(getName());
            new Button(By.id("MEM_crmForm:Go")).click();
            return this;
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method without rowNumber is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class UpdateParticipantStudent extends AbstractAction {
        @Override
        public String getName() {
            return "Update Participant";
        }

        @Override
        public Workspace getView() {
            return new UpdateParticipantView();
        }

        public AbstractAction start(int rowNumber) {
            CustomerSummaryPage.tableStudentCensus.getColumn(1).getCell(rowNumber).controls.checkBoxes.getFirst().setValue(true);
            new ComboBox(By.id("STU_crmForm:censusSelect")).setValue(getName());
            new Button(By.id("STU_crmForm:Go")).click();
            return this;
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method without rowNumber is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class AssociateExistingCustomer extends AbstractAction {
        @Override
        public String getName() {
            return "Associate Existing Customer";
        }

        @Override
        public Workspace getView() {
            return new AssociateExistingCustomerView();
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class RemoveNewProductDetails extends AbstractAction {
        @Override
        public String getName() {
            return "Remove New Product Details";
        }

        @Override
        public Workspace getView() {
            return new DetailsContactView();
        }

        public AbstractAction start(int rowNumber) {
            new Table(By.id("crmForm:otherProductsTable")).getRow(rowNumber).getCell(CustomerConstants.CustomerProductsTable.ACTION).controls.buttons.get("Remove").click();
            Page.dialogConfirmation.confirm();
            GeneralTab.buttonNext.click();
            BusinessEntityTab.buttonNext.click();
            return this;
        }

        public AbstractAction perform(int rowNumber) {
            start(rowNumber);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            RelationshipTab.buttonDone.click();
            return this;
        }
    }

    public static class Qualify extends AbstractAction {
        @Override
        public String getName() {
            return "Qualify";
        }

        @Override
        public Workspace getView() {
            return new QualifyView();
        }

        public AbstractAction perform() {
            start();
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform() instead.");
        }

        @Override
        public AbstractAction submit() {
            Page.dialogConfirmation.confirm();
            return this;
        }
    }

    public static class MakeInvalid extends AbstractAction {
        @Override
        public String getName() {
            return "Make Invalid";
        }

        @Override
        public Workspace getView() {
            return new MakeInvalidView();
        }

        public AbstractAction perform() {
            start();
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform() instead.");
        }

        @Override
        public AbstractAction submit() {
            Page.dialogConfirmation.confirm();
            return this;
        }
    }

    public static class UndoInvalid extends AbstractAction {
        @Override
        public String getName() {
            return "Undo Invalid";
        }

        @Override
        public Workspace getView() {
            return new UndoInvalidView();
        }

        public AbstractAction perform() {
            start();
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform() instead.");
        }

        @Override
        public AbstractAction submit() {
            Page.dialogConfirmation.confirm();
            return this;
        }
    }

    public static class RemoveDivisions extends AbstractAction {
        @Override
        public String getName() {
            return "Remove This Division";
        }

        @Override
        public Workspace getView() {
            return new DefaultView();
        }

        @Override
        public AbstractAction start() {
            NavigationPage.setActionAndGo("Update");
            GeneralTab.buttonNext.click();
            CustomerSummaryPage.linkRemoveThisDivision.click();
            DivisionsTab.buttonNext.click();
            return this;
        }

        public AbstractAction perform() {
            start();
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform() instead.");
        }

        @Override
        public AbstractAction submit() {
            RelationshipTab.buttonDone.click();
            return this;
        }
    }

    public static class ViewHistory extends AbstractAction {
        @Override
        public String getName() {
            return "View History";
        }

        @Override
        public Workspace getView() {
            return new ViewHistoryView();
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class UpdateContactsDetails extends AbstractAction {
        @Override
        public String getName() {
            return "Update Contacts Details";
        }

        @Override
        public Workspace getView() {
            return new DetailsContactView();
        }

        public AbstractAction start(int rowNumber) {
            CustomerSummaryPage.buttonAddNewContactsDetails.click();
            CustomerSummaryPage.tableCustomerContacts.getRow(rowNumber).getCell(CustomerConstants.CustomerContactsTable.ACTION).controls.buttons.get("Change").click();
            return this;
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class AddAssociateQuoteOnOpportunity extends AbstractAction {
        @Override
        public String getName() {
            return "Add Associate Quote On Opportunity";
        }

        @Override
        public Workspace getView() {
            return new OpportunityView();
        }

        @Override
        public AbstractAction start() {
            OpportunityActionTab.buttonCreateNewOpportunity.click();
            OpportunityActionTab.buttonAddOpportunity.click();
            return this;
        }

        public AbstractAction perform(TestData td, String id) {
            start();
            getView().fill(td);
            new TextBox(By.id("editOpportunity:QUOTEAssignId")).setValue(id);
            new Button(By.id("editOpportunity:QUOTEAssociateAction")).click();
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, String id) instead.");
        }

        @Override
        public AbstractAction submit() {
            OpportunityActionTab.buttonDone.click();
            OpportunityActionTab.buttonDone.click();
            return this;
        }
    }

    public static class RemoveAssociateQuoteOnOpportunity extends AbstractAction {
        @Override
        public String getName() {
            return "Remove Associate Quote On Opportunity";
        }

        @Override
        public Workspace getView() {
            return new OpportunityView();
        }

        public AbstractAction start(int rowNumber) {
            OpportunityActionTab.tableOpportunity.getRow(rowNumber).getCell(CustomerConstants.CustomerOpportunityTable.ID).controls.links.get(rowNumber).click();
            NavigationPage.setActionAndGo("Update");
            new Button(By.xpath("//a[contains(@id, 'QUOTERemoveAssociationAction')]")).click();
            return this;
        }

        public AbstractAction perform(int rowNumber) {
            start(rowNumber);
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td) instead.");
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            OpportunityActionTab.buttonDone.click();
            OpportunityActionTab.buttonDone.click();
            return this;
        }
    }

    public static class AddAssociatePolicyOnOpportunity extends AbstractAction {
        @Override
        public String getName() {
            return "Add Associate Policy On Opportunity";
        }

        @Override
        public Workspace getView() {
            return new OpportunityView();
        }

        @Override
        public AbstractAction start() {
            OpportunityActionTab.buttonCreateNewOpportunity.click();
            OpportunityActionTab.buttonAddOpportunity.click();
            return this;
        }

        public AbstractAction perform(TestData td, String id) {
            start();
            getView().fill(td);
            new TextBox(By.id("editOpportunity:POLICYAssignId")).setValue(id);
            new Button(By.id("editOpportunity:POLICYAssociateAction")).click();
            return submit();

        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, String id) instead.");
        }

        @Override
        public AbstractAction submit() {
            OpportunityActionTab.buttonDone.click();
            OpportunityActionTab.buttonDone.click();
            return this;
        }
    }

    public static class RemoveAssociatePolicyOnOpportunity extends AbstractAction {
        @Override
        public String getName() {
            return "Remove Associate Policy On Opportunity";
        }

        @Override
        public Workspace getView() {
            return new OpportunityView();
        }

        public AbstractAction start(int rowNumber) {
            OpportunityActionTab.tableOpportunity.getRow(rowNumber).getCell(CustomerConstants.CustomerOpportunityTable.ID).controls.links.get(rowNumber).click();
            NavigationPage.setActionAndGo("Update");
            new Button(By.xpath("//a[contains(@id, 'POLICYRemoveAssociationAction')]")).click();
            return this;
        }

        public AbstractAction perform(int rowNumber) {
            start(rowNumber);
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td) instead.");
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            OpportunityActionTab.buttonDone.click();
            OpportunityActionTab.buttonDone.click();
            return this;
        }
    }

    public static class StartNewQuoteInOpportunity extends AbstractAction {
        @Override
        public String getName() {
            return "Start new quote in Opportunity";
        }

        @Override
        public Workspace getView() {
            return new OpportunityView();
        }

        @Override
        public AbstractAction start() {
            OpportunityActionTab.buttonCreateNewOpportunity.click();
            OpportunityActionTab.buttonAddOpportunity.click();
            return this;
        }

        @Override
        public AbstractAction perform(TestData td) {
            start();
            getView().fill(td);
            new Button(By.id("editOpportunity:QUOTEstartBtn")).click();
            Page.dialogConfirmation.buttonYes.click();
            return submit();
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class StartNewQuoteInOpportunityUpdate extends AbstractAction {
        @Override
        public String getName() {
            return "Start new quote in Opportunity update";
        }

        @Override
        public Workspace getView() {
            return new OpportunityView();
        }

        public AbstractAction start(int rowNumber) {
            OpportunityActionTab.tableOpportunity.getRow(rowNumber).getCell(CustomerConstants.CustomerOpportunityTable.ID).controls.links.get(rowNumber).click();
            NavigationPage.setActionAndGo("Update");
            new Button(By.id("editOpportunity:QUOTEstartBtn")).click();
            Page.dialogConfirmation.buttonYes.click();
            return this;
        }

        public AbstractAction perform(int rowNumber) {
            start(rowNumber);
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td) instead.");
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class StartNewQuoteInOpportunityPreview extends AbstractAction {
        @Override
        public String getName() {
            return "Start new quote in Opportunity preview";
        }

        @Override
        public Workspace getView() {
            return new OpportunityView();
        }

        public AbstractAction start(int rowNumber) {
            OpportunityActionTab.tableOpportunity.getRow(rowNumber).getCell(CustomerConstants.CustomerOpportunityTable.ID).controls.links.get(rowNumber).click();
            new Button(By.id("quoteStartForm:QUOTEstartBtn")).click();
            return this;
        }

        public AbstractAction perform(int rowNumber) {
            start(rowNumber);
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td) instead.");
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            return this;
        }
    }

    public static class AddAssociateCampaignOnOpportunity extends AbstractAction {
        @Override
        public String getName() {
            return "Add Associate Campaign On Opportunity";
        }

        @Override
        public Workspace getView() {
            return new OpportunityView();
        }

        @Override
        public AbstractAction start() {
            OpportunityActionTab.buttonCreateNewOpportunity.click();
            OpportunityActionTab.buttonAddOpportunity.click();
            return this;
        }

        public AbstractAction perform(TestData td, String id) {
            start();
            getView().fill(td);
            new TextBox(By.id("editOpportunity:campaignAssignId")).setValue(id);
            new Button(By.id("editOpportunity:associateById")).click();
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, String id) instead.");
        }

        @Override
        public AbstractAction submit() {
            OpportunityActionTab.buttonDone.click();
            OpportunityActionTab.buttonDone.click();
            return this;
        }
    }

    public static class RemoveAssociateCampaignOnOpportunity extends AbstractAction {
        @Override
        public String getName() {
            return "Remove Associate Campaign On Opportunity";
        }

        @Override
        public Workspace getView() {
            return new OpportunityView();
        }

        public AbstractAction start(int rowNumber) {
            OpportunityActionTab.tableOpportunity.getRow(rowNumber).getCell(CustomerConstants.CustomerOpportunityTable.ID).controls.links.get(rowNumber).click();
            NavigationPage.setActionAndGo("Update");
            new Button(By.id("editOpportunity:removeCampaignButton")).click();
            return this;
        }

        public AbstractAction perform(int rowNumber) {
            start(rowNumber);
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td) instead.");
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            OpportunityActionTab.buttonDone.click();
            OpportunityActionTab.buttonDone.click();
            return this;
        }
    }

    public static class RemoveGroup extends AbstractAction {
        @Override
        public String getName() {
            return "Update";
        }

        @Override
        public Workspace getView() {
            return new DefaultView();
        }

        public AbstractAction perform() {
            start();
            new Button(By.xpath("//a[.= 'Remove This Group Info']")).click();
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform() instead.");
        }

        @Override
        public AbstractAction submit() {
            RelationshipTab.buttonSaveAndExit.click();
            return this;
        }
    }

    public static class AssociateDivisions extends AbstractAction {

        @Override
        public String getName() {
            return "Update";
        }

        @Override
        public Workspace getView() {
            return new DefaultView();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(boolean) instead.");
        }

        public AbstractAction perform(boolean value) {
            start();
            new CheckBox(By.id("crmForm:generalInfoLeft_associateDivisions")).setValue(value);

            if (Page.dialogConfirmation.isPresent()) {
                Page.dialogConfirmation.confirm();
            }
            return submit();
        }

        @Override
        public AbstractAction submit() {
            RelationshipTab.buttonSaveAndExit.click();
            return this;
        }
    }

    public static class InitiateRenewalEntry extends AbstractAction {

        @Override
        public String getName() {
            return "Initiate Renewal Entry";
        }

        @Override
        public Workspace getView() {
            return new InitiateRenewalEntryView();
        }

        @Override
        public AbstractAction submit() {
            Arrays.asList(Tab.buttonGo,Tab.buttonOk).forEach(Button::click);
            Page.dialogConfirmation.confirm();
            log.info("{} action has been finished.", getName());
            return this;
        }
    }

}
