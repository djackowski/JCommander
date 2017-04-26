import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class TablePopUp extends JPopupMenu {
    private final File[] files;
    private OnTableListChanged onTableListChanged;
    private static File[] filesToCopy;
    private String currentPath;
    private JMenuItem pasteItem;
    private static boolean filesCut;

    public interface OnTableListChanged {
        void onTableListChanged(String parentPath);
    }

    public TablePopUp(File[] files, String currentPath) {
        this.files = files;
        this.currentPath = currentPath;
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
        String cutMessage = Resources.getCurrentResources().getString("cut");
        JMenuItem cutItem = new JMenuItem(cutMessage);
        cutItem.addActionListener(new CutActionListener());

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
        }


    }

    private class PasteActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (filesToCopy == null) return;
            if (filesCut) {
                pasteFiles();
                FileList.delete(filesToCopy, false);
                filesCut = false;
            } else {
                pasteFiles();
            }
        }

        private void pasteFiles() {
            // new CopyFileWorker();
        }

        private class CopyFileWorker extends SwingWorker<Object, Object> {

            @Override
            protected Object doInBackground() throws Exception {
                for (File f : Arrays.asList(filesToCopy)) {
                    File file = new File(currentPath.concat("\\" + f.getName()));
                    FileList.copyFolder(f, file);
                    publish();
                }
                return null;
            }

            @Override
            protected void process(List<Object> chunks) {
                super.process(chunks);
            }

            @Override
            protected void done() {
                super.done();
                onTableListChanged.onTableListChanged(currentPath);
                pasteItem.setEnabled(false);
                filesToCopy = null;
            }
        }

    }

    private class CutActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            filesToCopy = files;
            filesCut = true;
        }
    }
}
