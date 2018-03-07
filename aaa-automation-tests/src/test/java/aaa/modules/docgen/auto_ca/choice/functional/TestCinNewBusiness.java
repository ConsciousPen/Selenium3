package aaa.modules.docgen.auto_ca.choice.functional;

import java.util.List;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.google.inject.internal.ImmutableMap;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.docgen.template.functional.PolicyCINTemplate;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestCinNewBusiness extends PolicyCINTemplate {

	private Map<String, String> adjustmentMap = ImmutableMap.of(
			"PrefillTabMVR", TestData.makeKeyPath(AutoCaMetaData.PrefillTab.class.getSimpleName()),
			"PremiumAndCoveragesTab", TestData.makeKeyPath(AutoCaMetaData.PremiumAndCoveragesTab.class.getSimpleName()),
			"AAAProductOwned", TestData.makeKeyPath(AutoCaMetaData.GeneralTab.class.getSimpleName()),
			"PrefillTabClue", TestData.makeKeyPath(AutoCaMetaData.PrefillTab.class.getSimpleName())
	);

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name Test CIN Document generation (MVR activity exists)
	 * @details TODO: Change testCaseID
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.DOCGEN, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-6341")
	public void testCinNewBusinessMvr(@Optional("CA") String state) {
		TestData policyTestData = preparePolicyTestData(adjustmentMap,
				"PrefillTabMVR", "PremiumAndCoveragesTab", "AAAProductOwned");
		String policyNumber = createPolicyForTest(policyTestData);
		//get all the documents in the package
		List<Document> documentsList = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE);
		//check the document sequence
		verifyDocumentOrder(documentsList, DocGenEnum.Documents.AA02CA, DocGenEnum.Documents.WUAECA);
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name Test CIN Document generation (CLUE activity exists)
	 * @details TODO: Change testCaseID
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.DOCGEN, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-6341")
	public void testCinNewBusinessClue(@Optional("CA") String state) {
		TestData policyTestData = preparePolicyTestData(adjustmentMap,
				"PrefillTabClue", "PremiumAndCoveragesTab", "AAAProductOwned");
		String policyNumber = createPolicyForTest(policyTestData);
		//get all the documents in the package
		List<Document> documentsList = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE);
		//check the document sequence
		verifyDocumentOrder(documentsList, DocGenEnum.Documents.AA02CA, DocGenEnum.Documents.WUAECA);
	}
}