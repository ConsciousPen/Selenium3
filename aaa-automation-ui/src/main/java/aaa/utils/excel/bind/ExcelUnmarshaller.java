package aaa.utils.excel.bind;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.bind.cache.TableClassesCache;
import aaa.utils.excel.bind.helper.BindHelper;
import aaa.utils.excel.bind.helper.ColumnFieldHelper;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.entity.area.ExcelCell;
import aaa.utils.excel.io.entity.area.table.TableCell;
import aaa.utils.excel.io.entity.area.table.TableRow;
import toolkit.exceptions.IstfException;

public class ExcelUnmarshaller {
	private static final Object UNMARSHAL_LOCK = new Object();
	private final ExcelManager excelManager;
	private final boolean strictMatch;
	private final TableClassesCache cache;

	private Logger log = LoggerFactory.getLogger(ExcelUnmarshaller.class);

	public ExcelUnmarshaller(File excelFile) {
		this(excelFile, true);
	}

	public ExcelUnmarshaller(File excelFile, boolean strictMatch) {
		this(new ExcelManager(excelFile), strictMatch);
	}

	// TODO-dchubkov: sremove after implementation of getting objects by prvided excel rows (or kind of filter table)
	public ExcelUnmarshaller(ExcelManager excelManager) {
		this(excelManager, true);
	}

	public ExcelUnmarshaller(ExcelManager excelManager, boolean strictMatch) {
		this.excelManager = excelManager;
		this.strictMatch = strictMatch;
		this.cache = new TableClassesCache(excelManager, strictMatch);
	}

	public File getExcelFile() {
		return this.excelManager.getFile();
	}

	public boolean isStrictMatch() {
		return strictMatch;
	}

	public <T> T unmarshalFile(Class<T> excelFileModel) {
		//TODO: check excelFileModel is valid class (not primitive, etc...)
		log.info(String.format("Getting \"%1$s\" object model from excel file \"%2$s\" %3$s strict match binding",
				excelFileModel.getSimpleName(), excelManager.getFile().getAbsolutePath(), strictMatch ? "with" : "without"));

		T excelFileObject = BindHelper.getInstance(excelFileModel);
		for (Field tableField : BindHelper.getAllAccessibleFields(excelFileModel, true)) {
			List<Object> tablesObjects = unmarshalRows(cache.of(tableField).getTableClass());
			BindHelper.setFieldValue(tableField, excelFileObject, tablesObjects);
		}

		log.info("Excel unmarshalling was successful.");
		return excelFileObject;
	}

	public <T> List<T> unmarshalRows(Class<T> excelTableModel) {
		/*log.info(String.format("Getting \"%1$s\" object model from excel file \"%2$s\" %3$s strict match binding",
				excelFileModel.getSimpleName(), excelManager.getFile().getAbsolutePath(), strictMatch ? "with" : "without"));*/

		List<T> tableRowsObjects;
		synchronized (UNMARSHAL_LOCK) { // Used to solve performance issues when parsing thousands of excel rows simultaneously by multiple threads
			tableRowsObjects = new ArrayList<>(cache.of(excelTableModel).getExcelTable().getRows().size());
			for (TableRow row : cache.of(excelTableModel).getExcelTable().getRows()) {
				T tableRowObject = getTableObjectValue(excelTableModel, row);
				tableRowsObjects.add(tableRowObject);
			}
		}

		//log.info("Excel unmarshalling was successful.");
		return tableRowsObjects;
	}

	public void marshal(Object excelFileObject, File excelFile) {
		//TODO-dchubkov: To be implemented...
		throw new NotImplementedException("Excel marshalling is not implemented yet");
	}

	public void flushCache() {
		this.cache.flushAll();
	}

	private void setFieldValue(Field tableColumnField, Object tableObject, TableCell cell, boolean isTableField) {
		//TableCell cell = row.getCell(cache.of(tableField).getHeaderColumnIndex(tableColumnField));

		if (isTableField && !List.class.equals(tableColumnField.getType())) {
			//setTableFieldValue(getLinkedTableRowIds(cell, tableColumnField), tableField, tableColumnField, tableObject);
			Object tableObjectValue = getTableObjectValue(cache.of(tableColumnField).getTableClass(), cache.of(tableColumnField).getRow(cell.getIntValue()));
			BindHelper.setFieldValue(tableColumnField, tableObject, tableObjectValue);
			return;
		}

		switch (tableColumnField.getType().getName()) {
			case "int":
			case "java.lang.Integer":
				BindHelper.setFieldValue(tableColumnField, tableObject, cell.isEmpty() ? null : cell.getIntValue());
				break;
			case "boolean":
			case "java.lang.Boolean":
				BindHelper.setFieldValue(tableColumnField, tableObject, cell.isEmpty() ? null : cell.getBoolValue());
				break;
			case "java.lang.String":
				BindHelper.setFieldValue(tableColumnField, tableObject, cell.getStringValue());
				break;
			case "java.time.LocalDateTime":
				BindHelper.setFieldValue(tableColumnField, tableObject, cell.isEmpty() ? null : cell.getDateValue(ColumnFieldHelper.getFormatters(tableColumnField)));
				break;
			case "double":
			case "java.lang.Double":
				BindHelper.setFieldValue(tableColumnField, tableObject, cell.isEmpty() ? null : cell.getDoubleValue());
				break;
			case "java.util.List":
				//if (BindHelper.isTableClassField(tableColumnField)) {
				if (isTableField) {
					//setTableFieldValue(getLinkedTableRowIds(cell, tableColumnField), tableField, tableColumnField, tableObject);
					List<Object> tableObjectValues = getTableObjectValues(cache.of(tableColumnField).getTableClass(), getLinkedTableRowIds(cell, tableColumnField));
					BindHelper.setFieldValue(tableColumnField, tableObject, tableObjectValues);
				} else {
					//TODO-dchubkov: add List of contains logic...
				}

				break;
			default:
				throw new IstfException(String.format("Field type \"%s\" is not supported for unmarshalling", tableColumnField.getType().getName()));
		}
	}

	private List<Integer> getLinkedTableRowIds(TableCell cell, Field tableColumnField) {
		if (cell.hasType(ExcelCell.INTEGER_TYPE)) {
			return Arrays.asList(cell.getIntValue());
		}
		String[] linkedTableRowStringIds = cell.getStringValue().split(cache.of(tableColumnField).getPrimaryKeysSeparator());
		List<Integer> linkedTableRowIds = new ArrayList<>(linkedTableRowStringIds.length);
		for (String id : linkedTableRowStringIds) {
			linkedTableRowIds.add(Integer.valueOf(id));
		}
		return linkedTableRowIds;
	}

	@SuppressWarnings("unchecked")
	private <T> T getTableObjectValue(Class<T> tableClass, TableRow row) {
		if (cache.of(tableClass).hasObject(row.getIndex())) {
			return cache.of(tableClass).getObject(row.getIndex());
		}

		T tableObjectValue = BindHelper.getInstance(cache.of(tableClass).getTableClass());
		for (Field tableColumnField : cache.of(tableClass).getTableColumnsFields()) {
			TableCell cell = row.getCell(cache.of(tableClass).getHeaderColumnIndex(tableColumnField));
			boolean isTableField = cache.of(tableClass).isTableField(tableColumnField);
			setFieldValue(tableColumnField, tableObjectValue, cell, isTableField);
		}
		cache.of(tableClass).setObject(row.getIndex(), tableObjectValue);

		return tableObjectValue;
	}

	private List<Object> getTableObjectValues(Class<?> tableClass) {
		return getTableObjectValues(tableClass, null);
	}

	private List<Object> getTableObjectValues(Class<?> tableClass, List<Integer> tableRowsIds) {
		/*if (tableRowsIds.isEmpty()) {
			//TODO-dchubkov: think what is better in empty IDs case
			return null;
		}*/
		List<TableRow> tableRows = cache.of(tableClass).getRows(tableRowsIds);
		List<Object> tableObjectValues = new ArrayList<>(tableRows.size());
		for (TableRow row : tableRows) {
			tableObjectValues.add(getTableObjectValue(tableClass, row));
		}
		return tableObjectValues;
	}
}
