import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FilesTable extends JTable{
    private String parentPath;
    private String currentPath;
    private final FilesModel filesModel;
    private final FileList fileList;
    private List<File> files;
    private String rootPath;
    private final JTableMouseAdapter jTableMouseAdapter;
    private File[] filesArray;
    private CommanderFrame commanderFrame;
    private String destinationPath;


    public File[] getFilesArray() {
        return filesArray;
    }

    public FilesTable(CommanderFrame commanderFrame, String rootPath) {
        this.rootPath = rootPath;
        this.commanderFrame = commanderFrame;
        this.currentPath = rootPath;
        destinationPath = currentPath;
        fileList = new FileList();
        files = Arrays.asList(fileList.getFiles(rootPath));
        filesModel = new FilesModel(files);
        setModel(filesModel);
        setAutoCreateRowSorter(true);
        getColumnModel().setSelectionModel(new TableListSelectionModel());
        getSelectionModel().addListSelectionListener(new RowColumnListSelectionListener());
        setShowGrid(false);
        getTableHeader().setReorderingAllowed(false);
        jTableMouseAdapter = new JTableMouseAdapter();
        addMouseListener(jTableMouseAdapter);
        setDragEnabled(true);

    }

    public CommanderFrame getCommanderFrame() {
        return commanderFrame;
    }

    public JTableMouseAdapter getTableMouseAdapter() {
        return jTableMouseAdapter;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    @Override
    public void clearSelection() {
        super.clearSelection();
        destinationPath = currentPath;
    }

    public void changeLocal() {
        getColumnModel().getColumn(0).setHeaderValue(Resources.getCurrentResources().getString("name"));
        getColumnModel().getColumn(1).setHeaderValue(Resources.getCurrentResources().getString("size"));
        getColumnModel().getColumn(2).setHeaderValue(Resources.getCurrentResources().getString("date"));
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public class JTableMouseAdapter extends MouseAdapter implements TablePopUp.OnTableListChanged {
        @Override
        public void mousePressed(MouseEvent e) {
            onMousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            onMouseReleased(e);
        }

        @Override
        public void onTableListChanged(String parentPath) {
            files = Arrays.asList(fileList.getFiles(parentPath));
            filesModel.setTableData(files);
        }

        private void onMouseReleased(MouseEvent e) {
            int rowIndex = getSelectedRow();
            if (rowIndex < 0)
                return;

            if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                int[] rows = getSelectedRows();
                if (rows.length == 0) return;
                System.out.println("Selected Rows: " + Arrays.toString(rows));

                File[] files = filesModel.getFileList(rows);
                TablePopUp popup = new TablePopUp(commanderFrame, files, currentPath, destinationPath);
                popup.setOnTableListChanged(this);
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        private void onMousePressed(MouseEvent me) {
            Point p = me.getPoint();
            int selectedRow = rowAtPoint(p);
            File f = filesModel.getFile(selectedRow);
            if(!f.isFile()) {
                destinationPath = f.getPath();
            }
            //currentPath = f.getPath();
            if (SwingUtilities.isRightMouseButton(me)) {
                if (!getSelectionModel().isSelectedIndex(selectedRow)) {
                    getSelectionModel().addSelectionInterval(selectedRow, selectedRow);
                }

            } else {
                if (me.getClickCount() == 2) {
                    if (selectedRow < 0) return;

                    File clickedFile = filesModel.getFile(selectedRow);
                    if (clickedFile.isFile()) {
                        try {
                            Desktop.getDesktop().open(clickedFile);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        List<File> file = filesModel.getFileList(selectedRow);
                        parentPath = clickedFile.getParent();
                        currentPath = clickedFile.getPath();
                        destinationPath = currentPath;
                        filesModel.setTableData(file);
                    }
                }
            }
        }
    }


    public void changeRoot() {
        if(parentPath == null) return;
        this.changeRoot(parentPath);
        File file = new File(parentPath);
        parentPath = file.getParent();
        currentPath = file.getPath();
        destinationPath = currentPath;

    }

    public void changeRoot(String rootPath) {
        if(rootPath == null) return;
        File[] files = fileList.getFiles(rootPath);
        if (files == null) {
           // showNoMountedWarning();
            return;
        }
        this.files = Arrays.asList(files);
        filesModel.setTableData(this.files);
        currentPath = rootPath;
        destinationPath = currentPath;
    }

    private void showNoMountedWarning() {
        int dialogButton = JOptionPane.OK_CANCEL_OPTION;
        JOptionPane.showConfirmDialog(null,
                "You have no mounted disk", "JCommander", dialogButton);
    }

    private class TableListSelectionModel extends DefaultListSelectionModel {
        @Override
        public int getLeadSelectionIndex() {
            return -1;
        }
    }

    private class RowColumnListSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) return;
            if (getRowSelectionAllowed() && !getColumnSelectionAllowed()) {
                int[] rows = getSelectedRows();
                if (rows.length == 0) return;
                System.out.println("Selected Rows: " + Arrays.toString(rows));
            }
        }
    }
}
