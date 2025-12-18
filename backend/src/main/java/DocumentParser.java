import java.io.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

//Parse the uploaded document based on its type
public class DocumentParser {
    public static String parse(InputStream is, String name) {
        String fileName = name.toLowerCase();
        try {
            // .pdf, use PDFbox
            if (fileName.endsWith(".pdf")) {
                try (PDDocument doc = PDDocument.load(is)) {
                    return new PDFTextStripper().getText(doc);
                }
                // .docx, use POI
            } else if (fileName.endsWith(".docx")) {
                try (XWPFDocument doc = new XWPFDocument(is);
                        XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
                    return extractor.getText();
                }
            } else {
                return new String(is.readAllBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}