package xyz.kohara.adjcore.misc.credits;

import java.util.List;

public interface ModCreditsBase {
    List<ModInfo> getMods();
    LoaderInfo getLoaderInfo();
}