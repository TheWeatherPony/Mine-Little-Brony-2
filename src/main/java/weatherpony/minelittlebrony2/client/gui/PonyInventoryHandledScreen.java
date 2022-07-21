package weatherpony.minelittlebrony2.client.gui;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IAmAPony;
import weatherpony.minelittlebrony2.screen.PonyInventoryScreenHandler;

public class PonyInventoryHandledScreen<PONY extends LivingEntity & IAmAPony> extends HandledScreen<PonyInventoryScreenHandler<PONY>> {
    public PonyInventoryHandledScreen(PonyInventoryScreenHandler<PONY> screenHandler){
        super(screenHandler, screenHandler.playerInv, (screenHandler.pony != null) ? screenHandler.pony.getName() : MutableText.of(new LiteralTextContent("Unknown pony")));
    }
    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        //InventoryScreen.drawEntity()
    }
    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
    }
    @Override
    protected void init() {
        super.init();
    }
    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        super.drawForeground(matrices, mouseX, mouseY);
    }
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
    }

}
