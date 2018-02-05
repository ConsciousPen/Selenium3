package aaa.utils.excel.io.celltype;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.utils.excel.io.entity.cell.EditableCell;
import aaa.utils.excel.io.entity.cell.ExcelCell;
import toolkit.utils.datetime.DateTimeUtils;

public class LocalDateTimeCellType extends AbstractCellType<LocalDateTime> {
	private Set<DateTimeFormatter> dateTimeFormatters;

	public LocalDateTimeCellType(Class<LocalDateTime> endType, DateTimeFormatter... formatters) {
		super(endType);
		if (ArrayUtils.isNotEmpty(formatters)) {
			this.dateTimeFormatters = new HashSet<>(Arrays.asList(formatters));
		} else {
			this.dateTimeFormatters = new HashSet<>();
			this.dateTimeFormatters.add(DateTimeUtils.MM_DD_YYYY);
			this.dateTimeFormatters.add(DateTimeUtils.TIME_STAMP);
		}
	}

	public Set<DateTimeFormatter> getFormatters() {
		return new HashSet<>(this.dateTimeFormatters);
	}

	@Override
	public void setValueTo(EditableCell cell, LocalDateTime value) {
		Date date = Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
		createPoiCellIfNull(cell).getPoiCell().setCellValue(date);
	}

	@Override
	public boolean isTypeOf(ExcelCell cell) {
		Set<DateTimeFormatter> formatters = getFormatters();
		return isTypeOf(cell, formatters.toArray(new DateTimeFormatter[formatters.size()]));
	}

	@Override
	public boolean hasValueInTextFormat(ExcelCell cell) {
		Set<DateTimeFormatter> formatters = getFormatters();
		return hasValueInTextFormat(cell, formatters.toArray(new DateTimeFormatter[formatters.size()]));
	}

	@Override
	public LocalDateTime getValueFrom(ExcelCell cell) {
		Set<DateTimeFormatter> formatters = getFormatters();
		return getValueFrom(cell, formatters.toArray(new DateTimeFormatter[formatters.size()]));
	}

	public void registerFormatters(DateTimeFormatter... formatters) {
		this.dateTimeFormatters.addAll(Arrays.asList(formatters));
	}

	public LocalDateTime getValueFrom(ExcelCell cell, DateTimeFormatter... formatters) {
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
	}

	public boolean isTypeOf(ExcelCell cell, DateTimeFormatter... formatters) {
		Cell c = cell.getPoiCell();
		return c == null || c.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.NUMERIC && DateUtil.isCellDateFormatted(c) || hasValueInTextFormat(cell, formatters);
	}

	public boolean hasValueInTextFormat(ExcelCell cell, DateTimeFormatter... formatters) {
		return super.hasValueInTextFormat(cell) && getValidFormatter(cell, formatters) != null;
	}

	protected DateTimeFormatter getValidFormatter(ExcelCell cell, DateTimeFormatter... formatters) {
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
	}
}
