package aaa.utils.excel.io.entity.area.table;

import java.util.Arrays;
import javax.ws.rs.NotSupportedException;
import org.apache.poi.ss.usermodel.Cell;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.ExcelCell;

public class HeaderCell extends TableCell {
	public HeaderCell(Cell cell, int columnIndexInTable, int columnIndexOnSheet, TableHeader header) {
		super(cell, columnIndexInTable, columnIndexOnSheet, header, header.getCellTypes());
	}

	@Override
	public HeaderCell clear() {
		throw new NotSupportedException("Clearing of table header's cell is not supported");
	}

	@Override
	public HeaderCell delete() {
		throw new NotSupportedException("Deletion of table header's cell is not supported");
	}

	@Override
	public HeaderCell registerCellType(CellType<?>... cellTypes) {
		if (Arrays.stream(cellTypes).anyMatch(t -> !ExcelCell.STRING_TYPE.equals(t))) {
			throw new NotSupportedException("Table header's cell does not support non string cell types");
		}
		return (HeaderCell) super.registerCellType(cellTypes);
	}
}
