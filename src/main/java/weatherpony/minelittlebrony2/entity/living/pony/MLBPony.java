package weatherpony.minelittlebrony2.entity.living.pony;

import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.client.pony.Pony;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.ISitWithoutRiding;
import weatherpony.minelittlebrony2.util.ImageUtil;

import java.lang.reflect.Constructor;

public class MLBPony {//extends Pony {

    public static IPonyData makeData_fromPixels(Identifier skin){
        try {
            Class NPD = MLBPony.class.getClassLoader().loadClass("com.minelittlepony.client.pony.NativePonyData");
            Constructor NPDC = NPD.getDeclaredConstructor(NativeImage.class);
            NPDC.setAccessible(true);
            return (IPonyData) NPDC.newInstance(ImageUtil.getNativeFromResource(skin));
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }
    public MLBPony(Identifier skin){
        //super(skin,makeData_fromPixels(skin));
    }
    /*
    @Override
    public boolean isSitting(LivingEntity entity){
        boolean norm = super.isSitting(entity);
        if(entity instanceof ISitWithoutRiding){
            ISitWithoutRiding sitter = (ISitWithoutRiding) entity;
            return sitter.shouldRenderAsSitting() || norm;
        }
        return norm;
    }
    */
}
