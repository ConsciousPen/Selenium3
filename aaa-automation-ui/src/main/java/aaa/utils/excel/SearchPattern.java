package aaa.utils.excel;

public class SearchPattern {
	private String searchPattern;
	private boolean ignoreCase;

	public SearchPattern(String searchPattern) {
		this(searchPattern, true);
	}

	public SearchPattern(String searchPattern, boolean ignoreCase) {
		this.searchPattern = searchPattern;
		this.ignoreCase = ignoreCase;
	}

	public String getSearchPattern() {
		return searchPattern;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	@Override
	public String toString() {
		return "SearchPattern{" +
				"searchPattern='" + searchPattern + '\'' +
				", ignoreCase=" + ignoreCase +
				'}';
	}

	public boolean matches(String value) {
		String inputValue = value;
		String pattern = searchPattern;
		if (ignoreCase) {
			inputValue = inputValue.toLowerCase();
			pattern = pattern.toLowerCase();
		}
		return inputValue.contains(pattern);
	}
}
