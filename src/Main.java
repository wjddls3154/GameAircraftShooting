
public class Main {

    // 창의 너비와 높이는 다른 클래스에도 사용할 것이기 때문에, 미리 여기에 static final 변수로 선언해둠.
    public static final int SCREEN_WIDTH = 1200;
    public static final int SCREEN_HEIGHT = 720;

    public static void main(String[] args) {
        new ShootingGame(); // 메인 메소드에서 생성자 ShootingGame 호출

    }

}
