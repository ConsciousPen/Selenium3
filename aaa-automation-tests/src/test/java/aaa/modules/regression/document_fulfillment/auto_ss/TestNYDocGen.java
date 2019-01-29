package aaa.modules.regression.document_fulfillment.auto_ss;

import static aaa.main.enums.DocGenEnum.Documents.valueOf;
import static aaa.main.enums.PolicyConstants.PolicyVehiclesTable.*;
import static aaa.main.metadata.policy.AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.SUPPLEMENTARY_UNINSURED_MOTORISTS_COVERAGE_REJECTION;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverTab.LICENSE_STATE;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.*;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TERM;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.PolicyInformation.*;
import static aaa.main.metadata.policy.AutoSSMetaData.PremiumAndCoveragesTab.*;
import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.DataElementChoice;
import aaa.helpers.xml.model.Document;
import aaa.helpers.xml.model.DocumentDataElement;
import aaa.helpers.xml.model.DocumentDataSection;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.RadioGroup;

public class TestNYDocGen extends AutoSSBaseTest {

	private final Tab generalTab = new GeneralTab();
	private final Tab premiumCovTab = new PremiumAndCoveragesTab();
	private final Tab driverReportTab = new DriverActivityReportsTab();
	private final Tab docAndBind = new DocumentsAndBindTab();
	private final GenerateOnDemandDocumentActionTab generateOnDemandDocumentActionTab = new GenerateOnDemandDocumentActionTab();

	/**
	 * @author Igor Garkusha
	 * @name NY doc gen check for AADNNY1, AAINXX1, AAMTNY, AASANY, AAOANY, AAACNY
	 * @scenario
	 * 1. Initiate a manual entry conversion for SIS - NY from PAS..
	 * 2. Navigate through the application and calculate the premium.
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
		String getDataSql = String.format(AaaDocGenEntityQueries.GET_DOCUMENT_BY_POLICY_NUMBER, policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);
		List<DocGenEnum.Documents> docsToCheck = getEnumList(getStateTestData(testDataManager.getDefault(this.getClass()), "DocToCheck").
				getList("DocumentsNames"));
		docsToCheck.forEach(docID -> {
			//Select doc from DB
			List<DocumentDataSection> docData = DocGenHelper.getDocumentDataElemByName("PlcyNum", docID, getDataSql);
			assertThat(docData).isNotEmpty();

			DataElementChoice actualNode = docData.get(0).getDocumentDataElements().get(0).getDataElementChoice();
			//Check that doc contains expected node
			assertSoftly(softly -> softly.assertThat(actualNode).isEqualTo(new DataElementChoice().setTextField(policyNumber)));
		});

	}

	/**
	 * @author Igor Garkusha
	 * @name Test Supplementary UM UIM Reject/Elect Lower Limits
	 * @scenario
	 * 1. Initiate a manual entry conversion for SIS - NY from PAS.
	 * 2. Navigate through the application and calculate the premium with BI Limit as the very lowest option selected on P&C page.
	 * 3. Run the renewal process till Renewal Offer stage.
	 * 4. Complete the renewal process on the policy.
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
	 * @name Test Supplementary UM UIM Reject/Elect Lower Limits
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
	 * @author Igor Garkusha
	 * @name Test Sequencing for NY Conversion Docs
	 * @scenario
	 * 1. Initiate a manual entry conversion for SIS - NY from PAS.
	 * 2. Navigate through all tabs, enter required information, calculate premium and bind the policy.
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
		String getDataSql = String.format(AaaDocGenEntityQueries.GET_DOCUMENT_BY_POLICY_NUMBER, policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);

		List<DocGenEnum.Documents> docsToCheck = getEnumList(getStateTestData(testDataManager.getDefault(this.getClass()), "DocToCheck").
				getList("DocumentsNamesInOrder"));

		Document privDoc = null;

		for (DocGenEnum.Documents doc : docsToCheck) {

			Document currentDoc = DocGenHelper.getDocument(doc, getDataSql);
			assertThat(currentDoc).isNotNull();

			if (privDoc != null) {
				assertThat(Integer.parseInt(currentDoc.getSequence())).
						isGreaterThan(Integer.parseInt(privDoc.getSequence()));
			}
			privDoc = currentDoc;
		}
	}

	private String getVehicleInfo(int rowNum) {
		String yearVeh = PolicySummaryPage.tablePolicyVehicles.getRow(rowNum).getCell(YEAR).getValue();
		String makeVeh = PolicySummaryPage.tablePolicyVehicles.getRow(rowNum).getCell(MAKE).getValue();
		String modelVeh = PolicySummaryPage.tablePolicyVehicles.getRow(rowNum).getCell(MODEL).getValue();
		return yearVeh + " " + makeVeh + " " + modelVeh;
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
		String getDataSql = String.format(AaaDocGenEntityQueries.GET_DOCUMENT_BY_POLICY_NUMBER, policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);

		//Get actual value
		DocumentDataSection docData = DocGenHelper.getDocumentDataElemByName(xmlTag, valueOf("AA52" + state), getDataSql).get(0);

		//Compare with actual value
		assertSoftly(softly -> softly.assertThat(docData.getDocumentDataElements()).contains(expectedValue));
	}

	private String conversionPolicyPreconditions(TestData policyTd) {
		mainApp().open();
		createCustomerIndividual();
		LocalDateTime effectiveDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(35);
		customer.initiateRenewalEntry().perform(getPolicyTD("InitiateRenewalEntry", "TestData"), effectiveDate);

		policy.getDefaultView().fillUpTo(policyTd, DriverActivityReportsTab.class);
		driverReportTab.getAssetList().getAsset(VALIDATE_DRIVING_HISTORY.getLabel(), Button.class).click();
		driverReportTab.getAssetList().getAsset(SALES_AGENT_AGREEMENT.getLabel(), RadioGroup.class).setValue("I Agree");
		driverReportTab.submitTab();
		docAndBind.fillTab(policyTd);
		DocumentsAndBindTab.btnPurchase.click();
		Page.dialogConfirmation.confirm();
		return PolicySummaryPage.linkPolicy.getValue();

	}

	private TestData prepareConvTD(TestData policyTd, String state) {
		return policyTd.mask(TestData.makeKeyPath(generalTab.getMetaKey(), POLICY_INFORMATION.getLabel(), EFFECTIVE_DATE.getLabel())).
				mask(TestData.makeKeyPath(generalTab.getMetaKey(), POLICY_INFORMATION.getLabel(), LEAD_SOURCE.getLabel())).
				mask(TestData.makeKeyPath(generalTab.getMetaKey(), AAA_MEMBERSHIP.getLabel())).
				adjust(TestData.makeKeyPath(generalTab.getMetaKey(), AAA_MEMBERSHIP.getLabel(), CURRENT_AAA_MEMBER.getLabel()), "No").
				mask(TestData.makeKeyPath(generalTab.getMetaKey(), OTHER_AAA_PRODUCTS_OWNED.getLabel())).
				mask(TestData.makeKeyPath(premiumCovTab.getMetaKey(), POLICY_TERM.getLabel())).
				adjust(TestData.makeKeyPath(premiumCovTab.getMetaKey(), PAYMENT_PLAN.getLabel()), "Annual (Renewal)").
				adjust(TestData.makeKeyPath(new DriverTab().getMetaKey(), LICENSE_STATE.getLabel()), state).
				adjust(TestData.makeKeyPath(new VehicleTab().getMetaKey(), AutoSSMetaData.VehicleTab.TYPE.getLabel()), "Private Passenger Auto").
				mask(TestData.makeKeyPath(docAndBind.getMetaKey(), "Agreement"));

	}

	private List<DocGenEnum.Documents> getEnumList(List<String> valuesList) {
		return valuesList.stream().map(DocGenEnum.Documents::valueOf).collect(Collectors.toList());
	}

}
