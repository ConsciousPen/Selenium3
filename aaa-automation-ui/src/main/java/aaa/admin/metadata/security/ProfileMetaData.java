/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.metadata.security;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.controls.AdvancedSelector;
import com.exigen.ipb.etcsa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.etcsa.controls.dialog.type.AbstractDialog;

import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public final class ProfileMetaData {

    public static final class GeneralProfileTab extends MetaData {
        public static final AttributeDescriptor CHANNEL = declare("Channel", StaticElement.class);
        public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
        public static final AttributeDescriptor MIDDLE_NAME = declare("Middle Name", TextBox.class);
        public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
        public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
        public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
        public static final AttributeDescriptor PHONE = declare("Phone #", TextBox.class);
        public static final AttributeDescriptor EXT = declare("Ext.", TextBox.class);
        public static final AttributeDescriptor FAX = declare("Fax #", TextBox.class);
        public static final AttributeDescriptor E_MAIL_ADDRESS = declare("E-Mail Address", TextBox.class);
        public static final AttributeDescriptor JOB_TITLE = declare("Job Title", TextBox.class);
        public static final AttributeDescriptor SIGNATURE_URI = declare("Signature URI", TextBox.class);
        public static final AttributeDescriptor SELLS_INSURANCE_PRODUCTS = declare("Sells Insurance Products", RadioGroup.class);
        public static final AttributeDescriptor COMMISSIONABLE = declare("Commissionable", CheckBox.class);
        public static final AttributeDescriptor PROFILE_SUBPRODUCER_ID = declare("Profile/Subproducer ID", TextBox.class, By.id("userProfileForm:profileId"));
        public static final AttributeDescriptor EIS_USER = declare("EIS User", RadioGroup.class, By.id("userProfileForm:ipbUser"));
        public static final AttributeDescriptor AGENCY_LOCATIONS = declare("Agency/Locations", DialogSingleSelector.class,
                AddAgencyMetaData.class);
        public static final AttributeDescriptor AGENCY = declare("Agency", DialogSingleSelector.class,
                AddAgencyNotEisUserMetaData.class);
        public static final AttributeDescriptor DEFAULT = declare("Default", CheckBox.class,
                By.xpath("//table[@id='userProfileForm:userAgencyLocations']/tbody/tr[last()]//input[contains(@id, 'default')]"));
        public static final AttributeDescriptor RESTRICT_TO_SELECTED_AGENCY_LOCATIONS = declare("Restrict to Selected Agency/Locations", RadioGroup.class);
        public static final AttributeDescriptor USER_NAME = declare("User Name", TextBox.class, By.id("userProfileForm:profile_name"));
        public static final AttributeDescriptor USER_LOGIN = declare("User Login", TextBox.class, By.id("userProfileForm:profile_login"));
        public static final AttributeDescriptor PASSWORD = declare("Password", TextBox.class);
        public static final AttributeDescriptor CONFIRM_PASSWORD = declare("Confirm Password", TextBox.class);
        public static final AttributeDescriptor SECURITY_DOMAIN = declare("Security Domain", ComboBox.class);
        public static final AttributeDescriptor CATEGORY = declare("Category", ComboBox.class);
        public static final AttributeDescriptor USER_WORK_STATUS = declare("User Work Status", RadioGroup.class);
        public static final AttributeDescriptor USER_LANGUAGE = declare("User Language", ComboBox.class);
        public static final AttributeDescriptor ROLES = declare("Roles", AdvancedSelector.class,
                By.xpath("//table[@class='pfForm pfSimpleForm' and .//input[@id='userProfileForm:select_allowed_roles']]"));
        public static final AttributeDescriptor PAR = declare("Product Access Roles", AdvancedSelector.class,
                By.xpath("//table[@class='pfForm pfSimpleForm' and .//input[@id='userProfileForm:select_par']]"));
        public static final AttributeDescriptor MANAGERS = declare("Managers", StaticElement.class);
        public static final AttributeDescriptor MANAGERS_SELECT = declare("Managers Select", AdvancedSelector.class, 
        		By.xpath("//tr[contains(@class,'oddRow') and .//input[@id='userProfileForm:select_managers']]")); 
        public static final AttributeDescriptor USER_SUBORDINATES = declare("User Subordinates", StaticElement.class);
        public static final AttributeDescriptor USER_SUBORDINATES_SELECT = declare("User Subordinates Select", AdvancedSelector.class, 
        		By.xpath("//tr[contains(@class,'evenRow') and .//input[@id='userProfileForm:select_subordinates']]"));
        public static final AttributeDescriptor BILLING_AUTHORITY_LEVEL = declare("Billing Authority Level", ComboBox.class);
        public static final AttributeDescriptor CLAIM_AUTHORITY_LEVEL = declare("Claim Authority Level", ComboBox.class);

        public static final class AddAgencyMetaData extends MetaData {
            public static final AttributeDescriptor CHANNEL = declare("Channel", ComboBox.class);
            public static final AttributeDescriptor AGENCY_NAME = declare("Agency Name", TextBox.class);
            public static final AttributeDescriptor AGENCY_CODE = declare("Agency Code", TextBox.class);
            public static final AttributeDescriptor ZIP_CODE = declare("Zip Code", TextBox.class);
            public static final AttributeDescriptor CITY = declare("City", TextBox.class);
            public static final AttributeDescriptor STATE = declare("State", TextBox.class);

            public static final AttributeDescriptor BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("userProfileForm:selectBroker"));
            public static final AttributeDescriptor BUTTON_SUBMIT_POPUP = declare(AbstractDialog.DEFAULT_POPUP_SUBMITTER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("brokerSearchFromsearchBrokerCd:addBtn"));
            public static final AttributeDescriptor BUTTON_SEARCH = declare("Search", Button.class, Waiters.DEFAULT, false,
                    By.id("brokerSearchFromsearchBrokerCd:searchBtn"));
            public static final AttributeDescriptor BUTTON_CANCEL = declare("Cancel", Button.class, Waiters.DEFAULT, false,
                    By.id("brokerSearchFromsearchBrokerCd:cancelBtn"));
        }

        public static final class AddAgencyNotEisUserMetaData extends MetaData {
            public static final AttributeDescriptor AGENCY_NAME = declare("Agency Name", TextBox.class);
            public static final AttributeDescriptor AGENCY_CODE = declare("Agency Code", TextBox.class);
            public static final AttributeDescriptor ZIP_CODE = declare("Zip Code", TextBox.class);
            public static final AttributeDescriptor CITY = declare("City", TextBox.class);
            public static final AttributeDescriptor STATE = declare("State", TextBox.class);

            public static final AttributeDescriptor BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("userProfileForm:selectBrokerBtn"));
        }
    }

    public static final class AuthorityLevelsTab extends MetaData {
        public static final AttributeDescriptor TYPE = declare("Type", ComboBox.class);
        public static final AttributeDescriptor PRODUCT = declare("Product", ComboBox.class);
        public static final AttributeDescriptor LEVEL = declare("Level", ComboBox.class);
        
        public static final AttributeDescriptor SAVE_NEW_AUTHORITY = declare("Save New Authority", Button.class,
                By.id("userProfileForm:saveAuthority"));
        public static final AttributeDescriptor UPDATE_AUTHORITY = declare("Update Authority", Button.class,
                By.id("userProfileForm:updateAuthority"));
        public static final AttributeDescriptor CANCEL_AUTHORITY = declare("Cancel Authority", Button.class,
                By.id("userProfileForm:cancelAuthority"));
    }
    
    public static final class BankingDetailsTab extends MetaData {
        public static final AttributeDescriptor SETTLEMENT_METHOD = declare("Settlement Method", ComboBox.class);
        public static final AttributeDescriptor ACCOUNT_HOLDER_NAME = declare("Account Holder Name", TextBox.class);
        public static final AttributeDescriptor ACCOUNT_NUMBER = declare("Account #", TextBox.class);
        public static final AttributeDescriptor ABA_TRANSIT_NUMBER = declare("ABA Transit #", TextBox.class);
        public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
        public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
        public static final AttributeDescriptor BANK_NAME = declare("Bank Name", ComboBox.class);
    }

    public static final class SearchByField extends MetaData {
        public static final AttributeDescriptor USER_LOGIN = declare("User Login", TextBox.class, By.id("profileSearchForm:profileSearch_searchCard_userLogin"));
        public static final AttributeDescriptor CHANNEL = declare("Channel", ComboBox.class);
        public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
        public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
        public static final AttributeDescriptor PROFILE_TYPE = declare("Profile Type", RadioGroup.class);
    }
}
