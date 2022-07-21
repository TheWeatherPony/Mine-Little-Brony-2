package weatherpony.minelittlebrony2.entity.living;

import java.util.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PonySounds{
	public PonySounds(String modID, String ponyName, boolean isChangeling){
		if(isChangeling){
			normal = changeling = new EnumMap(SoundTypes.class);
			poisoned = poisonedChangeling = new EnumMap(SoundTypes.class);
		}else{
			normal = new EnumMap(SoundTypes.class);
			poisoned = new EnumMap(SoundTypes.class);
			changeling = new EnumMap(SoundTypes.class);
			poisonedChangeling = new EnumMap(SoundTypes.class);
		}
		String[] modifiers = new String[]{"normal", "poisoned","changeling", "poisoned_changeling"};
		final EnumMap<SoundTypes, SoundEvent>[] maps =
				(this.normal != this.changeling)
						? new EnumMap[]{normal,poisoned,changeling,poisonedChangeling}
						:new EnumMap[]{normal,poisoned};
		SoundTypes[] soundTypes = SoundTypes.values();
		for(int i=3;i>=0;i--){//for actual changelings, the "changeling section is overwritten
			String modifiedNameBase = "entity.ponies."+ponyName+"."+modifiers[i]+".";
			for(SoundTypes eachSound : soundTypes){
				Identifier resourcelocation = new Identifier(modID,modifiedNameBase+eachSound.name());
				SoundEvent sound = new SoundEvent(resourcelocation);
				Registry.register(Registry.SOUND_EVENT, resourcelocation, sound);
				maps[i].put(eachSound, sound);
			}
		}
	}
	public List<SoundEvent> getSoundsForJSON(){
		final EnumMap<SoundTypes, SoundEvent>[] maps =
				(this.normal != this.changeling)
						? new EnumMap[]{normal,poisoned,changeling,poisonedChangeling}
						:new EnumMap[]{normal,poisoned};
		ArrayList<SoundEvent> s = new ArrayList();
		for(EnumMap<SoundTypes, SoundEvent> e1 : maps){
			s.addAll(e1.values());
		}
		return s;
	}
	public SoundEvent get(SoundTypes type, boolean isPoisoned, boolean isDisguisedChangeling){
		EnumMap<SoundTypes, SoundEvent> map =
				(isPoisoned ? 
						(isDisguisedChangeling ? poisonedChangeling : poisoned) :
						(isDisguisedChangeling ? changeling : normal));
		return map.get(type);
	}
	final EnumMap<SoundTypes, SoundEvent> normal;
	final EnumMap<SoundTypes, SoundEvent> poisoned;
	final EnumMap<SoundTypes, SoundEvent> changeling;
	final EnumMap<SoundTypes, SoundEvent> poisonedChangeling;
}
