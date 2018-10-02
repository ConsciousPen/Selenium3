package aaa.modules.regression.sales.helper;

import java.util.List;
import com.google.common.collect.ImmutableList;

public class SectionFieldData {
	private static final Object SECTION_UIFIELD_SEPARATOR = ".";
	private final String sectionPath;

	private final String fieldName;

	/**
	 * Marks section
	 */
	private boolean section;

	private final List<Integer> treePosition;

	public SectionFieldData(String sectionPath, String field, List<Integer> fieldTreePosition) {
		this(sectionPath, field, fieldTreePosition, false);
	}

	public SectionFieldData(String sectionPath, String field, List<Integer> fieldTreePosition, boolean isSection) {
		this.sectionPath = sectionPath;
		this.treePosition = ImmutableList.copyOf(fieldTreePosition);
		this.fieldName = field;
		this.section = isSection;
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

	public boolean isSection() {
		return section;
	}

	public String getFullPath() {
		return section ? sectionPath : (sectionPath + SECTION_UIFIELD_SEPARATOR + fieldName);
	}
}