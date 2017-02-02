package onp;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import botControl.Bot;
import botControl.Pair;
import main.Main;
import main.Vector2D;

import java.util.Queue;
import java.util.Random;
import java.util.Stack;

public final class Parser {
	private final Map<Character,IOperation> Operators = new HashMap<Character,IOperation>();
	private final Map<String, IFunction> Functions = new HashMap<String,IFunction>();
	private final Random rand;
	public Parser(){
		rand=new Random();
		//OPERATORS
		addOperator(new IOperation(){
			@Override
			public String toString(){return "<";}
			@Override
			public int priority() {return 0;}
			@Override
			public boolean leftSided() {return false;}
			@Override
			public Object result(Object left, Object right) {
				if (left.getClass().equals(right.getClass())){
					if (left instanceof Double) return (((Double)left)<((Double)right))?1.0d:0.0d;
					if (left instanceof String) return (((String)left).compareTo((String)right)<0)?1.0d:0.0d;
					return 0.0d;
				}
				return 0.0d;
			}
		});
		addOperator(new IOperation(){
			@Override
			public String toString(){return ">";}
			@Override
			public int priority() {return 0;}
			@Override
			public boolean leftSided() {return false;}
			@Override
			public Object result(Object left, Object right) {
				if (left.getClass().equals(right.getClass())){
					if (left instanceof Double) return (((Double)left)>((Double)right))?1.0d:0.0d;
					if (left instanceof String) return (((String)left).compareTo((String)right)>0)?1.0d:0.0d;
					return 0.0d;
				}
				return 0.0d;
			}
		});
		addOperator(new IOperation(){
			@Override
			public String toString(){return "|";}
			@Override
			public int priority() {return 0;}
			@Override
			public boolean leftSided() {return false;}
			@Override
			public Object result(Object left, Object right) {
				return (Double)left>0.5f||(Double)right>0.5f;
			}
		});
		addOperator(new IOperation(){
			@Override
			public String toString(){return "&";}
			@Override
			public int priority() {return 0;}
			@Override
			public boolean leftSided() {return false;}
			@Override
			public Object result(Object left, Object right) {
				return (Double)left>0.5f&&(Double)right>0.5f;
			}
		});
		addOperator(new IOperation(){
			@Override
			public String toString(){return "=";}
			@Override
			public int priority() {return 0;}
			@Override
			public boolean leftSided() {return false;}
			@Override
			public Object result(Object left, Object right) {
				if (left==null)	return right==null?1.0d:0.0d;
				if (right==null) return 0.0d;
				if (left.getClass().equals(right.getClass())){
					if (left instanceof Vector2D) return (((Vector2D)left).distanceSqr((Vector2D) right)<0.000000001)?1.0d:0.0d;
					if (left instanceof Double) return (Math.abs(((Double)left).doubleValue()-((Double)right).doubleValue())<0.000000001)?1.0d:0.0d;
					if (left instanceof String) return ((String)left).equals(right)?1.0d:0.0d;
					return 0.0d;
				}
				return 0.0d;
			}
		});
		addOperator(new IOperation(){
			@Override
			public String toString(){return "#";}
			@Override
			public int priority() {return 0;}
			@Override
			public boolean leftSided() {return false;}
			@Override
			public Object result(Object left, Object right) {
				if (left==null)	return right==null?0.0d:1.0d;
				if (right==null) return 1.0d;
				if (left.getClass().equals(right.getClass())){
					if (left instanceof Vector2D) return (((Vector2D)left).distanceSqr((Vector2D) right)<0.000000001)?0.0d:1.0d;
					if (left instanceof Double) return (Math.abs(((Double)left).doubleValue()-((Double)right).doubleValue())<0.000000001)?0.0d:1.0d;
					if (left instanceof String) return ((String)left).equals(right)?0.0d:1.0d;
					return 1.0d;
				}
				return 1.0d;
			}
		});
		addOperator(new IOperation(){
			@Override
			public String toString(){return "+";}
			@Override
			public int priority() {return 1;}
			@Override
			public boolean leftSided() {return true;}
			@Override
			public Object result(Object left, Object right) {
				if (left instanceof String || right instanceof String) return left.toString()+right.toString();
				if (left instanceof Double) return ((Double)left)+((Double)right);
				if (left instanceof Vector2D) return ((Vector2D)left).add((Vector2D)right);
				ArrayList<Object> nowaLista=new ArrayList<Object>();
				for(int i=0;i<((List<?>)left).size();++i) nowaLista.add(0, null);
				for(int i=0;i<nowaLista.size();++i) nowaLista.set(i, result(((List<?>)left).get(i),right));
				return nowaLista;
			}
		});
		addOperator(new IOperation(){
			@Override
			public String toString(){return "-";}
			@Override
			public int priority() {return 1;}
			@Override
			public boolean leftSided() {return true;}
			@Override
			public Object result(Object left, Object right) {
				if (left instanceof Double) return ((Double)left)-((Double)right);
				if (left instanceof Vector2D) return ((Vector2D)left).sub((Vector2D)right);
				ArrayList<Object> nowaLista=new ArrayList<Object>();
				for(int i=0;i<((List<?>)left).size();++i) nowaLista.add(0, null);
				for(int i=0;i<nowaLista.size();++i) nowaLista.set(i, result(((List<?>)left).get(i),right));
				return nowaLista;
			}
		});
		addOperator(new IOperation(){
			@Override
			public String toString(){return "%";}
			@Override
			public int priority() {return 1;}
			@Override
			public boolean leftSided() {return true;}
			@Override
			public Object result(Object left, Object right) {
				if (left instanceof Double)	return ((Double)left) - ((Double)right)*Math.floor(((Double)left)/((Double)right));
				ArrayList<Object> nowaLista=new ArrayList<Object>();
				for(int i=0;i<((List<?>)left).size();++i) nowaLista.add(0, null);
				for(int i=0;i<nowaLista.size();++i) nowaLista.set(i, result(((List<?>)left).get(i),right));
				return nowaLista;
			}
		});
		addOperator(new IOperation(){
			@Override
			public String toString(){return "*";}
			@Override
			public int priority() {return 2;}
			@Override
			public boolean leftSided() {return true;}
			@Override
			public Object result(Object left, Object right) {
				if (left instanceof Double) {
					if (right instanceof Vector2D) return ((Vector2D)right).mul((Double)left);
					return ((Double)left)*((Double)right);
				}
				if (left instanceof Vector2D) return ((Vector2D)left).mul((Double)right);
				if (left instanceof String) {
					StringBuilder sb=new StringBuilder((int) (((String)left).length()*((Double)right)));
					for(int i=0;i<((Double)right).intValue();++i) sb.append((String)left);
					return sb.toString();
				}
				ArrayList<Object> nowaLista=new ArrayList<Object>();
				for(int i=0;i<((List<?>)left).size();++i) nowaLista.add(0, null);
				for(int i=0;i<nowaLista.size();++i) nowaLista.set(i, result(((List<?>)left).get(i),right));
				return nowaLista;
			}
		});
		addOperator(new IOperation(){
			@Override
			public String toString(){return "/";}
			@Override
			public int priority() {return 2;}
			@Override
			public boolean leftSided() {return true;}
			@Override
			public Object result(Object left, Object right) {
				if (left instanceof Double) return ((Double)left)/((Double)right);
				if (left instanceof Vector2D) return ((Vector2D)left).div((Double)right);
				ArrayList<Object> nowaLista=new ArrayList<Object>();
				for(int i=0;i<((List<?>)left).size();++i) nowaLista.add(0, null);
				for(int i=0;i<nowaLista.size();++i) nowaLista.set(i, result(((List<?>)left).get(i),right));
				return nowaLista;
			}
		});
		addOperator(new IOperation(){
			@Override
			public String toString(){return "^";}
			@Override
			public int priority() {return 3;}
			@Override
			public boolean leftSided() {return false;}
			@Override
			public Object result(Object left, Object right) {
				if (left instanceof Double) return Math.pow((Double)left,(Double)right);
				ArrayList<Object> nowaLista=new ArrayList<Object>();
				for(int i=0;i<((List<?>)left).size();++i) nowaLista.add(0, null);
				for(int i=0;i<nowaLista.size();++i) nowaLista.set(i, result(((List<?>)left).get(i),right));
				return nowaLista;
			}
		});
		addOperator(new IOperation(){
			@Override
			public String toString(){return ":";}
			@Override
			public int priority() {return 4;}
			@Override
			public boolean leftSided() {return false;}
			@Override
			public Object result(Object left, Object right) {
				synchronized((Bot)left){
				return ((Bot)left).getMemory().get((String)right).obj1;
				}
			}
		});
		addOperator(new IOperation(){
			@Override
			public String toString(){return "@";}
			@Override
			public int priority() {return 10;}
			@Override
			public boolean leftSided() {return true;}
			@Override
			@SuppressWarnings("unchecked")
			public Object result(Object left, Object right) {
				if (left instanceof Vector2D)
					return ((Double)right).intValue()==0?((Vector2D)left).X:((Vector2D)left).Y;
				if (left instanceof Pair<?,?>)
					return ((Double)right).intValue()==0?((Pair<Object,Object>)left).obj1:((Pair<Object,Object>)left).obj2;
				if (left instanceof String)
					return String.valueOf(((String)left).charAt(((Double)right).intValue()));
				if (left instanceof Queue){
					Queue<Object> q=(Queue<Object>)left;
					int howMuch=Math.min(q.size(),(int)(double)right);
					List<Object> a=new ArrayList<Object>();
					for(int i=0;i<howMuch;++i) a.add(q.remove());
					return a;
				}
				try{
					return ((List<Object>)left).get(((Double)right).intValue());
				}catch(IndexOutOfBoundsException e){
					return null;
				}
			}
		});
		//FUNCTIONS
		addFunction(new IFunction(){
			@Override
			public String toString(){return "pi";}
			@Override
			public int priority() {return 5;}
			@Override
			public int argCount() {return 0;}
			@Override
			public Object result(List<Object> data) {
				return Math.PI;
			}
		});
		addFunction(new IFunction(){
			@Override
			public String toString(){return "e";}
			@Override
			public int priority() {return 5;}
			@Override
			public int argCount() {return 0;}
			@Override
			public Object result(List<Object> data) {
				return Math.E;
			}
		});
		addFunction(new IFunction(){
			@Override
			public String toString(){return "exp";}
			@Override
			public int priority() {return 5;}
			@Override
			public int argCount() {return 1;}
			@Override
			public Object result(List<Object> data) {
				return Math.exp((Double)data.get(0));
			}
		});
		addFunction(new IFunction(){
			@Override
			public String toString(){return "sin";}
			@Override
			public int priority() {return 5;}
			@Override
			public int argCount() {return 1;}
			@Override
			public Object result(List<Object> data) {
				Object d=data.get(0);
				if (d instanceof Double) return Math.sin((Double)d);
				ArrayList<Object> nowaLista=new ArrayList<Object>();
				for(int i=0;i<((List<?>)d).size();++i) nowaLista.add(0, null);
				for(int i=0;i<nowaLista.size();++i) nowaLista.set(i, result(Arrays.asList(((List<?>)d).get(i))));
				return nowaLista;
			}
		});
		addFunction(new IFunction(){
			@Override
			public String toString(){return "cos";}
			@Override
			public int priority() {return 5;}
			@Override
			public int argCount() {return 1;}
			@Override
			public Object result(List<Object> data) {
				Object d=data.get(0);
				if (d instanceof Double) return Math.cos((Double)d);
				ArrayList<Object> nowaLista=new ArrayList<Object>();
				for(int i=0;i<((List<?>)d).size();++i) nowaLista.add(0, null);
				for(int i=0;i<nowaLista.size();++i) nowaLista.set(i, result(Arrays.asList(((List<?>)d).get(i))));
				return nowaLista;
			}
		});
		addFunction(new IFunction(){
			@Override
			public String toString(){return "tan";}
			@Override
			public int priority() {return 5;}
			@Override
			public int argCount() {return 1;}
			@Override
			public Object result(List<Object> data) {
				Object d=data.get(0);
				if (d instanceof Double) return Math.tan((Double)d);
				ArrayList<Object> nowaLista=new ArrayList<Object>();
				for(int i=0;i<((List<?>)d).size();++i) nowaLista.add(0, null);
				for(int i=0;i<nowaLista.size();++i) nowaLista.set(i, result(Arrays.asList(((List<?>)d).get(i))));
				return nowaLista;
			}
		});
		addFunction(new IFunction(){
			@Override
			public String toString(){return "atan";}
			@Override
			public int priority() {return 5;}
			@Override
			public int argCount() {return 1;}
			@Override
			public Object result(List<Object> data) {
				Object d=data.get(0);
				if (d instanceof Double) return Math.atan((Double)d);
				ArrayList<Object> nowaLista=new ArrayList<Object>();
				for(int i=0;i<((List<?>)d).size();++i) nowaLista.add(0, null);
				for(int i=0;i<nowaLista.size();++i) nowaLista.set(i, result(Arrays.asList(((List<?>)d).get(i))));
				return nowaLista;
			}
		});
		addFunction(new IFunction(){
			@Override
			public String toString(){return "atan2";}
			@Override
			public int priority() {return 5;}
			@Override
			public int argCount() {return 2;}
			@Override
			public Object result(List<Object> data) {
				return Math.atan2((Double)data.get(1),(Double)data.get(0));
			}
		});
		addFunction(new IFunction(){
			@Override
			public String toString(){return "sign";}
			@Override
			public int priority() {return 5;}
			@Override
			public int argCount() {return 1;}
			@Override
			public Object result(List<Object> data) {
				Object d=data.get(0);
				if (d instanceof Double) return Math.signum((Double)d);
				ArrayList<Object> nowaLista=new ArrayList<Object>();
				for(int i=0;i<((List<?>)d).size();++i) nowaLista.add(0, null);
				for(int i=0;i<nowaLista.size();++i) nowaLista.set(i, result(Arrays.asList(((List<?>)d).get(i))));
				return nowaLista;
			}
		});
		addFunction(new IFunction(){
			@Override
			public String toString(){return "abs";}
			@Override
			public int priority() {return 5;}
			@Override
			public int argCount() {return 1;}
			@Override
			public Object result(List<Object> data) {
				Object d=data.get(0);
				if (d instanceof Double) return Math.abs((Double)d);
				ArrayList<Object> nowaLista=new ArrayList<Object>();
				for(int i=0;i<((List<?>)d).size();++i) nowaLista.add(0, null);
				for(int i=0;i<nowaLista.size();++i) nowaLista.set(i, result(Arrays.asList(((List<?>)d).get(i))));
				return nowaLista;
			}
		});
		addFunction(new IFunction(){
			@Override
			public String toString(){return "max";}
			@Override
			public int priority() {return 5;}
			@Override
			public int argCount() {return 2;}
			@Override
			public Object result(List<Object> data) {
				return Math.max((Double)data.get(0),(Double)data.get(1));
			}
		});
		addFunction(new IFunction(){
			@Override
			public String toString(){return "min";}
			@Override
			public int priority() {return 5;}
			@Override
			public int argCount() {return 2;}
			@Override
			public Object result(List<Object> data) {
				return Math.min((Double)data.get(0),(Double)data.get(1));
			}
		});
		addFunction(new IFunction(){
			@Override
			public String toString(){return "rand";}
			@Override
			public int priority() {return 5;}
			@Override
			public int argCount() {return 0;}
			@Override
			public Object result(List<Object> data) {
				return rand.nextDouble();
			}
		});
		addFunction(new IFunction(){
			@Override
			public String toString(){return "array";}
			@Override
			public int priority() {return 5;}
			@Override
			public int argCount() {return 1;}
			@Override
			public Object result(List<Object> data) {
				return new ArrayList<Object>(((Double)data.get(0)).intValue());
			}
		});
		addFunction(new IFunction(){
			@Override
			public String toString(){return "string";}
			@Override
			public int priority() {return 5;}
			@Override
			public int argCount() {return 1;}
			@Override
			public Object result(List<Object> data) {
				return data.get(0).toString();
			}
		});
		addFunction(new IFunction(){
			@Override
			public String toString(){return "vector";}
			@Override
			public int priority() {return 5;}
			@Override
			public int argCount() {return 2;}
			@Override
			public Object result(List<Object> data) {
				return new Vector2D((Double)data.get(0),(Double)data.get(1));
			}
		});
		addFunction(new IFunction(){
			@Override
			public String toString(){return "length";}
			@Override
			public int priority() {return 5;}
			@Override
			public int argCount() {return 1;}
			@Override
			@SuppressWarnings("unchecked")
			public Object result(List<Object> data) {
				Object o=data.get(0);
				if (o instanceof String) return ((String)o).length();
				if (o instanceof List) return ((List<Object>)o).size();
				if (o instanceof Vector2D) return ((Vector2D)o).len();
				return (double)o;
			}
		});
		addFunction(new IFunction(){
			@Override
			public String toString(){return "now";}
			@Override
			public int priority() {return 5;}
			@Override
			public int argCount() {return 0;}
			@Override
			public Object result(List<Object> data) {
				return (double)System.currentTimeMillis();
			}
		});
		addFunction(new IFunction(){
			@Override
			public String toString(){return "nearest";}
			@Override
			public int priority() {return 5;}
			@Override
			public int argCount() {return 2;}
			@Override
			public Object result(List<Object> data) {
				Vector2D p=(Vector2D) data.get(1);
				Bot me=(Bot) data.get(0);
				Bot near=null;
				double dist=Double.MAX_VALUE;
				for(Bot b:Main.bf.getAllBots()){
					if (b==me) continue;
					double d=b.getPosition().distanceSqr(p);
					if (dist>d){
						near=b;
						dist=d;
					}
				}
				return new Pair<Bot, Double>(near,dist);
			}
		});
	}
	
	public final void addOperator(IOperation op){
		Operators.put(op.toString().charAt(0), op);
	}
	
	public final void addFunction(IFunction f){
		Functions.put(f.toString(), f);
	}
	
	public final Object Solve(Queue<Object> parse, Map<String,Pair<Object,Boolean>> Variables){
		Stack<Object> Calculations=new Stack<Object>();
		while (!parse.isEmpty()){
			Object o=parse.poll();
			if (o instanceof IOperation){
				Object o1;
				Object o2;
				try{
				o1=Calculations.pop();
				o2=Calculations.pop();
				} catch(Exception e) {throw new IllegalArgumentException("Wrong operand! "+o.toString());}
				Calculations.push(((IOperation)o).result(o2,o1));
			}else if (o instanceof IFunction){
				IFunction func=(IFunction)o;
				List<Object> data=new LinkedList<Object>();
				for(int i=0;i<func.argCount();i++){
					if (Calculations.isEmpty()) throw new IllegalArgumentException("Wrong function! "+o.toString());
					data.add(Calculations.pop());
				}
				Calculations.push(func.result(data));
			} else {
				String s=(String)o;
				try{
					Calculations.push(Double.parseDouble(s));
				} catch(Exception e) {
					if (s.equalsIgnoreCase("null")) Calculations.push(null);
					else if (Variables.containsKey(s)) Calculations.push(Variables.get(s).obj1);
					else if (s.charAt(0)=='\"'&&s.charAt(s.length()-1)=='\"') Calculations.push(s.substring(1, s.length()-1));
					else throw new IllegalArgumentException("Invalid variable: "+s);
				}
			}
		}
		return Calculations.pop();
	}
	
	public final Queue<Object> Parse(String formula){
		Queue<Object> Result=new LinkedList<Object>();
		Stack<Object> Things=new Stack<Object>();
		
		StringBuilder actVar=new StringBuilder(5);
		boolean cudz=false;
		for (char c: formula.toCharArray()){
			if (c=='\"') {
				cudz=!cudz;
				actVar.append(c);
			}else if (cudz||Character.isLetterOrDigit(c)||c=='['||c==']'||c=='_'||c=='.'||c==' '||c=='\''||c=='?'||c=='!') actVar.append(c);
			else if (c==',') {
				if (actVar.length()>0){
					String var=actVar.toString();
					actVar.delete(0,var.length());
					String var2=var.toLowerCase();
					if (Functions.containsKey(var2)) Things.push(Functions.get(var2)); else Things.push(var);
				}
				Object d=Things.pop();
				while(!((d instanceof String)&&((String)d).equals("("))){
					Result.add(d);
					d=Things.pop();
				}
				Things.push("(");
			} else if (c=='('){
				if (actVar.length()>0){
					String var=actVar.toString();
					actVar.delete(0,var.length());
					String var2=var.toLowerCase();
					if (Functions.containsKey(var2)) Things.push(Functions.get(var2)); else Things.push(var);
				}
				Things.push("(");
			} else if (c==')'){
				if (actVar.length()>0){
					String var=actVar.toString();
					actVar.delete(0,var.length());
					String var2=var.toLowerCase();
					if (Functions.containsKey(var2)) Result.add(Functions.get(var2)); else Result.add(var);
				}
				boolean GotBracket=false;
				while(!Things.isEmpty()){
					Object o=Things.pop();
					if ((o instanceof IOnpUnit)) {
						Result.add(o);
					}else{
						if (((String)o).equals("(")) {GotBracket=true;break;}
						Result.add(o);
					}
				}
				if (!GotBracket) throw new IllegalArgumentException("Wrong brackets number!");
				
			} else {
				boolean wasEmpty=true;
				if (actVar.length()>0){
					wasEmpty=false;
					String var=actVar.toString();
					actVar.delete(0,var.length());
					String var2=var.toLowerCase();
					if (Functions.containsKey(var2)) Result.add(Functions.get(var2)); else Result.add(var);
				}
				if (c=='-'&&wasEmpty) actVar.append('-');
				else if (Operators.containsKey(c)){
					IOperation F=Operators.get(c);
					while(!Things.isEmpty()){
						Object o=Things.pop();
						if (o instanceof IOnpUnit){
							IOnpUnit io2=((IOnpUnit)o);
							if (F.leftSided()){
								if (F.priority()<=io2.priority()) {
									Result.add(io2);
								} else {
									Things.push(o);
									break;
								}
							}else{
								if (F.priority()<io2.priority()) {
									Result.add(io2);
								} else {
									Things.push(o);
									break;
								}
							}
						}else {
							Things.push(o);
							break;
						}
					}
					Things.push(F);
				}else throw new IllegalArgumentException("No operator found: "+c);
			}
		}
		if (actVar.length()>0){
			String var=actVar.toString();
			actVar.delete(0,var.length());
			String var2=var.toLowerCase();
			if (Functions.containsKey(var2)) Things.push(Functions.get(var2)); else Things.push(var);
		}
		while (!Things.isEmpty()){
			Result.add(Things.pop());
		}
		return Result;
	}
}
