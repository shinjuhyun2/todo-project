import javax.swing.*;

import model.*;
import service.*;
import ui.*;

public class Main {
    public static void main(String[] args) {
        // Swing GUI는 Event Dispatch Thread에서 실행되어야 함
        SwingUtilities.invokeLater(() -> {
            try {
                // 시스템 Look and Feel 사용
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Look and Feel 설정 실패: " + e.getMessage());
            }

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
