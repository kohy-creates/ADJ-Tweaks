package xyz.kohara.adjtweaks.misc;

import dev.shadowsoffire.placebo.color.GradientColor;
import dev.shadowsoffire.placebo.util.PlaceboUtil;

public class Colors {

    private static int[] doubleUpGradient(int[] data) {
        int[] out = new int[data.length * 2];
        System.arraycopy(data, 0, out, 0, data.length);
        for (int i = data.length - 1; i >= 0; i--) {
            out[data.length * 2 - 1 - i] = data[i];
        }
        return out;
    }

    private static final int[] _LIGHT_BLUE_FLASH = {0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff,
            0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff,
            0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff,
            0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x0bb5ff,
            0x17b8ff, 0x22bbff, 0x2dbdff, 0x39c0ff, 0x44c3ff, 0x4fc6ff, 0x5bc9ff, 0x66ccff};

    private static final int[] _RED_FLASH = {
            0xA00000, 0xA40000, 0xA80000, 0xAC0000,
            0xB30000, 0xB70000, 0xBB0000, 0xBF0000,
            0xC00000, 0xC40000, 0xC80000, 0xCC0000,
            0xCA0000, 0xCE0000, 0xD20000, 0xD60000,
            0xD30000, 0xD70000, 0xDB0000, 0xDF0000,
            0xDC0000, 0xE00000, 0xE40000, 0xE80000,
            0xE30000, 0xE70000, 0xEB0000, 0xEF0000,
            0xE70000, 0xEB0000, 0xEF0000, 0xF30000,
            0xF50000
    };

    private static final int[] _YELLOW_FLASH = {0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700,
            0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700,
            0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700,
            0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700, 0xf0a700, 0xf0b200,
            0xf0bd00, 0xf0c800, 0xf6d608, 0xfadb12, 0xfde020, 0xffe433, 0xffe74a};

    public static GradientColor LIGHT_BLUE_FLASH = new GradientColor(doubleUpGradient(_LIGHT_BLUE_FLASH), "light_blue_flash");
    public static GradientColor RED_FLASH = new GradientColor(doubleUpGradient(_RED_FLASH), "light_blue_flash");
    public static GradientColor YELLOW_FLASH = new GradientColor(doubleUpGradient(_YELLOW_FLASH), "light_blue_flash");

    public static void register() {
        PlaceboUtil.registerCustomColor(LIGHT_BLUE_FLASH);
        PlaceboUtil.registerCustomColor(RED_FLASH);
        PlaceboUtil.registerCustomColor(YELLOW_FLASH);
    }
}
