package aaa.modules.regression.sales.auto_ca.select.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.actiontabs.EndorsementActionTab;
import aaa.modules.regression.sales.template.functional.TestEndorsementActionRulesAbstract;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestEndorsementActionRules extends TestEndorsementActionRulesAbstract {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}


	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-5860")
	public void pas5860_EndorsementActionTabRules(@Optional("CA") String state) {

		CustomAssert.enableSoftMode();
		pas5860_EndorsementActionTabRulesHelper();
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	@Override
	protected Tab getEndorsementActionTab() {
		return new EndorsementActionTab();
	}

	@Override
	protected AssetDescriptor<TextBox> getEndorsementDate() {
		return AutoCaMetaData.EndorsementActionTab.ENDORSEMENT_DATE;
	}

	@Override
	protected AssetDescriptor<ComboBox> getEndorsementReason() {
		return AutoCaMetaData.EndorsementActionTab.ENDORSEMENT_REASON;
	}

	@Override
	protected AssetDescriptor<TextBox> getEndorsementOtherReason() {
		return AutoCaMetaData.EndorsementActionTab.OTHER;
	}
}
