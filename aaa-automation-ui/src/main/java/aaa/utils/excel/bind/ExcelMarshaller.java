package aaa.utils.excel.bind;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
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
	protected Logger log = LoggerFactory.getLogger(ExcelMarshaller.class);
	
	public ExcelMarshaller() {
		this(ExcelCell.getBaseTypes());
	}
	
	public ExcelMarshaller(List<CellType<?>> allowableCellTypes) {
		this.allowableCellTypes = Collections.unmodifiableList(allowableCellTypes);
	}
	
	public List<CellType<?>> getAllowableCellTypes() {
		return Collections.unmodifiableList(allowableCellTypes);
	}
	
	public void marshal(Object excelFileObject, File outputExcelFile) {
		marshal(excelFileObject, outputExcelFile, true);
	}
	
	public void marshalRows(List<?> tableRowsObjects, File outputExcelFile) {
		marshal(tableRowsObjects, outputExcelFile, false);
	}
	
	private void marshal(Object objectToMarshall, File outputExcelFile, boolean isExcelFileObject) {
		Assertions.assertThat(objectToMarshall).as("Unable to marshall null object").isNotNull();
		Assertions.assertThat(outputExcelFile).as("Unable to marshall to null output file").isNotNull();
		log.info("Starting excel object marshalling to file \"{}\"", outputExcelFile.getAbsolutePath());
		if (outputExcelFile.exists()) {
			log.warn("File \"{}\" already exists and will be overwritten after marshalling", outputExcelFile.getAbsolutePath());
			if (!outputExcelFile.delete()) {
				log.warn("Unable to delete original \"{}\" file, marshalled data will be added to this file", outputExcelFile.getAbsolutePath());
			}
		}
		
		MarshallingCache cache = null;
		try (ExcelManager excelManager = new ExcelManager(outputExcelFile, getAllowableCellTypes())) {
			cache = new MarshallingCache(excelManager);
			if (isExcelFileObject) {
				for (Field tableField : ReflectionHelper.getAllAccessibleTableFieldsFromThisAndSuperClasses(objectToMarshall.getClass())) {
					marshalRows(ReflectionHelper.getValueAsList(tableField, objectToMarshall), outputExcelFile, cache);
				}
			} else {
				marshalRows(ReflectionHelper.getValueAsList(objectToMarshall), outputExcelFile, cache);
			}
			
			excelManager.save();
		} finally {
			if (cache != null) {
				cache.flushAll();
			}
		}
		log.info("Excel marshalling completed successfully.");
	}
	
	private void marshalRows(List<?> tableRowsObjects, File outputExcelFile, MarshallingCache cache) {
		MarshallingClassInfo tableClassInfo = cache.of(tableRowsObjects);
		ExcelTable table = tableClassInfo.getExcelTable();
		
		log.info("Writing rows objects from \"{}\" table model class", tableClassInfo.getTableClass().getName());
		for (Object rowObject : tableRowsObjects) {
			TableRow row = table.addRow();
			for (Field tableColumnField : tableClassInfo.getTableColumnsFields()) {
				switch (tableClassInfo.getBindType(tableColumnField)) {
					case REGULAR:
						row.setValue(tableClassInfo.getHeaderColumnIndex(tableColumnField), ReflectionHelper.getFieldValue(tableColumnField, rowObject));
						break;
					case TABLE:
						List<?> linkedTableRowObjects = ReflectionHelper.getValueAsList(tableColumnField, rowObject);
						Field primaryKeyField = cache.of(tableColumnField).getPrimaryKeyColumnField();
						if (linkedTableRowObjects.size() == 1) {
							Integer primaryKeyValue = (Integer) ReflectionHelper.getFieldValue(primaryKeyField, linkedTableRowObjects.get(0));
							row.setValue(tableClassInfo.getHeaderColumnIndex(tableColumnField), primaryKeyValue, ExcelCell.INTEGER_TYPE);
						} else {
							StringBuilder linkedTableRowIDs = new StringBuilder();
							for (int i = 0; i < linkedTableRowObjects.size(); i++) {
								linkedTableRowIDs.append(ReflectionHelper.getFieldValue(primaryKeyField, linkedTableRowObjects.get(i)));
								if (i != linkedTableRowObjects.size() - 1) {
									linkedTableRowIDs.append(cache.of(tableColumnField).getPrimaryKeysSeparator());
								}
							}
							row.setValue(tableClassInfo.getHeaderColumnIndex(tableColumnField), linkedTableRowIDs.toString(), ExcelCell.STRING_TYPE);
						}
						break;
					case MULTI_COLUMNS:
						List<?> columnsObjects = ReflectionHelper.getValueAsList(tableColumnField, rowObject);
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
