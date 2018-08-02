package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import aaa.modules.policy.PolicyBaseTest;

import static org.assertj.core.api.Assertions.assertThat;

public class TestEndorsementsTabAbstract extends PolicyBaseTest {

	EndorsementTab endorsementTab = new EndorsementTab();

	public void initiateNewBusinessTx(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();

		policyType.get().initiate();
		policyType.get().getDefaultView().fillUpTo(getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData"),
				EndorsementTab.class, false);
	}

	public void initiateEndorsementTx(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();

		policyType.get().createPolicy(getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData"));
		policyType.get().endorse().perform(getStateTestData(testDataManager.policy.get(policyType).getTestData("Endorsement"), "TestData"));
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
	}

	public void initiateRenewalTx(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();

		policyType.get().createPolicy(getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData"));
		policyType.get().renew().perform();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
	}

	public void testEndorsementForms(String endorsementName, String endorsementFormId) {
		checkEndorsementIsAvailableInOptionalEndorsements(endorsementName, endorsementFormId);

		addOptionalEndorsement(endorsementFormId);

		checkEditLinkIsAvailable(endorsementFormId);
		editEndorsementAndVerify(endorsementFormId);

		checkRemoveLinkIsAvailable(endorsementFormId);
		removeEndorsementAndVerify(endorsementName, endorsementFormId);
	}

	private void checkEndorsementIsAvailableInOptionalEndorsements (String endorsementName, String endorsementFormId) {
		assertThat(endorsementTab.tblOptionalEndorsements.getRowContains("Form ID", endorsementName).isPresent());
		assertThat(endorsementTab.getAddEndorsementLink(endorsementFormId).isPresent());
	}

	private void addOptionalEndorsement(String endorsementFormId) {
		endorsementTab.getAddEndorsementLink(endorsementFormId).click();
		endorsementTab.btnSaveForm.click();
	}

	private void checkEditLinkIsAvailable(String endorsementFormId) {
		assertThat(endorsementTab.verifyLinkEditIsPresent(endorsementFormId)).isEqualTo(true);
	}

	private void editEndorsementAndVerify(String endorsementFormId) {
		endorsementTab.getEditEndorsementLink(endorsementFormId,1).click();
		endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_69)
				.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0469.DEDUCTIBLE).setValue("10%");
		endorsementTab.btnSaveForm.click();

		endorsementTab.getEditEndorsementLink(endorsementFormId,1).click();
		assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_69)
				.getAsset(HomeSSMetaData.EndorsementTab.EndorsementDS0469.DEDUCTIBLE).getValue()).isEqualTo("10%");
		endorsementTab.btnSaveForm.click();
	}

	private void checkRemoveLinkIsAvailable(String endorsementFormId) {
		assertThat(endorsementTab.verifyLinkRemoveIsPresent(endorsementFormId)).isEqualTo(true);
	}

	private void removeEndorsementAndVerify(String endorsementName, String endorsementFormId) {
		endorsementTab.getRemoveEndorsementLink(endorsementFormId,1).click();
		Page.dialogConfirmation.confirm();

		assertThat(endorsementTab.tblIncludedEndorsements.getRowContains("Form ID", endorsementName).isPresent()).isFalse();
	}
}
