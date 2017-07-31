/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.auto_ss;

import java.time.format.DateTimeFormatter;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.common.components.Efolder;
import aaa.common.pages.Page;
import aaa.main.enums.DocGenConstants;
import aaa.main.modules.policy.auto_ss.defaulttabs.PrefillTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * @author Viachaslau Markouski
 * @name Test Generate On Demand Document for Auto Quote
 * @scenario
 * 1. Create Customer
 * 2. Initiated Auto (Preconfigured) Quote
 * 3. Generate On Demand Document
 * 4. Verify document is generated successfully and saved in the specified folder.
 * @details
 */
public class TestQuoteDocGen extends AutoSSBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testQuoteDocGen() {
        mainApp().open();

        createCustomerIndividual();

        policy.initiate();
        PrefillTab.buttonSaveAndExit.click();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("Initiated Quote #" + policyNumber);

        log.info("TEST: DocGen for  Quote #" + policyNumber);
        policy.quoteDocGen().start();

        Table table = new Table(By.id("manualDocGenTemplateForm:documentTemplates"));
        String filepath = table.getRow(1).getCell(DocGenConstants.DocGendDcumentTemplatesTable.FOLDER).getValue();

        table.getRow(1).getCell(DocGenConstants.DocGendDcumentTemplatesTable.TEMPLATE_ID).controls.links.getFirst().click();
        new Button(By.id("manualDocGenForm:btnGenerate")).click();
        Page.dialogConfirmation.confirm();

        Efolder.isDocumentExist(filepath + "/" + TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
}
