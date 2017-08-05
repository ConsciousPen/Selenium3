package aaa.admin.pages.reports;

import org.openqa.selenium.By;

import toolkit.webdriver.controls.StaticElement;

public class TemplatesSummaryPage {

    public static StaticElement labelNoErrors = new StaticElement(By.xpath("//td[normalize-space(text()) = 'no errors']"));

}
