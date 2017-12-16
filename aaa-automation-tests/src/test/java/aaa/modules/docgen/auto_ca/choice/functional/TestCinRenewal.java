package aaa.modules.docgen.auto_ca.choice.functional;

import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.models.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.docgen.template.functional.PolicyCINTemplate;
import toolkit.utils.TestInfo;

public class TestCinRenewal extends PolicyCINTemplate {

	/**
	 * @author Rokas Lazdauskas
	 * @name Test CIN Document generation (MVR Activity)
	 * @details TODO: Change testCaseID
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.DOCGEN, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-6341")
	public void testCinRenewalMvr(@Optional("CA") String state) {
		String policyNumber = createPolicyForTest(getPolicyDefaultTD());
		renewPolicy(MVR, policyNumber);
		List<Document> documentsList = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);
		verifyDocumentOrder(documentsList, DocGenEnum.Documents.AA02CA, DocGenEnum.Documents.AA09CA);
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name Test CIN Document generation (CLUE Activity)
	 * @details TODO: Change testCaseID
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.DOCGEN, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-6341")
	public void testCinRenewalClue(@Optional("CA") String state) {
		String policyNumber = createPolicyForTest(getPolicyDefaultTD());
		renewPolicy(CLUE, policyNumber);
		List<Document> documentsList = DocGenHelper.getDocumentsList(policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER);
		verifyDocumentOrder(documentsList, DocGenEnum.Documents.AA02CA, DocGenEnum.Documents.AA09CA);
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}
}
