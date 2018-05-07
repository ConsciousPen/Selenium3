package aaa.utils.excel.io.celltype;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import aaa.utils.excel.io.entity.area.ExcelCell;

public class LocalDateCellType extends DateCellType<LocalDate> {

	public LocalDateCellType(Class<LocalDate> endType, DateTimeFormatter... dateTimeFormatters) {
		super(endType);
		if (ArrayUtils.isEmpty(dateTimeFormatters)) {
			List<DateTimeFormatter> defaultFormatters = new ArrayList<>();
			defaultFormatters.add(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
			defaultFormatters.add(DateTimeFormatter.ofPattern("yyyyMMdd"));

			setFormatters(defaultFormatters);
		}
	}

	@Override
	protected LocalDate parseText(String dateInTextFormat, DateTimeFormatter dateTimeFormatter) {
		return LocalDate.parse(dateInTextFormat, dateTimeFormatter);
	}

	@Override
	protected LocalDate getDate(ExcelCell cell) {
		return cell.getPoiCell().getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	@Override
	protected Date convertToJavaDate(LocalDate value) {
		return Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
}
