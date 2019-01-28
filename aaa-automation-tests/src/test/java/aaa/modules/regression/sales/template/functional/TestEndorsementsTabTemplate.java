package aaa.modules.regression.sales.template.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.*;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.PrivilegeEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.datax.TestData;

public class TestEndorsementsTabTemplate extends TestEndorsementsTabAbstract {

	protected void newBusinessTx_privileged(Boolean isDependentFromEachOther, String... endorsementFormIds) {
		//Create Quote and fill up tp Endorsement tab
		createQuoteAndFillUpTo(EndorsementTab.class);

		//Add endorsements
		addEndorsementForm(endorsementFormIds);

		//Edit endorsements, Remove endorsements
		if (isDependentFromEachOther) {
			//It is done this way because if you remove parent endorsement, child endorsement is also removed.
			checkEndorsementFunctionality(endorsementFormIds[1]);
			checkEndorsementFunctionality(endorsementFormIds[0]);
		} else {
			checkEndorsementFunctionality(endorsementFormIds);
		}
	}

	protected void newBusinessTx_privileged(String... endorsementFormIds) {
		newBusinessTx_privileged(false, endorsementFormIds);
	}

	protected void endorsementTx_privileged(Boolean isDependentFromEachOther, String... endorsementFormIds) {
		//Create Policy
		openAppAndCreatePolicy();

		//Initiate Endorsement Transaction
		initiateEndorsementTx();

		//Add endorsements
		addEndorsementForm(endorsementFormIds);

		//Edit endorsements, Remove endorsements
		if (isDependentFromEachOther) {
			//It is done this way because if you remove parent endorsement, child endorsement is also removed.
			checkEndorsementFunctionality(endorsementFormIds[1]);
			checkEndorsementFunctionality(endorsementFormIds[0]);
		} else {
			checkEndorsementFunctionality(endorsementFormIds);
		}
	}

	protected void endorsementTx_privileged(String... endorsementFormIds) {
		endorsementTx_privileged(false, endorsementFormIds);
	}

	protected void renewalTx_privileged(Boolean isDependentFromEachOther, String... endorsementFormIds) {
		//Create Policy
		openAppAndCreatePolicy();

		//Initiate Renewal Transaction
		initiateRenewalTx();

		//Add endorsements
		addEndorsementForm(endorsementFormIds);

		//Edit endorsements, Remove endorsements
		if (isDependentFromEachOther) {
			//It is done this way because if you remove parent endorsement, child endorsement is also removed.
			checkEndorsementFunctionality(endorsementFormIds[1]);
			checkEndorsementFunctionality(endorsementFormIds[0]);
		} else {
			checkEndorsementFunctionality(endorsementFormIds);
		}
	}

	protected void renewalTx_privileged(String... endorsementFormIds) {
		renewalTx_privileged(false, endorsementFormIds);
	}

	protected void newBusinessTx_NonPrivileged(String parentEndorsementFormId) {
		//Open application as non privileged user - "F35" and Create Quote and fill up tp Endorsement tab
		initiateNewBusinessTx_NonPrivileged(PrivilegeEnum.Privilege.F35);

		//Check that Endorsements are not available in "Optional Endorsements" section
		checkEndorsementIsNotAvailableInOptionalEndorsements(parentEndorsementFormId);

		//Check that Endorsements are not available in "Included Endorsements" section
		checkEndorsementIsNotAvailableInIncludedEndorsements(parentEndorsementFormId);
	}

	protected void newBusinessTx_NonPrivileged_AlreadyHadEndorsement(String parentEndorsementFormId, String subEndorsementFormId) {
		//Create Quote and fill up tp Endorsement tab -> Add endorsements -> Save & Exit -> Open quote with user "F35"
		initiateNewBusinessTx_NonPrivileged_AlreadyHadEndorsement(PrivilegeEnum.Privilege.F35, parentEndorsementFormId, subEndorsementFormId);

		//Edit endorsements, Remove endorsements
		checkEndorsementFunctionality(subEndorsementFormId, parentEndorsementFormId);

		//Check that endorsements are not available for non privileged user
		checkEndorsementIsNotAvailableInOptionalEndorsements(parentEndorsementFormId, subEndorsementFormId);

		//Purchase Quote
		finishNewBusinessTx();

		//Do Endorsement Tx -> Check that 'Earthquake' endorsement doesn't exist in Endorsement tab
		initiateEndorsementTx();
		checkEndorsementIsNotAvailableInIncludedEndorsements(parentEndorsementFormId);
		checkEndorsementIsNotAvailableInOptionalEndorsements(parentEndorsementFormId);
	}

	protected void endorsementTx_NonPrivileged(String parentEndorsementFormId) {
		//Create Policy
		String policyNumber = openAppAndCreatePolicy();

		//Open application as non privileged user "F35"
		mainApp().close();
		openAppNonPrivilegedUser(PrivilegeEnum.Privilege.F35);

		//Initiate Endorsement Transaction
		SearchPage.openPolicy(policyNumber);
		initiateEndorsementTx();

		//Check that Endorsements are not available in "Optional Endorsements" section
		checkEndorsementIsNotAvailableInOptionalEndorsements(parentEndorsementFormId);

		//Check that Endorsements are not available in "Included Endorsements" section
		checkEndorsementIsNotAvailableInIncludedEndorsements(parentEndorsementFormId);
	}

	protected void endorsementTx_NonPrivileged_AlreadyHadEndorsement(String parentEndorsementFormId, String subEndorsementFormId) {
		//Create Policy with endorsements added
		String policyNumber = createPolicyWithEndorsement(parentEndorsementFormId, subEndorsementFormId);

		//Open application as non privileged user "F35"
		mainApp().close();
		openAppNonPrivilegedUser(PrivilegeEnum.Privilege.F35);

		//Initiate Endorsement Transaction
		SearchPage.openPolicy(policyNumber);
		initiateEndorsementTx();

		//Edit endorsements, Remove endorsements
		checkEndorsementFunctionality(subEndorsementFormId, parentEndorsementFormId);

		//Check that Endorsements are not available in "Optional Endorsements" section
		checkEndorsementIsNotAvailableInOptionalEndorsements(subEndorsementFormId, parentEndorsementFormId);

		//Finish Endorsement Transaction
		finishRenewalOrEndorsementTx(false);

		//Do Endorsement Tx -> Check that 'Earthquake' endorsement doesn't exist in Endorsement tab
		initiateEndorsementTx();
		checkEndorsementIsNotAvailableInIncludedEndorsements(parentEndorsementFormId, subEndorsementFormId);
		checkEndorsementIsNotAvailableInOptionalEndorsements(parentEndorsementFormId, subEndorsementFormId);
	}

	protected void renewalTx_NonPrivileged(String parentEndorsementFormId) {
		//Create Policy
		String policyNumber = openAppAndCreatePolicy();

		//Initiate Renewal Transaction
		initiateRenewalTx();
		new PremiumsAndCoveragesQuoteTab().saveAndExit();

		//Open application as non privileged user "F35"
		mainApp().close();
		openAppNonPrivilegedUser(PrivilegeEnum.Privilege.F35);

		//Open Policy and navigate to 'Premium and Coverages' tab
		SearchPage.openPolicy(policyNumber);
		navigateToRenewalPremiumAndCoveragesTab();

		//Check that Endorsements are not available in "Optional Endorsements" section
		checkEndorsementIsNotAvailableInOptionalEndorsements(parentEndorsementFormId);

		//Check that Endorsements are not available in "Included Endorsements" section
		checkEndorsementIsNotAvailableInIncludedEndorsements(parentEndorsementFormId);
	}

	protected void renewalTx_NonPrivileged_AlreadyHadEndorsement(String parentEndorsementFormId, String subEndorsementFormId) {
		//Create Policy with already added endorsements
		String policyNumber = createPolicyWithEndorsement(parentEndorsementFormId, subEndorsementFormId);

		//Initiate Renewal Transaction
		initiateRenewalTx();
		new PremiumsAndCoveragesQuoteTab().saveAndExit();

		//Open application as non privileged user "F35"
		mainApp().close();
		openAppNonPrivilegedUser(PrivilegeEnum.Privilege.F35);

		//Open Policy and navigate to 'Premium and Coverages' tab
		SearchPage.openPolicy(policyNumber);
		navigateToRenewalPremiumAndCoveragesTab();

		//Edit endorsements, Remove endorsements
		checkEndorsementFunctionality(subEndorsementFormId, parentEndorsementFormId);

		//Check that Endorsements are not available in "Optional Endorsements" section
		checkEndorsementIsNotAvailableInOptionalEndorsements(parentEndorsementFormId, subEndorsementFormId);

		//Finish Renewal Transaction
		finishRenewalOrEndorsementTx(false);

		//Do Endorsement Tx -> Check that 'Earthquake' endorsement doesn't exist in Endorsement tab
		navigateToRenewalPremiumAndCoveragesTab();
		checkEndorsementIsNotAvailableInIncludedEndorsements(parentEndorsementFormId, subEndorsementFormId);
		checkEndorsementIsNotAvailableInOptionalEndorsements(parentEndorsementFormId, subEndorsementFormId);
	}

	protected void checkDocGenTrigger_NewBusinessTx(String parentEndorsementFormId, String subEndorsementFormId, String parentEndorsementDocGenId, String childEndorsementDocGenId) {
		//Create Policy with already added endorsements
		String policyNumber = createPolicyWithEndorsement(parentEndorsementFormId, subEndorsementFormId);

		//Check that Document generation triggered after Policy Creation
		checkDocGenTriggered(policyNumber, POLICY_ISSUE, parentEndorsementDocGenId, childEndorsementDocGenId);
	}

	protected void checkDocGenTrigger_EndorsementTx(String parentEndorsementFormId, String subEndorsementFormId, String parentEndorsementDocGenId, String childEndorsementDocGenId) {
		//Create Policy
		String policyNumber = openAppAndCreatePolicy();

		//Initiate Endorsements transaction
		initiateEndorsementTx();

		//Add endorsement forms
		addEndorsementForm(parentEndorsementFormId, subEndorsementFormId);

		//Finish Endorsement Transcation
		finishRenewalOrEndorsementTx(true);

		//Check that Document generation triggered after Policy Endorsement
		checkDocGenTriggered(policyNumber, ENDORSEMENT_ISSUE, parentEndorsementDocGenId, childEndorsementDocGenId);
	}

	protected void checkDocGenTrigger_RenewalTx(String parentEndorsementFormId, String subEndorsementFormId, String parentEndorsementDocGenId, String childEndorsementDocGenId) {
		//Create Policy
		String policyNumber =  openAppAndCreatePolicy();

		//Move time to R-35 and generate Renewal in 'Proposed' status
		moveTimeAndRunRenewJobs(getTimePoints().getRenewOfferGenerationDate(PolicySummaryPage.getExpirationDate()));

		//Navigate to Renewal 'Premium and Coverages' tab
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		navigateToRenewalPremiumAndCoveragesTab();

		//Add endorsements
		addEndorsementForm(parentEndorsementFormId, subEndorsementFormId);

		//Finish Renewal Transaction
		finishRenewalOrEndorsementTx(true);

		//Check that Document generation triggered after Policy Endorsement
		checkDocGenTriggered(policyNumber, RENEWAL_OFFER, parentEndorsementDocGenId, childEndorsementDocGenId);
	}

	protected void checkPremium_NewBusinessTx(String parentEndorsementFormId, String subEndorsementFormId) {
		//Create Quote and fill up to Endorsements tab
		createQuoteAndFillUpTo(EndorsementTab.class);

		//Check that endorsements increases Premium
		checkEndorsementsIncreasesPremium(parentEndorsementFormId, subEndorsementFormId);
	}

	protected void checkPremium_EndorsementTx(String parentEndorsementFormId, String subEndorsementFormId) {
		//Create Policy
		openAppAndCreatePolicy();

		//Initiate Endorsements Transaction
		initiateEndorsementTx();

		//Check that endorsements increases Premium
		checkEndorsementsIncreasesPremium(parentEndorsementFormId, subEndorsementFormId);
	}

	protected void checkPremium_RenewalTx(String parentEndorsementFormId, String subEndorsementFormId) {
		//Create Policy
		openAppAndCreatePolicy();

		//Initiate Renewal Transaction
		initiateRenewalTx();

		//Check that endorsements increases Premium
		checkEndorsementsIncreasesPremium(parentEndorsementFormId, subEndorsementFormId);
	}

	protected void newBusinessTx_Privileged_Conversion(String parentEndorsementFormId, String subEndorsementFormId) {
		//Create Conversion Quote and fill up to Endorsements tab
		createConversionQuoteAndFillUpTo(getConversionPolicyDefaultTD(), EndorsementTab.class, false);

		//Add endorsements
		addEndorsementForm(parentEndorsementFormId, subEndorsementFormId);

		//Edit endorsements, Remove endorsements
		checkEndorsementFunctionality(subEndorsementFormId, parentEndorsementFormId);

		//Check privileged user can add endorsement back
		checkEndorsementIsAvailableInOptionalEndorsements(parentEndorsementFormId);
	}

	protected void endorsementTx_Privileged_Conversion(String parentEndorsementFormId, String subEndorsementFormId) {
		//Create Conversion Policy
		String policyNumber = openAppAndCreateConversionPolicy();
		payTotalAmtDue(TimeSetterUtil.getInstance().getCurrentTime().plusDays(35), policyNumber);

		//Initiate Endorsement transaction
		initiateEndorsementTx();

		//Add endorsements
		addEndorsementForm(parentEndorsementFormId, subEndorsementFormId);

		//Edit endorsements, Remove endorsements
		checkEndorsementFunctionality(subEndorsementFormId, parentEndorsementFormId);

		//Check privileged user can add endorsement back
		checkEndorsementIsAvailableInOptionalEndorsements(parentEndorsementFormId);
	}

	protected void renewalTx_Privileged_Conversion(String parentEndorsementFormId, String subEndorsementFormId) {
		//Create Conversion Policy
		String policyNumber = openAppAndCreateConversionPolicy();
		payTotalAmtDue(TimeSetterUtil.getInstance().getCurrentTime().plusDays(35), policyNumber);

		//Initiate Renewal transactions
		initiateRenewalTx();

		//Add endorsements
		addEndorsementForm(parentEndorsementFormId, subEndorsementFormId);

		//Edit endorsements, Remove endorsements
		checkEndorsementFunctionality(subEndorsementFormId, parentEndorsementFormId);

		//Check privileged user can add endorsement back
		checkEndorsementIsAvailableInOptionalEndorsements(parentEndorsementFormId);
	}

	private void initiateNewBusinessTx_NonPrivileged(PrivilegeEnum.Privilege privilege) {
		openAppNonPrivilegedUser(privilege);

		createCustomerIndividual();

		//field is disabled for F35 user
		TestData quoteTd = getPolicyTD().mask(TestData.makeKeyPath(HomeSSMetaData.GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG.getLabel()));

		policy.initiate();
		policy.getDefaultView().fillUpTo(quoteTd, EndorsementTab.class, false);
	}

	private void initiateNewBusinessTx_NonPrivileged_AlreadyHadEndorsement(PrivilegeEnum.Privilege privilege, String... endorsementFormIds) {
		createQuoteAndFillUpTo(EndorsementTab.class);

		for (String endorsementFormId : endorsementFormIds) {
			addOptionalEndorsement(endorsementFormId);
		}

		endorsementTab.saveAndExit();

		String quoteNumber = PolicySummaryPage.getPolicyNumber();

		mainApp().close();
		openAppNonPrivilegedUser(privilege);

		SearchPage.openQuote(quoteNumber);
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
	}
}