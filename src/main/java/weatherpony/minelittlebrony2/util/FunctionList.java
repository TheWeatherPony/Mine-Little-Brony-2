package weatherpony.minelittlebrony2.util;

import java.util.List;
import java.util.function.Function;

public class FunctionList<INPUT> implements Function<INPUT,Void>{
	public FunctionList(List<Function<INPUT,Void>> l) {
		this.l = l;
	}
	private final List<Function<INPUT,Void>> l;
	@Override
	public Void apply(INPUT t) {
		for(Function<INPUT,Void> e : l) {
			e.apply(t);
		}
		return null;
	}
}
