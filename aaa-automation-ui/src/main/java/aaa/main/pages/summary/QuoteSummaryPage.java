/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.pages.summary;

import static toolkit.verification.CustomAssertions.assertThat;

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
	public static Table tablePremiumSummary = new Table(By.xpath("//table[contains(@id,'productConsolidatedViewForm')]"));
	public static Table tableQuoteList = new Table(By.id("quotePageContents:body_quote_list_table"));
	public Button buttonAddNewQuote = new Button(By.xpath("//input[@id='quoteForm:newQuoteButton']"));
	public ComboBox broadLineOfBusiness = new ComboBox(By.xpath("//*[@id='quoteForm:quoteCreationPopup_container']//select[@id='quoteForm:quoteCreationPopupMultiEdit_blob']"));
	public ComboBox product = new ComboBox(By.xpath("//*[@id='quoteForm:quoteCreationPopup_container']//select[@id='quoteForm:quoteCreationPopupMultiEdit_productCd']"));
	public Button nextBtn = new Button(By.xpath("//*[@id='quoteForm:quoteCreationPopup_container']//input[@id='quoteForm:createQuoteButton']"), Waiters.AJAX.then(Waiters.AJAX));
	public Button cancelBtn = new Button(By.xpath("//*[@id='quoteForm:quoteCreationPopup_container']//input[@id='quoteForm:cancelButton']"));
	public Dialog dialogSelectProduct = new Dialog("//div[@id='quoteForm:quotePreCreationPopup_container']");

	public void initiateQuote(PolicyType policyType) {
		buttonAddNewQuote.click();
		broadLineOfBusiness.setValue(PERSONAL_LINES);
		product.setValue(policyType.getName());
		nextBtn.click();
	}
	
	public void verifyProductDoesNotContainOption(PolicyType policyType) {
		buttonAddNewQuote.click();
		broadLineOfBusiness.setValue(PERSONAL_LINES);
		assertThat(product).as("User can select a product: " + policyType.getName()).doesNotContainOption(policyType.getName());
	}
}
