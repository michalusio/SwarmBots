package botControl;

public final class Pair<T1, T2> {
	public final T1 obj1;
	public final T2 obj2;
	
	public Pair(T1 a, T2 b) {
		obj1=a;
		obj2=b;
	}
	
	public final String toString(){
		return obj1.toString()+"|"+obj2.toString();
	}

}
