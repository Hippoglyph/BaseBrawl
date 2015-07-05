import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javafx.scene.Parent;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.JPanel;


public class Board extends JPanel implements ActionListener {
	private Ball ball;
	private Player player1;
	private Player player2;
	private Bot bot;
	public boolean GameOver = false;
	
	public static final int XInset = 60;
	public static final int YInset = 28;
	
	private Timer timer;
	private static float timeScaling = 1.0f;
	private static final float TimeBoost = 0.8f;
	private static final float TickInterval = 0.005f;
	private int tickCount = 0;
	
	private Sound matchStartSound = new Sound("MatchStart.wav");
	private Sound matchEndSound = new Sound("MatchEnd.wav");
	private Sound backgroundSound = new Sound("BackGround.wav");
	private Image imgBG;
	
	private boolean playSolo = true;
	
	public Board(boolean playSolo){
		ball = new Ball(ApplicationFrame.WIDTH/2, 60, 50, 50, this);
		player1 = new Player(ApplicationFrame.WIDTH-100,ApplicationFrame.HEIGHT-200, 260, 200, 1, new Controls(), this, ball,1);
		player2 = new Player(100,ApplicationFrame.HEIGHT-200, 260, 200, -1, new Controls(), this, ball,2);
		
		player1.c.loadControls(true);
		player2.c.loadControls(false);
		
		this.playSolo = playSolo;
		
		setBackground(Color.GRAY);
		setBackgroundImg("BG.jpg");
		setDoubleBuffered(true);
		setFocusable(true);
		if(!playSolo)
			addKeyListener(new TAdapter());
		else{
			addKeyListener(new SoloTAdapter());
			bot = new Bot(player2,ball);
		}
		
		timer = new Timer((int)(1000*TickInterval), this);
		timer.start();
		
		matchStartSound.play();
	}
	
	private void setBackgroundImg(String imgSrc){
		try {
			imgBG = ImageIO.read(this.getClass().getResourceAsStream("Textures/"+imgSrc));
		} catch (IOException e) {
			System.err.println(e);
			System.err.println("Cant find " + imgSrc + " for: "
								+ getClass().getName());
			System.exit(1);
		}
	}
	
	public float getTimeScale(){
		return TimeBoost;
	}
	
	public float getTickInterval(){
		return TickInterval;
	}
	
	public float getRealTick(){
		return TimeBoost*TickInterval*timeScaling;
	}

	private int debugInt = 0;
	private boolean releaseBall = false;
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(GameOver){
			restart();
		}
		if(!releaseBall)
			tickCount++;
		if(tickCount > 1000){
			releaseBall  = true;
			tickCount = 0;
			backgroundSound.playLoop();
			backgroundSound.setVolume(0.3f);
		}
		if(playSolo)
			bot.update();
		player1.update();
		player2.update();
		changeTimeScaling();
		if(releaseBall)
			ball.update();
		repaint();
	}
	
	private float TimeWidth = 3*PhysicalObject.METER;
	private float TimeCap = 0.2f;
	private static float speedCap = 10.0f;
	public boolean enableSlowMotion = false;
	private void changeTimeScaling(){
		float ballSpeed = (float)Math.sqrt(Math.pow(ball.dx, 2) + Math.pow(ball.dy, 2));
		if(ballSpeed < speedCap || ball.getOwner() == 0){
			timeScaling = 1;
			return;
		}
//		float dist = (float)Math.abs(Math.min(Math.sqrt(Math.pow(player1.X-ball.X, 2)+
//				(Math.pow(player1.Y-ball.Y, 2))),
//				Math.sqrt(Math.pow(player2.X-ball.X, 2)+
//						(Math.pow(player2.Y-ball.Y, 2)))));
		float dist = -1000;
		if(ball.getOwner() == 2){
			dist = (float)Math.sqrt(Math.pow(player1.X-ball.X, 2) + Math.pow(player1.Y-ball.Y, 2));
		}
		if(ball.getOwner() == 1){
			dist = (float)Math.sqrt(Math.pow(player2.X-ball.X, 2) + Math.pow(player2.Y-ball.Y, 2));
		}
		timeScaling = (float)Math.pow(dist/TimeWidth, 2);
		if(timeScaling > 1)
			timeScaling = 1;
		else if(timeScaling < TimeCap)
			timeScaling = TimeCap;
		
	}
	
	Image image;
	private int loser;
	public void paint(Graphics g){
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		
//		try {
//			image = ImageIO.read(new File("Textures/strawberry.jpg"));
//		} catch (IOException e) {
//			System.err.println(e);
//			System.err.println("Cant find " + "strawberry.jpg" + " for: "
//								+ getClass().getName());
//			System.exit(1);
//		}
//		
//		for(HitBox hb : player1.hitBoxes){
//			g2.drawImage(image, (int)hb.getPosX(), (int)hb.getPosY(), (int)hb.getWidth(), (int)hb.getHeight(), this);
//		}
//		for(HitBox hb : player2.hitBoxes){
//			g2.drawImage(image, (int)hb.getPosX(), (int)hb.getPosY(), (int)hb.getWidth(), (int)hb.getHeight(), this);
//		}
		g2.drawImage(imgBG, 0, 0, getWidth(), getHeight(), this);
		drawFromSheet(g2, player1);
		drawFromSheet(g2, player2);
		g2.drawImage(ball.image, (int)(ball.X-ball.getWidth()/2), (int)(ball.Y-ball.getHeight()/2), (int)ball.getWidth(), (int)ball.getHeight(), this);
		
		drawHP(g2, player1);
		drawHP(g2, player2);

		//g2.drawImage(image, (int)ball.ballBox.getPosX(), (int)ball.ballBox.getPosY(), (int)ball.ballBox.getWidth(), (int)ball.ballBox.getHeight(), this);
		drawWinner(g2);
		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	}
	
	private void drawHP(Graphics2D g, Player player) {
		g.setFont(new Font("TimesRoman", Font.BOLD, 30));
		if(player.player == 1)
			g.setColor(Color.blue);
		else
			g.setColor(Color.red);
		String s = player.HPString;
		int stringLen = (int)g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, -stringLen/2 + player.X, player.Y-player.height/2);
	}

	private void drawWinner(Graphics2D g){
		if(GameOver){
			String winner = "WTF?";
			if(loser == 1){
				winner = "Red";
				g.setColor(Color.red);
			}
			else{
				winner = "Blue";
				g.setColor(Color.blue);
			}
			
			g.setFont(new Font("TimesRoman", Font.BOLD, 100));
			String s = "Winner "+ winner;
			int stringLen = (int)g.getFontMetrics().getStringBounds(s, g).getWidth();
		    g.drawString(s, -stringLen/2 + ApplicationFrame.WIDTH/2, ApplicationFrame.HEIGHT/2);

		    backgroundSound.stop();
		    matchEndSound.play();
		}
	}
	
	public void setLoser(int player){
		GameOver = true;
		loser = player;
	}
	
	private void restart(){
		timer.stop();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		switchView(new Board(playSolo));
	}
	
	private void switchView(JPanel panel){
		timer.stop();
		closeSound();
		JFrame parent = (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, Board.this);
        parent.getContentPane().removeAll();
        parent.add(panel);
        parent.revalidate();
        parent.repaint();
        panel.requestFocus();
	}
	
	private void closeSound() {
		matchStartSound.close();
		matchEndSound.close();
		backgroundSound.stop();
		player1.closeSounds();
		player2.closeSounds();
		ball.closeSound();
	}

	private void drawFromSheet(Graphics2D g, Player p){
		g.drawImage(p.image,(int)(p.X-p.direction*p.width/2), (int)(p.Y-p.height/2),
				(int)(p.X+p.direction*p.width/2), (int)(p.Y+p.height/2),
				(p.dispInt%p.SSCol)*p.SSWidth,
				(p.dispInt/p.SSCol)*p.SSHeight,
				(1+(p.dispInt%p.SSCol))*p.SSWidth-1,
				(1+(p.dispInt/p.SSCol))*p.SSHeight-1, this);
	}
	
	private class TAdapter extends KeyAdapter {
		public void keyReleased(KeyEvent e){
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
				switchView(new Menu());
			}
			player1.keyReleased(e.getKeyCode());
			player2.keyReleased(e.getKeyCode());
		}
		
		public void keyPressed(KeyEvent e){
			player1.keyPressed(e.getKeyCode());
			player2.keyPressed(e.getKeyCode());
		}
	}
	
	private class SoloTAdapter extends KeyAdapter {
		public void keyReleased(KeyEvent e){
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
				switchView(new Menu());
			}
			player1.keyReleased(e.getKeyCode());
		}
		
		public void keyPressed(KeyEvent e){
			player1.keyPressed(e.getKeyCode());
		}
	}

}
