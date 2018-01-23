package aaa.common.components;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.List;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.Select;
import aaa.toolkit.webdriver.WebDriverHelper;
import toolkit.exceptions.IstfException;
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
	private ByT pageNumberLocator;

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
		Integer pageNumber = WebDriverHelper.getInnerNumber(new ByChained(locator, By.xpath(".//*[contains(@class, 'ds-act') or contains(@class, 'ui-state-active')]")));
		return pageNumber == null ? 1 : pageNumber;
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

	public Integer getSelectedRowsPerPage() {
		if (hasSelectedRowsPerPage()) {
			return Integer.valueOf(pageOptionsSelector.getValue());
		}
		return null;
	}

	public boolean hasSelectedRowsPerPage() {
		return hasPageOptionsSelector()
				&& !new Select(pageOptionsSelector.getWebElement()).getAllSelectedOptions().isEmpty() // To prevent "NoSuchElementException" on ComboBox.getValue() when no option is selected
				&& NumberUtils.isCreatable(pageOptionsSelector.getValue());
	}

	public boolean setRowsPerPage(int value) {
		if (getSelectedRowsPerPage() != value) {
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
			int minRowsPerPage = getPageOptionsList().stream().filter(NumberUtils::isCreatable).mapToInt(Integer::valueOf).min().getAsInt();
			pageOptionsSelector.setValue(String.valueOf(minRowsPerPage));
			return true;
		}
		return false;
	}

	public boolean setMaxRowsPerPage() {
		if (hasPageOptionsSelector()) {
			int maxRowsPerPage = getPageOptionsList().stream().filter(NumberUtils::isCreatable).mapToInt(Integer::valueOf).max().getAsInt();
			pageOptionsSelector.setValue(String.valueOf(maxRowsPerPage));
			return true;
		}
		return false;
	}

	protected class Verify {
		Pagination page = Pagination.this;

		public void present() {
			present(true);
		}

		public void present(boolean expectedValue) {
			String assertionMessage = String.format("Pagination with \"%1$s\" locator is\"%2$s\" present.", page.getLocator(), expectedValue ? " not" : "");
			if (expectedValue) {
				assertThat(page.isPresent()).isTrue().as(assertionMessage);
			} else {
				assertThat(page.isPresent()).isFalse().as(assertionMessage);
			}
		}

		public void hasNextPage() {
			assertThat(page.hasNextPage()).isTrue().as("Pagination with \"%1$s\" locator does not have next page. Selected page is: %2$s.", page.getLocator(), page.getSelectedPage());
		}

		public void hasPreviousPage() {
			assertThat(page.hasPreviousPage()).isTrue().as("Pagination with \"%1$s\" locator does not have previous page. Selected page is: %2$s.", page.getLocator(), page.getSelectedPage());
		}

		public void hasPageOptionsSelector() {
			assertThat(page.hasPageOptionsSelector()).isTrue()
					.as("Pagination with \"%1$s\" locator does not have page options combobox with \"%2$s\" locator.", page.getLocator(), page.pageOptionsSelector.getLocator());
		}
	}
}
