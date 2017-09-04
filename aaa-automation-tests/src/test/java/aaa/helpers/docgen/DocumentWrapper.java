package aaa.helpers.docgen;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.docgen.searchNodes.SearchBy;
import aaa.helpers.xml.models.CreateDocuments;
import aaa.helpers.xml.models.Document;
import aaa.helpers.xml.models.DocumentPackage;
import aaa.helpers.xml.models.StandardDocumentRequest;
import toolkit.verification.CustomAssert;

public class DocumentWrapper {
	public Verify verify = new Verify();
	private CreateDocuments createDocuments;

	public DocumentWrapper(CreateDocuments createDocuments) {
		this.createDocuments = createDocuments;
	}

	public StandardDocumentRequest getStandardDocumentRequest() {
		return createDocuments.getStandardDocumentRequest();
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
		return searchFilter.search(getAllDocumentPackages());
	}


	public class Verify {
		public <D> void exists(SearchBy<?, D> searchFilter) {
			exists(true, searchFilter);
		}

		public <D> void exists(boolean expectedValue, SearchBy<?, D> searchFilter) {
			String assertionMessage = String.format("Entries are %s in generated document by provided search criterias.", expectedValue ? "absent" : "present");
			CustomAssert.assertEquals(assertionMessage, getList(searchFilter).isEmpty(), !expectedValue);
		}
	}
}