package aaa.utils.excel.bind;

import static aaa.utils.excel.io.entity.area.ExcelCell.LOCAL_DATE_TIME_TYPE;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.bind.cache.TableClassesCache;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.celltype.LocalDateTimeCellType;
import aaa.utils.excel.io.entity.area.ExcelCell;
import aaa.utils.excel.io.entity.area.table.TableCell;
import aaa.utils.excel.io.entity.area.table.TableRow;
import toolkit.exceptions.IstfException;

public class ExcelUnmarshaller {
	private final ExcelManager excelManager;
	private final boolean strictMatchBinding;
	private final TableClassesCache cache;

	private Logger log = LoggerFactory.getLogger(ExcelUnmarshaller.class);

	public ExcelUnmarshaller(File excelFile) {
		this(excelFile, true);
	}

	public ExcelUnmarshaller(File excelFile, boolean strictMatchBinding, CellType<?>... allowableCellTypes) {
		this.excelManager = new ExcelManager(excelFile, allowableCellTypes);
		this.strictMatchBinding = strictMatchBinding;
		this.cache = new TableClassesCache(excelManager, strictMatchBinding);
	}

	public File getExcelFile() {
		return excelManager.getFile();
	}

	public boolean isStrictMatchBinding() {
		return strictMatchBinding;
	}

	public ExcelUnmarshaller registerCellType(CellType<?>... cellTypes) {
		excelManager.registerCellType(cellTypes);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T unmarshal(Class<T> excelFileModel) {
		//TODO-dchubkov: check excelFileModel is valid class (not primitive, etc...)
		log.info(String.format("Getting excel file object of \"%1$s\" model from file \"%2$s\" %3$s strict match binding",
				excelFileModel.getSimpleName(), getExcelFile().getAbsolutePath(), isStrictMatchBinding() ? "with" : "without"));

		T excelFileObject = (T) getInstance(excelFileModel);
		for (Field tableField : BindHelper.getAllAccessibleFields(excelFileModel, true)) {
			List<?> tablesObjects = unmarshalRows(cache.of(tableField).getTableClass());
			setFieldValue(tableField, excelFileObject, tablesObjects);
		}

		log.info("Excel file {} unmarshalling completed successfully.", getExcelFile().getName());
		return excelFileObject;
	}

	public <T> List<T> unmarshalRows(Class<T> excelTableModel) {
		return unmarshalRows(excelTableModel, null);
	}

	public <T> List<T> unmarshalRows(Class<T> excelTableModel, List<Integer> rowsWithPrimaryKeyValues) {
		//TODO-dchubkov: check excelTableModel is valid class (is table class, not primitive, etc...)
		log.info(String.format("Getting list of table row objects of \"%1$s\" model from excel file \"%2$s\"%3$s %4$s strict match binding",
				excelTableModel.getSimpleName(),
				getExcelFile().getAbsolutePath(),
				CollectionUtils.isNotEmpty(rowsWithPrimaryKeyValues) ? ", containing values in primary key column: " + rowsWithPrimaryKeyValues : "",
				isStrictMatchBinding() ? "with" : "without"));

		List<TableRow> rows = cache.of(excelTableModel).getRows(rowsWithPrimaryKeyValues);
		List<T> tablesObjects = new ArrayList<>(rows.size());
		for (TableRow row : rows) {
			T tableRowObject = getTableRowObject(excelTableModel, row);
			tablesObjects.add(tableRowObject);
		}

		log.info("Excel table rows unmarshalling completed successfully.");
		return tablesObjects;
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
	private <T> T getTableRowObject(Class<T> tableClass, TableRow row) {
		if (cache.of(tableClass).hasObject(row.getIndex())) {
			return (T) cache.of(tableClass).getObject(row.getIndex());
		}

		T tableObject = (T) getInstance(cache.of(tableClass).getTableClass());
		for (Field tableColumnField : cache.of(tableClass).getTableColumnsFields()) {
			Object value = null;
			switch (cache.of(tableClass).getBindType(tableColumnField)) {
				case REGULAR:
					value = getFieldValue(tableColumnField, row.getCell(cache.of(tableClass).getHeaderColumnIndex(tableColumnField)));
					break;
				case TABLE:
					value = getTableValue(tableColumnField, row.getCell(cache.of(tableClass).getHeaderColumnIndex(tableColumnField)));
					break;
				case MULTY_COLUMNS:
					value = getMultyColumnsFieldValue(tableClass, tableColumnField, row);
					break;
			}
			setFieldValue(tableColumnField, tableObject, value);
		}

		cache.of(tableClass).setObject(row.getIndex(), tableObject);
		return tableObject;
	}

	private Object getFieldValue(Field field, TableCell cell) {
		if (cell.isEmpty()) {
			return null;
		}

		cell.getDateValue(ColumnFieldHelper.getFormatters(tableColumnField));
		if (cell.getCellTypes((LocalDateTimeCellType) LOCAL_DATE_TIME_TYPE).isTypeOf(cell)) {

		}

		Class<?> fieldType = List.class.equals(field.getType()) ? BindHelper.getGenericTypeClass(field) : field.getType();
		for (CellType<?> cType : excelManager.getCellTypes()) {
			/*if (cType instanceof DateCellType) {
				(DateCellType) cType.getValueFrom()
			}*/

			if (ClassUtils.isAssignable(cType.getEndType(), fieldType, true)) {
				return cell.getValue(cType);
			}
		}

		throw new IstfException(String.format("Field type \"%s\" is not supported for unmarshalling", fieldType.getName()));
	}

	private Object getTableValue(Field field, TableCell cell) {
		if (!List.class.equals(field.getType())) {
			return getTableRowObject(cache.of(field).getTableClass(), cache.of(field).getRow(cell.getIntValue()));
		}
		List<Integer> linkedTableRowIds;
		if (cell.hasType(ExcelCell.INTEGER_TYPE)) {
			linkedTableRowIds = Collections.singletonList(cell.getIntValue());
		} else {
			String[] linkedTableRowStringIds = cell.getStringValue().split(cache.of(field).getPrimaryKeysSeparator());
			linkedTableRowIds = new ArrayList<>(linkedTableRowStringIds.length);
			for (String id : linkedTableRowStringIds) {
				linkedTableRowIds.add(Integer.valueOf(id));
			}
		}

		return getTableObjectValues(cache.of(field).getTableClass(), linkedTableRowIds);
	}

	private List<Object> getMultyColumnsFieldValue(Class<?> tableClass, Field field, TableRow row) {
		List<Object> multyColumnsValues = new ArrayList<>(cache.of(tableClass).getHeaderColumnsIndexes(field).size());
		for (Integer columnIndex : cache.of(tableClass).getHeaderColumnsIndexes(field)) {
			multyColumnsValues.add(getFieldValue(field, row.getCell(columnIndex)));
		}
		return multyColumnsValues;
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
			tableObjectValues.add(getTableRowObject(tableClass, row));
		}
		return tableObjectValues;
	}

	private void setFieldValue(Field field, Object classInstance, Object value) {
		if (!field.isAccessible()) {
			//TODO-dchubkov: find appropriate setter method and use it for set value
			field.setAccessible(true);
		}
		try {
			field.set(classInstance, value);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new IstfException(String.format("Unable set value \"%1$s\" to the field \"%2$s\" with type \"%3$s\" in class \"%4$s\"",
					value != null ? value.toString() : null, field.getName(), field.getType(), classInstance.getClass().getName()), e);
		}
	}

	private Object getInstance(Class<?> clazz) {
		try {
			return clazz.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new IstfException(String.format("Failed to create instance of \"%s\" class.", clazz.getName()), e);
		}
	}
}
