/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.policy.pup;

import aaa.common.AbstractAction;
import aaa.common.Workspace;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.PolicyActions;
import aaa.main.modules.policy.pup.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.pup.defaulttabs.CreateQuoteVersionTab;
import aaa.main.modules.policy.pup.views.CancelNoticeView;
import aaa.main.modules.policy.pup.views.CancellationView;
import aaa.main.modules.policy.pup.views.ChangeBrokerView;
import aaa.main.modules.policy.pup.views.ChangePendedEndEffDateView;
import aaa.main.modules.policy.pup.views.CopyFromPolicyView;
import aaa.main.modules.policy.pup.views.CopyFromQuoteView;
import aaa.main.modules.policy.pup.views.DeclineByCompanyView;
import aaa.main.modules.policy.pup.views.DeclineByCustomerView;
import aaa.main.modules.policy.pup.views.DefaultView;
import aaa.main.modules.policy.pup.views.DeleteCancelNoticeView;
import aaa.main.modules.policy.pup.views.DeletePendedTransactionView;
import aaa.main.modules.policy.pup.views.DoNotRenewView;
import aaa.main.modules.policy.pup.views.EndorseView;
import aaa.main.modules.policy.pup.views.GenerateOnDemandDocumentView;
import aaa.main.modules.policy.pup.views.ManualRenewView;
import aaa.main.modules.policy.pup.views.ManualRenewalWithOrWithoutLapseView;
import aaa.main.modules.policy.pup.views.NonPremiumBearingEndorsementView;
import aaa.main.modules.policy.pup.views.ProposeView;
import aaa.main.modules.policy.pup.views.ReinstatementView;
import aaa.main.modules.policy.pup.views.RemoveDoNotRenewView;
import aaa.main.modules.policy.pup.views.RemoveManualRenewView;
import aaa.main.modules.policy.pup.views.RenewView;
import aaa.main.modules.policy.pup.views.RescindCancellationView;
import aaa.main.modules.policy.pup.views.RewriteView;
import aaa.main.modules.policy.pup.views.RollBackEndorsementView;
import aaa.main.modules.policy.pup.views.RollOnChangesView;
import aaa.main.modules.policy.pup.views.SuspendQuoteView;
import toolkit.datax.TestData;

/**
 * Set of concrete actions for a specific entity type.
 * Modify this class (and its nested classes) if concrete actions have to be customized.
 * @category Generated
 */
public final class PupPolicyActions {
    private PupPolicyActions() {}

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

    public static class PriorTermEndorsement extends PolicyActions.PriorTermEndorsement {
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

    public static class Bind extends PolicyActions.Bind {
        @Override
        public Workspace getView() {
            return new CancellationView();
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

        @Override
        public AbstractAction start() {
            NavigationPage.setActionAndGo(getName());
            new CreateQuoteVersionTab().submitIfPresent();
            return this;
        }

        public AbstractAction start(TestData td) {
            NavigationPage.setActionAndGo(getName());
            CreateQuoteVersionTab versionTab = new CreateQuoteVersionTab();
            versionTab.fillTab(td);
            versionTab.submitTab();
            return this;
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

    public static class DeletePendingTransaction extends PolicyActions.DeletePendedTransaction {
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

    public static class PolicyDocGenFlow extends PolicyActions.PolicyDocGen {
        @Override
        public Workspace getView() {
            return new GenerateOnDemandDocumentView();
        }
        
        @Override
        public AbstractAction submit() {
            getView().getTab(GenerateOnDemandDocumentActionTab.class).submitTab();
            return this;
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

    public static class QuoteDocGenFlow extends PolicyActions.QuoteDocGen {
        @Override
        public Workspace getView() {
            return new GenerateOnDemandDocumentView();
        }
        
        @Override
        public AbstractAction submit() {
            getView().getTab(GenerateOnDemandDocumentActionTab.class).submitTab();
            return this;
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

    public static class ManualRenewalWithOrWithoutLapse extends PolicyActions.ManualRenewalWithOrWithoutLapse {
        @Override
        public Workspace getView() {
            return new ManualRenewalWithOrWithoutLapseView();
        }
    }
    
    //TODO remove next actions if not used in AAA
    public static class NonPremiumBearingEndorsement extends PolicyActions.NonPremiumBearingEndorsement {

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
        public Workspace getView() {
            return new RescindCancellationView();
        }
    }

    public static class InitiateHOQuote extends PolicyActions.InitiateHOQuote {
        @Override
        public Workspace getView() {
            return new aaa.main.modules.policy.home_ss.views.DefaultView();
        }

        @Override
        public AbstractAction start() {
            NavigationPage.setActionAndGo(getName());
            return this;
        }
    }
}
