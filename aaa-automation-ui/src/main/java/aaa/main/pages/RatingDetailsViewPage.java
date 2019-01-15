package aaa.main.pages;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

public abstract class RatingDetailsViewPage{
	public static Button buttonRatingDetailsOk = new Button(By.id("ratingDetailsPopupButton:ratingDetailsPopupCancel"), Waiters.AJAX);
	/**
	 * Vehicle Summary
	 */
	public String vehiclePanelXpath = "//div[@id='ratingDetailsPopup_container']//div[@id='ratingDetailsPopupForm:vehiclePanel']";
	public Table tableVehicleSummary = new Table(By.xpath(vehiclePanelXpath + "//table[@id='ratingDetailsPopupForm:vehicle_summary']"));

	protected static Logger log = LoggerFactory.getLogger(PropertyQuoteTab.RatingDetailsView.class);

	private int numberVehicleSummaryPages;
	private int currentVehicleSummaryPageNumber;
	private StaticElement ratingDetailsContainer = new StaticElement(By.xpath("//div[@id,'ratingDetailsPopup_container']"));

	/**
	 * from 1 to ...
	 * @param pageNumber
	 */
	public void openVehicleSummaryPage(int pageNumber) {
		numberVehicleSummaryPages = getNumberVehicleSummaryPages();

		if (numberVehicleSummaryPages >= pageNumber) {
			new Link(By.xpath(String.format(vehiclePanelXpath + "//td[@class='pageText']//*[not(contains(text(),'Pages')) and contains(text(),'%s')]", pageNumber))).click();
		} else {
			log.info("Vehicle Summary don't have this link");
		}
	}

	public int getNumberVehicleSummaryPages() {
		return BrowserController.get().driver()
				.findElements(By.xpath(vehiclePanelXpath + "//td[@class='pageText']//td[not(contains(text(),'Pages'))]")).size();
	}
	/**
	 * Driver Summary
	 */
	Table tableDriverInfo = new Table(By.xpath("//div[@id,'ratingDetailsPopup_container']//div[@id='ratingDetailsPopupForm:driverPanel']//table[@id='ratingDetailsPopupForm:driver_summary']"));

	/**
	 *
	 * @return number from <td></td> which doesn't have link
	 */
	public int getVehicleSummaryCurrentPageNumber(){
		currentVehicleSummaryPageNumber = Integer.parseInt(new StaticElement(By.xpath(vehiclePanelXpath + "//td[@class='pageText']//td[not(contains(text(),'Pages')) and not(*)]")).getValue());
		return currentVehicleSummaryPageNumber;
	}

}
