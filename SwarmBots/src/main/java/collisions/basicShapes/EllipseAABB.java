package collisions.basicShapes;


import java.util.LinkedList;
import java.util.List;

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
		return MathUtil.Sqr((point.X-BoundingBox.getCenter().X)/BoundingBox.getWidth())+MathUtil.Sqr((point.Y-BoundingBox.getCenter().Y)/BoundingBox.getHeight())<=0.25d;
	}

	@Override
	public void translateTo(Vector2D newPos) {
		BoundingBox.Location.X=newPos.X-(BoundingBox.getWidth()/2);
		BoundingBox.Location.Y=newPos.Y-(BoundingBox.getHeight()/2);
	}

	@Override
	public List<Vector2D> getPointMap() {
		List<Vector2D> a=new LinkedList<Vector2D>();
		for(int i=0;i<32;++i) a.add(new Vector2D(BoundingBox.Location.X+BoundingBox.Size.X*0.5*(1+Math.cos(i*Math.PI/16)),BoundingBox.Location.Y+BoundingBox.Size.Y*0.5*(1+Math.sin(i*Math.PI/16))));
		return a;
	}

}
