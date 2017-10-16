/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.pages.summary;

import org.openqa.selenium.By;

import aaa.common.components.Dialog;
import aaa.common.pages.MainPage;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.table.Table;

public class QuoteSummaryPage extends MainPage {
    public static final String DIALOG_META = "ProductInitiateDialog";

    public static Button buttonAddNewQuote = new Button(By.xpath("//input[@id='quoteForm:actionButton_newQuote' or @id='quoteForm:newQuoteButton']"));
    public static ComboBox comboBoxProduct = new ComboBox(By.id("quoteForm:selectedProduct"));
    public static Dialog dialogSelectProduct = new Dialog("//div[@id='quoteForm:quotePreCreationPopup_container']");
    public static Table tablePremiumSummary = new Table(By.xpath("//table[contains(@id,'productConsolidatedViewForm')]"));
    public static Table tableQuoteList = new Table(By.id("quotePageContents:body_quote_list_table"));

}
