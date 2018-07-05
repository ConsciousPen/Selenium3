package aaa.main.modules.customer.actiontabs;

import org.openqa.selenium.By;
import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.CustomerMetaData;
import toolkit.webdriver.controls.Button;

public class InitiateRenewalEntryActionTab extends ActionTab {

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
