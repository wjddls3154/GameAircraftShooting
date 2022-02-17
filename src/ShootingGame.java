import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.awt.event.KeyListener;
import java.sql.Time;
import java.util.TimerTask;

public class ShootingGame extends JFrame {

    // 플레이어와, 적의 움직임의 깜빡임을 없애기 위해, 더블 버퍼링을 위한 변수 2개 선언
    private Image bufferImage;
    private Graphics screenGraphic;

    private Image mainScreen = new ImageIcon("src/images/main_screen.png").getImage(); // ImageIcon 에 메인화면 이미지 파일 경로 넣음.
    private Image loadingScreen = new ImageIcon("src/images/loading_screen.png").getImage(); // 로딩화면
    private Image gameScreen = new ImageIcon("src/images/game_screen.png").getImage(); // 게임화면

    // boolean 변수로 화면 컨트롤
    private boolean isMainScreen, isLoadingScreen, isGameScreen ;

    // Game 클래스의 객체를 추가
    private Game game = new Game();

    private Audio backgroundMusic;

    // 화면 구성 메소드
    public ShootingGame() { // 생성자 생성
        setTitle("Aircraft Shooting Game"); // 창 제목
        setUndecorated(true); // 테두리가 없는 창으로 구현
        setSize(Main.SCREEN_WIDTH,Main.SCREEN_HEIGHT); // 창 크기
        setResizable(false); // 창 크기 조절 여부
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(null);

        init();
    }

    // 초기화를 해주는 init 메소드
    private void init() {
        isMainScreen = true;    // 메인화면만 true
        isLoadingScreen = false;
        isGameScreen = false;

        // 메뉴 배경음악을 Audio 클래스로 만들고, 재생을 해준다.
        backgroundMusic = new Audio("src/audio/menuBGM.wav",true);
        backgroundMusic.start();

        addKeyListener(new keyListener());
    }

    // 로딩, 게임화면으로 넘어가는 메소드
    private void gameStart() {
        isMainScreen = false;
        isLoadingScreen = true; // 로딩화면만 true

        Timer loadingTimer = new Timer();
        TimerTask loadingTask = new TimerTask() {
            @Override
            public void run() { // run 안에 실행할 내용 작성.
                backgroundMusic.stop(); // 게임화면일때는 재생중이었떤 메뉴 배경음악을 중단.
                isLoadingScreen = false;
                isGameScreen = true; // 게임화면만 true
                game.start(); // Game 클래스의 쓰레드를 시작
            }
        };
        loadingTimer.schedule(loadingTask,3000); // 앞에서 만든 Timer 와 TimerTask 를 이용해 로딩화면에서 3초후 게임화면으로 넘어가도록 만듬.
    }

    // paint 메소드에서 버퍼 이미지를 만들고, 이를 화면에 뿌려줌으로써 화면 깜빡임을 최소화
    public void paint(Graphics g) {
        bufferImage = createImage(Main.SCREEN_WIDTH,Main.SCREEN_HEIGHT);
        screenGraphic = bufferImage.getGraphics();
        screenDraw(screenGraphic);
        g.drawImage(bufferImage, 0,0,null);
    }

    // 필요한 요소를 그려줄것
    public void screenDraw(Graphics g) {
        if (isMainScreen) { // 메인화면이 true 일때
            g.drawImage(mainScreen, 0, 0, null);
        }
        if (isLoadingScreen) { // 로딩화면이 true 일때
            g.drawImage(loadingScreen, 0, 0, null);
        }
        if (isGameScreen) { // 게임화면이 true 일때
            g.drawImage(gameScreen, 0, 0, null);
            game.gameDraw(g); // 게임화면일때, game 클래스의 gameDraw 메소드 실행
        }
        this.repaint();
    }

    // 키 움직임을 받아줄 KeyListener 클래스
    class keyListener extends KeyAdapter {
        // 키를 눌렀을 때
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                // w,s,a,d 를 눌렀을때, game 클래스의 up,down,left,right 를 true 로 만들어준다.
                case KeyEvent.VK_W:
                    game.setUp(true);
                    break;
                case KeyEvent.VK_S:
                    game.setDown(true);
                    break;
                case KeyEvent.VK_A:
                    game.setLeft(true);
                    break;
                case KeyEvent.VK_D:
                    game.setRight(true);
                    break;
                // isOver 가 true 일 경우에만, R키 누르면 리셋
                case KeyEvent.VK_R:
                    if (game.isOver()) game.reset();
                    break;
                // 스페이스바 눌렀을때, game 클래스의 슈팅 true 로 만들어준다.
                case KeyEvent.VK_SPACE:
                    game.setShooting(true);
                    break;

                case KeyEvent.VK_ENTER: // 엔터를 눌렀을때 메인화면이면, gameStart(); 를 호출
                    if (isMainScreen) {
                        gameStart();
                    }
                    break;
                case KeyEvent.VK_ESCAPE:    // Esc 를 눌렀을 때 System.exit(0)을 호출. (종료)
                    System.exit(0);
                    break;
            }
        }

        // 키 입력을 뗐을 때
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                // w,s,a,d 를 눌렀을때, game 클래스의 up,down,left,right 를 true 로 만들어준다.
                case KeyEvent.VK_W:
                    game.setUp(false);
                    break;
                case KeyEvent.VK_S:
                    game.setDown(false);
                    break;
                case KeyEvent.VK_A:
                    game.setLeft(false);
                    break;
                case KeyEvent.VK_D:
                    game.setRight(false);
                    break;

                case KeyEvent.VK_SPACE:
                    game.setShooting(false);
                    break;

            }
        }

    }


}
