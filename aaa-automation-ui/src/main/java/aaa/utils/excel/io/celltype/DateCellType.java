package aaa.utils.excel.io.celltype;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import aaa.utils.excel.io.entity.area.ExcelCell;

public abstract class DateCellType<T extends Temporal> extends AbstractCellType<T> {
	private DateTimeFormatter[] dateTimeFormatters;

	public DateCellType(Class<T> endType, DateTimeFormatter... dateTimeFormatters) {
		super(endType);
		if (ArrayUtils.isNotEmpty(dateTimeFormatters)) {
			this.dateTimeFormatters = Arrays.stream(dateTimeFormatters).distinct().collect(Collectors.toList()).toArray(new DateTimeFormatter[0]);
		}
	}

	public List<DateTimeFormatter> getFormatters() {
		return Stream.of(this.dateTimeFormatters).collect(Collectors.toList());
	}

	protected final void setFormatters(List<DateTimeFormatter> dateTimeFormatters) {
		this.dateTimeFormatters = dateTimeFormatters.toArray(new DateTimeFormatter[0]);
	}

	@Override
	public boolean isTypeOf(ExcelCell cell) {
		return isTypeOf(cell, this.dateTimeFormatters);
	}

	@Override
	public void setValueTo(ExcelCell cell, T value) {
		cell.getPoiCell().setCellValue(convertToJavaDate(value));
	}

	@Override
	public T getValueFrom(ExcelCell cell) {
		return getValueFrom(cell, this.dateTimeFormatters);
	}

	@Override
	public boolean hasValueInTextFormat(ExcelCell cell) {
		return hasValueInTextFormat(cell, this.dateTimeFormatters);
	}

	public boolean isTypeOf(ExcelCell cell, DateTimeFormatter... dateTimeFormatters) {
		Cell c = cell.getPoiCell();
		return c == null || c.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.NUMERIC && DateUtil.isCellDateFormatted(c) || hasValueInTextFormat(cell, dateTimeFormatters);
	}

	public boolean hasValueInTextFormat(ExcelCell cell, DateTimeFormatter... dateTimeFormatters) {
		return hasStringValue(cell) && getValidFormatter(cell, dateTimeFormatters) != null;
	}

	public T getValueFrom(ExcelCell cell, DateTimeFormatter... dateTimeFormatters) {
		assertThat(isTypeOf(cell, dateTimeFormatters)).as("Unable to get value with \"%1$s\" type from %2$s using %3$s date formatter", getEndType(), cell, Arrays.asList(dateTimeFormatters)).isTrue();
		if (cell.getPoiCell() == null) {
			return null;
		}
		if (hasStringValue(cell)) {
			DateTimeFormatter formatter = getValidFormatter(cell, dateTimeFormatters);
			if (formatter != null) {
				return parseText(getText(cell), formatter);
			}
		}
		return getDate(cell);
	}

	protected abstract T parseText(String dateInTextFormat, DateTimeFormatter dateTimeFormatter) throws DateTimeParseException;

	protected abstract T getDate(ExcelCell cell);

	protected abstract Date convertToJavaDate(T value);

	protected boolean hasStringValue(ExcelCell cell) {
		return cell.getPoiCell() != null && cell.getPoiCell().getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.STRING;
	}

	protected DateTimeFormatter getValidFormatter(ExcelCell cell, DateTimeFormatter... dateTimeFormatters) {
		String text = getText(cell);
		if (text == null) {
			return null;
		}
		DateTimeFormatter[] availableFormatters = ArrayUtils.isNotEmpty(dateTimeFormatters) ? dateTimeFormatters : this.dateTimeFormatters;
		for (DateTimeFormatter formatter : availableFormatters) {
			try {
				parseText(text, formatter);
				return formatter;
			} catch (DateTimeParseException ignore) {
			}
		}
		return null;
	}

}
