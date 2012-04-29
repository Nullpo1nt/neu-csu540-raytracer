package raytracer.materials;

import raytracer.DColor;

/**
 * Material class.  Contains various factors for determining the final color or
 * a Surface.
 * 
 * @author Bryant Martin
 *
 */
public class Material {
	public DColor color;
	
	public float diffuse = 0.8f;
	public float emission = 0.2f;
	public float specular = 0.8f;
	public float shininess = 8.0f;
	public float reflect = 0.7f;

	public float refractionIndex = 1.2f;
	public DColor attenuation = new DColor(0.83,0.83,0.83);
	
	
	public Material() {
	
	}
	
	public Material(DColor c) {
		color = c;
	}
	
	public String toString() {
		String s = "";
		
		s += "\tMaterial {";
		s +=  " Color: " + color;
		s += ", Diffuse: " + diffuse;
		s += ", Emission: " + emission;
		s += ", Specular: " + specular;
		s += ", Shininess: " + shininess;
		s += ", Reflect: " + reflect;
		s += " }";
		return s;
	}
}
