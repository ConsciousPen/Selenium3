package aaa.modules.regression.sales.template.functional;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.EndorsementForms;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestEndorsementsTabAbstract extends CommonTemplateMethods {

	PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	EndorsementTab endorsementTab = new EndorsementTab();

	protected void initiateNewBusinessTx_NonPrivileged(String privilege) {
		openAppNonPrivilegedUser(privilege);

		createCustomerIndividual();

		//field is disabled for F35 user
		TestData quoteTd = getPolicyTD().mask(TestData.makeKeyPath(HomeSSMetaData.GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG.getLabel()));

		policy.initiate();
		policy.getDefaultView().fillUpTo(quoteTd, EndorsementTab.class, false);
	}

	protected void initiateNewBusinessTx_NonPrivileged_AlreadyHadEndorsement(String privilege, String... endorsementFormIds) {
		createQuoteAndFillUpTo(EndorsementTab.class);

		for (String endorsementFormId : endorsementFormIds) {
			addOptionalEndorsement(endorsementFormId);
		}

		endorsementTab.saveAndExit();

		String quoteNumber = PolicySummaryPage.getPolicyNumber();

		closeOpenAppNonPrivilegedUser(privilege);

		SearchPage.openQuote(quoteNumber);
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
	}

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

	protected void addEndorsementForm(String endorsementFormId) {
		checkEndorsementIsAvailableInOptionalEndorsements(endorsementFormId);
		addOptionalEndorsement(endorsementFormId);
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
		assertThat(endorsementTab.tblOptionalEndorsements.getRowContains(PolicyConstants.PolicyIncludedAndSelectedEndorsementsTable.FORM_ID, formId).isPresent());
		assertThat(endorsementTab.getAddEndorsementLink(formId).isPresent());
	}

	protected void checkEndorsementIsNotAvailableInOptionalEndorsements(String... formIds) {
		for (String formId : formIds) {
			assertThat(endorsementTab.tblOptionalEndorsements.getRowContains(PolicyConstants.PolicyIncludedAndSelectedEndorsementsTable.FORM_ID, formId).isPresent()).isFalse();
		}
	}

	protected void checkEndorsementIsAvailableInIncludedEndorsements(String formId) {
		assertThat(endorsementTab.tblIncludedEndorsements.getRowContains(PolicyConstants.PolicyIncludedAndSelectedEndorsementsTable.FORM_ID, formId).isPresent());
	}

	protected void checkEndorsementIsNotAvailableInIncludedEndorsements(String... formIds) {
		for (String formId : formIds){
			assertThat(endorsementTab.tblIncludedEndorsements.getRowContains(PolicyConstants.PolicyIncludedAndSelectedEndorsementsTable.FORM_ID, formId).isPresent()).isFalse();
		}
	}

	private void addOptionalEndorsement(String endorsementFormId) {
		endorsementTab.getAddEndorsementLink(endorsementFormId).click();

		if (endorsementFormId == EndorsementForms.HomeSSEndorsementForms.DS_04_68.getFormId()){
			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_68)
					.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0468.LOCATION_TYPE).setValue("Described Location");
			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_68)
					.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0468.CONSTRUCTION_TYPE).setValue("Frame");
		} else if (endorsementFormId == EndorsementForms.HomeSSEndorsementForms.HS_04_36.getFormId()) {
			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_04_36)
					.getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS0436.COVERAGE_LIMIT).setValue("index=1");
			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_04_36)
					.getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS0436.DESCRIPTION_OF_STRUCTURE).setValue("Residence Premises");
			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_04_36)
					.getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS0436.CONSTRUCTION_TYPE).setValue("Frame");
		}

		endorsementTab.btnSaveForm.click();
	}

	public void checkEditLinkIsAvailable(String endorsementFormId) {
		assertThat(endorsementTab.isLinkEditPresent(endorsementFormId)).isEqualTo(true);
	}

	private void editEndorsementAndVerify(String endorsementFormId) {
		endorsementTab.getEditEndorsementLink(endorsementFormId,1).click();

		if (endorsementFormId == EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId()) {
			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_69)
					.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0469.DEDUCTIBLE).setValue("10%");
		} else if (endorsementFormId == EndorsementForms.HomeSSEndorsementForms.DS_04_68.getFormId()){
			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_68)
					.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0468.CONSTRUCTION_TYPE).setValue("Masonry");
		} else if (endorsementFormId == EndorsementForms.HomeSSEndorsementForms.HS_04_36.getFormId()) {
			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_04_36)
					.getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS0436.CONSTRUCTION_TYPE).setValue("Masonry");
		}

		endorsementTab.btnSaveForm.click();
		endorsementTab.getEditEndorsementLink(endorsementFormId,1).click();

		if (endorsementFormId == EndorsementForms.HomeSSEndorsementForms.DS_04_69.getFormId()) {
			assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_69)
					.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0469.DEDUCTIBLE).getValue()).isEqualTo("10%");
		} else if (endorsementFormId == EndorsementForms.HomeSSEndorsementForms.DS_04_68.getFormId()){
			assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_68)
					.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0468.CONSTRUCTION_TYPE).getValue()).isEqualTo("Masonry");
		} else if (endorsementFormId == EndorsementForms.HomeSSEndorsementForms.DS_04_68.getFormId()) {
			assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_04_36)
					.getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS0436.CONSTRUCTION_TYPE).getValue()).isEqualTo("Masonry");
		}

		endorsementTab.btnSaveForm.click();
	}

	private void checkRemoveLinkIsAvailable(String endorsementFormId) {
		assertThat(endorsementTab.isLinkRemovePresent(endorsementFormId)).isEqualTo(true);
	}

	private void removeEndorsementAndVerify(String endorsementFormId) {
		endorsementTab.getRemoveEndorsementLink(endorsementFormId,1).click();
		Page.dialogConfirmation.confirm();

		assertThat(endorsementTab.tblIncludedEndorsements.getRowContains(PolicyConstants.PolicyIncludedAndSelectedEndorsementsTable.FORM_ID, endorsementFormId).isPresent()).isFalse();
	}

	protected void closeOpenAppNonPrivilegedUser(String privilege) {
		mainApp().close();
		openAppNonPrivilegedUser(privilege);
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
		assertThat(new Dollar(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium()).moreThan(origPremiumValue));

		verifyEndorsementsPresent(PolicyConstants.PolicyEndorsementFormsTable.DESCRIPTION, endorsementFormIds);
	}

	private void verifyEndorsementsPresent(String columnName, String... endorsements) {
		for (String endorsement : endorsements) {
			PremiumsAndCoveragesQuoteTab.tableEndorsementForms.getRowContains(columnName, endorsement).verify.present();
		}
	}
}
