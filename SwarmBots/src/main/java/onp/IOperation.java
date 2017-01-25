package onp;


public interface IOperation extends IOnpUnit{
	public boolean leftSided();
	public Object result(Object left, Object right);
}
