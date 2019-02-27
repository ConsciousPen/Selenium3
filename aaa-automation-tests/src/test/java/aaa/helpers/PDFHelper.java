package aaa.helpers;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import toolkit.webdriver.BrowserController;
import java.io.IOException;
import java.net.URL;

/**
 * Handles converting PDFs to Strings. <br>
 * Works with HTTP PDFs and local PDF files.
 * @author Tyrone Jemison
 */
public class PDFHelper {

    /**
     * Given that the webdriver is focused on an HTTP page that ends with a .PDF extension... <br>
     * This method will convert that PDF into a String.
     * @return The text from the parsed PDF on the currently focused HTTP page.
     */
    public static String convertPDFToText_HTTP(){

        PDFParser parser = null;
        PDDocument pdDoc = null;
        COSDocument cosDoc = null;
        PDFTextStripper pdfStripper;
        try {
            URL url = new URL(BrowserController.get().driver().getCurrentUrl());
            RandomAccessBufferedFileInputStream inputStream = new RandomAccessBufferedFileInputStream(url.openStream());
            parser = new PDFParser(inputStream);
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            return pdfStripper.getText(pdDoc);
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR OCCURRED";
        }
    }

    /**
     * This method will convert a PDF file into a text string when provided a full file path.
     * @return The text extracted from the local PDF file.
     */
    public static String convertPDFToText_LocalFile(String in_fullFilePathWithName){
        PDFParser parser = null;
        PDDocument pdDoc = null;
        COSDocument cosDoc = null;
        PDFTextStripper pdfStripper;
        String fullFilePathWithName = in_fullFilePathWithName;

        try{
            parser = new PDFParser(new RandomAccessBufferedFileInputStream(fullFilePathWithName));
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            return pdfStripper.getText(pdDoc);

        }catch(IOException ex){
            ex.printStackTrace();
            try{
                if (cosDoc != null)
                    cosDoc.close();
                if (pdDoc != null)
                    pdDoc.close();
                return "ERROR OCCURRED";
            }catch(IOException ex2){
                ex2.printStackTrace();
                return "ERROR OCCURRED CLOSING DOC";
            }
        }
    }
}
