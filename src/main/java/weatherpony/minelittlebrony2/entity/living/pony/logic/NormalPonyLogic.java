package weatherpony.minelittlebrony2.entity.living.pony.logic;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import weatherpony.minelittlebrony2.entity.living.PonySounds;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IAmAPony;

public class NormalPonyLogic<PONY extends LivingEntity & IAmAPony> extends PonyLogic<PONY> {

	public NormalPonyLogic(PonyLogicFactory factory, String modID, String name){
		super(factory);
		this.setSkinWithDerivedData(new Identifier(modID, "textures/entity/"+name+".png"));
		this.name = name;
	}
	protected String name;
	public void setSound(PonySounds sounds){
		this.sounds = sounds;
	}
	@Override
	protected String getName(PONY pony){
		return this.name;
	}
}
