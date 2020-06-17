package raytracer.surfaces;

import javax.vecmath.Vector3d;

import raytracer.IntersectRecord;
import raytracer.Ray;
import raytracer.materials.Material;

/**
 * Abstract class for a surface to be rendered by the raytracer.
 * 
 * @author Bryant Martin
 *
 */
public abstract class Surface {
	public abstract Vector3d getNormal(Vector3d position);

	/**
	 * Returns the intersection point on aRay as a value, t , between [0..1] in
	 * the IntersectRecord.
	 * 
	 *   t < 0     -> Surface is behind ray   
	 * 	 t = 0     -> Surface is at aRay's origin position
	 *   0 < t < 1 -> Surface is somewhere between the rays origin and 
	 *                destination positions
	 *   t = 1     -> Surface intersects ray at destination position
	 *   t > 1     -> Surface intersects ray somewhere beyond its destination
	 *                position
	 * 	
	 * @param aRay
	 * @param rec
	 * @return
	 */
	public abstract boolean intersects(Ray aRay, IntersectRecord rec);
	
	/**
	 * Returns the position of the rays intersection on the surface.
	 * 
	 * @param aRay
	 * @param rec
	 * @return
	 */
	public abstract Vector3d getIntersect(Ray aRay, IntersectRecord rec);
	
	public abstract void setMaterial(Material m);
	public abstract Material getMaterial();
	
	public abstract Box getBoundingBox();
}
