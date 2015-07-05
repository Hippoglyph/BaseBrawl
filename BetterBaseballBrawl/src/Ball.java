public class Ball extends PhysicalObject {
	
	private String redImgsrc = "red_ball.png";
	private String blueImgsrc = "blue_ball.png";
	private String idleImgsrc = "idle_ball.png";
	public HitBox ballBox;
	private Sound bounceSound;
	private int owner = 0;

	public Ball(int x, int y, int width, int height, Board b) {
		super(x,y,0.2f,b,0,0);
		super.width = width;
		super.height = height;
		super.setImage(idleImgsrc);
		ballBox = new HitBox(-super.width/6, -super.height/6, super.width/3, super.height/3, this);
		bounceSound = new Sound("ball_bounce.wav");
	}

	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}

	public void update() {
		moveY();
		moveX();
		ballBox.update();
	}
	
	public void setOffsetDy(float offset){
		dy += offset;
	}
	
	public void setOffsetDx(float offset){
		dx += offset;
	}

	private float bounceY(float dy){
		if(super.Y+super.height/2 + Board.YInset > (super.b.getHeight())){
			dy *= -0.8f;
			super.Y = super.b.getHeight() - super.height/2 - Board.YInset;
			//collision(this, new PhysicalObject(this.X, super.b.getHeight()+10, 10000, this.b, 0, 0));
			playBounce(dy);
		}
		else if(super.Y-super.height/2 - Board.YInset < 0){
			dy *= -0.8f; 
			super.Y = 0+super.height/2 + Board.YInset;
			playBounce(dy);
		}
		if (super.Y >= super.b.getHeight() - super.height/2 - Board.YInset)
			super.friction();
		return dy;
	}
	
	private float bounceX(float dx){
		if(super.X + super.width/2 + Board.XInset > (super.b.getWidth())){
			dx *= -0.95f; 
			super.X = super.b.getWidth() - super.width/2 - Board.XInset;
			playBounce(dx);
		}
		else if(super.X - super.width/2 - Board.XInset< 0){
			dx *= -0.95f; 
			super.X = 0 + super.width/2 + Board.XInset;
			playBounce(dx);
		}
		return dx;
	}
	
	private void moveY() {
		dy = super.gravity(this.dy, 1);
		dy = bounceY(dy);
		super.Y += dy*super.b.getRealTick();
	}
	
	private void moveX() {
		dx = bounceX(dx);
		super.X += dx*super.b.getRealTick();
	}

	public void setAsPlayer(int player){
		switch(player){
		case 1:
			super.setImage(blueImgsrc);
			b.enableSlowMotion = true;
			owner = 1;
			break;
		case 2:
			super.setImage(redImgsrc);
			b.enableSlowMotion = true;
			owner = 2;
			break;
		default:
			super.setImage(idleImgsrc);
			b.enableSlowMotion = false;
			owner = 0;
			break;
		}
	}
	
	public int getOwner(){
		return owner;
	}
	
	private void playBounce(float speed) {
		
		if(Math.abs(speed) > 1000){
			bounceSound.play(1);
		}
		else if(Math.abs(speed) > 200){
			double dB = (0.5*Math.abs(speed))/(1000-200);
			bounceSound.play((float)dB);
		}
	}
	
	public void closeSound(){
		bounceSound.close();
	}
}
