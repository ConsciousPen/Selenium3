package aaa.helpers.openl;

import java.io.File;
import aaa.utils.excel.ExcelUnmarshaller;
import aaa.utils.openl.model.OpenLFile;

public class OpenLHelper {
	public static <T extends OpenLFile> T excelToModel(String pathToOpenLFile, Class<T> modelClass) {
		T model;
		ExcelUnmarshaller eUnmarshaller = new ExcelUnmarshaller(modelClass);
		model = eUnmarshaller.unmarshal(new File(pathToOpenLFile), modelClass);

		return model;
	}
}
