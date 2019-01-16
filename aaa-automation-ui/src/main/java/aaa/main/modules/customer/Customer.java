/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.etcsa.base.config.CustomTestProperties;
import aaa.common.Workspace;
import aaa.common.pages.MainPage;
import aaa.common.pages.SearchPage;
import aaa.main.modules.customer.views.DefaultView;
import aaa.utils.EntityLogger;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;

public class Customer implements ICustomer {

    protected static Logger log = LoggerFactory.getLogger(Customer.class);

    private Workspace defaultView = new DefaultView();
    private static boolean isRestCustomerEnabled = Boolean.valueOf(PropertyProvider.getProperty(CustomTestProperties.REST_CUSTOMER_ENABLED, "false"));

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void create(TestData td) {
//        if (isRestCustomerEnabled) {
//            try {
//                createViaREST(td);
//            } catch (IstfException e) {
//	            log.info("REST customer creation failed: {}", e);
//                createViaUI(td);
//            }
//        } else {
            createViaUI(td);
//        }
    }

    @Override
    public void createViaUI(TestData td) {
        if (!SearchPage.buttonSearch.isPresent()) {
            MainPage.QuickSearch.buttonSearchPlus.click();
        }
        SearchPage.buttonCreateCustomer.click();
        getDefaultView().fill(td);
	    log.info("Created {}", EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));
    }

    @Override
    public CustomerActions.AddCommunication addCommunication() {
        return new CustomerActions.AddCommunication();
    }

    @Override
    public CustomerActions.RemoveCommunication removeCommunication() {
        return new CustomerActions.RemoveCommunication();
    }

    @Override
    public CustomerActions.UpdateCommunication updateCommunication() {
        return new CustomerActions.UpdateCommunication();
    }

    @Override
    public CustomerActions.AddCommunicationThread addCommunicationThread() {
        return new CustomerActions.AddCommunicationThread();
    }

    @Override
    public CustomerActions.DeleteCustomer deleteCustomer() {
        return new CustomerActions.DeleteCustomer();
    }

    @Override
    public CustomerActions.AddOpportunity addOpportunity() {
        return new CustomerActions.AddOpportunity();
    }

    @Override
    public CustomerActions.RemoveOpportunity removeOpportunity() {
        return new CustomerActions.RemoveOpportunity();
    }

    @Override
    public CustomerActions.UpdateOpportunity updateOpportunity() {
        return new CustomerActions.UpdateOpportunity();
    }

    @Override
    public CustomerActions.Update update() {
        return new CustomerActions.Update();
    }

    @Override
    public CustomerActions.RemoveRelationshipContact removeRelationshipContact() {
        return new CustomerActions.RemoveRelationshipContact();
    }

    @Override
    public CustomerActions.AddRelationshipContact addRelationshipContact() {
        return new CustomerActions.AddRelationshipContact();
    }

    @Override
    public CustomerActions.UpdateRelationshipContact updateRelationshipContact() {
        return new CustomerActions.UpdateRelationshipContact();
    }

    @Override
    public CustomerActions.ScheduledUpdate scheduledUpdate() {
        return new CustomerActions.ScheduledUpdate();
    }

    @Override
    public CustomerActions.Inquiry inquiry() {
        return new CustomerActions.Inquiry();
    }

    @Override
    public CustomerActions.DeletePendingUpdates deletePendingUpdates() {
        return new CustomerActions.DeletePendingUpdates();
    }

    @Override
    public CustomerActions.AddNewContactsDetails addNewContactsDetails() {
        return new CustomerActions.AddNewContactsDetails();
    }

    @Override
    public CustomerActions.RemoveNewContactsDetails removeNewContactsDetails() {
        return new CustomerActions.RemoveNewContactsDetails();
    }

    @Override
    public CustomerActions.AddNewRelationshipContacts addNewRelationshipContacts() {
        return new CustomerActions.AddNewRelationshipContacts();
    }

    @Override
    public CustomerActions.RemoveNewRelationshipContacts removeNewRelationshipContacts() {
        return new CustomerActions.RemoveNewRelationshipContacts();
    }

    @Override
    public CustomerActions.RemoveBusinessEntity removeBusinessEntity() {
        return new CustomerActions.RemoveBusinessEntity();
    }

    @Override
    public CustomerActions.AddAgency addAgency() {
        return new CustomerActions.AddAgency();
    }

    @Override
    public CustomerActions.RemoveAgency removeAgency() {
        return new CustomerActions.RemoveAgency();
    }

    @Override
    public CustomerActions.AddParticipant addParticipant() {
        return new CustomerActions.AddParticipant();
    }

    @Override
    public CustomerActions.RemoveParticipantEmployment removeParticipantEmployment() {
        return new CustomerActions.RemoveParticipantEmployment();
    }

    @Override
    public CustomerActions.RemoveParticipantMembership removeParticipantMembership() {
        return new CustomerActions.RemoveParticipantMembership();
    }

    @Override
    public CustomerActions.RemoveParticipantStudent removeParticipantStudent() {
        return new CustomerActions.RemoveParticipantStudent();
    }

    @Override
    public CustomerActions.UpdateParticipantEmployment updateParticipantEmployment() {
        return new CustomerActions.UpdateParticipantEmployment();
    }

    @Override
    public CustomerActions.UpdateParticipantMembership updateParticipantMembership() {
        return new CustomerActions.UpdateParticipantMembership();
    }

    @Override
    public CustomerActions.UpdateParticipantStudent updateParticipantStudent() {
        return new CustomerActions.UpdateParticipantStudent();
    }

    @Override
    public CustomerActions.AssociateExistingCustomer associateExistingCustomer() {
        return new CustomerActions.AssociateExistingCustomer();
    }

    @Override
    public CustomerActions.RemoveNewProductDetails removeNewProductDetails() {
        return new CustomerActions.RemoveNewProductDetails();
    }

    @Override
    public CustomerActions.Qualify qualify() {
        return new CustomerActions.Qualify();
    }

    @Override
    public CustomerActions.MakeInvalid makeInvalid() {
        return new CustomerActions.MakeInvalid();
    }

    @Override
    public CustomerActions.UndoInvalid undoInvalid() {
        return new CustomerActions.UndoInvalid();
    }

    @Override
    public CustomerActions.RemoveDivisions removeDivisions() {
        return new CustomerActions.RemoveDivisions();
    }

    @Override
    public CustomerActions.ViewHistory viewHistory() {
        return new CustomerActions.ViewHistory();
    }

    @Override
    public CustomerActions.UpdateContactsDetails updateContactsDetails() {
        return new CustomerActions.UpdateContactsDetails();
    }

    @Override
    public CustomerActions.AddAssociateQuoteOnOpportunity addAssociateQuoteOnOpportunity() {
        return new CustomerActions.AddAssociateQuoteOnOpportunity();
    }

    @Override
    public CustomerActions.RemoveAssociateQuoteOnOpportunity removeAssociateQuoteOnOpportunity() {
        return new CustomerActions.RemoveAssociateQuoteOnOpportunity();
    }

    @Override
    public CustomerActions.AddAssociatePolicyOnOpportunity addAssociatePolicyOnOpportunity() {
        return new CustomerActions.AddAssociatePolicyOnOpportunity();
    }

    @Override
    public CustomerActions.RemoveAssociatePolicyOnOpportunity removeAssociatePolicyOnOpportunity() {
        return new CustomerActions.RemoveAssociatePolicyOnOpportunity();
    }

    @Override
    public CustomerActions.StartNewQuoteInOpportunity startNewQuoteInOpportunity() {
        return new CustomerActions.StartNewQuoteInOpportunity();
    }

    @Override
    public CustomerActions.StartNewQuoteInOpportunityUpdate startNewQuoteInOpportunityUpdate() {
        return new CustomerActions.StartNewQuoteInOpportunityUpdate();
    }

    @Override
    public CustomerActions.StartNewQuoteInOpportunityPreview startNewQuoteInOpportunityPreview() {
        return new CustomerActions.StartNewQuoteInOpportunityPreview();
    }

    @Override
    public CustomerActions.AddAssociateCampaignOnOpportunity addAssociateCampaignOnOpportunity() {
        return new CustomerActions.AddAssociateCampaignOnOpportunity();
    }

    @Override
    public CustomerActions.RemoveAssociateCampaignOnOpportunity removeAssociateCampaignOnOpportunity() {
        return new CustomerActions.RemoveAssociateCampaignOnOpportunity();
    }

    @Override
    public CustomerActions.RemoveGroup removeGroup() {
        return new CustomerActions.RemoveGroup();
    }

    @Override
    public CustomerActions.AssociateDivisions associateDivisions() {
        return new CustomerActions.AssociateDivisions();
    }

    @Override
    public CustomerActions.MergeCustomer mergeCustomer() {
	    return new CustomerActions.MergeCustomer();
    }

    @Override
    public CustomerActions.AddCustomerAdditionalNames addCustomerAdditionalNames() {
	    return new CustomerActions.AddCustomerAdditionalNames();
    }

    @Override
    public CustomerActions.InitiateRenewalEntry initiateRenewalEntry() {
	    return new CustomerActions.InitiateRenewalEntry();
    }
}
