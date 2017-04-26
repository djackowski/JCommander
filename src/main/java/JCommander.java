import javax.swing.*;
import java.io.IOException;

public class JCommander {

    public static void main(String[] args) {
        System.setProperty("Resources_pl", "UTF-8");
        SwingUtilities.invokeLater(() -> {
            try {
                new CommanderFrame();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
