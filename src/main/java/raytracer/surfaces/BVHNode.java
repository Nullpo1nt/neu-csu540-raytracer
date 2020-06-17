package raytracer.surfaces;

import javax.vecmath.Vector3d;

import raytracer.IntersectRecord;
import raytracer.Ray;
import raytracer.materials.Material;

/**
 * Partial implementation of a bounding volume hierarchy structure.
 * 
 * TODO Implement initialization of structure given a list of Surfaces
 * 
 * @author Bryant Martin
 *
 */
public class BVHNode extends Surface {
	Surface left, 
	        right;
	Box bbox;
	
	
	@Override
	public Box getBoundingBox() {
		return bbox;
	}
	
	@Override
	public boolean intersects(Ray aRay, IntersectRecord rec) {
		if(bbox.intersects(aRay)) {
			final IntersectRecord leftIRec = new IntersectRecord(), 
								  rightIRec = new IntersectRecord();
			boolean leftHit = (left != null) && left.intersects(aRay, leftIRec),
					rightHit = (right != null) && right.intersects(aRay, rightIRec);
			if (leftHit && rightHit) {
				if (leftIRec.t < rightIRec.t) {
					rec = leftIRec;
					return true;
				} else {
					rec = rightIRec;
					return true;
				}
			} else if (leftHit) {
				rec = leftIRec;
				return true;
			} else if (rightHit) {
				rec = rightIRec;
				return true;
			} else {
				return false;
			}
		}

		return false;
	}


	@Override
	public Vector3d getIntersect(Ray aRay, IntersectRecord iRec) { return null; }

	@Override
	public Material getMaterial() { return null; }

	@Override
	public Vector3d getNormal(Vector3d position) { return null; }
	
	@Override
	public void setMaterial(Material m) { }
	
}
