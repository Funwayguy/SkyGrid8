package funwayguy.skygrid.world;

import funwayguy.skygrid.config.GridBlock;
import funwayguy.skygrid.config.GridRegistry;
import funwayguy.skygrid.core.SG_Settings;
import funwayguy.skygrid.handlers.EventHandler;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nullable;
import java.util.*;

public class ChunkProviderGrid implements IChunkGenerator
{
    private World worldObj;
    private Random random;
    private final List<GridBlock> gridBlocks;
    
    public ChunkProviderGrid(World world, long seed, List<GridBlock> blocks)
    {
    	worldObj = world;
    	random = new Random(seed);
    	gridBlocks = blocks;
    }
	
	@Override
	public Chunk generateChunk(int x, int z)
	{
        Biome[] abiomegenbase = this.worldObj.getBiomeProvider().getBiomes(null, x * 16, z * 16, 16, 16);
        ChunkPrimer chunkprimer = new ChunkPrimer();
        
        int spaceX = random.nextInt(Math.max(1, SG_Settings.dist + 1));
        int spaceY = random.nextInt(Math.max(1, SG_Settings.dist + 1));
        int spaceZ = random.nextInt(Math.max(1, SG_Settings.dist + 1));
        
        if(!SG_Settings.rngSpacing)
        {
        	spaceX = spaceY = spaceZ = SG_Settings.dist;
        }
        
        boolean[] validSpawns = new boolean[16 * 16];
        
        int amount = (256 / spaceY) * (16 / spaceX) * (16 / spaceZ);
        List<GridBlock> randomBlocks = GridRegistry.getRandom(random, gridBlocks, abiomegenbase[0], worldObj.provider.getDimension(), amount);
        int n = 0;
        
        for (int i = 0; i < 256 && i < SG_Settings.height; i += spaceY)
        {
            for (int j = (x*16)%spaceX; j < 16; j += spaceX)
            {
                for (int k = (z*16)%spaceZ; k < 16; k += spaceZ)
                {
                	validSpawns[k * 16 + j] = true;
					
                	GridBlock gb = randomBlocks.get(n);
                	n++;
                	
					chunkprimer.setBlockState(j, i, k, gb.getState());
					
					IBlockState plant = gb.plants.size() <= 0? null : gb.plants.get(random.nextInt(gb.plants.size())).getState();
					
					if(i < 255 && plant != null)
					{
						chunkprimer.setBlockState(j, i + 1, k, plant);
					}
					
					if(gb.getState().getBlock() instanceof ITileEntityProvider)
					{
						PostGenerator.addLocation(worldObj.provider.getDimension(), x, z, new BlockPos(x*16 + j, i, z*16 + k));
					}
                }
            }
        }
		
		EventHandler.spawnCache.put(x + "," + z + "," + worldObj.provider.getDimension(), validSpawns);
        
        if(x == 0 && z == 0)
        {
        	chunkprimer.setBlockState(0, SG_Settings.height, 0, Blocks.BEDROCK.getDefaultState());
        }
        
        Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
        byte[] abyte = chunk.getBiomeArray();
        
        for (int l = 0; l < abyte.length; ++l)
        {
            abyte[l] = (byte)Biome.getIdForBiome(abiomegenbase[l]);
        }
        
        chunk.generateSkylightMap();
        return chunk;
	}
	
	@Override
	public void populate(int p_73153_2_, int p_73153_3_)
	{
		if(!SG_Settings.populate)
		{
			return;
		}
		
        int i = p_73153_2_ * 16;
        int j = p_73153_3_ * 16;
        BlockPos blockpos = new BlockPos(i, 0, j);
        Biome biomegenbase = this.worldObj.getBiome(new BlockPos(i + 16, 0, j + 16));
        
        biomegenbase.decorate(this.worldObj, this.random, blockpos);
	}
	
	@Override
	public boolean generateStructures(Chunk p_177460_2_, int p_177460_3_, int p_177460_4_)
	{
		return false;
	}
	
	private Map<String, List<SpawnListEntry>> cachedSpawns = new HashMap<>();
	
	@Override
	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
	{
        Biome biomegenbase = this.worldObj.getBiome(pos);
        String key = biomegenbase.getRegistryName().toString() + ":" + creatureType.toString();
        List<SpawnListEntry> list = cachedSpawns.get(key);
        
        if(list == null)
		{
			list = biomegenbase.getSpawnableList(creatureType);
			cachedSpawns.put(key, list);
		}
		
        return list;
	}

	@Nullable
	@Override
	public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
		return null;
	}
	
	@Override
	public void recreateStructures(Chunk p_180514_1_, int p_180514_2_, int p_180514_3_)
	{
	}

	@Override
	public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
		return false;
	}
}
