/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.common.efolder;

import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;
import aaa.common.efolder.defaulttabs.AddDocumentTab;
import aaa.common.enums.EfolderConstants;
import toolkit.datax.TestData;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.waiters.Waiters;

public abstract class Efolder {

    public static Link linkOpenEfolder = new Link(By.id("leftContextHeader"));
    public static Link linkCloseEfolder = new Link(By.id("slide_panel_close_ctrl"));

    public static void isDocumentExist(String path, String documentName) {
        if (!isTreeExpanded(path)) {
            expandFolder(path);
        }
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
            Waiters.SLEEP(3000).go();
            new Link(By.xpath(String.format("//form[@id='efForm']//div[span/span/span[contains(@id, 'efForm:efTree') and .='%s']]/span[1]", p))).click();
        }
    }

    public static boolean isOpened() {
        return linkCloseEfolder.isPresent() && linkCloseEfolder.isVisible();
    }

    public static void addDocument(TestData testData, String path){
        executeContextMenu(path, EfolderConstants.DocumentOparetions.ADD_DOCUMENT);
        new AddDocumentTab().fillTab(testData).submitTab();
    }

    private static void executeContextMenu(String path, String operation){
        if (!isTreeExpanded(path)) {
            expandFolder(path);
        }
        String[] pathParts = path.split("/");
        new Actions(BrowserController.get().driver()).contextClick(getLabel(pathParts[pathParts.length-1]).getWebElement()).perform();
        BrowserController.get().executeScript("$('.rf-tt.ef-tree-node-tooltip').hide();");
        new Link(By.xpath(String.format("//div[@id='jqContextMenu']//li[.='%s']", operation))).click();
    }

    public static StaticElement getLabel(String label){
        if (!isOpened()) {
            linkOpenEfolder.click();
        }
        return new StaticElement(By.xpath(String.format("//div[@id='efForm:efTree']//span[@class='rf-trn-lbl']/span[.='%s']", label)));
    }

    //TODO: functionality is not full, only one layer is checked
    private static boolean isTreeExpanded(String path){
        String[] pathParts = path.split("/");
        StaticElement lastFolder = new StaticElement(By.xpath(String.format("//form[@id='efForm']//div[span/span/span[contains(@id, 'efForm:efTree') and .='%s']]/span[1]",
                pathParts[pathParts.length-1])));
        StaticElement expandButton = new StaticElement(By.xpath(String.format("//form[@id='efForm']//div[span/span/span[contains(@id, 'efForm:efTree') and .='%s']]/span[@class='rf-trn-hnd-colps rf-trn-hnd']",
                pathParts[pathParts.length-1])));
        return lastFolder.isPresent() && !expandButton.isPresent();
    }
}
