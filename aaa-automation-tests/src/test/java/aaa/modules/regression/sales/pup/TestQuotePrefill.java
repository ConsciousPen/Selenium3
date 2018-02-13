package aaa.modules.regression.sales.pup;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Map;
import java.util.Map.Entry;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.common.Tab;
import aaa.common.metadata.SearchMetaData.Search;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.PupActiveUnderlyingPoliciesVerifier;
import aaa.main.enums.CustomerConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.defaulttabs.GeneralTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;

/**
 * @author Ryan Yu
 * @name Test Quote Prefill
 * @scenario
 * 1. Create new or open existent Customer.
 * 2. Start PUP quote creation.
 * 6. Click Add AAA Policy button. Enter wrong search criteria. Click Search button. Verify no results message.
 * 7. Input correct request to get data from stub(HO3 and Auto policies). Click found policies links. Verify Found policies are added to AAA policies table.
 * 8. Fill other tabs for bind quote. Bind quote. Verify policy status is Active.
 * 9. Precondition. Should be at least one existed PUP Policy with known First Name/Last Name/Zip code combintion. Go to advanced search. Search for existent Policy, by First, Second Name and Zip code combination of the primary insurade person
 * @details
 */
public class TestQuotePrefill extends PersonalUmbrellaBaseTest {
	private PrefillTab prefillTab = policy.getDefaultView().getTab(PrefillTab.class);
	private final String WRONG_SEARCH_CRITERIA_KEY = "TestData_WrongSearchCriteria";
	private final String POLICY_SEARCH_KEY = "TestData_PolicySearch";
	private Map<String, String> primaryPolicies;
	private String pupPolicyNum;
	private String firstName;
	private String lastName;
	private String zipCode;

	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.HIGH })
	@TestInfo(component = ComponentConstant.Sales.PUP)
	public void testQuotePrefill(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		storeCustomerInfo();

		primaryPolicies = getPrimaryPoliciesForPup();
		TestData td = prefillTab.adjustWithRealPolicies(getPolicyTD(), primaryPolicies);

		policy.initiate();
		checkWrongSearchCriteria();
		prefillTab.fillTab(td);
		checkPolicyTable();
		prefillTab.submitTab();
		policy.getDefaultView().fill(td.mask(prefillTab.getMetaKey()));
		assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		pupPolicyNum = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Created PUP Policy " + pupPolicyNum);
		checkPolicySearch();
	}

	private void storeCustomerInfo() {
		GeneralTab generalTab = customer.getDefaultView().getTab(GeneralTab.class);
		customer.update().start();
		firstName = generalTab.getAssetList().getAsset(CustomerMetaData.GeneralTab.FIRST_NAME).getValue();
		lastName = generalTab.getAssetList().getAsset(CustomerMetaData.GeneralTab.LAST_NAME).getValue();
		GeneralTab.tableContactDetails.getRow(1).getCell(CustomerConstants.CustomerContactsTable.ACTION).controls.buttons.get("Change").click();
		zipCode = generalTab.getAssetList().getAsset(CustomerMetaData.GeneralTab.ZIP_CODE).getValue();
		Tab.buttonTopCancel.click();
		Tab.dialogCancelAction.confirm();
	}

	private void checkWrongSearchCriteria() {
		prefillTab.buttonAddPolicy.click();
		prefillTab.searchDialog.setRawValue(getTestSpecificTD(WRONG_SEARCH_CRITERIA_KEY));
		prefillTab.searchDialog.search();
		//prefillTab.searchDialog.tableSearchResults.verify.present(false);
		assertThat(!prefillTab.searchDialog.tableSearchResults.isPresent());
		prefillTab.searchDialog.cancel();
		prefillTab.buttonRemovePolicy.click();
	}

	private void checkPolicyTable() {
		for (Entry<String, String> entry : primaryPolicies.entrySet()) {
			new PupActiveUnderlyingPoliciesVerifier().setPolicyNumber(entry.getValue()).verifyPresent();
		}
	}

	private void checkPolicySearch() {
		String firstNameKey = TestData.makeKeyPath(Search.class.getSimpleName(), Search.FIRST_NAME.getLabel());
		String lastNameKey = TestData.makeKeyPath(Search.class.getSimpleName(), Search.LAST_NAME.getLabel());
		String zipKey = TestData.makeKeyPath(Search.class.getSimpleName(), Search.ZIP_CODE.getLabel());
		TestData tdSearch = getTestSpecificTD(POLICY_SEARCH_KEY).adjust(firstNameKey, firstName).adjust(lastNameKey, lastName).adjust(zipKey, zipCode);
		SearchPage.search(tdSearch);
		
		if (SearchPage.tableSearchResults.isPresent()) {
			SearchPage.tableSearchResults.getRow("Product", "Personal Umbrella Policy").getCell(1).controls.links.getFirst().click();
		}
		
		//PolicySummaryPage.labelPolicyNumber.verify.value(pupPolicyNum);
		assertThat(PolicySummaryPage.labelPolicyNumber.getValue()).isEqualTo(pupPolicyNum);
	}
}
