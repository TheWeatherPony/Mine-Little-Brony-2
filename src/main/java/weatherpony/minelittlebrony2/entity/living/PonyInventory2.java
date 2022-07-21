package weatherpony.minelittlebrony2.entity.living;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IAmAPony;
import weatherpony.minelittlebrony2.entity.living.pony.logic.PonyInventoryLogicStub;
import weatherpony.minelittlebrony2.entity.living.pony.logic.PonyLogic;
import weatherpony.minelittlebrony2.util.ArmorEquipmentHelp;
import weatherpony.minelittlebrony2.util.ResizableDefaultedList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * A pony's inventory. Most of this code is blatantly stolen from PlayerInventory
 * @param <PONY> the sole final version of the Pony class
 */
public class PonyInventory2<PONY extends LivingEntity & IAmAPony> implements Inventory, Nameable {
    public static final int NOT_FOUND = -1;
    protected static int startingMainInvSize = 36;

    public final PONY pony;

    public final ResizableDefaultedList<ItemStack> main;
    public final ResizableDefaultedList<ItemStack> armor;
    public final ResizableDefaultedList<ItemStack> offHand;
    private final List<DefaultedList<ItemStack>> combinedInventory;
    private int mainHand = NOT_FOUND;
    private int changeCount;
    private PonyInventoryLogicStub overridingLogic;
    public PonyInventory2(PonyInventoryLogicStub logic, PONY pony){
        this.main = ResizableDefaultedList.ofSize(startingMainInvSize, ItemStack.EMPTY);
        this.armor = ResizableDefaultedList.ofSize(ArmorEquipmentHelp._get().length, ItemStack.EMPTY);
        this.offHand = ResizableDefaultedList.ofSize(1, ItemStack.EMPTY);
        this.combinedInventory = ImmutableList.of(this.main, this.armor, this.offHand);
        this.pony = pony;
        this.overridingLogic = logic;
    }
    public PonyInventory2(PONY pony) {
        this(null, pony);
    }
    public List<ItemStack> enforcePonyProperties(){
        List<ItemStack> ret = new ArrayList<>();
        if(this.pony == null)
            return ret;
        PonyInventoryLogicStub logic = overridingLogic != null ? overridingLogic : pony.getMyLogic(PonyLogicReasoning.CAPABILITIES);
        ret.addAll(this.main.resize(logic.mainInventorySize(this.pony)));
        if(!logic.canWearArmor()){
            ret.addAll(this.armor.resize(0));
        }
        if(!logic.canDualWield()){
            ret.addAll(this.offHand.resize(0));
        }
        return ret;
    }
    public ItemStack getOffhandStack(){
        return offHand.size() > 0 ? this.offHand.get(0) : ItemStack.EMPTY;
    }
    public ItemStack getMainHandStack() {
        return this.isValidMainInvIndex(this.mainHand) ? (ItemStack)this.main.get(this.mainHand) : ItemStack.EMPTY;
    }
    public int getMainInvSize(){
        return this.main.size();
    }
    private boolean canStackAddMore(ItemStack existingStack, ItemStack stack) {
        return !existingStack.isEmpty() && ItemStack.canCombine(existingStack, stack) && existingStack.isStackable() && existingStack.getCount() < existingStack.getMaxCount() && existingStack.getCount() < this.getMaxCountPerStack();
    }
    public void clearMainHand(){
        this.mainHand = -1;
    }

    public int getEmptySlot() {
        for(int i = 0; i < this.main.size(); ++i) {
            if (((ItemStack)this.main.get(i)).isEmpty()) {
                return i;
            }
        }

        return -1;
    }
    public boolean isValidMainInvIndex(int slot) {
        return slot >= 0 && slot < this.main.size();
    }
    public int getSlotWithStack(ItemStack stack) {
        for(int i = 0; i < this.main.size(); ++i) {
            if (!((ItemStack)this.main.get(i)).isEmpty() && ItemStack.canCombine(stack, (ItemStack)this.main.get(i))) {
                return i;
            }
        }

        return -1;
    }
    /**
     * Given the item stack to search for, returns the equivalent slot index with a matching stack that is all of:
     * not damaged, not enchanted, and not renamed.
     *
     * @return the index where a matching stack was found, or {@value #NOT_FOUND}
     */
    public int indexOf(ItemStack stack) {
        for(int i = 0; i < this.main.size(); ++i) {
            ItemStack itemStack = (ItemStack)this.main.get(i);
            if (!((ItemStack)this.main.get(i)).isEmpty() && ItemStack.canCombine(stack, (ItemStack)this.main.get(i)) && !((ItemStack)this.main.get(i)).isDamaged() && !itemStack.hasEnchantments() && !itemStack.hasCustomName()) {
                return i;
            }
        }

        return -1;
    }
/*public int remove(Predicate<ItemStack> shouldRemove, int maxCount, Inventory craftingInventory) {
      int i = 0;
      boolean bl = maxCount == 0;
      int i = i + Inventories.remove((Inventory)this, shouldRemove, maxCount - i, bl);
      i += Inventories.remove(craftingInventory, shouldRemove, maxCount - i, bl);
      ItemStack itemStack = this.player.currentScreenHandler.getCursorStack();
      i += Inventories.remove(itemStack, shouldRemove, maxCount - i, bl);
      if (itemStack.isEmpty()) {
         this.player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
      }

      return i;
   }*/
    private int addStack(ItemStack stack) {
        int i = this.getOccupiedSlotWithRoomForStack(stack);
        if (i == -1) {
            i = this.getEmptySlot();
        }

        return i == -1 ? stack.getCount() : this.addStack(i, stack);
    }
    private int addStack(int slot, ItemStack stack) {
        Item item = stack.getItem();
        int i = stack.getCount();
        ItemStack itemStack = this.getStack(slot);
        if (itemStack.isEmpty()) {
            itemStack = new ItemStack(item, 0);
            if (stack.hasNbt()) {
                itemStack.setNbt(stack.getNbt().copy());
            }

            this.setStack(slot, itemStack);
        }

        int j = i;
        if (i > itemStack.getMaxCount() - itemStack.getCount()) {
            j = itemStack.getMaxCount() - itemStack.getCount();
        }

        if (j > this.getMaxCountPerStack() - itemStack.getCount()) {
            j = this.getMaxCountPerStack() - itemStack.getCount();
        }

        if (j == 0) {
            return i;
        } else {
            i -= j;
            itemStack.increment(j);
            itemStack.setBobbingAnimationTime(5);
            return i;
        }
    }
    public int getOccupiedSlotWithRoomForStack(ItemStack stack) {
        if (this.canStackAddMore(this.getStack(this.mainHand), stack)) {
            return this.mainHand;
        } else if (this.canStackAddMore(this.getStack(40), stack)) {
            return 40;
        } else {
            for(int i = 0; i < this.main.size(); ++i) {
                if (this.canStackAddMore((ItemStack)this.main.get(i), stack)) {
                    return i;
                }
            }

            return -1;
        }
    }
    public void updateItems() {
        Iterator var1 = this.combinedInventory.iterator();

        while(var1.hasNext()) {
            DefaultedList<ItemStack> defaultedList = (DefaultedList)var1.next();

            for(int i = 0; i < defaultedList.size(); ++i) {
                if (!((ItemStack)defaultedList.get(i)).isEmpty()) {
                    ((ItemStack)defaultedList.get(i)).inventoryTick(this.pony.world, this.pony, i, this.mainHand == i);
                }
            }
        }

    }

    public boolean insertStack(ItemStack stack) {
        return this.insertStack(-1, stack);
    }
    public boolean insertStack(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        } else {
            try {
                if (stack.isDamaged()) {
                    if (slot == -1) {
                        slot = this.getEmptySlot();
                    }

                    if (slot >= 0) {
                        this.main.set(slot, stack.copy());
                        ((ItemStack)this.main.get(slot)).setBobbingAnimationTime(5);
                        stack.setCount(0);
                        return true;
                    /*} else if (this.player.getAbilities().creativeMode) {
                        stack.setCount(0);
                        return true;*/
                    } else {
                        return false;
                    }
                } else {
                    int i;
                    do {
                        i = stack.getCount();
                        if (slot == -1) {
                            stack.setCount(this.addStack(stack));
                        } else {
                            stack.setCount(this.addStack(slot, stack));
                        }
                    } while(!stack.isEmpty() && stack.getCount() < i);

                    /*if (stack.getCount() == i && this.player.getAbilities().creativeMode) {
                        stack.setCount(0);
                        return true;
                    } else {*/
                        return stack.getCount() < i;
                    //}
                }
            } catch (Throwable var6) {
                CrashReport crashReport = CrashReport.create(var6, "Adding item to inventory");
                CrashReportSection crashReportSection = crashReport.addElement("Item being added");
                crashReportSection.add("Item ID", (Object)Item.getRawId(stack.getItem()));
                crashReportSection.add("Item data", (Object)stack.getDamage());
                crashReportSection.add("Item name", () -> {
                    return stack.getName().getString();
                });
                throw new CrashException(crashReport);
            }
        }
    }
    public void offerOrDrop(ItemStack stack) {
        this.offer(stack, true);
    }
    public void offer(ItemStack stack, boolean notifiesClient) {
        while(true) {
            if (!stack.isEmpty()) {
                int i = this.getOccupiedSlotWithRoomForStack(stack);
                if (i == -1) {
                    i = this.getEmptySlot();
                }

                if (i != -1) {
                    int j = stack.getMaxCount() - this.getStack(i).getCount();
                    if (this.insertStack(i, stack.split(j)) && !this.pony.world.isClient()){
                        //((ServerPlayerEntity)this.player).networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, 0, i, this.getStack(i)));
                        //FIXME -
                    }
                    continue;
                }

                this.pony.dropItem(stack, false, false);
            }

            return;
        }
    }
    @Override
    public ItemStack removeStack(int slot, int amount) {
        List<ItemStack> list = null;

        DefaultedList defaultedList;
        for(Iterator var4 = this.combinedInventory.iterator(); var4.hasNext(); slot -= defaultedList.size()) {
            defaultedList = (DefaultedList)var4.next();
            if (slot < defaultedList.size()) {
                list = defaultedList;
                break;
            }
        }

        return list != null && !((ItemStack)list.get(slot)).isEmpty() ? Inventories.splitStack(list, slot, amount) : ItemStack.EMPTY;
    }

    public void removeOne(ItemStack stack) {
        Iterator var2 = this.combinedInventory.iterator();

        while(true) {
            while(var2.hasNext()) {
                DefaultedList<ItemStack> defaultedList = (DefaultedList)var2.next();

                for(int i = 0; i < defaultedList.size(); ++i) {
                    if (defaultedList.get(i) == stack) {
                        defaultedList.set(i, ItemStack.EMPTY);
                        break;
                    }
                }
            }

            return;
        }
    }
    @Override
    public ItemStack removeStack(int slot) {
        DefaultedList<ItemStack> defaultedList = null;

        DefaultedList defaultedList2;
        for(Iterator var3 = this.combinedInventory.iterator(); var3.hasNext(); slot -= defaultedList2.size()) {
            defaultedList2 = (DefaultedList)var3.next();
            if (slot < defaultedList2.size()) {
                defaultedList = defaultedList2;
                break;
            }
        }

        if (defaultedList != null && !((ItemStack)defaultedList.get(slot)).isEmpty()) {
            ItemStack itemStack = (ItemStack)defaultedList.get(slot);
            defaultedList.set(slot, ItemStack.EMPTY);
            return itemStack;
        } else {
            return ItemStack.EMPTY;
        }
    }
@Override
    public void setStack(int slot, ItemStack stack) {
        DefaultedList<ItemStack> defaultedList = null;

        DefaultedList defaultedList2;
        for(Iterator var4 = this.combinedInventory.iterator(); var4.hasNext(); slot -= defaultedList2.size()) {
            defaultedList2 = (DefaultedList)var4.next();
            if (slot < defaultedList2.size()) {
                defaultedList = defaultedList2;
                break;
            }
        }

        if (defaultedList != null) {
            defaultedList.set(slot, stack);
        }

    }
    public float getBlockBreakingSpeed(BlockState block) {
        return ((ItemStack)this.getMainHandStack()).getMiningSpeedMultiplier(block);
    }
    public NbtList writeNbt(NbtCompound nbtroot) {
        int k;
        NbtCompound nbtCompound3;
        NbtList nbtList =new NbtList();
        nbtroot.put("main",nbtList);
        for(k = 0; k < this.main.size(); ++k) {
            if (!((ItemStack)this.main.get(k)).isEmpty()) {
                nbtCompound3 = new NbtCompound();
                nbtCompound3.putByte("Slot", (byte)k);
                ((ItemStack)this.main.get(k)).writeNbt(nbtCompound3);
                nbtList.add(nbtCompound3);
            }
        }

        nbtList =new NbtList();
        nbtroot.put("armor",nbtList);
        for(k = 0; k < this.armor.size(); ++k) {
            if (!((ItemStack)this.armor.get(k)).isEmpty()) {
                nbtCompound3 = new NbtCompound();
                nbtCompound3.putByte("Slot", (byte)(k/* + 100*/));
                ((ItemStack)this.armor.get(k)).writeNbt(nbtCompound3);
                nbtList.add(nbtCompound3);
            }
        }

        nbtList =new NbtList();
        nbtroot.put("offhand",nbtList);
        for(k = 0; k < this.offHand.size(); ++k) {
            if (!((ItemStack)this.offHand.get(k)).isEmpty()) {
                nbtCompound3 = new NbtCompound();
                nbtCompound3.putByte("Slot", (byte)(k/* + 150*/));
                ((ItemStack)this.offHand.get(k)).writeNbt(nbtCompound3);
                nbtList.add(nbtCompound3);
            }
        }

        return nbtList;
    }
    public void readNbt(NbtCompound nbtroot) {
        this.main.clear();
        this.armor.clear();
        this.offHand.clear();
        List<ItemStack> awkward = new ArrayList();
        NbtList nbtList = nbtroot.getList("main", 9);//seriously... why do we still need to tell the getList method that the NbtList we want is an NbtList? (the "9")
        for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 255;
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
            if (!itemStack.isEmpty()) {
                if (j >= 0 && j < this.main.size()) {
                    this.main.set(j, itemStack);
                } else {
                    awkward.add(itemStack);
                }
            }
        }
        nbtList = nbtroot.getList("armor", 9);//seriously... why do we still need to tell the getList method that the NbtList we want is an NbtList? (the "9")
        for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 255;
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
            if (!itemStack.isEmpty()) {
                if (j >= 0 && j < this.armor.size()) {
                    this.armor.set(j, itemStack);
                }
            }
        }
        nbtList = nbtroot.getList("armor", 9);//seriously... why do we still need to tell the getList method that the NbtList we want is an NbtList? (the "9")
        for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
            if (!itemStack.isEmpty()) {
                this.offHand.set(0, itemStack);
            }
        }
        if(!awkward.isEmpty()) {
            //oh boy...
            for(ItemStack e : awkward)
                this.offerOrDrop(e);
        }
    }
    @Override
    public int size() {
        return this.main.size() + this.armor.size() + this.offHand.size();
    }
    @Override
    public boolean isEmpty() {
        Iterator var1 = this.main.iterator();

        ItemStack itemStack3;
        do {
            if (!var1.hasNext()) {
                var1 = this.armor.iterator();

                do {
                    if (!var1.hasNext()) {
                        var1 = this.offHand.iterator();

                        do {
                            if (!var1.hasNext()) {
                                return true;
                            }

                            itemStack3 = (ItemStack)var1.next();
                        } while(itemStack3.isEmpty());

                        return false;
                    }

                    itemStack3 = (ItemStack)var1.next();
                } while(itemStack3.isEmpty());

                return false;
            }

            itemStack3 = (ItemStack)var1.next();
        } while(itemStack3.isEmpty());

        return false;
    }
    @Override
    public ItemStack getStack(int slot) {
        List<ItemStack> list = null;

        DefaultedList defaultedList;
        for(Iterator var3 = this.combinedInventory.iterator(); var3.hasNext(); slot -= defaultedList.size()) {
            defaultedList = (DefaultedList)var3.next();
            if (slot < defaultedList.size()) {
                list = defaultedList;
                break;
            }
        }

        return list == null ? ItemStack.EMPTY : (ItemStack)list.get(slot);
    }
    @Override
    public Text getName() {
        return (this.pony != null) ? this.pony.getName() : Text.translatable("container.inventory");
    }
    public ItemStack getArmorStack(int slot) {
        return this.armor.get(slot);
    }
    public void damageArmor(DamageSource damageSource, float amount, int[] slots) {
        if (!(amount <= 0.0F)) {
            amount /= 4.0F;
            if (amount < 1.0F) {
                amount = 1.0F;
            }

            int[] var4 = slots;
            int var5 = slots.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                int i = var4[var6];
                ItemStack itemStack = (ItemStack)this.armor.get(i);
                if ((!damageSource.isFire() || !itemStack.getItem().isFireproof()) && itemStack.getItem() instanceof ArmorItem) {
                    itemStack.damage((int)amount, (LivingEntity)this.pony, (Consumer)((player) -> {
                        //player.sendEquipmentBreakStatus(EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, i));
                    }));
                }
            }

        }
    }
    public void dropAll() {
        Iterator var1 = this.combinedInventory.iterator();

        while(var1.hasNext()) {
            List<ItemStack> list = (List)var1.next();

            for(int i = 0; i < list.size(); ++i) {
                ItemStack itemStack = (ItemStack)list.get(i);
                if (!itemStack.isEmpty()) {
                    this.pony.dropItem(itemStack, true, false);
                    list.set(i, ItemStack.EMPTY);
                }
            }
        }

    }
    @Override
    public void markDirty() {
        ++this.changeCount;
    }

    public int getChangeCount() {
        return this.changeCount;
    }
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if(pony == null)
            return true;
        if (this.pony.isRemoved())
            return false;
        return !(player.squaredDistanceTo(this.pony) > 64.0D);
    }
    public boolean contains(ItemStack stack) {
        Iterator var2 = this.combinedInventory.iterator();

        while(var2.hasNext()) {
            List<ItemStack> list = (List)var2.next();
            Iterator var4 = list.iterator();

            while(var4.hasNext()) {
                ItemStack itemStack = (ItemStack)var4.next();
                if (!itemStack.isEmpty() && itemStack.isItemEqualIgnoreDamage(stack)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean contains(TagKey<Item> tag) {
        Iterator var2 = this.combinedInventory.iterator();

        while(var2.hasNext()) {
            List<ItemStack> list = (List)var2.next();
            Iterator var4 = list.iterator();

            while(var4.hasNext()) {
                ItemStack itemStack = (ItemStack)var4.next();
                if (!itemStack.isEmpty() && itemStack.isIn(tag)) {
                    return true;
                }
            }
        }

        return false;
    }
    public void clone(PonyInventory2 other) {
        for(int i = 0; i < this.size(); ++i) {
            this.setStack(i, other.getStack(i));
        }

        this.mainHand = other.mainHand;
    }
    @Override
    public void clear() {
        Iterator var1 = this.combinedInventory.iterator();

        while(var1.hasNext()) {
            List<ItemStack> list = (List)var1.next();
            list.clear();
        }
    }
    public void populateRecipeFinder(RecipeMatcher finder) {
        Iterator var2 = this.main.iterator();

        while(var2.hasNext()) {
            ItemStack itemStack = (ItemStack)var2.next();
            finder.addUnenchantedInput(itemStack);
        }

    }

    public ItemStack dropSelectedItem(boolean entireStack) {
        ItemStack itemStack = this.getMainHandStack();
        return itemStack.isEmpty() ? ItemStack.EMPTY : this.removeStack(this.mainHand, entireStack ? itemStack.getCount() : 1);
    }
    public boolean isUsableByPlayer(PlayerEntity player)
    {
        //TODO - might need to check permission
        if (this.pony.isDead())
        {
            return false;
        }
        else
        {
            return player.squaredDistanceTo(this.pony) <= 64.0D;
        }
    }
}
