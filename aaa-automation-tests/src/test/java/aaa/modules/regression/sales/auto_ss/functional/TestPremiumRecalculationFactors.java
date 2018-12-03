package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.stream.IntStream;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(statesExcept = Constants.States.CA)
public class TestPremiumRecalculationFactors extends AutoSSBaseTest {

	private DriverTab driverTab = new DriverTab();
	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private ErrorTab errorTab = new ErrorTab();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

	/**
	 * @author Josh Carpenter
	 * @name Test that system forces premium recalculation after changing marital status during NB Quote
	 * @scenario
	 * 1. Create Customer
	 * 2. Initiate Auto SS quote
	 * 3. Fill with marital status of single
	 * 4. Calculate premium
	 * 5. Order Driver Activity Reports
	 * 6. Navigate to Driver tab
	 * 7. Change status to 'Married'
	 * 8. Navigate to Documents & Bind tab directly
	 * 9. Bind quote
	 * 10. Validate error message
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-16430")
	public void pas16430_testMaritalStatusNB(@Optional("") String state) {

		// Create a quote, fill with up to DAR tab (marital status of driver 'Single'
		createQuoteAndFillUpTo(getPolicyTD(), DriverActivityReportsTab.class);

		// Navigate back to Driver tab and adjust to 'Single'
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.MARITAL_STATUS).setValue("Single");

		// Navigate directly to Documents & Bind tab, attempt to bind, and validate
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();
		assertThat(policyNeedsRated()).isTrue();

	}

	/**
	 * @author Josh Carpenter
	 * @name Test that system forces premium recalculation after changing marital status during Endorsement
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Auto SS Policy with marital status = 'Single'
	 * 3. Initiate endorsement
	 * 4. Change marital status to 'Married'
	 * 5. Calculate premium
	 * 6. Navigate to Driver tab and change status back to 'Single'
	 * 7. Navigate to Documents & Bind tab directly
	 * 8. Bind endorsement
	 * 9. Validate error message
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = "PAS-16430")
	public void pas16430_testMaritalStatusEndorsement(@Optional("") String state) {

		// Create policy with marital status = 'Married' and initiate endorsement
		openAppAndCreatePolicy(getPolicyTD());
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		calculatePremiumAndChangeMaritalStatus();
		documentsAndBindTab.submitTab();
		assertThat(policyNeedsRated()).isTrue();

	}

	/**
	 * @author Josh Carpenter
	 * @name Test that system forces premium recalculation after changing marital status during Renewal
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Auto SS Policy with marital status = 'Single'
	 * 3. Create renewal image
	 * 4. Change marital status to 'Married'
	 * 5. Calculate premium
	 * 6. Navigate to Driver tab and change status back to 'Single'
	 * 7. Navigate to Documents & Bind tab directly
	 * 8. Save renewal image
	 * 9. Validate error message
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Renewal.AUTO_SS, testCaseId = "PAS-16430")
	public void pas16430_testMaritalStatusRenewal(@Optional("") String state) {

		// Create policy with marital status = 'Single' and initiate endorsement
		openAppAndCreatePolicy(getPolicyTD());
		policy.renew().perform();

		// Calculate premium and update marital status
		calculatePremiumAndChangeMaritalStatus();

		// Validate policy is in data gathering mode
		assertThat(documentsAndBindTab.getPolicyStatus()).contains(ProductConstants.PolicyStatus.DATA_GATHERING);

	}

	private void calculatePremiumAndChangeMaritalStatus() {
		premiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.MARITAL_STATUS).setValue("Single");
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
	}

	private boolean policyNeedsRated() {
		return IntStream.rangeClosed(1, errorTab.tableErrors.getRows().size()).anyMatch(i ->
				errorTab.tableErrors.getRow(i).getCell(ErrorEnum.ErrorsColumn.MESSAGE.get()).getValue().contains(ErrorEnum.Errors.ERROR_POLICY_NOT_RATED.getMessage()));
	}
}
