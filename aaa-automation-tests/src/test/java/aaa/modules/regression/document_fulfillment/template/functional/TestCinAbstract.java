package aaa.modules.regression.document_fulfillment.template.functional;

import aaa.common.pages.SearchPage;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.xml.model.Document;
import aaa.helpers.xml.model.DocumentDataElement;
import aaa.helpers.xml.model.DocumentDataSection;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.auto_ca.AutoCaPolicyActions;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;

import java.time.LocalDateTime;

public abstract class TestCinAbstract extends BaseTest {
    IPolicy policy;

    /**
     * Prepares error message for logging
     *
     * @param msg error message
     * @return error message containing data about policy and DocGen even
     */
    protected String getPolicyErrorMessage(String msg, String policyNumber, AaaDocGenEntityQueries.EventNames event) {
        return msg + " for policy: " + policyNumber + ", event: " + event;
    }

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
     * Create a policy base on custom {@link TestData}
     *
     * @param policyTD
     * @return policyNumber
     */
    protected String createPolicy (TestData policyTD) {

        mainApp().open();
        createCustomerIndividual();
        super.createPolicy(policyTD);

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        return PolicySummaryPage.getPolicyNumber();
    }
}
