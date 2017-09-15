package aaa.helpers.docgen.searchNodes;

import aaa.helpers.AdvancedMarkupParser;
import aaa.helpers.xml.models.StandardDocumentRequest;
import javafx.util.Pair;

import java.util.*;
import java.util.function.Function;

public abstract class SearchBy<N, D> {
	public static StandardDocumentRequestNode standardDocumentRequest = new StandardDocumentRequestNode();
	protected Map<String, Pair<Function<D, String>, String>> conditionsMap = new LinkedHashMap<>();

	public abstract List<D> search(StandardDocumentRequest sDocumentRequest);

	@SuppressWarnings("unchecked")
	protected final N addCondition(String conditionName, Function<D, String> conditionFunction, String expectedValue) {
		String conditionPrefix = this.getClass().getSimpleName().replaceAll("Node", "") + " -> ";
		conditionsMap.put(conditionPrefix + conditionName, new Pair<>(conditionFunction, expectedValue));
		return (N) this;
	}

	public List<D> filter(D input) {
		return filter(Collections.singletonList(input));
	}

	public List<D> filter(List<D> inputList) {
		List<D> filteredList = new ArrayList<>();
		for (D d : inputList) {
			boolean allConditionsAreMet = true;
			for (Map.Entry<String, Pair<Function<D, String>, String>> cond : conditionsMap.entrySet()) {
				if (!isConditionMet(cond.getValue().getKey().apply(d), cond.getValue().getValue())) {
					allConditionsAreMet = false;
					break;
				}
			}

			if (allConditionsAreMet) {
				filteredList.add(d);
			}
		}
		return filteredList;
	}

	@Override
	public String toString() {
		StringBuilder conditionsOutput = new StringBuilder("SearchBy{\n");
		for (Map.Entry<String, Pair<Function<D, String>, String>> cond : conditionsMap.entrySet()) {
			conditionsOutput.append("  \"").append(cond.getKey()).append("\" is \"").append(cond.getValue().getValue()).append("\"\n");
		}
		conditionsOutput.append("}");
		return conditionsOutput.toString();
	}

	private boolean isConditionMet(String actualValue, String expectedValue) {
		if (Objects.nonNull(actualValue) && Objects.nonNull(expectedValue) && expectedValue.startsWith(AdvancedMarkupParser.CONTAINS_PREFIX)) {
			return actualValue.contains(expectedValue.replaceAll(AdvancedMarkupParser.CONTAINS_PREFIX, ""));
		} else {
			return Objects.equals(actualValue, expectedValue);
		}
	}
}
