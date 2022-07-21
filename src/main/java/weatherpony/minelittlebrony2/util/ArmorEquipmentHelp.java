package weatherpony.minelittlebrony2.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EquipmentSlot;

public class ArmorEquipmentHelp {
	private static EquipmentSlot[] VALID_EQUIPMENT_SLOTS;
	private static EquipmentSlot[] form(){
		List<EquipmentSlot> l=new ArrayList();
		for(EquipmentSlot e : EquipmentSlot.values()) {
			if(e.getType()==EquipmentSlot.Type.ARMOR)
				l.add(e);
		}
		//now... to flip indexes 0-3
		EquipmentSlot t = l.get(0);
		l.set(0,l.get(3));
		l.set(3,t);
		t = l.get(1);
		l.set(1,l.get(2));
		l.set(2,t);


		return l.toArray(new EquipmentSlot[l.size()]);
	}
	public static EquipmentSlot[] _get() {
		if(VALID_EQUIPMENT_SLOTS == null)
			VALID_EQUIPMENT_SLOTS=form();
		return VALID_EQUIPMENT_SLOTS;
	}
}
