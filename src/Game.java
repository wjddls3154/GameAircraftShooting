import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Game extends Thread {
    private int delay = 20; // 게임의 딜레이
    private long pretime;
    private int cnt;        // 딜레이마다 증가할 카운트(cnt), 이벤트 발생 주기를 컨트롤하는 변수가 될 것
    private int score;      // 점수를 나타내는 변수

    private Image player = new ImageIcon("src/images/player.png").getImage(); // 플레이어 이미지 받아오기

    // 플레이어 관련 변수 선언
    private int playerX, playerY;
    private int playerWidth = player.getWidth(null);
    private int playerHeight = player.getHeight(null);
    private int playerSpeed = 10; // 키 입력이 한 번 인식됐을 때 플레이어가 이동할 거리
    private int playerHp = 30;

    private boolean up, down, left, right, shooting ; // 플레이어의 움직임을 제어할 이동 변수 선언, shooting 변수는 true 일 경우 공격 발사되게 할 것.
    private boolean isOver; // 게임 오버 여부 알려주는 변수

    private ArrayList<PlayerAttack> playerAttackList = new ArrayList<PlayerAttack>(); // 플레이어의 공격을 담을 ArrayList 만듬
    private ArrayList<Enemy> enemyList = new ArrayList<Enemy>(); // Enemy 를 담을 ArrayList 만듬
    private ArrayList<EnemyAttack> enemyAttackList = new ArrayList<EnemyAttack>();// EnemyAttack 을 담을 ArrayList 만듬

    // ArrayList 안의 내용에 쉽게 접근할 수 있게, 변수 선언
    private PlayerAttack playerAttack;
    private Enemy enemy;
    private EnemyAttack enemyAttack;

    private Audio backgroundMusic; // 배경음악
    private Audio hitSound; // 피격 효과음


    // run 메소드는 이 쓰레드를 시작할 시 실행될 내용
    @Override
    public void run() {
        backgroundMusic = new Audio("src/audio/gameBGM.wav",true); // 게임 배경음악 실행
        hitSound = new Audio("src/audio/hitSoundBGM.wav",false);

        reset(); // 쓰레드가 시작할 때 reset 메소드도 한번 실행한다.

        while (true) { // 이제부터 cnt 를 delay 밀리초가 지날때마다 증가 시켜준다.
            while (!isOver) { // isOver 이 False 일 경우에만, 아래 내용들이 반복되도록 한다.
                pretime = System.currentTimeMillis();
                // 단순하게 Thread.sleep(delay); 를 해줄수도 있지만,
                // 좀 더 정확한 주기를 위해, (현재 시간 - (cnt 가 증가하기 전 시간) < delay 일 경우, 그 차이만큼 Thread에 sleep 을 준다.
                if (System.currentTimeMillis() - pretime < delay) {
                    try {
                        Thread.sleep(delay - System.currentTimeMillis() + pretime);
                        keyProcess();
                        playerAttackProcess();
                        enemyAppearProcess();
                        enemyMoveProcess();
                        enemyAttackProcess();
                        cnt++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
            try {
                Thread.sleep(100); // Thread.sleep 을 추가해줘서 isOver = true 일 경우, 쓰레드가 계속 쉬도록 한다.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 게임 상태 초기화해주는 메소드
    public void  reset() {
        isOver = false;
        cnt = 0; // cnt 0으로 초기화
        score = 0; // 점수 초기화

        // 플레이어 위치 초기화
        playerX = 10;
        playerY = (Main.SCREEN_HEIGHT - playerHeight) / 2 ;

        playerHp = 30; // hp 초기화

        backgroundMusic.start();

        // ArrayList 초기화
        playerAttackList.clear();
        enemyAttackList.clear();
        enemyList.clear();
    }

    // 키 입력을 처리할 메소드
    private void keyProcess(){
        // 화면에서 안나가는 선에서 playerX 와 playerY 의 값을 조정
        if (up && playerY - playerSpeed > 0) playerY -= playerSpeed;
        if (down && playerY + playerHeight + playerSpeed < Main.SCREEN_HEIGHT) playerY += playerSpeed;
        if (left && playerX - playerSpeed > 0) playerX -= playerSpeed;
        if (right && playerX + playerWidth + playerSpeed < Main.SCREEN_WIDTH) playerX += playerSpeed;
        // cnt 가 0.02 초마다 올라가는 것을 생각한다면, 0.3초마다 미사일이 발사되도록 한다.
        if (shooting && cnt % 15 == 0) {
            playerAttack = new PlayerAttack(playerX + 222, playerY + 25); // 플레이어와 적당히 떨어진 위치에 공격을 만들어주고
            playerAttackList.add(playerAttack); // 이를 ArrayList 에 넣어준다.
        }

    }

    // 공격을 처리해 줄 메소드
    private void playerAttackProcess() {
        for (int i = 0; i < playerAttackList.size(); i++) {
            playerAttack = playerAttackList.get(i); // ArrayList 의 get 메소드를 통해, 담긴 객체 하나하나에 접근해 fire 메소드를 실행해준다.
            playerAttack.fire();

            // 플레이어 공격에 충돌판정 넣기
            for (int j = 0; j < enemyList.size(); j++) {
                // 플레이어의 공격 이미지가 적 이미지와 겹쳐있는 부분이 있는지 검사해준다.
                enemy = enemyList.get(j);
                if (playerAttack.x > enemy.x && playerAttack.x < enemy.x + enemy.width && playerAttack.y > enemy.y && playerAttack.y < enemy.y + enemy.height) {
                    // 겹쳐있는 부분이 있을 때 적의 hp를 줄이고, 해당 공격을 삭제한다.
                    enemy.hp -= playerAttack.attack;
                    playerAttackList.remove(playerAttack);
                }
                // 또한, 적의 hp가 0 이하라면, 적을 제거한다.
                if (enemy.hp <= 0) {
                    hitSound.start(); // 적을 격추했을 때, 피격음 설정
                    enemyList.remove(enemy);
                    score += 1000;
                }

            }
        }
    }

    // 주기적으로 적을 출현시키는 메소드
    private void enemyAppearProcess() {
        if (cnt % 80 == 0) {
            enemy = new Enemy(1120,(int)(Math.random()*621)); // 화면 끝에서 랜덤한 위치에 출현시키기 위해 y값을 1~620 랜덤으로 나오게 한다.
            enemyList.add(enemy); // 이를 ArrayList 에 추가해줌.
        }
    }

    // 적을 이동시키는 메소드
    private void enemyMoveProcess() {
        for (int i = 0; i < enemyList.size(); i++) {
           // ArrayList 안의 요소에 접근해서, move 메소드 호출
           enemy = enemyList.get(i);
           enemy.move();
        }
    }

    // 적의 공격 구현하는 메소드
    private void enemyAttackProcess() {
        // 일정 주기마다 적의 공격을 생성해 ArrayList 안에 추가해준다.
        if (cnt % 50 == 0) {
            enemyAttack = new EnemyAttack(enemy.x - 79, enemy.y + 35);
            enemyAttackList.add(enemyAttack);
        }
        // 또한, ArrayList 에 담긴 공격 하나하나에 접근해, fire 메소드를 호출해준다.
        for (int i = 0; i < enemyAttackList.size(); i++) {
            enemyAttack = enemyAttackList.get(i);
            enemyAttack.fire();
        }
        // 적의 공격에 충돌판정 넣기
        if (enemyAttack.x > playerX && enemyAttack.x < playerX + playerWidth && enemyAttack.y > playerY && enemyAttack.y < playerY + playerHeight) {
            hitSound.start();
            playerHp -= enemyAttack.attack;
            enemyAttackList.remove(enemyAttack);
            if (playerHp <= 0) isOver = true; // 플레이어 hp가 0이 되면 게임 오버
        }

    }

    // 게임 안의 요소를 그려줄 메소드
    public void gameDraw(Graphics g) {
        playerDraw(g);
        enemyDraw(g);
        infoDraw(g);
    }

    // 게임 관련 정보를 그려주는 메소드
    public void  infoDraw(Graphics g) {
        // 색깔,폰트,폰트크기 설정
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial",Font.BOLD,40));
        g.drawString("SCORE : " + score,40,80); // 설정한 폰트를 토대로 drawString 메소드를 통해 x:40, y:80 의 위치에 점수를 출력해준다.

        // 게임이 끝난 경우 R키를 눌러 재시작할 수 있다는 안내문을 띄워준다.
        if (isOver) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial",Font.BOLD,80));
            g.drawString("Press R to restart",295,380);
        }
    }

    // player 에 관한 요소를 그려줄 메소드
    public void playerDraw(Graphics g) {
        g.drawImage(player,playerX,playerY,null); // 앞서 만든 플레이어 이미지를, playerX,Y 좌표에 그려주면 된다.
        // 플레이어와 적의 체력을 눈으로 확인할수있도록 체력바 생성, 체력바의 배수만큼의 초록색 사각형을 플레이어 위와 적의 위에 그려주는 방식으로 구현
        g.setColor(Color.GREEN);
        g.fillRect(playerX - 1, playerY - 40,playerHp * 6, 20);

        for (int i = 0; i < playerAttackList.size(); i++) {
            playerAttack = playerAttackList.get(i); // ArrayList 의 get 메소드를 통해, 담긴 객체 하나하나에 접근해 fire 메소드를 실행해준다.
            g.drawImage(playerAttack.image,playerAttack.x,playerAttack.y,null); // 플레이어 공격을 각각의 x,y 에 그려준다.
        }
    }

    // 적과 적의 공격을 그려줄 메소드
    public void enemyDraw(Graphics g) {
        for (int i = 0; i < enemyList.size(); i++) {
            enemy = enemyList.get(i);
            g.drawImage(enemy.image, enemy.x, enemy.y, null ); // 선언했던 클래스의 필드 x,y 를 이용하여 적을 그려준다.
            g.setColor(Color.GREEN);
            g.fillRect(enemy.x + 1, enemy.y - 40,enemy.hp * 15, 20);
        }
        // 적의 공격을 그림
        for (int i = 0; i < enemyAttackList.size(); i++) {
            enemyAttack = enemyAttackList.get(i);
            g.drawImage(enemyAttack.image,enemyAttack.x,enemyAttack.y,null);
        }

    }

    // isOver 변수의 상태를 알 수 있도록 이에 대한 getter 를 생성.
    public boolean isOver() {
        return isOver;
    }

    // private 변수의 경우, 객체를 통한 직접적인 접근을 못하므로, setter 를 만들어 준다.
    public void setUp(boolean up) {
        this.up = up;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void setShooting(boolean shooting) {
        this.shooting = shooting;
    }

}
