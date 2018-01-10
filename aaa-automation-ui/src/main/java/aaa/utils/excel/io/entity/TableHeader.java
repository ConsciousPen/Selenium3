package aaa.utils.excel.io.entity;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.celltype.CellType;

public class TableHeader extends TableRow {

	public TableHeader(Row row, ExcelTable table) {
		super(row, 0, row.getRowNum() + 1, table);
		this.cellTypes.removeIf(t -> !t.equals(ExcelCell.STRING_TYPE));
		assertThat(this.cellTypes).as("Table header row should have type " + ExcelCell.STRING_TYPE).isNotEmpty();
	}

	@Override
	public String toString() {
		return "TableHeader{" +
				"headerColumns=" + getColumnsNames() +
				'}';
	}

	@Override
	public Boolean getBoolValue(int columnIndex) {
		throw new UnsupportedOperationException("Table header cells don't have boolean values");
	}

	@Override
	public Boolean getBoolValue(String headerColumnName) {
		throw new UnsupportedOperationException("Table header cells don't have boolean values");
	}

	@Override
	public Integer getIntValue(int columnIndex) {
		throw new UnsupportedOperationException("Table header cells don't have int values");
	}

	@Override
	public Integer getIntValue(String headerColumnName) {
		throw new UnsupportedOperationException("Table header cells don't have int values");
	}

	@Override
	public LocalDateTime getDateValue(int columnIndex) {
		throw new UnsupportedOperationException("Table header cells don't have LocalDateTime values");
	}

	@Override
	public LocalDateTime getDateValue(String headerColumnName) {
		throw new UnsupportedOperationException("Table header cells don't have LocalDateTime values");
	}

	@Override
	public TableHeader registerCellType(CellType<?>... cellTypes) {
		throw new UnsupportedOperationException("Table header cell types should not be updated");
	}

	@Override
	public TableHeader exclude() {
		throw new UnsupportedOperationException("Table header exclusion is not supported");
	}

	@Override
	public TableHeader clear() {
		throw new UnsupportedOperationException("Table header erasing is not supported");
	}

	@Override
	public ExcelTable delete() {
		throw new UnsupportedOperationException("Table header deleting is not supported");
	}
}
