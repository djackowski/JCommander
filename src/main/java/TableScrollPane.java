import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class TableScrollPane extends JScrollPane {
    private final FilesTable filesTable;

    public TableScrollPane(FilesTable filesTable) {
        super(filesTable);
        this.filesTable = filesTable;

        addMouseListener(new TableScrollPaneMouseAdapter());
    }

    private class TableScrollPaneMouseAdapter extends MouseAdapter implements TablePopUp.OnTableListChanged {

        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            if (!SwingUtilities.isRightMouseButton(e)) {
                return;
            }
            filesTable.clearSelection();
            File[] filesArray = filesTable.getFilesArray();
            String currentPath = filesTable.getCurrentPath();
           /* if(filesArray == null) {
                currentPath = new File(currentPath).getParent();
            }*/
            String destinationPath = filesTable.getDestinationPath();
            TablePopUp popup = new TablePopUp(filesTable.getCommanderFrame(), filesArray, currentPath, destinationPath);
            popup.setOnTableListChanged(this);
            popup.show(e.getComponent(), e.getX(), e.getY());


        }

        @Override
        public void onTableListChanged(String parentPath) {
            filesTable.getTableMouseAdapter().onTableListChanged(parentPath);
        }
    }
}
