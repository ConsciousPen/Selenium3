package aaa.admin.pages.product;

import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFButton;
import aaa.common.pages.Page;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.waiters.Waiters;

public class CommonProductFactoryPage extends Page {
    public static PFButton buttonNavMenu = new PFButton(By.xpath("//button[contains(@class,'hamburger pf-button')]"));
    public static StaticElement formNavigation = new StaticElement(By.xpath("//form[@id='nav']"));

    public static void activateNavigation() {
        if (!formNavigation.isPresent()) {
            buttonNavMenu.waitForAccessible(60000);
            buttonNavMenu.click(Waiters.DEFAULT.then(Waiters.SLEEP(2000)));
            formNavigation.waitForAccessible(60000);
        }
    }
}
