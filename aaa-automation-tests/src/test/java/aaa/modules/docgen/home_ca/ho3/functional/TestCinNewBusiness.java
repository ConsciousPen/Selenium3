package aaa.modules.docgen.home_ca.ho3.functional;

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
import aaa.helpers.xml.models.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.docgen.template.functional.PolicyCINTemplate;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestCinNewBusiness extends PolicyCINTemplate {

	private Map<String, String> adjustmentMap = ImmutableMap.of(
			"NamedInsuredProperty", TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(), HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel()),
			"PublicProtectionClass", TestData.makeKeyPath(HomeCaMetaData.ReportsTab.class.getSimpleName(), HomeCaMetaData.ReportsTab.PUBLIC_PROTECTION_CLASS.getLabel())
	);

	/**
	 * @author Rokas Lazdauskas
	 * @name Test CIN Document generation (PROPERTY activity)
	 * @details TODO: Change testCaseID
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.DOCGEN, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-6341")
	public void testCinNewBusinessProperty(@Optional("CA") String state) {
		TestData policyTestData = preparePolicyTestData(adjustmentMap, "NamedInsuredProperty", "PublicProtectionClass");
		String policyNumber = createPolicyForTest(policyTestData);
		//get all the documents in the package
		List<Document> documentsList = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE);
		//check the document sequence
		verifyDocumentOrder(documentsList, DocGenEnum.Documents._61_2006, null);
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}
}
