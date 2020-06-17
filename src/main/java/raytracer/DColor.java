package raytracer;

import java.awt.Color;
import javax.vecmath.Vector3d;

/**
 * Extension of Vector3d to represent color. 
 * 
 * @author Bryant Martin
 *
 */
public class DColor extends Vector3d {
	private static final long serialVersionUID = -2638261488050334516L;
	
	
	public DColor() {
		this(0d,0d,0d);
	}
	
	public DColor(double r, double g, double b) {
		super(r, g, b);
	}
	
	public DColor(Color c) {
		setColor(c);
	}
	
	public DColor(Vector3d v) {
		set(v);
	}
	
	
	public double getRed()   { return x; }
	public double getGreen() { return y; }
	public double getBlue()  { return z; }
	
	public void setColor(Color c) {
		x = (double) c.getRed()   / 255;
		y = (double) c.getGreen() / 255;
		z = (double) c.getBlue()  / 255;
	}
	
	public Color getColor() {
		return new Color((float)x, (float)y, (float)z);
	}
}
