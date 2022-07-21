package weatherpony.minelittlebrony2.entity.living.pony.logic;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.pony.Pony;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import weatherpony.minelittlebrony2.MineLBrony2_FabricMod;
import weatherpony.minelittlebrony2.entity.living.PonySounds;
import weatherpony.minelittlebrony2.entity.living.SoundTypes;
import weatherpony.minelittlebrony2.entity.living.ai.InteractionUnit;
import weatherpony.minelittlebrony2.entity.living.pony.LogicFactories;
import weatherpony.minelittlebrony2.entity.living.pony.MLBPony;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IAmAPony;
import weatherpony.minelittlebrony2.util.ImageUtil;

public class PonyLogic<PONY extends LivingEntity & IAmAPony> implements PonyInventoryLogicStub<PONY>{
	public static String logicToString(PonyLogic<?> logic){
		if(logic == null)
			return "";
		return logic.maker.getFactoryIdentifier()+" "+logic.maker.compressToString(logic);
	}
	public static PonyLogic<?> stringToLogic(String logic){
		if(logic.isEmpty())
			return null;
		int space = logic.indexOf(' ');
		String factoryString = logic.substring(0, space);
		String logicString = logic.substring(space +1);
		System.out.println("factory='"+factoryString+"', logicString='"+logicString+"'");
		Identifier factoryRes = new Identifier(factoryString);
		PonyLogicFactory factory = LogicFactories.getFactoryByRes(factoryRes);
		return factory.expandToLogic(logicString);
	}
	public PonyLogic(final PonyLogicFactory maker){
		if(maker == null)
			throw new NullPointerException();
		this.maker = maker;
	}
	final PonyLogicFactory maker;
	
	protected boolean canFlyWell;
	Identifier skin;
	IPony ponyTypeInfo;
	protected boolean canWearArmor = false;
	@Override
	public final boolean canWearArmor() {
		return this.canWearArmor;
	}
	protected boolean canDualWield = false;
	@Override
	public final boolean canDualWield() {
		return this.canDualWield;
	}
	protected PonyLogic<PONY> setSkinWithDerivedData(Identifier skin){
		//FIXME - super prone to breaking, etc.
		IPonyData pd;
		Pony pny;
		try{
			pd = MLBPony.makeData_fromPixels(skin);
			Constructor<Pony> PC = Pony.class.getDeclaredConstructor(Identifier.class, IPonyData.class);
			PC.setAccessible(true);
			pny = PC.newInstance(skin, pd);

		} catch (Exception e) {
			MineLBrony2_FabricMod.LOGGER.error(skin.toString());
			throw new RuntimeException(e);
		}
		return this.setSkin(skin).setPonyData(pny);
	}
	protected PonyLogic<PONY> setSkin(final Identifier skin){
		this.skin = skin;
		return this;
	}
	protected PonyLogic setPonyData(final IPony data){
		this.canFlyWell = data.getRace(true).hasWings();
		this.ponyTypeInfo = data;
		return this;
	}
	public boolean canFlyWell(){
		return this.canFlyWell;
	}
	@Override
	public int mainInventorySize(PONY pony){
		return 9;
	}
	public double movementSpeed(PONY pony){
		return 0.5; //0.6
	}
	public double getMaxHealth(PONY pony){
		if(MineLBrony2_FabricMod.AICREATOR.<PONY>shouldBeConsidered_Tamed(pony))
			return 30;
		return 20;
	}
	public double getAttackDamage(PONY pony){
		Race race = this.ponyTypeInfo.getRace(true);
		double s = 2;
		if(MineLBrony2_FabricMod.AICREATOR.<PONY>shouldBeConsidered_Tamed(pony))
			s +=2;
		if(race == Race.ALICORN || race == Race.EARTH)
		      s *= 1.5;
		return s;
	}
	public float fallReduction_constant(PONY pony){
		return 3;
	}
	public float fallReduction_magnitude(PONY pony){
		if(this.canFlyWell)
			return 0;
		return 1;
	}
	public double getSizeMagnification(PONY pony){
		return 1;
	}
	public double getHeight(PONY pony){
		return 1.95 * getSizeMagnification(pony);
	}
	
	protected PonySounds sounds;
	protected SoundEvent getSoundEvent_forPlaying(PONY pony, SoundTypes sound){
		return this.sounds.get(sound,pony.isPoisoned(), pony.isChangeling());
	}
	public void playSound(PONY pony, SoundTypes sound){
		if(!pony.isSilent()){
			actuallyPlaySound(pony,sound);
		}
	}
	protected void actuallyPlaySound(PONY pony, SoundTypes sound){
		World world = pony.getEntityWorld();
		SoundEvent soundevent = getSoundEvent_forPlaying(pony, sound);
		if(world != null && soundevent != null) {
			world.playSoundFromEntity(null,
					pony,
					soundevent,
					SoundCategory.VOICE,
					this.getVoiceVolume(pony),
					this.getVoicePitch(pony));
		}
	}
	public float getVoiceVolume(PONY pony){
		return 1f;
	}
	public float getVoicePitch(PONY pony){
		return 1f;
	}
	public void setSize(PONY pony){
		double h = this.getHeight(pony);
		double w = 0.6 * getSizeMagnification(pony);
		//pony._setSize((float)w, (float)h);//FIXME
	}
	public Identifier getSkin(){
		return this.skin;
	}
	public IPony getPonyTypeInfo(){
		return this.ponyTypeInfo;
	}
	protected String getName(PONY pony){
		return null;
	}
	public String getDisplayName(PONY pony){
		if(pony.isChangeling() && pony.isDisguised()) {
			PonyLogic<PONY> disguise = pony.getActiveDisguiseLogic();
			if(this != disguise)
				return disguise.getDisplayName(pony);
		}
		return getName(pony);
	}
	public List<InteractionUnit<PONY>> getInteractions(){
		return new ArrayList(0);
	}
	public void onTick(PONY pony){
		
	}
	/**"What if... what if she exploded, and then, and then exploded again?!"</br>
	 * "Can you do that? Can you explode twice?"</br>
	 * Well, now you can :)
	*/
	public boolean explodeTwice(){
		return false;
	}
}
