/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.product.moratorium;

import aaa.admin.modules.product.moratorium.views.DefaultView;
import aaa.admin.pages.product.MoratoriumPage;
import aaa.common.AbstractAction;
import aaa.common.Tab;
import aaa.common.Workspace;
import toolkit.datax.TestData;

public class MoratoriumActions {

	public static final String ACTIONS = "Actions";

	public static class Edit extends AbstractAction {
		@Override
		public String getName() {
			return "Edit";
		}

		@Override
		public Workspace getView() {
			return new DefaultView();
		}

		@Override
		public AbstractAction submit() {
			Tab.buttonSave.click();
			return this;
		}

		public AbstractAction perform(TestData td, int rowIndex) {
			start(rowIndex);
			getView().fill(td);
			return submit();
		}

		public AbstractAction start(int rowIndex) {
			MoratoriumPage.tableSearchResult.getRow(rowIndex + 1).getCell(ACTIONS).controls.links.getFirst().click();
			return this;
		}
	}
}
