/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.swaggerui;

import aaa.common.Tab;
import aaa.swaggerui.metadata.SwaggerUiMetaData;
import org.openqa.selenium.By;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 *
 * @category Generated
 */


public class SwaggerUiTab extends Tab {
	public static ComboBox swaggerServiceSelection = new ComboBox(By.id("baseUrl"));
	public static Link productOperations = new Link(By.xpath(".//*[@id='resource_product-operations']/div/h2/a"), Waiters.AJAX);
	public static Link productOperationsProcessOperations = new Link(By.xpath(".//*[@id='product-operations_processOperations']/div[1]/h3/span[2]/a"), Waiters.AJAX);
	public static Link productState = new Link(By.xpath(".//*[@id='resource_product-state']/div/h2/a"), Waiters.AJAX);
	public static Link productStateLoad = new Link(By.xpath(".//*[@id='product-state_load']/div[1]/h3/span[2]/a"), Waiters.AJAX);
	public static Link productStateStart = new Link(By.xpath(".//*[@id='product-state_start']/div[1]/h3/span[2]/a"), Waiters.AJAX);

	public static Link customerV1Endorsements = new Link(By.xpath(".//*[@id='endpointListTogger_/customer/v1/endorsements']"), Waiters.AJAX);
	public static Link customerV1EndorsementsPost = new Link(By.xpath(".//*[@id='/customer/v1/endorsements_updateEmail']/div[1]/h3/span[2]/a"), Waiters.AJAX);
	public static TextBox policyNumber = new TextBox(By.xpath(".//*[@id='/customer/v1/endorsements_updateEmail']//input[@name='policyNumber']"), Waiters.AJAX);
	public static TextBox updateEmailRequest = new TextBox(By.xpath(".//*[@id='/customer/v1/endorsements_updateEmail']//textarea[@name='updateEmailRequest']"), Waiters.AJAX);

	//common pattern xpath
	public static final String ROOT_XPATH = "/../../../../div[2]";
	public static final String MODEL_XPATH = "/p/span/div/div/div/div/div[2]/pre/code";
	public static final String BODY_XPATH = "//textarea[@class='body-textarea']";
	public static final String BUTTON_TRY_IT_XPATH = "//input[@value='Try it out!']";
	public static final String RESPONSE_BODY_XPATH = "//div[@class='block response_body hljs json']";
	public static final String RESPONSE_CODE_XPATH = "//div[@class='block response_code']";


	public SwaggerUiTab() {
		super(SwaggerUiMetaData.ServiceSelection.class);
	}

	/**
	 * gets root element xpath for sub element selection by generic xpath
	 * @param locator - link clicking to which all sub-fields are displayed
	 * @return selected root element locator
	 */
	private String getXpath0(By locator) {
		return (locator + ROOT_XPATH).replace("By.xpath: ", "");
	}

	/**
	 * gets the value of Model field
	 * @param locator - link clicking which all sub-fields are displayed
	 * @return Model field value
	 */
	public String getModelValue(By locator) {
		String xpath0 = getXpath0(locator);
		StaticElement modelField = new StaticElement(By.xpath(xpath0 + MODEL_XPATH));
		modelField.waitForAccessible(5000);
		return modelField.getValue();
	}

	/**
	 * sets the value to Body field
	 * @param locator - link clicking which all sub-fields are displayed
	 */
	public void setBodyValue(By locator, String value) {
		String xpath0 = getXpath0(locator);
		TextBox bodyField = new TextBox(By.xpath(xpath0 + BODY_XPATH));
		bodyField.waitForAccessible(5000);
		bodyField.setValue(value);
	}

	/**
	 * clicks Try It Out button for the selected request type
	 * @param locator - link clicking  which all sub-fields are displayed
	 */
	public void clickButtonTryIt(By locator) {
		String xpath0 = getXpath0(locator);
		Button buttonTryItOut = new Button(By.xpath(xpath0 + BUTTON_TRY_IT_XPATH));
		buttonTryItOut.waitForAccessible(5000);
		buttonTryItOut.click();
	}

	/**
	 * gets the value of Response Body field
	 * @param locator - link clicking which all sub-fields are displayed
	 * @return Response Body field value
	 */
	public String getResponseBodyValue(By locator) {
		String xpath0 = getXpath0(locator);
		StaticElement responseBodyField = new StaticElement(By.xpath(xpath0 + RESPONSE_BODY_XPATH));
		responseBodyField.waitForAccessible(5000);
		return responseBodyField.getValue();
	}

	/**
	 * gets the value of Response Code field
	 * @param locator - link clicking which all sub-fields are displayed
	 * @return Response Code field value
	 */
	public String getResponseCodeValue(By locator) {
		String xpath0 = getXpath0(locator);
		StaticElement responseCodeField = new StaticElement(By.xpath(xpath0 + RESPONSE_CODE_XPATH));
		responseCodeField.waitForAccessible(5000);
		return responseCodeField.getValue();
	}
}
