package aaa.main.pages;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * This page appears after click by View Rating Details button at the Premium & Calculation tab
 */
public class ViewRatingDetailsPage {
	protected String ratingDetailsContainer = "//div[@id='ratingDetailsPopup_container']";
	public static Button buttonRatingDetailsOk = new Button(By.id("ratingDetailsPopupButton:ratingDetailsPopupCancel"), Waiters.AJAX);
	/** Vehicle Summary */
	public String vehiclePanelXpath = ratingDetailsContainer + "//div[@id='ratingDetailsPopupForm:vehiclePanel']";
	public Table tableVehicleSummary = new Table(By.xpath(vehiclePanelXpath + "//table[@id='ratingDetailsPopupForm:vehicle_summary']"));
	/** Driver Summary */
	public String driverPanelXpath = ratingDetailsContainer + "//div[@id='ratingDetailsPopupForm:driverPanel']";
	public Table tableDriverInfo = new Table(By.xpath(driverPanelXpath + "//table[@id='ratingDetailsPopupForm:driver_summary']"));

	protected static Logger log = LoggerFactory.getLogger(ViewRatingDetailsPage.class);

	private int numberVehicleSummaryPages;
	private int currentVehicleSummaryPageNumber;

	/**
	 * @param pageNumber from 1 to ...
	 */
	public void openVehicleSummaryPage(int pageNumber) {
		numberVehicleSummaryPages = getNumberOfVehicleSummaryPages();
		if (numberVehicleSummaryPages >= pageNumber) {
			new Link(By.xpath(String.format(vehiclePanelXpath + "//td[@class='pageText']//*[not(contains(text(),'Pages')) and contains(text(),'%s')]", pageNumber))).click();
		} else
			log.info("Vehicle Summary don't have this link");
	}

	/**
	 * @return number from <td></td> which doesn't have link
	 */
	public int getVehicleSummaryCurrentPageNumber() {
		currentVehicleSummaryPageNumber = Integer.parseInt(new StaticElement(By.xpath(vehiclePanelXpath + "//td[@class='pageText']//td[not(contains(text(),'Pages')) and not(*)]")).getValue());
		return currentVehicleSummaryPageNumber;
	}

	/**
	 * @return amount of available pages under vehicle summary table
	 */
	public int getNumberOfVehicleSummaryPages() {
		return BrowserController.get().driver()
				.findElements(By.xpath(vehiclePanelXpath + "//td[@class='pageText']//td[not(contains(text(),'Pages'))]")).size();
	}
}
