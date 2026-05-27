package xyz.kohara.adjcore.registry.lootconditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;
import xyz.kohara.adjcore.registry.ADJLootConditions;

public class IsChapter implements LootItemCondition {

	private final int chapter;

	public IsChapter(int fromChapter) {
		this.chapter = fromChapter;
	}

	@Override
	public @NotNull LootItemConditionType getType() {
		return ADJLootConditions.IS_CHAPTER.get();
	}

	@Override
	public boolean test(LootContext lootContext) {
		var pData = lootContext.getLevel().getServer().kjs$getPersistentData();
		if (pData != null && pData.get("chapters") != null) {
			var chapters = pData.getCompound("chapters");
			if (chapters.get("current_stage") != null) {
				return Integer.parseInt(chapters.getString("current_stage").split("_")[1]) >= chapter;
			}
		}
		return false;
	}

	public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<IsChapter> {
		public void serialize(@NotNull JsonObject jsonObject,
							  @NotNull IsChapter arg,
							  @NotNull JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("from", arg.chapter);
		}

		public @NotNull IsChapter deserialize(@NotNull JsonObject jsonObject,
											  @NotNull JsonDeserializationContext jsonDeserializationContext) {
			return new IsChapter(GsonHelper.getAsInt(jsonObject, "from"));
		}
	}
}
