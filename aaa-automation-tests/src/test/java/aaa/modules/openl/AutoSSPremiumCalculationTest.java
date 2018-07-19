package aaa.modules.openl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLPolicy;
import aaa.helpers.openl.testdata_generator.AutoSSTestDataGenerator;
import aaa.helpers.openl.testdata_generator.TestDataGenerator;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import toolkit.datax.TestData;

public class AutoSSPremiumCalculationTest extends OpenLRatingBaseTest<AutoSSOpenLPolicy> {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}
	
	@Override
	protected TestData getRatingDataPattern() {
		return super.getRatingDataPattern().mask(new DriverTab().getMetaKey(), new VehicleTab().getMetaKey(), new PremiumAndCoveragesTab().getMetaKey());
	}
	
	@Override
	protected String createQuote(AutoSSOpenLPolicy openLPolicy) {
		boolean isLegacyConvPolicy = false;
		AutoSSTestDataGenerator tdGenerator = openLPolicy.getTestDataGenerator(getState(), getRatingDataPattern());
		
		if (TestDataGenerator.LEGACY_CONV_PROGRAM_CODE.equals(openLPolicy.getCappingDetails().getProgramCode())) {
			isLegacyConvPolicy = true;
			TestData renewalEntryData = tdGenerator.getRenewalEntryData(openLPolicy);
			
			if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.CUSTOMER.get())) {
				NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
			}
			customer.initiateRenewalEntry().perform(renewalEntryData);
		} else {
			policy.initiate();
		}
		
		TestData quoteRatingData = tdGenerator.getRatingData(openLPolicy, isLegacyConvPolicy);
		policy.getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesTab.class, false);
		new PremiumAndCoveragesTab().getAssetList().fill(quoteRatingData);
		return Tab.labelPolicyNumber.getValue();
	}

	@Override
	protected Dollar calculatePremium(AutoSSOpenLPolicy openLPolicy) {
		new PremiumAndCoveragesTab().calculatePremium();
		Dollar totalPremium = PremiumAndCoveragesTab.getTotalTermPremium();
		if (PremiumAndCoveragesTab.tableStateAndLocalTaxesSummary.isPresent()) { // WV and KY states have AP/RP taxes
			totalPremium = totalPremium.subtract(PremiumAndCoveragesTab.getStateAndLocalTaxesAndPremiumSurchargesPremium());
		}
		return totalPremium;
	}

	@Override
	protected Map<String, String> getOpenLFieldsMapFromTest(AutoSSOpenLPolicy openLPolicy) {
		Map<String, String> openLFieldsMap = super.getOpenLFieldsMapFromTest(openLPolicy);

		Pattern driverIdOrNamePattern = Pattern.compile("^policy\\.drivers\\[\\d+\\]\\.(?:name|id)$");
		Pattern vehicleIdPattern = Pattern.compile("^policy\\.vehicles\\[\\d+\\]\\.id");
		Pattern vehicleAnnualMileagePattern = Pattern.compile("^policy\\.vehicles\\[\\d+\\].annualMileage$");
		Pattern vehicleRatedDriverIdOrNamePattern = Pattern.compile("^policy\\.vehicles\\[\\d+\\]\\.ratedDriver\\.(?:name|id)$");
		Pattern coverageAdditionalLimitAmountPattern = Pattern.compile("^policy\\.vehicles\\[\\d+\\]\\.coverages\\[\\d+\\]\\.additionalLimitAmount$");
		LocalDate plcyInception = openLPolicy.getCappingDetails().getPlcyInceptionDate();
		if (plcyInception != null) {
			openLFieldsMap.put("policy.cappingDetails.plcyInception", String.valueOf(plcyInception.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
		}

		openLFieldsMap.entrySet().removeIf(e -> "policy.policyNumber".equals(e.getKey())
				|| driverIdOrNamePattern.matcher(e.getKey()).matches()
				|| vehicleIdPattern.matcher(e.getKey()).matches()
				|| vehicleAnnualMileagePattern.matcher(e.getKey()).matches()
				|| vehicleRatedDriverIdOrNamePattern.matcher(e.getKey()).matches()
				|| coverageAdditionalLimitAmountPattern.matcher(e.getKey()).matches());

		List<String> coverageCDsList = openLFieldsMap.entrySet().stream().filter(e -> e.getKey().endsWith("coverageCd")).map(Map.Entry::getKey).collect(Collectors.toList());
		coverageCDsList.forEach(cd -> openLFieldsMap.put(cd.replace("coverageCd", "coverageCD"), openLFieldsMap.remove(cd)));

		return openLFieldsMap;
	}
}
