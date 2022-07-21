package weatherpony.minelittlebrony2.entity.living.pony.ducks;


import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.text.HoverEvent;
import net.minecraft.util.math.random.Random;

public interface MiscHelperFunctions {
	//void _setSize(float w, float h);
	DataTracker _getEntityDataManager();
	HoverEvent pony_getHoverEvent();
	boolean getInWaterVariable();
	Random RAND();
	void pony_onInitialSpawn();
}
