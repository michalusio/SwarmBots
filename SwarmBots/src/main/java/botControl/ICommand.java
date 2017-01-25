package botControl;


import java.util.Map;

public interface ICommand {
	public ErrorCode process(String args, Map<String, Pair<Object,Boolean>> Memory);
}
