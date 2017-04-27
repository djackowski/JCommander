import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class TablePopUp extends JPopupMenu {
    private final File[] files;
    private final String destinationPath;
    private OnTableListChanged onTableListChanged;
    private static File[] filesToCopy;
    private String currentPath;
    private JMenuItem pasteItem;
    private static boolean filesCut;
    private final Component component;

    public interface OnTableListChanged {
        void onTableListChanged(String parentPath);
    }

    public TablePopUp(CommanderFrame component, File[] files, String currentPath, String destinationPath) {
        this.files = files;
        this.currentPath = currentPath;
        this.component = component;
        this.destinationPath = destinationPath;
        String copyMessage = Resources.getCurrentResources().getString("copy");
        JMenuItem copyItem = new JMenuItem(copyMessage);
        copyItem.addActionListener(new CopyActionListener());
        String deleteMessage = Resources.getCurrentResources().getString("delete");
        JMenuItem deleteItem = new JMenuItem(deleteMessage);
        deleteItem.addActionListener(new DeleteActionListener());
        String pasteMessage = Resources.getCurrentResources().getString("paste");
        pasteItem = new JMenuItem(pasteMessage);
        pasteItem.addActionListener(new PasteActionListener());
        add(pasteItem);
        if(filesToCopy == null) {
            pasteItem.setEnabled(false);
        }
        String cutMessage = Resources.getCurrentResources().getString("cut");
        JMenuItem cutItem = new JMenuItem(cutMessage);
        cutItem.addActionListener(new CutActionListener());
        if(files == null) {
            cutItem.setEnabled(false);
            deleteItem.setEnabled(false);
            copyItem.setEnabled(false);
        }
        add(cutItem);
        add(copyItem);
        add(deleteItem);
    }

    public void setOnTableListChanged(OnTableListChanged onTableListChanged) {
        this.onTableListChanged = onTableListChanged;
    }


    private class DeleteActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            onDeleteActionPerformed();

        }

        private void onDeleteActionPerformed() {
            String delete = FileList.delete(files, true);
            onTableListChanged.onTableListChanged(delete);
        }
    }

    private class CopyActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            filesToCopy = files;
            pasteItem.setEnabled(true);
        }
    }

    private class PasteActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (filesToCopy == null) return;
            if (filesCut) {
                new CutFileWorker().execute();
            } else {
                new PasteFileWorker().execute();
            }
        }

        private class PasteFileWorker extends SwingWorker<Object, Object> {

            @Override
            protected Object doInBackground() throws Exception {
                int dialogButton = JOptionPane.YES_NO_OPTION;
                String confirmMessage = Resources.getCurrentResources().getString("pasteConfirm");
                int dialogResult = JOptionPane.showConfirmDialog(null,
                        confirmMessage, "JCommander", dialogButton);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    List<File> files = Arrays.asList(filesToCopy);
                    for (File f : files) {
                        int size = files.size();
                        int currentIndex = files.indexOf(f);
                        int remainingFiles = size - currentIndex + 1;
                        publish(remainingFiles);
                        File file = new File(destinationPath.concat("\\" + f.getName()));
                        FileList.copyFolder(f, file);
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                super.done();
                onTableListChanged.onTableListChanged(currentPath);
                pasteItem.setEnabled(false);
                filesToCopy = null;
            }
        }
        private class CutFileWorker extends SwingWorker<Integer, Integer> {

            @Override
            protected Integer doInBackground() throws Exception {
                List<File> files = Arrays.asList(filesToCopy);
                for (File f : files) {
                    int size = files.size();
                    int currentIndex = files.indexOf(f);
                    int remainingFiles = size - currentIndex + 1;
                    publish(remainingFiles);
                    File file = new File(destinationPath.concat("\\" + f.getName()));
                    FileList.copyFolder(f, file);
                }
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                super.process(chunks);
                System.out.println(chunks.size() - 1);
            }

            @Override
            protected void done() {
                super.done();
                onTableListChanged.onTableListChanged(currentPath);
                pasteItem.setEnabled(false);
                FileList.delete(filesToCopy, false);
                filesCut = false;
                filesToCopy = null;
            }
        }

    }

    private class CutActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            filesToCopy = files;
            filesCut = true;
            pasteItem.setEnabled(true);
        }
    }
}
