package botControl;

public class BasicBot extends Bot {

	public BasicBot(int id, BotFactory factory) {
		super(id, factory,BotType.Basic);
		this.getMemory().put("Static", new Pair<Object,Boolean>(false,true));
	}

}
