/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common.pages;

import java.time.LocalDateTime;
import org.openqa.selenium.By;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.StaticElement;

public class QuoteDataGatherPage extends MainPage {

	public static StaticElement labelCustomerName = new StaticElement(By.xpath("//div[@id='policyDataGatherForm:pathContextPlaceHolder']//td[1]"));
	public static StaticElement labelQuoteNumber = new StaticElement(By.xpath("//div[@id='policyDataGatherForm:pathContextPlaceHolder']//td[2]"));
	public static StaticElement labelEffectiveDate = new StaticElement(By.xpath("//div[@id='policyDataGatherForm:pathContextPlaceHolder']//td[3]"));
	public static StaticElement labelTransEffectiveDate = new StaticElement(By.xpath("//div[@id='policyDataGatherForm:pathContextPlaceHolder']//td[4]"));


	public static String getQuoteNumber() {
		return labelQuoteNumber.getValue().replaceAll("Quote #", "").trim();
	}

	public static LocalDateTime getEffectiveDate() {
		return TimeSetterUtil.getInstance().parse(labelEffectiveDate.getValue().replaceAll("Eff. Date:", "").trim(), DateTimeUtils.MM_DD_YYYY);
	}

	public static LocalDateTime getTransEffectiveDate() {
		return TimeSetterUtil.getInstance().parse(labelTransEffectiveDate.getValue().replaceAll("Trans. Eff. Date:", "").trim(), DateTimeUtils.MM_DD_YYYY);
	}
}
