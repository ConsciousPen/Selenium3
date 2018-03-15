/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.common.pages;

import org.openqa.selenium.By;
import toolkit.exceptions.IstfException;
import toolkit.webdriver.controls.Button;

public class MainPage extends Page {

	public static class QuickSearch {
		public static Button buttonSearchPlus = new Button(By.id("aaaSearchForm:searchLink"));

		public static void search(String searchValue) {
			//TODO remove
			throw new IstfException("Unsupported method!!");
		}
	}
}
