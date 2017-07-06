/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.cem.campaigns;

import org.openqa.selenium.By;

import aaa.admin.modules.cem.campaigns.actiontabs.CopyCampaignActionTab;
import aaa.admin.modules.cem.campaigns.actiontabs.CreateChildCampaignActionTab;
import aaa.admin.modules.cem.campaigns.actiontabs.UpdateCampaignActionTab;
import aaa.admin.modules.cem.campaigns.views.ArchiveCampaignView;
import aaa.admin.modules.cem.campaigns.views.CopyCampaignView;
import aaa.admin.modules.cem.campaigns.views.CreateChildCampaignView;
import aaa.admin.modules.cem.campaigns.views.EndCampaignView;
import aaa.admin.modules.cem.campaigns.views.RestartCampaignView;
import aaa.admin.modules.cem.campaigns.views.StartCampaignView;
import aaa.admin.modules.cem.campaigns.views.SuspendCampaignView;
import aaa.admin.modules.cem.campaigns.views.UpdateCampaignView;
import aaa.admin.pages.cem.CampaignPage;
import aaa.common.AbstractAction;
import aaa.common.Workspace;
import aaa.common.pages.Page;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;

public final class CampaignActions {
    private CampaignActions() {}

    public static class StartCampaign extends AbstractAction {
        @Override
        public String getName() {
            return "Start";
        }

        @Override
        public Workspace getView() {
            return new StartCampaignView();
        }

        public AbstractAction start(int rowNumber) {
            CampaignPage.tableCampaigns.getRow(rowNumber).getCell(1).controls.checkBoxes.getFirst().setValue(true);
            return super.start();
        }

        public AbstractAction perform(int rowNumber) {
            start(rowNumber);
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(int rowNumber) instead.");
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            Page.dialogConfirmation.confirm();
            return this;
        }
    }

    public static class UpdateCampaign extends AbstractAction {
        @Override
        public String getName() {
            return "Update";
        }

        @Override
        public Workspace getView() {
            return new UpdateCampaignView();
        }

        public AbstractAction start(int rowNumber) {
            CampaignPage.tableCampaigns.getRow(rowNumber).getCell(1).controls.checkBoxes.getFirst().setValue(true);
            return super.start();
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            UpdateCampaignActionTab.buttonSave.click();
            return this;
        }
    }

    public static class EndCampaign extends AbstractAction {
        @Override
        public String getName() {
            return "End";
        }

        @Override
        public Workspace getView() {
            return new EndCampaignView();
        }

        public AbstractAction start(int rowNumber) {
            CampaignPage.tableCampaigns.getRow(rowNumber).getCell(1).controls.checkBoxes.getFirst().setValue(true);
            return super.start();
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            Page.dialogConfirmation.confirm();
            return this;
        }
    }

    public static class SuspendCampaign extends AbstractAction {
        @Override
        public String getName() {
            return "Suspend";
        }

        @Override
        public Workspace getView() {
            return new SuspendCampaignView();
        }

        public AbstractAction start(int rowNumber) {
            CampaignPage.tableCampaigns.getRow(rowNumber).getCell(1).controls.checkBoxes.getFirst().setValue(true);
            return super.start();
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            Page.dialogConfirmation.confirm();
            return this;
        }
    }

    public static class RestartCampaign extends AbstractAction {
        @Override
        public String getName() {
            return "Restart";
        }

        @Override
        public Workspace getView() {
            return new RestartCampaignView();
        }

        public AbstractAction start(int rowNumber) {
            CampaignPage.tableCampaigns.getRow(rowNumber).getCell(1).controls.checkBoxes.getFirst().setValue(true);
            return super.start();
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            Page.dialogConfirmation.confirm();
            return this;
        }
    }

    public static class CopyCampaign extends AbstractAction {
        @Override
        public String getName() {
            return "Copy";
        }

        @Override
        public Workspace getView() {
            return new CopyCampaignView();
        }

        public AbstractAction start(int rowNumber) {
            CampaignPage.tableCampaigns.getRow(rowNumber).getCell(1).controls.checkBoxes.getFirst().setValue(true);
            super.start();
            new Button(By.id("copyForm:copyButton")).click();
            return this;
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            CopyCampaignActionTab.buttonSave.click();
            return this;
        }
    }

    public static class CreateChildCampaign extends AbstractAction {
        @Override
        public String getName() {
            return "Copy";
        }

        @Override
        public Workspace getView() {
            return new CreateChildCampaignView();
        }

        public AbstractAction start(int rowNumber) {
            CampaignPage.tableCampaigns.getRow(rowNumber).getCell(1).controls.checkBoxes.getFirst().setValue(true);
            super.start();
            new Button(By.id("copyForm:copyAsChildButton")).click();
            return this;
        }

        public AbstractAction perform(TestData td, int rowNumber) {
            start(rowNumber);
            getView().fill(td);
            return submit();
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            CreateChildCampaignActionTab.buttonSave.click();
            return this;
        }
    }

    public static class ArchiveCampaign extends AbstractAction {
        @Override
        public String getName() {
            return "Archive";
        }

        @Override
        public Workspace getView() {
            return new ArchiveCampaignView();
        }

        public AbstractAction start(int rowNumber) {
            CampaignPage.tableCampaigns.getRow(rowNumber).getCell(1).controls.checkBoxes.getFirst().setValue(true);
            return super.start();
        }

        public AbstractAction perform(int rowNumber) {
            start(rowNumber);
            return submit();
        }

        @Override
        public AbstractAction perform(TestData td) {
            throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(int rowNumber) instead.");
        }

        @Override
        public AbstractAction start() {
            throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
        }

        @Override
        public AbstractAction submit() {
            Page.dialogConfirmation.confirm();
            return this;
        }
    }
}
