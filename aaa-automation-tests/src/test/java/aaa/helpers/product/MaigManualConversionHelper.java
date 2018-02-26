package aaa.helpers.product;

import static aaa.helpers.docgen.DocGenHelper.getPackageDataElemByName;
import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.PolicyType;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.verification.CustomAssert;

public class MaigManualConversionHelper {

    private final static String SELECT_POLICY_SOURCE_NUMBER = "select p.SOURCEPOLICYNUM from POLICYSUMMARY p Where p.Policynumber = '%s'";
    private final static String INSERT_HOME_BANKING_FOR_POLICY = "INSERT INTO REMINDERPOLICYNUMBERCHANGE rpc"
            + "(rpc.ID, rpc.DTYPE, rpc.POLICYNUMBER, rpc.DATEOFLASTPAYMENT, rpc.NUMBEROFPAYMENTS, rpc.SINGLEPAYMENTSCOUNT, rpc.RECURRINGPAYMENTSCOUNT, rpc.REMINDERIND)"
            + "values (eis_sequence.nextval,'HBReminderPolicyNumberChangeEntity', '%1$s', to_date('%2$s', 'YYYY-MM-dd'), 10,'' ,'', 0)";

    /**
     * Method to verify tags are present and contain specific values in Package
     * Note: Will be refactored after the refactoring of {@link DocGenHelper}
     *
     * @param legacyPolicyNumber
     * @param policyNumber
     * @param eventName
     */
    public void verifyPackageTagData(String legacyPolicyNumber, String policyNumber, AaaDocGenEntityQueries.EventNames eventName) throws NoSuchFieldException {
        CustomAssert.assertTrue(MessageFormat.format("Problem is in tags: [{0}], [{1}]", "PlcyPrfx", "PlcyNum"), policyNumber
                .equals(getPackageTag(policyNumber, "PlcyPrfx", eventName) + getPackageTag(policyNumber, "PlcyNum", eventName)));
        CustomAssert.assertTrue(MessageFormat.format("Problem is in tag: [{0}]", "HdesPlcyNum"), legacyPolicyNumber
                .equals(getPackageTag(policyNumber, "HdesPlcyNum", eventName).replaceAll("-", "")));
    }

	/**
	 * Method to verify tags are present and contain specific values in Document
	 * Note: Will be refactored after the refactoring of {@link DocGenHelper}
	 *
	 * @param document
	 * @param testData
	 * @param isPupPresent
	 */
	public void verifyDocumentTagData(Document document, TestData testData, boolean isPupPresent) throws NoSuchFieldException {
		if (isPupPresent) {
			verifyTagData(document, "PupCvrgYN", "Y");
		} else {
			verifyTagData(document, "PupCvrgYN", "N");
		}
		if ("Yes".equals(testData.getTestData("MortgageesTab").getValue("Mortgagee"))) {
			verifyTagData(document, "ThrdPrtyHdr", "TestName");
			verifyTagData(document, "ThrdPrtyLnNum", "12345678");
		}
	}

	/**
	 * Verify that tag value is present in the Documents section
	 */
	public void verifyTagData(Document document, String tag, String textFieldValue) {
		CustomAssert.assertTrue(MessageFormat.format("Problem is in tag: [{0}]", tag), textFieldValue
				.equals(DocGenHelper.getDocumentDataElemByName(tag, document).getDataElementChoice().getTextField()));
	}

	/**
	 * Verify that tag value is present in the Package
	 */
	public String getPackageTag(String policyNumber, String tag, AaaDocGenEntityQueries.EventNames name) throws NoSuchFieldException {
		return getPackageDataElemByName(policyNumber, "PolicyDetails", tag, name);
	}

	public void pas9607_verifyPolicyTransactionCode(String expectedCode, String policyNumber, AaaDocGenEntityQueries.EventNames eventName) throws NoSuchFieldException {
		String policyTransactionCode = getPackageTag(policyNumber, "PlcyTransCd", eventName);
		assertThat(policyTransactionCode).isEqualTo(expectedCode);
	}

	public void verifyFormSequence(List<String> expectedFormsOrder, List<Document> documentList) {
		assertSoftly(softly -> {
			// Check that all documents where generated
			List<String> allDocs = new ArrayList<>();
			documentList.forEach(doc -> allDocs.add(doc.getTemplateId()));
			assertThat(allDocs).containsAll(expectedFormsOrder);
			// Get all docs +  sequence number
			HashMap<Integer, String> actualDocuments = new HashMap<>();
			documentList.forEach(doc -> actualDocuments.put(Integer.parseInt(doc.getSequence()), doc.getTemplateId()));
			// Sort keys
			List<Integer> sortedKeys = new ArrayList(actualDocuments.keySet());
			Collections.sort(sortedKeys);
			// Get documents order by sequence number
			List<String> actualOrder = new ArrayList<>();
			sortedKeys.forEach(sequenceId -> actualOrder.add(actualDocuments.get(sequenceId)));
			// Get Intersection order
			List<String> intersectionsWithActualList = actualOrder.stream().filter(expectedFormsOrder::contains).collect(Collectors.toList());
			// Check sequence
			softly.assertThat(intersectionsWithActualList).isEqualTo(expectedFormsOrder);
		});
	}

	public void pas9816_verifyRenewalBillingPackageForms(String policyNumber, PolicyType policyType) {
		List<Document> actualConversionRenewalBillingDocumentsList = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_BILL);
		assertThat(actualConversionRenewalBillingDocumentsList).isNotEmpty().isNotNull();

		List<String> expectedFormsAndOrder = new ArrayList<>(Arrays.asList(DocGenEnum.Documents.AHRBXX.getId(),
				DocGenEnum.Documents.AH35XX.getId()));

		//Adding of 'Delta' form for PUP and Home products in the Forms List
		if (!policyType.equals(PolicyType.PUP)) {
			expectedFormsAndOrder.add(DocGenEnum.Documents.HSRNHBXX.getId());
		} else {
			expectedFormsAndOrder.add(DocGenEnum.Documents.HSRNHBPUPXX.getId());
		}

		verifyFormSequence(expectedFormsAndOrder, actualConversionRenewalBillingDocumentsList);
	}

    private String getSourcePolicyNumber(String policyNumber) {

        String sourcePolicyNumberValue = DBService.get().getValue(String.format(SELECT_POLICY_SOURCE_NUMBER, policyNumber)).orElse(null);
        assertThat(sourcePolicyNumberValue).isNotEqualTo(null);

        return sourcePolicyNumberValue;
    }

    public void setUpHomeBankingForConversionRenewal(String policyNumber) {

        String currentDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));

        int a = DBService.get().executeUpdate(String.format(INSERT_HOME_BANKING_FOR_POLICY, getSourcePolicyNumber(policyNumber), currentDate));
        assertThat(a).isGreaterThan(0).as("MaigManualConversionHelper# setUpHomeBankingForConversionRenewal method failed, value was not inserted in DB");
    }

    public List<String> getHO3NJForms() {
        return Arrays.asList(
                DocGenEnum.Documents.HSRNHODPXX.getId(), //HO form instead of Mortgagee form
                DocGenEnum.Documents.HSTP.getId(),
                DocGenEnum.Documents.HS02.getId(),
                DocGenEnum.Documents.AHAUXX.getId(),
                DocGenEnum.Documents.AHPNXX.getId(),
                DocGenEnum.Documents.AHMVCNV.getId(),
                DocGenEnum.Documents.HSMPDCNVXX.getId(),
                DocGenEnum.Documents.HSCSNA.getId()
        );
    }

    public List<String> getHO3OtherStatesForms() {
        return Arrays.asList(
                DocGenEnum.Documents.HSRNMXX.getId(), //Mortgagee form instead of HO form
                DocGenEnum.Documents.HS02.getId(),
                DocGenEnum.Documents.AHAUXX.getId(),
                DocGenEnum.Documents.AHPNXX.getId(),
                DocGenEnum.Documents.AHMVCNV.getId(),
                DocGenEnum.Documents.HSMPDCNVXX.getId()
        );
    }

	public List<String> getHO4NJForms() {
		return Arrays.asList(
				DocGenEnum.Documents.HSRNHODPXX.getId(),
				DocGenEnum.Documents.HSTP.getId(),
				DocGenEnum.Documents.HS02_4.getId(),
				DocGenEnum.Documents.AHAUXX.getId(),
				DocGenEnum.Documents.AHPNXX.getId(),
				DocGenEnum.Documents.AHMVCNV.getId(), //membership validation
				DocGenEnum.Documents.HSMPDCNVXX.getId() //multi policy discount
				//todo add HSCSNB
		);
	}

	public List<String> getHO4OtherStatesForms() {
		return Arrays.asList(
				DocGenEnum.Documents.HSRNHODPXX.getId(),
				DocGenEnum.Documents.HS02_4.getId(),
				DocGenEnum.Documents.AHAUXX.getId(),
				DocGenEnum.Documents.AHPNXX.getId(),
				DocGenEnum.Documents.AHMVCNV.getId(), //membership validation
				DocGenEnum.Documents.HSMPDCNVXX.getId() //multi policy discount
		);
	}

	public List<String> getHO6NJForms() {
		return Arrays.asList(
				DocGenEnum.Documents.HSRNHODPXX.getId(), //HO form instead of Mortgagee form
				DocGenEnum.Documents.HSTP.getId(),
				DocGenEnum.Documents.HS02_6.getId(),
				DocGenEnum.Documents.AHAUXX.getId(),
				DocGenEnum.Documents.AHPNXX.getId(),
				DocGenEnum.Documents.AHMVCNV.getId(),
				DocGenEnum.Documents.HSMPDCNVXX.getId()
				//todo add HSCSNB
		);
	}

	public List<String> getHO6OtherStatesForms() {
		return Arrays.asList(
				DocGenEnum.Documents.HSRNMXX.getId(), //Mortgagee form instead of HO form
				DocGenEnum.Documents.HS02_6.getId(),
				DocGenEnum.Documents.AHAUXX.getId(),
				DocGenEnum.Documents.AHPNXX.getId(),
				DocGenEnum.Documents.AHMVCNV.getId(),
				DocGenEnum.Documents.HSMPDCNVXX.getId()
				//todo add HSCSNB
		);
	}

	public List<String> getPupNJForms() {
		return Arrays.asList(
				DocGenEnum.Documents.HSRNPUPXX.getId(),
				DocGenEnum.Documents.HSTP.getId(),
				DocGenEnum.Documents.PS02.getId(),
				DocGenEnum.Documents.AHPNXX.getId()
				//todo figure out should it be here or not
				//DocGenEnum.Documents.HSMPDCNVXX.getId()
		);
	}

	public List<String> getPupOtherStatesForms() {
		return Arrays.asList(
				DocGenEnum.Documents.HSRNPUPXX.getId(),
				DocGenEnum.Documents.PS02.getId(),
				//Could be skipped as per Chris and Teri
				//                DocGenEnum.Documents.PS0922.getId(), //add PS 09 23, PS 09 24, PS 98 11, PS 98 13, PS 98 14, PS 98 15, PS 98 16
				DocGenEnum.Documents.AHPNXX.getId()
				//todo figure out should it be here or not
				//DocGenEnum.Documents.HSMPDCNVXX.getId()
		);
	}

	public List<String> getDP3NJForms() {
		return Arrays.asList(
				DocGenEnum.Documents.HSRNHODPXX.getId(), //HO form instead of Mortgagee form
				DocGenEnum.Documents.HSTP.getId(),
				DocGenEnum.Documents.DS02.getId(),
				DocGenEnum.Documents.AHAUXX.getId(),
				DocGenEnum.Documents.AHPNXX.getId(),
				DocGenEnum.Documents.AHMVCNV.getId(),
				DocGenEnum.Documents.HSMPDCNVXX.getId()
		);
	}

	public List<String> getDP3OtherStatesForms() {
		return Arrays.asList(
				DocGenEnum.Documents.HSRNMXX.getId(), //Mortgagee form instead of HO form
				DocGenEnum.Documents.DS02.getId(),
				DocGenEnum.Documents.AHAUXX.getId(),
				DocGenEnum.Documents.AHPNXX.getId(),
				DocGenEnum.Documents.AHMVCNV.getId(),
				DocGenEnum.Documents.HSMPDCNVXX.getId()
		);
	}
}
