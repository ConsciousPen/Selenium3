package aaa.helpers.docgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import aaa.helpers.docgen.searchNodes.SearchBy;
import aaa.helpers.xml.models.Document;
import aaa.helpers.xml.models.DocumentPackage;
import aaa.helpers.xml.models.StandardDocumentRequest;
import toolkit.verification.CustomAssert;

public class DocumentWrapper {
	public Verify verify = new Verify();
	private StandardDocumentRequest standardDocumentRequest;

	public DocumentWrapper(StandardDocumentRequest standardDocumentRequest) {
		this.standardDocumentRequest = standardDocumentRequest;
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
	}
}
