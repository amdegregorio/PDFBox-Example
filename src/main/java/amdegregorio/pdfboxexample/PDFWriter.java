package amdegregorio.pdfboxexample;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class PDFWriter {
   private String pdfOutputDirectory = "";
   private String pdfFileName = "";
   private PDDocument doc = null;
   private PDFont font = null;

   public PDFWriter(String pdfOutputDirectory, String pdfFileName) {
      this.pdfOutputDirectory = pdfOutputDirectory;
      if (!this.pdfOutputDirectory.endsWith("/"))
         this.pdfOutputDirectory += "/";
      if (!pdfFileName.endsWith(".pdf")) {
         pdfFileName = pdfFileName + ".pdf";
      }
      this.pdfFileName = pdfFileName;
   }

   public void createPdfFile() {
      doc = new PDDocument();
      try {
         font = PDType0Font.load(doc, new File("/Windows/Fonts" +"/ARIALUNI.TTF"));
      } catch (IOException e) {
         font = PDType1Font.HELVETICA;
         e.printStackTrace();
      }
   }
   
   public boolean addPage(String pageHeader, StringBuffer pageText, String imageDirectory, List<String> imageFileNames) {
      boolean ok = false;
      //Create and add the page to the document
      PDPage page = new PDPage();
      doc.addPage(page);
      PDPageContentStream contents = null;
      
      float fontSize = 12;
      float leading = 1.5f*fontSize;
      PDRectangle mediabox = page.getMediaBox();
      float margin = 75;
      float width = mediabox.getWidth() - 2*margin;
      float startX = mediabox.getLowerLeftX() + margin;
      float startY = mediabox.getUpperRightY() - margin;
      float yOffset = startY;
      
      try {
         contents = new PDPageContentStream(doc, page);
         contents.beginText();
         contents.setFont(font, 14);
         contents.newLineAtOffset(startX, startY);
         yOffset-=leading;
         contents.showText(pageHeader);
         contents.newLineAtOffset(0, -leading);
         yOffset-=leading;
         
         List<String> lines = new ArrayList<>();
         parseIndividualLines(pageText, lines, fontSize, font, width);

         contents.setFont(font, fontSize);
         for (String line:lines) { 
            contents.showText(line);
            contents.newLineAtOffset(0, -leading);
            yOffset-=leading;

            if (yOffset <= 0) {
               contents.endText();
               try {
                  if (contents != null) contents.close();
               } catch (IOException e) {
                  ok = false;
                  e.printStackTrace();
               }
               page = new PDPage();
               doc.addPage(page);
               contents = new PDPageContentStream(doc, page);
               contents.beginText();
               contents.setFont(font, fontSize);
               yOffset = startY;
               contents.newLineAtOffset(startX, startY);
            }
         }
         contents.endText();
         
         float scale = 1f;
         for (String attachmentName : imageFileNames) {
            PDImageXObject pdImage = PDImageXObject.createFromFile(imageDirectory + attachmentName, doc);
            scale = width/pdImage.getWidth();
            yOffset-=(pdImage.getHeight()*scale);
            if (yOffset <= 0) {
               System.out.println("Starting a new page");
               try {
                  if (contents != null) contents.close();
               } catch (IOException e) {
                  ok = false;
                  e.printStackTrace();
               }
               page = new PDPage();
               doc.addPage(page);
               contents = new PDPageContentStream(doc, page);
               yOffset = startY-(pdImage.getHeight()*scale);
            }
            System.out.println("yOffset: " + yOffset);
            System.out.println("page width: " + width + "  imageWidth: " + pdImage.getWidth() + " imageHeight: " + (pdImage.getHeight()*scale) + " scale: " + scale);
            contents.drawImage(pdImage, startX, yOffset, width, pdImage.getHeight()*scale);
         }
         ok = true;
      } catch (IOException e) {
         e.printStackTrace();
         ok = false;
      } finally {
         try {
            if (contents != null) contents.close();
         } catch (IOException e) {
            ok = false;
            e.printStackTrace();
         }
      }
      
      return ok;
   }

   public void saveAndClose() {
      try {
         doc.save(pdfOutputDirectory + pdfFileName);
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         try {
            doc.close();
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }
   
   private void parseIndividualLines(StringBuffer wholeLetter, List<String> lines, float fontSize, PDFont pdfFont, float width) throws IOException {
      String[] paragraphs = wholeLetter.toString().split(System.getProperty("line.separator"));
      for (int i = 0; i < paragraphs.length; i++) {
         int lastSpace = -1;
         lines.add(" ");
         while (paragraphs[i].length() > 0) {
            int spaceIndex = paragraphs[i].indexOf(' ', lastSpace + 1);
            if (spaceIndex < 0) {
                spaceIndex = paragraphs[i].length();
            }
            String subString = paragraphs[i].substring(0, spaceIndex);
            float size = fontSize * pdfFont.getStringWidth(subString) / 1000;
            //System.out.printf("'%s' - %f of %f\n", subString, size, width);
            if (size > width) {
               if (lastSpace < 0) {
                   lastSpace = spaceIndex;
               }
               subString = paragraphs[i].substring(0, lastSpace);
               lines.add(subString);
               paragraphs[i] = paragraphs[i].substring(lastSpace).trim();
               //System.out.printf("'%s' is line\n", subString);
               lastSpace = -1;
            } else if (spaceIndex == paragraphs[i].length()) {
               lines.add(paragraphs[i]);
               //System.out.printf("'%s' is line\n", paragraphs[i]);
               paragraphs[i] = "";
            } else {
               lastSpace = spaceIndex;
            }
         }
      }
   }
}
