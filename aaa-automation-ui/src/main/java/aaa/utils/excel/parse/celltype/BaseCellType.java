package aaa.utils.excel.parse.celltype;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import org.apache.poi.ss.usermodel.Cell;
import aaa.utils.excel.ExcelParser;

public abstract class BaseCellType<T> {
	protected ExcelParser excelParser;
	protected String name;

	public BaseCellType() {
		this.name = ((Class<?>)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getSimpleName();
	}

	public abstract T getValueFrom(Cell cell);

	public abstract boolean isTypeOf(Cell cell);


	public String getName() {
		return name;
	}

	protected ExcelParser getParser(Cell cell) {
		if (excelParser == null) {
			excelParser = new ExcelParser(cell.getSheet());
		}
		return excelParser.switchSheet(cell.getSheet().getSheetName());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		BaseCellType<?> that = (BaseCellType<?>) o;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
