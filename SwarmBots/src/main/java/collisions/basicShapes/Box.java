package collisions.basicShapes;


import collisions.AABB;
import collisions.CollisionShape;
import main.Vector2D;

public class Box extends CollisionShape {

	public Box(AABB bb) {
		super(bb);
	}

	@Override
	protected boolean getPreciseCollision(Vector2D point) {
		return true;
	}

	@Override
	public void translateTo(Vector2D newPos) {
		BoundingBox.Location.X=newPos.X;
		BoundingBox.Location.Y=newPos.Y;
	}

}
