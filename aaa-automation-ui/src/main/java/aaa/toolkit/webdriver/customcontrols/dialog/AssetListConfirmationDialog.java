package aaa.toolkit.webdriver.customcontrols.dialog;

import org.openqa.selenium.By;

import toolkit.webdriver.controls.AbstractEditableStringElement;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.waiters.Waiter;

public class AssetListConfirmationDialog extends AbstractEditableStringElement{

    public AssetListConfirmationDialog(By locator, Waiter waiter) {
        super(locator, waiter);
    }

    public AssetListConfirmationDialog(BaseElement<?, ?> parent, By locator, Waiter waiter) {
        super(parent, locator, waiter);
    }

    @Override
    protected void setRawValue(String value) {
        new Button(this, By.xpath(String.format(".//input[@value='%s']", value))).click();;
    }

    @Override
    protected String getRawValue() {
        return null;
    }
}
