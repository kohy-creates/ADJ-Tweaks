package xyz.kohara.adjcore.misc.credits;

import java.util.ArrayList;
import java.util.List;

public record LoaderInfo(String name, String altName, List<String> authors, List<String> contributors) {
    public LoaderInfo(String name, String altName, List<String> authors) {
        this(name, altName, authors, new ArrayList<>());
    }
}
