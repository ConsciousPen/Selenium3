package aaa.toolkit.webdriver.customcontrols;

import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;

import toolkit.webdriver.ByT;
import toolkit.webdriver.controls.AbstractEditableStringElement;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.HighlightableElement;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.waiters.Waiter;
import toolkit.webdriver.controls.waiters.Waiters;

public class CalendarBox extends AbstractEditableStringElement implements HighlightableElement {
	private Button btnHeader = new Button(new ByChained(this.getLocator(), By.xpath("//td[contains(@id,'Header')]//div[contains(@onclick,'showDateEditor')]")));
	private Button btnScrollYearMinus = new Button(By.xpath("//table[contains(@id,'DateEditorLayout')]//div[.='<']"));
	private Button btnScrollYearPlus = new Button(By.xpath("//table[contains(@id,'DateEditorLayout')]//div[.='>']"));
	private StaticElement btnYear0 = new StaticElement(By.xpath("//div[contains(@id,'DateEditorLayoutY0')]"));
	private StaticElement btnYear9 = new StaticElement(By.xpath("//div[contains(@id,'DateEditorLayoutY9')]"));
	private Button btnOK = new Button(By.xpath("//div[contains(@id,'DateEditorButtonOk')]"));
	private ByT yearTemplate = ByT.xpath("//div[contains(@id,'DateEditorLayoutY') and .=%s]");
	private ByT monthTemplate = ByT.xpath("//div[contains(@id,'DateEditorLayoutM%s')]");
	private ByT dayTemplate = ByT.xpath("//table[contains(@id,'Content')]//td[contains(.,'%s') and contains(@class,'btn')]");
	
	public CalendarBox(By locator) {
		super(locator, Waiters.DEFAULT);
	}

	public CalendarBox(By locator, Waiter waiter) {
		super(locator, waiter);
	}

	public CalendarBox(BaseElement<?, ?> parent, By locator) {
		super(parent, locator, Waiters.DEFAULT);
	}

	public CalendarBox(BaseElement<?, ?> parent, By locator, Waiter waiter) {
		super(parent, locator, waiter);
	}

	@Override
	protected void setRawValue(String value) {
		String[] values = value.split("/");
		int year = Integer.parseInt(values[2]);
		int month = Integer.parseInt(values[0]);
		int day = Integer.parseInt(values[1]);
		this.click();
		btnHeader.waitForAccessible(2000);
		btnHeader.click();
		setYear(year);
		setMonth(month);
		setDay(day);
	}

	private void setYear(int year) {
		int year0 = Integer.parseInt(btnYear0.getValue());
		int year9 = Integer.parseInt(btnYear9.getValue());
		if (year < year0) {
			btnScrollYearMinus.click();
			setYear(year);
		} else if (year > year9) {
			btnScrollYearPlus.click();
			setYear(year);
		} else {
			new Button(yearTemplate.format(year)).click();
		}
	}
	
	private void setMonth(int month) {
		new Button(monthTemplate.format(month-1)).click();
		btnOK.click();
	}
	
	private void setDay(int day) {
		new Button(dayTemplate.format(day)).click();
	}

	@Override
	protected String getRawValue() {
		return getAttribute("value").trim();
	}
}
