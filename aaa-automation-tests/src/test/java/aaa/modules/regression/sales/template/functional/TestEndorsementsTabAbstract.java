package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.enums.EndorsementForms;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.datax.TestData;


public class TestEndorsementsTabAbstract extends CommonTemplateMethods {

	PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	EndorsementTab endorsementTab = new EndorsementTab();

	protected String createPolicyWithEndorsement(String... endorsementFormIds) {
		createQuoteAndFillUpTo(EndorsementTab.class);

		for (String endorsementFormId : endorsementFormIds) {
			addOptionalEndorsement(endorsementFormId);
		}

		premiumsAndCoveragesQuoteTab.calculatePremium();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.UNDERWRITING_AND_APPROVAL.get());
		policy.getDefaultView().fillFromTo(getPolicyTD(), UnderwritingAndApprovalTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();

		return PolicySummaryPage.getPolicyNumber();
	}

	protected void initiateEndorsementTx() {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
	}

	protected void initiateRenewalTx() {
		policy.renew().perform();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
	}

	protected void addEndorsementForm(String... endorsementFormIds) {
		for(String endorsementFormId: endorsementFormIds) {
			checkEndorsementIsAvailableInOptionalEndorsements(endorsementFormId);
			addOptionalEndorsement(endorsementFormId);
		}
	}

	protected void editEndorsementForm(String endorsementFormId) {
		checkEditLinkIsAvailable(endorsementFormId);
		editEndorsementAndVerify(endorsementFormId);
	}

	protected void removeEndorsementForm(String endorsementFormId) {
		checkRemoveLinkIsAvailable(endorsementFormId);
		removeEndorsementAndVerify(endorsementFormId);
	}

	protected void finishNewBusinessTx() {
		premiumsAndCoveragesQuoteTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.UNDERWRITING_AND_APPROVAL.get());
		policy.getDefaultView().fillFromTo(getPolicyTD(), UnderwritingAndApprovalTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();
	}

	protected void finishRenewalOrEndorsementTx(Boolean isEndorsementsAdded) {
		premiumsAndCoveragesQuoteTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.UNDERWRITING_AND_APPROVAL.get());

		TestData td = getPolicyTD();
		if (isEndorsementsAdded && (getPolicyType() == PolicyType.HOME_SS_DP3 || getPolicyType() == PolicyType.HOME_SS_HO3)) {
			td.adjust(TestData.makeKeyPath(HomeSSMetaData.UnderwritingAndApprovalTab.class.getSimpleName(),
					HomeSSMetaData.UnderwritingAndApprovalTab.UNDERWRITER_SELECTED_INSPECTION_TYPE.getLabel()), "Interior");
		}

		policy.getDefaultView().fillFromTo(td, UnderwritingAndApprovalTab.class, BindTab.class, true);
		new BindTab().submitTab();
	}

	protected void checkEndorsementIsAvailableInOptionalEndorsements(String formId) {
		assertThat(endorsementTab.tblOptionalEndorsements.getRowContains(PolicyConstants.PolicyIncludedAndSelectedEndorsementsTable.FORM_ID, formId)).isPresent();
		assertThat(endorsementTab.getAddEndorsementLink(formId)).isPresent();
	}

	protected void checkEndorsementIsNotAvailableInOptionalEndorsements(String... formIds) {
		for (String formId : formIds) {
			assertThat(endorsementTab.tblOptionalEndorsements.getRowContains(PolicyConstants.PolicyIncludedAndSelectedEndorsementsTable.FORM_ID, formId)).isAbsent();
		}
	}

	protected void checkEndorsementIsAvailableInIncludedEndorsements(String formId) {
		assertThat(endorsementTab.tblIncludedEndorsements.getRowContains(PolicyConstants.PolicyIncludedAndSelectedEndorsementsTable.FORM_ID, formId)).isPresent();
	}

	protected void checkEndorsementIsNotAvailableInIncludedEndorsements(String... formIds) {
		for (String formId : formIds){
			assertThat(endorsementTab.tblIncludedEndorsements.getRowContains(PolicyConstants.PolicyIncludedAndSelectedEndorsementsTable.FORM_ID, formId)).isAbsent();
		}
	}

	protected void addOptionalEndorsement(String endorsementFormId) {
		endorsementTab.getAddEndorsementLink(endorsementFormId).click();

		EndorsementForms.HomeSSEndorsementForms formEnum = EndorsementForms.HomeSSEndorsementForms.getFormEnum(endorsementFormId);

		switch (formEnum) {
			case DS_04_63:
				endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_63)
						.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0463.LOCATION_TYPE).setValue("Described Location");
				endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_63)
						.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0463.COVERAGE_LIMIT).setValue("index=1");
				break;
			case DS_04_68:
				endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_68)
						.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0468.LOCATION_TYPE).setValue("Described Location");
				endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_68)
						.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0468.CONSTRUCTION_TYPE).setValue("Frame");
				break;
			case HS_04_36:
				endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_04_36)
						.getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS0436.COVERAGE_LIMIT).setValue("index=1");
				endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_04_36)
						.getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS0436.DESCRIPTION_OF_STRUCTURE).setValue("Residence Premises");
				endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_04_36)
						.getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS0436.CONSTRUCTION_TYPE).setValue("Frame");
				break;
			default:
				log.error("No Such Form Configuration Found");
				break;
		}

		endorsementTab.btnSaveForm.click();
	}

	protected void checkEditLinkIsAvailable(String endorsementFormId) {
		// Link should be Present and Editable. Method checks for both (Disabled link is <span> tags, which isn't considered as links)
		assertThat(endorsementTab.getLinkEdit(endorsementFormId)).isPresent();
	}

	protected void editEndorsementAndVerify(String endorsementFormId) {
		endorsementTab.getEditEndorsementLink(endorsementFormId,1).click();

		EndorsementForms.HomeSSEndorsementForms formEnum = EndorsementForms.HomeSSEndorsementForms.getFormEnum(endorsementFormId);

		switch (formEnum) {
			case DS_04_20:
				endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_20)
						.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0420.COVERAGE_LIMIT).setValue("150%");
				break;
			case DS_04_63:
				endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_63)
						.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0463.COVERAGE_LIMIT).setValue("$10000");
				break;
			case DS_04_68:
				endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_68)
						.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0468.CONSTRUCTION_TYPE).setValue("Masonry");
				break;
			case DS_04_69:
				endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_69)
						.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0469.DEDUCTIBLE).setValue("10%");
				break;
			case HS_04_36:
				endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_04_36)
						.getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS0436.CONSTRUCTION_TYPE).setValue("Masonry");
				break;
			default:
				log.error("No Such Form Configuration Found");
				break;
		}

		endorsementTab.btnSaveForm.click();
		endorsementTab.getEditEndorsementLink(endorsementFormId,1).click();

		switch (formEnum) {
			case DS_04_20:
				assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_20)
						.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0420.COVERAGE_LIMIT).getValue()).contains("150%");
				break;
			case DS_04_63:
				assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_63)
						.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0463.COVERAGE_LIMIT).getValue()).contains("$10000");
				break;
			case DS_04_68:
				assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_68)
						.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0468.CONSTRUCTION_TYPE).getValue()).contains("Masonry");
				break;
			case DS_04_69:
				assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_69)
						.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0469.DEDUCTIBLE).getValue()).contains("10%");
				break;
			case HS_04_36:
				assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_04_36)
						.getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS0436.CONSTRUCTION_TYPE).getValue()).contains("Masonry");
				break;
			default:
				log.error("No Such Form Configuration Found");
				break;
		}

		endorsementTab.btnSaveForm.click();
	}

	protected void checkRemoveLinkIsAvailable(String endorsementFormId) {
		assertThat(endorsementTab.getLinkRemove(endorsementFormId)).isPresent();
	}

	protected void removeEndorsementAndVerify(String endorsementFormId) {
		endorsementTab.getRemoveEndorsementLink(endorsementFormId,1).click();
		Page.dialogConfirmation.confirm();

		assertThat(endorsementTab.tblIncludedEndorsements.getRowContains(PolicyConstants.PolicyIncludedAndSelectedEndorsementsTable.FORM_ID, endorsementFormId)).isAbsent();
	}

	protected void navigateToRenewalPremiumAndCoveragesTab() {
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
	}

	/**
	 * Check Endorsement functionality after policy was already created with Endorsements added.
	 * Non privileged user.
	 * PAS-14057, PAS-17039
	 */
	protected void checkEndorsementFunctionality(String... endorsementFormIds) {
		for (String endorsementFormId : endorsementFormIds) {
			checkEndorsementIsAvailableInIncludedEndorsements(endorsementFormId);
			editEndorsementForm(endorsementFormId);
			removeEndorsementForm(endorsementFormId);
		}
	}

	protected void checkEndorsementsIncreasesPremium(String... endorsementFormIds) {
		premiumsAndCoveragesQuoteTab.calculatePremium();

		Dollar origPremiumValue = new Dollar(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());

		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
		for (String endorsementFormId : endorsementFormIds) {
			addOptionalEndorsement(endorsementFormId);
		}
		premiumsAndCoveragesQuoteTab.calculatePremium();

		//Endorsements sum is added.
		assertThat(new Dollar(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium()).moreThan(origPremiumValue)).isTrue();

		verifyEndorsementsPresent(PolicyConstants.PolicyEndorsementFormsTable.DESCRIPTION, endorsementFormIds);
	}

	protected void verifyEndorsementsPresent(String columnName, String... endorsements) {
		for (String endorsement : endorsements) {
			assertThat(PremiumsAndCoveragesQuoteTab.tableEndorsementForms.getRowContains(columnName, endorsement)).exists();
		}
	}
}
