/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ca;

import aaa.common.AbstractAction;
import aaa.common.Workspace;
import aaa.main.modules.policy.PolicyActions;
import aaa.main.modules.policy.home_ca.views.CancelNoticeView;
import aaa.main.modules.policy.home_ca.views.CancellationView;
import aaa.main.modules.policy.home_ca.views.ChangeBrokerView;
import aaa.main.modules.policy.home_ca.views.ChangeReinstatementLapsePeriodView;
import aaa.main.modules.policy.home_ca.views.CopyPolicyView;
import aaa.main.modules.policy.home_ca.views.CopyQuoteView;
import aaa.main.modules.policy.home_ca.views.DataGatheringEndorsementView;
import aaa.main.modules.policy.home_ca.views.DeclineByCompanyView;
import aaa.main.modules.policy.home_ca.views.DeclineByCustomerView;
import aaa.main.modules.policy.home_ca.views.DefaultView;
import aaa.main.modules.policy.home_ca.views.DeleteCancelNoticeView;
import aaa.main.modules.policy.home_ca.views.DeletePendedTransactionView;
import aaa.main.modules.policy.home_ca.views.DoNotRenewView;
import aaa.main.modules.policy.home_ca.views.EndorseView;
import aaa.main.modules.policy.home_ca.views.ManualRenewFlagView;
import aaa.main.modules.policy.home_ca.views.NonPremiumBearingEndorsementView;
import aaa.main.modules.policy.home_ca.views.PolicyRenewalChangeLapseView;
import aaa.main.modules.policy.home_ca.views.ProposeView;
import aaa.main.modules.policy.home_ca.views.QuoteRenewalChangeLapseView;
import aaa.main.modules.policy.home_ca.views.ReinstateView;
import aaa.main.modules.policy.home_ca.views.RemoveDoNotRenewView;
import aaa.main.modules.policy.home_ca.views.RemoveManualRenewView;
import aaa.main.modules.policy.home_ca.views.RenewView;
import aaa.main.modules.policy.home_ca.views.RescindCancellationView;
import aaa.main.modules.policy.home_ca.views.RollBackEndorsementView;
import aaa.main.modules.policy.home_ca.views.RewriteView;
import aaa.main.modules.policy.home_ca.views.SuspendQuoteView;
import toolkit.datax.TestData;

/**
 * Set of concrete actions for a specific entity type.
 * Modify this class (and its nested classes) if concrete actions have to be customized.
 * @category Generated
 */
public final class HomeCaPolicyActions {
    private HomeCaPolicyActions() {}

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
            new DataGatheringEndorsementView().fill(td);
            return this;
        }
    }

    public static class Renew extends PolicyActions.Renew {
        @Override
        public Workspace getView() {
            return new RenewView();
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
            return new ChangeBrokerView();
        }
    }

    public static class ChangeReinstatementLapse extends PolicyActions.ChangeReinstatementLapse {
        @Override
        public Workspace getView() {
            return new ChangeReinstatementLapsePeriodView();
        }
    }

    public static class CopyQuote extends PolicyActions.CopyQuote {
        @Override
        public Workspace getView() {
            return new CopyQuoteView();
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
            return new DeleteCancelNoticeView();
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
            return new ManualRenewFlagView();
        }
    }

    public static class PolicyInquiry extends PolicyActions.PolicyInquiry {
        @Override
        public Workspace getView() {
            return new DefaultView();
        }
    }

    public static class Propose extends PolicyActions.Propose {
        @Override
        public Workspace getView() {
            return new ProposeView();
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
            return new ReinstateView();
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
    
    public static class Rewrite extends PolicyActions.Rewrite {
        @Override
        public Workspace getView() {
            return new RewriteView();
        }
    }
    
    public static class RollBackEndorsement extends PolicyActions.RollBackEndorsement {
        @Override
        public Workspace getView() {
            return new RollBackEndorsementView();
        }
    }

    public static class SuspendQuote extends PolicyActions.SuspendQuote {
        @Override
        public Workspace getView() {
            return new SuspendQuoteView();
        }
    }

    public static class PolicyCopy extends PolicyActions.PolicyCopy {
        @Override
        public Workspace getView() {
            return new CopyPolicyView();
        }
    }
    
    //TODO Delete next actions if not used in AAA:
    public static class ChangeRenewalQuoteLapse extends PolicyActions.ChangeRenewalQuoteLapse {
        @Override
        public String getName() {
            return "Change Renewal Lapse Eff. Date";
        }

        @Override
        public Workspace getView() {
            return new QuoteRenewalChangeLapseView();
        }
    }

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

    public static class PolicyChangeRenewalLapse extends PolicyActions.PolicyChangeRenewalLapse {
        @Override
        public String getName() {
            return "Change Renewal Lapse Period";
        }

        @Override
        public Workspace getView() {
            return new PolicyRenewalChangeLapseView();
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
