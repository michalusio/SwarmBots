package botControl;

public class StationBot extends Bot {

	public StationBot(int id, BotFactory factory) {
		super(id, factory,BotType.Station);
		this.getMemory().put("Static", new Pair<Object,Boolean>(true,true));
	}

}
