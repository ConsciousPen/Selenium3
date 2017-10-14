/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common.pages;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.base.app.Application;
import com.exigen.ipb.etcsa.base.app.ApplicationFactory;
import com.exigen.ipb.etcsa.base.app.ILogin;

import aaa.common.Tab;
import aaa.common.components.Dialog;
import aaa.common.metadata.LoginPageMeta;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.ListBox;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.waiters.Waiters;

public class LoginPage extends Page implements ILogin {

	public String user;
	public String password;
	public String state;

	public LoginPage(String appUser, String appPw) {
		user = appUser;
		password = appPw;
		state = null;
	}
	
	public LoginPage(TestData td) {
		user = td.getValue(LoginPageMeta.USER.getLabel());
		password = td.getValue(LoginPageMeta.PASSWORD.getLabel());
		state = td.getValue(LoginPageMeta.STATES.getLabel());
	}

	public static StaticElement lblHeader = new StaticElement(By.xpath("//form[@id='loginForm']/table[1]/tbody/tr/td"));
	private static Button btnLogin = new Button(By.xpath("//input[@id='submit']"), Waiters.AJAX.then(Waiters.AJAX));
	public static Link closeSession = new Link(By.xpath("//form[@id='loginForm']//a[contains(.,'Close CAS Session')]"), Waiters.AJAX);
	public static Link lnkLogout = new Link(By.xpath("//*[@id='logoutForm:logout_link']"), Waiters.AJAX);
	public static Dialog logoutDialog = new Dialog("//div[@id='logoutConfirmDialogDialog']");
	public static Link startPage = new Link(By.xpath("//form[@id='loginForm']//a[contains(.,'Start Page.')]"), Waiters.AJAX);
	public static Link timeOutStartPage = new Link(By.xpath("//input[contains(.,'Start Page.')]"), Waiters.AJAX);
	public static Link lnkSwitchToAdmin = new Link(By.xpath("//*[@id='logoutForm:switchToAdmin']"), Waiters.AJAX);
	public static AssetList login = new AssetList(By.tagName("body"), LoginPageMeta.class);

	public static boolean isPageDisplayed() {
		TextBox user = login.getAsset(LoginPageMeta.USER.getLabel(), TextBox.class);
		if (user.isPresent() && user.isVisible())
			return true;
		else
			return false;
	}

	public void verifyDisplayed() {
		CustomAssert.assertTrue("LoginPanel is displayed", isPageDisplayed());
	}

	public void fillLogin(String user, String pw) {
		startLogin();

		Map<String, Object> td = new LinkedHashMap<>();
		td.put(LoginPageMeta.USER.getLabel(), user);
		td.put(LoginPageMeta.PASSWORD.getLabel(), pw);
		SimpleDataProvider dp = new SimpleDataProvider(td);
		login.setValue(dp);
		btnLogin.click();
	}

	public TestData fillLogin(TestData td) {
		startLogin();
		if (td.containsKey(LoginPageMeta.STATES.getLabel()))
			login.getAsset(LoginPageMeta.STATES.getLabel(), ListBox.class).unsetAllValues();
		if (td.containsKey(LoginPageMeta.GROUPS.getLabel()))
			login.getAsset(LoginPageMeta.GROUPS.getLabel(), ListBox.class).unsetAllValues();
		login.setValue(td);
		btnLogin.click();
		return td;
	}

	public void switchToAdmin() {
		lnkSwitchToAdmin.click();
	}

	public void logout() {
		if (Tab.buttonSaveAndExit.isPresent() && Tab.buttonSaveAndExit.isVisible()) {
			Tab.buttonSaveAndExit.click();
		}
		if (lnkLogout.isPresent() && lnkLogout.isVisible()) {
			lnkLogout.click();
			logoutDialog.confirm();
		}
		if(timeOutStartPage.isPresent() && timeOutStartPage.isVisible()) {
			timeOutStartPage.click();
		}
		if (closeSession.isPresent() && closeSession.isVisible()) {
			closeSession.click();
		}
	}

	private void startLogin() {
		logout();
		if (startPage.isPresent())
			startPage.click();
		verifyDisplayed();
	}

	@Override
	public void login(String username, String password, Boolean loginThroughURL) {
		fillLogin(username, password);
		// TODO Workaround: Sometimes system throws out with timeout
		if (!(lnkLogout.isPresent() && lnkLogout.isVisible())) {
			// Session time-out screen
			if (startPage.isPresent())
				startPage.click();
			if(isPageDisplayed())
				fillLogin(username, password);
		}
		//setApplicationLogFileName();
	}

	@Override
	public void login() {
		Map<String, Object> td = new LinkedHashMap<>();
		td.put(LoginPageMeta.USER.getLabel(), user);
		td.put(LoginPageMeta.PASSWORD.getLabel(), password);
		td.put(LoginPageMeta.STATES.getLabel(), state);
		login(new SimpleDataProvider(td));
	}

	@Override
	public void login(Boolean loginViaURL) {
		login(user, password, true);
	}

	@Override
	public void login(String username, String password) {
		login(username, password, true);
	}

	@Override
	public void login(TestData td) {
		login(td, true);
	}

	@Override
	public void login(TestData td, Boolean loginViaURL) {
		fillLogin(td);
		// TODO Workaround: Sometimes system throws out with timeout
		if (!(lnkLogout.isPresent() && lnkLogout.isVisible())) {
			// Session time-out screen
			if (startPage.isPresent())
				startPage.click();
			if(isPageDisplayed())
				fillLogin(td);
		}
		setApplicationLogFileName(td.getValue(LoginPageMeta.STATES.getLabel()));
	}

	private void setApplicationLogFileName(String state) {
		String metghodName =  "TestNameWasNotFound";
		StackTraceElement result = Arrays.stream(Thread.currentThread().getStackTrace()).filter(s -> s.getClassName().startsWith("aaa.modules")).reduce((a, b) -> b).orElse(null);
		if (result != null) {
			metghodName = result.getClassName() + "." + result.getMethodName();
		}
		BrowserController.get().open(BrowserController.get().driver().getCurrentUrl().replace("#noback", "") + "&scenarioName=" + metghodName + "_" + state);
	}

}
