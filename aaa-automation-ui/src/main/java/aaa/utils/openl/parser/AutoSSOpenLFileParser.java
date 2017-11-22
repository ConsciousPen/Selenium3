package aaa.utils.openl.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.exigen.ipb.etcsa.utils.ExcelUtils;
import aaa.utils.AAAExcelUtils;
import aaa.utils.openl.model.AutoSSOpenLPolicy;

public class AutoSSOpenLFileParser extends OpenLFileParser<AutoSSOpenLPolicy> {
	public AutoSSOpenLFileParser(String openLFilePath) {
		super(openLFilePath);
		parse(getOpenLFile()); // TODO-dchubkov: maybe not a good idea
	}

	@Override
	protected final boolean parse(File openLFile) {
		Workbook wb = ExcelUtils.getWorkbook(openLFile.getAbsolutePath());
		Sheet policySheet = ExcelUtils.getSheet(wb, POLICY_SHEET_NAME);
		Set<String> policyHeader = new HashSet<>();
		policyHeader.add(PolicyFields.PK.get());
		policyHeader.add(PolicyFields.POLICYNUMBER.get());//, "policyNumber", "effectiveDate", "term", "isHomeOwner", "creditScore");
		Row policyHeaderRow = AAAExcelUtils.getHeaderRow(policySheet, policyHeader);

		List<Map<String, String>> policiesMaps = new ArrayList<>();

		for (int rowNumber = policyHeaderRow.getRowNum(); rowNumber < policySheet.getLastRowNum(); rowNumber++) {
			AutoSSOpenLPolicy openLPolicy = new AutoSSOpenLPolicy();

			openLPolicy.setNumber(Integer.valueOf(
					ExcelUtils.getCellValue(policySheet, rowNumber, ExcelUtils.getColumnNumber(policyHeaderRow, PolicyFields.PK.get()))));
			openLPolicy.setPolicyNumber(
					ExcelUtils.getCellValue(policySheet, rowNumber, ExcelUtils.getColumnNumber(policyHeaderRow, PolicyFields.POLICYNUMBER.get())));
			//AAAExcelUtils.getCellValue(policySheet, rowNumber, 4, "policyNumber");
			openLPolicies.add(openLPolicy);
		}

		//TODO-dchubkov: implement this method
		//throw new NotImplementedException("method is not implemented yet for " + this.getClass().getSimpleName());
		return true;
	}

	public enum PolicyFields {
		PK("_PK_"),
		POLICYNUMBER("policyNumber");

		private String name;

		PolicyFields(String name) {
			this.name = name;
		}

		public String get() {
			return name;
		}
	}

}
