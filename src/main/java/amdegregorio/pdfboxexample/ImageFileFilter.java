package amdegregorio.pdfboxexample;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class ImageFileFilter implements FilenameFilter {

   private List<String> imageExtensions = new ArrayList<String>();

   public ImageFileFilter() {
      imageExtensions.add("JPG");
      imageExtensions.add("JPEG");
      imageExtensions.add("PNG");
   }

   @Override
   public boolean accept(File dir, String name) {
      boolean accept = false;
      int extensionIndex = name.lastIndexOf(".");
      String extension = name.substring((extensionIndex+1));
      accept = imageExtensions.contains(extension.toUpperCase());
      return accept;
   }

}
