package net.mytoolsbox.everquest.packeq.actions;

import net.mytoolsbox.everquest.packeq.singleton.EQDirectory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SearchEQDirectory extends AbstractAction implements ActionListener {
    public SearchEQDirectory(String name) {
        super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fc = EQDirectory.getInstance().getFileChooser();
        int returnVal = fc.showOpenDialog((Component) e.getSource());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            EQDirectory.getInstance().setEqDirectory(file);
        }
    }

    @Override
    public boolean accept(Object sender) {
        return super.accept(sender);
    }
}
