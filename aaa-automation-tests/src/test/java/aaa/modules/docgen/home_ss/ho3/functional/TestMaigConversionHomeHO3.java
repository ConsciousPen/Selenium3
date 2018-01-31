/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.docgen.home_ss.ho3.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.xml.model.Document;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.conversion.manual.ManualConversionUtil;
import aaa.modules.policy.HomeSSHO3BaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

import static aaa.main.enums.DocGenEnum.Documents.HSPRNXX;

public class TestMaigConversionHomeHO3 extends HomeSSHO3BaseTest {

    int PRE_RENEWAL_LETTER_TIMELINE = 40;

    private static final String POLICY_TYPE_PATH = TestData.makeKeyPath(
            CustomerMetaData.InitiateRenewalEntryActionTab.class.getName(),
            CustomerMetaData.InitiateRenewalEntryActionTab.POLICY_TYPE.getLabel());

    /**
     * @name Test MAIG Document generation (Pre-renewal package)
     * @scenario 1. Create Customer
     * 2. Initiate MAIG Renewal Entry
     * 3. Fill Conversion Policy data
     * 3. Check that HSPRNXX document section is getting generated
     * @details
     */
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = {"PAS-2305"})
    public void pas2305_preRenewalLetterHSPRNXX(@Optional("VA") String state) {
        CustomAssert.enableSoftMode();
        mainApp().open();

        //Populate individual test data, initiate renewal entry
        initiateManualConversionForTest();

        policy.getDefaultView().fillUpTo(getPolicyTD(), BindTab.class, false);
        policy.getDefaultView().getTab(BindTab.class).submitTab();

        //policy generated, proceed with PreRenewal
        String policyNumber = PolicySummaryPage.linkPolicy.getValue();
        TimeSetterUtil.getInstance().nextPhase(PolicySummaryPage.getEffectiveDate().minusDays(PRE_RENEWAL_LETTER_TIMELINE));
        JobUtils.executeJob(Jobs.aaaPreRenewalNoticeAsyncJob);

        //check pre-renewal package
        Document document = DocGenHelper.waitForDocumentsAppearanceInDB(HSPRNXX, policyNumber, "PRE_RENEWAL");

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * Initiate Customer Individual test data, fill the InitiateRenewalEntry
     * ToDo: find a more flexible way to define the short Product Name
     */
    public void initiateManualConversionForTest() {
        TestData customerTestData = super.getCustomerIndividualTD("InitiateRenewalEntry", "TestData_Home_MAIG")
                .adjust(POLICY_TYPE_PATH, ManualConversionUtil.getShortPolicyType(getPolicyType()));
        initiateManualConversion(customerTestData);
    }

    /**
     * Retrieve {@link TestData} for Conversion policy
     */
    public TestData getConversionPolicyTD() {
        return super.getPolicyTD("Conversion", "TestData_ConversionHomeSS");
    }

}
