import com.sun.jna.platform.FileUtils;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

public class FileList {

    public File[] getFiles(String root) {
        File file = new File(root);
        return file.listFiles();
    }

    File[] getRoots() {
        return File.listRoots();
    }

    public String getSize(File file) {
        String length;
        if (file.isFile()) {
            length = String.valueOf(file.length());
        } else {
            length = "<DIR>";
        }
        return length;
    }

    public String getCreationDate(File file) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        return String.valueOf(attr.creationTime());
    }


    public static String delete(File[] files, boolean showDialog) {
        if(showDialog) {
            int dialogButton = JOptionPane.YES_NO_OPTION;
            String confirmMessage = Resources.getCurrentResources().getString("deleteConfirm");
            int dialogResult = JOptionPane.showConfirmDialog(null,
                    confirmMessage, "JCommander", dialogButton);
            if (dialogResult == JOptionPane.YES_OPTION) {
                deleteFiles(files);
            }
        } else {
            deleteFiles(files);
        }
        return files[0].getParent();
    }

    private static void deleteFiles(File[] files) {
        FileUtils fileUtils = FileUtils.getInstance();
        if (fileUtils.hasTrash()) {
            try {
                //TODO: add progress bar
                fileUtils.moveToTrash(files);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            for (File file : Arrays.asList(files)) {
                file.delete();
            }
        }
    }

    public static void copyFolder(File src, File dest)
            throws IOException {

        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
                System.out.println("Directory copied from "
                        + src + "  to " + dest);
            }

            String files[] = src.list();

            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                copyFolder(srcFile, destFile);
            }

        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
            System.out.println("File copied from " + src + " to " + dest);
        }
    }


}
