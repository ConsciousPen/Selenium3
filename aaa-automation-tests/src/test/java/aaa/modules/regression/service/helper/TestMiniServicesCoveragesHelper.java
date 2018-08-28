package aaa.modules.regression.service.helper;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.dtoDxp.*;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.verification.ETCSCoreSoftAssertions;

public class TestMiniServicesCoveragesHelper extends PolicyBaseTest {

	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private HelperMiniServices helperMiniServices = new HelperMiniServices();
	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();

	protected void pas11741_ViewManageVehicleLevelCoverages(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Perform Endorsement
		PolicySummary endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		Dollar comprehensiveDeductible = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)");
		Dollar collisionDeductible = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)");
		String fullSafetyGlassVeh1 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "");
		String loanLeaseCov = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), " (+$0.00)");
		Dollar transportationExpense = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)");
		String towingAndLabor = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");
		Dollar excessElectronicEquipment = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PolicyCoverageInfo coverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {

			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageCd).isEqualTo("COMPDED");
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageDescription).isEqualTo("Other Than Collision");
			assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimit)).isEqualTo(comprehensiveDeductible);
			assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimitDisplay)).isEqualTo(comprehensiveDeductible);
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageType).isEqualTo("Deductible");
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).customerDisplayed).isEqualTo(true);
			assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageCd).isEqualTo("COLLDED");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageDescription).isEqualTo("Collision Deductible");
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimit)).isEqualTo(collisionDeductible);
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageLimitDisplay)).isEqualTo(collisionDeductible);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageCd).isEqualTo("GLASS");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageDescription).isEqualTo("Full Safety Glass");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimit).isEqualTo("false");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageLimitDisplay).isEqualTo(fullSafetyGlassVeh1);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageType).isEqualTo("None");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitFullGlassCov(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageCd).isEqualTo("LOAN");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageDescription).isEqualTo("Auto Loan/Lease Coverage");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimit).isEqualTo("0");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageLimitDisplay).isEqualTo(loanLeaseCov);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageType).isEqualTo("None");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitLoan(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageCd).isEqualTo("RREIM");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageDescription).isEqualTo("Transportation Expense");
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimit)).isEqualTo(transportationExpense);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageLimitDisplay).contains(transportationExpense.toString().replace(".00", ""));
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageType).isEqualTo("Per Occurrence");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTransportationExpense(coverageResponse, false);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageCd).isEqualTo("TOWINGLABOR");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageDescription).isEqualTo("Towing and Labor Coverage");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageLimitDisplay).isEqualTo(towingAndLabor);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageType).isEqualTo("Per Disablement/Maximum");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTowingLabor(coverageResponse);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).coverageCd).isEqualTo("SPECEQUIP");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).coverageDescription).isEqualTo("Excess Electronic Equipment");
			softly.assertThat(new Dollar(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).coverageLimit)).isEqualTo(excessElectronicEquipment);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).coverageLimitDisplay).isEqualTo("$1,000.00");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).canChangeCoverage).isEqualTo(false);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).coverageCd).isEqualTo("NEWCAR");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).coverageDescription).isEqualTo("New Car Added Protection");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).coverageLimit).isEqualTo("false");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).coverageLimitDisplay).isEqualTo("No");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).customerDisplayed).isEqualTo(false);
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).canChangeCoverage).isEqualTo(false);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(8).coverageCd).isEqualTo("WL");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(8).coverageDescription).isEqualTo("Waive Liability");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(8).customerDisplayed).isEqualTo(false);

		});

		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "1,000");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "750");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "Yes");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), "Yes");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), "900");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), "50");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "1,500");

		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		SearchPage.openPolicy(policyNumber);

		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		Dollar comprehensiveDeductible1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)");
		Dollar collisionDeductible1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)");
		String fullSafetyGlassVeh2 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "");
		String loanLeaseCov1 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), " (+$0.00)");
		Dollar transportationExpense1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)");
		String towingAndLabor1 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");
		Dollar excessElectronicEquipment1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages;
			softly.assertThat(coveragesV1.get(0).coverageCd).isEqualTo("COMPDED");
			softly.assertThat(coveragesV1.get(0).coverageDescription).isEqualTo("Other Than Collision");
			softly.assertThat(new Dollar(coveragesV1.get(0).coverageLimit)).isEqualTo(comprehensiveDeductible1);
			softly.assertThat(new Dollar(coveragesV1.get(0).coverageLimitDisplay)).isEqualTo(comprehensiveDeductible1);
			softly.assertThat(coveragesV1.get(0).coverageType).isEqualTo("Deductible");
			softly.assertThat(coveragesV1.get(0).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesV1.get(0).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageEndorsementResponse);

			softly.assertThat(coveragesV1.get(1).coverageCd).isEqualTo("COLLDED");
			softly.assertThat(coveragesV1.get(1).coverageDescription).isEqualTo("Collision Deductible");
			softly.assertThat(new Dollar(coveragesV1.get(1).coverageLimit)).isEqualTo(collisionDeductible1);
			softly.assertThat(new Dollar(coveragesV1.get(1).coverageLimitDisplay)).isEqualTo(collisionDeductible1);
			softly.assertThat(coveragesV1.get(1).coverageType).isEqualTo("Deductible");
			softly.assertThat(coveragesV1.get(1).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesV1.get(1).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageEndorsementResponse);

			softly.assertThat(coveragesV1.get(2).coverageCd).isEqualTo("GLASS");
			softly.assertThat(coveragesV1.get(2).coverageDescription).isEqualTo("Full Safety Glass");
			softly.assertThat(coveragesV1.get(2).coverageLimit).isEqualTo("true");
			softly.assertThat(coveragesV1.get(2).coverageLimitDisplay).isEqualTo(fullSafetyGlassVeh2);
			softly.assertThat(coveragesV1.get(2).coverageType).isEqualTo("None");
			softly.assertThat(coveragesV1.get(2).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesV1.get(2).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitFullGlassCov(coverageEndorsementResponse);

			softly.assertThat(coveragesV1.get(3).coverageCd).isEqualTo("LOAN");
			softly.assertThat(coveragesV1.get(3).coverageDescription).isEqualTo("Auto Loan/Lease Coverage");
			softly.assertThat(coveragesV1.get(3).coverageLimit).isEqualTo("1");
			softly.assertThat(coveragesV1.get(3).coverageLimitDisplay).isEqualTo(loanLeaseCov1);
			softly.assertThat(coveragesV1.get(3).coverageType).isEqualTo("None");
			softly.assertThat(coveragesV1.get(3).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesV1.get(3).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitLoan(coverageEndorsementResponse);

			softly.assertThat(coveragesV1.get(4).coverageCd).isEqualTo("RREIM");
			softly.assertThat(coveragesV1.get(4).coverageDescription).isEqualTo("Transportation Expense");
			softly.assertThat(new Dollar(coveragesV1.get(4).coverageLimit)).isEqualTo(transportationExpense1);
			softly.assertThat(new Dollar(coveragesV1.get(4).coverageLimitDisplay)).isEqualTo(transportationExpense1);
			softly.assertThat(coveragesV1.get(4).coverageType).isEqualTo("Per Occurrence");
			softly.assertThat(coveragesV1.get(4).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesV1.get(4).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTransportationExpense(coverageEndorsementResponse, true);

			softly.assertThat(coveragesV1.get(5).coverageCd).isEqualTo("TOWINGLABOR");
			softly.assertThat(coveragesV1.get(5).coverageDescription).isEqualTo("Towing and Labor Coverage");
			softly.assertThat(coveragesV1.get(5).coverageLimit).isEqualTo("50/300");
			softly.assertThat(coveragesV1.get(5).coverageLimitDisplay.replace("$", "")).isEqualTo(towingAndLabor1);
			softly.assertThat(coveragesV1.get(5).coverageType).isEqualTo("Per Disablement/Maximum");
			softly.assertThat(coveragesV1.get(5).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesV1.get(5).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTowingLabor(coverageEndorsementResponse);

			softly.assertThat(coveragesV1.get(6).coverageCd).isEqualTo("SPECEQUIP");
			softly.assertThat(coveragesV1.get(6).coverageDescription).isEqualTo("Excess Electronic Equipment");
			softly.assertThat(new Dollar(coveragesV1.get(6).coverageLimit)).isEqualTo(excessElectronicEquipment1);
			softly.assertThat(coveragesV1.get(6).coverageLimitDisplay).isEqualTo("$1,500.00");
			softly.assertThat(coveragesV1.get(6).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesV1.get(6).canChangeCoverage).isEqualTo(false);

			softly.assertThat(coveragesV1.get(7).coverageCd).isEqualTo("NEWCAR");
			softly.assertThat(coveragesV1.get(7).coverageDescription).isEqualTo("New Car Added Protection");
			softly.assertThat(coveragesV1.get(7).coverageLimit).isEqualTo("false");
			softly.assertThat(coveragesV1.get(7).coverageLimitDisplay).isEqualTo("No");
			softly.assertThat(coveragesV1.get(7).customerDisplayed).isEqualTo(false);
			softly.assertThat(coveragesV1.get(7).canChangeCoverage).isEqualTo(false);
		});
	}

	protected void pas13353_LoanLeaseCoverage(PolicyType policyType) {
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();

		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		PolicySummary endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		String oid = viewVehicleResponse.vehicleList.get(0).oid;

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		Dollar comprehensiveDeductible = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)");
		Dollar collisionDeductible = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)");
		String loanLeaseCov1 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), " (+$0.00)");
		String fullSafetyGlassVeh1 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "");
		Dollar transportationExpense = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)");
		String towingAndLabor = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");
		Dollar excessElectronicEquipment = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PremiumAndCoveragesTab.buttonSaveAndExit.click();

		PolicyCoverageInfo coverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesLoanVehicle = coverageResponse.vehicleLevelCoverages.get(0).coverages;
			assertThat(coveragesLoanVehicle.get(0).coverageCd).isEqualTo("COMPDED");
			assertThat(coveragesLoanVehicle.get(0).coverageDescription).isEqualTo("Other Than Collision");
			assertThat(new Dollar(coveragesLoanVehicle.get(0).coverageLimit)).isEqualTo(comprehensiveDeductible);
			assertThat(new Dollar(coveragesLoanVehicle.get(0).coverageLimitDisplay)).isEqualTo(comprehensiveDeductible);
			assertThat(coveragesLoanVehicle.get(0).coverageType).isEqualTo("Deductible");
			assertThat(coveragesLoanVehicle.get(0).customerDisplayed).isEqualTo(true);
			assertThat(coveragesLoanVehicle.get(0).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			softly.assertThat(coveragesLoanVehicle.get(1).coverageCd).isEqualTo("COLLDED");
			softly.assertThat(coveragesLoanVehicle.get(1).coverageDescription).isEqualTo("Collision Deductible");
			softly.assertThat(new Dollar(coveragesLoanVehicle.get(1).coverageLimit)).isEqualTo(collisionDeductible);
			softly.assertThat(new Dollar(coveragesLoanVehicle.get(1).coverageLimitDisplay)).isEqualTo(collisionDeductible);
			softly.assertThat(coveragesLoanVehicle.get(1).coverageType).isEqualTo("Deductible");
			softly.assertThat(coveragesLoanVehicle.get(1).customerDisplayed).isEqualTo(true);
			assertThat(coveragesLoanVehicle.get(1).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			softly.assertThat(coveragesLoanVehicle.get(2).coverageCd).isEqualTo("GLASS");
			softly.assertThat(coveragesLoanVehicle.get(2).coverageDescription).isEqualTo("Full Safety Glass");
			softly.assertThat(coveragesLoanVehicle.get(2).coverageLimit).isEqualTo("false");
			softly.assertThat(coveragesLoanVehicle.get(2).coverageLimitDisplay).isEqualTo(fullSafetyGlassVeh1);
			softly.assertThat(coveragesLoanVehicle.get(2).coverageType).isEqualTo("None");
			softly.assertThat(coveragesLoanVehicle.get(2).customerDisplayed).isEqualTo(true);
			assertThat(coveragesLoanVehicle.get(2).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitFullGlassCov(coverageResponse);

			softly.assertThat(coveragesLoanVehicle.get(3).coverageCd).isEqualTo("LOAN");
			softly.assertThat(coveragesLoanVehicle.get(3).coverageDescription).isEqualTo("Auto Loan/Lease Coverage");
			softly.assertThat(coveragesLoanVehicle.get(3).coverageLimit).isEqualTo("0");
			softly.assertThat(coveragesLoanVehicle.get(3).coverageLimitDisplay).isEqualTo(loanLeaseCov1);
			softly.assertThat(coveragesLoanVehicle.get(3).coverageType).isEqualTo("None");
			softly.assertThat(coveragesLoanVehicle.get(3).customerDisplayed).isEqualTo(true);
			assertThat(coveragesLoanVehicle.get(3).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitLoan(coverageResponse);

			softly.assertThat(coveragesLoanVehicle.get(4).coverageCd).isEqualTo("RREIM");
			softly.assertThat(coveragesLoanVehicle.get(4).coverageDescription).isEqualTo("Transportation Expense");
			softly.assertThat(new Dollar(coveragesLoanVehicle.get(4).coverageLimit)).isEqualTo(transportationExpense);
			softly.assertThat(coveragesLoanVehicle.get(4).coverageLimitDisplay).contains(transportationExpense.toString().replace(".00", ""));
			softly.assertThat(coveragesLoanVehicle.get(4).coverageType).isEqualTo("Per Occurrence");
			softly.assertThat(coveragesLoanVehicle.get(4).customerDisplayed).isEqualTo(true);
			assertThat(coveragesLoanVehicle.get(4).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTransportationExpense(coverageResponse, false);

			softly.assertThat(coveragesLoanVehicle.get(5).coverageCd).isEqualTo("TOWINGLABOR");
			softly.assertThat(coveragesLoanVehicle.get(5).coverageDescription).isEqualTo("Towing and Labor Coverage");
			softly.assertThat(coveragesLoanVehicle.get(5).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coveragesLoanVehicle.get(5).coverageLimitDisplay).isEqualTo(towingAndLabor);
			softly.assertThat(coveragesLoanVehicle.get(5).coverageType).isEqualTo("Per Disablement/Maximum");
			softly.assertThat(coveragesLoanVehicle.get(5).customerDisplayed).isEqualTo(true);
			assertThat(coveragesLoanVehicle.get(5).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTowingLabor(coverageResponse);

			softly.assertThat(coveragesLoanVehicle.get(6).coverageCd).isEqualTo("SPECEQUIP");
			softly.assertThat(coveragesLoanVehicle.get(6).coverageDescription).isEqualTo("Excess Electronic Equipment");
			softly.assertThat(new Dollar(coveragesLoanVehicle.get(6).coverageLimit)).isEqualTo(excessElectronicEquipment);
			softly.assertThat(coveragesLoanVehicle.get(6).coverageLimitDisplay).isEqualTo("$1,000.00");
			softly.assertThat(coveragesLoanVehicle.get(6).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesLoanVehicle.get(6).canChangeCoverage).isEqualTo(false);

			softly.assertThat(coveragesLoanVehicle.get(7).coverageCd).isEqualTo("NEWCAR");
			softly.assertThat(coveragesLoanVehicle.get(7).coverageDescription).isEqualTo("New Car Added Protection");
			softly.assertThat(coveragesLoanVehicle.get(7).coverageLimit).isEqualTo("false");
			softly.assertThat(coveragesLoanVehicle.get(7).coverageLimitDisplay).isEqualTo("No");
			softly.assertThat(coveragesLoanVehicle.get(7).customerDisplayed).isEqualTo(false);
			softly.assertThat(coveragesLoanVehicle.get(7).canChangeCoverage).isEqualTo(false);

			softly.assertThat(coveragesLoanVehicle.get(8).coverageCd).isEqualTo("WL");
			softly.assertThat(coveragesLoanVehicle.get(8).coverageDescription).isEqualTo("Waive Liability");
			softly.assertThat(coveragesLoanVehicle.get(8).customerDisplayed).isEqualTo(false);

		});

		VehicleUpdateDto updateVehicleUsageRequest = new VehicleUpdateDto();
		updateVehicleUsageRequest.vehicleOwnership = new VehicleOwnership();
		updateVehicleUsageRequest.usage = "Pleasure";
		updateVehicleUsageRequest.vehicleOwnership.ownership = "OWN";
		updateVehicleUsageRequest.registeredOwner = true;
		Vehicle updateVehicleUsageResponse = HelperCommon.updateVehicle(policyNumber, oid, updateVehicleUsageRequest);
		assertThat(updateVehicleUsageResponse.usage).isEqualTo("Pleasure");

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		Dollar comprehensiveDeductible1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)");
		Dollar collisionDeductible1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)");
		String fullSafetyGlassVeh2 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "");
		Dollar transportationExpense1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)");
		String towingAndLabor1 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");
		Dollar excessElectronicEquipment1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.NEW_CAR_ADDED_PROTECTION.getLabel(), "Yes");
		premiumAndCoveragesTab.saveAndExit();

		PolicyCoverageInfo coverageResponse1 = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse1.vehicleLevelCoverages.get(0).coverages;
			assertThat(coveragesV1.get(0).coverageCd).isEqualTo("COMPDED");
			assertThat(coveragesV1.get(0).coverageDescription).isEqualTo("Other Than Collision");
			assertThat(new Dollar(coveragesV1.get(0).coverageLimit)).isEqualTo(comprehensiveDeductible1);
			assertThat(new Dollar(coveragesV1.get(0).coverageLimitDisplay)).isEqualTo(comprehensiveDeductible1);
			assertThat(coveragesV1.get(0).coverageType).isEqualTo("Deductible");
			assertThat(coveragesV1.get(0).customerDisplayed).isEqualTo(true);
			assertThat(coveragesV1.get(0).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompColl(coverageResponse1);

			softly.assertThat(coveragesV1.get(1).coverageCd).isEqualTo("COLLDED");
			softly.assertThat(coveragesV1.get(1).coverageDescription).isEqualTo("Collision Deductible");
			softly.assertThat(new Dollar(coveragesV1.get(1).coverageLimit)).isEqualTo(collisionDeductible1);
			softly.assertThat(new Dollar(coveragesV1.get(1).coverageLimitDisplay)).isEqualTo(collisionDeductible1);
			softly.assertThat(coveragesV1.get(1).coverageType).isEqualTo("Deductible");
			softly.assertThat(coveragesV1.get(1).customerDisplayed).isEqualTo(true);
			assertThat(coveragesV1.get(1).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompColl(coverageResponse1);

			softly.assertThat(coveragesV1.get(2).coverageCd).isEqualTo("GLASS");
			softly.assertThat(coveragesV1.get(2).coverageDescription).isEqualTo("Full Safety Glass");
			softly.assertThat(coveragesV1.get(2).coverageLimit).isEqualTo("false");
			softly.assertThat(coveragesV1.get(2).coverageLimitDisplay).isEqualTo(fullSafetyGlassVeh2);
			softly.assertThat(coveragesV1.get(2).coverageType).isEqualTo("None");
			softly.assertThat(coveragesV1.get(2).customerDisplayed).isEqualTo(true);
			assertThat(coveragesV1.get(2).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitFullGlassCov(coverageResponse1);

			softly.assertThat(coveragesV1.get(3).coverageCd).isEqualTo("LOAN");
			softly.assertThat(coveragesV1.get(3).coverageDescription).isEqualTo("Auto Loan/Lease Coverage");
			softly.assertThat(coveragesV1.get(3).coverageLimit).isEqualTo("0");
			softly.assertThat(coveragesV1.get(3).coverageLimitDisplay).isEqualTo("No Coverage");
			softly.assertThat(coveragesV1.get(3).coverageType).isEqualTo("None");
			softly.assertThat(coveragesV1.get(3).customerDisplayed).isEqualTo(false);
			softly.assertThat(coveragesV1.get(3).canChangeCoverage).isEqualTo(false);

			assertCoverageLimitLoan(coverageResponse1);

			softly.assertThat(coveragesV1.get(4).coverageCd).isEqualTo("RREIM");
			softly.assertThat(coveragesV1.get(4).coverageDescription).isEqualTo("Transportation Expense");
			softly.assertThat(new Dollar(coveragesV1.get(4).coverageLimit)).isEqualTo(transportationExpense1);
			softly.assertThat(coveragesV1.get(4).coverageLimitDisplay).contains(transportationExpense1.toString().replace(".00", ""));
			softly.assertThat(coveragesV1.get(4).coverageType).isEqualTo("Per Occurrence");
			softly.assertThat(coveragesV1.get(4).customerDisplayed).isEqualTo(true);
			assertThat(coveragesV1.get(4).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTransportationExpense(coverageResponse1, false);

			softly.assertThat(coveragesV1.get(5).coverageCd).isEqualTo("TOWINGLABOR");
			softly.assertThat(coveragesV1.get(5).coverageDescription).isEqualTo("Towing and Labor Coverage");
			softly.assertThat(coveragesV1.get(5).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coveragesV1.get(5).coverageLimitDisplay).isEqualTo(towingAndLabor1);
			softly.assertThat(coveragesV1.get(5).coverageType).isEqualTo("Per Disablement/Maximum");
			softly.assertThat(coveragesV1.get(5).customerDisplayed).isEqualTo(true);
			assertThat(coveragesV1.get(5).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTowingLabor(coverageResponse1);

			softly.assertThat(coveragesV1.get(6).coverageCd).isEqualTo("SPECEQUIP");
			softly.assertThat(coveragesV1.get(6).coverageDescription).isEqualTo("Excess Electronic Equipment");
			softly.assertThat(new Dollar(coveragesV1.get(6).coverageLimit)).isEqualTo(excessElectronicEquipment1);
			softly.assertThat(coveragesV1.get(6).coverageLimitDisplay).isEqualTo("$1,000.00");
			softly.assertThat(coveragesV1.get(6).customerDisplayed).isEqualTo(true);
			assertThat(coveragesV1.get(6).canChangeCoverage).isEqualTo(false);

			softly.assertThat(coveragesV1.get(7).coverageCd).isEqualTo("NEWCAR");
			softly.assertThat(coveragesV1.get(7).coverageDescription).isEqualTo("New Car Added Protection");
			softly.assertThat(coveragesV1.get(7).coverageLimit).isEqualTo("true");
			softly.assertThat(coveragesV1.get(7).coverageLimitDisplay).isEqualTo("Yes");
			softly.assertThat(coveragesV1.get(7).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesV1.get(7).canChangeCoverage).isEqualTo(false);

			softly.assertThat(coveragesV1.get(8).coverageCd).isEqualTo("WL");
			softly.assertThat(coveragesV1.get(8).coverageDescription).isEqualTo("Waive Liability");
			softly.assertThat(coveragesV1.get(8).customerDisplayed).isEqualTo(false);

		});
	}

	protected void pas11741_ViewManageVehicleLevelCoveragesForAZ(PolicyType policyType) {
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();

		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		Dollar comprehensiveDeductible = new Dollar(premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel()).replace("(+$0.00)", "").trim());
		Dollar collisionDeductible = new Dollar(premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel()).replace("(+$0.00)", "").trim());
		String fullSafetyGlassVeh1 = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel());
		String loanLeaseCov = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.VEHICLE_LOAN_LEASE_PROTECTION.getLabel()).replace("(+$0.00)", "").trim();
		String transportationExpense = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.RENTAL_REIMBURSEMENT.getLabel()).replace("(+$0.00)", "").trim();
		String towingAndLabor = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel()).replace("(Included)", "").replace("(+$0.00)", "").replace("$", "").trim();
		Dollar excessElectronicEquipment = new Dollar(premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.SPECIAL_EQUIPMENT_COVERAGE.getLabel()));

		PolicyCoverageInfo coverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			Coverage coverageCompded = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0);
			softly.assertThat(coverageCompded.coverageCd).isEqualTo("COMPDED");
			softly.assertThat(coverageCompded.coverageDescription).isEqualTo("Comprehensive Deductible");
			softly.assertThat(new Dollar(coverageCompded.coverageLimit)).isEqualTo(comprehensiveDeductible);
			softly.assertThat(new Dollar(coverageCompded.coverageLimitDisplay)).isEqualTo(comprehensiveDeductible);
			softly.assertThat(coverageCompded.coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageCompded.customerDisplayed).isEqualTo(true);

			availableCoverageCompdedForAz(coverageResponse, softly);

			Coverage coverageCollded = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1);
			softly.assertThat(coverageCollded.coverageCd).isEqualTo("COLLDED");
			softly.assertThat(coverageCollded.coverageDescription).isEqualTo("Collision Deductible");
			softly.assertThat(new Dollar(coverageCollded.coverageLimit)).isEqualTo(collisionDeductible);
			softly.assertThat(new Dollar(coverageCollded.coverageLimitDisplay)).isEqualTo(collisionDeductible);
			softly.assertThat(coverageCollded.coverageType).isEqualTo("Deductible");
			softly.assertThat(coverageCollded.customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageCollded.availableLimits.get(0).coverageLimit).isEqualTo("100");
			softly.assertThat(coverageCollded.availableLimits.get(0).coverageLimitDisplay).isEqualTo("$100");

			softly.assertThat(coverageCollded.availableLimits.get(1).coverageLimit).isEqualTo("250");
			softly.assertThat(coverageCollded.availableLimits.get(1).coverageLimitDisplay).isEqualTo("$250");

			softly.assertThat(coverageCollded.availableLimits.get(2).coverageLimit).isEqualTo("500");
			softly.assertThat(coverageCollded.availableLimits.get(2).coverageLimitDisplay).isEqualTo("$500");

			softly.assertThat(coverageCollded.availableLimits.get(3).coverageLimit).isEqualTo("750");
			softly.assertThat(coverageCollded.availableLimits.get(3).coverageLimitDisplay).isEqualTo("$750");

			softly.assertThat(coverageCollded.availableLimits.get(4).coverageLimit).isEqualTo("1000");
			softly.assertThat(coverageCollded.availableLimits.get(4).coverageLimitDisplay).isEqualTo("$1,000");

			Coverage coverageGlass = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2);
			softly.assertThat(coverageGlass.coverageCd).isEqualTo("GLASS");
			softly.assertThat(coverageGlass.coverageDescription).isEqualTo("Full Safety Glass");
			softly.assertThat(coverageGlass.coverageLimit).isEqualTo("false");
			softly.assertThat(coverageGlass.coverageLimitDisplay).isEqualTo(fullSafetyGlassVeh1);
			softly.assertThat(coverageGlass.coverageType).isEqualTo("None");
			softly.assertThat(coverageGlass.customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageGlass.availableLimits.get(0).coverageLimit).isEqualTo("false");
			softly.assertThat(coverageGlass.availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageGlass.availableLimits.get(1).coverageLimit).isEqualTo("true");
			softly.assertThat(coverageGlass.availableLimits.get(1).coverageLimitDisplay).isEqualTo("Yes");

			Coverage coverageLoan = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3);
			softly.assertThat(coverageLoan.coverageCd).isEqualTo("LOAN");
			softly.assertThat(coverageLoan.coverageDescription).isEqualTo("Vehicle Loan/Lease Protection");
			softly.assertThat(coverageLoan.coverageLimit).isEqualTo("0");
			softly.assertThat(coverageLoan.coverageLimitDisplay).isEqualTo(loanLeaseCov);
			softly.assertThat(coverageLoan.coverageType).isEqualTo("None");
			softly.assertThat(coverageLoan.customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageLoan.availableLimits.get(0).coverageLimit).isEqualTo("0");
			softly.assertThat(coverageLoan.availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageLoan.availableLimits.get(1).coverageLimit).isEqualTo("1");
			softly.assertThat(coverageLoan.availableLimits.get(1).coverageLimitDisplay).isEqualTo("Yes");

			Coverage coverageRreim = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4);
			softly.assertThat(coverageRreim.coverageCd).isEqualTo("RREIM");
			softly.assertThat(coverageRreim.coverageDescription).isEqualTo("Rental Reimbursement");
			softly.assertThat(coverageRreim.coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageRreim.coverageLimitDisplay).isEqualTo(transportationExpense.toString().replace(".00", "").replace("(+$0)", ""));
			softly.assertThat(coverageRreim.coverageType).isEqualTo("Per Day/Maximum");
			softly.assertThat(coverageRreim.customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageRreim.availableLimits.get(0).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageRreim.availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageRreim.availableLimits.get(1).coverageLimit).isEqualTo("30/900");
			softly.assertThat(coverageRreim.availableLimits.get(1).coverageLimitDisplay).isEqualTo("$30/$900");

			softly.assertThat(coverageRreim.availableLimits.get(2).coverageLimit).isEqualTo("40/1200");
			softly.assertThat(coverageRreim.availableLimits.get(2).coverageLimitDisplay).isEqualTo("$40/$1,200");

			softly.assertThat(coverageRreim.availableLimits.get(3).coverageLimit).isEqualTo("50/1500");
			softly.assertThat(coverageRreim.availableLimits.get(3).coverageLimitDisplay).isEqualTo("$50/$1,500");

			Coverage coverageTowing = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5);
			softly.assertThat(coverageTowing.coverageCd).isEqualTo("TOWINGLABOR");
			softly.assertThat(coverageTowing.coverageDescription).isEqualTo("Towing and Labor Coverage");
			softly.assertThat(coverageTowing.coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageTowing.coverageLimitDisplay).isEqualTo(towingAndLabor);
			softly.assertThat(coverageTowing.coverageType).isEqualTo("Per Disablement/Maximum");
			softly.assertThat(coverageTowing.customerDisplayed).isEqualTo(true);

			softly.assertThat(coverageTowing.availableLimits.get(0).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageTowing.availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageTowing.availableLimits.get(1).coverageLimit).isEqualTo("50/300");
			softly.assertThat(coverageTowing.availableLimits.get(1).coverageLimitDisplay).isEqualTo("$50/$300");

			Coverage coverageSpeq = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6);
			softly.assertThat(coverageSpeq.coverageCd).isEqualTo("SPECEQUIP");
			softly.assertThat(coverageSpeq.coverageDescription).isEqualTo("Special Equipment Coverage");
			softly.assertThat(new Dollar(coverageSpeq.coverageLimit)).isEqualTo(excessElectronicEquipment);
			softly.assertThat(coverageSpeq.coverageLimitDisplay).isEqualTo("$1,000.00");
			softly.assertThat(coverageSpeq.customerDisplayed).isEqualTo(true);
			softly.assertThat(coverageSpeq.canChangeCoverage).isEqualTo(false);

			Coverage coverageNewcar = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7);
			softly.assertThat(coverageNewcar.coverageCd).isEqualTo("NEWCAR");
			softly.assertThat(coverageNewcar.coverageDescription).isEqualTo("New Car Added Protection");
			softly.assertThat(coverageNewcar.coverageLimit).isEqualTo("false");
			softly.assertThat(coverageNewcar.coverageLimitDisplay).isEqualTo("No");
			softly.assertThat(coverageNewcar.customerDisplayed).isEqualTo(false);
			softly.assertThat(coverageNewcar.canChangeCoverage).isEqualTo(false);

			Coverage coverageWL = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(8);
			softly.assertThat(coverageWL.coverageCd).isEqualTo("WL");
			softly.assertThat(coverageWL.coverageDescription).isEqualTo("Waive Liability");
			softly.assertThat(coverageWL.customerDisplayed).isEqualTo(false);

		});
	}

	private void availableCoverageCompdedForAz(PolicyCoverageInfo coverageResponse, ETCSCoreSoftAssertions softly) {
		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(0).coverageLimit).isEqualTo("50");
		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(0).coverageLimitDisplay).isEqualTo("$50");

		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(1).coverageLimit).isEqualTo("100");
		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(1).coverageLimitDisplay).isEqualTo("$100");

		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(2).coverageLimit).isEqualTo("250");
		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(2).coverageLimitDisplay).isEqualTo("$250");

		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(3).coverageLimit).isEqualTo("500");
		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(3).coverageLimitDisplay).isEqualTo("$500");

		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(4).coverageLimit).isEqualTo("750");
		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(4).coverageLimitDisplay).isEqualTo("$750");

		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(5).coverageLimit).isEqualTo("1000");
		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(5).coverageLimitDisplay).isEqualTo("$1,000");
	}

	protected void pas14316_LoanLeasedCovForFinancedNewVehicleBody(ETCSCoreSoftAssertions softly, String ownershipType) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//VIN Will need to be updated yearly to make the test functional
		String purchaseDate = "2013-02-22";
		//String vin = "JF1GJAA64EH012557"; //2014 Subaru Impreza
		String vin = "JF1GPAT66FH237608"; //2015 Subaru Impreza

		//Add vehicle with specific info
		Vehicle vehicleAddRequest = new Vehicle();
		vehicleAddRequest.purchaseDate = purchaseDate;
		vehicleAddRequest.vehIdentificationNo = vin;
		String newVehicleOid = helperMiniServices.vehicleAddRequestWithCheck(policyNumber, vehicleAddRequest);

		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		//Add coverage check here
		String coverageCdValue = "LOAN";
		PolicyCoverageInfo endorsementCoverageResponseOwnedOldVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage endorsementCoverageResponseOwnedOldVehFiltered = endorsementCoverageResponseOwnedOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, endorsementCoverageResponseOwnedOldVehFiltered, "0", "No Coverage", false, false);

		String zipCode = "23703";
		String addressLine1 = "4112 FORREST HILLS DR";
		String addressLine2 = "Apt. 202";
		String city = "PORTSMOUTH";
		String state = "VA";
		String otherName = "other name";
		String secondName = "Second Name";

		//Update vehicle Leased Financed Info
		VehicleUpdateDto updateVehicleLeasedFinanced = new VehicleUpdateDto();
		updateVehicleLeasedFinanced.vehicleOwnership = new VehicleOwnership();
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine1 = addressLine1;
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine2 = addressLine2;
		updateVehicleLeasedFinanced.vehicleOwnership.city = city;
		updateVehicleLeasedFinanced.vehicleOwnership.stateProvCd = state;
		updateVehicleLeasedFinanced.vehicleOwnership.postalCode = zipCode;
		updateVehicleLeasedFinanced.vehicleOwnership.ownership = ownershipType;
		updateVehicleLeasedFinanced.vehicleOwnership.name = otherName;
		updateVehicleLeasedFinanced.vehicleOwnership.secondName = secondName;
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleLeasedFinanced);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo endorsementCoverageResponseLsdFinOldVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage endorsementCoverageResponseLsdFinOldVehFiltered = endorsementCoverageResponseLsdFinOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, endorsementCoverageResponseLsdFinOldVehFiltered, "0", "No Coverage", true, true);

		PolicyCoverageInfo updateLoanLeaseCoverage = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdValue, "1"), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage updateLoanLeaseCoverageFiltered = updateLoanLeaseCoverage.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, updateLoanLeaseCoverageFiltered, "1", "Yes", true, true);
		//BUG Status is not reset when updating coverages
		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");

		helperMiniServices.endorsementRateAndBind(policyNumber);
		PolicyCoverageInfo policyCoverageResponseLsdFinOldVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage policyCoverageResponseLsdFinOldVehFiltered = policyCoverageResponseLsdFinOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, policyCoverageResponseLsdFinOldVehFiltered, "1", "Yes", true, true);

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		VehicleUpdateDto updateVehicleOwned = new VehicleUpdateDto();
		updateVehicleOwned.vehicleOwnership = new VehicleOwnership();
		updateVehicleOwned.vehicleOwnership.ownership = "OWN";
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleOwned);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo endorsementCoverageResponseOwnedOldVeh2 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage endorsementCoverageResponseOwnedOldVeh2Filtered = endorsementCoverageResponseOwnedOldVeh2.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, endorsementCoverageResponseOwnedOldVeh2Filtered, "0", "No Coverage", false, false);
	}

	private void loanLeaseCovPropertiesCheck(ETCSCoreSoftAssertions softly, Coverage endorsementCoverageResponseOwnedOldVeh2Filtered, String coverageLimit, String coverageDisplayLimit, boolean customerDisplayed, boolean canChangeCoverage) {
		softly.assertThat(endorsementCoverageResponseOwnedOldVeh2Filtered.coverageCd).isEqualTo("LOAN");
		softly.assertThat(endorsementCoverageResponseOwnedOldVeh2Filtered.coverageDescription).isEqualTo("Auto Loan/Lease Coverage");
		softly.assertThat(endorsementCoverageResponseOwnedOldVeh2Filtered.coverageLimit).isEqualTo(coverageLimit);
		softly.assertThat(endorsementCoverageResponseOwnedOldVeh2Filtered.coverageLimitDisplay).isEqualTo(coverageDisplayLimit);
		softly.assertThat(endorsementCoverageResponseOwnedOldVeh2Filtered.coverageType).isEqualTo("None");
		softly.assertThat(endorsementCoverageResponseOwnedOldVeh2Filtered.customerDisplayed).isEqualTo(customerDisplayed);
		softly.assertThat(endorsementCoverageResponseOwnedOldVeh2Filtered.canChangeCoverage).isEqualTo(canChangeCoverage);
	}

	protected void pas10352_ManageVehicleCoverageUpdateCoverage(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		String purchaseDate = "2012-02-21";
		String vin = "SHHFK7H41JU201444";
		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
		assertThat(addVehicle.oid).isNotEmpty();
		String newVehicleOid = addVehicle.oid;

		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		String zipCodeOwnership = "23703";
		String addressLine1Ownership = "4112 FORREST HILLS DR";
		String addressLine2Ownership = "Apt. 202";
		String cityOwnership = "PORTSMOUTH";
		String stateOwnership = "VA";
		String otherNameOwnership = "other name";
		String secondNameOwnership = "Second Name";

		//Update vehicle Leased Financed Info
		VehicleUpdateDto updateVehicleLeasedFinanced = new VehicleUpdateDto();
		updateVehicleLeasedFinanced.vehicleOwnership = new VehicleOwnership();
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine1 = addressLine1Ownership;
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine2 = addressLine2Ownership;
		updateVehicleLeasedFinanced.vehicleOwnership.city = cityOwnership;
		updateVehicleLeasedFinanced.vehicleOwnership.stateProvCd = stateOwnership;
		updateVehicleLeasedFinanced.vehicleOwnership.postalCode = zipCodeOwnership;
		updateVehicleLeasedFinanced.vehicleOwnership.ownership = "LSD";
		updateVehicleLeasedFinanced.vehicleOwnership.name = otherNameOwnership;
		updateVehicleLeasedFinanced.vehicleOwnership.secondName = secondNameOwnership;
		VehicleUpdateResponseDto ownershipUpdateResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleLeasedFinanced);
		assertThat(ownershipUpdateResponse.vehicleOwnership.ownership).isEqualTo("LSD");

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		Dollar comprehensiveDeductible1 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)", "(+$39.00)");
		Dollar collisionDeductible1 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)", "(+$185.00)");
		String fullSafetyGlassVeh1 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "");
		String loanLeaseCov1 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), " (+$0.00)");
		Dollar transportationExpense1 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)", " (+$21.00)");
		String towingAndLabor1 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");
		Dollar excessElectronicEquipment1 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		premiumAndCoveragesTab.saveAndExit();

		String coverageCd = "COMPDED";
		String availableLimits = "100";

		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, availableLimits), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {

					coverageXproperties(softly, 0, coverageResponse, "COMPDED", "Other Than Collision", availableLimits, availableLimits, "Deductible", true, true);

					assertCoverageLimitForCompCollLoanLease(coverageResponse);

					coverageXproperties(softly, 1, coverageResponse, "COLLDED", "Collision Deductible", collisionDeductible1.toPlaingString(), collisionDeductible1.toPlaingString(), "Deductible", true, true);

					assertCoverageLimitForCompCollLoanLease(coverageResponse);

					coverageXproperties(softly, 2, coverageResponse, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh1, "None", true, true);

					assertCoverageLimitFullGlassCov(coverageResponse);

					coverageXproperties(softly, 3, coverageResponse, "LOAN", "Auto Loan/Lease Coverage", "0", loanLeaseCov1, "None", true, true);

					assertCoverageLimitLoan(coverageResponse);

					coverageXproperties(softly, 4, coverageResponse, "RREIM", "Transportation Expense", transportationExpense1.toPlaingString(), transportationExpense1.toPlaingString(), "Per Occurrence", true, true);

					assertCoverageLimitTransportationExpense(coverageResponse, false);

					coverageXproperties(softly, 5, coverageResponse, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor1, "Per Disablement/Maximum", true, true);

					assertCoverageLimitTowingLabor(coverageResponse);
				}
		);

		String coverageCd1 = "COLLDED";
		String availableLimits1 = "100";

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd1, availableLimits1), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse1, "COMPDED", "Other Than Collision", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse1, "COLLDED", "Collision Deductible", availableLimits1, availableLimits1, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse1, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh1, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse1, "LOAN", "Auto Loan/Lease Coverage", "0", loanLeaseCov1, "None", true, true);

			coverageXproperties(softly, 4, coverageResponse1, "RREIM", "Transportation Expense", transportationExpense1.toPlaingString(), transportationExpense1.toPlaingString(), "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor1, "Per Disablement/Maximum", true, true);

		});

		String coverageCd2 = "GLASS";
		String availableLimits2 = "Yes";

		PolicyCoverageInfo coverageResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd2, availableLimits2), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse2, "COMPDED", "Other Than Collision", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse2, "COLLDED", "Collision Deductible", availableLimits1, availableLimits1, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse2, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse2, "LOAN", "Auto Loan/Lease Coverage", "0", loanLeaseCov1, "None", true, true);

			coverageXproperties(softly, 4, coverageResponse2, "RREIM", "Transportation Expense", transportationExpense1.toPlaingString(), transportationExpense1.toPlaingString(), "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse2, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor1, "Per Disablement/Maximum", true, true);

		});

		String coverageCd3 = "LOAN";
		String availableLimits3 = "1";

		PolicyCoverageInfo coverageResponse3 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd3, availableLimits3), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse3, "COMPDED", "Other Than Collision", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse3, "COLLDED", "Collision Deductible", availableLimits1, availableLimits1, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse3, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse3, "LOAN", "Auto Loan/Lease Coverage", availableLimits3, "Yes", "None", true, true);

			coverageXproperties(softly, 4, coverageResponse3, "RREIM", "Transportation Expense", transportationExpense1.toPlaingString(), transportationExpense1.toPlaingString(), "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse3, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor1, "Per Disablement/Maximum", true, true);

		});

		String coverageCd4 = "RREIM";
		String availableLimits4 = "900";

		PolicyCoverageInfo coverageResponse4 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd4, availableLimits4), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse4, "COMPDED", "Other Than Collision", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse4, "COLLDED", "Collision Deductible", availableLimits1, availableLimits1, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse4, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse4, "LOAN", "Auto Loan/Lease Coverage", availableLimits3, "Yes", "None", true, true);

			coverageXproperties(softly, 4, coverageResponse4, "RREIM", "Transportation Expense", availableLimits4, availableLimits4, "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse4, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor1, "Per Disablement/Maximum", true, true);

		});

		String coverageCd5 = "TOWINGLABOR";
		String availableLimits5 = "50/300";

		PolicyCoverageInfo coverageResponse5 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd5, availableLimits5), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse5, "COMPDED", "Other Than Collision", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse5, "COLLDED", "Collision Deductible", availableLimits1, availableLimits1, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse5, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse5, "LOAN", "Auto Loan/Lease Coverage", availableLimits3, "Yes", "None", true, true);

			coverageXproperties(softly, 4, coverageResponse5, "RREIM", "Transportation Expense", availableLimits4, availableLimits4, "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse5, "TOWINGLABOR", "Towing and Labor Coverage", availableLimits5, "$50/$300", "Per Disablement/Maximum", true, true);

		});

		String coverageCdRemove = "COMPDED";
		String availableLimitsRemove = "-1";

		PolicyCoverageInfo coverageResponse14 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdRemove, availableLimitsRemove), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse14, "COMPDED", "Other Than Collision", availableLimitsRemove, "No Coverage", "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse14, "COLLDED", "Collision Deductible", availableLimitsRemove, "No Coverage", "Deductible", true, false);

			coverageXproperties(softly, 2, coverageResponse14, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, false);

			coverageXproperties(softly, 3, coverageResponse14, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", true, false);

			//coverageXproperties(softly, 4, coverageResponse14, "RREIM", "Transportation Expense", "0", "0", "Per Occurrence", true, false);

			coverageXproperties(softly, 5, coverageResponse14, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, false);

		});

		String coverageCdChange = "COMPDED";
		String availableLimitsChange = "500";

		PolicyCoverageInfo coverageResponse6 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChange, availableLimitsChange), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse6, "COMPDED", "Other Than Collision", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse6, "COLLDED", "Collision Deductible", availableLimitsRemove, "No Coverage", "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse6, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, true);

			coverageXproperties(softly, 3, coverageResponse6, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", true, false);

			//	coverageXproperties(softly, 4, coverageResponse6, "RREIM", "Transportation Expense", "0", "0", "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse6, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, false);

		});

		String coverageCdChangeColl = "COLLDED";
		String availableLimitsChangeColl = "500";

		PolicyCoverageInfo coverageResponse7 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeColl, availableLimitsChangeColl), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse7, "COMPDED", "Other Than Collision", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse7, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse7, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, true);

			coverageXproperties(softly, 3, coverageResponse7, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", true, true);

			//coverageXproperties(softly, 4, coverageResponse7, "RREIM", "Transportation Expense","0", "0", "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse7, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeGlass = "GLASS";
		String availableLimitsChangeGlass = "Yes";

		PolicyCoverageInfo coverageResponse8 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeGlass, availableLimitsChangeGlass), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {

			coverageXproperties(softly, 0, coverageResponse8, "COMPDED", "Other Than Collision", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse8, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse8, "GLASS", "Full Safety Glass", "true", availableLimitsChangeGlass, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse8, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", true, true);

			//coverageXproperties(softly, 4, coverageResponse8, "RREIM", "Transportation Expense","0", "0", "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse8, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeGlassNoCov = "GLASS";
		String availableLimitsChangeGlassNoCov = "No Coverage";

		PolicyCoverageInfo coverageResponse9 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeGlassNoCov, availableLimitsChangeGlassNoCov), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {

			coverageXproperties(softly, 0, coverageResponse9, "COMPDED", "Other Than Collision", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse9, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse9, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse9, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", true, true);

			//	coverageXproperties(softly, 4, coverageResponse9, "RREIM", "Transportation Expense","0", "0", "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse9, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeLoanNoCov = "LOAN";
		String availableLimitsChangeLoanNoCov = "1";

		PolicyCoverageInfo coverageResponse10 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeLoanNoCov, availableLimitsChangeLoanNoCov), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {

			coverageXproperties(softly, 0, coverageResponse10, "COMPDED", "Other Than Collision", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse10, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse10, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse10, "LOAN", "Auto Loan/Lease Coverage", "1", "Yes", "None", true, true);

			//coverageXproperties(softly, 4, coverageResponse10, "RREIM", "Transportation Expense", availableLimits4, availableLimits4, "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse10, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeTransport = "RREIM";
		String availableLimitsChangeTransport = "900";

		PolicyCoverageInfo coverageResponse11 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTransport, availableLimitsChangeTransport), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {

			coverageXproperties(softly, 0, coverageResponse11, "COMPDED", "Other Than Collision", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse11, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse11, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse11, "LOAN", "Auto Loan/Lease Coverage", "1", "Yes", "None", true, true);

			coverageXproperties(softly, 4, coverageResponse11, "RREIM", "Transportation Expense", availableLimitsChangeTransport, availableLimitsChangeTransport, "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse11, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeTowing = "TOWINGLABOR";
		String availableLimitsChangeTowing = "50/300";

		PolicyCoverageInfo coverageResponse12 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTowing, availableLimitsChangeTowing), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {

			coverageXproperties(softly, 0, coverageResponse12, "COMPDED", "Other Than Collision", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse12, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse12, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse12, "LOAN", "Auto Loan/Lease Coverage", "1", "Yes", "None", true, true);

			//	coverageXproperties(softly, 4, coverageResponse12, "RREIM", "Transportation Expense", availableLimitsChangeTransport, availableLimitsChangeTransport, "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse12, "TOWINGLABOR", "Towing and Labor Coverage", availableLimitsChangeTowing, "$50/$300", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeTowingNoCov = "TOWINGLABOR";
		String availableLimitsChangeTowingNoCov = "0/0";

		PolicyCoverageInfo coverageResponse13 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTowingNoCov, availableLimitsChangeTowingNoCov), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {

			coverageXproperties(softly, 0, coverageResponse13, "COMPDED", "Other Than Collision", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse13, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse13, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse13, "LOAN", "Auto Loan/Lease Coverage", "1", "Yes", "None", true, true);

			//coverageXproperties(softly, 4, coverageResponse13, "RREIM", "Transportation Expense", availableLimitsChangeTransport, availableLimitsChangeTransport, "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse13, "TOWINGLABOR", "Towing and Labor Coverage", availableLimitsChangeTowingNoCov, "No Coverage", "Per Disablement/Maximum", true, true);

		});

		helperMiniServices.endorsementRateAndBind(policyNumber);

		testEValueDiscount.secondEndorsementIssueCheck();
	}

	protected void pas10352_ManageVehicleCoverageUpdateCoverageOtherState(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		String purchaseDate = "2012-02-21";
		String vin = "SHHFK7H41JU201444";
		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
		assertThat(addVehicle.oid).isNotEmpty();
		String newVehicleOid = addVehicle.oid;

		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		String zipCodeOwnership = "23703";
		String addressLine1Ownership = "4112 FORREST HILLS DR";
		String addressLine2Ownership = "Apt. 202";
		String cityOwnership = "PORTSMOUTH";
		String stateOwnership = "VA";
		String otherNameOwnership = "other name";
		String secondNameOwnership = "Second Name";

		//Update vehicle Leased Financed Info
		VehicleUpdateDto updateVehicleLeasedFinanced = new VehicleUpdateDto();
		updateVehicleLeasedFinanced.vehicleOwnership = new VehicleOwnership();
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine1 = addressLine1Ownership;
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine2 = addressLine2Ownership;
		updateVehicleLeasedFinanced.vehicleOwnership.city = cityOwnership;
		updateVehicleLeasedFinanced.vehicleOwnership.stateProvCd = stateOwnership;
		updateVehicleLeasedFinanced.vehicleOwnership.postalCode = zipCodeOwnership;
		updateVehicleLeasedFinanced.vehicleOwnership.ownership = "LSD";
		updateVehicleLeasedFinanced.vehicleOwnership.name = otherNameOwnership;
		updateVehicleLeasedFinanced.vehicleOwnership.secondName = secondNameOwnership;
		VehicleUpdateResponseDto ownershipUpdateResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleLeasedFinanced);
		assertThat(ownershipUpdateResponse.vehicleOwnership.ownership).isEqualTo("LSD");

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		Dollar comprehensiveDeductible = new Dollar(premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel()).replace("(+$0.00)", "").trim());
		Dollar collisionDeductible = new Dollar(premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel()).replace("(+$0.00)", "").trim());
		String fullSafetyGlassVeh = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel());
		String loanLeaseCov = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.VEHICLE_LOAN_LEASE_PROTECTION.getLabel()).replace("(+$0.00)", "").trim();
		String transportationExpense = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.RENTAL_REIMBURSEMENT.getLabel().replace("(+$0.00)", "").trim());
		String towingAndLabor = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel()).replace("(Included)", "").replace("(+$0.00)", "").replace("$", "").trim();

		premiumAndCoveragesTab.saveAndExit();

		String coverageCd = "COMPDED";
		String availableLimits = "100";

		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, availableLimits), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {

			coverageXproperties(softly, 0, coverageResponse, "COMPDED", "Comprehensive Deductible", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse, "COLLDED", "Collision Deductible", collisionDeductible.toPlaingString(), collisionDeductible.toPlaingString(), "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse, "LOAN", "Vehicle Loan/Lease Protection", "0", loanLeaseCov, "None", true, true);

			coverageXproperties(softly, 4, coverageResponse, "RREIM", "Rental Reimbursement", "0/0", transportationExpense, "Per Day/Maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);

		});

		String coverageCd1 = "COLLDED";
		String availableLimits1 = "100";

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd1, availableLimits1), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse1, "COMPDED", "Comprehensive Deductible", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse1, "COLLDED", "Collision Deductible", availableLimits1, availableLimits1, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse1, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse1, "LOAN", "Vehicle Loan/Lease Protection", "0", loanLeaseCov, "None", true, true);

			coverageXproperties(softly, 4, coverageResponse1, "RREIM", "Rental Reimbursement", "0/0", transportationExpense, "Per Day/Maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);

		});

		String coverageCd2 = "GLASS";
		String availableLimits2 = "Yes";

		PolicyCoverageInfo coverageResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd2, availableLimits2), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse2, "COMPDED", "Comprehensive Deductible", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse2, "COLLDED", "Collision Deductible", availableLimits1, availableLimits1, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse2, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse2, "LOAN", "Vehicle Loan/Lease Protection", "0", loanLeaseCov, "None", true, true);

			coverageXproperties(softly, 4, coverageResponse2, "RREIM", "Rental Reimbursement", "0/0", transportationExpense, "Per Day/Maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse2, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);

		});

		String coverageCd3 = "LOAN";
		String availableLimits3 = "1";

		PolicyCoverageInfo coverageResponse3 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd3, availableLimits3), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse3, "COMPDED", "Comprehensive Deductible", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse3, "COLLDED", "Collision Deductible", availableLimits1, availableLimits1, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse3, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse3, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);

			coverageXproperties(softly, 4, coverageResponse3, "RREIM", "Rental Reimbursement", "0/0", transportationExpense, "Per Day/Maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse3, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);

		});

		String coverageCd4 = "RREIM";
		String availableLimits4 = "30/900";

		PolicyCoverageInfo coverageResponse4 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd4, availableLimits4), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse4, "COMPDED", "Comprehensive Deductible", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse4, "COLLDED", "Collision Deductible", availableLimits1, availableLimits1, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse4, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse4, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);

			coverageXproperties(softly, 4, coverageResponse4, "RREIM", "Rental Reimbursement", availableLimits4, "$30/$900", "Per Day/Maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse4, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);

		});

		String coverageCd5 = "TOWINGLABOR";
		String availableLimits5 = "50/300";

		PolicyCoverageInfo coverageResponse5 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd5, availableLimits5), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse5, "COMPDED", "Comprehensive Deductible", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse5, "COLLDED", "Collision Deductible", availableLimits1, availableLimits1, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse5, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse5, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);

			coverageXproperties(softly, 4, coverageResponse5, "RREIM", "Rental Reimbursement", availableLimits4, "$30/$900", "Per Day/Maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse5, "TOWINGLABOR", "Towing and Labor Coverage", availableLimits5, "$50/$300", "Per Disablement/Maximum", true, true);

		});

		String coverageCdRemove = "COMPDED";
		String availableLimitsRemove = "-1";

		PolicyCoverageInfo coverageResponse6 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdRemove, availableLimitsRemove), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse6, "COMPDED", "Comprehensive Deductible", availableLimitsRemove, "No Coverage", "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse6, "COLLDED", "Collision Deductible", availableLimitsRemove, "No Coverage", "Deductible", true, false);

			coverageXproperties(softly, 2, coverageResponse6, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, false);

			coverageXproperties(softly, 3, coverageResponse6, "LOAN", "Vehicle Loan/Lease Protection", "0", "No Coverage", "None", true, false);

			coverageXproperties(softly, 4, coverageResponse6, "RREIM", "Rental Reimbursement", "0/0", "No Coverage", "Per Day/Maximum", true, false);

			coverageXproperties(softly, 5, coverageResponse6, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, false);

		});

		String coverageCdChange = "COMPDED";
		String availableLimitsChange = "500";

		PolicyCoverageInfo coverageResponse7 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChange, availableLimitsChange), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse7, "COMPDED", "Comprehensive Deductible", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse7, "COLLDED", "Collision Deductible", availableLimitsRemove, "No Coverage", "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse7, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, true);

			coverageXproperties(softly, 3, coverageResponse7, "LOAN", "Vehicle Loan/Lease Protection", "0", "No Coverage", "None", true, false);

			//coverageXproperties(softly, 4, coverageResponse7, "RREIM", "Rental Reimbursement", availableLimits4, availableLimits4, "Per Day/Maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse7, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, false);

		});

		String coverageCdChangeColl = "COLLDED";
		String availableLimitsChangeColl = "500";

		PolicyCoverageInfo coverageResponse8 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeColl, availableLimitsChangeColl), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse8, "COMPDED", "Comprehensive Deductible", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse8, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse8, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, true);

			coverageXproperties(softly, 3, coverageResponse8, "LOAN", "Vehicle Loan/Lease Protection", "0", "No Coverage", "None", true, true);

			//coverageXproperties(softly, 4, coverageResponse8, "RREIM", "Rental Reimbursement", availableLimits4, availableLimits4, "Per Day/Maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse8, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeGlass = "GLASS";
		String availableLimitsChangeGlass = "Yes";

		PolicyCoverageInfo coverageResponse9 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeGlass, availableLimitsChangeGlass), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse9, "COMPDED", "Comprehensive Deductible", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse9, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse9, "GLASS", "Full Safety Glass", "true", availableLimitsChangeGlass, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse9, "LOAN", "Vehicle Loan/Lease Protection", "0", "No Coverage", "None", true, true);

			//coverageXproperties(softly, 4, coverageResponse9, "RREIM", "Rental Reimbursement", availableLimits4, availableLimits4, "Per Day/Maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse9, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeGlassNoCov = "GLASS";
		String availableLimitsChangeGlassNoCov = "No Coverage";

		PolicyCoverageInfo coverageResponse10 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeGlassNoCov, availableLimitsChangeGlassNoCov), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse10, "COMPDED", "Comprehensive Deductible", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse10, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse10, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse10, "LOAN", "Vehicle Loan/Lease Protection", "0", "No Coverage", "None", true, true);

			//coverageXproperties(softly, 4, coverageResponse10, "RREIM", "Rental Reimbursement", availableLimits4, availableLimits4, "Per Day/Maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse10, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeLoanNoCov = "LOAN";
		String availableLimitsChangeLoanNoCov = "1";

		PolicyCoverageInfo coverageResponse11 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeLoanNoCov, availableLimitsChangeLoanNoCov), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse11, "COMPDED", "Comprehensive Deductible", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse11, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse11, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse11, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);

			//coverageXproperties(softly, 4, coverageResponse11, "RREIM", "Rental Reimbursement", availableLimits4, availableLimits4, "Per Day/Maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse11, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);
		});

		String coverageCdChangeTransport = "RREIM";
		String availableLimitsChangeTransport = "30/900";

		PolicyCoverageInfo coverageResponse12 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTransport, availableLimitsChangeTransport), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse12, "COMPDED", "Comprehensive Deductible", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse12, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse12, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse12, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);

			coverageXproperties(softly, 4, coverageResponse12, "RREIM", "Rental Reimbursement", availableLimitsChangeTransport, "$30/$900", "Per Day/Maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse12, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeTowing = "TOWINGLABOR";
		String availableLimitsChangeTowing = "50/300";

		PolicyCoverageInfo coverageResponse13 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTowing, availableLimitsChangeTowing), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse13, "COMPDED", "Comprehensive Deductible", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse13, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse13, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse13, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);

			coverageXproperties(softly, 4, coverageResponse13, "RREIM", "Rental Reimbursement", availableLimitsChangeTransport, "$30/$900", "Per Day/Maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse13, "TOWINGLABOR", "Towing and Labor Coverage", availableLimitsChangeTowing, "$50/$300", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeTowingNoCov = "TOWINGLABOR";
		String availableLimitsChangeTowingNoCov = "0/0";

		PolicyCoverageInfo coverageResponse14 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTowingNoCov, availableLimitsChangeTowingNoCov), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse14, "COMPDED", "Comprehensive Deductible", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse14, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse14, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse14, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);

			coverageXproperties(softly, 4, coverageResponse14, "RREIM", "Rental Reimbursement", availableLimitsChangeTransport, "$30/$900", "Per Day/Maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse14, "TOWINGLABOR", "Towing and Labor Coverage", availableLimitsChangeTowingNoCov, "No Coverage", "Per Disablement/Maximum", true, true);

		});
	}

	protected void pas14693_viewCoverageAndUpdateCoverageRentalReimbursement(PolicyType policyType, ETCSCoreSoftAssertions softly) {
		mainApp().open();
		createCustomerIndividual();

		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo coverageResponse1 = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		List<Coverage> coveragesVehicle = coverageResponse1.vehicleLevelCoverages.get(0).coverages;
		verifyRREIM(softly, coveragesVehicle);
		assertCoverageLimitRentalReimbursement(coverageResponse1);

		//Add first vehicle
		String purchaseDate = "2013-02-22";
		String vin1 = "1HGFA16526L081415";
		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin1);
		assertThat(addVehicle.oid).isNotEmpty();
		String oid1 = addVehicle.oid;
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, oid1);

		PolicyCoverageInfo coverageResponse2 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, oid1, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

		List<Coverage> coveragesVehicle1 = coverageResponse2.vehicleLevelCoverages.get(0).coverages;
		verifyRREIM(softly, coveragesVehicle1);
		assertCoverageLimitRentalReimbursement(coverageResponse2);

		String coverageCdChangeTransport = "RREIM";
		String availableLimitsChangeTransport = "30/900";

		PolicyCoverageInfo rreimUpdatedCoverage = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTransport, availableLimitsChangeTransport), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage filteredCoverageResponseRREIM = rreimUpdatedCoverage.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "RREIM".equals(cov.coverageCd)).findFirst().orElse(null);

		softly.assertThat(filteredCoverageResponseRREIM.coverageLimit).isEqualTo("30/900");
		softly.assertThat(filteredCoverageResponseRREIM.coverageLimitDisplay).isEqualTo("$30/$900");
		assertCoverageLimitRentalReimbursementAfterUpdate(rreimUpdatedCoverage);

		String coverageCdChangeTransport1 = "RREIM";
		String availableLimitsChangeTransport1 = "0/0";

		PolicyCoverageInfo rreimUpdatedCoverage1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTransport1, availableLimitsChangeTransport1), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage filteredCoverageResponseRREIM1 = rreimUpdatedCoverage1.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "RREIM".equals(cov.coverageCd)).findFirst().orElse(null);

		softly.assertThat(filteredCoverageResponseRREIM1.coverageLimit).isEqualTo("0/0");
		softly.assertThat(filteredCoverageResponseRREIM1.coverageLimitDisplay).isEqualTo("No Coverage");
		assertCoverageLimitRentalReimbursement(rreimUpdatedCoverage1);

	}

	private void verifyRREIM(ETCSCoreSoftAssertions softly, List<Coverage> coveragesVehicle) {
		softly.assertThat(coveragesVehicle.get(4).coverageCd).isEqualTo("RREIM");
		softly.assertThat(coveragesVehicle.get(4).coverageDescription).isEqualTo("Rental Reimbursement");
		softly.assertThat(coveragesVehicle.get(4).coverageLimit).isEqualTo("0/0");
		softly.assertThat(coveragesVehicle.get(4).coverageLimitDisplay).isEqualTo("No Coverage");
		softly.assertThat(coveragesVehicle.get(4).coverageType).isEqualTo("Per Day/Maximum");
		softly.assertThat(coveragesVehicle.get(4).customerDisplayed).isEqualTo(true);
		softly.assertThat(coveragesVehicle.get(4).canChangeCoverage).isEqualTo(true);
	}

	protected void pas14693_updateCoverageRentalReimbursementBody(PolicyType policyType, ETCSCoreSoftAssertions softly) {
		mainApp().open();
		createCustomerIndividual();

		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo coverageResponse1 = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		List<Coverage> coveragesVehicle = coverageResponse1.vehicleLevelCoverages.get(0).coverages;
		verifyRREIM(softly, coveragesVehicle);
		assertCoverageLimitRentalReimbursement(coverageResponse1);

		//Add first vehicle
		String purchaseDate = "2013-02-22";
		String vin1 = "1HGFA16526L081415";
		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin1);
		assertThat(addVehicle.oid).isNotEmpty();
		String oid1 = addVehicle.oid;
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, oid1);

		PolicyCoverageInfo coverageResponse2 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, oid1, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

		List<Coverage> coveragesVehicle1 = coverageResponse2.vehicleLevelCoverages.get(0).coverages;
		verifyRREIM(softly, coveragesVehicle1);
		assertCoverageLimitRentalReimbursement(coverageResponse2);

		String coverageCdChangeTransport = "RREIM";
		String availableLimitsChangeTransport = "30/900";

		PolicyCoverageInfo rreimUpdatedCoverage = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTransport, availableLimitsChangeTransport), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage filteredCoverageResponseRREIM1 = rreimUpdatedCoverage.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "RREIM".equals(cov.coverageCd)).findFirst().orElse(null);

		softly.assertThat(filteredCoverageResponseRREIM1.coverageLimit).isEqualTo("30/900");
		softly.assertThat(filteredCoverageResponseRREIM1.coverageLimitDisplay).isEqualTo("$30/$900");
		assertCoverageLimitRentalReimbursementAfterUpdate(rreimUpdatedCoverage);

		String coverageCdChange = "COMPDED";
		String availableLimitsChange = "-1";

		PolicyCoverageInfo rreimUpdatedCoverage1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChange, availableLimitsChange), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage filteredCoverageResponseComp = rreimUpdatedCoverage1.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COMPDED".equals(cov.coverageCd)).findFirst().orElse(null);
		Coverage filteredCoverageResponseRreim = rreimUpdatedCoverage1.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "RREIM".equals(cov.coverageCd)).findFirst().orElse(null);

		softly.assertThat(filteredCoverageResponseComp.coverageLimit).isEqualTo("-1");
		softly.assertThat(filteredCoverageResponseComp.coverageLimitDisplay).isEqualTo("No Coverage");

		softly.assertThat(filteredCoverageResponseRreim.coverageLimit).isEqualTo("0/0");
		softly.assertThat(filteredCoverageResponseRreim.coverageLimitDisplay).isEqualTo("No Coverage");
		softly.assertThat(filteredCoverageResponseRreim.canChangeCoverage).isEqualTo(false);
		assertCoverageLimitRentalReimbursement(rreimUpdatedCoverage1);

		String coverageCdChangeCom = "COMPDED";
		String availableLimitsChangeCom = "500";

		PolicyCoverageInfo rreimUpdatedCoverageComp = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeCom, availableLimitsChangeCom), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage filteredCoverageResponseComp1 = rreimUpdatedCoverageComp.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COMPDED".equals(cov.coverageCd)).findFirst().orElse(null);
		Coverage filteredCoverageResponseRreim1 = rreimUpdatedCoverageComp.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "RREIM".equals(cov.coverageCd)).findFirst().orElse(null);

		softly.assertThat(filteredCoverageResponseComp1.coverageLimit).isEqualTo("500");
		softly.assertThat(filteredCoverageResponseComp1.coverageLimitDisplay).isEqualTo("$500");

		softly.assertThat(filteredCoverageResponseRreim1.coverageLimit).isEqualTo("0/0");
		softly.assertThat(filteredCoverageResponseRreim1.coverageLimitDisplay).isEqualTo("No Coverage");
		assertCoverageLimitRentalReimbursement(rreimUpdatedCoverageComp);

		String coverageCdChangeColl = "COLLDED";
		String availableLimitsChangeColl = "500";

		PolicyCoverageInfo rreimUpdatedCoverageColl1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeColl, availableLimitsChangeColl), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

		Coverage filteredCoverageResponseColl = rreimUpdatedCoverageColl1.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COLLDED".equals(cov.coverageCd)).findFirst().orElse(null);
		Coverage filteredCoverageResponseRreimColl = rreimUpdatedCoverageColl1.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "RREIM".equals(cov.coverageCd)).findFirst().orElse(null);

		softly.assertThat(filteredCoverageResponseColl.coverageLimit).isEqualTo("500");
		softly.assertThat(filteredCoverageResponseColl.coverageLimitDisplay).isEqualTo("$500");

		softly.assertThat(filteredCoverageResponseRreimColl.coverageLimit).isEqualTo("0/0");
		softly.assertThat(filteredCoverageResponseRreimColl.coverageLimitDisplay).isEqualTo("No Coverage");
		assertCoverageLimitRentalReimbursement(rreimUpdatedCoverageColl1);

		String coverageChangeTransport = "RREIM";
		String availableLimitsTransport = "50/1500";

		PolicyCoverageInfo rreimUpdatedCoverageHigh = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageChangeTransport, availableLimitsTransport), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage filteredCoverageResponseRreimHigh = rreimUpdatedCoverageHigh.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "RREIM".equals(cov.coverageCd)).findFirst().orElse(null);

		softly.assertThat(filteredCoverageResponseRreimHigh.coverageLimit).isEqualTo("50/1500");
		softly.assertThat(filteredCoverageResponseRreimHigh.coverageLimitDisplay).isEqualTo("$50/$1,500");
		assertCoverageLimitRentalReimbursementAfterUpdateRreim(rreimUpdatedCoverageHigh);
	}

	protected void pas14316_LoanLeasedCovForLeasedOldVehicleBody(ETCSCoreSoftAssertions softly, String ownershipType) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//VIN Will need to be updated yearly to make the test functional
		String purchaseDate = "2013-02-22";
		String vin = "JF1GJAA64EH012557"; //2014 Subaru Impreza
		//String vin = "JF1GPAT66FH237608"; //2015 Subaru Impreza

		//Add vehicle with specific info
		Vehicle vehicleAddRequest = new Vehicle();
		vehicleAddRequest.purchaseDate = purchaseDate;
		vehicleAddRequest.vehIdentificationNo = vin;
		String newVehicleOid = helperMiniServices.vehicleAddRequestWithCheck(policyNumber, vehicleAddRequest);

		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		//Add coverage check here
		String coverageCdValue = "LOAN";
		PolicyCoverageInfo endorsementCoverageResponseOwnedOldVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage endorsementCoverageResponseOwnedOldVehFiltered = endorsementCoverageResponseOwnedOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, endorsementCoverageResponseOwnedOldVehFiltered, "0", "No Coverage", false, false);

		String zipCode = "23703";
		String addressLine1 = "4112 FORREST HILLS DR";
		String addressLine2 = "Apt. 202";
		String city = "PORTSMOUTH";
		String state = "VA";
		String otherName = "other name";
		String secondName = "Second Name";

		//Update vehicle Leased Financed Info
		VehicleUpdateDto updateVehicleLeasedFinanced = new VehicleUpdateDto();
		updateVehicleLeasedFinanced.vehicleOwnership = new VehicleOwnership();
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine1 = addressLine1;
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine2 = addressLine2;
		updateVehicleLeasedFinanced.vehicleOwnership.city = city;
		updateVehicleLeasedFinanced.vehicleOwnership.stateProvCd = state;
		updateVehicleLeasedFinanced.vehicleOwnership.postalCode = zipCode;
		updateVehicleLeasedFinanced.vehicleOwnership.ownership = ownershipType;
		updateVehicleLeasedFinanced.vehicleOwnership.name = otherName;
		updateVehicleLeasedFinanced.vehicleOwnership.secondName = secondName;
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleLeasedFinanced);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo endorsementCoverageResponseLsdFinOldVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage endorsementCoverageResponseLsdFinOldVehFiltered = endorsementCoverageResponseLsdFinOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, endorsementCoverageResponseLsdFinOldVehFiltered, "0", "No Coverage", false, false);

		helperMiniServices.endorsementRateAndBind(policyNumber);
		PolicyCoverageInfo policyCoverageResponseLsdFinOldVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage policyCoverageResponseLsdFinOldVehFiltered = policyCoverageResponseLsdFinOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, policyCoverageResponseLsdFinOldVehFiltered, "0", "No Coverage", false, false);

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		VehicleUpdateDto updateVehicleOwned = new VehicleUpdateDto();
		updateVehicleOwned.vehicleOwnership = new VehicleOwnership();
		updateVehicleOwned.vehicleOwnership.ownership = "OWN";
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleOwned);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo endorsementCoverageResponseOwnedOldVeh2 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage endorsementCoverageResponseOwnedOldVeh2Filtered = endorsementCoverageResponseOwnedOldVeh2.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, endorsementCoverageResponseOwnedOldVeh2Filtered, "0", "No Coverage", false, false);
	}

	protected void pas14721_UpdateCoveragesServiceBIPD(PolicyType policyType, String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		//Rate Policy
		helperMiniServices.rateEndorsementWithCheck(policyNumber);

		String coverageCd = "BI";
		String newBILimits = "500000/500000";

		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, newBILimits), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {

			Coverage filteredCoverageResponseBI = coverageResponse.policyCoverages.stream().filter(cov -> "BI".equals(cov.coverageCd)).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseBI.coverageLimit.equals(newBILimits)).isEqualTo(true);
			softly.assertThat("$500,000/$500,000".equals(filteredCoverageResponseBI.coverageLimitDisplay)).isEqualTo(true);

			assertCoverageLimitForBI(coverageResponse, state);

			Coverage filteredCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat("50000".equals(filteredCoverageResponsePD.coverageLimit)).isEqualTo(true);
			softly.assertThat("$50,000".equals(filteredCoverageResponsePD.coverageLimitDisplay)).isEqualTo(true);

			assertCoverageLimitForPDBI(coverageResponse, state);
		});

		String coverageCd1 = "PD";
		String newPD1 = "500000";

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd1, newPD1), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {

			Coverage filteredCoverageResponse1 = coverageResponse1.policyCoverages.stream().filter(cov -> "BI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponse1.coverageLimit.equals(newBILimits)).isEqualTo(true);
			softly.assertThat("$500,000/$500,000".equals(filteredCoverageResponse1.coverageLimitDisplay)).isEqualTo(true);

			Coverage filteredCoverageResponsePD1 = coverageResponse1.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat("500000".equals(filteredCoverageResponsePD1.coverageLimit)).isEqualTo(true);
			softly.assertThat("$500,000".equals(filteredCoverageResponsePD1.coverageLimitDisplay)).isEqualTo(true);

			assertCoverageLimitForPDBI(coverageResponse1, state);

		});

		String coverageCd2 = "BI";
		String newBI2 = "50000/100000";

		PolicyCoverageInfo coverageResponse2 = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd2, newBI2), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			Coverage filteredCoverageResponseBI = coverageResponse2.policyCoverages.stream().filter(cov -> "BI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponseBI.coverageLimit.equals(newBI2)).isEqualTo(true);
			softly.assertThat("$50,000/$100,000".equals(filteredCoverageResponseBI.coverageLimitDisplay)).isEqualTo(true);

			Coverage filteredCoverageResponsePD2 = coverageResponse2.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat("100000".equals(filteredCoverageResponsePD2.coverageLimit)).isEqualTo(true);
			softly.assertThat("$100,000".equals(filteredCoverageResponsePD2.coverageLimitDisplay)).isEqualTo(true);

			assertCoverageLimitForPD(coverageResponse2, state);
		});

	}

	protected void pas15254_14733_UpdateCoveragesUM_UIM_Body(PolicyType policyType, String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		//Rate Policy
		helperMiniServices.rateEndorsementWithCheck(policyNumber);

		String coverageCd = "BI";
		String newBILimits = "500000/500000";

		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, newBILimits), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {

			Coverage filteredCoverageResponseBI = coverageResponse.policyCoverages.stream().filter(cov -> "BI".equals(cov.coverageCd)).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseBI.coverageLimit.equals(newBILimits)).isEqualTo(true);
			softly.assertThat("$500,000/$500,000".equals(filteredCoverageResponseBI.coverageLimitDisplay)).isEqualTo(true);

			//assertCoverageLimitForBI(coverageResponse, state); //TODO-mstrazds: uncomment when pas14721_UpdateCoveragesServiceBIPD is implemented for all states

			Coverage filteredCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat("50000".equals(filteredCoverageResponsePD.coverageLimit)).isEqualTo(true);
			softly.assertThat("$50,000".equals(filteredCoverageResponsePD.coverageLimitDisplay)).isEqualTo(true);

			//assertCoverageLimitForPDBI(coverageResponse, state); //TODO-mstrazds: uncomment when pas14721_UpdateCoveragesServiceBIPD is implemented for all states

			Coverage filteredCoverageResponseUMBI = coverageResponse.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(newBILimits.equals(filteredCoverageResponseUMBI.coverageLimit)).isEqualTo(true);
			softly.assertThat("$500,000/$500,000".equals(filteredCoverageResponseUMBI.coverageLimitDisplay)).isEqualTo(true);

			validateUIMBI_pas15254(softly, state, coverageResponse, newBILimits); //validate UIMBI for states where it is separate coverage

		});

		String coverageCd1 = "PD";
		String newPD1 = "500000";

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd1, newPD1), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {

			Coverage filteredCoverageResponse1 = coverageResponse1.policyCoverages.stream().filter(cov -> "BI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponse1.coverageLimit.equals(newBILimits)).isEqualTo(true);
			softly.assertThat("$500,000/$500,000".equals(filteredCoverageResponse1.coverageLimitDisplay)).isEqualTo(true);

			Coverage filteredCoverageResponsePD1 = coverageResponse1.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat("500000".equals(filteredCoverageResponsePD1.coverageLimit)).isEqualTo(true);
			softly.assertThat("$500,000".equals(filteredCoverageResponsePD1.coverageLimitDisplay)).isEqualTo(true);

			//assertCoverageLimitForPDBI(coverageResponse1, state); //TODO-mstrazds: uncomment when pas14721_UpdateCoveragesServiceBIPD is implemented for all states

			Coverage filteredCoverageResponseUMBI = coverageResponse1.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(newBILimits.equals(filteredCoverageResponseUMBI.coverageLimit)).isEqualTo(true);
			softly.assertThat("$500,000/$500,000".equals(filteredCoverageResponseUMBI.coverageLimitDisplay)).isEqualTo(true);

			validateUIMBI_pas15254(softly, state, coverageResponse1, newBILimits); //validate UIMBI for states where it is separate coverage

		});

		String coverageCd2 = "BI";
		String newBI2 = "50000/100000";

		PolicyCoverageInfo coverageResponse2 = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd2, newBI2), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			Coverage filteredCoverageResponseBI = coverageResponse2.policyCoverages.stream().filter(cov -> "BI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponseBI.coverageLimit.equals(newBI2)).isEqualTo(true);
			softly.assertThat("$50,000/$100,000".equals(filteredCoverageResponseBI.coverageLimitDisplay)).isEqualTo(true);

			Coverage filteredCoverageResponsePD2 = coverageResponse2.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat("100000".equals(filteredCoverageResponsePD2.coverageLimit)).isEqualTo(true);
			softly.assertThat("$100,000".equals(filteredCoverageResponsePD2.coverageLimitDisplay)).isEqualTo(true);
			//TODO Maris to fix it during refactoring
			//assertCoverageLimitForPD(coverageResponse2, state);

			Coverage filteredCoverageResponseUMBI = coverageResponse2.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(newBI2.equals(filteredCoverageResponseUMBI.coverageLimit)).isEqualTo(true);
			softly.assertThat("$50,000/$100,000".equals(filteredCoverageResponseUMBI.coverageLimitDisplay)).isEqualTo(true);

			validateUIMBI_pas15254(softly, state, coverageResponse2, newBI2); //validate UIMBI for states where it is separate coverage
		});

		mainApp().close();
		helperMiniServices.endorsementRateAndBind(policyNumber);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		testEValueDiscount.secondEndorsementIssueCheck();

	}

	protected void pas14646_UimDelimiter(String state, ETCSCoreSoftAssertions softly) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		SearchPage.openPolicy(policyNumber);
		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage filteredPolicyCoverageResponse = policyCoverageResponse.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.coverageCd)).findFirst().orElse(null);
		softly.assertThat(filteredPolicyCoverageResponse.coverageType).isEqualTo("Per Person/Per Accident");
		softly.assertThat(filteredPolicyCoverageResponse.availableLimits.size()).isNotEqualTo(0);

		PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage filteredCoverageEndorsementResponse = coverageEndorsementResponse.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.coverageCd)).findFirst().orElse(null);
		softly.assertThat(filteredCoverageEndorsementResponse.coverageType).isEqualTo("Per Person/Per Accident");
		softly.assertThat(filteredPolicyCoverageResponse.availableLimits.size()).isNotEqualTo(0);
	}

	protected void pas14648_MedpmDelimiter(PolicyType policyType) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage filteredPolicyCoverageResponse = policyCoverageResponse.policyCoverages.stream().filter(cov -> "MEDPM".equals(cov.coverageCd)).findFirst().orElse(null);
		assertSoftly(softly -> {
			softly.assertThat(filteredPolicyCoverageResponse.coverageCd).isEqualTo("MEDPM");
			softly.assertThat(filteredPolicyCoverageResponse.coverageType).isEqualTo("Per Person");
			softly.assertThat(filteredPolicyCoverageResponse.availableLimits.size()).isNotEqualTo(0);
		});

		PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage filteredPolicyCoverageResponse1 = coverageEndorsementResponse.policyCoverages.stream().filter(cov -> "MEDPM".equals(cov.coverageCd)).findFirst().orElse(null);
		assertSoftly(softly -> {
			softly.assertThat(filteredPolicyCoverageResponse1.coverageCd).isEqualTo("MEDPM");
			softly.assertThat(filteredPolicyCoverageResponse1.coverageType).isEqualTo("Per Person");
			softly.assertThat(filteredPolicyCoverageResponse1.availableLimits.size()).isNotEqualTo(0);
		});
	}

	protected void pas15228_UmUimDelimiterBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		assertSoftly(softly -> {
			PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			Coverage filteredPolicyCoverageResponseUMBI = policyCoverageResponse.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseUMBI.coverageType).isEqualTo("Per Person/Per Accident");
			softly.assertThat(filteredPolicyCoverageResponseUMBI.availableLimits.size()).isNotEqualTo(0);
			softly.assertThat(filteredPolicyCoverageResponseUMBI.canChangeCoverage).isFalse();

			Coverage filteredPolicyCoverageResponseUIMBI = policyCoverageResponse.policyCoverages.stream().filter(cov -> "UIMBI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseUIMBI.coverageType).isEqualTo("Per Person/Per Accident");
			softly.assertThat(filteredPolicyCoverageResponseUIMBI.availableLimits.size()).isNotEqualTo(0);
			softly.assertThat(filteredPolicyCoverageResponseUIMBI.canChangeCoverage).isFalse();

			PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			Coverage filteredEndorsementCoverageResponseUMBI = coverageEndorsementResponse.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredEndorsementCoverageResponseUMBI.coverageType).isEqualTo("Per Person/Per Accident");
			softly.assertThat(filteredEndorsementCoverageResponseUMBI.availableLimits.size()).isNotEqualTo(0);
			softly.assertThat(filteredEndorsementCoverageResponseUMBI.canChangeCoverage).isFalse();

			Coverage filteredEndorsementCoverageResponseUIMBI = coverageEndorsementResponse.policyCoverages.stream().filter(cov -> "UIMBI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredEndorsementCoverageResponseUIMBI.coverageType).isEqualTo("Per Person/Per Accident");
			softly.assertThat(filteredEndorsementCoverageResponseUIMBI.availableLimits.size()).isNotEqualTo(0);
			softly.assertThat(filteredEndorsementCoverageResponseUIMBI.canChangeCoverage).isFalse();
		});
	}

	protected void pas15824_UmpdDelimiterBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		assertSoftly(softly -> {
			PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			Coverage filteredPolicyCoverageResponseUMPD = policyCoverageResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.coverageCd)).findFirst().orElse(null);
			//BUG: PAS-15829 UMPD not returned from viewPolicyCoverages for NJ (for Policy and Endorsement)
			softly.assertThat(filteredPolicyCoverageResponseUMPD.coverageType).isEqualTo("Per Accident");
			softly.assertThat(filteredPolicyCoverageResponseUMPD.availableLimits.size()).isEqualTo(0);
			softly.assertThat(filteredPolicyCoverageResponseUMPD.canChangeCoverage).isFalse();

			PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			Coverage filteredEndorsementCoverageResponseUMPD = coverageEndorsementResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredEndorsementCoverageResponseUMPD.coverageType).isEqualTo("Per Accident");
			softly.assertThat(filteredEndorsementCoverageResponseUMPD.availableLimits.size()).isEqualTo(0);
			softly.assertThat(filteredEndorsementCoverageResponseUMPD.canChangeCoverage).isFalse();
		});
	}

	protected void pas15325_UmpdNotExistBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		assertSoftly(softly -> {
			PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			assertThat(policyCoverageResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.coverageCd)).findFirst().orElse(null)).isNull();

			PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			assertThat(coverageEndorsementResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.coverageCd)).findFirst().orElse(null)).isNull();
		});
	}

	//validate UIMBI for states where it is separate coverage
	private void validateUIMBI_pas15254(ETCSCoreSoftAssertions softly, String state, PolicyCoverageInfo coverageResponse, String newBILimits) {
		if ("AZ, ID, KY, PA, SD, UT, WV, MT".contains(state)) {
			Coverage filteredCoverageResponseUIMBI = coverageResponse.policyCoverages.stream().filter(cov -> "UIMBI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(newBILimits.equals(filteredCoverageResponseUIMBI.coverageLimit)).isEqualTo(true);
		}
	}

	private void coverageXproperties(ETCSCoreSoftAssertions softly, int coverageXnumber, PolicyCoverageInfo coverageResponse, String coverageCd, String coverageDesc, String availableLimits, String coverageLimitDisplay, String coverageType, boolean customerDisplay, boolean canChangeCoverage) {
		softly.assertThat(getCoverageX(coverageResponse, coverageXnumber).coverageCd).isEqualTo(coverageCd);
		softly.assertThat(getCoverageX(coverageResponse, coverageXnumber).coverageDescription).isEqualTo(coverageDesc);
		softly.assertThat(getCoverageX(coverageResponse, coverageXnumber).coverageLimit.replace(".00", "")).isEqualTo(availableLimits.replace(".00", ""));
		softly.assertThat(getCoverageX(coverageResponse, coverageXnumber).coverageLimitDisplay).contains(coverageLimitDisplay.toString().replace(".00", "").replace("(+$0)", "").trim());
		softly.assertThat(getCoverageX(coverageResponse, coverageXnumber).coverageType).isEqualTo(coverageType);
		softly.assertThat(getCoverageX(coverageResponse, coverageXnumber).customerDisplayed).isEqualTo(customerDisplay);
		softly.assertThat(getCoverageX(coverageResponse, coverageXnumber).canChangeCoverage).isEqualTo(canChangeCoverage);
	}

	private Coverage getCoverageX(PolicyCoverageInfo coverageResponse, int number) {
		return coverageResponse.vehicleLevelCoverages.get(0).coverages.get(number);
	}

	private void assertCoverageLimitTowingLabor(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			Coverage coverageLimit = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5);
			softly.assertThat(coverageLimit.availableLimits.get(0).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageLimit.availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageLimit.availableLimits.get(1).coverageLimit).isEqualTo("50/300");
			softly.assertThat(coverageLimit.availableLimits.get(1).coverageLimitDisplay).isEqualTo("$50/$300");
		});
	}

	private void assertCoverageLimitTransportationExpense(PolicyCoverageInfo coverageResponse, boolean filtered) {
		assertSoftly(softly -> {
			Coverage coverageLimitTransportation = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4);
			softly.assertThat(coverageLimitTransportation.availableLimits.get(0).coverageLimit).isEqualTo("600");
			softly.assertThat(coverageLimitTransportation.availableLimits.get(0).coverageLimitDisplay).isEqualTo("$600 (Included)");

			softly.assertThat(coverageLimitTransportation.availableLimits.get(1).coverageLimit).isEqualTo("900");
			softly.assertThat(coverageLimitTransportation.availableLimits.get(1).coverageLimitDisplay).isEqualTo("$900");

			if (filtered) {
				softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).availableLimits.size()).isEqualTo(2);
			} else {
				softly.assertThat(coverageLimitTransportation.availableLimits.get(2).coverageLimit).isEqualTo("1200");
				softly.assertThat(coverageLimitTransportation.availableLimits.get(2).coverageLimitDisplay).isEqualTo("$1,200");

				softly.assertThat(coverageLimitTransportation.availableLimits.get(3).coverageLimit).isEqualTo("1500");
				softly.assertThat(coverageLimitTransportation.availableLimits.get(3).coverageLimitDisplay).isEqualTo("$1,500");
			}
		});
	}

	private void assertCoverageLimitRentalReimbursement(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			Coverage coverageLimitTransportation = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4);

			softly.assertThat(coverageLimitTransportation.availableLimits.get(0).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageLimitTransportation.availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageLimitTransportation.availableLimits.get(1).coverageLimit).isEqualTo("30/900");
			softly.assertThat(coverageLimitTransportation.availableLimits.get(1).coverageLimitDisplay).isEqualTo("$30/$900");

			softly.assertThat(coverageLimitTransportation.availableLimits.get(2).coverageLimit).isEqualTo("40/1200");
			softly.assertThat(coverageLimitTransportation.availableLimits.get(2).coverageLimitDisplay).isEqualTo("$40/$1,200");

			softly.assertThat(coverageLimitTransportation.availableLimits.get(3).coverageLimit).isEqualTo("50/1500");
			softly.assertThat(coverageLimitTransportation.availableLimits.get(3).coverageLimitDisplay).isEqualTo("$50/$1,500");

		});
	}

	private void assertCoverageLimitRentalReimbursementAfterUpdate(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			Coverage coverageLimitTransportation = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4);

			softly.assertThat(coverageLimitTransportation.availableLimits.get(0).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageLimitTransportation.availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageLimitTransportation.availableLimits.get(1).coverageLimit).isEqualTo("30/900");
			softly.assertThat(coverageLimitTransportation.availableLimits.get(1).coverageLimitDisplay).isEqualTo("$30/$900");

		});
	}

	private void assertCoverageLimitRentalReimbursementAfterUpdateRreim(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			Coverage coverageLimitTransportation = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4);

			softly.assertThat(coverageLimitTransportation.availableLimits.get(0).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageLimitTransportation.availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageLimitTransportation.availableLimits.get(1).coverageLimit).isEqualTo("50/1500");
			softly.assertThat(coverageLimitTransportation.availableLimits.get(1).coverageLimitDisplay).isEqualTo("$50/$1,500");

		});
	}

	private void assertCoverageLimitLoan(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			List<CoverageLimit> availableLimitsLoan = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).availableLimits;
			softly.assertThat(availableLimitsLoan.get(0).coverageLimit).isEqualTo("0");
			softly.assertThat(availableLimitsLoan.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(availableLimitsLoan.get(1).coverageLimit).isEqualTo("1");
			softly.assertThat(availableLimitsLoan.get(1).coverageLimitDisplay).isEqualTo("Yes");
		});
	}

	private void assertCoverageLimitForCompColl(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			List<CoverageLimit> availableLimitsCompColl = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits;
			softly.assertThat(availableLimitsCompColl.get(0).coverageLimit).isEqualTo("-1");
			softly.assertThat(availableLimitsCompColl.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(availableLimitsCompColl.get(1).coverageLimit).isEqualTo("100");
			softly.assertThat(availableLimitsCompColl.get(1).coverageLimitDisplay).isEqualTo("$100");

			softly.assertThat(availableLimitsCompColl.get(2).coverageLimit).isEqualTo("250");
			softly.assertThat(availableLimitsCompColl.get(2).coverageLimitDisplay).isEqualTo("$250");

			softly.assertThat(availableLimitsCompColl.get(3).coverageLimit).isEqualTo("500");
			softly.assertThat(availableLimitsCompColl.get(3).coverageLimitDisplay).isEqualTo("$500");

			softly.assertThat(availableLimitsCompColl.get(4).coverageLimit).isEqualTo("750");
			softly.assertThat(availableLimitsCompColl.get(4).coverageLimitDisplay).isEqualTo("$750");

			softly.assertThat(availableLimitsCompColl.get(5).coverageLimit).isEqualTo("1000");
			softly.assertThat(availableLimitsCompColl.get(5).coverageLimitDisplay).isEqualTo("$1,000");
		});
	}

	private void assertCoverageLimitForCompCollLoanLease(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			List<CoverageLimit> availableLimitsCollLoan = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits;
			softly.assertThat(availableLimitsCollLoan.get(0).coverageLimit).isEqualTo("100");
			softly.assertThat(availableLimitsCollLoan.get(0).coverageLimitDisplay).isEqualTo("$100");

			softly.assertThat(availableLimitsCollLoan.get(1).coverageLimit).isEqualTo("250");
			softly.assertThat(availableLimitsCollLoan.get(1).coverageLimitDisplay).isEqualTo("$250");

			softly.assertThat(availableLimitsCollLoan.get(2).coverageLimit).isEqualTo("500");
			softly.assertThat(availableLimitsCollLoan.get(2).coverageLimitDisplay).isEqualTo("$500");

			softly.assertThat(availableLimitsCollLoan.get(3).coverageLimit).isEqualTo("750");
			softly.assertThat(availableLimitsCollLoan.get(3).coverageLimitDisplay).isEqualTo("$750");

			softly.assertThat(availableLimitsCollLoan.get(4).coverageLimit).isEqualTo("1000");
			softly.assertThat(availableLimitsCollLoan.get(4).coverageLimitDisplay).isEqualTo("$1,000");
		});
	}

	private void assertCoverageLimitFullGlassCov(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			Coverage coverageFullGlass = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2);
			softly.assertThat(coverageFullGlass.availableLimits.get(0).coverageLimit).isEqualTo("false");
			softly.assertThat(coverageFullGlass.availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageFullGlass.availableLimits.get(1).coverageLimit).isEqualTo("true");
			softly.assertThat(coverageFullGlass.availableLimits.get(1).coverageLimitDisplay).isEqualTo("Yes");
		});
	}

	private void assertCoverageLimitForBI(PolicyCoverageInfo coverageResponse, String state) {
		assertSoftly(softly -> {
			if ("VA".contains(state)) {
				List<CoverageLimit> availableLimits = coverageResponse.policyCoverages.get(0).availableLimits;

				softly.assertThat(availableLimits.get(0).coverageLimit).isEqualTo("25000/50000");
				softly.assertThat(availableLimits.get(0).coverageLimitDisplay).isEqualTo("$25,000/$50,000");

				softly.assertThat(availableLimits.get(1).coverageLimit).isEqualTo("50000/100000");
				softly.assertThat(availableLimits.get(1).coverageLimitDisplay).isEqualTo("$50,000/$100,000");

				softly.assertThat(availableLimits.get(2).coverageLimit).isEqualTo("100000/300000");
				softly.assertThat(availableLimits.get(2).coverageLimitDisplay).isEqualTo("$100,000/$300,000");

				softly.assertThat(availableLimits.get(3).coverageLimit).isEqualTo("250000/500000");
				softly.assertThat(availableLimits.get(3).coverageLimitDisplay).isEqualTo("$250,000/$500,000");

				softly.assertThat(availableLimits.get(4).coverageLimit).isEqualTo("300000/500000");
				softly.assertThat(availableLimits.get(4).coverageLimitDisplay).isEqualTo("$300,000/$500,000");

				softly.assertThat(availableLimits.get(5).coverageLimit).isEqualTo("500000/500000");
				softly.assertThat(availableLimits.get(5).coverageLimitDisplay).isEqualTo("$500,000/$500,000");

				softly.assertThat(availableLimits.get(6).coverageLimit).isEqualTo("500000/1000000");
				softly.assertThat(availableLimits.get(6).coverageLimitDisplay).isEqualTo("$500,000/$1,000,000");

				softly.assertThat(availableLimits.get(7).coverageLimit).isEqualTo("1000000/1000000");
				softly.assertThat(availableLimits.get(7).coverageLimitDisplay).isEqualTo("$1,000,000/$1,000,000");

			} else if ("AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY".contains(state)) {
				List<CoverageLimit> availableLimits = coverageResponse.policyCoverages.get(0).availableLimits;

				softly.assertThat(availableLimits.get(0).coverageLimit).isEqualTo("15000/30000");
				softly.assertThat(availableLimits.get(0).coverageLimitDisplay).isEqualTo("$15,000/$30,000");

				softly.assertThat(availableLimits.get(1).coverageLimit).isEqualTo("25000/50000");
				softly.assertThat(availableLimits.get(1).coverageLimitDisplay).isEqualTo("$25,000/$50,000");

				softly.assertThat(availableLimits.get(2).coverageLimit).isEqualTo("50000/100000");
				softly.assertThat(availableLimits.get(2).coverageLimitDisplay).isEqualTo("$50,000/$100,000");

				softly.assertThat(availableLimits.get(3).coverageLimit).isEqualTo("100000/300000");
				softly.assertThat(availableLimits.get(3).coverageLimitDisplay).isEqualTo("$100,000/$300,000");

				softly.assertThat(availableLimits.get(4).coverageLimit).isEqualTo("250000/500000");
				softly.assertThat(availableLimits.get(4).coverageLimitDisplay).isEqualTo("$250,000/$500,000");

				softly.assertThat(availableLimits.get(5).coverageLimit).isEqualTo("300000/500000");
				softly.assertThat(availableLimits.get(5).coverageLimitDisplay).isEqualTo("$300,000/$500,000");

				softly.assertThat(availableLimits.get(6).coverageLimit).isEqualTo("500000/500000");
				softly.assertThat(availableLimits.get(6).coverageLimitDisplay).isEqualTo("$500,000/$500,000");

				softly.assertThat(availableLimits.get(7).coverageLimit).isEqualTo("500000/1000000");
				softly.assertThat(availableLimits.get(7).coverageLimitDisplay).isEqualTo("$500,000/$1,000,000");

				softly.assertThat(availableLimits.get(8).coverageLimit).isEqualTo("1000000/1000000");
				softly.assertThat(availableLimits.get(8).coverageLimitDisplay).isEqualTo("$1,000,000/$1,000,000");

			}

		});
	}

	private void assertCoverageLimitForPD(PolicyCoverageInfo coverageResponse, String state) {
		assertSoftly(softly -> {
			if ("VA".contains(state)) {
				List<CoverageLimit> availableLimitsPD = coverageResponse.policyCoverages.get(1).availableLimits;

				softly.assertThat(availableLimitsPD.get(0).coverageLimit).isEqualTo("20000");
				softly.assertThat(availableLimitsPD.get(0).coverageLimitDisplay).isEqualTo("$20,000");

				softly.assertThat(availableLimitsPD.get(1).coverageLimit).isEqualTo("25000");
				softly.assertThat(availableLimitsPD.get(1).coverageLimitDisplay).isEqualTo("$25,000");

				softly.assertThat(availableLimitsPD.get(2).coverageLimit).isEqualTo("40000");
				softly.assertThat(availableLimitsPD.get(2).coverageLimitDisplay).isEqualTo("$40,000");

				softly.assertThat(availableLimitsPD.get(3).coverageLimit).isEqualTo("50000");
				softly.assertThat(availableLimitsPD.get(3).coverageLimitDisplay).isEqualTo("$50,000");

				softly.assertThat(availableLimitsPD.get(4).coverageLimit).isEqualTo("100000");
				softly.assertThat(availableLimitsPD.get(4).coverageLimitDisplay).isEqualTo("$100,000");

			} else if ("AZ, UT".contains(state)) {
				List<CoverageLimit> availableLimitsPD = coverageResponse.policyCoverages.get(1).availableLimits;

				softly.assertThat(availableLimitsPD.get(0).coverageLimit).isEqualTo("10000");
				softly.assertThat(availableLimitsPD.get(0).coverageLimitDisplay).isEqualTo("$10,000");

				softly.assertThat(availableLimitsPD.get(1).coverageLimit).isEqualTo("15000");
				softly.assertThat(availableLimitsPD.get(1).coverageLimitDisplay).isEqualTo("$15,000");

				softly.assertThat(availableLimitsPD.get(2).coverageLimit).isEqualTo("25000");
				softly.assertThat(availableLimitsPD.get(2).coverageLimitDisplay).isEqualTo("$25,000");

				softly.assertThat(availableLimitsPD.get(3).coverageLimit).isEqualTo("50000");
				softly.assertThat(availableLimitsPD.get(3).coverageLimitDisplay).isEqualTo("$50,000");

				softly.assertThat(availableLimitsPD.get(4).coverageLimit).isEqualTo("100000");
				softly.assertThat(availableLimitsPD.get(4).coverageLimitDisplay).isEqualTo("$100,000");
			}
		});
	}

	private void assertCoverageLimitForPDBI(PolicyCoverageInfo coverageResponse, String state) {
		assertSoftly(softly -> {
			if ("AZ, UT".contains(state)) {

				List<CoverageLimit> availableLimitsPDBI = coverageResponse.policyCoverages.get(1).availableLimits;

				softly.assertThat(availableLimitsPDBI.get(0).coverageLimit).isEqualTo("10000");
				softly.assertThat(availableLimitsPDBI.get(0).coverageLimitDisplay).isEqualTo("$10,000");

				softly.assertThat(availableLimitsPDBI.get(1).coverageLimit).isEqualTo("15000");
				softly.assertThat(availableLimitsPDBI.get(1).coverageLimitDisplay).isEqualTo("$15,000");

				softly.assertThat(availableLimitsPDBI.get(2).coverageLimit).isEqualTo("25000");
				softly.assertThat(availableLimitsPDBI.get(2).coverageLimitDisplay).isEqualTo("$25,000");

				softly.assertThat(availableLimitsPDBI.get(3).coverageLimit).isEqualTo("50000");
				softly.assertThat(availableLimitsPDBI.get(3).coverageLimitDisplay).isEqualTo("$50,000");

				softly.assertThat(availableLimitsPDBI.get(4).coverageLimit).isEqualTo("100000");
				softly.assertThat(availableLimitsPDBI.get(4).coverageLimitDisplay).isEqualTo("$100,000");

				softly.assertThat(availableLimitsPDBI.get(5).coverageLimit).isEqualTo("300000");
				softly.assertThat(availableLimitsPDBI.get(5).coverageLimitDisplay).isEqualTo("$300,000");

				softly.assertThat(availableLimitsPDBI.get(6).coverageLimit).isEqualTo("500000");
				softly.assertThat(availableLimitsPDBI.get(6).coverageLimitDisplay).isEqualTo("$500,000");

			} else if ("VA".contains(state)) {

				List<CoverageLimit> availableLimitsPDBI = coverageResponse.policyCoverages.get(1).availableLimits;

				softly.assertThat(availableLimitsPDBI.get(0).coverageLimit).isEqualTo("20000");
				softly.assertThat(availableLimitsPDBI.get(0).coverageLimitDisplay).isEqualTo("$20,000");

				softly.assertThat(availableLimitsPDBI.get(1).coverageLimit).isEqualTo("25000");
				softly.assertThat(availableLimitsPDBI.get(1).coverageLimitDisplay).isEqualTo("$25,000");

				softly.assertThat(availableLimitsPDBI.get(2).coverageLimit).isEqualTo("40000");
				softly.assertThat(availableLimitsPDBI.get(2).coverageLimitDisplay).isEqualTo("$40,000");

				softly.assertThat(availableLimitsPDBI.get(3).coverageLimit).isEqualTo("50000");
				softly.assertThat(availableLimitsPDBI.get(3).coverageLimitDisplay).isEqualTo("$50,000");

				softly.assertThat(availableLimitsPDBI.get(4).coverageLimit).isEqualTo("100000");
				softly.assertThat(availableLimitsPDBI.get(4).coverageLimitDisplay).isEqualTo("$100,000");

				softly.assertThat(availableLimitsPDBI.get(5).coverageLimit).isEqualTo("300000");
				softly.assertThat(availableLimitsPDBI.get(5).coverageLimitDisplay).isEqualTo("$300,000");

				softly.assertThat(availableLimitsPDBI.get(6).coverageLimit).isEqualTo("500000");
				softly.assertThat(availableLimitsPDBI.get(6).coverageLimitDisplay).isEqualTo("$500,000");
			}

		});
	}

	protected void pas14645_ViewCoveragesBiPd(PolicyType policyType) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		SearchPage.openPolicy(policyNumber);

		PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly ->
				viewCoveragesBiPd(policyCoverageResponse, softly)
		);

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo policyCoverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly ->
				viewCoveragesBiPd(policyCoverageEndorsementResponse, softly)
		);
	}

	private void viewCoveragesBiPd(PolicyCoverageInfo policyCoverageResponse, ETCSCoreSoftAssertions softly) {
		Coverage coverageLimit = policyCoverageResponse.policyCoverages.get(0);
		softly.assertThat(coverageLimit.coverageCd).isEqualTo("BI");
		softly.assertThat(coverageLimit.coverageDescription).isEqualTo("Bodily Injury Liability");
		softly.assertThat(coverageLimit.coverageLimit).isEqualTo("100000/300000");
		softly.assertThat(coverageLimit.coverageLimitDisplay).isEqualTo("$100,000/$300,000");
		softly.assertThat(coverageLimit.coverageType).isEqualTo("Per Person/Per Accident");
		softly.assertThat(coverageLimit.customerDisplayed).isEqualTo(true);
		softly.assertThat(coverageLimit.canChangeCoverage).isEqualTo(true);

		softly.assertThat(coverageLimit.availableLimits.get(0).coverageLimit).isEqualTo("25000/50000");
		softly.assertThat(coverageLimit.availableLimits.get(0).coverageLimitDisplay).isEqualTo("$25,000/$50,000");

		softly.assertThat(coverageLimit.availableLimits.get(1).coverageLimit).isEqualTo("50000/100000");
		softly.assertThat(coverageLimit.availableLimits.get(1).coverageLimitDisplay).isEqualTo("$50,000/$100,000");

		softly.assertThat(coverageLimit.availableLimits.get(2).coverageLimit).isEqualTo("100000/300000");
		softly.assertThat(coverageLimit.availableLimits.get(2).coverageLimitDisplay).isEqualTo("$100,000/$300,000");

		softly.assertThat(coverageLimit.availableLimits.get(3).coverageLimit).isEqualTo("250000/500000");
		softly.assertThat(coverageLimit.availableLimits.get(3).coverageLimitDisplay).isEqualTo("$250,000/$500,000");

		softly.assertThat(coverageLimit.availableLimits.get(4).coverageLimit).isEqualTo("300000/500000");
		softly.assertThat(coverageLimit.availableLimits.get(4).coverageLimitDisplay).isEqualTo("$300,000/$500,000");

		softly.assertThat(coverageLimit.availableLimits.get(5).coverageLimit).isEqualTo("500000/500000");
		softly.assertThat(coverageLimit.availableLimits.get(5).coverageLimitDisplay).isEqualTo("$500,000/$500,000");

		softly.assertThat(coverageLimit.availableLimits.get(6).coverageLimit).isEqualTo("500000/1000000");
		softly.assertThat(coverageLimit.availableLimits.get(6).coverageLimitDisplay).isEqualTo("$500,000/$1,000,000");

		softly.assertThat(coverageLimit.availableLimits.get(7).coverageLimit).isEqualTo("1000000/1000000");
		softly.assertThat(coverageLimit.availableLimits.get(7).coverageLimitDisplay).isEqualTo("$1,000,000/$1,000,000");

		Coverage coveragePD = policyCoverageResponse.policyCoverages.get(1);
		softly.assertThat(coveragePD.coverageCd).isEqualTo("PD");
		softly.assertThat(coveragePD.coverageDescription).isEqualTo("Property Damage Liability");
		softly.assertThat(coveragePD.coverageLimit).isEqualTo("50000");
		softly.assertThat(coveragePD.coverageLimitDisplay).isEqualTo("$50,000");
		softly.assertThat(coveragePD.coverageType).isEqualTo("Per Accident");
		softly.assertThat(coveragePD.customerDisplayed).isEqualTo(true);
		softly.assertThat(coveragePD.canChangeCoverage).isEqualTo(true);

		softly.assertThat(coveragePD.availableLimits.get(0).coverageLimit).isEqualTo("20000");
		softly.assertThat(coveragePD.availableLimits.get(0).coverageLimitDisplay).isEqualTo("$20,000");

		softly.assertThat(coveragePD.availableLimits.get(1).coverageLimit).isEqualTo("25000");
		softly.assertThat(coveragePD.availableLimits.get(1).coverageLimitDisplay).isEqualTo("$25,000");

		softly.assertThat(coveragePD.availableLimits.get(2).coverageLimit).isEqualTo("40000");
		softly.assertThat(coveragePD.availableLimits.get(2).coverageLimitDisplay).isEqualTo("$40,000");

		softly.assertThat(coveragePD.availableLimits.get(3).coverageLimit).isEqualTo("50000");
		softly.assertThat(coveragePD.availableLimits.get(3).coverageLimitDisplay).isEqualTo("$50,000");

		softly.assertThat(coveragePD.availableLimits.get(4).coverageLimit).isEqualTo("100000");
		softly.assertThat(coveragePD.availableLimits.get(4).coverageLimitDisplay).isEqualTo("$100,000");

		softly.assertThat(coveragePD.availableLimits.get(5).coverageLimit).isEqualTo("300000");
		softly.assertThat(coveragePD.availableLimits.get(5).coverageLimitDisplay).isEqualTo("$300,000");
	}

	protected void pas11741_ViewVehicleLevelCoverages(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		SearchPage.openPolicy(policyNumber);

		// view vehicle coverage to get OID
		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		String oid = viewVehicleResponse.vehicleList.get(0).oid;

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//add  vehicle1
		String purchaseDate = "2012-02-21";
		String vin = "SHHFK7H41JU201444";
		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
		assertThat(addVehicle.oid).isNotEmpty();
		String newVehicleOid = addVehicle.oid;

		VehicleUpdateDto updateVehicleUsageRequest = new VehicleUpdateDto();
		updateVehicleUsageRequest.vehicleOwnership = new VehicleOwnership();
		updateVehicleUsageRequest.usage = "Pleasure";
		updateVehicleUsageRequest.vehicleOwnership.ownership = "LSD";
		updateVehicleUsageRequest.registeredOwner = true;
		Vehicle updateVehicleUsageResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleUsageRequest);
		assertThat(updateVehicleUsageResponse.usage).isEqualTo("Pleasure");

		SearchPage.openPolicy(policyNumber);

		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		//vehicle1
		Dollar comprehensiveDeductible = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)");
		Dollar collisionDeductible = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)");
		String fullSafetyGlassVeh = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "");
		String loanLeaseCov = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), " (+$0.00)");
		Dollar transportationExpense = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)");
		String towingAndLabor = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");
		Dollar excessElectronicEquipment = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PolicyCoverageInfo coverageResponseV = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, oid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesVehicle = coverageResponseV.vehicleLevelCoverages.get(0).coverages;
			softly.assertThat(coveragesVehicle.get(0).coverageCd).isEqualTo("COMPDED");
			softly.assertThat(coveragesVehicle.get(0).coverageDescription).isEqualTo("Other Than Collision");
			softly.assertThat(new Dollar(coveragesVehicle.get(0).coverageLimit)).isEqualTo(comprehensiveDeductible);
			softly.assertThat(new Dollar(coveragesVehicle.get(0).coverageLimitDisplay)).isEqualTo(comprehensiveDeductible);
			softly.assertThat(coveragesVehicle.get(0).coverageType).isEqualTo("Deductible");
			softly.assertThat(coveragesVehicle.get(0).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesVehicle.get(0).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageResponseV);

			softly.assertThat(coveragesVehicle.get(1).coverageCd).isEqualTo("COLLDED");
			softly.assertThat(coveragesVehicle.get(1).coverageDescription).isEqualTo("Collision Deductible");
			softly.assertThat(new Dollar(coveragesVehicle.get(1).coverageLimit)).isEqualTo(collisionDeductible);
			softly.assertThat(new Dollar(coveragesVehicle.get(1).coverageLimitDisplay)).isEqualTo(collisionDeductible);
			softly.assertThat(coveragesVehicle.get(1).coverageType).isEqualTo("Deductible");
			softly.assertThat(coveragesVehicle.get(1).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesVehicle.get(1).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageResponseV);

			softly.assertThat(coveragesVehicle.get(2).coverageCd).isEqualTo("GLASS");
			softly.assertThat(coveragesVehicle.get(2).coverageDescription).isEqualTo("Full Safety Glass");
			softly.assertThat(coveragesVehicle.get(2).coverageLimit).isEqualTo("false");
			softly.assertThat(coveragesVehicle.get(2).coverageLimitDisplay).isEqualTo(fullSafetyGlassVeh);
			softly.assertThat(coveragesVehicle.get(2).coverageType).isEqualTo("None");
			softly.assertThat(coveragesVehicle.get(2).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesVehicle.get(2).canChangeCoverage).isEqualTo(true);
			assertCoverageLimitFullGlassCov(coverageResponseV);

			softly.assertThat(coveragesVehicle.get(3).coverageCd).isEqualTo("LOAN");
			softly.assertThat(coveragesVehicle.get(3).coverageDescription).isEqualTo("Auto Loan/Lease Coverage");
			softly.assertThat(coveragesVehicle.get(3).coverageLimit).isEqualTo("0");
			softly.assertThat(coveragesVehicle.get(3).coverageLimitDisplay).isEqualTo(loanLeaseCov);
			softly.assertThat(coveragesVehicle.get(3).coverageType).isEqualTo("None");
			softly.assertThat(coveragesVehicle.get(3).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesVehicle.get(3).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitLoan(coverageResponseV);

			softly.assertThat(coveragesVehicle.get(4).coverageCd).isEqualTo("RREIM");
			softly.assertThat(coveragesVehicle.get(4).coverageDescription).isEqualTo("Transportation Expense");
			softly.assertThat(new Dollar(coveragesVehicle.get(4).coverageLimit)).isEqualTo(transportationExpense);
			softly.assertThat(coveragesVehicle.get(4).coverageLimitDisplay).contains(transportationExpense.toString().replace(".00", ""));
			softly.assertThat(coveragesVehicle.get(4).coverageType).isEqualTo("Per Occurrence");
			softly.assertThat(coveragesVehicle.get(4).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesVehicle.get(4).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTransportationExpense(coverageResponseV, false);

			softly.assertThat(coveragesVehicle.get(5).coverageCd).isEqualTo("TOWINGLABOR");
			softly.assertThat(coveragesVehicle.get(5).coverageDescription).isEqualTo("Towing and Labor Coverage");
			softly.assertThat(coveragesVehicle.get(5).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coveragesVehicle.get(5).coverageLimitDisplay).isEqualTo(towingAndLabor);
			softly.assertThat(coveragesVehicle.get(5).coverageType).isEqualTo("Per Disablement/Maximum");
			softly.assertThat(coveragesVehicle.get(5).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesVehicle.get(5).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTowingLabor(coverageResponseV);

			softly.assertThat(coveragesVehicle.get(6).coverageCd).isEqualTo("SPECEQUIP");
			softly.assertThat(coveragesVehicle.get(6).coverageDescription).isEqualTo("Excess Electronic Equipment");
			softly.assertThat(new Dollar(coveragesVehicle.get(6).coverageLimit)).isEqualTo(excessElectronicEquipment);
			softly.assertThat(coveragesVehicle.get(6).coverageLimitDisplay).isEqualTo("$1,000.00");
			softly.assertThat(coveragesVehicle.get(6).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesVehicle.get(6).canChangeCoverage).isEqualTo(false);

			softly.assertThat(coveragesVehicle.get(7).coverageCd).isEqualTo("NEWCAR");
			softly.assertThat(coveragesVehicle.get(7).coverageDescription).isEqualTo("New Car Added Protection");
			softly.assertThat(coveragesVehicle.get(7).coverageLimit).isEqualTo("false");
			softly.assertThat(coveragesVehicle.get(7).coverageLimitDisplay).isEqualTo("No");
			softly.assertThat(coveragesVehicle.get(7).customerDisplayed).isEqualTo(false);
			softly.assertThat(coveragesVehicle.get(7).canChangeCoverage).isEqualTo(false);

		});

		// Vehicle2
		Dollar comprehensiveDeductiblePendingV = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)");
		Dollar collisionDeductiblePendingV = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)");
		String fullSafetyGlassVehPendingV = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel());
		String loanLeaseCovPendingV = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), " (+$0.00)");
		Dollar transportationExpensePendingV = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)");
		String towingAndLaborPendingV = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");
		Dollar excessElectronicEquipmentPendingV = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PolicyCoverageInfo coverageResponse = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesVehicle2 = coverageResponse.vehicleLevelCoverages.get(0).coverages;
			softly.assertThat(coveragesVehicle2.get(0).coverageCd).isEqualTo("COMPDED");
			softly.assertThat(coveragesVehicle2.get(0).coverageDescription).isEqualTo("Other Than Collision");
			softly.assertThat(new Dollar(coveragesVehicle2.get(0).coverageLimit)).isEqualTo(comprehensiveDeductiblePendingV);
			softly.assertThat(new Dollar(coveragesVehicle2.get(0).coverageLimitDisplay)).isEqualTo(comprehensiveDeductiblePendingV);
			softly.assertThat(coveragesVehicle2.get(0).coverageType).isEqualTo("Deductible");
			softly.assertThat(coveragesVehicle2.get(0).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesVehicle2.get(0).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			softly.assertThat(coveragesVehicle2.get(1).coverageCd).isEqualTo("COLLDED");
			softly.assertThat(coveragesVehicle2.get(1).coverageDescription).isEqualTo("Collision Deductible");
			softly.assertThat(new Dollar(coveragesVehicle2.get(1).coverageLimit)).isEqualTo(collisionDeductiblePendingV);
			softly.assertThat(new Dollar(coveragesVehicle2.get(1).coverageLimitDisplay)).isEqualTo(collisionDeductiblePendingV);
			softly.assertThat(coveragesVehicle2.get(1).coverageType).isEqualTo("Deductible");
			softly.assertThat(coveragesVehicle2.get(1).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesVehicle2.get(1).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			softly.assertThat(coveragesVehicle2.get(2).coverageCd).isEqualTo("GLASS");
			softly.assertThat(coveragesVehicle2.get(2).coverageDescription).isEqualTo("Full Safety Glass");
			softly.assertThat(coveragesVehicle2.get(2).coverageLimit).isEqualTo("false");
			softly.assertThat(coveragesVehicle2.get(2).coverageLimitDisplay).isEqualTo(fullSafetyGlassVehPendingV);
			softly.assertThat(coveragesVehicle2.get(2).coverageType).isEqualTo("None");
			softly.assertThat(coveragesVehicle2.get(2).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesVehicle2.get(2).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitFullGlassCov(coverageResponse);

			softly.assertThat(coveragesVehicle2.get(3).coverageCd).isEqualTo("LOAN");
			softly.assertThat(coveragesVehicle2.get(3).coverageDescription).isEqualTo("Auto Loan/Lease Coverage");
			softly.assertThat(coveragesVehicle2.get(3).coverageLimit).isEqualTo("0");
			softly.assertThat(coveragesVehicle2.get(3).coverageLimitDisplay).isEqualTo(loanLeaseCovPendingV);
			softly.assertThat(coveragesVehicle2.get(3).coverageType).isEqualTo("None");
			softly.assertThat(coveragesVehicle2.get(3).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesVehicle2.get(3).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitLoan(coverageResponse);

			softly.assertThat(coveragesVehicle2.get(4).coverageCd).isEqualTo("RREIM");
			softly.assertThat(coveragesVehicle2.get(4).coverageDescription).isEqualTo("Transportation Expense");
			softly.assertThat(new Dollar(coveragesVehicle2.get(4).coverageLimit)).isEqualTo(transportationExpensePendingV);
			softly.assertThat(coveragesVehicle2.get(4).coverageLimitDisplay).contains(transportationExpensePendingV.toString().replace(".00", ""));
			softly.assertThat(coveragesVehicle2.get(4).coverageType).isEqualTo("Per Occurrence");
			softly.assertThat(coveragesVehicle2.get(4).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesVehicle2.get(4).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTransportationExpense(coverageResponse, false);

			softly.assertThat(coveragesVehicle2.get(5).coverageCd).isEqualTo("TOWINGLABOR");
			softly.assertThat(coveragesVehicle2.get(5).coverageDescription).isEqualTo("Towing and Labor Coverage");
			softly.assertThat(coveragesVehicle2.get(5).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coveragesVehicle2.get(5).coverageLimitDisplay).isEqualTo(towingAndLaborPendingV);
			softly.assertThat(coveragesVehicle2.get(5).coverageType).isEqualTo("Per Disablement/Maximum");
			softly.assertThat(coveragesVehicle2.get(5).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesVehicle2.get(5).canChangeCoverage).isEqualTo(true);

			assertCoverageLimitTowingLabor(coverageResponse);

			softly.assertThat(coveragesVehicle2.get(6).coverageCd).isEqualTo("SPECEQUIP");
			softly.assertThat(coveragesVehicle2.get(6).coverageDescription).isEqualTo("Excess Electronic Equipment");
			softly.assertThat(new Dollar(coveragesVehicle2.get(6).coverageLimit)).isEqualTo(excessElectronicEquipmentPendingV);
			softly.assertThat(coveragesVehicle2.get(6).coverageLimitDisplay).isEqualTo("$1,000.00");
			softly.assertThat(coveragesVehicle2.get(6).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesVehicle2.get(6).canChangeCoverage).isEqualTo(false);

			softly.assertThat(coveragesVehicle2.get(7).coverageCd).isEqualTo("NEWCAR");
			softly.assertThat(coveragesVehicle2.get(7).coverageDescription).isEqualTo("New Car Added Protection");
			softly.assertThat(coveragesVehicle2.get(7).coverageLimit).isEqualTo("false");
			softly.assertThat(coveragesVehicle2.get(7).coverageLimitDisplay).isEqualTo("No");
			softly.assertThat(coveragesVehicle2.get(7).customerDisplayed).isEqualTo(false);
			softly.assertThat(coveragesVehicle2.get(7).canChangeCoverage).isEqualTo(false);

			softly.assertThat(coveragesVehicle2.get(8).coverageCd).isEqualTo("WL");
			softly.assertThat(coveragesVehicle2.get(8).coverageDescription).isEqualTo("Waive Liability");
			softly.assertThat(coveragesVehicle2.get(8).customerDisplayed).isEqualTo(false);
		});

		//Change coverages v1
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "1,000");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "750");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "Yes");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), "Yes");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), "900");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), "50");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "1,500");
		// change cov 2
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(2, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "500");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(2, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "750");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(2, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "Yes");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(2, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), "Yes");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(2, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), "900");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(2, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), "50");
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(2, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "1,500");

		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		Dollar comprehensiveDeductiblePendingV1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)");
		Dollar collisionDeductiblePendingV1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)");
		String fullSafetyGlassVehPendingV1 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "");
		String loanLeaseCovPendingV1 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), " (+$0.00)");
		Dollar transportationExpensePendingV1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)");
		String towingAndLaborPendingV1 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");
		Dollar excessElectronicEquipmentPendingV1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PolicyCoverageInfo coverageEndorsementResponseV1 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, oid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages;
			softly.assertThat(coveragesV1.get(0).coverageCd).isEqualTo("COMPDED");
			softly.assertThat(coveragesV1.get(0).coverageDescription).isEqualTo("Other Than Collision");
			softly.assertThat(new Dollar(coveragesV1.get(0).coverageLimit)).isEqualTo(comprehensiveDeductiblePendingV1);
			softly.assertThat(new Dollar(coveragesV1.get(0).coverageLimitDisplay)).isEqualTo(comprehensiveDeductiblePendingV1);
			softly.assertThat(coveragesV1.get(0).coverageType).isEqualTo("Deductible");
			softly.assertThat(coveragesV1.get(0).customerDisplayed).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageEndorsementResponseV1);

			softly.assertThat(coveragesV1.get(1).coverageCd).isEqualTo("COLLDED");
			softly.assertThat(coveragesV1.get(1).coverageDescription).isEqualTo("Collision Deductible");
			softly.assertThat(new Dollar(coveragesV1.get(1).coverageLimit)).isEqualTo(collisionDeductiblePendingV1);
			softly.assertThat(new Dollar(coveragesV1.get(1).coverageLimitDisplay)).isEqualTo(collisionDeductiblePendingV1);
			softly.assertThat(coveragesV1.get(1).coverageType).isEqualTo("Deductible");
			softly.assertThat(coveragesV1.get(1).customerDisplayed).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageEndorsementResponseV1);

			softly.assertThat(coveragesV1.get(2).coverageCd).isEqualTo("GLASS");
			softly.assertThat(coveragesV1.get(2).coverageDescription).isEqualTo("Full Safety Glass");
			softly.assertThat(coveragesV1.get(2).coverageLimit).isEqualTo("true");
			softly.assertThat(coveragesV1.get(2).coverageLimitDisplay).isEqualTo(fullSafetyGlassVehPendingV1);
			softly.assertThat(coveragesV1.get(2).coverageType).isEqualTo("None");
			softly.assertThat(coveragesV1.get(2).customerDisplayed).isEqualTo(true);

			assertCoverageLimitFullGlassCov(coverageEndorsementResponseV1);

			softly.assertThat(coveragesV1.get(3).coverageCd).isEqualTo("LOAN");
			softly.assertThat(coveragesV1.get(3).coverageDescription).isEqualTo("Auto Loan/Lease Coverage");
			softly.assertThat(coveragesV1.get(3).coverageLimit).isEqualTo("1");
			softly.assertThat(coveragesV1.get(3).coverageLimitDisplay).isEqualTo(loanLeaseCovPendingV1);
			softly.assertThat(coveragesV1.get(3).coverageType).isEqualTo("None");
			softly.assertThat(coveragesV1.get(3).customerDisplayed).isEqualTo(true);

			assertCoverageLimitLoan(coverageEndorsementResponseV1);

			softly.assertThat(coveragesV1.get(4).coverageCd).isEqualTo("RREIM");
			softly.assertThat(coveragesV1.get(4).coverageDescription).isEqualTo("Transportation Expense");
			softly.assertThat(new Dollar(coveragesV1.get(4).coverageLimit)).isEqualTo(transportationExpensePendingV1);
			softly.assertThat(new Dollar(coveragesV1.get(4).coverageLimitDisplay)).isEqualTo(transportationExpensePendingV1);
			softly.assertThat(coveragesV1.get(4).coverageType).isEqualTo("Per Occurrence");
			softly.assertThat(coveragesV1.get(4).customerDisplayed).isEqualTo(true);

			assertCoverageLimitTransportationExpense(coverageEndorsementResponseV1, true);

			softly.assertThat(coveragesV1.get(5).coverageCd).isEqualTo("TOWINGLABOR");
			softly.assertThat(coveragesV1.get(5).coverageDescription).isEqualTo("Towing and Labor Coverage");
			softly.assertThat(coveragesV1.get(5).coverageLimit).isEqualTo("50/300");
			softly.assertThat(coveragesV1.get(5).coverageLimitDisplay.replace("$", "")).isEqualTo(towingAndLaborPendingV1);
			softly.assertThat(coveragesV1.get(5).coverageType).isEqualTo("Per Disablement/Maximum");
			softly.assertThat(coveragesV1.get(5).customerDisplayed).isEqualTo(true);

			assertCoverageLimitTowingLabor(coverageEndorsementResponseV1);

			softly.assertThat(coveragesV1.get(6).coverageCd).isEqualTo("SPECEQUIP");
			softly.assertThat(coveragesV1.get(6).coverageDescription).isEqualTo("Excess Electronic Equipment");
			softly.assertThat(new Dollar(coveragesV1.get(6).coverageLimit)).isEqualTo(excessElectronicEquipmentPendingV1);
			softly.assertThat(coveragesV1.get(6).coverageLimitDisplay).isEqualTo("$1,500.00");
			softly.assertThat(coveragesV1.get(6).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesV1.get(6).canChangeCoverage).isEqualTo(false);

			softly.assertThat(coveragesV1.get(7).coverageCd).isEqualTo("NEWCAR");
			softly.assertThat(coveragesV1.get(7).coverageDescription).isEqualTo("New Car Added Protection");
			softly.assertThat(new Dollar(coveragesV1.get(7).coverageLimit)).isEqualTo("false");
			softly.assertThat(coveragesV1.get(7).coverageLimitDisplay).isEqualTo("No");
			softly.assertThat(coveragesV1.get(7).customerDisplayed).isEqualTo(false);
			softly.assertThat(coveragesV1.get(7).canChangeCoverage).isEqualTo(false);

		});

		//vehicle2
		Dollar comprehensiveDeductiblePendingV2 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)");
		Dollar collisionDeductiblePendingV2 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)");
		String fullSafetyGlassVehPendingV2 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel());
		String loanLeaseCovPendingV2 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), " (+$0.00)");
		Dollar transportationExpensePendingV2 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)");
		String towingAndLaborPendingV2 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");
		Dollar excessElectronicEquipmentPendingV2 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PolicyCoverageInfo coverageEndorsementResponsePendingV2 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesVehicle2 = coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages;
			softly.assertThat(coveragesVehicle2.get(0).coverageCd).isEqualTo("COMPDED");
			softly.assertThat(coveragesVehicle2.get(0).coverageDescription).isEqualTo("Other Than Collision");
			softly.assertThat(new Dollar(coveragesVehicle2.get(0).coverageLimit)).isEqualTo(comprehensiveDeductiblePendingV2);
			softly.assertThat(new Dollar(coveragesVehicle2.get(0).coverageLimitDisplay)).isEqualTo(comprehensiveDeductiblePendingV2);
			softly.assertThat(coveragesVehicle2.get(0).coverageType).isEqualTo("Deductible");
			softly.assertThat(coveragesVehicle2.get(0).customerDisplayed).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageEndorsementResponsePendingV2);

			softly.assertThat(coveragesVehicle2.get(1).coverageCd).isEqualTo("COLLDED");
			softly.assertThat(coveragesVehicle2.get(1).coverageDescription).isEqualTo("Collision Deductible");
			softly.assertThat(new Dollar(coveragesVehicle2.get(1).coverageLimit)).isEqualTo(collisionDeductiblePendingV2);
			softly.assertThat(new Dollar(coveragesVehicle2.get(1).coverageLimitDisplay)).isEqualTo(collisionDeductiblePendingV2);
			softly.assertThat(coveragesVehicle2.get(1).coverageType).isEqualTo("Deductible");
			softly.assertThat(coveragesVehicle2.get(1).customerDisplayed).isEqualTo(true);

			assertCoverageLimitForCompCollLoanLease(coverageEndorsementResponsePendingV2);

			softly.assertThat(coveragesVehicle2.get(2).coverageCd).isEqualTo("GLASS");
			softly.assertThat(coveragesVehicle2.get(2).coverageDescription).isEqualTo("Full Safety Glass");
			softly.assertThat(coveragesVehicle2.get(2).coverageLimit).isEqualTo("true");
			softly.assertThat(coveragesVehicle2.get(2).coverageLimitDisplay).isEqualTo(fullSafetyGlassVehPendingV2);
			softly.assertThat(coveragesVehicle2.get(2).coverageType).isEqualTo("None");
			softly.assertThat(coveragesVehicle2.get(2).customerDisplayed).isEqualTo(true);

			assertCoverageLimitFullGlassCov(coverageEndorsementResponsePendingV2);

			softly.assertThat(coveragesVehicle2.get(3).coverageCd).isEqualTo("LOAN");
			softly.assertThat(coveragesVehicle2.get(3).coverageDescription).isEqualTo("Auto Loan/Lease Coverage");
			softly.assertThat(coveragesVehicle2.get(3).coverageLimit).isEqualTo("1");
			softly.assertThat(coveragesVehicle2.get(3).coverageLimitDisplay).isEqualTo(loanLeaseCovPendingV2);
			softly.assertThat(coveragesVehicle2.get(3).coverageType).isEqualTo("None");
			softly.assertThat(coveragesVehicle2.get(3).customerDisplayed).isEqualTo(true);

			assertCoverageLimitLoan(coverageEndorsementResponsePendingV2);

			softly.assertThat(coveragesVehicle2.get(4).coverageCd).isEqualTo("RREIM");
			softly.assertThat(coveragesVehicle2.get(4).coverageDescription).isEqualTo("Transportation Expense");
			softly.assertThat(new Dollar(coveragesVehicle2.get(4).coverageLimit)).isEqualTo(transportationExpensePendingV2);
			softly.assertThat(new Dollar(coveragesVehicle2.get(4).coverageLimitDisplay)).isEqualTo(transportationExpensePendingV2);
			softly.assertThat(coveragesVehicle2.get(4).coverageType).isEqualTo("Per Occurrence");
			softly.assertThat(coveragesVehicle2.get(4).customerDisplayed).isEqualTo(true);

			assertCoverageLimitTransportationExpense(coverageEndorsementResponsePendingV2, true);

			softly.assertThat(coveragesVehicle2.get(5).coverageCd).isEqualTo("TOWINGLABOR");
			softly.assertThat(coveragesVehicle2.get(5).coverageDescription).isEqualTo("Towing and Labor Coverage");
			softly.assertThat(coveragesVehicle2.get(5).coverageLimit).isEqualTo("50/300");
			softly.assertThat(coveragesVehicle2.get(5).coverageLimitDisplay.replace("$", "")).isEqualTo(towingAndLaborPendingV2);
			softly.assertThat(coveragesVehicle2.get(5).coverageType).isEqualTo("Per Disablement/Maximum");
			softly.assertThat(coveragesVehicle2.get(5).customerDisplayed).isEqualTo(true);

			assertCoverageLimitTransportationExpense(coverageEndorsementResponsePendingV2, true);

			softly.assertThat(coveragesVehicle2.get(6).coverageCd).isEqualTo("SPECEQUIP");
			softly.assertThat(coveragesVehicle2.get(6).coverageDescription).isEqualTo("Excess Electronic Equipment");
			softly.assertThat(new Dollar(coveragesVehicle2.get(6).coverageLimit)).isEqualTo(excessElectronicEquipmentPendingV2);
			softly.assertThat(coveragesVehicle2.get(6).coverageLimitDisplay).isEqualTo("$1,500.00");
			softly.assertThat(coveragesVehicle2.get(6).customerDisplayed).isEqualTo(true);
			softly.assertThat(coveragesVehicle2.get(6).canChangeCoverage).isEqualTo(false);

			softly.assertThat(coveragesVehicle2.get(7).coverageCd).isEqualTo("NEWCAR");
			softly.assertThat(coveragesVehicle2.get(7).coverageDescription).isEqualTo("New Car Added Protection");
			softly.assertThat(new Dollar(coveragesVehicle2.get(7).coverageLimit)).isEqualTo("false");
			softly.assertThat(coveragesVehicle2.get(7).coverageLimitDisplay).isEqualTo("No");
			softly.assertThat(coveragesVehicle2.get(7).customerDisplayed).isEqualTo(false);
			softly.assertThat(coveragesVehicle2.get(7).canChangeCoverage).isEqualTo(false);
		});
	}

	protected void pas14536_TransportationExpensePart1Body(PolicyType policyType, ETCSCoreSoftAssertions softly) {
		mainApp().open();
		createCustomerIndividual();
		createQuote();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		//Remove Collision Deductible and issue
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION).setValueContains("No Coverage");
		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		String policyNumber = testEValueDiscount.simplifiedQuoteIssue();

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//Add first vehicle
		String purchaseDate = "2013-02-22";
		String vin1 = "1HGFA16526L081415";
		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin1);
		assertThat(addVehicle.oid).isNotEmpty();
		String oid1 = addVehicle.oid;
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, oid1);

		PolicyCoverageInfo coverageResponse1 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, oid1, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

		//Transportation Expense
		List<Coverage> coveragesVehicle = coverageResponse1.vehicleLevelCoverages.get(0).coverages;
		softly.assertThat(coveragesVehicle.get(4).coverageCd).isEqualTo("RREIM");
		softly.assertThat(coveragesVehicle.get(4).coverageDescription).isEqualTo("Transportation Expense");
		//Bug PAS-15473: Transportation Expense: coverageLimit is not displaying for new added vehicle
		softly.assertThat(coveragesVehicle.get(4).coverageLimit).isEqualTo("600");
		softly.assertThat(coveragesVehicle.get(4).coverageLimitDisplay).isEqualTo("$600");
		softly.assertThat(coveragesVehicle.get(4).coverageType).isEqualTo("Per Occurrence");
		softly.assertThat(coveragesVehicle.get(4).customerDisplayed).isEqualTo(true);
		softly.assertThat(coveragesVehicle.get(4).canChangeCoverage).isEqualTo(true);
		assertCoverageLimitTransportationExpense(coverageResponse1, false);

		//Other Than Collision
		softly.assertThat(coveragesVehicle.get(0).coverageCd).isEqualTo("COMPDED");
		softly.assertThat(coveragesVehicle.get(0).coverageDescription).isEqualTo("Other Than Collision");
		softly.assertThat(coveragesVehicle.get(0).coverageLimit).isEqualTo("250");
		softly.assertThat(coveragesVehicle.get(0).coverageLimitDisplay).isEqualTo("$250");
		softly.assertThat(coveragesVehicle.get(0).coverageType).isEqualTo("Deductible");
		softly.assertThat(coveragesVehicle.get(0).customerDisplayed).isEqualTo(true);
		softly.assertThat(coveragesVehicle.get(0).canChangeCoverage).isEqualTo(true);
		assertCoverageLimitForCompColl(coverageResponse1);

		helperMiniServices.endorsementRateAndBind(policyNumber);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
	}

	protected void pas14536_TransportationExpensePart2Body(PolicyType policyType, ETCSCoreSoftAssertions softly) {
		mainApp().open();
		createCustomerIndividual();
		createQuote();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		//Remove Collision Deductible and issue
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE).setValueContains("$1,200");
		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		String policyNumber = testEValueDiscount.simplifiedQuoteIssue();

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//Add first vehicle
		String purchaseDate = "2013-02-22";
		String vin1 = "1HGFA16526L081415";
		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin1);
		assertThat(addVehicle.oid).isNotEmpty();
		String oid1 = addVehicle.oid;
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, oid1);

		String coverageCd = "COMPDED";
		String availableLimits1 = "-1";
		String availableLimits2 = "100";

		//Remove COMPDED coverage and check Transportation Expense
		PolicyCoverageInfo updateCoverageResponse1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, availableLimits1), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

		List<Coverage> coveragesVehicle = updateCoverageResponse1.vehicleLevelCoverages.get(0).coverages;
		softly.assertThat(coveragesVehicle.get(4).coverageCd).isEqualTo("RREIM");
		softly.assertThat(coveragesVehicle.get(4).coverageDescription).isEqualTo("Transportation Expense");
		softly.assertThat(coveragesVehicle.get(4).coverageLimit).isEqualTo("0");
		softly.assertThat(coveragesVehicle.get(4).coverageType).isEqualTo("Per Occurrence");
		softly.assertThat(coveragesVehicle.get(4).customerDisplayed).isEqualTo(true);
		softly.assertThat(coveragesVehicle.get(4).canChangeCoverage).isEqualTo(false);

		List<CoverageLimit> availableLimits = coveragesVehicle.get(4).availableLimits;
		softly.assertThat(availableLimits.get(0).coverageLimit).isEqualTo("600");
		softly.assertThat(availableLimits.get(0).coverageLimitDisplay).isEqualTo("$600 (Included)");
		softly.assertThat(availableLimits.get(1).coverageLimit).isEqualTo("1200");
		softly.assertThat(availableLimits.get(1).coverageLimitDisplay).isEqualTo("$1,200");

		//Other Than Collision
		softly.assertThat(coveragesVehicle.get(0).coverageCd).isEqualTo("COMPDED");
		softly.assertThat(coveragesVehicle.get(0).coverageDescription).isEqualTo("Other Than Collision");
		softly.assertThat(coveragesVehicle.get(0).coverageLimit).isEqualTo("-1");
		softly.assertThat(coveragesVehicle.get(0).coverageLimitDisplay).isEqualTo("No Coverage");
		softly.assertThat(coveragesVehicle.get(0).coverageType).isEqualTo("Deductible");
		softly.assertThat(coveragesVehicle.get(0).customerDisplayed).isEqualTo(true);
		softly.assertThat(coveragesVehicle.get(0).canChangeCoverage).isEqualTo(true);
		assertCoverageLimitForCompColl(updateCoverageResponse1);

		//Add COMPDED coverage again and check Transportation Expense
		PolicyCoverageInfo updateCoverageResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, availableLimits2), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		List<Coverage> coveragesVehicle2 = updateCoverageResponse2.vehicleLevelCoverages.get(0).coverages;
		softly.assertThat(coveragesVehicle2.get(4).coverageCd).isEqualTo("RREIM");
		softly.assertThat(coveragesVehicle2.get(4).coverageDescription).isEqualTo("Transportation Expense");
		softly.assertThat(coveragesVehicle2.get(4).coverageLimit).isEqualTo("600");
		softly.assertThat(coveragesVehicle2.get(4).coverageLimitDisplay).isEqualTo("$600 (Included)");
		softly.assertThat(coveragesVehicle2.get(4).coverageType).isEqualTo("Per Occurrence");
		softly.assertThat(coveragesVehicle2.get(4).customerDisplayed).isEqualTo(true);
		softly.assertThat(coveragesVehicle2.get(4).canChangeCoverage).isEqualTo(true);

		List<CoverageLimit> availableLimitsNd = coveragesVehicle2.get(4).availableLimits;
		softly.assertThat(availableLimitsNd.get(0).coverageLimit).isEqualTo("600");
		softly.assertThat(availableLimitsNd.get(0).coverageLimitDisplay).isEqualTo("$600 (Included)");
		softly.assertThat(availableLimitsNd.get(1).coverageLimit).isEqualTo("1200");
		softly.assertThat(availableLimitsNd.get(1).coverageLimitDisplay).isEqualTo("$1,200");

		//Other Than Collision
		softly.assertThat(coveragesVehicle2.get(0).coverageCd).isEqualTo("COMPDED");
		softly.assertThat(coveragesVehicle2.get(0).coverageDescription).isEqualTo("Other Than Collision");
		softly.assertThat(coveragesVehicle2.get(0).coverageLimit).isEqualTo("100");
		softly.assertThat(coveragesVehicle2.get(0).coverageLimitDisplay).isEqualTo("$100");
		softly.assertThat(coveragesVehicle2.get(0).coverageType).isEqualTo("Deductible");
		softly.assertThat(coveragesVehicle2.get(0).customerDisplayed).isEqualTo(true);
		softly.assertThat(coveragesVehicle2.get(0).canChangeCoverage).isEqualTo(true);
		assertCoverageLimitForCompColl(updateCoverageResponse2);

		helperMiniServices.endorsementRateAndBind(policyNumber);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
	}

	protected void pas14536_TransportationExpensePart3Body(PolicyType policyType, ETCSCoreSoftAssertions softly) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//Add first vehicle
		String purchaseDate = "2013-02-22";
		String vin1 = "1HGFA16526L081415";
		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin1);
		assertThat(addVehicle.oid).isNotEmpty();
		String oid1 = addVehicle.oid;
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, oid1);

		String coverageCd = "COMPDED";
		String availableLimits = "-1";
		String availableLimits2 = "100";

		//Remove COMPDED coverage and check Transportation Expense
		PolicyCoverageInfo updateCoverageResponse1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, availableLimits), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

		List<Coverage> coveragesVehicle = updateCoverageResponse1.vehicleLevelCoverages.get(0).coverages;
		softly.assertThat(coveragesVehicle.get(4).coverageCd).isEqualTo("RREIM");
		softly.assertThat(coveragesVehicle.get(4).coverageDescription).isEqualTo("Transportation Expense");
		softly.assertThat(coveragesVehicle.get(4).coverageLimit).isEqualTo("0");
		softly.assertThat(coveragesVehicle.get(4).coverageType).isEqualTo("Per Occurrence");
		softly.assertThat(coveragesVehicle.get(4).customerDisplayed).isEqualTo(true);
		softly.assertThat(coveragesVehicle.get(4).canChangeCoverage).isEqualTo(false);

		List<CoverageLimit> availableLimitsSt = coveragesVehicle.get(4).availableLimits;
		softly.assertThat(availableLimitsSt.get(0).coverageLimit).isEqualTo("600");
		softly.assertThat(availableLimitsSt.get(0).coverageLimitDisplay).isEqualTo("$600 (Included)");
		softly.assertThat(availableLimitsSt.get(1).coverageLimit).isEqualTo("900");
		softly.assertThat(availableLimitsSt.get(1).coverageLimitDisplay).isEqualTo("$900");
		softly.assertThat(availableLimitsSt.get(2).coverageLimit).isEqualTo("1200");
		softly.assertThat(availableLimitsSt.get(2).coverageLimitDisplay).isEqualTo("$1,200");
		softly.assertThat(availableLimitsSt.get(3).coverageLimit).isEqualTo("1500");
		softly.assertThat(availableLimitsSt.get(3).coverageLimitDisplay).isEqualTo("$1,500");

		//Other Than Collision
		softly.assertThat(coveragesVehicle.get(0).coverageCd).isEqualTo("COMPDED");
		softly.assertThat(coveragesVehicle.get(0).coverageDescription).isEqualTo("Other Than Collision");
		softly.assertThat(coveragesVehicle.get(0).coverageLimit).isEqualTo("-1");
		softly.assertThat(coveragesVehicle.get(0).coverageLimitDisplay).isEqualTo("No Coverage");
		softly.assertThat(coveragesVehicle.get(0).coverageType).isEqualTo("Deductible");
		softly.assertThat(coveragesVehicle.get(0).customerDisplayed).isEqualTo(true);
		softly.assertThat(coveragesVehicle.get(0).canChangeCoverage).isEqualTo(true);
		assertCoverageLimitForCompColl(updateCoverageResponse1);

		//Add COMPDED coverage again and check Transportation Expense
		PolicyCoverageInfo updateCoverageResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, availableLimits2), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		List<Coverage> coveragesVehicle2 = updateCoverageResponse2.vehicleLevelCoverages.get(0).coverages;
		softly.assertThat(coveragesVehicle2.get(4).coverageCd).isEqualTo("RREIM");
		softly.assertThat(coveragesVehicle2.get(4).coverageDescription).isEqualTo("Transportation Expense");
		softly.assertThat(coveragesVehicle2.get(4).coverageLimit).isEqualTo("600");
		softly.assertThat(coveragesVehicle2.get(4).coverageLimitDisplay).isEqualTo("$600 (Included)");
		softly.assertThat(coveragesVehicle2.get(4).coverageType).isEqualTo("Per Occurrence");
		softly.assertThat(coveragesVehicle2.get(4).customerDisplayed).isEqualTo(true);
		softly.assertThat(coveragesVehicle2.get(4).canChangeCoverage).isEqualTo(true);

		List<CoverageLimit> availableLimitsNd = coveragesVehicle2.get(4).availableLimits;
		softly.assertThat(availableLimitsNd.get(0).coverageLimit).isEqualTo("600");
		softly.assertThat(availableLimitsNd.get(0).coverageLimitDisplay).isEqualTo("$600 (Included)");
		softly.assertThat(availableLimitsNd.get(1).coverageLimit).isEqualTo("900");
		softly.assertThat(availableLimitsNd.get(1).coverageLimitDisplay).isEqualTo("$900");
		softly.assertThat(availableLimitsNd.get(2).coverageLimit).isEqualTo("1200");
		softly.assertThat(availableLimitsNd.get(2).coverageLimitDisplay).isEqualTo("$1,200");
		softly.assertThat(availableLimitsNd.get(3).coverageLimit).isEqualTo("1500");
		softly.assertThat(availableLimitsNd.get(3).coverageLimitDisplay).isEqualTo("$1,500");

		//Other Than Collision
		softly.assertThat(coveragesVehicle2.get(0).coverageCd).isEqualTo("COMPDED");
		softly.assertThat(coveragesVehicle2.get(0).coverageDescription).isEqualTo("Other Than Collision");
		softly.assertThat(coveragesVehicle2.get(0).coverageLimit).isEqualTo("100");
		softly.assertThat(coveragesVehicle2.get(0).coverageLimitDisplay).isEqualTo("$100");
		softly.assertThat(coveragesVehicle2.get(0).coverageType).isEqualTo("Deductible");
		softly.assertThat(coveragesVehicle2.get(0).customerDisplayed).isEqualTo(true);
		softly.assertThat(coveragesVehicle2.get(0).canChangeCoverage).isEqualTo(true);
		assertCoverageLimitForCompColl(updateCoverageResponse2);

		helperMiniServices.endorsementRateAndBind(policyNumber);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
	}

	protected void pas14734_UpdateViewCoverageILAndMedicalBody(PolicyType policyType) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo viewCoverageResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

		assertSoftly(softly -> {
			Coverage filteredCoverageResponseMEDPM = viewCoverageResponse.policyCoverages.stream().filter(cov -> "MEDPM".equals(cov.coverageCd)).findFirst().orElse(null);
			Coverage filteredCoverageResponseIL = viewCoverageResponse.policyCoverages.stream().filter(cov -> "IL".equals(cov.coverageCd)).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseMEDPM.coverageLimit).isEqualTo("2000");
			softly.assertThat(filteredCoverageResponseMEDPM.availableLimits.get(0).coverageLimit).isEqualTo("0");
			softly.assertThat(filteredCoverageResponseMEDPM.availableLimits.get(1).coverageLimit).isEqualTo("1000");
			softly.assertThat(filteredCoverageResponseMEDPM.availableLimits.get(2).coverageLimit).isEqualTo("2000");
			softly.assertThat(filteredCoverageResponseMEDPM.availableLimits.get(3).coverageLimit).isEqualTo("5000");
			softly.assertThat(filteredCoverageResponseMEDPM.availableLimits.get(4).coverageLimit).isEqualTo("10000");
			softly.assertThat(filteredCoverageResponseMEDPM.availableLimits.get(5).coverageLimit).isEqualTo("25000");

			softly.assertThat(filteredCoverageResponseIL.coverageLimit).isEqualTo("100");
			softly.assertThat(filteredCoverageResponseIL.availableLimits.get(0).coverageLimit).isEqualTo("0");
			softly.assertThat(filteredCoverageResponseIL.availableLimits.get(1).coverageLimit).isEqualTo("100");
		});

		String coverageCd = "MEDPM";
		String newLimit = "10000";

		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, newLimit), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			Coverage filteredCoverageResponseMEPD = coverageResponse.policyCoverages.stream().filter(cov -> "MEDPM".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponseMEPD.coverageLimit).isEqualTo(newLimit);

		});

		String coverageCd1 = "IL";
		String newLimit1 = "0";

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd1, newLimit1), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			Coverage filteredCoverageResponseIL = coverageResponse1.policyCoverages.stream().filter(cov -> "IL".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponseIL.coverageLimit).isEqualTo(newLimit1);
		});

		PolicyCoverageInfo viewCoverageResponse1 = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			Coverage filteredCoverageResponseMEDPM = viewCoverageResponse1.policyCoverages.stream().filter(cov -> "MEDPM".equals(cov.coverageCd)).findFirst().orElse(null);
			Coverage filteredCoverageResponseIL = viewCoverageResponse1.policyCoverages.stream().filter(cov -> "IL".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponseMEDPM.coverageLimit).isEqualTo(newLimit);
			softly.assertThat(filteredCoverageResponseIL.coverageLimit).isEqualTo(newLimit1);
		});
	}

	protected void pas14730_UpdateCoverageUMPDAndPDBody(PolicyType policyType) {
		mainApp().open();

		String policyNumber = getCopiedPolicy();

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo viewCoverageResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

		assertSoftly(softly -> {
			Coverage filteredCoverageResponseUMPD = viewCoverageResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.coverageCd)).findFirst().orElse(null);
			Coverage filteredCoverageResponsePD = viewCoverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseUMPD.coverageLimit).isEqualTo("50000");

			softly.assertThat(filteredCoverageResponsePD.coverageLimit).isEqualTo("50000");
		});

		String coverageCd = "PD";
		String newLimit = "15000";

		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, newLimit), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			Coverage filteredCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);
			Coverage filteredCoverageResponseUMPD = coverageResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.coverageCd)).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseUMPD.coverageLimit).isEqualTo(newLimit);
			assertAvailableCoverageLimitForPD(coverageResponse);

			softly.assertThat(filteredCoverageResponsePD.coverageLimit).isEqualTo(newLimit);
		});
	}

	protected void pas14680_TrailersCoveragesThatDoNotApplyBody(PolicyType policyType) {
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData;
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");

		//adjust test data to override errors for NJ and NY
		if ("NJ, NY".contains(getState())) {
			testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_PPAandTrailer").getTestDataList("VehicleTab"))
					.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError).resolveLinks();
		} else {
			testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_PPAandTrailer").getTestDataList("VehicleTab")).resolveLinks();
		}

		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		//validate that viewPolicyCoverages response contains only one instance of Policy level coverages (For policy)
		PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertThatOnlyOneInstanceOfPolicyLevelCoverages(policyCoverageResponse);

		//validate that viewPolicyCoverages response contains only one instance of Policy level coverages (For endorsement)
		PolicyCoverageInfo endorsementCoverageResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertThatOnlyOneInstanceOfPolicyLevelCoverages(endorsementCoverageResponse);

		// view vehicle to get OID
		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		assertThat(viewVehicleResponse.vehicleList.get(1).vehTypeCd.contains("Trailer")).isTrue();
		String oidTrailer = viewVehicleResponse.vehicleList.get(1).oid;

		//validate that viewPolicyCoveragesByVehicle response for Trailer contains all vehicle level coverages AND only comp and coll have CanChangeCoverage = true AND CustomerDisplay = true (For policy)
		PolicyCoverageInfo viewPolicyCoveragesByVehicleResponse = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, oidTrailer, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		validateTrailerCoverages(viewPolicyCoveragesByVehicleResponse);
		assertThatOnlyOneInstanceOfPolicyLevelCoverages(viewPolicyCoveragesByVehicleResponse);

		//validate that viewEndorsementCoveragesByVehicle response for Trailer contains all vehicle level coverages AND only comp and coll have CanChangeCoverage = true AND CustomerDisplay = true (For endorsement)
		PolicyCoverageInfo viewEndorsementCoveragesByVehicleResponse = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, oidTrailer, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		validateTrailerCoverages(viewEndorsementCoveragesByVehicleResponse);
		assertThatOnlyOneInstanceOfPolicyLevelCoverages(viewEndorsementCoveragesByVehicleResponse);
	}

	private void assertThatOnlyOneInstanceOfPolicyLevelCoverages(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			//make sure that no Policy Level coverages are missed
			validateCountOfPolicyLevelCoverages(coverageResponse, softly);

			List<Coverage> filteredPolicyCoverageResponseBI = coverageResponse.policyCoverages.stream().filter(cov -> "BI".equals(cov.coverageCd)).collect(Collectors.toList());
			softly.assertThat(filteredPolicyCoverageResponseBI.size()).isEqualTo(1);

			List<Coverage> filteredPolicyCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).collect(Collectors.toList());
			softly.assertThat(filteredPolicyCoverageResponsePD.size()).isEqualTo(1);

			if (!"NY".contains(getState())) {
				List<Coverage> filteredPolicyCoverageResponseUMBI = coverageResponse.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.coverageCd)).collect(Collectors.toList());
				softly.assertThat(filteredPolicyCoverageResponseUMBI.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseUIMBI = coverageResponse.policyCoverages.stream().filter(cov -> "UIMBI".equals(cov.coverageCd)).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseUIMBI.isEmpty()) {
				softly.assertThat(filteredPolicyCoverageResponseUIMBI.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseUMPD = coverageResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.coverageCd)).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseUMPD.isEmpty()) {
				softly.assertThat(filteredPolicyCoverageResponseUMPD.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseMEDPM = coverageResponse.policyCoverages.stream().filter(cov -> "MEDPM".equals(cov.coverageCd)).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseMEDPM.isEmpty()) {
				softly.assertThat(filteredPolicyCoverageResponseMEDPM.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseIL = coverageResponse.policyCoverages.stream().filter(cov -> "IL".equals(cov.coverageCd)).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseIL.isEmpty()) {
				softly.assertThat(filteredPolicyCoverageResponseIL.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponsePIP = coverageResponse.policyCoverages.stream().filter(cov -> "PIP".equals(cov.coverageCd)).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponsePIP.isEmpty()) {
				softly.assertThat(filteredPolicyCoverageResponsePIP.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseBPIP = coverageResponse.policyCoverages.stream().filter(cov -> "BPIP".equals(cov.coverageCd)).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseBPIP.isEmpty()) {
				softly.assertThat(filteredPolicyCoverageResponseBPIP.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseADDPIP = coverageResponse.policyCoverages.stream().filter(cov -> "ADDPIP".equals(cov.coverageCd)).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseADDPIP.isEmpty()) {
				softly.assertThat(filteredPolicyCoverageResponseADDPIP.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseGPIP = coverageResponse.policyCoverages.stream().filter(cov -> "GPIP".equals(cov.coverageCd)).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseGPIP.isEmpty()) {
				softly.assertThat(filteredPolicyCoverageResponseGPIP.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseUMSU = coverageResponse.policyCoverages.stream().filter(cov -> "Underinsured Motorist Stacked/Unstacked".equals(cov.coverageDescription)).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseUMSU.isEmpty()) {//TODO-mstrazds: PAS-16038 View Coverages - UIM/UIM Stacked/Unstacked - PA
				softly.assertThat(filteredPolicyCoverageResponseUMSU.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseFPB = coverageResponse.policyCoverages.stream().filter(cov -> "First Party Benefits".equals(cov.coverageDescription)).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseFPB.isEmpty()) { //TODO-mstrazds: PAS-15350 View Coverages - First Party Benefits and Pennsylvania
				softly.assertThat(filteredPolicyCoverageResponseFPB.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseTH = coverageResponse.policyCoverages.stream().filter(cov -> "Tort Threshold".equals(cov.coverageDescription)).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseTH.isEmpty()) {//TODO-mstrazds: PAS-15304 View coverages - Tort - PA
				softly.assertThat(filteredPolicyCoverageResponseTH.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseRoWLB = coverageResponse.policyCoverages.stream().filter(cov -> "Rejection of Work Loss Benefit (Y/N)".equals(cov.coverageDescription)).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseRoWLB.isEmpty()) {//TODO-mstrazds: PAS-16040 View Coverages - Rejection of Work Loss Benefit - NY
				softly.assertThat(filteredPolicyCoverageResponseRoWLB.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponsePIPD = coverageResponse.policyCoverages.stream().filter(cov -> "Personal Injury Protection Deductible".equals(cov.coverageDescription)).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponsePIPD.isEmpty()) {//TODO-mstrazds: US needed
				softly.assertThat(filteredPolicyCoverageResponsePIPD.size()).isEqualTo(1);
			}

			if ("NY".contains(getState())) {
				List<Coverage> filteredPolicyCoverageResponseUMSUMny = coverageResponse.policyCoverages.stream().filter(cov -> "UM/SUM".equals(cov.coverageCd)).collect(Collectors.toList());
				softly.assertThat(filteredPolicyCoverageResponseUMSUMny.size()).isEqualTo(1);

				List<Coverage> filteredPolicyCoverageResponseSSLny = coverageResponse.policyCoverages.stream().filter(cov -> "Supplemental Spousal Liability".equals(cov.coverageDescription)).collect(Collectors.toList());
				softly.assertThat(filteredPolicyCoverageResponseSSLny.size()).isEqualTo(0);//TODO-mstrazds: PAS-15309 View Coverage - Supplementary UM/UIM - New York

				List<Coverage> filteredPolicyCoverageResponsePIPny = coverageResponse.policyCoverages.stream().filter(cov -> "Personal Injury Protection".equals(cov.coverageDescription)).collect(Collectors.toList());
				softly.assertThat(filteredPolicyCoverageResponsePIPny.size()).isEqualTo(0);//TODO-mstrazds: PAS-15363 View Coverages - PIP - New York (Only for Ny or also for othr states?)

				List<Coverage> filteredPolicyCoverageResponseAPIPny = coverageResponse.policyCoverages.stream().filter(cov -> "APIP".equals(cov.coverageCd)).collect(Collectors.toList());
				softly.assertThat(filteredPolicyCoverageResponseAPIPny.size()).isEqualTo(1); //TODO-mstrazds: PAS-15363 View Coverages - PIP - New York (is this correct coverage or it really is about PIP)

				// Possible issue that this coverage will be missing. It is possible, that it should be displayed only for one of the vehicles.
				List<Coverage> filteredPolicyCoverageResponseMEEny = coverageResponse.policyCoverages.stream().filter(cov -> "Medical Expense Elimination".equals(cov.coverageDescription)).collect(Collectors.toList());
				softly.assertThat(filteredPolicyCoverageResponseMEEny.size()).isEqualTo(0);//TODO-mstrazds: PAS-16042 View Coverages - Medical Expenses Elimination - New York

				List<Coverage> filteredPolicyCoverageResponseOBELny = coverageResponse.policyCoverages.stream().filter(cov -> "OBEL".equals(cov.coverageCd)).collect(Collectors.toList());
				softly.assertThat(filteredPolicyCoverageResponseOBELny.size()).isEqualTo(1);
			}

			// Possible issue that this coverage will be missing. It is possible, that it should be displayed only for one of the vehicles. Seems this coverage is only for IN.//TODO-mstrazds:US needed
			List<Coverage> filteredPolicyCoverageResponseUMPDD = coverageResponse.policyCoverages.stream().filter(cov -> "Uninsured Motorist Property Damage Deductible".equals(cov.coverageDescription)).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseUMPDD.isEmpty()) {
				softly.assertThat(filteredPolicyCoverageResponseUMPDD.size()).isEqualTo(1);
			}

			//For CT this coverage is without coverageCD//TODO-mstrazds: PAS-15265 View Coverages - UM Conversion Coverage - CT
			List<Coverage> filteredPolicyCoverageResponseUMCC = coverageResponse.policyCoverages.stream().filter(cov -> "Underinsured Motorist Conversion Coverage".equals(cov.coverageDescription)).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseUMCC.isEmpty()) {
				softly.assertThat(filteredPolicyCoverageResponseUMCC.size()).isEqualTo(1);
			}

		});
	}

	private void validateTrailerCoverages(PolicyCoverageInfo viewPolicyCoveragesByVehicleResponse) {
		assertSoftly(softly -> {
			//make sure that no Vehicle Level coverages are missed
			if ("NV, OR, UT, DE, OH, KY".contains(getState())) {
				softly.assertThat(viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.size()).isEqualTo(10);
			} else {
				softly.assertThat(viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.size()).isEqualTo(9);
			}

			Coverage filteredPolicyCoverageResponseCOMPDED = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COMPDED".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseCOMPDED.canChangeCoverage).isTrue();
			softly.assertThat(filteredPolicyCoverageResponseCOMPDED.customerDisplayed).isTrue();

			Coverage filteredPolicyCoverageResponseCOLLDED = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COLLDED".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseCOLLDED.canChangeCoverage).isTrue();
			softly.assertThat(filteredPolicyCoverageResponseCOLLDED.customerDisplayed).isTrue();

			Coverage filteredPolicyCoverageResponseGLASS = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "GLASS".equals(cov.coverageCd)).findFirst().orElse(null);
			if ("KY".equals(getState())) {
				softly.assertThat(filteredPolicyCoverageResponseGLASS.canChangeCoverage).isNull(); //TODO-mstrazds: PAS-15329 View Coverages - Full Safety Glass - Kentucky
				softly.assertThat(filteredPolicyCoverageResponseGLASS.customerDisplayed).isFalse();
			} else {
				softly.assertThat(filteredPolicyCoverageResponseGLASS.canChangeCoverage).isFalse();
				softly.assertThat(filteredPolicyCoverageResponseGLASS.customerDisplayed).isFalse();
			}

			Coverage filteredPolicyCoverageResponseLOAN = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "LOAN".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseLOAN.canChangeCoverage).isFalse();
			softly.assertThat(filteredPolicyCoverageResponseLOAN.customerDisplayed).isFalse();

			Coverage filteredPolicyCoverageResponseRREIM = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "RREIM".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseRREIM.canChangeCoverage).isFalse();
			softly.assertThat(filteredPolicyCoverageResponseRREIM.customerDisplayed).isFalse();

			Coverage filteredPolicyCoverageResponseTOWINGLABOR = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "TOWINGLABOR".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseTOWINGLABOR.canChangeCoverage).isFalse();
			softly.assertThat(filteredPolicyCoverageResponseTOWINGLABOR.customerDisplayed).isFalse();

			Coverage filteredPolicyCoverageResponseSPECEQUIP = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "SPECEQUIP".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseSPECEQUIP.canChangeCoverage).isFalse();
			softly.assertThat(filteredPolicyCoverageResponseSPECEQUIP.customerDisplayed).isFalse();

			Coverage filteredPolicyCoverageResponseNEWCAR = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "NEWCAR".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseNEWCAR.canChangeCoverage).isFalse();
			softly.assertThat(filteredPolicyCoverageResponseNEWCAR.customerDisplayed).isFalse();

			Coverage filteredPolicyCoverageResponseWL = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "WL".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseWL.canChangeCoverage).isFalse();
			softly.assertThat(filteredPolicyCoverageResponseWL.customerDisplayed).isFalse();

			//UMPD is Vehicle level coverage for NV, OR, UT, OH //TODO-mstrazds:PAS-15496 View Coverages - UMPD Colorado, NV, Ohio, Utah // PAS-16112 View Coverages - UMPD - Oregon
			if ("NV, OR, UT, OH".contains(getState())) {
				Coverage filteredPolicyCoverageResponseUMPD = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "UMPD".equals(cov.coverageCd)).findFirst().orElse(null);
				softly.assertThat(filteredPolicyCoverageResponseUMPD).isNotNull();
				//softly.assertThat(filteredPolicyCoverageResponseUMPD.canChangeCoverage).isFalse(); //not clear yet what value should be
				//softly.assertThat(filteredPolicyCoverageResponseUMPD.customerDisplayed).isFalse(); //not clear yet what value should be
			}

			//MEDPM ("Medical Payments") is Vehicle level coverage for KY, DE
			Coverage filteredPolicyCoverageResponseMEDPM = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "MEDPM".equals(cov.coverageCd)).findFirst().orElse(null);
			if ("KY, DE".contains(getState())) {
				softly.assertThat(filteredPolicyCoverageResponseMEDPM).isNotNull();
				softly.assertThat(filteredPolicyCoverageResponseMEDPM.canChangeCoverage).isNull(); //TODO-mstrazds: US needed
				softly.assertThat(filteredPolicyCoverageResponseMEDPM.customerDisplayed).isFalse();
			} else {
				softly.assertThat(filteredPolicyCoverageResponseMEDPM).isNull();
			}

		});
	}

	private void validateCountOfPolicyLevelCoverages(PolicyCoverageInfo coverageResponse, ETCSCoreSoftAssertions softly) {
		switch (getState()) {
			case "AZ":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(5);
				break;
			case "CO":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(4);
				break;
			case "CT":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(4);
				break;
			case "DC":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(9);
				break;
			case "MD":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(6);
				break;
			case "NJ":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(4);
				break;
			case "NV":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(4);
				break;
			case "WV":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(7);
				break;
			case "WY":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(4);
				break;
			case "DE":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(5);
				break;
			case "ID":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(5);
				break;
			case "IN":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(5);//possible issue with missing "Uninsured Motorist Property Damage Deductible"
				break;
			case "KS":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(4);
				break;
			case "KY":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(7);
				break;
			case "MT":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(5);
				break;
			case "NY":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(6);//possible issue with missing "Medical Expense Elimination"
				break;
			case "OK":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(4);
				break;
			case "SD":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(5);
				break;
			case "VA":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(6);
				break;
			case "UT":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(5);
				break;
			case "OH":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(4);
				break;
			case "OR":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(4);
				break;
			case "PA":
				softly.assertThat(coverageResponse.policyCoverages.size()).isEqualTo(9);
				break;
			default:
		}
	}

	private Dollar getCoverage(int index, String coverageCd, String... replacement) {
		String coverage = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(index, coverageCd);
		for (String replace : replacement) {
			coverage = coverage.replace(replace, "");
		}
		return new Dollar(coverage);
	}

	private String getCoverages(int index, String coverageCd, String... replacement) {
		String coverage = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(index, coverageCd);
		for (String replace : replacement) {
			coverage = coverage.replace(replace, "");

		}
		return coverage;
	}

	void vehicleCoverageComparisonByCoverageCd(PolicyCoverageInfo response1, PolicyCoverageInfo response2, String coverageCd) {
		assertSoftly(softly -> {
			Coverage coverageResponse1 = getVehicleCoverageDetails(response1, coverageCd);
			Coverage coverageResponse2 = getVehicleCoverageDetails(response2, coverageCd);
			softly.assertThat(coverageResponse1.coverageCd).isEqualTo(coverageResponse2.coverageCd);
			softly.assertThat(coverageResponse1.coverageDescription).isEqualTo(coverageResponse2.coverageDescription);
			softly.assertThat(coverageResponse1.coverageLimit).isEqualTo(coverageResponse2.coverageLimit);
			softly.assertThat(coverageResponse1.coverageLimitDisplay).isEqualTo(coverageResponse2.coverageLimitDisplay);
			softly.assertThat(coverageResponse1.coverageType).isEqualTo(coverageResponse2.coverageType);
			softly.assertThat(coverageResponse1.customerDisplayed).isEqualTo(coverageResponse2.customerDisplayed);
			softly.assertThat(coverageResponse1.canChangeCoverage).isEqualTo(coverageResponse2.canChangeCoverage);
		});
	}

	void vehicleCoverageComparisonByCoverageCdNotEqual(PolicyCoverageInfo response1, PolicyCoverageInfo response2, String coverageCd) {
		assertSoftly(softly -> {
			Coverage coverageResponse1 = getVehicleCoverageDetails(response1, coverageCd);
			Coverage coverageResponse2 = getVehicleCoverageDetails(response2, coverageCd);
			softly.assertThat(coverageResponse1.coverageCd).isEqualTo(coverageResponse2.coverageCd);
			softly.assertThat(coverageResponse1.coverageDescription).isEqualTo(coverageResponse2.coverageDescription);
			softly.assertThat(coverageResponse1.coverageLimit).isNotEqualTo(coverageResponse2.coverageLimit);
			if (!"SPECEQUIP".equals(coverageCd)) {
				softly.assertThat(coverageResponse1.coverageLimitDisplay).isNotEqualTo(coverageResponse2.coverageLimitDisplay);
			}
			softly.assertThat(coverageResponse1.coverageType).isEqualTo(coverageResponse2.coverageType);
			softly.assertThat(coverageResponse1.customerDisplayed).isEqualTo(coverageResponse2.customerDisplayed);
			softly.assertThat(coverageResponse1.canChangeCoverage).isEqualTo(coverageResponse2.canChangeCoverage);
		});
	}

	void policyCoverageComparisonByCoverageCd(PolicyCoverageInfo response1, PolicyCoverageInfo response2, String coverageCd) {
		assertSoftly(softly -> {
			Coverage coverageResponse1 = getPolicyCoverageDetails(response1, coverageCd);
			Coverage coverageResponse2 = getPolicyCoverageDetails(response2, coverageCd);
			softly.assertThat(coverageResponse1.coverageCd).isEqualTo(coverageResponse2.coverageCd);
			softly.assertThat(coverageResponse1.coverageDescription).isEqualTo(coverageResponse2.coverageDescription);
			softly.assertThat(coverageResponse1.coverageLimit).isEqualTo(coverageResponse2.coverageLimit);
			softly.assertThat(coverageResponse1.coverageLimitDisplay).isEqualTo(coverageResponse2.coverageLimitDisplay);
			softly.assertThat(coverageResponse1.coverageType).isEqualTo(coverageResponse2.coverageType);
			softly.assertThat(coverageResponse1.customerDisplayed).isEqualTo(coverageResponse2.customerDisplayed);
			softly.assertThat(coverageResponse1.canChangeCoverage).isEqualTo(coverageResponse2.canChangeCoverage);
		});
	}

	Coverage getVehicleCoverageDetails(PolicyCoverageInfo policyCoverageResponseReplacedLeasedVeh, String coverageCode) {
		return policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCode.equals(attribute.coverageCd)).findFirst().orElse(null);
	}

	private Coverage getPolicyCoverageDetails(PolicyCoverageInfo policyCoverageResponseReplacedLeasedVeh, String coverageCode) {

		return policyCoverageResponseReplacedLeasedVeh.policyCoverages.stream().filter(attribute -> coverageCode.equals(attribute.coverageCd)).findFirst().orElse(null);
	}

	private void assertAvailableCoverageLimitForPD(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			List<CoverageLimit> availableLimitsPD = coverageResponse.policyCoverages.get(2).availableLimits;

			softly.assertThat(availableLimitsPD.get(0).coverageLimit).isEqualTo("15000");
			softly.assertThat(availableLimitsPD.get(0).coverageLimitDisplay).isEqualTo("$15,000");

			softly.assertThat(availableLimitsPD.get(1).coverageLimit).isEqualTo("25000");
			softly.assertThat(availableLimitsPD.get(1).coverageLimitDisplay).isEqualTo("$25,000");

			softly.assertThat(availableLimitsPD.get(2).coverageLimit).isEqualTo("50000");
			softly.assertThat(availableLimitsPD.get(2).coverageLimitDisplay).isEqualTo("$50,000");

			softly.assertThat(availableLimitsPD.get(3).coverageLimit).isEqualTo("100000");
			softly.assertThat(availableLimitsPD.get(3).coverageLimitDisplay).isEqualTo("$100,000");
		});
	}
}



