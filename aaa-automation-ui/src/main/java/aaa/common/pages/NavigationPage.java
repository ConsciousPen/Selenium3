/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common.pages;

import static toolkit.verification.CustomAssertions.assertThat;
import java.text.MessageFormat;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import com.exigen.ipb.eisa.base.app.Application;
import aaa.common.Tab;
import aaa.toolkit.webdriver.customcontrols.JavaScriptLink;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;

public class NavigationPage extends Page {

	public static final String FLOW_NAVIGATE_TEMPLATE = "/flow?_flowId={0}&_windowId={1}";
	public static final String LINKS_NAVIGATION_VIEW_TREE = "//*[contains(@id, ':billingTabs') or @id='policyDataGatherForm:CFClaimOverviewDataGatherTabsList' "
			+ "or @id='policyDataGatherForm:tabList' or @id='customerMainTab:customerFirstTabs' or @id='pmBrokerForm:brokerInformationTabs' or @id='producContextInfoPanel' "
			+ "or @id='producContextInfoForm:CFClaimOverviewTabsList' or contains(@id, 'CFClaimOverviewTabsList') or contains(@id, 'TopTabsList') "
			+ "or @id='reportsSubTabsForm:reportTabsBarList' or @id='policyDataGatherForm:sideTree']//a";
	private static final String LABEL_NAVIGATION_MAIN_TAB =
			"//form[@id='tabForm' or @id='adminTabsForm' or @id='tabForm:topTabsBarList' or @id='customerMainTab' or @id='caseForm']//a/span[text()='%1s']";
	private static final String LABEL_NAVIGATION_VIEW_TAB = "//*[contains(@id, ':billingTabs') or @id='policyDataGatherForm:CFClaimOverviewDataGatherTabsList' "
			+ "or @id='policyDataGatherForm:tabList' or @id='customerMainTab:customerFirstTabs' or @id='pmBrokerForm:brokerInformationTabs' or @id='producContextInfoPanel' "
			+ "or @id='producContextInfoForm:CFClaimOverviewTabsList' or contains(@id, 'CFClaimOverviewTabsList') or contains(@id, 'TopTabsList') "
			+ "or @id='reportsSubTabsForm:reportTabsBarList' or @id='policyDataGatherForm:sideTree']//a/span[.='%1s']";
	private static final String LABEL_NAVIGATION_VIEW_TAB_LEFT_GENERAL = "//div[@id='policyDataGatherForm:tabTree']/div/div[@title='%1s']/span[2]/span[2]";
	private static final String LABEL_NAVIGATION_VIEW_TAB_LEFT_SINGLE = "//div[@id='policyDataGatherForm:tabTree']/div/div[@title='%s']|//div[@title='%s']/span[2]/span[2]";
	private static final String LABEL_NAVIGATION_VIEW_SUBTAB =
			"//ul[@id='policyDataGatherForm:tabListList_2' or @id='policyDataGatherForm:tabListList_1' "
					+ "or @id='crmForm:crmMainTabsList' or @id='generalTab:customerSecondTabs' or @id='caseConsolidatedTabsForm:caseConsolidatedTabsList'"
					+ "or @id='proposalForm:groupProposalDataGatherTabsList' or @id='policyDataGatherForm:tabListList_2' or @id='generalTab:customerSecondTabsList']//a/span[.='%1s']";
	public static final String LABEL_NAVIGATION_VIEW_LEFT_MENU = "//span[@id='left_menu_form:left_menu' or @id='left_menu_form:left_menu_commission']//a/span[.='%1s']";
	public static ComboBox comboBoxListAction = new ComboBox(By.xpath("//select[@id='productContextInfoForm:moveToBox' or @id='producContextInfoForm:moveToBox' "
			+ "or @id='producContextInfoForm:moveToBox:0:moveToDropdown' or @id='productContextInfoForm:moveToBox:0:moveToDropdown' "
			+ "or @id='endorsementForm:endorsementList:0:moveToDropdown' or @id='renewalForm:renewals_list_table:0:moveToDropdown' "
			+ "or @id='billingInfoForm:moveToDropdown' or @id='custInfoForm:actionsForCustomerHeaderId' or @id='oppInfoForm:actionsForOpportunity' "
			+ "or @id='acctInfoForm:actionsForCustomerHeaderId' or @id='actionsMenu' or @id='producContextInfoForm:select' or @id='caseProfileContextInfoForm:moveToBox']"));

	public static void setActionAndGo(String action) {
		comboBoxListAction.setValue(action);
		if (Tab.buttonGo.isPresent()) {
			Tab.buttonGo.click();
		}
	}

	public static void toMainTab(String tab) {
		new JavaScriptLink(By.xpath(String.format(LABEL_NAVIGATION_MAIN_TAB, tab))).click();
	}

	public static void toMainAdminTab(String tab) {
		new JavaScriptLink(By.xpath(String.format(LABEL_NAVIGATION_MAIN_TAB, tab))).click();
	}

	public static void toViewTab(String tab) {
		Link topViewTabLink = new Link(By.xpath(String.format(LABEL_NAVIGATION_VIEW_TAB, tab)));
		Link leftViewTabLinkGeneral = new Link(By.xpath(String.format(LABEL_NAVIGATION_VIEW_TAB_LEFT_GENERAL, tab)));
		Link leftViewTabLinkSingle = new Link(By.xpath(String.format(LABEL_NAVIGATION_VIEW_TAB_LEFT_SINGLE, tab, tab)));
		if (topViewTabLink.isPresent() && topViewTabLink.isVisible()) {
			topViewTabLink.click();
		} else {
			if (leftViewTabLinkGeneral.isPresent()) {
				leftViewTabLinkGeneral.click();
			} else {
				leftViewTabLinkSingle.click();
			}
		}
	}

	public static void toViewSubTab(String tab) {
		new JavaScriptLink(By.xpath(String.format(LABEL_NAVIGATION_VIEW_SUBTAB, tab))).click();
	}

	public static void toViewLeftMenu(String link) {
		new JavaScriptLink(By.xpath(String.format(LABEL_NAVIGATION_VIEW_LEFT_MENU, link))).click();
	}

	public static void toFlow(Application appType, String flow) {
		String serverUrl = appType.getUrl();
		String flowNavigate = MessageFormat.format(FLOW_NAVIGATE_TEMPLATE, flow, resolveWindowId());
		BrowserController.get().open(serverUrl + flowNavigate);
		log.info("[navigateToFlow] {}{}", serverUrl, flowNavigate);
	}

	public static void toFlow(Application appType, String flow, String flowParameter) {
		String serverUrl = appType.getUrl();
		String flowNavigate = MessageFormat.format(FLOW_NAVIGATE_TEMPLATE, flow, resolveWindowId());
		BrowserController.get().open(serverUrl + flowNavigate + flowParameter);
		log.info("[navigateToFlow] {}{}{}", serverUrl, flowNavigate, flowParameter);
	}

	public static void policyNavigation(String parentTab, String tab) {
		List<WebElement> pluses = BrowserController.get().driver().findElements(By.xpath("//span[@class='rf-trn-hnd-colps rf-trn-hnd']"));
		for (WebElement plus : pluses) {
			plus.click();
		}
		new Link(By.xpath(String.format("//div[@id='policyDataGatherForm:sideTree']//div[@title='%1s']/..//span[text()='%2s']", parentTab, tab))).click();
	}

	public static boolean isMainTabSelected(String tab) {
		StaticElement tabLabel = new StaticElement(By.xpath(String.format(LABEL_NAVIGATION_MAIN_TAB, tab) + "/../.."));
		return tabLabel.getAttribute("class").contains("selected");
	}

	private static String resolveWindowId() {
		String windowId = (String) BrowserController.get().executeScript("return window.name");
		if (windowId.isEmpty()) {
			windowId = "W" + System.currentTimeMillis();
			log.error(MessageFormat.format("WindowId isn't available (should be generated on server-side, falling back to generated value {0}", windowId));
			BrowserController.get().executeScript(MessageFormat.format("window.name=''{0}''", windowId));
		}
		return windowId;
	}

	public static class Verify {

		public static void mainTabSelected(String tab) {
			StaticElement tabLabel = new StaticElement(By.xpath(String.format(LABEL_NAVIGATION_MAIN_TAB, tab) + "/../.."));
			assertThat(tabLabel.getAttribute("class").contains("selected")).as("Tab with locator %s is not selected", tabLabel.getLocator()).isTrue();
		}

		public static void viewTabSelected(String tab) {
			StaticElement tabLabel = new StaticElement(By.xpath(String.format(LABEL_NAVIGATION_VIEW_TAB, tab) + "/../.."));
			if (tabLabel.getAttribute("class").contains("selected")) {
				assertThat(tabLabel.getAttribute("class").contains("selected")).as("Tab with locator %s is not selected", tabLabel.getLocator()).isTrue();
			} else {
				tabLabel = new StaticElement(By.xpath(String.format(LABEL_NAVIGATION_VIEW_TAB, tab)));
				assertThat(tabLabel.getAttribute("class").contains("sel")).as("Tab with locator %s is not selected", tabLabel.getLocator()).isTrue();
			}
		}

		public static void viewMainTabSelected(String tab) {
			StaticElement tabLabel = new StaticElement(By.xpath(String.format(LABEL_NAVIGATION_VIEW_SUBTAB, tab)));
			assertThat(tabLabel.getAttribute("class").contains("selected")).as("Tab with locator %s is not selected", tabLabel.getLocator()).isTrue();
		}

		public static void viewTabPresent(String tab, boolean isPresent) {
			assertThat(new StaticElement(By.xpath(String.format(LABEL_NAVIGATION_VIEW_TAB, tab)))).isPresent(isPresent);
		}

		public static void viewTabLeftPresent(String tab, boolean isPresent) {
			assertThat(new StaticElement(By.xpath(String.format(LABEL_NAVIGATION_VIEW_TAB_LEFT_SINGLE, tab, tab)))).isPresent(isPresent);
		}

		public static void viewTabMainPresent(String tab, boolean isPresent) {
			assertThat(new StaticElement(By.xpath(String.format(LABEL_NAVIGATION_MAIN_TAB, tab)))).isPresent(isPresent);
		}
	}
}
