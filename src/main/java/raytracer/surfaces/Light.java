package raytracer.surfaces;

import javax.vecmath.Vector3d;
import raytracer.DColor;

/**
 * Represents a light.
 * 
 * TODO Make a subclass of Surface
 * 
 * @author Bryant Martin
 *
 */
public class Light { //extends Surface {
	Vector3d position;
	DColor color;
	
	public Light(Vector3d p, DColor c) {
		position = p;
		color = c;
	}
	
	public Vector3d getPosition() { return position; }
	public DColor getColor() { return color; }
	
	public String toString() {
		String s = "";
		
		s +=   "\tPosition: " + position;
		s += "\n\tColor:    " + color;
		s += "\n";
		return s;
	}
}
