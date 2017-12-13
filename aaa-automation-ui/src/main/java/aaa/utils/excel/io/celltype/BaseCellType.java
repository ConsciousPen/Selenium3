package aaa.utils.excel.io.celltype;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

public abstract class BaseCellType<T> {
	protected String name;

	public BaseCellType() {
		this.name = ((Class<?>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getSimpleName();
	}

	public String getName() {
		return name;
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

	public abstract T getValueFrom(Cell cell);

	public abstract boolean isTypeOf(Cell cell);

	public boolean isStoredAsText(Cell cell) {
		return normalizeCell(cell).getCellType() == Cell.CELL_TYPE_STRING;
	}

	protected String getText(Cell cell) {
		return CellType.STRING.get().getText(cell);
	}

	protected Cell normalizeCell(Cell cell) {
		if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
			FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
			return evaluator.evaluateInCell(cell);
		}
		return cell;
	}
}
