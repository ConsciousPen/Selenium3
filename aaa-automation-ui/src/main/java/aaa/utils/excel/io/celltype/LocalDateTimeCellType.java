package aaa.utils.excel.io.celltype;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.utils.excel.io.entity.ExcelCell;
import toolkit.utils.datetime.DateTimeUtils;

public class LocalDateTimeCellType extends CellType<LocalDateTime> {
	private Set<DateTimeFormatter> commonDateTimeFormatters;

	public LocalDateTimeCellType() {
		commonDateTimeFormatters = new HashSet<>();
		commonDateTimeFormatters.add(DateTimeUtils.MM_DD_YYYY);
		commonDateTimeFormatters.add(DateTimeUtils.DD_MM_YYYY);
		commonDateTimeFormatters.add(DateTimeUtils.TIME_STAMP);
		commonDateTimeFormatters.add(DateTimeUtils.TIME_STAMP_WITH_MS);
		//TODO-dchubkov: extend list of date time formatters
	}

	@Override
	public LocalDateTime getValueFrom(ExcelCell cell) {
		assertThat(isTypeOf(cell)).as("Cell type is not a %s type, unable to get value", getEndType());
		if (hasTextValue(cell)) {
			return TimeSetterUtil.getInstance().parse(getText(cell), getFormatter(cell));
		}
		return cell.getPoiCell().getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	@Override
	public boolean isTypeOf(ExcelCell cell) {
		Cell c = cell.getPoiCell();
		return c.getCellType() == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(c) || hasTextValue(cell);
	}

	@Override
	public boolean hasTextValue(ExcelCell cell) {
		return super.hasTextValue(cell) && getFormatter(cell) != null;
	}

	public DateTimeFormatter getFormatter(ExcelCell cell) {
		DateTimeFormatter formatter;
		for (DateTimeFormatter dateTimeFormatter : commonDateTimeFormatters) {
			try {
				TimeSetterUtil.getInstance().parse(getText(cell), dateTimeFormatter);
				return dateTimeFormatter;
			} catch (DateTimeParseException ignore) {
			}
		}
		return null;
	}
}
