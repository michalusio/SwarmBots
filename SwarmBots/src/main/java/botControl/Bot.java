package botControl;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import collisions.AABB;
import collisions.CollisionShape;
import collisions.basicShapes.Disc;
import main.Vector2D;

public abstract class Bot {
	
	public final BotType Type;
	
	private final BotFactory Creator;	
	protected final int ID;
	
	private final Map<String,Pair<Object,Boolean>> Memory;
	private ErrorCode errorCode;
	private Thread BotMind;
	
	private int terminalcount;
	private boolean work,upd;
	
	public Bot(int id, BotFactory factory, BotType Type){
		this.Type=Type;
		Creator=factory;
		ID=id;
		Memory=new HashMap<String,Pair<Object,Boolean>>();
		Memory.put("Position", new Pair<Object,Boolean>(new Vector2D(0),true));
		Memory.put("Angle", new Pair<Object,Boolean>(0.0d,true));
		Disc sh=new Disc(new AABB(new Vector2D(0,0),new Vector2D(32,32)));
		sh.translateTo(new Vector2D(0));
		Memory.put("Shape", new Pair<Object,Boolean>(sh,true));
		Memory.put("Static", new Pair<Object,Boolean>(false,true));
		Restart();
	}
	
	public final synchronized void Restart(){
		Vector2D Pos=(Vector2D) Memory.get("Position").obj1;
		double Ang=(Double) Memory.get("Angle").obj1;
		CollisionShape cs=(CollisionShape) Memory.get("Shape").obj1;
		boolean stat=(Boolean) Memory.get("Static").obj1;
		Memory.clear();
		Memory.put("This", new Pair<Object,Boolean>(this,true));
		Memory.put("BracketLock", new Pair<Object,Boolean>(0,true));
		Memory.put("CommentLock", new Pair<Object,Boolean>(false,true));
		Memory.put("Position", new Pair<Object,Boolean>(Pos,true));
		Memory.put("Angle", new Pair<Object,Boolean>(Ang,true));
		Memory.put("Shape", new Pair<Object,Boolean>(cs,true));
		Memory.put("ID", new Pair<Object,Boolean>((double)ID,true));
		Memory.put("IP",new Pair<Object,Boolean>(0,true));
		Memory.put("LoopStack", new Pair<Object,Boolean>(new Stack<Object>(),true));
		Memory.put("Terminal",new Pair<Object,Boolean>(new LinkedList<String>(),true));
		Memory.put("NetCard", new Pair<Object,Boolean>((Queue<?>)new LinkedList<Pair<Object,Object>>(),true));
		Memory.put("Static", new Pair<Object,Boolean>(stat,true));
		terminalcount=0;
		upd=true;
	}
	
	public final int getId(){
		return ID;
	}
	
	public final Vector2D getPosition(){
		return (Vector2D) Memory.get("Position").obj1;
	}
	
	public final double getAngle(){
		return (double) Memory.get("Angle").obj1;
	}
	
	public final synchronized void Start(){
		if (!working()){
			work=true;
			errorCode=null;
			BotMind=new Thread(() ->
		         {
		              while(errorCode==null&&work){
		            	  if (Creator.Pause) {
		            		  try {
								Thread.sleep(100);
							} catch (Exception e) {
							}
		            		  continue;
		            	  }
		            	  Step();
		              }
		         });
			BotMind.start();
		}
	}
	
	@SuppressWarnings("unchecked")
	public final void Step(){
		int index;
		synchronized(Memory){
			upd=terminalcount!=((List<String>)Memory.get("Terminal").obj1).size();
			terminalcount=((List<String>)Memory.get("Terminal").obj1).size();
			index=(int) Memory.get("IP").obj1;
		}
		if (index>=Creator.Code.Commands.size()){
			errorCode=new ErrorCode("Code Done");
		} else {
			String command=Creator.Code.Commands.get(index);
			if (command.startsWith("/*")) Memory.put("CommentLock", new Pair<Object,Boolean>(true,true));
			if (!(Boolean)Memory.get("CommentLock").obj1){
				int lock=((Integer)Memory.get("BracketLock").obj1);
				if (lock==0&&!command.startsWith("//")){
					String lowCommand=command.toLowerCase();
					String commandType="";
					for(String s:Creator.Code.CommandMap.keySet()){
						if (lowCommand.startsWith(s)&&s.length()>commandType.length()) commandType=s;
					}
					if (commandType.equals("")) {
						errorCode=new ErrorCode("Invalid command at: "+Creator.Code.Commands.get((int) Memory.get("IP").obj1)+". Command: "+command);
					} else {
						try{
							errorCode=Creator.Code.CommandMap.get(commandType).process(command.substring(commandType.length()),Memory);
						}catch (Exception e){
							errorCode=new ErrorCode(e.getMessage());
							System.out.println(e);
						}
					}
				}else{
					if (command.equals("}")) {
						lock=Math.max(0,lock-1);
						Memory.put("BracketLock", new Pair<Object,Boolean>(lock,true));
					}else if (command.equals("}else{")) {
						lock=Math.max(0,lock-1);
						Memory.put("BracketLock", new Pair<Object,Boolean>(lock,true));
						if (lock==0){
							Stack<Object> o=(Stack<Object>) Memory.get("LoopStack").obj1;
							o.push(((Integer)Memory.get("IP").obj1) -1);
							o.push(0);
						}
					}else if (command.endsWith("{")) {
						lock++;
						Memory.put("BracketLock", new Pair<Object,Boolean>(lock,true));
					}
				}
			}
			if (command.endsWith("*/")) Memory.put("CommentLock", new Pair<Object,Boolean>(false,true));
			Memory.put("IP",new Pair<Object,Boolean>(((Integer)Memory.get("IP").obj1)+1,true));
		}
		if (errorCode!=null) {
			synchronized(Memory){
				((List<String>)Memory.get("Terminal").obj1).add(errorCode.Msg);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public final List<String> getTerminal() {
		upd=false;
		synchronized(Memory){
			return (List<String>) Memory.get("Terminal").obj1;
		}
	}

	public final boolean working() {
		return BotMind!=null&&!BotMind.isInterrupted()&&BotMind.isAlive()&&work;
	}

	public final void Interrupt() {
		work=false;
	}

	public final void Continue() {
		if (errorCode==null) Start();
	}
	
	public final Map<String,Pair<Object,Boolean>> getMemory(){
		return Memory;
	}
	
	public final boolean updateTerminal() {
		return upd;
	}

	public void setPosition(Vector2D newPos) {
		CollisionShape cs=(CollisionShape)Memory.get("Shape").obj1;
		if (SwarmCode.ColMap!=null){
			SwarmCode.ColMap.deleteCollisionShape(cs);
		}
		Memory.put("Position", new Pair<Object,Boolean>(newPos,true));
		cs.translateTo(newPos);
		if (SwarmCode.ColMap!=null){
			SwarmCode.ColMap.addCollisionShape(cs);
		}
	}
}
