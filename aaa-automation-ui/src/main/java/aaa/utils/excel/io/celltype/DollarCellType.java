package aaa.utils.excel.io.celltype;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.utils.excel.io.entity.area.ExcelCell;

public class DollarCellType extends AbstractCellType<Dollar> {
	public DollarCellType(Class<Dollar> endType) {
		super(endType);
	}

	@Override
	public Dollar getRawValueFrom(ExcelCell cell) {
		return hasValueInTextFormat(cell) ? new Dollar(getText(cell)) : new Dollar(cell.getPoiCell().getNumericCellValue());
	}

	@Override
	public boolean hasValueInTextFormat(ExcelCell cell) {
		return super.hasValueInTextFormat(cell) && (NumberUtils.isCreatable(getText(cell)) || hasValueInDollarFormat(cell));
	}

	@Override
	public void setRawValueTo(ExcelCell cell, Dollar value) {
		cell.getPoiCell().setCellValue(value.toString());
	}

	@Override
	public boolean isTypeOf(ExcelCell cell) {
		return cell.getPoiCell() == null || isNumeric(cell) || hasValueInTextFormat(cell);
	}

	protected boolean isNumeric(ExcelCell cell) {
		Cell c = cell.getPoiCell();
		return c.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.NUMERIC && !DateUtil.isCellDateFormatted(c);
	}

	private boolean hasValueInDollarFormat(ExcelCell cell) {
		return getText(cell).matches("^\\(?\\$\\d+(\\.\\d{2})?\\)?$");
	}
}
