package weatherpony.minelittlebrony2.entity.living.pony;

import java.util.*;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IAmAPony;
import weatherpony.minelittlebrony2.entity.living.pony.logic.PonyLogic;
import weatherpony.minelittlebrony2.entity.living.pony.logic.PonyLogicFactory;

public class LogicFactories {
	private static Random RAND = new Random();
	private static List<PonyLogicFactory> factories = new ArrayList();
	static Map<Identifier,PonyLogicFactory> ponylogics = new HashMap();
	private LogicFactories() {
	}
	public static void registerFactories(PonyLogicFactory...factories) {
		for(PonyLogicFactory e : factories)
			registerFactory(e);
	}
	public static void registerFactories(List<PonyLogicFactory> factories) {
		for(PonyLogicFactory e : factories)
			registerFactory(e);
	}
	
	public static void registerFactory(PonyLogicFactory factory) {
		if(factory == null)
			throw new NullPointerException();
		if(ponylogics.containsKey(factory.getFactoryIdentifier()))
			throw new IllegalStateException();
		ponylogics.put(factory.getFactoryIdentifier(), factory);
		factories.add(factory);
	}
	public static void postInit(){
		for(PonyLogicFactory e : factories)
			e.connect();
	}
	public static <PONY extends LivingEntity & IAmAPony> PonyLogic<PONY> getRandomLogic(PONY brandNewPony) {
		if(brandNewPony == null)
			throw new NullPointerException();
		return factories.get(RAND.nextInt(factories.size())).getRandomLogic();
	}
	public static PonyLogicFactory getFactoryByRes(Identifier factoryRes){
		return ponylogics.get(factoryRes);
	}
	
	
}
