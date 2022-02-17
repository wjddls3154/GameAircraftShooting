import javax.swing.*;
import java.awt.*;

public class PlayerAttack {
    Image image = new ImageIcon("src/images/player_attack.png").getImage(); // 공격의 이미지
    int x,y; // 공격의 위치
    // 공격의 충돌 판정을 위해 이미지의 너비와 높이도 설정
    int width = image.getWidth(null);
    int height = image.getHeight(null);
    int attack = 5; // 공격력

    // x,y 를 매개변수로 하는 생성자를 만들어 준다.
    public PlayerAttack(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // 발사 메소드
    public void fire() {
        this.x += 15; // 플레이어 공격은 오른쪽으로 나가므로 x 값만 증가시켜주면 된다.
    }

}
