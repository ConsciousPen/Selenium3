package aaa.main.metadata;

import org.openqa.selenium.By;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class QuoteMetaData {
	public static final class InitiateQuote extends MetaData {
		public static final AssetDescriptor<ComboBox> BROAD_LINE_OF_BUSINESS = declare("Broad Line of Business", ComboBox.class);
		public static final AssetDescriptor<ComboBox> PRODUCT = declare("Product", ComboBox.class);
		public static final AssetDescriptor<Button> NEXT_BTN = declare("Next", Button.class, By.xpath("//input[@id='quoteForm:createQuoteButton']"));
		public static final AssetDescriptor<Button> CANCEL_BTN = declare("Cancel", Button.class, By.xpath("//input[@id='quoteForm:cancelButton']"));
	}
}
