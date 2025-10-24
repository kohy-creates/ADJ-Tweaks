package xyz.kohara.adjcore.misc.credits;

import java.util.ArrayList;
import java.util.List;

public record ModInfo(String modName, List<String> authors, List<String> contributors, String modId) {
    public ModInfo(String modName, List<String> authors, String modId) {
        this(modName, authors, new ArrayList<>(), modId);
    }
}