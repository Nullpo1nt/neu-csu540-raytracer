package raytracer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Date;
import javax.vecmath.Vector3d;
import raytracer.materials.*;
import raytracer.surfaces.*;

/**
 * A simple raytracer implementation for CSU540.
 * 
 * Supports (or will):
 * 	- Hard shadows
 *  - Z-Plane shadows, at z=0;
 *  - Ambient+Diffuse shading
 *  - Specular highlights
 *  - Specular reflections
 *  - Refraction and transparency
 *  - Multiple lights
 *  - Subsampling
 * 
 * @author Bryant Martin
 */
public class Raytracer {
	private int recursionLimit,
	    		subSamples;
	// Statistics
	long primaryRays,
         reflectionRays,
         shadowRays,
         maxReflect,
         renderTime;
	// TODO Remove scene member, pass as a parameter
	private Scene scene;
	
	
	public Raytracer(int rLimit, int ss) {
		setRecursionLimit(rLimit);
		setSubsample(subSamples = ss);
	}
	
	public Raytracer() {
		this(30, 1);
	}
	
	
	/**
	 * Render a Scene s to an image.  The image should be initialized with the
	 * desired size in an RGB format and otherwise in a ready to be rendered to
	 * state.
	 * 
	 * @param image
	 * @param s
	 */
	public void render(Image image, Scene s) {
		render((Graphics2D)image.getGraphics(), image.getWidth(null), image.getHeight(null), s);
	}
	
	/**
	 * Render a scene to a provide graphics context with at the provide 
	 * dimensions.
	 * 
	 * @param g
	 * @param width
	 * @param height
	 * @param s
	 */
	public void render(Graphics2D g, int width, int height, Scene s) {
		scene  = s;
	
		final DColor color = new DColor(0.0d, 0.0d, 0.0d);
		final DColor subSample = new DColor(0.0d, 0.0d, 0.0d);
		final Ray ray = new Ray(scene.camera, new Vector3d());
		
		System.out.println(this);
		System.out.print("Rendering: ("+width+"x"+height+") .");
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);

		primaryRays = 0;
		shadowRays = 0;
		reflectionRays = 0;
		maxReflect = 0;
		renderTime = new Date().getTime();
		
		for (int y = 0; y < height; y++) {
			if (y % 100 == 0)
				System.out.print(".");
			
			for (int x = 0; x < width; x++) {
				color.setColor(Color.BLACK);
				
				// Regular subsample an nxn grid for each pixel
				for (int p = 0; p < subSamples; p++) {
					for (int q = 0; q < subSamples; q++) {
						subSample.setColor(Color.BLACK);
						ray.p1.x = (double)x + (((double)p + 0.5d) / (double)subSamples);
						ray.p1.y = (double)y + (((double)q + 0.5d) / (double)subSamples);
						
						// Recalculate length of ray, probably not needed
						ray.set(ray.p0, ray.p1);
						
						primaryRays++;
						
						traceRay(ray, subSample, 1, null);
						
						color.add(subSample);
					}
				}
				
				color.scale(1.0d/Math.pow(subSamples, 2));

				g.setColor(color.getColor());
				g.drawLine(x,y,x,y);
			}
		}
		
		renderTime = new Date().getTime() - renderTime;
		
		System.out.println(" Done");
		System.out.println("  Render Time: " + ((float)renderTime/1000f) + "s");
		System.out.println("  Emitted Rays:");
		System.out.println("\tPrimary:    " + primaryRays);
		System.out.println("\tShadow:     " + shadowRays);
		System.out.println("\tReflection: " + reflectionRays + " (Max: " + maxReflect + ")");
		System.out.println("\tTotal:      " + (primaryRays + shadowRays + reflectionRays));
		System.out.println();
	}
	
	/**
	 * Main ray tracing method.  Finds the Surface hit by the ray and determines
	 * the color based on ambient, diffuse, specular components.
	 * 
	 * TODO Remove exclude paramter.
	 * 
	 * @param aRay - The ray to trace
	 * @param aColor - A DColor object to hold the calculated color
	 * @param recurseDepth - Current recusion depth
	 * @param exclude - Surface to exclude from intersection test
	 * @return
	 */
	protected Surface traceRay(Ray aRay, DColor aColor, int recurseDepth, Surface exclude) {
		if (recurseDepth > recursionLimit) { return null; }
		
		final IntersectRecord iRec = new IntersectRecord();
		// Find the object hit by the ray.
		Surface surface = getIntersectSurface(aRay, iRec, exclude);
		
		if (surface == null) {
			// No Surface was hit
			if (aRay.p1.z <= 0) {
				// Prevents odd behavoir with reflections, check z <= 0
				// (Without check, shadows appear around lights in reflections)
				for (int l = 0; l < scene.lights.size(); l++) {
					// TODO Should remove this and use a Plane-type surface object instead
					Light light = scene.lights.get(l);
					
					if (scene.zPlaneShadow) {
						final Ray shadowRay = new Ray(aRay.p1, light.getPosition());
						final IntersectRecord rec = new IntersectRecord();
						Surface shadower = getIntersectSurface(shadowRay, rec, surface);
						shadowRays++;
						
						if (shadower != null) {
							aColor.set(scene.bgColorShadow);
							return null;
						}
					}
				}
			}

			aColor.set(scene.bgColor);
			
			// No hit
			return null;
		}

		Material material = surface.getMaterial();
		
		// Calculate color at point of intersection
		Vector3d p = surface.getIntersect(aRay, iRec);			
		
		// Surface normal UV 
		Vector3d n = surface.getNormal(p);
		
		// UV from viewer to surface  d = p - rayOrigin
		Vector3d d = new Vector3d();
		//d.scaleAdd(-1, aRay.p0, p);
		d.sub(p, aRay.p0);
		d.normalize();
		
		// UV of reflection  r = -2(d dot n) * n
		Vector3d r = new Vector3d();
		r.scaleAdd(-2.0d * d.dot(n), n, d);
		r.normalize();
		
		for (int i = 0; i < scene.lights.size(); i++) {
			Light light = scene.lights.get(i);
			float specularFactor = 0.0f;
			float diffuseFactor = material.diffuse;
			
			// UV from point on surface to light
			Vector3d l = new Vector3d();
			l.scaleAdd(-1, p, light.getPosition());
			l.normalize();
			
			// Diffuse shading
			if (material.diffuse > 0.0f) {
				diffuseFactor *= (float) Math.max(0, n.dot(l));
			}
		
			// Determine if shadow exists
			if (scene.surfaceShadow) {
				final Ray shadowRay = new Ray(p, light.getPosition());
				final IntersectRecord rec = new IntersectRecord();
				Surface shadower = getIntersectSurface(shadowRay, rec ,surface);
				shadowRays++;
				
				if (shadower != null) {
					diffuseFactor = 0;
				}
			}
			
			// Set diffuse color
			aColor.x += diffuseFactor * material.color.x * light.getColor().x;
			aColor.y += diffuseFactor * material.color.y * light.getColor().y;
			aColor.z += diffuseFactor * material.color.z * light.getColor().z;

			// Specular highlight
			if (material.specular > 0.0) {
				specularFactor = (float) Math.pow(Math.max(0, l.dot(r)), material.shininess);
				specularFactor *= material.specular;
				
				aColor.scaleAdd(specularFactor, light.getColor(), aColor);
			}
		}
		
		// Ambient light (emission)
		if (material.emission > 0.0f) {
			aColor.scaleAdd(material.emission, material.color, aColor);
		}
		
		// Specular reflection
		if (material.reflect > 0.0f) {
			// Scale UV to length of original ray
			// r = r*|aRay| + p
			r.scaleAdd(aRay.distance, r, p);
			
			Ray reflRay = new Ray(p, r);
			DColor reflCol = new DColor(0d,0d,0d);
			
			reflectionRays++;
			
			if (recurseDepth+1 > maxReflect) {
				maxReflect = recurseDepth+1;
			}
			
			traceRay(reflRay, reflCol, recurseDepth + 1, null);
			
			aColor.x += reflCol.x * material.reflect * material.color.x;
			aColor.y += reflCol.y * material.reflect * material.color.y;
			aColor.z += reflCol.z * material.reflect * material.color.z;
		}
		
		// Transparency (with refraction)
		// TODO Make it work...
//			if (material.alpha < 1.0f) {
////			if (p is on a dielectric) {
//				DColor rColor = new DColor();
//				Vector3d k = new Vector3d();
//				double c = 0;
//				double refracN = 1.5d;
//				Vector3d t = new Vector3d();
//
//				if (d.dot(n) < 0) {
//					//System.out.print("+");
//					// On the inside of object
//					refract(d, n, 1.0d, refracN, t);
//					//refract(d, n, refracN, 1.0d, t);
//					
//					Vector3d negD = new Vector3d(d);
//					negD.negate();
//					c = negD.dot(n);
//					k.x = k.y = k.z = 1;
//				} else {
//					System.out.println("-");
//					k.x = Math.exp(-1 * Math.log(material.attenuation.x) * aRay.distance);
//					k.y = Math.exp(-1 * Math.log(material.attenuation.y) * aRay.distance);
//					k.z = Math.exp(-1 * Math.log(material.attenuation.z) * aRay.distance);
//					
//					Vector3d negN = new Vector3d(n);
//					negN.negate();
//					
//					//if (refract (d, negN, 1/refracN ,t)) {
//					//if (refract (d, negN, 1.0d, refracN ,t)) {
//					if (refract (d, negN, refracN, 1.0d ,t)) {
//						c = t.dot(n);
//					} else {
//						// total internal reflection
//						r.scale(aRay.distance);
//						Ray reflectionRay = new Ray(p, r);
//						traceRay(reflectionRay, rColor, 1, null);
//						
//						rColor.x = k.x * rColor.x;
//						rColor.y = k.y * rColor.y;
//						rColor.z = k.z * rColor.z;
//
//						aColor.add(rColor);
//					}
//				}
//				
//				double R0 = Math.pow((refracN - 1), 2) / Math.pow((refracN + 1), 2),
//						R = R0 + ((1 - R0) * Math.pow((1 - c), 5));
//				
//				DColor r1Color = new DColor();
//				DColor r2Color = new DColor();
//
//				Vector3d r1 = new Vector3d(r);
//				r1.scaleAdd(aRay.distance, r1, p);
//								
//				Vector3d T = new Vector3d(t);
//				T.scaleAdd(aRay.distance, T, p);
//				
//				Vector3d pp = new Vector3d();
//				pp.scaleAdd(0.001d, t, p);
//				
//				Ray r1Ray = new Ray(p, r1), 
//					r2Ray = new Ray(pp, T);
//				
//				//System.out.println(r2Ray.p0 + " " + r2Ray.p1);
//				
//				traceRay(r1Ray, r1Color, recurseDepth + 1, null);
//				traceRay(r2Ray, r2Color, recurseDepth + 1, null);
//				
//				r1Color.scale(R);
//				r2Color.scale(1.0d - R);
//				
//				rColor.add(r1Color, r2Color);
//				
//				rColor.x *= k.x;
//				rColor.y *= k.y;
//				rColor.z *= k.z;
//				
//				
//				
//				//System.out.println(rColor);
//				
//				//return k (R color(p + tr) + (1-R) color(p + tt));
//				aColor.set(rColor);
////			}
//				
//				//DColor refractionColor = refraction(aRay, d, n, p, r, surface, aColor);
//				
//				//aColor.set(refractionColor);
//				//System.out.println(refractionColor);
//			}
		
		// Make sure to not exceed the range [0..1]
		aColor.clamp(0.0d, 1.0d);
		
		return surface;
	}
	
	
	/**
	 * Calculates the refraction ray r given the surface normal and viewer
	 * normal vectors.
	 * 
	 * Returns false if total internal refraction.
	 * 
	 * @param d - Viewer normal
	 * @param n - Surface normal
	 * @param n1 - "From" refraction index
	 * @param n2 - "To" refraction index
	 * @param t - Vector3d to hold the refraction vector
	 * @return
	 */
//	private boolean refract(Vector3d d, Vector3d n, double n1, double n2, Vector3d t) {
//		//double nt = 1.0d;
//		double dn = d.dot(n);
//		double sqrt;
//		sqrt = (n1 * n1) * (1 - (dn*dn)) / (n2 * n2);
//		sqrt = 1 - sqrt;
//		
//		if (sqrt < 0) {
//			return false;
//		}
//		
//		sqrt = Math.sqrt(sqrt);
//		
//		Vector3d u = new Vector3d(n);
//		u.scale(sqrt);
//		
//		Vector3d v = new Vector3d();
//		
//		v.scaleAdd(dn, n);  // n(d.n)
//		v.sub(d, v);  // d - n(d.n);
//		v.scale(n1 / n2);
//
//		t.sub(v, u);		
//		t.normalize();
//				
//		return true;
//	}
	
	/**
	 * Returns the Surface object that a ray intersects or null if no such
	 * object exists.
	 * 
	 * TODO Move into Surface class once container surfaces are implemented
	 * TODO Remove exclude parameter
	 * 
	 * @param r
	 * @param iRec
	 * @param exclude
	 * @return
	 */
	private Surface getIntersectSurface(Ray r, IntersectRecord iRec, Surface exclude) {
		Surface s = null;
		double sT = Double.POSITIVE_INFINITY;
		
		for (int i = 0; i < scene.surfaces.size(); i++) {
			Surface o = scene.surfaces.get(i);

			if (o != exclude) {
				// o.t > 0 -> Ensure object is on or infront of ray's origin
				if (o.intersects(r, iRec) && (iRec.t > 0)) {
					if ((s == null) || (iRec.t < sT)) {
						s = o;
						sT = iRec.t;
					}
				}
			}
		}
		
		iRec.t = sT;
		iRec.intersectSurface = s;
		
		return s;
	}
	
	/**
	 * Set the subsample value.  Number of primary rays needed for a scene is
	 * determined by the subsample value i as follows:
	 * 		Primary Rays = (width * height) * i^2
	 * 
	 * Values larger than 4 should be used with caution.
	 * 
	 * @param i
	 */
	public void setSubsample(int i) {
		if (i < 1) i = 1;
		
		subSamples = i;
	}
	
	/**
	 * Set the recursion limit for ray tracing of reflections and refraction.
	 * 
	 * @param i
	 */
	public void setRecursionLimit(int i) {
		if (i < 1) i = 1;
		
		recursionLimit = i;
	}
	
	
	public String toString() {
		return   "Raytracer: Subsamples="+subSamples+"^2, ReflectionLim="+recursionLimit
		       + "\n           "+scene.surfaces.size() + " surfaces, "+scene.lights.size() + " lights";
	}
}
