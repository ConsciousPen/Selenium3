package aaa.main.metadata;

import org.openqa.selenium.By;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public final class DialogsMetaData {
    public static final class AddressValidationMetaData extends MetaData {
    	public static final AssetDescriptor<RadioGroup> RADIOGROUP_SELECT = declare("Select Address", RadioGroup.class, Waiters.AJAX, By.xpath(".//table[@id = 'addressValidationFormAAAPrefillAddressValidation:primaryAddressSelectAAAPrefillAddressValidation' or @id = 'addressValidationFormAAAHODwellAddressValidationComp:primaryAddressSelectAAAHODwellAddressValidationComp']"));
		public static final AssetDescriptor<CheckBox> ADDRESS_IS_PO_BOX = declare("Address is PO Box", CheckBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> STREET_NUMBER = declare("Street number", TextBox.class, Waiters.AJAX, By.xpath(".//input[contains(@id, ':primaryStreetNumberInput')]"));
		public static final AssetDescriptor<TextBox> STREET_NAME = declare("Street Name", TextBox.class, Waiters.AJAX, By.xpath(".//input[contains(@id, ':primaryStreetNameInput')]"));
		public static final AssetDescriptor<TextBox> UNIT_NUMBER = declare("Unit number", TextBox.class, Waiters.AJAX, By.xpath(".//input[contains(@id, ':primaryUnitNumberInput')]"));
		public static final AssetDescriptor<RadioGroup> MAILING_ADDRESS_SELECT = declare("Mailing Address", RadioGroup.class, Waiters.AJAX, 
				By.xpath(".//table[@id='addressValidationFormAAAHOMailingAddressValidation:primaryAddressAAAHOMailingAddressValidation']"));
	    public static final AssetDescriptor<RadioGroup> PREVIOUS_ADDRESS_SELECT = declare("Previous Address", RadioGroup.class, Waiters.AJAX,
			    By.xpath(".//table[@id='addressValidationFormAAAHOPrevAddressValidationComp:primaryAddressSelectAAAHOPrevAddressValidationComp']"));
	    public static final AssetDescriptor<RadioGroup> DWELLING_ADDITIONAL_ADDRESS_SELECT = declare("Additional Dwelling Address", RadioGroup.class, Waiters.AJAX,
			    By.xpath("//table[@id='addressValidationFormAAAHOAdditionalDwelAddressValidation:primaryAddressSelectAAAHOAdditionalDwelAddressValidation']"));
		/*  public static final AssetDescriptor<CheckBox> ADDRESS_IS_PO_BOX = declare("Address is PO Box", CheckBox.class, Waiters.AJAX, false, By.xpath("//input[contains(@id, 'primaryPoBoxSelector')]"));
        public static final AssetDescriptor<TextBox> STREET_NUMBER = declare("Street number", TextBox.class, Waiters.AJAX, true, By.xpath("//input[contains(@id, 'primaryStreetNumberInput')]"));
        public static final AssetDescriptor<TextBox> STREET_NAME = declare("Street Name", TextBox.class, Waiters.AJAX, false, By.xpath("//input[contains(@id, 'primaryStreetNameInput')]"));
        public static final AssetDescriptor<TextBox> UNIT_NUMBER = declare("Unit number", TextBox.class, Waiters.AJAX, false, By.xpath("//input[contains(@id, 'primaryUnitNumberInput')]"));
        public static final AssetDescriptor<RadioGroup> RADIOGROUP_SELECT = declare("Select Address", RadioGroup.class, Waiters.AJAX, true, By.xpath("//table//table[contains(@id,'AddressSelect')]"));*/
        public static final AssetDescriptor<Button> BTN_OK = declare("Ok", Button.class, Waiters.AJAX, true, By.xpath(".//input[@value='OK']"));
        public static final AssetDescriptor<Button> BTN_CANCEL = declare("Cancel", Button.class, Waiters.AJAX, true, By.xpath(".//input[@value='Cancel']"));
    }
    
    public static final class DialogSearch extends MetaData {
        public static final AssetDescriptor<TextBox> POLICY = declare("Policy #", TextBox.class);
        public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
        public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
        public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of birth", TextBox.class);
        public static final AssetDescriptor<TextBox> MEMBERSHIP_NUM = declare("Membership #", TextBox.class);
        public static final AssetDescriptor<TextBox> PHONE_NUMBER = declare("Phone Number", TextBox.class);
        public static final AssetDescriptor<TextBox> DRIVING_LICENSE = declare("Driving License #", TextBox.class);

        public static final AssetDescriptor<ComboBox> DRIVING_LICENSE_ISSUED_STATE = declare("Driving License Issued State", ComboBox.class);
        public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
        public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class);
        public static final AssetDescriptor<TextBox> POSTAL_CODE = declare("Postal Code", TextBox.class);
        public static final AssetDescriptor<TextBox> EMAIL_ADDRESS = declare("E-Mail Address", TextBox.class);

    }
}
