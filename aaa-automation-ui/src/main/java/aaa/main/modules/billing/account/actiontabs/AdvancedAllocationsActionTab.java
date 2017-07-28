package aaa.main.modules.billing.account.actiontabs;

import org.openqa.selenium.By;
import toolkit.webdriver.controls.StaticElement;
import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.BillingAccountMetaData;

public class AdvancedAllocationsActionTab extends ActionTab {
    public AdvancedAllocationsActionTab() {
        super(BillingAccountMetaData.AdvancedAllocationsActionTab.class);
    }

    public StaticElement getBottomWarning() {
        return new StaticElement(By.xpath("//div[@id='contentWrapper']//span[@class='error_message']"));
    }

    @Override
    public Tab submitTab() {
        buttonOk.click();
        return this;
    }
}
