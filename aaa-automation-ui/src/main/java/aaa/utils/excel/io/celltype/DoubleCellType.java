package aaa.utils.excel.io.celltype;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.regex.Pattern;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import aaa.utils.excel.io.entity.cell.EditableCell;
import aaa.utils.excel.io.entity.cell.ExcelCell;

public class DoubleCellType extends AbstractCellType<Double> {
	public DoubleCellType(Class<Double> endType) {
		super(endType);
	}

	@Override
	public Double getValueFrom(ExcelCell cell) {
		assertThat(isTypeOf(cell)).as("Cell type is not a %s type, unable to get value", getEndType()).isTrue();
		if (cell.getPoiCell() == null) {
			return null;
		}
		if (hasTextValue(cell)) {
			return Double.valueOf(getText(cell));
		}
		return cell.getPoiCell().getNumericCellValue();
	}

	@Override
	public void setValueTo(EditableCell cell, Double value) {
		createPoiCellIfNull(cell).getPoiCell().setCellValue(value);
	}

	@Override
	public boolean isTypeOf(ExcelCell cell) {
		Cell c = cell.getPoiCell();
		return c == null
				|| c.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.NUMERIC && !DateUtil.isCellDateFormatted(c) && isDoubleFormat(new DataFormatter().formatCellValue(c))
				|| hasTextValue(cell);
	}

	@Override
	public boolean hasTextValue(ExcelCell cell) {
		if (super.hasTextValue(cell)) {
			String textValue = getText(cell);
			return NumberUtils.isCreatable(textValue) && isDoubleFormat(textValue);
		}
		return false;
	}

	private boolean isDoubleFormat(String value) {
		//TODO-dchubkov: test this regexp better
		Pattern p = Pattern.compile("^([+-])?\\d+\\.[0]*[1-9]+\\d*$");
		return p.matcher(value).matches();
	}
}
