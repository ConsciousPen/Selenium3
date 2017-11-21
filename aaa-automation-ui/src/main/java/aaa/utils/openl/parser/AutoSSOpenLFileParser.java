package aaa.utils.openl.parser;

import java.io.File;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.exigen.ipb.etcsa.utils.ExcelUtils;
import aaa.utils.openl.model.AutoSSOpenLPolicy;
import aaa.utils.openl.testdata_builder.AutoSSTestDataBuilder;

public class AutoSSOpenLFileParser extends OpenLFileParser<AutoSSOpenLPolicy, AutoSSTestDataBuilder> {
	public AutoSSOpenLFileParser() {
		super(new AutoSSTestDataBuilder());
	}

	@Override
	public OpenLFileParser parse(File openLtestsFile) {
		/*openLPolicies = gatherPolicies();
		finalPremiums = gatherFinalPremiums();*/

		Workbook wb = ExcelUtils.getWorkbook(openLtestsFile.getAbsolutePath());
		Sheet policySheet = wb.getSheet(POLICY_SHEET_NAME);
		//TODO-dchubkov: implement this method
		throw new NotImplementedException("method is not implemented yet for " + this.getClass().getSimpleName());
	}

}
