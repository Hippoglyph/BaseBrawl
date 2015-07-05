import javax.swing.JFrame;
import javax.swing.WindowConstants;


public class ApplicationFrame extends JFrame {

	public static final int WIDTH = 1440;
	public static final int HEIGHT = 900;
	
	public ApplicationFrame(int width, int height){
		add(new Menu());
		setSize(width, height);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("BaseBrawl");
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new ApplicationFrame(WIDTH, HEIGHT);
	}

}
