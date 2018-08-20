package aaa.modules.regression.sales.template.functional;

import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import java.util.Arrays;

public class TestEndorsementTabTemplate extends TestEndorsementsTabAbstract {

	protected void privilegedAddEditRemoveNB(String... endorsementFormId){

		// Create NB Quote fill up to Endorsements
		createQuoteAndFillUpTo(EndorsementTab.class);

		// For each endorsement Form run Assertions
		Arrays.stream(endorsementFormId).forEach(this::assertEndorsementEditable);
	}

	protected void privilegedAddEditRemoveEndorsement(String... endorsementFormId){

		// Create Policy and Endorse
		openAppAndCreatePolicy();
		initiateEndorsementTx();

		// For each endorsement Form run Assertions
		Arrays.stream(endorsementFormId).forEach(this::assertEndorsementEditable);
	}

	protected void privilegedAddEditRemoveRenewal(String... endorsementFormId){

		// Create Policy and Renew
		openAppAndCreatePolicy();
		initiateRenewalTx();

		// For each endorsement Form run Assertions
		Arrays.stream(endorsementFormId).forEach(this::assertEndorsementEditable);
	}

	private void assertEndorsementEditable(String endorsementFormId) {
		// Add Endorsement and Check its added
		addEndorsementForm(endorsementFormId);
		// Edit and check its saved
		editEndorsementAndVerify(endorsementFormId);
		// Remove and check it is removed
		removeEndorsementAndVerify(endorsementFormId);
	}
}
