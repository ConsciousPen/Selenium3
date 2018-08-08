package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.EndorsementForms;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestEndorsementsTabAbstract extends PolicyBaseTest {

	EndorsementTab endorsementTab = new EndorsementTab();

	public void initiateNewBusinessTx(Boolean isConversion) {
		mainApp().open();
		createCustomerIndividual();

		if (isConversion) {
			customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
			policy.getDefaultView().fillUpTo(getConversionPolicyDefaultTD(), EndorsementTab.class, false);
		} else {
			policy.initiate();
			policy.getDefaultView().fillUpTo(getPolicyTD(), EndorsementTab.class, false);
		}
	}

	public void initiateNewBusinessTx_NonPrivileged(String privilege) {
		mainApp().open(initiateLoginTD()
				.adjust("User","qa_roles")
				.adjust("Groups", privilege)
				.adjust("UW_AuthLevel", "01")
				.adjust("Billing_AuthLevel", "01")
		);

		createCustomerIndividual();

		//field is disabled for F35 user
		TestData quoteTd = getPolicyTD().mask(TestData.makeKeyPath(HomeSSMetaData.GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG.getLabel()));

		policy.initiate();
		policy.getDefaultView().fillUpTo(quoteTd, EndorsementTab.class, false);
	}

	public void initiateNewBusinessTx_NonPrivileged_AlreadyHadEndorsement(String privilege, String... endorsementFormIds) {
		initiateNewBusinessTx(false);

		for (String endorsementFormId : endorsementFormIds) {
			addOptionalEndorsement(endorsementFormId);
		}

		endorsementTab.saveAndExit();

		String quoteNumber = PolicySummaryPage.getPolicyNumber();

		openAppNonPrivilegedUser(privilege);

		SearchPage.openQuote(quoteNumber);
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
	}

	public String createPolicy(Boolean isConversion) {
		mainApp().open();
		createCustomerIndividual();

		if (isConversion) {
			return createConversionPolicy();
		} else {
			return createPolicy();
		}
	}

	public void createProposedRenewal() {
		//Move time to R-35
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));//-35 days

		//Create Proposed Renewal
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
	}

	public String createPolicyWithEndorsement(Boolean isConversion, String... endorsementFormIds) {
		initiateNewBusinessTx(isConversion);

		for (String endorsementFormId : endorsementFormIds) {
			addOptionalEndorsement(endorsementFormId);
		}

		new PremiumsAndCoveragesQuoteTab().calculatePremium();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.UNDERWRITING_AND_APPROVAL.get());
		policy.getDefaultView().fillFromTo(getPolicyTD(), UnderwritingAndApprovalTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();

		return PolicySummaryPage.getPolicyNumber();
	}

	public void initiateEndorsementTx() {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
	}

	public void initiateRenewalTx() {
		policy.renew().perform();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
	}

	public void addEndorsementForm(String endorsementName, String endorsementFormId) {
		checkEndorsementIsAvailableInOptionalEndorsements(endorsementName, endorsementFormId);
		addOptionalEndorsement(endorsementFormId);
	}

	public void editEndorsementForm(String endorsementFormId) {
		checkEditLinkIsAvailable(endorsementFormId);
		editEndorsementAndVerify(endorsementFormId);
	}

	public void removeEndorsementForm(String endorsementName, String endorsementFormId) {
		checkRemoveLinkIsAvailable(endorsementFormId);
		removeEndorsementAndVerify(endorsementName, endorsementFormId);
	}

	public void finishNewBusinessTx() {
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.UNDERWRITING_AND_APPROVAL.get());
		policy.getDefaultView().fillFromTo(getPolicyTD(), UnderwritingAndApprovalTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();
	}

	public void finishRenewalOrEndorsementTx(Boolean isEndorsementsAdded) {
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.UNDERWRITING_AND_APPROVAL.get());

		TestData td = getPolicyTD();
		if (isEndorsementsAdded) {
			td.adjust(TestData.makeKeyPath(HomeSSMetaData.UnderwritingAndApprovalTab.class.getSimpleName(),
					HomeSSMetaData.UnderwritingAndApprovalTab.UNDERWRITER_SELECTED_INSPECTION_TYPE.getLabel()), "Interior");
		}

		policy.getDefaultView().fillFromTo(td, UnderwritingAndApprovalTab.class, BindTab.class, true);
		new BindTab().submitTab();
	}

	public void checkEndorsementIsAvailableInOptionalEndorsements (String endorsementName, String endorsementFormId) {
		assertThat(endorsementTab.tblOptionalEndorsements.getRowContains("Form ID", endorsementName).isPresent());
		assertThat(endorsementTab.getAddEndorsementLink(endorsementFormId).isPresent());
	}

	public void checkEndorsementIsNotAvailableInOptionalEndorsements (String... endorsementNames) {
		for (String endorsementName : endorsementNames) {
			assertThat(endorsementTab.tblOptionalEndorsements.getRowContains("Form ID", endorsementName).isPresent()).isFalse();
		}
	}

	public void checkEndorsementIsAvailableInIncludedEndorsements(String endorsementName) {
		assertThat(endorsementTab.tblIncludedEndorsements.getRowContains("Form ID", endorsementName).isPresent());
	}

	public void checkEndorsementIsNotAvailableInIncludedEndorsements(String... endorsementNames) {
		for (String endorsementName : endorsementNames){
			assertThat(endorsementTab.tblIncludedEndorsements.getRowContains("Form ID", endorsementName).isPresent()).isFalse();
		}
	}

	public void addOptionalEndorsement(String endorsementFormId) {
		endorsementTab.getAddEndorsementLink(endorsementFormId).click();

		if (endorsementFormId == EndorsementForms.HomeSSEndorsementForms.DS_04_68.getFormId()){
			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_68)
					.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0468.LOCATION_TYPE).setValue("Described Location");
			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_68)
					.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0468.CONSTRUCTION_TYPE).setValue("Frame");
		}

		endorsementTab.btnSaveForm.click();
	}

	public void checkEditLinkIsAvailable(String endorsementFormId) {
		assertThat(endorsementTab.verifyLinkEditIsPresent(endorsementFormId)).isEqualTo(true);
	}

	public void editEndorsementAndVerify(String endorsementFormId) {
		endorsementTab.getEditEndorsementLink(endorsementFormId,1).click();

		if (endorsementFormId == EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId()) {
			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_69)
					.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0469.DEDUCTIBLE).setValue("10%");
		} else if (endorsementFormId == EndorsementForms.HomeSSEndorsementForms.DS_04_68.getFormId()){
			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_68)
					.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0468.CONSTRUCTION_TYPE).setValue("Masonry");
		}

		endorsementTab.btnSaveForm.click();
		endorsementTab.getEditEndorsementLink(endorsementFormId,1).click();

		if (endorsementFormId == EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId()) {
			assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_69)
					.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0469.DEDUCTIBLE).getValue()).isEqualTo("10%");
		} else if (endorsementFormId == EndorsementForms.HomeSSEndorsementForms.DS_04_68.getFormId()){
			assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_68)
					.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0468.CONSTRUCTION_TYPE).getValue()).isEqualTo("Masonry");
		}

		endorsementTab.btnSaveForm.click();
	}

	public void checkRemoveLinkIsAvailable(String endorsementFormId) {
		assertThat(endorsementTab.verifyLinkRemoveIsPresent(endorsementFormId)).isEqualTo(true);
	}

	public void removeEndorsementAndVerify(String endorsementName, String endorsementFormId) {
		endorsementTab.getRemoveEndorsementLink(endorsementFormId,1).click();
		Page.dialogConfirmation.confirm();

		assertThat(endorsementTab.tblIncludedEndorsements.getRowContains("Form ID", endorsementName).isPresent()).isFalse();
	}

	public void openAppNonPrivilegedUser(String privilege) {
		mainApp().close();
		mainApp().open(initiateLoginTD()
				.adjust("User","qa_roles")
				.adjust("Groups", privilege)
				.adjust("UW_AuthLevel", "01")
				.adjust("Billing_AuthLevel", "01")
		);
	}

	public void navigateToRenewalPremiumAndCoveragesTab() {
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
	}

	public void checkDocGenTriggered(String policyNumber, AaaDocGenEntityQueries.EventNames eventName, String... docGenIds) {
		List<Document> policyDocuments = DocGenHelper.getDocumentsList(policyNumber, eventName);
		Object[] documentTemplate = policyDocuments.stream().map(Document::getTemplateId).toArray();
		for (String docGenId : docGenIds) {
			assertThat(documentTemplate).contains(docGenId);
		}
	}

	/**
	 * Check Endorsement functionality after policy was already created with Endorsements added.
	 * Non privileged user.
	 * PAS-14057, PAS-17039
	 */
	public void pas17039_checkEndorsementFunctionality() {
		checkEndorsementIsAvailableInIncludedEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName());
		checkEndorsementIsAvailableInIncludedEndorsements(EndorsementForms.HomeSSEndorsementForms.DS_04_68.getName());
		editEndorsementForm(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId());
		editEndorsementForm(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId());
		removeEndorsementForm(EndorsementForms.HomeSSEndorsementForms.DS_04_68.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_68.getFormId());
		removeEndorsementForm(EndorsementForms.HomeSSEndorsementForms.DS_04_69.getName(),
				EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId());
	}
}
