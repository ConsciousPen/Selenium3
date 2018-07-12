package aaa.toolkit.webdriver.customcontrols;

import org.openqa.selenium.By;
import toolkit.webdriver.controls.StaticElement;

public class RatingDetailsTable {

    private final String LOCATOR_TEMPLATE = "//td[.='%s']/following-sibling::td[1]";
    private final String LABEL_LOCATOR_TEMPLATE = "//td[.='%s']";
    private String locator;

    public RatingDetailsTable(String tableLocator) {
        this.locator = tableLocator;
    }

    public String getValueByKey(String key) {
        String label = this.locator + String.format(LOCATOR_TEMPLATE, key);
        return new StaticElement(By.xpath(label)).getValue();
    }

    public StaticElement getLabel(String key) {
        return new StaticElement(By.xpath(this.locator + String.format(LABEL_LOCATOR_TEMPLATE, key)));
    }
}