package net.mytoolsbox.everquest.packeq.singleton;

import com.github.sarxos.winreg.HKey;
import com.github.sarxos.winreg.RegistryException;
import com.github.sarxos.winreg.WindowsRegistry;
import net.mytoolsbox.everquest.packeq.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public final class EQDirectory {
    private static final Logger LOGGER = LoggerFactory.getLogger(EQDirectory.class);
    private static EQDirectory INSTANCE;
    private static final String TREE = "SOFTWARE\\MyToolsBox\\PackEQ";
    private static final String VALUE_NAME = "EQDirectory";
    private static WindowsRegistry reg;
    private File eqDirectory;
    private JFileChooser fileChooser;

    private EQDirectory() {
        reg = WindowsRegistry.getInstance();
        String value = null;
        boolean readFromRegistry = false;
        try {
            value = reg.readString(HKey.HKCU, TREE, VALUE_NAME);
            eqDirectory = new File(value);
            if (eqDirectory.exists() && eqDirectory.isDirectory() && directoryContainsEQ(eqDirectory)) {
                LOGGER.info("EQ Directory read from Windows registry.");
                readFromRegistry = true;
            }
        } catch (RegistryException e) {
            try {
                reg.createKey(HKey.HKCU, TREE);
            } catch (RegistryException ex) {
                LOGGER.error("Couldn't add key to Windows registry.");
                throw new RuntimeException(ex);
            }
        }
        if (!readFromRegistry) {
            findEQDirectory();
            writeEQDirectoryToRegistry(eqDirectory);
        }
    }

    public static EQDirectory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EQDirectory();
        }
        return INSTANCE;
    }

    private void findEQDirectory() {
        File[] roots = File.listRoots();
        File result = null;
        for (int i = 0; i < roots.length; i++) {
            File content = roots[i];
            if (content.isDirectory()) {
                result = checkDirectory(content);
            }
        }
        if (result != null) {
            eqDirectory = result;
            LOGGER.info("EQ Directory is : {}", result.getAbsolutePath());
        } else {
            LOGGER.warn("Couldn't determine where is EQ Directory");
        }
    }

    private File checkDirectory(File directory) {
        String[] filesArray = directory.list();
        if (filesArray != null) {
            for (int i = 0; i < filesArray.length; i++) {
                File content = new File(directory, filesArray[i]);
                if (content.getName().toLowerCase().equals(Constants.EQ_EXE_NAME.toLowerCase())) {
                    return directory;
                }
                if (content.isDirectory()) {
                    File result = checkDirectory(content);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    private boolean directoryContainsEQ(File directory) {
        String[] filesArray = directory.list();
        boolean result = false;
        if (filesArray != null) {
            for (int i = 0; i < filesArray.length; i++) {
                File content = new File(directory, filesArray[i]);
                if (content.getName().toLowerCase().equals(Constants.EQ_EXE_NAME.toLowerCase())) {
                    result = true;
                }
            }
        }
        return result;
    }

    private void writeEQDirectoryToRegistry(File directory) {
        try {
            reg.writeStringValue(HKey.HKCU, TREE, VALUE_NAME, directory.getAbsolutePath());
        } catch (RegistryException e) {
            LOGGER.error("Couldn't write key value to Windows registry.");
            throw new RuntimeException(e);
        }
        MainTextArea.getInstance().logText("EQ Directory set to : " + directory.getAbsolutePath());
    }

    public File getEqDirectory() {
        return eqDirectory;
    }

    public void setEqDirectory(File inputDirectory) {
        if (directoryContainsEQ(inputDirectory)) {
            eqDirectory = inputDirectory;
            writeEQDirectoryToRegistry(eqDirectory);
        } else {
            MainTextArea.getInstance().logText("No " + Constants.EQ_EXE_NAME + " found in : " + inputDirectory.getAbsolutePath());
            LOGGER.error("Directory {} doesn't contain Everquest", inputDirectory.getAbsolutePath());
        }
    }

    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    public void setFileChooser(JFileChooser fileChooser) {
        this.fileChooser = fileChooser;
        this.fileChooser.setName(Constants.MENU_CHOSE_EQ_DIRECTORY);
        this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        this.fileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LOGGER.trace("Handled in the inner listener of the JFileChooser class");
            }
        });
    }
}
