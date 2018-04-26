package aaa.utils.excel.io.celltype;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.utils.excel.io.entity.area.ExcelCell;

public class LocalDateTimeCellType extends DateCellType<LocalDateTime> {

	public LocalDateTimeCellType(Class<LocalDateTime> endType, DateTimeFormatter... formatters) {
		super(endType);
		if (ArrayUtils.isEmpty(formatters)) {
			List<DateTimeFormatter> defaultFormatters = new ArrayList<>();
			defaultFormatters.add(new DateTimeFormatterBuilder().appendPattern("MM/dd/yyyy")
					.parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
					.parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
					.parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
					.toFormatter());
			defaultFormatters.add(DateTimeFormatter.ofPattern("MM/dd/yyyy HHmmss"));

			setFormatters(defaultFormatters);
		}
	}

	@Override
	protected LocalDateTime parseText(String dateInTextFormat, DateTimeFormatter dateTimeFormatters) {
		//TODO-dchubkov: implement independent logic from TimeSetterUtil
		return TimeSetterUtil.getInstance().parse(dateInTextFormat, dateTimeFormatters);
	}

	@Override
	protected LocalDateTime getDateValue(ExcelCell cell) {
		return cell.getPoiCell().getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	/*public Set<DateTimeFormatter> getFormatters() {
		return new HashSet<>(this.dateTimeFormatters);
	}*/

	/*@Override
	public void setValueTo(ExcelCell cell, LocalDateTime value) {
		Date date = Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
		cell.getPoiCell().setCellValue(date);
	}*/

	@Override
	protected Date convertToDate(LocalDateTime value) {
		return Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
	}

	/*@Override
	public boolean isTypeOf(ExcelCell cell) {
		Set<DateTimeFormatter> formatters = getFormatters();
		return isTypeOf(cell, formatters.toArray(new DateTimeFormatter[formatters.size()]));
	}

	@Override
	public boolean hasValueInTextFormat(ExcelCell cell) {
		Set<DateTimeFormatter> formatters = getFormatters();
		return hasValueInTextFormat(cell, formatters.toArray(new DateTimeFormatter[formatters.size()]));
	}*/

	/*@Override
	public LocalDateTime getValueFrom(ExcelCell cell) {
		Set<DateTimeFormatter> formatters = getFormatters();
		return getValueFrom(cell, formatters.toArray(new DateTimeFormatter[formatters.size()]));
	}

	public boolean isTypeOf(ExcelCell cell, DateTimeFormatter... formatters) {
		Cell c = cell.getPoiCell();
		return c == null || c.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.NUMERIC && DateUtil.isCellDateFormatted(c) || hasValueInTextFormat(cell, formatters);
	}*/

	/*public void registerFormatters(DateTimeFormatter... formatters) {
		this.dateTimeFormatters.addAll(Arrays.asList(formatters));
	}*/

	/*public LocalDateTime getValueFrom(ExcelCell cell, DateTimeFormatter... formatters) {
		assertThat(isTypeOf(cell, formatters)).as("Unable to get value with \"%1$s\" type from %2$s", getEndType(), cell).isTrue();
		if (cell.getPoiCell() == null) {
			return null;
		}
		if (super.hasValueInTextFormat(cell)) {
			DateTimeFormatter formatter = getValidFormatter(cell, formatters);
			if (formatter != null) {
				return TimeSetterUtil.getInstance().parse(getText(cell), formatter);
			}
		}
		return cell.getPoiCell().getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}*/

	/*public boolean hasValueInTextFormat(ExcelCell cell, DateTimeFormatter... formatters) {
		return super.hasValueInTextFormat(cell) && getValidFormatter(cell, formatters) != null;
	}*/

	/*protected DateTimeFormatter getValidFormatter(ExcelCell cell, DateTimeFormatter... formatters) {
		String text = getText(cell);
		if (text == null) {
			return null;
		}
		Set<DateTimeFormatter> availableFormatters = ArrayUtils.isNotEmpty(formatters) ? new HashSet<>(Arrays.asList(formatters)) : getFormatters();
		for (DateTimeFormatter dateTimeFormatter : availableFormatters) {
			try {
				TimeSetterUtil.getInstance().parse(text, dateTimeFormatter);
				return dateTimeFormatter;
			} catch (DateTimeParseException ignore) {
			}
		}
		return null;
	}*/
}
