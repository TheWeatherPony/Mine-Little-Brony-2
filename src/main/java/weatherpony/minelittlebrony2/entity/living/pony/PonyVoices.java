package weatherpony.minelittlebrony2.entity.living.pony;

import java.util.HashMap;
import net.minecraft.util.Identifier;
import weatherpony.minelittlebrony2.entity.living.PonySounds;

public class PonyVoices {
	public static final PonyVoices INSTANCE = new PonyVoices();
	private PonyVoices(){}

	record PonySoundUnit(String modID, String ponyName, boolean isChangeling) {
	}
	HashMap<Identifier, PonySoundUnit> soundSetup = new HashMap();
	HashMap<Identifier, PonySounds> ponySounds = new HashMap();
	public void applyPonySound(String modID, String ponyName, boolean isChangeling){
		Identifier pony = new Identifier(modID,ponyName);
		PonySoundUnit u = soundSetup.get(pony);
		if(u == null){
			u = new PonySoundUnit(modID, ponyName, isChangeling);
			soundSetup.put(pony, u);
			PonySounds sounds = new PonySounds(u.modID,u.ponyName,u.isChangeling);
			ponySounds.put(pony, sounds);
		}else{
			if(u.isChangeling != isChangeling)
				throw new IllegalStateException();
		}
	}
	public PonySounds getSounds(String modID, String ponyName){
	  return this.getSounds(new Identifier(modID,ponyName));
	}
	public PonySounds getSounds(Identifier ponyID){
	  return ponySounds.get(ponyID);
	}
}
