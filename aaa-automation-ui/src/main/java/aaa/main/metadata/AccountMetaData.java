/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.metadata;

import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class AccountMetaData {

	public static final class AccountTab extends MetaData {}

	public static final class MoveCustomerSearchActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CUSTOMER = declare("Customer #", TextBox.class);
	}

	public static final class MoveCustomerConfirmationActionTab extends MetaData {}

	public static final class AddAffinityGroupActionTab extends MetaData {
		public static final AssetDescriptor<ComboBox> GROUP = declare("Group", ComboBox.class);
	}

	public static final class EliminateAffinityGroupActionTab extends MetaData {}

	public static final class CommunicationActionTab extends MetaData {
		public static final AssetDescriptor<ComboBox> COMMUNICATION_CHANNEL = declare("Communication Channel", ComboBox.class);
		public static final AssetDescriptor<ComboBox> ENTITY_TYPE = declare("Entity Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> COMMUNICATION_DIRECTION = declare("Communication Direction", ComboBox.class);
	}

	public static final class AcctInfoTab extends MetaData {
		public static final AssetDescriptor<TextBox> ACCOUNT_NAME = declare("Account Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> SPECIAL_HANDLING = declare("Special Handling", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> CONFIDENTIAL_ACCOUNT = declare("Confidential Account", RadioGroup.class);
	}

	public static final class DesignatedContactsTab extends MetaData {
		public static final AssetDescriptor<TextBox> CONTACT_PHONE = declare("Contact Phone", TextBox.class);
	}

	public static final class AffinityGroupsTab extends MetaData {}

	public static final class SelectContactTab extends MetaData {
		public static final AssetDescriptor<ComboBox> ROLE = declare("Role", ComboBox.class);
		public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
		public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> CATEGORY = declare("Category", ComboBox.class);
		public static final AssetDescriptor<ComboBox> CHANNEL = declare("Channel", ComboBox.class);
	}

	public static final class EliminateDesignatedContactActionTab extends MetaData {}
}
