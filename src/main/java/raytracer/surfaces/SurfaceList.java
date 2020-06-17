package raytracer.surfaces;

import java.util.ArrayList;

import javax.vecmath.Vector3d;

import raytracer.IntersectRecord;
import raytracer.Ray;
import raytracer.materials.Material;

/**
 * Surface container, stores surfaces as a list and use brute-force to
 * determine itersections.
 * 
 * Not funtional.
 * 
 * @author Bryant Martin
 *
 */
public class SurfaceList extends Surface {
	ArrayList<Surface> surfaces = new ArrayList<Surface>();
	
	@Override
	public Box getBoundingBox() { return null; }

	@Override
	public Vector3d getIntersect(Ray aRay, IntersectRecord iRec) { return null;	}

	@Override
	public Material getMaterial() {	return null; }

	@Override
	public Vector3d getNormal(Vector3d position) { return null;	}

	@Override
	public boolean intersects(Ray aRay, IntersectRecord rec) {
		final IntersectRecord iRec = new IntersectRecord();
		Surface s = null;
		double sT = 0;
		
		for (int i = 0; i < surfaces.size(); i++) {
			Surface surface = surfaces.get(i);

			// o.t >= 0 -> Ensure object is on or infront of ray
			if (surface.intersects(aRay, iRec) && (iRec.t >= 0)) {
				if ((s != null) || (iRec.t < sT)) {
					s = surface;
					sT = iRec.t;
				}
			}
		}
		
		iRec.intersectSurface = s;
		iRec.t = sT;

		return (s != null);
	}

	@Override
	public void setMaterial(Material m) { }

}
