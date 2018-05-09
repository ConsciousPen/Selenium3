package aaa.utils.excel.io.celltype;

import static org.assertj.core.api.Assertions.assertThat;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.utils.excel.io.entity.area.ExcelCell;

public class DollarCellType extends AbstractCellType<Dollar> {
	public DollarCellType(Class<Dollar> endType) {
		super(endType);
	}

	@Override
	public Dollar getValueFrom(ExcelCell cell) {
		assertThat(isTypeOf(cell)).as("Unable to get value with \"%1$s\" type from %2$s", getEndType(), cell).isTrue();
		if (cell.getPoiCell() == null) {
			return null;
		}
		if (hasValueInTextFormat(cell)) {
			return new Dollar(getText(cell));
		}
		return new Dollar(cell.getPoiCell().getNumericCellValue());
	}

	@Override
	public boolean hasValueInTextFormat(ExcelCell cell) {
		return super.hasValueInTextFormat(cell) && (NumberUtils.isCreatable(getText(cell)) || hasValueInDollarFormat(cell));
	}

	@Override
	public void setValueTo(ExcelCell cell, Dollar value) {
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
