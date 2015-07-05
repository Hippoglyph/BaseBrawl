import java.util.ArrayList;
import java.util.Random;

public class Player extends PhysicalObject{

	private int keydx = 0;
	private int keydy = 0;
	private boolean holdUp;
	private boolean holdDown;
	private boolean holdLeft;
	private boolean holdRight;
	private boolean holdSwing;
	public int player;
	public int direction;
	protected static float speedMultiple = 1;
	
	public ArrayList<HitBox> hitBoxes = new ArrayList<HitBox>();
	
	private static final float SPEED = 8.0f;
	
	//For animation:
	public int dispInt = 0;
	public int SSRows = 5;
	public int SSCol = 3;
	public int SSWidth = 687;
	public int SSHeight = 536;
	private String blueSource = "Blue.png";
	private String redSource = "Red.png";
	private String SpriteSheet;
	private int[] IdleAnim = {0,1,2};
	private int[] RunAnim = {3,4,5,6};
	private int[] FallAnim = {7,8};
	private int[] SwingAnim = {9,10,11,12,13};
		
	private Ball ball;
	private Sound batHitSound;
	private Sound swingSound1;
	private Sound swingSound2;
	private Sound swingSound3;
	private Sound ballHitSound;
	
	public Controls c;
	private int HP = 3;
	public String HPString = "♥♥♥";
	public Player(int x, int y, int w, int h, int direct, Controls c, Board b, Ball ball, int player){
		super(x,y,70,b,0,0);
		
		this.player = player;
		this.ball = ball;
		direction = direct;
		super.width = w;
		super.height = h;
		if(player == 2)
			SpriteSheet = redSource;
		else
			SpriteSheet = blueSource;
		super.setImage(SpriteSheet);
		this.c = c;
		
		batHitSound =  new Sound("BatBall.wav");
		swingSound1 = new Sound("SwingPlayer"+player+"_1.wav");
		swingSound2 = new Sound("SwingPlayer"+player+"_2.wav");
		swingSound3 = new Sound("SwingPlayer"+player+"_3.wav");
		ballHitSound = new Sound("BallHit.wav");
		
		setHitboxes();
	}
	public void move() {
		keyHold();
		super.X += dx + keydx*SPEED*super.METER*b.getRealTick();
		super.Y += dy*b.getRealTick();
	}
		
	public void update(){
		super.friction();
		dy = super.gravity(this.dy, 1);
		dy += keydy*b.getRealTick();
		if(super.Y >= super.b.getHeight() - super.height/2 - Board.YInset){
			dy = 0; super.Y = super.b.getHeight() - super.height/2 - Board.YInset;
		}
		else if(super.Y < super.height/2 + Board.YInset){
			dy = -0.1f*dy; super.Y = super.height/2 + Board.YInset;
		}
		if(super.X >= super.b.getWidth() - super.width/3 - Board.XInset){
			dx *= -0.2; super.X = super.b.getWidth() - super.width/3 - Board.XInset;
		}
		else if(super.X <= super.width/3 + Board.XInset){
			dx *= -0.2; super.X = super.width/3 + Board.XInset;
		}
		move();
		if(keydx != 0 && !isSwinging && dy == 0){
			if(!isRunning)
				time = 0;
			isRunning = true;
			isIdle = false;
			isFalling = false;
			isSwinging = false;
		}
		if(keydx == 0 && !isSwinging && dy == 0){
			if(!isIdle)
				time = 0;
			isRunning = false;
			isIdle = true;
			isFalling = false;
			isSwinging = false;
		}
		if(dy != 0 && !isSwinging){
			if(!isFalling)
				time = 0;
			isRunning = false;
			isIdle = false;
			isFalling = true;
			isSwinging = false;
		}
		if(keydx > 0)
			direction = -1;
		if(keydx < 0)
			direction = 1;
		updateAnimation();
		
		if(ballHit.collisionCheck(ball.ballBox) && player != ball.getOwner() && ball.getOwner() != 0){
			collision(ball, this);
			ballHitSound.play();
			ball.setAsPlayer(0);
			speedMultiple = 1;
			HP--;
			HPString = "";
			for(int i=0; i<HP;i++)
				HPString += "♥";
			if(HP < 1)
				b.setLoser(player);
		}
		
		for(HitBox hb : this.hitBoxes){
			hb.update();
		}
	}

	public void keyPressed(int key){
		if(key == c.LEFT)
			holdLeft = true;
		if(key == c.RIGHT)
			holdRight = true;
		if(key == c.UP)
			holdUp = true;
		if(key == c.DOWN)
			holdDown= true;
		if(key == c.SWING && !isSwinging){
			if(direction < 0)
				updateHitsR();
			if(direction > 0)
				updateHitsL();
			
			time = 0;
			isRunning = false;
			isIdle = false;
			isFalling = false;
			isSwinging = true;
			playSwingSound();
		}
		//System.out.println(e);
	}
	public void keyReleased(int key) {
		if(key == c.LEFT)
			holdLeft = false;
		if(key == c.RIGHT)
			holdRight = false;
		if(key == c.UP)
			holdUp = false;
		if(key == c.DOWN)
			holdDown= false;
		if(key == c.SWING)
			holdSwing = false;
	}
	boolean letGoY;
	private void keyHold(){
		keydx = 0;
		keydy = 0;
		
		if(holdUp && dy == 0){
			dy = -10.0f*super.METER;
			letGoY = false;
		}
		if(holdUp && !letGoY)
			keydy = 0;
		else{
			letGoY = true;
			keydy += 1000;
		}
		if(holdDown)
			keydy += 1000;
		
		if(holdLeft){
			dx = 0;
			keydx -= 1;
		}
		if(holdRight){
			dx = 0;
			keydx += 1;
		}
	}

	
	private boolean isIdle = false;
	private boolean isRunning = false;
	private boolean isFalling = false;
	private boolean isSwinging = false;
	private int frame = 0;
	private void nextFrame(){
		if (isIdle){
			if (frame >= IdleAnim.length)
				frame = 0;
			dispInt = IdleAnim[frame];
		}
		else if (isRunning){
			if (frame >= RunAnim.length)
				frame = 0;
			dispInt = RunAnim[frame];
		}
		else if (isFalling){
			if (frame >= FallAnim.length)
				frame = 0;
			dispInt = FallAnim[frame];
		}
		else if (isSwinging){
			if (frame >= SwingAnim.length){
				frame = 0;
				isSwinging = false;
			}
			dispInt = SwingAnim[frame];
		}
		else
			frame = 0;
		frame++;
	}
	
	private int time = 0;
	public void updateAnimation(){
		if(isIdle){
			if(time > 0.05/b.getRealTick()){
				time = 0;
			if(time == 0)
				nextFrame();
			}
		}
		else if(isRunning){
			if(time > 0.05/b.getRealTick()){
				time = 0;
			if(time == 0)
				nextFrame();
			}
		}
		else if(isFalling){
			if(time > 0.05/b.getRealTick()){
				time = 0;
			if(time == 0)
				nextFrame();
			}
		}
		else if(isSwinging){
			if(time > 0.01/b.getRealTick()){
				time = 0;
			if(time == 0)
				nextFrame();
			}
		}
		else 
			time = 0;
		time++;
	}
	private HitBox hitBallTL;
	private HitBox hitBallBL;
	private HitBox hitBallTR;
	private HitBox hitBallBR;
	private HitBox ballHit;
	
	private final float pHitWidth = 0.1f;
	private final float pHitHeight = 0.8f;
	private final float ballHitHeight = 1.0f;
	private final float ballHitWidth = 1.0f;
	private final float topHalfHeight = 0.8f;
	
	public void setHitboxes(){
		ballHit = new HitBox(-super.width*pHitWidth/2, -super.height*pHitHeight/2, pHitWidth*super.width, pHitHeight*super.height, this);
			hitBoxes.add(ballHit);
		
		hitBallTL = new HitBox(-super.width*ballHitWidth/2, -super.height*ballHitHeight/2,
				Math.abs(super.width*(ballHitWidth-pHitWidth)/2), super.height*topHalfHeight, this);
			hitBoxes.add(hitBallTL);
		hitBallBL = new HitBox(hitBallTL.XfromObject, hitBallTL.YfromObject+hitBallTL.getHeight(),
				hitBallTL.getWidth(), this.height-hitBallTL.getHeight(), this);
			hitBoxes.add(hitBallBL);
		hitBallTR = new HitBox(-hitBallTL.XfromObject-hitBallTL.getWidth(), hitBallTL.YfromObject, hitBallTL.getWidth(), hitBallTL.getHeight(), this);
			hitBoxes.add(hitBallTR);
		hitBallBR = new HitBox(-hitBallBL.XfromObject-hitBallBL.getWidth(), hitBallBL.YfromObject, hitBallBL.getWidth(), hitBallBL.getHeight(), this);
			hitBoxes.add(hitBallBR);
	}
	
	private static final float changeConst = 0.2f;
	private static final float bbmassConst = 1.1f;
	private static final float bbspeedConst = 12.0f;
	private void updateHitsL() {
		if(hitBallTL.collisionCheck(ball.ballBox)){
			collision(ball, new PhysicalObject(super.X, hitBallTL.centerY, bbmassConst+this.mass, this.b,  -speedMultiple*bbspeedConst*super.METER, 0));
			ball.setAsPlayer(player);
			speedMultiple += changeConst;
			playBallHitSound();
		}
		if(hitBallBL.collisionCheck(ball.ballBox)){
			collision(ball, new PhysicalObject(super.X, hitBallBL.centerY+hitBallBL.getHeight()/2, bbmassConst+this.mass, this.b,  -speedMultiple*bbspeedConst*super.METER, 0));
			ball.setAsPlayer(player);
			speedMultiple += changeConst;
			playBallHitSound();
		}
	}
	private void updateHitsR() {
		if(hitBallTR.collisionCheck(ball.ballBox)){
			collision(ball, new PhysicalObject(super.X, hitBallTR.centerY, bbmassConst+this.mass, this.b,  speedMultiple*bbspeedConst*super.METER, 0));
			ball.setAsPlayer(player);
			speedMultiple += changeConst;
			playBallHitSound();
		}
		if(hitBallBR.collisionCheck(ball.ballBox)){
			collision(ball, new PhysicalObject(super.X, hitBallBR.centerY+hitBallBR.getHeight()/2, bbmassConst+this.mass, this.b,  speedMultiple*bbspeedConst*super.METER, 0));
			ball.setAsPlayer(player);
			speedMultiple += changeConst;
			playBallHitSound();
		}
	}
	
	private void playBallHitSound(){
		if(speedMultiple >= 2)
			batHitSound.play(1);
		else
			batHitSound.play((float)0.5*speedMultiple);
	}
	
	private void playSwingSound(){
		Random rnd = new Random();
		int r = rnd.nextInt(3)+1;
		switch(r){
		case 1:
			swingSound1.play(0.5f);
			break;
		case 2:
			swingSound2.play(0.5f);
			break;
		default:
			swingSound3.play(0.5f);
			break;
		}
	}
	
	public void closeSounds(){
		batHitSound.close();
		swingSound1.close();
		swingSound2.close();
		swingSound3.close();
		ballHitSound.close();
	}
}
