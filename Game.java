
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleGame extends JPanel implements ActionListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PLAYER_RADIUS = 10;
    private static final int PROJECTILE_RADIUS = 5;
    private static final int ENEMY_RADIUS_MIN = 4;
    private static final int ENEMY_RADIUS_MAX = 30;
    private static final int ENEMY_RADIUS_REDUCTION = 10;

    private Player player;
    private List<Projectile> projectiles;
    private List<Enemy> enemies;

    public SimpleGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        // Initialize player, projectiles, and enemies
        player = new Player(WIDTH / 2, HEIGHT / 2, PLAYER_RADIUS, Color.WHITE);
        projectiles = new ArrayList<>();
        enemies = new ArrayList<>();

        // Mouse click listener for shooting projectiles
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                shootProjectile(e);
            }
        });

        // Timer to spawn enemies at regular intervals
        Timer timer = new Timer(1000, e -> spawnEnemies());
        timer.start();

        // Timer for game rendering and updates
        Timer gameTimer = new Timer(10, this);
        gameTimer.start();
    }

    // Method to handle shooting projectiles
    private void shootProjectile(MouseEvent e) {
        double angle = Math.atan2(e.getY() - HEIGHT / 2, e.getX() - WIDTH / 2);
        double velocityX = Math.cos(angle) * 5;
        double velocityY = Math.sin(angle) * 5;
        projectiles.add(new Projectile(WIDTH / 2, HEIGHT / 2, PROJECTILE_RADIUS, Color.WHITE, velocityX, velocityY));
    }

    // Method to spawn enemies
    private void spawnEnemies() {
        double radius = Math.random() * (ENEMY_RADIUS_MAX - ENEMY_RADIUS_MIN) + ENEMY_RADIUS_MIN;
        double x, y;

        if (Math.random() < 0.5) {
            x = Math.random() < 0.5 ? 0 - radius : WIDTH + radius;
            y = Math.random() * HEIGHT;
        } else {
            x = Math.random() * WIDTH;
            y = Math.random() * HEIGHT;
        }

        Color color = new Color((float) Math.random(), 0.5f, 0.5f);
        double angle = Math.atan2(HEIGHT / 2 - y, WIDTH / 2 - x);
        double velocityX = Math.cos(angle);
        double velocityY = Math.sin(angle);

        enemies.add(new Enemy(x, y, radius, color, velocityX, velocityY));
    }

    // Method to handle collisions between projectiles and enemies
    private void handleProjectileEnemyCollisions() {
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            Iterator<Enemy> enemyIterator = enemies.iterator();

            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();
                double distToEnemy = Math.hypot(projectile.getX() - enemy.getX(), projectile.getY() - enemy.getY());

                if (distToEnemy - enemy.getRadius() - PROJECTILE_RADIUS < 1) {
                    if (enemy.getRadius() - ENEMY_RADIUS_REDUCTION > ENEMY_RADIUS_MIN) {
                        enemy.setRadius(enemy.getRadius() - ENEMY_RADIUS_REDUCTION);
                    } else {
                        enemyIterator.remove();
                    }
                    iterator.remove();
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the player
        player.draw(g);

        // Update and draw projectiles
        for (Projectile projectile : projectiles) {
            projectile.update();
            projectile.draw(g);
        }

        // Update and draw enemies
        for (Enemy enemy : enemies) {
            enemy.update();
            enemy.draw(g);

            // Check for player-enemy collision
            double dist = Math.hypot(player.getX() - enemy.getX(), player.getY() - enemy.getY());

            if (dist - enemy.getRadius() - PROJECTILE_RADIUS < 1) {
                gameOver();
            }
        }

        // Handle collisions between projectiles and enemies
        handleProjectileEnemyCollisions();
    }

    // Method to handle game over
    private void gameOver() {
        // Game over logic here
        // For simplicity, stopping the game in this example
        System.exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Simple Game");
            SimpleGame game = new SimpleGame();

            frame.add(game);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
