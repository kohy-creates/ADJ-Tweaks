package xyz.kohara.adjcore;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ADJMixinCanceller implements MixinCanceller {

    private static final String configFile = "config/" + ADJCore.MOD_ID + "/disabled_mixins.txt";

    private static final List<String> disabledMixins = new ArrayList<>();

    static {
        File config = new File(configFile);
        try {
            if (!config.exists()) {
                config.createNewFile();
            }
            Scanner reader = new Scanner(config);
            while (reader.hasNextLine()) {
                String mixin = reader.nextLine();
                disabledMixins.add(mixin);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        return (disabledMixins.contains(mixinClassName));
    }
}
