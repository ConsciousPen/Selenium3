package aaa.toolkit.webdriver.customcontrols.endorsements;

import org.openqa.selenium.By;

import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Table;

public class AutoSSForms {

	public static class AutoSSPolicyFormsController extends AutoFormsController {

		public AutoSSPolicyFormsController(By locator, Class<? extends MetaData> metaDataClass) {
			super(locator, metaDataClass);
			tableSelectedForms = new Table(By.id("policyDataGatherForm:body_selectedManageableComponentsTable_PolicyEndorsementFormsManager"));
			tableAvailableForms = new Table(By.id("policyDataGatherForm:body_availableManageableComponentsTable_PolicyEndorsementFormsManager"));
		}
	}

	public static class AutoSSVehicleFormsController extends AutoFormsController {
		public AutoSSVehicleFormsController(By locator, Class<? extends MetaData> metaDataClass) {
			super(locator, metaDataClass);
			tableSwitcher = new Table(By.id("policyDataGatherForm:formsTableVehicleFormsTable"));
			tableSelectedForms = new Table(By.id("policyDataGatherForm:body_selectedManageableComponentsTable_VehicleEndorsementFormsManager"));
			tableAvailableForms = new Table(By.id("policyDataGatherForm:body_availableManageableComponentsTable_VehicleEndorsementFormsManager"));
		}
	}

	public static class AutoSSDriverFormsController extends AutoFormsController {
		public AutoSSDriverFormsController(By locator, Class<? extends MetaData> metaDataClass) {
			super(locator, metaDataClass);
			tableSwitcher = new Table(By.id("policyDataGatherForm:formsTableDriverFormsTable"));
			tableSelectedForms = new Table(By.id("policyDataGatherForm:body_selectedManageableComponentsTable_DriverEndorsementFormsManager"));
			tableAvailableForms = new Table(By.id("policyDataGatherForm:body_availableManageableComponentsTable_DriverEndorsementFormsManager"));
		}
	}
}
