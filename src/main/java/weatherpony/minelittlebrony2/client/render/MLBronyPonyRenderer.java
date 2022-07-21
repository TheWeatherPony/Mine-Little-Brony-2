package weatherpony.minelittlebrony2.client.render;

import com.minelittlepony.api.model.IUnicorn;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.render.entity.PonyRenderer;
import com.minelittlepony.mson.api.ModelKey;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import weatherpony.minelittlebrony2.MineLBrony2_FabricMod;
import weatherpony.minelittlebrony2.entity.living.PonyLogicReasoning;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IAmAPony;

import java.util.HashMap;

public class MLBronyPonyRenderer<
        PONY extends MobEntity& IAmAPony,
        M extends ClientPonyModel<PONY> & IUnicorn>
            extends PonyRenderer.Caster<PONY, M>{
    public static final boolean skinnyArms = false;
    public MLBronyPonyRenderer(EntityRendererFactory.Context context) {
        super(context, (ModelKey<? super M>) ModelType.EARTH_PONY.getKey(skinnyArms));//earth, just so it has something, otherwise it's a NPE

        this.models = new HashMap();
        this.models.put(Race.EARTH,this.manager.getModelWrapper());
        for(Race e : Race.values()){
            if(e == Race.HUMAN)
                continue;
            if(e == Race.EARTH)
                continue;
            try {
                this.models.put(e, manager.setModel(ModelType.getPlayerModel(e).getKey(skinnyArms)));
            }catch(NullPointerException e2){
                MineLBrony2_FabricMod.LOGGER.error("Racial Error: "+e);
            }
        }
    }
    private HashMap<Race, ModelWrapper<PONY, M>> models;

    @Override
    public Identifier findTexture(PONY pony) {
        return pony.getMyLogic(PonyLogicReasoning.VISUAL).getSkin();
        //return baseTextures.supplyTexture(villager);
    }
    @Override
    public IPony getEntityPony(PONY pony){
        IPony ret =  pony.getMyLogic(PonyLogicReasoning.VISUAL).getPonyTypeInfo();

        this.model = this.manager.setModel(this.models.get(ret.getRace(true))).body();

        return ret;
    }
}
