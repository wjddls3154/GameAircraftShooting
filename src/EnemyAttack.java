import javax.swing.*;
import java.awt.*;

public class EnemyAttack {
    Image image = new ImageIcon("src/images/enemy_attack.png").getImage();
    int x,y;
    int width = image.getWidth(null);
    int height = image.getHeight(null);
    int attack = 5;

    // 생성자
    public EnemyAttack(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // 공격을 이동시킬 메소드
    public void fire() {
        this.x -= 12;
    }

}
