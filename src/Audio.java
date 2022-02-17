import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Audio {
    private Clip clip;
    private File audioFile;
    private AudioInputStream audioInputStream;
    private boolean isLoop;

    // 생성자의 매개변수로 파일의 경로와, 무한 반복 여부를 넣는다.
    public Audio(String pathName, boolean isLoop) {
        try {
            clip = AudioSystem.getClip(); // 오디오 재생에 사용할 수 있는 클립을 받아온다.
            // 경로명에 있는 파일로부터 오디오 입력 스트림을 가져온다.
            audioFile = new File(pathName);
            audioInputStream =  AudioSystem.getAudioInputStream(audioFile);
            clip.open(audioInputStream); // 클립에 오디오 입력 스트림을 받아온다. 이로써 파일을 재생할 수 있는 준비가 끝났다.
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // clip을 재생할 수 있는 메소드 생성
    public void start() {
        // 클립을 파일의 처음을 가리키게 하고, 재생해준다.
        clip.setFramePosition(0);
        clip.start();
        // 생성자에서 받아온 무한반복 여부를 통해 무한반복일 경우도 구현해준다.
        if(isLoop) clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    // 재생하고 있는 파일을 멈추는 메소드도 만들어준다.
    public void stop() {
        clip.stop();
    }

}
