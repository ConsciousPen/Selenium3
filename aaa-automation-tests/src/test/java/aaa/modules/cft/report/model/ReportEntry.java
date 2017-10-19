package aaa.modules.cft.report.model;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ReportEntry {


	private Map<DataSourceKey, Double> amount = new HashMap<>();

	public void addAmount(DataSourceKey key, Double value) {
		this.amount.put(key, value);
	}

	public Map<DataSourceKey, Double> getAmount() {
		return amount;
	}

	public Double getDelta(DataSourceKey key, DataSourceKey key2) {
		if (Objects.nonNull(amount.get(key)) && Objects.nonNull(amount.get(key2))) {
			return amount.get(key) - amount.get(key2);
		}
		return null;
	}

	public EntryStatus getEntryStatus() {
		DataSourceKey[] keys = DataSourceKey.values();
		EntryStatus result = EntryStatus.MATCHED;
		for (int i = 0; i < keys.length - 1; i++) {
			if (getDelta(keys[i], keys[i + 1]) == null) {
				result = EntryStatus.MISSED;
				break;
			}
			if (getDelta(keys[i], keys[i + 1]) > 0) {
				result = EntryStatus.COLLISION;
			}
		}
		if (getDelta(keys[0], keys[keys.length - 1]) == null) {
			result = EntryStatus.MISSED;
		} else if (getDelta(keys[0], keys[keys.length - 1]) > 0) {
			result = EntryStatus.COLLISION;
		}
		return result;
	}
}
