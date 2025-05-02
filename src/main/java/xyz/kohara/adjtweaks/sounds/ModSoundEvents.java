// Auditory
package xyz.kohara.adjtweaks.sounds;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
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

    public static final RegistryObject<SoundEvent> SHULKER_BOX_BREAK = registerSoundEvent("block.shulker_box.break");
    public static final RegistryObject<SoundEvent> SHULKER_BOX_STEP = registerSoundEvent("block.shulker_box.step");
    public static final RegistryObject<SoundEvent> SHULKER_BOX_PLACE = registerSoundEvent("block.shulker_box.place");
    public static final RegistryObject<SoundEvent> SHULKER_BOX_HIT = registerSoundEvent("block.shulker_box.hit");
    public static final RegistryObject<SoundEvent> SHULKER_BOX_FALL = registerSoundEvent("block.shulker_box.fall");

    public static final RegistryObject<SoundEvent> PURPUR_BREAK = registerSoundEvent("block.purpur.break");
    public static final RegistryObject<SoundEvent> PURPUR_STEP = registerSoundEvent("block.purpur.step");
    public static final RegistryObject<SoundEvent> PURPUR_PLACE = registerSoundEvent("block.purpur.place");
    public static final RegistryObject<SoundEvent> PURPUR_HIT = registerSoundEvent("block.purpur.hit");
    public static final RegistryObject<SoundEvent> PURPUR_FALL = registerSoundEvent("block.purpur.fall");

    public static final RegistryObject<SoundEvent> CHORUS_PLANT_BREAK = registerSoundEvent("block.chorus_plant.break");
    public static final RegistryObject<SoundEvent> CHORUS_PLANT_STEP = registerSoundEvent("block.chorus_plant.step");
    public static final RegistryObject<SoundEvent> CHORUS_PLANT_PLACE = registerSoundEvent("block.chorus_plant.place");
    public static final RegistryObject<SoundEvent> CHORUS_PLANT_HIT = registerSoundEvent("block.chorus_plant.hit");
    public static final RegistryObject<SoundEvent> CHORUS_PLANT_FALL = registerSoundEvent("block.chorus_plant.fall");

    public static final RegistryObject<SoundEvent> SPAWNER_BREAK = registerSoundEvent("block.spawner.break");

    public static final RegistryObject<SoundEvent> ICE_BREAK = registerSoundEvent("block.ice.break");
    public static final RegistryObject<SoundEvent> ICE_STEP = registerSoundEvent("block.ice.step");
    public static final RegistryObject<SoundEvent> ICE_PLACE = registerSoundEvent("block.ice.place");
    public static final RegistryObject<SoundEvent> ICE_HIT = registerSoundEvent("block.ice.hit");
    public static final RegistryObject<SoundEvent> ICE_FALL = registerSoundEvent("block.ice.fall");

    public static final RegistryObject<SoundEvent> GOURD_BREAK = registerSoundEvent("block.gourd.break");
    public static final RegistryObject<SoundEvent> GOURD_STEP = registerSoundEvent("block.gourd.step");
    public static final RegistryObject<SoundEvent> GOURD_PLACE = registerSoundEvent("block.gourd.place");
    public static final RegistryObject<SoundEvent> GOURD_HIT = registerSoundEvent("block.gourd.hit");
    public static final RegistryObject<SoundEvent> GOURD_FALL = registerSoundEvent("block.gourd.fall");

    // - Other Sounds:

    public static final RegistryObject<SoundEvent> ENTITY_PLAYER_DROP_ITEM = registerSoundEvent("entity.player.drop_item");
    public static final RegistryObject<SoundEvent> JUKEBOX_USE = registerSoundEvent("block.jukebox.use");
    public static final RegistryObject<SoundEvent> JUKEBOX_EJECT = registerSoundEvent("block.jukebox.eject");

    // Sound Groups:

    public static final SoundType ICE = new ForgeSoundType(1f, 1f,
            ModSoundEvents.ICE_BREAK, ModSoundEvents.ICE_STEP,
            ModSoundEvents.ICE_PLACE, ModSoundEvents.ICE_HIT,
            ModSoundEvents.ICE_FALL);

    public static final SoundType GOURD = new ForgeSoundType(1f, 1f,
            ModSoundEvents.GOURD_BREAK, ModSoundEvents.GOURD_STEP,
            ModSoundEvents.GOURD_PLACE, ModSoundEvents.GOURD_HIT,
            ModSoundEvents.GOURD_FALL);

    public static final SoundType SHULKER_BOX = new ForgeSoundType(1f, 1f,
            ModSoundEvents.SHULKER_BOX_BREAK, ModSoundEvents.SHULKER_BOX_STEP,
            ModSoundEvents.SHULKER_BOX_PLACE, ModSoundEvents.SHULKER_BOX_HIT,
            ModSoundEvents.SHULKER_BOX_FALL);

    public static final SoundType SPAWNER = new ForgeSoundType(1f, 1f,
            ModSoundEvents.SPAWNER_BREAK, () -> SoundEvents.NETHERITE_BLOCK_STEP,
            () -> SoundEvents.NETHERITE_BLOCK_PLACE, () -> SoundEvents.NETHERITE_BLOCK_HIT,
            () -> SoundEvents.NETHERITE_BLOCK_FALL);

    public static final SoundType PURPUR = new ForgeSoundType(1f, 1f,
            ModSoundEvents.PURPUR_BREAK, ModSoundEvents.PURPUR_STEP,
            ModSoundEvents.PURPUR_PLACE, ModSoundEvents.PURPUR_HIT,
            ModSoundEvents.PURPUR_FALL);

    public static final SoundType CHORUS_PLANT = new ForgeSoundType(1f, 1f,
            ModSoundEvents.CHORUS_PLANT_BREAK, ModSoundEvents.CHORUS_PLANT_STEP,
            ModSoundEvents.CHORUS_PLANT_PLACE, ModSoundEvents.CHORUS_PLANT_HIT,
            ModSoundEvents.CHORUS_PLANT_FALL);

    public static final SoundType STONE_ORE = new ForgeSoundType(1f, 0.9f,
            () -> SoundEvents.NETHER_ORE_BREAK, () -> SoundEvents.NETHER_ORE_STEP,
            () -> SoundEvents.NETHER_ORE_PLACE, () -> SoundEvents.NETHER_ORE_HIT,
            () -> SoundEvents.NETHER_ORE_FALL);

    public static final SoundType OBSIDIAN = new ForgeSoundType(1f, 0.7f,
            () -> SoundEvents.DEEPSLATE_BREAK, () -> SoundEvents.DEEPSLATE_STEP,
            () -> SoundEvents.DEEPSLATE_PLACE, () -> SoundEvents.DEEPSLATE_HIT,
            () -> SoundEvents.DEEPSLATE_FALL);

    public static final SoundType TERRACOTTA = new ForgeSoundType(1f, 0.6f,
            () -> SoundEvents.CALCITE_BREAK, () -> SoundEvents.CALCITE_STEP,
            () -> SoundEvents.CALCITE_PLACE, () -> SoundEvents.CALCITE_HIT,
            () -> SoundEvents.CALCITE_FALL);

    public static final SoundType STONE_BRICKS = new ForgeSoundType(1f, 0.6f,
            () -> SoundEvents.DEEPSLATE_TILES_BREAK, () -> SoundEvents.DEEPSLATE_TILES_STEP,
            () -> SoundEvents.DEEPSLATE_TILES_PLACE, () -> SoundEvents.DEEPSLATE_TILES_HIT,
            () -> SoundEvents.DEEPSLATE_TILES_FALL);

    public static final SoundType CLAY_BRICKS = new ForgeSoundType(1f, 1.3f,
            () -> SoundEvents.NETHER_BRICKS_BREAK, () -> SoundEvents.NETHER_BRICKS_STEP,
            () -> SoundEvents.NETHER_BRICKS_PLACE, () -> SoundEvents.NETHER_BRICKS_HIT,
            () -> SoundEvents.NETHER_BRICKS_FALL);

    public static final SoundType METAL = new ForgeSoundType(1f, 1.2f,
            () -> SoundEvents.NETHERITE_BLOCK_BREAK, () -> SoundEvents.NETHERITE_BLOCK_STEP,
            () -> SoundEvents.NETHERITE_BLOCK_PLACE, () -> SoundEvents.NETHERITE_BLOCK_HIT,
            () -> SoundEvents.NETHERITE_BLOCK_FALL);

    public static final SoundType GOLD = new ForgeSoundType(1f, 1.6f,
            () -> SoundEvents.NETHERITE_BLOCK_BREAK, () -> SoundEvents.NETHERITE_BLOCK_STEP,
            () -> SoundEvents.NETHERITE_BLOCK_PLACE, () -> SoundEvents.NETHERITE_BLOCK_HIT,
            () -> SoundEvents.NETHERITE_BLOCK_FALL);

    public static final SoundType LILY_PAD = new ForgeSoundType(1f, 1f,
            () -> SoundEvents.BIG_DRIPLEAF_BREAK, () -> SoundEvents.BIG_DRIPLEAF_STEP,
            () -> SoundEvents.LILY_PAD_PLACE, () -> SoundEvents.BIG_DRIPLEAF_HIT,
            () -> SoundEvents.BIG_DRIPLEAF_FALL);

    public static final SoundType SMALL_OBJECT = new ForgeSoundType(1f, 0.8f,
            () -> SoundEvents.CANDLE_BREAK, () -> SoundEvents.CANDLE_STEP,
            () -> SoundEvents.CANDLE_PLACE, () -> SoundEvents.CANDLE_HIT,
            () -> SoundEvents.CANDLE_FALL);

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("auditory", name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void registerSounds() {
        System.out.println("Registering Sounds for " + ADJTweaks.MOD_ID);
    }
}