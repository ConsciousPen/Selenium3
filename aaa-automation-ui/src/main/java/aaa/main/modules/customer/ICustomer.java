/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer;

import aaa.main.modules.customer.CustomerActions;

import aaa.common.Workspace;
import toolkit.datax.TestData;

public interface ICustomer {
	void create(TestData td);

    void createViaUI(TestData td);

    void createViaREST(TestData td);

    Workspace getDefaultView();

    CustomerActions.AddCommunication addCommunication();

    CustomerActions.AddCommunicationThread addCommunicationThread();

    CustomerActions.RemoveCommunication removeCommunication();

    CustomerActions.UpdateCommunication updateCommunication();

    CustomerActions.AddOpportunity addOpportunity();

    CustomerActions.UpdateOpportunity updateOpportunity();

    CustomerActions.Update update();

    CustomerActions.RemoveRelationshipContact removeRelationshipContact();

    CustomerActions.AddRelationshipContact addRelationshipContact();

    CustomerActions.RemoveOpportunity removeOpportunity();

    CustomerActions.DeleteCustomer deleteCustomer();

    CustomerActions.UpdateRelationshipContact updateRelationshipContact();

    CustomerActions.ScheduledUpdate scheduledUpdate();

    CustomerActions.Inquiry inquiry();

    CustomerActions.DeletePendingUpdates deletePendingUpdates();

    CustomerActions.AddNewContactsDetails addNewContactsDetails();

    CustomerActions.RemoveNewContactsDetails removeNewContactsDetails();

    CustomerActions.AddNewRelationshipContacts addNewRelationshipContacts();

    CustomerActions.RemoveNewRelationshipContacts removeNewRelationshipContacts();

    CustomerActions.RemoveBusinessEntity removeBusinessEntity();

    CustomerActions.AddAgency addAgency();

    CustomerActions.RemoveAgency removeAgency();

    CustomerActions.AddParticipant addParticipant();

    CustomerActions.UpdateParticipantEmployment updateParticipantEmployment();

    CustomerActions.UpdateParticipantMembership updateParticipantMembership();

    CustomerActions.UpdateParticipantStudent updateParticipantStudent();

    CustomerActions.RemoveParticipantEmployment removeParticipantEmployment();

    CustomerActions.RemoveParticipantMembership removeParticipantMembership();

    CustomerActions.RemoveParticipantStudent removeParticipantStudent();

    CustomerActions.AssociateExistingCustomer associateExistingCustomer();

    CustomerActions.RemoveNewProductDetails removeNewProductDetails();

    CustomerActions.Qualify qualify();

    CustomerActions.MakeInvalid makeInvalid();

    CustomerActions.UndoInvalid undoInvalid();

    CustomerActions.RemoveDivisions removeDivisions();

    CustomerActions.ViewHistory viewHistory();

    CustomerActions.UpdateContactsDetails updateContactsDetails();

    CustomerActions.AddAssociateQuoteOnOpportunity addAssociateQuoteOnOpportunity();

    CustomerActions.RemoveAssociateQuoteOnOpportunity removeAssociateQuoteOnOpportunity();

    CustomerActions.AddAssociatePolicyOnOpportunity addAssociatePolicyOnOpportunity();

    CustomerActions.RemoveAssociatePolicyOnOpportunity removeAssociatePolicyOnOpportunity();

    CustomerActions.StartNewQuoteInOpportunity startNewQuoteInOpportunity();

    CustomerActions.StartNewQuoteInOpportunityUpdate startNewQuoteInOpportunityUpdate();

    CustomerActions.StartNewQuoteInOpportunityPreview startNewQuoteInOpportunityPreview();

    CustomerActions.AddAssociateCampaignOnOpportunity addAssociateCampaignOnOpportunity();

    CustomerActions.RemoveAssociateCampaignOnOpportunity removeAssociateCampaignOnOpportunity();

    CustomerActions.RemoveGroup removeGroup();

    CustomerActions.AssociateDivisions associateDivisions();

    CustomerActions.MergeCustomer mergeCustomer();

    CustomerActions.AddCustomerAdditionalNames addCustomerAdditionalNames();

    CustomerActions.InitiateRenewalEntry initiateRenewalEntry();
}
