package aaa.main.pages;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.toolkit.webdriver.customcontrols.RatingDetailsTable;
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

}
