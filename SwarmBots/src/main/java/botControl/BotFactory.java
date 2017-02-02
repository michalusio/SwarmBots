package botControl;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import collisions.CollisionShape;

public final class BotFactory {
	public SwarmCode Code;
	
	private final String Name;
	private final LinkedList<Integer> FreeIDs;
	private int MaxID;
	private final List<Bot> Bots;

	public boolean Pause;
	
	public BotFactory(String Name){
		this.Name=Name;
		FreeIDs=new LinkedList<Integer>();
		Bots=new ArrayList<Bot>();
		enlargeIDs(1000);
	}
	
	public final String getName(){
		return Name;
	}
	
	public final void enlargeIDs(int howMuch){
		for (int i=0;i<howMuch;i++){
			FreeIDs.addFirst(MaxID+i);
		}
		MaxID+=howMuch;
	}
	
	public final Bot getNewBot(BotType Type){
		if (FreeIDs.isEmpty()) enlargeIDs(1000);
		Bot b;
		switch(Type){
		case Basic:
			b=new BasicBot(FreeIDs.removeLast(),this);
			break;
		case Station:
			b=new StationBot(FreeIDs.removeLast(),this);
			break;
		default:
			return null;
		}
		Bots.add(b);
		if (SwarmCode.ColMap!=null){
			SwarmCode.ColMap.addCollisionShape((CollisionShape) b.getMemory().get("Shape").obj1);
		}
		return b;
	}
	
	public final void returnBot(Bot bot){
		FreeIDs.add(bot.getId());
		Bots.remove(bot);
	}

	public List<Bot> getAllBots() {
		return Bots;
	}
}
