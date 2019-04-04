package aaa.modules.regression.service.home_ss.ho3;

import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.mock.ApplicationMocksManager;
import aaa.helpers.mock.model.customer.CustomerMasterMock;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

import java.util.HashSet;
import java.util.List;

import static toolkit.verification.CustomAssertions.assertThat;

/**
 * @author mlaptsionak
 * @name Validate Auto Insurance Persistency is carried over to HO policy ("PC81-M-C-HO3-UT-3603")
 * @scernario 1. Create UT customer
 * 2. Create one UT Auto Policy and note down the Auto Insurance Persistency value (View Rating Details at P&C tab).
 * NOTE: Base date (General tab) can be set a couple of years ago, so then persistency value will be not null.
 * 3. Create one UT HO3 policy and add the UT Auto policy created in Step2 to the current policy by adding in Other active AAA Policies section.
 * 4. View the 'List of other Active AAA policies'
 * Expected: Auto Insurance Persistency value should reflect the correctly in the HO Policy. i.e. If Auto Insurance Persistency value = 25 (in months) for the Auto Policy created in step 3, same value should be reflected but in years (2 years).
 * 5. Fill in the Mandatory details and navigate to PnC Tab and Calculate premium
 * 6. Click on View Rating Details
 * Expected: Auto Insurance Persistency value should reflect the correctly in the HO Policy
 * 7.  Fill in mandatory details and bind the policy.
 * 8. Retrieve the created HO3 policy and initiate an endorsement. Enter Endorsement Effective date is same as Auto Effective date
 * 9. User navigate to Applicant page and select Customer Search in Named Insured field
 * In this search regular policies doesn't show, you need to choose PUP and auto policy from CustomerMasterMockData.xslx tab POLICIES and then search this customer in CUSTOMER_MASTER_REQUEST tab (Connemara Morgan).
 * The main purpose of this and further steps is to check, whether customer shows in the search results and check if additional policies of this customers shows in the Other Active AAA policies tab.
 * 10. Search for customer and select first customer from the displayed results( First name, Last name, Birth date and click on Search button
 * 11. Validate if the Other Active policies of the customer prefill into ‘Other Active AAA Policies’ section of Home policy
 */

public class TestAutoInsuranceCarriedOverToHoPolicy extends HomeSSHO3BaseTest {

	@Parameters({"state"})
	@StateList(statesExcept = {Constants.States.CA})
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testAutoInsuranceCarriedOverToHoPolicy(@Optional("") String state) {

		ApplicantTab applicantTab = new ApplicantTab();

		String autoInsurancePersistency = "Auto insurance persistency";
		TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS), "DataGather", TEST_DATA_KEY)
				.adjust("GeneralTab|NamedInsuredInformation[0]|Base Date",
						TimeSetterUtil.getInstance().getCurrentTime().minusMonths(Integer.parseInt(getTestSpecificTD(TEST_DATA_KEY).getValue("AAA Insurance Persistency"))).format(DateTimeUtils.MM_DD_YYYY));
		mainApp().open();
		createCustomerIndividual();

		PolicyType.AUTO_SS.get().initiate();
		PolicyType.AUTO_SS.get().getDefaultView().fillUpTo(tdAuto, PremiumAndCoveragesTab.class, true);

		//Check 'Auto Insurance Persistency' in Rating Details Section for AutoSS policy
		PremiumAndCoveragesTab.RatingDetailsView.open();
		assertThat(getTestSpecificTD(TEST_DATA_KEY).getValue(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.AUTO_INSURANCE_PERSISTENCY.getLabel()))
				.isEqualTo(new PremiumAndCoveragesTab().getRatingDetailsUnderwritingValueData().getValue(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.AUTO_INSURANCE_PERSISTENCY.getLabel()));
		PremiumAndCoveragesTab.RatingDetailsView.close();
		PolicyType.AUTO_SS.get().getDefaultView().fillFromTo(tdAuto, PremiumAndCoveragesTab.class, PurchaseTab.class, true);
		PolicyType.AUTO_SS.get().dataGather().getView().getTab(PurchaseTab.class).submitTab();
		String autoPolicyNum = PolicySummaryPage.labelPolicyNumber.getValue();

		//Initiate Ho3 policy and add the UT Auto policy created before
		policy.initiate();
		TestData td = getTestSpecificTD("OtherActiveAAAPolicies")
				.adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel(), "Policy number"), autoPolicyNum);
		TestData tdHome = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3), "DataGather", TEST_DATA_KEY)
				.adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()),
						td);
		policy.getDefaultView().fillUpTo(tdHome, ApplicantTab.class, true);

		//Check that Auto Insurance Persistency value reflect correctly from AutoSS policy (Applicant Tab)
		assertThat(getTestSpecificTD(TEST_DATA_KEY).getValue(autoInsurancePersistency))
				.isEqualTo(new ApplicantTab().getAssetList()
						.getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES)
						.getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL)
						.getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.AUTO_INSURANCE_PERSISTENCY).getValue());
		applicantTab.submitTab();
		policy.getDefaultView().fillFromTo(tdHome, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, true);

		//Check 'Auto insurance persistency' in Rating Details (Ho3 policy)
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
		assertThat(getTestSpecificTD(TEST_DATA_KEY).getValue(autoInsurancePersistency))
				.isEqualTo(new PremiumsAndCoveragesQuoteTab().getRatingDetailsDiscountsData().getValue(autoInsurancePersistency));
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
		new PremiumsAndCoveragesQuoteTab().submitTab();
		policy.getDefaultView().fillFromTo(tdHome, MortgageesTab.class, aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab.class, true);
		policy.dataGather().getView().getTab(aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab.class).submitTab();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String homePolicyNum = PolicySummaryPage.labelPolicyNumber.getValue();

		//DD1: Retrieve the created HO3 policy and initiate an endorsement
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusMonths(1));
		mainApp().open();
		SearchPage.openPolicy(homePolicyNum);
		policy.endorse().perform(getPolicyTD("Endorsement", TEST_DATA_KEY));

		//Navigate to Applicant page -> select Customer Search in Named Insured section and set 'Connemara Morgan' as insured
		policy.getDefaultView().fillUpTo(getTestSpecificTD("TestDataEndorsement"), ApplicantTab.class, true);

		//Get list of Other active AAA policies from UI
		List<String> listOfPoliciesFromUI = applicantTab.tblListOfOtherActiveAAAPolicies.getValuesFromRows("Policy Number");

		//Get list of policies from Mock
		CustomerMasterMock cm = ApplicationMocksManager.getMock(CustomerMasterMock.class);
		List<String> listOfPoliciesFromMock = cm.getPolicies(getTestSpecificTD(TEST_DATA_KEY).getValue("Customer"));

		//Add created Auto policy to list from Mock and compare with list of Other Active policies from UI
		listOfPoliciesFromMock.add(autoPolicyNum);
		log.info(String.format("List of policies from UI: %s \n Expected list of policies: %s", listOfPoliciesFromUI.toString(), listOfPoliciesFromMock.toString()));
		assertThat(new HashSet(listOfPoliciesFromMock).equals(new HashSet(listOfPoliciesFromUI))).as("Wrong list of policies").isEqualTo(true);
	}

}
