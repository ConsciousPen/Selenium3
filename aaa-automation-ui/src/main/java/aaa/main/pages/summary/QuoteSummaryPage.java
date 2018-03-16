/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.pages.summary;

import static org.assertj.core.api.Assertions.assertThat;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import aaa.common.components.Dialog;
import aaa.common.pages.MainPage;
import aaa.main.modules.policy.PolicyType;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

public class QuoteSummaryPage extends MainPage {
	public static final String DIALOG_META = "ProductInitiateDialog";
	public static final String PERSONAL_LINES = "Personal Lines";

	public static Button buttonAddNewQuote = new Button(By.xpath("//input[@id='quoteForm:newQuoteButton']"), Waiters.AJAX.then(Waiters.SLEEP(5000)));
	public static Dialog dialogSelectProduct = new Dialog("//div[@id='quoteForm:quotePreCreationPopup_container']");
	public static Table tablePremiumSummary = new Table(By.xpath("//table[contains(@id,'productConsolidatedViewForm')]"));
	public static Table tableQuoteList = new Table(By.id("quotePageContents:body_quote_list_table"));
	public static ComboBox broadLineOfBusiness =
			new ComboBox(By.xpath("//*[@id='quoteForm:quoteCreationPopup_container']//select[@id='quoteForm:quoteCreationPopupMultiEdit_blob']"), Waiters.AJAX.then(Waiters.AJAX));
	public static ComboBox product =
			new ComboBox(By.xpath("//*[@id='quoteForm:quoteCreationPopup_container']//select[@id='quoteForm:quoteCreationPopupMultiEdit_productCd']"), Waiters.AJAX.then(Waiters.AJAX));
	public static Button nextBtn = new Button(By.xpath("//*[@id='quoteForm:quoteCreationPopup_container']//input[@id='quoteForm:createQuoteButton']"));
	public static Button cancelBtn = new Button(By.xpath("//*[@id='quoteForm:quoteCreationPopup_container']//input[@id='quoteForm:cancelButton']"));

	public static void initiateQuote(PolicyType policyType) {
		buttonAddNewQuote.click();
		broadLineOfBusiness.setValue(PERSONAL_LINES);
		assertThat(StringUtils.isNotBlank(broadLineOfBusiness.getValue())).isTrue();
		product.setValue(policyType.getName());
		assertThat(StringUtils.isNotBlank(product.getValue())).isTrue();
		nextBtn.click();
		assertThat(buttonAddNewQuote.isPresent()).isFalse();
	}
}
