import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.Image;


public class Menu extends JPanel{
	
	private Color background = Color.cyan;

	public Menu(){
		makeFrame();
	}

	private void makeFrame() {
		setLayout(new BorderLayout());
		
		JPanel panel = new JPanel();
		panel.setBackground(background);
		
		GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
	    
		panel.setLayout(new GridBagLayout());
		
		panel.add(createPlayFriendButton(), gbc);
		panel.add(createPlayNoFriendButton(), gbc);
		panel.add(createContolsButton(), gbc);
		panel.add(createExitButton(), gbc);
		
		add(panel,BorderLayout.CENTER);
	}

	private ImageIcon getIcon(String path) {
		Image image = null;
		try{
			image = ImageIO.read(this.getClass().getResourceAsStream("Textures/"+path));
		} catch (IOException e) {
			System.err.println(e);
			System.err.println("Cant find texture for: "
								+ getClass().getName());
			System.exit(1);
		}
		return new ImageIcon(image);
	}
	
	private void switchView(JPanel panel){
		JFrame parent = (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, Menu.this);
        parent.getContentPane().removeAll();
        parent.add(panel);
        parent.revalidate();
        parent.repaint();
        panel.requestFocus();
	}
	
	private JPanel createPlayFriendButton(){
		JButton play = new JButton(getIcon("Play_F.png"));
		play.addActionListener(new ActionListener() {	 
            public void actionPerformed(ActionEvent e)
            {
            	switchView(new Board(false));
            }
        });
        play.setBorderPainted(false); 
        play.setContentAreaFilled(false); 
        play.setFocusPainted(false); 
        play.setOpaque(false);
        
        JPanel playPanel = new JPanel();
        playPanel.setBackground(background);
        playPanel.add(play);
		return playPanel;
	}
	
	private JPanel createPlayNoFriendButton(){
		JButton play = new JButton(getIcon("Play_No_F.png"));
		play.addActionListener(new ActionListener() {	 
            public void actionPerformed(ActionEvent e)
            {
            	switchView(new Board(true));
            }
        });
        play.setBorderPainted(false); 
        play.setContentAreaFilled(false); 
        play.setFocusPainted(false); 
        play.setOpaque(false);
        
        JPanel playPanel = new JPanel();
        playPanel.setBackground(background);
        playPanel.add(play);
		return playPanel;
	}
	
	private JPanel createContolsButton(){
		JButton controls = new JButton(getIcon("Controls_button.png"));
		controls.addActionListener(new ActionListener() {	 
            public void actionPerformed(ActionEvent e)
            {
            	switchView(new ControlsMenu());
            }
        }); 
        controls.setBorderPainted(false); 
        controls.setContentAreaFilled(false); 
        controls.setFocusPainted(false); 
        controls.setOpaque(false);
		
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(background);
        controlPanel.add(controls);
		return controlPanel;
	}
	
	private JPanel createExitButton(){
		JButton exit = new JButton(getIcon("Exit_button.png"));
		exit.addActionListener(new ActionListener() {	 
            public void actionPerformed(ActionEvent e)
            {
                //System.out.println("Exited through button");
                System.exit(1);
            }
        }); 
        exit.setBorderPainted(false); 
        exit.setContentAreaFilled(false); 
        exit.setFocusPainted(false);
        exit.setOpaque(false);
        
        JPanel exitPanel = new JPanel();
        exitPanel.setBackground(background);
        exitPanel.add(exit);
		return exitPanel;
	}
}
