package xyz.kohara.adjcore.client.renderer.entity.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface VillagerEntityHeadModel {
	void hatVisible(boolean visible);
}