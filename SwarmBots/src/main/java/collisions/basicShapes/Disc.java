package collisions.basicShapes;


import collisions.AABB;

public class Disc extends EllipseAABB {
	
	public Disc(AABB bb) {
		super(bb);
		if (bb.getWidth()!=bb.getHeight()) throw new IllegalArgumentException("Width and height of a disc not equal!");
	}

}
