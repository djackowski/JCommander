import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilesModel extends AbstractTableModel {
    private String[] columnNames = {Resources.getCurrentResources().getString("name")
            , Resources.getCurrentResources().getString("size"),
            Resources.getCurrentResources().getString("date")};
    private List<File> fileList;

    public FilesModel(List<File> fileList) {
        this.fileList = fileList;
    }

    @Override
    public int getRowCount() {
        if (fileList == null) {
            return 0;
        } else {
            return fileList.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }


    @Override
    public Object getValueAt(int row, int col) {
        FileList files = new FileList();
        Object temp = null;
        File file = fileList.get(row);
        if (file == null) return "";

        if (col == 0) {
            temp = file.getName();
        } else if (col == 1) {
            temp = files.getSize(file);
        } else if (col == 2) {
            try {
                temp = files.getCreationDate(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return temp;
    }

    public List<File> getFileList(int row) {
        File[] files = fileList.get(row).listFiles();
        return files == null ? new ArrayList<>() : Arrays.asList(files);
    }

    public File getFile(int row) {
        return fileList.get(row);
    }

    public File[] getFileList(int[] rows) {
        File[] files = new File[rows.length];
        for (int i = 0; i < rows.length; i++) {
            files[i] = fileList.get(rows[i]);
        }
        return files;
    }

    public void setTableData(List<File> files) {
        this.fileList = files;
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }


}
