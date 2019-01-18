package aaa.modules.docgen.auto_ss;

import static aaa.main.enums.DocGenEnum.Documents.*;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.mortbay.log.Log;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.TextBox;

public class TestScenario2 extends AutoSSBaseTest {

	private String policyNumber;
	private LocalDateTime policyExpirationDate;
	private LocalDateTime policyExpirationDate_CurrentTerm;
	private LocalDateTime policyEffectiveDate;
	private String termEffDt;
	private String termExprDt;
	private String plcyEffDt;
	private String plcyExprDt;
	private String endrEffDt;
	private String termExprDtPA;
	private PremiumAndCoveragesTab premiumAndCoveragesTab = policy.getDefaultView().getTab(PremiumAndCoveragesTab.class);
	private List<TestData> vehClsnDed = new ArrayList<TestData>();
	private List<TestData> vehCompDed = new ArrayList<TestData>();
	private List<TestData> vehBdyInjPrem = new ArrayList<TestData>();
	private List<TestData> vehPDPrem = new ArrayList<TestData>();
	private List<TestData> vehUMPrem = new ArrayList<TestData>();
	private List<TestData> vehUIMBPrem = new ArrayList<TestData>();
	private List<TestData> vehUMUIMBPrem = new ArrayList<TestData>();
	private List<TestData> vehUMPDPrem = new ArrayList<TestData>();
	private List<TestData> vehMPPrem = new ArrayList<TestData>();
	private List<TestData> vehClsnPrem = new ArrayList<TestData>();
	private List<TestData> vehCompPrem = new ArrayList<TestData>();
	private List<TestData> vehSftyGlsDed = new ArrayList<TestData>();
	private List<TestData> vehNwAddPrtcDed = new ArrayList<TestData>();
	private List<TestData> vehRntlReimbsDed = new ArrayList<TestData>();
	private List<TestData> vehTwgLbrDed = new ArrayList<TestData>();
	private List<TestData> vehLnPrtcDed = new ArrayList<TestData>();
	private List<TestData> plcyMpEaPers = new ArrayList<TestData>();
	private List<TestData> plcyPdEaOcc = new ArrayList<TestData>();
	private List<TestData> vehRntlReimbsPrem = new ArrayList<TestData>();
	private List<TestData> vehTwgLbrPrem = new ArrayList<TestData>();
	private List<TestData> vehTotPrem = new ArrayList<TestData>();
	private List<TestData> fPBCovTyp = new ArrayList<TestData>();
	private List<TestData> tortTrshldTyp = new ArrayList<TestData>();
	private List<TestData> plcyMedBenEaPers = new ArrayList<TestData>();
	private List<TestData> vehMedBenPrem = new ArrayList<TestData>();
	private List<TestData> plcyFnrlExpBenEaOcc = new ArrayList<TestData>();
	private List<TestData> vehFnrlExpBenPrem = new ArrayList<TestData>();
	private List<TestData> plcyAcdntDeadBenEaOcc = new ArrayList<TestData>();
	private List<TestData> vehAcdntDeadBenPrem = new ArrayList<TestData>();
	private List<TestData> vehExtrMedBenPrem = new ArrayList<TestData>();
	private List<TestData> plcySpclEqpmtTotAmt = new ArrayList<TestData>();
	private List<TestData> vehUMStckPrem = new ArrayList<TestData>();
	private List<TestData> vehUIMBStckPrem = new ArrayList<TestData>();
	private List<TestData> vehUMNonStckPrem = new ArrayList<TestData>();
	private List<TestData> vehUIMBNonStckPrem = new ArrayList<TestData>();
	private List<TestData> genPlcyCovPrem = new ArrayList<TestData>();
	private String plcyTotPrem;
	private String netWrtPrem;
	private String plcyTotFee;
	//	private String instlFee;
	private String plcyTotRnwlPrem;
	private String curRnwlAmt;
	private String totNwCrgAmt;
	private String plcyPayMinAmt;
	private String plcyDueDt;
	private String allVehTotPrem;
	private String rnwlTrmEffDt;
	private String rnwlTrmDt;
	private String vehCovPrem;
	private String plcyPastDueBal;
	private List<TestData> dueAmount = new ArrayList<TestData>();
	private List<TestData> installmentDueDate = new ArrayList<TestData>();

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.IN, States.OK, States.PA})
	@Test(groups = {Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL})
	public void TC01_CreatePolicy(@Optional("") String state) {

		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		policyExpirationDate_CurrentTerm = PolicySummaryPage.getExpirationDate();
		log.info("Original Policy #" + policyNumber);

		storeCoveragesData();
		storeBillingData();

		switch (getState()) {
			case "AZ":
		/* verify the xml file for AZ state
			AH35XX
			AA02AZ
			AA10XX
			AA43AZ
			AA52AZ
			AA59XX
			AAGCAZ
			AARFIXX
			AASR22 
			AHNBXX	*/

				DocGenHelper.verifyDocumentsGenerated(policyNumber, AASR22).verify.mapping(getTestSpecificTD("TestData_AASR22")
								.adjust(TestData.makeKeyPath("AASR22", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AASR22", "form", "TermEffDt", "DateTimeField"), termEffDt),
						policyNumber);

				DocGenHelper.verifyDocumentsGenerated(policyNumber, AA43AZ, AH35XX, AA59XX, AAGCAZ, AA52AZ, AARFIXX, AHNBXX, AA10XX, AA02AZ).verify.mapping(getTestSpecificTD("TestData_VerificationNB")
								.adjust(TestData.makeKeyPath("AA43AZ", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA43AZ", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AH35XX", "PaymentDetails", "PlcyTotWdrlAmt"), dueAmount)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "FutInstlDueDt"), installmentDueDate)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyEffDt", "DateTimeField"), plcyEffDt)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyExprDt", "DateTimeField"), plcyExprDt)
								.adjust(TestData.makeKeyPath("AA59XX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AAGCAZ", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA52AZ", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA52AZ", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AARFIXX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AHNBXX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AHNBXX", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA10XX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA10XX", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA10XX", "form", "TermExprDt", "DateTimeField"), termExprDt)
								.adjust(TestData.makeKeyPath("AA02AZ", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "PlcyMpEaPers"), plcyMpEaPers)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "PlcyPdEaOcc"), plcyPdEaOcc)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehBdyInjPrem"), vehBdyInjPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehPDPrem"), vehPDPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehUMPrem"), vehUMPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehUIMBPrem"), vehUIMBPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehMPPrem"), vehMPPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehCompPrem"), vehCompPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehClsnPrem"), vehClsnPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehRntlReimbsPrem"), vehRntlReimbsPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehTwgLbrPrem"), vehTwgLbrPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehClsnDed"), vehClsnDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehCompDed"), vehCompDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehSftyGlsDed"), vehSftyGlsDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehNwAddPrtcDed"), vehNwAddPrtcDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehRntlReimbsDed"), vehRntlReimbsDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehTwgLbrDed"), vehTwgLbrDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehLnPrtcDed"), vehLnPrtcDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "NetWrtPrem", "TextField"), netWrtPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehTotPrem"), vehTotPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "PaymentDetails", "PlcyTotFee", "TextField"), plcyTotFee)
								.adjust(TestData.makeKeyPath("AA02AZ", "PaymentDetails", "PlcyTotPrem", "TextField"), plcyTotPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA02AZ", "form", "TermExprDt", "DateTimeField"), termExprDt),
						policyNumber);
				break;
			case "IN":
		 /*verify the xml file for IN state
			AH35XX
			AA02IN
			AA10XX
			AA43IN
			AA52IN
			AA59XX
			AA53IN
			AASR22 
			AHNBXX
			AARFIXX*/

				DocGenHelper.verifyDocumentsGenerated(policyNumber, AASR22).verify.mapping(getTestSpecificTD("TestData_AASR22")
								.adjust(TestData.makeKeyPath("AASR22", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AASR22", "form", "TermEffDt", "DateTimeField"), termEffDt),
						policyNumber);

				DocGenHelper.verifyDocumentsGenerated(policyNumber, AA43IN, AH35XX, AA59XX, AA53IN, AA52IN, AARFIXX, AHNBXX, AA10XX, AA02IN).verify.mapping(getTestSpecificTD("TestData_VerificationNB")
								.adjust(TestData.makeKeyPath("AA43IN", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA43IN", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AH35XX", "PaymentDetails", "PlcyTotWdrlAmt"), dueAmount)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "FutInstlDueDt"), installmentDueDate)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyEffDt", "DateTimeField"), plcyEffDt)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyExprDt", "DateTimeField"), plcyExprDt)
								.adjust(TestData.makeKeyPath("AA59XX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA53IN", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA52IN", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA52IN", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AARFIXX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AHNBXX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AHNBXX", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA10XX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA10XX", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA10XX", "form", "TermExprDt", "DateTimeField"), termExprDt)
								.adjust(TestData.makeKeyPath("AA02IN", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "PlcyMpEaPers"), plcyMpEaPers)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "PlcyPdEaOcc"), plcyPdEaOcc)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehBdyInjPrem"), vehBdyInjPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehPDPrem"), vehPDPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehUMUIMBPrem"), vehUMUIMBPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehUMPDPrem"), vehUMPDPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehMPPrem"), vehMPPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehCompPrem"), vehCompPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehClsnPrem"), vehClsnPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehRntlReimbsPrem"), vehRntlReimbsPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehTwgLbrPrem"), vehTwgLbrPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehClsnDed"), vehClsnDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehCompDed"), vehCompDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehSftyGlsDed"), vehSftyGlsDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehNwAddPrtcDed"), vehNwAddPrtcDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehRntlReimbsDed"), vehRntlReimbsDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehTwgLbrDed"), vehTwgLbrDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehLnPrtcDed"), vehLnPrtcDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "NetWrtPrem", "TextField"), netWrtPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehTotPrem"), vehTotPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "AllVehTotPrem", "TextField"), allVehTotPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "PaymentDetails", "PlcyTotPrem", "TextField"), plcyTotPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA02IN", "form", "TermExprDt", "DateTimeField"), termExprDt),
						policyNumber);
				break;
			case "OK":
			/*verify the xml file for IN state
			AH35XX
			AA02OK
			AA10OK
			AA43OK
			AA52OK
			AA59XX
			AAAEOK 
			AASR22 
			AHNBXX
			AARFIXX*/

				DocGenHelper.verifyDocumentsGenerated(policyNumber, AASR22).verify.mapping(getTestSpecificTD("TestData_AASR22")
								.adjust(TestData.makeKeyPath("AASR22", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AASR22", "form", "TermEffDt", "DateTimeField"), termEffDt),
						policyNumber);

				DocGenHelper.verifyDocumentsGenerated(policyNumber, AA43OK, AH35XX, AA59XX, AAAEOK, AA52OK, AARFIXX, AHNBXX, AA10OK, AA02OK).verify.mapping(getTestSpecificTD("TestData_VerificationNB")
								.adjust(TestData.makeKeyPath("AA43OK", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA43OK", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AH35XX", "PaymentDetails", "PlcyTotWdrlAmt"), dueAmount)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "FutInstlDueDt"), installmentDueDate)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyEffDt", "DateTimeField"), plcyEffDt)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyExprDt", "DateTimeField"), plcyExprDt)
								.adjust(TestData.makeKeyPath("AA59XX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AAAEOK", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AAAEOK", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA52OK", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA52OK", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA52OK", "form", "PlcyExprDt", "DateTimeField"), plcyExprDt)
								.adjust(TestData.makeKeyPath("AARFIXX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AHNBXX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AHNBXX", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA10OK", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA10OK", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA10OK", "form", "TermExprDt", "DateTimeField"), termExprDt)
								.adjust(TestData.makeKeyPath("AA02OK", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "PlcyMpEaPers"), plcyMpEaPers)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "PlcyPdEaOcc"), plcyPdEaOcc)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehBdyInjPrem"), vehBdyInjPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehPDPrem"), vehPDPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehUMUIMBPrem"), vehUMUIMBPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "GenPlcyCovPrem"), genPlcyCovPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehMPPrem"), vehMPPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehCompPrem"), vehCompPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehClsnPrem"), vehClsnPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehRntlReimbsPrem"), vehRntlReimbsPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehTwgLbrPrem"), vehTwgLbrPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehClsnDed"), vehClsnDed)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehCompDed"), vehCompDed)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "PlcySpclEqpmtTotAmt"), plcySpclEqpmtTotAmt)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "NetWrtPrem", "TextField"), netWrtPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehTotPrem"), vehTotPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehCovPrem", "TextField"), vehCovPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "AllVehTotPrem", "TextField"), allVehTotPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "PaymentDetails", "PlcyTotPrem", "TextField"), plcyTotPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA02OK", "form", "TermExprDt", "DateTimeField"), termExprDt),
						policyNumber);
				break;
			case "PA":
		/* verify the xml file for IN state
			AH35XX
			AARFIXX
			AHNBXX
			AA59XX
			AA02PA
			AA10PA
			AA43PA
			AA52UPAC
			AA52IPAC
			AASDPA
			AADNPAC
			AADNPAD 
			AADNPAE
			AALTPA*/

				DocGenHelper.verifyDocumentsGenerated(policyNumber, AA43PA, AH35XX, AA59XX, AA52UPAC, AA52IPAC, AARFIXX, AHNBXX, AA10PA, AA02PA, AASDPA, AADNPAC, AADNPAD, AADNPAE, AALTPA).verify
						.mapping(getTestSpecificTD("TestData_VerificationNB")
										.adjust(TestData.makeKeyPath("AA43PA", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AA43PA", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AH35XX", "PaymentDetails", "PlcyTotWdrlAmt"), dueAmount)
										.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AH35XX", "form", "FutInstlDueDt"), installmentDueDate)
										.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyEffDt", "DateTimeField"), plcyEffDt)
										.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyExprDt", "DateTimeField"), plcyExprDt)
										.adjust(TestData.makeKeyPath("AA59XX", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AA52UPAC", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AA52UPAC", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AA52IPAC", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AA52IPAC", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AARFIXX", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AHNBXX", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AHNBXX", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AASDPA", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AASDPA", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AADNPAC", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AADNPAC", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AADNPAD", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AADNPAD", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AADNPAE", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AADNPAE", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AALTPA", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AALTPA", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AA10PA", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AA10PA", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AA10PA", "form", "TermExprDt", "DateTimeField"), termExprDtPA)
										.adjust(TestData.makeKeyPath("AA02PA", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "TortTrshldTyp"), tortTrshldTyp)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "PlcyPdEaOcc"), plcyPdEaOcc)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "PlcyMedBenEaPers"), plcyMedBenEaPers)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehMedBenPrem"), vehMedBenPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "PlcyFnrlExpBenEaOcc"), plcyFnrlExpBenEaOcc)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehFnrlExpBenPrem"), vehFnrlExpBenPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "PlcyAcdntDeadBenEaOcc"), plcyAcdntDeadBenEaOcc)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehAcdntDeadBenPrem"), vehAcdntDeadBenPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehExtrMedBenPrem"), vehExtrMedBenPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "PlcySpclEqpmtTotAmt"), plcySpclEqpmtTotAmt)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehBdyInjPrem"), vehBdyInjPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehPDPrem"), vehPDPrem)
										//.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehUMStckPrem"), vehUMStckPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehUIMBStckPrem"), vehUIMBStckPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehCompPrem"), vehCompPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehClsnPrem"), vehClsnPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehRntlReimbsPrem"), vehRntlReimbsPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehTwgLbrPrem"), vehTwgLbrPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehClsnDed"), vehClsnDed)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehCompDed"), vehCompDed)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "FPBCovTyp"), fPBCovTyp)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "NetWrtPrem", "TextField"), netWrtPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehTotPrem"), vehTotPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "AllVehTotPrem", "TextField"), allVehTotPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "PaymentDetails", "PlcyTotPrem", "TextField"), plcyTotPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AA02PA", "form", "TermExprDt", "DateTimeField"), termExprDt),
								policyNumber);
				break;
		}

		clearList();
	}

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.IN, States.OK})
	@Test(groups = {Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL}, dependsOnMethods = "TC01_CreatePolicy")
	public void TC02_01_EndorsePolicy(@Optional("") String state) {

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		TestData endorsementTd = getTestSpecificTD("TestData_Endorsement");
		policy.createEndorsement(endorsementTd.adjust(getPolicyTD("Endorsement", "TestData")));
		assertThat(PolicySummaryPage.buttonPendedEndorsement).isEnabled(false);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		storeCoveragesData();
		storeBillingData();

		endrEffDt = DocGenHelper.convertToZonedDateTime(TimeSetterUtil.getInstance()
				.parse(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, "Endorsement - Maintain Vehicle(s)")
						.getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.EFF_DATE).getValue(), DateTimeUtils.MM_DD_YYYY));

		switch (getState()) {
			case "AZ":
				DocGenHelper.verifyDocumentsGenerated(policyNumber, AASR26).verify.mapping(getTestSpecificTD("TestData_AASR26")
								.adjust(TestData.makeKeyPath("AASR26", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AASR26", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AASR26", "form", "TermExprDt", "DateTimeField"), termExprDt),
						policyNumber);

				break;
			case "IN":
				DocGenHelper.verifyDocumentsGenerated(policyNumber, AASR26).verify.mapping(getTestSpecificTD("TestData_AASR26")
								.adjust(TestData.makeKeyPath("AASR26", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AASR26", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AASR26", "form", "TermExprDt", "DateTimeField"), termExprDt),
						policyNumber);
				
				break;
			case "OK":
				DocGenHelper.verifyDocumentsGenerated(policyNumber, AASR26).verify.mapping(getTestSpecificTD("TestData_AASR26")
								.adjust(TestData.makeKeyPath("AASR26", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AASR26", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AASR26", "form", "TermExprDt", "DateTimeField"), termExprDt),
						policyNumber);

				break;

		}

		clearList();

	}

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.IN, States.OK, States.PA})
	@Test(groups = {Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL}, dependsOnMethods = "TC01_CreatePolicy")
	public void TC02_02_EndorsePolicy(@Optional("") String state) {

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		TestData endorsementTd = getTestSpecificTD("TestData_Endorsement2");
		policy.createEndorsement(endorsementTd.adjust(getPolicyTD("Endorsement", "TestData")));
		assertThat(PolicySummaryPage.buttonPendedEndorsement).isEnabled(false);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		storeCoveragesData();
		storeBillingData();

		endrEffDt = DocGenHelper.convertToZonedDateTime(TimeSetterUtil.getInstance()
				.parse(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, "Endorsement - Maintain Vehicle(s)")
						.getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.EFF_DATE).getValue(), DateTimeUtils.MM_DD_YYYY));

		switch (getState()) {
			case "AZ":
				DocGenHelper.verifyDocumentsGenerated(policyNumber, AA43AZ, AH35XX, AA59XX, AA52AZ, AA10XX, AAPDXX, AA02AZ).verify.mapping(getTestSpecificTD("TestData_VerificationED")
								.adjust(TestData.makeKeyPath("AA43AZ", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA43AZ", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA43AZ", "form", "EndrEffDt", "DateTimeField"), endrEffDt)
								.adjust(TestData.makeKeyPath("AH35XX", "PaymentDetails", "PlcyTotWdrlAmt"), dueAmount)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "FutInstlDueDt"), installmentDueDate)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyEffDt", "DateTimeField"), plcyEffDt)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyExprDt", "DateTimeField"), plcyExprDt)
								.adjust(TestData.makeKeyPath("AA59XX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA52AZ", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA52AZ", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA52AZ", "form", "EndrEffDt", "DateTimeField"), endrEffDt)
								.adjust(TestData.makeKeyPath("AA10XX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA10XX", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA10XX", "form", "TermExprDt", "DateTimeField"), termExprDt)
								.adjust(TestData.makeKeyPath("AA10XX", "form", "EndrEffDt", "DateTimeField"), endrEffDt)
								.adjust(TestData.makeKeyPath("AA02AZ", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "PlcyMpEaPers"), plcyMpEaPers)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "PlcyPdEaOcc"), plcyPdEaOcc)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehBdyInjPrem"), vehBdyInjPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehPDPrem"), vehPDPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehUMPrem"), vehUMPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehUIMBPrem"), vehUIMBPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehMPPrem"), vehMPPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehCompPrem"), vehCompPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehClsnPrem"), vehClsnPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehRntlReimbsPrem"), vehRntlReimbsPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehTwgLbrPrem"), vehTwgLbrPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehClsnDed"), vehClsnDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehCompDed"), vehCompDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehSftyGlsDed"), vehSftyGlsDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehNwAddPrtcDed"), vehNwAddPrtcDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehRntlReimbsDed"), vehRntlReimbsDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehTwgLbrDed"), vehTwgLbrDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehLnPrtcDed"), vehLnPrtcDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "NetWrtPrem", "TextField"), netWrtPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehTotPrem"), vehTotPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "PaymentDetails", "PlcyTotFee", "TextField"), plcyTotFee)
								.adjust(TestData.makeKeyPath("AA02AZ", "PaymentDetails", "PlcyTotPrem", "TextField"), plcyTotPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA02AZ", "form", "TermExprDt", "DateTimeField"), termExprDt)
								.adjust(TestData.makeKeyPath("AA02AZ", "form", "EndrEffDt", "DateTimeField"), endrEffDt),
						policyNumber);
				break;
				
			case "IN":
				DocGenHelper.verifyDocumentsGenerated(policyNumber, AA43IN, AH35XX, AA59XX, AA52IN, AA53IN, AA10XX, AAPDXX, AA02IN).verify.mapping(getTestSpecificTD("TestData_VerificationED")
								.adjust(TestData.makeKeyPath("AA43IN", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA43IN", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA43IN", "form", "EndrEffDt", "DateTimeField"), endrEffDt)
								.adjust(TestData.makeKeyPath("AH35XX", "PaymentDetails", "PlcyTotWdrlAmt"), dueAmount)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "FutInstlDueDt"), installmentDueDate)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyEffDt", "DateTimeField"), plcyEffDt)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyExprDt", "DateTimeField"), plcyExprDt)
								.adjust(TestData.makeKeyPath("AA59XX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA53IN", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA52IN", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA52IN", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA52IN", "form", "EndrEffDt", "DateTimeField"), endrEffDt)
								.adjust(TestData.makeKeyPath("AA10XX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA10XX", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA10XX", "form", "TermExprDt", "DateTimeField"), termExprDt)
								.adjust(TestData.makeKeyPath("AA10XX", "form", "EndrEffDt", "DateTimeField"), endrEffDt)
								.adjust(TestData.makeKeyPath("AA02IN", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "PlcyMpEaPers"), plcyMpEaPers)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "PlcyPdEaOcc"), plcyPdEaOcc)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehBdyInjPrem"), vehBdyInjPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehPDPrem"), vehPDPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehUMUIMBPrem"), vehUMUIMBPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehUMPDPrem"), vehUMPDPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehMPPrem"), vehMPPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehCompPrem"), vehCompPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehClsnPrem"), vehClsnPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehRntlReimbsPrem"), vehRntlReimbsPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehTwgLbrPrem"), vehTwgLbrPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehClsnDed"), vehClsnDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehCompDed"), vehCompDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehSftyGlsDed"), vehSftyGlsDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehNwAddPrtcDed"), vehNwAddPrtcDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehRntlReimbsDed"), vehRntlReimbsDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehTwgLbrDed"), vehTwgLbrDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehLnPrtcDed"), vehLnPrtcDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "NetWrtPrem", "TextField"), netWrtPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehTotPrem"), vehTotPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "AllVehTotPrem", "TextField"), allVehTotPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "PaymentDetails", "PlcyTotPrem", "TextField"), plcyTotPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA02IN", "form", "TermExprDt", "DateTimeField"), termExprDt)
								.adjust(TestData.makeKeyPath("AA02IN", "form", "EndrEffDt", "DateTimeField"), endrEffDt),
						policyNumber);
				break;
			case "OK":
				DocGenHelper.verifyDocumentsGenerated(policyNumber, AA43OK, AH35XX, AA59XX, AA52OK, AA10OK, AAPDXX, AA02OK).verify.mapping(getTestSpecificTD("TestData_VerificationED")
								.adjust(TestData.makeKeyPath("AA43OK", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA43OK", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA43OK", "form", "EndrEffDt", "DateTimeField"), endrEffDt)
								.adjust(TestData.makeKeyPath("AH35XX", "PaymentDetails", "PlcyTotWdrlAmt"), dueAmount)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "FutInstlDueDt"), installmentDueDate)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyEffDt", "DateTimeField"), plcyEffDt)
								.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyExprDt", "DateTimeField"), plcyExprDt)
								.adjust(TestData.makeKeyPath("AA59XX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA52OK", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA52OK", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA52OK", "form", "EndrEffDt", "DateTimeField"), endrEffDt)
								.adjust(TestData.makeKeyPath("AA52OK", "form", "PlcyExprDt", "DateTimeField"), plcyExprDt)
								.adjust(TestData.makeKeyPath("AA10OK", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA10OK", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA10OK", "form", "TermExprDt", "DateTimeField"), termExprDt)
								.adjust(TestData.makeKeyPath("AA10OK", "form", "EndrEffDt", "DateTimeField"), endrEffDt)
								.adjust(TestData.makeKeyPath("AA02OK", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "PlcyMpEaPers"), plcyMpEaPers)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "PlcyPdEaOcc"), plcyPdEaOcc)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehBdyInjPrem"), vehBdyInjPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehPDPrem"), vehPDPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehUMUIMBPrem"), vehUMUIMBPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "GenPlcyCovPrem"), genPlcyCovPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehMPPrem"), vehMPPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehCompPrem"), vehCompPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehClsnPrem"), vehClsnPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehRntlReimbsPrem"), vehRntlReimbsPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehTwgLbrPrem"), vehTwgLbrPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehClsnDed"), vehClsnDed)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehCompDed"), vehCompDed)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "PlcySpclEqpmtTotAmt"), plcySpclEqpmtTotAmt)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehTotPrem"), vehTotPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehCovPrem", "TextField"), vehCovPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "AllVehTotPrem", "TextField"), allVehTotPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "PaymentDetails", "PlcyTotPrem", "TextField"), plcyTotPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA02OK", "form", "TermExprDt", "DateTimeField"), termExprDt)
								.adjust(TestData.makeKeyPath("AA02OK", "form", "EndrEffDt", "DateTimeField"), endrEffDt),
						policyNumber);
				break;
			case "PA":
				DocGenHelper.verifyDocumentsGenerated(policyNumber, AA43PA, AH35XX, AA59XX, AA52IPAB, AA52UPAB, AA10PA, AAPDXX, AA02PA, AAFPPA).verify
						.mapping(getTestSpecificTD("TestData_VerificationED")
										.adjust(TestData.makeKeyPath("AA43PA", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AA43PA", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AA43PA", "form", "EndrEffDt", "DateTimeField"), endrEffDt)
										.adjust(TestData.makeKeyPath("AH35XX", "PaymentDetails", "PlcyTotWdrlAmt"), dueAmount)
										.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AH35XX", "form", "FutInstlDueDt"), installmentDueDate)
										.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyEffDt", "DateTimeField"), plcyEffDt)
										.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyExprDt", "DateTimeField"), plcyExprDt)
										.adjust(TestData.makeKeyPath("AA59XX", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AA52IPAB", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AA52IPAB", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AA52IPAB", "form", "EndrEffDt", "DateTimeField"), endrEffDt)
										.adjust(TestData.makeKeyPath("AA52UPAB", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AA52UPAB", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AA52UPAB", "form", "EndrEffDt", "DateTimeField"), endrEffDt)
										.adjust(TestData.makeKeyPath("AA10PA", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AA10PA", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AA10PA", "form", "TermExprDt", "DateTimeField"), termExprDtPA)
										.adjust(TestData.makeKeyPath("AA10PA", "form", "EndrEffDt", "DateTimeField"), endrEffDt)
										.adjust(TestData.makeKeyPath("AA02PA", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "TortTrshldTyp"), tortTrshldTyp)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "PlcyPdEaOcc"), plcyPdEaOcc)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "PlcyMedBenEaPers"), plcyMedBenEaPers)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehMedBenPrem"), vehMedBenPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "PlcyFnrlExpBenEaOcc"), plcyFnrlExpBenEaOcc)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehFnrlExpBenPrem"), vehFnrlExpBenPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "PlcyAcdntDeadBenEaOcc"), plcyAcdntDeadBenEaOcc)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehAcdntDeadBenPrem"), vehAcdntDeadBenPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehExtrMedBenPrem"), vehExtrMedBenPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "PlcySpclEqpmtTotAmt"), plcySpclEqpmtTotAmt)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehBdyInjPrem"), vehBdyInjPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehPDPrem"), vehPDPrem)
										//.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehUMNonStckPrem"), vehUMNonStckPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehUIMBNonStckPrem"), vehUIMBNonStckPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehCompPrem"), vehCompPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehClsnPrem"), vehClsnPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehRntlReimbsPrem"), vehRntlReimbsPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehTwgLbrPrem"), vehTwgLbrPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehClsnDed"), vehClsnDed)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehCompDed"), vehCompDed)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "FPBCovTyp"), fPBCovTyp)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "NetWrtPrem", "TextField"), netWrtPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehTotPrem"), vehTotPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "AllVehTotPrem", "TextField"), allVehTotPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "PaymentDetails", "PlcyTotPrem", "TextField"), plcyTotPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AA02PA", "form", "TermExprDt", "DateTimeField"), termExprDt)
										.adjust(TestData.makeKeyPath("AA02PA", "form", "EndrEffDt", "DateTimeField"), endrEffDt)
										.adjust(TestData.makeKeyPath("AAFPPA", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AAFPPA", "form", "EndrEffDt", "DateTimeField"), endrEffDt),
								policyNumber);
				break;
		}

		clearList();

	}
	
	@Parameters({"state"})
	@StateList(states = {States.AZ, States.IN, States.OK, States.PA})
	@Test(groups = {Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL}, dependsOnMethods = "TC01_CreatePolicy")
	public void TC03_RenewalImageGeneration(@Optional("") String state) {
		clearList();
		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate_CurrentTerm);
		Log.info("Policy Renewal Image Generation Date" + renewImageGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.IN, States.OK, States.PA})
	@Test(groups = {Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL}, dependsOnMethods = "TC01_CreatePolicy")
	public void TC04_RenewaPreviewGeneration(@Optional("") String state) {

		LocalDateTime renewPreviewGenDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate_CurrentTerm);
		Log.info("Policy Renewal Preview Generation Date" + renewPreviewGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewPreviewGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		policy.getDefaultView().fill(getTestSpecificTD("TestData_AddRenewal"));
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);

	}

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.IN, States.OK, States.PA})
	@Test(groups = {Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL}, dependsOnMethods = "TC01_CreatePolicy")
	public void TC05_RenewaOfferGeneration(@Optional("") String state) {
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate_CurrentTerm);
		Log.info("Policy Renewal Offer Generation Date" + renewOfferGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		storeCoveragesData();
		BillingSummaryPage.open();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyEffectiveDate);

		switch (getState()) {
			case "AZ":
		   /* verify the xml file
			AA02AZ
			AA10XX
			AHAUXX //consumer information notice is removed
			AHPNXX*/

				DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, AHPNXX, AA10XX, AA02AZ).verify.mapping(getTestSpecificTD("TestData_VerificationRenewal")
								.adjust(TestData.makeKeyPath("AA10XX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA10XX", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA10XX", "form", "TermExprDt", "DateTimeField"), termExprDt)
								.adjust(TestData.makeKeyPath("AA02AZ", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "PlcyMpEaPers"), plcyMpEaPers)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "PlcyPdEaOcc"), plcyPdEaOcc)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehBdyInjPrem"), vehBdyInjPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehPDPrem"), vehPDPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehUMPrem"), vehUMPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehUIMBPrem"), vehUIMBPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehMPPrem"), vehMPPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehCompPrem"), vehCompPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehClsnPrem"), vehClsnPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehRntlReimbsPrem"), vehRntlReimbsPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehTwgLbrPrem"), vehTwgLbrPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehClsnDed"), vehClsnDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehCompDed"), vehCompDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehSftyGlsDed"), vehSftyGlsDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehNwAddPrtcDed"), vehNwAddPrtcDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehRntlReimbsDed"), vehRntlReimbsDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehTwgLbrDed"), vehTwgLbrDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehLnPrtcDed"), vehLnPrtcDed)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "NetWrtPrem", "TextField"), netWrtPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehTotPrem"), vehTotPrem)
								//				.adjust(TestData.makeKeyPath("AA02AZ", "PaymentDetails", "PlcyTotFee", "TextField"), plcyTotFee)
								.adjust(TestData.makeKeyPath("AA02AZ", "PaymentDetails", "PlcyTotPrem", "TextField"), plcyTotPrem)
								.adjust(TestData.makeKeyPath("AA02AZ", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA02AZ", "form", "TermExprDt", "DateTimeField"), termExprDt),
						policyNumber);
				break;
			case "IN":
			   /* verify the xml file
				AA02IN
				AA10XX
				AHAUXX
				AHPNXX*/
				DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, AHPNXX, AA10XX, AA02IN).verify.mapping(getTestSpecificTD("TestData_VerificationRenewal")
								.adjust(TestData.makeKeyPath("AA10XX", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA10XX", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA10XX", "form", "TermExprDt", "DateTimeField"), termExprDt)
								.adjust(TestData.makeKeyPath("AA02IN", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "PlcyMpEaPers"), plcyMpEaPers)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "PlcyPdEaOcc"), plcyPdEaOcc)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehBdyInjPrem"), vehBdyInjPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehPDPrem"), vehPDPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehUMUIMBPrem"), vehUMUIMBPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehUMPDPrem"), vehUMPDPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehMPPrem"), vehMPPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehCompPrem"), vehCompPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehClsnPrem"), vehClsnPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehRntlReimbsPrem"), vehRntlReimbsPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehTwgLbrPrem"), vehTwgLbrPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehClsnDed"), vehClsnDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehCompDed"), vehCompDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehSftyGlsDed"), vehSftyGlsDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehNwAddPrtcDed"), vehNwAddPrtcDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehRntlReimbsDed"), vehRntlReimbsDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehTwgLbrDed"), vehTwgLbrDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehLnPrtcDed"), vehLnPrtcDed)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "NetWrtPrem", "TextField"), netWrtPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "VehTotPrem"), vehTotPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "CoverageDetails", "AllVehTotPrem", "TextField"), allVehTotPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "PaymentDetails", "PlcyTotPrem", "TextField"), plcyTotPrem)
								.adjust(TestData.makeKeyPath("AA02IN", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA02IN", "form", "TermExprDt", "DateTimeField"), termExprDt),
						policyNumber);
				break;
			case "OK":
			/* verify the xml file
				AA02OK
				AA10OK
				AHAUXX
				AHPNXX*/
				DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, AHPNXX, AA10OK, AA02OK).verify.mapping(getTestSpecificTD("TestData_VerificationRenewal")
								.adjust(TestData.makeKeyPath("AA10OK", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA10OK", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA10OK", "form", "TermExprDt", "DateTimeField"), termExprDt)
								.adjust(TestData.makeKeyPath("AA02OK", "form", "PlcyNum", "TextField"), policyNumber)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "PlcyMpEaPers"), plcyMpEaPers)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "PlcyPdEaOcc"), plcyPdEaOcc)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehBdyInjPrem"), vehBdyInjPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehPDPrem"), vehPDPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehUMUIMBPrem"), vehUMUIMBPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "GenPlcyCovPrem"), genPlcyCovPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehMPPrem"), vehMPPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehCompPrem"), vehCompPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehClsnPrem"), vehClsnPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehRntlReimbsPrem"), vehRntlReimbsPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehTwgLbrPrem"), vehTwgLbrPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehClsnDed"), vehClsnDed)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehCompDed"), vehCompDed)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "PlcySpclEqpmtTotAmt"), plcySpclEqpmtTotAmt)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "NetWrtPrem", "TextField"), netWrtPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehTotPrem"), vehTotPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "VehCovPrem", "TextField"), vehCovPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "CoverageDetails", "AllVehTotPrem", "TextField"), allVehTotPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "PaymentDetails", "PlcyTotPrem", "TextField"), plcyTotPrem)
								.adjust(TestData.makeKeyPath("AA02OK", "form", "TermEffDt", "DateTimeField"), termEffDt)
								.adjust(TestData.makeKeyPath("AA02OK", "form", "TermExprDt", "DateTimeField"), termExprDt),
						policyNumber);
				break;
			case "PA":
			/* verify the xml file
				AA02PA
				AA10PA
				AHAUXX
				AHPNXX
				F122G
				AASDPA
				AADNPAD
				AADNPAE
				AADNPAC*/
				DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, AHPNXX, AA10PA, AA02PA, F122G, AASDPA, AADNPAD, AADNPAE, AADNPAC).verify
						.mapping(getTestSpecificTD("TestData_VerificationRenewal")
										.adjust(TestData.makeKeyPath("AA10PA", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AA10PA", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AA10PA", "form", "TermExprDt", "DateTimeField"), termExprDtPA)
										.adjust(TestData.makeKeyPath("AA02PA", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "TortTrshldTyp"), tortTrshldTyp)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "PlcyPdEaOcc"), plcyPdEaOcc)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "PlcyMedBenEaPers"), plcyMedBenEaPers)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehMedBenPrem"), vehMedBenPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "PlcyFnrlExpBenEaOcc"), plcyFnrlExpBenEaOcc)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehFnrlExpBenPrem"), vehFnrlExpBenPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "PlcyAcdntDeadBenEaOcc"), plcyAcdntDeadBenEaOcc)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehAcdntDeadBenPrem"), vehAcdntDeadBenPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehExtrMedBenPrem"), vehExtrMedBenPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "PlcySpclEqpmtTotAmt"), plcySpclEqpmtTotAmt)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehBdyInjPrem"), vehBdyInjPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehPDPrem"), vehPDPrem)
										//.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehUMNonStckPrem"), vehUMNonStckPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehUIMBNonStckPrem"), vehUIMBNonStckPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehCompPrem"), vehCompPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehClsnPrem"), vehClsnPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehRntlReimbsPrem"), vehRntlReimbsPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehTwgLbrPrem"), vehTwgLbrPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehClsnDed"), vehClsnDed)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehCompDed"), vehCompDed)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "FPBCovTyp"), fPBCovTyp)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "NetWrtPrem", "TextField"), netWrtPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "VehTotPrem"), vehTotPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "CoverageDetails", "AllVehTotPrem", "TextField"), allVehTotPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "PaymentDetails", "PlcyTotPrem", "TextField"), plcyTotPrem)
										.adjust(TestData.makeKeyPath("AA02PA", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AA02PA", "form", "TermExprDt", "DateTimeField"), termExprDt)
										.adjust(TestData.makeKeyPath("F122G", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "TortTrshldTyp"), tortTrshldTyp)
										.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "PlcyMedBenEaPers"), plcyMedBenEaPers)
										.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "VehMedBenPrem"), vehMedBenPrem)
										.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "PlcyFnrlExpBenEaOcc"), plcyFnrlExpBenEaOcc)
										.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "VehFnrlExpBenPrem"), vehFnrlExpBenPrem)
										.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "PlcyAcdntDeadBenEaOcc"), plcyAcdntDeadBenEaOcc)
										.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "VehAcdntDeadBenPrem"), vehAcdntDeadBenPrem)
										.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "VehExtrMedBenPrem"), vehExtrMedBenPrem)
										//					.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "PlcySpclEqpmtTotAmt"), plcySpclEqpmtTotAmt) //TODO defect_44888 the field was missed in xml file
										.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "VehBdyInjPrem"), vehBdyInjPrem)
										.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "VehPDPrem"), vehPDPrem)
										//.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "VehUMNonStckPrem"), vehUMNonStckPrem)
										.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "VehUIMBNonStckPrem"), vehUIMBNonStckPrem)
										.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "VehCompPrem"), vehCompPrem)
										.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "VehClsnPrem"), vehClsnPrem)
										.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "VehRntlReimbsPrem"), vehRntlReimbsPrem)
										.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "VehTwgLbrPrem"), vehTwgLbrPrem)
										.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "VehClsnDed"), vehClsnDed)
										.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "VehCompDed"), vehCompDed)
										.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "FPBCovTyp"), fPBCovTyp)
										.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "VehTotPrem"), vehTotPrem)
										.adjust(TestData.makeKeyPath("F122G", "CoverageDetails", "AllVehTotPrem", "TextField"), allVehTotPrem)
										.adjust(TestData.makeKeyPath("F122G", "form", "RnwlTrmDt", "DateTimeField"), rnwlTrmDt)
										.adjust(TestData.makeKeyPath("AASDPA", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AASDPA", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AASDPA", "form", "RnwlTrmEffDt", "DateTimeField"), rnwlTrmEffDt)
										.adjust(TestData.makeKeyPath("AADNPAD", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AADNPAD", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AADNPAD", "form", "RnwlTrmEffDt", "DateTimeField"), rnwlTrmEffDt)
										.adjust(TestData.makeKeyPath("AADNPAE", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AADNPAE", "form", "TermEffDt", "DateTimeField"), termEffDt)
										.adjust(TestData.makeKeyPath("AADNPAE", "form", "RnwlTrmEffDt", "DateTimeField"), rnwlTrmEffDt)
										.adjust(TestData.makeKeyPath("AADNPAC", "form", "PlcyNum", "TextField"), policyNumber)
										.adjust(TestData.makeKeyPath("AADNPAC", "form", "TermEffDt", "DateTimeField"), termEffDt),
								policyNumber);
				break;
		}

		clearList();
	}

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.IN, States.OK, States.PA})
	@Test(groups = {Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL}, dependsOnMethods = "TC01_CreatePolicy")
	public void TC06_RenewaOfferBillGeneration(@Optional("") String state) {
		LocalDateTime renewOfferBillGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate_CurrentTerm);
		Log.info("Policy Renewal Offer Bill Generation Date" + renewOfferBillGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferBillGenDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		policy.policyInquiry().start();
		policyEffectiveDate = TimeSetterUtil.getInstance()
				.parse(policy.getDefaultView().getTab(GeneralTab.class).getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel(), TextBox.class)
						.getValue(), DateTimeUtils.MM_DD_YYYY);
		policyExpirationDate = TimeSetterUtil.getInstance()
				.parse(policy.getDefaultView().getTab(GeneralTab.class).getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EXPIRATION_DATE.getLabel(), TextBox.class)
						.getValue(), DateTimeUtils.MM_DD_YYYY);
		plcyEffDt = DocGenHelper.convertToZonedDateTime(policyEffectiveDate);
		termEffDt = DocGenHelper.convertToZonedDateTime(policyEffectiveDate);
		plcyExprDt = DocGenHelper.convertToZonedDateTime(policyExpirationDate);
		Tab.buttonCancel.click();

		storeBillingData();

		plcyTotRnwlPrem =
				formatValue(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, "Renewal - Policy Renewal Proposal")
						.getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());
		Dollar _curRnwlAmt = new Dollar(BillingSummaryPage.tableInstallmentSchedule.getRow(12).getCell(BillingConstants.BillingInstallmentScheduleTable.BILLED_AMOUNT).getValue());
		Dollar _instlFee = new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, "EFT Installment Fee")
				.getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());
		curRnwlAmt = _curRnwlAmt.subtract(_instlFee).toString().replace("$", "").replace(",", "");
		totNwCrgAmt = formatValue(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue());
		plcyPayMinAmt = formatValue(BillingSummaryPage.getMinimumDue().toString());
		plcyDueDt = DocGenHelper.convertToZonedDateTime(TimeSetterUtil.getInstance()
				.parse(BillingSummaryPage.tableBillsStatements.getRow(BillingConstants.BillingBillsAndStatmentsTable.TYPE, "Bill").getCell(BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE)
						.getValue(), DateTimeUtils.MM_DD_YYYY));
		//		instlFee = formatValue(BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, "EFT Installment Fee").getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());
		plcyPastDueBal = formatValue(BillingSummaryPage.tableBillingAccountPolicies.getRow(2).getCell(BillingConstants.BillingAccountPoliciesTable.PAST_DUE).getValue());

		/*verify the xml file
		AH35XX
	    AHRBXX*/

		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, AH35XX, AHRBXX).verify.mapping(getTestSpecificTD("TestData_VerificationRenewalBill")
						.adjust(TestData.makeKeyPath("AH35XX", "PaymentDetails", "PlcyTotWdrlAmt"), dueAmount)
						.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyNum", "TextField"), policyNumber)
						.adjust(TestData.makeKeyPath("AH35XX", "form", "FutInstlDueDt"), installmentDueDate)
						.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyEffDt", "DateTimeField"), plcyEffDt)
						.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyExprDt", "DateTimeField"), plcyExprDt)
						.adjust(TestData.makeKeyPath("AHRBXX", "form", "PlcyNum", "TextField"), policyNumber)
						.adjust(TestData.makeKeyPath("AHRBXX", "form", "PlcyEffDt", "DateTimeField"), plcyEffDt)
						.adjust(TestData.makeKeyPath("AHRBXX", "form", "TermEffDt", "DateTimeField"), termEffDt)
						.adjust(TestData.makeKeyPath("AHRBXX", "form", "PlcyExprDt", "DateTimeField"), plcyExprDt)
						.adjust(TestData.makeKeyPath("AHRBXX", "PaymentDetails", "CurRnwlAmt", "TextField"), curRnwlAmt)
						//					.adjust(TestData.makeKeyPath("AHRBXX", "PaymentDetails", "InstlFee","TextField"), instlFee)
						.adjust(TestData.makeKeyPath("AHRBXX", "PaymentDetails", "TotNwCrgAmt", "TextField"), totNwCrgAmt)
						.adjust(TestData.makeKeyPath("AHRBXX", "PaymentDetails", "PlcyPayMinAmt", "TextField"), plcyPayMinAmt)
						.adjust(TestData.makeKeyPath("AHRBXX", "PaymentDetails", "PlcyTotRnwlPrem", "TextField"), plcyTotRnwlPrem)
						.adjust(TestData.makeKeyPath("AHRBXX", "PaymentDetails", "PlcyPastDueBal", "TextField"), plcyPastDueBal)
						.adjust(TestData.makeKeyPath("AHRBXX", "PaymentDetails", "PlcyDueDt", "DateTimeField"), plcyDueDt),
				policyNumber);

	}

	private void storeCoveragesData() {
		policy.policyInquiry().start();
		// Get the policy effective date and expiration date from general page
		policyEffectiveDate = TimeSetterUtil.getInstance()
				.parse(policy.getDefaultView().getTab(GeneralTab.class).getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel(), TextBox.class)
						.getValue(), DateTimeUtils.MM_DD_YYYY);
		policyExpirationDate = TimeSetterUtil.getInstance()
				.parse(policy.getDefaultView().getTab(GeneralTab.class).getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EXPIRATION_DATE.getLabel(), TextBox.class)
						.getValue(), DateTimeUtils.MM_DD_YYYY);
		String _termEffDt = policyEffectiveDate.toString();
		String _plcyEffDt = policyEffectiveDate.toString();
		String _plcyExprDt = policyExpirationDate.toString();
		String _termExprDt = policyExpirationDate.toString();
		//PASBB-414 MDd and PA both require that a single set of ID cards for each vehicle are issued every six months regardless if the policy is on an annual term.
		String _termExprDtPA = policyExpirationDate.minusMonths(6).toString();
		termEffDt = "contains=" + _termEffDt.substring(0, _termEffDt.indexOf("T"));
		rnwlTrmEffDt = "contains=" + _termEffDt.substring(0, _termEffDt.indexOf("T"));
		rnwlTrmDt = "contains=" + _termEffDt.substring(0, _termEffDt.indexOf("T"));
		plcyEffDt = "contains=" + _plcyEffDt.substring(0, _plcyEffDt.indexOf("T"));
		plcyExprDt = "contains=" + _plcyExprDt.substring(0, _plcyExprDt.indexOf("T"));
		termExprDt = "contains=" + _termExprDt.substring(0, _termExprDt.indexOf("T"));
		termExprDtPA = "contains=" + _termExprDtPA.substring(0, _termExprDtPA.indexOf("T"));

		// Get the coverage information from premium and coverage page
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		switch (getState()) {
			case "PA":
				for (TestData td : premiumAndCoveragesTab.getRatingDetailsVehiclesData()) {
					vehClsnDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Collision Deductible"))));
					vehCompDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Comprehensive Deductible"))));
					vehSftyGlsDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Full Safety Glass"))));
					vehNwAddPrtcDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("New Car Added Protection Coverage"))));
					vehRntlReimbsDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Rental Reimbursement Limit"))));
					vehTwgLbrDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Towing and Labor Coverage"))));
					vehLnPrtcDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Vehicle Loan/Lease Coverage"))));
					plcyPdEaOcc.add(DataProviderFactory.dataOf("TextField", td.getValue("Property Damage Liability").replace("$", "").replace(",", "")));
					plcyMedBenEaPers.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Medical Expenses"))));
					plcyFnrlExpBenEaOcc.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Funeral Expenses"))));
					plcyAcdntDeadBenEaOcc.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Accidental Death Benefits"))));
					plcySpclEqpmtTotAmt.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Special Equipment Limit"))));
					fPBCovTyp.add(DataProviderFactory.dataOf("TextField", td.getValue("First Party Benefit").toUpperCase()));
					tortTrshldTyp.add(DataProviderFactory.dataOf("TextField", td.getValue("Tort Threshold").substring(0, td.getValue("Tort Threshold").indexOf(" T")).toUpperCase()));
				}
				break;
			default:
				for (TestData td : premiumAndCoveragesTab.getRatingDetailsVehiclesData()) {
					vehClsnDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Collision Deductible"))));
					vehCompDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Comprehensive Deductible"))));
					vehSftyGlsDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Full Safety Glass"))));
					vehNwAddPrtcDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("New Car Added Protection Coverage"))));
					vehRntlReimbsDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Rental Reimbursement Limit"))));
					vehTwgLbrDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Towing and Labor Coverage"))));
					vehLnPrtcDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Vehicle Loan/Lease Coverage"))));
					plcyMpEaPers.add(DataProviderFactory.dataOf("TextField", td.getValue("Medical Payments").replace("$", "").replace(",", "")));
					plcyPdEaOcc.add(DataProviderFactory.dataOf("TextField", td.getValue("Property Damage Liability").replace("$", "").replace(",", "")));
				}
				break;
		}

		 PremiumAndCoveragesTab.RatingDetailsView.close();

		netWrtPrem = formatValue(PremiumAndCoveragesTab.tableAAAPremiumSummary.getRow(1).getCell("Actual Premium").getValue());
		allVehTotPrem = formatValue(PremiumAndCoveragesTab.totalTermPremium.getValue());

		switch (getState()) {
			case "AZ":
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
					vehTotPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Total Vehicle Term Premium"))));
				}
				break;

			case "IN":
				for (TestData td : premiumAndCoveragesTab.getTermPremiumByVehicleData()) {
					vehBdyInjPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Bodily Injury Liability"))));
					vehPDPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Property Damage Liability"))));
					vehUMUIMBPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Uninsured/Underinsured Motorist Bodily Injury"))));
					vehUMPDPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Uninsured Motorist Property Damage Limit"))));
					vehMPPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Medical Payments"))));
					vehClsnPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Collision Deductible"))));
					vehCompPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Comprehensive Deductible"))));
					vehRntlReimbsPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Rental Reimbursement"))));
					vehTwgLbrPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Towing and Labor Coverage"))));
					vehTotPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Total Vehicle Term Premium"))));
				}
				break;
			case "OK":
				for (TestData td : premiumAndCoveragesTab.getTermPremiumByVehicleData()) {
					vehBdyInjPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Bodily Injury Liability"))));
					vehPDPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Property Damage Liability"))));
					vehUMUIMBPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Uninsured/Underinsured Motorist Bodily Injury"))));
					genPlcyCovPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Uninsured/Underinsured Motorist Bodily Injury"))));
					vehMPPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Medical Payments"))));
					vehClsnPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Collision Deductible"))));
					vehCompPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Comprehensive Deductible"))));
					vehRntlReimbsPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Rental Reimbursement"))));
					vehTwgLbrPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Towing and Labor Coverage"))));
					Dollar _vehTotPrem = new Dollar(td.getValue("Total Vehicle Term Premium"));
					String genplcycovprem = td.getValue("Uninsured/Underinsured Motorist Bodily Injury");
					Dollar _genPlcyCovPrem = new Dollar(genplcycovprem.contains("No Coverage") ? "0.00" : genplcycovprem.replace("\n", "").replace("$", ""));
					Dollar _allVehTotPrem = new Dollar(PremiumAndCoveragesTab.totalTermPremium.getValue());
					vehCovPrem = formatValue(_allVehTotPrem.subtract(_genPlcyCovPrem).toString());
					vehTotPrem.add(DataProviderFactory.dataOf("TextField", formatValue(_vehTotPrem.subtract(_genPlcyCovPrem).toString())));
				}
				break;
			case "PA":
				for (TestData td : premiumAndCoveragesTab.getTermPremiumByVehicleData()) {
					vehBdyInjPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Bodily Injury Liability"))));
					vehPDPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Property Damage Liability"))));
					//vehUMStckPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Uninsured Motorists Bodily Injury"))));
					//vehUMNonStckPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Uninsured Motorists Bodily Injury"))));
					vehUIMBStckPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Underinsured Motorists Bodily Injury"))));
					vehUIMBNonStckPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Underinsured Motorists Bodily Injury"))));
					vehClsnPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Collision Deductible"))));
					vehCompPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Comprehensive Deductible"))));
					vehRntlReimbsPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Rental Reimbursement"))));
					vehTwgLbrPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Towing and Labor Coverage"))));
					vehTotPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Total Vehicle Term Premium"))));
					vehMedBenPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Medical Expenses"))));
					vehFnrlExpBenPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Funeral Benefits"))));
					vehAcdntDeadBenPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Accidental Death Benefits (ADB)"))));
					vehExtrMedBenPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Extraordinary Medical Expense Benefits"))));
				}
				break;
		}

		//		plcyAutoDeadBenPrem = formatValue(PremiumAndCoveragesTab.tableFormsSummary.getRow("Forms", "ADBE").getCell(2).getValue());

		// Store the value for total fee from table tablefeesSummary
		Dollar _plcyTotFee = new Dollar(0);
		if (PremiumAndCoveragesTab.tablefeesSummary.isPresent()) {
			for (int i = 1; i <= PremiumAndCoveragesTab.tablefeesSummary.getRowsCount(); i++) {
				_plcyTotFee = _plcyTotFee.add(new Dollar(PremiumAndCoveragesTab.tablefeesSummary.getRow(i).getCell(2).getValue()));
			}
			plcyTotFee = _plcyTotFee.toString().replace("$", "").replace(",", "");
			plcyTotPrem = new Dollar(PremiumAndCoveragesTab.totalTermPremium.getValue()).add(_plcyTotFee).toString().replace("$", "").replace(",", "");
		} else {
			plcyTotFee = "0.00";
			plcyTotPrem = new Dollar(PremiumAndCoveragesTab.totalTermPremium.getValue()).toString().replace("$", "").replace(",", "");
		}
		Tab.buttonTopCancel.click();
	}

	private void storeBillingData() {
		BillingSummaryPage.open();
		for (int i = 1; i <= BillingSummaryPage.tableInstallmentSchedule.getAllRowsCount(); i++) {
			if ("Installment".equals(BillingSummaryPage.tableInstallmentSchedule.getRow(i).getCell(BillingConstants.BillingInstallmentScheduleTable.DESCRIPTION).getValue()) && "Unbilled"
					.equals(BillingSummaryPage.tableInstallmentSchedule.getRow(i).getCell(BillingConstants.BillingInstallmentScheduleTable.BILLED_STATUS).getValue())) {
				dueAmount.add(DataProviderFactory.dataOf("TextField", formatValue(BillingSummaryPage.getInstallmentAmount(i).add(2).toString())));
				String _installmentDueDate = BillingSummaryPage.getInstallmentDueDate(i).toString();
				installmentDueDate.add(DataProviderFactory.dataOf("DateTimeField", "contains=" + _installmentDueDate.substring(0, _installmentDueDate.indexOf("T"))));
			}
		}
	}

	private String formatValue(String value) {
		return value.contains("No Coverage") ? "0.00" : new Dollar(value.replace("\n", "")).toString().replace("$", "").replace(",", "");
	}

	//	Clear all the list value
	private void clearList() {
		vehClsnDed.clear();
		vehCompDed.clear();
		vehBdyInjPrem.clear();
		vehPDPrem.clear();
		vehUMPrem.clear();
		vehUIMBPrem.clear();
		vehMPPrem.clear();
		vehClsnPrem.clear();
		vehCompPrem.clear();
		vehSftyGlsDed.clear();
		vehNwAddPrtcDed.clear();
		vehRntlReimbsDed.clear();
		vehTwgLbrDed.clear();
		vehLnPrtcDed.clear();
		plcyMpEaPers.clear();
		plcyPdEaOcc.clear();
		vehRntlReimbsPrem.clear();
		vehTwgLbrPrem.clear();
		dueAmount.clear();
		installmentDueDate.clear();
		vehTotPrem.clear();
		vehUMUIMBPrem.clear();
		vehMedBenPrem.clear();
		vehExtrMedBenPrem.clear();
		vehUMNonStckPrem.clear();
		vehUIMBNonStckPrem.clear();
		vehUMStckPrem.clear();
		vehUIMBStckPrem.clear();
		plcySpclEqpmtTotAmt.clear();
		genPlcyCovPrem.clear();
	}

}
