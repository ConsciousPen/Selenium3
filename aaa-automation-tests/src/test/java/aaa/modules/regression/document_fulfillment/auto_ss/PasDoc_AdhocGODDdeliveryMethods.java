package aaa.modules.regression.document_fulfillment.auto_ss;

import static aaa.main.enums.DocGenEnum.Documents.*;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.impl.PasDocImpl;
import aaa.helpers.xml.model.pasdoc.*;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.RadioGroup;

public class PasDoc_AdhocGODDdeliveryMethods extends AutoSSBaseTest {

	private GenerateOnDemandDocumentActionTab documentActionTab = new GenerateOnDemandDocumentActionTab();

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario1(@Optional("") String state) {

		mainApp().open();
		createCustomerIndividual();
		TestData td_1 = getPolicyTD().adjust(getTestSpecificTD("TestData_with_EnrolledInUBI_1").resolveLinks());
		createPolicy(td_1);
		log.info("PAS DOC: Policy created with #" + PolicySummaryPage.getPolicyNumber());
		policy.policyDocGen().start();

		CustomSoftAssertions.assertSoftly(softly -> {

			verifyOptionIsEnabled(softly, DocGenEnum.DeliveryMethod.E_SIGNATURE, false, ACPUBI);
			verifyOptionIsEnabled(softly, DocGenEnum.DeliveryMethod.EMAIL, true, ACPUBI);
			verifyOptionIsEnabled(softly, DocGenEnum.DeliveryMethod.FAX, false, ACPUBI);
			verifyOptionIsEnabled(softly, DocGenEnum.DeliveryMethod.CENTRAL_PRINT, false, ACPUBI);
			verifyOptionIsEnabled(softly, DocGenEnum.DeliveryMethod.LOCAL_PRINT, true, true, ACPUBI);

			verifyOptionIsEnabled(softly, DocGenEnum.DeliveryMethod.E_SIGNATURE, true, AA11AZ);
			verifyOptionIsEnabled(softly, DocGenEnum.DeliveryMethod.EMAIL, true, AA11AZ);
			verifyOptionIsEnabled(softly, DocGenEnum.DeliveryMethod.FAX, false, AA11AZ);
			verifyOptionIsEnabled(softly, DocGenEnum.DeliveryMethod.CENTRAL_PRINT, true, AA11AZ);
			verifyOptionIsEnabled(softly, DocGenEnum.DeliveryMethod.LOCAL_PRINT, true, true, AA11AZ);
		});

		documentActionTab.saveAndExit();
	}

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario2(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData copyPolicy = getCopyFromPolicyTD();
		createPolicy();
		//cancel policy
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		policy.policyDocGen().start();
		verifyOptionIsEnabled(null, DocGenEnum.DeliveryMethod.E_SIGNATURE, true, false, AA11AZ);
		documentActionTab.buttonCancel.click();

		//reinstate policy
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));

		//copy policy
		policy.policyCopy().perform(copyPolicy);
		policy.calculatePremium(copyPolicy);
		Tab.buttonSaveAndExit.click();

		//declined policy
		policy.declineByCustomerQuote().perform(getPolicyTD("DeclineByCustomer", "TestData"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_DECLINED);

		policy.policyDocGen().start();
		verifyOptionIsEnabled(null, DocGenEnum.DeliveryMethod.E_SIGNATURE, true, false, AA11AZ);
	}

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario3(@Optional("") String state) {

		mainApp().open();
		createCustomerIndividual();
		TestData td_1 = getPolicyTD().adjust(getTestSpecificTD("TestData_with_EnrolledInUBI_1").resolveLinks());
		createPolicy(td_1);
		log.info("PAS DOC: Policy created with #" + PolicySummaryPage.getPolicyNumber());
		policy.policyDocGen().start();

		CustomSoftAssertions.assertSoftly(softly -> {
			verifyOptionIsEnabled(softly, DocGenEnum.DeliveryMethod.E_SIGNATURE, false, AAUBI, ACPUBI);
			verifyOptionIsEnabled(softly, DocGenEnum.DeliveryMethod.FAX, false, AAUBI, ACPUBI);
			verifyOptionIsEnabled(softly, DocGenEnum.DeliveryMethod.CENTRAL_PRINT, false, AAUBI, ACPUBI);
			verifyOptionIsEnabled(softly, DocGenEnum.DeliveryMethod.EMAIL, true, AAUBI, ACPUBI);
			verifyOptionIsEnabled(softly, DocGenEnum.DeliveryMethod.LOCAL_PRINT, true, AAUBI, ACPUBI);

			documentActionTab.saveAndExit();
		});
	}

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario4(@Optional("") String state) {

		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		verifyDistributionChannel(null, AA11AZ);
		verifyDistributionChannel(DocGenEnum.DeliveryMethod.EMAIL, AA11AZ);
		verifyDistributionChannel(DocGenEnum.DeliveryMethod.LOCAL_PRINT, AA11AZ);
		verifyDistributionChannel(DocGenEnum.DeliveryMethod.CENTRAL_PRINT, AA11AZ);
		verifyDistributionChannel(DocGenEnum.DeliveryMethod.E_SIGNATURE, AA11AZ);
	}

	private void verifyOptionIsEnabled(ETCSCoreSoftAssertions softly, DocGenEnum.DeliveryMethod id, boolean unselectDocuments, boolean expectedValue, DocGenEnum.Documents... documents) {
		documentActionTab.selectDocuments(documents);
		if (softly != null) {
			softly.assertThat(documentActionTab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.DELIVERY_METHOD).
					getRadioButton(id.get()).isEnabled()).isEqualTo(expectedValue);
		} else {
			assertThat(documentActionTab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.DELIVERY_METHOD).
					getRadioButton(id.get()).isEnabled()).isEqualTo(expectedValue);
		}
		if (unselectDocuments) {
			documentActionTab.unselectDocuments(documents);
		}
	}

	private void verifyOptionIsEnabled(ETCSCoreSoftAssertions softly, DocGenEnum.DeliveryMethod deliveryMethod, boolean expectedValue, DocGenEnum.Documents... documents) {
		verifyOptionIsEnabled(softly, deliveryMethod, false, expectedValue, documents);
	}

	private void verifyDistributionChannel(DocGenEnum.DeliveryMethod deliveryMethod, DocGenEnum.Documents... documents) {

		policy.policyDocGen().start();
		if (deliveryMethod != null) {
			verifyOptionIsEnabled(null, deliveryMethod, true, documents);
			documentActionTab.getAssetList().getAsset("Delivery Method", RadioGroup.class)
					.setValue(deliveryMethod.get());
			if (documentActionTab.textboxEmailAddress.isPresent()) {
				documentActionTab.textboxEmailAddress.setValue("test@gmail.com");
			} else if (documentActionTab.eSignatureEmail.isPresent()) {
				documentActionTab.eSignatureOkBtn.click();
				assertThat(documentActionTab.eSignatureEmailError).isPresent();
				documentActionTab.eSignatureEmail.setValue("test@gmail.com");
				documentActionTab.eSignatureOkBtn.click();
			}
			documentActionTab.buttonOk.click();
		} else {
			documentActionTab.previewDocuments(null, AA11AZ);
			WebDriverHelper.switchToDefault();
		}
		if (documentActionTab.buttonOk.isPresent()) {
			documentActionTab.saveAndExit();
		}
		String distributionChannel = PasDocImpl.verifyDocumentsGenerated(PolicySummaryPage.getPolicyNumber(), documents)
				.getDistributionChannel().get(0).getClass().getSimpleName();
		if (deliveryMethod == null) {
			assertThat(distributionChannel).isEqualTo(PreviewChannel.class.getSimpleName());
		} else {
			switch (deliveryMethod) {
				case EMAIL:
					assertThat(distributionChannel).isEqualTo(EmailChannel.class.getSimpleName());
					break;
				case LOCAL_PRINT:
					assertThat(distributionChannel).isEqualTo(LocalPrintChannel.class.getSimpleName());
					break;
				case CENTRAL_PRINT:
					assertThat(distributionChannel).isEqualTo(CentralPrintChannel.class.getSimpleName());
					break;
				case FAX:
					assertThat(distributionChannel).isEqualTo(FaxChannel.class.getSimpleName());
					break;
				case E_SIGNATURE:
					assertThat(distributionChannel).isEqualTo(ESignatureChannel.class.getSimpleName());
					break;
			}
		}
	}
}