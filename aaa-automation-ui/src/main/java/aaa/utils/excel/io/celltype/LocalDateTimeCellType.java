package aaa.utils.excel.io.celltype;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.utils.excel.io.entity.ExcelCell;
import toolkit.utils.datetime.DateTimeUtils;

public class LocalDateTimeCellType extends AbstractCellType<LocalDateTime> {
	private Set<DateTimeFormatter> commonDateTimeFormatters;

	public LocalDateTimeCellType(Class<LocalDateTime> endType) {
		super(endType);
		commonDateTimeFormatters = new HashSet<>();
		commonDateTimeFormatters.add(DateTimeUtils.MM_DD_YYYY);
		commonDateTimeFormatters.add(DateTimeUtils.DD_MM_YYYY);
		commonDateTimeFormatters.add(DateTimeUtils.TIME_STAMP);
		commonDateTimeFormatters.add(DateTimeUtils.TIME_STAMP_WITH_MS);
		//TODO-dchubkov: extend list of date time formatters
	}

	@Override
	public LocalDateTime getValueFrom(ExcelCell cell) {
		assertThat(isTypeOf(cell)).as("Cell type is not a %s type, unable to get value", getEndType()).isTrue();
		if (cell.getPoiCell() == null) {
			return null;
		}
		if (hasTextValue(cell)) {
			return TimeSetterUtil.getInstance().parse(getText(cell), getFormatter(cell));
		}
		return cell.getPoiCell().getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	@Override
	public void setValueTo(ExcelCell cell, LocalDateTime value) {
		Date date = Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
		createPoiCellIfNull(cell).getPoiCell().setCellValue(date);
	}

	@Override
	public boolean isTypeOf(ExcelCell cell) {
		Cell c = cell.getPoiCell();
		return c == null || c.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.NUMERIC && DateUtil.isCellDateFormatted(c) || hasTextValue(cell);
	}

	@Override
	public boolean hasTextValue(ExcelCell cell) {
		return super.hasTextValue(cell) && getFormatter(cell) != null;
	}

	public DateTimeFormatter getFormatter(ExcelCell cell) {
		String text = getText(cell);
		if (text == null) {
			return null;
		}

		DateTimeFormatter formatter;
		for (DateTimeFormatter dateTimeFormatter : commonDateTimeFormatters) {

			try {
				TimeSetterUtil.getInstance().parse(text, dateTimeFormatter);
				return dateTimeFormatter;
			} catch (DateTimeParseException ignore) {
			}
		}
		return null;
	}
}
