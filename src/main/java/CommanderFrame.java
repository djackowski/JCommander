import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.awt.BorderLayout.EAST;
import static java.awt.BorderLayout.WEST;

public class CommanderFrame extends JFrame implements ContextChangeListener, ChangeLocalListener {
    private Context context;
    private JMenu menu;
    private final JMenuItem english;
    private final JMenuItem polish;
    private final JMenu submenu;
    private final Container container;
    private final FilesPanel firstPanel;
    private final FilesPanel secondPanel;

    public CommanderFrame() throws IOException {
        context = new Context("Resources");
        context.addContextChangeListener(this);
        container = getContentPane();
        setSize(300, 300);
        setLocation(50, 50);
        setLayout(new BorderLayout());
        firstPanel = new FilesPanel();
        container.add(firstPanel, WEST);
        secondPanel = new FilesPanel();
        container.add(secondPanel, EAST);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("JCommander");
        setSize(800, 600);
        setVisible(true);
        JMenuBar menuBar = new JMenuBar();

        //Build the first menu.
        String optionsMessage = Resources.getCurrentResources().getString("options");
        menu = new JMenu(optionsMessage);
        menu.getAccessibleContext().setAccessibleDescription(
                "The only menu in this program that has menu items");
        menuBar.add(menu);

        //a group of JMenuItems

        String changeLanguageMessage = Resources.getCurrentResources().getString("changeLanguage");
        submenu = new JMenu(changeLanguageMessage);
        submenu.setMnemonic(KeyEvent.VK_S);
        String englishLanguage = Resources.getCurrentResources().getString("english");
        english = new JMenuItem(englishLanguage);
        submenu.add(english);
        english.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                context.setLocale(new Locale("en"));
            }
        });
        String polishLanguage = Resources.getCurrentResources().getString("polish");
        polish = new JMenuItem(polishLanguage);
        submenu.add(polish);
        polish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                context.setLocale(new Locale("pl"));
            }
        });
        menu.add(submenu);

        setJMenuBar(menuBar);


    }

    @Override
    public void contextChanged() {
        Locale locale = context.getLocale();
        Locale.setDefault(locale);
        ResourceBundle.clearCache();
        onChangeLocal();
    }

    @Override
    public void onChangeLocal() {
        menu.setText(Resources.getCurrentResources().getString("options"));
        submenu.setText(Resources.getCurrentResources().getString("changeLanguage"));
        english.setText(Resources.getCurrentResources().getString("english"));
        polish.setText(Resources.getCurrentResources().getString("polish"));
        firstPanel.getFilesTable().changeLocal();
        secondPanel.getFilesTable().changeLocal();
        repaint();
        invalidate();

    }
}
