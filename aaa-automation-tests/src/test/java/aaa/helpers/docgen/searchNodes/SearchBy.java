package aaa.helpers.docgen.searchNodes;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import aaa.helpers.xml.models.DocumentPackage;

public abstract class SearchBy<N, D> {
	public static DocumentPackageNode documentPackage = new DocumentPackageNode();

	protected Predicate<D> condition;

	public abstract List<D> search(List<DocumentPackage> documentsList);

	@SuppressWarnings("unchecked")
	protected final N addCondition(Predicate<D> condition) {
		this.condition = Objects.isNull(this.condition) ? condition : this.condition.and(condition);
		return (N) this;
	}

	protected Predicate<D> getConditionAndClear() {
		Predicate<D> copiedCondition = Objects.isNull(condition) ? d -> true : condition;
		condition = null;
		return copiedCondition;
	}

	@Override
	//TODO-dchubkov: implement output of all condition values
	public String toString() {
		return "SearchBy{" +
				"condition=" + condition +
				'}';
	}
}
