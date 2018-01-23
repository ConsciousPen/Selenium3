package aaa.main.modules.customer.actiontabs;

import org.openqa.selenium.By;
import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.CustomerMetaData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;

public class InitiateRenewalEntryActionTab extends ActionTab {

    public static StaticElement rmeScreenMpdErrorMessage = new StaticElement(By.xpath
            ("//span[@id='genericForm:id_AAAStartConversionPolicyFormBean_mPDiscount_error']"));
    private static Button buttonOk = new Button(By.xpath("//input[@id='genericForm:ok']"));

    public InitiateRenewalEntryActionTab() {
        super(CustomerMetaData.InitiateRenewalEntryActionTab.class);
    }

    @Override
    public Tab submitTab() {
        buttonOk.click();
        return this;
    }
}
