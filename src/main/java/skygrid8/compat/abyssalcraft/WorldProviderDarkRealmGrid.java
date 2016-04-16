package skygrid8.compat.abyssalcraft;

import skygrid8.ChunkProviderGrid;
import skygrid8.config.GridRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.shinoow.abyssalcraft.AbyssalCraft;

public class WorldProviderDarkRealmGrid extends WorldProvider {

	@Override
	public IChunkGenerator createChunkGenerator() {
		return new ChunkProviderGrid(worldObj, worldObj.getSeed(), GridRegistry.blocksDarkRealm);
	}

	@Override
	public void registerWorldChunkManager() {
		worldChunkMgr = new BiomeProviderSingle(AbyssalCraft.darkRealm);
		setDimension(AbyssalCraft.configDimId4);
		hasNoSky = true;
	}

	@Override
	public boolean canRespawnHere() {
		return false;
	}

	@Override
	protected void generateLightBrightnessTable() {
		float f = 0.10F;

		for (int i = 0; i <= 15; ++i) {
			float f1 = 1.0F - i / 15.0F;
			lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vec3d getFogColor(float par1, float par2)
	{
		int i = 10518688;
		float f2 = MathHelper.cos(par1 * (float)Math.PI * 2.0F) * 2.0F + 0.5F;

		if (f2 < 0.0F)
			f2 = 0.0F;

		if (f2 > 1.0F)
			f2 = 1.0F;

		float f3 = (i >> 16 & 255) / 255.0F;
		float f4 = (i >> 8 & 255) / 255.0F;
		float f5 = (i & 255) / 255.0F;
		f3 *= f2 * 0.0F + 0.15F;
		f4 *= f2 * 0.0F + 0.15F;
		f5 *= f2 * 0.0F + 0.15F;
		return new Vec3d(f3, f4, f5);
	}

	@Override
	public boolean canDoRainSnowIce(Chunk chunk) {
		return false;
	}

	@Override
	public boolean isSurfaceWorld() {
		return false;
	}

	@Override
	public float calculateCelestialAngle(long par1, float par3) {
		return 0.0F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float[] calcSunriseSunsetColors(float par1, float par2) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isSkyColored() {
		return true;
	}

	@Override
	public Vec3d getSkyColor(Entity cameraEntity, float partialTicks) {
		return new Vec3d(0, 0, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getCloudHeight() {
		return 8.0F;
	}

	@Override
	public String getSaveFolder() {
		return "The_Dark_Realm";
	}

	@Override
	public int getAverageGroundLevel() {
		return 50;
	}

	@Override
	public DimensionType getDimensionType() {

		return AbyssalCraft.THE_DARK_REALM;
	}
}