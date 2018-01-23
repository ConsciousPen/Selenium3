/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.metadata;

import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public final class CertificateMetaData {
	public static final class CertificateCoveragesTabBeneficiaries extends MetaData {
		public static final AssetDescriptor<ComboBox> COVERAGE_NAME = declare("Coverage Name", ComboBox.class);
		public static final AssetDescriptor<ComboBox> MEMBER_PAYMENT_MODE = declare("Member Payment Mode", ComboBox.class);
		public static final AssetDescriptor<ComboBox> PARTICIPANT_SELECTION = declare("Participant Selection", ComboBox.class);
		public static final AssetDescriptor<ComboBox> BENEFICIARY_SELECTION = declare("Beneficiary Selection", ComboBox.class);
		public static final AssetDescriptor<TextBox> ROLE_PERCENT = declare("Role Percent", TextBox.class);
		public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
		public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
		public static final AssetDescriptor<TextBox> TAX_IDENTIFICATION = declare("Tax Identification", TextBox.class);
		public static final AssetDescriptor<ComboBox> RELATIONSHIP_TO_INSURED = declare("Relationship to Insured", ComboBox.class);
		public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
		public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
		public static final AssetDescriptor<TextBox> ZIP_POST_CODE = declare("Zip / Post Code", TextBox.class);
		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
		public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
	}

	public static final class CertificateCoveragesTabDetails extends MetaData {
		public static final AssetDescriptor<ComboBox> COVERAGE_NAME = declare("Coverage Name", ComboBox.class);
		public static final AssetDescriptor<ComboBox> COVERAGE_TIER = declare("Coverage Tier", ComboBox.class);
		public static final AssetDescriptor<ComboBox> MEMBER_PAYMENT_MODE = declare("Member Payment Mode", ComboBox.class);
	}

	public static final class CertificateCoveragesTabParticipants extends MetaData {
		public static final AssetDescriptor<ComboBox> COVERAGE_NAME = declare("Coverage Name", ComboBox.class);
		public static final AssetDescriptor<ComboBox> MEMBER_PAYMENT_MODE = declare("Member Payment Mode", ComboBox.class);
		public static final AssetDescriptor<ComboBox> PARTICIPANT_SELECTION = declare("Participant Selection", ComboBox.class);
		public static final AssetDescriptor<ComboBox> BENEFICIARY_SELECTION = declare("Beneficiary Selection", ComboBox.class);
		public static final AssetDescriptor<ComboBox> ROLE_NAME = declare("Role Name", ComboBox.class);
		public static final AssetDescriptor<TextBox> ROLE_PERCENT = declare("Role Percent", TextBox.class);
		public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
		public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
		public static final AssetDescriptor<TextBox> TAX_IDENTIFICATION = declare("Tax Identification", TextBox.class);
		public static final AssetDescriptor<ComboBox> RELATIONSHIP_TO_INSURED = declare("Relationship to Insured", ComboBox.class);
		public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
		public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
		public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
	}

	public static final class CertificateInsuredTab extends MetaData {
		public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
		public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
		public static final AssetDescriptor<TextBox> TAX_IDENTIFICATION = declare("Tax Identification", TextBox.class);
		public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
		public static final AssetDescriptor<ComboBox> GENDER = declare("Gender", ComboBox.class);
		public static final AssetDescriptor<ComboBox> TOBACCO = declare("Tobacco", ComboBox.class);
		public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
		public static final AssetDescriptor<TextBox> ZIP_POST_CODE = declare("Zip / Post Code", TextBox.class);
		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
		public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AssetDescriptor<TextBox> STUDENT_START_DATE = declare("Student Start Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> STUDENT_STATUS = declare("Student Status", ComboBox.class);
		public static final AssetDescriptor<ComboBox> STUDENT_TYPE = declare("Student Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> STUDENT_ATHLETE = declare("Student Athlete", ComboBox.class);
		public static final AssetDescriptor<ComboBox> JOB_CODE = declare("Job Code", ComboBox.class);
		public static final AssetDescriptor<ComboBox> EMPLOYMENT_STATUS = declare("Employment Status", ComboBox.class);
		public static final AssetDescriptor<ComboBox> EXPATRIATE = declare("Expatriate", ComboBox.class);
		public static final AssetDescriptor<ComboBox> PAY_TYPE = declare("Pay Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> SALARY_AMOUNT = declare("Salary Amount", TextBox.class);
		public static final AssetDescriptor<ComboBox> UNION_MEMBER = declare("Union Member", ComboBox.class);
		public static final AssetDescriptor<TextBox> JOB_TITLE = declare("Job Title", TextBox.class);
		public static final AssetDescriptor<TextBox> ORIGINAL_HIRE_DATE = declare("Original Hire Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> EMPLOYMENT_TYPE = declare("Employment Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> PAY_CLASS = declare("Pay Class", ComboBox.class);
		public static final AssetDescriptor<ComboBox> PAYROLL_FREQUENCY = declare("Payroll Frequency", ComboBox.class);
		public static final AssetDescriptor<TextBox> HOURLY_WAGE = declare("Hourly Wage", TextBox.class);
	}

	public static final class CertificatePolicyTab extends MetaData {
		public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> ISSUE_COUNTRY = declare("Issue Country", ComboBox.class);
		public static final AssetDescriptor<ComboBox> ISSUE_STATE = declare("Issue State", ComboBox.class);
	}
}
