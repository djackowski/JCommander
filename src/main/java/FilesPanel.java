import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class FilesPanel extends JPanel {
    private FilesTable filesTable;

    public FilesPanel() throws IOException {
        final FileList fileList = new FileList();
        JComboBox<File> rootsBox = new JComboBox<>();
        JButton returnButton = new JButton("Return");

        File[] roots = fileList.getRoots();
        for (File file : roots) {
            rootsBox.addItem(file);
        }
        filesTable = new FilesTable(rootsBox.getSelectedItem().toString());
        rootsBox.addItemListener(e -> filesTable.changeRoot(e.getItem().toString()));
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filesTable.changeRoot();
            }
        });
//        setLayout(new BorderLayout());
        //Create the menu bar.

        add(returnButton);
        add(rootsBox);
        TableScrollPane scrollPane = new TableScrollPane(filesTable);
        add(scrollPane);

    }

    public FilesTable getFilesTable() {
        return filesTable;
    }
}
