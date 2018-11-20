package aaa.modules.regression.service.helper;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.rest.dtoDxp.*;
import aaa.main.enums.AvailableCoverageLimits;
import aaa.main.enums.CoverageInfo;
import aaa.main.enums.CoverageLimits;
import aaa.main.enums.ErrorDxpEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.CheckBox;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public class TestMiniServicesCoveragesHelper extends PolicyBaseTest {

	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	public HelperMiniServices helperMiniServices = new HelperMiniServices();
	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	private CheckBox enhancedUIM = new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.ENHANCED_UIM);

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

		PolicyCoverageInfo coverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class);
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

			//			softly.assertThat(coveragesV1.get(8).coverageCd).isEqualTo("WL"); //do not have this coverage in response anymore. Karen Yifru doesn't care about it.
			//			softly.assertThat(coveragesV1.get(8).coverageDescription).isEqualTo("Waive Liability");
			//			softly.assertThat(coveragesV1.get(8).customerDisplayed).isEqualTo(false);

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

		PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", comprehensiveDeductible1.toPlaingString(), comprehensiveDeductible1.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageEndorsementResponse);

			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", collisionDeductible1.toPlaingString(), collisionDeductible1.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageEndorsementResponse);

			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "true", fullSafetyGlassVeh2, "None", true, true);
			assertCoverageLimitFullGlassCov(coverageEndorsementResponse);

			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "1", loanLeaseCov1, "None", true, true);
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

		PolicyCoverageInfo coverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesLoanVehicle = coverageResponse.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesLoanVehicle, "COMPDED", "Other Than Collision", comprehensiveDeductible.toPlaingString(), comprehensiveDeductible.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			coverageXproperties(softly, 1, coveragesLoanVehicle, "COLLDED", "Collision Deductible", collisionDeductible.toPlaingString(), collisionDeductible.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			coverageXproperties(softly, 2, coveragesLoanVehicle, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh1, "None", true, true);
			assertCoverageLimitFullGlassCov(coverageResponse);

			coverageXproperties(softly, 3, coveragesLoanVehicle, "LOAN", "Auto Loan/Lease Coverage", "0", loanLeaseCov1, "None", true, true);
			assertCoverageLimitLoan(coverageResponse);

			coverageXproperties(softly, 4, coveragesLoanVehicle, "RREIM", "Transportation Expense", transportationExpense.toPlaingString(), transportationExpense + " (Included)", "Per Occurrence", true, true);
			assertCoverageLimitTransportationExpense(coverageResponse, false);

			coverageXproperties(softly, 5, coveragesLoanVehicle, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);
			assertCoverageLimitTowingLabor(coverageResponse);

			coverageXproperties(softly, 6, coveragesLoanVehicle, "SPECEQUIP", "Excess Electronic Equipment", excessElectronicEquipment.toPlaingString(), "$1,000.00", null, true, false);
			coverageXproperties(softly, 7, coveragesLoanVehicle, "NEWCAR", "New Car Added Protection", "false", "No", null, false, false);

			//			softly.assertThat(coveragesLoanVehicle.get(8).coverageCd).isEqualTo("WL"); //do not have this coverage in response anymore. Karen Yifru doesn't care about it.
			//			softly.assertThat(coveragesLoanVehicle.get(8).getCoverageDescription()).isEqualTo("Waive Liability");
			//			softly.assertThat(coveragesLoanVehicle.get(8).customerDisplayed).isEqualTo(false);

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

		PolicyCoverageInfo coverageResponse1 = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse1.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", comprehensiveDeductible1.toPlaingString(), comprehensiveDeductible1.toString(), "Deductible", true, true);
			assertCoverageLimitForCompColl(coverageResponse1);

			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", collisionDeductible1.toPlaingString(), collisionDeductible1.toString(), "Deductible", true, true);
			assertCoverageLimitForCompColl(coverageResponse1);

			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh2, "None", true, true);
			assertCoverageLimitFullGlassCov(coverageResponse1);

			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", false, false);
			assertCoverageLimitLoan(coverageResponse1);

			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Transportation Expense", transportationExpense1.toPlaingString(), transportationExpense1 + " (Included)", "Per Occurrence", true, true);
			assertCoverageLimitTransportationExpense(coverageResponse1, false);

			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor1, "Per Disablement/Maximum", true, true);
			assertCoverageLimitTowingLabor(coverageResponse1);

			coverageXproperties(softly, 6, coveragesV1, "SPECEQUIP", "Excess Electronic Equipment", excessElectronicEquipment1.toPlaingString(), "$1,000.00", null, true, false);
			coverageXproperties(softly, 7, coveragesV1, "NEWCAR", "New Car Added Protection", "true", "Yes", null, true, false);

			//			softly.assertThat(coveragesV1.get(8).coverageCd).isEqualTo("WL"); //do not have this coverage in response anymore. Karen Yifru doesn't care about it.
			//			softly.assertThat(coveragesV1.get(8).getCoverageDescription()).isEqualTo("Waive Liability");
			//			softly.assertThat(coveragesV1.get(8).customerDisplayed).isEqualTo(false);

		});
	}

	protected void pas16984_validateCoverageConstraints(PolicyType policyType) {
		ComparablePolicy policyResponse = HelperCommon.viewEndorsementChangeLog("UTSS952918553", Response.Status.OK.getStatusCode());
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();

		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo endorsementCoverageResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);

		/*
		 * Test first cycles through the coverages that can be updated. The test first tries to update the limit
		 * to some made up value and validates that the service returns back an error message.
		 */
		endorsementCoverageResponse.policyCoverages.stream().filter(Coverage::getCanChangeCoverage)
				.forEach(coverage -> {
					ErrorResponseDto response = HelperCommon.updateEndorsementCoverage(policyNumber,
							DXPRequestFactory.createUpdateCoverageRequest(coverage.getCoverageCd(), "invalidLimit"), ErrorResponseDto.class, 422);
					assertSoftly(softly -> {
						softly.assertThat(response.errorCode).isEqualTo("ERROR_SERVICE_VALIDATION");
						softly.assertThat(response.message).isEqualTo("Invalid coverage limit 'invalidLimit' provided for coverage code '" + coverage.getCoverageCd() + "'");
					});
				});
		endorsementCoverageResponse.vehicleLevelCoverages.forEach(vehicleCoverageInfo ->
				vehicleCoverageInfo.coverages.stream().filter(Coverage::getCanChangeCoverage)
						.forEach(vehicleCoverage -> {
							assertSoftly(softly -> {
								ErrorResponseDto response = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, vehicleCoverageInfo.oid,
										DXPRequestFactory.createUpdateCoverageRequest(vehicleCoverage.getCoverageCd(), "invalidLimit"), ErrorResponseDto.class, 422);
								softly.assertThat(response.errorCode).isEqualTo("ERROR_SERVICE_VALIDATION");
								softly.assertThat(response.message).isEqualTo("Invalid coverage limit 'invalidLimit' provided for coverage code '" + vehicleCoverage.getCoverageCd() + "'");
							});
						}));

		/*
		 * Test next cycles through the coverages that cannot be updated. The test tries to update the coverage and
		 * validates that the service returns an error.
		 */
		endorsementCoverageResponse.policyCoverages.stream().filter(coverage -> !coverage.getCanChangeCoverage())
				.forEach(coverage -> {
					ErrorResponseDto response = HelperCommon.updateEndorsementCoverage(policyNumber,
							DXPRequestFactory.createUpdateCoverageRequest(coverage.getCoverageCd(), "0"), ErrorResponseDto.class, 422);
					assertSoftly(softly -> {
						softly.assertThat(response.errorCode).isEqualTo("ERROR_SERVICE_VALIDATION");
						softly.assertThat(response.message).isEqualTo("Update actions is not allowed for coverage code '" + coverage.getCoverageCd() + "'");
					});
				});
		endorsementCoverageResponse.vehicleLevelCoverages.forEach(vehicleCoverageInfo ->
				vehicleCoverageInfo.coverages.stream().filter(vehicleCoverage -> !vehicleCoverage.getCanChangeCoverage())
						.forEach(vehicleCoverage -> {
							assertSoftly(softly -> {
								ErrorResponseDto response = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, vehicleCoverageInfo.oid,
										DXPRequestFactory.createUpdateCoverageRequest(vehicleCoverage.getCoverageCd(), "0"), ErrorResponseDto.class, 422);
								softly.assertThat(response.errorCode).isEqualTo("ERROR_SERVICE_VALIDATION");
								softly.assertThat(response.message).isEqualTo("Update actions is not allowed for coverage code '" + vehicleCoverage.getCoverageCd() + "'");
							});
						}));

		/*
		 * Test next tries to update a bogus coverage code and validates that the service returns an error.
		 */
		ErrorResponseDto response = HelperCommon.updateEndorsementCoverage(policyNumber,
				DXPRequestFactory.createUpdateCoverageRequest("TEST", "0"), ErrorResponseDto.class, 422);
		assertSoftly(softly -> {
			softly.assertThat(response.errorCode).isEqualTo("ERROR_SERVICE_VALIDATION");
			softly.assertThat(response.message).isEqualTo("Cannot find policy level coverage with coverage code 'TEST'");
		});
	}

	protected void pas20818_ViewPdCoverageLimitsBody(String date, boolean policyWithOldLimits) {
		TestData td = getPolicyTD("DataGather", "TestData").adjust(TestData.makeKeyPath(new GeneralTab().getMetaKey(),
				AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
				AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()), date);

		mainApp().open();
		createCustomerIndividual();
		createQuote(td);

		NavigationPage.comboBoxListAction.setValue("Data Gathering");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		//Get values from 'PD coverage' dropdown PAS UI
		List<String> pdLimitsPasQuote = new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY).getAllValues();
		premiumAndCoveragesTab.saveAndExit();
		String policyNumber = testEValueDiscount.simplifiedQuoteIssue();

		//Policy coverage service
		PolicyCoverageInfo coverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class);

		//Get PD cov from DXP response
		Coverage filteredCoverageResponse1 = findCoverage(coverageResponse.policyCoverages, CoverageInfo.PD.getCode());
		checkUiCoverageLimits(policyWithOldLimits, pdLimitsPasQuote, filteredCoverageResponse1);

		String endorsementDate = "2018-12-08";
		HelperCommon.createEndorsement(policyNumber, endorsementDate);

		//Policy endorsement coverage service
		PolicyCoverageInfo coverageResponse2 = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		Coverage filteredCoverageResponse2 = findCoverage(coverageResponse2.policyCoverages, CoverageInfo.PD.getCode());

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		List<String> pdLimitsPasPolicy = new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY).getAllValues();
		checkUiCoverageLimits(policyWithOldLimits, pdLimitsPasPolicy, filteredCoverageResponse2);
		premiumAndCoveragesTab.saveAndExit();
	}

	private void checkUiCoverageLimits(boolean policyWithOldLimits, List<String> pdLimits, Coverage filteredCoverageResponse) {
		Coverage toMatch = Coverage.create(CoverageInfo.PD);
		if (policyWithOldLimits) {
			assertSoftly(softly -> {
				softly.assertThat(pdLimits.get(0)).contains("$10,000");
				softly.assertThat(pdLimits.get(1)).contains("$15,000");
			});

		} else {
			assertSoftly(softly -> {
				softly.assertThat(pdLimits.get(0)).contains("$25,000");
				softly.assertThat(pdLimits.get(1)).contains("$50,000");
			});
			toMatch = Coverage.create(CoverageInfo.PD).removeAvailableLimit(CoverageLimits.COV_10000).removeAvailableLimit(CoverageLimits.COV_15000);
		}
		assertThat(filteredCoverageResponse).isEqualToIgnoringGivenFields(toMatch, "coverageType");
	}

	protected void pas17083_ViewUmpdAndUimpdCoveragesBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Policy coverage service
		PolicyCoverageInfo coverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class);
		checkUmpdAndUimpdCoverages(coverageResponse, CoverageLimits.COV_50000);

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo coverageResponse2 = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		checkUmpdAndUimpdCoverages(coverageResponse2, CoverageLimits.COV_50000);

		PolicyCoverageInfo updateCoverageResponse = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest("PD", "100000"), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

		checkUmpdAndUimpdCoverages(updateCoverageResponse, CoverageLimits.COV_100000);
		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	private void checkUmpdAndUimpdCoverages(PolicyCoverageInfo coverageResponse, CoverageLimits limit) {
		Coverage filteredCoverageUimpd = findCoverage(coverageResponse.policyCoverages, CoverageInfo.UIMPD.getCode());
		Coverage filteredCoverageUmpd = findCoverage(coverageResponse.policyCoverages, CoverageInfo.UMPD.getCode());
		assertSoftly(softly -> {
			//To check the coverages limits
			Coverage toMatchUimpd = Coverage.create(CoverageInfo.UIMPD).changeLimit(limit).disableCanChange();
			softly.assertThat(filteredCoverageUimpd).isEqualToIgnoringGivenFields(toMatchUimpd, "coverageType");

			Coverage toMatchUmpd = Coverage.create(CoverageInfo.UMPD).changeLimit(limit).disableCanChange();
			softly.assertThat(filteredCoverageUmpd).isEqualToIgnoringGivenFields(toMatchUmpd);
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

		PolicyCoverageInfo coverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			Coverage coverageCompded = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0);
			coverageXproperties(softly, coverageCompded, "COMPDED", "Comprehensive Deductible", comprehensiveDeductible.toPlaingString(), comprehensiveDeductible.toString(), "Deductible", true, true);
			availableCoverageCompdedForAz(coverageResponse, softly);

			Coverage coverageCollded = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(1);
			coverageXproperties(softly, coverageCollded, "COLLDED", "Collision Deductible", collisionDeductible.toPlaingString(), collisionDeductible.toString(), "Deductible", true, true);

			softly.assertThat(coverageCollded.getAvailableLimits().get(0).coverageLimit).isEqualTo("100");
			softly.assertThat(coverageCollded.getAvailableLimits().get(0).coverageLimitDisplay).isEqualTo("$100");

			softly.assertThat(coverageCollded.getAvailableLimits().get(1).coverageLimit).isEqualTo("250");
			softly.assertThat(coverageCollded.getAvailableLimits().get(1).coverageLimitDisplay).isEqualTo("$250");

			softly.assertThat(coverageCollded.getAvailableLimits().get(2).coverageLimit).isEqualTo("500");
			softly.assertThat(coverageCollded.getAvailableLimits().get(2).coverageLimitDisplay).isEqualTo("$500");

			softly.assertThat(coverageCollded.getAvailableLimits().get(3).coverageLimit).isEqualTo("750");
			softly.assertThat(coverageCollded.getAvailableLimits().get(3).coverageLimitDisplay).isEqualTo("$750");

			softly.assertThat(coverageCollded.getAvailableLimits().get(4).coverageLimit).isEqualTo("1000");
			softly.assertThat(coverageCollded.getAvailableLimits().get(4).coverageLimitDisplay).isEqualTo("$1,000");

			Coverage coverageGlass = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(2);
			coverageXproperties(softly, coverageGlass, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh1, "None", true, true);

			softly.assertThat(coverageGlass.getAvailableLimits().get(0).coverageLimit).isEqualTo("false");
			softly.assertThat(coverageGlass.getAvailableLimits().get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageGlass.getAvailableLimits().get(1).coverageLimit).isEqualTo("true");
			softly.assertThat(coverageGlass.getAvailableLimits().get(1).coverageLimitDisplay).isEqualTo("Yes");

			Coverage coverageLoan = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3);
			coverageXproperties(softly, coverageLoan, "LOAN", "Vehicle Loan/Lease Protection", "0", loanLeaseCov, "None", true, true);

			softly.assertThat(coverageLoan.getAvailableLimits().get(0).coverageLimit).isEqualTo("0");
			softly.assertThat(coverageLoan.getAvailableLimits().get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageLoan.getAvailableLimits().get(1).coverageLimit).isEqualTo("1");
			softly.assertThat(coverageLoan.getAvailableLimits().get(1).coverageLimitDisplay).isEqualTo("Yes");

			Coverage coverageRreim = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4);
			coverageXproperties(softly, coverageRreim, "RREIM", "Rental Reimbursement", "0/0", transportationExpense, "Per Day/Maximum", true, true);

			softly.assertThat(coverageRreim.getAvailableLimits().get(0).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageRreim.getAvailableLimits().get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageRreim.getAvailableLimits().get(1).coverageLimit).isEqualTo("30/900");
			softly.assertThat(coverageRreim.getAvailableLimits().get(1).coverageLimitDisplay).isEqualTo("$30/$900");

			softly.assertThat(coverageRreim.getAvailableLimits().get(2).coverageLimit).isEqualTo("40/1200");
			softly.assertThat(coverageRreim.getAvailableLimits().get(2).coverageLimitDisplay).isEqualTo("$40/$1,200");

			softly.assertThat(coverageRreim.getAvailableLimits().get(3).coverageLimit).isEqualTo("50/1500");
			softly.assertThat(coverageRreim.getAvailableLimits().get(3).coverageLimitDisplay).isEqualTo("$50/$1,500");

			Coverage coverageTowing = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5);
			coverageXproperties(softly, coverageTowing, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);

			softly.assertThat(coverageTowing.getAvailableLimits().get(0).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageTowing.getAvailableLimits().get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageTowing.getAvailableLimits().get(1).coverageLimit).isEqualTo("50/300");
			softly.assertThat(coverageTowing.getAvailableLimits().get(1).coverageLimitDisplay).isEqualTo("$50/$300");

			Coverage coverageSpeq = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(6);
			coverageXproperties(softly, coverageSpeq, "SPECEQUIP", "Special Equipment Coverage", excessElectronicEquipment.toPlaingString(), "$1,000.00", null, true, false);

			Coverage coverageNewcar = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(7);
			coverageXproperties(softly, coverageNewcar, "NEWCAR", "New Car Added Protection", "false", "No", null, false, false);

			//			Coverage coverageWL = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(8); //do not have this coverage in response anymore. Karen Yifru doesn't care about it.
			//			softly.assertThat(coverageWL.coverageCd).isEqualTo("WL");
			//			softly.assertThat(coverageWL.getCoverageDescription()).isEqualTo("Waive Liability");
			//			softly.assertThat(coverageWL.customerDisplayed).isEqualTo(false);

		});
	}

	private void availableCoverageCompdedForAz(PolicyCoverageInfo coverageResponse, ETCSCoreSoftAssertions softly) {
		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getAvailableLimits().get(0).coverageLimit).isEqualTo("50");
		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getAvailableLimits().get(0).coverageLimitDisplay).isEqualTo("$50");

		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getAvailableLimits().get(1).coverageLimit).isEqualTo("100");
		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getAvailableLimits().get(1).coverageLimitDisplay).isEqualTo("$100");

		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getAvailableLimits().get(2).coverageLimit).isEqualTo("250");
		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getAvailableLimits().get(2).coverageLimitDisplay).isEqualTo("$250");

		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getAvailableLimits().get(3).coverageLimit).isEqualTo("500");
		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getAvailableLimits().get(3).coverageLimitDisplay).isEqualTo("$500");

		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getAvailableLimits().get(4).coverageLimit).isEqualTo("750");
		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getAvailableLimits().get(4).coverageLimitDisplay).isEqualTo("$750");

		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getAvailableLimits().get(5).coverageLimit).isEqualTo("1000");
		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getAvailableLimits().get(5).coverageLimitDisplay).isEqualTo("$1,000");
	}

	protected void pas14316_LoanLeasedCovForFinancedNewVehicleBody(ETCSCoreSoftAssertions softly, String ownershipType) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//VIN Will need to be updated yearly to make the test functional
		String purchaseDate = "2013-02-22";
		String vin = "JF1GPAT66FH237608"; //2015 Subaru Impreza

		//Add vehicle with specific info
		String newVehicleOid = helperMiniServices.vehicleAddRequestWithCheck(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin, purchaseDate));

		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		//Add coverage check here
		String coverageCdValue = "LOAN";
		PolicyCoverageInfo endorsementCoverageResponseOwnedOldVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class);
		Coverage endorsementCoverageResponseOwnedOldVehFiltered = endorsementCoverageResponseOwnedOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.getCoverageCd())).findFirst().orElse(null);
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
		PolicyCoverageInfo endorsementCoverageResponseLsdFinOldVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class);
		Coverage endorsementCoverageResponseLsdFinOldVehFiltered = endorsementCoverageResponseLsdFinOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.getCoverageCd())).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, endorsementCoverageResponseLsdFinOldVehFiltered, "0", "No Coverage", true, true);

		PolicyCoverageInfo updateLoanLeaseCoverage = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdValue, "1"), PolicyCoverageInfo.class);
		Coverage updateLoanLeaseCoverageFiltered = updateLoanLeaseCoverage.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.getCoverageCd())).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, updateLoanLeaseCoverageFiltered, "1", "Yes", true, true);
		//BUG Status is not reset when updating coverages
		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");

		helperMiniServices.endorsementRateAndBind(policyNumber);
		PolicyCoverageInfo policyCoverageResponseLsdFinOldVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class);
		Coverage policyCoverageResponseLsdFinOldVehFiltered = policyCoverageResponseLsdFinOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.getCoverageCd())).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, policyCoverageResponseLsdFinOldVehFiltered, "1", "Yes", true, true);

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		VehicleUpdateDto updateVehicleOwned = new VehicleUpdateDto();
		updateVehicleOwned.vehicleOwnership = new VehicleOwnership();
		updateVehicleOwned.vehicleOwnership.ownership = "OWN";
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleOwned);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo endorsementCoverageResponseOwnedOldVeh2 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class);
		Coverage endorsementCoverageResponseOwnedOldVeh2Filtered = endorsementCoverageResponseOwnedOldVeh2.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.getCoverageCd())).findFirst().orElse(null);
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
		Vehicle addVehicle =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin, purchaseDate), Vehicle.class, 201);
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

		Dollar collisionDeductible1 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "  (+$0.00)", "(+$185.00)");
		String fullSafetyGlassVeh1 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel(), "");
		String loanLeaseCov1 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.AUTO_LOAN_LEASE_COVERAGE.getLabel(), " (+$0.00)");
		Dollar transportationExpense1 = getCoverage(2, AutoSSMetaData.PremiumAndCoveragesTab.TRANSPORTATION_EXPENSE.getLabel(), " (Included)", " (+$0.00)", " (+$21.00)");
		String towingAndLabor1 = getCoverages(2, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel(), " (Included)", " (+$0.00)", "$");

		premiumAndCoveragesTab.saveAndExit();

		String coverageCd = "COMPDED";
		String availableLimits = "100";

		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, availableLimits), PolicyCoverageInfo.class);
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

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd1, availableLimits1), PolicyCoverageInfo.class);
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
		String availableLimits2 = "true";

		PolicyCoverageInfo coverageResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd2, availableLimits2), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse2.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimits, "$" + availableLimits, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimits1, "$" + availableLimits1, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", availableLimits2, "Yes", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "0", loanLeaseCov1, "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Transportation Expense", transportationExpense1.toPlaingString(), transportationExpense1 + " (Included)", "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor1, "Per Disablement/Maximum", true, true);
		});

		String coverageCd3 = "LOAN";
		String availableLimits3 = "1";

		PolicyCoverageInfo coverageResponse3 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd3, availableLimits3), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse3.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimits, "$" + availableLimits, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimits1, "$" + availableLimits1, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", availableLimits2, "Yes", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", availableLimits3, "Yes", "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Transportation Expense", transportationExpense1.toPlaingString(), transportationExpense1 + " (Included)", "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor1, "Per Disablement/Maximum", true, true);
		});

		String coverageCd4 = "RREIM";
		String availableLimits4 = "900";

		PolicyCoverageInfo coverageResponse4 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd4, availableLimits4), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse4.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimits, "$" + availableLimits, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimits1, "$" + availableLimits1, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", availableLimits2, "Yes", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", availableLimits3, "Yes", "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Transportation Expense", availableLimits4, "$" + availableLimits4, "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor1, "Per Disablement/Maximum", true, true);
		});

		String coverageCd5 = "TOWINGLABOR";
		String availableLimits5 = "50/300";

		PolicyCoverageInfo coverageResponse5 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd5, availableLimits5), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse5.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimits, "$" + availableLimits, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimits1, "$" + availableLimits1, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", availableLimits2, "Yes", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", availableLimits3, "Yes", "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Transportation Expense", availableLimits4, "$" + availableLimits4, "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", availableLimits5, "$50/$300", "Per Disablement/Maximum", true, true);
		});

		updateVehicleLeasedFinanced.vehicleOwnership.ownership = "OWN";
		VehicleUpdateResponseDto ownershipUpdateResponse2 = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleLeasedFinanced);
		assertThat(ownershipUpdateResponse2.vehicleOwnership.ownership).isEqualTo("OWN");

		String coverageCdRemove = "COMPDED";
		String availableLimitsRemove = "-1";

		PolicyCoverageInfo coverageResponse14 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdRemove, availableLimitsRemove), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse14.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimitsRemove, "No Coverage", "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsRemove, "No Coverage", "Deductible", true, false);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, false);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", false, false);
			//coverageXproperties(softly, 4, coverageResponse14, "RREIM", "Transportation Expense", "0", "0", "Per Occurrence", true, false);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, false);
		});

		String coverageCdChange = "COMPDED";
		String availableLimitsChange = "500";

		PolicyCoverageInfo coverageResponse6 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChange, availableLimitsChange), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse6.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsRemove, "No Coverage", "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", false, false);
			//	coverageXproperties(softly, 4, coverageResponse6, "RREIM", "Transportation Expense", "0", "0", "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, false);
		});

		updateVehicleLeasedFinanced.vehicleOwnership.ownership = "LSD";
		VehicleUpdateResponseDto ownershipUpdateResponse3 = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleLeasedFinanced);
		assertThat(ownershipUpdateResponse3.vehicleOwnership.ownership).isEqualTo("LSD");

		String coverageCdChangeColl = "COLLDED";
		String availableLimitsChangeColl = "500";

		PolicyCoverageInfo coverageResponse7 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeColl, availableLimitsChangeColl), PolicyCoverageInfo.class);
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
		String availableLimitsChangeGlass = "true";

		PolicyCoverageInfo coverageResponse8 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeGlass, availableLimitsChangeGlass), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse8.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", availableLimitsChangeGlass, "Yes", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", true, true);
			//coverageXproperties(softly, 4, coverageResponse8, "RREIM", "Transportation Expense","0", "0", "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);
		});

		String coverageCdChangeGlassNoCov = "GLASS";
		String availableLimitsChangeGlassNoCov = "false";

		PolicyCoverageInfo coverageResponse9 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeGlassNoCov, availableLimitsChangeGlassNoCov), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse9.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", availableLimitsChangeGlassNoCov, "No Coverage", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "0", "No Coverage", "None", true, true);
			//	coverageXproperties(softly, 4, coverageResponse9, "RREIM", "Transportation Expense","0", "0", "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeLoanNoCov = "LOAN";
		String availableLimitsChangeLoanNoCov = "1";

		PolicyCoverageInfo coverageResponse10 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeLoanNoCov, availableLimitsChangeLoanNoCov), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse10.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", availableLimitsChangeGlassNoCov, "No Coverage", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "1", "Yes", "None", true, true);
			//coverageXproperties(softly, 4, coverageResponse10, "RREIM", "Transportation Expense", availableLimits4, availableLimits4, "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeTransport = "RREIM";
		String availableLimitsChangeTransport = "900";

		PolicyCoverageInfo coverageResponse11 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTransport, availableLimitsChangeTransport), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse11.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", availableLimitsChangeGlassNoCov, "No Coverage", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "1", "Yes", "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Transportation Expense", availableLimitsChangeTransport, "$" + availableLimitsChangeTransport, "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeTowing = "TOWINGLABOR";
		String availableLimitsChangeTowing = "50/300";

		PolicyCoverageInfo coverageResponse12 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTowing, availableLimitsChangeTowing), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse12.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", availableLimitsChangeGlassNoCov, "No Coverage", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Auto Loan/Lease Coverage", "1", "Yes", "None", true, true);
			//	coverageXproperties(softly, 4, coverageResponse12, "RREIM", "Transportation Expense", availableLimitsChangeTransport, availableLimitsChangeTransport, "Per Occurrence", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", availableLimitsChangeTowing, "$50/$300", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeTowingNoCov = "TOWINGLABOR";
		String availableLimitsChangeTowingNoCov = "0/0";

		PolicyCoverageInfo coverageResponse13 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTowingNoCov, availableLimitsChangeTowingNoCov), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse13.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Other Than Collision", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", availableLimitsChangeGlassNoCov, "No Coverage", "None", true, true);
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
		Vehicle addVehicle =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin, purchaseDate), Vehicle.class, 201);
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

		Dollar collisionDeductible = new Dollar(premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel()).replace("(+$0.00)", "").trim());
		String fullSafetyGlassVeh = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.FULL_SAFETY_GLASS.getLabel());
		String loanLeaseCov = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.VEHICLE_LOAN_LEASE_PROTECTION.getLabel()).replace("(+$0.00)", "").trim();
		String transportationExpense = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.RENTAL_REIMBURSEMENT.getLabel().replace("(+$0.00)", "").trim());
		String towingAndLabor = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel()).replace("(Included)", "").replace("(+$0.00)", "").replace("$", "").trim();

		premiumAndCoveragesTab.saveAndExit();

		String coverageCd = "COMPDED";
		String availableLimits = "100";

		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, availableLimits), PolicyCoverageInfo.class);
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

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd1, availableLimits1), PolicyCoverageInfo.class);
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
		String availableLimits2 = "true";

		PolicyCoverageInfo coverageResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd2, availableLimits2), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse2.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimits, "$" + availableLimits, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimits1, "$" + availableLimits1, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", availableLimits2, "Yes", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "0", loanLeaseCov, "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Rental Reimbursement", "0/0", transportationExpense, "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);
		});

		String coverageCd3 = "LOAN";
		String availableLimits3 = "1";

		PolicyCoverageInfo coverageResponse3 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd3, availableLimits3), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse3.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimits, "$" + availableLimits, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimits1, "$" + availableLimits1, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", availableLimits2, "Yes", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Rental Reimbursement", "0/0", transportationExpense, "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);
		});

		String coverageCd4 = "RREIM";
		String availableLimits4 = "30/900";

		PolicyCoverageInfo coverageResponse4 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd4, availableLimits4), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse4.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimits, "$" + availableLimits, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimits1, "$" + availableLimits1, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", availableLimits2, "Yes", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Rental Reimbursement", availableLimits4, "$30/$900", "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);
		});

		String coverageCd5 = "TOWINGLABOR";
		String availableLimits5 = "50/300";

		PolicyCoverageInfo coverageResponse5 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCd5, availableLimits5), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse5.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimits, "$" + availableLimits, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimits1, "$" + availableLimits1, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", availableLimits2, "Yes", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Rental Reimbursement", availableLimits4, "$30/$900", "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", availableLimits5, "$50/$300", "Per Disablement/Maximum", true, true);
		});

		updateVehicleLeasedFinanced.vehicleOwnership.ownership = "OWN";
		VehicleUpdateResponseDto ownershipUpdateResponse2 = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleLeasedFinanced);
		assertThat(ownershipUpdateResponse2.vehicleOwnership.ownership).isEqualTo("OWN");

		String coverageCdRemove = "COMPDED";
		String availableLimitsRemove = "-1";

		PolicyCoverageInfo coverageResponse6 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdRemove, availableLimitsRemove), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse6.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimitsRemove, "No Coverage", "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsRemove, "No Coverage", "Deductible", true, false);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", "false", "No Coverage", "None", true, false);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "0", "No Coverage", "None", false, false);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Rental Reimbursement", "0/0", "No Coverage", "Per Day/Maximum", true, false);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, false);
		});

		updateVehicleLeasedFinanced.vehicleOwnership.ownership = "LSD";
		VehicleUpdateResponseDto ownershipUpdateResponse3 = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleLeasedFinanced);
		assertThat(ownershipUpdateResponse3.vehicleOwnership.ownership).isEqualTo("LSD");

		String coverageCdChange = "COMPDED";
		String availableLimitsChange = "500";

		PolicyCoverageInfo coverageResponse7 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChange, availableLimitsChange), PolicyCoverageInfo.class);
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

		PolicyCoverageInfo coverageResponse8 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeColl, availableLimitsChangeColl), PolicyCoverageInfo.class);
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
		String availableLimitsChangeGlass = "true";

		PolicyCoverageInfo coverageResponse9 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeGlass, availableLimitsChangeGlass), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse9.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", availableLimitsChangeGlass, "Yes", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "0", "No Coverage", "None", true, true);
			//coverageXproperties(softly, 4, coverageResponse9, "RREIM", "Rental Reimbursement", availableLimits4, availableLimits4, "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);
		});

		String coverageCdChangeGlassNoCov = "GLASS";
		String availableLimitsChangeGlassNoCov = "false";

		PolicyCoverageInfo coverageResponse10 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeGlassNoCov, availableLimitsChangeGlassNoCov), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse10.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", availableLimitsChangeGlassNoCov, "No Coverage", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "0", "No Coverage", "None", true, true);
			//coverageXproperties(softly, 4, coverageResponse10, "RREIM", "Rental Reimbursement", availableLimits4, availableLimits4, "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);

		});

		String coverageCdChangeLoanNoCov = "LOAN";
		String availableLimitsChangeLoanNoCov = "1";

		PolicyCoverageInfo coverageResponse11 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeLoanNoCov, availableLimitsChangeLoanNoCov), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse11.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", availableLimitsChangeGlassNoCov, "No Coverage", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);
			//coverageXproperties(softly, 4, coverageResponse11, "RREIM", "Rental Reimbursement", availableLimits4, availableLimits4, "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);
		});

		String coverageCdChangeTransport = "RREIM";
		String availableLimitsChangeTransport = "30/900";

		PolicyCoverageInfo coverageResponse12 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTransport, availableLimitsChangeTransport), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse12.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", availableLimitsChangeGlassNoCov, "No Coverage", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Rental Reimbursement", availableLimitsChangeTransport, "$30/$900", "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", "No Coverage", "Per Disablement/Maximum", true, true);
		});

		String coverageCdChangeTowing = "TOWINGLABOR";
		String availableLimitsChangeTowing = "50/300";

		PolicyCoverageInfo coverageResponse13 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTowing, availableLimitsChangeTowing), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse13.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", availableLimitsChangeGlassNoCov, "No Coverage", "None", true, true);
			coverageXproperties(softly, 3, coveragesV1, "LOAN", "Vehicle Loan/Lease Protection", "1", "Yes", "None", true, true);
			coverageXproperties(softly, 4, coveragesV1, "RREIM", "Rental Reimbursement", availableLimitsChangeTransport, "$30/$900", "Per Day/Maximum", true, true);
			coverageXproperties(softly, 5, coveragesV1, "TOWINGLABOR", "Towing and Labor Coverage", availableLimitsChangeTowing, "$50/$300", "Per Disablement/Maximum", true, true);
		});

		String coverageCdChangeTowingNoCov = "TOWINGLABOR";
		String availableLimitsChangeTowingNoCov = "0/0";

		PolicyCoverageInfo coverageResponse14 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTowingNoCov, availableLimitsChangeTowingNoCov), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesV1 = coverageResponse14.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesV1, "COMPDED", "Comprehensive Deductible", availableLimitsChange, "$" + availableLimitsChange, "Deductible", true, true);
			coverageXproperties(softly, 1, coveragesV1, "COLLDED", "Collision Deductible", availableLimitsChangeColl, "$" + availableLimitsChangeColl, "Deductible", true, true);
			coverageXproperties(softly, 2, coveragesV1, "GLASS", "Full Safety Glass", availableLimitsChangeGlassNoCov, "No Coverage", "None", true, true);
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

		PolicyCoverageInfo coverageResponse1 = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class);
		List<Coverage> coveragesVehicle = coverageResponse1.vehicleLevelCoverages.get(0).coverages;
		verifyRREIM(softly, coveragesVehicle);
		assertCoverageLimitRentalReimbursement(coverageResponse1);

		//Add first vehicle
		String purchaseDate = "2013-02-22";
		String vin1 = "1HGFA16526L081415";
		Vehicle addVehicle =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin1, purchaseDate), Vehicle.class, 201);
		assertThat(addVehicle.oid).isNotEmpty();
		String oid1 = addVehicle.oid;
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, oid1);

		PolicyCoverageInfo coverageResponse2 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, oid1, PolicyCoverageInfo.class);

		List<Coverage> coveragesVehicle1 = coverageResponse2.vehicleLevelCoverages.get(0).coverages;
		verifyRREIM(softly, coveragesVehicle1);
		assertCoverageLimitRentalReimbursement(coverageResponse2);

		String coverageCdChangeTransport = "RREIM";
		String availableLimitsChangeTransport = "30/900";

		PolicyCoverageInfo rreimUpdatedCoverage = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTransport, availableLimitsChangeTransport), PolicyCoverageInfo.class);
		Coverage filteredCoverageResponseRREIM = rreimUpdatedCoverage.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "RREIM".equals(cov.getCoverageCd())).findFirst().orElse(null);

		softly.assertThat(filteredCoverageResponseRREIM.getCoverageLimit()).isEqualTo("30/900");
		softly.assertThat(filteredCoverageResponseRREIM.getCoverageLimitDisplay()).isEqualTo("$30/$900");
		assertCoverageLimitRentalReimbursementAfterUpdate(rreimUpdatedCoverage);

		String coverageCdChangeTransport1 = "RREIM";
		String availableLimitsChangeTransport1 = "0/0";

		PolicyCoverageInfo rreimUpdatedCoverage1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTransport1, availableLimitsChangeTransport1), PolicyCoverageInfo.class);
		Coverage filteredCoverageResponseRREIM1 = rreimUpdatedCoverage1.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "RREIM".equals(cov.getCoverageCd())).findFirst().orElse(null);

		softly.assertThat(filteredCoverageResponseRREIM1.getCoverageLimit()).isEqualTo("0/0");
		softly.assertThat(filteredCoverageResponseRREIM1.getCoverageLimitDisplay()).isEqualTo("No Coverage");
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

		PolicyCoverageInfo coverageResponse1 = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class);
		List<Coverage> coveragesVehicle = coverageResponse1.vehicleLevelCoverages.get(0).coverages;
		verifyRREIM(softly, coveragesVehicle);
		assertCoverageLimitRentalReimbursement(coverageResponse1);

		//Add first vehicle
		String purchaseDate = "2013-02-22";
		String vin1 = "1HGFA16526L081415";
		Vehicle addVehicle =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin1, purchaseDate), Vehicle.class, 201);
		assertThat(addVehicle.oid).isNotEmpty();
		String oid1 = addVehicle.oid;
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, oid1);

		PolicyCoverageInfo coverageResponse2 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, oid1, PolicyCoverageInfo.class);

		List<Coverage> coveragesVehicle1 = coverageResponse2.vehicleLevelCoverages.get(0).coverages;
		verifyRREIM(softly, coveragesVehicle1);
		assertCoverageLimitRentalReimbursement(coverageResponse2);

		String coverageCdChangeTransport = "RREIM";
		String availableLimitsChangeTransport = "30/900";

		PolicyCoverageInfo rreimUpdatedCoverage = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeTransport, availableLimitsChangeTransport), PolicyCoverageInfo.class);
		Coverage filteredCoverageResponseRREIM1 = rreimUpdatedCoverage.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "RREIM".equals(cov.getCoverageCd())).findFirst().orElse(null);

		softly.assertThat(filteredCoverageResponseRREIM1.getCoverageLimit()).isEqualTo("30/900");
		softly.assertThat(filteredCoverageResponseRREIM1.getCoverageLimitDisplay()).isEqualTo("$30/$900");
		assertCoverageLimitRentalReimbursementAfterUpdate(rreimUpdatedCoverage);

		String coverageCdChange = "COMPDED";
		String availableLimitsChange = "-1";

		PolicyCoverageInfo rreimUpdatedCoverage1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChange, availableLimitsChange), PolicyCoverageInfo.class);
		Coverage filteredCoverageResponseComp = rreimUpdatedCoverage1.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COMPDED".equals(cov.getCoverageCd())).findFirst().orElse(null);
		Coverage filteredCoverageResponseRreim = rreimUpdatedCoverage1.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "RREIM".equals(cov.getCoverageCd())).findFirst().orElse(null);

		softly.assertThat(filteredCoverageResponseComp.getCoverageLimit()).isEqualTo("-1");
		softly.assertThat(filteredCoverageResponseComp.getCoverageLimitDisplay()).isEqualTo("No Coverage");

		softly.assertThat(filteredCoverageResponseRreim.getCoverageLimit()).isEqualTo("0/0");
		softly.assertThat(filteredCoverageResponseRreim.getCoverageLimitDisplay()).isEqualTo("No Coverage");
		softly.assertThat(filteredCoverageResponseRreim.getCanChangeCoverage()).isEqualTo(false);
		assertCoverageLimitRentalReimbursement(rreimUpdatedCoverage1);

		String coverageCdChangeCom = "COMPDED";
		String availableLimitsChangeCom = "500";

		PolicyCoverageInfo rreimUpdatedCoverageComp = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeCom, availableLimitsChangeCom), PolicyCoverageInfo.class);
		Coverage filteredCoverageResponseComp1 = rreimUpdatedCoverageComp.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COMPDED".equals(cov.getCoverageCd())).findFirst().orElse(null);
		Coverage filteredCoverageResponseRreim1 = rreimUpdatedCoverageComp.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "RREIM".equals(cov.getCoverageCd())).findFirst().orElse(null);

		softly.assertThat(filteredCoverageResponseComp1.getCoverageLimit()).isEqualTo("500");
		softly.assertThat(filteredCoverageResponseComp1.getCoverageLimitDisplay()).isEqualTo("$500");

		softly.assertThat(filteredCoverageResponseRreim1.getCoverageLimit()).isEqualTo("0/0");
		softly.assertThat(filteredCoverageResponseRreim1.getCoverageLimitDisplay()).isEqualTo("No Coverage");
		assertCoverageLimitRentalReimbursement(rreimUpdatedCoverageComp);

		String coverageCdChangeColl = "COLLDED";
		String availableLimitsChangeColl = "500";

		PolicyCoverageInfo rreimUpdatedCoverageColl1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChangeColl, availableLimitsChangeColl), PolicyCoverageInfo.class);

		Coverage filteredCoverageResponseColl = rreimUpdatedCoverageColl1.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COLLDED".equals(cov.getCoverageCd())).findFirst().orElse(null);
		Coverage filteredCoverageResponseRreimColl = rreimUpdatedCoverageColl1.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "RREIM".equals(cov.getCoverageCd())).findFirst().orElse(null);

		softly.assertThat(filteredCoverageResponseColl.getCoverageLimit()).isEqualTo("500");
		softly.assertThat(filteredCoverageResponseColl.getCoverageLimitDisplay()).isEqualTo("$500");

		softly.assertThat(filteredCoverageResponseRreimColl.getCoverageLimit()).isEqualTo("0/0");
		softly.assertThat(filteredCoverageResponseRreimColl.getCoverageLimitDisplay()).isEqualTo("No Coverage");
		assertCoverageLimitRentalReimbursement(rreimUpdatedCoverageColl1);

		String coverageChangeTransport = "RREIM";
		String availableLimitsTransport = "50/1500";

		PolicyCoverageInfo rreimUpdatedCoverageHigh = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageChangeTransport, availableLimitsTransport), PolicyCoverageInfo.class);
		Coverage filteredCoverageResponseRreimHigh = rreimUpdatedCoverageHigh.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "RREIM".equals(cov.getCoverageCd())).findFirst().orElse(null);

		softly.assertThat(filteredCoverageResponseRreimHigh.getCoverageLimit()).isEqualTo("50/1500");
		softly.assertThat(filteredCoverageResponseRreimHigh.getCoverageLimitDisplay()).isEqualTo("$50/$1,500");
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
		PolicyCoverageInfo endorsementCoverageResponseOwnedOldVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class);
		Coverage endorsementCoverageResponseOwnedOldVehFiltered = endorsementCoverageResponseOwnedOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.getCoverageCd())).findFirst().orElse(null);
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
		PolicyCoverageInfo endorsementCoverageResponseLsdFinOldVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class);
		Coverage endorsementCoverageResponseLsdFinOldVehFiltered = endorsementCoverageResponseLsdFinOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.getCoverageCd())).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, endorsementCoverageResponseLsdFinOldVehFiltered, "0", "No Coverage", false, false);

		helperMiniServices.endorsementRateAndBind(policyNumber);
		PolicyCoverageInfo policyCoverageResponseLsdFinOldVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class);
		Coverage policyCoverageResponseLsdFinOldVehFiltered = policyCoverageResponseLsdFinOldVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.getCoverageCd())).findFirst().orElse(null);
		loanLeaseCovPropertiesCheck(softly, policyCoverageResponseLsdFinOldVehFiltered, "0", "No Coverage", false, false);

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		VehicleUpdateDto updateVehicleOwned = new VehicleUpdateDto();
		updateVehicleOwned.vehicleOwnership = new VehicleOwnership();
		updateVehicleOwned.vehicleOwnership.ownership = "OWN";
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleOwned);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo endorsementCoverageResponseOwnedOldVeh2 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class);
		Coverage endorsementCoverageResponseOwnedOldVeh2Filtered = endorsementCoverageResponseOwnedOldVeh2.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCdValue.equals(attribute.getCoverageCd())).findFirst().orElse(null);
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

		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, newBILimits), PolicyCoverageInfo.class);
		assertSoftly(softly -> {

			Coverage filteredCoverageResponseBI = coverageResponse.policyCoverages.stream().filter(cov -> "BI".equals(cov.getCoverageCd())).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseBI.getCoverageLimit().equals(newBILimits)).isEqualTo(true);
			softly.assertThat("$500,000/$500,000".equals(filteredCoverageResponseBI.getCoverageLimitDisplay())).isEqualTo(true);

			assertCoverageLimitForBI(coverageResponse, state);

			Coverage filteredCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat("50000".equals(filteredCoverageResponsePD.getCoverageLimit())).isEqualTo(true);
			softly.assertThat("$50,000".equals(filteredCoverageResponsePD.getCoverageLimitDisplay())).isEqualTo(true);

			assertCoverageLimitForPDBI(coverageResponse, state);
		});

		String coverageCd1 = "PD";
		String newPD1 = "500000";

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd1, newPD1), PolicyCoverageInfo.class);
		assertSoftly(softly -> {

			Coverage filteredCoverageResponse1 = coverageResponse1.policyCoverages.stream().filter(cov -> "BI".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponse1.getCoverageLimit().equals(newBILimits)).isEqualTo(true);
			softly.assertThat("$500,000/$500,000".equals(filteredCoverageResponse1.getCoverageLimitDisplay())).isEqualTo(true);

			Coverage filteredCoverageResponsePD1 = coverageResponse1.policyCoverages.stream().filter(cov -> "PD".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat("500000".equals(filteredCoverageResponsePD1.getCoverageLimit())).isEqualTo(true);
			softly.assertThat("$500,000".equals(filteredCoverageResponsePD1.getCoverageLimitDisplay())).isEqualTo(true);

			assertCoverageLimitForPDBI(coverageResponse1, state);

		});

		String coverageCd2 = "BI";
		String newBI2 = "50000/100000";

		PolicyCoverageInfo coverageResponse2 = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd2, newBI2), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			Coverage filteredCoverageResponseBI = coverageResponse2.policyCoverages.stream().filter(cov -> "BI".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponseBI.getCoverageLimit().equals(newBI2)).isEqualTo(true);
			softly.assertThat("$50,000/$100,000".equals(filteredCoverageResponseBI.getCoverageLimitDisplay())).isEqualTo(true);

			Coverage filteredCoverageResponsePD2 = coverageResponse2.policyCoverages.stream().filter(cov -> "PD".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat("100000".equals(filteredCoverageResponsePD2.getCoverageLimit())).isEqualTo(true);
			softly.assertThat("$100,000".equals(filteredCoverageResponsePD2.getCoverageLimitDisplay())).isEqualTo(true);

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

		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, newBILimits), PolicyCoverageInfo.class);
		assertSoftly(softly -> {

			Coverage filteredCoverageResponseBI = coverageResponse.policyCoverages.stream().filter(cov -> "BI".equals(cov.getCoverageCd())).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseBI.getCoverageLimit().equals(newBILimits)).isEqualTo(true);
			softly.assertThat("$500,000/$500,000".equals(filteredCoverageResponseBI.getCoverageLimitDisplay())).isEqualTo(true);

			//assertCoverageLimitForBI(coverageResponse, state); //TODO-mstrazds: uncomment when pas14721_UpdateCoveragesServiceBIPD is implemented for all states

			Coverage filteredCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat("50000".equals(filteredCoverageResponsePD.getCoverageLimit())).isEqualTo(true);
			softly.assertThat("$50,000".equals(filteredCoverageResponsePD.getCoverageLimitDisplay())).isEqualTo(true);

			//assertCoverageLimitForPDBI(coverageResponse, state); //TODO-mstrazds: uncomment when pas14721_UpdateCoveragesServiceBIPD is implemented for all states

			Coverage filteredCoverageResponseUMBI = coverageResponse.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(newBILimits.equals(filteredCoverageResponseUMBI.getCoverageLimit())).isEqualTo(true);
			softly.assertThat("$500,000/$500,000".equals(filteredCoverageResponseUMBI.getCoverageLimitDisplay())).isEqualTo(true);

			validateUIMBI_pas15254(softly, state, coverageResponse, newBILimits); //validate UIMBI for states where it is separate coverage

		});

		String coverageCd1 = "PD";
		String newPD1 = "500000";

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd1, newPD1), PolicyCoverageInfo.class);
		assertSoftly(softly -> {

			Coverage filteredCoverageResponse1 = coverageResponse1.policyCoverages.stream().filter(cov -> "BI".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponse1.getCoverageLimit().equals(newBILimits)).isEqualTo(true);
			softly.assertThat("$500,000/$500,000".equals(filteredCoverageResponse1.getCoverageLimitDisplay())).isEqualTo(true);

			Coverage filteredCoverageResponsePD1 = coverageResponse1.policyCoverages.stream().filter(cov -> "PD".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat("500000".equals(filteredCoverageResponsePD1.getCoverageLimit())).isEqualTo(true);
			softly.assertThat("$500,000".equals(filteredCoverageResponsePD1.getCoverageLimitDisplay())).isEqualTo(true);

			//assertCoverageLimitForPDBI(coverageResponse1, state); //TODO-mstrazds: uncomment when pas14721_UpdateCoveragesServiceBIPD is implemented for all states

			Coverage filteredCoverageResponseUMBI = coverageResponse1.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(newBILimits.equals(filteredCoverageResponseUMBI.getCoverageLimit())).isEqualTo(true);
			softly.assertThat("$500,000/$500,000".equals(filteredCoverageResponseUMBI.getCoverageLimitDisplay())).isEqualTo(true);

			validateUIMBI_pas15254(softly, state, coverageResponse1, newBILimits); //validate UIMBI for states where it is separate coverage

		});

		String coverageCd2 = "BI";
		String newBI2 = "50000/100000";

		PolicyCoverageInfo coverageResponse2 = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd2, newBI2), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			Coverage filteredCoverageResponseBI = coverageResponse2.policyCoverages.stream().filter(cov -> "BI".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponseBI.getCoverageLimit().equals(newBI2)).isEqualTo(true);
			softly.assertThat("$50,000/$100,000".equals(filteredCoverageResponseBI.getCoverageLimitDisplay())).isEqualTo(true);

			Coverage filteredCoverageResponsePD2 = coverageResponse2.policyCoverages.stream().filter(cov -> "PD".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat("100000".equals(filteredCoverageResponsePD2.getCoverageLimit())).isEqualTo(true);
			softly.assertThat("$100,000".equals(filteredCoverageResponsePD2.getCoverageLimitDisplay())).isEqualTo(true);
			//TODO Maris to fix it during refactoring
			//assertCoverageLimitForPD(coverageResponse2, state);

			Coverage filteredCoverageResponseUMBI = coverageResponse2.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(newBI2.equals(filteredCoverageResponseUMBI.getCoverageLimit())).isEqualTo(true);
			softly.assertThat("$50,000/$100,000".equals(filteredCoverageResponseUMBI.getCoverageLimitDisplay())).isEqualTo(true);

			validateUIMBI_pas15254(softly, state, coverageResponse2, newBI2); //validate UIMBI for states where it is separate coverage
		});

		mainApp().close();
		helperMiniServices.endorsementRateAndBind(policyNumber);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		testEValueDiscount.secondEndorsementIssueCheck();

	}

	protected void pas17646_OrderOfCoverageBody(ETCSCoreSoftAssertions softly) {
		List<String> orderOfPolicyLevelCoveragesExpected = getTestSpecificTD("TestData_OrderOfCoverages").getList("PolicyLevelCoverages");
		List<String> orderOfVehicleLevelCoveragesExpected = getTestSpecificTD("TestData_OrderOfCoverages").getList("VehicleLevelCoverages");
		List<String> orderOfDriverLevelCoveragesExpected = getTestSpecificTD("TestData_OrderOfCoverages").getList("DriverLevelCoverages");

		mainApp().open();
		String policyNumber = getCopiedPolicy();
		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//Run viewEndorsementCoverages and validate order of coverages in response
		PolicyCoverageInfo policyCoverageInfo = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		validateOrderOfAllLevelCoverages(softly, orderOfPolicyLevelCoveragesExpected, orderOfVehicleLevelCoveragesExpected, orderOfDriverLevelCoveragesExpected, policyCoverageInfo);

		//Run updateCoverage service and validate order of coverages in response
		Coverage coverageToUpdate = policyCoverageInfo.policyCoverages.get(0);
		String newLimit = coverageToUpdate.getAvailableLimits().get(0).coverageLimit;
		UpdateCoverageRequest updateCoverageRequest = DXPRequestFactory.createUpdateCoverageRequest(coverageToUpdate.getCoverageCd(), newLimit);
		policyCoverageInfo = HelperCommon.updateEndorsementCoverage(policyNumber, updateCoverageRequest, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		validateOrderOfAllLevelCoverages(softly, orderOfPolicyLevelCoveragesExpected, orderOfVehicleLevelCoveragesExpected, orderOfDriverLevelCoveragesExpected, policyCoverageInfo);

		//NOTE: Validation of Change History is too complicated for automation - have to update every coverage. Should be tested manually if needed.
	}

	private void validateOrderOfAllLevelCoverages(ETCSCoreSoftAssertions softly, List<String> orderOfPolicyCoveragesExpected, List<String> orderOfVehicleCoveragesExpected, List<String> orderOfDriverCoveragesExpected, PolicyCoverageInfo coverageEndorsementResponse) {
		validateOrderOfCoverages(softly, orderOfPolicyCoveragesExpected, coverageEndorsementResponse.policyCoverages);
		validateOrderOfCoverages(softly, orderOfVehicleCoveragesExpected, coverageEndorsementResponse.vehicleLevelCoverages.get(0).coverages);
		if (!orderOfDriverCoveragesExpected.isEmpty()) { //do not have requirements regarding to driver coverages for all states
			validateOrderOfCoverages(softly, orderOfDriverCoveragesExpected, coverageEndorsementResponse.driverCoverages);
		}
	}

	private void validateOrderOfCoverages(ETCSCoreSoftAssertions softly, List<String> orderOfCoveragesExpected, List<Coverage> coveragesActual) {
		//Put Coverages and SubCoverages (if exist) in the same list
		List<Coverage> coverageWithSubCoveragesActual = new ArrayList<>();
		for (Coverage coverage : coveragesActual) {
			coverageWithSubCoveragesActual.add(coverage);
			List<Coverage> subCoverages = coverage.getSubCoverages();
			if (subCoverages != null) {
				for (Coverage subCoverage : subCoverages) {
					coverageWithSubCoveragesActual.add(subCoverage);
				}
			}
		}

		softly.assertThat(coverageWithSubCoveragesActual.size()).isEqualTo(orderOfCoveragesExpected.size());
		for (String coverageCD : orderOfCoveragesExpected) {
			int index = orderOfCoveragesExpected.indexOf(coverageCD);
			softly.assertThat(coverageWithSubCoveragesActual.get(index).getCoverageCd()).as(coverageCD + " is expected to be at index " + index).isEqualTo(coverageCD);
		}
	}

	protected void pas14646_UimDelimiter(String state, ETCSCoreSoftAssertions softly) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		SearchPage.openPolicy(policyNumber);
		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class);
		Coverage filteredPolicyCoverageResponse = policyCoverageResponse.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.getCoverageCd())).findFirst().orElse(null);
		softly.assertThat(filteredPolicyCoverageResponse.getCoverageType()).isEqualTo("Per Person/Per Accident");
		softly.assertThat(filteredPolicyCoverageResponse.getAvailableLimits().size()).isNotEqualTo(0);

		PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		Coverage filteredCoverageEndorsementResponse = coverageEndorsementResponse.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.getCoverageCd())).findFirst().orElse(null);
		softly.assertThat(filteredCoverageEndorsementResponse.getCoverageType()).isEqualTo("Per Person/Per Accident");
		softly.assertThat(filteredPolicyCoverageResponse.getAvailableLimits().size()).isNotEqualTo(0);

	}

	protected void pas14648_MedpmDelimiter(PolicyType policyType) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class);
		Coverage filteredPolicyCoverageResponse = policyCoverageResponse.policyCoverages.stream().filter(cov -> "MEDPM".equals(cov.getCoverageCd())).findFirst().orElse(null);
		assertSoftly(softly -> {
			softly.assertThat(filteredPolicyCoverageResponse.getCoverageCd()).isEqualTo("MEDPM");
			softly.assertThat(filteredPolicyCoverageResponse.getCoverageType()).isEqualTo("Per Person");
			softly.assertThat(filteredPolicyCoverageResponse.getAvailableLimits().size()).isNotEqualTo(0);
		});

		PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		Coverage filteredPolicyCoverageResponse1 = coverageEndorsementResponse.policyCoverages.stream().filter(cov -> "MEDPM".equals(cov.getCoverageCd())).findFirst().orElse(null);
		assertSoftly(softly -> {
			softly.assertThat(filteredPolicyCoverageResponse1.getCoverageCd()).isEqualTo("MEDPM");
			softly.assertThat(filteredPolicyCoverageResponse1.getCoverageType()).isEqualTo("Per Person");
			softly.assertThat(filteredPolicyCoverageResponse1.getAvailableLimits().size()).isNotEqualTo(0);
		});
	}

	protected void pas15228_UmUimDelimiterBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		assertSoftly(softly -> {
			PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class);
			Coverage filteredPolicyCoverageResponseUMBI = policyCoverageResponse.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseUMBI.getCoverageType()).isEqualTo("Per Person/Per Accident");
			softly.assertThat(filteredPolicyCoverageResponseUMBI.getAvailableLimits().size()).isNotEqualTo(0);
			softly.assertThat(filteredPolicyCoverageResponseUMBI.getCanChangeCoverage()).isFalse();

			Coverage filteredPolicyCoverageResponseUIMBI = policyCoverageResponse.policyCoverages.stream().filter(cov -> "UIMBI".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseUIMBI.getCoverageType()).isEqualTo("Per Person/Per Accident");
			softly.assertThat(filteredPolicyCoverageResponseUIMBI.getAvailableLimits().size()).isNotEqualTo(0);
			softly.assertThat(filteredPolicyCoverageResponseUIMBI.getCanChangeCoverage()).isFalse();

			PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
			Coverage filteredEndorsementCoverageResponseUMBI = coverageEndorsementResponse.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredEndorsementCoverageResponseUMBI.getCoverageType()).isEqualTo("Per Person/Per Accident");
			softly.assertThat(filteredEndorsementCoverageResponseUMBI.getAvailableLimits().size()).isNotEqualTo(0);
			softly.assertThat(filteredEndorsementCoverageResponseUMBI.getCanChangeCoverage()).isFalse();

			Coverage filteredEndorsementCoverageResponseUIMBI = coverageEndorsementResponse.policyCoverages.stream().filter(cov -> "UIMBI".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredEndorsementCoverageResponseUIMBI.getCoverageType()).isEqualTo("Per Person/Per Accident");
			softly.assertThat(filteredEndorsementCoverageResponseUIMBI.getAvailableLimits().size()).isNotEqualTo(0);
			softly.assertThat(filteredEndorsementCoverageResponseUIMBI.getCanChangeCoverage()).isFalse();
		});
	}

	protected void pas15824_UmpdDelimiterBody() {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		assertSoftly(softly -> {
			PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class);
			Coverage filteredPolicyCoverageResponseUMPD = policyCoverageResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.getCoverageCd())).findFirst().orElse(null);
			//BUG: PAS-15829 UMPD not returned from viewPolicyCoverages for NJ (for Policy and Endorsement)
			softly.assertThat(filteredPolicyCoverageResponseUMPD.getCoverageType()).isEqualTo("Per Accident");
			softly.assertThat(filteredPolicyCoverageResponseUMPD.getCanChangeCoverage()).isFalse();

			PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
			Coverage filteredEndorsementCoverageResponseUMPD = coverageEndorsementResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredEndorsementCoverageResponseUMPD.getCoverageType()).isEqualTo("Per Accident");
			softly.assertThat(filteredEndorsementCoverageResponseUMPD.getCanChangeCoverage()).isFalse();
		});
	}

	protected void pas15325_UmpdNotExistBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		assertSoftly(softly -> {
			PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class);
			assertThat(policyCoverageResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.getCoverageCd())).findFirst().orElse(null)).isNull();

			PolicyCoverageInfo coverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
			assertThat(coverageEndorsementResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.getCoverageCd())).findFirst().orElse(null)).isNull();
		});
	}

	//validate UIMBI for states where it is separate coverage
	private void validateUIMBI_pas15254(ETCSCoreSoftAssertions softly, String state, PolicyCoverageInfo coverageResponse, String newBILimits) {
		if ("AZ, ID, KY, PA, SD, UT, WV, MT".contains(state)) {
			Coverage filteredCoverageResponseUIMBI = coverageResponse.policyCoverages.stream().filter(cov -> "UIMBI".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(newBILimits.equals(filteredCoverageResponseUIMBI.getCoverageLimit())).isEqualTo(true);
		}
	}

	private void coverageXproperties(ETCSCoreSoftAssertions softly, int coverageXnumber, List<Coverage> coverageList, String coverageCd, String coverageDesc, String coverageLimit, String coverageLimitDisplay, String coverageType, boolean customerDisplay, boolean canChangeCoverage) {
		Coverage coverage = coverageList.get(coverageXnumber);
		coverageXproperties(softly, coverage, coverageCd, coverageDesc, coverageLimit, coverageLimitDisplay, coverageType, customerDisplay, canChangeCoverage);
	}

	private void coverageXproperties(ETCSCoreSoftAssertions softly, Coverage coverage, String coverageCd, String coverageDesc, String coverageLimit, String coverageLimitDisplay, String coverageType, boolean customerDisplay, boolean canChangeCoverage) {
		softly.assertThat(coverage.getCoverageCd()).isEqualTo(coverageCd);
		softly.assertThat(coverage.getCoverageDescription()).isEqualTo(coverageDesc);

		//check coverageLimit
		if (StringUtils.isEmpty(coverageLimit)) {
			softly.assertThat(coverage.getCoverageLimit()).isEqualTo(coverageLimit);
		} else {
			softly.assertThat(coverage.getCoverageLimit()).isEqualTo(coverageLimit.replace(".00", ""));
		}

		//check coverageLimitDisplay
		if (StringUtils.isEmpty(coverageLimitDisplay)) {
			softly.assertThat(coverage.getCoverageLimitDisplay()).isEqualTo(coverageLimitDisplay);
		} else {
			//for SPECEQUIP and CUSTEQUIP coverageLimitDisplay should be with ".00", for other coverages without ".00"
			if ("SPECEQUIP, CUSTEQUIP".contains(coverage.getCoverageCd())) {
				softly.assertThat(coverage.getCoverageLimitDisplay()).isEqualTo(coverageLimitDisplay.toString().replace("(+$0)", "").trim());
			} else {
				softly.assertThat(coverage.getCoverageLimitDisplay()).isEqualTo(coverageLimitDisplay.toString().replace(".00", "").replace("(+$0)", "").trim());
			}
		}

		softly.assertThat(coverage.getCoverageType()).isEqualTo(coverageType);
		softly.assertThat(coverage.getCustomerDisplayed()).isEqualTo(customerDisplay);
		softly.assertThat(coverage.getCanChangeCoverage()).isEqualTo(canChangeCoverage);
	}

	private void assertCoverageLimitTowingLabor(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			Coverage coverageLimit = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(5);
			softly.assertThat(coverageLimit.getAvailableLimits().get(0).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageLimit.getAvailableLimits().get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageLimit.getAvailableLimits().get(1).coverageLimit).isEqualTo("50/300");
			softly.assertThat(coverageLimit.getAvailableLimits().get(1).coverageLimitDisplay).isEqualTo("$50/$300");
		});
	}

	private void assertCoverageLimitTransportationExpense(PolicyCoverageInfo coverageResponse, boolean filtered) {
		assertSoftly(softly -> {
			Coverage coverageLimitTransportation = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4);
			softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(0).coverageLimit).isEqualTo("600");
			softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(0).coverageLimitDisplay).isEqualTo("$600 (Included)");

			softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(1).coverageLimit).isEqualTo("900");
			softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(1).coverageLimitDisplay).isEqualTo("$900");

			if (filtered) {
				softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4).getAvailableLimits().size()).isEqualTo(2);
			} else {
				softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(2).coverageLimit).isEqualTo("1200");
				softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(2).coverageLimitDisplay).isEqualTo("$1,200");

				softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(3).coverageLimit).isEqualTo("1500");
				softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(3).coverageLimitDisplay).isEqualTo("$1,500");
			}
		});
	}

	private void assertCoverageLimitRentalReimbursement(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			Coverage coverageLimitTransportation = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4);

			softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(0).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(1).coverageLimit).isEqualTo("30/900");
			softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(1).coverageLimitDisplay).isEqualTo("$30/$900");

			softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(2).coverageLimit).isEqualTo("40/1200");
			softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(2).coverageLimitDisplay).isEqualTo("$40/$1,200");

			softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(3).coverageLimit).isEqualTo("50/1500");
			softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(3).coverageLimitDisplay).isEqualTo("$50/$1,500");

		});
	}

	private void assertCoverageLimitRentalReimbursementAfterUpdate(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			Coverage coverageLimitTransportation = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4);

			softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(0).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(1).coverageLimit).isEqualTo("30/900");
			softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(1).coverageLimitDisplay).isEqualTo("$30/$900");

		});
	}

	private void assertCoverageLimitRentalReimbursementAfterUpdateRreim(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			Coverage coverageLimitTransportation = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(4);

			softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(0).coverageLimit).isEqualTo("0/0");
			softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(1).coverageLimit).isEqualTo("50/1500");
			softly.assertThat(coverageLimitTransportation.getAvailableLimits().get(1).coverageLimitDisplay).isEqualTo("$50/$1,500");

		});
	}

	private void assertCoverageLimitLoan(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			List<CoverageLimit> availableLimitsLoan = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(3).getAvailableLimits();
			softly.assertThat(availableLimitsLoan.get(0).coverageLimit).isEqualTo("0");
			softly.assertThat(availableLimitsLoan.get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(availableLimitsLoan.get(1).coverageLimit).isEqualTo("1");
			softly.assertThat(availableLimitsLoan.get(1).coverageLimitDisplay).isEqualTo("Yes");
		});
	}

	private void assertCoverageLimitForCompColl(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			List<CoverageLimit> availableLimitsCompColl = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getAvailableLimits();
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
			List<CoverageLimit> availableLimitsCollLoan = coverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getAvailableLimits();
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
			softly.assertThat(coverageFullGlass.getAvailableLimits().get(0).coverageLimit).isEqualTo("false");
			softly.assertThat(coverageFullGlass.getAvailableLimits().get(0).coverageLimitDisplay).isEqualTo("No Coverage");

			softly.assertThat(coverageFullGlass.getAvailableLimits().get(1).coverageLimit).isEqualTo("true");
			softly.assertThat(coverageFullGlass.getAvailableLimits().get(1).coverageLimitDisplay).isEqualTo("Yes");
		});
	}

	private void assertCoverageLimitForBI(PolicyCoverageInfo coverageResponse, String state) {
		assertSoftly(softly -> {
			if ("VA".contains(state)) {

				Coverage filteredCoverageResponseBI = coverageResponse.policyCoverages.stream().filter(cov -> "BI".equals(cov.getCoverageCd())).findFirst().orElse(null);
				List<CoverageLimit> availableLimits = filteredCoverageResponseBI.getAvailableLimits();

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

				Coverage filteredCoverageResponseBI = coverageResponse.policyCoverages.stream().filter(cov -> "BI".equals(cov.getCoverageCd())).findFirst().orElse(null);
				List<CoverageLimit> availableLimits = filteredCoverageResponseBI.getAvailableLimits();

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
				Coverage filteredCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.getCoverageCd())).findFirst().orElse(null);
				List<CoverageLimit> availableLimitsPD = filteredCoverageResponsePD.getAvailableLimits();

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
				Coverage filteredCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.getCoverageCd())).findFirst().orElse(null);
				List<CoverageLimit> availableLimitsPD = filteredCoverageResponsePD.getAvailableLimits();

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

				Coverage filteredCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.getCoverageCd())).findFirst().orElse(null);
				List<CoverageLimit> availableLimitsPDBI = filteredCoverageResponsePD.getAvailableLimits();

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
				Coverage filteredCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.getCoverageCd())).findFirst().orElse(null);
				List<CoverageLimit> availableLimitsPDBI = filteredCoverageResponsePD.getAvailableLimits();

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

		PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class);
		assertSoftly(softly ->
				viewCoveragesBiPd(policyCoverageResponse, softly)
		);

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo policyCoverageEndorsementResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		assertSoftly(softly ->
				viewCoveragesBiPd(policyCoverageEndorsementResponse, softly)
		);
	}

	private void viewCoveragesBiPd(PolicyCoverageInfo policyCoverageResponse, ETCSCoreSoftAssertions softly) {
		Coverage coverageLimit = policyCoverageResponse.policyCoverages.get(0);
		coverageXproperties(softly, coverageLimit, "BI", "Bodily Injury Liability", "100000/300000", "$100,000/$300,000", "Per Person/Per Accident", true, true);

		softly.assertThat(coverageLimit.getAvailableLimits().get(0).coverageLimit).isEqualTo("25000/50000");
		softly.assertThat(coverageLimit.getAvailableLimits().get(0).coverageLimitDisplay).isEqualTo("$25,000/$50,000");

		softly.assertThat(coverageLimit.getAvailableLimits().get(1).coverageLimit).isEqualTo("50000/100000");
		softly.assertThat(coverageLimit.getAvailableLimits().get(1).coverageLimitDisplay).isEqualTo("$50,000/$100,000");

		softly.assertThat(coverageLimit.getAvailableLimits().get(2).coverageLimit).isEqualTo("100000/300000");
		softly.assertThat(coverageLimit.getAvailableLimits().get(2).coverageLimitDisplay).isEqualTo("$100,000/$300,000");

		softly.assertThat(coverageLimit.getAvailableLimits().get(3).coverageLimit).isEqualTo("250000/500000");
		softly.assertThat(coverageLimit.getAvailableLimits().get(3).coverageLimitDisplay).isEqualTo("$250,000/$500,000");

		softly.assertThat(coverageLimit.getAvailableLimits().get(4).coverageLimit).isEqualTo("300000/500000");
		softly.assertThat(coverageLimit.getAvailableLimits().get(4).coverageLimitDisplay).isEqualTo("$300,000/$500,000");

		softly.assertThat(coverageLimit.getAvailableLimits().get(5).coverageLimit).isEqualTo("500000/500000");
		softly.assertThat(coverageLimit.getAvailableLimits().get(5).coverageLimitDisplay).isEqualTo("$500,000/$500,000");

		softly.assertThat(coverageLimit.getAvailableLimits().get(6).coverageLimit).isEqualTo("500000/1000000");
		softly.assertThat(coverageLimit.getAvailableLimits().get(6).coverageLimitDisplay).isEqualTo("$500,000/$1,000,000");

		softly.assertThat(coverageLimit.getAvailableLimits().get(7).coverageLimit).isEqualTo("1000000/1000000");
		softly.assertThat(coverageLimit.getAvailableLimits().get(7).coverageLimitDisplay).isEqualTo("$1,000,000/$1,000,000");

		Coverage coveragePD = policyCoverageResponse.policyCoverages.get(1);
		coverageXproperties(softly, coveragePD, "PD", "Property Damage Liability", "50000", "$50,000", "Per Accident", true, true);

		softly.assertThat(coveragePD.getAvailableLimits().get(0).coverageLimit).isEqualTo("20000");
		softly.assertThat(coveragePD.getAvailableLimits().get(0).coverageLimitDisplay).isEqualTo("$20,000");

		softly.assertThat(coveragePD.getAvailableLimits().get(1).coverageLimit).isEqualTo("25000");
		softly.assertThat(coveragePD.getAvailableLimits().get(1).coverageLimitDisplay).isEqualTo("$25,000");

		softly.assertThat(coveragePD.getAvailableLimits().get(2).coverageLimit).isEqualTo("40000");
		softly.assertThat(coveragePD.getAvailableLimits().get(2).coverageLimitDisplay).isEqualTo("$40,000");

		softly.assertThat(coveragePD.getAvailableLimits().get(3).coverageLimit).isEqualTo("50000");
		softly.assertThat(coveragePD.getAvailableLimits().get(3).coverageLimitDisplay).isEqualTo("$50,000");

		softly.assertThat(coveragePD.getAvailableLimits().get(4).coverageLimit).isEqualTo("100000");
		softly.assertThat(coveragePD.getAvailableLimits().get(4).coverageLimitDisplay).isEqualTo("$100,000");

		softly.assertThat(coveragePD.getAvailableLimits().get(5).coverageLimit).isEqualTo("300000");
		softly.assertThat(coveragePD.getAvailableLimits().get(5).coverageLimitDisplay).isEqualTo("$300,000");
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
		Vehicle addVehicle =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin, purchaseDate), Vehicle.class, 201);
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

		PolicyCoverageInfo coverageResponseV = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, oid, PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesVehicle = coverageResponseV.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesVehicle, "COMPDED", "Other Than Collision", comprehensiveDeductible.toPlaingString(), comprehensiveDeductible.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageResponseV);

			coverageXproperties(softly, 1, coveragesVehicle, "COLLDED", "Collision Deductible", collisionDeductible.toPlaingString(), collisionDeductible.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageResponseV);

			coverageXproperties(softly, 2, coveragesVehicle, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVeh, "None", true, true);
			assertCoverageLimitFullGlassCov(coverageResponseV);

			coverageXproperties(softly, 3, coveragesVehicle, "LOAN", "Auto Loan/Lease Coverage", "0", loanLeaseCov, "None", true, true);
			assertCoverageLimitLoan(coverageResponseV);

			coverageXproperties(softly, 4, coveragesVehicle, "RREIM", "Transportation Expense", transportationExpense.toPlaingString(), transportationExpense + " (Included)", "Per Occurrence", true, true);

			assertCoverageLimitTransportationExpense(coverageResponseV, false);

			coverageXproperties(softly, 5, coveragesVehicle, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLabor, "Per Disablement/Maximum", true, true);
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

		PolicyCoverageInfo coverageResponse = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			List<Coverage> coveragesVehicle2 = coverageResponse.vehicleLevelCoverages.get(0).coverages;
			coverageXproperties(softly, 0, coveragesVehicle2, "COMPDED", "Other Than Collision", comprehensiveDeductiblePendingV.toPlaingString(), comprehensiveDeductiblePendingV.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			coverageXproperties(softly, 1, coveragesVehicle2, "COLLDED", "Collision Deductible", collisionDeductiblePendingV.toPlaingString(), collisionDeductiblePendingV.toString(), "Deductible", true, true);
			assertCoverageLimitForCompCollLoanLease(coverageResponse);

			coverageXproperties(softly, 2, coveragesVehicle2, "GLASS", "Full Safety Glass", "false", fullSafetyGlassVehPendingV, "None", true, true);
			assertCoverageLimitFullGlassCov(coverageResponse);

			coverageXproperties(softly, 3, coveragesVehicle2, "LOAN", "Auto Loan/Lease Coverage", "0", loanLeaseCovPendingV, "None", true, true);
			assertCoverageLimitLoan(coverageResponse);

			coverageXproperties(softly, 4, coveragesVehicle2, "RREIM", "Transportation Expense", transportationExpensePendingV.toPlaingString(), transportationExpensePendingV + " (Included)", "Per Occurrence", true, true);
			assertCoverageLimitTransportationExpense(coverageResponse, false);

			coverageXproperties(softly, 5, coveragesVehicle2, "TOWINGLABOR", "Towing and Labor Coverage", "0/0", towingAndLaborPendingV, "Per Disablement/Maximum", true, true);
			assertCoverageLimitTowingLabor(coverageResponse);

			coverageXproperties(softly, 6, coveragesVehicle2, "SPECEQUIP", "Excess Electronic Equipment", excessElectronicEquipmentPendingV.toPlaingString(), "$1,000.00", null, true, false);
			coverageXproperties(softly, 7, coveragesVehicle2, "NEWCAR", "New Car Added Protection", "false", "No", null, false, false);

			//			softly.assertThat(coveragesVehicle2.get(8).getCoverageCd()).isEqualTo("WL"); //do not have this coverage in response anymore. Karen Yifru doesn't care about it.
			//			softly.assertThat(coveragesVehicle2.get(8).getCoverageDescription()).isEqualTo("Waive Liability");
			//			softly.assertThat(coveragesVehicle2.get(8).customerDisplayed).isEqualTo(false);
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

		PolicyCoverageInfo coverageEndorsementResponseV1 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, oid, PolicyCoverageInfo.class);
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

		PolicyCoverageInfo coverageEndorsementResponsePendingV2 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class);
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
		Vehicle addVehicle =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin1, purchaseDate), Vehicle.class, 201);
		assertThat(addVehicle.oid).isNotEmpty();
		String oid1 = addVehicle.oid;
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, oid1);

		PolicyCoverageInfo coverageResponse1 = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, oid1, PolicyCoverageInfo.class);

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
		Vehicle addVehicle =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin1, purchaseDate), Vehicle.class, 201);
		assertThat(addVehicle.oid).isNotEmpty();
		String oid1 = addVehicle.oid;
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, oid1);

		String coverageCd = "COMPDED";
		String availableLimits1 = "-1";
		String availableLimits2 = "100";

		//Remove COMPDED coverage and check Transportation Expense
		PolicyCoverageInfo updateCoverageResponse1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, availableLimits1), PolicyCoverageInfo.class);

		List<Coverage> coveragesVehicle = updateCoverageResponse1.vehicleLevelCoverages.get(0).coverages;
		//can not use coverageXproperties() because coverageLimitDisplay is expected to be null
		softly.assertThat(coveragesVehicle.get(4).getCoverageCd()).isEqualTo("RREIM");
		softly.assertThat(coveragesVehicle.get(4).getCoverageDescription()).isEqualTo("Transportation Expense");
		softly.assertThat(coveragesVehicle.get(4).getCoverageLimit()).isEqualTo("0");
		softly.assertThat(coveragesVehicle.get(4).getCoverageLimitDisplay()).isEqualTo(null);
		softly.assertThat(coveragesVehicle.get(4).getCoverageType()).isEqualTo("Per Occurrence");
		softly.assertThat(coveragesVehicle.get(4).getCustomerDisplayed()).isEqualTo(true);
		softly.assertThat(coveragesVehicle.get(4).getCanChangeCoverage()).isEqualTo(false);

		List<CoverageLimit> availableLimits = coveragesVehicle.get(4).getAvailableLimits();
		softly.assertThat(availableLimits.get(0).coverageLimit).isEqualTo("600");
		softly.assertThat(availableLimits.get(0).coverageLimitDisplay).isEqualTo("$600 (Included)");
		softly.assertThat(availableLimits.get(1).coverageLimit).isEqualTo("1200");
		softly.assertThat(availableLimits.get(1).coverageLimitDisplay).isEqualTo("$1,200");

		//Other Than Collision
		coverageXproperties(softly, 0, coveragesVehicle, "COMPDED", "Other Than Collision", "-1", "No Coverage", "Deductible", true, true);
		assertCoverageLimitForCompColl(updateCoverageResponse1);

		//Add COMPDED coverage again and check Transportation Expense
		PolicyCoverageInfo updateCoverageResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, availableLimits2), PolicyCoverageInfo.class);
		List<Coverage> coveragesVehicle2 = updateCoverageResponse2.vehicleLevelCoverages.get(0).coverages;
		coverageXproperties(softly, 4, coveragesVehicle2, "RREIM", "Transportation Expense", "600", "$600 (Included)", "Per Occurrence", true, true);

		List<CoverageLimit> availableLimitsNd = coveragesVehicle2.get(4).getAvailableLimits();
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
		Vehicle addVehicle =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin1, purchaseDate), Vehicle.class, 201);
		assertThat(addVehicle.oid).isNotEmpty();
		String oid1 = addVehicle.oid;
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, oid1);

		String coverageCd = "COMPDED";
		String availableLimits = "-1";
		String availableLimits2 = "100";

		//Remove COMPDED coverage and check Transportation Expense
		PolicyCoverageInfo updateCoverageResponse1 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, availableLimits), PolicyCoverageInfo.class);

		List<Coverage> coveragesVehicle = updateCoverageResponse1.vehicleLevelCoverages.get(0).coverages;
		//can not use coverageXproperties() because coverageLimitDisplay is expected to be null
		softly.assertThat(coveragesVehicle.get(4).getCoverageCd()).isEqualTo("RREIM");
		softly.assertThat(coveragesVehicle.get(4).getCoverageDescription()).isEqualTo("Transportation Expense");
		softly.assertThat(coveragesVehicle.get(4).getCoverageLimit()).isEqualTo("0");
		softly.assertThat(coveragesVehicle.get(4).getCoverageLimitDisplay()).isEqualTo(null);
		softly.assertThat(coveragesVehicle.get(4).getCoverageType()).isEqualTo("Per Occurrence");
		softly.assertThat(coveragesVehicle.get(4).getCustomerDisplayed()).isEqualTo(true);
		softly.assertThat(coveragesVehicle.get(4).getCanChangeCoverage()).isEqualTo(false);

		List<CoverageLimit> availableLimitsSt = coveragesVehicle.get(4).getAvailableLimits();
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
		PolicyCoverageInfo updateCoverageResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, availableLimits2), PolicyCoverageInfo.class);
		List<Coverage> coveragesVehicle2 = updateCoverageResponse2.vehicleLevelCoverages.get(0).coverages;

		List<CoverageLimit> availableLimitsNd = coveragesVehicle2.get(4).getAvailableLimits();
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

		PolicyCoverageInfo viewCoverageResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);

		assertSoftly(softly -> {
			Coverage filteredCoverageResponseMEDPM = viewCoverageResponse.policyCoverages.stream().filter(cov -> "MEDPM".equals(cov.getCoverageCd())).findFirst().orElse(null);
			Coverage filteredCoverageResponseIL = viewCoverageResponse.policyCoverages.stream().filter(cov -> "IL".equals(cov.getCoverageCd())).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseMEDPM.getCoverageLimit()).isEqualTo("2000");
			softly.assertThat(filteredCoverageResponseMEDPM.getAvailableLimits().get(0).coverageLimit).isEqualTo("0");
			softly.assertThat(filteredCoverageResponseMEDPM.getAvailableLimits().get(1).coverageLimit).isEqualTo("1000");
			softly.assertThat(filteredCoverageResponseMEDPM.getAvailableLimits().get(2).coverageLimit).isEqualTo("2000");
			softly.assertThat(filteredCoverageResponseMEDPM.getAvailableLimits().get(3).coverageLimit).isEqualTo("5000");
			softly.assertThat(filteredCoverageResponseMEDPM.getAvailableLimits().get(4).coverageLimit).isEqualTo("10000");
			softly.assertThat(filteredCoverageResponseMEDPM.getAvailableLimits().get(5).coverageLimit).isEqualTo("25000");

			softly.assertThat(filteredCoverageResponseIL.getCoverageLimit()).isEqualTo("100");
			softly.assertThat(filteredCoverageResponseIL.getAvailableLimits().get(0).coverageLimit).isEqualTo("0");
			softly.assertThat(filteredCoverageResponseIL.getAvailableLimits().get(1).coverageLimit).isEqualTo("100");
		});

		String coverageCd = "MEDPM";
		String newLimit = "10000";

		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, newLimit), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			Coverage filteredCoverageResponseMEPD = coverageResponse.policyCoverages.stream().filter(cov -> "MEDPM".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponseMEPD.getCoverageLimit()).isEqualTo(newLimit);

		});

		String coverageCd1 = "IL";
		String newLimit1 = "0";

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd1, newLimit1), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			Coverage filteredCoverageResponseIL = coverageResponse1.policyCoverages.stream().filter(cov -> "IL".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponseIL.getCoverageLimit()).isEqualTo(newLimit1);
		});

		PolicyCoverageInfo viewCoverageResponse1 = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			Coverage filteredCoverageResponseMEDPM = viewCoverageResponse1.policyCoverages.stream().filter(cov -> "MEDPM".equals(cov.getCoverageCd())).findFirst().orElse(null);
			Coverage filteredCoverageResponseIL = viewCoverageResponse1.policyCoverages.stream().filter(cov -> "IL".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponseMEDPM.getCoverageLimit()).isEqualTo(newLimit);
			softly.assertThat(filteredCoverageResponseIL.getCoverageLimit()).isEqualTo(newLimit1);
		});
	}

	protected void pas14730_UpdateCoverageUMPDAndPDBody(PolicyType policyType) {
		mainApp().open();

		String policyNumber = getCopiedPolicy();

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo viewCoverageResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);

		assertSoftly(softly -> {
			Coverage filteredCoverageResponseUMPD = viewCoverageResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.getCoverageCd())).findFirst().orElse(null);
			Coverage filteredCoverageResponsePD = viewCoverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.getCoverageCd())).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseUMPD.getCoverageLimit()).isEqualTo("50000");

			softly.assertThat(filteredCoverageResponsePD.getCoverageLimit()).isEqualTo("50000");
		});

		String coverageCd = "PD";
		String newLimit = "15000";

		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, newLimit), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			Coverage filteredCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.getCoverageCd())).findFirst().orElse(null);
			Coverage filteredCoverageResponseUMPD = coverageResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.getCoverageCd())).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseUMPD.getCoverageLimit()).isEqualTo(newLimit);
			assertAvailableCoverageLimitForPD(coverageResponse);

			softly.assertThat(filteredCoverageResponsePD.getCoverageLimit()).isEqualTo(newLimit);
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

		PolicyCoverageInfo viewCoverageResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		assertSoftly(softly -> {

			softly.assertThat(viewCoverageResponse.policyCoverages.get(0).getCoverageLimit()).isEqualTo("100000/300000");
			softly.assertThat(viewCoverageResponse.policyCoverages.get(2).getCoverageLimit()).isEqualTo("100000/300000");
		});

		String coverageCd = "BI";
		String newBILimits = "25000/50000";

		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, newBILimits), PolicyCoverageInfo.class);

		assertSoftly(softly -> {

			Coverage filteredCoverageResponseBI1 = coverageResponse.policyCoverages.stream().filter(cov -> "BI".equals(cov.getCoverageCd())).findFirst().orElse(null);
			Coverage filteredCoverageResponseUMUIM1 = coverageResponse.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.getCoverageCd())).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseBI1.getCoverageLimit()).isEqualTo("25000/50000");
			softly.assertThat(filteredCoverageResponseUMUIM1.getCoverageLimit()).isEqualTo("50000/50000");
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

		PolicyCoverageInfo viewCoverageResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			Coverage filteredCoverageResponseUmpd = viewCoverageResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.getCoverageCd())).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseUmpd.getCoverageType()).isEqualTo("Per Accident");
			softly.assertThat(filteredCoverageResponseUmpd.getCustomerDisplayed()).isEqualTo(true);
			softly.assertThat(filteredCoverageResponseUmpd.getCanChangeCoverage()).isEqualTo(false);
		});
		//To Do uncomment after PAS 15788 Done
	/*	String coverageCd1 = "PD";
		String newBILimits1 = "15000";

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updatePolicyLevelCoverageEndorsement(policyNumber, coverageCd1, newBILimits1);
		assertSoftly(softly -> {

			Coverage filteredCoverageResponseUmpd = coverageResponse1.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.getCoverageCd())).findFirst().orElse(null);
			Coverage filteredCoverageResponsePD = coverageResponse1.policyCoverages.stream().filter(cov -> "PD".equals(cov.getCoverageCd())).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseUmpd.getCoverageLimit()).isEqualTo("25000");
			softly.assertThat(filteredCoverageResponsePD.getCoverageLimit()).isEqualTo(newBILimits1);
		}); */

		String coverageCd = "PD";
		String newBILimits = "300000";

		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, newBILimits), PolicyCoverageInfo.class);

		assertSoftly(softly -> {

			Coverage filteredCoverageResponseUmpd = coverageResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.getCoverageCd())).findFirst().orElse(null);
			Coverage filteredCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.getCoverageCd())).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseUmpd.getCoverageLimit()).isEqualTo(newBILimits);
			softly.assertThat(filteredCoverageResponsePD.getCoverageLimit()).isEqualTo(newBILimits);
		});

	}

	protected void pas17628_pas17628_ViewCoverageUpdateCoverageUmpdDeductibleBody(PolicyType policyType) {
		mainApp().open();

		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo viewCoverageResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			Coverage umpdCoverage = viewCoverageResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(coverage -> "UMPDDED".equals(coverage.getCoverageCd()))
					.findFirst().orElse(null);
			coverageXproperties(softly, umpdCoverage, "UMPDDED", "Uninsured Motorist Property Damage Deductible", "250", "$250", null, true, true);

			softly.assertThat(viewCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getAvailableLimits().get(0).coverageLimit).isEqualTo("0");
			softly.assertThat(viewCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getAvailableLimits().get(1).coverageLimit).isEqualTo("250");
			softly.assertThat(viewCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getAvailableLimits().get(0).coverageLimitDisplay).isEqualTo("$0");
			softly.assertThat(viewCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getAvailableLimits().get(1).coverageLimitDisplay).isEqualTo("$250");

		});

		goToPasAndChangeUMPD(policyNumber);

		String coverageCd = "PD";
		String newBILimits = "300000";

		PolicyCoverageInfo coverageResponse1 = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, newBILimits), PolicyCoverageInfo.class);

		assertSoftly(softly -> {

			Coverage filteredCoverageResponseUmpd = coverageResponse1.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.getCoverageCd())).findFirst().orElse(null);
			Coverage filteredCoverageResponsePD = coverageResponse1.policyCoverages.stream().filter(cov -> "PD".equals(cov.getCoverageCd())).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseUmpd.getCoverageLimit()).isEqualTo(newBILimits);
			softly.assertThat(filteredCoverageResponsePD.getCoverageLimit()).isEqualTo(newBILimits);

			softly.assertThat(viewCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getCoverageDescription()).isEqualTo("Uninsured Motorist Property Damage Deductible");

			softly.assertThat(viewCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getCoverageCd()).isEqualTo("UMPDDED");
			softly.assertThat(viewCoverageResponse.vehicleLevelCoverages.get(0).coverages.get(0).getCoverageLimit()).isEqualTo("250");
		});

		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";
		Vehicle addVehicle =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin, purchaseDate), Vehicle.class, 201);
		assertThat(addVehicle.oid).isNotEmpty();
		String oid1 = addVehicle.oid;
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, oid1);

		String new_Umpdded = "0";
		String new_CoverageCd = "UMPDDED";

		PolicyCoverageInfo updateCoverageResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid1, DXPRequestFactory.createUpdateCoverageRequest(new_CoverageCd, new_Umpdded), PolicyCoverageInfo.class);
		assertSoftly(softly -> {
			softly.assertThat(updateCoverageResponse2.vehicleLevelCoverages.get(0).coverages.get(0).getCoverageCd()).isEqualTo("UMPDDED");
			softly.assertThat(updateCoverageResponse2.vehicleLevelCoverages.get(0).coverages.get(0).getCoverageLimit()).isEqualTo("0");
		});

		String coverageCd1 = "PD";
		String newBILimits1 = "50000";

		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd1, newBILimits1), PolicyCoverageInfo.class);

		assertSoftly(softly -> {

			List<Coverage> coverages1 = coverageResponse.vehicleLevelCoverages.get(0).coverages;
			List<Coverage> coverages2 = coverageResponse.vehicleLevelCoverages.get(1).coverages;

			Coverage filteredCoverageResponseUmpd = coverageResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.getCoverageCd())).findFirst().orElse(null);
			Coverage filteredCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.getCoverageCd())).findFirst().orElse(null);

			softly.assertThat(filteredCoverageResponseUmpd.getCoverageLimit()).isEqualTo(newBILimits1);
			softly.assertThat(filteredCoverageResponsePD.getCoverageLimit()).isEqualTo(newBILimits1);

			softly.assertThat(coverages1.get(0).getCoverageLimit()).isEqualTo("250");
			softly.assertThat(coverages2.get(0).getCoverageLimit()).isEqualTo("0");
		});
	}

	protected void pas11654_MDEnhancedUIMBICoverageBody(ETCSCoreSoftAssertions softly, PolicyType policyType) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class);

		Coverage coverageEUIM = policyCoverageResponse.policyCoverages.get(3);
		coverageXproperties(softly, coverageEUIM, "EUIM", "Enhanced UIM Selected", "false", "No", null, true, true);

		String coverageCd1 = "EUIM";
		String newBILimits1 = "true";
		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd1, newBILimits1), PolicyCoverageInfo.class);

		Coverage coverageEUIM1 = coverageResponse.policyCoverages.get(3);
		coverageXproperties(softly, coverageEUIM1, "EUIM", "Enhanced UIM Selected", "true", "Yes", null, true, true);

		String coverageCd2 = "EUIM";
		String newBILimits2 = "false";
		PolicyCoverageInfo coverageResponse1 = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd2, newBILimits2), PolicyCoverageInfo.class);

		Coverage coverageEUIM2 = coverageResponse1.policyCoverages.get(3);
		coverageXproperties(softly, coverageEUIM2, "EUIM", "Enhanced UIM Selected", "false", "No", null, true, true);
	}

	protected void pas20675_TortCoverageBody(ETCSCoreSoftAssertions softly, PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
		TestData td = getTestSpecificTD("TestData_All_Type_Driver").adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError);

		String policyNumber = createPolicy(td);

		//Perform Endorsement

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		ViewDriversResponse responseViewDriver = HelperCommon.viewPolicyDrivers(policyNumber);

		String oid1 = responseViewDriver.driverList.get(0).oid;
		String oid2 = responseViewDriver.driverList.get(1).oid;
		String oid3 = responseViewDriver.driverList.get(2).oid;

		PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		softly.assertThat(policyCoverageResponse.driverCoverages.get(1).getCoverageCd()).contains("TORT");
		softly.assertThat(policyCoverageResponse.driverCoverages.get(1).getAvailableDrivers()).contains(oid1);
		softly.assertThat(policyCoverageResponse.driverCoverages.get(1).getAvailableDrivers()).contains(oid2);
		softly.assertThat(policyCoverageResponse.driverCoverages.get(1).getAvailableDrivers()).contains(oid3);
		softly.assertThat(policyCoverageResponse.driverCoverages.get(1).getCurrentlyAddedDrivers()).isEmpty();

		PolicyCoverageInfo updateCoverageResponse = updateTORTCoverage(policyNumber, ImmutableList.of(oid1, oid2, oid3));
		softly.assertThat(updateCoverageResponse.driverCoverages.get(1).getCurrentlyAddedDrivers()).contains(oid1);
		softly.assertThat(updateCoverageResponse.driverCoverages.get(1).getCurrentlyAddedDrivers()).contains(oid2);
		softly.assertThat(updateCoverageResponse.driverCoverages.get(1).getCurrentlyAddedDrivers()).contains(oid3);

		PolicyCoverageInfo updateCoverageResponse1 = updateTORTCoverage(policyNumber, ImmutableList.of(oid1));
		softly.assertThat(updateCoverageResponse1.driverCoverages.get(1).getCurrentlyAddedDrivers()).containsExactly(oid1);
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
		PolicyCoverageInfo policyCoverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class);
		assertThatOnlyOneInstanceOfPolicyLevelCoverages(policyCoverageResponse);

		//validate that viewPolicyCoverages response contains only one instance of Policy level coverages (For endorsement)
		PolicyCoverageInfo endorsementCoverageResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		assertThatOnlyOneInstanceOfPolicyLevelCoverages(endorsementCoverageResponse);

		// view vehicle to get OID
		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		assertThat(viewVehicleResponse.vehicleList.get(1).vehTypeCd.contains("Trailer")).isTrue();
		String oidTrailer = viewVehicleResponse.vehicleList.get(1).oid;

		//validate that viewPolicyCoveragesByVehicle response for Trailer contains all vehicle level coverages AND only comp and coll have CanChangeCoverage = true AND CustomerDisplay = true (For policy)
		PolicyCoverageInfo viewPolicyCoveragesByVehicleResponse = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, oidTrailer, PolicyCoverageInfo.class);
		validateTrailerCoverages(viewPolicyCoveragesByVehicleResponse);
		assertThatOnlyOneInstanceOfPolicyLevelCoverages(viewPolicyCoveragesByVehicleResponse);

		//validate that viewEndorsementCoveragesByVehicle response for Trailer contains all vehicle level coverages AND only comp and coll have CanChangeCoverage = true AND CustomerDisplay = true (For endorsement)
		PolicyCoverageInfo viewEndorsementCoveragesByVehicleResponse = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, oidTrailer, PolicyCoverageInfo.class);
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

			PolicyCoverageInfo viewCoverageResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
			Coverage coverageBI = viewCoverageResponse.policyCoverages.stream().filter(coverage -> "BI".equals(coverage.getCoverageCd())).findFirst().orElse(null);
			List<CoverageLimit> coverageLimitsBI = coverageBI.getAvailableLimits();
			Collections.reverse(coverageLimitsBI); //reverse, so that highest available limit is first element in the list

			//Update BI to the highest one so that all PD limits are available
			UpdateCoverageRequest updateCoverageRequest = DXPRequestFactory.createUpdateCoverageRequest("BI", coverageLimitsBI.get(0).coverageLimit);
			HelperCommon.updateEndorsementCoverage(policyNumber, updateCoverageRequest, PolicyCoverageInfo.class);

			//Get all PD limits
			viewCoverageResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
			Coverage coveragePD = viewCoverageResponse.policyCoverages.stream().filter(coverage -> "PD".equals(coverage.getCoverageCd())).findFirst().orElse(null);
			List<CoverageLimit> coverageLimitsPD = coveragePD.getAvailableLimits();
			Collections.reverse(coverageLimitsPD);//reverse, so that highest available limit is first element in the list

			//update PD to highest one, so that it doesn't give error
			updateCoverageRequest = DXPRequestFactory.createUpdateCoverageRequest("PD", coverageLimitsPD.get(0).coverageLimit);
			HelperCommon.updateEndorsementCoverage(policyNumber, updateCoverageRequest, PolicyCoverageInfo.class);

			//validate with BI limits
			validatePUPError_pas15379(softly, policyNumber, coverageLimitsBI, "BI");

			//Update BI to the highest one so that all PD values are available
			updateCoverageRequest = DXPRequestFactory.createUpdateCoverageRequest("BI", coverageLimitsBI.get(0).coverageLimit);
			HelperCommon.updateEndorsementCoverage(policyNumber, updateCoverageRequest, PolicyCoverageInfo.class);

			//validate with PD limits
			validatePUPError_pas15379(softly, policyNumber, coverageLimitsPD, "PD");

			/*Update BI to value with error expected and bind. For OH not setting to the lowest BI and PD limits,
			 because otherwise on rate there is error "Limits selected are lower than state minimums. Referral will be denied." */
			if ("OH".contains(getState())) {
				updateCoverageRequest = DXPRequestFactory.createUpdateCoverageRequest("BI", "25000/50000");
				HelperCommon.updateEndorsementCoverage(policyNumber, updateCoverageRequest, PolicyCoverageInfo.class);
				updateCoverageRequest = DXPRequestFactory.createUpdateCoverageRequest("PD", "25000");
				HelperCommon.updateEndorsementCoverage(policyNumber, updateCoverageRequest, PolicyCoverageInfo.class);

			} else {
				Collections.reverse(coverageLimitsBI); //reverse, so that lowest available limit is first element in the list
				updateCoverageRequest = DXPRequestFactory.createUpdateCoverageRequest("BI", coverageLimitsBI.get(0).coverageLimit);
				HelperCommon.updateEndorsementCoverage(policyNumber, updateCoverageRequest, PolicyCoverageInfo.class);
			}

			/*Not trying to rate and bind with NY and IN, because there is error on rate not related with this US - for IN- "UMPD limit may not exceed PD limit",
			for NY - "UM/SUM limits may not exceed BI limits". Currently not possible to update them through DXP.
			 */
			if (!"NY, IN".contains(getState())) {
				helperMiniServices.endorsementRateAndBind(policyNumber);
			}
		});
	}

	protected void pas15255_UpdateCompCollCoveragesCheckUmpdBody(String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();

		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		String oid = viewVehicleResponse.vehicleList.get(0).oid;

		String coverageCdComp = "COMPDED";
		String coverageCdColl = "COLLDED";
		String limitDisplayNoCov = "No Coverage";
		String availableLimitsChange = "-1";
		String availableLimitsChange2 = "250";

		//TC1
		//Remove COMP/COLL and check UMBI
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		updateCoverageAndCheckUmbi_pas15255(policyNumber, coverageCdComp, availableLimitsChange, oid, true, true, limitDisplayNoCov, state);
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		updateCoverageAndCheckUmbi_pas15255(policyNumber, coverageCdColl, availableLimitsChange, oid, true, true, limitDisplayNoCov, state);

		//TC3
		//Prepare policy, COLL = No coverage
		helperMiniServices.endorsementRateAndBind(policyNumber); //Policy without Coll
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		//Add Coll back
		updateCoverageAndCheckUmbi_pas15255(policyNumber, coverageCdColl, availableLimitsChange2, oid, false, false, limitDisplayNoCov, state);
		helperMiniServices.endorsementRateAndBind(policyNumber);

		//Prepare policy, COMP = No coverage
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdComp, availableLimitsChange), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		helperMiniServices.endorsementRateAndBind(policyNumber);//Policy without Comp/Coll
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		updateCoverageAndCheckUmbi_pas15255(policyNumber, coverageCdComp, availableLimitsChange2, oid, true, true, limitDisplayNoCov, state);
		HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, oid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdColl, availableLimitsChange2), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		helperMiniServices.endorsementRateAndBind(policyNumber);

		//TC2
		//Prepare policy UM/UIM = no coverage
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.setPolicyCoverageDetailsValue(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel(), "No Coverage");
		premiumAndCoveragesTab.saveAndExit();
		helperMiniServices.endorsementRateAndBind(policyNumber); //Policy without UM/UIM

		//Remove COMP/COLL and check UMBI
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		updateCoverageAndCheckUmbi_pas15255(policyNumber, coverageCdComp, availableLimitsChange, oid, false, false, limitDisplayNoCov, state);
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		updateCoverageAndCheckUmbi_pas15255(policyNumber, coverageCdColl, availableLimitsChange, oid, false, false, limitDisplayNoCov, state);
	}

	private void updateCoverageAndCheckUmbi_pas15255(String policyNumber, String coverageCdChange, String availableLimitsChange, String vehicleOid, boolean customerDisplayed, boolean canChangeCoverage, String coverageLimitDisplay, String state) {

		PolicyCoverageInfo updateCoverageResponse = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, vehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChange, availableLimitsChange), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

		if (state.equals(Constants.States.CO)) {
			Coverage filteredCoverageResponseUmbi = findCoverage(updateCoverageResponse.vehicleLevelCoverages.get(0).coverages, "UMPDDED");
			assertSoftly(softly -> {
				softly.assertThat(filteredCoverageResponseUmbi.getCustomerDisplayed()).isEqualTo(customerDisplayed);
				softly.assertThat(filteredCoverageResponseUmbi.getCanChangeCoverage()).isEqualTo(canChangeCoverage);
				softly.assertThat(filteredCoverageResponseUmbi.getCoverageLimitDisplay()).isEqualTo(coverageLimitDisplay);
			});
		} else {
			Coverage filteredCoverageResponseUmbi = findCoverage(updateCoverageResponse.vehicleLevelCoverages.get(0).coverages, "UMPD");
			assertSoftly(softly -> {
				softly.assertThat(filteredCoverageResponseUmbi.getCustomerDisplayed()).isEqualTo(customerDisplayed);
				softly.assertThat(filteredCoverageResponseUmbi.getCanChangeCoverage()).isEqualTo(canChangeCoverage);
				softly.assertThat(filteredCoverageResponseUmbi.getCoverageLimitDisplay()).isEqualTo(coverageLimitDisplay);
			});
		}
	}

	protected void pas19627_UMAndUimCoverageBody(PolicyType policyType) {
		assertSoftly(softly -> {

			mainApp().open();
			String policyNumber = getCopiedPolicy();

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			verifyUMUIMIsSeparateCoverage(softly, policyNumber);

			String coverageCd = "BI";
			String newBILimits = "25000/50000";

			verifyBIUmbiUimbiForKy(softly, policyNumber, coverageCd, newBILimits);

			String coverageCd1 = "BI";
			String newBILimits1 = "100000/300000";

			verifyBIUmbiUimbiForKy(softly, policyNumber, coverageCd1, newBILimits1);

			String coverageCd2 = "BI";
			String newBILimits2 = "1000000/1000000";

			verifyBIUmbiUimbiForKy(softly, policyNumber, coverageCd2, newBILimits2);
		});
	}

	private void verifyBIUmbiUimbiForKy(ETCSCoreSoftAssertions softly, String policyNumber, String coverageCd, String newBILimits) {
		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, newBILimits), PolicyCoverageInfo.class);
		Coverage filteredCoverageResponseBI = coverageResponse.policyCoverages.stream().filter(cov -> "BI".equals(cov.getCoverageCd())).findFirst().orElse(null);
		softly.assertThat(filteredCoverageResponseBI.getCoverageLimit().equals(newBILimits)).isEqualTo(true);

		Coverage filteredCoverageResponseUMBI = coverageResponse.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.getCoverageCd())).findFirst().orElse(null);
		softly.assertThat(filteredCoverageResponseUMBI.getCoverageLimit().equals(newBILimits)).isEqualTo(true);
		if (newBILimits.equals("25000/50000")) {
			softly.assertThat(filteredCoverageResponseUMBI.getAvailableLimits().get(0).coverageLimit).isEqualTo("25000/50000");
			softly.assertThat(filteredCoverageResponseUMBI.getAvailableLimits().get(1).coverageLimit).isEqualTo("0/0");
		} else if (newBILimits.equals("100000/300000")) {
			verifyAvailableLimitsForUMUIM(softly, filteredCoverageResponseUMBI);
		} else if (newBILimits.equals("1000000/1000000")) {
			verifyAvailableLimitsForUMUIM1(softly, filteredCoverageResponseUMBI);
		}

		Coverage filteredCoverageResponseUIMBI = coverageResponse.policyCoverages.stream().filter(cov -> "UIMBI".equals(cov.getCoverageCd())).findFirst().orElse(null);
		softly.assertThat(filteredCoverageResponseUIMBI.getCoverageLimit().equals(newBILimits)).isEqualTo(true);
		if (newBILimits.equals("25000/50000")) {
			softly.assertThat(filteredCoverageResponseUMBI.getAvailableLimits().get(0).coverageLimit).isEqualTo("25000/50000");
			softly.assertThat(filteredCoverageResponseUMBI.getAvailableLimits().get(1).coverageLimit).isEqualTo("0/0");
		} else if (newBILimits.equals("100000/300000")) {
			verifyAvailableLimitsForUMUIM(softly, filteredCoverageResponseUIMBI);
		} else if (newBILimits.equals("1000000/1000000")) {
			verifyAvailableLimitsForUMUIM1(softly, filteredCoverageResponseUIMBI);
		}
	}

	private void verifyUMUIMIsSeparateCoverage(ETCSCoreSoftAssertions softly, String policyNumber) {
		PolicyCoverageInfo viewCoverageResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);

		Coverage coverageUIM = viewCoverageResponse.policyCoverages.get(2);
		coverageXproperties(softly, coverageUIM, "UMBI", "Uninsured Motorists Bodily Injury", "100000/300000", "$100,000/$300,000", "Per Person/Per Accident", true, false);

		Coverage coverageUIMBI = viewCoverageResponse.policyCoverages.get(3);
		coverageXproperties(softly, coverageUIMBI, "UIMBI", "Underinsured Motorists Bodily Injury", "100000/300000", "$100,000/$300,000", "Per Person/Per Accident", true, false);
	}

	protected void pas19626_biAndUMAndUIMCoverageSDBody(PolicyType policyType) {
		assertSoftly(softly -> {

			mainApp().open();
			String policyNumber = getCopiedPolicy();

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			verifyUMUIMIsSeparateCoverage(softly, policyNumber);

			String coverageCd = "BI";
			String newBILimits = "25000/50000";

			verifyUMUIMAfterBIChange(softly, policyNumber, coverageCd, newBILimits, false);

			String coverageCd1 = "BI";
			String newBILimits1 = "100000/300000";

			verifyUMUIMAfterBIChange(softly, policyNumber, coverageCd1, newBILimits1, false);

			String coverageCd2 = "BI";
			String newBILimits2 = "1000000/1000000";

			verifyUMUIMAfterBIChange(softly, policyNumber, coverageCd2, newBILimits2, true);
		});
	}

	private void verifyUMUIMAfterBIChange(ETCSCoreSoftAssertions softly, String policyNumber, String coverageCd, String newBILimits, boolean availableLimitCheck) {
		PolicyCoverageInfo coverageResponse = HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(coverageCd, newBILimits), PolicyCoverageInfo.class);
		Coverage filteredCoverageResponseBI = coverageResponse.policyCoverages.stream().filter(cov -> "BI".equals(cov.getCoverageCd())).findFirst().orElse(null);
		softly.assertThat(filteredCoverageResponseBI.getCoverageLimit().equals(newBILimits)).isEqualTo(true);

		Coverage filteredCoverageResponseUMBI = coverageResponse.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.getCoverageCd())).findFirst().orElse(null);
		if (availableLimitCheck) {
			softly.assertThat(filteredCoverageResponseUMBI.getCoverageLimit().equals(newBILimits)).isEqualTo(true);
			softly.assertThat(filteredCoverageResponseUMBI.getCanChangeCoverage().equals(true));
			AvailableLimitsForUMUIMSD(softly, filteredCoverageResponseUMBI);
		} else {
			softly.assertThat(filteredCoverageResponseUMBI.getCoverageLimit().equals(newBILimits)).isEqualTo(true);
			softly.assertThat(filteredCoverageResponseUMBI.getCanChangeCoverage().equals(false));
			softly.assertThat(filteredCoverageResponseUMBI.getAvailableLimits().get(0).coverageLimit).isEqualTo(newBILimits);
		}

		Coverage filteredCoverageResponseUIMBI = coverageResponse.policyCoverages.stream().filter(cov -> "UIMBI".equals(cov.getCoverageCd())).findFirst().orElse(null);

		if (availableLimitCheck) {
			softly.assertThat(filteredCoverageResponseUIMBI.getCoverageLimit().equals(newBILimits)).isEqualTo(true);
			softly.assertThat(filteredCoverageResponseUIMBI.getCanChangeCoverage().equals(true));
			AvailableLimitsForUMUIMSD(softly, filteredCoverageResponseUIMBI);
		} else {
			softly.assertThat(filteredCoverageResponseUIMBI.getCoverageLimit().equals(newBILimits)).isEqualTo(true);
			softly.assertThat(filteredCoverageResponseUIMBI.getCanChangeCoverage().equals(false));
			softly.assertThat(filteredCoverageResponseUIMBI.getAvailableLimits().get(0).coverageLimit).isEqualTo(newBILimits);
		}
	}

	private void AvailableLimitsForUMUIMSD(ETCSCoreSoftAssertions softly, Coverage filteredCoverageResponseUMBI2) {
		softly.assertThat(filteredCoverageResponseUMBI2.getAvailableLimits().get(0).coverageLimit).isEqualTo("100000/300000");
		softly.assertThat(filteredCoverageResponseUMBI2.getAvailableLimits().get(1).coverageLimit).isEqualTo("250000/500000");
		softly.assertThat(filteredCoverageResponseUMBI2.getAvailableLimits().get(2).coverageLimit).isEqualTo("300000/500000");
		softly.assertThat(filteredCoverageResponseUMBI2.getAvailableLimits().get(3).coverageLimit).isEqualTo("500000/500000");
		softly.assertThat(filteredCoverageResponseUMBI2.getAvailableLimits().get(4).coverageLimit).isEqualTo("500000/1000000");
		softly.assertThat(filteredCoverageResponseUMBI2.getAvailableLimits().get(5).coverageLimit).isEqualTo("1000000/1000000");
	}

	private void verifyAvailableLimitsForUMUIM(ETCSCoreSoftAssertions softly, Coverage filteredCoverageResponseUMBI1) {
		softly.assertThat(filteredCoverageResponseUMBI1.getAvailableLimits().get(0).coverageLimit).isEqualTo("25000/50000");
		softly.assertThat(filteredCoverageResponseUMBI1.getAvailableLimits().get(1).coverageLimit).isEqualTo("50000/100000");
		softly.assertThat(filteredCoverageResponseUMBI1.getAvailableLimits().get(2).coverageLimit).isEqualTo("100000/300000");
		softly.assertThat(filteredCoverageResponseUMBI1.getAvailableLimits().get(3).coverageLimit).isEqualTo("0/0");
	}

	private void verifyAvailableLimitsForUMUIM1(ETCSCoreSoftAssertions softly, Coverage filteredCoverageResponseUMBI1) {
		softly.assertThat(filteredCoverageResponseUMBI1.getAvailableLimits().get(0).coverageLimit).isEqualTo("25000/50000");
		softly.assertThat(filteredCoverageResponseUMBI1.getAvailableLimits().get(1).coverageLimit).isEqualTo("50000/100000");
		softly.assertThat(filteredCoverageResponseUMBI1.getAvailableLimits().get(2).coverageLimit).isEqualTo("100000/300000");
		softly.assertThat(filteredCoverageResponseUMBI1.getAvailableLimits().get(3).coverageLimit).isEqualTo("250000/500000");
		softly.assertThat(filteredCoverageResponseUMBI1.getAvailableLimits().get(4).coverageLimit).isEqualTo("300000/500000");
		softly.assertThat(filteredCoverageResponseUMBI1.getAvailableLimits().get(5).coverageLimit).isEqualTo("500000/500000");
		softly.assertThat(filteredCoverageResponseUMBI1.getAvailableLimits().get(6).coverageLimit).isEqualTo("500000/1000000");
		softly.assertThat(filteredCoverageResponseUMBI1.getAvailableLimits().get(7).coverageLimit).isEqualTo("1000000/1000000");
		softly.assertThat(filteredCoverageResponseUMBI1.getAvailableLimits().get(8).coverageLimit).isEqualTo("0/0");
	}

	protected void pas17642_UpdateCoverageADBBody() {
		assertSoftly(softly -> {
			TestData td = getPolicyTD("DataGather", "TestData");
			TestData drivers = getTestSpecificTD("FourDrivers");
			td.adjust(new DriverTab().getMetaKey(), drivers.getTestDataList("DriverTab")).resolveLinks();
			td.adjust(new DocumentsAndBindTab().getMetaKey(), getTestSpecificTD("DocumentsAndBindTab")).resolveLinks();
			mainApp().open();
			createCustomerIndividual();
			createPolicy(td);
			String policyNumber = PolicySummaryPage.getPolicyNumber();

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			ViewDriversResponse viewDriversResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			String driverFNI = viewDriversResponse.driverList.get(0).oid;
			String driverAFR = viewDriversResponse.driverList.get(1).oid;
			String driver3 = viewDriversResponse.driverList.get(2).oid;
			String driver4 = viewDriversResponse.driverList.get(3).oid;
			softly.assertThat(viewDriversResponse.driverList.get(0).availableCoverages.toString()).contains("ADB");
			softly.assertThat(viewDriversResponse.driverList.get(1).availableCoverages.toString()).contains("ADB");
			softly.assertThat(viewDriversResponse.driverList.get(2).availableCoverages.toString()).doesNotContain("ADB");
			softly.assertThat(viewDriversResponse.driverList.get(3).availableCoverages.toString()).doesNotContain("ADB");

			//Validate view coverages that 0 drivers have ADB added
			Coverage adbCoverageToMatch = Coverage.create(CoverageInfo.ADB)
					.addAvailableDrivers(driverFNI, driverAFR)
					//initialize empty currentlyAddedDrivers list
					.addCurrentlyAddedDrivers();
			String grossPremiumOnOriginal = validateDriverCoverage(softly, adbCoverageToMatch, policyNumber, false);

			//Add ADB to FNI driver
			adbCoverageToMatch.addCurrentlyAddedDrivers(driverFNI);
			String grossPremiumAfterUpdate1 = validateDriverCoverage(softly, adbCoverageToMatch, policyNumber, true);
			assertThat(new Dollar(grossPremiumAfterUpdate1).moreThan(new Dollar(grossPremiumOnOriginal))).isTrue();

			//Add ADB to AFR driver
			adbCoverageToMatch.addCurrentlyAddedDrivers(driverAFR);
			String grossPremiumAfterUpdate2 = validateDriverCoverage(softly, adbCoverageToMatch, policyNumber, true);
			assertThat(new Dollar(grossPremiumAfterUpdate2).moreThan(new Dollar(grossPremiumAfterUpdate1))).isTrue();

			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.dataGather().start();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			softly.assertThat(PremiumAndCoveragesTab.tableFormsSummary.getRowContains("Forms", "ADB").isPresent()).isTrue();
			premiumAndCoveragesTab.saveAndExit();
			helperMiniServices.endorsementRateAndBind(policyNumber);
		});
	}

	@SuppressWarnings("unchecked")
	private String validateDriverCoverage(ETCSCoreSoftAssertions softly, Coverage driverCoverageToMatch, String policyNumber, boolean performUpdate) {
		PolicyCoverageInfo policyCoverageInfo;
		if (performUpdate) {
			UpdateCoverageRequest updateCoverageRequest = DXPRequestFactory.createUpdateCoverageRequest(driverCoverageToMatch.getCoverageCd(), "true", driverCoverageToMatch.getCurrentlyAddedDrivers());
			policyCoverageInfo = HelperCommon.updateEndorsementCoverage(policyNumber, updateCoverageRequest, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		} else {
			policyCoverageInfo = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		}
		//validate ADB coverage from response
		softly.assertThat(findCoverage(policyCoverageInfo.driverCoverages, driverCoverageToMatch.getCoverageCd())).isEqualToIgnoringGivenFields(driverCoverageToMatch, "availableLimits");
		if (performUpdate) {
			//Validate transaction history
			ComparablePolicy comparablePolicy = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
			ComparableObject<Coverage> modifiedDriverCoverage = comparablePolicy.driverCoverages.get(driverCoverageToMatch.getCoverageCd());
			softly.assertThat(modifiedDriverCoverage.data).isEqualToIgnoringGivenFields(driverCoverageToMatch, "availableDrivers", "availableLimits");
			for (String driverOid : driverCoverageToMatch.getCurrentlyAddedDrivers()) {
				softly.assertThat((List<String>) modifiedDriverCoverage.modifiedAttributes.get("currentlyAddedDrivers").newValue).contains(driverOid);
			}
		}
		return HelperCommon.endorsementRate(policyNumber, Response.Status.OK.getStatusCode())[0].termPremium;
	}

	protected void pas20835_mdAndEnhancedCoverageBody(PolicyType policyType) {
		assertSoftly(softly -> {
			mainApp().open();
			String policyNumber = getCopiedPolicy();

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			Coverage covBI = Coverage.create(CoverageInfo.BI);
			Coverage covUMBI = Coverage.create(CoverageInfo.UMBI_MD).disableCanChange();
			Coverage covUMPD = Coverage.create(CoverageInfo.UMPD_MD).disableCanChange();
			Coverage covEUIM = Coverage.create(CoverageInfo.EUIM_MD).disableCanChange();
			PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);

			softly.assertThat(findPolicyCoverage(viewEndorsementCoverages, covBI.getCoverageCd())).isEqualToComparingFieldByField(covBI);
			softly.assertThat(findPolicyCoverage(viewEndorsementCoverages, covUMBI.getCoverageCd())).isEqualToComparingFieldByField(covUMBI);
			softly.assertThat(findPolicyCoverage(viewEndorsementCoverages, covUMPD.getCoverageCd())).isEqualToComparingFieldByField(covUMPD);
			softly.assertThat(findPolicyCoverage(viewEndorsementCoverages, covEUIM.getCoverageCd())).isEqualToComparingFieldByField(covEUIM);

			//update coverages
			covBI = covBI.changeLimit(CoverageLimits.COV_250500);
			covUMPD = covUMPD.changeLimit(CoverageLimits.COV_50000);
			covUMBI = covUMBI.changeLimit(CoverageLimits.COV_250500);

			PolicyCoverageInfo coverageResponse = updateCoverage(policyNumber, covBI);

			assertThat(findPolicyCoverage(coverageResponse, covBI.getCoverageCd())).isEqualToComparingFieldByField(covBI);
			assertThat(findPolicyCoverage(coverageResponse, covUMPD.getCoverageCd())).isEqualToComparingFieldByField(covUMPD);
			assertThat(findPolicyCoverage(coverageResponse, covUMBI.getCoverageCd())).isEqualToComparingFieldByField(covUMBI);

			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.dataGather().start();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			enhancedUIM.setValue(true);
			premiumAndCoveragesTab.calculatePremium();
			premiumAndCoveragesTab.saveAndExit();

			PolicyCoverageInfo viewEndorsementCoverages1 = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
			Coverage covEUIM1 = Coverage.create(CoverageInfo.EUIM_MD_TRUE).disableCanChange();
			Coverage enhancedUMBI = Coverage.create(CoverageInfo.UMBI_MD_ENHANCED_UIM_TRUE).disableCanChange().changeLimit(CoverageLimits.COV_250500);
			Coverage enhancedUMPD = Coverage.create(CoverageInfo.UMPD_MD_ENHANCED_UIM_TRUE).disableCanChange().changeLimit(CoverageLimits.COV_50000);
			assertThat(findPolicyCoverage(viewEndorsementCoverages1, enhancedUMBI.getCoverageCd())).isEqualToComparingFieldByField(enhancedUMBI);
			assertThat(findPolicyCoverage(viewEndorsementCoverages1, enhancedUMPD.getCoverageCd())).isEqualToComparingFieldByField(enhancedUMPD);
			assertThat(findPolicyCoverage(viewEndorsementCoverages1, covEUIM1.getCoverageCd())).isEqualToComparingFieldByField(covEUIM1);
			//update coverages
			covBI = covBI.changeLimit(CoverageLimits.COV_100300);
			enhancedUMBI = enhancedUMBI.changeLimit(CoverageLimits.COV_100300);
			enhancedUMPD = enhancedUMPD.changeLimit(CoverageLimits.COV_50000);

			PolicyCoverageInfo coverageResponse2 = updateCoverage(policyNumber, covBI);

			softly.assertThat(findPolicyCoverage(coverageResponse2, covBI.getCoverageCd())).isEqualToComparingFieldByField(covBI);
			softly.assertThat(findPolicyCoverage(coverageResponse2, enhancedUMPD.getCoverageCd())).isEqualToComparingFieldByField(enhancedUMPD);
			softly.assertThat(findPolicyCoverage(coverageResponse2, enhancedUMBI.getCoverageCd())).isEqualToComparingFieldByField(enhancedUMBI);

			helperMiniServices.endorsementRateAndBind(policyNumber);
		});
	}

	protected void pas20292_updateCoverageBIPDWvBody(PolicyType policyType) {
		assertSoftly(softly -> {
			mainApp().open();
			String policyNumber = getCopiedPolicy();

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			Coverage covBI = Coverage.create(CoverageInfo.BIWV).changeLimit(CoverageLimits.COV_250500);
			Coverage covUIMBI = Coverage.create(CoverageInfo.UIMBI).disableCanChange().changeLimit((CoverageLimits.COV_250500));
			Coverage covUMBI = Coverage.create(CoverageInfo.UMBI).disableCanChange().changeLimit((CoverageLimits.COV_250500));
			Coverage covPD = Coverage.create(CoverageInfo.PDWV);
			Coverage covUMPD = Coverage.create(CoverageInfo.UMPD_WV).disableCanChange();
			Coverage covUIMPD = Coverage.create(CoverageInfo.UIMPD).disableCanChange();

			PolicyCoverageInfo coverageResponse = updateCoverage(policyNumber, covBI);
			assertThat(findPolicyCoverage(coverageResponse, covBI.getCoverageCd())).isEqualToComparingFieldByField(covBI);
			assertThat(findPolicyCoverage(coverageResponse, covUIMBI.getCoverageCd())).isEqualToComparingFieldByField(covUIMBI);
			assertThat(findPolicyCoverage(coverageResponse, covUMBI.getCoverageCd())).isEqualToComparingFieldByField(covUMBI);
			assertThat(findPolicyCoverage(coverageResponse, covUMPD.getCoverageCd())).isEqualToComparingFieldByField(covUMPD);
			assertThat(findPolicyCoverage(coverageResponse, covUIMPD.getCoverageCd())).isEqualToComparingFieldByField(covUIMPD);

			//AC2 update PD
			Coverage covPDChange = Coverage.create(CoverageInfo.PDWV).changeLimit(CoverageLimits.COV_100000);
			Coverage covUmpdChange = Coverage.create(CoverageInfo.UMPD_WV).changeLimit(CoverageLimits.COV_100000).disableCanChange();
			Coverage covUimpdChange = Coverage.create(CoverageInfo.UIMPD).changeLimit(CoverageLimits.COV_100000).disableCanChange();
			PolicyCoverageInfo coverageResponsePdUpdate = updateCoverage(policyNumber, covPDChange);
			assertThat(findPolicyCoverage(coverageResponsePdUpdate, covPD.getCoverageCd())).isEqualToComparingFieldByField(covPDChange);
			assertThat(findPolicyCoverage(coverageResponsePdUpdate, covUmpdChange.getCoverageCd())).isEqualToComparingFieldByField(covUmpdChange);
			assertThat(findPolicyCoverage(coverageResponsePdUpdate, covUimpdChange.getCoverageCd())).isEqualToComparingFieldByField(covUimpdChange);
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			covBI = covBI.changeLimit(CoverageLimits.COV_2550);
			covUIMBI = covUIMBI.changeLimit(CoverageLimits.COV_2550);
			covUMBI = covUMBI.changeLimit(CoverageLimits.COV_2550);
			covUIMPD = covUIMPD.changeLimit(CoverageLimits.COV_50000);
			covUMPD = covUMPD.changeLimit(CoverageLimits.COV_50000);

			Coverage covPDChange1 = Coverage.create(CoverageInfo.PDWV)
					.changeLimit(CoverageLimits.COV_50000)
					.removeAvailableLimit(CoverageLimits.COV_100000, CoverageLimits.COV_300000, CoverageLimits.COV_500000);

			PolicyCoverageInfo coverageResponseUpdateBI = updateCoverage(policyNumber, covBI);
			assertThat(findPolicyCoverage(coverageResponseUpdateBI, covBI.getCoverageCd())).isEqualToComparingFieldByField(covBI);
			assertThat(findPolicyCoverage(coverageResponseUpdateBI, covUIMBI.getCoverageCd())).isEqualToComparingFieldByField(covUIMBI);
			assertThat(findPolicyCoverage(coverageResponseUpdateBI, covUMBI.getCoverageCd())).isEqualToComparingFieldByField(covUMBI);
			assertThat(findPolicyCoverage(coverageResponseUpdateBI, covPDChange1.getCoverageCd())).isEqualToComparingFieldByField(covPDChange1);
			assertThat(findPolicyCoverage(coverageResponseUpdateBI, covUIMPD.getCoverageCd())).isEqualToComparingFieldByField(covUIMPD);
			assertThat(findPolicyCoverage(coverageResponseUpdateBI, covUMPD.getCoverageCd())).isEqualToComparingFieldByField(covUMPD);
			helperMiniServices.endorsementRateAndBind(policyNumber);
		});
	}

	protected void pas22037_updateBiCoverageBody(){
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		helperMiniServices.createEndorsementWithCheck(policyNumber);
		verifyUMUIMAfterBIChange(policyNumber, "25000/50000", "50000/100000", "50000/100000", false, ImmutableList.of("50000/100000"));
		verifyUMUIMAfterBIChange(policyNumber, "50000/100000", "25000/50000", "25000/50000", false, ImmutableList.of("25000/50000"));
		verifyUMUIMAfterBIChange(policyNumber, "100000/300000", "25000/50000", "25000/50000", false, ImmutableList.of("25000/50000"));
		verifyUMUIMAfterBIChange(policyNumber, "100000/300000", "300000/500000", "100000/300000", true, ImmutableList.of("100000/300000", "250000/500000", "300000/500000"));
		verifyUMUIMAfterBIChange(policyNumber, "300000/500000", "250000/500000", "250000/500000", true, ImmutableList.of("100000/300000", "250000/500000"));
		verifyUMUIMAfterBIChange(policyNumber, "300000/500000", "1000000/1000000", "300000/500000", true, ImmutableList.of("100000/300000", "250000/500000", "300000/500000", "500000/500000", "500000/1000000", "1000000/1000000"));
		verifyUMUIMAfterBIChange(policyNumber, "1000000/1000000", "25000/50000", "25000/50000", false, ImmutableList.of("25000/50000"));
	}

	private void verifyUMUIMAfterBIChange(String policyNumber, String startingBILimit, String newBILimit,
			String expectedUMUIMLimit, boolean expectedCanChange, List<String> expectedAvailableLimits) {
		// Code starts by setting BI to the expected startingBILimit
		PolicyCoverageInfo coverageResponse = updateCoverage(policyNumber,"BI", startingBILimit);
		Coverage filteredCoverageResponseBI = findCoverage(coverageResponse.policyCoverages,CoverageInfo.BI.getCode());
		assertThat(filteredCoverageResponseBI.getCoverageLimit()).isEqualTo(startingBILimit);

		// Code checks if UMBI is equal to the startingBILimit. If it's not, it updates UMBI.
		Coverage startingUMBIResponse = findCoverage(coverageResponse.policyCoverages,CoverageInfo.UMBI.getCode());
		if (BooleanUtils.isTrue(startingUMBIResponse.getCanChangeCoverage()) && !startingUMBIResponse.getCoverageLimit().equals(startingBILimit)) {
			PolicyCoverageInfo umbiCoverageResponse = updateCoverage(policyNumber, "UMBI", startingBILimit);
			Coverage startingUMBIUpdatedResponse = findCoverage(umbiCoverageResponse.policyCoverages,CoverageInfo.UMBI.getCode());
			assertThat(startingUMBIUpdatedResponse.getCoverageLimit()).isEqualTo(startingBILimit);
		}

		// Code checks if UIMBI is equal to the startingBILimit. If it's not, it updates UIMBI.
		Coverage startingUIMBIResponse = findCoverage(coverageResponse.policyCoverages,CoverageInfo.UIMBI.getCode());
		if (BooleanUtils.isTrue(startingUIMBIResponse.getCanChangeCoverage()) && !startingUMBIResponse.getCoverageLimit().equals(startingBILimit)) {
			PolicyCoverageInfo uimbiCoverageResponse = updateCoverage(policyNumber, "UIMBI", startingBILimit);
			Coverage startingUIMBIUpdatedResponse = findCoverage(uimbiCoverageResponse.policyCoverages,CoverageInfo.UIMBI.getCode());
			assertThat(startingUIMBIUpdatedResponse.getCoverageLimit()).isEqualTo(startingBILimit);
		}

		// Code updates BI to be equal to the newBILimit
		PolicyCoverageInfo coverageResponseUpdate = updateCoverage(policyNumber,"BI", newBILimit);
		Coverage updateBIResponse = findCoverage(coverageResponseUpdate.policyCoverages,CoverageInfo.BI.getCode());
		assertThat(updateBIResponse.getCoverageLimit()).isEqualTo(newBILimit);

		// Code checks that UMBI limit is equal to expectedUMUIMLimit, that canChangeCoverage is equal to expectedCanChange and that the availableLimits returned are equal to the expectedAvailableLimits
		Coverage updatedUMBIResponse = findCoverage(coverageResponseUpdate.policyCoverages,CoverageInfo.UMBI.getCode());
		assertThat(updatedUMBIResponse.getCoverageLimit()).isEqualTo(expectedUMUIMLimit);
		assertThat(updatedUMBIResponse.getCanChangeCoverage().equals(expectedCanChange));
		assertThat(expectedAvailableLimits.size()).isEqualTo(updatedUMBIResponse.getAvailableLimits().size());
		updatedUMBIResponse.getAvailableLimits().forEach(coverageLimit -> expectedAvailableLimits.contains(coverageLimit.getCoverageLimit()));

		// Code checks that UIMBI limit is equal to expectedUMUIMLimit, that canChangeCoverage is equal to expectedCanChange and that the availableLimits returned are equal to the expectedAvailableLimits
		Coverage updatedUIMBIResponse = findCoverage(coverageResponseUpdate.policyCoverages,CoverageInfo.UIMBI.getCode());
		assertThat(updatedUIMBIResponse.getCoverageLimit()).isEqualTo(expectedUMUIMLimit);
		assertThat(updatedUIMBIResponse.getCanChangeCoverage().equals(expectedCanChange));
		assertThat(expectedAvailableLimits.size()).isEqualTo(updatedUIMBIResponse.getAvailableLimits().size());
		updatedUIMBIResponse.getAvailableLimits().forEach(coverageLimit -> expectedAvailableLimits.contains(coverageLimit.getCoverageLimit()));
	}

	protected void pas20344_trailerMotorHomeAndGolfCartViewCoverageBody(ETCSCoreSoftAssertions softly, PolicyType policyType) {

		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_PPAandTrailerMotorHome").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo endorsementCoverageResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

		List<Coverage> coveragesV1 = endorsementCoverageResponse.vehicleLevelCoverages.get(0).coverages;
		validatePPA(softly, coveragesV1, true);

		List<Coverage> coveragesV2 = endorsementCoverageResponse.vehicleLevelCoverages.get(1).coverages;
		validatePPA(softly, coveragesV2, false);

		List<Coverage> coveragesV3 = endorsementCoverageResponse.vehicleLevelCoverages.get(2).coverages;
		validatePPA(softly, coveragesV3, false);

		List<Coverage> coveragesV4 = endorsementCoverageResponse.vehicleLevelCoverages.get(3).coverages;
		validatePPA(softly, coveragesV4, false);
	}

	private void validatePPA(ETCSCoreSoftAssertions softly, List<Coverage> coverages, boolean isPPA) {
		Coverage compded = Coverage.create(CoverageInfo.COMPDED_AZ);
		Coverage collded = Coverage.create(CoverageInfo.COLLDED_AZ);
		Coverage glass = Coverage.create(CoverageInfo.GLASS_AZ);
		Coverage loan = Coverage.create(CoverageInfo.LOAN);
		Coverage rreim = Coverage.create(CoverageInfo.RREIM);
		Coverage towinglabor = Coverage.create(CoverageInfo.TOWINGLABOR);
		Coverage specequip = Coverage.create(CoverageInfo.SPECEQUIP).disableCanChange();

		if (!isPPA) {
			Coverage compdedNonPpa = Coverage.create(CoverageInfo.COMPDED_AZ_PPA);
			Coverage colldedNonPpa = Coverage.create(CoverageInfo.COLLDED_AZ_PPA);
			Coverage specialEquipment = Coverage.create(CoverageInfo.SPECEQUIP_NONPPA).disableCustomerDisplay();

			glass.disableCustomerDisplay();
			loan.disableCustomerDisplay();
			rreim.disableCustomerDisplay();
			towinglabor.disableCustomerDisplay();
			specequip.disableCustomerDisplay();

			softly.assertThat(coverages.get(0)).isEqualToComparingFieldByField(compdedNonPpa);
			softly.assertThat(coverages.get(1)).isEqualToComparingFieldByField(colldedNonPpa);
			softly.assertThat(coverages.get(2)).isEqualToComparingFieldByField(glass);
			softly.assertThat(coverages.get(3)).isEqualToComparingFieldByField(loan);
			softly.assertThat(coverages.get(4)).isEqualToComparingFieldByField(rreim);
			softly.assertThat(coverages.get(5)).isEqualToComparingFieldByField(towinglabor);
			softly.assertThat(coverages.get(6)).isEqualToComparingFieldByField(specialEquipment);
		} else {
			softly.assertThat(coverages.get(0)).isEqualToComparingFieldByField(compded);
			softly.assertThat(coverages.get(1)).isEqualToComparingFieldByField(collded);
			softly.assertThat(coverages.get(2)).isEqualToComparingFieldByField(glass);
			softly.assertThat(coverages.get(3)).isEqualToComparingFieldByField(loan);
			softly.assertThat(coverages.get(4)).isEqualToComparingFieldByField(rreim);
			softly.assertThat(coverages.get(5)).isEqualToComparingFieldByField(towinglabor);
			softly.assertThat(coverages.get(6)).isEqualToComparingFieldByField(specequip);
		}
	}

	private PolicyCoverageInfo updateCoverage(String policyNumber, Coverage updateData) {
		return HelperCommon.updateEndorsementCoverage(policyNumber, DXPRequestFactory.createUpdateCoverageRequest(updateData.getCoverageCd(),
				updateData.getCoverageLimit()), PolicyCoverageInfo.class);
	}

	private Coverage findPolicyCoverage(PolicyCoverageInfo policyCoverageInfo, String coverageCd) {
		return policyCoverageInfo.policyCoverages.stream()
				.filter(cov -> coverageCd.equals(cov.getCoverageCd()))
				.findFirst()
				.orElse(null);
	}

	private Coverage findVehicleCoverage(PolicyCoverageInfo policyCoverageInfo, String coverageCd, String oid) {
		return policyCoverageInfo.vehicleLevelCoverages.stream()
				.filter(cov -> oid.equals(cov.oid))
				.flatMap(cov -> cov.coverages.stream())
				.filter(cov -> coverageCd.equals(cov.getCoverageCd()))
				.findFirst()
				.orElse(null);
	}

	protected void pas15496_viewCoveragesUmpdWhenYouDontHaveCompCollBody(String state, PolicyType policyType, boolean runOnMotorHome) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_PPA_and_MotorHome").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		String vehicleOid;

		if (runOnMotorHome) {
			ViewVehicleResponse viewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
			String vin2 = td.getTestDataList("VehicleTab").get(1).getValue("VIN"); //MotorHome
			vehicleOid = viewVehicles.vehicleList.stream().filter(vehicle -> vin2.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;
		} else {
			ViewVehicleResponse viewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
			String vin1 = td.getTestDataList("VehicleTab").get(0).getValue("VIN"); //PPA
			vehicleOid = viewVehicles.vehicleList.stream().filter(vehicle -> vin1.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;
		}

		String coverageCdComp = "COMPDED";
		String coverageCdColl = "COLLDED";
		String limitDisplayNoCov = "No Coverage";
		String availableLimitsChange = "-1";
		String availableLimitsChange2 = "250";

		//TC1
		//Remove COMP/COLL and check UMBI
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, vehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdComp, availableLimitsChange), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		helperMiniServices.endorsementRateAndBind(policyNumber);
		//Hit viewPolicyCoverages, check UMBI
		viewUmpdCoverage(policyNumber, vehicleOid, true, true, limitDisplayNoCov, state);

		//Motor home on CO cannot be rated without Coll
		if (!runOnMotorHome || !state.equals(Constants.States.OH)) {
			//Prepare policy: Coll = No coverage
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, vehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdComp, availableLimitsChange2), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			helperMiniServices.endorsementRateAndBind(policyNumber);
			//Hit viewPolicyCoverages, check UMBI
			viewUmpdCoverage(policyNumber, vehicleOid, true, true, limitDisplayNoCov, state);
		}

		//TC2
		//Comp/Coll = both have other than no coverage.
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, vehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdComp, availableLimitsChange2), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, vehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdColl, availableLimitsChange2), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		helperMiniServices.endorsementRateAndBind(policyNumber);
		//Hit viewPolicyCoverages, check UMBI
		viewUmpdCoverage(policyNumber, vehicleOid, false, false, limitDisplayNoCov, state);

		//TC3
		//UM/UIM - No Coverage, Comp/Coll = no coverage

		helperMiniServices.createEndorsementWithCheck(policyNumber);
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.setPolicyCoverageDetailsValue(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel(), "No Coverage");
		premiumAndCoveragesTab.saveAndExit();
		helperMiniServices.endorsementRateAndBind(policyNumber);
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, vehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdComp, availableLimitsChange), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		helperMiniServices.endorsementRateAndBind(policyNumber);
		//Hit viewPolicyCoverages, check UMBI
		viewUmpdCoverage(policyNumber, vehicleOid, false, false, limitDisplayNoCov, state);

		//Motor home on CO cannot be rated without Coll
		if (!runOnMotorHome || !state.equals(Constants.States.OH)) {
			//Return back Comp coverage
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, vehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdComp, availableLimitsChange2), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			helperMiniServices.endorsementRateAndBind(policyNumber);
			viewUmpdCoverage(policyNumber, vehicleOid, false, false, limitDisplayNoCov, state);
		}
	}

	private void viewUmpdCoverage(String policyNumber, String vehicleOid, boolean customerDisplayed, boolean canChangeCoverage, String coverageLimitDisplay, String state) {

		PolicyCoverageInfo policyCoveragesRegularVehicle = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, vehicleOid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		if (state.equals(Constants.States.CO)) {
			Coverage filteredCoverageResponseUmbi = findCoverage(policyCoveragesRegularVehicle.vehicleLevelCoverages.get(0).coverages, "UMPDDED");
			assertAvailableCoverageLimitsForUMBI(filteredCoverageResponseUmbi, customerDisplayed, canChangeCoverage, coverageLimitDisplay);
		} else {
			Coverage filteredCoverageResponseUmbi = findCoverage(policyCoveragesRegularVehicle.vehicleLevelCoverages.get(0).coverages, "UMPD");
			assertAvailableCoverageLimitsForUMBI(filteredCoverageResponseUmbi, customerDisplayed, canChangeCoverage, coverageLimitDisplay);
		}
	}

	private void assertAvailableCoverageLimitsForUMBI(Coverage coverageResponse, boolean customerDisplayed, boolean canChangeCoverage, String coverageLimitDisplay) {

		assertSoftly(softly -> {
			softly.assertThat(coverageResponse.getCustomerDisplayed()).isEqualTo(customerDisplayed);
			softly.assertThat(coverageResponse.getCanChangeCoverage()).isEqualTo(canChangeCoverage);
			softly.assertThat(coverageResponse.getCoverageLimitDisplay()).isEqualTo(coverageLimitDisplay);

			//List<CoverageLimit> availableLimitsUMBI = coverageResponse.getAvailableLimits();
			//TODO jpukenaite: update this method with available limits check
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
			updateCoverageResponse = HelperCommon.updateEndorsementCoverage(policyNumber, updateCoverageRequest, PolicyCoverageInfo.class);

			if (coverageLimit.coverageLimit.contains("/")) {
				coverageLimitFormatted = new BigDecimal(coverageLimit.coverageLimit.substring(0, coverageLimit.coverageLimit.indexOf("/")));
			} else {
				coverageLimitFormatted = new BigDecimal(coverageLimit.coverageLimit);
			}

			/*
			BI values 500000/500000 or greater should not have error. Lower values should have error.
			PD values 100000 or greater should not have error. Lower values should have error.
			*/
			if (coverageLimitFormatted.compareTo(coverageLimitThreshold) < 0) {
				softly.assertThat(updateCoverageResponse.validations.stream().anyMatch(validation -> validation.message.contains(ErrorDxpEnum.Errors.VERIFY_PUP_POLICY.getMessage()))).
						as(coverageCd + " " + coverageLimit.coverageLimit + " should have error").isTrue();
			} else {
				softly.assertThat(updateCoverageResponse.validations.stream().anyMatch(validation -> validation.message.contains(ErrorDxpEnum.Errors.VERIFY_PUP_POLICY.getMessage()))).
						as(coverageCd + " " + coverageLimit.coverageLimit + " should not have error").isFalse();
			}

			//Validate that coverage is updated
			softly.assertThat(updateCoverageResponse.policyCoverages.stream().filter(coverage -> coverageCd.equals(coverage.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo(coverageLimit.coverageLimit);
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
			PolicyCoverageInfo viewEndorsementCoveragesResponse = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
			validateCustomEquipCov(softly, false, regularVehicle.oid, viewEndorsementCoveragesResponse);
			validateCustomEquipCov(softly, false, vanWithoutCE.oid, viewEndorsementCoveragesResponse);
			validateCustomEquipCov(softly, true, vanWithCE.oid, viewEndorsementCoveragesResponse);

			//viewPolicyCoverages
			PolicyCoverageInfo viewPolicyCoveragesResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class);
			validateCustomEquipCov(softly, false, regularVehicle.oid, viewPolicyCoveragesResponse);
			validateCustomEquipCov(softly, false, vanWithoutCE.oid, viewPolicyCoveragesResponse);
			validateCustomEquipCov(softly, true, vanWithCE.oid, viewPolicyCoveragesResponse);

			// viewPolicyCoveragesByVehicle
			PolicyCoverageInfo policyCoveragesRegularVehicle = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, regularVehicle.oid, PolicyCoverageInfo.class);
			validateCustomEquipCov(softly, false, regularVehicle.oid, policyCoveragesRegularVehicle);

			PolicyCoverageInfo policyCoveragesVanWithoutCE = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, vanWithoutCE.oid, PolicyCoverageInfo.class);
			validateCustomEquipCov(softly, false, vanWithoutCE.oid, policyCoveragesVanWithoutCE);

			PolicyCoverageInfo policyCoveragesVanWithCE = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, vanWithCE.oid, PolicyCoverageInfo.class);
			validateCustomEquipCov(softly, true, vanWithCE.oid, policyCoveragesVanWithCE);

			//viewEndorsementCoveragesByVehicle
			PolicyCoverageInfo endorsementCoveragesRegularVehicle = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, regularVehicle.oid, PolicyCoverageInfo.class);
			validateCustomEquipCov(softly, false, regularVehicle.oid, endorsementCoveragesRegularVehicle);

			PolicyCoverageInfo endorsementCoveragesVanWithoutCE = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, vanWithoutCE.oid, PolicyCoverageInfo.class);
			validateCustomEquipCov(softly, false, vanWithoutCE.oid, endorsementCoveragesVanWithoutCE);

			//START PAS-19834
			PolicyCoverageInfo endorsementCoveragesVanWithCE = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, vanWithCE.oid, PolicyCoverageInfo.class);
			validateCustomEquipCov(softly, true, vanWithCE.oid, endorsementCoveragesVanWithCE);

			//remove COMP coverage
			String coverageCdChange = "COMPDED";
			String availableLimitsChange = "-1";
			String availableLimitsChange2 = "500";

			PolicyCoverageInfo compUpdatedCoverage = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, vanWithCE.oid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChange, availableLimitsChange), PolicyCoverageInfo.class);
			Coverage filteredCoverageResponseComp = compUpdatedCoverage.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COMPDED".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponseComp.getCoverageLimit()).isEqualTo(availableLimitsChange);
			softly.assertThat(filteredCoverageResponseComp.getCoverageLimitDisplay()).isEqualTo("No Coverage");

			//check if CUSTEQUIP was removed
			validateCustomEquipCov(softly, false, vanWithCE.oid, compUpdatedCoverage);

			//rate, to check if there is no error
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//return COMPED back
			PolicyCoverageInfo compUpdatedCoverage2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, vanWithCE.oid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdChange, availableLimitsChange2), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			Coverage filteredCoverageResponseComp2 = compUpdatedCoverage2.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COMPDED".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredCoverageResponseComp2.getCoverageLimit()).isEqualTo(availableLimitsChange2);

			validateCustomEquipCov(softly, false, vanWithCE.oid, compUpdatedCoverage2);

			//rate, to check if there is no error
			helperMiniServices.endorsementRateAndBind(policyNumber);
		});
	}

	protected void pas15788_PDAvailableLimitsWhenBIisTheLowestAvailableBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		helperMiniServices.createEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo policyCoverageInfo = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage coverageBI = findCoverage(policyCoverageInfo.policyCoverages, "BI");
		String lowestBILimitForState = coverageBI.getAvailableLimits().get(0).coverageLimit; //availableLimit at index 0 should be the lowest one
		//Update BI to lowest available limit
		Coverage coveragePDAfterUpdate = updateBIAndGetPD_pas15788(policyNumber, lowestBILimitForState);

		int availablePDLimitsSizeExpected = 3;//expected availableLimits size for PD when BI is the lowest available limit
		if (Constants.States.WY.equals(getState())) {
			checkLowestAvailableBILimit_pas15788(lowestBILimitForState, "25000/50000");
			validateAvailableCoverageLimit(coveragePDAfterUpdate, 0, "20000", "$20,000");
			validateAvailableCoverageLimit(coveragePDAfterUpdate, 1, "25000", "$25,000");
			validateAvailableCoverageLimit(coveragePDAfterUpdate, 2, "50000", "$50,000");
		} else if (Constants.States.UT.equals(getState())) {
			checkLowestAvailableBILimit_pas15788(lowestBILimitForState, "25000/65000");
			validateAvailableCoverageLimit(coveragePDAfterUpdate, 0, "15000", "$15,000");
			validateAvailableCoverageLimit(coveragePDAfterUpdate, 1, "25000", "$25,000");
			validateAvailableCoverageLimit(coveragePDAfterUpdate, 2, "50000", "$50,000");
		} else if (Constants.States.NV.contains(getState()) || Constants.States.SD.contains(getState())) {
			//BUG:PAS-20384 DXP: view Coverages service displays wrong available BI limits for NV
			//BUG:PAS-20398 DXP: view Coverages service displays wrong available PD limits for NV
			checkLowestAvailableBILimit_pas15788(lowestBILimitForState, "25000/50000");
			availablePDLimitsSizeExpected = 2;
			validateAvailableCoverageLimit(coveragePDAfterUpdate, 0, "25000", "$25,000");
			validateAvailableCoverageLimit(coveragePDAfterUpdate, 1, "50000", "$50,000");
		} else if (Constants.States.AZ.contains(getState())) {
			checkLowestAvailableBILimit_pas15788(lowestBILimitForState, "15000/30000");
			validateAvailableCoverageLimit(coveragePDAfterUpdate, 0, "10000", "$10,000");
			validateAvailableCoverageLimit(coveragePDAfterUpdate, 1, "15000", "$15,000");
			validateAvailableCoverageLimit(coveragePDAfterUpdate, 2, "25000", "$25,000");
		}

		assertThat(coveragePDAfterUpdate.getAvailableLimits().size())
				.as("Only values available for Property Damage should be those that are less than or equal to the Per Accident amount for Bodily Injury")
				.isEqualTo(availablePDLimitsSizeExpected);

		//assert that delimiter for Bodily Injury Liability shows as Per Person/Per Accident and the delimiter for Property Damage Liability shows as Per Accident (as said in US)
		assertThat(coveragePDAfterUpdate.getCoverageType()).isEqualTo("Per Accident");
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		assertThat(findCoverage(viewEndorsementCoverages.policyCoverages, "BI").getCoverageType()).isEqualTo("Per Person/Per Accident");

		//Update BI to second lowest available limit and check that available PD limits size is greater than with lowest available BI limit
		coveragePDAfterUpdate = updateBIAndGetPD_pas15788(policyNumber, coverageBI.getAvailableLimits().get(1).coverageLimit);
		assertThat(coveragePDAfterUpdate.getAvailableLimits().size()).isGreaterThan(availablePDLimitsSizeExpected);

		//rate and bind to finish transaction
		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	protected void pas19195_viewUpdatePIPCoverage_KYBody() {
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
		TestData td = getPolicyDefaultTD();
		td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_FNI_AFR_Excluded_NAFR").getTestDataList("DriverTab"))
				.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError).resolveLinks();

		td.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName()
				, AutoSSMetaData.PremiumAndCoveragesTab.BASIC_PERSONAL_INJURY_PROTECTION_COVERAGE.getLabel()), "contains=$10,000")
				.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName()
						, AutoSSMetaData.PremiumAndCoveragesTab.ADDITIONAL_PERSONAL_INJURY_PROTECTION_COVERAGE.getLabel()), "contains=$30,000")
				.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName()
						, AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION_DEDUCTIBLE.getLabel()), "contains=$500");

		String policyNumber = openAppAndCreatePolicy(td);

		helperMiniServices.createEndorsementWithCheck(policyNumber);
		//validate view endorsement coverages
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

		assertSoftly(softly -> {

			Map<String, Coverage> mapPIPCoveragesActual = getPIPCoverages(viewEndorsementCoverages.policyCoverages);

			Map<String, Coverage> mapPIPCoveragesExpected = new LinkedHashMap<>();
			mapPIPCoveragesExpected.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP));
			mapPIPCoveragesExpected.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP).changeLimit(CoverageLimits.COV_30000));
			mapPIPCoveragesExpected.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED).changeLimit(CoverageLimits.DED_500));
			mapPIPCoveragesExpected.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());

			validatePIPCoverages_KY(softly, policyNumber, mapPIPCoveragesExpected, mapPIPCoveragesActual, null);

			//get drivers with TORT coverage available
			Coverage tortCoverage = findCoverage(viewEndorsementCoverages.driverCoverages, "TORT");
			List<String> tortAvailableDrivers = new ArrayList<>(tortCoverage.getAvailableDrivers());
			String driverWithTORTOid1 = tortAvailableDrivers.get(0);
			String driverWithTORTOid2 = tortAvailableDrivers.get(1);
			assertThat(tortAvailableDrivers.size()).as("In this test only 2 drivers are expected to have TORT available.").isEqualTo(2);

			//AC#1: update Basic PIP to No Coverage
			validateTORTPrecondition_pas19195(policyNumber, true);
			PolicyCoverageInfo updateCoverageResponse = updateCoverage(policyNumber, CoverageInfo.BPIP.getCode(), CoverageLimits.COV_0.getLimit());
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			Map<String, Coverage> mapPipNoCoverage = new LinkedHashMap<>();
			mapPipNoCoverage.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP).changeLimit(CoverageLimits.COV_0));
			mapPipNoCoverage.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP).disableCanChange().disableCustomerDisplay());
			mapPipNoCoverage.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED).disableCanChange().disableCustomerDisplay());
			mapPipNoCoverage.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).changeLimit(CoverageLimits.COV_10000).disableCanChange());

			validatePIPCoverages_KY(softly, policyNumber, mapPipNoCoverage, mapPIPCoveragesActual, updateCoverageResponse);

			//AC#2: update Basic PIP to $10,000
			validateTORTPrecondition_pas19195(policyNumber, true);
			updateCoverageResponse = updateCoverage(policyNumber, CoverageInfo.BPIP.getCode(), CoverageLimits.COV_10000.getLimit());
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			Map<String, Coverage> mapPipToTenThous = new LinkedHashMap<>();
			mapPipToTenThous.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP));
			mapPipToTenThous.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP));
			mapPipToTenThous.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED));
			mapPipToTenThous.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());

			validatePIPCoverages_KY(softly, policyNumber, mapPipToTenThous, mapPIPCoveragesActual, updateCoverageResponse);

			//AC#5: update one or more drivers to be Reject Limit to Sue = No
			validateTORTPrecondition_pas19195(policyNumber, true);
			updateCoverageResponse = updateTORTCoverage(policyNumber, ImmutableList.of(driverWithTORTOid2));
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			//PIP doesn't change
			Map<String, Coverage> pipDoesntChange = new LinkedHashMap<>();
			pipDoesntChange.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP).removeAvailableLimit(CoverageLimits.COV_0).disableCanChange());
			pipDoesntChange.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP));
			pipDoesntChange.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED));
			pipDoesntChange.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());
			validateTORTPrecondition_pas19195(policyNumber, false);
			validatePIPCoverages_KY(softly, policyNumber, pipDoesntChange, mapPIPCoveragesActual, updateCoverageResponse);

			//AC#3: validate update endorsement coverages (ADDPIP) to other than 0
			validateTORTPrecondition_pas19195(policyNumber, false);
			updateCoverageResponse = updateCoverage(policyNumber, CoverageInfo.ADDPIP.getCode(), CoverageLimits.COV_20000.getLimit());
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			Map<String, Coverage> addPipOtherThenZero = new LinkedHashMap<>();
			addPipOtherThenZero.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP).removeAvailableLimit(CoverageLimits.COV_0).disableCanChange());
			addPipOtherThenZero.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP).changeLimit(CoverageLimits.COV_20000));
			addPipOtherThenZero.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED));
			addPipOtherThenZero.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());

			validateTORTPrecondition_pas19195(policyNumber, false);
			validatePIPCoverages_KY(softly, policyNumber, addPipOtherThenZero, mapPIPCoveragesActual, updateCoverageResponse);

			//Update back to 0
			validateTORTPrecondition_pas19195(policyNumber, false);
			updateCoverageResponse = updateCoverage(policyNumber, CoverageInfo.ADDPIP.getCode(), CoverageLimits.COV_0.getLimit());
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			Map<String, Coverage> addPipEqualToZero = new LinkedHashMap<>();
			addPipEqualToZero.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP).removeAvailableLimit(CoverageLimits.COV_0).disableCanChange());
			addPipEqualToZero.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP));
			addPipEqualToZero.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED));
			addPipEqualToZero.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());
			validatePIPCoverages_KY(softly, policyNumber, addPipEqualToZero, mapPIPCoveragesActual, updateCoverageResponse);

			//Update back to to other than 0
			validateTORTPrecondition_pas19195(policyNumber, false);
			updateCoverageResponse = updateCoverage(policyNumber, CoverageInfo.ADDPIP.getCode(), CoverageLimits.COV_40000.getLimit());
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			Map<String, Coverage> pipFortyThous = new LinkedHashMap<>();
			pipFortyThous.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP).removeAvailableLimit(CoverageLimits.COV_0).disableCanChange());
			pipFortyThous.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP).changeLimit(CoverageLimits.COV_40000));
			pipFortyThous.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED));
			pipFortyThous.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());
			validatePIPCoverages_KY(softly, policyNumber, pipFortyThous, mapPIPCoveragesActual, updateCoverageResponse);

			//AC#3: validate update endorsement coverages (PIPDED) to other than 0
			validateTORTPrecondition_pas19195(policyNumber, false);
			updateCoverageResponse = updateCoverage(policyNumber, CoverageInfo.PIPDED.getCode(), CoverageLimits.DED_250.getLimit());
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			Map<String, Coverage> pipdedMoreZero = new LinkedHashMap<>();
			pipdedMoreZero.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP).removeAvailableLimit(CoverageLimits.COV_0).disableCanChange());
			pipdedMoreZero.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP).changeLimit(CoverageLimits.COV_40000));
			pipdedMoreZero.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED).changeLimit(CoverageLimits.DED_250));
			pipdedMoreZero.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());

			validatePIPCoverages_KY(softly, policyNumber, pipdedMoreZero, mapPIPCoveragesActual, updateCoverageResponse);

			//Update back to 0
			validateTORTPrecondition_pas19195(policyNumber, false);
			updateCoverageResponse = updateCoverage(policyNumber, CoverageInfo.PIPDED.getCode(), CoverageLimits.DED_0.getLimit());
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			Map<String, Coverage> pipdedBackToZero = new LinkedHashMap<>();
			pipdedBackToZero.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP).removeAvailableLimit(CoverageLimits.COV_0).disableCanChange());
			pipdedBackToZero.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP).changeLimit(CoverageLimits.COV_40000));
			pipdedBackToZero.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED));
			pipdedBackToZero.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());

			validatePIPCoverages_KY(softly, policyNumber, pipdedBackToZero, mapPIPCoveragesActual, updateCoverageResponse);

			//Update back to other than 0
			validateTORTPrecondition_pas19195(policyNumber, false);
			updateCoverageResponse = updateCoverage(policyNumber, CoverageInfo.PIPDED.getCode(), CoverageLimits.DED_1000.getLimit());
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			Map<String, Coverage> pipdedMoreThenZero = new LinkedHashMap<>();
			pipdedMoreThenZero.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP).removeAvailableLimit(CoverageLimits.COV_0).disableCanChange());
			pipdedMoreThenZero.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP).changeLimit(CoverageLimits.COV_40000));
			pipdedMoreThenZero.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED).changeLimit(CoverageLimits.DED_1000));
			pipdedMoreThenZero.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());

			validatePIPCoverages_KY(softly, policyNumber, pipdedMoreThenZero, mapPIPCoveragesActual, updateCoverageResponse);

			//AC#4: update all drivers to be Reject Limit to Sue = YES
			validateTORTPrecondition_pas19195(policyNumber, false);
			updateCoverageResponse = updateTORTCoverage(policyNumber, ImmutableList.of(driverWithTORTOid1, driverWithTORTOid2));
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			//values stays the same as above
			Map<String, Coverage> rejectLimitEqualYes = new LinkedHashMap<>();
			rejectLimitEqualYes.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP));
			rejectLimitEqualYes.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP).changeLimit(CoverageLimits.COV_40000));
			rejectLimitEqualYes.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED).changeLimit(CoverageLimits.DED_1000));
			rejectLimitEqualYes.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());

			validatePIPCoverages_KY(softly, policyNumber, rejectLimitEqualYes, mapPIPCoveragesActual, updateCoverageResponse);

			//AC#6: update one or more drivers to be Reject Limit to Sue = No
			//update BPIP to no coverage (this is AC#1 functionality again)
			updateCoverage(policyNumber, CoverageInfo.BPIP.getCode(), CoverageLimits.COV_0.getLimit());

			//update one or more drivers to be Reject Limit to Sue = No
			validateTORTPrecondition_pas19195(policyNumber, true);
			updateCoverageResponse = updateTORTCoverage(policyNumber, ImmutableList.of(driverWithTORTOid1));
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			Map<String, Coverage> bpipToNoCoverage = new LinkedHashMap<>();
			bpipToNoCoverage.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP).removeAvailableLimit(CoverageLimits.COV_0).disableCanChange());
			bpipToNoCoverage.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP));
			bpipToNoCoverage.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED));
			bpipToNoCoverage.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());

			validatePIPCoverages_KY(softly, policyNumber, bpipToNoCoverage, mapPIPCoveragesActual, updateCoverageResponse);

			helperMiniServices.endorsementRateAndBind(policyNumber);
		});
	}

	protected void pas15358_viewUpdatePIPCoverage_KSBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		//validate view endorsement coverages
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);

		Coverage pipExpected = Coverage.createWithCdAndDescriptionOnly(CoverageInfo.PIP_KS_4500);
		Coverage medexpExpected = Coverage.create(CoverageInfo.MEDEXP_KS);
		Coverage worklossExpected = Coverage.create(CoverageInfo.WORKLOSS_KS_4500).disableCanChange();

		assertSoftly(softly -> {
			Coverage pipCoverageActual = findCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.PIP_KS_4500.getCode());
			softly.assertThat(pipCoverageActual).isEqualToIgnoringGivenFields(pipExpected, "subCoverages");

			List<Coverage> pipSubCoveragesActual = findCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.PIP_KS_4500.getCode()).getSubCoverages();
			softly.assertThat(findCoverage(pipSubCoveragesActual, CoverageInfo.MEDEXP_KS.getCode())).isEqualToComparingFieldByField(medexpExpected);
			softly.assertThat(findCoverage(pipSubCoveragesActual, CoverageInfo.WORKLOSS_KS_4500.getCode())).isEqualToComparingFieldByField(worklossExpected);
			validatePIPInUI_pas15358(softly, findCoverage(pipSubCoveragesActual, CoverageInfo.MEDEXP_KS.getCode()));
			validatePIPSubCoveragesThatDoesntChange_pas15358(pipSubCoveragesActual);

			//Update PIP (MEDPIP) coverage to every available limit
			List<CoverageLimits> reversedAvailableCoverageLimitList = AvailableCoverageLimits.MEDEXP_KS.getReversedAvailableLimits(); //reversing list so that first update is not to already selected limit
			for (CoverageLimits coverageLimit : reversedAvailableCoverageLimitList) {
				pipSubCoveragesActual = validateUpdatePIP_pas15359(softly, policyNumber, coverageLimit);
				validatePIPSubCoveragesThatDoesntChange_pas15358(pipSubCoveragesActual);
			}
		});

		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	protected void pas15365_viewUpdatePIPCoverage_ORBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		//validate view endorsement coverages
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);

		Coverage pipExpected = Coverage.createWithCdAndDescriptionOnly(CoverageInfo.PIP_OR);
		Coverage medexpExpected = Coverage.create(CoverageInfo.MEDEXP_OR);
		Coverage pipdedExpected = Coverage.create(CoverageInfo.PIPDED_OR);

		assertSoftly(softly -> {
			Coverage pipCoverageActual = findCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.PIP_OR.getCode());
			softly.assertThat(pipCoverageActual).isEqualToIgnoringGivenFields(pipExpected, "subCoverages");
			Coverage pipdedCoverageActual = findCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.PIPDED_OR.getCode());
			assertThat(pipdedCoverageActual).isEqualToIgnoringGivenFields(pipdedExpected);

			List<Coverage> pipSubCoveragesActual = findCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.PIP_OR.getCode()).getSubCoverages();
			softly.assertThat(findCoverage(pipSubCoveragesActual, CoverageInfo.MEDEXP_OR.getCode())).isEqualToComparingFieldByField(medexpExpected);
			validatePIPSubCoveragesThatDoesntChange_pas15365(pipSubCoveragesActual);
			validatePIPInUI_pas15365(softly, findCoverage(pipSubCoveragesActual, CoverageInfo.MEDEXP_OR.getCode()), pipdedCoverageActual);

			//update PIP by updating MEDEXP
			CoverageLimits medexpNewLimit = CoverageLimits.COV_100000;
			PolicyCoverageInfo updateCoverageResponse1 = updateCoverage(policyNumber, CoverageInfo.MEDEXP_OR.getCode(), medexpNewLimit.getLimit());
			List<Coverage> pipSubCovAfterUpdateActual1 = findCoverage(updateCoverageResponse1.policyCoverages, CoverageInfo.PIP_OR.getCode()).getSubCoverages();
			Coverage pipAfterUpdateActual1 = findCoverage(updateCoverageResponse1.policyCoverages, CoverageInfo.PIP_OR.getCode());
			Coverage medexpAfterUpdateExpected1 = Coverage.create(CoverageInfo.MEDEXP_OR).changeLimit(medexpNewLimit);

			softly.assertThat(pipAfterUpdateActual1).isEqualToIgnoringGivenFields(pipExpected, "subCoverages");
			softly.assertThat(findCoverage(pipSubCovAfterUpdateActual1, CoverageInfo.MEDEXP_OR.getCode())).isEqualToComparingFieldByField(medexpAfterUpdateExpected1);
			softly.assertThat(findCoverage(updateCoverageResponse1.policyCoverages, CoverageInfo.PIPDED_OR.getCode())).isEqualToComparingFieldByField(pipdedExpected);
			validatePIPSubCoveragesThatDoesntChange_pas15365(pipSubCovAfterUpdateActual1);
			validatePIPInUI_pas15365(softly, findCoverage(pipSubCovAfterUpdateActual1, CoverageInfo.MEDEXP_OR.getCode()), findCoverage(updateCoverageResponse1.policyCoverages, CoverageInfo.PIPDED_OR.getCode()));

			//update PIPDED
			CoverageLimits pipdedNewLimit = CoverageLimits.DED_0;
			PolicyCoverageInfo updateCoverageResponse2 = updateCoverage(policyNumber, CoverageInfo.PIPDED_OR.getCode(), pipdedNewLimit.getLimit());
			List<Coverage> pipSubCovAfterUpdateActual2 = findCoverage(updateCoverageResponse2.policyCoverages, CoverageInfo.PIP_OR.getCode()).getSubCoverages();
			Coverage pipAfterUpdateActual2 = findCoverage(updateCoverageResponse2.policyCoverages, CoverageInfo.PIP_OR.getCode());
			Coverage pipdedAfterUpdateExpected2 = Coverage.create(CoverageInfo.PIPDED_OR).changeLimit(pipdedNewLimit);

			softly.assertThat(pipAfterUpdateActual2).isEqualToIgnoringGivenFields(pipExpected, "subCoverages");
			softly.assertThat(findCoverage(pipSubCovAfterUpdateActual2, CoverageInfo.MEDEXP_OR.getCode())).isEqualToComparingFieldByField(medexpAfterUpdateExpected1);
			softly.assertThat(findCoverage(updateCoverageResponse2.policyCoverages, CoverageInfo.PIPDED_OR.getCode())).isEqualToComparingFieldByField(pipdedAfterUpdateExpected2);
			validatePIPSubCoveragesThatDoesntChange_pas15365(pipSubCovAfterUpdateActual2);
			validatePIPInUI_pas15365(softly, findCoverage(pipSubCovAfterUpdateActual2, CoverageInfo.MEDEXP_OR.getCode()), findCoverage(updateCoverageResponse2.policyCoverages, CoverageInfo.PIPDED_OR.getCode()));
		});
		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	protected void pas15368_viewUpdatePIPCoverage_UTBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		//validate view endorsement coverages
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);

		Coverage rejectWorklossExpected = Coverage.create(CoverageInfo.WLB_UT);
		Coverage pipExpectedBasic = Coverage.createWithCdAndDescriptionOnly(CoverageInfo.PIP_NO_UT);
		Coverage pipExpectedLimited = Coverage.createWithCdAndDescriptionOnly(CoverageInfo.PIP_YES_UT);
		Coverage medexpExpected = Coverage.create(CoverageInfo.MEDEXP_UT);
		Coverage worklossExpected = Coverage.create(CoverageInfo.WORKLOSS_UT).disableCanChange();

		assertSoftly(softly -> {
			Coverage pipCoverageActual = findCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.PIP_NO_UT.getCode());
			softly.assertThat(pipCoverageActual).isEqualToIgnoringGivenFields(pipExpectedBasic, "subCoverages");

			List<Coverage> pipSubCoveragesActual = findCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.PIP_NO_UT.getCode()).getSubCoverages();
			softly.assertThat(findCoverage(pipSubCoveragesActual, CoverageInfo.MEDEXP_UT.getCode())).isEqualToComparingFieldByField(medexpExpected);
			softly.assertThat(findCoverage(pipSubCoveragesActual, CoverageInfo.WORKLOSS_UT.getCode())).isEqualToComparingFieldByField(worklossExpected);
			softly.assertThat(findCoverage(pipSubCoveragesActual, CoverageInfo.WLB_UT.getCode())).isEqualToComparingFieldByField(rejectWorklossExpected);
			validatePIPSubCoveragesThatDoesntChange_pas15368(pipSubCoveragesActual);
			validatePIPInUI_15368(softly, findCoverage(pipSubCoveragesActual, CoverageInfo.MEDEXP_UT.getCode()), findCoverage(pipSubCoveragesActual, CoverageInfo.MEDEXP_UT.getCode()));

			//update PIP by updating MEDEXP while WLB is false
			CoverageLimits medexpNewLimit = CoverageLimits.COV_10000;
			PolicyCoverageInfo updateCoverageResponse1 = updateCoverage(policyNumber, CoverageInfo.MEDEXP_UT.getCode(), medexpNewLimit.getLimit());
			List<Coverage> pipSubCovAfterUpdateActual1 = findCoverage(updateCoverageResponse1.policyCoverages, CoverageInfo.PIP_NO_UT.getCode()).getSubCoverages();
			Coverage pipAfterUpdateActual1 = findCoverage(updateCoverageResponse1.policyCoverages, CoverageInfo.PIP_NO_UT.getCode());
			Coverage medexpAfterUpdateExpected1 = Coverage.create(CoverageInfo.MEDEXP_UT).changeLimit(medexpNewLimit);
			Coverage rejectWorkLossExpectedAfterUpdate1 = Coverage.create(CoverageInfo.WLB_UT);
			Coverage rejectWorkLossActualAfterUpdate1 = findCoverage(pipSubCovAfterUpdateActual1, CoverageInfo.WLB_UT.getCode());

			softly.assertThat(pipAfterUpdateActual1).isEqualToIgnoringGivenFields(pipExpectedBasic, "subCoverages");
			softly.assertThat(findCoverage(pipSubCovAfterUpdateActual1, CoverageInfo.MEDEXP_UT.getCode())).isEqualToComparingFieldByField(medexpAfterUpdateExpected1);
			softly.assertThat(rejectWorkLossActualAfterUpdate1).isEqualToComparingFieldByField(rejectWorkLossExpectedAfterUpdate1);
			validatePIPSubCoveragesThatDoesntChange_pas15368(pipSubCovAfterUpdateActual1);
			validatePIPInUI_15368(softly, findCoverage(pipSubCovAfterUpdateActual1, CoverageInfo.WLB_UT.getCode()), findCoverage(pipSubCovAfterUpdateActual1, CoverageInfo.MEDEXP_UT.getCode()));//code is the same in all cases
			validateSubCoveragesChangeLog(policyNumber, "update-PIP-while-WLB-false.json");

			//update WLB to true
			CoverageLimits rejectWorklossNewLimit = CoverageLimits.COV_TRUE;
			PolicyCoverageInfo updateCoverageResponse2 = updateCoverage(policyNumber, CoverageInfo.WLB_UT.getCode(), rejectWorklossNewLimit.getLimit());
			List<Coverage> pipSubCovAfterUpdateActual2 = findCoverage(updateCoverageResponse2.policyCoverages, CoverageInfo.PIP_YES_UT.getCode()).getSubCoverages();
			Coverage pipAfterUpdateActual2 = findCoverage(updateCoverageResponse2.policyCoverages, CoverageInfo.PIP_YES_UT.getCode());
			Coverage rejectWorkLossExpectedAfterUpdate2 = Coverage.create(CoverageInfo.WLB_UT).changeLimit(rejectWorklossNewLimit);
			Coverage rejectWorkLossActualAfterUpdate2 = findCoverage(pipSubCovAfterUpdateActual2, CoverageInfo.WLB_UT.getCode());

			softly.assertThat(pipAfterUpdateActual2).isEqualToIgnoringGivenFields(pipExpectedLimited, "subCoverages");
			softly.assertThat(findCoverage(pipSubCovAfterUpdateActual2, CoverageInfo.MEDEXP_UT.getCode())).isEqualToComparingFieldByField(medexpAfterUpdateExpected1);
			softly.assertThat(rejectWorkLossActualAfterUpdate2).isEqualToComparingFieldByField(rejectWorkLossExpectedAfterUpdate2);
			validatePIPSubCoveragesThatDoesntChange_pas15368(pipSubCovAfterUpdateActual2);
			validatePIPInUI_15368(softly, findCoverage(pipSubCovAfterUpdateActual2, CoverageInfo.WLB_UT.getCode()), findCoverage(pipSubCovAfterUpdateActual2, CoverageInfo.MEDEXP_UT.getCode()));
			validateSubCoveragesChangeLog(policyNumber, "update-WLB-to-true.json");

			//update PIP by updating MEDEXP while WLB is true
			medexpNewLimit = CoverageLimits.COV_5000;
			PolicyCoverageInfo updateCoverageResponse3 = updateCoverage(policyNumber, CoverageInfo.MEDEXP_UT.getCode(), medexpNewLimit.getLimit());
			List<Coverage> pipSubCovAfterUpdateActual3 = findCoverage(updateCoverageResponse3.policyCoverages, CoverageInfo.PIP_YES_UT.getCode()).getSubCoverages();
			Coverage pipAfterUpdateActual3 = findCoverage(updateCoverageResponse3.policyCoverages, CoverageInfo.PIP_YES_UT.getCode());
			Coverage medexpAfterUpdateExpected3 = Coverage.create(CoverageInfo.MEDEXP_UT).changeLimit(medexpNewLimit);
			Coverage rejectWorkLossActualAfterUpdate3 = findCoverage(pipSubCovAfterUpdateActual3, CoverageInfo.WLB_UT.getCode());

			softly.assertThat(pipAfterUpdateActual3).isEqualToIgnoringGivenFields(pipExpectedLimited, "subCoverages");
			softly.assertThat(findCoverage(pipSubCovAfterUpdateActual3, CoverageInfo.MEDEXP_UT.getCode())).isEqualToComparingFieldByField(medexpAfterUpdateExpected3);
			softly.assertThat(rejectWorkLossActualAfterUpdate3).isEqualToComparingFieldByField(rejectWorkLossExpectedAfterUpdate2);
			validatePIPSubCoveragesThatDoesntChange_pas15368(pipSubCovAfterUpdateActual3);
			validatePIPInUI_15368(softly, findCoverage(pipSubCovAfterUpdateActual3, CoverageInfo.WLB_UT.getCode()), findCoverage(pipSubCovAfterUpdateActual3, CoverageInfo.MEDEXP_UT.getCode()));//code is the same in all cases
			validateSubCoveragesChangeLog(policyNumber, "update-PIP-while-WLB-true.json");

			//update WLB to false
			rejectWorklossNewLimit = CoverageLimits.COV_FALSE;
			PolicyCoverageInfo updateCoverageResponse4 = updateCoverage(policyNumber, CoverageInfo.WLB_UT.getCode(), rejectWorklossNewLimit.getLimit());
			List<Coverage> pipSubCovAfterUpdateActual4 = findCoverage(updateCoverageResponse4.policyCoverages, CoverageInfo.PIP_NO_UT.getCode()).getSubCoverages();
			Coverage pipAfterUpdateActual4 = findCoverage(updateCoverageResponse4.policyCoverages, CoverageInfo.PIP_NO_UT.getCode());
			Coverage rejectWorkLossExpectedAfterUpdate4 = Coverage.create(CoverageInfo.WLB_UT).changeLimit(rejectWorklossNewLimit);
			Coverage rejectWorkLossActualAfterUpdate4 = findCoverage(pipSubCovAfterUpdateActual4, CoverageInfo.WLB_UT.getCode());

			softly.assertThat(pipAfterUpdateActual4).isEqualToIgnoringGivenFields(pipExpectedBasic, "subCoverages");
			softly.assertThat(findCoverage(pipSubCovAfterUpdateActual4, CoverageInfo.MEDEXP_UT.getCode())).isEqualToComparingFieldByField(medexpAfterUpdateExpected3);
			softly.assertThat(rejectWorkLossActualAfterUpdate4).isEqualToComparingFieldByField(rejectWorkLossExpectedAfterUpdate4);
			validatePIPSubCoveragesThatDoesntChange_pas15368(pipSubCovAfterUpdateActual4);
			validatePIPInUI_15368(softly, findCoverage(pipSubCovAfterUpdateActual4, CoverageInfo.WLB_UT.getCode()), findCoverage(pipSubCovAfterUpdateActual4, CoverageInfo.MEDEXP_UT.getCode()));
			validateSubCoveragesChangeLog(policyNumber, "update-WLB-to-false.json");
		});
		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	private void validateSubCoveragesChangeLog(String policyNumber, String expectedResponse) {
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		ComparablePolicy changeLogActual = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			ComparablePolicy changeLogExpected = objectMapper
					.readValue(new File("../aaa-automation-tests/src/test/resources/feature/testMiniServicesCoverages/pipCoverageUTpas15367/" + expectedResponse), ComparablePolicy.class);
			assertThat(changeLogActual).isEqualToComparingFieldByFieldRecursively(changeLogExpected);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Error getting Expected Change Log response.");
		}
	}

	private Coverage updateBIAndGetPD_pas15788(String policyNumber, String coverageBILimit) {
		UpdateCoverageRequest updateCoverageRequest = DXPRequestFactory.createUpdateCoverageRequest("BI", coverageBILimit);
		HelperCommon.updateEndorsementCoverage(policyNumber, updateCoverageRequest, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		PolicyCoverageInfo policyCoverageInfoAfterUpdate = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		return findCoverage(policyCoverageInfoAfterUpdate.policyCoverages, "PD");
	}

	//check that lowest BI limit is as expected
	private void checkLowestAvailableBILimit_pas15788(String lowestBILimitForState, String lowestBILimitForStateExpected) {
		assertThat(lowestBILimitForState).as("Lowest available limit for " + getState() + " is expected to be " + lowestBILimitForStateExpected).isEqualTo(lowestBILimitForStateExpected);
	}

	private void validateCustomEquipCov(ETCSCoreSoftAssertions softly, boolean coverageExpected, String oid, PolicyCoverageInfo policyCoverageInfo) {
		VehicleCoverageInfo vehicleCoverageInfo = getVehicleCoverages(policyCoverageInfo, oid);
		Coverage custEquip = findCoverage(vehicleCoverageInfo.coverages, "CUSTEQUIP");

		if (coverageExpected && "VA".equals(getState())) {
			assertThat(vehicleCoverageInfo.coverages.stream().anyMatch(coverage -> "CUSTEQUIP".equals(coverage.getCoverageCd()))).as("CUSTEQUIP is expected").isTrue();
			coverageXproperties(softly, custEquip, "CUSTEQUIP", "Customized Equipment", "2500.25", "$2,500.25", null, true, false);
			validateCustomEquipCoverageOrder_pas18624(softly, vehicleCoverageInfo, custEquip);
		} else {
			softly.assertThat(vehicleCoverageInfo.coverages.stream().anyMatch(coverage -> "CUSTEQUIP".equals(coverage.getCoverageCd()))).as("CUSTEQUIP is not expected").isFalse();
		}
	}

	protected void validatePIPCoverages_KY(ETCSCoreSoftAssertions softly, String policyNumber, Map<String, Coverage> mapPIPCoveragesExpected, Map<String, Coverage> mapPIPCoveragesActual, PolicyCoverageInfo updateCoverageResponse) {
		for (Map.Entry<String, Coverage> stringCoverageEntry : mapPIPCoveragesExpected.entrySet()) {
			softly.assertThat(mapPIPCoveragesActual.get(stringCoverageEntry.getKey())).isEqualToComparingFieldByField(stringCoverageEntry.getValue());
		}
		if (updateCoverageResponse != null) {
			//validate that viewEndorsementCoverages is the same as updateCoverages response
			validateViewEndorsementCoveragesIsTheSameAsUpdateCoverage(softly, policyNumber, updateCoverageResponse);
		}
		//validate PIP coverages in UI
		validatePIPInUI_pas19195(softly, mapPIPCoveragesActual);
	}

	private void validatePIPInUI_pas19195(ETCSCoreSoftAssertions softly, Map<String, Coverage> mapPIPCoverages) {
		openPendedEndorsementInquiryAndNavigateToPC();

		softly.assertThat(premiumAndCoveragesTab.getPolicyCoverageDetailsValue(AutoSSMetaData.PremiumAndCoveragesTab.BASIC_PERSONAL_INJURY_PROTECTION_COVERAGE.getLabel()))
				.isEqualTo(mapPIPCoverages.get(CoverageInfo.BPIP.getCode()).getCoverageLimitDisplay());

		//check that if BPIP is No Coverage, then ADDPIP and PIPDED is not displayed in UI
		if (CoverageLimits.COV_0.getLimit().equals(mapPIPCoverages.get(CoverageInfo.BPIP.getCode()).getCoverageLimit())) {
			softly.assertThat(PremiumAndCoveragesTab.tablePolicyLevelLiabilityCoverages.getRowContains(1, AutoSSMetaData.PremiumAndCoveragesTab.ADDITIONAL_PERSONAL_INJURY_PROTECTION_COVERAGE.getLabel())
					.getValue()).isEmpty();
			softly.assertThat(PremiumAndCoveragesTab.tablePolicyLevelLiabilityCoverages.getRowContains(1, AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION_DEDUCTIBLE.getLabel())
					.getValue()).isEmpty();
		} else {
			softly.assertThat(PremiumAndCoveragesTab.tablePolicyLevelLiabilityCoverages.getRowContains(1, AutoSSMetaData.PremiumAndCoveragesTab.ADDITIONAL_PERSONAL_INJURY_PROTECTION_COVERAGE.getLabel())
					.getValue()).isNotEmpty();
			softly.assertThat(PremiumAndCoveragesTab.tablePolicyLevelLiabilityCoverages.getRowContains(1, AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION_DEDUCTIBLE.getLabel())
					.getValue()).isNotEmpty();
			softly.assertThat(premiumAndCoveragesTab.getPolicyCoverageDetailsValue(AutoSSMetaData.PremiumAndCoveragesTab.ADDITIONAL_PERSONAL_INJURY_PROTECTION_COVERAGE.getLabel()))
					.isEqualTo(mapPIPCoverages.get(CoverageInfo.ADDPIP.getCode()).getCoverageLimitDisplay());
			softly.assertThat(premiumAndCoveragesTab.getPolicyCoverageDetailsValue(AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION_DEDUCTIBLE.getLabel()))
					.isEqualTo(mapPIPCoverages.get(CoverageInfo.PIPDED.getCode()).getCoverageLimitDisplay());
		}

		softly.assertThat(premiumAndCoveragesTab.getPolicyCoverageDetailsValue(AutoSSMetaData.PremiumAndCoveragesTab.GUEST_PIP.getLabel()))
				.isEqualTo(mapPIPCoverages.get(CoverageInfo.GPIP.getCode()).getCoverageLimitDisplay());
		premiumAndCoveragesTab.cancel();
	}

	private void validateViewEndorsementCoveragesIsTheSameAsUpdateCoverage(ETCSCoreSoftAssertions softly, String policyNumber, PolicyCoverageInfo updateCoverageResponse) {
		PolicyCoverageInfo viewEndorsementCoverages;
		viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		softly.assertThat(updateCoverageResponse).isEqualToComparingFieldByFieldRecursively(viewEndorsementCoverages);
	}

	protected Map<String, Coverage> getPIPCoverages(List<Coverage> policyCoverages) {
		Map<String, Coverage> mapPIPCoverages = new LinkedHashMap<>();
		mapPIPCoverages.put(CoverageInfo.BPIP.getCode(), findCoverage(policyCoverages, CoverageInfo.BPIP.getCode()));
		mapPIPCoverages.put(CoverageInfo.ADDPIP.getCode(), findCoverage(policyCoverages, CoverageInfo.ADDPIP.getCode()));
		mapPIPCoverages.put(CoverageInfo.PIPDED.getCode(), findCoverage(policyCoverages, CoverageInfo.PIPDED.getCode()));
		mapPIPCoverages.put(CoverageInfo.GPIP.getCode(), findCoverage(policyCoverages, CoverageInfo.GPIP.getCode()));
		return mapPIPCoverages;
	}

	//check that all drivers/not all drivers have TORT
	protected void validateTORTPrecondition_pas19195(String policyNumber, boolean allDriversHaveTORT) {
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		if (allDriversHaveTORT) {
			assertThat(findCoverage(viewEndorsementCoverages.driverCoverages, "TORT").getCurrentlyAddedDrivers().size()).as("Precondiiton: All applicable drivers must have TORT").isEqualTo(2);
		} else {
			assertThat(findCoverage(viewEndorsementCoverages.driverCoverages, "TORT").getCurrentlyAddedDrivers().size()).as("Precondiiton: Not all applicable drivers must have TORT").isLessThan(2);
		}
	}

	protected List<Coverage> validateUpdatePIP_pas15359(ETCSCoreSoftAssertions softly, String policyNumber, CoverageLimits newCoverageLimit) {
		String pipCoverageCd = CoverageInfo.PIP_KS_10000_25000.getCode(); //PIP code is the same for all limits
		Coverage pipExpected;
		Coverage medexpExpected = Coverage.create(CoverageInfo.MEDEXP_KS).changeLimit(newCoverageLimit);
		Coverage worklossExpected;

		if (newCoverageLimit.equals(CoverageLimits.COV_4500)) {
			pipExpected = Coverage.createWithCdAndDescriptionOnly(CoverageInfo.PIP_KS_4500);
			worklossExpected = Coverage.create(CoverageInfo.WORKLOSS_KS_4500).disableCanChange();
		} else {
			pipExpected = Coverage.createWithCdAndDescriptionOnly(CoverageInfo.PIP_KS_10000_25000);
			worklossExpected = Coverage.create(CoverageInfo.WORKLOSS_KS_10000_25000).disableCanChange();
		}

		PolicyCoverageInfo updateCoverageResponse = updateCoverage(policyNumber, CoverageInfo.MEDEXP_KS.getCode(), newCoverageLimit.getLimit()); //PIP is updated by updating MEDEXP
		List<Coverage> pipSubCoveragesActual = findCoverage(updateCoverageResponse.policyCoverages, pipCoverageCd).getSubCoverages();
		Coverage pipCoverageActual = findCoverage(updateCoverageResponse.policyCoverages, pipCoverageCd);

		softly.assertThat(pipCoverageActual).isEqualToIgnoringGivenFields(pipExpected, "subCoverages");
		softly.assertThat(findCoverage(pipSubCoveragesActual, CoverageInfo.MEDEXP_KS.getCode())).isEqualToComparingFieldByField(medexpExpected);
		softly.assertThat(findCoverage(pipSubCoveragesActual, CoverageInfo.WORKLOSS_KS_4500.getCode())).isEqualToComparingFieldByField(worklossExpected); //WORKLOSS code is the same for all limits

		validateViewEndorsementCoveragesIsTheSameAsUpdateCoverage(softly, policyNumber, updateCoverageResponse);
		validatePIPInUI_pas15358(softly, findCoverage(pipSubCoveragesActual, CoverageInfo.MEDEXP_KS.getCode()));

		return pipSubCoveragesActual;
	}

	private void validatePIPSubCoveragesThatDoesntChange_pas15358(List<Coverage> pipSubCoveragesActual) {
		//these coverages are always the same
		Coverage rehabexpExpected = Coverage.create(CoverageInfo.REHABEXP_KS).disableCanChange();
		Coverage essenservExpected = Coverage.create(CoverageInfo.ESSENSERV_KS).disableCanChange();
		Coverage funexpExpected = Coverage.create(CoverageInfo.FUNEXP_KS).disableCanChange();
		Coverage survlossExpected = Coverage.create(CoverageInfo.SURVLOSS_KS).disableCanChange();

		assertThat(findCoverage(pipSubCoveragesActual, CoverageInfo.REHABEXP_KS.getCode())).isEqualToComparingFieldByField(rehabexpExpected);
		assertThat(findCoverage(pipSubCoveragesActual, CoverageInfo.ESSENSERV_KS.getCode())).isEqualToComparingFieldByField(essenservExpected);
		assertThat(findCoverage(pipSubCoveragesActual, CoverageInfo.FUNEXP_KS.getCode())).isEqualToComparingFieldByField(funexpExpected);
		assertThat(findCoverage(pipSubCoveragesActual, CoverageInfo.SURVLOSS_KS.getCode())).isEqualToComparingFieldByField(survlossExpected);
	}

	private void validatePIPSubCoveragesThatDoesntChange_pas15365(List<Coverage> pipSubCoveragesActual) {
		//these coverages are always the same
		Coverage incomelossExpected = Coverage.create(CoverageInfo.INCOMELOSS_OR).disableCanChange();
		Coverage essenservExpected = Coverage.create(CoverageInfo.ESSENSERV_OR).disableCanChange();
		Coverage funexpExpected = Coverage.create(CoverageInfo.FUNEXP_OR).disableCanChange();

		assertThat(findCoverage(pipSubCoveragesActual, CoverageInfo.INCOMELOSS_OR.getCode())).isEqualToComparingFieldByField(incomelossExpected);
		assertThat(findCoverage(pipSubCoveragesActual, CoverageInfo.ESSENSERV_OR.getCode())).isEqualToComparingFieldByField(essenservExpected);
		assertThat(findCoverage(pipSubCoveragesActual, CoverageInfo.FUNEXP_OR.getCode())).isEqualToComparingFieldByField(funexpExpected);
	}

	private void validatePIPSubCoveragesThatDoesntChange_pas15368(List<Coverage> pipSubCoveragesActual) {
		//these coverages are always the same
		Coverage housexpExpected = Coverage.create(CoverageInfo.HOUSEEXP_UT).disableCanChange();
		Coverage funexpExpected = Coverage.create(CoverageInfo.FUNEXP_UT).disableCanChange();
		Coverage survlossExpected = Coverage.create(CoverageInfo.SURVLOSS_UT).disableCanChange();

		assertThat(findCoverage(pipSubCoveragesActual, CoverageInfo.HOUSEEXP_UT.getCode())).isEqualToComparingFieldByField(housexpExpected);
		assertThat(findCoverage(pipSubCoveragesActual, CoverageInfo.FUNEXP_UT.getCode())).isEqualToComparingFieldByField(funexpExpected);
		assertThat(findCoverage(pipSubCoveragesActual, CoverageInfo.SURVLOSS_UT.getCode())).isEqualToComparingFieldByField(survlossExpected);
	}

	private void validatePIPInUI_pas15358(ETCSCoreSoftAssertions softly, Coverage medexpCoverage) {
		openPendedEndorsementInquiryAndNavigateToPC();

		softly.assertThat(premiumAndCoveragesTab.getPolicyCoverageDetailsValue(AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION.getLabel()))
				.isEqualTo(medexpCoverage.getCoverageLimitDisplay());

		premiumAndCoveragesTab.cancel();
	}

	private void validatePIPInUI_pas15365(ETCSCoreSoftAssertions softly, Coverage medexpCoverage, Coverage pipdedCoverage) {
		openPendedEndorsementInquiryAndNavigateToPC();

		softly.assertThat(premiumAndCoveragesTab.getPolicyCoverageDetailsValue(AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION.getLabel()))
				.isEqualTo(medexpCoverage.getCoverageLimitDisplay());
		softly.assertThat(premiumAndCoveragesTab.getPolicyCoverageDetailsValue(AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION_DEDUCTIBLE.getLabel()))
				.isEqualTo(pipdedCoverage.getCoverageLimitDisplay());

		premiumAndCoveragesTab.cancel();
	}

	private void validatePIPInUI_15368(ETCSCoreSoftAssertions softly, Coverage rejectWorkLossCoverage, Coverage medexpCoverage) {
		openPendedEndorsementInquiryAndNavigateToPC();

		softly.assertThat(premiumAndCoveragesTab.getPolicyCoverageDetailsValue(AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION.getLabel()))
				.isEqualTo(medexpCoverage.getCoverageLimitDisplay());
//		softly.assertThat(premiumAndCoveragesTab.getPolicyCoverageDetailsValue(AutoSSMetaData.PremiumAndCoveragesTab.REJECTION_OF_WORK_LOSS_BENEFIT.getLabel()))
//				.isEqualTo(rejectWorkLossCoverage.getCoverageLimitDisplay());//TODO-mstrazds:

		premiumAndCoveragesTab.cancel();
	}

	private void openPendedEndorsementInquiryAndNavigateToPC() {
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.quoteInquiry().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
	}

	private Vehicle getVehicleByVin(List<Vehicle> vehicleList, String vin) {
		return vehicleList.stream().filter(vehicle -> vin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null);
	}

	private VehicleCoverageInfo getVehicleCoverages(PolicyCoverageInfo policyCoverageInfo, String oid) {
		return policyCoverageInfo.vehicleLevelCoverages.stream().filter(vehicleCoverageInfo -> oid.equals(vehicleCoverageInfo.oid)).findFirst().orElse(null);
	}

	protected Coverage findCoverage(List<Coverage> coverageList, String coverageCd) {
		return coverageList.stream()
				.filter(coverage -> coverageCd.equals(coverage.getCoverageCd()))
				.findFirst()
				.orElse(null);
	}

	private void validateCustomEquipCoverageOrder_pas18624(ETCSCoreSoftAssertions softly, VehicleCoverageInfo vehicleCoverageInfo, Coverage custEquip) {
		Coverage collded = findCoverage(vehicleCoverageInfo.coverages, "COLLDED");
		int custEquipIndex = vehicleCoverageInfo.coverages.indexOf(custEquip);
		int colldedIndex = vehicleCoverageInfo.coverages.indexOf(collded);
		softly.assertThat(custEquipIndex).as("CUSTEQUIP should be displayed after COLLDED").isEqualTo(colldedIndex + 1);
	}

	protected void pas15265_UnderInsuredConversionCoverageCTBody(boolean testWithUimconv) {
		TestData td = getPolicyDefaultTD();
		String setUimconvValueUI;
		if (testWithUimconv) {
			setUimconvValueUI = "Yes";
		} else {
			setUimconvValueUI = "No";
		}
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName()
				, AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORIST_CONVERSION_COVERAGE.getLabel()), setUimconvValueUI);

		String policyNumber = openAppAndCreatePolicy(td);
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		//validate view endorsement coverages
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		Coverage uimconvCoverageActual = findCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.UIMCONV_CT.getCode());
		Coverage uimbCoverageActual = findCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.UMBI_CT_NO.getCode()); //code is the same in all cases

		Coverage uimbCoverageExpected;
		Coverage uimconvCoverageExpected;
		if (testWithUimconv) {
			uimbCoverageExpected = Coverage.create(CoverageInfo.UMBI_CT_YES);
			uimconvCoverageExpected = Coverage.create(CoverageInfo.UIMCONV_CT).disableCanChange().changeLimit(CoverageLimits.COV_TRUE);
		} else {
			uimbCoverageExpected = Coverage.create(CoverageInfo.UMBI_CT_NO);
			uimconvCoverageExpected = Coverage.create(CoverageInfo.UIMCONV_CT).disableCanChange();
		}

		assertThat(uimconvCoverageActual).isEqualToComparingFieldByField(uimconvCoverageExpected);
		assertThat(uimbCoverageActual).isEqualToComparingOnlyGivenFields(uimbCoverageExpected, "coverageCd", "coverageDescription", "availableLimits");
	}

    protected void pas20306_viewUpdateCoveragesUmpdCompCollBody(String state, PolicyType policyType) {
        TestData td = getPolicyDefaultTD();

		openAppAndCreatePolicy(td);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

        ViewVehicleResponse viewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
        String vin1 = td.getTestDataList("VehicleTab").get(0).getValue("VIN"); //PPA
		Vehicle veh1 = getVehicleByVin(viewVehicles.vehicleList, vin1);
        String vehicleOid = veh1.oid;

        String coverageCdComp = "COMPDED";
        String coverageCdColl = "COLLDED";
        String limitDisplayNoCov = "No Coverage";
        String availableLimitsChange1 = "-1";
        String availableLimitsChange2 = "500";

        //TC1
        assertSoftly(softly -> {
            helperMiniServices.createEndorsementWithCheck(policyNumber);

            // DXP COLL to -1
            PolicyCoverageInfo updateCoverageResponse = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, vehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdColl, availableLimitsChange1), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

			Coverage umbi = findCoverage(updateCoverageResponse.policyCoverages, CoverageInfo.UMBI.getCode());
            Coverage umbiExpected = Coverage.create(CoverageInfo.UMBI_UT_100_300).disableCanChange();
            softly.assertThat(umbi).isEqualToComparingFieldByField(umbiExpected);

			Coverage uimbi = findCoverage(updateCoverageResponse.policyCoverages, CoverageInfo.UIMBI.getCode());
            Coverage uimbiExpected = Coverage.create(CoverageInfo.UIMBI_UT_100_300).disableCanChange();
            softly.assertThat(uimbi).isEqualToComparingFieldByField(uimbiExpected);

            // UMPD present at 3500
			Coverage umpd = findCoverage(updateCoverageResponse.vehicleLevelCoverages.get(0).coverages, CoverageInfo.UMPD.getCode());
			Coverage umpdExpected = Coverage.create(CoverageInfo.UMPD_UT_3500);
            softly.assertThat(umpd).isEqualToComparingFieldByField(umpdExpected);

			validateViewEndorsementCoveragesIsTheSameAsUpdateCoverage(softly, policyNumber, updateCoverageResponse);

	        //Check transactionHistory
	        validateVehicleLevelCoverageChangeLog(policyNumber, vehicleOid, umpdExpected);

	        SearchPage.openPolicy(policyNumber);
	        openPendedEndorsementInquiryAndNavigateToPC();
			softly.assertThat(premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel())).isEqualTo("");

			HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());

        });

        // TC1b
		assertSoftly(softly -> {
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			// DXP COMP to -1
			PolicyCoverageInfo updateCoverageResponse = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, vehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdComp, availableLimitsChange1), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

			Coverage umbi = findCoverage(updateCoverageResponse.policyCoverages, CoverageInfo.UMBI.getCode());
			Coverage umbiExpected = Coverage.create(CoverageInfo.UMBI_UT_100_300).disableCanChange();
			softly.assertThat(umbi).isEqualToComparingFieldByField(umbiExpected);

			Coverage uimbi = findCoverage(updateCoverageResponse.policyCoverages, CoverageInfo.UIMBI.getCode());
			Coverage uimbiExpected = Coverage.create(CoverageInfo.UIMBI_UT_100_300).disableCanChange();
			softly.assertThat(uimbi).isEqualToComparingFieldByField(uimbiExpected);

			// UMPD present at 3500
			Coverage umpd = findCoverage(updateCoverageResponse.vehicleLevelCoverages.get(0).coverages, CoverageInfo.UMPD.getCode());
			Coverage umpdExpected = Coverage.create(CoverageInfo.UMPD_UT_3500);
			softly.assertThat(umpd).isEqualToComparingFieldByField(umpdExpected);

			validateViewEndorsementCoveragesIsTheSameAsUpdateCoverage(softly, policyNumber, updateCoverageResponse);

			//Check transactionHistory
			validateVehicleLevelCoverageChangeLog(policyNumber, vehicleOid, umpdExpected);

			SearchPage.openPolicy(policyNumber);
			openPendedEndorsementInquiryAndNavigateToPC();
			softly.assertThat(premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel())).isEqualTo("");

			HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());

		});

        // TC3
        assertSoftly(softly -> {
            helperMiniServices.createEndorsementWithCheck(policyNumber);
            SearchPage.openPolicy(policyNumber);
            PolicySummaryPage.buttonPendedEndorsement.click();
            policy.dataGather().start();
            NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
            premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), limitDisplayNoCov);
            premiumAndCoveragesTab.saveAndExit();

            // DXP COLL to 500
            PolicyCoverageInfo updateCoverageResponse = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, vehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdColl, availableLimitsChange2), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

			Coverage umbi = findCoverage(updateCoverageResponse.policyCoverages, CoverageInfo.UMBI.getCode());
            Coverage umbiExpected = Coverage.create(CoverageInfo.UMBI_UT_100_300).disableCanChange();
            softly.assertThat(umbi).isEqualToComparingFieldByField(umbiExpected);

			Coverage uimbi = findCoverage(updateCoverageResponse.policyCoverages, CoverageInfo.UIMBI.getCode());
            Coverage uimbiExpected = Coverage.create(CoverageInfo.UIMBI_UT_100_300).disableCanChange();
            softly.assertThat(uimbi).isEqualToComparingFieldByField(uimbiExpected);

			Coverage umpd = findCoverage(updateCoverageResponse.vehicleLevelCoverages.get(0).coverages, CoverageInfo.UMPD.getCode());
            Coverage umpdExpected = Coverage.create(CoverageInfo.UMPD_UT_0).disableCanChange().disableCustomerDisplay();
            softly.assertThat(umpd).isEqualToComparingFieldByField(umpdExpected);

			validateViewEndorsementCoveragesIsTheSameAsUpdateCoverage(softly, policyNumber, updateCoverageResponse);

			SearchPage.openPolicy(policyNumber);
			openPendedEndorsementInquiryAndNavigateToPC();
			softly.assertThat(premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel())).isEqualTo("500");

			HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());
        });

        //TC2
        assertSoftly(softly -> {
            helperMiniServices.createEndorsementWithCheck(policyNumber);
            SearchPage.openPolicy(policyNumber);
            PolicySummaryPage.buttonPendedEndorsement.click();
            policy.dataGather().start();
            NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
            premiumAndCoveragesTab.setPolicyCoverageDetailsValue(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY.getLabel(), limitDisplayNoCov);
            premiumAndCoveragesTab.setPolicyCoverageDetailsValue(AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel(), limitDisplayNoCov);
            premiumAndCoveragesTab.saveAndExit();

            // DXP COLL to -1
            PolicyCoverageInfo updateCoverageResponse = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, vehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdColl, availableLimitsChange1), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

			Coverage umbi = findCoverage(updateCoverageResponse.policyCoverages, CoverageInfo.UMBI.getCode());
            Coverage umbiExpected = Coverage.create(CoverageInfo.UMBI_UT_00).disableCanChange();
            softly.assertThat(umbi).isEqualToComparingFieldByField(umbiExpected);

			Coverage uimbi = findCoverage(updateCoverageResponse.policyCoverages, CoverageInfo.UIMBI.getCode());
            Coverage uimbiExpected = Coverage.create(CoverageInfo.UIMBI_UT_00).disableCanChange();
            softly.assertThat(uimbi).isEqualToComparingFieldByField(uimbiExpected);

			Coverage umpd = findCoverage(updateCoverageResponse.vehicleLevelCoverages.get(0).coverages, CoverageInfo.UMPD.getCode());
            Coverage umpdExpected = Coverage.create(CoverageInfo.UMPD_UT_0).disableCanChange().disableCustomerDisplay();
            softly.assertThat(umpd).isEqualToComparingFieldByField(umpdExpected);

			validateViewEndorsementCoveragesIsTheSameAsUpdateCoverage(softly, policyNumber, updateCoverageResponse);

	        //Check transactionHistory
	        validateVehicleLevelCoverageChangeLog(policyNumber, vehicleOid, umpdExpected);

			SearchPage.openPolicy(policyNumber);
			openPendedEndorsementInquiryAndNavigateToPC();
			softly.assertThat(premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel())).isEqualTo("");

            HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());
        });

		//TC2b
		assertSoftly(softly -> {
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.dataGather().start();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			premiumAndCoveragesTab.setPolicyCoverageDetailsValue(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY.getLabel(), limitDisplayNoCov);
			premiumAndCoveragesTab.setPolicyCoverageDetailsValue(AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel(), limitDisplayNoCov);
			premiumAndCoveragesTab.saveAndExit();

			// DXP COMP to -1
			PolicyCoverageInfo updateCoverageResponse = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, vehicleOid, DXPRequestFactory.createUpdateCoverageRequest(coverageCdComp, availableLimitsChange1), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

			Coverage umbi = findCoverage(updateCoverageResponse.policyCoverages, CoverageInfo.UMBI.getCode());
			Coverage umbiExpected = Coverage.create(CoverageInfo.UMBI_UT_00).disableCanChange();
			softly.assertThat(umbi).isEqualToComparingFieldByField(umbiExpected);

			Coverage uimbi = findCoverage(updateCoverageResponse.policyCoverages, CoverageInfo.UIMBI.getCode());
			Coverage uimbiExpected = Coverage.create(CoverageInfo.UIMBI_UT_00).disableCanChange();
			softly.assertThat(uimbi).isEqualToComparingFieldByField(uimbiExpected);

			Coverage umpd = findCoverage(updateCoverageResponse.vehicleLevelCoverages.get(0).coverages, CoverageInfo.UMPD.getCode());
			Coverage umpdExpected = Coverage.create(CoverageInfo.UMPD_UT_0).disableCanChange().disableCustomerDisplay();
			softly.assertThat(umpd).isEqualToComparingFieldByField(umpdExpected);

			validateViewEndorsementCoveragesIsTheSameAsUpdateCoverage(softly, policyNumber, updateCoverageResponse);

			//Check transactionHistory
			validateVehicleLevelCoverageChangeLog(policyNumber, vehicleOid, umpdExpected);

			SearchPage.openPolicy(policyNumber);
			openPendedEndorsementInquiryAndNavigateToPC();
			softly.assertThat(premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(1, AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel())).isEqualTo("");

			helperMiniServices.endorsementRateAndBind(policyNumber);
		});

    }

	private void assertThatOnlyOneInstanceOfPolicyLevelCoverages(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			//make sure that no Policy Level coverages are missed
			validateCountOfPolicyLevelCoverages(coverageResponse, softly);

			List<Coverage> filteredPolicyCoverageResponseBI = coverageResponse.policyCoverages.stream().filter(cov -> "BI".equals(cov.getCoverageCd())).collect(Collectors.toList());
			softly.assertThat(filteredPolicyCoverageResponseBI.size()).isEqualTo(1);

			List<Coverage> filteredPolicyCoverageResponsePD = coverageResponse.policyCoverages.stream().filter(cov -> "PD".equals(cov.getCoverageCd())).collect(Collectors.toList());
			softly.assertThat(filteredPolicyCoverageResponsePD.size()).isEqualTo(1);

			if (!"NY".contains(getState())) {
				List<Coverage> filteredPolicyCoverageResponseUMBI = coverageResponse.policyCoverages.stream().filter(cov -> "UMBI".equals(cov.getCoverageCd())).collect(Collectors.toList());
				softly.assertThat(filteredPolicyCoverageResponseUMBI.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseUIMBI = coverageResponse.policyCoverages.stream().filter(cov -> "UIMBI".equals(cov.getCoverageCd())).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseUIMBI.isEmpty()) {
				softly.assertThat(filteredPolicyCoverageResponseUIMBI.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseUMPD = coverageResponse.policyCoverages.stream().filter(cov -> "UMPD".equals(cov.getCoverageCd())).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseUMPD.isEmpty()) {
				softly.assertThat(filteredPolicyCoverageResponseUMPD.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseMEDPM = coverageResponse.policyCoverages.stream().filter(cov -> "MEDPM".equals(cov.getCoverageCd())).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseMEDPM.isEmpty()) {
				softly.assertThat(filteredPolicyCoverageResponseMEDPM.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseIL = coverageResponse.policyCoverages.stream().filter(cov -> "IL".equals(cov.getCoverageCd())).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseIL.isEmpty()) {
				softly.assertThat(filteredPolicyCoverageResponseIL.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponsePIP = coverageResponse.policyCoverages.stream().filter(cov -> "PIP".equals(cov.getCoverageCd())).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponsePIP.isEmpty()) {
				softly.assertThat(filteredPolicyCoverageResponsePIP.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseBPIP = coverageResponse.policyCoverages.stream().filter(cov -> "BPIP".equals(cov.getCoverageCd())).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseBPIP.isEmpty()) {
				softly.assertThat(filteredPolicyCoverageResponseBPIP.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseADDPIP = coverageResponse.policyCoverages.stream().filter(cov -> "ADDPIP".equals(cov.getCoverageCd())).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseADDPIP.isEmpty()) {
				softly.assertThat(filteredPolicyCoverageResponseADDPIP.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseGPIP = coverageResponse.policyCoverages.stream().filter(cov -> "GPIP".equals(cov.getCoverageCd())).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseGPIP.isEmpty()) {
				softly.assertThat(filteredPolicyCoverageResponseGPIP.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseUMSU = coverageResponse.policyCoverages.stream().filter(cov -> "Underinsured Motorist Stacked/Unstacked".equals(cov.getCoverageDescription())).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseUMSU.isEmpty()) {//TODO-mstrazds: PAS-16038 View Coverages - UIM/UIM Stacked/Unstacked - PA
				softly.assertThat(filteredPolicyCoverageResponseUMSU.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseFPB = coverageResponse.policyCoverages.stream().filter(cov -> "First Party Benefits".equals(cov.getCoverageDescription())).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseFPB.isEmpty()) { //TODO-mstrazds: PAS-15350 View Coverages - First Party Benefits and Pennsylvania
				softly.assertThat(filteredPolicyCoverageResponseFPB.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseTH = coverageResponse.policyCoverages.stream().filter(cov -> "Tort Threshold".equals(cov.getCoverageDescription())).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseTH.isEmpty()) {//TODO-mstrazds: PAS-15304 View coverages - Tort - PA
				softly.assertThat(filteredPolicyCoverageResponseTH.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponseRoWLB = coverageResponse.policyCoverages.stream().filter(cov -> "Rejection of Work Loss Benefit (Y/N)".equals(cov.getCoverageDescription())).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseRoWLB.isEmpty()) {//TODO-mstrazds: PAS-16040 View Coverages - Rejection of Work Loss Benefit - NY
				softly.assertThat(filteredPolicyCoverageResponseRoWLB.size()).isEqualTo(1);
			}

			List<Coverage> filteredPolicyCoverageResponsePIPD = coverageResponse.policyCoverages.stream().filter(cov -> "Personal Injury Protection Deductible".equals(cov.getCoverageDescription())).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponsePIPD.isEmpty()) {//TODO-mstrazds: US needed
				softly.assertThat(filteredPolicyCoverageResponsePIPD.size()).isEqualTo(1);
			}

			if ("NY".contains(getState())) {
				List<Coverage> filteredPolicyCoverageResponseUMSUMny = coverageResponse.policyCoverages.stream().filter(cov -> "UM/SUM".equals(cov.getCoverageCd())).collect(Collectors.toList());
				softly.assertThat(filteredPolicyCoverageResponseUMSUMny.size()).isEqualTo(1);

				List<Coverage> filteredPolicyCoverageResponseSSLny = coverageResponse.policyCoverages.stream().filter(cov -> "Supplemental Spousal Liability".equals(cov.getCoverageDescription())).collect(Collectors.toList());
				softly.assertThat(filteredPolicyCoverageResponseSSLny.size()).isEqualTo(0);//TODO-mstrazds: PAS-15309 View Coverage - Supplementary UM/UIM - New York

				List<Coverage> filteredPolicyCoverageResponsePIPny = coverageResponse.policyCoverages.stream().filter(cov -> "Personal Injury Protection".equals(cov.getCoverageDescription())).collect(Collectors.toList());
				softly.assertThat(filteredPolicyCoverageResponsePIPny.size()).isEqualTo(0);//TODO-mstrazds: PAS-15363 View Coverages - PIP - New York (Only for Ny or also for othr states?)

				List<Coverage> filteredPolicyCoverageResponseAPIPny = coverageResponse.policyCoverages.stream().filter(cov -> "APIP".equals(cov.getCoverageCd())).collect(Collectors.toList());
				softly.assertThat(filteredPolicyCoverageResponseAPIPny.size()).isEqualTo(1); //TODO-mstrazds: PAS-15363 View Coverages - PIP - New York (is this correct coverage or it really is about PIP)

				// Possible issue that this coverage will be missing. It is possible, that it should be displayed only for one of the vehicles.
				List<Coverage> filteredPolicyCoverageResponseMEEny = coverageResponse.policyCoverages.stream().filter(cov -> "Medical Expense Elimination".equals(cov.getCoverageDescription())).collect(Collectors.toList());
				softly.assertThat(filteredPolicyCoverageResponseMEEny.size()).isEqualTo(0);//TODO-mstrazds: PAS-16042 View Coverages - Medical Expenses Elimination - New York

				List<Coverage> filteredPolicyCoverageResponseOBELny = coverageResponse.policyCoverages.stream().filter(cov -> "OBEL".equals(cov.getCoverageCd())).collect(Collectors.toList());
				softly.assertThat(filteredPolicyCoverageResponseOBELny.size()).isEqualTo(1);
			}

			// Possible issue that this coverage will be missing. It is possible, that it should be displayed only for one of the vehicles. Seems this coverage is only for IN.//TODO-mstrazds:US needed
			List<Coverage> filteredPolicyCoverageResponseUMPDD = coverageResponse.policyCoverages.stream().filter(cov -> "Uninsured Motorist Property Damage Deductible".equals(cov.getCoverageDescription())).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseUMPDD.isEmpty()) {
				softly.assertThat(filteredPolicyCoverageResponseUMPDD.size()).isEqualTo(1);
			}

			//For CT this coverage is without coverageCD//TODO-mstrazds: PAS-15265 View Coverages - UM Conversion Coverage - CT
			List<Coverage> filteredPolicyCoverageResponseUMCC = coverageResponse.policyCoverages.stream().filter(cov -> "Underinsured Motorist Conversion Coverage".equals(cov.getCoverageDescription())).collect(Collectors.toList());
			if (!filteredPolicyCoverageResponseUMCC.isEmpty()) {
				softly.assertThat(filteredPolicyCoverageResponseUMCC.size()).isEqualTo(1);
			}

		});
	}

	private void validateTrailerCoverages(PolicyCoverageInfo viewPolicyCoveragesByVehicleResponse) {
		assertSoftly(softly -> {
			//make sure that no Vehicle Level coverages are missed
			if ("NV, OR, UT, DE, OH, KY".contains(getState())) {
				softly.assertThat(viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.size()).isEqualTo(9);
			} else {
				softly.assertThat(viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.size()).isEqualTo(8);
			}

			Coverage filteredPolicyCoverageResponseCOMPDED = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COMPDED".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseCOMPDED.getCanChangeCoverage()).isTrue();
			softly.assertThat(filteredPolicyCoverageResponseCOMPDED.getCustomerDisplayed()).isTrue();

			Coverage filteredPolicyCoverageResponseCOLLDED = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COLLDED".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseCOLLDED.getCanChangeCoverage()).isTrue();
			softly.assertThat(filteredPolicyCoverageResponseCOLLDED.getCustomerDisplayed()).isTrue();

			Coverage filteredPolicyCoverageResponseGLASS = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "GLASS".equals(cov.getCoverageCd())).findFirst().orElse(null);
			if ("KY".equals(getState())) {
				softly.assertThat(filteredPolicyCoverageResponseGLASS.getCanChangeCoverage()).isNull(); //TODO-mstrazds: PAS-15329 View Coverages - Full Safety Glass - Kentucky
				softly.assertThat(filteredPolicyCoverageResponseGLASS.getCustomerDisplayed()).isFalse();
			} else {
				softly.assertThat(filteredPolicyCoverageResponseGLASS.getCanChangeCoverage()).isFalse();
				softly.assertThat(filteredPolicyCoverageResponseGLASS.getCustomerDisplayed()).isFalse();
			}

			Coverage filteredPolicyCoverageResponseLOAN = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "LOAN".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseLOAN.getCanChangeCoverage()).isFalse();
			softly.assertThat(filteredPolicyCoverageResponseLOAN.getCustomerDisplayed()).isFalse();

			Coverage filteredPolicyCoverageResponseRREIM = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "RREIM".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseRREIM.getCanChangeCoverage()).isFalse();
			softly.assertThat(filteredPolicyCoverageResponseRREIM.getCustomerDisplayed()).isFalse();

			Coverage filteredPolicyCoverageResponseTOWINGLABOR = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "TOWINGLABOR".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseTOWINGLABOR.getCanChangeCoverage()).isFalse();
			softly.assertThat(filteredPolicyCoverageResponseTOWINGLABOR.getCustomerDisplayed()).isFalse();

			Coverage filteredPolicyCoverageResponseSPECEQUIP = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "SPECEQUIP".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseSPECEQUIP.getCanChangeCoverage()).isFalse();
			softly.assertThat(filteredPolicyCoverageResponseSPECEQUIP.getCustomerDisplayed()).isFalse();

			Coverage filteredPolicyCoverageResponseNEWCAR = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "NEWCAR".equals(cov.getCoverageCd())).findFirst().orElse(null);
			softly.assertThat(filteredPolicyCoverageResponseNEWCAR.getCanChangeCoverage()).isFalse();
			softly.assertThat(filteredPolicyCoverageResponseNEWCAR.getCustomerDisplayed()).isFalse();

			//do not have this coverage in response anymore. Karen Yifru doesn't care about it.
			//			Coverage filteredPolicyCoverageResponseWL = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "WL".equals(cov.getCoverageCd())).findFirst().orElse(null);
			//			softly.assertThat(filteredPolicyCoverageResponseWL.getCanChangeCoverage()).isFalse();
			//			softly.assertThat(filteredPolicyCoverageResponseWL.customerDisplayed).isFalse();

			//UMPD is Vehicle level coverage for NV, OR, UT, OH //TODO-mstrazds:PAS-15496 View Coverages - UMPD Colorado, NV, Ohio, Utah // PAS-16112 View Coverages - UMPD - Oregon
			if ("NV, OR, UT, OH".contains(getState())) {
				Coverage filteredPolicyCoverageResponseUMPD = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "UMPD".equals(cov.getCoverageCd())).findFirst().orElse(null);
				softly.assertThat(filteredPolicyCoverageResponseUMPD).isNotNull();
				//softly.assertThat(filteredPolicyCoverageResponseUMPD.getCanChangeCoverage()).isFalse(); //not clear yet what value should be
				//softly.assertThat(filteredPolicyCoverageResponseUMPD.customerDisplayed).isFalse(); //not clear yet what value should be
			}

			//MEDPM ("Medical Payments") is Vehicle level coverage for KY, DE
			Coverage filteredPolicyCoverageResponseMEDPM = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "MEDPM".equals(cov.getCoverageCd())).findFirst().orElse(null);
			if ("KY, DE".contains(getState())) {
				softly.assertThat(filteredPolicyCoverageResponseMEDPM).isNotNull();
				softly.assertThat(filteredPolicyCoverageResponseMEDPM.getCanChangeCoverage()).isNull(); //TODO-mstrazds: US needed
				softly.assertThat(filteredPolicyCoverageResponseMEDPM.getCustomerDisplayed()).isFalse();
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
			coverageXproperties(softly, coverageResponse1, coverageResponse2.getCoverageCd(), coverageResponse2.getCoverageDescription(), coverageResponse2.getCoverageLimit(), coverageResponse2.getCoverageLimitDisplay(), coverageResponse2.getCoverageType(), coverageResponse2.getCustomerDisplayed(), coverageResponse2.getCanChangeCoverage());
		});
	}

	void vehicleCoverageComparisonByCoverageCdNotEqual(PolicyCoverageInfo response1, PolicyCoverageInfo response2, String coverageCd) {
		assertSoftly(softly -> {
			Coverage coverageResponse1 = getVehicleCoverageDetails(response1, coverageCd);
			Coverage coverageResponse2 = getVehicleCoverageDetails(response2, coverageCd);
			softly.assertThat(coverageResponse1.getCoverageCd()).isEqualTo(coverageResponse2.getCoverageCd());
			softly.assertThat(coverageResponse1.getCoverageDescription()).isEqualTo(coverageResponse2.getCoverageDescription());
			softly.assertThat(coverageResponse1.getCoverageLimit()).isNotEqualTo(coverageResponse2.getCoverageLimit());
			if (!"SPECEQUIP".equals(coverageCd)) {
				softly.assertThat(coverageResponse1.getCoverageLimitDisplay()).isNotEqualTo(coverageResponse2.getCoverageLimitDisplay());
			}
			softly.assertThat(coverageResponse1.getCoverageType()).isEqualTo(coverageResponse2.getCoverageType());
			softly.assertThat(coverageResponse1.getCustomerDisplayed()).isEqualTo(coverageResponse2.getCustomerDisplayed());
			softly.assertThat(coverageResponse1.getCanChangeCoverage()).isEqualTo(coverageResponse2.getCanChangeCoverage());
		});
	}

	void policyCoverageComparisonByCoverageCd(PolicyCoverageInfo response1, PolicyCoverageInfo response2, String coverageCd) {
		assertSoftly(softly -> {
			Coverage coverageResponse1 = getPolicyCoverageDetails(response1, coverageCd);
			Coverage coverageResponse2 = getPolicyCoverageDetails(response2, coverageCd);
			coverageXproperties(softly, coverageResponse1, coverageResponse2.getCoverageCd(), coverageResponse2.getCoverageDescription(), coverageResponse2.getCoverageLimit(), coverageResponse2.getCoverageLimitDisplay(), coverageResponse2.getCoverageType(), coverageResponse2.getCustomerDisplayed(), coverageResponse2.getCanChangeCoverage());
		});
	}

	Coverage getVehicleCoverageDetails(PolicyCoverageInfo policyCoverageResponseReplacedLeasedVeh, String coverageCode) {
		return policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> coverageCode.equals(attribute.getCoverageCd())).findFirst().orElse(null);
	}

	private Coverage getPolicyCoverageDetails(PolicyCoverageInfo policyCoverageResponseReplacedLeasedVeh, String coverageCode) {

		return policyCoverageResponseReplacedLeasedVeh.policyCoverages.stream().filter(attribute -> coverageCode.equals(attribute.getCoverageCd())).findFirst().orElse(null);
	}

	private void assertAvailableCoverageLimitForPD(PolicyCoverageInfo coverageResponse) {
		assertSoftly(softly -> {
			List<CoverageLimit> availableLimitsPD = coverageResponse.policyCoverages.get(1).getAvailableLimits();

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

	private void validateAvailableCoverageLimit(Coverage coverage, int availableLimitIndex, String coverageLimitExpected, String coverageLimitDisplayExpected) {
		assertThat(coverage.getAvailableLimits().get(availableLimitIndex).coverageLimit)
				.as(coverage.getCoverageCd() + " expected coverageLimit at index " + availableLimitIndex + ": " + coverageLimitExpected).isEqualTo(coverageLimitExpected);
		assertThat(coverage.getAvailableLimits().get(availableLimitIndex).coverageLimitDisplay)
				.as(coverage.getCoverageCd() + " expected coverageLimitDisplay: " + coverageLimitDisplayExpected).isEqualTo(coverageLimitDisplayExpected);
	}

	private void goToPasAndChangeUMPD(String policyNumber) {

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		premiumAndCoveragesTab.setPolicyCoverageDetailsValue(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel(), "No Coverage");
	}

	private void validateVehicleLevelCoverageChangeLog(String policyNumber, String vehicleOid, Coverage coverageExpected) {
		HelperCommon.endorsementRate(policyNumber, Response.Status.OK.getStatusCode());
		ComparablePolicy changeLogResponse = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		assertThat(changeLogResponse.vehicles.get(vehicleOid).coverages.get(coverageExpected.getCoverageCd()).data).isEqualToIgnoringGivenFields(coverageExpected, "availableLimits");
	}

	private PolicyCoverageInfo updateCoverage(String policyNumber, String coverageCd, String coverageLimit) {
		UpdateCoverageRequest updateCoverageRequest = DXPRequestFactory.createUpdateCoverageRequest(coverageCd, coverageLimit);
		return HelperCommon.updateEndorsementCoverage(policyNumber, updateCoverageRequest, PolicyCoverageInfo.class);
	}

	private PolicyCoverageInfo updateTORTCoverage(String policyNumber, List<String> driverOidList) {
		UpdateCoverageRequest updateCoverageRequest = DXPRequestFactory.createUpdateCoverageRequest("TORT", "true", driverOidList);
		return HelperCommon.updateEndorsementCoverage(policyNumber, updateCoverageRequest, PolicyCoverageInfo.class);
	}

}



