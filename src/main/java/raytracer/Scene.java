package raytracer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.vecmath.Vector3d;
import raytracer.materials.*;
import raytracer.surfaces.*;

/**
 * A scene to render, includes Surfaces, lights and various rendering settings.
 * 
 * @author Bryant Martin
 *
 */
public class Scene {
	static final String SCENE = "scene\\s*\\{?",
						LIGHT = "light\\s*\\{?",
						SPHERE = "sphere\\s*\\{?",
						MATERIAL = "material\\s*\\{?",
						BACKGROUND = "background\\s*=.*;",
						BACKGROUND_SHADOW = "shadow\\s*=.*;",
						SHADOW_ZPLANE="zplaneshadow\\s*=.*;",
						SHADOW_SURFACE="surfaceshadow\\s*=.*;",
						CAMERA = "camera\\s*=.*;",
						POSITION = "position\\s*=.*;",
						COLOR = "color\\s*=.*;",
						DIFFUSE = "diffuse\\s*=.*;",
						EMISSION = "emission\\s*=.*;",
						SPECULAR = "specular\\s*=.*;",
						SHININESS = "shininess\\s*=.*;",
						REFLECT = "reflect\\s*=.*;",
						RADIUS = "radius\\s*=.*;",
						CBRAK = ".*\\}",
						COMMENT = "//";
	
	public ArrayList<Surface> surfaces;
	public ArrayList<Light> lights;
	
	public Vector3d camera = new Vector3d(0,0,0);
	public DColor bgColor = new DColor(0,0,0);
	public DColor bgColorShadow = new DColor(0,0,0);
	
	public boolean surfaceShadow = true,
				   zPlaneShadow = true;
	
	
	public Scene(File f) throws FileNotFoundException {
		this (new ArrayList<Surface>(), new ArrayList<Light>());
		
		parseFile(new BufferedReader(new FileReader(f)));
	}
	
	public Scene(ArrayList<Surface> ss, ArrayList<Light> ls) {
		surfaces = ss;
		lights = ls;
	}
	
	
	/**
	 * Very fragile parser.  
	 * 
	 * Format is something like this:
	 * 
	 * Mode {
	 *   parameter = value;
	 *   parameter2 = value;
	 *   
	 *   NestedMode {
	 *     ...
	 *   }
	 * }
	 * 
	 * 
	 * -Scene
	 *   |- background -> Vector, background color
	 *   |- shadow -> Vector, background shadow color
	 *   |- camera -> Vector, camera position
	 *   |- zplaneshadow -> Boolean, Shadows on Z-Plane (z=0)
	 *   |- surfaceshadow -> Boolean, Shadows on Surfaces
	 *   |- Light
	 *       |- position -> Vector, Position of light
	 *       |- color -> Vector, Color of light
	 *   |- Sphere
	 *       |- position -> Vector, position of sphere
	 *       |- radius -> Double, sphere radius
	 *       |- color -> Vector, Diffuse color of sphere
	 *       |- Material
	 *           |- diffuse -> Double, Diffuse scale factor
	 *           |- emission -> Double, Ambient scale factor
	 *           |- specular -> Double, Specular highlight scale factor
	 *           |- shininess -> Double, Specular highlight exponent
	 *           |- reflect -> Double, Reflection scale factor
	 * 
	 * Each value must be on a seperate line currently.
	 * 
	 * @param in
	 */
	protected void parseFile(BufferedReader in) {
		boolean scene = false,
				light = false,
				sphere = false,
				material = false;
		
		double radius = 0;
		Vector3d position = null;
		DColor color = null;
		Light alight = null;
		Material amaterial = null;
		
		try {
			while (in.ready()) {
				String line = in.readLine();
				
				// Lowercase, remove lead/trailing white space
				int i = line.indexOf(COMMENT);
				line = (i >= 0) ? line.substring(0, i) : line;
				line = line.toLowerCase().trim();
				
				if(line.length() == 0) {
					continue;
				}
				
				if (scene) {
					if (light) {
						if (line.matches(POSITION)) {
							position.set(getVector(line));
						} else if (line.matches(COLOR)) {
							color.set(getVector(line));
						} else if (line.matches(CBRAK)) light = false;
					} 
					
					else if (sphere) {
						
						if (material) {
							if (line.matches(DIFFUSE)) {
								amaterial.diffuse = (float)getDouble(line);
							} else if (line.matches(EMISSION)) {
								amaterial.emission = (float)getDouble(line);
							} else if (line.matches(SPECULAR)) {
								amaterial.specular = (float)getDouble(line);
							} else if (line.matches(SHININESS)) {
								amaterial.shininess = (float)getDouble(line);
							} else if (line.matches(REFLECT)) {
								amaterial.reflect = (float)getDouble(line);
							} else if (line.matches(CBRAK)) {
								material = false; 
							}	
						} 
						
						
						else if (line.matches(POSITION)) {
							position = getVector(line);
						} else if (line.matches(COLOR)) {
							color = new DColor();
							color.set(getVector(line));
							amaterial.color = color;
						} else if (line.matches(RADIUS)) {
							radius = getDouble(line);
						} else if (line.matches(MATERIAL)) {
							material = true;
						} else if (line.matches(CBRAK)) {
							surfaces.add(new Sphere(position, radius, color, amaterial));
							sphere = false;
						}
					} 
					
					
					else if (line.matches(LIGHT)) {
						position = new Vector3d();
						color = new DColor();
						alight = new Light(position, color);
						
						lights.add(alight);
						
						light = true;
					} 
					
					else if (line.matches(SPHERE)) {
						amaterial = new Material();
						sphere = true;
					} 
					
					else if (line.matches(BACKGROUND)) {
						bgColor = new DColor(getVector(line));
					} else if (line.matches(CAMERA)) {
						camera = getVector(line);
					} else if (line.matches(BACKGROUND_SHADOW)) {
						bgColorShadow = new DColor(getVector(line));
					} else if (line.matches(CBRAK)) {
						scene = false;
					} else if (line.matches(SHADOW_SURFACE)){
						surfaceShadow = getBoolean(line);
					} else if (line.matches(SHADOW_ZPLANE)){
						zPlaneShadow = getBoolean(line);
					}
				} else if (line.matches(SCENE)) {
					scene = true;
				} else {
					System.out.println("Parse Error >> " + line);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a Vector3d object from a string.  Expects the vector values to be
	 * in the following format:
	 * 	 (x,y,z)
	 * 		x, y, z -> Real numbers
	 * 
	 * Ignores any surrounding text.
	 * 
	 * @param line
	 * @return
	 */
	protected Vector3d getVector(String line) {
		double x,y,z;
		int i;
		
		line = line.substring(line.indexOf("(")+1); 
		i = line.indexOf(",");
		x = getDouble(line.substring(0, i));
		
		line = line.substring(i+1);
		i = line.indexOf(",");
		y = getDouble(line.substring(0, i));
		
		line = line.substring(i+1);
		i = line.indexOf(")");
		z = getDouble(line.substring(0, i));
		
		return new Vector3d(x,y,z);
	}
	
	/**
	 * Returns a double from a string of text.
	 * 
	 * Not very robust, removes any character that is not a '-', '.', or 0-9
	 * before processing.
	 * 
	 * @param line
	 * @return
	 */
	protected double getDouble(String line) {
		line = line.replaceAll("[^-\\.0-9]", "");
		
		try {
			return Double.parseDouble(line);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	/**
	 * Return a boolean from a string.
	 * 
	 * Expects either "true" or "false" between an '=' and ';' characters.
	 * 
	 * @param line
	 * @return
	 */
	protected boolean getBoolean(String line) {
		line = line.substring(line.indexOf("=")).replaceAll("=|;", "");
		
		return Boolean.parseBoolean(line);
	}
	
	
	public String toString() {
		String s = "Scene:";
		s += "\n\tCamera:          " + camera;
		s += "\n\tBackground:      " + bgColor;
		s += "\n\tBgShadow:        " + bgColorShadow;
		s += "\n\tZ-Plane Shadows: " + zPlaneShadow;
		s += "\n\tSurface Shadows: " + surfaceShadow;
		s += "\n";
		
		for (int i = 0; i < lights.size(); i++) {
			s += "Light " + (i+1) + ":\n";
			s += lights.get(i).toString();
		}
		
		for (int i = 0; i < surfaces.size(); i++) {
			s += "Sphere " + (i+1) + ":\n";
			s += surfaces.get(i).toString();
		}
		
		return s;
	}
}
