/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.auto_ca;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import aaa.common.Tab;
import aaa.common.components.Efolder;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.actiontabs.GenerateProposalActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaBaseTest;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.StaticElement;

/**
 * @author Marina Buryak
 * @name Test Propose Auto Quote
 * @scenario
 * 1. Create Customer
 * 2. Create Auto Quote
 * 3. Try to propose quote(click 'Cancel' button on propose screen)
 * 4. Verify quote status id 'Premium Calculated'
 * 5. Propose Quote
 * 6. Verify eFolder 'Applications and Proposals/New Business' was created
 * 7. Verify quote status is 'Proposed'
 * @details
 */
public class TestQuotePropose extends AutoCaBaseTest {

    @Test(groups = "7.2_DISABILITY,LIFE,AUTO,CGL,PROPC,AUTOP,HOME,UMBRP,ACHE,GROUP,INMRC_UC_ProposeQuote")
    @TestInfo(component = "Policy.PersonalLines")
    public void testQuotePropose() {
        mainApp().open();

        createCustomerIndividual();

        createQuote();

        log.info("TEST: Click Cancel button on Propose screen for Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.propose().start();
        Tab.buttonCancel.click();
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        log.info("TEST: Propose Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.propose().start();
        policy.propose().getView().getTab(GenerateProposalActionTab.class).getAssetList()
                .getControl(AutoCaMetaData.GenerateProposalActionTab.NOTES.getLabel()).verify.enabled();
        new StaticElement(By.id("policyDataGatherForm:note")).verify.value("Please note that once you click \"OK\" the documents will be queued for generation " +
                "and will be available for viewing within the folder structure as soon as they have been successfully processed. This usually takes 3 to 5 minutes.");
        policy.propose().submit();
        Efolder.isDocumentExist("Applications and Proposals", "New Business");

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PROPOSED);
    }
}
