package collisions;

import main.Vector2D;

public abstract class CollisionShape {
	public final AABB BoundingBox;
	
	public CollisionShape(AABB bb){
		BoundingBox=bb;
	}
	
	public final boolean getCollision(Vector2D point) {
		return BoundingBox.getInside(point)&&getPreciseCollision(point);
	}
	
	protected abstract boolean getPreciseCollision(Vector2D point);

	public abstract void translateTo(Vector2D newPos);
}
