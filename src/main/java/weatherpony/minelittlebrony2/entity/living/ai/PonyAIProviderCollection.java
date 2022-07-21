package weatherpony.minelittlebrony2.entity.living.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.minecraft.entity.LivingEntity;
import weatherpony.minelittlebrony2.entity.living.ai.PonyAIProvider.PonyAIHolderUnit;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IAmAPony;
import weatherpony.minelittlebrony2.util.FunctionList;

public class PonyAIProviderCollection<PONY extends LivingEntity&IAmAPony> extends PonyAIHolderUnit<PONY>{
	private List<PonyAIProvider<PONY>> components = new ArrayList();
	private final List<Function<PONY,Void>> applyEntityAttributes = new ArrayList();
	private final Function<PONY,Void> applyEntityAttributesF = new FunctionList(applyEntityAttributes);
	private final List<Function<PONY,Void>> applyAttributes_late = new ArrayList();
	private final Function<PONY,Void> applyAttributes_lateF = new FunctionList(applyAttributes_late);
	private final List<InteractionUnit<PONY>> interactions = new ArrayList();
	private final List<Function<PONY,Void>> onDamaged = new ArrayList();
	private final Function<PONY,Void> onDamagedF = new FunctionList(onDamaged);
	private final List<Function<PONY,Void>> onRefreshEarly = new ArrayList();
	private final Function<PONY,Void> onRefreshEarlyF = new FunctionList(onRefreshEarly);
	private final List<Function<PONY,Void>> onRefreshMiddle = new ArrayList();
	private final Function<PONY,Void> onRefreshMiddleF = new FunctionList(onRefreshMiddle);
	private final List<Function<PONY,Void>> onRefreshLast = new ArrayList();
	private final Function<PONY,Void> onRefreshLastF = new FunctionList(onRefreshLast);
	private final List<Function<PONY,Void>> onTick = new ArrayList();
	private final Function<PONY,Void> onTickF = new FunctionList(onTick);
	private IAllianceProvider ap = null;
	private ISittingController sc = null;
	
	public void add(PonyAIProvider component){
		if(component instanceof PonyAIProviderCollection)
			throw new IllegalArgumentException("chained collections is overly inefficient");
		if(components.contains(component))
			throw new IllegalStateException("no double-registering components");
		if(component instanceof IAllianceProvider && ap != null)
			throw new IllegalArgumentException("only one alliance provider is allowed");
		if(component instanceof ISittingController && sc != null)
			throw new IllegalArgumentException("only one sitting controller is allowed");
		//no conflicts
		components.add(component);
		if(component instanceof IAllianceProvider)
			ap = (IAllianceProvider)component;
		if(component instanceof ISittingController)
			sc = (ISittingController)component;
		this.addFunction(this.applyEntityAttributes, component.applyEntityAttributes());
		this.addFunction(this.applyAttributes_late, component.applyAttributes_late());
		this.addFunction(this.onDamaged, component.onDamaged());
		this.addFunction(this.onRefreshEarly, component.on_refresh_early());
		this.addFunction(this.onRefreshMiddle, component.on_refresh_middle());
		this.addFunction(this.onRefreshLast, component.on_refresh_last());
		this.addFunction(this.onTick, component.onTick());
		List<InteractionUnit<PONY>> interacts = component.getInteractions();
		if(interacts!=null)
			this.interactions.addAll(interacts);
	}
	private void addFunction(List<Function<PONY,Void>> section, Function<PONY,Void> f){
		if(f!=null)
			section.add(f);
	}
	
	@Override
	public void initAI(PONY pony){
		for(PonyAIProvider<PONY> e:this.components){
			e.initAI(pony);
		}
	}
	@Override
	public Function<PONY,Void> applyEntityAttributes(){
		return this.applyEntityAttributesF;
	}
	@Override
	public Function<PONY,Void> applyAttributes_late(){
		return this.applyAttributes_lateF;
	}
	
	@Override
	public Function<PONY,Void> on_refresh_early(){
		return this.onRefreshEarlyF;
	}
	@Override
	public Function<PONY,Void> on_refresh_middle(){
		return this.onRefreshMiddleF;
	}
	@Override
	public Function<PONY,Void> on_refresh_last(){
		return this.onRefreshLastF;
	}
	
	@Override
	public List<InteractionUnit<PONY>> getInteractions(){
		return this.interactions;
	}
	@Override
	public Function<PONY,Void> onDamaged(){
		return this.onDamagedF;
	}
	@Override
	public Function<PONY,Void> onTick(){
		return this.onTickF;
	}
	@Override
	public boolean isWild(PONY pony){
		if(this.ap == null)
			return true;
		return this.ap.isWild(pony);
	}
	@Override
	public boolean hasPledgedAllegiance(PONY pony){
		if(this.ap == null)
			return false;
		return this.ap.hasPledgedAllegiance(pony);
	}
	@Override
	public LivingEntity getAllegianceOwner_singular(PONY pony){
		if(this.ap == null)
			return null;
		return this.ap.getAllegianceOwner_singular(pony);
	}
	@Override
	public boolean isAllied(PONY pony, LivingEntity other){
		if(this.ap == null)
			return false;
		return this.ap.isAllied(pony, other);
	}
	@Override
	public boolean shouldBeConsidered_Tamed(PONY pony){
		if(this.ap == null)
			return false;
		return this.ap.shouldBeConsidered_Tamed(pony);
	}
	
	@Override
	public boolean isSitting(PONY pony){
		if(this.sc == null)
			return false;
		return this.sc.isSitting(pony);
	}
}
