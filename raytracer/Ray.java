package raytracer;

import javax.vecmath.Vector3d;

/**
 * A ray represented by an origin position and a destination position.
 * 
 * TODO May be better to store as a origin, direction, and length
 * 
 * @author Bryant Martin
 */
public class Ray {
	public Vector3d p0;
	public Vector3d p1;
	
	public double distance;
	
	public Ray(Vector3d o, Vector3d d) {
		set(o, d);
	}
	
	public void set(Vector3d o, Vector3d d) {
		p0 = o;
		p1 = d;
		
		Vector3d v = new Vector3d();
		v.scaleAdd(-1, o, d);
		distance = v.length();
	}
}
