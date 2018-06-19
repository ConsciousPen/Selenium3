package aaa.utils.excel.bind;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import aaa.utils.excel.bind.cache.TableClassesCache;
import aaa.utils.excel.bind.cache.TableFieldInfo;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.ExcelCell;
import aaa.utils.excel.io.entity.area.table.ExcelTable;
import aaa.utils.excel.io.entity.area.table.TableRow;
import toolkit.exceptions.IstfException;

public class ExcelMarshaller {
	//private final ExcelManager excelManager;
	private final boolean strictMatchBinding;
	private final TableClassesCache cache;
	private List<CellType<?>> allowableCellTypes;
	
	private Logger log = LoggerFactory.getLogger(ExcelMarshaller.class);
	
	public ExcelMarshaller(boolean strictMatchBinding) {
		this(strictMatchBinding, ExcelCell.getBaseTypes());
	}
	
	public ExcelMarshaller(boolean strictMatchBinding, List<CellType<?>> allowableCellTypes) {
		//this.excelManager = new ExcelManager(excelFile, allowableCellTypes);
		this.strictMatchBinding = strictMatchBinding;
		this.cache = new TableClassesCache(strictMatchBinding);
		this.allowableCellTypes = new ArrayList<>(allowableCellTypes);
	}
	
	public void marshal(Object excelFileObject, File outputExcelFile) {
		//TODO-dchubkov: check that outputExcelFile does not exist
		
		try (ExcelManager excelManager = new ExcelManager(outputExcelFile, this.allowableCellTypes)) {
			for (Field tableField : BindHelper.getAllAccessibleFields(excelFileObject.getClass(), true)) {
				setTableValues(tableField, excelFileObject, excelManager);
			}
			//to be implemented...
			excelManager.save();
		}
	}
	
	//TODO-dchubkov: rename?
	private void setTableValues(Field tableField, Object classInstance, ExcelManager excelManager) {
		cache.of(tableField).setExcelManager(excelManager);
		//Map<Integer, String> headerColumnsIndexesOnSheetAndNames = new HashMap<>(cache.of(tableField).getTableColumnsFields().size());
		
		//TODO-dchubkov: add check sheet name is set and check sheet name by contains
		String sheetName = cache.of(tableField).getAnnotatedTableClass().getAnnotation(ExcelTableElement.class).sheetName();
		if (ExcelTableElement.DEFAULT_SHEET_NAME.equals(sheetName)) {
			sheetName = cache.of(tableField).getAnnotatedTableClass().getAnnotation(ExcelTableElement.class).containsSheetName();
		}
		//TODO-dchubkov: add check for negative headerRowIndex
		int headerRowIndex = cache.of(tableField).getAnnotatedTableClass().getAnnotation(ExcelTableElement.class).headerRowIndex();
		headerRowIndex = headerRowIndex < 0 ? 1 : headerRowIndex;
		
		ExcelTable table;
		if (!excelManager.hasSheet(sheetName)) {
			table = excelManager.addSheet(sheetName).addTable(headerRowIndex, getHeaderColumnNames(tableField, classInstance).toArray(new String[0]));
			cache.of(tableField).setExcelTable(table);
		} else {
			//TODO-dchubkov: handle multiple tables on same sheet
			table = cache.of(tableField).getExcelTable();
		}
		
		for (Object rowObject : getTableRowObjects(tableField, classInstance)) {
			TableRow row = table.addRow();
			
			for (Field tableColumnField : cache.of(tableField).getTableColumnsFields()) {
				switch (cache.of(tableField).getBindType(tableColumnField)) {
					case REGULAR:
						//TODO-dchubkov: think how to set using known cell type
						row.setValue(cache.of(tableField).getHeaderColumnIndex(tableColumnField), getFieldValue(tableColumnField, rowObject));
						break;
					case TABLE:
						if (cache.of(tableColumnField).getExcelManager() == null) {
							cache.of(tableColumnField).setExcelManager(excelManager);
						}
						List<Object> linkedTableRowObjects = getTableRowObjects(tableColumnField, rowObject);
						if (linkedTableRowObjects.size() == 1) {
							row.setValue(cache.of(tableField).getHeaderColumnIndex(tableColumnField), (Integer) linkedTableRowObjects.get(1), ExcelCell.INTEGER_TYPE);
						} else {
							StringBuilder linkedTableRowIDs = new StringBuilder();
							for (int i = 0; i < linkedTableRowObjects.size(); i++) {
								//Integer linkedRowId = (Integer) getFieldValue(cache.of(tableColumnField).getPrimaryKeyColumnField(), getFieldValue(tableColumnField, rowObject));
								linkedTableRowIDs.append(getFieldValue(cache.of(tableColumnField).getPrimaryKeyColumnField(), linkedTableRowObjects.get(i)));
								if (i != linkedTableRowObjects.size() - 1) {
									linkedTableRowIDs.append(cache.of(tableColumnField).getPrimaryKeysSeparator());
								}
							}
							row.setValue(cache.of(tableField).getHeaderColumnIndex(tableColumnField), linkedTableRowIDs.toString(), ExcelCell.STRING_TYPE);
						}
						
						//ExcelCell.STRING_TYPE.setValueTo(row.getCell(cache.of(tableField).getHeaderColumnIndex(tableColumnField)), linkedTableRowIDs);
						//log.info("tableColumnField=" + tableColumnField.getName());
						//setTableValues(tableColumnField, rowObject, excelManager);
						break;
					case MULTY_COLUMNS:
						List<Object> columnsObjects = getTableRowObjects(tableColumnField, rowObject);
						List<Integer> headerColumnsIndexes = cache.of(tableField).getHeaderColumnsIndexes(tableColumnField);
						for (int i = 0; i < headerColumnsIndexes.size(); i++) {
							row.getCell(headerColumnsIndexes.get(i)).setValue(columnsObjects.get(i));
						}
						
						//TODO-dchubkov: ...
						break;
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getHeaderColumnNames(Field tableField, Object classInstance) {
		List<String> headerColumnNames = new ArrayList<>();
		for (Field field : cache.of(tableField).getTableColumnsFields()) {
			TableFieldInfo fieldInfo = cache.of(tableField).getFieldInfo(field);
			headerColumnNames.add(fieldInfo.getHeaderColumnName());
			if (fieldInfo.getBindType().equals(TableFieldInfo.BindType.MULTY_COLUMNS)) {
				int maxMultiColumnsNumber = 0;
				//TODO-dchubkov: check contains...
				for (Object tableRowObject : getTableRowObjects(tableField, classInstance)) {
					if (((List<Object>) getFieldValue(field, tableRowObject)).size() > maxMultiColumnsNumber) {
						maxMultiColumnsNumber++;
					}
				}
				
				for (int i = 0; i < maxMultiColumnsNumber - 1; i++) {
					headerColumnNames.add(fieldInfo.getHeaderColumnName());
				}
			}
		}
		return headerColumnNames;
	}
	
	@SuppressWarnings("unchecked")
	private List<Object> getTableRowObjects(Field tableField, Object classInstance) {
		Object fieldObject = getFieldValue(tableField, classInstance);
		List<Object> tableRowObjects = new ArrayList<>();
		if (List.class.equals(tableField.getType())) {
			tableRowObjects = (List<Object>) fieldObject;
		} else {
			tableRowObjects.add(fieldObject);
		}
		return tableRowObjects;
	}
	
	//TODO-dchubkov: add getInt(), getLong(), etc... methods
	private Object getFieldValue(Field field, Object classInstance) {
		if (!field.isAccessible()) {
			//TODO-dchubkov: find appropriate setter method and use it for set value
			field.setAccessible(true);
		}
		
		try {
			return field.get(classInstance);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new IstfException(String.format("Unable to get value from the field \"%1$s\" with type \"%2$s\" in class \"%3$s\"",
					field.getName(), field.getType(), classInstance.getClass().getName()), e);
		}
	}
}
