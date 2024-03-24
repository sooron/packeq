package net.mytoolsbox.everquest.packeq.actions;

import net.mytoolsbox.everquest.packeq.Constants;
import net.mytoolsbox.everquest.packeq.singleton.EQDirectory;
import net.mytoolsbox.everquest.packeq.singleton.MainTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PackFiles extends AbstractAction implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(PackFiles.class);
    private final Map<String, String> characterMap = new HashMap<>();

    public PackFiles(String name) {
        super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        readCharactersFromIniFile();
        List<File> filesToPack = listFilesForCharacters();
        zipFiles(filesToPack);
    }

    @Override
    public boolean accept(Object sender) {
        return super.accept(sender);
    }

    private void readCharactersFromIniFile() {
        String charactersFileName = EQDirectory.getInstance().getEqDirectory().getAbsoluteFile() + "/" + Constants.EQ_CHARACTERS;
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(charactersFileName));
            String line = reader.readLine();

            while (line != null) {
                if (line.contains("=") && line.contains(",")) {
                    String[] tokens = line.split("=")[1].split(",");
                    String charName = tokens[0];
                    String serverName = tokens[1];
                    LOGGER.trace("Found character {} on server {}", charName, serverName);
                    if (!characterMap.containsKey(charName)) {
                        characterMap.put(charName, charName);
                    }
                }
                line = reader.readLine();
            }
            reader.close();
            MainTextArea.getInstance().logText("Found " + characterMap.values().size() + " characters to pack.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<File> listFilesForCharacters() {
        List<File> filesList = new ArrayList<>();

        //EQ Directory ini files
        File eqDirectory = EQDirectory.getInstance().getEqDirectory();
        BufferedReader reader;
        File[] filesArray = eqDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".ini"));
        if (filesArray != null) {
            for (int i = 0; i < filesArray.length; i++) {
                if (fileIsACharacterFile(filesArray[i].getName())) {
                    LOGGER.trace("Adding file {} to the content list", filesArray[i].getAbsolutePath());
                    filesList.add(filesArray[i]);
                }
            }
        }

        //EQ Directory userdata ini files
        File userDataDirectory = new File(eqDirectory.getAbsoluteFile() + "/" + Constants.EQ_USERDATA);
        filesArray = userDataDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".ini"));
        if (filesArray != null) {
            for (int i = 0; i < filesArray.length; i++) {
                if (fileIsACharacterFile(filesArray[i].getName())) {
                    LOGGER.trace("Adding file {} to the content list", filesArray[i].getAbsolutePath());
                    filesList.add(filesArray[i]);
                }
            }
        }
        return filesList;
    }

    private void zipFiles(List<File> filesToPack) {
        File eqDirectory = EQDirectory.getInstance().getEqDirectory();
        String finalName = eqDirectory.getAbsolutePath() + "/packedEq.zip";
        File previousFile = new File(finalName);

        if (previousFile.exists()) {
            if (previousFile.delete()) {
                MainTextArea.getInstance().logText("Previous file " + finalName + " has been deleted.");
            }
        }

        final FileOutputStream fos;
        try {
            fos = new FileOutputStream(finalName);
            try (ZipOutputStream zipOut = new ZipOutputStream(fos)) {

                for (File srcFile : filesToPack) {
                    FileInputStream fis = new FileInputStream(srcFile);
                    String nameFromRootEq = srcFile.getAbsolutePath().replace(eqDirectory.getAbsolutePath() + "\\", "");
                    LOGGER.trace("Adding zipEntry {} to file", nameFromRootEq);
                    ZipEntry zipEntry = new ZipEntry(nameFromRootEq);
                    zipOut.putNextEntry(zipEntry);

                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }
                    fis.close();
                }
            }
            fos.close();
        } catch (IOException e) {
            MainTextArea.getInstance().logText("Failed to create file " + finalName);
            throw new RuntimeException(e);
        }
        MainTextArea.getInstance().logText("Packed files into " + finalName);
    }

    private boolean fileIsACharacterFile(String filename) {
        boolean found = false;
        Iterator<String> charIterator = characterMap.values().iterator();
        while (charIterator.hasNext() && !found) {
            String characterName = charIterator.next();
            found = filename.toLowerCase().contains(characterName.toLowerCase());
        }
        return found;
    }
}
