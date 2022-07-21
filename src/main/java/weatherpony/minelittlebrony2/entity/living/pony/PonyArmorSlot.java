package weatherpony.minelittlebrony2.entity.living.pony;

import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class PonyArmorSlot extends Slot {//FIXME - more than 4 armor slots
    final EquipmentSlot equipmentSlot;
    public PonyArmorSlot(Inventory inventory, int index, int x, int y, EquipmentSlot equipmentSlot) {
        super(inventory, index, x, y);
        this.equipmentSlot = equipmentSlot;
    }
    @Override
    public int getMaxItemCount() {
        return 1;
    }
    @Override
    public boolean canInsert(ItemStack stack) {
        return equipmentSlot == MobEntity.getPreferredEquipmentSlot(stack);
    }
    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        ItemStack itemStack = this.getStack();
        return !itemStack.isEmpty() && !playerEntity.isCreative() && EnchantmentHelper.hasBindingCurse(itemStack) ? false : super.canTakeItems(playerEntity);
    }
    @Override
    public Pair<Identifier, Identifier> getBackgroundSprite() {
        return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot.getEntitySlotId()]);
    }
}
