package aaa.common.metadata;

import org.openqa.selenium.By;

import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.ListBox;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;

public final class LoginPageMeta extends MetaData{
	public static final AttributeDescriptor USER = declare("User", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='username']"));
	public static final AttributeDescriptor GROUPS = declare("Groups", ListBox.class, Waiters.AJAX, false, By.xpath("//select[@id='groups']"));
	public static final AttributeDescriptor STATES = declare("States", ListBox.class, Waiters.AJAX, false, By.xpath("//select[@id='states']"));
	public static final AttributeDescriptor UW_AUTH_LEVEL = declare("UW_AuthLevel", ComboBox.class, Waiters.AJAX, false, By.xpath("//select[@id='uw_level']"));
	public static final AttributeDescriptor BILLING_AUTH_LEVEL = declare("Billing_AuthLevel", ComboBox.class, Waiters.AJAX, false, By.xpath("//select[@id='billing_level']"));
	public static final AttributeDescriptor PASSWORD = declare("Password", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='password']"));
}
