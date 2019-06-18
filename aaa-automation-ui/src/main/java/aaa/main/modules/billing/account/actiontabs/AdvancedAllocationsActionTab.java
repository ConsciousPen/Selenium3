package aaa.main.modules.billing.account.actiontabs;

import org.openqa.selenium.By;
import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.BillingAccountMetaData;
import toolkit.webdriver.controls.Link;

public class AdvancedAllocationsActionTab extends ActionTab {
    public AdvancedAllocationsActionTab() {
        super(BillingAccountMetaData.AdvancedAllocationsActionTab.class);
    }

	public static Link linkAdvancedAllocation = new Link(By.xpath("//a[contains(@id, 'openAdvAllocationLnk')]"));

	@Override
    public Tab submitTab() {
        buttonOk.click();
        return this;
    }
}
