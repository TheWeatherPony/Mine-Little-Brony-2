package weatherpony.minelittlebrony2.entity.living.pony.ducks;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.EntityNavigation;

public interface PossibleAI {
	public boolean keepsTrackOfAnAttackTargetForAI();
	public LivingEntity pony_getAttackTarget();
	public GoalSelector pony_getGoalSelector();
	public GoalSelector pony_getTargetSelector();
	public EntityNavigation pony_getNavigator();
}
