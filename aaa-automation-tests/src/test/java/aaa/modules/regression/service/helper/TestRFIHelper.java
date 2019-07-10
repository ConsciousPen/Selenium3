package aaa.modules.regression.service.helper;

import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.rest.dtoDxp.*;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.*;
import aaa.main.modules.policy.auto_ca.defaulttabs.ErrorTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.AbstractEditableStringElement;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.format.DateTimeFormatter;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public abstract class TestRFIHelper extends PolicyBaseTest {
    private static final TestMiniServicesCoveragesHelper COV_HELPER = new TestMiniServicesCoveragesHelper();
    private ErrorTab errorTab = new ErrorTab();
    private HelperMiniServices helperMiniServices = new HelperMiniServices();

    protected void verifyRFIScenarios(String coverageCd, AssetDescriptor<? extends AbstractEditableStringElement> coverageAsset, String updateLimitDXP, String updateLimitPAS, DocGenEnum.Documents document, AssetDescriptor<RadioGroup> documentAsset, ErrorEnum.Errors error, TestData td, boolean checkDocXML, boolean isRuleOverridden) {
        assertSoftly(softly -> {

            String policyNumber = policyCreationForRFI(coverageCd, updateLimitDXP, td);
            String doccId = checkDocumentInRfiService(policyNumber, document.getId(), document.getName());
            bindEndorsement(policyNumber, doccId, error.getCode(), error.getMessage(), isRuleOverridden);

            String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, document.getIdInXml(), AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
            if (checkDocXML) {
                verifyDocInDb(softly, query, document, true); //tags expected only for Electronically signed doc
            }

            //Go to PAS and verify
            goToPasAndVerifyRuleAndSignedBy(softly, policyNumber, documentAsset, coverageAsset, updateLimitPAS, error, isRuleOverridden);
            //Verify Signed by is not there in XML when Signed from PAS UI.
	        if (checkDocXML) {
		        if ((document.equals(DocGenEnum.Documents.AA52UPAA) || document.equals(DocGenEnum.Documents.AA52IPAA) || document.equals(DocGenEnum.Documents._550007) || document.equals(DocGenEnum.Documents.AAFPPA)) && !isRuleOverridden) { //isRuleOverridden means that Document was not signed.
			        DocGenHelper.checkDocumentsDoesNotExistInXml(policyNumber, AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE, document);// Document does not exist
		        } else {
			        validateDocSignTagsNotExist(document, query); //Document doesn't contain DocSignTags if signed in PAS
		        }
            }
        });
    }

    protected String policyCreationForRFI(String coverageId, String newCoverage, TestData td) {
        //Create Policy
        String policyNumber = openAppAndCreatePolicy(td);

        //Create endorsement
        helperMiniServices.createEndorsementWithCheck(policyNumber);
        //update coverage
        if (getState().equals(Constants.States.NJ) && coverageId.equals(CoverageInfo.PIPPRIMINS_NJ.getCode())) {
            Coverage covToUpdatePIPPRIMINS = Coverage.create(CoverageInfo.PIPPRIMINS_NJ).changeLimit(CoverageLimits.COV_PIPPRIMINS_PERSONAL_HEALTH_INSURANCE).addInsurerName("John"). addCertNum("12345");
            COV_HELPER.updateCoverageWithInsNameAndCertNum(policyNumber, covToUpdatePIPPRIMINS);
        } else {
            HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageId, newCoverage), PolicyCoverageInfo.class);
        }
        helperMiniServices.rateEndorsementWithCheck(policyNumber);
        //TODO-mstrazds: no document needs to be signed ?
        return policyNumber;
    }

    protected String checkDocumentInRfiService(String policyNumber, String documentCode, String documentName) {
        helperMiniServices.rateEndorsementWithCheck(policyNumber);
        RFIDocuments rfiServiceResponse = HelperCommon.rfiViewService(policyNumber, false);
        RFIDocument rfiDocument = rfiServiceResponse.documents.stream().filter(document -> document.documentCode.equals(documentCode)).findFirst().orElse(null);
        assertSoftly(softly -> {

            softly.assertThat(rfiServiceResponse.url).isNull();
            softly.assertThat(rfiDocument.documentCode).isEqualTo(documentCode);
            softly.assertThat(rfiDocument.documentName).isEqualTo(documentName);
            softly.assertThat(rfiDocument.documentId).startsWith(documentCode);
            softly.assertThat(rfiDocument.status).startsWith("NS");
            softly.assertThat(rfiDocument.parent).isEqualTo("policy");
            softly.assertThat(rfiDocument.parentOid).isNotEmpty();

            if (!PropertyProvider.getProperty("app.host").contains("aws")) {// Not checking document generation on AWS servers as DCS is not available from them

                RFIDocuments rfiServiceResponse2 = HelperCommon.rfiViewService(policyNumber, true);
                softly.assertThat(rfiServiceResponse2.url).endsWith(".pdf");
                softly.assertThat(rfiServiceResponse2.documents).isNotEmpty();

                //Verify that URL works
                HttpURLConnection con = null;
                try {
                    URL url = new URL(rfiServiceResponse2.url);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    softly.assertThat(con.getResponseCode()).isEqualTo(Response.Status.OK.getStatusCode());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        });

        return rfiDocument.documentId;
    }

    protected void bindEndorsement(String policyNumber, String doccId, String errorCode, String errorMessage, boolean isRuleOverridden) {
        if (!isRuleOverridden) {
            //Check that rule is fired when rule is not overridden. Not checking if rule is fired without signing, as per Digital flow it must always be signed.
            helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, errorCode, errorMessage);
        }
        //Bind policy with docId for successful bind and document is electronically signed. Not checking if rule is fired without signing, as per Digital flow it must always be signed.
        HelperCommon.endorsementBind(policyNumber, "Megha Gubbala", Response.Status.OK.getStatusCode(), doccId);
    }

    protected void verifyDocInDb(ETCSCoreSoftAssertions softly, String query, DocGenEnum.Documents document, boolean isDocSignTagsExpected) {
        if (isDocSignTagsExpected) {
            Document docInXml = DocGenHelper.getDocument(document, query);
            String name = DocGenHelper.getDocumentDataElemByName("DocSignedBy", docInXml).getDataElementChoice().getTextField();
            String date = DocGenHelper.getDocumentDataElemByName("DocSignedDate", docInXml).getDataElementChoice().getDateTimeField();
            String currentDate = DateTimeUtils.getCurrentDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            softly.assertThat(name).isEqualTo("Megha Gubbala");
            softly.assertThat(date).startsWith(currentDate);
            softly.assertThat(date).endsWith("-07:00"); // validates that the document's DocSignedDate ends with an AZ timestamp
        } else {
            validateDocSignTagsNotExist(document, query);
        }
    }

    protected void goToPasAndVerifyRuleAndSignedBy(ETCSCoreSoftAssertions softly, String policyNumber,
                                                 AssetDescriptor<RadioGroup> documentAsset, AssetDescriptor<? extends AbstractEditableStringElement> coverageAsset,
                                                 String coverageLimit, ErrorEnum.Errors error, boolean isRuleOverridden) {
        //create endorsement from pas go to bind page verify document is electronically signed
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        softly.assertThat(getDocumentAssetList().getAsset(documentAsset).getValue()).isEqualTo("Electronically Signed");

        updatePremiumAndCoveragesTab(coverageAsset, coverageLimit);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        //add line to verify not signed
        softly.assertThat(getDocumentAssetList().getAsset(documentAsset).getValue()).isEqualTo("Not Signed");
        getDocumentsAndBindTab().submitTab();
        if (!isRuleOverridden) {
            //On bind verify error message
            errorTab.verify.errorsPresent(true, error);
            errorTab.cancel();
            //Physically sign the doccument and bind policy
            getDocumentAssetList().getAsset(documentAsset).setValue("Physically Signed");
            getDocumentsAndBindTab().submitTab();
        }
    }

    protected void validateDocSignTagsNotExist(DocGenEnum.Documents document, String query) {
        assertThat(DocGenHelper.getDocument(document, query).toString().contains("DocSignedBy")).isFalse();
        assertThat(DocGenHelper.getDocument(document, query).toString().contains("DocSignedDate")).isFalse();
    }

    protected abstract AssetList getDocumentAssetList();

    protected abstract Tab getDocumentsAndBindTab();

    protected abstract void updatePremiumAndCoveragesTab(AssetDescriptor<? extends AbstractEditableStringElement> coverageAsset, String coverageLimit);
}
