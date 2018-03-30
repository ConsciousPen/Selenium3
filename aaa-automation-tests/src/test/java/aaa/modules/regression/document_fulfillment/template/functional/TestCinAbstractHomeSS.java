package aaa.modules.regression.document_fulfillment.template.functional;

import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.auto_ss.AutoSSPolicyActions;
import aaa.main.modules.policy.home_ss.HomeSSPolicyActions;
import aaa.main.pages.summary.PolicySummaryPage;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;

import java.time.LocalDateTime;

public class TestCinAbstractHomeSS extends TestCinAbstract{

    public static final String DISABLE_MEMBERSHIP = TestData.makeKeyPath(
            HomeSSMetaData.ApplicantTab.class.getSimpleName(),
            HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());

    public static final String MEMBERSHIP_REPORT_PATH = TestData.makeKeyPath(
            HomeSSMetaData.ReportsTab.class.getSimpleName(),
            HomeSSMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT.getLabel());

    public static final String APPLICANT_TAB_NAME_INSURED = TestData.makeKeyPath(
            HomeSSMetaData.ApplicantTab.class.getSimpleName(),
            HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel());

    public static final String REPORTS_TAB = TestData.makeKeyPath(
            HomeSSMetaData.ReportsTab.class.getSimpleName());

    public void renewPolicy(String policyNumber, TestData renewalTD) {
        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        LocalDateTime renewImageGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
        TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        new HomeSSPolicyActions.Renew().performAndFill(renewalTD);

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        String renewedPolicyNumber = PolicySummaryPage.getPolicyNumber();
    }
}
