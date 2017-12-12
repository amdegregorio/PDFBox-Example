package amdegregorio.pdfboxexample;

import java.io.File;
import java.util.Arrays;

public class PDFBoxExample {
	private static String PDF_OUTPUT_DIR = "/work/PDFBoxExample/";
	private static String PDF_FILENAME = "example.pdf";
	
	public static void main(String[] args) {
		PDFWriter pdfWriter = new PDFWriter(PDF_OUTPUT_DIR, PDF_FILENAME);
		pdfWriter.createPdfFile();
		String heading = "PDFBox Example";
		StringBuffer pageText = new StringBuffer();
		pageText.append("This is text outputted from the PDFBoxExample application.");
		pageText.append("There should be a header above this text and some images below it.");
		pageText.append("The font should be what was specified or Helvetica");
		pageText.append(System.getProperty("line.separator"));
		pageText.append(System.getProperty("line.separator"));
		pageText.append("This is another paragraph. This should be formatted like a second paragraph.");
		File imageDir = new File(PDF_OUTPUT_DIR);
		pdfWriter.addPage(heading, pageText, PDF_OUTPUT_DIR, Arrays.asList(imageDir.list(new ImageFileFilter())));
		pdfWriter.saveAndClose();
	}
	
}
