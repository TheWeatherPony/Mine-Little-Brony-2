package weatherpony.minelittlebrony2;

import com.minelittlepony.mson.api.Mson;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import weatherpony.minelittlebrony2.client.gui.PonyInventoryHandledScreen;
import weatherpony.minelittlebrony2.client.render.MLBronyPonyRenderer;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IAmAPony;
import weatherpony.minelittlebrony2.screen.PonyInventoryScreenHandler;

import java.util.concurrent.Callable;

public class MineLBrony2_FabricClientMod<PONY extends LivingEntity & IAmAPony> implements ClientModInitializer {
    public static MineLBrony2_FabricClientMod INSTANCE;
    public MineLBrony2_FabricClientMod() {
        if(INSTANCE != null)
            throw new IllegalStateException("already created!");
        INSTANCE = this;
    }
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(MineLBrony2_FabricMod.PONY_INVENTORY_OPEN_ID, (client, handler, buf, responseSender) -> {
            int syncId = buf.readByte();
            int entityId = buf.readInt();
            int logicStub = buf.readInt();
            client.execute(() -> {
                ClientPlayerEntity clientPlayerEntity = client.player;
                @Nullable Entity entity = null;
                try {
                    entity = client.world.getEntityById(entityId);
                } catch (NullPointerException e) {
                    //swallow it
                }
                PonyInventoryScreenHandler phandler = PonyInventoryScreenHandler.makeForClient(syncId, clientPlayerEntity.getInventory(), (PONY) entity, logicStub);
                System.out.println("client opening pony inv. "+logicStub+": "+phandler.logic.mainInventorySize(null)+", "+phandler.inventory.main.size());
                clientPlayerEntity.currentScreenHandler = phandler;
                client.setScreen(new PonyInventoryHandledScreen(phandler));

            });
        });
        Callable alert = () -> {
            MineLBrony2_FabricClientMod.this.connectPonyFromScala();
            return null;
        };

        if(MineLBrony2_FabricMod.ponyType==null)
            MineLBrony2_FabricMod.clientAlertNeeded = alert;
        else
            try{alert.call();}catch(Throwable e){}
    }
    void connectPonyFromScala() {
        Mson.getInstance().getEntityRendererRegistry().registerEntityRenderer(MineLBrony2_FabricMod.ponyType, ctx -> new MLBronyPonyRenderer(ctx));
        //EntityRendererRegistry.INSTANCE.register(MineLBrony2_FabricMod.ponyType, (context) -> new MLBronyPonyRenderer(context));
    }
}
