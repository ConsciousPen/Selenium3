package aaa.main.modules.policy.abstract_tabs;

import aaa.common.Tab;
import toolkit.exceptions.IstfException;
import toolkit.webdriver.ByT;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

public abstract class PropertyEndorsementsTab extends Tab {

	public Table tblIncludedEndorsements;
	public Table tblOptionalEndorsements;
	public Button btnSaveForm;
	public Button btnSaveEndo;
	public Button btnCancelForm;
	protected String TBL_SUB_ENDORSEMENTS_BY_FORMID_TEMPLATE;
	protected String LNK_ADD_SUB_ENDORSEMENT_BY_FORMID_TEMPLATE;

	protected PropertyEndorsementsTab(Class<? extends MetaData> mdClass) {
		super(mdClass);
	}

	public Link getViewLink(String formID) {
		return tblIncludedEndorsements.getRow("Form ID", formID).getCell(tblIncludedEndorsements.getColumnsCount()).controls.links.get("View", Waiters.AJAX);
	}

	public Table getInstancesTable(String formID) {
		return new Table(ByT.xpath(TBL_SUB_ENDORSEMENTS_BY_FORMID_TEMPLATE).format(formID));
	}

	public Link getAddEndorsementLink(String formID) {
		tblOptionalEndorsements.waitForAccessible(15000);
		if (tblOptionalEndorsements.getRow("Form ID", formID).isPresent()) {
			return tblOptionalEndorsements.getRow("Form ID", formID).getCell(tblOptionalEndorsements.getColumnsCount()).controls.links.get("Add", Waiters.AJAX);
		} else if (tblIncludedEndorsements.getRow("Form ID", formID).isPresent()) {
			Link view = getViewLink(formID);
			Link addSub = tblIncludedEndorsements.getRow("Form ID", formID).getCell(tblIncludedEndorsements.getColumnsCount()).controls.links.get("Add", Waiters.AJAX);
			if (view.isPresent()) {
				view.click();
			} else if (addSub.isPresent()) {
				return addSub;
			}
			return new Link(ByT.xpath(LNK_ADD_SUB_ENDORSEMENT_BY_FORMID_TEMPLATE).format(formID), Waiters.AJAX);
		} else {
			throw new IstfException("Can't get 'Add' link for endorsement " + formID + ". Endorsement not found.");
		}
	}

	public Link getEditEndorsementLink(String formID, int instanceNum) {
		Link edit = tblIncludedEndorsements.getRow("Form ID", formID).getCell(tblIncludedEndorsements.getColumnsCount()).controls.links.get("Edit", Waiters.AJAX);
		if (edit.isPresent()) {
			return edit;
		} else if (tblIncludedEndorsements.getRow("Form ID", formID).isPresent()) {
			Link view = getViewLink(formID);
			if (view.isPresent()) {
				view.click();
			}
			Table tblInstances = getInstancesTable(formID);
			if (tblInstances.getColumnsCount() == 0) {
				tblInstances.applyConfiguration("HeaderInRow");
			}
			if (tblInstances.getRowsCount() < instanceNum) {
				throw new IstfException(String.format("There is no instance number %s for Endorsement %s", instanceNum, formID));
			}
			return tblInstances.getRow(instanceNum).getCell(tblInstances.getColumnsCount()).controls.links.get("Edit", Waiters.AJAX);
		} else {
			throw new IstfException("Can't get 'Edit' link for endorsement " + formID + ". Endorsement not added.");
		}
	}

	public Link getRemoveEndorsementLink(String formID, int instanceNum) {
		Link remove = tblIncludedEndorsements.getRow("Form ID", formID).getCell(tblIncludedEndorsements.getColumnsCount()).controls.links.get("Remove", Waiters.AJAX);
		Link removeAll = tblIncludedEndorsements.getRow("Form ID", formID).getCell(tblIncludedEndorsements.getColumnsCount()).controls.links.get("Remove all", Waiters.AJAX);
		if (!removeAll.isPresent() && remove.isPresent()) {
			return remove;
		} else if (tblIncludedEndorsements.getRow("Form ID", formID).isPresent()) {
			Link view = getViewLink(formID);
			if (view.isPresent()) {
				view.click(Waiters.AJAX);
			}
			Table tblInstances = getInstancesTable(formID);
			if (tblInstances.getColumnsCount() == 0) {
				tblInstances.applyConfiguration("HeaderInRow");
			}
			if (tblInstances.getRowsCount() < instanceNum) {
				throw new IstfException(String.format("There is no instance number %s for Endorsement %s", instanceNum, formID));
			}
			return tblInstances.getRow(instanceNum).getCell(tblInstances.getColumnsCount()).controls.links.get("Remove", Waiters.AJAX);
		} else {
			throw new IstfException("Can't get 'Remove' link for endorsement " + formID + ". Endorsement not added.");
		}
	}

	public Link getLinkEdit(String formID) {
		return tblIncludedEndorsements.getRow("Form ID", formID).getCell(tblIncludedEndorsements.getColumnsCount()).controls.links.get("Edit");
	}

	public Link getLinkRemove(String formID) {
		return tblIncludedEndorsements.getRow("Form ID", formID).getCell(tblIncludedEndorsements.getColumnsCount()).controls.links.get("Remove");
	}

}
