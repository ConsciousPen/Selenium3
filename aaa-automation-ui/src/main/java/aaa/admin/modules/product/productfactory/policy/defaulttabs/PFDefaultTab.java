package aaa.admin.modules.product.productfactory.policy.defaulttabs;

import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFButton;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFLink;
import aaa.common.DefaultTab;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class PFDefaultTab extends DefaultTab {
    PFButton btnSave = new PFButton(By.id("persistence:save"));
    PFLink linkNextTab = new PFLink(By.xpath("//a[@class='active' and contains(@id,'nav:routes')]/following-sibling::a[1]"));

    protected PFDefaultTab(Class<? extends MetaData> mdClass) {
        super(mdClass);
    }

}
