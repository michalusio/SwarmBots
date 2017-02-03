package sim;

import collisions.AABB;
import collisions.basicShapes.Box;
import gui.MainDrawPanel;
import main.Vector2D;

public class ObstacleBox extends Obstacle {
	
	public ObstacleBox(Vector2D Center,Vector2D Size){
		cs=new Box(new AABB(Center.sub(Size.mul(0.5)),Size));
		ObstacleImage=MainDrawPanel.BoxImage;
	}

}
