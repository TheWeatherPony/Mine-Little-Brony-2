package weatherpony.minelittlebrony2.entity.living.pony.ducks;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import weatherpony.minelittlebrony2.entity.living.PonyLogicReasoning;

public interface IAmAPony extends IMonitorPoisons, ISpeak, IHavePonyLogic, IMayBeChangeling, IChangeAttributes,
		ISitWithoutRiding,
		//IStoreData,//to simplify generics, IStoreData is written in scala. only those that work with adding data would care, though. Not present here, so the Java portion compiles first, else the Scala compiler uses the wrong bridge methods for methods with intersection-type generic arguments
		PossibleAI, MiscHelperFunctions,
		IHaveAnInventory{
	@SuppressWarnings("unchecked")
	public default double getSizeMagnification(){
		return this.getMyLogic(PonyLogicReasoning.VISUAL).getSizeMagnification((LivingEntity) this);
	}
	public default Identifier getSkin(){
		return this.getMyLogic(PonyLogicReasoning.VISUAL).getSkin();
	}
}
