import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class FilesPanel extends JPanel {
    private FilesTable filesTable;

    public FilesPanel(CommanderFrame commanderFrame) throws IOException {
        final FileList fileList = new FileList();
        JComboBox<File> rootsBox = new JComboBox<>();
        JButton returnButton = new JButton("<---");

        File[] roots = fileList.getRoots();
        for (File file : roots) {
            rootsBox.addItem(file);
        }
        filesTable = new FilesTable(commanderFrame, rootsBox.getSelectedItem().toString());
        rootsBox.addItemListener(e -> filesTable.changeRoot(e.getItem().toString()));
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filesTable.changeRoot();
            }
        });
        setLayout(new FlowLayout());
        add(returnButton, FlowLayout.LEFT);
        add(rootsBox, FlowLayout.CENTER);
        TableScrollPane scrollPane = new TableScrollPane(filesTable);
        add(scrollPane);

    }

    public FilesTable getFilesTable() {
        return filesTable;
    }
}
