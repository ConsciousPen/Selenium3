package aaa.helpers.mock.model;

public interface UpdatableMock extends Cloneable {
	/**
	 * returns filename with extension of excel stub file
	 *
	 * @return filename
	 */
	String getFileName();

	/**
	 * adds entries from other mock to this UpdatableMock object
	 *
	 * @param mock from which entries should be added to this UpdatableMock object
	 *
	 * @return common entries from this and {@code mock} object
	 */
	boolean add(UpdatableMock mock);

	/**
	 * clones this UpdatableMock object
	 *
	 * @return cloned copy of this object
	 */
	UpdatableMock clone();
}
