package raytracer;

import raytracer.surfaces.Surface;

/**
 * Structure for Ray itersection to record the surface hit and value of t 
 * (representing the location on the ray).
 * 
 * @author Bryant Martin
 *
 */
public class IntersectRecord {
	public double t; // Interval [0..1] of valid hit on a ray
	public Surface intersectSurface;
}
