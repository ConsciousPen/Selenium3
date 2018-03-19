/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.pages.summary;

import static toolkit.verification.CustomAssertions.assertThat;
import org.openqa.selenium.By;
import aaa.common.components.Dialog;
import aaa.common.pages.MainPage;
import aaa.main.metadata.QuoteMetaData;
import aaa.main.modules.policy.PolicyType;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class QuoteSummaryPage extends MainPage {
	public static final String DIALOG_META = "ProductInitiateDialog";
	public static final String PERSONAL_LINES = "Personal Lines";
	public static Dialog dialogSelectProduct = new Dialog("//div[@id='quoteForm:quotePreCreationPopup_container']");
	public static Table tablePremiumSummary = new Table(By.xpath("//table[contains(@id,'productConsolidatedViewForm')]"));
	public static Table tableQuoteList = new Table(By.id("quotePageContents:body_quote_list_table"));
	public Button buttonAddNewQuote = new Button(By.xpath("//input[@id='quoteForm:newQuoteButton']"));
	public AbstractContainer<?, ?> initiateQuote = new AssetList(By.xpath("//*[@id='quoteForm:quoteCreationPopup_container']"), QuoteMetaData.InitiateQuote.class);

	public synchronized void initiateQuote(PolicyType policyType) {
		buttonAddNewQuote.click();

		TestData td = DataProviderFactory.dataOf(
				QuoteMetaData.InitiateQuote.BROAD_LINE_OF_BUSINESS.getLabel(), PERSONAL_LINES,
				QuoteMetaData.InitiateQuote.PRODUCT.getLabel(), policyType.getName(),
				QuoteMetaData.InitiateQuote.NEXT_BTN.getLabel(), "click"
		);

		TestData tdInitiateQuote = DataProviderFactory.dataOf(QuoteMetaData.InitiateQuote.class.getSimpleName(), td);

		initiateQuote.fill(tdInitiateQuote);
		assertThat(buttonAddNewQuote.isPresent()).isFalse();
	}
}
