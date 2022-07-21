package weatherpony.minelittlebrony2.entity.living.ai;

import java.util.Collections;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import weatherpony.minelittlebrony2.MineLBrony2_FabricMod;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IAmAPony;

public class PonyAIProvider_openInventory<PONY extends LivingEntity &IAmAPony> extends PonyAIProvider<PONY> {
	List<InteractionUnit<PONY>> interaction = Collections.singletonList(new InteractionUnit<PONY>() {
		@Override
		public boolean applies(PONY pony, PlayerEntity player, ItemStack with){
			if(with.getItem() == Blocks.CHEST.asItem()) {
				if(MineLBrony2_FabricMod.AICREATOR.hasPledgedAllegiance(pony) &&
					MineLBrony2_FabricMod.AICREATOR.shouldBeConsidered_Tamed(pony) &&
					MineLBrony2_FabricMod.AICREATOR.getAllegianceOwner_singular(pony) == player){
					return true;
				}
			}
			return false;
		}
		private ItemStack icon;
		@Override
		public ItemStack iconInCaseOfConflict(){
			if(this.icon == null) {
				this.icon = new ItemStack(Blocks.CHEST);
			}
			return this.icon;
		}
		@Override
		public void execute(PONY pony, PlayerEntity player, ItemStack with){
			pony._openInventory(player);
		}
	});
	public List<InteractionUnit<PONY>> getInteractions(){
		return this.interaction;
	}
}
