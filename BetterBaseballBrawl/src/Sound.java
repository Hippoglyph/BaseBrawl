import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class Sound {
	
	private Clip clip;
	float volume = 1;
	
	public Sound(String fileName){
		
		try{
			clip = AudioSystem.getClip();
			URL in = this.getClass().getResource("Sounds/"+fileName);
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(in);
			clip.open(audioStream);
		}
		catch(LineUnavailableException e){
			System.err.println("Line unaviable " + fileName + " closed");
			e.printStackTrace();
		}
		catch(UnsupportedAudioFileException e){
			System.err.println("Unsupported format");
			e.printStackTrace();
		}
		catch(IOException e){
			System.err.println("Can not find audio file");
			e.printStackTrace();
		}
	}
	
	public void play(){
		play(volume);
	}
	
	public void stop(){
		clip.stop();
	}
	
	public void continue_(){
		clip.start();
	}
	
	public void close(){
		clip.close();
	}
	
	public void playLoop(){
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void play(float decible){
		setVolume(decible);
		new Thread(new Runnable(){

			@Override
			public void run() {//Förlåt Linus för dataracet men detta funkar
				while(clip.isRunning()){
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				clip.setFramePosition(0);
				clip.start();
			}
			
		}).start();
	}
	
	public void setVolume(float decible){
		volume = decible;
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		float dB = (float) (Math.log(decible) / Math.log(10.0) * 20.0);
		gainControl.setValue(dB);
	}
}
