package weatherpony.minelittlebrony2.entity.living.pony.ducks;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import weatherpony.minelittlebrony2.entity.living.PonyInventory2;

public interface IHaveAnInventory {
	public ItemEntity dropItem(ItemStack droppedItem, boolean dropAround, boolean traceItem);
	@SuppressWarnings("rawtypes")
	public PonyInventory2 getInventory();
	public void _openInventory(PlayerEntity player);
}
