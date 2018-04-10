/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.customer;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.etcsa.base.config.CustomTestProperties;
import aaa.common.Workspace;
import aaa.common.pages.MainPage;
import aaa.common.pages.SearchPage;
import aaa.main.metadata.CustomerMetaData.GeneralTab;
import aaa.main.modules.customer.CustomerActions.AddAgency;
import aaa.main.modules.customer.CustomerActions.AddAssociateCampaignOnOpportunity;
import aaa.main.modules.customer.CustomerActions.AddAssociatePolicyOnOpportunity;
import aaa.main.modules.customer.CustomerActions.AddAssociateQuoteOnOpportunity;
import aaa.main.modules.customer.CustomerActions.AddCommunication;
import aaa.main.modules.customer.CustomerActions.AddCommunicationThread;
import aaa.main.modules.customer.CustomerActions.AddCustomerAdditionalNames;
import aaa.main.modules.customer.CustomerActions.AddNewContactsDetails;
import aaa.main.modules.customer.CustomerActions.AddNewRelationshipContacts;
import aaa.main.modules.customer.CustomerActions.AddOpportunity;
import aaa.main.modules.customer.CustomerActions.AddParticipant;
import aaa.main.modules.customer.CustomerActions.AddRelationshipContact;
import aaa.main.modules.customer.CustomerActions.AssociateDivisions;
import aaa.main.modules.customer.CustomerActions.AssociateExistingCustomer;
import aaa.main.modules.customer.CustomerActions.DeleteCustomer;
import aaa.main.modules.customer.CustomerActions.DeletePendingUpdates;
import aaa.main.modules.customer.CustomerActions.InitiateRenewalEntry;
import aaa.main.modules.customer.CustomerActions.Inquiry;
import aaa.main.modules.customer.CustomerActions.MakeInvalid;
import aaa.main.modules.customer.CustomerActions.MergeCustomer;
import aaa.main.modules.customer.CustomerActions.Qualify;
import aaa.main.modules.customer.CustomerActions.RemoveAgency;
import aaa.main.modules.customer.CustomerActions.RemoveAssociateCampaignOnOpportunity;
import aaa.main.modules.customer.CustomerActions.RemoveAssociatePolicyOnOpportunity;
import aaa.main.modules.customer.CustomerActions.RemoveAssociateQuoteOnOpportunity;
import aaa.main.modules.customer.CustomerActions.RemoveBusinessEntity;
import aaa.main.modules.customer.CustomerActions.RemoveCommunication;
import aaa.main.modules.customer.CustomerActions.RemoveDivisions;
import aaa.main.modules.customer.CustomerActions.RemoveGroup;
import aaa.main.modules.customer.CustomerActions.RemoveNewContactsDetails;
import aaa.main.modules.customer.CustomerActions.RemoveNewProductDetails;
import aaa.main.modules.customer.CustomerActions.RemoveNewRelationshipContacts;
import aaa.main.modules.customer.CustomerActions.RemoveOpportunity;
import aaa.main.modules.customer.CustomerActions.RemoveParticipantEmployment;
import aaa.main.modules.customer.CustomerActions.RemoveParticipantMembership;
import aaa.main.modules.customer.CustomerActions.RemoveParticipantStudent;
import aaa.main.modules.customer.CustomerActions.RemoveRelationshipContact;
import aaa.main.modules.customer.CustomerActions.ScheduledUpdate;
import aaa.main.modules.customer.CustomerActions.StartNewQuoteInOpportunity;
import aaa.main.modules.customer.CustomerActions.StartNewQuoteInOpportunityPreview;
import aaa.main.modules.customer.CustomerActions.StartNewQuoteInOpportunityUpdate;
import aaa.main.modules.customer.CustomerActions.UndoInvalid;
import aaa.main.modules.customer.CustomerActions.Update;
import aaa.main.modules.customer.CustomerActions.UpdateCommunication;
import aaa.main.modules.customer.CustomerActions.UpdateContactsDetails;
import aaa.main.modules.customer.CustomerActions.UpdateOpportunity;
import aaa.main.modules.customer.CustomerActions.UpdateParticipantEmployment;
import aaa.main.modules.customer.CustomerActions.UpdateParticipantMembership;
import aaa.main.modules.customer.CustomerActions.UpdateParticipantStudent;
import aaa.main.modules.customer.CustomerActions.UpdateRelationshipContact;
import aaa.main.modules.customer.CustomerActions.ViewHistory;
import aaa.main.modules.customer.views.DefaultView;
import aaa.rest.customer.CustomerCoreRESTMethods;
import aaa.utils.EntityLogger;
import aaa.utils.EntityLogger.EntityType;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.rest.ResponseWrapper;

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
        if (isRestCustomerEnabled) {
            try {
                createViaREST(td);
            } catch (IstfException e) {
                log.info("REST customer creation failed: {}", e);
                createViaUI(td);
            }
        } else {
            createViaUI(td);
        }
    }

    @Override
    public void createViaUI(TestData td) {
        MainPage.QuickSearch.buttonSearchPlus.click();
        SearchPage.buttonCreateCustomer.click();
        getDefaultView().fill(td);
        log.info("Created {}", EntityLogger.getEntityHeader(EntityType.CUSTOMER));
    }

    @Override
    public void createViaREST(TestData td) {
        ResponseWrapper response;
        try {
            CustomerCoreRESTMethods restCustomer = new CustomerCoreRESTMethods();

            if (td.getTestData(GeneralTab.class.getSimpleName()).containsKey(GeneralTab.NON_INDIVIDUAL_TYPE.getLabel())) {
                response = restCustomer.postCustomersNonIndividual(td.resolveLinks());
            } else {
                response = restCustomer.postCustomersIndividual(td.resolveLinks());
            }

            JSONObject object = (JSONObject) JSONValue.parse(response.getResponse().readEntity(String.class));
            MainPage.QuickSearch.search(object.get("customerNumber").toString());
            log.info("Created {}", EntityLogger.getEntityHeader(EntityType.CUSTOMER));
        } catch (RuntimeException e) {
            throw new IstfException(e);
        }
    }

    @Override
    public AddCommunication addCommunication() {
        return new CustomerActions.AddCommunication();
    }

    @Override
    public RemoveCommunication removeCommunication() {
        return new CustomerActions.RemoveCommunication();
    }

    @Override
    public UpdateCommunication updateCommunication() {
        return new CustomerActions.UpdateCommunication();
    }

    @Override
    public AddCommunicationThread addCommunicationThread() {
        return new CustomerActions.AddCommunicationThread();
    }

    @Override
    public DeleteCustomer deleteCustomer() {
        return new CustomerActions.DeleteCustomer();
    }

    @Override
    public AddOpportunity addOpportunity() {
        return new CustomerActions.AddOpportunity();
    }

    @Override
    public RemoveOpportunity removeOpportunity() {
        return new CustomerActions.RemoveOpportunity();
    }

    @Override
    public UpdateOpportunity updateOpportunity() {
        return new CustomerActions.UpdateOpportunity();
    }

    @Override
    public Update update() {
        return new CustomerActions.Update();
    }

    @Override
    public RemoveRelationshipContact removeRelationshipContact() {
        return new CustomerActions.RemoveRelationshipContact();
    }

    @Override
    public AddRelationshipContact addRelationshipContact() {
        return new CustomerActions.AddRelationshipContact();
    }

    @Override
    public UpdateRelationshipContact updateRelationshipContact() {
        return new CustomerActions.UpdateRelationshipContact();
    }

    @Override
    public ScheduledUpdate scheduledUpdate() {
        return new CustomerActions.ScheduledUpdate();
    }

    @Override
    public Inquiry inquiry() {
        return new CustomerActions.Inquiry();
    }

    @Override
    public DeletePendingUpdates deletePendingUpdates() {
        return new CustomerActions.DeletePendingUpdates();
    }

    @Override
    public AddNewContactsDetails addNewContactsDetails() {
        return new CustomerActions.AddNewContactsDetails();
    }

    @Override
    public RemoveNewContactsDetails removeNewContactsDetails() {
        return new CustomerActions.RemoveNewContactsDetails();
    }

    @Override
    public AddNewRelationshipContacts addNewRelationshipContacts() {
        return new CustomerActions.AddNewRelationshipContacts();
    }

    @Override
    public RemoveNewRelationshipContacts removeNewRelationshipContacts() {
        return new CustomerActions.RemoveNewRelationshipContacts();
    }

    @Override
    public RemoveBusinessEntity removeBusinessEntity() {
        return new CustomerActions.RemoveBusinessEntity();
    }

    @Override
    public AddAgency addAgency() {
        return new CustomerActions.AddAgency();
    }

    @Override
    public RemoveAgency removeAgency() {
        return new CustomerActions.RemoveAgency();
    }

    @Override
    public AddParticipant addParticipant() {
        return new CustomerActions.AddParticipant();
    }

    @Override
    public RemoveParticipantEmployment removeParticipantEmployment() {
        return new CustomerActions.RemoveParticipantEmployment();
    }

    @Override
    public RemoveParticipantMembership removeParticipantMembership() {
        return new CustomerActions.RemoveParticipantMembership();
    }

    @Override
    public RemoveParticipantStudent removeParticipantStudent() {
        return new CustomerActions.RemoveParticipantStudent();
    }

    @Override
    public UpdateParticipantEmployment updateParticipantEmployment() {
        return new CustomerActions.UpdateParticipantEmployment();
    }

    @Override
    public UpdateParticipantMembership updateParticipantMembership() {
        return new CustomerActions.UpdateParticipantMembership();
    }

    @Override
    public UpdateParticipantStudent updateParticipantStudent() {
        return new CustomerActions.UpdateParticipantStudent();
    }

    @Override
    public AssociateExistingCustomer associateExistingCustomer() {
        return new CustomerActions.AssociateExistingCustomer();
    }

    @Override
    public RemoveNewProductDetails removeNewProductDetails() {
        return new CustomerActions.RemoveNewProductDetails();
    }

    @Override
    public Qualify qualify() {
        return new CustomerActions.Qualify();
    }

    @Override
    public MakeInvalid makeInvalid() {
        return new CustomerActions.MakeInvalid();
    }

    @Override
    public UndoInvalid undoInvalid() {
        return new CustomerActions.UndoInvalid();
    }

    @Override
    public RemoveDivisions removeDivisions() {
        return new CustomerActions.RemoveDivisions();
    }

    @Override
    public ViewHistory viewHistory() {
        return new CustomerActions.ViewHistory();
    }

    @Override
    public UpdateContactsDetails updateContactsDetails() {
        return new CustomerActions.UpdateContactsDetails();
    }

    @Override
    public AddAssociateQuoteOnOpportunity addAssociateQuoteOnOpportunity() {
        return new CustomerActions.AddAssociateQuoteOnOpportunity();
    }

    @Override
    public RemoveAssociateQuoteOnOpportunity removeAssociateQuoteOnOpportunity() {
        return new CustomerActions.RemoveAssociateQuoteOnOpportunity();
    }

    @Override
    public AddAssociatePolicyOnOpportunity addAssociatePolicyOnOpportunity() {
        return new CustomerActions.AddAssociatePolicyOnOpportunity();
    }

    @Override
    public RemoveAssociatePolicyOnOpportunity removeAssociatePolicyOnOpportunity() {
        return new CustomerActions.RemoveAssociatePolicyOnOpportunity();
    }

    @Override
    public StartNewQuoteInOpportunity startNewQuoteInOpportunity() {
        return new CustomerActions.StartNewQuoteInOpportunity();
    }

    @Override
    public StartNewQuoteInOpportunityUpdate startNewQuoteInOpportunityUpdate() {
        return new CustomerActions.StartNewQuoteInOpportunityUpdate();
    }

    @Override
    public StartNewQuoteInOpportunityPreview startNewQuoteInOpportunityPreview() {
        return new CustomerActions.StartNewQuoteInOpportunityPreview();
    }

    @Override
    public AddAssociateCampaignOnOpportunity addAssociateCampaignOnOpportunity() {
        return new CustomerActions.AddAssociateCampaignOnOpportunity();
    }

    @Override
    public RemoveAssociateCampaignOnOpportunity removeAssociateCampaignOnOpportunity() {
        return new CustomerActions.RemoveAssociateCampaignOnOpportunity();
    }

    @Override
    public RemoveGroup removeGroup() {
        return new CustomerActions.RemoveGroup();
    }

    @Override
    public AssociateDivisions associateDivisions() {
        return new CustomerActions.AssociateDivisions();
    }

    @Override
    public MergeCustomer mergeCustomer() {
        return new MergeCustomer();
    }

    @Override
    public AddCustomerAdditionalNames addCustomerAdditionalNames() {
        return new AddCustomerAdditionalNames();
    }

    @Override
    public InitiateRenewalEntry initiateRenewalEntry() {
        return new InitiateRenewalEntry();
    }
}
