package raytracer.surfaces;

import javax.vecmath.Vector3d;
import raytracer.Ray;

/**
 * Used for bounding box, but since the bounding volume hierarchy isn't complete
 * it is not used.
 * 
 * @author Bryant Martin
 *
 */
public class Box {
	public Vector3d min,
					max;
	
	public Box(Vector3d min, Vector3d max) {
		this.min = min;
		this.max = max;
	}
	
	public boolean intersects(Ray r) {
		//return true;
		double txmin, txmax, tymin, tymax, tzmin, tzmax;
		double a;
		
		a = 1 / r.p1.x;
		if (a >= 0) {
			txmin = a * (min.x - r.p0.x);
			txmax = a * (max.x - r.p0.x);
		} else {
			txmin = a * (max.x - r.p0.x);
			txmax = a * (min.x - r.p0.x);
		}
		
		a = 1 / r.p1.y;
		if (a >= 0) {
			tymin = a * (min.y - r.p0.y);
			tymax = a * (max.y - r.p0.y);
		} else {
			tymin = a * (max.y - r.p0.y);
			tymax = a * (min.y - r.p0.y);
		}
		
		a = 1 / r.p1.z;
		if (a >= 0) {
			tzmin = a * (min.z - r.p0.z);
			tzmax = a * (max.z - r.p0.z);
		} else {
			tzmin = a * (max.z - r.p0.z);
			tzmax = a * (min.z - r.p0.z);
		}
		
		if ((txmin > tymax || tymin > txmax) || 
			(tzmin > tymax || tymin > tzmax) ||
			(txmin > tzmax || tzmin > txmax)) {
			return true;
		}
		
		return true;
	}
}
