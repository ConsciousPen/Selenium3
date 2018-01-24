package aaa.modules.docgen.template.functional;

import static aaa.main.enums.DocGenEnum.Documents.HSPRNXX;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.xml.model.Document;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import aaa.modules.conversion.manual.ManualConversionHelper;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

public interface TestMaigConversionAbstract {

    int PRE_RENEWAL_LETTER_TIMELINE = 40;

    ManualConversionHelper getManualConversionHelper();

    Tab getBindTab();

    void initiateManualConversionForTest();

    IPolicy getPolicy();

    TestData getPolicyTD();

    PolicyType getPolicyType();

    BaseTest getBaseTest();

    default void pas2305_preRenewalLetterHSPRNXXProductSpecific(String state) {
        CustomAssert.enableSoftMode();
        getBaseTest().mainApp().open();
        initiateManualConversionForTest();
        getPolicy().getDefaultView().fillUpTo(getManualConversionHelper().modifyConversionPolicyDefaultTD(getPolicyTD(),getPolicyType()), BindTab.class, false);
        getBindTab().submitTab();
        String policyNumber = PolicySummaryPage.linkPolicy.getValue();
        TimeSetterUtil.getInstance().nextPhase(PolicySummaryPage.getEffectiveDate().minusDays(PRE_RENEWAL_LETTER_TIMELINE));
        JobUtils.executeJob(Jobs.aaaPreRenewalNoticeAsyncJob);
        Document document = DocGenHelper.waitForDocumentsAppearanceInDB(HSPRNXX, policyNumber, "PRE_RENEWAL");
        //TODO shoulb be retured after docGen update
        /*        String plcyPrfx = DocGenHelper.getDocumentDataElemByName("PlcyPrfx", document).getDataElementChoice().getTextField();
        String plcyNum = DocGenHelper.getDocumentDataElemByName("PlcyNum", document).getDataElementChoice().getTextField();
        CustomAssert.assertTrue(MessageFormat.format("Problem is in tags: [{0}], [{1}]", "PlcyPrfx", "PlcyNum"), policyNumber
                .equals(plcyPrfx+plcyNum));*/
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

}
