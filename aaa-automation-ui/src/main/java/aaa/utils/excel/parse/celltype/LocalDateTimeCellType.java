package aaa.utils.excel.parse.celltype;

import java.time.LocalDateTime;
import org.apache.poi.ss.usermodel.Cell;

public class LocalDateTimeCellType extends BaseCellType<LocalDateTime> {
	@Override
	public LocalDateTime getValueFrom(Cell cell) {
		return null;
	}

	@Override
	public boolean isTypeOf(Cell cell) {
		return false;
	}
}
