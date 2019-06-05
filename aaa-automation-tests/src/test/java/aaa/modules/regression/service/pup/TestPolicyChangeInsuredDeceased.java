package aaa.modules.regression.service.pup;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * @author Lina Li
 * <b> Test PolicyChangeInsuredDeceased </b>
 * <p> Steps:
 * <p> 1. Create an individual policy
 * <p> 2. Create Pup Policy with more than one named insured
 * <p> 3. Initiate Endorsement and navigate to Applicant tab
 * <p> 4. Change the 'Relationship to Primary Named Insured' to 'Deceased' for one or more named insureds, except at least one surviving insured, but don't mark him/her as primary
 * <p> 5. Check the error <ER-0387>
 * <p> 6. Change the status of the surviving named insured to 'Primary Insured'
 * <p> 7. Rate and Bind the policy
 *
 */

public class TestPolicyChangeInsuredDeceased extends PersonalUmbrellaBaseTest{
	
	@Parameters({"state"})
	//@StateList("All")
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.PUP )
	public void testPolicyChangeInsuredDeceased(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		
		 Map<String, String> primaryPolicies = getPrimaryPoliciesForPup();
		 TestData tdPolicy = getPolicyTD().adjust(getTestSpecificTD("TestData_AddNI").resolveLinks());
	     PrefillTab prefillTab = policy.getDefaultView().getTab(PrefillTab.class);
	     tdPolicy = prefillTab.adjustWithRealPolicies(tdPolicy, primaryPolicies);
		
		String policyNumber= createPolicy(tdPolicy);
		log.info("TEST: Change the namd insured to Deceased #" + policyNumber);
		
		TestData td_Endorsement = getPolicyTD("Endorsement", "TestData");
		TestData td_Endorsement1 = getTestSpecificTD("TestData_Endorsement1");
		TestData td_Endorsement2 = getTestSpecificTD("TestData_Endorsement2");

//       change NI1 as Deceased 
		policy.endorse().performAndFill(td_Endorsement);
		policy.getDefaultView().fill(td_Endorsement1);
		assertThat(prefillTab.getNamedInsuredAssetList().getWarning(PersonalUmbrellaMetaData.PrefillTab.NamedInsured.RELATIONSHIP_TO_PRIMARY_NAMED_INSURED))
				.valueContains("One named insured must be selected as the primary insured");
		
//      and NI2 as primary insured
		prefillTab.getNamedInsuredRow(2).getCell(5).controls.links.get("View/Edit").click(Waiters.AJAX);		
	    policy.getDefaultView().fill(td_Endorsement2);
	    assertThat(PolicySummaryPage.tableInsuredInformation.getRow(2).getCell(3)).hasValue("Primary Insured");
	    
	}
}



