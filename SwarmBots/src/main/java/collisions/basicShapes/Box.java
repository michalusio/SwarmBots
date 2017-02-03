package collisions.basicShapes;


import java.util.LinkedList;
import java.util.List;

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
		BoundingBox.Location.X=newPos.X-(BoundingBox.getWidth()/2);
		BoundingBox.Location.Y=newPos.Y-(BoundingBox.getHeight()/2);
	}

	@Override
	public List<Vector2D> getPointMap() {
		List<Vector2D> a=new LinkedList<Vector2D>();
		a.add(BoundingBox.Location);
		a.add(BoundingBox.Location.add(new Vector2D(BoundingBox.Size.X*0.5,0)));
		a.add(BoundingBox.Location.add(new Vector2D(BoundingBox.Size.X,0)));
		a.add(BoundingBox.Location.add(new Vector2D(BoundingBox.Size.X*0.5,BoundingBox.Size.Y)));
		a.add(BoundingBox.Location.add(new Vector2D(BoundingBox.Size.X,BoundingBox.Size.Y)));
		a.add(BoundingBox.Location.add(new Vector2D(BoundingBox.Size.X,BoundingBox.Size.Y*0.5)));
		a.add(BoundingBox.Location.add(new Vector2D(0,BoundingBox.Size.Y*0.5)));
		a.add(BoundingBox.Location.add(new Vector2D(0,BoundingBox.Size.Y)));
		return a;
	}

}
