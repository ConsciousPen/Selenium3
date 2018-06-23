package aaa.modules.regression.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.dtoDxp.*;
import toolkit.datax.TestData;

public class TestMiniServicesCoveragesHelper extends PolicyBaseTest {

	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private HelperMiniServices helperMiniServices = new HelperMiniServices();


	protected void pas11741_ViewManageVehicleLevelCoverages(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Perform Endorsement
		AAAEndorseResponse endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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

		PolicyCoverageInfo coverageResponse = HelperCommon.viewPolicyCoverages(policyNumber);
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
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).customerDisplayed).isEqualTo(false);

			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).coverageCd).isEqualTo("NEWCAR");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).coverageDescription).isEqualTo("New Car Added Protection");
			softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).customerDisplayed).isEqualTo(false);

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

		PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber);
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
			softly.assertThat(coveragesV1.get(6).customerDisplayed).isEqualTo(false);
		});
	}

	protected void pas13353_LoanLeaseCoverage(PolicyType policyType) {
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();

		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		AAAEndorseResponse endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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

		PolicyCoverageInfo coverageResponse = HelperCommon.viewPolicyCoverages(policyNumber);
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
			softly.assertThat(coveragesLoanVehicle.get(6).customerDisplayed).isEqualTo(false);
			assertThat(coveragesLoanVehicle.get(6).canChangeCoverage).isEqualTo(true);

			softly.assertThat(coveragesLoanVehicle.get(7).coverageCd).isEqualTo("NEWCAR");
			softly.assertThat(coveragesLoanVehicle.get(7).coverageDescription).isEqualTo("New Car Added Protection");
			softly.assertThat(coveragesLoanVehicle.get(7).customerDisplayed).isEqualTo(false);

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

		PolicyCoverageInfo coverageResponse1 = HelperCommon.viewEndorsementCoverages(policyNumber);
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
			softly.assertThat(coveragesV1.get(6).customerDisplayed).isEqualTo(false);
			assertThat(coveragesV1.get(6).canChangeCoverage).isEqualTo(true);

			softly.assertThat(coveragesV1.get(7).coverageCd).isEqualTo("NEWCAR");
			softly.assertThat(coveragesV1.get(7).coverageDescription).isEqualTo("New Car Added Protection");
			softly.assertThat(coveragesV1.get(7).customerDisplayed).isEqualTo(false);

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
		String transportationExpense = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.RENTAL_REIMBURSEMENT.getLabel().replace("(+$0.00)", "").trim());
		String towingAndLabor = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel()).replace("(Included)", "").replace("(+$0.00)", "").replace("$", "").trim();
		Dollar excessElectronicEquipment = new Dollar(premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.SPECIAL_EQUIPMENT_COVERAGE.getLabel()));

		PolicyCoverageInfo coverageResponse = HelperCommon.viewPolicyCoverages(policyNumber);
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
			softly.assertThat(coverageRreim.coverageLimitDisplay).contains(transportationExpense.toString().replace(".00", ""));
			softly.assertThat(coverageRreim.coverageType).isEqualTo("per day/maximum");
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
			softly.assertThat(coverageSpeq.customerDisplayed).isEqualTo(false);

			Coverage coverageNewcar = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7);
			softly.assertThat(coverageNewcar.coverageCd).isEqualTo("NEWCAR");
			softly.assertThat(coverageNewcar.coverageDescription).isEqualTo("New Car Added Protection");
			softly.assertThat(coverageNewcar.customerDisplayed).isEqualTo(false);

			Coverage coverageWL = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(8);
			softly.assertThat(coverageWL.coverageCd).isEqualTo("WL");
			softly.assertThat(coverageWL.coverageDescription).isEqualTo("Waive Liability");
			softly.assertThat(coverageWL.customerDisplayed).isEqualTo(false);

		});
	}

	private void availableCoverageCompdedForAz(PolicyCoverageInfo coverageResponse, SoftAssertions softly) {
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

	protected void pas14316_LoanLeasedCovForFinancedNewVehicleBody(SoftAssertions softly, String ownershipType) {
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
		PolicyCoverageInfo endorsementCoverageResponseOwnedOldVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid);
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
		PolicyCoverageInfo endorsementCoverageResponseLsdFinOldVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid);
		Coverage endorsementCoverageResponseLsdFinOldVehFiltered = endorsementCoverageResponseLsdFinOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, endorsementCoverageResponseLsdFinOldVehFiltered, "0", "No Coverage", true, true);

		PolicyCoverageInfo updateLoanLeaseCoverage = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdValue, "1");
		Coverage updateLoanLeaseCoverageFiltered = updateLoanLeaseCoverage.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, updateLoanLeaseCoverageFiltered, "1", "Yes", true, true);
		//BUG Status is not reset when updating coverages
		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");

		helperMiniServices.endorsementRateAndBind(policyNumber);
		PolicyCoverageInfo policyCoverageResponseLsdFinOldVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, newVehicleOid);
		Coverage policyCoverageResponseLsdFinOldVehFiltered = policyCoverageResponseLsdFinOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, policyCoverageResponseLsdFinOldVehFiltered, "1", "Yes", true, true);

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		VehicleUpdateDto updateVehicleOwned = new VehicleUpdateDto();
		updateVehicleOwned.vehicleOwnership = new VehicleOwnership();
		updateVehicleOwned.vehicleOwnership.ownership = "OWN";
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleOwned);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo endorsementCoverageResponseOwnedOldVeh2 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid);
		Coverage endorsementCoverageResponseOwnedOldVeh2Filtered = endorsementCoverageResponseOwnedOldVeh2.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, endorsementCoverageResponseOwnedOldVeh2Filtered, "0", "No Coverage", false, false);
	}

	private void loanLeaseCovPropertiesCheck(SoftAssertions softly, Coverage endorsementCoverageResponseOwnedOldVeh2Filtered, String coverageLimit, String coverageDisplayLimit, boolean customerDisplayed, boolean canChangeCoverage) {
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

		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCd, availableLimits);
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

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCd1, availableLimits1);
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

		PolicyCoverageInfo coverageResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCd2, availableLimits2);
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

		PolicyCoverageInfo coverageResponse3 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCd3, availableLimits3);
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

		PolicyCoverageInfo coverageResponse4 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCd4, availableLimits4);
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

		PolicyCoverageInfo coverageResponse5 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCd5, availableLimits5);
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

		PolicyCoverageInfo coverageResponse14 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdRemove, availableLimitsRemove);
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

		PolicyCoverageInfo coverageResponse6 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChange, availableLimitsChange);
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

		PolicyCoverageInfo coverageResponse7 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeColl, availableLimitsChangeColl);
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

		PolicyCoverageInfo coverageResponse8 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeGlass, availableLimitsChangeGlass);
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

		PolicyCoverageInfo coverageResponse9 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeGlassNoCov, availableLimitsChangeGlassNoCov);
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

		PolicyCoverageInfo coverageResponse10 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeLoanNoCov, availableLimitsChangeLoanNoCov);
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

		PolicyCoverageInfo coverageResponse11 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeTransport, availableLimitsChangeTransport);
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

		PolicyCoverageInfo coverageResponse12 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeTowing, availableLimitsChangeTowing);
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

		PolicyCoverageInfo coverageResponse13 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeTowingNoCov, availableLimitsChangeTowingNoCov);
		assertSoftly(softly -> {

			coverageXproperties(softly, 0, coverageResponse13, "COMPDED", "Other Than Collision", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse13, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse13, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse13, "LOAN", "Auto Loan/Lease Coverage", "1", "Yes", "None", true, true);

			//coverageXproperties(softly, 4, coverageResponse13, "RREIM", "Transportation Expense", availableLimitsChangeTransport, availableLimitsChangeTransport, "Per Occurrence", true, true);

			coverageXproperties(softly, 5, coverageResponse13, "TOWINGLABOR", "Towing and Labor Coverage", availableLimitsChangeTowingNoCov, "No Coverage", "Per Disablement/Maximum", true, true);

		});

		helperMiniServices.endorsementRateAndBind(policyNumber);

		helperMiniServices.secondEndorsementIssueCheck();
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

		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCd, availableLimits);
		assertSoftly(softly -> {

			coverageXproperties(softly, 0, coverageResponse, "COMPDED", "Comprehensive Deductible", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse, "COLLDED", "Collision Deductible", collisionDeductible.toPlaingString(), collisionDeductible.toPlaingString(), "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse, "LOAN", "Vehicle Loan/Lease Protection", "0", loanLeaseCov, "None", true, true);

			coverageXproperties(softly, 4, coverageResponse, "RREIM", "Rental Reimbursement", "0/0", transportationExpense, "per day/maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);

		});

		String coverageCd1 = "COLLDED";
		String availableLimits1 = "100";

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCd1, availableLimits1);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse1, "COMPDED", "Comprehensive Deductible", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse1, "COLLDED", "Collision Deductible", availableLimits1, availableLimits1, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse1, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse1, "LOAN", "Vehicle Loan/Lease Protection", "0", loanLeaseCov, "None", true, true);

			coverageXproperties(softly, 4, coverageResponse1, "RREIM", "Rental Reimbursement", "0/0", transportationExpense, "per day/maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);

		});

		String coverageCd2 = "GLASS";
		String availableLimits2 = "Yes";

		PolicyCoverageInfo coverageResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCd2, availableLimits2);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse2, "COMPDED", "Comprehensive Deductible", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse2, "COLLDED", "Collision Deductible", availableLimits1, availableLimits1, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse2, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse2, "LOAN", "Vehicle Loan/Lease Protection", "0", loanLeaseCov, "None", true, true);

			coverageXproperties(softly, 4, coverageResponse2, "RREIM", "Rental Reimbursement", "0/0", transportationExpense, "per day/maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse2, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);

		});

		String coverageCd3 = "LOAN";
		String availableLimits3 = "1";

		PolicyCoverageInfo coverageResponse3 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCd3, availableLimits3);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse3, "COMPDED", "Comprehensive Deductible", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse3, "COLLDED", "Collision Deductible", availableLimits1, availableLimits1, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse3, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse3, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);

			coverageXproperties(softly, 4, coverageResponse3, "RREIM", "Rental Reimbursement", "0/0", transportationExpense, "per day/maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse3, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);

		});

		String coverageCd4 = "RREIM";
		String availableLimits4 = "30/900";

		PolicyCoverageInfo coverageResponse4 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCd4, availableLimits4);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse4, "COMPDED", "Comprehensive Deductible", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse4, "COLLDED", "Collision Deductible", availableLimits1, availableLimits1, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse4, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse4, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);

			coverageXproperties(softly, 4, coverageResponse4, "RREIM", "Rental Reimbursement", availableLimits4, "$30/$900", "per day/maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse4, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);

		});

		String coverageCd5 = "TOWINGLABOR";
		String availableLimits5 = "50/300";

		PolicyCoverageInfo coverageResponse5 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCd5, availableLimits5);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse5, "COMPDED", "Comprehensive Deductible", availableLimits, availableLimits, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse5, "COLLDED", "Collision Deductible", availableLimits1, availableLimits1, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse5, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse5, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);

			coverageXproperties(softly, 4, coverageResponse5, "RREIM", "Rental Reimbursement", availableLimits4, "$30/$900", "per day/maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse5, "TOWINGLABOR", "Towing and Labor Coverage", availableLimits5, "$50/$300", "Per Disablement/Maximum", true, true);

		});

		String coverageCdRemove = "COMPDED";
		String availableLimitsRemove = "-1";

		PolicyCoverageInfo coverageResponse6 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdRemove, availableLimitsRemove);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse6, "COMPDED", "Comprehensive Deductible", availableLimitsRemove, "No Coverage", "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse6, "COLLDED", "Collision Deductible", availableLimitsRemove, "No Coverage", "Deductible", true, false);

			coverageXproperties(softly, 2, coverageResponse6, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, false);

			coverageXproperties(softly, 3, coverageResponse6, "LOAN", "Vehicle Loan/Lease Protection", "0", "No Coverage", "None", true, false);

			coverageXproperties(softly, 4, coverageResponse6, "RREIM", "Rental Reimbursement", "0/0", "No Coverage", "per day/maximum", true, false);

			coverageXproperties(softly, 5, coverageResponse6, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, false);

		});

		String coverageCdChange = "COMPDED";
		String availableLimitsChange = "500";

		PolicyCoverageInfo coverageResponse7 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChange, availableLimitsChange);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse7, "COMPDED", "Comprehensive Deductible", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse7, "COLLDED", "Collision Deductible", availableLimitsRemove, "No Coverage", "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse7, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, true);

			coverageXproperties(softly, 3, coverageResponse7, "LOAN", "Vehicle Loan/Lease Protection", "0", "No Coverage", "None", true, false);

			//coverageXproperties(softly, 4, coverageResponse7, "RREIM", "Rental Reimbursement", availableLimits4, availableLimits4, "per day/maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse7, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, false);

		});

		String coverageCdChangeColl = "COLLDED";
		String availableLimitsChangeColl = "500";

		PolicyCoverageInfo coverageResponse8 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeColl, availableLimitsChangeColl);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse8, "COMPDED", "Comprehensive Deductible", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse8, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse8, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, true);

			coverageXproperties(softly, 3, coverageResponse8, "LOAN", "Vehicle Loan/Lease Protection", "0", "No Coverage", "None", true, true);

			//coverageXproperties(softly, 4, coverageResponse8, "RREIM", "Rental Reimbursement", availableLimits4, availableLimits4, "per day/maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse8, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeGlass = "GLASS";
		String availableLimitsChangeGlass = "Yes";

		PolicyCoverageInfo coverageResponse9 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeGlass, availableLimitsChangeGlass);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse9, "COMPDED", "Comprehensive Deductible", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse9, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse9, "GLASS", "Full Safety Glass", "true", availableLimitsChangeGlass, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse9, "LOAN", "Vehicle Loan/Lease Protection", "0", "No Coverage", "None", true, true);

			//coverageXproperties(softly, 4, coverageResponse9, "RREIM", "Rental Reimbursement", availableLimits4, availableLimits4, "per day/maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse9, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeGlassNoCov = "GLASS";
		String availableLimitsChangeGlassNoCov = "No Coverage";

		PolicyCoverageInfo coverageResponse10 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeGlassNoCov, availableLimitsChangeGlassNoCov);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse10, "COMPDED", "Comprehensive Deductible", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse10, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse10, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse10, "LOAN", "Vehicle Loan/Lease Protection", "0", "No Coverage", "None", true, true);

			//coverageXproperties(softly, 4, coverageResponse10, "RREIM", "Rental Reimbursement", availableLimits4, availableLimits4, "per day/maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse10, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeLoanNoCov = "LOAN";
		String availableLimitsChangeLoanNoCov = "1";

		PolicyCoverageInfo coverageResponse11 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeLoanNoCov, availableLimitsChangeLoanNoCov);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse11, "COMPDED", "Comprehensive Deductible", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse11, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse11, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse11, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);

			//coverageXproperties(softly, 4, coverageResponse11, "RREIM", "Rental Reimbursement", availableLimits4, availableLimits4, "per day/maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse11, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);
		});

		String coverageCdChangeTransport = "RREIM";
		String availableLimitsChangeTransport = "30/900";

		PolicyCoverageInfo coverageResponse12 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeTransport, availableLimitsChangeTransport);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse12, "COMPDED", "Comprehensive Deductible", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse12, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse12, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse12, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);

			coverageXproperties(softly, 4, coverageResponse12, "RREIM", "Rental Reimbursement", availableLimitsChangeTransport, "$30/$900", "per day/maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse12, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeTowing = "TOWINGLABOR";
		String availableLimitsChangeTowing = "50/300";

		PolicyCoverageInfo coverageResponse13 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeTowing, availableLimitsChangeTowing);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse13, "COMPDED", "Comprehensive Deductible", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse13, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse13, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse13, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);

			coverageXproperties(softly, 4, coverageResponse13, "RREIM", "Rental Reimbursement", availableLimitsChangeTransport, "$30/$900", "per day/maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse13, "TOWINGLABOR", "Towing and Labor Coverage", availableLimitsChangeTowing, "$50/$300", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeTowingNoCov = "TOWINGLABOR";
		String availableLimitsChangeTowingNoCov = "0/0";

		PolicyCoverageInfo coverageResponse14 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, coverageCdChangeTowingNoCov, availableLimitsChangeTowingNoCov);
		assertSoftly(softly -> {
			coverageXproperties(softly, 0, coverageResponse14, "COMPDED", "Comprehensive Deductible", availableLimitsChange, availableLimitsChange, "Deductible", true, true);

			coverageXproperties(softly, 1, coverageResponse14, "COLLDED", "Collision Deductible", availableLimitsChangeColl, availableLimitsChangeColl, "Deductible", true, true);

			coverageXproperties(softly, 2, coverageResponse14, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);

			coverageXproperties(softly, 3, coverageResponse14, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);

			coverageXproperties(softly, 4, coverageResponse14, "RREIM", "Rental Reimbursement", availableLimitsChangeTransport, "$30/$900", "per day/maximum", true, true);

			coverageXproperties(softly, 5, coverageResponse14, "TOWINGLABOR", "Towing and Labor Coverage", availableLimitsChangeTowingNoCov, "No Coverage", "Per Disablement/Maximum", true, true);

		});
	}

	protected void pas14316_LoanLeasedCovForLeasedOldVehicleBody(SoftAssertions softly, String ownershipType) {
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
		PolicyCoverageInfo endorsementCoverageResponseOwnedOldVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid);
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
		PolicyCoverageInfo endorsementCoverageResponseLsdFinOldVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid);
		Coverage endorsementCoverageResponseLsdFinOldVehFiltered = endorsementCoverageResponseLsdFinOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, endorsementCoverageResponseLsdFinOldVehFiltered, "0", "No Coverage", false, false);

		helperMiniServices.endorsementRateAndBind(policyNumber);
		PolicyCoverageInfo policyCoverageResponseLsdFinOldVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, newVehicleOid);
		Coverage policyCoverageResponseLsdFinOldVehFiltered = policyCoverageResponseLsdFinOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, policyCoverageResponseLsdFinOldVehFiltered, "0", "No Coverage", false, false);

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		VehicleUpdateDto updateVehicleOwned = new VehicleUpdateDto();
		updateVehicleOwned.vehicleOwnership = new VehicleOwnership();
		updateVehicleOwned.vehicleOwnership.ownership = "OWN";
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleOwned);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo endorsementCoverageResponseOwnedOldVeh2 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid);
		Coverage endorsementCoverageResponseOwnedOldVeh2Filtered = endorsementCoverageResponseOwnedOldVeh2.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.coverageCd)).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, endorsementCoverageResponseOwnedOldVeh2Filtered, "0", "No Coverage", false, false);
	}

	protected void pas14721_UpdateCoveragesServiceBIPD(PolicyType policyType) {
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

		PolicyCoverageInfo coverageResponse = HelperCommon.updatePolicyLevelCoverageEndorsement(policyNumber, coverageCd, newBILimits);
		assertSoftly(softly -> {

			Coverage filteredCoverageResponseBI = coverageResponse.policyCoverages.stream().filter(cov -> "BI".equals(cov.coverageCd)).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseBI.coverageLimit.equals(newBILimits)).isEqualTo(true);
			softly.assertThat("$500,000/$500,000".equals(filteredCoverageResponseBI.coverageLimitDisplay)).isEqualTo(true);

			assertCoverageLimitForBI(coverageResponse);

			Coverage filteredCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat("50000".equals(filteredCoverageResponsePD.coverageLimit)).isEqualTo(true);
			softly.assertThat("$50,000".equals(filteredCoverageResponsePD.coverageLimitDisplay)).isEqualTo(true);

			assertCoverageLimitForPDBI(coverageResponse);
		});

		String coverageCd1 = "PD";
		String newPD1 = "500000";

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updatePolicyLevelCoverageEndorsement(policyNumber, coverageCd1, newPD1);
		assertSoftly(softly -> {

			Coverage filteredCoverageResponse1 = coverageResponse1.policyCoverages.stream().filter(cov -> "BI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponse1.coverageLimit.equals(newBILimits)).isEqualTo(true);
			softly.assertThat("$500,000/$500,000".equals(filteredCoverageResponse1.coverageLimitDisplay)).isEqualTo(true);

			Coverage filteredCoverageResponsePD1 = coverageResponse1.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat("500000".equals(filteredCoverageResponsePD1.coverageLimit)).isEqualTo(true);
			softly.assertThat("$500,000".equals(filteredCoverageResponsePD1.coverageLimitDisplay)).isEqualTo(true);

			assertCoverageLimitForPDBI(coverageResponse1);

		});

		String coverageCd2 = "BI";
		String newBI2 = "50000/100000";

		PolicyCoverageInfo coverageResponse2 = HelperCommon.updatePolicyLevelCoverageEndorsement(policyNumber, coverageCd2, newBI2);
		assertSoftly(softly -> {
			Coverage filteredCoverageResponseBI = coverageResponse2.policyCoverages.stream().filter(cov -> "BI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponseBI.coverageLimit.equals(newBI2)).isEqualTo(true);
			softly.assertThat("$50,000/$100,000".equals(filteredCoverageResponseBI.coverageLimitDisplay)).isEqualTo(true);

			Coverage filteredCoverageResponsePD2 = coverageResponse2.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat("100000".equals(filteredCoverageResponsePD2.coverageLimit)).isEqualTo(true);
			softly.assertThat("$100,000".equals(filteredCoverageResponsePD2.coverageLimitDisplay)).isEqualTo(true);

			assertCoverageLimitForPD(coverageResponse2);
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

		PolicyCoverageInfo coverageResponse = HelperCommon.updatePolicyLevelCoverageEndorsement(policyNumber, coverageCd, newBILimits);
		assertSoftly(softly -> {

			Coverage filteredCoverageResponseBI = coverageResponse.policyCoverages.stream().filter(cov -> "BI".equals(cov.coverageCd)).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseBI.coverageLimit.equals(newBILimits)).isEqualTo(true);
			softly.assertThat("$500,000/$500,000".equals(filteredCoverageResponseBI.coverageLimitDisplay)).isEqualTo(true);

			assertCoverageLimitForBI(coverageResponse);

			Coverage filteredCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat("50000".equals(filteredCoverageResponsePD.coverageLimit)).isEqualTo(true);
			softly.assertThat("$50,000".equals(filteredCoverageResponsePD.coverageLimitDisplay)).isEqualTo(true);

			assertCoverageLimitForPDBI(coverageResponse);

			Coverage filteredCoverageResponseUMBI = coverageResponse.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(newBILimits.equals(filteredCoverageResponseUMBI.coverageLimit)).isEqualTo(true);
			softly.assertThat("$500,000/$500,000".equals(filteredCoverageResponseUMBI.coverageLimitDisplay)).isEqualTo(true);

			validateUIMBI_pas15254(softly, state, coverageResponse, newBILimits); //validate UIMBI for states where it is separate coverage

		});

		String coverageCd1 = "PD";
		String newPD1 = "500000";

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updatePolicyLevelCoverageEndorsement(policyNumber, coverageCd1, newPD1);
		assertSoftly(softly -> {

			Coverage filteredCoverageResponse1 = coverageResponse1.policyCoverages.stream().filter(cov -> "BI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponse1.coverageLimit.equals(newBILimits)).isEqualTo(true);
			softly.assertThat("$500,000/$500,000".equals(filteredCoverageResponse1.coverageLimitDisplay)).isEqualTo(true);

			Coverage filteredCoverageResponsePD1 = coverageResponse1.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat("500000".equals(filteredCoverageResponsePD1.coverageLimit)).isEqualTo(true);
			softly.assertThat("$500,000".equals(filteredCoverageResponsePD1.coverageLimitDisplay)).isEqualTo(true);

			assertCoverageLimitForPDBI(coverageResponse1);

			Coverage filteredCoverageResponseUMBI = coverageResponse1.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(newBILimits.equals(filteredCoverageResponseUMBI.coverageLimit)).isEqualTo(true);
			softly.assertThat("$500,000/$500,000".equals(filteredCoverageResponseUMBI.coverageLimitDisplay)).isEqualTo(true);

			validateUIMBI_pas15254(softly, state, coverageResponse1, newBILimits); //validate UIMBI for states where it is separate coverage

		});

		String coverageCd2 = "BI";
		String newBI2 = "50000/100000";

		PolicyCoverageInfo coverageResponse2 = HelperCommon.updatePolicyLevelCoverageEndorsement(policyNumber, coverageCd2, newBI2);
		assertSoftly(softly -> {
			Coverage filteredCoverageResponseBI = coverageResponse2.policyCoverages.stream().filter(cov -> "BI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponseBI.coverageLimit.equals(newBI2)).isEqualTo(true);
			softly.assertThat("$50,000/$100,000".equals(filteredCoverageResponseBI.coverageLimitDisplay)).isEqualTo(true);

			Coverage filteredCoverageResponsePD2 = coverageResponse2.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat("100000".equals(filteredCoverageResponsePD2.coverageLimit)).isEqualTo(true);
			softly.assertThat("$100,000".equals(filteredCoverageResponsePD2.coverageLimitDisplay)).isEqualTo(true);

			assertCoverageLimitForPD(coverageResponse2);

			Coverage filteredCoverageResponseUMBI = coverageResponse2.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(newBILimits.equals(filteredCoverageResponseUMBI.coverageLimit)).isEqualTo(true);
			softly.assertThat("$50,000/$100,000".equals(filteredCoverageResponseUMBI.coverageLimitDisplay)).isEqualTo(true);

			validateUIMBI_pas15254(softly, state, coverageResponse2, newBILimits); //validate UIMBI for states where it is separate coverage
		});

		helperMiniServices.endorsementRateAndBind(policyNumber);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		helperMiniServices.secondEndorsementIssueCheck();

	}

	protected void pas14646_UimDelimiter(String state, SoftAssertions softly) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		SearchPage.openPolicy(policyNumber);
		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber);
		Coverage filteredPolicyCoverageResponse = policyCoverageResponse.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.coverageCd)).findFirst().orElse(null);
		softly.assertThat(filteredPolicyCoverageResponse.coverageType).isEqualTo("Per Person/Per Accident");
		softly.assertThat(filteredPolicyCoverageResponse.availableLimits.size()).isNotEqualTo(0);

		PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber);
		Coverage filteredCoverageEndorsementResponse = coverageEndorsementResponse.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.coverageCd)).findFirst().orElse(null);
		softly.assertThat(filteredCoverageEndorsementResponse.coverageType).isEqualTo("Per Person/Per Accident");
		softly.assertThat(filteredPolicyCoverageResponse.availableLimits.size()).isNotEqualTo(0);
	}

	protected void pas14648_MedpmDelimiter(PolicyType policyType) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(policyCoverageResponse.policyCoverages.get(4).coverageCd).isEqualTo("MEDPM");
			softly.assertThat(policyCoverageResponse.policyCoverages.get(4).coverageType).isEqualTo("Per Person");
			softly.assertThat(policyCoverageResponse.policyCoverages.get(4).availableLimits.size()).isNotEqualTo(0);
		});

		PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(coverageEndorsementResponse.policyCoverages.get(4).coverageCd).isEqualTo("MEDPM");
			softly.assertThat(coverageEndorsementResponse.policyCoverages.get(4).coverageType).isEqualTo("Per Person");
			softly.assertThat(coverageEndorsementResponse.policyCoverages.get(4).availableLimits.size()).isNotEqualTo(0);
		});
	}

	protected void pas15228_UmUimDelimiterBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		assertSoftly(softly -> {
			PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber);
			Coverage filteredPolicyCoverageResponseUMBI = policyCoverageResponse.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseUMBI.coverageType).isEqualTo("Per Person/Per Accident");
			softly.assertThat(filteredPolicyCoverageResponseUMBI.availableLimits.size()).isNotEqualTo(0);
			softly.assertThat(filteredPolicyCoverageResponseUMBI.canChangeCoverage).isFalse();

			Coverage filteredPolicyCoverageResponseUIMBI = policyCoverageResponse.policyCoverages.stream().filter(cov -> "UIMBI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseUIMBI.coverageType).isEqualTo("Per Person/Per Accident");
			softly.assertThat(filteredPolicyCoverageResponseUIMBI.availableLimits.size()).isNotEqualTo(0);
			softly.assertThat(filteredPolicyCoverageResponseUIMBI.canChangeCoverage).isFalse();

			PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber);
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
			PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber);
			Coverage filteredPolicyCoverageResponseUMPD = policyCoverageResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.coverageCd)).findFirst().orElse(null);
			//BUG: PAS-15829 UMPD not returned from viewPolicyCoverages for NJ (for Policy and Endorsement)
			softly.assertThat(filteredPolicyCoverageResponseUMPD.coverageType).isEqualTo("Per Accident");
			softly.assertThat(filteredPolicyCoverageResponseUMPD.availableLimits.size()).isEqualTo(0);
			softly.assertThat(filteredPolicyCoverageResponseUMPD.canChangeCoverage).isFalse();

			PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber);
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
			PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber);
			assertThat(policyCoverageResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.coverageCd)).findFirst().orElse(null)).isNull();

			PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber);
			assertThat(coverageEndorsementResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.coverageCd)).findFirst().orElse(null)).isNull();
		});
	}

	//validate UIMBI for states where it is separate coverage
	private void validateUIMBI_pas15254(SoftAssertions softly, String state, PolicyCoverageInfo coverageResponse, String newBILimits) {
		if (state.equals(Constants.States.AZ) || state.equals(Constants.States.ID) || state.equals(Constants.States.KY) || state.equals(Constants.States.PA)
				|| state.equals(Constants.States.SD) || state.equals(Constants.States.UT) || state.equals(Constants.States.WV)) {
			Coverage filteredCoverageResponseUIMBI = coverageResponse.policyCoverages.stream().filter(cov -> "UIMBI".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(newBILimits.equals(filteredCoverageResponseUIMBI.coverageLimit)).isEqualTo(true);
		}
	}

	private void coverageXproperties(SoftAssertions softly, int coverageXnumber, PolicyCoverageInfo coverageResponse, String coverageCd, String coverageDesc, String availableLimits, String coverageLimitDisplay, String coverageType, boolean customerDisplay, boolean canChangeCoverage) {
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

	private void assertCoverageLimitForBI(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
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
		});
	}

	private void assertCoverageLimitForPD(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {

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

		});
	}

	private void assertCoverageLimitForPDBI(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
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

		});
	}

	protected void pas14645_ViewCoveragesBiPd(PolicyType policyType) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		SearchPage.openPolicy(policyNumber);

		PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber);
		assertSoftly(softly ->
				viewCoveragesBiPd(policyCoverageResponse, softly)
		);

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo policyCoverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber);
		assertSoftly(softly ->
				viewCoveragesBiPd(policyCoverageEndorsementResponse, softly)
		);
	}

	private void viewCoveragesBiPd(PolicyCoverageInfo policyCoverageResponse, SoftAssertions softly) {
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

		PolicyCoverageInfo coverageResponseV = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, oid);
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
			softly.assertThat(coveragesVehicle.get(6).customerDisplayed).isEqualTo(false);

		});

		// Vehicle2
		Dollar comprehensiveDeductiblePendingV = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)");
		Dollar collisionDeductiblePendingV = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)");
		String fullSafetyGlassVehPendingV = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel());
		String loanLeaseCovPendingV = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), " (+$0.00)");
		Dollar transportationExpensePendingV = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)");
		String towingAndLaborPendingV = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");
		Dollar excessElectronicEquipmentPendingV = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PolicyCoverageInfo coverageResponse = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid);
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
			softly.assertThat(coveragesVehicle2.get(6).customerDisplayed).isEqualTo(false);

			softly.assertThat(coveragesVehicle2.get(7).coverageCd).isEqualTo("NEWCAR");
			softly.assertThat(coveragesVehicle2.get(7).coverageDescription).isEqualTo("New Car Added Protection");
			softly.assertThat(coveragesVehicle2.get(7).customerDisplayed).isEqualTo(false);

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

		PolicyCoverageInfo coverageEndorsementResponseV1 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, oid);
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
			softly.assertThat(coveragesV1.get(6).customerDisplayed).isEqualTo(false);
		});

		//vehicle2
		Dollar comprehensiveDeductiblePendingV2 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)");
		Dollar collisionDeductiblePendingV2 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)");
		String fullSafetyGlassVehPendingV2 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel());
		String loanLeaseCovPendingV2 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), " (+$0.00)");
		Dollar transportationExpensePendingV2 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)");
		String towingAndLaborPendingV2 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");
		Dollar excessElectronicEquipmentPendingV2 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PolicyCoverageInfo coverageEndorsementResponsePendingV2 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid);
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
			softly.assertThat(coveragesVehicle2.get(6).customerDisplayed).isEqualTo(false);
		});
	}

	protected void pas14536_TransportationExpensePart1Body(PolicyType policyType, SoftAssertions softly) {
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

		PolicyCoverageInfo coverageResponse1 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, oid1);

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

	protected void pas14536_TransportationExpensePart2Body(PolicyType policyType, SoftAssertions softly) {
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
		PolicyCoverageInfo updateCoverageResponse1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, coverageCd, availableLimits1);

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
		PolicyCoverageInfo updateCoverageResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, coverageCd, availableLimits2);
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

	protected void pas14536_TransportationExpensePart3Body(PolicyType policyType, SoftAssertions softly) {
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
		PolicyCoverageInfo updateCoverageResponse1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, coverageCd, availableLimits);

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
		PolicyCoverageInfo updateCoverageResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, coverageCd, availableLimits2);
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
}



