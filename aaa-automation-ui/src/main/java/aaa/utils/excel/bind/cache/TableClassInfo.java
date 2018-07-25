package aaa.utils.excel.bind.cache;

import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.bind.ReflectionHelper;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.sheet.ExcelSheet;
import aaa.utils.excel.io.entity.area.table.ExcelTable;
import toolkit.exceptions.IstfException;

public abstract class TableClassInfo {
	protected static Logger log = LoggerFactory.getLogger(TableClassInfo.class);
	private final Class<?> tableClass;
	protected List<Field> tableColumnsFields;
	private ExcelManager excelManager;
	private Class<?> annotatedTableClass;
	private Boolean isCaseIgnoredForAllColumns;
	private List<TableFieldInfo> tableFieldsInfos;
	private String primaryKeysSeparator;
	private Field primaryKeyColumnField;
	private Integer primaryKeyColumnIndex;
	
	TableClassInfo(Class<?> tableClass, ExcelManager excelManager) {
		this.tableClass = tableClass;
		this.excelManager = excelManager;
	}
	
	public Class<?> getTableClass() {
		return tableClass;
	}
	
	public Class<?> getAnnotatedTableClass() {
		if (this.annotatedTableClass == null) {
			this.annotatedTableClass = ReflectionHelper.getThisAndAllSuperClasses(tableClass).stream().filter(clazz -> clazz.isAnnotationPresent(ExcelTableElement.class)).findFirst().orElseThrow(
					() -> new IstfException(String.format("Unable to find excel table for \"%1$s\" class, neither it nor any super class has \"%2$s\" annotation", tableClass.getSimpleName(), ExcelTableElement.class.getSimpleName())));
		}
		return this.annotatedTableClass;
	}
	
	public boolean isCaseIgnoredForAllColumns() {
		if (this.isCaseIgnoredForAllColumns == null) {
			this.isCaseIgnoredForAllColumns = getAnnotatedTableClass().getAnnotation(ExcelTableElement.class).ignoreCase();
		}
		return isCaseIgnoredForAllColumns;
	}
	
	public List<Field> getTableColumnsFields() {
		if (this.tableColumnsFields == null) {
			this.tableColumnsFields = ReflectionHelper.getAllAccessibleFieldsFromThisAndSuperClasses(getTableClass());
		}
		return Collections.unmodifiableList(this.tableColumnsFields);
	}
	
	public abstract ExcelSheet getExcelSheet();
	
	public abstract ExcelTable getExcelTable();
	
	public void flushInfo() {
		this.tableColumnsFields = null;
		this.tableFieldsInfos = null;
	}
	
	public String getPrimaryKeysSeparator() {
		if (this.primaryKeysSeparator == null) {
			this.primaryKeysSeparator = getFieldInfo(getPrimaryKeyColumnField()).getPrimaryKeySeparator();
		}
		return this.primaryKeysSeparator;
	}
	
	public Field getPrimaryKeyColumnField() {
		if (this.primaryKeyColumnField == null) {
			this.primaryKeyColumnField = getTableFieldsInfos().stream().filter(TableFieldInfo::isPrimaryKeyField).findFirst()
					.orElseThrow(() -> new IstfException(String.format("\"%s\" class does not have any primary key field", tableClass.getName()))).getTableField();
		}
		return this.primaryKeyColumnField;
	}
	
	public Integer getPrimaryKeyColumnIndex() {
		if (this.primaryKeyColumnIndex == null) {
			this.primaryKeyColumnIndex = getHeaderColumnIndex(getPrimaryKeyColumnField());
		}
		return this.primaryKeyColumnIndex;
	}
	
	public boolean isCaseIgnoredInAnyColumnField() {
		return getTableColumnsFields().stream().anyMatch(this::isCaseIgnored);
	}
	
	ExcelManager getExcelManager() {
		return excelManager;
	}
	
	public TableFieldInfo getFieldInfo(Field tableField) {
		for (TableFieldInfo tableFieldInfo : getTableFieldsInfos()) {
			if (tableFieldInfo.getTableField().equals(tableField)) {
				return tableFieldInfo;
			}
		}
		throw new IstfException(String.format("Class \"%s\" does not have \"%s\" field", getTableClass().getName(), tableField));
	}
	
	public boolean isCaseIgnored(Field tableField) {
		return isCaseIgnoredForAllColumns() || getFieldInfo(tableField).isCaseIgnored();
	}
	
	public String getHeaderColumnName(Field tableField) {
		return getFieldInfo(tableField).getHeaderColumnName();
	}
	
	public int getHeaderColumnIndex(Field tableField) {
		return getFieldInfo(tableField).getHeaderColumnIndex(getExcelTable().getHeader(), isCaseIgnoredForAllColumns());
	}
	
	public List<Integer> getHeaderColumnsIndexes(Field tableField) {
		return getFieldInfo(tableField).getHeaderColumnsIndexes(getExcelTable().getHeader(), isCaseIgnoredForAllColumns());
	}
	
	public TableFieldInfo.BindType getBindType(Field tableField) {
		return getFieldInfo(tableField).getBindType();
	}
	
	public Class<?> getFieldType(Field tableField) {
		return getFieldInfo(tableField).getFieldType();
	}
	
	public DateTimeFormatter[] getDateTimeFormatters(Field tableField) {
		return getFieldInfo(tableField).getDateTimeFormatters();
	}
	
	public CellType<?> getCellType(Field tableField) {
		return getFieldInfo(tableField).getCellType(this.excelManager.getCellTypes());
	}
	
	private List<TableFieldInfo> getTableFieldsInfos() {
		if (this.tableFieldsInfos == null) {
			List<Field> tableColumnsFields = getTableColumnsFields();
			this.tableFieldsInfos = new ArrayList<>(tableColumnsFields.size());
			for (Field tableField : tableColumnsFields) {
				this.tableFieldsInfos.add(new TableFieldInfo(tableField, getTableClass(), getExcelManager().getCellTypes()));
			}
		}
		return this.tableFieldsInfos;
	}
}
