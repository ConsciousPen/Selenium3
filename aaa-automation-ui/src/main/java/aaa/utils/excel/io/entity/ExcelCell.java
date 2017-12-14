package aaa.utils.excel.io.entity;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import org.apache.poi.ss.usermodel.Cell;
import aaa.utils.excel.io.celltype.BaseCellType;
import aaa.utils.excel.io.celltype.CellType;

public class ExcelCell<T> {
	private Cell cell;
	private BaseCellType<T> cellType;

	public ExcelCell(Cell cell, BaseCellType<T> cellType) {
		assertThat(cell).isNotNull().as("Excel cell should not be null");
		assertThat(cellType).isNotNull().as("Cell type should not be null");
		this.cell = cell;
		this.cellType = cellType;
	}

	public BaseCellType<T> getCellType() {
		return cellType;
	}

	public int getCellNumber() {
		return getCell().getColumnIndex() + 1;
	}

	public Object getValue() {
		return getCellType().getValueFrom(getCell());
	}

	public Boolean getBoolValue() {
		return (Boolean) getValue(CellType.BOOLEAN.get());
	}

	public String getStringValue() {
		return (String) getValue(CellType.STRING.get());
	}

	public int getIntValue() {
		return (Integer) getValue(CellType.INTEGER.get());
	}

	public LocalDateTime getDateValue() {
		return (LocalDateTime) getValue(CellType.LOCAL_DATE_TIME.get());
	}

	Cell getCell() {
		return cell;
	}

	public T getValue(BaseCellType<T> cellType) {
		assertThat(cellType.isTypeOf(cell)).as("Unable to get value with type %s from cell, located in %s", cellType.getName(), getLocation());
		return cellType.getValueFrom(getCell());
	}

	private Object getLocation() {
		return ExcelRow.getLocation(getCell());
	}
}
