package net.mytoolsbox.everquest.packeq.singleton;

import javax.swing.*;

public class MainTextArea extends JTextArea {
    private static MainTextArea INSTANCE;

    private JTextArea textArea;

    private MainTextArea() {
    }

    public static MainTextArea getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MainTextArea();
        }
        return INSTANCE;
    }

    public void logText(String text) {
        textArea.append(text);
        textArea.append("\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
        textArea.update(textArea.getGraphics());
    }

    public void setTextArea(JTextArea textArea) {
        this.textArea = textArea;
    }
}
