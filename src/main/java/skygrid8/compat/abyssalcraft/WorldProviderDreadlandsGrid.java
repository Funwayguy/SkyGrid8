package skygrid8.compat.abyssalcraft;

import com.shinoow.abyssalcraft.AbyssalCraft;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import skygrid8.config.GridRegistry;
import skygrid8.world.ChunkProviderGrid;

public class WorldProviderDreadlandsGrid extends WorldProvider {

	@Override
	public void createBiomeProvider() {
		biomeProvider = new BiomeProviderDreadlandsGrid(worldObj.getSeed(), worldObj.getWorldInfo().getTerrainType());
		hasNoSky = true;
		setDimension(AbyssalCraft.configDimId2);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vec3d getFogColor(float par1, float par2) {
		return new Vec3d(0.20000000298023224D, 0.029999999329447746D, 0.029999999329447746D);
	}

	@Override
	protected void generateLightBrightnessTable() {
		float f = 0.35F;

		for (int i = 0; i <= 15; ++i) {
			float f1 = 1.0F - i / 15.0F;
			lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
		}
	}

	@Override
	public IChunkGenerator createChunkGenerator() {
		return new ChunkProviderGrid(worldObj, worldObj.getSeed(), GridRegistry.blocksDreadlands);
	}

	@Override
	public boolean isSurfaceWorld() {
		return false;
	}

	@Override
	public boolean canCoordinateBeSpawn(int par1, int par2) {
		return false;
	}

	@Override
	public float calculateCelestialAngle(long par1, float par3) {
		return 0.5F;
	}

	@Override
	public boolean canRespawnHere() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean doesXZShowFog(int par1, int par2) {
		return true;
	}

	@Override
	public String getSaveFolder() {
		return "The_Dreadlands";
	}

	@Override
	public int getAverageGroundLevel() {
		return 50;
	}

	@Override
	public DimensionType getDimensionType() {

		return AbyssalCraft.THE_DREADLANDS;
	}
}