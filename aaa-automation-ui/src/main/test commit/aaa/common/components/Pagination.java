package aaa.common.components;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;
import toolkit.exceptions.IstfException;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.ByT;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Pagination component which allows navigate through pages on UI
 */
public class Pagination {
	public final Verify verify = this.new Verify();
	private By locator;
	private Link firstPage;
	private Link lastPage;
	private Link previousPage;
	private Link nextPage;
	private ComboBox pageOptionsSelector;
	//private ByT pageNumberLocator;

	public Pagination(By locator) {
		this.locator = locator;
		this.firstPage = new Link(new ByChained(locator, By.xpath(".//*[contains(@class, 'btn-first') or contains(@class, 'ui-paginator-first')]")), Waiters.AJAX);
		this.lastPage = new Link(new ByChained(locator, By.xpath(".//*[contains(@class, 'btn-last') or contains(@class, 'ui-paginator-last')]")), Waiters.AJAX);
		this.previousPage = new Link(new ByChained(locator, By.xpath(".//*[contains(@class, 'btn-prev') or contains(@class, 'ui-paginator-prev')]")), Waiters.AJAX);
		this.nextPage = new Link(new ByChained(locator, By.xpath(".//*[contains(@class, 'btn-next') or contains(@class, 'ui-paginator-next')]")), Waiters.AJAX);
		this.pageOptionsSelector = new ComboBox(new ByChained(locator, By.xpath(".//*[contains(@class, 'rpp-options')]")), Waiters.AJAX);
		this.pageNumberLocator = ByT.xpath(".//*[text()=%d]");
	}

	public By getLocator() {
		return this.locator;
	}

	public int getPagesCount() {
		int pagesCount = BrowserController.get().driver().findElements(new ByChained(locator, By.xpath(".//*[contains(@class, 'nmb-btn') or contains(@class, 'paginator-page ')]"))).size();
		if (0 == pagesCount) {
			pagesCount++;
		}
		return pagesCount;
	}

	public int getSelectedPage() {
		int pageNumber;
		try {
			WebElement selectedPage = BrowserController.get().driver().findElement(new ByChained(locator, By.xpath(".//*[contains(@class, 'ds-act') or contains(@class, 'ui-state-active')]")));
			pageNumber = Integer.parseInt(selectedPage.getAttribute("innerText").trim());
		} catch (NoSuchElementException ignored) {
			pageNumber = 1;
		}

		return pageNumber;
	}

	public boolean isPresent() {
		return new StaticElement(this.locator).isPresent();
	}

	public boolean isVisible() {
		return new StaticElement(this.locator).isVisible();
	}

	public List<String> getPageOptionsList() {
		return hasPageOptionsSelector() ? this.pageOptionsSelector.getAllValues() : null;
	}

	public int getSelectedRowsPerPage() {
		return hasPageOptionsSelector() ? Integer.valueOf(pageOptionsSelector.getValue()) : null;
	}

	public boolean setRowsPerPage(int value) {
		if (hasPageOptionsSelector() && getSelectedRowsPerPage() != value) {
			pageOptionsSelector.setValue(String.valueOf(value));
			return true;
		}
		return false;
	}

	public boolean hasPageOptionsSelector() {
		return this.pageOptionsSelector.isPresent() && this.pageOptionsSelector.isVisible();
	}

	public boolean hasNextPage() {
		By navigationPage = pageNumberLocator.format(getSelectedPage() + 1);
		return new Link(new ByChained(locator, navigationPage)).isPresent();
	}

	public boolean hasPreviousPage() {
		By navigationPage = pageNumberLocator.format(getSelectedPage() - 1);
		return new Link(new ByChained(locator, navigationPage)).isPresent();
	}

	public boolean goToPage(int pageNumber) {
		if (getSelectedPage() != pageNumber && pageExists(pageNumber)) {
			try {
				By navigationPage = pageNumberLocator.format(pageNumber);
				new Link(new ByChained(locator, navigationPage)).click();
				return true;
			} catch (IstfException e) {
				throw new IstfException("Can't navigate to page \"%s\". It does not exist!\n", e);
			}
		}
		return false;
	}

	public boolean pageExists(int pageNumber) {
		return getPagesCount() >= pageNumber;
	}

	public boolean goToNextPage() {
		if (this.nextPage.isPresent() && hasNextPage()) {
			this.nextPage.click();
			return true;
		}
		return false;
	}

	public boolean goToPreviousPage() {
		if (this.previousPage.isPresent() && hasPreviousPage()) {
			this.previousPage.click();
			return true;
		}
		return false;
	}

	public boolean goToLastPage() {
		if (this.lastPage.isPresent() && this.getSelectedPage() != getPagesCount()) {
			this.lastPage.click();
			return true;
		}
		return false;
	}

	public boolean goToFirstPage() {
		if (this.firstPage.isPresent() && this.getSelectedPage() != 1) {
			this.firstPage.click();
			return true;
		}
		return false;
	}

	public boolean setMinRowsPerPage() {
		if (hasPageOptionsSelector()) {
			pageOptionsSelector.setValueByIndex(0);
			return true;
		}
		return false;
	}

	public boolean setMaxRowsPerPage() {
		if (hasPageOptionsSelector()) {
			pageOptionsSelector.setValueByIndex(getPageOptionsList().size() - 1);
			return true;
		}
		return false;
	}

	protected class Verify {
		Pagination p = Pagination.this;

		public void present() {
			present(true);
		}

		public void present(boolean expectedValue) {
			String assertionMessage = String.format("Pagination with \"%1$s\" locator is\"%2$s\" present.", p.getLocator(), expectedValue ? " not" : "");
			if (expectedValue) {
				CustomAssert.assertTrue(assertionMessage, p.isPresent());
			} else {
				CustomAssert.assertFalse(assertionMessage, p.isPresent());
			}
		}

		public void hasNextPage() {
			CustomAssert.assertTrue(String.format("Pagination with \"%1$s\" locator does not have next page. Selected page is: %2$s.", p.getLocator(), p.getSelectedPage()), p.hasNextPage());
		}

		public void hasPreviousPage() {
			CustomAssert.assertTrue(String.format("Pagination with \"%1$s\" locator does not have previous page. Selected page is: %2$s.", p.getLocator(), p.getSelectedPage()), p.hasPreviousPage());
		}

		public void hasPageOptionsSelector() {
			CustomAssert.assertTrue(String.format("Pagination with \"%1$s\" locator does not have page options combobox with \"%2$s\" locator.", p.getLocator(), p.pageOptionsSelector), p.hasPageOptionsSelector());
		}
	}
}
