package aaa.utils.openl.parser;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import aaa.utils.excel.ExcelParser;
import aaa.utils.excel.ExcelTable;
import aaa.utils.excel.TableRow;
import aaa.utils.openl.model.AutoSSOpenLPolicy;

public class AutoSSOpenLFileParser extends OpenLFileParser<AutoSSOpenLPolicy> {
	public AutoSSOpenLFileParser(String openLFilePath) {
		super(openLFilePath);
		parse(getOpenLFile()); // TODO-dchubkov: maybe not a good idea
	}

	@Override
	protected final boolean parse(File openLFile) {
		ExcelParser ep = new ExcelParser(openLFile, POLICY_SHEET_NAME);
		Set<String> policyHeader = new HashSet<>();
		policyHeader.add(PolicyFields.PK.get());
		policyHeader.add(PolicyFields.POLICYNUMBER.get());//, "policyNumber", "effectiveDate", "term", "isHomeOwner", "creditScore");

		ExcelTable excelTable = ep.getTable(policyHeader);
		for (TableRow row : excelTable) {
			AutoSSOpenLPolicy openLPolicy = new AutoSSOpenLPolicy();
			openLPolicy.setNumber(Integer.valueOf(row.getValue(PolicyFields.PK.get())));
			openLPolicy.setPolicyNumber(row.getValue(PolicyFields.POLICYNUMBER.get()));
			//to be continued...

			openLPolicies.add(openLPolicy);
		}
		return true;
	}

	public enum PolicyFields {
		PK("_PK_"),
		POLICYNUMBER("policyNumber");

		private final String name;

		PolicyFields(String name) {
			this.name = name;
		}

		public String get() {
			return name;
		}
	}

}
