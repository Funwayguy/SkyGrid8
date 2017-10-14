package funwayguy.skygrid.world;

import net.minecraft.world.WorldProviderHell;
import funwayguy.skygrid.config.GridRegistry;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderNetherGrid extends WorldProviderHell
{

    @Override
    public IChunkGenerator createChunkGenerator()
    {
        return new ChunkProviderGrid(this.world, this.world.getSeed(), GridRegistry.blocksNether);
    }
}