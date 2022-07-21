package weatherpony.minelittlebrony2.entity.living.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IAmAPony;

public interface InteractionUnit<PONY extends LivingEntity &IAmAPony> {
	public boolean applies(PONY pony, PlayerEntity player, ItemStack with);
	public ItemStack iconInCaseOfConflict();
	public void execute(PONY pony, PlayerEntity player, ItemStack with);
}
