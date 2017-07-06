/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.pages.summary;

import org.openqa.selenium.By;

import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.table.Table;

public class QuoteProposalSummaryPage extends SummaryPage {


    public static StaticElement proposalErrorMessage = new StaticElement(By.xpath("//table[@id='proposalForm:proposalGlobalErrors']//ul[contains(@class,'error_message')]"));
    public static StaticElement masterQuoteErrorMessage = new StaticElement(By.xpath("//div[@id='proposalForm:proposalsQuotesSelectionTable']//table//span[contains(@class,'error_message')]"));

    public static StaticElement assumptionsErrorMessage = new StaticElement(
            By.xpath("//div[@id='proposalForm:proposalsQuotesSelectionTable']//table[contains(@class,'formGrid')]//label[contains(@class,'error')]"));

    public static Table tableProposalQuotesSelection = new Table(By.xpath("//div[@id='proposalForm:proposalQuotesSelectionTable']//table"));
    public static Table tableAssumptions = new Table(By.xpath("//div[@id='proposalForm:proposalsQuotesSelectionTable']//table[contains(@class,'formGrid')]"));

    public static TextBox textBoxProposalName = new TextBox(By.id("proposalForm:proposalInfoEdit_proposalName"));

    public static Button buttonStartProposal = new Button(By.id("proposalForm:proposalSaveAndExitBtn_footer"));
    public static Button buttonCalculatePremium = new Button(By.xpath("//input[@value = 'Calculate Premium' and not(@class = 'hidden') and not(contains(@style,'none'))]"));
    public static Button buttonGenerateProposal = new Button(By.id("proposalForm:generateProposalBtn_footer"));
}
