package skygrid8.world;

import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import skygrid8.config.GridRegistry;
import skygrid8.core.SG_Settings;

public class WorldProviderEndGrid extends WorldProvider
{
    /**
     * creates a new world chunk manager for WorldProvider
     */
	@Override
    public void createBiomeProvider()
    {
        this.biomeProvider = new BiomeProviderSingle(Biomes.SKY);
        this.hasNoSky = true;
    }
	
	@Override
    public IChunkGenerator createChunkGenerator()
    {
        return new ChunkProviderGrid(this.worldObj, this.worldObj.getSeed(), GridRegistry.blocksEnd);
    }

    /**
     * Calculates the angle of sun and moon in the sky relative to a specified time (usually worldTime)
     */
    @Override
    public float calculateCelestialAngle(long worldTime, float partialTicks)
    {
        return 0.0F;
    }

    /**
     * Returns array with sunrise/sunset colors
     */
    @Override
    @SideOnly(Side.CLIENT)
    public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks)
    {
        return null;
    }

    /**
     * Return Vec3D with biome specific fog color
     */
    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(float p_76562_1_, float p_76562_2_)
    {
        int i = 10518688;
        float f = MathHelper.cos(p_76562_1_ * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
        f = MathHelper.clamp_float(f, 0.0F, 1.0F);
        float f1 = (float)(i >> 16 & 255) / 255.0F;
        float f2 = (float)(i >> 8 & 255) / 255.0F;
        float f3 = (float)(i & 255) / 255.0F;
        f1 = f1 * (f * 0.0F + 0.15F);
        f2 = f2 * (f * 0.0F + 0.15F);
        f3 = f3 * (f * 0.0F + 0.15F);
        return new Vec3d((double)f1, (double)f2, (double)f3);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean isSkyColored()
    {
        return false;
    }

    /**
     * True if the player can respawn in this dimension (true = overworld, false = nether).
     */
    @Override
    public boolean canRespawnHere()
    {
        return false;
    }

    /**
     * Returns 'true' if in the "main surface world", but 'false' if in the Nether or End dimensions.
     */
    @Override
    public boolean isSurfaceWorld()
    {
        return false;
    }

    /**
     * the y level at which clouds are rendered.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public float getCloudHeight()
    {
        return 8.0F;
    }

    /**
     * Will check if the x, z position specified is alright to be set as the map spawn point
     */
    @Override
    public boolean canCoordinateBeSpawn(int x, int z)
    {
        return this.worldObj.getGroundAboveSeaLevel(new BlockPos(x, 0, z)).getMaterial().blocksMovement();
    }
    
    @Override
    public BlockPos getSpawnCoordinate()
    {
        return new BlockPos(100, SG_Settings.height + 1, 0);
    }
    
    @Override
    public int getAverageGroundLevel()
    {
        return SG_Settings.height;
    }

    /**
     * Returns true if the given X,Z coordinate should show environmental fog.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean doesXZShowFog(int x, int z)
    {
        return false;
    }
    
    @Override
    public DimensionType getDimensionType()
    {
        return DimensionType.THE_END;
    }
}