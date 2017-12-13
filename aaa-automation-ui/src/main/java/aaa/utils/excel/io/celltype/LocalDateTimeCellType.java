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
import toolkit.utils.datetime.DateTimeUtils;

public class LocalDateTimeCellType extends BaseCellType<LocalDateTime> {
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
	public LocalDateTime getValueFrom(Cell cell) {
		assertThat(isTypeOf(cell)).as("Cell type is not a %s type, unable to get value", getName());
		if (isStoredAsText(cell)) {
			return TimeSetterUtil.getInstance().parse(getText(cell), getFormatter(cell));
		}
		return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	@Override
	public boolean isTypeOf(Cell cell) {
		Cell c = normalizeCell(cell);
		return c.getCellType() == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(c) || isStoredAsText(c);
	}

	@Override
	public boolean isStoredAsText(Cell cell) {
		return super.isStoredAsText(cell) && getFormatter(cell) != null;
	}

	public DateTimeFormatter getFormatter(Cell cell) {
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
