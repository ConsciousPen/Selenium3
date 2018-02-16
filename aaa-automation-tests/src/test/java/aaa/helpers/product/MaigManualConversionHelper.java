package aaa.helpers.product;

import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.Document;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

import java.text.MessageFormat;
import static aaa.helpers.docgen.DocGenHelper.getPackageDataElemByName;

public class MaigManualConversionHelper{

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

}
