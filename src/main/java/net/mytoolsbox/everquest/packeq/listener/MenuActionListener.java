package net.mytoolsbox.everquest.packeq.listener;

import net.mytoolsbox.everquest.packeq.Constants;
import net.mytoolsbox.everquest.packeq.singleton.MainTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuActionListener implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MenuActionListener.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        switch (actionCommand) {
            case Constants.MENU_EQ_DIRECTORY:
                break;
            case Constants.MENU_CHOSE_EQ_DIRECTORY:
                break;
            case Constants.MENU_VERSION:
                MainTextArea.getInstance().logText("PackEQ version : 1.0.0");
                MainTextArea.getInstance().logText("Author : Patoo <Legion Rising> from Antonius Bayle server");
                break;
            case Constants.MENU_EXIT:
                LOGGER.info("Exiting application.");
                System.exit(0);
                break;
            default:
                break;
        }
    }
}