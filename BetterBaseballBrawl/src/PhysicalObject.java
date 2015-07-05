import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

public class PhysicalObject {
	public float Y;
	public float X;
	protected float dy;
	protected float dx;
	public float width;
	public float height;
	public float mass;
	protected Board b;
	public Image image;
	private static final float GRAVITY = 9.82f;
	protected final static float METER = 120f;
	
	public PhysicalObject(float x, float y, float mass, Board b, float dx, float dy){
		this.Y = y;
		this.X = x;
		this.b = b;
		this.mass = mass;
		this.dx = dx;
		this.dy = dy;
	}
	public float gravity(float dy, float multiple){
		dy += GRAVITY*METER * b.getRealTick() * multiple;
		return dy;
	}
	
	public void friction(){
		dx *= 0.95f;
	}
	
	protected void setImage(String imgSrc){
		try {
			image = ImageIO.read(this.getClass().getResourceAsStream("Textures/"+imgSrc));
		} catch (IOException e) {
			System.err.println(e);
			System.err.println("Cant find " + imgSrc + " for: "
								+ getClass().getName());
			System.exit(1);
		}
	}
	
	protected void collision(PhysicalObject o1, PhysicalObject o2){
		float materialConstant = 0.9f;
		float angleO1 = getAngle(o1.dx, o1.dy);
		float angleO2 = getAngle(o2.dx, o2.dy);
		float angleBoth = getAngle(o1.X-o2.X, o1.Y-o2.Y);
		
		float velocityBefore1 = (float)Math.sqrt(Math.pow(o1.dx, 2) + Math.pow(o1.dy,2));
		float velocityBefore2 = (float)Math.sqrt(Math.pow(o2.dx, 2) + Math.pow(o2.dy,2));
		
		float velocityAfter1 =
				(materialConstant*o2.mass*(velocityBefore2-velocityBefore1)
						+o1.mass*velocityBefore1+o2.mass*velocityBefore2)/(o1.mass+o2.mass);
		float velocityAfter2 = 
				(materialConstant*o1.mass*(velocityBefore1-velocityBefore2)
						+o2.mass*velocityBefore2+o1.mass*velocityBefore1)/(o2.mass+o1.mass);
		
		float massSum = o1.mass+o2.mass;
		
		angleO1 = angleBoth + angleO1*(o1.mass/massSum);
		angleO2 = angleBoth + angleO2*(o2.mass/massSum);
		
		o1.dx = (float)Math.cos(angleO1) * Math.abs(velocityAfter1);
		o1.dy = (float)Math.sin(angleO1) * Math.abs(velocityAfter1);
		o2.dx = -(float)Math.cos(angleO2) * Math.abs(velocityAfter2);
		o2.dy = (float)Math.sin(angleO2) * Math.abs(velocityAfter2);
	} 
	
	private float getAngle(float dx, float dy) {
		float angle;
		float hypo = (float)Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2));
		if(hypo == 0)
			return 0;
		angle = (float)Math.asin(dy/hypo);
		if(dx < 0)
			angle = (float)Math.PI-angle;
		return angle;
	}
}
