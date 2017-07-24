/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.common.metadata;

import org.openqa.selenium.By;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.FileUpload;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public final class SearchMetaData {
	public static final class DialogSearch extends MetaData {
		public static final AttributeDescriptor ASSIGN_ADJUSTER = declare("Assign Adjuster", RadioGroup.class);
		public static final AttributeDescriptor BILLING_ACCOUNT = declare("Billing Account #", TextBox.class);
		public static final AttributeDescriptor POLICY = declare("Policy #", TextBox.class);
		public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
		public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
		public static final AttributeDescriptor MORTGAGEE = declare("Mortgagee", TextBox.class);
		public static final AttributeDescriptor AGENCY = declare("Agency", TextBox.class);
		public static final AttributeDescriptor AGENCY_NAME = declare("Agency Name", TextBox.class);

		public static final AttributeDescriptor BROAD_LINE_OF_BUSINESS = declare("Broad Line of Business", ComboBox.class);
		public static final AttributeDescriptor COVERAGE_EFFECTIVE_DATE = declare("Coverage Effective Date", TextBox.class);
		public static final AttributeDescriptor CASE_PROFILE = declare("Case Profile", ComboBox.class);
		public static final AttributeDescriptor PRODUCT = declare("Product", ComboBox.class);
		public static final AttributeDescriptor DELIVERY_MODEL = declare("Delivery Model", ComboBox.class);

		public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class, By.id("quoteVersionCreationPopupForm:quoteVersionDescription"));

		public static final AttributeDescriptor DEPLOY_PROCESS = declare("Upload File", FileUpload.class, By.xpath("//input[@type='file']"));
	}

	public static final class Search extends MetaData {
		public static final AttributeDescriptor SEARCH_FOR = declare("Search For", RadioGroup.class, Waiters.AJAX, By.xpath("//table[@id='searchForm:entityTypeSelect']"));
		public static final AttributeDescriptor POLICY_QUOTE = declare("Policy/Quote #", TextBox.class);
		/*public static final AttributeDescriptor CLAIM = declare("Claim #", TextBox.class);
		public static final AttributeDescriptor DATE_OF_LOSS = declare("Date of Loss", TextBox.class);*/
		public static final AttributeDescriptor PRODUCT_ID = declare("Product ID", ComboBox.class);
		public static final AttributeDescriptor AGENT_OF_RECORD = declare("Agent of Record", TextBox.class);
		public static final AttributeDescriptor AGENT = declare("Agent #", TextBox.class);
		public static final AttributeDescriptor ACCOUNT = declare("Account #", TextBox.class);
		public static final AttributeDescriptor BILLING_ACCOUNT = declare("Billing Account #", TextBox.class);
		public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
		public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
		/*public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);*/
		public static final AttributeDescriptor CITY = declare("City", TextBox.class);
		public static final AttributeDescriptor STATE = declare("State", TextBox.class);
		public static final AttributeDescriptor ZIP_CODE = declare("Zip Code", TextBox.class);
		public static final AttributeDescriptor PHONE = declare("Phone #", TextBox.class);
		public static final AttributeDescriptor CUSTOMER = declare("Customer #", TextBox.class);
		public static final AttributeDescriptor AGENCY_NAME = declare("Agency Name", TextBox.class);
		public static final AttributeDescriptor AGENCY = declare("Agency #", TextBox.class);
		public static final AttributeDescriptor UNDERWRITING_COMPANY = declare("Underwriting Company #", TextBox.class);
		public static final AttributeDescriptor SSN = declare("SSN", TextBox.class);
		/*public static final AttributeDescriptor POLICY_TITLE = declare("Policy Title", TextBox.class);
		public static final AttributeDescriptor BROKER_NAME = declare("Broker Name", TextBox.class);
	    public static final AttributeDescriptor BROKER = declare("Broker #", TextBox.class);
        public static final AttributeDescriptor PARTY_TYPE = declare("Party Type", ComboBox.class);
        public static final AttributeDescriptor BUSINESS_NAME = declare("Non-Individual / Business Name", TextBox.class);*/
	}
}
