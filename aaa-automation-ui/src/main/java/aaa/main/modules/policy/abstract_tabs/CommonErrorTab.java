package aaa.main.modules.policy.abstract_tabs;

import aaa.common.Tab;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.toolkit.webdriver.customcontrols.FillableErrorTable;
import org.openqa.selenium.By;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lkazarnovskiy on 8/8/2017.
 */
public abstract class CommonErrorTab extends Tab {
	public Button buttonOverride = new Button(By.id("errorsForm:overrideRules"));
	public Button buttonApproval = new Button(By.id("errorsForm:referForApproval"));
	public FillableErrorTable errorsList = new FillableErrorTable(By.id("errorsForm:msgList"), AutoSSMetaData.ErrorTab.RuleRow.class);
	public Verify verify = new Verify();

	protected CommonErrorTab(Class<? extends MetaData> mdClass) {
		super(mdClass);
	}

	public Tab cancel() {
		buttonCancel.click();
		return this;
	}


	//TODO-dchubkov: implement methods override/refer list of errors/all errors

	public void overrideError(ErrorEnum.Errors error) {
		overrideError(error, ErrorEnum.Duration.LIFE, ErrorEnum.ReasonForOverride.OTHER);
	}

	public void overrideError(ErrorEnum.Errors error, ErrorEnum.Duration duration, ErrorEnum.ReasonForOverride reason) {
		TestData td = DataProviderFactory.dataOf(
				"Override", "true",
				"Code", error.getCode(),
				"Duration", duration.get(),
				"Reason for override", reason.get());
		errorsList.setValue(Collections.singletonList(td));
		buttonOverride.click();
	}

	public void referForApproval(ErrorEnum.Errors error) {
		referForApproval(error, ErrorEnum.Duration.LIFE, ErrorEnum.ReasonForOverride.OTHER);
	}

	public void referForApproval(ErrorEnum.Errors error, ErrorEnum.Duration duration, ErrorEnum.ReasonForOverride reason) {
		TestData td = DataProviderFactory.dataOf(
				"Approval", "true",
				"Code", error.getCode(),
				"Duration", duration.get(),
				"Reason for override", reason.get());

		errorsList.setValue(Collections.singletonList(td));
		buttonApproval.click();
	}

	public class Verify {

		public void errorPresent(ErrorEnum.Errors error) {
			errorPresent(error, true);
		}

		public void errorPresent(ErrorEnum.Errors error, boolean expectedValue) {
			Map<String, String> errorQuery = new HashMap<>();
			errorQuery.put("Code", error.getCode());
			errorQuery.put("Message", error.getMessage());
			String message = String.format("Underwriting Rule %1$s is not %2$s as expected.", error, expectedValue ? "present" : "absent");

			errorsList.getTable().getRow(errorQuery).verify.present(message, expectedValue);
		}
	}
}
