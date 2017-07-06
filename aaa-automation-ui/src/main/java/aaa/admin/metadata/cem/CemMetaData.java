/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.metadata.cem;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.controls.cem.campaigns.AssociateTextBox;
import com.exigen.ipb.etcsa.controls.cem.composite.capmaigns.CampaignProductsAssetList;
import com.exigen.ipb.etcsa.controls.cem.composite.capmaigns.MarketChannelsAssetList;

import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class CemMetaData {

	 public static final class CreateCampaignTab extends MetaData {
	        public static final AttributeDescriptor CAMPAIGN_NAME = declare("Campaign Name", TextBox.class);
	        public static final AttributeDescriptor PROMOTION_CODE = declare("Promotion Code", TextBox.class);
	        public static final AttributeDescriptor BUDGET_COST = declare("Budget Cost", TextBox.class);
	        public static final AttributeDescriptor ACTUAL_COST = declare("Actual Cost", TextBox.class);
	        public static final AttributeDescriptor EXPECTED_REVENUE = declare("Expected Revenue", TextBox.class);
	        public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
	        public static final AttributeDescriptor RELATED_WITH = declare("Related with", ComboBox.class);
	        public static final AttributeDescriptor ID = declare("ID", AssociateTextBox.class, By.id("parentId"));
	        public static final AttributeDescriptor NAME = declare("Name", AssociateTextBox.class, By.id("parentName"));
	        public static final AttributeDescriptor CUSTOMER_TYPE = declare("Customer Type", ComboBox.class);
	        public static final AttributeDescriptor START_DATE = declare("Start date", TextBox.class, By.id("startDateInputDate"));
	        public static final AttributeDescriptor END_DATE = declare("End date", TextBox.class, By.id("endDateInputDate"));
	        public static final AttributeDescriptor DURATION = declare("Duration (days)", TextBox.class, By.id("duration"));
	        public static final AttributeDescriptor START_CAMPAIGN_AUTOMATICALY = declare("Start campaign automatically", CheckBox.class, By.id("autoStart"));
	        public static final AttributeDescriptor DIVERT_CAMPAIGN_NOTIFICATION = declare("Divert Campaign Notification?", CheckBox.class);
	        public static final AttributeDescriptor MARKETING_CHANELS = declare("Marketing Channels", MarketChannelsAssetList.class, MarketingChannelsSection.class);
	        public static final AttributeDescriptor CAMPAIGN_PRODUCTS = declare("Campaign Products", CampaignProductsAssetList.class, CampaignProductsSection.class);
	    }

	    public static final class MarketingChannelsSection extends MetaData{
	        public static final AttributeDescriptor CAMPAIGN_CHANEL = declare("Campaign Channel", ComboBox.class, By.id("selectChannel"));
	        public static final AttributeDescriptor SUB_CHANEL = declare("Sub Channel", ComboBox.class, By.id("selectSubChannel"));
	        public static final AttributeDescriptor MATERIAL = declare("Material", ComboBox.class, By.id("selectMaterial"));
	    }

	    public static final class CampaignProductsSection extends MetaData{
	        public static final AttributeDescriptor LINE_OF_BUSINESS = declare("Line of Business", ComboBox.class, By.id("selectLOB"));
	        public static final AttributeDescriptor PRODUCT_NAME = declare("Product Name", ComboBox.class, By.id("selectProduct"));
	        public static final AttributeDescriptor RISK_ITEM = declare("Risk Item", ComboBox.class, By.id("productRiskCd"));
	    }

	    public static final class StartCampaignActionTab extends MetaData {}

	    public static final class CreateMajorLargeAccountTab extends MetaData {
	        public static final AttributeDescriptor ACCOUNT_DESIGNATION_TYPE = declare("Account Designation Type", ComboBox.class);
	        public static final AttributeDescriptor MAJOR_LARGE_ACCOUNT_ID = declare("Major/Large Account ID", TextBox.class);
	        public static final AttributeDescriptor ACCOUNT_NAME = declare("Account Name", TextBox.class);
	        public static final AttributeDescriptor SERVICE_LEVEL_CLASSIFICATION = declare("Service Level Classification", TextBox.class);
	    }

	    public static final class DeleteMajorLargeAccountActionTab extends MetaData {}

	    public static final class EditMajorLargeAccountActionTab extends MetaData {
	        public static final AttributeDescriptor ACCOUNT_DESIGNATION_TYPE = declare("Account Designation Type", ComboBox.class);
	        public static final AttributeDescriptor MAJOR_LARGE_ACCOUNT_ID = declare("Major/Large Account ID", TextBox.class);
	        public static final AttributeDescriptor ACCOUNT_NAME = declare("Account Name", TextBox.class);
	        public static final AttributeDescriptor SERVICE_LEVEL_CLASSIFICATION = declare("Service Level Classification", TextBox.class);
	    }

	    public static final class SearchByField extends MetaData {
	        public static final AttributeDescriptor CAMPAIGN_ID = declare("Campaign ID", TextBox.class);
	        public static final AttributeDescriptor ACCOUNT_ID = declare("Account ID", TextBox.class);
	        public static final AttributeDescriptor GROUP_ID = declare("Group ID", TextBox.class);
	    }

	    public static final class UpdateCampaignActionTab extends MetaData {
	        public static final AttributeDescriptor CAMPAIGN_NAME = declare("Campaign Name", TextBox.class);
	        public static final AttributeDescriptor ID = declare("ID", AssociateTextBox.class, By.id("parentId"));
	        public static final AttributeDescriptor NAME = declare("Name", AssociateTextBox.class, By.id("parentName"));
	        public static final AttributeDescriptor MARKETING_CHANELS = declare("Marketing Channels", MarketChannelsAssetList.class, MarketingChannelsSection.class);
	        public static final AttributeDescriptor CAMPAIGN_PRODUCTS = declare("Campaign Products", CampaignProductsAssetList.class, CampaignProductsSection.class);
	    }

	    public static final class EndCampaignActionTab extends MetaData {
	        public static final AttributeDescriptor CAMPAIGN_TERMINATION_REASON = declare("Campaign termination reason", ComboBox.class);
	        public static final AttributeDescriptor PLEASE_EXPLAIN = declare("Please Explain", TextBox.class);
	    }

	    public static final class SuspendCampaignActionTab extends MetaData {
	        public static final AttributeDescriptor SUSPEND_FROM = declare("Suspend From", TextBox.class);
	        public static final AttributeDescriptor SUSPEND_TO = declare("Suspend To", TextBox.class);
	    }

	    public static final class RestartCampaignActionTab extends MetaData {
	        public static final AttributeDescriptor RESTART_DATE = declare("Restart Date", TextBox.class, By.id("restartDateInputDate"));
	    }

	    public static final class CopyCampaignActionTab extends MetaData {
	        public static final AttributeDescriptor CAMPAIGN_NAME = declare("Campaign Name", TextBox.class);
	        public static final AttributeDescriptor CUSTOMER_TYPE = declare("Customer Type", ComboBox.class);
	        public static final AttributeDescriptor START_DATE = declare("Start date", TextBox.class, By.id("startDateInputDate"));
	        public static final AttributeDescriptor END_DATE = declare("End date", TextBox.class, By.id("endDateInputDate"));
	        public static final AttributeDescriptor DURATION = declare("Duration (days)", TextBox.class, By.id("duration"));
	        public static final AttributeDescriptor MARKETING_CHANELS = declare("Marketing Channels", MarketChannelsAssetList.class, MarketingChannelsSection.class);
	        public static final AttributeDescriptor CAMPAIGN_PRODUCTS = declare("Campaign Products", CampaignProductsAssetList.class, CampaignProductsSection.class);
	    }

	    public static final class CreateGroupsInformationTab extends MetaData {
	        public static final AttributeDescriptor GROUP_TYPE = declare("Group Type", TextBox.class);
	        public static final AttributeDescriptor GROUP_ID = declare("Group ID", TextBox.class);
	        public static final AttributeDescriptor GROUP_NAME = declare("Group Name", TextBox.class);
	    }

	    public static final class DeleteGroupsInformationActionTab extends MetaData {}

	    public static final class EditGroupsInformationActionTab extends MetaData {
	        public static final AttributeDescriptor GROUP_TYPE = declare("Group Type", TextBox.class);
	        public static final AttributeDescriptor GROUP_ID = declare("Group ID", TextBox.class);
	        public static final AttributeDescriptor GROUP_NAME = declare("Group Name", TextBox.class);
	    }

	    public static final class CreateChildCampaignActionTab extends MetaData {
	        public static final AttributeDescriptor CAMPAIGN_NAME = declare("Campaign Name", TextBox.class);
	        public static final AttributeDescriptor CUSTOMER_TYPE = declare("Customer Type", ComboBox.class);
	        public static final AttributeDescriptor START_DATE = declare("Start date", TextBox.class, By.id("startDateInputDate"));
	        public static final AttributeDescriptor END_DATE = declare("End date", TextBox.class, By.id("endDateInputDate"));
	        public static final AttributeDescriptor DURATION = declare("Duration (days)", TextBox.class, By.id("duration"));
	        public static final AttributeDescriptor MARKETING_CHANELS = declare("Marketing Channels", MarketChannelsAssetList.class, MarketingChannelsSection.class);
	    }

	    public static final class ArchiveCampaignActionTab extends MetaData {}

	    public static final class FieldsConfigurationTab extends MetaData {
	        public static final AttributeDescriptor ADDRESS_LINE_3_HIDDEN = declare("Address Line 3", CheckBox.class, By.id("configForm:fieldsConfigurationTable:5:fieldHidden"));
	        public static final AttributeDescriptor ADDRESS_LINE_2_DEFAULT_VALUE = declare("Address Line 2", TextBox.class, By.id("configForm:fieldsConfigurationTable:4:defaultSTRING"));
	        public static final AttributeDescriptor ATTENTION_ORDER = declare("Attention", TextBox.class, By.id("configForm:fieldsConfigurationTable:6:fieldOrder"));
	    }
}
