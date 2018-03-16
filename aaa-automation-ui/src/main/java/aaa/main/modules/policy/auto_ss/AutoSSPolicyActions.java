/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.main.modules.policy.auto_ss;

import org.openqa.selenium.By;
import aaa.common.AbstractAction;
import aaa.common.Tab;
import aaa.common.Workspace;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.modules.policy.PolicyActions;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.CreateQuoteVersionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.views.*;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.TextBox;

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
        
        @Override
        public AbstractAction performAndExit(TestData td) {
            start();
            getView().fill(td);
            submit();
            Tab.buttonSaveAndExit.click();
            
            if (Page.dialogConfirmation.isPresent() && Page.dialogConfirmation.isVisible()) {
            	new TextBox(By.xpath("//textarea[@id='policyDataGatherForm:newbusinessnotes']")).setValue("save as incomplete");
            	Page.dialogConfirmation.confirm();
            }
            
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

		@Override
		public AbstractAction performAndExit(TestData td) {
			start();
			getView().fill(td);
			submit();
			Tab.buttonSaveAndExit.click();

			if (Page.dialogConfirmation.isPresent() && Page.dialogConfirmation.isVisible()) {
				new TextBox(By.xpath("//textarea[@id='policyDataGatherForm:newbusinessnotes']")).setValue("save as incomplete");
				Page.dialogConfirmation.confirm();
			}

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
            if(Tab.buttonSaveAndExit.isVisible()) {
                Tab.buttonSaveAndExit.click();
            }
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

