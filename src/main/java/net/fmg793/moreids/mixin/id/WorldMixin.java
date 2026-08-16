package net.fmg793.moreids.mixin.id;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import ext.block.ExtBlock;
import ext.client.InputHandler;
import net.fmg793.moreids.id.ChunkCacheExt;
import net.fmg793.moreids.id.WorldChunkExt;
import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.storage.AlphaChunkStorage;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;

@Mixin(World.class)
public abstract class WorldMixin {

	@Shadow
	public List players;

	@Shadow
	private Set tickingChunks;

	@Shadow
	public Random random;

	@Shadow
	public boolean snowCovered;

	@Shadow
	public boolean sandCovered;

	@Shadow
	private int ambientSoundCooldown;

	@Shadow
	protected int randomTickLCG;

	@Shadow
	protected int randomTickLCGIncrement;

	@Shadow
	public long seed;

	@Shadow
	public abstract WorldChunk getChunkAt(int chunkX, int chunkZ);

	@Shadow
	public abstract boolean setBlock(int x, int y, int z, int block);

	@Shadow
	public abstract int getSurfaceHeight(int x, int z);

	@Shadow
	public abstract int getRawBrightness(int x, int y, int z);

	@Shadow
	public abstract int getLight(LightType type, int x, int y, int z);

	@Shadow
	public abstract PlayerEntity getNearestPlayer(double x, double y, double z, double range);

	@Shadow
	public abstract void playSound(double x, double y, double z, String sound, float volume, float pitch);

	@Overwrite
	public void tickChunks() {
		this.tickingChunks.clear();

		int i1;
		int i2;
		int i3;
		int i4;
		for(int i5 = 0; i5 < this.players.size(); ++i5) {
			PlayerEntity playerEntity6 = (PlayerEntity)this.players.get(i5);
			i1 = MathHelper.floor(playerEntity6.x / 16.0D);
			i2 = MathHelper.floor(playerEntity6.z / 16.0D);
			byte b7 = 9;

			for(i3 = -b7; i3 <= b7; ++i3) {
				for(i4 = -b7; i4 <= b7; ++i4) {
					this.tickingChunks.add(new ChunkPos(i3 + i1, i4 + i2));
				}
			}
		}

		if(this.ambientSoundCooldown > 0) {
			--this.ambientSoundCooldown;
		}

		Iterator iterator12 = this.tickingChunks.iterator();

		while(iterator12.hasNext()) {
			ChunkPos chunkPos13 = (ChunkPos)iterator12.next();
			i1 = chunkPos13.x * 16;
			i2 = chunkPos13.z * 16;
			WorldChunkExt worldChunk14 = (WorldChunkExt) this.getChunkAt(chunkPos13.x, chunkPos13.z);
			int i8;
			int i9;
			int i10;
			if(this.ambientSoundCooldown == 0) {
				this.randomTickLCG = this.randomTickLCG * 3 + this.randomTickLCGIncrement;
				i3 = this.randomTickLCG >> 2;
				i4 = i3 & 15;
				i8 = i3 >> 8 & 15;
				i9 = i3 >> 16 & 127;
				i10 = worldChunk14.getBlockAt(i4, i9, i8);
				i4 += i1;
				i8 += i2;
				if(i10 == 0 && this.getRawBrightness(i4, i9, i8) <= this.random.nextInt(8) && this.getLight(LightType.SKY, i4, i9, i8) <= 0) {
					PlayerEntity playerEntity11 = this.getNearestPlayer((double)i4 + 0.5D, (double)i9 + 0.5D, (double)i8 + 0.5D, 8.0D);
					if(playerEntity11 != null && playerEntity11.squaredDistanceTo((double)i4 + 0.5D, (double)i9 + 0.5D, (double)i8 + 0.5D) > 4.0D) {
						this.playSound((double)i4 + 0.5D, (double)i9 + 0.5D, (double)i8 + 0.5D, "ambient.cave.cave", 0.7F, 0.8F + this.random.nextFloat() * 0.2F);
						this.ambientSoundCooldown = this.random.nextInt(12000) + 6000;
					}
				}
			}

			if(this.snowCovered && this.random.nextInt(4) == 0) {
				this.randomTickLCG = this.randomTickLCG * 3 + this.randomTickLCGIncrement;
				i3 = this.randomTickLCG >> 2;
				i4 = i3 & 15;
				i8 = i3 >> 8 & 15;
				i9 = this.getSurfaceHeight(i4 + i1, i8 + i2);
				if(i9 >= 0 && i9 < 128 && worldChunk14.getLightAt(LightType.BLOCK, i4, i9, i8) < 10) {
					i10 = worldChunk14.getBlockAt(i4, i9 - 1, i8);
					if(worldChunk14.getBlockAt(i4, i9, i8) == 0 && ExtBlock.SNOW_LAYER.canBePlaced((World)(Object)this, i4 + i1, i9, i8 + i2)) {
						this.setBlock(i4 + i1, i9, i8 + i2, ExtBlock.SNOW_LAYER.id);
					}

					if(i10 == ExtBlock.WATER.id && worldChunk14.getBlockMetadataAt(i4, i9 - 1, i8) == 0) {
						this.setBlock(i4 + i1, i9 - 1, i8 + i2, ExtBlock.ICE.id);
					}
				}
			}

			if(this.sandCovered && this.random.nextInt(4) == 1) {
				this.randomTickLCG = this.randomTickLCG * 3 + this.randomTickLCGIncrement;
				i3 = this.randomTickLCG >> 2;
				i4 = i3 & 15;
				i8 = i3 >> 8 & 15;
				i9 = this.getSurfaceHeight(i4 + i1, i8 + i2);
				if(i9 >= 0 && i9 < 128 && worldChunk14.getLightAt(LightType.BLOCK, i4, i9, i8) < 10) {
					i10 = worldChunk14.getBlockAt(i4, i9 - 1, i8);
					if(i10 == ExtBlock.WATER.id && worldChunk14.getBlockMetadataAt(i4, i9 - 1, i8) == 0 && this.random.nextInt(16) == 0) {
						this.setBlock(i4 + i1, i9 - 1, i8 + i2, ExtBlock.CLAY_BLOCK.id);
					}
				}
			}

			if(InputHandler.minecraft.raining) {
				this.randomTickLCG = this.randomTickLCG * 3 + this.randomTickLCGIncrement;
				i3 = this.randomTickLCG >> 2;
				i4 = i3 & 15;
				i8 = i3 >> 8 & 15;
				i9 = this.getSurfaceHeight(i4 + i1, i8 + i2);
				if(i9 >= 0 && i9 < 128 && worldChunk14.getLightAt(LightType.BLOCK, i4, i9, i8) < 8) {
					worldChunk14.getBlockAt(i4, i9 - 1, i8);
					if(worldChunk14.getBlockAt(i4, i9, i8) == 0 && i9 < 66) {
						this.setBlock(i4 + i1, i9, i8 + i2, ExtBlock.WATER.id);
					}
				}
			}

			for(i3 = 0; i3 < 80; ++i3) {
				this.randomTickLCG = this.randomTickLCG * 3 + this.randomTickLCGIncrement;
				i4 = this.randomTickLCG >> 2;
				i8 = i4 & 15;
				i9 = i4 >> 8 & 15;
				i10 = i4 >> 16 & 127;
				int i15 = worldChunk14.newblocks[i8 << 11 | i9 << 7 | i10];
				if(ExtBlock.TICKS_RANDOMLY[i15]) {
					ExtBlock.BY_ID[i15].tick((World)(Object)this, i8 + i1, i10, i9 + i2, this.random);
				}
			}
		}

	}

	@Overwrite
	public ChunkSource createChunkCache(File dir) {
		return new ChunkCacheExt((World)(Object)this, new AlphaChunkStorage(dir, true), new OverworldChunkGenerator((World)(Object)this, this.seed));
	}
}
