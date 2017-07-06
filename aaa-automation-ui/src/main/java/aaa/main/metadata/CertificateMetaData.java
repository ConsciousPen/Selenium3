/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.metadata;

import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public final class CertificateMetaData {
    public static final class CertificateCoveragesTabBeneficiaries extends MetaData {
        public static final AttributeDescriptor COVERAGE_NAME = declare("Coverage Name", ComboBox.class);
        public static final AttributeDescriptor MEMBER_PAYMENT_MODE = declare("Member Payment Mode", ComboBox.class);
        public static final AttributeDescriptor PARTICIPANT_SELECTION = declare("Participant Selection", ComboBox.class);
        public static final AttributeDescriptor BENEFICIARY_SELECTION = declare("Beneficiary Selection", ComboBox.class);
        public static final AttributeDescriptor ROLE_PERCENT = declare("Role Percent", TextBox.class);
        public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
        public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
        public static final AttributeDescriptor TAX_IDENTIFICATION = declare("Tax Identification", TextBox.class);
        public static final AttributeDescriptor RELATIONSHIP_TO_INSURED = declare("Relationship to Insured", ComboBox.class);
        public static final AttributeDescriptor ADDRESS_TYPE = declare("Address Type", ComboBox.class);
        public static final AttributeDescriptor COUNTRY = declare("Country", ComboBox.class);
        public static final AttributeDescriptor ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
        public static final AttributeDescriptor ZIP_POST_CODE = declare("Zip / Post Code", TextBox.class);
        public static final AttributeDescriptor CITY = declare("City", TextBox.class);
        public static final AttributeDescriptor STATE_PROVINCE = declare("State / Province", ComboBox.class);
        public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
    }

    public static final class CertificateCoveragesTabDetails extends MetaData {
        public static final AttributeDescriptor COVERAGE_NAME = declare("Coverage Name", ComboBox.class);
        public static final AttributeDescriptor COVERAGE_TIER = declare("Coverage Tier", ComboBox.class);
        public static final AttributeDescriptor MEMBER_PAYMENT_MODE = declare("Member Payment Mode", ComboBox.class);
    }

    public static final class CertificateCoveragesTabParticipants extends MetaData {
        public static final AttributeDescriptor COVERAGE_NAME = declare("Coverage Name", ComboBox.class);
        public static final AttributeDescriptor MEMBER_PAYMENT_MODE = declare("Member Payment Mode", ComboBox.class);
        public static final AttributeDescriptor PARTICIPANT_SELECTION = declare("Participant Selection", ComboBox.class);
        public static final AttributeDescriptor BENEFICIARY_SELECTION = declare("Beneficiary Selection", ComboBox.class);
        public static final AttributeDescriptor ROLE_NAME = declare("Role Name", ComboBox.class);
        public static final AttributeDescriptor ROLE_PERCENT = declare("Role Percent", TextBox.class);
        public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
        public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
        public static final AttributeDescriptor TAX_IDENTIFICATION = declare("Tax Identification", TextBox.class);
        public static final AttributeDescriptor RELATIONSHIP_TO_INSURED = declare("Relationship to Insured", ComboBox.class);
        public static final AttributeDescriptor ADDRESS_TYPE = declare("Address Type", ComboBox.class);
        public static final AttributeDescriptor COUNTRY = declare("Country", ComboBox.class);
        public static final AttributeDescriptor ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
        public static final AttributeDescriptor CITY = declare("City", TextBox.class);
        public static final AttributeDescriptor STATE_PROVINCE = declare("State / Province", ComboBox.class);
        public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
    }

    public static final class CertificateInsuredTab extends MetaData {
        public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
        public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
        public static final AttributeDescriptor TAX_IDENTIFICATION = declare("Tax Identification", TextBox.class);
        public static final AttributeDescriptor DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
        public static final AttributeDescriptor GENDER = declare("Gender", ComboBox.class);
        public static final AttributeDescriptor TOBACCO = declare("Tobacco", ComboBox.class);
        public static final AttributeDescriptor ADDRESS_TYPE = declare("Address Type", ComboBox.class);
        public static final AttributeDescriptor COUNTRY = declare("Country", ComboBox.class);
        public static final AttributeDescriptor ZIP_POST_CODE = declare("Zip / Post Code", TextBox.class);
        public static final AttributeDescriptor CITY = declare("City", TextBox.class);
        public static final AttributeDescriptor STATE_PROVINCE = declare("State / Province", ComboBox.class);
        public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
        public static final AttributeDescriptor STUDENT_START_DATE = declare("Student Start Date", TextBox.class);
        public static final AttributeDescriptor STUDENT_STATUS = declare("Student Status", ComboBox.class);
        public static final AttributeDescriptor STUDENT_TYPE = declare("Student Type", ComboBox.class);
        public static final AttributeDescriptor STUDENT_ATHLETE = declare("Student Athlete", ComboBox.class);
        public static final AttributeDescriptor JOB_CODE = declare("Job Code", ComboBox.class);
        public static final AttributeDescriptor EMPLOYMENT_STATUS = declare("Employment Status", ComboBox.class);
        public static final AttributeDescriptor EXPATRIATE = declare("Expatriate", ComboBox.class);
        public static final AttributeDescriptor PAY_TYPE = declare("Pay Type", ComboBox.class);
        public static final AttributeDescriptor SALARY_AMOUNT = declare("Salary Amount", TextBox.class);
        public static final AttributeDescriptor UNION_MEMBER = declare("Union Member", ComboBox.class);
        public static final AttributeDescriptor JOB_TITLE = declare("Job Title", TextBox.class);
        public static final AttributeDescriptor ORIGINAL_HIRE_DATE = declare("Original Hire Date", TextBox.class);
        public static final AttributeDescriptor EMPLOYMENT_TYPE = declare("Employment Type", ComboBox.class);
        public static final AttributeDescriptor PAY_CLASS = declare("Pay Class", ComboBox.class);
        public static final AttributeDescriptor PAYROLL_FREQUENCY = declare("Payroll Frequency", ComboBox.class);
        public static final AttributeDescriptor HOURLY_WAGE = declare("Hourly Wage", TextBox.class);
    }

    public static final class CertificatePolicyTab extends MetaData {
        public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
        public static final AttributeDescriptor ISSUE_COUNTRY = declare("Issue Country", ComboBox.class);
        public static final AttributeDescriptor ISSUE_STATE = declare("Issue State", ComboBox.class);
    }
}
