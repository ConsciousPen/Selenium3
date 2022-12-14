package aaa.helpers.openl.testdata_generator;

import java.util.List;
import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLForm;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;

public class HomeSSDP3TestDataGenerator extends HomeSSTestDataGenerator {
	public HomeSSDP3TestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(HomeSSOpenLPolicy openLPolicy) {
		return super.getRatingData(openLPolicy);
	}

	@Override
	public TestData getProofData(HomeSSOpenLPolicy openLPolicy) {
		TestData proofData = super.getProofData(openLPolicy);
		if (openLPolicy.getPolicyDiscountInformation().isUnderlyingRenterPolicy()) {
			proofData = TestDataHelper.merge(DataProviderFactory.dataOf(HomeSSMetaData.DocumentsTab.DOCUMENTS_TO_BIND.getLabel(), DataProviderFactory.dataOf(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_UNDERLYING_INSURANCE_POLICY.getLabel(), "Yes")), proofData);
		}
		return proofData;
	}

	@Override
	public TestData getOverrideErrorData(HomeSSOpenLPolicy openLPolicy) {
		if (Double.parseDouble(openLPolicy.getCoverages().stream().filter(c -> "CovC".equals(c.getCode())).findFirst().get().getLimit()) > 100000.00) {

			TestData td = getRatingDataPattern();
			TestData overrideErrorData = DataProviderFactory.dataOf(
					MortgageesTab.class.getSimpleName(), td.getTestData(MortgageesTab.class.getSimpleName()),
					UnderwritingAndApprovalTab.class.getSimpleName(), td.getTestData(UnderwritingAndApprovalTab.class.getSimpleName()),
					DocumentsTab.class.getSimpleName(), td.getTestData(DocumentsTab.class.getSimpleName()),
					BindTab.class.getSimpleName(), td.getTestData(BindTab.class.getSimpleName())
			);

			overrideErrorData = TestDataHelper.merge(getMortgageeTabData(openLPolicy), overrideErrorData);

			TestData documentsTabData = DataProviderFactory.dataOf(
					DocumentsTab.class.getSimpleName(), getProofData(openLPolicy));

			if (openLPolicy.getPolicyAddress().isOhioMineSubsidenceCounty()) {
				documentsTabData = TestDataHelper.merge(DataProviderFactory.dataOf(
						HomeSSMetaData.DocumentsTab.class.getSimpleName(), DataProviderFactory.dataOf(
								HomeSSMetaData.DocumentsTab.DOCUMENTS_TO_ISSUE.getLabel(), DataProviderFactory.dataOf(
										HomeSSMetaData.DocumentsTab.DocumentsToIssue.OHIO_MINE_SUBSIDENCE_INSURANCE_UNDERWRITING_ASSOCIATION_APPLICATION.getLabel(), "Physically Signed"))),
						documentsTabData);
			}

			overrideErrorData = TestDataHelper.merge(documentsTabData, overrideErrorData)
					.adjust(TestData.makeKeyPath(new ErrorTab().getMetaKey()), DataProviderFactory.dataOf(
							HomeSSMetaData.ErrorTab.ERROR_OVERRIDE.getLabel(), DataProviderFactory.dataOf(
									HomeSSMetaData.ErrorTab.ErrorsOverride.MESSAGE.getLabel(), "contains=UW approval is required to bind due to Personal Property Value",
									HomeSSMetaData.ErrorTab.ErrorsOverride.OVERRIDE.getLabel(), true,
									HomeSSMetaData.ErrorTab.ErrorsOverride.APPROVAL.getLabel(), true,
									HomeSSMetaData.ErrorTab.ErrorsOverride.DURATION.getLabel(), "Life",
									HomeSSMetaData.ErrorTab.ErrorsOverride.REASON_FOR_OVERRIDE.getLabel(), "index=1")
					));
			return overrideErrorData;
		}
		return DataProviderFactory.emptyData();
	}

	@Override
	protected TestData getEndorsementTabData(HomeSSOpenLPolicy openLPolicy) {
		TestData endorsementData = new SimpleDataProvider();
		for (HomeSSOpenLForm openLForm : openLPolicy.getForms()) {
			String formCode = openLForm.getFormCode();
			if (!endorsementData.containsKey(HomeSSDP3FormTestDataGenerator.getFormMetaKey(formCode))) {
				List<TestData> tdList = HomeSSDP3FormTestDataGenerator.getFormTestData(openLPolicy, formCode);
				if (tdList != null) {
					TestData td = tdList.size() == 1 ? DataProviderFactory.dataOf(HomeSSDP3FormTestDataGenerator.getFormMetaKey(formCode), tdList.get(0)) : DataProviderFactory.dataOf(HomeSSDP3FormTestDataGenerator.getFormMetaKey(formCode), tdList);
					endorsementData.adjust(td);
				}
			}
		}
		return endorsementData;
	}
}
