package xyz.kohara.adjcore.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import xyz.kohara.adjcore.ADJCore;

public class Keybindings {

    public static final Keybindings INSTANCE = new Keybindings();

    private Keybindings() {
    }

    private static final String CATEGORY = "key.categories." + ADJCore.MOD_ID;

    private static KeyMapping loadoutKey(int number) {
        return new KeyMapping(
                ADJCore.MOD_ID + ".loadout." + number,
                KeyConflictContext.UNIVERSAL,
                InputConstants.getKey(289 + number, -1),
                CATEGORY
        );
    }

    public final KeyMapping LOADOUT_1 = loadoutKey(1);
    public final KeyMapping LOADOUT_2 = loadoutKey(2);
    public final KeyMapping LOADOUT_3 = loadoutKey(3);

//    public final KeyMapping NEW_DEBUG_MENU = new KeyMapping(
//            ADJCore.MOD_ID + ".debug_menu",
//            KeyConflictContext.IN_GAME,
//            InputConstants.getKey(InputConstants.KEY_F4, -1),
//            CATEGORY
//    );

    public final KeyMapping NEW_HIDE_GUI = new KeyMapping(
            ADJCore.MOD_ID + ".hide_gui",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_F8, -1),
            CATEGORY
    );
}
