package aaa.utils.excel.bind;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.bind.cache.TableClassesCache;
import aaa.utils.excel.bind.helper.BindHelper;
import aaa.utils.excel.bind.helper.ColumnFieldHelper;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.ExcelCell;
import aaa.utils.excel.io.entity.area.table.TableCell;
import aaa.utils.excel.io.entity.area.table.TableRow;
import toolkit.exceptions.IstfException;

public class ExcelUnmarshaller {
	private final ExcelManager excelManager;
	private final boolean strictMatch;
	private final TableClassesCache cache;

	private Logger log = LoggerFactory.getLogger(ExcelUnmarshaller.class);

	public ExcelUnmarshaller(File excelFile) {
		this(excelFile, true);
	}

	public ExcelUnmarshaller(File excelFile, boolean strictMatch, CellType<?>... allowableCellTypes) {
		this.excelManager = new ExcelManager(excelFile, allowableCellTypes);
		this.strictMatch = strictMatch;
		this.cache = new TableClassesCache(excelManager, strictMatch);
	}

	public File getExcelFile() {
		return excelManager.getFile();
	}

	public boolean isStrictMatch() {
		return strictMatch;
	}

	@SuppressWarnings("unchecked")
	public <T> T unmarshal(Class<T> excelFileModel) {
		//TODO: check excelFileModel is valid class (not primitive, etc...)
		log.info(String.format("Getting excel file object of \"%1$s\" model from file \"%2$s\" %3$s strict match binding",
				excelFileModel.getSimpleName(), getExcelFile().getAbsolutePath(), isStrictMatch() ? "with" : "without"));

		T excelFileObject = (T) BindHelper.getInstance(excelFileModel);
		for (Field tableField : BindHelper.getAllAccessibleFields(excelFileModel, true)) {
			List<?> tablesObjects = unmarshalRows(cache.of(tableField).getTableClass());
			BindHelper.setFieldValue(tableField, excelFileObject, tablesObjects);
		}

		log.info("Excel file {} unmarshalling completed successfully.", getExcelFile().getName());
		return excelFileObject;
	}

	public <T> List<T> unmarshalRows(Class<T> excelTableModel) {
		return unmarshalRows(excelTableModel, null);
	}

	public <T> List<T> unmarshalRows(Class<T> excelTableModel, List<Integer> rowsWithPrimaryKeyValues) {
		log.info(String.format("Getting list of table row objects of \"%1$s\" model from excel file \"%2$s\"%3$s %4$s strict match binding",
				excelTableModel.getSimpleName(),
				getExcelFile().getAbsolutePath(),
				CollectionUtils.isNotEmpty(rowsWithPrimaryKeyValues) ? ", containing values in primary key column: " + rowsWithPrimaryKeyValues : "",
				isStrictMatch() ? "with" : "without"));

		List<T> tableRowsObjects;
			List<TableRow> rows = cache.of(excelTableModel).getRows(rowsWithPrimaryKeyValues);
			tableRowsObjects = new ArrayList<>(rows.size());
			for (TableRow row : rows) {
				T tableRowObject = getTableObjectValue(excelTableModel, row);
				tableRowsObjects.add(tableRowObject);
			}
		log.info("Excel table rows unmarshalling completed successfully.");
		return tableRowsObjects;
	}

	public ExcelUnmarshaller marshal(Object excelFileObject) {
		return marshal(excelFileObject, getExcelFile());
	}

	public ExcelUnmarshaller marshal(Object excelFileObject, File excelFile) {
		//TODO-dchubkov: To be implemented...
		throw new NotImplementedException("Excel marshalling is not implemented yet");
	}

	public ExcelUnmarshaller flushCache() {
		this.cache.flushAll();
		return this;
	}

	public ExcelUnmarshaller close() {
		this.excelManager.close();
		return this;
	}

	@SuppressWarnings("unchecked")
	private <T> T getTableObjectValue(Class<T> tableClass, TableRow row) {
		if (cache.of(tableClass).hasObject(row.getIndex())) {
			return (T) cache.of(tableClass).getObject(row.getIndex());
		}

		T tableObject = (T) BindHelper.getInstance(cache.of(tableClass).getTableClass());
		for (Field tableColumnField : cache.of(tableClass).getTableColumnsFields()) {
			Object object;
			if (cache.of(tableClass).isMultyColumnsField(tableColumnField)) {
				List<Object> objects = new ArrayList<>(cache.of(tableClass).getHeaderColumnsIndexes(tableColumnField).size());
				for (Integer columnIndex : cache.of(tableClass).getHeaderColumnsIndexes(tableColumnField)) {
					objects.add(getFieldObject(tableColumnField, row.getCell(columnIndex)));
				}
				object = objects;
			} else if (cache.of(tableClass).isTableField(tableColumnField)) {
				object = getFieldTableObject(tableColumnField, tableObject, row.getCell(cache.of(tableClass).getHeaderColumnIndex(tableColumnField)));
			} else {
				object = getFieldObject(tableColumnField, row.getCell(cache.of(tableClass).getHeaderColumnIndex(tableColumnField)));
			}
			BindHelper.setFieldValue(tableColumnField, tableObject, object);
		}
		cache.of(tableClass).setObject(row.getIndex(), tableObject);

		return tableObject;
	}

	private Object getFieldObject(Field tableColumnField, TableCell cell) {
		Class<?> tableColumnFieldType = List.class.equals(tableColumnField.getType()) ? BindHelper.getGenericTypeClass(tableColumnField) : tableColumnField.getType();

		switch (tableColumnFieldType.getName()) {
			case "int":
			case "java.lang.Integer":
				return cell.isEmpty() ? null : cell.getIntValue();
			//break;
			case "boolean":
			case "java.lang.Boolean":
				return cell.isEmpty() ? null : cell.getBoolValue();
			//break;
			case "java.lang.String":
				return cell.getStringValue();
			//break;
			case "java.time.LocalDateTime":
				//break;
				return cell.isEmpty() ? null : cell.getDateValue(ColumnFieldHelper.getFormatters(tableColumnField));
			case "double":
			case "java.lang.Double":
				return cell.isEmpty() ? null : cell.getDoubleValue();
			default:
				throw new IstfException(String.format("Field type \"%s\" is not supported for unmarshalling", tableColumnField.getType().getName()));
		}
	}

	private Object getFieldTableObject(Field tableColumnField, Object tableObject, TableCell cell) {
		if (!List.class.equals(tableColumnField.getType())) {
			return getTableObjectValue(cache.of(tableColumnField).getTableClass(), cache.of(tableColumnField).getRow(cell.getIntValue()));
		}
		return getTableObjectValues(cache.of(tableColumnField).getTableClass(), getLinkedTableRowIds(cell, tableColumnField));
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

	private List<Object> getTableObjectValues(Class<?> tableClass) {
		return getTableObjectValues(tableClass, null);
	}

	private List<Object> getTableObjectValues(Class<?> tableClass, List<Integer> tableRowsIds) {
		if (tableRowsIds.isEmpty()) {
			return null;
		}
		List<TableRow> tableRows = cache.of(tableClass).getRows(tableRowsIds);
		List<Object> tableObjectValues = new ArrayList<>(tableRows.size());
		for (TableRow row : tableRows) {
			tableObjectValues.add(getTableObjectValue(tableClass, row));
		}
		return tableObjectValues;
	}
}
