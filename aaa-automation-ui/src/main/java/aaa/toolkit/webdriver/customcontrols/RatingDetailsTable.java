package aaa.toolkit.webdriver.customcontrols;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.exceptions.IstfException;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;

public class RatingDetailsTable {
    protected static Logger log = LoggerFactory.getLogger(RatingDetailsTable.class);

    private final String LOCATOR_TEMPLATE = "//td[.='%s']/following-sibling::td[1]";
    private final String LABEL_LOCATOR_TEMPLATE = "//td[.='%s']";

    private String locator;
    private String panelLocator;
    private String paginationLocator;

    private int currentPageNumber;
    private int amountOfPages;

    public RatingDetailsTable(String tableLocator) {
        this.locator = tableLocator;
        this.panelLocator = locator + "/ancestor::div[contains(@id,'vehiclePanel_body')]";
        this.paginationLocator = panelLocator + "//td[@class='pageText']";
    }

    public String getValueByKey(String key) {
        String label = this.locator + String.format(LOCATOR_TEMPLATE, key);
        return new StaticElement(By.xpath(label)).getValue();
    }

    public StaticElement getLabel(String key) {
        return new StaticElement(By.xpath(this.locator + String.format(LABEL_LOCATOR_TEMPLATE, key)));
    }

    /**
     * Open page if present
     * @param pageNumber
     * @throws IstfException if page not present
     */
    public void moveToPage(int pageNumber) {
        amountOfPages = getAmountOfPages();
        if (amountOfPages >= pageNumber) {
            new Link(By.xpath(String.format(paginationLocator + "//*[not(contains(text(),'Pages')) and contains(text(),'%s')]", pageNumber))).click();
        } else {
            throw new IstfException(String.format("%s: Page \"%s\" is not present", this.getClass().getName(), pageNumber));
        }
    }

    /**
     * @return number from <td></td> which doesn't have link
     */
    public int getCurrentPageNumber() {
        currentPageNumber = Integer.parseInt(new StaticElement(By.xpath(paginationLocator + "//td[not(contains(text(),'Pages')) and not(*)]")).getValue());
        return currentPageNumber;
    }

   /**
    * @return amount of available pages under table
    */
   public int getAmountOfPages() {
       return BrowserController.get().driver()
               .findElements(By.xpath(paginationLocator+ "//td[not(contains(text(),'Pages'))]")).size();
   }
}