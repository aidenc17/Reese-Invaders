import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/**
 * SpaceInvaders is the main class to my SpaceInvaders replica game! In the game you can move, shoot, kill and save Reeses' ship from aliens
 * Keeps a running score count and hopefully can get to add a dificulty ramping mechanic.
 * @author Aiden Cox
 */

public class SpaceInvaders extends JPanel implements KeyListener {
    static final int SCREEN_WIDTH = 800; //global variable for the width. Used for screen size and when to change direction for aliens
    private static final int SCREEN_HEIGHT = 600; // global variable for the screen height. Same uses as the width 

    private Player player; //player from player class
    private ArrayList<Enemy> enemies; //creates an arraylist that will hold all of the enemies
    private ArrayList<Bullet> bullets; // creates an arraylist that will hold all of the bullets
    private boolean isRunning = false; // is the game running boolean. will help shut off the main game loop
    private static int score = 0; // keeps runnning score count
    private boolean showStartScreen = true; //instruction screen
    private long lastBulletTime = 0; //sets the time the last bullet was shot, used to check against to see if user can shoot again
    private static final long SHOOT_COOLDOWN = 200  ; // shooting cooldown time so user cant spam

    
    /**
     * Default Constructor for the SpaceInvaders JPanel. Sets tehe size, color, focuses the panel in the frame and makes sure to add
     * keylisteners, as well as a bunch of other game mechanics listed below
     * @param none
     * @return nothing
     */
    public SpaceInvaders() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT)); //sets size 
        setBackground(Color.BLACK); //background color
        setFocusable(true); //sets the compenents to focusable. makes sure they pop up
        addKeyListener(this); //key listener to the constructor
        

        player = new Player(SCREEN_WIDTH / 2, SCREEN_HEIGHT - 50); // starts him in the middle of the screen
        enemies = new ArrayList<>(); //list of enemies to cycle through
        bullets = new ArrayList<>(); //list of bullets to cycle through
        
        // Initialize enemies
        for (int row = 0; row < 6; row++) { //nested loop to spawn in enemies and instanly throw them in the list
            for (int col = 0; col < 3; col++) {
                enemies.add(new Enemy(50 + row * 100, 50 + col * 50));
            }
        }
        
        //https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html
        //Found on java docs. But basically in my own words creates a new thread aside from the main one to run this portion of the code.
        //This separation is important because the game loop continuously updates the game state like the movemetns and bullets,
        //and repaints the screen. The main application thread is good to handle user input and update the user interface so the enemy ships bullets and everthing else is smooth.
        
        // game loop until player dies
        new Thread(() -> {
            while (true) {
                if(isRunning){
                update(); // gets my positions of bullets, the collisions, player and the enemies
                repaint(); // repaints the panel, and goes to the paintComponetn method to draw the graphics
                }
                try {
                    Thread.sleep(10); // controls the speed of the game, will slow it down and regualte the frame rate
                } catch (InterruptedException e) {
                    e.printStackTrace();// prints out the error is in the code
                }
            }
            
        }).start(); //starts new thread instantly
    }

        /**
         * Class update method that contains all the other update methods for player, enemy, and bullet
         * @param nothing 
         * @return nothing
         */
        private void update() {
        player.update(); //update method that keeps the players positon and makes him not go out of bounds

        
        for (Enemy enemy : enemies) { //uses arrayList of enemies and updates all of them by looping through and getting 
            enemy.update();           // position and moving them down if they hit the border
        } 
        if (enemies.size() == 0) { //resets the enemies
            
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace(); //shows where program went wrong
            }
            for (int row = 0; row < 6; row++) { //initialize enemies again
                for (int col = 0; col < 3; col++) {
                    enemies.add(new Enemy(50 + row * 100, 50 + col * 50));
    
                }
            }

        }

        
        for (Bullet bullet : bullets) { //same thing as the enemy one, arrayList that updates them all at teh same time 
            bullet.update();
        }

        
        for (int i = bullets.size() - 1; i >= 0; i--) {  //checks for collisions between bullets and enemies
            Bullet bullet = bullets.get(i);
            for (int j = enemies.size() - 1; j >= 0; j--) {
                Enemy enemy = enemies.get(j);
                if (bullet.intersects(enemy)) {
                    bullets.remove(i); //removes from the list
                    enemies.remove(j); //removes from the list
                    score = score + 10; //adds score to score counter
                    break;
                }
            }
        }

        // checks to see if player has been hit by enemies
        for (Enemy enemy : enemies) {
            if (player.intersects(enemy)) {
                gameOver();
                break;
            }
        }
    }

    /**
     * Main class method that will end the game thread using boolean isRunning and displays end game PopUp
     * @param none 
     * @return nothing
     */
    private void gameOver() {
        isRunning = false;
        JOptionPane.showMessageDialog(this, "Game Over! Commander Reese would not be proud of your effort. :( Better luck next time", "Game Over", JOptionPane.PLAIN_MESSAGE);
        System.exit(0); //found on java doc, basically ends peacefully. Tells system that it was a good ending
    }
    /**
     * Paints the start screen if user has not hit enter, otherwise is incharge of painting score, player, enemy, and bullets.
     * Protected method from java doc helps it to extend to other subclassses
     * @param g. Uses the graphics class to be able to paint on the GUI
     * @return nothing, but paints to show graphics on the GUI
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (showStartScreen){ // i would comment all of this, but its kinda all right there. This section displays
            g.setColor(Color.WHITE); // the starting screen text, as well as instructions to the game
            g.setFont(new Font("Ariel", Font.BOLD, 20));
            g.drawString("Greeting fellow astronaut,", 50, 50);
            g.drawString("My name is Commander Reese. I have called you here today for a special", 50, 80);
            g.drawString("mission. The invaders are here and they want to attack our ship!", 50, 110);
            g.drawString("As Commander Reese I order that you help us defend against the alien", 50, 140);
            g.drawString("attack. This attack will likely end in your death, however you have", 50, 170);
            g.drawString("the opportunity to make me proud! With all that in mind,", 50, 200);
            g.drawString("Do you wish to continue??", 265, 230);
            g.drawString("CONTROLS:", 50, 360);
            g.drawString("SPACE: Shoot", 50, 390);
            g.drawString("LEFT ARROW KEY: Moves your spaceship to the left", 50, 420);
            g.drawString("RIGHT ARROW KEY: Move your spaceship to the right", 50, 450);
            g.drawString("PRESS ENTER TO CONTINUE", 265, 500);


        }else{
        player.draw(g); // draws player

        
        for (Enemy enemy : enemies) {  //draws each enemy on screen
            enemy.draw(g);
        }

        
        for (Bullet bullet : bullets) { //draws each bullet on screen
            bullet.draw(g);
        }
        g.setColor(Color.WHITE);//
        g.setFont(new Font("Ariel", Font.BOLD, 20)); //sets font
        g.drawString("Score: " + score, 20, 20); // draws the score and updates it
    }
    }
    /**
     * Overrides keyPressed method. Is responsible for getting off the start screen, and the shooting aspect. 
     * Like commented, I found a way to delay the shooting from a snake game online and implemented it with some tweaks into my code.
     * @param KeyEvent e. Key presssed event holder
     * @return nothing
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if(showStartScreen && e.getKeyCode() == KeyEvent.VK_ENTER){ //starts game when user presses enter for the firsst time
            startGame();
        }else if (isRunning){
            player.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_SPACE && System.currentTimeMillis() - lastBulletTime >= SHOOT_COOLDOWN) {
                    bullets.add(new Bullet(player.getX() + player.getWidth() / 2, player.getY())); //shoots it in the middle of player
                    lastBulletTime = System.currentTimeMillis(); // found online in a snake game actually, makes it so the user cant just hold down the shoot
    }
}
    }
    /**
     * Key Released method for the player to make sure that only when the keys are released and pressed the player moves/doesn't
     * @param e. Uses Key event
     * @return nothing, but moves the player on screen
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if(isRunning){
        player.keyReleased(e);
        }
    }
    /**
     * Key typed method, important to override because the class implements keyListner
     * @param e. Uses Key event
     * @return nothing
     */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * start game method starts the game, duh. It makes sure to turn off the start screen and flips on the other thread to assist in
     * the running of the game. requestFocusInWindow(); was added after some bugs occured when the switch from the start screen to the game window.
     * JavaDocs helped when reading, this method basically helps the computer dial in on the new window and makes it run smooth.
     * @param none
     * @return nothing
     */
    public void startGame(){
        showStartScreen = false; 
        isRunning = true;
        requestFocusInWindow();
    }

    //main method
    public static void main(String[] args) {
        JFrame frame = new JFrame("Reese Invaders"); //sets title
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //ends program
        frame.setResizable(false); //makes the user not able to size the screen
        frame.getContentPane().add(new SpaceInvaders()); // adds the contructor the the pane to view
        frame.pack(); //compresses the elements together
        frame.setLocationRelativeTo(null); //sets the screen to the center 
        frame.setVisible(true);// makes visable
        
    }
}
/**
 * Player class for the space invaders game. I took inspiration for the inner classes from buttons
 * so I wouldn't extend tons of times and into different classes.
 * @author Aiden Cox
 */
class Player {
    private int x; //player x
    private int y; //player y
    private int velocityX; //movement speed
    private static final int WIDTH = 50; //width of player
    private static final int HEIGHT = 30;//height of player
    private ImageIcon reeseHead; // 

    /**
     * Player contructor to make an object of the user
     * @param x x coord
     * @param y y coord
     */
    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        reeseHead = new ImageIcon("blackReese.jpg");
    }
    /**
     * Update method for the player. It moves the player when the key gets pressed and stops movement if player hits border
     * @param nothing
     */
    public void update() {
        x = x + velocityX;
        if (x < 0) {
            x = 0;
        }
        if (x > SpaceInvaders.SCREEN_WIDTH - WIDTH) {
            x = SpaceInvaders.SCREEN_WIDTH - WIDTH;
        }
    }
    /**
     * Draw method for the player, makes sure to draw him white and in the coords specified
     * @param g graphics param
     */
    public void draw(Graphics g) {
        if (reeseHead != null) {
            // Retrieve the Image object from the ImageIcon
            Image image = reeseHead.getImage();
            
            // Draw the Image onto the Graphics object
            g.drawImage(image, x, y, null);
        }
        //g.setColor(Color.WHITE);
        //g.fillRect(x, y, WIDTH, HEIGHT);
    }
    /**
     * Keypressed method for the player for movement left and right
     * @param e keyevent param to get the key pressed
     */
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) { 
            velocityX = -5; //sets movement left
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            velocityX = 5; //sets movemetn right
        }
    }
    /**
     * Keyreleased method for the player for movement left and right to stop the movement when key is released.
     * @param e keyevent param to get the key pressed
     */
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            velocityX = 0;
        }
    }
    /**
     * Get bounds method for the player. Used for hit detection
     * @return a new rectangle where the method was ran. 
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }
    /**
     * Interssects method for the player. Used for hit detection on enemy
     * @return bool that sees if the enemy and plater are touching 
     */
    public boolean intersects(Enemy enemy) {
        return getBounds().intersects(enemy.getBounds());
    }
    /**
     * Getter method for X coord. 
     * @return x coord
     */
    public int getX() {
        return x;
    }
    /**
     * Getter method for width. 
     * @return width
     */
    public int getWidth() {
        return WIDTH;
    }
    /**
     * Getter method for y coord. 
     * @return y coord
     */
    public int getY() {
        return y;
    }
}
/**
 * Enemy class for the space invaders game. Contains anything to do with the enemy
 * @author Aiden Cox
 */
class Enemy {
    private int x;
    private int y;
    private int velocityX = 3; // Enemy movement speed
    private static final int WIDTH = 30; //width 
    private static final int HEIGHT = 30;// height
    
    /**
     * Enemy contructor to make an object of the user
     * @param x x coord
     * @param y y coord
     */
    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }
    /**
     * Update method for the enemy. It moves the enemy when the border gets hit
     * @param nothing
     */
    public void update() {
        x = x + velocityX;
        if (x < 0 || x > SpaceInvaders.SCREEN_WIDTH - WIDTH) { //subtracts width so its when player first touches out of bounds
            velocityX = -velocityX; // changes direction when hitting the wall
            y = y + HEIGHT; // moves down the enemy
        }
    }
    /**
     * Draw method for the enemy, makes sure to draw him green and in the coords specified
     * @param g graphics param
     */
    public void draw(Graphics g) {
            g.setColor(Color.GREEN);
            g.fillRect(x, y, WIDTH, HEIGHT);
        
    }
    /**
     * Get bounds method for the enemy. Used for hit detection
     * @return a new rectangle where the method was ran. 
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }
}

/**
 * Bullet class for the space invaders game. Contains anything to do with the bullet  
 * @author Aiden Cox
 */
class Bullet {
    private int x, y;
    private static final int WIDTH = 5;
    private static final int HEIGHT = 10;
    private static final int SPEED = 5;
    public Bullet(int x, int y) {
        this.x = x - WIDTH / 2; //middle of the character
        this.y = y - HEIGHT;
    }
    /**
     * Update method for the bullet. It moves the bullet in one direction when the key gets pressed.
     * @param nothing
     */
    public void update() {
        y = y - SPEED; // bullet only goes up
    }
    /**
     * Draw method for the bullet, makes sure to draw it on the coords of player and colored red
     * @param g graphics param
     */
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, WIDTH, HEIGHT);
    }
    /**
     * Get bounds method for the bullet. Used for hit detection on enemy
     * @return a new rectangle where the method was ran. 
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }
    /**
     * Intersects method for the bullet. Used for hit detection on enemy
     * @return bool that sees if the enemy and bullet are touching 
     */
    public boolean intersects(Enemy enemy) {
        return getBounds().intersects(enemy.getBounds());
    }
}