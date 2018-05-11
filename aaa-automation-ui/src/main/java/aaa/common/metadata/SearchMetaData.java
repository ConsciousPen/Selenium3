/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.common.metadata;

import org.openqa.selenium.By;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.FileUpload;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public final class SearchMetaData {
	public static final class DialogSearch extends MetaData {
		public static final AssetDescriptor<RadioGroup> ASSIGN_ADJUSTER = declare("Assign Adjuster", RadioGroup.class);
		public static final AssetDescriptor<TextBox> BILLING_ACCOUNT = declare("Billing Account #", TextBox.class);
		public static final AssetDescriptor<TextBox> POLICY = declare("Policy #", TextBox.class);
		public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
		public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
		public static final AssetDescriptor<TextBox> MORTGAGEE = declare("Mortgagee", TextBox.class);
		public static final AssetDescriptor<TextBox> AGENCY = declare("Agency", TextBox.class);
		public static final AssetDescriptor<TextBox> AGENCY_NAME = declare("Agency Name", TextBox.class);

		public static final AssetDescriptor<ComboBox> BROAD_LINE_OF_BUSINESS = declare("Broad Line of Business", ComboBox.class);
		public static final AssetDescriptor<TextBox> COVERAGE_EFFECTIVE_DATE = declare("Coverage Effective Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> CASE_PROFILE = declare("Case Profile", ComboBox.class);
		public static final AssetDescriptor<ComboBox> PRODUCT = declare("Product", ComboBox.class);
		public static final AssetDescriptor<ComboBox> DELIVERY_MODEL = declare("Delivery Model", ComboBox.class);

		public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, By.id("quoteVersionCreationPopupForm:quoteVersionDescription"));

		public static final AssetDescriptor<FileUpload> DEPLOY_PROCESS = declare("Upload File", FileUpload.class, By.xpath("//input[@type='file']"));
	}

	public static final class Search extends MetaData {
		public static final AssetDescriptor<TextBox> POLICY_QUOTE = declare("Policy/Quote #", TextBox.class);
		/*public static final AttributeDescriptor CLAIM = declare("Claim #", TextBox.class);
		public static final AttributeDescriptor DATE_OF_LOSS = declare("Date of Loss", TextBox.class);*/
		public static final AssetDescriptor<ComboBox> PRODUCT_ID = declare("Product ID", ComboBox.class);
		public static final AssetDescriptor<TextBox> AGENT_OF_RECORD = declare("Agent of Record", TextBox.class);
		public static final AssetDescriptor<TextBox> AGENT = declare("Agent #", TextBox.class);
		public static final AssetDescriptor<TextBox> ACCOUNT = declare("Account #", TextBox.class);
		public static final AssetDescriptor<TextBox> BILLING_ACCOUNT = declare("Billing Account #", TextBox.class);
		public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
		public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
		/*public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);*/
		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
		public static final AssetDescriptor<TextBox> STATE = declare("State", TextBox.class);
		public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class);
		public static final AssetDescriptor<TextBox> PHONE = declare("Phone #", TextBox.class);
		public static final AssetDescriptor<TextBox> CUSTOMER = declare("Customer #", TextBox.class);
		public static final AssetDescriptor<TextBox> AGENCY_NAME = declare("Agency Name", TextBox.class);
		public static final AssetDescriptor<TextBox> AGENCY = declare("Agency #", TextBox.class);
		public static final AssetDescriptor<TextBox> UNDERWRITING_COMPANY = declare("Underwriting Company #", TextBox.class);
		public static final AssetDescriptor<TextBox> SSN = declare("SSN", TextBox.class);
		/*public static final AttributeDescriptor POLICY_TITLE = declare("Policy Title", TextBox.class);
		public static final AttributeDescriptor BROKER_NAME = declare("Broker Name", TextBox.class);
	    public static final AttributeDescriptor BROKER = declare("Broker #", TextBox.class);
        public static final AttributeDescriptor PARTY_TYPE = declare("Party Type", ComboBox.class);
        public static final AttributeDescriptor BUSINESS_NAME = declare("Non-Individual / Business Name", TextBox.class);*/
	}
}
