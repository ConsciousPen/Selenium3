package aaa.utils.excel.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.io.entity.ExcelTable;

public class ExcelWriter {
	protected static Logger log = LoggerFactory.getLogger(ExcelWriter.class);

	private File file;
	//private List<ExcelTable> tables;

	public ExcelWriter(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public ExcelWriter write(ExcelTable table) {
		try (Workbook workbook = table.getSheet().getWorkbook();
				FileOutputStream outputStream = new FileOutputStream(getFile())) {
			workbook.write(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}
}
