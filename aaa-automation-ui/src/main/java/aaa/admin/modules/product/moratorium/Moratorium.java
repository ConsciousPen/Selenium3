/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.product.moratorium;

import aaa.admin.modules.product.IProduct;
import aaa.admin.modules.product.moratorium.views.DefaultView;
import aaa.admin.pages.product.MoratoriumPage;
import aaa.common.Tab;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;

public class Moratorium implements IProduct {
	@Override
	public Workspace getDefaultView() {
		return new DefaultView();
	}

	@Override
	public void search(TestData td) {
		navigate();
		MoratoriumPage.search(td);
	}

	@Override
	public void navigate() {
		if (!MoratoriumPage.buttonAddMoratorium.isPresent()) {
			NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.PRODUCT.get());
			NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.PRODUCT_MORATORIUM.get());
		}
	}

	@Override
	public void create(TestData td) {
		initiate();
		getDefaultView().fill(td);
		submit();
	}

	public void initiate() {
		navigate();
		MoratoriumPage.buttonAddMoratorium.click();
	}

	public void submit() {
		Tab.buttonSave.click();
	}

	public MoratoriumActions.Edit edit() {
		return new MoratoriumActions.Edit();
	}
}
