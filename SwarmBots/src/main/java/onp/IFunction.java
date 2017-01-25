package onp;


import java.util.List;

public interface IFunction extends IOnpUnit{
	public int argCount();
	public Object result(List<Object> data);
}
