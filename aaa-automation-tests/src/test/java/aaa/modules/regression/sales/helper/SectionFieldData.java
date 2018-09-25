package aaa.modules.regression.sales.helper;

import java.util.List;
import com.google.common.collect.ImmutableList;

public class SectionFieldData {
	private static final Object SECTION_UIFIELD_SEPARATOR = ".";
	private final String sectionPath;

	private final String fieldName;

	private final List<Integer> treePosition;
	public SectionFieldData(String sectionPath, String field, List<Integer> fieldTreePosition) {
		this.sectionPath = sectionPath;
		this.treePosition = ImmutableList.copyOf(fieldTreePosition);
		this.fieldName = field;
	}

	public String getFieldName() {
		return fieldName;
	}
	public String getSectionPath() {
		return sectionPath;
	}

	public List<Integer> getTreePosition() {
		return treePosition;
	}

	public String getFullPath() {
		return sectionPath + SECTION_UIFIELD_SEPARATOR + fieldName;
	}
}