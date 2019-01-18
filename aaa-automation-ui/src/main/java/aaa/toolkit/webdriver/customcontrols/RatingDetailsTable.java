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

    public void openVehicleSummaryPage(int pageNumber) {
       //numberVehicleSummaryPages = getNumberOfVehicleSummaryPages();
       //if (numberVehicleSummaryPages >= pageNumber) {
       //    new Link(By.xpath(String.format(vehiclePanelXpath + "//td[@class='pageText']//*[not(contains(text(),'Pages')) and contains(text(),'%s')]", pageNumber))).click();
       //} else
       //    log.info("Vehicle Summary don't have this link");
    }
//
    ///**
    // * @return number from <td></td> which doesn't have link
    // */
    //public int getVehicleSummaryCurrentPageNumber() {
    //    currentVehicleSummaryPageNumber = Integer.parseInt(new StaticElement(By.xpath(vehiclePanelXpath + "//td[@class='pageText']//td[not(contains(text(),'Pages')) and not(*)]")).getValue());
    //    return currentVehicleSummaryPageNumber;
    //}
//
    ///**
    // * @return amount of available pages under vehicle summary table
    // */
    //public int getNumberOfVehicleSummaryPages() {
    //    return BrowserController.get().driver()
    //            .findElements(By.xpath(vehiclePanelXpath + "//td[@class='pageText']//td[not(contains(text(),'Pages'))]")).size();
    //}
}