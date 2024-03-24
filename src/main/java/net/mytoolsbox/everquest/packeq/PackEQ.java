package net.mytoolsbox.everquest.packeq;

import net.mytoolsbox.everquest.packeq.actions.PackFiles;
import net.mytoolsbox.everquest.packeq.actions.SearchEQDirectory;
import net.mytoolsbox.everquest.packeq.listener.MenuActionListener;
import net.mytoolsbox.everquest.packeq.singleton.EQDirectory;
import net.mytoolsbox.everquest.packeq.singleton.MainTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class PackEQ extends JPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(PackEQ.class);

    public static void main(String[] args) {
        LOGGER.info("Launching application, please wait.");
        EQDirectory eqDirectory = EQDirectory.getInstance();
        LOGGER.info("EQ Directory set to : {}", eqDirectory.getEqDirectory().getAbsolutePath());

        //Creating the Frame
        JFrame frame = new JFrame(Constants.WINDOW_NAME);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        //Creating the MenuBar and adding components
        MenuActionListener menuActionListener = new MenuActionListener();
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu(Constants.MENU_FILE);
        JMenu m2 = new JMenu(Constants.MENU_ABOUT);
        mb.add(m1);
        mb.add(m2);

        SearchEQDirectory searchEQDirectory = new SearchEQDirectory(Constants.MENU_EQ_DIRECTORY);
        JMenuItem m11 = new JMenuItem(searchEQDirectory);
        m11.addActionListener(menuActionListener);
        m1.add(m11);

        PackFiles packFiles = new PackFiles(Constants.MENU_PACK_FILES);
        JMenuItem m12 = new JMenuItem(packFiles);
        m12.addActionListener(menuActionListener);
        m1.add(m12);

        JMenuItem m13 = new JMenuItem(Constants.MENU_EXIT);
        m13.addActionListener(menuActionListener);
        m1.add(m13);

        JMenuItem m21 = new JMenuItem(Constants.MENU_VERSION);
        m21.addActionListener(menuActionListener);
        m2.add(m21);

        JFileChooser fc = new JFileChooser(EQDirectory.getInstance().getEqDirectory().getAbsolutePath());
        EQDirectory.getInstance().setFileChooser(fc);

        // Text Area at the Center
        JTextArea ta = new JTextArea();
        ta.setBackground(Color.BLACK);
        ta.setForeground(Color.MAGENTA);
        ta.setEditable(false);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(ta);

        MainTextArea.getInstance().setTextArea(ta);
        MainTextArea.getInstance().logText("Welcome to PackEQ");
        MainTextArea.getInstance().logText("EQ Directory set to : " + eqDirectory.getEqDirectory().getAbsolutePath());

        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.getContentPane().add(BorderLayout.CENTER, scrollPane);
        frame.setVisible(true);
    }
}
