/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.pup;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import aaa.common.Tab;
import aaa.common.components.Efolder;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.pup.actiontabs.ProposeActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;

/**
 * @author Deivydas Piliukaitis
 * @name Test Propose Umbrella Quote
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (Preconfigured) Quote
 * 3. Propose Quote
 * 4. Verify policy status is 'Proposed'
 * @details
 */
public class TestQuotePropose extends PersonalUmbrellaBaseTest {

    @Test
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
        policy.propose().getView().getTab(ProposeActionTab.class).getAssetList().getAsset(PersonalUmbrellaMetaData.ProposeActionTab.NOTES.getLabel()).verify.enabled();
        new StaticElement(By.id("policyDataGatherForm:note")).verify.value("Please note that once you click \"OK\" the documents will be queued for generation " +
                "and will be available for viewing within the folder structure as soon as they have been successfully processed. This usually takes 3 to 5 minutes.");
        policy.propose().getView().getTab(ProposeActionTab.class).getAssetList().getAsset(PersonalUmbrellaMetaData.ProposeActionTab.NOTES.getLabel(), TextBox.class).setValue("Comment");
        policy.propose().submit();
        Efolder.isDocumentExist("Applications and Proposals", "New Business");

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PROPOSED);
    }
}
