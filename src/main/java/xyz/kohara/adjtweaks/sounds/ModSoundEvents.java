// Auditory
package xyz.kohara.adjtweaks.sounds;

import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.adjtweaks.ADJTweaks;

public class ModSoundEvents {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ADJTweaks.MOD_ID);

    // - Item Sounds:

    public static final RegistryObject<SoundEvent> ITEM_BOW_PULLING = registerSoundEvent("item.bow.pulling");
    public static final RegistryObject<SoundEvent> ITEM_TRIDENT_PULLING = registerSoundEvent("item.trident.pulling");
    public static final RegistryObject<SoundEvent> ITEM_SPAWN_EGG_USE = registerSoundEvent("item.spawn_egg.use");
    public static final RegistryObject<SoundEvent> ITEM_SHIELD_RAISE = registerSoundEvent("item.shield.raise");
    public static final RegistryObject<SoundEvent> ITEM_SHIELD_EQUIP = registerSoundEvent("item.shield.equip");
    public static final RegistryObject<SoundEvent> DRIED_KELP_EAT = registerSoundEvent("item.dried_kelp.eat");
    public static final RegistryObject<SoundEvent> SOFT_FRUIT_EAT = registerSoundEvent("item.soft_fruit.eat");
    public static final RegistryObject<SoundEvent> CRUNCHY_FRUIT_EAT = registerSoundEvent("item.crunchy_fruit.eat");
    public static final RegistryObject<SoundEvent> STEW_EAT = registerSoundEvent("item.stew.eat");
    public static final RegistryObject<SoundEvent> VEGETABLE_EAT = registerSoundEvent("item.vegetable.eat");

    // - Block Sounds:

    public static final RegistryObject<SoundEvent> BLOCK_SHULKER_BOX_BREAK = registerSoundEvent("block.shulker_box.break");
    public static final RegistryObject<SoundEvent> BLOCK_SHULKER_BOX_STEP = registerSoundEvent("block.shulker_box.step");
    public static final RegistryObject<SoundEvent> BLOCK_SHULKER_BOX_PLACE = registerSoundEvent("block.shulker_box.place");
    public static final RegistryObject<SoundEvent> BLOCK_SHULKER_BOX_HIT = registerSoundEvent("block.shulker_box.hit");
    public static final RegistryObject<SoundEvent> BLOCK_SHULKER_BOX_FALL = registerSoundEvent("block.shulker_box.fall");

    public static final RegistryObject<SoundEvent> BLOCK_PURPUR_BREAK = registerSoundEvent("block.purpur.break");
    public static final RegistryObject<SoundEvent> BLOCK_PURPUR_STEP = registerSoundEvent("block.purpur.step");
    public static final RegistryObject<SoundEvent> BLOCK_PURPUR_PLACE = registerSoundEvent("block.purpur.place");
    public static final RegistryObject<SoundEvent> BLOCK_PURPUR_HIT = registerSoundEvent("block.purpur.hit");
    public static final RegistryObject<SoundEvent> BLOCK_PURPUR_FALL = registerSoundEvent("block.purpur.fall");

    public static final RegistryObject<SoundEvent> BLOCK_CHORUS_PLANT_BREAK = registerSoundEvent("block.chorus_plant.break");
    public static final RegistryObject<SoundEvent> BLOCK_CHORUS_PLANT_STEP = registerSoundEvent("block.chorus_plant.step");
    public static final RegistryObject<SoundEvent> BLOCK_CHORUS_PLANT_PLACE = registerSoundEvent("block.chorus_plant.place");
    public static final RegistryObject<SoundEvent> BLOCK_CHORUS_PLANT_HIT = registerSoundEvent("block.chorus_plant.hit");
    public static final RegistryObject<SoundEvent> BLOCK_CHORUS_PLANT_FALL = registerSoundEvent("block.chorus_plant.fall");

    public static final RegistryObject<SoundEvent> BLOCK_SPAWNER_BREAK = registerSoundEvent("block.spawner.break");

    public static final RegistryObject<SoundEvent> BLOCK_ICE_BREAK = registerSoundEvent("block.ice.break");
    public static final RegistryObject<SoundEvent> BLOCK_ICE_STEP = registerSoundEvent("block.ice.step");
    public static final RegistryObject<SoundEvent> BLOCK_ICE_PLACE = registerSoundEvent("block.ice.place");
    public static final RegistryObject<SoundEvent> BLOCK_ICE_HIT = registerSoundEvent("block.ice.hit");
    public static final RegistryObject<SoundEvent> BLOCK_ICE_FALL = registerSoundEvent("block.ice.fall");

    public static final RegistryObject<SoundEvent> BLOCK_GOURD_BREAK = registerSoundEvent("block.gourd.break");
    public static final RegistryObject<SoundEvent> BLOCK_GOURD_STEP = registerSoundEvent("block.gourd.step");
    public static final RegistryObject<SoundEvent> BLOCK_GOURD_PLACE = registerSoundEvent("block.gourd.place");
    public static final RegistryObject<SoundEvent> BLOCK_GOURD_HIT = registerSoundEvent("block.gourd.hit");
    public static final RegistryObject<SoundEvent> BLOCK_GOURD_FALL = registerSoundEvent("block.gourd.fall");

    // - Other Sounds:

    public static final RegistryObject<SoundEvent> ENTITY_PLAYER_DROP_ITEM = registerSoundEvent("entity.player.drop_item");
    public static final RegistryObject<SoundEvent> BLOCK_JUKEBOX_USE = registerSoundEvent("block.jukebox.use");
    public static final RegistryObject<SoundEvent> BLOCK_JUKEBOX_EJECT = registerSoundEvent("block.jukebox.eject");

    // Sound Groups:

    public static final BlockSoundGroup ICE = new ForgeSoundType(1f, 1f,
            ModSoundEvents.BLOCK_ICE_BREAK, ModSoundEvents.BLOCK_ICE_STEP,
            ModSoundEvents.BLOCK_ICE_PLACE, ModSoundEvents.BLOCK_ICE_HIT,
            ModSoundEvents.BLOCK_ICE_FALL);

    public static final BlockSoundGroup GOURD = new ForgeSoundType(1f, 1f,
            ModSoundEvents.BLOCK_GOURD_BREAK, ModSoundEvents.BLOCK_GOURD_STEP,
            ModSoundEvents.BLOCK_GOURD_PLACE, ModSoundEvents.BLOCK_GOURD_HIT,
            ModSoundEvents.BLOCK_GOURD_FALL);

    public static final BlockSoundGroup SHULKER_BOX = new ForgeSoundType(1f, 1f,
            ModSoundEvents.BLOCK_SHULKER_BOX_BREAK, ModSoundEvents.BLOCK_SHULKER_BOX_STEP,
            ModSoundEvents.BLOCK_SHULKER_BOX_PLACE, ModSoundEvents.BLOCK_SHULKER_BOX_HIT,
            ModSoundEvents.BLOCK_SHULKER_BOX_FALL);

    public static final BlockSoundGroup SPAWNER = new ForgeSoundType(1f, 1f,
            ModSoundEvents.BLOCK_SPAWNER_BREAK, () -> SoundEvents.BLOCK_NETHERITE_BLOCK_STEP,
            () -> SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, () -> SoundEvents.BLOCK_NETHERITE_BLOCK_HIT,
            () -> SoundEvents.BLOCK_NETHERITE_BLOCK_FALL);

    public static final BlockSoundGroup PURPUR = new ForgeSoundType(1f, 1f,
            ModSoundEvents.BLOCK_PURPUR_BREAK, ModSoundEvents.BLOCK_PURPUR_STEP,
            ModSoundEvents.BLOCK_PURPUR_PLACE, ModSoundEvents.BLOCK_PURPUR_HIT,
            ModSoundEvents.BLOCK_PURPUR_FALL);

    public static final BlockSoundGroup CHORUS_PLANT = new ForgeSoundType(1f, 1f,
            ModSoundEvents.BLOCK_CHORUS_PLANT_BREAK, ModSoundEvents.BLOCK_CHORUS_PLANT_STEP,
            ModSoundEvents.BLOCK_CHORUS_PLANT_PLACE, ModSoundEvents.BLOCK_CHORUS_PLANT_HIT,
            ModSoundEvents.BLOCK_CHORUS_PLANT_FALL);

    public static final BlockSoundGroup STONE_ORE = new ForgeSoundType(1f, 0.9f,
            () -> SoundEvents.BLOCK_NETHER_ORE_BREAK, () -> SoundEvents.BLOCK_NETHER_ORE_STEP,
            () -> SoundEvents.BLOCK_NETHER_ORE_PLACE, () -> SoundEvents.BLOCK_NETHER_ORE_HIT,
            () -> SoundEvents.BLOCK_NETHER_ORE_FALL);

    public static final BlockSoundGroup OBSIDIAN = new ForgeSoundType(1f, 0.7f,
            () -> SoundEvents.BLOCK_DEEPSLATE_BREAK, () -> SoundEvents.BLOCK_DEEPSLATE_STEP,
            () -> SoundEvents.BLOCK_DEEPSLATE_PLACE, () -> SoundEvents.BLOCK_DEEPSLATE_HIT,
            () -> SoundEvents.BLOCK_DEEPSLATE_FALL);

    public static final BlockSoundGroup TERRACOTTA = new ForgeSoundType(1f, 0.6f,
            () -> SoundEvents.BLOCK_CALCITE_BREAK, () -> SoundEvents.BLOCK_CALCITE_STEP,
            () -> SoundEvents.BLOCK_CALCITE_PLACE, () -> SoundEvents.BLOCK_CALCITE_HIT,
            () -> SoundEvents.BLOCK_CALCITE_FALL);

    public static final BlockSoundGroup STONE_BRICKS = new ForgeSoundType(1f, 0.6f,
            () -> SoundEvents.BLOCK_DEEPSLATE_TILES_BREAK, () -> SoundEvents.BLOCK_DEEPSLATE_TILES_STEP,
            () -> SoundEvents.BLOCK_DEEPSLATE_TILES_PLACE, () -> SoundEvents.BLOCK_DEEPSLATE_TILES_HIT,
            () -> SoundEvents.BLOCK_DEEPSLATE_TILES_FALL);

    public static final BlockSoundGroup CLAY_BRICKS = new ForgeSoundType(1f, 1.3f,
            () -> SoundEvents.BLOCK_NETHER_BRICKS_BREAK, () -> SoundEvents.BLOCK_NETHER_BRICKS_STEP,
            () -> SoundEvents.BLOCK_NETHER_BRICKS_PLACE, () -> SoundEvents.BLOCK_NETHER_BRICKS_HIT,
            () -> SoundEvents.BLOCK_NETHER_BRICKS_FALL);

    public static final BlockSoundGroup METAL = new ForgeSoundType(1f, 1.2f,
            () -> SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK, () -> SoundEvents.BLOCK_NETHERITE_BLOCK_STEP,
            () -> SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, () -> SoundEvents.BLOCK_NETHERITE_BLOCK_HIT,
            () -> SoundEvents.BLOCK_NETHERITE_BLOCK_FALL);

    public static final BlockSoundGroup GOLD = new ForgeSoundType(1f, 1.6f,
            () -> SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK, () -> SoundEvents.BLOCK_NETHERITE_BLOCK_STEP,
            () -> SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, () -> SoundEvents.BLOCK_NETHERITE_BLOCK_HIT,
            () -> SoundEvents.BLOCK_NETHERITE_BLOCK_FALL);

    public static final BlockSoundGroup LILY_PAD = new ForgeSoundType(1f, 1f,
            () -> SoundEvents.BLOCK_BIG_DRIPLEAF_BREAK, () -> SoundEvents.BLOCK_BIG_DRIPLEAF_STEP,
            () -> SoundEvents.BLOCK_LILY_PAD_PLACE, () -> SoundEvents.BLOCK_BIG_DRIPLEAF_HIT,
            () -> SoundEvents.BLOCK_BIG_DRIPLEAF_FALL);

    public static final BlockSoundGroup SMALL_OBJECT = new ForgeSoundType(1f, 0.8f,
            () -> SoundEvents.BLOCK_CANDLE_BREAK, () -> SoundEvents.BLOCK_CANDLE_STEP,
            () -> SoundEvents.BLOCK_CANDLE_PLACE, () -> SoundEvents.BLOCK_CANDLE_HIT,
            () -> SoundEvents.BLOCK_CANDLE_FALL);

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        Identifier id = Identifier.of("auditory", name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.of(id));
    }

    public static void registerSounds() {
        System.out.println("Registering Sounds for " + ADJTweaks.MOD_ID);
    }
}