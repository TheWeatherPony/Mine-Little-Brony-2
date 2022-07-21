package weatherpony.minelittlebrony2.entity.living.pony.provider;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import weatherpony.minelittlebrony2.MineLBrony2_FabricMod;
import weatherpony.minelittlebrony2.entity.living.PonySounds;
import weatherpony.minelittlebrony2.entity.living.SoundTypes;
import weatherpony.minelittlebrony2.entity.living.pony.PonyVoices;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IAmAPony;
import weatherpony.minelittlebrony2.entity.living.pony.logic.PonyLogic;
import weatherpony.minelittlebrony2.entity.living.pony.logic.PonyLogicFactory;

public class TestPonyLogicProvider extends PonyLogicFactory{
	public static final Identifier FACTORYRESOURCE = new Identifier(MineLBrony2_FabricMod.MODID,"test_logic_factory");
	static class TestLogic<PONY extends LivingEntity & IAmAPony> extends PonyLogic<PONY>{
		public TestLogic(TestPonyLogicProvider maker, String modID, int testPonyNumber) {
			super(maker);
			this.number = testPonyNumber;
			this.canWearArmor = testPonyNumber%2 ==0;
		}
		int number;
		void setupSkin() {
			this.setSkinWithDerivedData(new Identifier(MineLBrony2_FabricMod.MODID, "textures/entity/"+"test"+number+".png"));
		}
		public void setSound(PonySounds sounds){
			this.sounds = sounds;
		}
		@Override
		protected String getName(PONY pony){
			return "test"+this.number;
		}
		public int mainInventorySize(PONY pony){
			return 9 * ((this.number%4)+1);
		}
		@Override
		protected SoundEvent getSoundEvent_forPlaying(PONY pony, SoundTypes sound){
			//return this.sounds.get(sound,pony.isPoisoned(), pony.isChangeling());
			if(!pony.getEntityWorld().isClient) {
				Text message = MutableText.of(new LiteralTextContent("Debug: nearby test pony says ("+sound+",poison="+pony.isPoisoned()+",changeling="+pony.isChangeling()+")"));
				for(PlayerEntity eachInWorld : pony.getEntityWorld().getPlayers()){
					if(eachInWorld.distanceTo(pony) < 32)
						eachInWorld.sendMessage(message, false);
				}
			}
			return super.getSoundEvent_forPlaying(pony, sound);
		}
	}
	public <PONY extends LivingEntity & IAmAPony> TestPonyLogicProvider(){
		super(FACTORYRESOURCE);
		PonyVoices.INSTANCE.applyPonySound(MineLBrony2_FabricMod.MODID, "test", false);
		try{
			int logicCount = 6;
			this.testLogic = new TestLogic[logicCount];
			for(int i=0;i<logicCount;i++)
				(this.testLogic[i] = new TestLogic(this,MineLBrony2_FabricMod.MODID,i))
						.setSound(PonyVoices.INSTANCE.getSounds(MineLBrony2_FabricMod.MODID, "test"));
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("Failed to make test logic",e);
		}

	}
	@Override
	public void connect() {
		for(TestLogic e : this.testLogic)
			e.setupSkin();
	}
	private TestLogic[] testLogic; 
	@Override
	public String compressToString(PonyLogic logic){
		return ""+((TestLogic)logic).number;
	}
	@Override
	public PonyLogic expandToLogic(String logic){
		if("test".equals(logic))
			return this.getRandomLogic();
		try {
			return testLogic[Integer.parseInt(logic)];
		}catch(Throwable e) {
			return null;
		}
	}
	@Override
	public PonyLogic getRandomLogic(){
		return this.testLogic[RAND.nextInt(this.testLogic.length)];
	}
}
