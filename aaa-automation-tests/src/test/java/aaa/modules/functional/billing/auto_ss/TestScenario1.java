package aaa.modules.functional.billing.auto_ss;

import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PrefillTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.functional.billing.details.CustomerAccountDetails;
import aaa.modules.functional.billing.details.CustomerDetails;
import aaa.modules.functional.billing.details.CustomerInformationHolder;
import aaa.modules.policy.PolicyBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class TestScenario1 extends PolicyBaseTest {
//	protected static final String DEFAULT_TEST_DATA_KEY = "TestData";

	@Test(groups = {Groups.FUNCTIONAL})
	@TestInfo(component = Groups.FUNCTIONAL)
	@Parameters({STATE_PARAM})
	public void testScenario1(@Optional(StringUtils.EMPTY) String state) {
		mainApp().open();
		createCustomerIndividual();
		CustomerSummaryPage.labelCustomerName.getValue();
		CustomerSummaryPage.labelCustomerAddress.getValue();
		CustomerSummaryPage.labelCustomerDOB.getValue();
		CustomerInformationHolder.addCustomerAccountDetails(
				new CustomerAccountDetails.Builder()
						.setCustomerNumber(CustomerSummaryPage.labelCustomerNumber.getValue())
						.addCustomerDetails(new CustomerDetails.Builder()
								.setCustomerFirstName(CustomerSummaryPage.labelCustomerName.getValue().split(" ")[0])
								.setCustomerLastName(CustomerSummaryPage.labelCustomerName.getValue().split(" ")[1])
								.setCustomerDOB(TimeSetterUtil.getInstance().parse(CustomerSummaryPage.labelCustomerDOB.getValue(), DateTimeUtils.MM_DD_YYYY))
//								.setPolicyInstallmentsSchedule(BillingHelper.getInstallmentDueDates())
//								.setPolicyEffectiveDate(TimeSetterUtil.getInstance().parse(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(3).getValue(), DateTimeUtils.MM_DD_YYYY))
//								.setPolicyExpirationDate(TimeSetterUtil.getInstance().parse(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(3).getValue(), DateTimeUtils.MM_DD_YYYY).plusYears(1))
								.build())
						.build());
		createQuote();
		policy.dataGather().start();
		verifyPrefillTab();

		log.info("Quote created");
	}

	private void verifyPrefillTab() {
		PrefillTab prefillTab = new PrefillTab();
		assertSoftly(softly -> {
			softly.assertThat(prefillTab.getAssetList().getAsset(AutoSSMetaData.PrefillTab.FIRST_NAME).getValue()).isEqualTo(CustomerInformationHolder.getCurrentCustomerAccountDetails().getCurrentCustomerDetails().getCustomerFirstName());
			softly.assertThat(prefillTab.getAssetList().getAsset(AutoSSMetaData.PrefillTab.LAST_NAME).getValue()).isEqualTo(CustomerInformationHolder.getCurrentCustomerAccountDetails().getCurrentCustomerDetails().getCustomerLastName());
		});
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

//	protected TestData getPolicyTestData() {
//		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
//		td.adjust(TestData.makeKeyPath(PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), getTestSpecificTD(
//				"PremiumAndCoveragesTab_DataGather").getValue(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()));
//		td.adjust(PurchaseTab.class.getSimpleName(), getTestSpecificTD("PurchaseTab_DataGather"));
//		return td.resolveLinks();
//	}
}
