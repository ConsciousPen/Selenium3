package aaa.modules.regression.document_fulfillment.template.functional;

import aaa.common.pages.SearchPage;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.Job;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ca.AutoCaPolicyActions;
import aaa.main.modules.policy.auto_ss.AutoSSPolicyActions;
import aaa.main.pages.summary.PolicySummaryPage;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.inject.internal.ImmutableList;
import com.google.inject.internal.ImmutableMap;
import org.junit.Assert;
import toolkit.datax.TestData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.PRE_RENEWAL;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER;
import static java.util.Arrays.asList;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class TestCinAbstractAutoSS extends TestCinAbstract{

    protected static final Map<AaaDocGenEntityQueries.EventNames, List<Job>> JOBS_FOR_EVENT =
            ImmutableMap.of(PRE_RENEWAL, ImmutableList.of(Jobs.aaaBatchMarkerJob, Jobs.aaaPreRenewalNoticeAsyncJob),
                    RENEWAL_OFFER, ImmutableList.of(Jobs.aaaBatchMarkerJob, Jobs.renewalOfferGenerationPart2, Jobs.aaaDocGenBatchJob));



    public static final String DISABLE_MEMBERSHIP = TestData.makeKeyPath(
            AutoSSMetaData.GeneralTab.class.getSimpleName(),
            AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());

    public static final String SUPPRESS_INSURANCE_SCORE_TRIGGER = TestData.makeKeyPath(
            AutoSSMetaData.RatingDetailReportsTab.class.getSimpleName(),
            AutoSSMetaData.RatingDetailReportsTab.INSURANCE_SCORE_OVERRIDE.getLabel());

    public static final String SUPPRESS_PRIOR_BI_TRIGGER = TestData.makeKeyPath(
            AutoSSMetaData.GeneralTab.class.getSimpleName(),
            AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(),
            AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS.getLabel());

    public static final String DRIVER_ACTIVITY_REPORTS_PATH = TestData.makeKeyPath(
            AutoSSMetaData.DriverActivityReportsTab.class.getSimpleName());

    public static final String NAME_INSURED_FIRST_NAME = TestData.makeKeyPath(
            AutoSSMetaData.GeneralTab.class.getSimpleName(),
            AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel()+"[0]",
            AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME.getLabel());

    public static final String NAME_INSURED_LAST_NAME = TestData.makeKeyPath(
            AutoSSMetaData.GeneralTab.class.getSimpleName(),
            AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel()+"[0]",
            AutoSSMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME.getLabel());

    public static final String INSURANCE_SCORE_OVERRIDE = TestData.makeKeyPath(
            AutoSSMetaData.RatingDetailReportsTab.class.getSimpleName(),
            AutoSSMetaData.RatingDetailReportsTab.INSURANCE_SCORE_OVERRIDE.getLabel());

    public static final String CURRENT_CARRIER_INFORMATION = TestData.makeKeyPath(
            AutoSSMetaData.GeneralTab.class.getSimpleName(),
            AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel());

    public static final String REQUIRED_TO_ISSUE = TestData.makeKeyPath(
            AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(),
            AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel());

    public static final String ADJUST_ELC = TestData.makeKeyPath(
            AutoSSMetaData.GeneralTab.class.getSimpleName(),
            AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
            AutoSSMetaData.GeneralTab.PolicyInformation.EXTRAORDINARY_LIFE_CIRCUMSTANCE.getLabel());

    public static final String ERROR_TAB_CALCULATE_PREMIUM = TestData.makeKeyPath(
            AutoSSMetaData.ErrorTabCalculatePremium.class.getSimpleName());

    public static final String ERROR_TAB = TestData.makeKeyPath(
            AutoSSMetaData.ErrorTab.class.getSimpleName());

    protected Document ssNewBusinessMainFlow(TestData testData) {

        String policyNumber = createPolicy(testData);

        //wait for CIN specific form and a package itself to appear in the DB
        return DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE);

        //ToDo: verify the order
    }

    public void renewPolicy(String policyNumber, TestData renewalTD) {
        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        LocalDateTime renewImageGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
        TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        new AutoSSPolicyActions.Renew().performAndFill(renewalTD);

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        String renewedPolicyNumber = PolicySummaryPage.getPolicyNumber();
    }

    protected void verifyCinGenerated (Document cinDocument, String policyNumber) {
        Assert.assertNotNull(getPolicyErrorMessage("CIN document failed to generate", policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE), cinDocument);
    }

    protected void verifyCinNotGenerated (Document cinDocument, String policyNumber) {
        Assert.assertNull(getPolicyErrorMessage("CIN document mustn't have been generated, but it's there", policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE), cinDocument);
    }

    public TestData adjustNameInsured(TestData testData, String nameInsuredTestData) {
        TestData nameInsuredTD = getTestSpecificTD(nameInsuredTestData);

        String firstName = nameInsuredTD.getValue("First Name");
        String lastName = nameInsuredTD.getValue("Last Name");

        return testData.adjust(NAME_INSURED_FIRST_NAME, firstName)
                .adjust(NAME_INSURED_LAST_NAME, lastName);
    }

    protected void assertStateEquals(String state, String... applicableStates) {
        assertTrue(asList(applicableStates).contains(state), "Test does not support this state: " + state);
    }

    protected void assertStateNotEquals(String state, String... applicableStates) {
        assertFalse(asList(applicableStates).contains(state), "Test does not support this state: " + state);
    }

}
