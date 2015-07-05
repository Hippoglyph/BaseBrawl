import java.util.Random;

public class Bot {
	
	private int idealDistanceFromBall = 100;
	private int idealJumpHeight = 200;
	
	private Player player;
	private Ball ball;
	
	private Random rnd;

	public Bot(Player player, Ball ball){
		this.player = player;
		this.ball = ball;
		this.rnd = new Random();
	}

	public void update() {
		moveX();
		moveY();
		swing();
	}
	
	private void swing() {
		if(ball.X - player.X < idealDistanceFromBall && ball.X - player.X > -idealDistanceFromBall &&
				ball.Y - player.Y < idealDistanceFromBall && ball.Y - player.Y > -idealDistanceFromBall){
			if(ball.getOwner() == player.player && rnd.nextInt(50) == 0)// 1/50 chance
				player.keyPressed(player.c.SWING);
			else if(rnd.nextInt(15) == 0){
				player.keyPressed(player.c.SWING);
			}
		}
		else
			player.keyReleased(player.c.SWING);
	}

	private void moveY(){
		if(ball.Y < idealJumpHeight)
			player.keyPressed(player.c.UP);
		else if(ball.Y < 2*idealJumpHeight && ball.dy < 0)
			player.keyPressed(player.c.UP);
		else
			player.keyReleased(player.c.UP);
	}
	
	private void moveX(){
		if(ball.X - player.X > idealDistanceFromBall){
			player.keyPressed(player.c.RIGHT);
		}
		else if(ball.X - player.X < -idealDistanceFromBall){
			player.keyPressed(player.c.LEFT);
		}
		else{
			player.keyReleased(player.c.RIGHT);
			player.keyReleased(player.c.LEFT);
		}
	}

}
