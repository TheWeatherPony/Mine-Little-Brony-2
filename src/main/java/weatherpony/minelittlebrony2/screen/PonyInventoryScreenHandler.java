package weatherpony.minelittlebrony2.screen;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import weatherpony.minelittlebrony2.entity.living.PonyInventory2;
import weatherpony.minelittlebrony2.entity.living.PonyLogicReasoning;
import weatherpony.minelittlebrony2.entity.living.pony.PonyArmorSlot;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IAmAPony;
import weatherpony.minelittlebrony2.entity.living.pony.logic.PonyInventoryLogicStub;
import weatherpony.minelittlebrony2.util.ArmorEquipmentHelp;

import java.util.Arrays;

/**
 * This is an unholy amalgam of various vanilla ScreenHandlers. It must be worked with in a special way very similar to net.minecraft.screen.HorseScreenHandler, including custom packets to open it on the client
 */
public class PonyInventoryScreenHandler<PONY extends LivingEntity & IAmAPony> extends ScreenHandler {
    public final PonyInventory2<PONY> inventory;
    public final PonyInventoryLogicStub logicStub;
    public final PONY pony;
    public final PlayerEntity player;
    public final PlayerInventory playerInv;
    public static <PONY extends LivingEntity & IAmAPony> PonyInventoryScreenHandler makeForServer(int syncId, PlayerInventory playerInventory, PONY pony){
        return new PonyInventoryScreenHandler(syncId, playerInventory, pony.getInventory(),pony, pony.getMyLogic(PonyLogicReasoning.CAPABILITIES));
    }
    public static <PONY extends LivingEntity & IAmAPony> PonyInventoryScreenHandler makeForClient(int syncId, PlayerInventory playerInventory, @Nullable PONY pony, int inventoryPayload){
        PonyInventoryLogicStub logic = new PonyInventoryLogicStub.PonyLogicStublet(inventoryPayload);
        PonyInventory2 inv = new PonyInventory2(logic, pony);
        inv.enforcePonyProperties();
        return new PonyInventoryScreenHandler(syncId, playerInventory, inv, pony, logic);
    }
    private static final int mainWidth=9;
    public PonyInventoryLogicStub logic;
    private PonyInventoryScreenHandler(int syncId, PlayerInventory playerInventory, PonyInventory2<PONY> inv, PONY pony, PonyInventoryLogicStub logic){
        super((ScreenHandlerType)null, syncId);
        this.logic = logic;
        this.logicStub = logic==null?pony.getMyLogic(PonyLogicReasoning.CAPABILITIES):logic;
        this.inventory = inv;
        this.pony = pony;
        this.playerInv = playerInventory;
        this.player = playerInventory.player;
        inv.onOpen(playerInventory.player);
        int ponyInvCount=inv.main.size();
        int rows=(ponyInvCount/mainWidth) + (ponyInvCount % mainWidth == 0 ? 0 : 1);
        int[] cols = new int[rows];
        Arrays.fill(cols, mainWidth);
        if(cols.length>0) {
            int ov=ponyInvCount%9;
            cols[cols.length-1]=(ov==0)?mainWidth:ov;
        }
        System.out.print(cols);
        EquipmentSlot[] armors = ArmorEquipmentHelp._get();
        //lower player inv
        for (int l = 0; l < 9; l++){

            addSlot(new Slot(this.playerInv, l, 8 + l * 18, 142));
        }
        //main player inv
        for (int row = 0; row < 3; row++){
            for (int col = 0; col < 9; col++){
                addSlot(new Slot(this.playerInv, col + (row+1) * 9, 8 + col * 18, 84 + row * 18));
            }
        }
        //player armor
        for (int i = 0; i < armors.length; i++){
            this.addSlot(new PonyArmorSlot(inventory, this.playerInv.size() - 1 -1 - i, 174, 88 + i * 18, armors[i]));
        }
        //player offhand - needs relocating
        /*this.addSlot(new Slot(this.playerInv, 40, 77, 62) {
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT);
            }
        });*/
        //main pony inv
        for (int row = 0; row < rows; row++){
            for (int col = 0; col < cols[row]; col++){
                addSlot(new Slot(this.inventory, col + row * 9, 8 + col * 18, 17 + row * 18));
            }
        }

        if (this.logicStub.canWearArmor()){
            //pony armor
            for (int i = 0; i < armors.length; i++){
                addSlot(new PonyArmorSlot(this.inventory, this.inventory.size() - 1 - i, 174, 17 + i * 18, armors[i]));
            }
        }
        //TODO - hmm... should offhand be used for ponies? shown for players? (yes)
        //pony offhand - may not exist, needs relocating, bad index number
        /*this.addSlot(new Slot(inventory, 40, 77, 62) {
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT);
            }
        });*/
    }
    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.isUsableByPlayer(player);
    }
    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            int i = this.inventory.size();
            if (index < i) {
                if (!this.insertItem(itemStack2, i, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(1).canInsert(itemStack2) && !this.getSlot(1).hasStack()) {
                if (!this.insertItem(itemStack2, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(0).canInsert(itemStack2)) {
                if (!this.insertItem(itemStack2, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (i <= 2 || !this.insertItem(itemStack2, 2, i, false)) {
                int k = i + 27;
                int m = k + 9;
                if (index >= k && index < m) {
                    if (!this.insertItem(itemStack2, i, k, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= i && index < k) {
                    if (!this.insertItem(itemStack2, k, m, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.insertItem(itemStack2, k, k, false)) {
                    return ItemStack.EMPTY;
                }

                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return itemStack;
    }
    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.onClose(player);
    }
}
