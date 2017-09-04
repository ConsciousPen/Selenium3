package aaa.helpers.docgen.searchNodes;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import aaa.helpers.xml.models.DocumentPackage;

public abstract class SearchBy<N, D> {
	public static DocumentPackageNode documentPackage = new DocumentPackageNode();

	protected Predicate<D> condition;

	@SuppressWarnings("unchecked")
	protected final N addCondition(Predicate<D> condition) {
		this.condition = Objects.isNull(this.condition) ? condition : this.condition.and(condition);
		return (N) this;
	}

	@SuppressWarnings("unchecked")
	protected <D> Predicate<D> getConditionAndClear() {
		Predicate<D> copiedCondition = Objects.isNull(condition) ? d -> true : (Predicate<D>) condition;
		condition = null;
		return copiedCondition;
	}

	public abstract List<D> search(List<DocumentPackage> documentsList);

	@Override
	public String toString() {
		return "SearchBy{" +
				"condition=" + condition +
				'}';
	}
}