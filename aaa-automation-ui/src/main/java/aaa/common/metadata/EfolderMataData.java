/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common.metadata;

import org.openqa.selenium.By;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.FileUpload;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class EfolderMataData {
	public static final class AddFileTab extends MetaData{
		public static final AssetDescriptor<FileUpload> FILE_UPLOAD = declare("File", FileUpload.class, By.id("addDocumentForm:documentUpload_input"));
		public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> FOLDER = declare("Folder", ComboBox.class);
		public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
	}

	public static class AddExtFileTab extends MetaData {
		public static final AssetDescriptor<TextBox> FILE_URL = declare("File URL", TextBox.class);
		public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class);
	}

	public static class RenameFileTab extends MetaData {
		public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class);
	}

	public static class ReidexFileTab extends MetaData {
		public static final AssetDescriptor<ComboBox> FOLDER = declare("Folder", ComboBox.class);
	}
}