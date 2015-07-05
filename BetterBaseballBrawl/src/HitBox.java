
public class HitBox {
	private float positionX;
	private float positionY;
	private float width;
	private float height;
	public int centerX;
	public int centerY;
	private PhysicalObject object;
	public float XfromObject;
	public float YfromObject;
	public HitBox(float x, float y, float w, float h, PhysicalObject p){
		width = w;
		height = h;
		object = p;
		XfromObject = x;
		YfromObject = y;
		update();
	}
	
	public void update(){
		setPos(object.X + XfromObject, object.Y + YfromObject);
	}
	public float getWidth(){
		return width;
	}
	public float getHeight(){
		return height;
	}
	public void setPos(float x, float y){
		positionX = x;
		positionY = y;
		centerX = (int)(x+width/2);
		centerY = (int)(y+height/2);
	}
	public float getPosX(){
		return positionX;
	}
	public float getPosY(){
		return positionY;
	}
	
	public boolean collisionCheck(HitBox other){
		if(this.positionX+width > other.positionX && this.positionX < other.positionX+other.getWidth()
			&& this.positionY+height > other.positionY && this.positionY < other.positionY+other.getHeight())
			return true;
		return false;
	}
}
