package weatherpony.minelittlebrony2;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import weatherpony.minelittlebrony2.client.render.MLBronyPonyRenderer;

import java.util.concurrent.Callable;

public class MineLBrony2_MineLPMod implements ModInitializer, ClientModInitializer {
    void initialize(){
        Callable alert = () -> {
            MineLBrony2_FabricMod.registerLogicFactory();
            return null;
        };
        if(MineLBrony2_FabricMod.INSTANCE != null) {
            try {
                alert.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            MineLBrony2_FabricMod.minelpAlertNeeded = alert;
        }
    }
    @Override
    public void onInitialize() {
        initialize();
    }

    @Override
    public void onInitializeClient() {
        initialize();
    }
}
