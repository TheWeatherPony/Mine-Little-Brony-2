package weatherpony.minelittlebrony2.entity.living.ai;


import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import weatherpony.minelittlebrony2.entity.living.PonyLogicReasoning;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IAmAPony;
import java.util.function.Function;

/**
 * This implementation provides the ponies with an AI created by mixing the AI for wolves, villagers, and a couple tidbits from others
 * MC 1.17/Fabric update: reworked to more closely mirror villager AI format, now that villager AI has been reworked. Hooks still available for other implementations to use the traditional AI form
 */
public class PonyAIProviderBase<PONY extends LivingEntity&IAmAPony> extends PonyAIProvider<PONY>{

	public void initAI(PONY pony){//FIXME - stop with the assumptions, as they may not always be true in the future
		@SuppressWarnings("unchecked")
		GoalSelector gs = pony.pony_getGoalSelector();

		MobEntity pony2 = (MobEntity)pony;
		PathAwareEntity pony3 = (PathAwareEntity)pony;
		((MobNavigation)pony2.getNavigation()).setCanPathThroughDoors(true);
		pony2.getNavigation().setCanSwim(true);
		pony2.setCanPickUpLoot(true);
		gs.add(1, new SwimGoal(pony2));
		//sitting task level 2
		gs.add(3, new FleeEntityGoal(pony3, ZombieEntity.class, 8.0F, 1D, 1.375D));//in truth, idk what movement speed numbers to use
		gs.add(3, new FleeEntityGoal(pony3, EvokerEntity.class, 12.0F, 0.8D, 0.8D));//TODO - movement speeds
		gs.add(3, new FleeEntityGoal(pony3, VindicatorEntity.class, 8.0F, 0.8D, 0.8D));
		gs.add(3, new FleeEntityGoal(pony3, VexEntity.class, 8.0F, 0.6D, 0.6D));
		//gs.add(4, new EntityAIMoveIndoors(pony3));//TODO
		((MobNavigation) pony2.getNavigation()).setCanPathThroughDoors(true);
		((MobNavigation) pony2.getNavigation()).setCanEnterOpenDoors(true);
		//gs.add(5, new EntityAIRestrictOpenDoor(pony3));
		gs.add(6, new LongDoorInteractGoal(pony2, true));//EntityAIOpenDoor
		gs.add(7, new GoToWalkTargetGoal(pony3, 0.8));
		gs.add(8, new StopAndLookAtEntityGoal(pony2, PlayerEntity.class, 3.0F, 1.0F));
		gs.add(9, new StopAndLookAtEntityGoal(pony2, VillagerEntity.class, 5.0F, 0.02F));
		gs.add(10, new WanderAroundFarGoal(pony3, .6D, 0.02f));
		gs.add(11, new LookAtEntityGoal(pony2, LivingEntity.class, 8.0F));
		gs.add(12, new EscapeDangerGoal(pony3, 1.25D));//EntityAIPanic -.-
		//follow owner task level 12
	}
	public Function<PONY,Void> applyEntityAttributes(){
		return (pony)->{
			return null;
		};
	}
	public void applyEntityAttributes(PONY pony) {
		pony.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(3);
	}
	public void applyAttributes_late(PONY pony){
		AttributeContainer map = pony.getAttributes();
		map.getCustomInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(40.0D);
		map.getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(pony.getMyLogic(PonyLogicReasoning.CAPABILITIES).movementSpeed(pony));
		map.getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(pony.getMyLogic(PonyLogicReasoning.CAPABILITIES).getMaxHealth(pony));
	}
	public void onDamaged(PONY pony){
		//well, I think it's a nice idea :P
		if(pony.isChangeling() && pony.isDisguised()) {
			pony.shockOutOfDisguise();
		}
	}
}
