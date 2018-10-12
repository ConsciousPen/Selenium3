package aaa.modules.regression.service.helper;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableMap;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ErrorDxpEnum;
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
		String towingAndLabor = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)");
		Dollar excessElectronicEquipment = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PolicyCoverageInfo coverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", comprehensiveDeductible.toPlaingString(), comprehensiveDeductible.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", collisionDeductible.toPlaingString(), collisionDeductible.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh1, "None", true, true);
			assertCoverageLimitFullGlassCov(coverageResponse);

			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "0", loanLeaseCov, "None", true, true);
			assertCoverageLimitLoan(coverageResponse);

			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Transportation Expense", transportationExpense.toPlaingString(), transportationExpense + " (Included)", "Per Occurrence", true, true);
			assertCoverageLimitTransportationExpense(coverageResponse, false);

			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);
			assertCoverageLimitTowingLabor(coverageResponse);

			coverageXproperties(softly, 6, coveragesV1, "SPECEQUIP", "Excess Electronic Equipment", excessElectronicEquipment.toPlaingString(), "$1,000.00", null, true, false);
			coverageXproperties(softly, 7, coveragesV1, "NEWCAR", "New Car Added Protection", "false", "No", null, false, false);

			softly.assertThat(coveragesV1.get(8).coverageCd).isEqualTo("WL");
			softly.assertThat(coveragesV1.get(8).coverageDescription).isEqualTo("Waive Liability");
			softly.assertThat(coveragesV1.get(8).customerDisplayed).isEqualTo(false);

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
		String towingAndLabor1 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)");
		Dollar excessElectronicEquipment1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", comprehensiveDeductible1.toPlaingString(), comprehensiveDeductible1.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageEndorsementResponse);

			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", collisionDeductible1.toPlaingString(), collisionDeductible1.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageEndorsementResponse);

			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "true", fullSafetyGlassVeh2.toString(), "None", true, true);
			assertCoverageLimitFullGlassCov(coverageEndorsementResponse);

			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "1", loanLeaseCov1.toString(), "None", true, true);
			assertCoverageLimitLoan(coverageEndorsementResponse);

			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Transportation Expense", transportationExpense1.toPlaingString(), transportationExpense1.toString(), "Per Occurrence", true, true);
			assertCoverageLimitTransportationExpense(coverageEndorsementResponse, true);

			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "50/300", towingAndLabor1, "Per Disablement/Maximum", true, true);
			assertCoverageLimitTowingLabor(coverageEndorsementResponse);

			coverageXproperties(softly, 6, coveragesV1, "SPECEQUIP", "Excess Electronic Equipment", excessElectronicEquipment1.toPlaingString(), "$1,500.00", null, true, false);
			coverageXproperties(softly, 7, coveragesV1, "NEWCAR", "New Car Added Protection", "false", "No", null, false, false);

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
			coverageXproperties(softly, 0, coveragesLoanVehicle, "COMPDED", "Other Than Collision", comprehensiveDeductible.toPlaingString(), comprehensiveDeductible.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			coverageXproperties(softly, 1, coveragesLoanVehicle, "COLLDED", "Collision Deductible", collisionDeductible.toPlaingString(), collisionDeductible.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			coverageXproperties(softly, 2, coveragesLoanVehicle, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh1.toString(), "None", true, true);
			assertCoverageLimitFullGlassCov(coverageResponse);

			coverageXproperties(softly, 3, coveragesLoanVehicle, "LOAN", "Auto Loan/Lease Coverage", "0", loanLeaseCov1.toString(), "None", true, true);
			assertCoverageLimitLoan(coverageResponse);

			coverageXproperties(softly, 4, coveragesLoanVehicle, "RREIM", "Transportation Expense", transportationExpense.toPlaingString(), transportationExpense + " (Included)", "Per Occurrence", true, true);
			assertCoverageLimitTransportationExpense(coverageResponse, false);

			coverageXproperties(softly, 5, coveragesLoanVehicle, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor.toString(), "Per Disablement/Maximum", true, true);
			assertCoverageLimitTowingLabor(coverageResponse);

			coverageXproperties(softly, 6, coveragesLoanVehicle, "SPECEQUIP", "Excess Electronic Equipment", excessElectronicEquipment.toPlaingString(), "$1,000.00", null, true, false);
			coverageXproperties(softly, 7, coveragesLoanVehicle, "NEWCAR", "New Car Added Protection", "false", "No", null, false, false);

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
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", comprehensiveDeductible1.toPlaingString(), comprehensiveDeductible1.toString(), "Deductible", true, true);
			assertCoverageLimitForCompColl(coverageResponse1);

			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", collisionDeductible1.toPlaingString(), collisionDeductible1.toString(), "Deductible", true, true);
			assertCoverageLimitForCompColl(coverageResponse1);

			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh2.toString(), "None", true, true);
			assertCoverageLimitFullGlassCov(coverageResponse1);

			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", false, false);
			assertCoverageLimitLoan(coverageResponse1);

			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Transportation Expense", transportationExpense1.toPlaingString(), transportationExpense1 + " (Included)", "Per Occurrence", true, true);
			assertCoverageLimitTransportationExpense(coverageResponse1, false);

			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor1.toString(), "Per Disablement/Maximum", true, true);
			assertCoverageLimitTowingLabor(coverageResponse1);

			coverageXproperties(softly, 6, coveragesV1, "SPECEQUIP", "Excess Electronic Equipment", excessElectronicEquipment1.toPlaingString(), "$1,000.00", null, true, false);
			coverageXproperties(softly, 7, coveragesV1, "NEWCAR", "New Car Added Protection", "true", "Yes", null, true, false);

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
			coverageXproperties(softly, coverageCompded, "COMPDED", "Comprehensive Deductible", comprehensiveDeductible.toPlaingString(), comprehensiveDeductible.toString(), "Deductible", true, true);
			availableCoverageCompdedForAz(coverageResponse, softly);

			Coverage coverageCollded = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1);
			coverageXproperties(softly, coverageCollded, "COLLDED", "Collision Deductible", collisionDeductible.toPlaingString(), collisionDeductible.toString(), "Deductible", true, true);

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
			coverageXproperties(softly, coverageGlass, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh1.toString(), "None", true, true);

			softly.assertThat(coverageGlass.availableLimits.get(0).coverageLimit).isEqualTo("false");
			softly.assertThat(coverageGlass.availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageGlass.availableLimits.get(1).coverageLimit).isEqualTo("true");
			softly.assertThat(coverageGlass.availableLimits.get(1).coverageLimitDisplay).isEqualTo("Yes");

			Coverage coverageLoan = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3);
			coverageXproperties(softly, coverageLoan, "LOAN", "Vehicle Loan/Lease Protection", "0", loanLeaseCov.toString(), "None", true, true);

			softly.assertThat(coverageLoan.availableLimits.get(0).coverageLimit).isEqualTo("0");
			softly.assertThat(coverageLoan.availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageLoan.availableLimits.get(1).coverageLimit).isEqualTo("1");
			softly.assertThat(coverageLoan.availableLimits.get(1).coverageLimitDisplay).isEqualTo("Yes");

			Coverage coverageRreim = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4);
			coverageXproperties(softly, coverageRreim, "RREIM", "Rental Reimbursement", "0/0", transportationExpense.toString(), "Per Day/Maximum", true, true);

			softly.assertThat(coverageRreim.availableLimits.get(0).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageRreim.availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageRreim.availableLimits.get(1).coverageLimit).isEqualTo("30/900");
			softly.assertThat(coverageRreim.availableLimits.get(1).coverageLimitDisplay).isEqualTo("$30/$900");

			softly.assertThat(coverageRreim.availableLimits.get(2).coverageLimit).isEqualTo("40/1200");
			softly.assertThat(coverageRreim.availableLimits.get(2).coverageLimitDisplay).isEqualTo("$40/$1,200");

			softly.assertThat(coverageRreim.availableLimits.get(3).coverageLimit).isEqualTo("50/1500");
			softly.assertThat(coverageRreim.availableLimits.get(3).coverageLimitDisplay).isEqualTo("$50/$1,500");

			Coverage coverageTowing = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5);
			coverageXproperties(softly, coverageTowing, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor.toString(), "Per Disablement/Maximum", true, true);

			softly.assertThat(coverageTowing.availableLimits.get(0).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageTowing.availableLimits.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageTowing.availableLimits.get(1).coverageLimit).isEqualTo("50/300");
			softly.assertThat(coverageTowing.availableLimits.get(1).coverageLimitDisplay).isEqualTo("$50/$300");

			Coverage coverageSpeq = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6);
			coverageXproperties(softly, coverageSpeq, "SPECEQUIP", "Special Equipment Coverage", excessElectronicEquipment.toPlaingString(), "$1,000.00", null, true, false);

			Coverage coverageNewcar = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7);
			coverageXproperties(softly, coverageNewcar, "NEWCAR", "New Car Added Protection", "false", "No", null, false, false);

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
		coverageXproperties(softly, endorsementCoverageResponseOwnedOldVeh2Filtered, "LOAN", "Auto Loan/Lease Coverage", coverageLimit, coverageDisplayLimit, "None", customerDisplayed, canChangeCoverage);
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
					List<Coverage> coveragesV1 = coverageResponse.vehicleLevelCoverages.get(0).coverages;
					coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimits, "$" + availableLimits, "Deductible", true, true);
					assertCoverageLimitForCompCollLoanLease(coverageResponse);

					coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", collisionDeductible1.toPlaingString(), collisionDeductible1.toString(), "Deductible", true, true);
					assertCoverageLimitForCompCollLoanLease(coverageResponse);

					coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh1, "None", true, true);
					assertCoverageLimitFullGlassCov(coverageResponse);

					coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "0", loanLeaseCov1, "None", true, true);
					assertCoverageLimitLoan(coverageResponse);

					coverageXproperties(softly, 4, coveragesV1, "RREIM", "Transportation Expense", transportationExpense1.toPlaingString(), transportationExpense1 + " (Included)", "Per Occurrence", true, true);
					assertCoverageLimitTransportationExpense(coverageResponse, false);

					coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor1, "Per Disablement/Maximum", true, true);
					assertCoverageLimitTowingLabor(coverageResponse);
				}
		);

		String coverageCd1 = "COLLDED";
		String availableLimits1 = "100";

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd1, availableLimits1), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse1.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimits, "$" + availableLimits, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimits1, "$" + availableLimits1, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh1, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "0", loanLeaseCov1, "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Transportation Expense", transportationExpense1.toPlaingString(), transportationExpense1 + " (Included)", "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor1, "Per Disablement/Maximum", true, true);
		});

		String coverageCd2 = "GLASS";
		String availableLimits2 = "Yes";

		PolicyCoverageInfo coverageResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd2, availableLimits2), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse2.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimits, "$" + availableLimits, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimits1, "$" + availableLimits1, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "0", loanLeaseCov1, "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Transportation Expense", transportationExpense1.toPlaingString(), transportationExpense1 + " (Included)", "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor1, "Per Disablement/Maximum", true, true);
		});

		String coverageCd3 = "LOAN";
		String availableLimits3 = "1";

		PolicyCoverageInfo coverageResponse3 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd3, availableLimits3), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse3.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimits, "$" + availableLimits, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimits1, "$" + availableLimits1, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", availableLimits3, "Yes", "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Transportation Expense", transportationExpense1.toPlaingString(), transportationExpense1 + " (Included)", "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor1, "Per Disablement/Maximum", true, true);
		});

		String coverageCd4 = "RREIM";
		String availableLimits4 = "900";

		PolicyCoverageInfo coverageResponse4 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd4, availableLimits4), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse4.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimits, "$" + availableLimits, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimits1, "$" + availableLimits1, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", availableLimits3, "Yes", "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Transportation Expense", availableLimits4, "$" + availableLimits4, "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor1, "Per Disablement/Maximum", true, true);
		});

		String coverageCd5 = "TOWINGLABOR";
		String availableLimits5 = "50/300";

		PolicyCoverageInfo coverageResponse5 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd5, availableLimits5), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse5.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimits, "$" + availableLimits, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimits1, "$" + availableLimits1, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", availableLimits3, "Yes", "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Transportation Expense", availableLimits4, "$" + availableLimits4, "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", availableLimits5, "$50/$300", "Per Disablement/Maximum", true, true);
		});

		String coverageCdRemove = "COMPDED";
		String availableLimitsRemove = "-1";

		PolicyCoverageInfo coverageResponse14 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdRemove, availableLimitsRemove), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse14.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimitsRemove, "No Coverage", "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsRemove, "No Coverage", "Deductible", true, false);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, false);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", true, false);
			//coverageXproperties(softly, 4, coverageResponse14, "RREIM", "Transportation Expense", "0", "0", "Per Occurrence", true, false);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, false);
		});

		String coverageCdChange = "COMPDED";
		String availableLimitsChange = "500";

		PolicyCoverageInfo coverageResponse6 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChange, availableLimitsChange), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse6.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsRemove, "No Coverage", "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", true, false);
			//	coverageXproperties(softly, 4, coverageResponse6, "RREIM", "Transportation Expense", "0", "0", "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, false);
		});

		String coverageCdChangeColl = "COLLDED";
		String availableLimitsChangeColl = "500";

		PolicyCoverageInfo coverageResponse7 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeColl, availableLimitsChangeColl), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse7.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", true, true);
			//coverageXproperties(softly, 4, coverageResponse7, "RREIM", "Transportation Expense","0", "0", "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);
		});

		String coverageCdChangeGlass = "GLASS";
		String availableLimitsChangeGlass = "Yes";

		PolicyCoverageInfo coverageResponse8 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeGlass, availableLimitsChangeGlass), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse8.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "true", availableLimitsChangeGlass, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", true, true);
			//coverageXproperties(softly, 4, coverageResponse8, "RREIM", "Transportation Expense","0", "0", "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);
		});

		String coverageCdChangeGlassNoCov = "GLASS";
		String availableLimitsChangeGlassNoCov = "No Coverage";

		PolicyCoverageInfo coverageResponse9 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeGlassNoCov, availableLimitsChangeGlassNoCov), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse9.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", true, true);
			//	coverageXproperties(softly, 4, coverageResponse9, "RREIM", "Transportation Expense","0", "0", "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeLoanNoCov = "LOAN";
		String availableLimitsChangeLoanNoCov = "1";

		PolicyCoverageInfo coverageResponse10 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeLoanNoCov, availableLimitsChangeLoanNoCov), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse10.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "1", "Yes", "None", true, true);
			//coverageXproperties(softly, 4, coverageResponse10, "RREIM", "Transportation Expense", availableLimits4, availableLimits4, "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeTransport = "RREIM";
		String availableLimitsChangeTransport = "900";

		PolicyCoverageInfo coverageResponse11 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTransport, availableLimitsChangeTransport), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse11.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "1", "Yes", "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Transportation Expense", availableLimitsChangeTransport, "$" + availableLimitsChangeTransport, "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeTowing = "TOWINGLABOR";
		String availableLimitsChangeTowing = "50/300";

		PolicyCoverageInfo coverageResponse12 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTowing, availableLimitsChangeTowing), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse12.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "1", "Yes", "None", true, true);
			//	coverageXproperties(softly, 4, coverageResponse12, "RREIM", "Transportation Expense", availableLimitsChangeTransport, availableLimitsChangeTransport, "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", availableLimitsChangeTowing, "$50/$300", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeTowingNoCov = "TOWINGLABOR";
		String availableLimitsChangeTowingNoCov = "0/0";

		PolicyCoverageInfo coverageResponse13 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTowingNoCov, availableLimitsChangeTowingNoCov), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse13.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "1", "Yes", "None", true, true);
			//coverageXproperties(softly, 4, coverageResponse13, "RREIM", "Transportation Expense", availableLimitsChangeTransport, availableLimitsChangeTransport, "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", availableLimitsChangeTowingNoCov, "No Coverage", "Per Disablement/Maximum", true, true);

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
			List<Coverage> coveragesV1 = coverageResponse.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimits, "$" + availableLimits, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", collisionDeductible.toPlaingString(), collisionDeductible.toString(), "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "0", loanLeaseCov, "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Rental Reimbursement", "0/0", transportationExpense, "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);
		});

		String coverageCd1 = "COLLDED";
		String availableLimits1 = "100";

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd1, availableLimits1), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse1.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimits, "$" + availableLimits, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimits1, "$" + availableLimits1, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "0", loanLeaseCov, "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Rental Reimbursement", "0/0", transportationExpense, "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);
		});

		String coverageCd2 = "GLASS";
		String availableLimits2 = "Yes";

		PolicyCoverageInfo coverageResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd2, availableLimits2), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse2.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimits, "$" + availableLimits, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimits1, "$" + availableLimits1, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "0", loanLeaseCov, "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Rental Reimbursement", "0/0", transportationExpense, "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);
		});

		String coverageCd3 = "LOAN";
		String availableLimits3 = "1";

		PolicyCoverageInfo coverageResponse3 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd3, availableLimits3), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse3.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimits, "$" + availableLimits, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimits1, "$" + availableLimits1, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Rental Reimbursement", "0/0", transportationExpense, "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);
		});

		String coverageCd4 = "RREIM";
		String availableLimits4 = "30/900";

		PolicyCoverageInfo coverageResponse4 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd4, availableLimits4), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse4.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimits, "$" + availableLimits, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimits1, "$" + availableLimits1, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Rental Reimbursement", availableLimits4, "$30/$900", "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);
		});

		String coverageCd5 = "TOWINGLABOR";
		String availableLimits5 = "50/300";

		PolicyCoverageInfo coverageResponse5 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd5, availableLimits5), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse5.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimits, "$" + availableLimits, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimits1, "$" + availableLimits1, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "true", availableLimits2, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Rental Reimbursement", availableLimits4, "$30/$900", "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", availableLimits5, "$50/$300", "Per Disablement/Maximum", true, true);
		});

		String coverageCdRemove = "COMPDED";
		String availableLimitsRemove = "-1";

		PolicyCoverageInfo coverageResponse6 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdRemove, availableLimitsRemove), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse6.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimitsRemove, "No Coverage", "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsRemove, "No Coverage", "Deductible", true, false);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, false);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "0", "No Coverage", "None", true, false);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Rental Reimbursement", "0/0", "No Coverage", "Per Day/Maximum", true, false);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, false);
		});

		String coverageCdChange = "COMPDED";
		String availableLimitsChange = "500";

		PolicyCoverageInfo coverageResponse7 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChange, availableLimitsChange), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse7.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsRemove, "No Coverage", "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "0", "No Coverage", "None", true, false);
			//coverageXproperties(softly, 4, coverageResponse7, "RREIM", "Rental Reimbursement", availableLimits4, availableLimits4, "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, false);
		});

		String coverageCdChangeColl = "COLLDED";
		String availableLimitsChangeColl = "500";

		PolicyCoverageInfo coverageResponse8 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeColl, availableLimitsChangeColl), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse8.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "0", "No Coverage", "None", true, true);
			//coverageXproperties(softly, 4, coverageResponse8, "RREIM", "Rental Reimbursement", availableLimits4, availableLimits4, "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);
		});

		String coverageCdChangeGlass = "GLASS";
		String availableLimitsChangeGlass = "Yes";

		PolicyCoverageInfo coverageResponse9 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeGlass, availableLimitsChangeGlass), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse9.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "true", availableLimitsChangeGlass, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "0", "No Coverage", "None", true, true);
			//coverageXproperties(softly, 4, coverageResponse9, "RREIM", "Rental Reimbursement", availableLimits4, availableLimits4, "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);
		});

		String coverageCdChangeGlassNoCov = "GLASS";
		String availableLimitsChangeGlassNoCov = "No Coverage";

		PolicyCoverageInfo coverageResponse10 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeGlassNoCov, availableLimitsChangeGlassNoCov), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse10.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "0", "No Coverage", "None", true, true);
			//coverageXproperties(softly, 4, coverageResponse10, "RREIM", "Rental Reimbursement", availableLimits4, availableLimits4, "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeLoanNoCov = "LOAN";
		String availableLimitsChangeLoanNoCov = "1";

		PolicyCoverageInfo coverageResponse11 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeLoanNoCov, availableLimitsChangeLoanNoCov), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse11.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);
			//coverageXproperties(softly, 4, coverageResponse11, "RREIM", "Rental Reimbursement", availableLimits4, availableLimits4, "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);
		});

		String coverageCdChangeTransport = "RREIM";
		String availableLimitsChangeTransport = "30/900";

		PolicyCoverageInfo coverageResponse12 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTransport, availableLimitsChangeTransport), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse12.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Rental Reimbursement", availableLimitsChangeTransport, "$30/$900", "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);
		});

		String coverageCdChangeTowing = "TOWINGLABOR";
		String availableLimitsChangeTowing = "50/300";

		PolicyCoverageInfo coverageResponse13 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTowing, availableLimitsChangeTowing), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse13.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Rental Reimbursement", availableLimitsChangeTransport, "$30/$900", "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", availableLimitsChangeTowing, "$50/$300", "Per Disablement/Maximum", true, true);
		});

		String coverageCdChangeTowingNoCov = "TOWINGLABOR";
		String availableLimitsChangeTowingNoCov = "0/0";

		PolicyCoverageInfo coverageResponse14 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTowingNoCov, availableLimitsChangeTowingNoCov), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse14.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", availableLimitsChangeGlassNoCov, "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Rental Reimbursement", availableLimitsChangeTransport, "$30/$900", "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", availableLimitsChangeTowingNoCov, "No Coverage", "Per Disablement/Maximum", true, true);
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
		coverageXproperties(softly, 4, coveragesVehicle, "RREIM", "Rental Reimbursement", "0/0", "No Coverage", "Per Day/Maximum", true, true);
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

	protected void pas17646_OrderOfCoverageBody(ETCSCoreSoftAssertions softly) {
		String mapKey = "common";
		List<String> deltaStateList = Arrays.asList("KY", "SD");
		if (deltaStateList.contains(getState())) {
			mapKey = getState();
		}

		//Expected order of coverages (common)
		List<String> orderOfPolicyCoveragesExpectedCommon = Arrays.asList("BI", "PD", "UMBI", "UMPD", "MEDPM", "IL");
		List<String> orderOfVehicleCoveragesExpectedCommon = Arrays.asList("COMPDED", "COLLDED", "GLASS", "LOAN", "RREIM", "TOWINGLABOR", "SPECEQUIP", "NEWCAR", "WL");

		//Expected order of KY coverages
		List<String> orderOfPolicyCoveragesExpectedKY = Arrays.asList("BI", "PD", "UMBI", "UIMBI", "BPIP", "ADDPIP", "PIPDED", "GPIP");
		List<String> orderOfVehicleCoveragesExpectedKY = Arrays.asList("COMPDED", "COLLDED", "LOAN", "RREIM", "TOWINGLABOR", "SPECEQUIP", "NEWCAR");
		List<String> orderOfDriverCoveragesExpectedKY = Arrays.asList("ADB", "TORT");

		//Expected order of SD coverages
		List<String> orderOfPolicyCoveragesExpectedSD = Arrays.asList("BI", "PD", "UMBI", "UMPD", "MEDPM");
		List<String> orderOfVehicleCoveragesExpectedSD = Arrays.asList("COMPDED", "COLLDED", "GLASS", "LOAN", "RREIM", "TOWINGLABOR", "SPECEQUIP", "NEWCAR");
		List<String> orderOfDriverCoveragesExpectedSD = Arrays.asList("ADB", "TDB");

		//map coverages
		Map<String, List<String>> mapPolicyCoverages = new LinkedHashMap<>();
		mapPolicyCoverages.put("common", orderOfPolicyCoveragesExpectedCommon);
		mapPolicyCoverages.put("KY", orderOfPolicyCoveragesExpectedKY);
		mapPolicyCoverages.put("SD", orderOfPolicyCoveragesExpectedSD);

		Map<String, List<String>> mapVehicleCoverages = new LinkedHashMap<>();
		mapVehicleCoverages.put("common", orderOfVehicleCoveragesExpectedCommon);
		mapVehicleCoverages.put("KY", orderOfVehicleCoveragesExpectedKY);
		mapVehicleCoverages.put("SD", orderOfVehicleCoveragesExpectedSD);

		Map<String, List<String>> mapDriverCoverages = new LinkedHashMap<>(); //do not have requirements regarding to driver coverages for all states
		mapDriverCoverages.put("KY", orderOfDriverCoveragesExpectedKY);
		mapDriverCoverages.put("SD", orderOfDriverCoveragesExpectedSD);

		mainApp().open();
		String policyNumber = getCopiedPolicy();
		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//Run viewEndorsementCoverages and validate order of coverages in response
		PolicyCoverageInfo policyCoverageInfo = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		validateOrderOfAllLevelCoverages(softly, mapPolicyCoverages.get(mapKey), mapVehicleCoverages.get(mapKey), mapDriverCoverages.get(mapKey), policyCoverageInfo);

		//Run updateCoverage service and validate order of coverages in response
		Coverage coverageToUpdate = policyCoverageInfo.policyCoverages.get(0);
		String newLimit = coverageToUpdate.availableLimits.get(0).coverageLimit;
		UpdateCoverageRequest updateCoverageRequest = DXPRequestFactory.createUpdateCoverageRequest(coverageToUpdate.coverageCd, newLimit);
		policyCoverageInfo = HelperCommon.updateEndorsementCoverage(policyNumber, updateCoverageRequest, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		validateOrderOfAllLevelCoverages(softly, mapPolicyCoverages.get(mapKey), mapVehicleCoverages.get(mapKey), mapDriverCoverages.get(mapKey), policyCoverageInfo);

		//NOTE: Validation of Change History is too complicated for automation - have to update every coverage. Should be tested manually if needed.
	}

	private void validateOrderOfAllLevelCoverages(ETCSCoreSoftAssertions softly, List<String> orderOfPolicyCoveragesExpected, List<String> orderOfVehicleCoveragesExpected, List<String> orderOfDriverCoveragesExpected, PolicyCoverageInfo coverageEndorsementResponse) {
		validateOrderOfCoverages(softly, orderOfPolicyCoveragesExpected, coverageEndorsementResponse.policyCoverages);
		validateOrderOfCoverages(softly, orderOfVehicleCoveragesExpected, coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages);
		if (Constants.States.KY.equals(getState()) || Constants.States.SD.equals(getState())) { //do not have requirements regarding to driver coverages for all states
			validateOrderOfCoverages(softly, orderOfDriverCoveragesExpected, coverageEndorsementResponse.driverCoverages);
		}
	}

	private void validateOrderOfCoverages(ETCSCoreSoftAssertions softly, List<String> orderOfCoveragesExpected, List<Coverage> coveragesActual) {
		softly.assertThat(coveragesActual.size()).isEqualTo(orderOfCoveragesExpected.size());
		for (String coverageCD : orderOfCoveragesExpected) {
			int index = orderOfCoveragesExpected.indexOf(coverageCD);
			softly.assertThat(coveragesActual.get(index).coverageCd).as(coverageCD + " is expected to be at index " + index).isEqualTo(coverageCD);
		}
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

	private void coverageXproperties(ETCSCoreSoftAssertions softly, int coverageXnumber, List<Coverage> coverageList, String coverageCd, String coverageDesc, String coverageLimit, String coverageLimitDisplay, String coverageType, boolean customerDisplay, boolean canChangeCoverage) {
		Coverage coverage = coverageList.get(coverageXnumber);
		coverageXproperties(softly, coverage, coverageCd, coverageDesc, coverageLimit, coverageLimitDisplay, coverageType, customerDisplay, canChangeCoverage);
	}

	private void coverageXproperties(ETCSCoreSoftAssertions softly, Coverage coverage, String coverageCd, String coverageDesc, String coverageLimit, String coverageLimitDisplay, String coverageType, boolean customerDisplay, boolean canChangeCoverage) {
		softly.assertThat(coverage.coverageCd).isEqualTo(coverageCd);
		softly.assertThat(coverage.coverageDescription).isEqualTo(coverageDesc);

		//check coverageLimit
		if (StringUtils.isEmpty(coverageLimit)) {
			softly.assertThat(coverage.coverageLimit).isEqualTo(coverageLimit);
		} else {
			softly.assertThat(coverage.coverageLimit).isEqualTo(coverageLimit.replace(".00", ""));
		}

		//check coverageLimitDisplay
		if (StringUtils.isEmpty(coverageLimitDisplay)) {
			softly.assertThat(coverage.coverageLimitDisplay).isEqualTo(coverageLimitDisplay);
		} else {
			//for SPECEQUIP and CUSTEQUIP coverageLimitDisplay should be with ".00", for other coverages without ".00"
			if ("SPECEQUIP, CUSTEQUIP".contains(coverage.coverageCd)) {
				softly.assertThat(coverage.coverageLimitDisplay).isEqualTo(coverageLimitDisplay.toString().replace("(+$0)", "").trim());
			} else {
				softly.assertThat(coverage.coverageLimitDisplay).isEqualTo(coverageLimitDisplay.toString().replace(".00", "").replace("(+$0)", "").trim());
			}
		}

		softly.assertThat(coverage.coverageType).isEqualTo(coverageType);
		softly.assertThat(coverage.customerDisplayed).isEqualTo(customerDisplay);
		softly.assertThat(coverage.canChangeCoverage).isEqualTo(canChangeCoverage);
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

				Coverage filteredCoverageResponseBI = coverageResponse.policyCoverages.stream().filter(cov -> "BI".equals(cov.coverageCd)).findFirst().orElse(null);
				List<CoverageLimit> availableLimits = filteredCoverageResponseBI.availableLimits;

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

				Coverage filteredCoverageResponseBI = coverageResponse.policyCoverages.stream().filter(cov -> "BI".equals(cov.coverageCd)).findFirst().orElse(null);
				List<CoverageLimit> availableLimits = filteredCoverageResponseBI.availableLimits;

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
				Coverage filteredCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);
				List<CoverageLimit> availableLimitsPD = filteredCoverageResponsePD.availableLimits;

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
				Coverage filteredCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);
				List<CoverageLimit> availableLimitsPD = filteredCoverageResponsePD.availableLimits;

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

				Coverage filteredCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);
				List<CoverageLimit> availableLimitsPDBI = filteredCoverageResponsePD.availableLimits;

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
				Coverage filteredCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);
				List<CoverageLimit> availableLimitsPDBI = filteredCoverageResponsePD.availableLimits;

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
		coverageXproperties(softly, coverageLimit, "BI", "Bodily Injury Liability", "100000/300000", "$100,000/$300,000", "Per Person/Per Accident", true, true);

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
		coverageXproperties(softly, coveragePD, "PD", "Property Damage Liability", "50000", "$50,000", "Per Accident", true, true);

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
			coverageXproperties(softly, 0, coveragesVehicle, "COMPDED", "Other Than Collision", comprehensiveDeductible.toPlaingString(), comprehensiveDeductible.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageResponseV);

			coverageXproperties(softly, 1, coveragesVehicle, "COLLDED", "Collision Deductible", collisionDeductible.toPlaingString(), collisionDeductible.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageResponseV);

			coverageXproperties(softly, 2, coveragesVehicle, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh.toString(), "None", true, true);
			assertCoverageLimitFullGlassCov(coverageResponseV);

			coverageXproperties(softly, 3, coveragesVehicle, "LOAN", "Auto Loan/Lease Coverage", "0", loanLeaseCov.toString(), "None", true, true);
			assertCoverageLimitLoan(coverageResponseV);

			coverageXproperties(softly, 4, coveragesVehicle, "RREIM", "Transportation Expense", transportationExpense.toPlaingString(), transportationExpense + " (Included)", "Per Occurrence", true, true);

			assertCoverageLimitTransportationExpense(coverageResponseV, false);

			coverageXproperties(softly, 5, coveragesVehicle, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor.toString(), "Per Disablement/Maximum", true, true);
			assertCoverageLimitTowingLabor(coverageResponseV);

			coverageXproperties(softly, 6, coveragesVehicle, "SPECEQUIP", "Excess Electronic Equipment", excessElectronicEquipment.toPlaingString(), "$1,000.00", null, true, false);
			coverageXproperties(softly, 7, coveragesVehicle, "NEWCAR", "New Car Added Protection", "false", "No", null, false, false);
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
			coverageXproperties(softly, 0, coveragesVehicle2, "COMPDED", "Other Than Collision", comprehensiveDeductiblePendingV.toPlaingString(), comprehensiveDeductiblePendingV.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			coverageXproperties(softly, 1, coveragesVehicle2, "COLLDED", "Collision Deductible", collisionDeductiblePendingV.toPlaingString(), collisionDeductiblePendingV.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			coverageXproperties(softly, 2, coveragesVehicle2, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVehPendingV.toString(), "None", true, true);
			assertCoverageLimitFullGlassCov(coverageResponse);

			coverageXproperties(softly, 3, coveragesVehicle2, "LOAN", "Auto Loan/Lease Coverage", "0", loanLeaseCovPendingV.toString(), "None", true, true);
			assertCoverageLimitLoan(coverageResponse);

			coverageXproperties(softly, 4, coveragesVehicle2, "RREIM", "Transportation Expense", transportationExpensePendingV.toPlaingString(), transportationExpensePendingV + " (Included)", "Per Occurrence", true, true);
			assertCoverageLimitTransportationExpense(coverageResponse, false);

			coverageXproperties(softly, 5, coveragesVehicle2, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLaborPendingV.toString(), "Per Disablement/Maximum", true, true);
			assertCoverageLimitTowingLabor(coverageResponse);

			coverageXproperties(softly, 6, coveragesVehicle2, "SPECEQUIP", "Excess Electronic Equipment", excessElectronicEquipmentPendingV.toPlaingString(), "$1,000.00", null, true, false);
			coverageXproperties(softly, 7, coveragesVehicle2, "NEWCAR", "New Car Added Protection", "false", "No", null, false, false);

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
		String towingAndLaborPendingV1 = getCoverages(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)");
		Dollar excessElectronicEquipmentPendingV1 = getCoverage(1, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PolicyCoverageInfo coverageEndorsementResponseV1 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, oid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageEndorsementResponseV1.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", comprehensiveDeductiblePendingV1.toPlaingString(), comprehensiveDeductiblePendingV1.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageEndorsementResponseV1);

			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", collisionDeductiblePendingV1.toPlaingString(), collisionDeductiblePendingV1.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageEndorsementResponseV1);

			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "true", fullSafetyGlassVehPendingV1.toString(), "None", true, true);
			assertCoverageLimitFullGlassCov(coverageEndorsementResponseV1);

			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "1", loanLeaseCovPendingV1.toString(), "None", true, true);
			assertCoverageLimitLoan(coverageEndorsementResponseV1);

			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Transportation Expense", transportationExpensePendingV1.toPlaingString(), transportationExpensePendingV1.toString(), "Per Occurrence", true, true);
			assertCoverageLimitTransportationExpense(coverageEndorsementResponseV1, true);

			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "50/300", towingAndLaborPendingV1.toString(), "Per Disablement/Maximum", true, true);
			assertCoverageLimitTowingLabor(coverageEndorsementResponseV1);

			coverageXproperties(softly, 6, coveragesV1, "SPECEQUIP", "Excess Electronic Equipment", excessElectronicEquipmentPendingV1.toPlaingString(), "$1,500.00", null, true, false);

			coverageXproperties(softly, 7, coveragesV1, "NEWCAR", "New Car Added Protection", "false", "No", null, false, false);
		});

		//vehicle2
		Dollar comprehensiveDeductiblePendingV2 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.OTHER_THAN_COLLISION.getLabel(), "  (+$0.00)");
		Dollar collisionDeductiblePendingV2 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)");
		String fullSafetyGlassVehPendingV2 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel());
		String loanLeaseCovPendingV2 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), " (+$0.00)");
		Dollar transportationExpensePendingV2 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)");
		String towingAndLaborPendingV2 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)");
		Dollar excessElectronicEquipmentPendingV2 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.EXCESS_ELECTRONIC_EQUIPMENT.getLabel(), "");

		PolicyCoverageInfo coverageEndorsementResponsePendingV2 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			List<Coverage> coveragesVehicle2 = coverageEndorsementResponsePendingV2.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesVehicle2, "COMPDED", "Other Than Collision", comprehensiveDeductiblePendingV2.toPlaingString(), comprehensiveDeductiblePendingV2.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageEndorsementResponsePendingV2);

			coverageXproperties(softly, 1, coveragesVehicle2, "COLLDED", "Collision Deductible", collisionDeductiblePendingV2.toPlaingString(), collisionDeductiblePendingV2.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageEndorsementResponsePendingV2);

			coverageXproperties(softly, 2, coveragesVehicle2, "GLASS", "Full Safety Glass", "true", fullSafetyGlassVehPendingV2.toString(), "None", true, true);
			assertCoverageLimitFullGlassCov(coverageEndorsementResponsePendingV2);

			coverageXproperties(softly, 3, coveragesVehicle2, "LOAN", "Auto Loan/Lease Coverage", "1", loanLeaseCovPendingV2.toString(), "None", true, true);
			assertCoverageLimitLoan(coverageEndorsementResponsePendingV2);

			coverageXproperties(softly, 4, coveragesVehicle2, "RREIM", "Transportation Expense", transportationExpensePendingV2.toPlaingString(), transportationExpensePendingV2.toString(), "Per Occurrence", true, true);
			assertCoverageLimitTransportationExpense(coverageEndorsementResponsePendingV2, true);

			coverageXproperties(softly, 5, coveragesVehicle2, "TOWINGLABOR", "Towing and Labor Coverage", "50/300", towingAndLaborPendingV2.toString(), "Per Disablement/Maximum", true, true);
			assertCoverageLimitTransportationExpense(coverageEndorsementResponsePendingV2, true);

			coverageXproperties(softly, 6, coveragesVehicle2, "SPECEQUIP", "Excess Electronic Equipment", excessElectronicEquipmentPendingV2.toPlaingString(), "$1,500.00", null, true, false);
			coverageXproperties(softly, 7, coveragesVehicle2, "NEWCAR", "New Car Added Protection", "false", "No", null, false, false);
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
		coverageXproperties(softly, 4, coveragesVehicle, "RREIM", "Transportation Expense", "600", "$600 (Included)", "Per Occurrence", true, true);
		//Bug PAS-15473: Transportation Expense: coverageLimit is not displaying for new added vehicle
		assertCoverageLimitTransportationExpense(coverageResponse1, false);

		//Other Than Collision
		coverageXproperties(softly, 0, coveragesVehicle, "COMPDED", "Other Than Collision", "250", "$250", "Deductible", true, true);
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
		//can not use coverageXproperties() because coverageLimitDisplay is expected to be null
		softly.assertThat(coveragesVehicle.get(4).coverageCd).isEqualTo("RREIM");
		softly.assertThat(coveragesVehicle.get(4).coverageDescription).isEqualTo("Transportation Expense");
		softly.assertThat(coveragesVehicle.get(4).coverageLimit).isEqualTo("0");
		softly.assertThat(coveragesVehicle.get(4).coverageLimitDisplay).isEqualTo(null);
		softly.assertThat(coveragesVehicle.get(4).coverageType).isEqualTo("Per Occurrence");
		softly.assertThat(coveragesVehicle.get(4).customerDisplayed).isEqualTo(true);
		softly.assertThat(coveragesVehicle.get(4).canChangeCoverage).isEqualTo(false);

		List<CoverageLimit> availableLimits = coveragesVehicle.get(4).availableLimits;
		softly.assertThat(availableLimits.get(0).coverageLimit).isEqualTo("600");
		softly.assertThat(availableLimits.get(0).coverageLimitDisplay).isEqualTo("$600 (Included)");
		softly.assertThat(availableLimits.get(1).coverageLimit).isEqualTo("1200");
		softly.assertThat(availableLimits.get(1).coverageLimitDisplay).isEqualTo("$1,200");

		//Other Than Collision
		coverageXproperties(softly, 0, coveragesVehicle, "COMPDED", "Other Than Collision", "-1", "No Coverage", "Deductible", true, true);
		assertCoverageLimitForCompColl(updateCoverageResponse1);

		//Add COMPDED coverage again and check Transportation Expense
		PolicyCoverageInfo updateCoverageResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, availableLimits2), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		List<Coverage> coveragesVehicle2 = updateCoverageResponse2.vehicleLevelCoverages.get(0).coverages;
		coverageXproperties(softly, 4, coveragesVehicle2, "RREIM", "Transportation Expense", "600", "$600 (Included)", "Per Occurrence", true, true);

		List<CoverageLimit> availableLimitsNd = coveragesVehicle2.get(4).availableLimits;
		softly.assertThat(availableLimitsNd.get(0).coverageLimit).isEqualTo("600");
		softly.assertThat(availableLimitsNd.get(0).coverageLimitDisplay).isEqualTo("$600 (Included)");
		softly.assertThat(availableLimitsNd.get(1).coverageLimit).isEqualTo("1200");
		softly.assertThat(availableLimitsNd.get(1).coverageLimitDisplay).isEqualTo("$1,200");

		//Other Than Collision
		coverageXproperties(softly, 0, coveragesVehicle2, "COMPDED", "Other Than Collision", "100", "$100", "Deductible", true, true);
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
		//can not use coverageXproperties() because coverageLimitDisplay is expected to be null
		softly.assertThat(coveragesVehicle.get(4).coverageCd).isEqualTo("RREIM");
		softly.assertThat(coveragesVehicle.get(4).coverageDescription).isEqualTo("Transportation Expense");
		softly.assertThat(coveragesVehicle.get(4).coverageLimit).isEqualTo("0");
		softly.assertThat(coveragesVehicle.get(4).coverageLimitDisplay).isEqualTo(null);
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
		coverageXproperties(softly, 0, coveragesVehicle, "COMPDED", "Other Than Collision", "-1", "No Coverage", "Deductible", true, true);
		assertCoverageLimitForCompColl(updateCoverageResponse1);

		//Add COMPDED coverage again and check Transportation Expense
		PolicyCoverageInfo updateCoverageResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, availableLimits2), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		List<Coverage> coveragesVehicle2 = updateCoverageResponse2.vehicleLevelCoverages.get(0).coverages;

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
		coverageXproperties(softly, 0, coveragesVehicle2, "COMPDED", "Other Than Collision", "100", "$100", "Deductible", true, true);
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

	protected void pas17629_Umuim_Update_coverageBody(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo viewCoverageResponse = HelperCommon.viewEndorsementCoverages(policyNumber);
		assertSoftly(softly -> {

			softly.assertThat(viewCoverageResponse.policyCoverages.get(0).coverageLimit).isEqualTo("100000/300000");
			softly.assertThat(viewCoverageResponse.policyCoverages.get(2).coverageLimit).isEqualTo("100000/300000");
		});

		String coverageCd = "BI";
		String newBILimits = "25000/50000";

		PolicyCoverageInfo coverageResponse = HelperCommon.updatePolicyLevelCoverageEndorsement(policyNumber, coverageCd, newBILimits);
		assertSoftly(softly -> {

			Coverage filteredCoverageResponseBI1 = coverageResponse.policyCoverages.stream().filter(cov -> "BI".equals(cov.coverageCd)).findFirst().orElse(null);
			Coverage filteredCoverageResponseUMUIM1 = coverageResponse.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.coverageCd)).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseBI1.coverageLimit).isEqualTo("25000/50000");
			softly.assertThat(filteredCoverageResponseUMUIM1.coverageLimit).isEqualTo("50000/50000");
		});
	}

	protected void pas16035ViewCoverageUpdateCoverageBody(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo viewCoverageResponse = HelperCommon.viewEndorsementCoverages(policyNumber);
		assertSoftly(softly -> {
			Coverage filteredCoverageResponseUmpd = viewCoverageResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.coverageCd)).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseUmpd.coverageType).isEqualTo("Per Accident");
			softly.assertThat(filteredCoverageResponseUmpd.customerDisplayed).isEqualTo(true);
			softly.assertThat(filteredCoverageResponseUmpd.canChangeCoverage).isEqualTo(false);
		});
		//To Do uncomment after PAS 15788 Done
	/*	String coverageCd1 = "PD";
		String newBILimits1 = "15000";

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updatePolicyLevelCoverageEndorsement(policyNumber, coverageCd1, newBILimits1);
		assertSoftly(softly -> {

			Coverage filteredCoverageResponseUmpd = coverageResponse1.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.coverageCd)).findFirst().orElse(null);
			Coverage filteredCoverageResponsePD = coverageResponse1.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseUmpd.coverageLimit).isEqualTo("25000");
			softly.assertThat(filteredCoverageResponsePD.coverageLimit).isEqualTo(newBILimits1);
		}); */

		String coverageCd = "PD";
		String newBILimits = "300000";

		PolicyCoverageInfo coverageResponse = HelperCommon.updatePolicyLevelCoverageEndorsement(policyNumber, coverageCd, newBILimits);
		assertSoftly(softly -> {

			Coverage filteredCoverageResponseUmpd = coverageResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.coverageCd)).findFirst().orElse(null);
			Coverage filteredCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseUmpd.coverageLimit).isEqualTo(newBILimits);
			softly.assertThat(filteredCoverageResponsePD.coverageLimit).isEqualTo(newBILimits);
		});

	}

	protected void pas17628_pas17628_ViewCoverageUpdateCoverageUmpdDeductibleBody(PolicyType policyType) {
		mainApp().open();

		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Perform Endorsementgbvgfc
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo viewCoverageResponse = HelperCommon.viewEndorsementCoverages(policyNumber);
		assertSoftly(softly -> {

			List<Coverage> coveragesV1 = viewCoverageResponse.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "UMPDDED", "Uninsured Motorist Property Damage Deductible", "250", "$250", null, true, true);

			softly.assertThat(viewCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(0).coverageLimit).isEqualTo("0");
			softly.assertThat(viewCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(1).coverageLimit).isEqualTo("250");
			softly.assertThat(viewCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(0).coverageLimitDisplay).isEqualTo("$0");
			softly.assertThat(viewCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).availableLimits.get(1).coverageLimitDisplay).isEqualTo("$250");

		});

		goToPasAndChangeUMPD(policyNumber);

		String coverageCd = "PD";
		String newBILimits = "300000";

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updatePolicyLevelCoverageEndorsement(policyNumber, coverageCd, newBILimits);
		assertSoftly(softly -> {

			Coverage filteredCoverageResponseUmpd = coverageResponse1.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.coverageCd)).findFirst().orElse(null);
			Coverage filteredCoverageResponsePD = coverageResponse1.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseUmpd.coverageLimit).isEqualTo(newBILimits);
			softly.assertThat(filteredCoverageResponsePD.coverageLimit).isEqualTo(newBILimits);

			softly.assertThat(viewCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageDescription).isEqualTo("Uninsured Motorist Property Damage Deductible");

			softly.assertThat(viewCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageCd).isEqualTo("UMPDDED");
			softly.assertThat(viewCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimit).isEqualTo("250");
		});

		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";
		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
		assertThat(addVehicle.oid).isNotEmpty();
		String oid1 = addVehicle.oid;
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, oid1);

		String new_Umpdded = "0";
		String new_CoverageCd = "UMPDDED";

		PolicyCoverageInfo updateCoverageResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(new_CoverageCd, new_Umpdded), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			softly.assertThat(updateCoverageResponse2.vehicleLevelCoverages.get(0).coverages.get(0).coverageCd).isEqualTo("UMPDDED");
			softly.assertThat(updateCoverageResponse2.vehicleLevelCoverages.get(0).coverages.get(0).coverageLimit).isEqualTo("0");
		});

		String coverageCd1 = "PD";
		String newBILimits1 = "50000";

		PolicyCoverageInfo coverageResponse = HelperCommon.updatePolicyLevelCoverageEndorsement(policyNumber, coverageCd1, newBILimits1);
		assertSoftly(softly -> {

			List<Coverage> coverages1 = coverageResponse.vehicleLevelCoverages.get(0).coverages;
			List<Coverage> coverages2 = coverageResponse.vehicleLevelCoverages.get(1).coverages;

			Coverage filteredCoverageResponseUmpd = coverageResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.coverageCd)).findFirst().orElse(null);
			Coverage filteredCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.coverageCd)).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseUmpd.coverageLimit).isEqualTo(newBILimits1);
			softly.assertThat(filteredCoverageResponsePD.coverageLimit).isEqualTo(newBILimits1);

			softly.assertThat(coverages1.get(0).coverageLimit).isEqualTo("250");
			softly.assertThat(coverages2.get(0).coverageLimit).isEqualTo("0");
		});

	}

	protected void pas11654_MDEnhancedUIMBICoverageBody(ETCSCoreSoftAssertions softly, PolicyType policyType) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Perform Endorsementgbvgfc
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

		//Pas-18202 Order of coverage for MD
		softly.assertThat(policyCoverageResponse.policyCoverages.get(0).coverageCd).isEqualTo("BI");
		softly.assertThat(policyCoverageResponse.policyCoverages.get(1).coverageCd).isEqualTo("PD");
		softly.assertThat(policyCoverageResponse.policyCoverages.get(2).coverageCd).isEqualTo("UMBI");
		softly.assertThat(policyCoverageResponse.policyCoverages.get(3).coverageCd).isEqualTo("EUIM");
		softly.assertThat(policyCoverageResponse.policyCoverages.get(4).coverageCd).isEqualTo("UMPD");
		softly.assertThat(policyCoverageResponse.policyCoverages.get(5).coverageCd).isEqualTo("MEDPM");
		softly.assertThat(policyCoverageResponse.policyCoverages.get(6).coverageCd).isEqualTo("PIP");

		softly.assertThat(policyCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).coverageCd).isEqualTo("COMPDED");
		softly.assertThat(policyCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(1).coverageCd).isEqualTo("COLLDED");
		softly.assertThat(policyCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(2).coverageCd).isEqualTo("GLASS");
		softly.assertThat(policyCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).coverageCd).isEqualTo("LOAN");
		softly.assertThat(policyCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).coverageCd).isEqualTo("RREIM");
		softly.assertThat(policyCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(5).coverageCd).isEqualTo("TOWINGLABOR");
		softly.assertThat(policyCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(6).coverageCd).isEqualTo("SPECEQUIP");
		softly.assertThat(policyCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(7).coverageCd).isEqualTo("NEWCAR");
		softly.assertThat(policyCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(8).coverageCd).isEqualTo("WL");

		Coverage coverageEUIM = policyCoverageResponse.policyCoverages.get(3);
		coverageXproperties(softly, coverageEUIM, "EUIM", "Enhanced UIM Selected", "false", "No", null, true, true);

		String coverageCd1 = "EUIM";
		String newBILimits1 = "true";
		PolicyCoverageInfo coverageResponse = HelperCommon.updatePolicyLevelCoverageEndorsement(policyNumber, coverageCd1, newBILimits1);
		Coverage coverageEUIM1 = coverageResponse.policyCoverages.get(3);
		coverageXproperties(softly, coverageEUIM1, "EUIM", "Enhanced UIM Selected", "true", "Yes", null, true, true);

		String coverageCd2 = "EUIM";
		String newBILimits2 = "false";
		PolicyCoverageInfo coverageResponse1 = HelperCommon.updatePolicyLevelCoverageEndorsement(policyNumber, coverageCd2, newBILimits2);
		Coverage coverageEUIM2 = coverageResponse1.policyCoverages.get(3);
		coverageXproperties(softly, coverageEUIM2, "EUIM", "Enhanced UIM Selected", "false", "No", null, true, true);
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

	protected void pas15379_ValidatePUPErrorRelatedWithBiPdLimitsBody() {

		assertSoftly(softly -> {

			TestData testData = getPolicyDefaultTD();
			testData.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
					AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(),
					AutoSSMetaData.GeneralTab.AAAProductOwned.PUP.getLabel()),
					"Yes");

			testData.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
					AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(),
					AutoSSMetaData.GeneralTab.AAAProductOwned.PUP_POLICY_NUM.getLabel()),
					"123");

			//Adjusting P&C tab for specific states so that there ar no other errors related with coverages that can interrupt test
			Map<String, String> testDataMap = ImmutableMap.of(
					"DC", "PremiumAndCoveragesTab_DC",
					"IN", "PremiumAndCoveragesTab_IN",
					"NJ", "PremiumAndCoveragesTab_NJ",
					"NY", "PremiumAndCoveragesTab_NY",
					"WV", "PremiumAndCoveragesTab_WV");

			if (testDataMap.containsKey(getState())) {
				testData.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName()), getTestSpecificTD(testDataMap.get(getState())).resolveLinks());
			}

			if ("NY".contains(getState())) {
				TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
				testData.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError).resolveLinks();
			}

			mainApp().open();
			createCustomerIndividual();
			String policyNumber = createPolicy(testData);
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			PolicyCoverageInfo viewCoverageResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			Coverage coverageBI = viewCoverageResponse.policyCoverages.stream().filter(coverage -> "BI".equals(coverage.coverageCd)).findFirst().orElse(null);
			List<CoverageLimit> coverageLimitsBI = coverageBI.availableLimits;
			Collections.reverse(coverageLimitsBI); //reverse, so that highest available limit is first element in the list

			//Update BI to the highest one so that all PD limits are available
			UpdateCoverageRequest updateCoverageRequest = DXPRequestFactory.createUpdateCoverageRequest("BI", coverageLimitsBI.get(0).coverageLimit);
			HelperCommon.updateEndorsementCoverage(policyNumber, updateCoverageRequest, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

			//Get all PD limits
			viewCoverageResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			Coverage coveragePD = viewCoverageResponse.policyCoverages.stream().filter(coverage -> "PD".equals(coverage.coverageCd)).findFirst().orElse(null);
			List<CoverageLimit> coverageLimitsPD = coveragePD.availableLimits;
			Collections.reverse(coverageLimitsPD);//reverse, so that highest available limit is first element in the list

			//update PD to highest one, so that it doesn't give error
			updateCoverageRequest = DXPRequestFactory.createUpdateCoverageRequest("PD", coverageLimitsPD.get(0).coverageLimit);
			HelperCommon.updateEndorsementCoverage(policyNumber, updateCoverageRequest, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

			//validate with BI limits
			validatePUPError_pas15379(softly, policyNumber, coverageLimitsBI, "BI");

			//Update BI to the highest one so that all PD values are available
			updateCoverageRequest = DXPRequestFactory.createUpdateCoverageRequest("BI", coverageLimitsBI.get(0).coverageLimit);
			HelperCommon.updateEndorsementCoverage(policyNumber, updateCoverageRequest, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

			//validate with PD limits
			validatePUPError_pas15379(softly, policyNumber, coverageLimitsPD, "PD");

			/*Update BI to value with error expected and bind. For OH not setting to the lowest BI and PD limits,
			 because otherwise on rate there is error "Limits selected are lower than state minimums. Referral will be denied." */
			if ("OH".contains(getState())) {
				updateCoverageRequest = DXPRequestFactory.createUpdateCoverageRequest("BI", "25000/50000");
				HelperCommon.updateEndorsementCoverage(policyNumber, updateCoverageRequest, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
				updateCoverageRequest = DXPRequestFactory.createUpdateCoverageRequest("PD", "25000");
				HelperCommon.updateEndorsementCoverage(policyNumber, updateCoverageRequest, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

			} else {
				Collections.reverse(coverageLimitsBI); //reverse, so that lowest available limit is first element in the list
				updateCoverageRequest = DXPRequestFactory.createUpdateCoverageRequest("BI", coverageLimitsBI.get(0).coverageLimit);
				HelperCommon.updateEndorsementCoverage(policyNumber, updateCoverageRequest, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			}

			/*Not trying to rate and bind with NY and IN, because there is error on rate not related with this US - for IN- "UMPD limit may not exceed PD limit",
			for NY - "UM/SUM limits may not exceed BI limits". Currently not possible to update them through DXP.
			 */
			if (!"NY, IN".contains(getState())) {
				helperMiniServices.endorsementRateAndBind(policyNumber);
			}
		});
	}

	private void validatePUPError_pas15379(ETCSCoreSoftAssertions softly, String policyNumber, List<CoverageLimit> coverageLimits, String coverageCd) {
		UpdateCoverageRequest updateCoverageRequest;
		PolicyCoverageInfo updateCoverageResponse;
		BigDecimal coverageLimitThreshold;
		BigDecimal coverageLimitFormatted;

		if ("BI".equals(coverageCd)) {
			coverageLimitThreshold = new BigDecimal("500000");//for BI
		} else {
			coverageLimitThreshold = new BigDecimal("100000");//for PD
		}

		for (CoverageLimit coverageLimit : coverageLimits) {
			updateCoverageRequest = DXPRequestFactory.createUpdateCoverageRequest(coverageCd, coverageLimit.coverageLimit);
			updateCoverageResponse = HelperCommon.updateEndorsementCoverage(policyNumber, updateCoverageRequest, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

			if (coverageLimit.coverageLimit.contains("/")) {
				coverageLimitFormatted = new BigDecimal(coverageLimit.coverageLimit.substring(0, coverageLimit.coverageLimit.indexOf("/")));
			} else {
				coverageLimitFormatted = new BigDecimal(coverageLimit.coverageLimit);
			}

			/*
			BI values 500000/500000 or greater should not have error. Lower values should have error.
			PD values 100000 or greater should not have error. Lower values should have error.
			*/
			if (coverageLimitFormatted.compareTo(coverageLimitThreshold) == -1) {
				softly.assertThat(updateCoverageResponse.validations.stream().anyMatch(validation -> validation.message.contains(ErrorDxpEnum.Errors.VERIFY_PUP_POLICY.getMessage()))).
						as(coverageCd + " " + coverageLimit.coverageLimit + " should have error").isTrue();
			} else {
				softly.assertThat(updateCoverageResponse.validations.stream().anyMatch(validation -> validation.message.contains(ErrorDxpEnum.Errors.VERIFY_PUP_POLICY.getMessage()))).
						as(coverageCd + " " + coverageLimit.coverageLimit + " should not have error").isFalse();
			}

			//Validate that coverage is updated
			softly.assertThat(updateCoverageResponse.policyCoverages.stream().filter(coverage -> coverageCd.equals(coverage.coverageCd)).findFirst().orElse(null).coverageLimit).isEqualTo(coverageLimit.coverageLimit);
		}
	}

	protected void pas18624_CustomisedEquipmentBody() {

		assertSoftly(softly -> {
			TestData testData = getPolicyTD("DataGather", "TestData");

			if ("VA".equals(getState())) {
				testData = testData.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_VAN_PICKUP_VA").getTestDataList("VehicleTab")).resolveLinks();
			} else {
				testData = testData.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_VAN_PICKUP_other_than_VA").getTestDataList("VehicleTab")).resolveLinks();
			}

			mainApp().open();
			createCustomerIndividual();
			String policyNumber = createPolicy(testData);

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			List<Vehicle> vehicleList = HelperCommon.viewEndorsementVehicles(policyNumber).vehicleList;
			Vehicle regularVehicle = getVehicleByVin(vehicleList, "1FM5K8D86JGA29926");
			Vehicle vanWithoutCE = getVehicleByVin(vehicleList, "2GTEC19V531282646");
			Vehicle vanWithCE = getVehicleByVin(vehicleList, "2GTEC19K8S1525936");

			//viewEndorsementCoverages
			PolicyCoverageInfo viewEndorsementCoveragesResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			validateCustomEquipCov(softly, false, regularVehicle.oid, viewEndorsementCoveragesResponse);
			validateCustomEquipCov(softly, false, vanWithoutCE.oid, viewEndorsementCoveragesResponse);
			validateCustomEquipCov(softly, true, vanWithCE.oid, viewEndorsementCoveragesResponse);

			//viewPolicyCoverages
			PolicyCoverageInfo viewPolicyCoveragesResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			validateCustomEquipCov(softly, false, regularVehicle.oid, viewPolicyCoveragesResponse);
			validateCustomEquipCov(softly, false, vanWithoutCE.oid, viewPolicyCoveragesResponse);
			validateCustomEquipCov(softly, true, vanWithCE.oid, viewPolicyCoveragesResponse);

			// viewPolicyCoveragesByVehicle
			PolicyCoverageInfo policyCoveragesRegularVehicle = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, regularVehicle.oid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			validateCustomEquipCov(softly, false, regularVehicle.oid, policyCoveragesRegularVehicle);

			PolicyCoverageInfo policyCoveragesVanWithoutCE = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, vanWithoutCE.oid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			validateCustomEquipCov(softly, false, vanWithoutCE.oid, policyCoveragesVanWithoutCE);

			PolicyCoverageInfo policyCoveragesVanWithCE = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, vanWithCE.oid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			validateCustomEquipCov(softly, true, vanWithCE.oid, policyCoveragesVanWithCE);

			//viewEndorsementCoveragesByVehicle
			PolicyCoverageInfo endorsementCoveragesRegularVehicle = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, regularVehicle.oid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			validateCustomEquipCov(softly, false, regularVehicle.oid, endorsementCoveragesRegularVehicle);

			PolicyCoverageInfo endorsementCoveragesVanWithoutCE = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, vanWithoutCE.oid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			validateCustomEquipCov(softly, false, vanWithoutCE.oid, endorsementCoveragesVanWithoutCE);

			//START PAS-19834
			PolicyCoverageInfo endorsementCoveragesVanWithCE = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, vanWithCE.oid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			validateCustomEquipCov(softly, true, vanWithCE.oid, endorsementCoveragesVanWithCE);

			//remove COMP coverage
			String coverageCdChange = "COMPDED";
			String availableLimitsChange = "-1";
			String availableLimitsChange2 = "500";

			PolicyCoverageInfo compUpdatedCoverage = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, vanWithCE.oid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChange, availableLimitsChange), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			Coverage filteredCoverageResponseComp = compUpdatedCoverage.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COMPDED".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponseComp.coverageLimit).isEqualTo(availableLimitsChange);
			softly.assertThat(filteredCoverageResponseComp.coverageLimitDisplay).isEqualTo("No Coverage");

			//check if CUSTEQUIP was removed
			validateCustomEquipCov(softly, false, vanWithCE.oid, compUpdatedCoverage);

			//rate, to check if there is no error
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//return COMPDED back
			PolicyCoverageInfo compUpdatedCoverage2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, vanWithCE.oid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChange, availableLimitsChange2), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			Coverage filteredCoverageResponseComp2 = compUpdatedCoverage2.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COMPDED".equals(cov.coverageCd)).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponseComp2.coverageLimit).isEqualTo(availableLimitsChange2);

			validateCustomEquipCov(softly, false, vanWithCE.oid, compUpdatedCoverage2);

			//rate, to check if there is no error
			helperMiniServices.endorsementRateAndBind(policyNumber);
		});
	}

	private void validateCustomEquipCov(ETCSCoreSoftAssertions softly, boolean coverageExpected, String oid, PolicyCoverageInfo policyCoverageInfo) {
		VehicleCoverageInfo vehicleCoverageInfo = getVehicleCoverages(policyCoverageInfo, oid);
		Coverage custEquip = getCoverage(vehicleCoverageInfo.coverages, "CUSTEQUIP");

		if (coverageExpected && "VA".equals(getState())) {
			assertThat(vehicleCoverageInfo.coverages.stream().anyMatch(coverage -> "CUSTEQUIP".equals(coverage.coverageCd))).as("CUSTEQUIP is expected").isTrue();
			coverageXproperties(softly, custEquip, "CUSTEQUIP", "Customized Equipment", "2500.25", "$2,500.25", null, true, false);
			validateCustomEquipCoverageOrder_pas18624(softly, vehicleCoverageInfo, custEquip);
		} else {
			softly.assertThat(vehicleCoverageInfo.coverages.stream().anyMatch(coverage -> "CUSTEQUIP".equals(coverage.coverageCd))).as("CUSTEQUIP is not expected").isFalse();
		}
	}

	private Vehicle getVehicleByVin(List<Vehicle> vehicleList, String vin) {
		return vehicleList.stream().filter(vehicle -> vin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null);
	}

	private VehicleCoverageInfo getVehicleCoverages(PolicyCoverageInfo policyCoverageInfo, String oid) {
		return policyCoverageInfo.vehicleLevelCoverages.stream().filter(vehicleCoverageInfo -> oid.equals(vehicleCoverageInfo.oid)).findFirst().orElse(null);
	}

	private Coverage getCoverage(List<Coverage> coverageList, String coverageCd) {
		return coverageList.stream().filter(coverage -> coverageCd.equals(coverage.coverageCd)).findFirst().orElse(null);
	}

	private void validateCustomEquipCoverageOrder_pas18624(ETCSCoreSoftAssertions softly, VehicleCoverageInfo vehicleCoverageInfo, Coverage custEquip) {
		Coverage collded = getCoverage(vehicleCoverageInfo.coverages, "COLLDED");
		int custEquipIndex = vehicleCoverageInfo.coverages.indexOf(custEquip);
		int colldedIndex = vehicleCoverageInfo.coverages.indexOf(collded);
		softly.assertThat(custEquipIndex).as("CUSTEQUIP should be displayed after COLLDED").isEqualTo(colldedIndex + 1);
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
			coverageXproperties(softly, coverageResponse1, coverageResponse2.coverageCd, coverageResponse2.coverageDescription, coverageResponse2.coverageLimit, coverageResponse2.coverageLimitDisplay, coverageResponse2.coverageType, coverageResponse2.customerDisplayed, coverageResponse2.canChangeCoverage);
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
			coverageXproperties(softly, coverageResponse1, coverageResponse2.coverageCd, coverageResponse2.coverageDescription, coverageResponse2.coverageLimit, coverageResponse2.coverageLimitDisplay, coverageResponse2.coverageType, coverageResponse2.customerDisplayed, coverageResponse2.canChangeCoverage);
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
			List<CoverageLimit> availableLimitsPD = coverageResponse.policyCoverages.get(1).availableLimits;

			softly.assertThat(availableLimitsPD.get(0).coverageLimit).isEqualTo("15000");
			softly.assertThat(availableLimitsPD.get(0).coverageLimitDisplay).isEqualTo("$15,000");

			softly.assertThat(availableLimitsPD.get(1).coverageLimit).isEqualTo("25000");
			softly.assertThat(availableLimitsPD.get(1).coverageLimitDisplay).isEqualTo("$25,000");

			softly.assertThat(availableLimitsPD.get(2).coverageLimit).isEqualTo("50000");
			softly.assertThat(availableLimitsPD.get(2).coverageLimitDisplay).isEqualTo("$50,000");

			softly.assertThat(availableLimitsPD.get(3).coverageLimit).isEqualTo("100000");
			softly.assertThat(availableLimitsPD.get(3).coverageLimitDisplay).isEqualTo("$100,000");

			softly.assertThat(availableLimitsPD.get(4).coverageLimit).isEqualTo("300000");
			softly.assertThat(availableLimitsPD.get(4).coverageLimitDisplay).isEqualTo("$300,000");
		});
	}

	private void goToPasAndChangeUMPD(String policyNumber) {

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		premiumAndCoveragesTab.setPolicyCoverageDetailsValue(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel(), "No Coverage");
	}

}



