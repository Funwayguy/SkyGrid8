package funwayguy.skygrid.world;


import funwayguy.skygrid.config.GridRegistry;
import funwayguy.skygrid.core.SG_Settings;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderEndGrid extends WorldProviderEnd
{
    /**
     * creates a new world chunk manager for WorldProvider
     */
	@Override
    public void init()
    {
        this.biomeProvider = new BiomeProviderSingle(Biomes.SKY);
        this.hasSkyLight = true;
    }

	@Override
    public IChunkGenerator createChunkGenerator()
    {
        return new ChunkProviderGrid(this.world, this.world.getSeed(), GridRegistry.blocksEnd);
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