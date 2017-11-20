package aaa.utils.rating;

import aaa.utils.rating.openl_objects.AutoSSOpenLPolicy;
import aaa.utils.rating.openl_objects.OpenLPolicy;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.ExcelUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import toolkit.datax.TestData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AutoSSOpenLFileParser extends OpenLFileParser {
	protected List<AutoSSOpenLPolicy> openLPolicies = new ArrayList();

	@Override
	public OpenLFileParser parse(File openLtestsFile) {
		Workbook wb = ExcelUtils.getWorkbook(openLtestsFile.getAbsolutePath());
		Sheet policySheet = wb.getSheet(POLICY_SHEET_NAME);
		//TODO-dchubkov: implement this method
		throw new NotImplementedException("method is not implemented yet for " + this.getClass().getSimpleName());
	}

	@Override
	public TestData generateTestData(OpenLPolicy openLPolicy) {
		//TODO-dchubkov: implement this method
		throw new NotImplementedException("method is not implemented yet for " + this.getClass().getSimpleName());
	}

	@Override
	public Dollar getFinalPremium(int policyNumber) {
		//TODO-dchubkov: implement this method
		throw new NotImplementedException("method is not implemented yet for " + this.getClass().getSimpleName());
	}
}
