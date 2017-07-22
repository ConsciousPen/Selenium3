package aaa.toolkit.webdriver.customcontrols;

import org.openqa.selenium.By;
import com.exigen.ipb.etcsa.utils.Dollar;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Control for filling table with payments allocation by payment methods on Purchase Tab.</br>
 * Pass TestData format input that contains control names of required payment methods (will be searched by contains in value of input in first column)</br>
 * Value can contain specific monetary input, percentage of 'Remaining Balance Due Today' 
 * or the rest of 'Remaining Balance Due Today' after filling other values (should be last in test data)</br>
 * Also Check Number can be set, set value to control with name 'Check Number ' + payment method.</br>
 * </br>
 * <b>Example of Test Data:</b></br>
 * <pre>
 * PaymentAllocation: {
      Cash: $123,
      Visa: /50%,
      Check: /rest,
      Check Number Check: 41244,
      Check Number Visa: 346342
    }
 * </pre>
 */
public class PaymentMethodAllocationControl extends AbstractContainer<TestData, TestData>{

    public static final String BALANCE_DUE_KEY = "Balance Due";
    
    public PaymentMethodAllocationControl(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
        super(parent, locator, metaDataClass);
    }

    @Override
    protected TestData getRawValue() {
	    // TODO Auto-generated method stub
	    return null;
    }

    @Override
    protected void setRawValue(TestData data) {
        Dollar due = new Dollar(data.getValue(BALANCE_DUE_KEY));
        Dollar enteredSum = new Dollar(0);
        for (String key : data.getKeys()) {
            if (key.equals(BALANCE_DUE_KEY)) {
                continue;
            }
            TextBox input = null;
            if (key.startsWith("Check Number ")) {
	            input = new TextBox(this, By.xpath(String.format(".//tr[td/input[contains(@value,'%s')]]/td[3]/input", key.replace("Check Number ", ""))), Waiters.AJAX);
            } else {
	            input = new TextBox(this, By.xpath(String.format(".//tr[td/input[contains(@value,'%s')]]/td[2]/input", key)), Waiters.AJAX);
            }
            String value = data.getValue(key);
            if (value.equals("rest")) {
                value = due.subtract(enteredSum).toString();
            } else if (value.endsWith("%")) {
                value = due.getPercentage(Double.valueOf(value.replace("%", ""))).toString();
            }
            input.setValue(value);
            enteredSum = enteredSum.add(new Dollar(value));
        }
    }

    @Override
    public TestData.Type testDataType() {
	    return TestData.Type.TESTDATA;
    }

	@Override
	protected TestData normalize(Object rawValue) {
		if (rawValue instanceof TestData) {
			return (TestData) rawValue;
		} else {
			throw new IllegalArgumentException("Value " + rawValue + " has incorrect type " + rawValue.getClass());
		}
    }

    @Override
    public void fill(TestData td) {
        if (td.containsKey(name)) {
            setValue(td.getTestData(name));
        }
    }

}
