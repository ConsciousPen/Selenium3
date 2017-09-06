package aaa.helpers.docgen;

import aaa.helpers.docgen.searchNodes.SearchBy;
import aaa.helpers.xml.models.Document;
import aaa.helpers.xml.models.DocumentPackage;
import aaa.helpers.xml.models.StandardDocumentRequest;
import aaa.main.enums.DocGenEnum;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.verification.CustomAssert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DocumentWrapper {
	public Verify verify = new Verify();
	private StandardDocumentRequest standardDocumentRequest;
	private boolean generatedByJob;

	public DocumentWrapper(StandardDocumentRequest standardDocumentRequest) {
		this.standardDocumentRequest = standardDocumentRequest;
		this.generatedByJob = false;
	}

	public DocumentWrapper(StandardDocumentRequest standardDocumentRequest, boolean generatedByJob) {
		this.standardDocumentRequest = standardDocumentRequest;
		this.generatedByJob = generatedByJob;
	}

	public boolean isGeneratedByJob() {
		return generatedByJob;
	}

	public StandardDocumentRequest getStandardDocumentRequest() {
		return this.standardDocumentRequest;
	}

	public List<DocumentPackage> getAllDocumentPackages() {
		return getStandardDocumentRequest().getDocumentPackages();
	}

	public List<Document> getAllDocuments() {
		List<Document> allDocuments = new ArrayList<>();
		for (DocumentPackage documentPackage : getAllDocumentPackages()) {
			allDocuments.addAll(documentPackage.getDocuments());
		}
		return allDocuments;
	}

	public <D> List<D> getList(SearchBy<?, D> searchFilter) {
		return searchFilter.search(getStandardDocumentRequest());
	}


	public class Verify {
		public <D> void exists(SearchBy<?, D> searchFilter) {
			exists(true, null, searchFilter);
		}

		public <D> void exists(String assertionMessage, SearchBy<?, D> searchFilter) {
			exists(true, assertionMessage, searchFilter);
		}

		public <D> void exists(boolean expectedValue, String assertionMessage, SearchBy<?, D> searchFilter) {
			assertionMessage = Objects.isNull(assertionMessage) ? String.format("Entries are %s in generated document by provided search criteria.", expectedValue ? "absent" : "present") : assertionMessage;
			CustomAssert.assertEquals(assertionMessage, getList(searchFilter).isEmpty(), !expectedValue);
		}

		public void mapping(TestData td) {
			mapping(true, td);
		}


		/**
		 * Verifies the documents mapping with <b>TestData</b> after documents generation
		 *
		 * @param expectedValue defines whether all mapping should be correct or not
		 * @param td TestData defined for documents mapping check.
		 */
		public void mapping(boolean expectedValue, TestData td) {
			for (String docKey : td.getKeys()) {
				DocGenEnum.Documents document = DocGenEnum.Documents.valueOf(docKey);

				for (String sectionName : td.getTestData(docKey).getKeys()) {
					for (String dataElementName : td.getTestData(docKey).getTestData(sectionName).getKeys()) {
						TestData tdDataElementChoice = td.getTestData(docKey).getTestData(sectionName).getTestData(dataElementName);
						if (tdDataElementChoice.getKeys().retainAll(Arrays.asList(DocGenEnum.DataElementChoiceTag.TEXTFIELD, DocGenEnum.DataElementChoiceTag.DATETIMEFIELD))) {
							throw new IstfException(String.format("Data mapping verification for \"DataElementChoice\" section is supported only by \"%s\" and \"%s\" tags values. Check your test data format.",
									DocGenEnum.DataElementChoiceTag.TEXTFIELD, DocGenEnum.DataElementChoiceTag.DATETIMEFIELD));
						}

						if (tdDataElementChoice.containsKey(DocGenEnum.DataElementChoiceTag.TEXTFIELD)) {
							String testFieldValue = tdDataElementChoice.getValue(DocGenEnum.DataElementChoiceTag.TEXTFIELD);
							String assertionMessage = String.format("The expected key \"%1$s\" -> \"%2$s\" -> \"%3$s\" -> \"%4$s\" is %5$s in the xml file.", docKey, sectionName, dataElementName, testFieldValue, expectedValue ? "absent" : "present");
							exists(expectedValue, assertionMessage, SearchBy.standardDocumentRequest.documentPackage.document.templateId(document.getIdInXml())
									.documentDataSection.sectionName(sectionName).documentDataElement.name(dataElementName).dataElementChoice.textField(testFieldValue));
						}

						if (tdDataElementChoice.containsKey(DocGenEnum.DataElementChoiceTag.DATETIMEFIELD)) {
							String dateTimeFieldValue = tdDataElementChoice.getValue(DocGenEnum.DataElementChoiceTag.DATETIMEFIELD);
							String assertionMessage = String.format("The expected key \"%1$s\" -> \"%2$s\" -> \"%3$s\" -> \"%4$s\" is %5$s in the xml file.", docKey, sectionName, dataElementName, dateTimeFieldValue, expectedValue ? "absent" : "present");
							exists(expectedValue, assertionMessage, SearchBy.standardDocumentRequest.documentPackage.document.templateId(document.getIdInXml())
									.documentDataSection.sectionName(sectionName).documentDataElement.name(dataElementName).dataElementChoice.dateTimeField(dateTimeFieldValue));
						}
					}
				}
			}
		}
	}
}