/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.home_ss;

import aaa.common.AbstractAction;
import aaa.common.Workspace;
import aaa.main.modules.policy.PolicyActions;
import aaa.main.modules.policy.home_ss.views.*;
import toolkit.datax.TestData;

/**
 * Set of concrete actions for a specific entity type.
 * Modify this class (and its nested classes) if concrete actions have to be customized.
 * @category Generated
 */
public final class HomeSSPolicyActions {
    private HomeSSPolicyActions() {}

    public static class Endorse extends PolicyActions.Endorse {
        @Override
        public AbstractAction performAndFill(TestData td) {
            start();
            getView().fill(td);
            submit();
            new DataGatheringEndorsementView().fill(td);
            return this;
        }
        
        @Override
        public Workspace getView() {
            return new EndorseView();
        }
    }

    public static class Renew extends PolicyActions.Renew {
        @Override
        public AbstractAction performAndFill(TestData td) {
            start();
            getView().fill(td);
            submit();
            new DataGather().getView().fill(td);
            return this;
        }
        
        @Override
        public Workspace getView() {
            return new RenewView();
        }
    }

    public static class Bind extends PolicyActions.Bind {
        @Override
        public Workspace getView() {
            return new BindView();
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
            return new ChangeReinstateLapseView();
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

    /* public static class Issue extends PolicyActions.Issue {
       @Override
        public Workspace getView() {
            return new IssueView();
        }

        @Override
        public Issue perform(TestData td) {
            start();
            getView().fill(td);
            Tab.buttonPurchase.click();
            Page.dialogConfirmation.confirm();

            Table tableOverrideErrors = new Table(By.id("errorsForm:msgList"));
            if (tableOverrideErrors.isPresent() && tableOverrideErrors.isVisible()) {
                for (int i = 1; i <= tableOverrideErrors.getRowsCount(); i++) {
                    tableOverrideErrors.getRow(i).getCell(1).controls.checkBoxes.getFirst().setValue(true);
                    tableOverrideErrors.getRow(i).getCell(6).controls.radioGroups.getFirst().setValue("Life");
                    tableOverrideErrors.getRow(i).getCell(7).controls.comboBoxes.getFirst().setValue("index=1");
                }
                new Button(By.xpath("//input[contains(@id, 'override') and @value='Override']")).click();

                Page.buttonPurchase.click();
                Page.dialogConfirmation.confirm();
            }

            //TODO(vmarkouski): there is no billing screen for PREC_HO endorsements
            if (Page.buttonFinish.isPresent() && Page.buttonFinish.isVisible()) {
                new BillingAccount().create(td);
                Page.buttonFinish.click();
            }
            return this;
        }

        @Override
        public Issue submit() {
            return this;
        
    }}*/

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
    
    //TODO Remove if not used in AAA
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
