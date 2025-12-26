package xyz.kohara.adjcore;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import xyz.kohara.adjcore.compat.kubejs.ClientEvents;
import xyz.kohara.adjcore.compat.kubejs.ServerEvents;

public class ADJKubeJSPlugin extends KubeJSPlugin {

    @Override
    public void registerEvents() {
        ServerEvents.GROUP.register();
        ClientEvents.GROUP.register();
    }
}
