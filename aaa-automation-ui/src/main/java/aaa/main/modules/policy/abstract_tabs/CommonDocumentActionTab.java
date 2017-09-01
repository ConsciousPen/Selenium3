package aaa.main.modules.policy.abstract_tabs;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.enums.DocGenConstants;
import aaa.main.enums.DocGenEnum;
import aaa.toolkit.webdriver.customcontrols.FillableDocumentsTable;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

import java.util.HashMap;
import java.util.Map;

import static org.openqa.selenium.By.id;

public abstract class CommonDocumentActionTab extends ActionTab {
	public Verify verify = new Verify();

	public Button buttonOk = new Button(id("policyDataGatherForm:generateDocLink"));
	public Button buttonCancel = new Button(id("policyDataGatherForm:adhocCancel"));
	public Button buttonPreviewDocuments = new Button(id("policyDataGatherForm:previewDocLink"));

	protected CommonDocumentActionTab(Class<? extends MetaData> mdClass) {
		super(mdClass);
	}

	@Override
	public Tab submitTab() {
		buttonOk.click();
		return this;
	}

	public abstract FillableDocumentsTable getDocumentsControl();

	public void selectAllDocuments() {
		for (int rowNumber = 1; rowNumber <= getDocumentsControl().getTable().getRowsCount(); rowNumber++) {
			getDocumentsControl().fillRow(rowNumber, DataProviderFactory.dataOf("Select", "true"));
		}
	}

	public void selectDocuments(DocGenEnum.Documents... documents) {
		for (DocGenEnum.Documents doc : documents) {
			getDocumentsControl().fillRow(DataProviderFactory.dataOf(
					DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NUM, doc.getId(),
					DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NAME, doc.getName(),
					DocGenConstants.OnDemandDocumentsTable.SELECT, "true"));
		}
	}

	public void generateDocuments(DocGenEnum.Documents... documents) {
		generateDocuments(DocGenEnum.DeliveryMethod.CENTRAL_PRINT, documents);
	}

	public void generateDocuments(DocGenEnum.DeliveryMethod deliveryMethod, DocGenEnum.Documents... documents) {
		generateDocuments(deliveryMethod, null, documents);
	}

	public void generateDocuments(TestData expandedDocumentsData, DocGenEnum.Documents... documents) {
		generateDocuments(DocGenEnum.DeliveryMethod.CENTRAL_PRINT, expandedDocumentsData, documents);
	}

	public void generateDocuments(DocGenEnum.DeliveryMethod deliveryMethod, TestData expandedDocumentsData, DocGenEnum.Documents... documents) {
		generateDocuments(deliveryMethod, null, null, expandedDocumentsData, documents);
	}

	public void generateDocuments(DocGenEnum.DeliveryMethod deliveryMethod, String emailAddress, String fax, TestData expandedDocumentsData, DocGenEnum.Documents... documents) {
		if (documents.length > 0) {
			selectDocuments(documents);
		} else {
			selectAllDocuments();
		}

		if (expandedDocumentsData != null) {
			getDocumentsControl().fillRow(expandedDocumentsData);
		}

		if (emailAddress != null) {
			getAssetList().getAsset("Email Address", TextBox.class).setValue(emailAddress);
		}
		if (fax != null) {
			getAssetList().getAsset("Fax", TextBox.class).setValue(fax);
		}

		getAssetList().getAsset("Delivery Method", RadioGroup.class).setValue(deliveryMethod.get());
		submitTab();
	}

	public class Verify {

		public void documentsPresent(DocGenEnum.Documents... documents) {
			documentsPresent(true, documents);
		}

		public void documentsPresent(boolean expectedValue, DocGenEnum.Documents... documents) {
			Map<String, String> documentQuery = new HashMap<>();

			for (DocGenEnum.Documents doc : documents) {
				String message = String.format("On demand document %1$s is not %2$s as expected.", doc, expectedValue ? "present" : "absent");
				documentQuery.put(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NUM, doc.getId());
				if (doc.getName().contains("'")) {
					documentQuery.put(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NAME, doc.getName().replaceAll("'.*", "")); // quote in documents name breaks xpath
					getDocumentsControl().getTable().getRowContains(documentQuery).verify.present(message, expectedValue); // search by part of documents name
				} else {
					documentQuery.put(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NAME, doc.getName());
					getDocumentsControl().getTable().getRow(documentQuery).verify.present(message, expectedValue);
				}
			}
		}
		
		public void documentsEnabled(DocGenEnum.Documents... documents) {
			documentsEnabled(true, documents);
		}
		
		public void documentsEnabled(boolean expectedValue, DocGenEnum.Documents... documents) {
			Map<String, String> documentQuery = new HashMap<>();

			for (DocGenEnum.Documents doc : documents) {
				String message = String.format("On demand document %1$s is not %2$s as expected.", doc, expectedValue ? "enabled" : "disabled");
				documentQuery.put(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NUM, doc.getId());
				if (doc.getName().contains("'")) {
					documentQuery.put(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NAME, doc.getName().replaceAll("'.*", "")); // quote in documents name breaks xpath
					getDocumentsControl().getTable().getRowContains(documentQuery).getCell(DocGenConstants.OnDemandDocumentsTable.SELECT).controls.checkBoxes.getFirst().verify.enabled(message, expectedValue);// search by part of documents name
				} else {
					documentQuery.put(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NAME, doc.getName());
					getDocumentsControl().getTable().getRow(documentQuery).getCell(DocGenConstants.OnDemandDocumentsTable.SELECT).controls.checkBoxes.getFirst().verify.enabled(message, expectedValue);
				}
			}
		}
	}
}
