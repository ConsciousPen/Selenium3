package aaa.main.metadata;

import org.openqa.selenium.By;

import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public final class DialogsMetaData {
    public static final class AddressValidationMetaData extends MetaData {
    	public static final AttributeDescriptor RADIOGROUP_SELECT = declare("Select Address", RadioGroup.class, Waiters.AJAX, By.id("addressValidationFormAAAPrefillAddressValidation:primaryAddressSelectAAAPrefillAddressValidation"));
		public static final AttributeDescriptor ADDRESS_IS_PO_BOX = declare("Address is PO Box", CheckBox.class, Waiters.AJAX);
		public static final AttributeDescriptor STREET_NUMBER = declare("Street number", TextBox.class, Waiters.AJAX, By.xpath(".//input[contains(@id, ':primaryStreetNumberInput')]"));
		public static final AttributeDescriptor STREET_NAME = declare("Street Name", TextBox.class, Waiters.AJAX, By.xpath(".//input[contains(@id, ':primaryStreetNameInput')]"));
		public static final AttributeDescriptor UNIT_NUMBER = declare("Unit number", TextBox.class, Waiters.AJAX, By.xpath(".//input[contains(@id, ':primaryUnitNumberInput')]"));
		public static final AttributeDescriptor MAILING_ADDRESS_SELECT = declare("Mailing Address", RadioGroup.class, Waiters.AJAX, 
				By.xpath(".//table[@id='addressValidationFormAAAHOMailingAddressValidation:primaryAddressAAAHOMailingAddressValidation']"));
		public static final AttributeDescriptor PREVIOUS_ADDRESS_SELECT = declare("Previous Address", RadioGroup.class, Waiters.AJAX, 
				By.xpath(".//table[@id='addressValidationFormAAAHOPrevAddressValidationComp:primaryAddressSelectAAAHOPrevAddressValidationComp']"));
		public static final AttributeDescriptor DWELLING_ADDITIONAL_ADDRESS_SELECT = declare("Additional Dwelling Address", RadioGroup.class, Waiters.AJAX, 
				By.xpath("//table[@id='addressValidationFormAAAHOAdditionalDwelAddressValidation:primaryAddressSelectAAAHOAdditionalDwelAddressValidation']"));
		/*  public static final AttributeDescriptor ADDRESS_IS_PO_BOX = declare("Address is PO Box", CheckBox.class, Waiters.AJAX, false, By.xpath("//input[contains(@id, 'primaryPoBoxSelector')]"));
        public static final AttributeDescriptor STREET_NUMBER = declare("Street number", TextBox.class, Waiters.AJAX, true, By.xpath("//input[contains(@id, 'primaryStreetNumberInput')]"));
        public static final AttributeDescriptor STREET_NAME = declare("Street Name", TextBox.class, Waiters.AJAX, false, By.xpath("//input[contains(@id, 'primaryStreetNameInput')]"));
        public static final AttributeDescriptor UNIT_NUMBER = declare("Unit number", TextBox.class, Waiters.AJAX, false, By.xpath("//input[contains(@id, 'primaryUnitNumberInput')]"));
        public static final AttributeDescriptor RADIOGROUP_SELECT = declare("Select Address", RadioGroup.class, Waiters.AJAX, true, By.xpath("//table//table[contains(@id,'AddressSelect')]"));*/
        public static final AttributeDescriptor BTN_OK = declare("Ok", Button.class, Waiters.AJAX, true, By.xpath(".//input[@value='OK']"));
        public static final AttributeDescriptor BTN_CANCEL = declare("Cancel", Button.class, Waiters.AJAX, true, By.xpath(".//input[@value='Cancel']"));
    }
    
    public static final class DialogSearch extends MetaData {
        public static final AttributeDescriptor POLICY = declare("Policy #", TextBox.class);
        public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
        public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
        public static final AttributeDescriptor DATE_OF_BIRTH = declare("Date of birth", TextBox.class);
        public static final AttributeDescriptor MEMBERSHIP_NUM = declare("Membership #", TextBox.class);
        public static final AttributeDescriptor PHONE_NUMBER = declare("Phone Number", TextBox.class);
        public static final AttributeDescriptor DRIVING_LICENSE = declare("Driving License #", TextBox.class);

        public static final AttributeDescriptor DRIVING_LICENSE_ISSUED_STATE = declare("Driving License Issued State", ComboBox.class);
        public static final AttributeDescriptor CITY = declare("City", TextBox.class);
        public static final AttributeDescriptor STATE = declare("State", ComboBox.class);
        public static final AttributeDescriptor POSTAL_CODE = declare("Postal Code", TextBox.class);
        public static final AttributeDescriptor EMAIL_ADDRESS = declare("E-Mail Address", TextBox.class);

    }
}
