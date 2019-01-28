package aaa.helpers.openl.testdata_generator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.toolkit.webdriver.customcontrols.AdvancedComboBox;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

public abstract class TestDataGenerator<P extends OpenLPolicy> {
	public static final String LEGACY_CONV_PROGRAM_CODE = "LegacyConv";
	protected static final Logger log = LoggerFactory.getLogger(TestDataGenerator.class);

	private String state;
	private TestData ratingDataPattern;

	public TestDataGenerator(String state) {
		this(state, DataProviderFactory.emptyData());
	}

	public TestDataGenerator(String state, TestData ratingDataPattern) {
		this.state = state;
		this.ratingDataPattern = ratingDataPattern;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	TestData getRatingDataPattern() {
		return ratingDataPattern;
	}

	public void setRatingDataPattern(TestData ratingDataPattern) {
		this.ratingDataPattern = ratingDataPattern;
	}

	public abstract TestData getRatingData(P openLPolicy);

	public TestData getRenewalEntryData(P openLPolicy) {
		List<String> maigSourceSystemStates = Arrays.asList(Constants.States.MD, Constants.States.PA, Constants.States.DE, Constants.States.NJ, Constants.States.VA);
		String underwritingCompany;

		if (StringUtils.isBlank(openLPolicy.getUnderwriterCode())) {
			underwritingCompany = AdvancedComboBox.RANDOM_MARK;
		} else {
			switch (openLPolicy.getUnderwriterCode()) {
				case "WUIC":
					underwritingCompany = "CSAA General Insurance Company";
					break;
				case "ACAIC":
					underwritingCompany = "CSAA Fire & Casualty Insurance Company";
					break;
				case "CSAAIB":
					underwritingCompany = "CSAA Interinsurance Bureau";
					break;
				case "KICO":
					underwritingCompany = "CSAA Affinity Insurance Company";
					break;
				default:
					throw new IstfException("Unknown maping for underwriterCode=" + openLPolicy.getUnderwriterCode());
			}
		}

		TestData initiateRenewalEntryActionData = DataProviderFactory.dataOf(
				CustomerMetaData.InitiateRenewalEntryActionTab.PREVIOUS_POLICY_NUMBER.getLabel(), "$<rx:\\d{10}>",
				CustomerMetaData.InitiateRenewalEntryActionTab.PREVIOUS_SOURCE_SYSTEM.getLabel(), maigSourceSystemStates.contains(getState()) ? "MAIG" : "SIS",
				CustomerMetaData.InitiateRenewalEntryActionTab.RISK_STATE.getLabel(), getState(),
				CustomerMetaData.InitiateRenewalEntryActionTab.RENEWAL_EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY),
				CustomerMetaData.InitiateRenewalEntryActionTab.RENEWAL_POLICY_PREMIUM.getLabel(), openLPolicy.getPreviousPolicyPremium() != null ? openLPolicy.getPreviousPolicyPremium() : 1000,
				CustomerMetaData.InitiateRenewalEntryActionTab.POLICY_TERM.getLabel(), getPremiumAndCoveragesPaymentPlan(openLPolicy.getTerm()),
				CustomerMetaData.InitiateRenewalEntryActionTab.PROGRAM_CODE.getLabel(), LEGACY_CONV_PROGRAM_CODE,
				CustomerMetaData.InitiateRenewalEntryActionTab.ENROLLED_IN_AUTOPAY.getLabel(), "No",
				CustomerMetaData.InitiateRenewalEntryActionTab.UNDERWRITING_COMPANY.getLabel(), underwritingCompany);

		return DataProviderFactory.dataOf(new InitiateRenewalEntryActionTab().getMetaKey(), initiateRenewalEntryActionData);
	}

	protected String getPremiumAndCoveragesPaymentPlan(int term) {
		StringBuilder paymentPlan = new StringBuilder("regex=^");
		switch (term) {
			case 12:
				paymentPlan.append("Annual");
				break;
			case 6:
				paymentPlan.append("Semi-[aA]nnual");
				break;
			default:
				throw new IstfException("Unable to build test data. Unsupported openL policy term: " + term);
		}
		paymentPlan.append("(\\s*\\(Renewal\\))?$");
		return paymentPlan.toString();
	}

	String getYesOrNo(Boolean value) {
		if (value == null) {
			return null;
		}
		return Boolean.TRUE.equals(value) ? "Yes" : "No";
	}

	String getYesOrNo(String value) {
		if (value == null) {
			return null;
		}
		return "Y".equalsIgnoreCase(value) ? "Yes" : "No";
	}

	String getDollarValue(int value) {
		return getDollarValue(value, true);
	}

	String getDollarValue(int value, boolean excludeZeroHundredths) {
		String dollarValue = new Dollar(value).toString();
		if (excludeZeroHundredths) {
			dollarValue = dollarValue.replaceAll("\\.00", "");
		}
		return dollarValue;
	}

	String getRangedDollarValue(int fromBoundary, int toBoundary) {
		return getRangedDollarValue(fromBoundary, toBoundary, true);
	}

	String getRangedDollarValue(int fromBoundary, int toBoundary, boolean excludeZeroHundredths) {
		return getDollarValue(fromBoundary, excludeZeroHundredths) + "/" + getDollarValue(toBoundary, excludeZeroHundredths);
	}

	String getRandom(String... values) {
		return getRandom(Arrays.asList(values));
	}

	String getRandom(List<String> values) {
		return values.get(new Random().nextInt(values.size()));
	}
}
