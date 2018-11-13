/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common.pages;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
import com.exigen.ipb.etcsa.base.app.ILogin;
import aaa.common.Tab;
import aaa.common.components.Dialog;
import aaa.common.metadata.LoginPageMeta;
import toolkit.datax.TestData;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.ListBox;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.waiters.Waiters;

public class LoginPage extends Page implements ILogin {

	public static Link closeSession = new Link(By.xpath("//form[@id='loginForm']//a[contains(.,'Close CAS Session')]"), Waiters.AJAX);
	public static Link lnkLogout = new Link(By.xpath("//*[@id='logoutForm:logout_link']"), Waiters.AJAX);
	public static Dialog logoutDialog = new Dialog("//div[@id='logoutConfirmDialogDialog']");
	public static Link startPage = new Link(By.xpath("//form[@id='loginForm']//a[contains(.,'Start Page.')]"), Waiters.AJAX);
	public static Link timeOutStartPage = new Link(By.xpath("//input[contains(.,'Start Page.')]"), Waiters.AJAX);
	public static AssetList login = new AssetList(By.tagName("body"), LoginPageMeta.class);
	private static Button btnLogin = new Button(By.xpath("//input[@id='submit']"), Waiters.AJAX.then(Waiters.AJAX));
	private TestData tdLogin;

	public LoginPage(TestData td) {
		tdLogin = td;
	}

	public static boolean isPageDisplayed() {
		TextBox user = login.getAsset(LoginPageMeta.USER.getLabel(), TextBox.class);
		return user.isPresent() && user.isVisible();
	}

	@Override
	public void logout() {
		try {
			if (!BrowserController.get().driver().findElements(By.xpath(NavigationPage.LINKS_NAVIGATION_VIEW_TREE)).isEmpty()) {
				BrowserController.get().executeScript("$(\'#headerForm\').show();");
				if (Tab.buttonSaveAndExit.isPresent() && Tab.buttonSaveAndExit.isVisible()) {
					Tab.buttonSaveAndExit.click();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e.getCause());
		}
		if (lnkLogout.isPresent() && lnkLogout.isVisible()) {
			try {
				lnkLogout.click();
				logoutDialog.confirm();
			} catch (Exception e) {
				// Logout when when dialog overlaps logout button
				String logoutUri = "/aaa-app/flow?_j_acegi_logout=_j_acegi_logout&local=true&_admin_app=false";
				try {
					URL currentUrl = new URL(BrowserController.get().driver().getCurrentUrl());
					BrowserController.get().open(new URL(currentUrl.getProtocol(), currentUrl.getHost(), currentUrl.getPort(), logoutUri).toString());
				} catch (MalformedURLException exception) {
					log.info("Failed to logout using logouturl", exception);
				}
			}
		}
		if (timeOutStartPage.isPresent() && timeOutStartPage.isVisible()) {
			timeOutStartPage.click();
		}
		if (closeSession.isPresent() && closeSession.isVisible()) {
			closeSession.click();
		}
	}

	@Override
	public void login() {
		login(tdLogin);
	}

	@Override
	public void login(TestData td) {
		fillLogin(td);
		// TODO Workaround: Sometimes system throws out with timeout
		if (!(lnkLogout.isPresent() && lnkLogout.isVisible())) {
			// Session time-out screen
			if (startPage.isPresent()) {
				startPage.click();
			}
			if (isPageDisplayed()) {
				fillLogin(td);
			}
		}
		setApplicationLogFileName(td.getValue(LoginPageMeta.STATES.getLabel()));
	}

	public void verifyDisplayed() {
		Assertions.assertThat(isPageDisplayed()).as("LoginPanel is displayed").isTrue();
	}

	public TestData fillLogin(TestData td) {
		startLogin();
		if (td.containsKey(LoginPageMeta.STATES.getLabel())) {
			login.getAsset(LoginPageMeta.STATES.getLabel(), ListBox.class).unsetAllValues();
		}
		if (td.containsKey(LoginPageMeta.GROUPS.getLabel())) {
			login.getAsset(LoginPageMeta.GROUPS.getLabel(), ListBox.class).unsetAllValues();
		}
		login.setValue(td);
		btnLogin.click();
		return td;
	}

	private void startLogin() {
		logout();
		if (startPage.isPresent()) {
			startPage.click();
		}
		verifyDisplayed();
	}

	private void setApplicationLogFileName(String state) {
		String methodName = "TestNameWasNotFound";
		StackTraceElement result = Arrays.stream(Thread.currentThread().getStackTrace()).filter(s -> s.getClassName().startsWith("aaa.modules")).reduce((a, b) -> b).orElse(null);
		if (result != null) {
			methodName = result.getClassName() + "." + result.getMethodName();
		}
		String url = BrowserController.get().driver().getCurrentUrl().replace("#noback", "");
		BrowserController.get().open((url.contains("flow") || url.contains("windowId") ? url : url.concat("flow%3F_flowId%3Dipb-entry-flow")).concat("&scenarioName=").concat(methodName).concat("_").concat(state));
	}
}
