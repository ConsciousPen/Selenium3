/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer.defaulttabs;

import org.openqa.selenium.By;

import aaa.common.DefaultTab;
import aaa.common.Tab;
import aaa.main.metadata.CustomerMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class GeneralTab extends DefaultTab {

    public static final ComboBox comboBoxSelectContactMethod = new ComboBox(By.id("crmForm:contactMethod:contactMethodSelect"));
    public static final Button buttonAddContact = new Button(By.id("crmForm:contactMethod:addContactEvent"));
    public static final Button buttonAddGroupInfo = new Button(By.id("crmForm:groupInfoMethod:addGroupInfoEvent"));
    public static final Button buttonAddAdditionalName = new Button(By.id("crmForm:additionalNameMethod:addAdditionalNameBtn"));
    public static final Button buttonAddProductOwned = new Button(By.id("crmForm:otherProductMethod:addProductOwnedEvent"));
    public static final Button buttonAssociateAccount = new Button(By.xpath("//a[.='Associate Account']"));
    public static final Button buttonAddMajorAccount = new Button(By.id("associateMajorAccount:addButton"));
    public static final Button buttonAddAllContacts = new Button(By.id("crmForm:contactActionsAddButton"));
    public static final Table table = new Table(By.id("crmForm:additionalNamesTable"));
    public static final Table tableContactDetails = new Table(By.xpath("//table[@id='crmForm:customerContactsTable']"));
    
    public GeneralTab() {
        super(CustomerMetaData.GeneralTab.class);
    }
/*
    @Override
    public Tab fillTab(TestData td) {
        for (TestData testData : td.getTestDataList(getMetaKey())) {
            String key = CustomerMetaData.GeneralTab.CONTACT_DETAILS_TYPE.getLabel();
            if (testData.containsKey(key)) {
                comboBoxSelectContactMethod.setValue(testData.getValue(key));
                buttonAddContact.click();
            }

            if (testData.containsKey(CustomerMetaData.GeneralTab.GROUP_SEARCH.getLabel())) {
                buttonAddGroupInfo.click();
            }

            if (testData.containsKey(CustomerMetaData.GeneralTab.ADDITIONAL_NAME_DETAILS_IND_SECTION.getLabel())) {
                for (TestData tdAdditionalName : testData.getTestDataList(CustomerMetaData.GeneralTab.ADDITIONAL_NAME_DETAILS_IND_SECTION.getLabel())) {
                    buttonAddAdditionalName.click();
                    assetList.getAsset(CustomerMetaData.GeneralTab.ADDITIONAL_NAME_DETAILS_IND_SECTION.getLabel(), AssetList.class).setValue(tdAdditionalName);
                    assetList.getAsset(CustomerMetaData.GeneralTab.ADDITIONAL_NAME_DETAILS_IND_SECTION.getLabel(), AssetList.class)
                            .getAsset(BUTTON_ADD_ALL.getLabel(), Button.class).click();
                }
                testData.mask(CustomerMetaData.GeneralTab.ADDITIONAL_NAME_DETAILS_IND_SECTION.getLabel());
            }

            if (testData.containsKey(CustomerMetaData.GeneralTab.POLICY_TYPE.getLabel())) {
                buttonAddProductOwned.click();
            }

            if (testData.containsKey(CustomerMetaData.GeneralTab.ACCOUNT_TYPE.getLabel())) {
                String accountType = CustomerMetaData.GeneralTab.ACCOUNT_TYPE.getLabel();
                String designation = CustomerMetaData.GeneralTab.ACCOUNT_DESIGNATION_TYPE.getLabel();
                assetList = new AssetList(By.id("associateMajorAccountPopup_container"), metaDataClass);
                assetList.config.applyConfiguration("AssociateAccount");
                buttonAssociateAccount.click();
                ((AssetList) getAssetList()).setValue(testData.ksam(accountType, designation));
                getAssetList().fill(testData.purgeAdjustments());
                buttonAddMajorAccount.click();
                assetList = new AssetList(By.xpath("//*"), metaDataClass);
            }

            ((AssetList) getAssetList()).setValue(testData.mask(key, "Account Type", "Account Designation Type"));
        }

        if (buttonAddAllContacts.isPresent() && buttonAddAllContacts.isVisible()) {
            buttonAddAllContacts.click();
        }

        return this;
    } */
    
    @Override
    public Tab fillTab(TestData td) {
      
    	for (TestData testData : td.getTestDataList(getMetaKey())) {
    		
    		String key = CustomerMetaData.GeneralTab.CONTACT_DETAILS_TYPE.getLabel();
            String keyAdd = "ADD Contact Details";
            String keyUpdate = "UPDATE Contact Details";
            
            if (testData.containsKey(keyAdd)) {
                for (TestData tdContact : testData.getTestDataList(keyAdd)) {
                    comboBoxSelectContactMethod.setValue(tdContact.getValue(key));
                    buttonAddContact.click();
                    ((AssetList) getAssetList()).setValue(tdContact.mask(key));
                }
            }
            if (testData.containsKey(keyUpdate)) {
                String keyRow = "ROW KEY";
                for (TestData tdContact : testData.getTestDataList(keyUpdate)) {
                	tableContactDetails.getRowContains("Contact Details", tdContact.getValue(keyRow)).getCell("Action").controls.buttons.get("Change").click();
                    ((AssetList) getAssetList()).setValue(tdContact);
                }
            }  

            ((AssetList) getAssetList()).setValue(testData.mask(key));
        }

        if (buttonAddAllContacts.isPresent() && buttonAddAllContacts.isVisible()) {
            buttonAddAllContacts.click();
        }

        return this;
    }

    @Override
    public Tab submitTab() {
        if (buttonDone.isPresent()) {
            buttonDone.click();
        } else {
            buttonNext.click();
        }
        return this;
    }
}
