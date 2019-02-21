package aaa.modules.docgen.auto_ss;

import static aaa.main.enums.DocGenEnum.Documents.AAIQAZ;
import static aaa.main.enums.DocGenEnum.Documents.AATSXX;
import static org.openqa.selenium.By.id;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.utils.StateList;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.Button;

public class TestScenario5 extends AutoSSBaseTest {
	private String policyNumber;
	private String plcyTotSopPrem;
	private String plcyTotPrem;
	private String dnPayReq;
	private String plcyEffDt;
	private List<TestData> vehClsnDed = new ArrayList<TestData>();
	private List<TestData> vehCompDed = new ArrayList<TestData>();
	private List<TestData> vehBdyInjPrem = new ArrayList<TestData>();
	private List<TestData> vehPDPrem = new ArrayList<TestData>();
	private List<TestData> vehUMPrem = new ArrayList<TestData>();
	private List<TestData> vehUIMBPrem = new ArrayList<TestData>();
	private List<TestData> vehMPPrem = new ArrayList<TestData>();
	private List<TestData> vehClsnPrem = new ArrayList<TestData>();
	private List<TestData> vehCompPrem = new ArrayList<TestData>();
	private List<TestData> vehNwAddPrtcDed = new ArrayList<TestData>();
	private List<TestData> vehRntlReimbsDed = new ArrayList<TestData>();
	private List<TestData> vehTwgLbrDed = new ArrayList<TestData>();
	private List<TestData> vehLnPrtcDed = new ArrayList<TestData>();
	private List<TestData> plcyMpEaPers = new ArrayList<TestData>();
	private List<TestData> plcyPdEaOcc = new ArrayList<TestData>();
	private List<TestData> vehRntlReimbsPrem = new ArrayList<TestData>();
	private List<TestData> vehTwgLbrPrem = new ArrayList<TestData>();
	private List<TestData> vehSpclEqpmtDed = new ArrayList<TestData>();
	private List<TestData> vehSpclEqpmtPrem = new ArrayList<TestData>();
	private List<TestData> vehTotPrem = new ArrayList<TestData>();
	private Button buttongenerate = new Button(id("policyDataGatherForm:generate_link"));
	private PremiumAndCoveragesTab premiumAndCoveragesTab = policy.getDefaultView().getTab(PremiumAndCoveragesTab.class);

	/**
	 * @author Lina Li
	 * @name Verify the documents generated during quote
	 * @scenario
	 * 1. Create a active quote with 1NI/2DR/1V 
	 * 2. At least one of the drivers should have the age less than or equal to 18 
	 * 3. The License status of the teenage driver should be 'Licensed(US)'
	 * 4. Calculate premium and navigate to Documents & Bind page 
	 * 5. Generate the form AATSXX and AAIQXX
	 * @details
	 */

	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testDocGenScenario05(@Optional("") String state) {
		DocGenHelper.checkPasDocEnabled(getState(), getPolicyType());
		mainApp().open();

		createCustomerIndividual();
		TestData tdpolicy = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks());
		policy.initiate();
		policy.getDefaultView().fillUpTo(tdpolicy, PremiumAndCoveragesTab.class, true);

		storeCoveragesData();
		premiumAndCoveragesTab.submitTab();

		policy.getDefaultView().fillFromTo(tdpolicy, DriverActivityReportsTab.class, DocumentsAndBindTab.class, true);
		buttongenerate.click();
		WebDriverHelper.switchToDefault();
		Tab.buttonSaveAndExit.click();
		policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		plcyEffDt = DocGenHelper.convertToZonedDateTime(PolicySummaryPage.getEffectiveDate());
		log.info("Create the quote" + policyNumber);

		//		Verify the document AAIQAZ,AATSXX
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
			DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, AAIQAZ, AATSXX).verify.mapping(getTestSpecificTD("TestData_Verification")
							.adjust(TestData.makeKeyPath("AAIQAZ", "form", "PlcyNum", "TextField"), policyNumber)
							.adjust(TestData.makeKeyPath("AAIQAZ", "form", "PlcyEffDt", "DateTimeField"), plcyEffDt)
							.adjust(TestData.makeKeyPath("AAIQAZ", "PaymentDetails", "PlcyTotPrem", "TextField"), plcyTotPrem)
							.adjust(TestData.makeKeyPath("AAIQAZ", "PaymentDetails", "DnPayReq", "TextField"), dnPayReq)
							.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "PlcyTotSopPrem", "TextField"), plcyTotSopPrem)
							.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "PlcyMpEaPers"), plcyMpEaPers)
							.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "PlcyPdEaOcc"), plcyPdEaOcc)
							.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "VehSpclEqpmtDed"), vehSpclEqpmtDed)
							.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "VehClsnDed"), vehClsnDed)
							.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "VehCompDed"), vehCompDed)
							.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "VehNwAddPrtcDed"), vehNwAddPrtcDed)
							.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "VehTwgLbrDed"), vehTwgLbrDed)
							.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "VehLnPrtcDed"), vehLnPrtcDed)
							.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "VehRntlReimbsDed"), vehRntlReimbsDed)
							.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "VehBdyInjPrem"), vehBdyInjPrem)
							.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "VehPDPrem"), vehPDPrem)
							.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "VehUMPrem"), vehUMPrem)
							.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "VehUIMBPrem"), vehUIMBPrem)
							.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "VehMPPrem"), vehMPPrem)
							.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "VehCompPrem"), vehCompPrem)
							.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "VehClsnPrem"), vehClsnPrem)
							.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "VehRntlReimbsPrem"), vehRntlReimbsPrem)
							.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "VehTwgLbrPrem"), vehTwgLbrPrem)
							.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "VehTotPrem"), vehTotPrem),
					//				.adjust(TestData.makeKeyPath("AAIQAZ", "CoverageDetails", "VehSpclEqpmtPrem"), vehSpclEqpmtPrem)
					policyNumber, softly);
		softly.close();
	}

	private void storeCoveragesData() {
		for (TestData td : premiumAndCoveragesTab.getRatingDetailsVehiclesData()) {
			vehClsnDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Collision Deductible"))));
			vehCompDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Comprehensive Deductible"))));
			vehNwAddPrtcDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("New Car Added Protection Coverage"))));
			vehRntlReimbsDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Rental Reimbursement Limit"))));
			vehTwgLbrDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Towing and Labor Coverage"))));
			vehLnPrtcDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Vehicle Loan/Lease Coverage"))));
			plcyMpEaPers.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Medical Payments"))));
			plcyPdEaOcc.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Property Damage Liability"))));
			vehSpclEqpmtDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Special Equipment Limit"))));
		}

		PremiumAndCoveragesTab.RatingDetailsView.close();

		for (TestData td : premiumAndCoveragesTab.getTermPremiumByVehicleData()) {
			vehBdyInjPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Bodily Injury Liability"))));
			vehPDPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Property Damage Liability"))));
			vehUMPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Uninsured Motorists Bodily Injury"))));
			vehUIMBPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Underinsured Motorists Bodily Injury"))));
			vehMPPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Medical Payments"))));
			vehClsnPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Collision Deductible"))));
			vehCompPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Comprehensive Deductible"))));
			vehRntlReimbsPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Rental Reimbursement"))));
			vehTwgLbrPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Towing and Labor Coverage"))));
			vehSpclEqpmtPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Special Equipment Coverage"))));
			vehTotPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Total Vehicle Term Premium"))));
		}

		plcyTotSopPrem = formatValue(PremiumAndCoveragesTab.totalTermPremium.getValue());
		plcyTotPrem = formatValue(PremiumAndCoveragesTab.totalTermPremium.getValue());
		dnPayReq = plcyTotPrem + "(100%)";
	}

	private String formatValue(String value) {
		return value.contains("No Coverage") ? "0.00" : new Dollar(value.replace("\n", "")).toString().replace("$", "").replace(",", "");
	}
}
