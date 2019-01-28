package aaa.modules.regression.document_fulfillment.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.xml.model.Document;
import aaa.helpers.xml.model.DocumentDataElement;
import aaa.helpers.xml.model.DocumentDataSection;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.policy.IPolicy;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;

public abstract class TestCinAbstract extends BaseTest {
    IPolicy policy;

    protected static final String CIN_DOCUMENT_MISSING_ERROR = "CIN document must have been generated.";
    protected static final String CIN_DOCUMENT_REDUNDANT_ERROR = "CIN document mustn't have been generated, but it's there.";

    /**
     * Prepares error message for logging
     *
     * @param msg error message
     * @return error message containing data about policy and DocGen even
     */
    protected String getPolicyErrorMessage(String msg, String policyNumber, AaaDocGenEntityQueries.EventNames event) {
        return msg + " for policy: " + policyNumber + ", event: " + event;
    }

    /**
     * Retrieves a value from a provided {@link Document} based on Section Name and Field Name
     *
     * @param sectionName
     * @param fieldName
     * @param cinDocument
     * @return error message containing data about policy and DocGen even
     */
    protected String retrieveElementValue(Document cinDocument, String sectionName, String fieldName) {
        String elementValue = null;
        for (DocumentDataSection documentDataSection : cinDocument.getDocumentDataSections()) {
            if (sectionName.equals(documentDataSection.getSectionName())) {
                for (DocumentDataElement documentDataElement : documentDataSection.getDocumentDataElements()) {
                    if (fieldName.equals(documentDataElement.getName())) {
                        elementValue = documentDataElement.getDataElementChoice().getTextField();
                    }
                }
            }
        }
        return elementValue;
    }

    /**
     * Perform a manual renewal on a policy specified by Policy Number with custom {@link TestData}
     *
     * @param policyNumber
     * @param renewalTD    {@link TestData}
     */
    public void renewPolicy(String policyNumber, TestData renewalTD) {
        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        LocalDateTime renewImageGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate).minusHours(1);
        TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        performRenewal(renewalTD);

        assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }

    /**
     * Perform a manual renewal on a policy specified by Policy Number with custom {@link TestData}
     * Method with workaround for test goes in common run with renewal jobs.
     *
     * @param policyNumber
     * @param renewalTD    {@link TestData}
     * @param doNotRenewTD
     */
    public void renewPolicy(String policyNumber, TestData renewalTD, TestData doNotRenewTD) {
        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        //Workaround in case test goes in common run with renewalOfferGenerationPart2 jobs are running
        performDoNotRenew(doNotRenewTD);

        LocalDateTime renewImageGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate).minusHours(1);
        TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);

        mainApp().reopen();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

        //Workaround in case test goes in common run with renewalOfferGenerationPart2 jobs are running
        performRemoveDoNotRenew();

        performRenewal(renewalTD);

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }

    /**
     * Create a policy based on custom {@link TestData}
     *
     * @param policyTD
     * @return policyNumber
     */
	@Override
	protected String createPolicy(TestData policyTD) {
        mainApp().open();
        createCustomerIndividual();
        super.createPolicy(policyTD);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        return PolicySummaryPage.getPolicyNumber();
    }

	/**
	 * Placeholder for product-specific renewal
	 *
	 * @param renewalTD {@link TestData}
	 */
	protected abstract void performRenewal(TestData renewalTD);

	protected abstract void performDoNotRenew(TestData doNotRenewTD);

	protected abstract void performRemoveDoNotRenew();
}
