package collisions;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.Vector2D;

public final class CollisionPlane {
	private final Set<CollisionShape> Map;
	
	public CollisionPlane(){
		Map=new HashSet<CollisionShape>();
	}
	
	public final synchronized void outShapes(){
		System.out.println("Shapes:");
		for(CollisionShape en:Map){
			System.out.println(en);
		}
	}
	
	public final synchronized void addCollisionShape(CollisionShape cs){
		Map.add(cs);
	}
	
	public final synchronized void deleteCollisionShape(CollisionShape cs){
		Map.remove(cs);
	}
	
	public final synchronized boolean getCollision(Vector2D Point){
		for(CollisionShape cs : Map){
			if (cs.getCollision(Point)) return true;
		}
		return false;
	}
	
	public final synchronized boolean getCollision(CollisionShape Shape){
		List<Vector2D> PointMap=Shape.getPointMap();
		for(CollisionShape cs : Map){
			if (cs==Shape) continue;
			for(Vector2D p:PointMap){
				if (cs.getCollision(p)) return true;
			}
		}
		return false;
	}
}
