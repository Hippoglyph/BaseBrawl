import java.awt.event.KeyEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.WindowConstants;



public class Controls {
	private final static String PATH = "Controls";
	private final static String PLAYER1 = "Player1:";
	private final static String PLAYER2 = "Player2:";
	
	public final static String UPString = "UP";
	public final static String DOWNString = "DOWN";
	public final static String LEFTString = "LEFT";
	public final static String RIGHTString = "RIGHT";
	public final static String SWINGString = "SWING";
	
	public int UP;
	public int DOWN;
	public int LEFT;
	public int RIGHT;
	public int SWING;
	
	public void loadControls(boolean isPlayer1) {
		boolean retry = true;
		int retries = 0;
		
		while(retry == true && retries < 2){
			try(BufferedReader file = new BufferedReader(new FileReader(PATH))){
				setPlayerControls(file, isPlayer1);
				file.close();
				retry = false;
			}
			catch (IOException e) {
				createControlFile();
				retry = true;
				retries++;
			}
			catch(Exception e){
				System.err.printf("Control file error %s", e);
	            System.exit(WindowConstants.EXIT_ON_CLOSE);
			}
		}
	}

	private void setPlayerControls(BufferedReader file, boolean isPlayer1) {
		String player = "";
		if(isPlayer1)
			player = PLAYER1;
		else
			player = PLAYER2;
		
		try{
			while(!file.readLine().equals(player)){}
			UP = Integer.parseInt(file.readLine().split("\\s+")[1]);
			DOWN = Integer.parseInt(file.readLine().split("\\s+")[1]);
			LEFT = Integer.parseInt(file.readLine().split("\\s+")[1]);
			RIGHT = Integer.parseInt(file.readLine().split("\\s+")[1]);
			SWING = Integer.parseInt(file.readLine().split("\\s+")[1]);
		}
		catch(Exception e){
			System.err.printf("Control file readnig error %s", e);
			System.exit(WindowConstants.EXIT_ON_CLOSE);
		}
		
	}
	
	private void createControlFile() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATH))) {
			writer.write(PLAYER1);
			writer.newLine();
			writer.write(UPString + " " + KeyEvent.VK_UP);
			writer.newLine();
			writer.write(DOWNString + " " + KeyEvent.VK_DOWN);
			writer.newLine();
			writer.write(LEFTString + " " + KeyEvent.VK_LEFT);
			writer.newLine();
			writer.write(RIGHTString + " " + KeyEvent.VK_RIGHT);
			writer.newLine();
			writer.write(SWINGString + " " + KeyEvent.VK_CONTROL);
			writer.newLine();
			writer.write(PLAYER2);
			writer.newLine();
			writer.write(UPString + " " + KeyEvent.VK_W);
			writer.newLine();
			writer.write(DOWNString + " " + KeyEvent.VK_S);
			writer.newLine();
			writer.write(LEFTString + " " + KeyEvent.VK_A);
			writer.newLine();
			writer.write(RIGHTString + " " + KeyEvent.VK_D);
			writer.newLine();
			writer.write(SWINGString + " " + KeyEvent.VK_SPACE);
			writer.close();
		}
		catch(IOException e) {
			System.err.printf("Control file could not be created. %s", e);
            System.exit(WindowConstants.EXIT_ON_CLOSE);
		}
	}
	
	public void rebind(Map<String, Integer> rebind, boolean isPlayer1){//newKey could be a KeyEvent
		  String oldFileName = PATH;
	      String tmpFileName = "tmp_try";

	      BufferedReader br = null;
	      BufferedWriter bw = null;
	      
	      String player = "";
			if(isPlayer1)
				player = PLAYER1;
			else
				player = PLAYER2;
	      
	      try {
	          br = new BufferedReader(new FileReader(oldFileName));
	          bw = new BufferedWriter(new FileWriter(tmpFileName));
	          String line;
	          while((line = br.readLine()) != null){
	        	  bw.write(line);
	        	  bw.newLine();
	        	  if(line.equals(player))
	        		  break;
	          }
	          while ((line = br.readLine()) != null) {
	        	 for(String key : rebind.keySet()) {
		             if (line.contains(key)){
		                line = key + " " + rebind.get(key);
		                rebind.remove(key);
		                break;
		             }
	        	 }
	             bw.write(line);
	             bw.newLine();
	          }
	      }catch (Exception e) {
	    	  System.err.printf("Error in key-rebinding %s", e);
	          System.exit(WindowConstants.EXIT_ON_CLOSE);
	      } finally {
	         try {
	            if(br != null)
	               br.close();
	         } catch (IOException e) {
	        	 System.err.printf("Could not close reading file (rebinding) %s", e);
		         System.exit(WindowConstants.EXIT_ON_CLOSE);
	         }
	         try {
	            if(bw != null)
	               bw.close();
	         } catch (IOException e) {
	        	 System.err.printf("Could not close writing file (rebinding) %s", e);
		         System.exit(WindowConstants.EXIT_ON_CLOSE);
	         }
	      }

	      File oldFile = new File(oldFileName);
	      oldFile.delete();

	      File newFile = new File(tmpFileName);
	      newFile.renameTo(oldFile);
	}
}
