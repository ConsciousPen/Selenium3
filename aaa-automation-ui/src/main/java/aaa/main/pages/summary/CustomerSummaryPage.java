/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.pages.summary;

import org.openqa.selenium.By;

import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;

public class CustomerSummaryPage extends SummaryPage {

    public static StaticElement labelCustomerNumber = new StaticElement(By.xpath("//span[@id='custInfoForm:customerId' or @id='customerId']"));
    public static StaticElement labelCustomerName =
            new StaticElement(By.xpath("//span[@id='custInfoForm:indvName' or @id='oppInfoForm:name' or @id='custInfoForm:legalName' or @id='custInfoForm:name']"));
    public static StaticElement labelCustomerAddress = new StaticElement(By.xpath("//span[@id='custInfoForm:address' or @id='oppInfoForm:address']"));
    public static StaticElement labelCustomerPhone = new StaticElement(By.xpath("//span[contains(@id,'phone')]"));
    public static StaticElement labelCustomerLeadSource = new StaticElement(By.id("custInfoForm:source"));
    public static StaticElement labelCustomerDOB = new StaticElement(By.id("custInfoForm:birthdate"));
    public static StaticElement labelCustomerLeadStatus = new StaticElement(By.id("custInfoForm:status"));
    public static StaticElement lableCustomerType = new StaticElement(By.id("custInfoForm:customerType"));
    public static StaticElement labelMembershipInformationExpanded = new StaticElement(By.id("crmForm:participantHeaderContentExpandedMembershipInfo_0"));
    public static StaticElement labelMembershipInformationCollapsed = new StaticElement(By.id("crmForm:participantHeaderContentCollapsedMembershipInfo_0"));
    public static StaticElement labelStudentInformationExpanded = new StaticElement(By.id("crmForm:participantHeaderContentExpandedStudentInfo_0"));
    public static StaticElement labelStudentInformationCollapsed = new StaticElement(By.id("crmForm:participantHeaderContentCollapsedStudentInfo_0"));
    public static StaticElement labelAccountName = new StaticElement(By.id("acctInfoForm:name"));
    public static StaticElement labelAccountNumber = new StaticElement(By.id("acctInfoForm:accountNo"));
    public static StaticElement labelConfidential = new StaticElement(By.id("acctInfoForm:confidential"));
    public static StaticElement labelMergedFrom = new StaticElement(By.id("custInfoForm:mergedFromNavigationLinks"));
    public static StaticElement labelMergedInto = new StaticElement(By.id("custInfoForm:mergeToNavigationLinks"));
    public static StaticElement labelMergedPolicy = new StaticElement(By.id("act-policies:body_act-policyList:1:act-selectPolicy"));
    public static StaticElement labelMergedQuote = new StaticElement(By.id("quotes:body_quoteList:1:selectPolicy"));

    public static Link linkAccountTab = new Link(By.xpath("//span[.='Account']"));
    public static Link linkAddNewRelationshipContact = new Link(By.xpath("//a[.='Add New Relationship Contact']"));
    public static Link linkCommunicationTab = new Link(By.xpath("//span[.='Communication']"));
    public static Link linkCreateNewClaimWithoutPolicy = new Link(By.id("claims:addNewClaimNoPolicy"));
    public static Link linkCustomerInfoTab = new Link(By.xpath("//span[.='Customer Info']"));
    public static Link linkEditThisRelationship = new Link(By.xpath("//a[.='Edit This Relationship']"));
    public static Link linkOpportunityTab = new Link(By.xpath("//span[.='Opportunity']"));
    public static Link linkPendingUpdatesPanel = new Link(By.xpath("//div[@id='pendingUpdatesForm:pendingUpdatesTogglePanel:header']"));
    public static Link linkRelatedCommunications = new Link(By.xpath("//div[@id='communicationsSwitchPanel:header']//td[2]"));
    public static Link linkRemoveThisBusinessEntity = new Link(By.xpath("//a[.='Remove This Business Entity']"));
    public static Link linkRemoveThisDivision = new Link(By.xpath("//a[.='Remove This Division']"));
    public static Link linkRemoveThisRelationship = new Link(By.xpath("//a[.='Remove This Relationship']"));

    public static Button buttonAddAffinityGroup = new Button(By.xpath("//a[.='Add Affinity Group']"));
    public static Button buttonAddAgency = new Button(By.xpath("//a[.='Add Agency']"));
    public static Button buttonAddContact = new Button(By.xpath("//a[.='Add Contact']"));
    public static Button buttonAddCustomer = new Button(By.xpath("//a[.='Add Customer']"));
    public static Button buttonAddNewContactsDetails = new Button(By.xpath("//a[.='Add New Contacts Details']"));
    public static Button buttonAddQuote = new Button(By.id("quotes:addPolicyBtn"));
    public static Button buttonAgencies = new Button(By.xpath("//div[.='Agencies'][2]"));
    public static Button buttonContactRelationship = new Button(By.xpath("//a[.='Contacts & Relationships']"));
    public static Button buttonEditRelationship = new Button(By.xpath("//a[.='Edit This Relationship']"));
    public static Button buttonOkKeepHistory = new Button(By.xpath("//input[contains(@id, 'crmForm:relationshipAssociationRemovalConfirmationPopup_btnOK_keepHistory')]"));
    public static Button buttonRemoveThisAccount = new Button(By.xpath("//a[.='Remove This Account']"));
    public static Button buttonStartNewCommunication = new Button(By.xpath("//a[.='Start New Communication']"));
    public static Button buttonTimeLine = new Button(By.xpath("//a[@id='crmForm:customerOverview_CUSTOMER' or @id='crmForm:customerContactsTable:0:history' "
            + "or @id ='crmForm:customerOverview_CUSTOMER_']"));
    public static Button buttonTasks = new Button(By.xpath("//*[contains(@id,'tasksList') and text()='Tasks']"));

    public static Table tableActivePolicies = new Table(By.id("act-policies:body_act-policyList"));
    public static Table tableAffinityGroups = new Table(By.id("associationForm:body_associationTbl"));
    public static Table tableAgencies = new Table(By.id("assignments:body_assignmentsTbl"));
    public static Table tableBundles = new Table(By.id("policyBundles:body_policyBundlesTable"));
    public static Table tableBusinessEntities = new Table(By.id("body_businessEntityList"));
    public static Table tableClaims = new Table(By.id("claims:customerClaimTbl"));
    public static Table tableCustomerContacts = new Table(By.id("crmForm:customerContactsTable"));
    public static Table tableCustomerInformation = new Table(By.xpath("//table[@id='custAssociations:body_currentCustomerToDisplay' or @id='custAssociations:body_customersToDisplay']"));
    public static Table tableCustomers = new Table(By.id("custAssociations:body_customersToDisplay"));
    public static Table tableDesignatedContacts = new Table(By.id("contactForm:body_contactsTbl"));
    public static Table tableDivisions = new Table(By.id("body_divisionTable"));
    public static Table tableEmploymentCensus = new Table(By.id("EMP_crmForm:censusDataTable"));
    public static Table tableGroup = new Table(By.id("crmForm:custGroupInfoListGroup"));
    public static Table tableInactivePolicies = new Table(By.id("inact-policies:body_inact-policyList"));
    public static Table tableMajorLargeAccount = new Table(By.id("crmForm:majorAccountPanel"));
    public static Table tableMembershipCensus = new Table(By.id("MEM_crmForm:censusDataTable"));
    public static Table tableNewProductDetails = new Table(By.id("crmForm:otherProductsTable"));
    public static Table tableQuotes = new Table(By.id("quotes:body_quoteList"));
    public static Table tableRelationshipPartySearchResult = new Table(By.id("crmPartyForm:partyList"));
    public static Table tableRelationshipResult = new Table(By.xpath("//div[@id='crmForm:newRelationshipsTogglePanel_0:header']//table"));
    public static Table tableStudentCensus = new Table(By.id("STU_crmForm:censusDataTable"));
    public static Table tableAddresslInfo = new Table(By.id("addressCompareTable"));
    public static Table tableGeneralInfo = new Table(By.id("indGeneralInfoCompareTable"));

    public static void expandRelatedCommunications() {
        Link collapsedRelatedCommunications = new Link(By.xpath("//div[text()='Related Communications' and contains(@class, 'colps')]"));
        if (collapsedRelatedCommunications.isVisible()) {
            collapsedRelatedCommunications.click();
        }
    }

    public static class BillingSection {
        private static Table tableBilling = new Table(By.id("customerBilling:customerBillingAccountTbl"));

        public static void open() {
            if (!tableBilling.isPresent()) {
                new Link(By.id("customerBilling:billingTogglePanel")).click();
            }
        }

        public static Table getTable() {
            open();
            return tableBilling;
        }
    }
}
