package aaa.helpers.docgen;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import aaa.helpers.docgen.searchNodes.SearchBy;
import aaa.helpers.xml.model.Document;
import aaa.helpers.xml.model.DocumentPackage;
import aaa.helpers.xml.model.StandardDocumentRequest;
import aaa.main.enums.DocGenEnum;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.verification.ETCSCoreSoftAssertions;

public class DocumentWrapper {
	public Verify verify = new Verify();
	private StandardDocumentRequest standardDocumentRequest;
	private boolean generatedByJob;

	public DocumentWrapper(StandardDocumentRequest standardDocumentRequest) {
		this(standardDocumentRequest, false);
	}

	public DocumentWrapper(StandardDocumentRequest standardDocumentRequest, boolean generatedByJob) {
		this.standardDocumentRequest = standardDocumentRequest;
		this.generatedByJob = generatedByJob;
	}

	public DocumentWrapper(Document document) {
		this(document, false);
	}

	public DocumentWrapper(Document document, boolean generatedByJob) {
		StandardDocumentRequest standardDocumentRequest = new StandardDocumentRequest();
		DocumentPackage documentPackage = new DocumentPackage();
		documentPackage.setDocuments(Arrays.asList(document));
		standardDocumentRequest.setDocumentPackages(Arrays.asList(documentPackage));
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

	public List<Document> getAllDocuments(String policyNumber) {
		return getList(SearchBy.standardDocumentRequest.documentPackage.packageIdentifier(policyNumber).document);
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

		public <D> void exists(boolean expectedValue, SearchBy<?, D> searchFilter) {
			exists(expectedValue, null, searchFilter);
		}

		public <D> void exists(boolean expectedValue, SearchBy<?, D> searchFilter, ETCSCoreSoftAssertions softly) {
			exists(expectedValue, null, searchFilter, softly);
		}

		public <D> void exists(boolean expectedValue, String assertionMessage, SearchBy<?, D> searchFilter) {
			exists(expectedValue, assertionMessage, searchFilter, null);
		}

		public <D> void exists(boolean expectedValue, String assertionMessage, SearchBy<?, D> searchFilter, ETCSCoreSoftAssertions softly) {
			assertionMessage =
					Objects.isNull(assertionMessage) ? String.format("Entries are %1$s in xml file by search criteria:\n%2$s", expectedValue ? "absent" : "present", searchFilter) : assertionMessage;
			if (softly == null) {
				assertThat(getList(searchFilter).isEmpty()).as(assertionMessage).isEqualTo(!expectedValue);
			} else {
				softly.assertThat(getList(searchFilter).isEmpty()).as(assertionMessage).isEqualTo(!expectedValue);
			}
		}

		public void mapping(TestData td, String policyNumber) {
			mapping(true, td, policyNumber);
		}

		public void mapping(TestData td, String policyNumber, ETCSCoreSoftAssertions softly) {
			mapping(true, td, policyNumber, softly);
		}

		public void mapping(boolean expectedValue, TestData td, String policyNumber) {
			mapping(expectedValue, td, policyNumber, null);
		}

		/**
		 * Verifies the documents mapping with <b>TestData</b> after documents generation
		 *
		 * @param expectedValue defines whether all mapping should be correct or not
		 * @param td TestData defined for documents mapping check.
		 * @param policyNumber Policy/Quote Number
		 */
		public void mapping(boolean expectedValue, TestData td, String policyNumber, ETCSCoreSoftAssertions softly) {
			DocGenHelper.checkPasDocEnabled(policyNumber, false);
			for (String docKey : td.getKeys()) {
				DocGenEnum.Documents document = null;
				if (!"DocumentPackageData".equals(docKey)) {
					document = DocGenEnum.Documents.valueOf(docKey);
				}
				TestData tdDoc = td.getTestData(docKey);
				for (String sectionName : tdDoc.getKeys()) {
					List<TestData> tdSectionList = tdDoc.getTestDataList(sectionName);
					for (TestData tdSection : tdSectionList) {
						for (String dataElementName : tdSection.getKeys()) {
							List<TestData> tdDataElementList = tdSection.getTestDataList(dataElementName);
							for (TestData tdDataElementChoice : tdDataElementList) {
								if (tdDataElementChoice.getKeys().retainAll(Arrays.asList(DocGenEnum.DataElementChoiceTag.TEXTFIELD, DocGenEnum.DataElementChoiceTag.DATETIMEFIELD))) {
									throw new IstfException(String
											.format("Data mapping verification for \"DataElementChoice\" section is supported only by \"%s\" and \"%s\" tags values. Check your test data format.",
													DocGenEnum.DataElementChoiceTag.TEXTFIELD, DocGenEnum.DataElementChoiceTag.DATETIMEFIELD));
								}

								if (tdDataElementChoice.containsKey(DocGenEnum.DataElementChoiceTag.TEXTFIELD)) {
									String testFieldValue = tdDataElementChoice.getValue(DocGenEnum.DataElementChoiceTag.TEXTFIELD);
									if ("DocumentPackageData".equals(docKey)) {
										exists(expectedValue, SearchBy.standardDocumentRequest.documentPackage.packageIdentifier(policyNumber)
												.documentPackageData.documentDataSection.sectionName(sectionName).documentDataElement.name(dataElementName).dataElementChoice
												.textField(testFieldValue), softly);
									} else {
										exists(expectedValue, SearchBy.standardDocumentRequest.documentPackage.packageIdentifier(policyNumber)
												.document.templateId(document.getIdInXml()).documentDataSection.sectionName(sectionName).documentDataElement.name(dataElementName).dataElementChoice
												.textField(testFieldValue), softly);
									}
								}

								if (tdDataElementChoice.containsKey(DocGenEnum.DataElementChoiceTag.DATETIMEFIELD)) {
									String dateTimeFieldValue = tdDataElementChoice.getValue(DocGenEnum.DataElementChoiceTag.DATETIMEFIELD);
									if ("DocumentPackageData".equals(docKey)) {
										exists(expectedValue, SearchBy.standardDocumentRequest.documentPackage.packageIdentifier(policyNumber)
												.documentPackageData.documentDataSection.sectionName(sectionName).documentDataElement.name(dataElementName).dataElementChoice
												.dateTimeField(dateTimeFieldValue), softly);
									} else {
										exists(expectedValue, SearchBy.standardDocumentRequest.documentPackage.packageIdentifier(policyNumber)
												.document.templateId(document.getIdInXml()).documentDataSection.sectionName(sectionName).documentDataElement.name(dataElementName).dataElementChoice
												.dateTimeField(dateTimeFieldValue), softly);
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
