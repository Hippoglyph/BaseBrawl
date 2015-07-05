import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;


public class ControlsMenu extends JPanel {
	
	private JButton buttonPressed = null;
	private String prevButtonPressedText;
	private RebindKey rebindKey = null;
	private Map<String, Integer> rebindPlayer1 = new HashMap<String,Integer>();
	private Map<String, Integer> rebindPlayer2 = new HashMap<String,Integer>();
	private Color background = Color.cyan;
	private Controls controls = new Controls();

	public ControlsMenu(){
		makeFrame();
	}

	private void makeFrame() {
		setLayout(new BorderLayout());
		
		JPanel panel = new JPanel();
		panel.setBackground(background);
	    
		panel.setLayout(new GridBagLayout());
		
		panel.add(createPlayerPanel(false));
		JLabel spaceLbl = new JLabel();
		spaceLbl.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		panel.add(spaceLbl);
		panel.add(createPlayerPanel(true));
		
		add(panel,BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setBackground(background);
		
		bottomPanel.add(createBottomPanel());
		
		add(bottomPanel,BorderLayout.SOUTH);
		
		setFocusable(true);
		addKeyListener(new TAdapter());
		requestFocus();
	}

	private JPanel createBottomPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout());
		
		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!rebindPlayer1.isEmpty())
					controls.rebind(rebindPlayer1, true);
				if(!rebindPlayer2.isEmpty())
					controls.rebind(rebindPlayer2, false);
				switchView(new Menu());
			}
		});
		panel.add(save);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchView(new Menu());
			}
		});
		panel.add(cancel);
		
		return panel;
	}

	private JPanel createPlayerPanel(boolean isPlayer1) {
		JPanel panel = new JPanel();

		if(isPlayer1){
			panel.setBackground(Color.blue);
		}
		else{
			panel.setBackground(Color.red);
		}
		
		GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10,10,10,10);

		panel.setLayout(new GridBagLayout());
		
		
		JPanel bindings = new JPanel();
		bindings.setLayout(new GridBagLayout());

		bindings.add(createPanel(isPlayer1, Controls.UPString), gbc);
		bindings.add(createPanel(isPlayer1, Controls.DOWNString), gbc);
		bindings.add(createPanel(isPlayer1, Controls.LEFTString), gbc);
		bindings.add(createPanel(isPlayer1, Controls.RIGHTString), gbc);
		bindings.add(createPanel(isPlayer1, Controls.SWINGString), gbc);
		
		panel.add(bindings, gbc);
		
		return panel;
	}
	
	private JPanel createPanel(boolean isPlayer1, String key){
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout());
		
		JLabel lbl = new JLabel(key);
		lbl.setMaximumSize(new Dimension(50, 20));
		panel.add(lbl);
		panel.add(createButton(isPlayer1, key));
		panel.setPreferredSize(new Dimension(500, 500));
		return panel;
	}
	
	private JButton createButton(boolean isPlayer1, String key){
		controls.loadControls(isPlayer1);
		JButton button = null;
		
		switch(key){
		case Controls.UPString:
			button = new JButton(KeyEvent.getKeyText(controls.UP));
			break;
		case Controls.DOWNString:
			button = new JButton(KeyEvent.getKeyText(controls.DOWN));
			break;
		case Controls.LEFTString:
			button = new JButton(KeyEvent.getKeyText(controls.LEFT));
			break;
		case Controls.RIGHTString:
			button = new JButton(KeyEvent.getKeyText(controls.RIGHT));
			break;
		case Controls.SWINGString:
			button = new JButton(KeyEvent.getKeyText(controls.SWING));
			break;
		default:
			System.err.println("Could not create button");
			System.exit(WindowConstants.EXIT_ON_CLOSE);
			break;
		}
		
		button.setMinimumSize(new Dimension(80,20));
		JButton button2 = button;//WTF?
		
		button2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String waitText = "??";
				if(buttonPressed != null && buttonPressed.getText().equals(waitText)){
					buttonPressed.setText(prevButtonPressedText);
				}
				prevButtonPressedText = button2.getText();
				button2.setText(waitText);
				buttonPressed = button2;
				
				rebindKey = new RebindKey(key, isPlayer1);
				requestFocus();
			}
		});
		return button2;	
	}
	
	private void addToRebindQ(RebindKey rebindKey, KeyEvent e) {
		if(rebindKey.isPlayer1)
			rebindPlayer1.put(rebindKey.key, e.getKeyCode());
		else
			rebindPlayer2.put(rebindKey.key, e.getKeyCode());
	}
	
	private void switchView(JPanel panel){
		JFrame parent = (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, ControlsMenu.this);
        parent.getContentPane().removeAll();
        parent.add(panel);
        parent.revalidate();
        parent.repaint();
        panel.requestFocus();
	}
	
	private class TAdapter extends KeyAdapter {
		public void keyReleased(KeyEvent e){
			if (rebindKey != null) {
				addToRebindQ(rebindKey, e);
				buttonPressed.setText(KeyEvent.getKeyText(e.getKeyCode()));
				rebindKey = null;
			}
		}
	}
	
	private class RebindKey{
		public String key;
		public boolean isPlayer1;
		
		RebindKey(String key, boolean isPlayer1){
			this.key = key;
			this.isPlayer1 = isPlayer1;
		}
	}
	
}
