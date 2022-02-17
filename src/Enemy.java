import javax.swing.*;
import java.awt.*;

// 위치 정보, 체력 담을 Enemy 클래스
public class Enemy {
    Image image = new ImageIcon("src/images/enemy.png").getImage();
    int x,y;
    int width = image.getWidth(null);
    int height = image.getHeight(null);
    int hp = 10;

    // 위치 정보를 매개변수로 받는 생성자 만듬
    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // 적 기체를 움직이게 할 move 메소드
    public void move() {
        this.x -= 7;
    }

}
