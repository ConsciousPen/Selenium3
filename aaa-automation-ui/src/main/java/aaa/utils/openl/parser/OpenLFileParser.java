package aaa.utils.openl.parser;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;
import org.apache.poi.ss.usermodel.Workbook;
import com.exigen.ipb.etcsa.utils.ExcelUtils;
import aaa.utils.excel.SearchPattern;
import aaa.utils.openl.model.OpenLPolicy;

public abstract class OpenLFileParser<P extends OpenLPolicy, F extends OpenLFields> {
	protected static final SearchPattern POLICY_SHEET_PATTERN = new SearchPattern("Policy");
	protected static final SearchPattern DRIVER_SHEET_PATTERN = new SearchPattern("Driver");
	/*protected final static String CAPPING_DETAILS_SHEET_NAME = "Batch- CappingDetails";
	protected final static String VEHICLE_SHEET_NAME = "Batch- Vehicle";
	protected final static String ADDRESS_SHEET_NAME = "Batch- Address";
	protected final static String DRIVER_SHEET_NAME = "Batch- Driver";
	protected final static String COVERAGE_SHEET_NAME = "Batch- Coverage";
	protected final static String ENVIRONMENT_SHEET_NAME = "Environment";
	protected final static String TESTS_SHEET_NAME = "Tests";*/

	protected File openLFile;
	protected Workbook openLWorkbook;
	protected F openLFields;

	public OpenLFileParser(String openLFilePath, F openLFields) {
		this(new File(openLFilePath), openLFields);
	}

	public OpenLFileParser(File openLFile, F openLFields) {
		this.openLFile = openLFile;
		this.openLWorkbook = ExcelUtils.getWorkbook(openLFile.getAbsolutePath());
		this.openLFields = openLFields;
	}

	public File getOpenLFile() {
		return openLFile;
	}

	public F getOpenLFields() {
		return openLFields;
	}

	public abstract List<P> getPolicies();

	protected int[] getNumbersArray(String numbersSequence) {
		String sequenceSplitter = ",";
		return Stream.of(numbersSequence.split(sequenceSplitter)).mapToInt(Integer::parseInt).toArray();
	}

	/*protected Set<String> getHeaders(AutoSSOpenLFields.OpenLField... fieldsEnum) {
		return Arrays.stream(fieldsEnum).map(AutoSSOpenLFields.OpenLField::get).collect(Collectors.toSet());
	}*/
}
