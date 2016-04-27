package skygrid8;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import skygrid8.config.GridBlock;
import skygrid8.config.GridRegistry;
import skygrid8.core.SG_Settings;

public class ChunkProviderGrid implements IChunkGenerator
{
    private World worldObj;
    private Random random;
    private final ArrayList<GridBlock> gridBlocks;
    
    public ChunkProviderGrid(World world, long seed, ArrayList<GridBlock> blocks)
    {
    	worldObj = world;
    	random = new Random(seed);
    	gridBlocks = blocks;
    }
	
	@Override
	public Chunk provideChunk(int x, int z)
	{
        BiomeGenBase[] abiomegenbase = this.worldObj.getBiomeProvider().loadBlockGeneratorData((BiomeGenBase[])null, x * 16, z * 16, 16, 16);
        ChunkPrimer chunkprimer = new ChunkPrimer();
        
        int spaceX = random.nextInt(Math.max(1, SG_Settings.dist + 1));
        int spaceY = random.nextInt(Math.max(1, SG_Settings.dist + 1));
        int spaceZ = random.nextInt(Math.max(1, SG_Settings.dist + 1));
        
        if(!SG_Settings.rngSpacing)
        {
        	spaceX = spaceY = spaceZ = SG_Settings.dist;
        }
        
        for (int i = 0; i < 256 && i < SG_Settings.height; i += spaceY)
        {
            for (int j = 0; j < 16; ++j)
            {
                for (int k = 0; k < 16; ++k)
                {
                	BiomeGenBase biome = abiomegenbase[k << 4 | j];
                	GridBlock gb = gridBlocks.size() <= 0? new GridBlock(Blocks.bedrock) : GridRegistry.getRandom(random, gridBlocks, biome);
                    
                	if((x*16 + j)%spaceX != 0 || (z*16 + k)%spaceZ != 0 || gb == null)
                	{
                		chunkprimer.setBlockState(j, i, k, Blocks.air.getDefaultState());
                	} else
                	{
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
        }
        
        if(x == 0 && z == 0)
        {
        	chunkprimer.setBlockState(0, SG_Settings.height, 0, Blocks.bedrock.getDefaultState());
        }
        
        Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
        byte[] abyte = chunk.getBiomeArray();
        
        for (int l = 0; l < abyte.length; ++l)
        {
            abyte[l] = (byte)BiomeGenBase.getIdForBiome(abiomegenbase[l]);
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
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(new BlockPos(i + 16, 0, j + 16));
        
        biomegenbase.decorate(this.worldObj, this.random, blockpos);
	}
	
	@Override
	public boolean generateStructures(Chunk p_177460_2_, int p_177460_3_, int p_177460_4_)
	{
		return false;
	}
	
	@Override
	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
	{
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(pos);
        return biomegenbase.getSpawnableList(creatureType);
	}
	
	@Override
	public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position)
	{
		return null;
	}
	
	@Override
	public void recreateStructures(Chunk p_180514_1_, int p_180514_2_, int p_180514_3_)
	{
	}
}
