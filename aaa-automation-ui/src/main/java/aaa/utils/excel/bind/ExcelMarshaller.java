package aaa.utils.excel.bind;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.bind.cache.MarshallingCache;
import aaa.utils.excel.bind.cache.MarshallingClassInfo;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.ExcelCell;
import aaa.utils.excel.io.entity.area.table.ExcelTable;
import aaa.utils.excel.io.entity.area.table.TableRow;

public class ExcelMarshaller {
	private final List<CellType<?>> allowableCellTypes;
	private final boolean strictMatchBinding;
	
	private Logger log = LoggerFactory.getLogger(ExcelMarshaller.class);
	
	public ExcelMarshaller() {
		this(true, ExcelCell.getBaseTypes());
	}
	
	public ExcelMarshaller(boolean strictMatchBinding) {
		this(strictMatchBinding, ExcelCell.getBaseTypes());
	}
	
	public ExcelMarshaller(boolean strictMatchBinding, List<CellType<?>> allowableCellTypes) {
		this.allowableCellTypes = new ArrayList<>(allowableCellTypes);
		this.strictMatchBinding = strictMatchBinding;
	}
	
	public List<CellType<?>> getAllowableCellTypes() {
		return Collections.unmodifiableList(allowableCellTypes);
	}
	
	public boolean isStrictMatchBinding() {
		return strictMatchBinding;
	}
	
	public void marshal(Object excelFileObject, File outputExcelFile) {
		//TODO-dchubkov: check that outputExcelFile does not exist
		MarshallingCache cache = null;
		try (ExcelManager excelManager = new ExcelManager(outputExcelFile, this.allowableCellTypes)) {
			cache = new MarshallingCache(excelManager, isStrictMatchBinding());
			for (Field tableField : BindHelper.getAllAccessibleFields(excelFileObject.getClass(), true)) {
				marshalRows(BindHelper.getValueAsList(tableField, excelFileObject), outputExcelFile, cache);
			}
			excelManager.save();
		} finally {
			if (cache != null) {
				cache.flushAll();
			}
		}
	}
	
	public void marshalRows(List<?> tableRowsObjects, File outputExcelFile) {
		MarshallingCache cache = null;
		try (ExcelManager excelManager = new ExcelManager(outputExcelFile, this.allowableCellTypes)) {
			cache = new MarshallingCache(excelManager, isStrictMatchBinding());
			marshalRows(tableRowsObjects, outputExcelFile, cache);
			excelManager.save();
		} finally {
			if (cache != null) {
				cache.flushAll();
			}
		}
	}
	
	private void marshalRows(List<?> tableRowsObjects, File outputExcelFile, MarshallingCache cache) {
		MarshallingClassInfo tableClassInfo = cache.of(tableRowsObjects);
		ExcelTable table = tableClassInfo.getExcelTable();
		
		for (Object rowObject : tableRowsObjects) {
			TableRow row = table.addRow();
			for (Field tableColumnField : tableClassInfo.getTableColumnsFields()) {
				switch (tableClassInfo.getBindType(tableColumnField)) {
					case REGULAR:
						row.setValue(tableClassInfo.getHeaderColumnIndex(tableColumnField), BindHelper.getFieldValue(tableColumnField, rowObject));
						break;
					case TABLE:
						List<?> linkedTableRowObjects = BindHelper.getValueAsList(tableColumnField, rowObject);
						Field primaryKeyField = cache.of(tableColumnField).getPrimaryKeyColumnField();
						if (linkedTableRowObjects.size() == 1) {
							Integer primaryKeyValue = (Integer) BindHelper.getFieldValue(primaryKeyField, linkedTableRowObjects.get(0));
							row.setValue(tableClassInfo.getHeaderColumnIndex(tableColumnField), primaryKeyValue, ExcelCell.INTEGER_TYPE);
						} else {
							StringBuilder linkedTableRowIDs = new StringBuilder();
							for (int i = 0; i < linkedTableRowObjects.size(); i++) {
								linkedTableRowIDs.append(BindHelper.getFieldValue(primaryKeyField, linkedTableRowObjects.get(i)));
								if (i != linkedTableRowObjects.size() - 1) {
									linkedTableRowIDs.append(cache.of(tableColumnField).getPrimaryKeysSeparator());
								}
							}
							row.setValue(tableClassInfo.getHeaderColumnIndex(tableColumnField), linkedTableRowIDs.toString(), ExcelCell.STRING_TYPE);
						}
						break;
					case MULTY_COLUMNS:
						List<?> columnsObjects = BindHelper.getValueAsList(tableColumnField, rowObject);
						List<Integer> headerColumnsIndexes = tableClassInfo.getHeaderColumnsIndexes(tableColumnField);
						for (int i = 0; i < headerColumnsIndexes.size(); i++) {
							row.getCell(headerColumnsIndexes.get(i)).setValue(columnsObjects.get(i));
						}
						break;
				}
			}
		}
	}
}
