package weatherpony.minelittlebrony2.mixins;

import com.minelittlepony.client.pony.Pony;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.ISitWithoutRiding;

@Mixin(Pony.class)
public class PonyEdit{
    @Inject(method = "isSitting", at = @At("HEAD"), cancellable = true,//(Lnet/minecraft/entity/LivingEntity;)Z
            require=1)
    public void isSitting_SitterAddition(LivingEntity entity, CallbackInfoReturnable<Boolean> cir){
        if(entity instanceof ISitWithoutRiding){
            ISitWithoutRiding sitter = (ISitWithoutRiding) entity;
            boolean sit = sitter.shouldRenderAsSitting();
            if(sit){
                cir.setReturnValue(true);
            }
        }
    }
}
