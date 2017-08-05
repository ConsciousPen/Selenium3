/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.common.components;

import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.By;

import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;

public abstract class Efolder {

    public static Link linkOpenEfolder = new Link(By.id("leftContextHeader"));
    public static Link linkCloseEfolder = new Link(By.id("slide_panel_close_ctrl"));

    public static void isDocumentExist(String path, String documentName) {
        expandFolder(path);
        new StaticElement(By.xpath(String.format("//form[@id='efForm']//span[@class='rf-trn-lbl'][contains(.,'%s')]", documentName))).verify.present();
    }

    public static void isDocumentExist(String pathWithDocumentName) {
        expandFolder(FilenameUtils.getPath(pathWithDocumentName));
        new StaticElement(By.xpath(String.format("//form[@id='efForm']//span[@class='rf-trn-lbl'][contains(.,'%s')]", FilenameUtils.getName(pathWithDocumentName)))).verify.present();
    }

    public static void expandFolder(String path) {
        if (!isOpened()) {
            linkOpenEfolder.click();
        }

        String[] pathParts = path.split("/");
        for (String p : pathParts) {
            new Link(By.xpath(String.format("//form[@id='efForm']//div[span/span/span[contains(@id, 'efForm:efTree') and .='%s']]/span[1]", p))).click();
        }
    }

    public static boolean isOpened() {
        return linkCloseEfolder.isPresent() && linkCloseEfolder.isVisible();
    }

    public static boolean isFolderEmpty(String path) {
        expandFolder(path);
        return false;
    }
}
