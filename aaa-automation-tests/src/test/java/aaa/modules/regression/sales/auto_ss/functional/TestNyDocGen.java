package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.main.enums.DocGenEnum.Documents.*;
import static aaa.main.metadata.policy.AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.SUPPLEMENTARY_UNINSURED_MOTORISTS_COVERAGE_REJECTION;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverTab.LICENSE_STATE;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.AAAProductOwned.LAST_NAME;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.POLICY_INFORMATION;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.PolicyInformation.LEAD_SOURCE;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TERM;
import static aaa.main.metadata.policy.AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY;
import static aaa.main.metadata.policy.AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN;
import static aaa.main.metadata.policy.AutoSSMetaData.PremiumAndCoveragesTab.RENTAL_REIMBURSEMENT;
import static aaa.main.metadata.policy.AutoSSMetaData.PremiumAndCoveragesTab.SUPPLEMENTAL_SPOUSAL_LIABILITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.xml.models.DataElementChoice;
import aaa.helpers.xml.models.Document;
import aaa.helpers.xml.models.DocumentDataElement;
import aaa.helpers.xml.models.DocumentDataSection;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.RadioGroup;

public class TestNyDocGen extends AutoSSBaseTest {

    private static final Set<DocGenEnum.Documents> DOC_TO_CHECK = EnumSet.of(AADNNY1, AAINXX1, AAMTNY, AASANY, AAOANY, AAACNY);
    //use List instead of EnumSet to determine sequence
    private static final List<DocGenEnum.Documents> DOC_TO_CHECK_SEQUENCING = Arrays.asList(AAINXX1, AA02NY, AHPNXX, FS20, AACDNYR, AADNNY2, AARTNY,
            AARRNY, AASLNY, AAACNY, AADNNY1, AAMTNY, AASANY, AAOANY);

    private final Tab generalTab = new GeneralTab();
    private final Tab premiumCovTab = new PremiumAndCoveragesTab();
    private final Tab driverReportTab = new DriverActivityReportsTab();
    private final Tab docAndBind = new DocumentsAndBindTab();

    /**
     * * @author Igor Garkusha
     * @name Test Paperless Preferences properties and Inquiry mode
     * @scenario 1.  Initiate a manual entry conversion for SIS - NY from PAS..
     * 2.Navigate through the application and calculate the premium.
     * 3. Run the renewal process till Renewal Offer stage.
     * 4. Complete the renewal process on the policy.
     * 5. Make sure the docgen xml is generated with the template id with proper Policy Number
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "include PAS-2832 ,PAS-2448 ,PAS-2829 ,PAS-2830 ,PAS-2833 ,PAS-2831")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2832")
    public void pas2832_IdentificationCardNoticeAADNNY1(@Optional("NY") String state) {
        TestData policyTd = prepareConvTD(getPolicyTD(), state);
        String policyNumber = conversionPolicyPreconditions(policyTd);
        String getDataSql = String.format(AaaDocGenEntityQueries.GET_DOCUMENT_BY_POLICY_NUMBER, policyNumber, AaaDocGenEntityQueries.EvantNames.RENEWAL_OFFER);

        DOC_TO_CHECK.forEach(docID -> {
            //Select doc from DB
            List<DocumentDataSection> docData = DocGenHelper.getDocumentDataElemByName("PlcyNum", docID, getDataSql);
            assertThat(docData).isNotEmpty();

            DataElementChoice actualNode = docData.get(0).getDocumentDataElements().get(0).getDataElementChoice();
            //Check that doc contains expected node
            assertSoftly(softly -> softly.assertThat(actualNode).isEqualTo(new DataElementChoice().setTextField(policyNumber)));
        });

    }

    /**
     * * @author Igor Garkusha
     * @name Test Paperless Preferences properties and Inquiry mode
     * @scenario 1. Initiate a manual entry conversion for SIS - NY from PAS.
     * 2.Navigate through the application and calculate the premium with BI Limit as the very lowest option selected on P&C page.
     * 3. Run the renewal process till Renewal Offer stage.
     * 4.  Complete the renewal process on the policy.
     * 5. Make sure the docgen xml is generated with the template id AA52NY and Physically Signed data in XML
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "include PAS-2834,PAS-2835")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2834")
    public void pas2832_docAA52PhysicallySigned(@Optional("NY") String state) {

        DocumentDataElement expectedElem = new DocumentDataElement().setName("SgnReqYN").
                setDataElementChoice(new DataElementChoice().setTextField("Y"));

        aa52TestBody("Physically Signed", "SgnReqYN", state, expectedElem);
    }

    /**
     * * @author Igor Garkusha
     * @name Test Paperless Preferences properties and Inquiry mode
     * @scenario 1. Initiate a manual entry conversion for SIS - NY from PAS.
     * 2.Navigate through the application and calculate the premium with BI Limit as the very lowest option selected on P&C page.
     * 3. Run the renewal process till Renewal Offer stage.
     * 4.  Complete the renewal process on the policy.
     * 5. Make sure the docgen xml is generated with the template id AA52NY and Electronically Signed data in XML
     * @details include
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "include PAS-2834,PAS-2835")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2834")
    public void pas2832_docAA52ElectronicallySigned(@Optional("NY") String state) {

        DocumentDataElement expectedElem = new DocumentDataElement().setName("SgntrOnFile").
                setDataElementChoice(new DataElementChoice().setTextField("SIGNATURE ON FILE"));

        aa52TestBody("Electronically Signed", "SgntrOnFile", state, expectedElem);
    }

    /**
     * * @author Igor Garkusha
     * @name Test Paperless Preferences properties and Inquiry mode
     * @scenario 1. Initiate a manual entry conversion for SIS - NY from PAS.
     * 2.Navigate through all tabs, enter required informantion, calculate premium and bind the policy.
     * 3. Check if Conversion Renewal Packet is generated.
     * 4. Check the forms sequence on XML.
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-4172")
    public void pas4172_sequencingForNYConversionDocs(@Optional("NY") String state) {
        TestData policyTd = prepareConvTD(getPolicyTD(), state);
        policyTd.
                adjust(TestData.makeKeyPath(premiumCovTab.getMetaKey(), SUPPLEMENTAL_SPOUSAL_LIABILITY.getLabel()), "Yes").
                adjust(TestData.makeKeyPath(premiumCovTab.getMetaKey(), RENTAL_REIMBURSEMENT.getLabel()), "index=1");
        String policyNumber = conversionPolicyPreconditions(policyTd);
        String getDataSql = String.format(AaaDocGenEntityQueries.GET_DOCUMENT_BY_POLICY_NUMBER, policyNumber, AaaDocGenEntityQueries.EvantNames.RENEWAL_OFFER);
        Document privDoc = null;

        for (DocGenEnum.Documents doc : DOC_TO_CHECK_SEQUENCING) {

            Document currentDoc = DocGenHelper.getDocument(doc, getDataSql);
            assertThat(currentDoc).isNotNull();

            if (privDoc != null) {
                assertThat(Integer.parseInt(currentDoc.getSequence())).
                        isGreaterThan(Integer.parseInt(privDoc.getSequence()));
            }
            privDoc = currentDoc;
        }

    }

    private DocGenEnum.Documents getStateRelatedDoc(String state) {
        switch (state) {
            case Constants.States.NY:
                return AA52NY;
            case Constants.States.MT:
            default:
                return AA52MT;
        }
    }

    private void aa52TestBody(String signeType, String xmlTag, String state, DocumentDataElement expectedValue) {
        //Preconditions

        TestData policyTd = prepareConvTD(getPolicyTD().
                        adjust(TestData.makeKeyPath(premiumCovTab.getMetaKey(), BODILY_INJURY_LIABILITY.getLabel()), "index=0"),
                state);

        policyTd.adjust(TestData.makeKeyPath(docAndBind.getMetaKey(), "RequiredToBind", SUPPLEMENTARY_UNINSURED_MOTORISTS_COVERAGE_REJECTION.getLabel()),
                signeType);
        String policyNumber = conversionPolicyPreconditions(policyTd);

        //Select data from DB
        String getDataSql = String.format(AaaDocGenEntityQueries.GET_DOCUMENT_BY_POLICY_NUMBER, policyNumber, AaaDocGenEntityQueries.EvantNames.RENEWAL_OFFER);

        //Get actual value
        DocumentDataSection docData = DocGenHelper.getDocumentDataElemByName(xmlTag, getStateRelatedDoc(state), getDataSql).get(0);

        //Compare with actual value
        assertSoftly(softly -> softly.assertThat(docData.getDocumentDataElements()).contains(expectedValue));
    }

    private String conversionPolicyPreconditions(TestData policyTd) {
        mainApp().open();
        initiateManualConversionR35();

        policy.getDefaultView().fillUpTo(policyTd, DriverActivityReportsTab.class);
        driverReportTab.getAssetList().getAsset(VALIDATE_DRIVING_HISTORY.getLabel(), Button.class).click();
        driverReportTab.getAssetList().getAsset(SALES_AGENT_AGREEMENT.getLabel(), RadioGroup.class).setValue("I Agree");
        driverReportTab.submitTab();
        docAndBind.fillTab(policyTd);
        DocumentsAndBindTab.btnPurchase.click();
        Page.dialogConfirmation.confirm();
        JobUtils.executeJob(Jobs.renewalJob);
        return PolicySummaryPage.linkPolicy.getValue();

    }

    private TestData prepareConvTD(TestData policyTd, String state) {
        return policyTd.mask(TestData.makeKeyPath(generalTab.getMetaKey(), POLICY_INFORMATION.getLabel(), EFFECTIVE_DATE.getLabel())).
                mask(TestData.makeKeyPath(generalTab.getMetaKey(), POLICY_INFORMATION.getLabel(), LEAD_SOURCE.getLabel())).
                mask(TestData.makeKeyPath(generalTab.getMetaKey(), AAA_PRODUCT_OWNED.getLabel(), LAST_NAME.getLabel())).
                adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AAA_PRODUCT_OWNED.getLabel(), CURRENT_AAA_MEMBER.getLabel()), "No").
                mask(TestData.makeKeyPath(premiumCovTab.getMetaKey(), POLICY_TERM.getLabel())).
                adjust(TestData.makeKeyPath(premiumCovTab.getMetaKey(), PAYMENT_PLAN.getLabel()), "Annual (Renewal)").
                adjust(TestData.makeKeyPath(new DriverTab().getMetaKey(), LICENSE_STATE.getLabel()), state).
                adjust(TestData.makeKeyPath(new VehicleTab().getMetaKey(), AutoSSMetaData.VehicleTab.TYPE.getLabel()), "Private Passenger Auto").
                mask(TestData.makeKeyPath(docAndBind.getMetaKey(), "Agreement"));

    }
}
