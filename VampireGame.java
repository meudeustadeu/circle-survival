import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class VampireGame extends JPanel implements ActionListener, KeyListener, MouseMotionListener {
    private int playerX = 400, playerY = 300, playerSize = 30;
    private int speed = 10;
    private int mouseX, mouseY;
    private int score = 0;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Projectile> projectiles = new ArrayList<>();
    private Timer gameLoop;
    private Random rand = new Random();
    private  boolean gameOver = false;

    public VampireGame() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.DARK_GRAY);
        addKeyListener(this);
        addMouseMotionListener(this);
        setFocusable(true);
        gameLoop = new Timer(30, this);
        gameLoop.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        spawnEnemies();
        moveEnemies();
        moveProjectiles();
        checkCollisions();
        repaint();
    }

    private void spawnEnemies() {
        if (rand.nextInt(100) < 3) { // 3% de chance de spawn por frame
            int x = rand.nextBoolean() ? 0 : getWidth();
            int y = rand.nextInt(getHeight());
            enemies.add(new Enemy(x, y));
        }
    }

    private void moveEnemies() {
        for (Enemy enemy : enemies) {
            enemy.move(playerX, playerY);
        }
    }

    private void moveProjectiles() {
        for (Projectile p : projectiles) {
            p.move();
        }
    }

    private void checkCollisions() {
        // Verifica se algum inimigo tocou no jogador
        for (Enemy enemy : enemies) {
            if (Math.hypot(playerX - enemy.x, playerY - enemy.y) < playerSize) {
                // resetGame();
                gameOver = true;
                return;
            }
        }

        // Verifica se algum projétil atingiu um inimigo
        Iterator<Projectile> pIt = projectiles.iterator();
        while (pIt.hasNext()) {
            Projectile p = pIt.next();
            Iterator<Enemy> eIt = enemies.iterator();
            while (eIt.hasNext()) {
                Enemy enemy = eIt.next();
                if (Math.hypot(p.x - enemy.x, p.y - enemy.y) < enemy.size) {
                    eIt.remove();
                    pIt.remove();

                    score++;

                    break;
                }
            }
        }
    }

    private void resetGame() {


            enemies.clear();
            projectiles.clear();
            playerX = 400;
            playerY = 300;
            score = 0;


    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        g.setColor(Color.BLUE);
        g.fillOval(playerX, playerY, playerSize, playerSize);

        g.setColor(Color.RED);
        for (Enemy enemy : enemies) {
            g.fillOval(enemy.x, enemy.y, enemy.size, enemy.size);
        }

        g.setColor(Color.YELLOW);
        for (Projectile p : projectiles) {
            g.fillOval(p.x, p.y, p.size, p.size);
        }

        pontuacao(g);

        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("GAME OVER", 250, 300);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press R to restart", 300, 350);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> playerY -= speed;
            case KeyEvent.VK_S -> playerY += speed;
            case KeyEvent.VK_A -> playerX -= speed;
            case KeyEvent.VK_D -> playerX += speed;
            case KeyEvent.VK_SPACE -> shootProjectile();
            case KeyEvent.VK_R -> resetGame();


        }
        repaint();
    }

    private void shootProjectile() {
        projectiles.add(new Projectile(playerX + playerSize / 2, playerY + playerSize / 2, mouseX, mouseY));
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Vampire Survivors Java");
        VampireGame game = new VampireGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void pontuacao (Graphics g) {
        g.drawString("Score: " + score, 10, 20);
    }
}

class Enemy {
    int x, y, size = 50;
    double speed = 2;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void move(int targetX, int targetY) {
        double angle = Math.atan2(targetY - y, targetX - x);
        x += (int) (speed * Math.cos(angle));
        y += (int) (speed * Math.sin(angle));
    }
}

class Projectile {
    int x, y, size = 10;
    double speed = 8;
    double angle;

    public Projectile(int startX, int startY, int targetX, int targetY) {
        x = startX;
        y = startY;
        angle = Math.atan2(targetY - startY, targetX - startX);
    }

    public void move() {
        x += (int) (speed * Math.cos(angle));
        y += (int) (speed * Math.sin(angle));
    }
}