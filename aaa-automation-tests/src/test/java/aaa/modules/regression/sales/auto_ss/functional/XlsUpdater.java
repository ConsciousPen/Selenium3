package aaa.modules.regression.sales.auto_ss.functional;

import java.io.File;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.ssh.RemoteHelper;
import aaa.utils.excel.io.ExcelManager;
import toolkit.exceptions.IstfException;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.waiters.Waiters;

public class XlsUpdater {

	private static final String ORIGIN_STUB_PATH = "src/test/resources/stubs/";
	private static final String DESTINATION_STUB_PATH = "/AAA/stubs/";//Will be moved to env properties

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "test Stub")
	public void pas123_XlsUpdater(@Optional() String state) {

		String value1 = "A100000XYZ";
		String value2 = "A100003";
		String originalFileName = "PolicyPreferenceApiMockData";

		File readFile = new File(ORIGIN_STUB_PATH + originalFileName + ".xls");
		File modifiedFileName1 = new File(originalFileName + value1 + ".xls");
		File modifiedFileName2 = new File(originalFileName + value2 + ".xls");

		File writeFile1 = new File(ORIGIN_STUB_PATH + modifiedFileName1);
		File writeFile2 = new File(ORIGIN_STUB_PATH + modifiedFileName2);

		//get cell by column name containing value
		ExcelManager excel1 = new ExcelManager(readFile);
		excel1.getSheet("POLICY_PREFERENCE_API_REQUEST")
				.getTable("ID", "policyNumber", "policySourceSystem")
				.getRow("policyNumber", "QVASS926232033")
				.getCell("policyNumber")
				.setValue(value1)
				.save(writeFile1); //writeFile can be replaced by new File(ORIGIN_STUB_PATH + modifiedFileName1)
		//TODO If work on the file is over, use .saveAndClose(writeFile1)

		ExcelManager excel2 = new ExcelManager(readFile);
		excel2.getSheet("PREFERENCEGROUP")
				.getTable(1)
				.getRow("ID", "A10")
				.getCell("notificationId")
				.setValue(value2)
				//!!!saveAndClose must be performed when done working with Excel File
				.saveAndClose(writeFile2); //writeFile can be replaced by new File(ORIGIN_STUB_PATH + modifiedFileName2)

		//Write to server
		//RemoteHelper.uploadFile(writeFile1.getAbsolutePath(), DESTINATION_STUB_PATH + modifiedFileName1);
		copyFileToServer(modifiedFileName1);
		//Delay added to check that the file is present in the server
		Waiters.SLEEP(60000).go();
		//Delete file from server
		//RemoteHelper.removeFile(DESTINATION_STUB_PATH + modifiedFileName1);
		removeFileFromServer(modifiedFileName1);
		//Delete local version of the file
		assert writeFile1.delete();

		copyFileToServer(modifiedFileName2);
		Waiters.SLEEP(60000).go();
		removeFileFromServer(modifiedFileName2);
		assert writeFile2.delete();
	}

	private static synchronized void copyFileToServer(File file) {
		if (file == null) {
			throw new IstfException("Stub file is NULL");
		}
		RemoteHelper.uploadFile(new File(ORIGIN_STUB_PATH + file).getAbsolutePath(), DESTINATION_STUB_PATH + file);
	}

	private static synchronized void removeFileFromServer(File file) {
		RemoteHelper.removeFile(DESTINATION_STUB_PATH + file);
	}

}
