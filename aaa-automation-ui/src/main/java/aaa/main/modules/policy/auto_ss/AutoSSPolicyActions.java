/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss;

import aaa.common.AbstractAction;
import aaa.common.Tab;
import aaa.common.Workspace;
import aaa.main.modules.policy.PolicyActions;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.views.BindView;
import aaa.main.modules.policy.auto_ss.views.CancelNoticeView;
import aaa.main.modules.policy.auto_ss.views.CancellationView;
import aaa.main.modules.policy.auto_ss.views.ChangeBorkerView;
import aaa.main.modules.policy.auto_ss.views.ChangePendedEndEffDateView;
import aaa.main.modules.policy.auto_ss.views.CopyFromPolicyView;
import aaa.main.modules.policy.auto_ss.views.CopyFromQuoteView;
import aaa.main.modules.policy.auto_ss.views.DeclineByCompanyView;
import aaa.main.modules.policy.auto_ss.views.DeclineByCustomerView;
import aaa.main.modules.policy.auto_ss.views.DefaultView;
import aaa.main.modules.policy.auto_ss.views.DeletePendedTransactionView;
import aaa.main.modules.policy.auto_ss.views.DoNotRenewView;
import aaa.main.modules.policy.auto_ss.views.EndorseView;
import aaa.main.modules.policy.auto_ss.views.GenerateOnDemandDocumentPolicyView;
import aaa.main.modules.policy.auto_ss.views.GenerateOnDemandDocumentQuoteView;
import aaa.main.modules.policy.auto_ss.views.ManualRenewView;
import aaa.main.modules.policy.auto_ss.views.NonPremiumBearingEndorsementView;
import aaa.main.modules.policy.auto_ss.views.ProposeView;
import aaa.main.modules.policy.auto_ss.views.ReinstatementView;
import aaa.main.modules.policy.auto_ss.views.RemoveCancelNoticeView;
import aaa.main.modules.policy.auto_ss.views.RemoveDoNotRenewView;
import aaa.main.modules.policy.auto_ss.views.RemoveManualRenewView;
import aaa.main.modules.policy.auto_ss.views.RenewView;
import aaa.main.modules.policy.auto_ss.views.RescindCancellationView;
import aaa.main.modules.policy.auto_ss.views.RewriteView;
import aaa.main.modules.policy.auto_ss.views.RollBackEndorsementView;
import aaa.main.modules.policy.auto_ss.views.RollOnChangesView;
import aaa.main.modules.policy.auto_ss.views.SpinView;
import aaa.main.modules.policy.auto_ss.views.SplitView;
import aaa.main.modules.policy.auto_ss.views.SuspendQuoteView;
import aaa.main.modules.policy.auto_ss.views.UpdateRulesOverrideView;
import toolkit.datax.TestData;

/**
 * Set of concrete actions for a specific entity type.
 * Modify this class (and its nested classes) if concrete actions have to be customized.
 * @category Generated
 */
public final class AutoSSPolicyActions {
    private AutoSSPolicyActions() {}

    public static class Endorse extends PolicyActions.Endorse {
        @Override
        public Workspace getView() {
            return new EndorseView();
        }
        
        @Override
        public AbstractAction performAndFill(TestData td) {
            start();
            getView().fill(td);
            submit();
            new DataGather().getView().fill(td);
            return this;
        }
    }

    public static class Renew extends PolicyActions.Renew {
	    // private TextBox textBoxRenewalDate = getView().getTab(RenewActionTab.class).getAssetList().getAsset(
	    //         AutoSSMetaData.RenewActionTab.RENEWAL_DATE.getLabel(), TextBox.class);

        @Override
        public Workspace getView() {
            return new RenewView();
        }
        
        @Override
        public AbstractAction performAndFill(TestData td) {
            start();
	        //getView().fill(td);
	        new DataGather().getView().fill(td);
	        submit();
	        return this;
        }

        @Override
        public AbstractAction submit() {
	        //    if (textBoxRenewalDate.isPresent() && textBoxRenewalDate.isVisible()) {
	        //        return super.submit();
	        //    }
	        Tab.buttonSaveAndExit.click();
	        return this;

        }
    }

    public static class Rewrite extends PolicyActions.Rewrite {
        @Override
        public Workspace getView() {
            return new RewriteView();
        }
    }

    public static class Bind extends PolicyActions.Bind {
        @Override
        public Workspace getView() {
            return new BindView();
        }

        @Override
        public AbstractAction submit() {
        	DocumentsAndBindTab.btnPurchase.click();
        	DocumentsAndBindTab.confirmPurchase.confirm();
            return this;
        }
    }

    public static class Cancel extends PolicyActions.Cancel {
        @Override
        public Workspace getView() {
            return new CancellationView();
        }
    }

    public static class CancelNotice extends PolicyActions.CancelNotice {
        @Override
        public Workspace getView() {
            return new CancelNoticeView();
        }
    }

    public static class ChangeBrokerRequest extends PolicyActions.ChangeBrokerRequest {
        @Override
        public Workspace getView() {
            return new ChangeBorkerView();
        }
    }

    public static class CopyQuote extends PolicyActions.CopyQuote {
        @Override
        public Workspace getView() {
            return new CopyFromQuoteView();
        }
    }

    public static class DataGather extends PolicyActions.DataGather {
        @Override
        public Workspace getView() {
            return new DefaultView();
        }
    }

    public static class DeclineByCompanyQuote extends PolicyActions.DeclineByCompanyQuote {
        @Override
        public Workspace getView() {
            return new DeclineByCompanyView();
        }
    }

    public static class DeclineByCustomerQuote extends PolicyActions.DeclineByCustomerQuote {
        @Override
        public Workspace getView() {
            return new DeclineByCustomerView();
        }
    }

    public static class DeleteCancelNotice extends PolicyActions.DeleteCancelNotice {
        @Override
        public Workspace getView() {
            return new RemoveCancelNoticeView();
        }
    }

    public static class DeletePendedTransaction extends PolicyActions.DeletePendedTransaction {
        @Override
        public Workspace getView() {
            return new DeletePendedTransactionView();
        }
    }
    
    public static class DeletePendingRenwals extends PolicyActions.DeletePendingRenwals {
        @Override
        public Workspace getView() {
            return new DeletePendedTransactionView();
        }
    }

    public static class DoNotRenew extends PolicyActions.DoNotRenew {
        @Override
        public Workspace getView() {
            return new DoNotRenewView();
        }
    }

    public static class ManualRenew extends PolicyActions.ManualRenew {
        @Override
        public Workspace getView() {
            return new ManualRenewView();
        }
    }

    public static class PolicyCopy extends PolicyActions.PolicyCopy {
        @Override
        public Workspace getView() {
            return new CopyFromPolicyView();
        }
    }

    public static class PolicyDocGen extends PolicyActions.PolicyDocGen {
        @Override
        public String getName() {
            return "Generate On Demand Document";
        }

        @Override
        public Workspace getView() {
            return new GenerateOnDemandDocumentPolicyView();
        }
    }

    public static class PolicyInquiry extends PolicyActions.PolicyInquiry {
        @Override
        public Workspace getView() {
            return new DefaultView();
        }
    }

    public static class PolicySpin extends PolicyActions.PolicySpin {
        @Override
        public Workspace getView() {
            return new SpinView();
        }
    }

    public static class PolicySplit extends PolicyActions.PolicySplit {
        @Override
        public Workspace getView() {
            return new SplitView();
        }
    }

    public static class Propose extends PolicyActions.Propose {
        @Override
        public Workspace getView() {
            return new ProposeView();
        }
    }

    public static class QuoteDocGen extends PolicyActions.QuoteDocGen {
        @Override
        public String getName() {
            return "Generate On Demand Document";
        }

        @Override
        public Workspace getView() {
            return new GenerateOnDemandDocumentQuoteView();
        }
    }

    public static class QuoteInquiry extends PolicyActions.QuoteInquiry {
        @Override
        public Workspace getView() {
            return new DefaultView();
        }
    }

    public static class Reinstate extends PolicyActions.Reinstate {
        @Override
        public Workspace getView() {
            return new ReinstatementView();
        }
    }

    public static class RemoveDoNotRenew extends PolicyActions.RemoveDoNotRenew {
        @Override
        public Workspace getView() {
            return new RemoveDoNotRenewView();
        }
    }

    public static class RemoveManualRenew extends PolicyActions.RemoveManualRenew {
        @Override
        public Workspace getView() {
            return new RemoveManualRenewView();
        }
    }
    
    public static class RollBackEndorsement extends PolicyActions.RollBackEndorsement {
        @Override
        public Workspace getView() {
            return new RollBackEndorsementView();
        }
    }

    public static class RollOn extends PolicyActions.RollOn {
        @Override
        public Workspace getView() {
            return new RollOnChangesView();
        }
    }

    public static class SuspendQuote extends PolicyActions.SuspendQuote {
        @Override
        public Workspace getView() {
            return new SuspendQuoteView();
        }
    }

	public static class UpdateRulesOverride extends PolicyActions.UpdateRulesOverride {
		@Override
		public Workspace getView() {
			return new UpdateRulesOverrideView();
		}
	}

	//TODO Remove next actions if not used in AAA
	public static class NonPremiumBearingEndorsement extends PolicyActions.NonPremiumBearingEndorsement {
		@Override
        public String getName() {
            return "Update Insured/Interest Info";
        }

        @Override
        public Workspace getView() {
            return new NonPremiumBearingEndorsementView();
        }
    }

    public static class PendedEndorsementChange extends PolicyActions.PendedEndorsementChange {
        @Override
        public String getName() {
            return "Change pended End. Eff. Date";
        }

        @Override
        public Workspace getView() {
            return new ChangePendedEndEffDateView();
        }
    }
    
    public static class RemoveSuspendQuote extends PolicyActions.RemoveSuspendQuote {
        @Override
        public String getName() {
            return "Remove Suspense";
        }

        @Override
        public Workspace getView() {
            return new DefaultView();
        }
    }

    public static class RescindCancellation extends PolicyActions.RescindCancellation {
        @Override
        public String getName() {
            return "Rescind Cancellation";
        }

        @Override
        public Workspace getView() {
            return new RescindCancellationView();
        }
    }
}
