package weatherpony.minelittlebrony2.entity.living.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IAmAPony;

//maybe I should have just written this in scala...
public abstract class PonyAIProvider<PONY extends LivingEntity &IAmAPony> {//possibly in scala with the trait DataFactory (IStoreData)
	public static interface IAllianceProvider<PONY extends LivingEntity&IAmAPony>{
		boolean isWild(PONY pony);
		boolean hasPledgedAllegiance(PONY pony);
		LivingEntity getAllegianceOwner_singular(PONY pony);
		boolean isAllied(PONY pony, LivingEntity other);
		boolean shouldBeConsidered_Tamed(PONY pony);
		
	}
	public static interface ISittingController<PONY extends LivingEntity&IAmAPony>{
		boolean isSitting(PONY pony);
	}
	public static abstract class PonyAIHolderUnit<PONY extends LivingEntity&IAmAPony> extends PonyAIProvider<PONY> implements IAllianceProvider<PONY>,ISittingController<PONY>{
		
	}
	public void initAI(PONY pony) {
		
	}
	public Function<PONY,Void> applyEntityAttributes(){
		return null;
	}
	public Function<PONY,Void> applyAttributes_late(){
		return null;
	}
	public Function<PONY,Void> on_refresh_early(){
		return null;
	}
	public Function<PONY,Void> on_refresh_middle(){
		return null;
	}
	public Function<PONY,Void> on_refresh_last(){
		return null;
	}
	public List<InteractionUnit<PONY>> getInteractions(){
		return new ArrayList(0);
	}
	public Function<PONY,Void> onDamaged(){
		return null;
	}
	public Function<PONY,Void> onTick(){
		return null;
	}
}
