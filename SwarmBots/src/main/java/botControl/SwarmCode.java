package botControl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import collisions.CollisionPlane;
import collisions.CollisionShape;
import main.Vector2D;
import onp.Parser;

public final class SwarmCode {
	public final BotFactory Factory;
	public static CollisionPlane ColMap;
	public final List<String> Commands;
	public final Map<String,ICommand> CommandMap=new HashMap<String,ICommand>();
	private final Parser Pars;
	
	public SwarmCode(String[] Code,BotFactory bf){
		Init();
		Factory=bf;
		Commands=new ArrayList<String>();
		boolean comment=false;
		for(int i=0;i<Code.length;i++){
			Code[i]=Code[i].trim();
			if (Code[i].startsWith("/*")) comment=true;
			if (comment||Code[i].startsWith("let ")||Code[i].startsWith("//"))	Commands.add(Code[i]);
			else Commands.add(UsunSpacje(Code[i]));
			if (Code[i].endsWith("*/")) comment=false;
		}
		Pars=new Parser();
	}
	
	private String UsunSpacje(String string) {
		StringBuilder result=new StringBuilder(string.length());
		boolean fr = true;
		for(char c:string.toCharArray()){
			fr =fr^(c=='"');
			if (c==' ' && fr) continue;
			result.append(c);
		}
		return result.toString();
	}

	@SuppressWarnings("unchecked")
	private final void Init(){
		
		CommandMap.put("let ",(String args,Map<String, Pair<Object,Boolean>> Memory) ->{
			if (args.matches("[A-Za-z_\\[\\]0-9]+=.+")){
				int ind=args.indexOf('=');
				String left=args.substring(0,ind);
				String right=args.substring(ind+1);
				left=FindArrays(left);
				right=FindArrays(right);
				int i=left.indexOf('@');
				String r=FindArrays(right);
				Object solve=Pars.Solve(Pars.Parse(r), Memory);
				if (i>=0){
					int index=((Double)Pars.Solve(Pars.Parse(left.substring(i+1)), Memory)).intValue();
					String varName=left.substring(0,i);
					if (Memory.containsKey(varName)&&Memory.get(varName).obj2) return new ErrorCode("Writing to system variable!");
					Object obj=Memory.get(varName).obj1;
					if (obj instanceof Vector2D) {
						if (index==0) {
							((Vector2D)obj).X=(Double)solve;
						} else {
							((Vector2D)obj).Y=(Double)solve;
						}
					}else if (obj instanceof String){
						Memory.put(varName, new Pair<Object,Boolean>(replace((String)obj,index,(String)solve),false));
					}else {
						List<Object> list=(List<Object>)obj;
						while (list.size()<=index) list.add(null);
						list.set(index, solve);
					}
				}else{
					if (Memory.containsKey(left)&&Memory.get(left).obj2) return new ErrorCode("Writing to system variable!");
					Memory.put(left, new Pair<Object,Boolean>(solve,false));
				}
				return null;
			}
			return new ErrorCode("Invalid 'let' statement!");
		});
		
		CommandMap.put("print(", (String args,Map<String, Pair<Object,Boolean>> Memory) ->{
			if (args.endsWith(")")){
				Object solve=Pars.Solve(Pars.Parse(FindArrays(args.substring(0,args.length()-1))), Memory);
				synchronized(Memory){
					((List<String>)Memory.get("Terminal").obj1).add(solve.toString());
				}
				return null;
			}
			return new ErrorCode("Invalid 'print' statement!");
		});
		
		CommandMap.put("send(", (String args,Map<String, Pair<Object,Boolean>> Memory)->{
			if (args.endsWith(")")){
				String[] tab=intelligentSplit(args.substring(0,args.length()-1),",","([",")]");
				if (tab.length!=2) return new ErrorCode("Invalid 'send' statement!");
				int ID=(int)((double)Pars.Solve(Pars.Parse(FindArrays(tab[0])), Memory));
				Object o=Pars.Solve(Pars.Parse(FindArrays(tab[1])), Memory);
				synchronized(Factory.getAllBots().get(ID).getMemory().get("NetCard").obj1){
					((Queue<Object>)Factory.getAllBots().get(ID).getMemory().get("NetCard").obj1).add(new Pair<Object,Object>(Memory.get("ID").obj1,o));
				}
				return null;
			}
			return new ErrorCode("Invalid 'send' statement!");
		});
		
		CommandMap.put("fail(",(String args,Map<String, Pair<Object,Boolean>> Memory) ->{
			if (args.endsWith(")"))	return new ErrorCode(args.substring(0, args.length()-1));
			return new ErrorCode("Invalid 'fail' statement!");
		});
		
		CommandMap.put("turnto(", (String args,Map<String, Pair<Object,Boolean>> Memory) ->{
			if (Memory.get("Static").obj1.equals(true)) return null;
			if (args.endsWith(")")){
				Object solve=Pars.Solve(Pars.Parse(FindArrays(args.substring(0,args.length()-1))), Memory);
				if (solve instanceof Double){
					synchronized(Memory){
						Memory.put("Angle", new Pair<Object,Boolean>(((((Double)solve)%(2*Math.PI))+2*Math.PI)%(2*Math.PI),true));
					}
					return null;
				}else if (solve instanceof Vector2D){
					synchronized(Memory){
						Memory.put("Angle", new Pair<Object,Boolean>(((Vector2D) solve).sub((Vector2D)Memory.get("Position").obj1).angle(),true));
					}
					return null;
				}
				return new ErrorCode("Wrong variable type!");
			}
			return new ErrorCode("Invalid 'turnto' statement!");
		});
		
		CommandMap.put("turnleft(", (String args,Map<String, Pair<Object,Boolean>> Memory) ->{
			if (Memory.get("Static").obj1.equals(true)) return null;
			if (args.endsWith(")")){
				Object solve=Pars.Solve(Pars.Parse(FindArrays(args.substring(0,args.length()-1))), Memory);
				if (solve instanceof Double){
					synchronized(Memory){
						Memory.put("Angle", new Pair<Object,Boolean>((((((Double)Memory.get("Angle").obj1)-((Double)solve))%(2*Math.PI))+2*Math.PI)%(2*Math.PI),true));
					}
					return null;
				}
				return new ErrorCode("Wrong variable type!");
			}
			return new ErrorCode("Invalid 'turnleft' statement!");
		});
		
		CommandMap.put("turnright(", (String args,Map<String, Pair<Object,Boolean>> Memory) ->{
			if (Memory.get("Static").obj1.equals(true)) return null;
			if (args.endsWith(")")){
				Object solve=Pars.Solve(Pars.Parse(FindArrays(args.substring(0,args.length()-1))), Memory);
				if (solve instanceof Double){
					synchronized(Memory){
						Memory.put("Angle", new Pair<Object,Boolean>((((((Double)Memory.get("Angle").obj1)+((Double)solve))%(2*Math.PI))+2*Math.PI)%(2*Math.PI),true));
					}
					return null;
				}
				return new ErrorCode("Wrong variable type!");
			}
			return new ErrorCode("Invalid 'turnright' statement!");
		});
		
		CommandMap.put("forward(", (String args,Map<String, Pair<Object,Boolean>> Memory) ->{
			if (Memory.get("Static").obj1.equals(true)) return null;
			if (args.endsWith(")")){
				Object solve=Pars.Solve(Pars.Parse(FindArrays(args.substring(0,args.length()-1))), Memory);
				if (solve instanceof Double){
					double ang;
					synchronized(Memory) {
						ang=(Double) Memory.get("Angle").obj1;
					}
					double ile=(Double) solve;
					CollisionShape cs=(CollisionShape)Memory.get("Shape").obj1;
					Vector2D dir=new Vector2D(1,0).rotate(ang);
					for (int i=0;i<(int)ile;++i){
						try {
							Thread.sleep(20);
						} catch (Exception e) {
							e.printStackTrace();
						}
						Vector2D newPos=((Vector2D)Memory.get("Position").obj1).add(dir);
						if (ColMap!=null) {
							ColMap.deleteCollisionShape(cs);
						}
						if (ColMap==null||!ColMap.getCollision(newPos)){
								Memory.put("Position", new Pair<Object,Boolean>(newPos,true));
							if (ColMap!=null){
								cs.translateTo(newPos);
								ColMap.addCollisionShape(cs);
							}
							Memory.put("MoveError", new Pair<Object,Boolean>((double)0,false));
						}else{
							if (ColMap!=null) ColMap.addCollisionShape(cs);
							Memory.put("MoveError", new Pair<Object,Boolean>((double)1,false));
						}
					}
					
					try {
						Thread.sleep((long) (20*(ile-((int)ile))));
					} catch (Exception e) {
						e.printStackTrace();
					}
					Vector2D newPos=((Vector2D)Memory.get("Position").obj1).add(dir.mul(ile-((int)ile)));
					if (ColMap!=null) {
						ColMap.deleteCollisionShape(cs);
					}
					if (ColMap==null||!ColMap.getCollision(newPos)){
							Memory.put("Position", new Pair<Object,Boolean>(newPos,true));
						if (ColMap!=null){
							cs.translateTo(newPos);
							ColMap.addCollisionShape(cs);
						}
						Memory.put("MoveError", new Pair<Object,Boolean>((double)0,false));
					}else{
						if (ColMap!=null) ColMap.addCollisionShape(cs);
						Memory.put("MoveError", new Pair<Object,Boolean>((double)1,false));
					}
					
					return null;
				}
				return new ErrorCode("Wrong variable type!");
			}
			return new ErrorCode("Invalid 'forward' statement!");
		});
		
		CommandMap.put("backward(", (String args,Map<String, Pair<Object,Boolean>> Memory) ->{
			if (Memory.get("Static").obj1.equals(true)) return null;
			if (args.endsWith(")")){
				Object solve=Pars.Solve(Pars.Parse(FindArrays(args.substring(0,args.length()-1))), Memory);
				if (solve instanceof Double){
					double ang;
					synchronized(Memory) {
						ang=(Double) Memory.get("Angle").obj1;
					}
					double ile=(Double) solve;
					CollisionShape cs=(CollisionShape)Memory.get("Shape").obj1;
					Vector2D dir=new Vector2D(1,0).rotate(ang);
					for (int i=0;i<(int)ile;++i){
						try {
							Thread.sleep(20);
						} catch (Exception e) {
							e.printStackTrace();
						}
						Vector2D newPos=((Vector2D)Memory.get("Position").obj1).sub(dir);
						if (ColMap!=null) {
							ColMap.deleteCollisionShape(cs);
						}
						if (ColMap==null||!ColMap.getCollision(newPos)){
								Memory.put("Position", new Pair<Object,Boolean>(newPos,true));
							if (ColMap!=null){
								cs.translateTo(newPos);
								ColMap.addCollisionShape(cs);
							}
							Memory.put("MoveError", new Pair<Object,Boolean>((double)0,false));
						}else{
							if (ColMap!=null) ColMap.addCollisionShape(cs);
							Memory.put("MoveError", new Pair<Object,Boolean>((double)1,false));
						}
					}
					
					try {
						Thread.sleep((long) (20*(ile-((int)ile))));
					} catch (Exception e) {
						e.printStackTrace();
					}
					Vector2D newPos=((Vector2D)Memory.get("Position").obj1).sub(dir.mul(ile-((int)ile)));
					if (ColMap!=null) {
						ColMap.deleteCollisionShape(cs);
					}
					if (ColMap==null||!ColMap.getCollision(newPos)){
							Memory.put("Position", new Pair<Object,Boolean>(newPos,true));
						if (ColMap!=null){
							cs.translateTo(newPos);
							ColMap.addCollisionShape(cs);
						}
						Memory.put("MoveError", new Pair<Object,Boolean>((double)0,false));
					}else{
						if (ColMap!=null) ColMap.addCollisionShape(cs);
						Memory.put("MoveError", new Pair<Object,Boolean>((double)1,false));
					}
					
					return null;
				}
				return new ErrorCode("Wrong variable type!");
			}
			return new ErrorCode("Invalid 'backward' statement!");
		});
		
		CommandMap.put("wait(", (String args,Map<String, Pair<Object,Boolean>> Memory) ->{
			if (args.endsWith(")")){
				Object solve=Pars.Solve(Pars.Parse(FindArrays(args.substring(0,args.length()-1))), Memory);
				if (solve instanceof Double){
					try {
						Thread.sleep(((Double)solve).intValue());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return null;
				}
				return new ErrorCode("Wrong variable type!");
			}
			return new ErrorCode("Invalid 'forward' statement!");
		});
		
		CommandMap.put("{",(String args,Map<String, Pair<Object,Boolean>> Memory) ->{
			if (args.equals("")){
				Stack<Object> o=(Stack<Object>) Memory.get("LoopStack").obj1;
				o.push(Memory.get("IP").obj1);
				o.push(0);
				return null;
			}
			return new ErrorCode("Block start invalid: "+args);
		});
		
		CommandMap.put("while(",(String args,Map<String, Pair<Object,Boolean>> Memory) ->{
			if (args.matches(".+\\)\\{")){
				Object solve=Pars.Solve(Pars.Parse(FindArrays(args.substring(0,args.length()-2))), Memory);
				Stack<Object> o=(Stack<Object>) Memory.get("LoopStack").obj1;
				if (solve instanceof Double && ((Double)solve)>0.5){
					o.push(((Integer)Memory.get("IP").obj1) -1);
					o.push(-1);
				}else {
					Memory.put("BracketLock", new Pair<Object,Boolean>(1,true));
					o.push(((Integer)Memory.get("IP").obj1) -1);
					o.push(0);
				}
				return null;
			}
			return new ErrorCode("Loop start invalid: "+args);
		});
		
		CommandMap.put("if(",(String args,Map<String, Pair<Object,Boolean>> Memory) ->{
			if (args.matches(".+\\)\\{")){
				Object solve=Pars.Solve(Pars.Parse(FindArrays(args.substring(0,args.length()-2))), Memory);
				if (!(solve instanceof Double && ((Double)solve)>0.5)) {
					Memory.put("BracketLock", new Pair<Object,Boolean>(1,true));
				}else {
					Stack<Object> o=(Stack<Object>) Memory.get("LoopStack").obj1;
					o.push(((Integer)Memory.get("IP").obj1) -1);
					o.push(0);
				}
				return null;
			}
			return new ErrorCode("If start invalid: "+args);
		});
		
		CommandMap.put("}else{",(String args,Map<String, Pair<Object,Boolean>> Memory) ->{
			if (args.equals("")){
				Memory.put("BracketLock", new Pair<Object,Boolean>(1,true));
				return null;
			}
			return new ErrorCode("Else start invalid: "+args);
		});
		
		CommandMap.put("}",(String args,Map<String, Pair<Object,Boolean>> Memory) ->{
			if (args.equals("")){
				Stack<Object> o=(Stack<Object>) Memory.get("LoopStack").obj1;
				int loop=((Integer)o.pop());
				int ip=((Integer)o.pop());
				if (loop<0) {
					Memory.put("IP", new Pair<Object,Boolean>(ip,true));
				}
				return null;
			}
			return new ErrorCode("Block end invalid: "+args);
		});
	}

	private String[] intelligentSplit(String string,String split, String levelOpen, String levelClose) {
		List<String> l=new ArrayList<String>();
		int level=0;
		StringBuilder actual=new StringBuilder();
		for(char c :string.toCharArray()){
			if (level==0&&split.contains(String.valueOf(c))){
				l.add(actual.toString());
				actual=new StringBuilder();
			} else actual.append(c);
			if (levelOpen.contains(String.valueOf(c))) level++;
			if (levelClose.contains(String.valueOf(c))) level--;
		}
		l.add(actual.toString());
		String[] stockArr = new String[l.size()];
		return l.toArray(stockArr);
	}

	private final String FindArrays(String formula) {
		while(true){
			int i=formula.indexOf('[');
			if (i==-1) break;
			int j=SzukajZamkniecia(formula, i);
			if (j<i) throw new IllegalArgumentException("Wrong bracketing!");
			String interior=FindArrays(formula.substring(i+1, j));
			formula=formula.substring(0,i)+"@("+interior+")"+formula.substring(j+1);
		}
		return formula;
	}
	
	private final int SzukajZamkniecia(String formula,int j) {
		int nawiasy=0;
		for(int i=j;i<formula.length();i++){
			char c=formula.charAt(i);
			if (c=='[') nawiasy++;
			else if (c==']') nawiasy--;
			if (nawiasy==0) return i;
		}
		return -1;
	}
	private String replace(String str, int index, String replace){     
	    if(str==null) return str;
	    if(index<0 || index>=str.length()) return str;
	    char[] chars = str.toCharArray();
	    chars[index] = replace.charAt(0);
	    return String.valueOf(chars);
	}
}
