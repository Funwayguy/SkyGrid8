package funwayguy.skygrid.world;

import javax.annotation.Nullable;

import funwayguy.skygrid.config.GridRegistry;
import funwayguy.skygrid.core.SG_Settings;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.end.DragonFightManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderEndGrid extends WorldProviderEnd
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
    
    @Override
    public void onWorldSave() {}
    
    @Override
    public void onWorldUpdateEntities() {}
    
    @Override
    public DragonFightManager getDragonFightManager()
    {
    	return null;
    }
}