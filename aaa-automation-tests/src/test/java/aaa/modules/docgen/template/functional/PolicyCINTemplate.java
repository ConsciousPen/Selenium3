package aaa.modules.docgen.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.testng.Assert;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

/**
 * @author Pavel_Mikhnevich
 * @name Test Create CA Select Auto Policy
 */
public abstract class PolicyCINTemplate extends PolicyBaseTest {

	protected static final String DEFAULT_TEST_RENEWAL_KEY = "TestData_Renewal_";
	protected static final String CLUE = "CLUE";
	protected static final String MVR = "MVR";

	/**
	 * Create a policy for test with specific data
	 *
	 * @scenario
	 * 1. Create Customer
	 * 2. Prepare a test-specific data
	 * 3. Create CA Select Auto Policy
	 * 4. Verify Policy status is 'Policy Active'
	 * @details
	 */
	protected String createPolicyForTest(TestData policyTestData) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(policyTestData);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		return policyNumber;
	}

	/**
	 * Verify CIN document has been generated and has the right sequenceID
	 *
	 * @param documentsList list of documents in a package
	 * @param docBefore     document configured to be right before the CIN
	 * @param docAfter      document configured to be right after the CIN
	 */
	protected void verifyDocumentOrder(@Nonnull List<Document> documentsList, @Nullable DocGenEnum.Documents docBefore, @Nullable DocGenEnum.Documents docAfter) {
		Map<String, String> sequenceMap = documentsList.stream().collect(Collectors.toMap(Document::getTemplateId, Document::getSequence));

		//check CIN has been generated
		Assert.assertTrue(sequenceMap.containsKey(DocGenEnum.Documents.AHAUXX.getId()));
		//check the order
		if (sequenceMap.containsKey(DocGenEnum.Documents.AHAUXX.getId())) {
			Long cinDocSequenceID = Long.parseLong(sequenceMap.get(DocGenEnum.Documents.AHAUXX.getId()));
			//if docBefore set and generated, compare the order
			if (docBefore != null && sequenceMap.containsKey(docBefore.getId())) {
				Long docBeforeSequenceID = Long.parseLong(sequenceMap.get(docBefore.getId()));
				Assert.assertTrue(docBeforeSequenceID.compareTo(cinDocSequenceID) < 0);
			}
			//if docAfter set and generated, compare the order
			if (docAfter != null && sequenceMap.containsKey(docAfter.getId())) {
				Long docAfterSequenceID = Long.parseLong(sequenceMap.get(docAfter.getId()));
				Assert.assertTrue(docAfterSequenceID.compareTo(cinDocSequenceID) > 0);
			}
		}
	}

	/**
	 * Renew the policy specified with policy number
	 *
	 * @param activityType
	 * @scenario
	 * 1. Change time to R-35
	 * 2. Create Manual Renewal for Policy (add driver which has chargeable violation)
	 * @details
	 */
	protected void renewPolicy(String activityType, String policyNumber) {
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		LocalDateTime renewImageGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate); //-35
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		TestData renewalTestData = getTestSpecificTD(DEFAULT_TEST_RENEWAL_KEY + activityType);
		policy.createRenewal(renewalTestData);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	protected TestData preparePolicyTestData(Map<String, String> adjustmentMap, String... adjustmentKeys) {
		//get common policy TestData
		TestData testData = getPolicyTD();
		//adjust TestData with a subset of keys
		Arrays.stream(adjustmentKeys).forEach(key -> testData.adjust(adjustmentMap.get(key), getTestSpecificTD(key)));
		return testData;
	}
}
