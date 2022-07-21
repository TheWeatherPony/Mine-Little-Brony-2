package weatherpony.minelittlebrony2.entity.living.pony.logic;

import java.util.Random;

import net.minecraft.util.Identifier;

public abstract class PonyLogicFactory{
	public static final Random RAND = new Random();
	public PonyLogicFactory(Identifier factoryID){
		if(factoryID == null)
			throw new NullPointerException();
		this.factoryID = factoryID;
	}
	final Identifier factoryID;
	
	public void connect() {}
	public abstract String compressToString(PonyLogic logic);
	public abstract PonyLogic expandToLogic(String logic);
	public abstract PonyLogic getRandomLogic();

	public final Identifier getFactoryIdentifier() {
		return this.factoryID;
	}
}
