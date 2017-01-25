package collisions.basicShapes;


import collisions.AABB;
import collisions.CollisionShape;
import main.MathUtil;
import main.Vector2D;

public class EllipseAABB extends CollisionShape {
	
	public EllipseAABB(AABB bb) {
		super(bb);
	}

	@Override
	protected boolean getPreciseCollision(Vector2D point) {
		return MathUtil.Sqr((point.X-BoundingBox.getX())/BoundingBox.getWidth())+MathUtil.Sqr((point.Y-BoundingBox.getY())/BoundingBox.getHeight())<=0.25d;
	}

	@Override
	public void translateTo(Vector2D newPos) {
		BoundingBox.Location.X=newPos.X-(BoundingBox.getWidth()/2);
		BoundingBox.Location.Y=newPos.Y-(BoundingBox.getHeight()/2);
	}

}
