package weatherpony.minelittlebrony2;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import weatherpony.minelittlebrony2.entity.living.ai.*;
import weatherpony.minelittlebrony2.entity.living.pony.LogicFactories;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IAmAPony;

import weatherpony.minelittlebrony2.entity.living.pony.provider.TestPonyLogicProvider;

import java.util.concurrent.Callable;


public class MineLBrony2_FabricMod<PONY extends LivingEntity&IAmAPony> implements ModInitializer {
    public static final String MODID = "mlbrony";
    public static final Logger LOGGER = LogManager.getLogger("minelbrony2");
    public static final Identifier PONY_INVENTORY_OPEN_ID = new Identifier(MODID, "ponyinv");
    public static final Identifier PONY_ENITY_ID = new Identifier(MODID,"pony");
    public static final Identifier PONY_SPAWN_EGG_ID = new Identifier(MODID,"pony_spawn_egg");
    public static PonyAIProvider.PonyAIHolderUnit AICREATOR;//= new PonyAICreator();
    public static void registerLogicFactory(){
        LogicFactories.registerFactory(new TestPonyLogicProvider());
        postInit();//FIXME -- definitely not the right place...
    }
    public static void postInit(){
        LogicFactories.postInit();
    }
    public static MineLBrony2_FabricMod INSTANCE;
    public MineLBrony2_FabricMod(){
        if(INSTANCE != null)
            throw new IllegalStateException();
        INSTANCE = this;
    }
    @Override
    public void onInitialize() {


        PonyAIProviderCollection collection = new PonyAIProviderCollection();
        collection.add(new PonyAIProviderBase());
        collection.add(new ChangelingOffense());
        //collection.add(new PonyAITamable2()); //can't... scala-side
        try{
            collection.add((PonyAIProvider) MineLBrony2_FabricMod.class.getClassLoader().loadClass("weatherpony.minelittlebrony2.entity.living.ai.PonyAITamable2").newInstance());
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
        collection.add(new PonyAIProvider_openInventory());

        AICREATOR = collection;

        if(minelpAlertNeeded != null)
            try{minelpAlertNeeded.call();} catch (Exception e) {
                throw new RuntimeException(e);
            }
    }
    private static Class ponyClass;
    static EntityType ponyType;
    static SpawnEggItem ponySpawnEgg;
    static Callable<Void> minelpAlertNeeded;
    static Callable clientAlertNeeded;
    public static <PONY extends LivingEntity&IAmAPony> void giveFromScala(Class<PONY> ponies, DefaultAttributeContainer.Builder attributes){
        if(ponyClass != null)
            throw new IllegalStateException();
        if((ponyClass = ponies) == null)
            throw new NullPointerException();
        if(clientAlertNeeded != null)
            try{clientAlertNeeded.call();}catch(Throwable e){}

        ponyType = Registry.register(
                Registry.ENTITY_TYPE,
                PONY_ENITY_ID,
                FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<PONY>) (type, world) -> {
                    PONY ret;
                    try {
                        ret = (PONY) ponyClass.getConstructor(EntityType.class,World.class).newInstance(type,world);
                    } catch (Throwable e){
                        LOGGER.atFatal().log("Failed to create a pony",e);
                        throw new RuntimeException(e);
                    }
                    return ret;
                }).dimensions(EntityDimensions.fixed(0.75f, 1.75f)).build()
        );
        FabricDefaultAttributeRegistry.register(ponyType, attributes);
        ponySpawnEgg = new SpawnEggItem(ponyType,0,0xffffff,(new Item.Settings()).group(ItemGroup.MISC));
        Registry.register(Registry.ITEM, PONY_SPAWN_EGG_ID, ponySpawnEgg);

        /*
        FabricDefaultAttributeRegistry.register(ponyType, attributes);
        ArrayList<Biome> hostileBiomes = new ArrayList();
        hostileBiomes.add(Biomes.HELL);
        hostileBiomes.add(Biomes.SKY);

        for(Biome each : Biome.REGISTRY){
            if(!hostileBiomes.contains(each))
                EntityRegistry.addSpawn(ponyClass, 2, 1, 3, EnumCreatureType.CREATURE, each);
        }*/
        /*
        RenderingRegistry.registerEntityRenderingHandler(pclass, DEVELOPMENT ?
                new IRenderFactory() {
                    @Override
                    public Render createRenderFor(RenderManager manager){
                        return new RenderBipedPony(manager);
                    }
                }
                :new IRenderFactory(){
            @Override
            public Render createRenderFor(RenderManager manager){
                return new RenderBronyPony(manager);
            }
        });
        */
    }
}
