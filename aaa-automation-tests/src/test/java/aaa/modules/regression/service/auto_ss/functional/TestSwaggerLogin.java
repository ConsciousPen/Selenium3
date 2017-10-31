/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss.functional;

import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.swaggerui.SwaggerUiTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.service.template.PolicyCancellation;
import com.exigen.ipb.etcsa.base.app.Application;
import org.openqa.selenium.By;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.config.PropertyProvider;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.waiters.Waiters;

public class TestSwaggerLogin extends PolicyCancellation {
	private static String swaggerUiUrl = PropertyProvider.getProperty("app.host") + PropertyProvider.getProperty(CustomTestProperties.APP_SWAGGER_URLTEMPLATE);

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Override
	protected TestData getBackDatedPolicyTD() {
		return new AutoSSBaseTest().getBackDatedPolicyTD();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test Midterm Cancellation
	 * @scenario 1. Create customer
	 * 2. Create backdated policy
	 * 3. Cancel policy with current date
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS)
	public void pas1441_LoginSwaggerUi(@Optional("") String state) {

		By productOperationsProcessOperations = SwaggerUiTab.productOperationsProcessOperations.getLocator();
		By productStateLoad = SwaggerUiTab.productStateLoad.getLocator();
		By productStateStart = SwaggerUiTab.productStateStart.getLocator();


		adminApp().open();
		Application.open(swaggerUiUrl);
		SwaggerUiTab swaggerUiTab = new SwaggerUiTab();


		SwaggerUiTab.swaggerServiceSelection.setValue("[PF] Restful Workspace");
		SwaggerUiTab.productOperations.click();
		SwaggerUiTab.productOperationsProcessOperations.click();
		log.info(swaggerUiTab.getModelValueByLocator(productOperationsProcessOperations));

		swaggerUiTab.setBodyValueByLocator(productOperationsProcessOperations, "111");
		swaggerUiTab.clickButtonTryItByLocator(productOperationsProcessOperations);

		log.info(swaggerUiTab.getResponseBodyValueByLocator(productOperationsProcessOperations));
		log.info(swaggerUiTab.getResponseCodeValueByLocator(productOperationsProcessOperations));



		SwaggerUiTab.productState.click();
		SwaggerUiTab.productStateLoad.click();
		log.info(swaggerUiTab.getModelValueByLocator(productStateLoad));
		swaggerUiTab.clickButtonTryItByLocator(productStateLoad);


		SwaggerUiTab.productStateStart.click();
		log.info(swaggerUiTab.getModelValueByLocator(productStateStart));
		swaggerUiTab.clickButtonTryItByLocator(productStateStart);
	}


}
