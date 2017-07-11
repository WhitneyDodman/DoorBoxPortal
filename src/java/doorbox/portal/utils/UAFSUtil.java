/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package doorbox.portal.utils;
 
import java.io.File; 
import java.io.IOException;
 
/**
 * Universal Something File Storage Utility.
 * @author torinw
 */
public class UAFSUtil {
    private static int KEY_DIVISOR = 1;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            System.out.println("Get storage path: " + getStoragePath("10000001"));
            System.out.println("Get full storage path: " + createRandomFile("c:/www", "gallery", "tirub123", "media", ".png").getAbsolutePath());
            System.out.println("Get full storage path: " + createRandomFile("c:/www", "gallery", "tirub123", "media", ".png").getAbsolutePath());
            System.out.println("Get full storage path: " + createRandomFile("c:/www", "gallery", "tirub123", "media", ".png").getAbsolutePath());
            System.out.println("Get full storage path: " + createFile("c:/www", "gallery", "tirub123", "foo.png").getAbsolutePath());
            String filename = "filename.ext";
            System.out.println("filename ext: " + filename.substring(filename.indexOf(".") + 1));
        } catch (Exception e) {
            System.out.println("e: " + e.getMessage());
        }
    }
        
    /**
     * Creates a File object given a unique pathway key and relative filename
     */
    public static String getStoragePath(String pathwayKey) {
        StringBuffer path = new StringBuffer();
        int length = pathwayKey.length();
        for (int i = 0; i < length; i += KEY_DIVISOR) {
            if (i != 0) path.append("/");
            if (i + KEY_DIVISOR < length) {
                path.append(pathwayKey.subSequence(i, i + KEY_DIVISOR));
            } else {
                path.append(pathwayKey.subSequence(i, i + length - i));
            }
        }
        return path.toString();
    }    
    
    /**
     * Creates a File object given a unique pathway key and relative filename
     * Given uafsRot, a pathwayKey, relativeDirectory, and extension
     */
    public static File createRandomFile(String uafsRoot, String relativeDirectory, String pathwayKey, String prefix, String extension) throws IOException {
        // Create a fully-qualified path including ufasRoot + relativeDirectory + pathwayKey + tmpFilename + extension
        // e.g. "/www/cityoftwohearts.com/gallery/t/o/r/i/n/img12345.png"

        StringBuffer path = new StringBuffer(uafsRoot);
        if (relativeDirectory != null && relativeDirectory.length() != 0) {
            if (!relativeDirectory.startsWith(File.separator)) {
                path.append(File.separator);
            }
            path.append(relativeDirectory);
        }

        path.append(File.separator).append(getStoragePath(pathwayKey));

        File directory = new File(path.toString());
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Could not create requested path");
            }
        }
        if (!extension.startsWith(".")) {
            extension = ".".concat(extension);
        }
        return File.createTempFile(prefix, extension, directory);
    }    

    /**
     * Creates a File object given a unique pathway key and relative filename
     * Given uafsRot, a pathwayKey, relativeDirectory, and extension
     */
    public static File createFile(String uafsRoot, String relativeDirectory, String pathwayKey, String filename) throws IOException {
        StringBuffer path = new StringBuffer(uafsRoot);
        if (relativeDirectory != null && relativeDirectory.length() != 0) {
            if (!relativeDirectory.startsWith(File.separator)) {
                path.append(File.separator);
            }
            path.append(relativeDirectory);
        }

        path.append(File.separator).append(getStoragePath(pathwayKey));
        
        File directory = new File(path.toString());
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Could not create requested path");
            }
        }
        return new File(directory, filename);
    }
}
