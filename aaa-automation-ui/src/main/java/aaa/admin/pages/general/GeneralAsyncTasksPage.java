/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.pages.general;

import org.openqa.selenium.By;
import aaa.admin.pages.AdminPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.db.DBService;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;

public class GeneralAsyncTasksPage extends AdminPage {

    public static Link linkStartManager = new Link(By.id("asyncTaskSummaryForm:startManager"));
    public static Link linkStartAllManager = new Link(By.id("asyncTaskSummaryForm:startAllManagers"));
    public static Link linkStopManager = new Link(By.id("asyncTaskSummaryForm:stopManager"));
    public static StaticElement labelFailedTasks = new StaticElement(By.id("asyncTaskSummaryForm:allStatsFailednode"));
    public static StaticElement labelLockedTasks = new StaticElement(By.id("asyncTaskSummaryForm:allStatsLockednode"));

	/**
	 * moved from BathMaintanance
	 * author : Sushil Sivaram
	 */
	public static void enableAsyncManager() {
		if (DBService.get().getValue("Select count(STATUS) from PASADM.ASYNCTASKMANAGER where Status = 'RUNNING'").get().equals("0")) {
			open();
			if (!linkStopManager.isPresent()) {
				linkStartAllManager.click();
			}
			linkStopManager.click();
			linkStartAllManager.click();
		}
	}

	public static void open() {
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_ASYNC_TASKS.get());
	}
}
