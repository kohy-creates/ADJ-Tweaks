package xyz.kohara.adjcore.misc.credits;

import xyz.kohara.adjcore.ADJCore;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Credits {

    public enum Files {
        POEM("poem.txt"),
        CREDITS("credits.json"),
        POST_CREDITS("post_credits.txt"),
        TITLES("titles.txt"),
        SKINS("name_to_skin_mappings.txt");

        private final String fileName;

        Files(String fileName) {
            this.fileName = fileName;
        }

        public File getFile() {
            return new File("config/" + ADJCore.MOD_ID + "/credits/", fileName);
        }

        public boolean exists() {
            return getFile().exists();
        }
    }

    public static Map<String, String> PLAYER_TITLES = new HashMap<>();

    public static Map<String, String> NAME_TO_SKIN = new HashMap<>();

    static {
        File config;
        try {
            config = Files.TITLES.getFile();
            if (config.exists()) {
                Scanner reader = new Scanner(config);
                while (reader.hasNextLine()) {
                    String lineEntry = reader.nextLine();
                    String[] line = lineEntry.split(":");
                    PLAYER_TITLES.put(line[0], line[1]);
                }
            }

            config = Files.SKINS.getFile();
            if (config.exists()) {
                Scanner reader = new Scanner(config);
                while (reader.hasNextLine()) {
                    String lineEntry = reader.nextLine();
                    String[] line = lineEntry.split(":");
                    NAME_TO_SKIN.put(line[0], line[1]);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
