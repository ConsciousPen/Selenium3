package aaa.main.modules.policy.abstract_tabs;

import static toolkit.verification.CustomAssertions.assertThat;
import static org.openqa.selenium.By.id;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.By;
import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.enums.DocGenConstants;
import aaa.main.enums.DocGenEnum;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.toolkit.webdriver.customcontrols.FillableDocumentsTable;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public abstract class CommonDocumentActionTab extends ActionTab {
	private static final Object lock = new Object();
	public Verify verify = new Verify();
	public Button buttonOk = new Button(By.xpath("//input[@id='policyDataGatherForm:generateDocButton'"));
	public Button buttonCancel = new Button(id("policyDataGatherForm:adhocCancel"));
	public Button buttonPreviewDocuments = new Button(id("policyDataGatherForm:previewDocButton"));
	public TextBox textboxEmailAddress = new TextBox(id("policyDataGatherForm:emailAddress"));

	protected CommonDocumentActionTab(Class<? extends MetaData> mdClass) {
		super(mdClass);
	}

	public abstract FillableDocumentsTable getDocumentsControl();
	@Override
	public Tab submitTab() {
		buttonOk.click();
		return this;
	}

	public void selectAllDocuments() {
		synchronized (lock) {
			for (int rowNumber = 1; rowNumber <= getDocumentsControl().getTable().getRowsCount(); rowNumber++) {
				getDocumentsControl().fillRow(rowNumber, DataProviderFactory.dataOf("Select", "true"));
			}
		}
	}

	public void selectDocuments(DocGenEnum.Documents... documents) {
		synchronized (lock) {
			for (DocGenEnum.Documents doc : documents) {
				getDocumentsControl().fillRow(DataProviderFactory.dataOf(
						DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NUM, doc.getId(),
						DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NAME, doc.getName(),
						DocGenConstants.OnDemandDocumentsTable.SELECT, "true"));
			}
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
		generateDocuments(true, deliveryMethod, null, null, expandedDocumentsData, documents);
	}
	
	public void generateDocuments(Boolean waitForPolicy, DocGenEnum.DeliveryMethod deliveryMethod, String emailAddress, String fax, TestData expandedDocumentsData, DocGenEnum.Documents... documents) {
		synchronized (lock) {
			if (documents.length > 0) {
				selectDocuments(documents);
			} else {
				selectAllDocuments();
			}

			if (expandedDocumentsData != null) {
				getDocumentsControl().fillRow(expandedDocumentsData);
			}

			getAssetList().getAsset("Delivery Method", RadioGroup.class).setValue(deliveryMethod.get());

			if (emailAddress != null) {
				textboxEmailAddress.setValue(emailAddress);
			}
			if (fax != null) {
				getAssetList().getAsset("Fax", TextBox.class).setValue(fax);
			}

			submitTab();
			
			if (waitForPolicy == true) 
			 PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
			
		}
	}

	public class Verify {

		public void documentsPresent(DocGenEnum.Documents... documents) {
			documentsPresent(true, documents);
		}

		public void documentsPresent(ETCSCoreSoftAssertions softly, DocGenEnum.Documents... documents) {
			documentsPresent(softly, true, documents);
		}

		public void documentsPresent(boolean expectedValue, DocGenEnum.Documents... documents) {
			documentsPresent(null, true, documents);
		}

		public void documentsPresent(ETCSCoreSoftAssertions softly, boolean expectedValue, DocGenEnum.Documents... documents) {
			Map<String, String> documentQuery = new HashMap<>();

			for (DocGenEnum.Documents doc : documents) {
				String message = String.format("On demand document %1$s is not %2$s as expected.", doc, expectedValue ? "present" : "absent");
				documentQuery.put(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NUM, doc.getId());
				documentQuery.put(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NAME, doc.getName());
				if (softly == null) {
					assertThat(getDocumentsControl().getTable().getRow(documentQuery)).as(message).isPresent(expectedValue);
				} else {
					softly.assertThat(getDocumentsControl().getTable().getRow(documentQuery)).as(message).isPresent(expectedValue);
				}
			}
		}

		public void documentsEnabled(DocGenEnum.Documents... documents) {
			documentsEnabled(true, documents);
		}

		public void documentsEnabled(ETCSCoreSoftAssertions softly, DocGenEnum.Documents... documents) {
			documentsEnabled(softly, true, documents);
		}

		public void documentsEnabled(boolean expectedValue, DocGenEnum.Documents... documents) {
			CustomSoftAssertions.assertSoftly(softly -> documentsEnabled(softly, expectedValue, documents));
		}

		public void documentsEnabled(ETCSCoreSoftAssertions softly, boolean expectedValue, DocGenEnum.Documents... documents) {
			Map<String, String> documentQuery = new HashMap<>();

			for (DocGenEnum.Documents doc : documents) {
				String message = String.format("On demand document %1$s is not %2$s as expected.", doc, expectedValue ? "enabled" : "disabled");
				documentQuery.put(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NUM, doc.getId());
				documentQuery.put(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NAME, doc.getName());
				if (softly == null) {
					assertThat(getDocumentsControl().getTable().getRow(documentQuery).getCell(DocGenConstants.OnDemandDocumentsTable.SELECT).controls.checkBoxes.getFirst())
							.as(message).isEnabled(expectedValue);
				} else {
					softly.assertThat(getDocumentsControl().getTable().getRow(documentQuery).getCell(DocGenConstants.OnDemandDocumentsTable.SELECT).controls.checkBoxes.getFirst())
							.as(message).isEnabled(expectedValue);
				}
			}
		}
	}
}
