package aaa.main.modules.policy.abstract_tabs;

import static org.openqa.selenium.By.id;
import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.By;
import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.common.components.Dialog;
import aaa.main.enums.DocGenConstants;
import aaa.main.enums.DocGenEnum;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.toolkit.webdriver.customcontrols.FillableDocumentsTable;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public abstract class CommonDocumentActionTab extends ActionTab {
	private static final Object lock = new Object();
	public Verify verify = new Verify();
	public Button buttonOk = new Button(By.xpath("//a[@id='policyDataGatherForm:generateDocLink' or @id='policyDataGatherForm:generateEmailDocLink']"));
	public Button buttonCancel = new Button(id("policyDataGatherForm:adhocCancel"));
	public Button buttonPreviewDocuments = new Button(id("policyDataGatherForm:previewDocLink"));
	public TextBox textboxEmailAddress = new TextBox(By.xpath("//input[@id='policyDataGatherForm:emailAddress' or @id='policyDataGatherForm:emailInputField']"));
	public Dialog dialogError = new Dialog(By.xpath("//div[@id='policyDataGatherForm:errorDialog_content']"));
	public StaticElement errorMsg = new StaticElement(By.xpath("//div[@id ='policyDataGatherForm:errorDialog_content']/span/table/tbody/tr/td/span"));
	public Button closeErrorDialogBtn = new Button(id("policyDataGatherForm:cancelBtn"));
	
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
		generateDocuments(DocGenEnum.DeliveryMethod.EMAIL, DocGenEnum.EMAIL, null, null, documents);
	}

	public void generateDocuments(DocGenEnum.DeliveryMethod deliveryMethod, DocGenEnum.Documents... documents) {
		if (deliveryMethod.equals(DocGenEnum.DeliveryMethod.EMAIL)) {
			generateDocuments(deliveryMethod, DocGenEnum.EMAIL, null, null, documents);
		} else {
			generateDocuments(deliveryMethod, null, null, null, documents);
		}
	}

	public void generateDocuments(TestData expandedDocumentsData, DocGenEnum.Documents... documents) {
		generateDocuments(DocGenEnum.DeliveryMethod.EMAIL, DocGenEnum.EMAIL, null, expandedDocumentsData, documents);
	}

	public void generateDocuments(DocGenEnum.DeliveryMethod deliveryMethod, String emailAddress, String fax, TestData expandedDocumentsData, DocGenEnum.Documents... documents) {
		generateDocuments(true, deliveryMethod, emailAddress, fax, expandedDocumentsData, documents);
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

			if (waitForPolicy == true) {
				PolicySummaryPage.labelPolicyNumber.waitForAccessible(30000);
			}
			WebDriverHelper.switchToDefault();
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
