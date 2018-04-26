package aaa.utils.excel.io.celltype;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.utils.excel.io.entity.area.ExcelCell;

public abstract class DateCellType<T extends Temporal> extends AbstractCellType<T> {
	private List<DateTimeFormatter> dateTimeFormatters;

	public DateCellType(Class<T> endType, DateTimeFormatter... formatters) {
		super(endType);
		if (ArrayUtils.isNotEmpty(formatters)) {
			this.dateTimeFormatters = new ArrayList<>(Arrays.stream(formatters).collect(Collectors.toSet())); // collect to set to remove possible duplicates
		}
	}

	public List<DateTimeFormatter> getFormatters() {
		return Collections.unmodifiableList(this.dateTimeFormatters);
	}

	protected final void setFormatters(List<DateTimeFormatter> dateTimeFormatters) {
		this.dateTimeFormatters = new ArrayList<>(dateTimeFormatters);
	}

	@Override
	public boolean isTypeOf(ExcelCell cell) {
		return isTypeOf(cell, this.dateTimeFormatters);
	}

	@Override
	public void setValueTo(ExcelCell cell, T value) {
		cell.getPoiCell().setCellValue(convertToDate(value));
	}

	@Override
	public T getValueFrom(ExcelCell cell) {
		return getValueFrom(cell, getFormatters());
	}

	public boolean isTypeOf(ExcelCell cell, List<DateTimeFormatter> dateTimeFormatters) {
		Cell c = cell.getPoiCell();
		return c == null || c.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.NUMERIC && DateUtil.isCellDateFormatted(c) || hasValueInTextFormat(cell, dateTimeFormatters);
	}

	public boolean hasValueInTextFormat(ExcelCell cell, List<DateTimeFormatter> dateTimeFormatters) {
		return hasValueInTextFormat(cell) && getValidFormatter(cell, dateTimeFormatters) != null;
	}

	public T getValueFrom(ExcelCell cell, List<DateTimeFormatter> dateTimeFormatters) {
		assertThat(isTypeOf(cell, dateTimeFormatters)).as("Unable to get value with \"%1$s\" type from %2$s", getEndType(), cell).isTrue();
		if (cell.getPoiCell() == null) {
			return null;
		}
		if (hasValueInTextFormat(cell)) {
			DateTimeFormatter formatter = getValidFormatter(cell, dateTimeFormatters);
			if (formatter != null) {
				//return TimeSetterUtil.getInstance().parse(getText(cell), formatter);
				return parseText(getText(cell), formatter);
			}
		}
		//return cell.getPoiCell().getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		return getDateValue(cell);
	}

	protected abstract T parseText(String dateInTextFormat, DateTimeFormatter dateTimeFormatters);

	protected abstract T getDateValue(ExcelCell cell);

	protected DateTimeFormatter getValidFormatter(ExcelCell cell, List<DateTimeFormatter> dateTimeFormatters) {
		String text = getText(cell);
		if (text == null) {
			return null;
		}
		List<DateTimeFormatter> availableFormatters = CollectionUtils.isNotEmpty(dateTimeFormatters) ? dateTimeFormatters : getFormatters();
		for (DateTimeFormatter dateTimeFormatter : availableFormatters) {
			try {
				TimeSetterUtil.getInstance().parse(text, dateTimeFormatter);
				return dateTimeFormatter;
			} catch (DateTimeParseException ignore) {
			}
		}
		return null;
	}

	protected abstract Date convertToDate(T value);

}
