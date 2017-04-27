import javax.swing.*;
import java.io.IOException;

public class JCommander {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new CommanderFrame();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
