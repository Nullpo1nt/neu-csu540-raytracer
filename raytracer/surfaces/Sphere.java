package raytracer.surfaces;

import javax.vecmath.Vector3d;
import raytracer.DColor;
import raytracer.IntersectRecord;
import raytracer.Ray;
import raytracer.materials.Material;

/**
 * Surface implementation for a sphere object.
 * 
 * @author Bryant Martin
 *
 */
public class Sphere extends Surface{
	Vector3d position;
	double radius;
	Material material;
	
	Box boundingBox;
	
	
	public Sphere(Vector3d p, double r, DColor c, Material m) {
		position = p;
		radius = r;
		material = m;
		
		Vector3d min = new Vector3d(radius, radius, radius),
	    max = new Vector3d(radius, radius, radius);
		min.scaleAdd(-1, min, position);
		min.scaleAdd(1, min, position);

		boundingBox = new Box(min,max);
	}
	
	public Sphere(Vector3d p, double r, DColor c) {
		this(p, r, c, new Material(c));
	}
	
	
	public boolean intersects(Ray r, IntersectRecord rec) {
		return intersects(r.p0, r.p1, rec);
	}
	
	/**
	 * Calculates the intersection point between p0 and p1 as a value, t, 
	 * between [0..1].
	 * 
	 * If the surface intersects the ray, return true, otherwise false.
	 * 
	 * @param p0
	 * @param p1
	 * @param rec
	 * @return
	 */
	protected boolean intersects(Vector3d p0, Vector3d p1, IntersectRecord rec) {
		double dx = p1.x - p0.x,
	           dy = p1.y - p0.y,
	           dz = p1.z - p0.z;
		double a = (dx*dx) + (dy*dy) + (dz*dz), 
		       b =   (2*dx*(p0.x-position.x)) 
		           + (2*dy*(p0.y-position.y)) 
		           + (2*dz*(p0.z-position.z)), 
		       c =   (position.x*position.x) 
	               + (position.y*position.y) 
	               + (position.z*position.z) 
	               + (p0.x*p0.x) 
	               + (p0.y*p0.y) 
	               + (p0.z*p0.z) 
	               + (-2*( (position.x*p0.x) 
	               + (position.y*p0.y) 
	               + (position.z*p0.z)))
	               - (radius * radius);

		double d = discriminant(a, b, c);
		
		if (d >= 0) {
			rec.t = quadratic(a, b, c, false);
			rec.intersectSurface = this;
			
			return true;
		}
		
		return false;
	}
	
	
	public Vector3d getIntersect(Ray r, IntersectRecord iRec) {
		return getIntersect(r.p0, r.p1, iRec);
	}
	
	public Vector3d getIntersect(Vector3d p0, Vector3d p1, IntersectRecord iRec) {
		double dx = p1.x - p0.x,
               dy = p1.y - p0.y,
               dz = p1.z - p0.z;
//		double a =   (dx*dx) + (dy*dy) + (dz*dz), 
//	           b =   (2*dx*(p0.x-position.x)) 
//	               + (2*dy*(p0.y-position.y)) 
//	               + (2*dz*(p0.z-position.z)), 
//	           c =   (position.x*position.x) 
//	               + (position.y*position.y) 
//	               + (position.z*position.z) 
//	               + (p0.x*p0.x) 
//	               + (p0.y*p0.y) 
//	               + (p0.z*p0.z) 
//	               + (-2*( (position.x*p0.x) 
//	     	       + (position.y*p0.y) 
//	     	       + (position.z*p0.z)))
//	               - (radius * radius);
//		
//		double t = quadratic(a, b, c, false);
//		double sX = p0.x + (t * dx), 
//		       sY = p0.y + (t * dy),
//		       sZ = p0.z + (t * dz);
		
		// Use the value stored in iRec for t 
		double sX = p0.x + (iRec.t * dx), 
	       sY = p0.y + (iRec.t * dy),
	       sZ = p0.z + (iRec.t * dz);
		
		return new Vector3d(sX, sY, sZ);
	}
	
	/**
	 * Calculates the discriminant given a, b, and c
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	protected double discriminant(double a, double b, double c) {
		return Math.pow(b, 2) - (4 * a * c);
	}
	
	/**
	 * Calculates one side of the quadratic.
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param plus
	 * @return
	 */
	protected double quadratic(double a, double b, double c, boolean plus) {
		if (plus) {
			return ((-b) + Math.sqrt((b*b) - (4 * a * c))) / (2 * a);
		}
		
		return ((-b) - Math.sqrt((b*b) - (4 * a * c))) / (2 * a);
	}
	
	/**
	 * Returns a normalized vector for the surface normal.
	 */
	public Vector3d getNormal(Vector3d p) {
		Vector3d unitNormal = new Vector3d();
		
		unitNormal.scaleAdd(-1, position, p);
		unitNormal.normalize();
		
		return unitNormal;
	}
	
	public void setMaterial(Material m) { material = m; }
	public Material getMaterial() { return material; }
	
	public Box getBoundingBox() {
		return boundingBox;
	}
	
	
	public String toString() {
		return  "\tPosition: "+ position
			+ "\n\tRadius:   "+ radius
			+ "\n" + material
			+ "\n";
	}
}
