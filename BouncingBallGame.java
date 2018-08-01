import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import java.awt.*;
import java.awt.event.*;

/**ƒtienne Cossart 
  * 
  * September  22, 2015
  * 
  * The objective of the game is to get the lowest score while keeping the ball in play.
  * The player uses the bumper to keep the ball in play.
  * Each collision adds a score.
  * To win, the ball must hit the green goal in the upper boundary.
  * 
  * 
  * Compared to the design, I took away the bumper drag method 
  * and replaced it the bumper following the mouse's x coordinate.
  * 
  * I also added a goal, changing the objective of the game from
  * most amount of collisions, to least amount of collisions before 
  * getting the ball in the goal.
  * 
  * I added the court changing colors at each collision
  * 
  * I added the if statement of having the ball fall out and ending the game
  * 
  * I changed the way the score was counted from an if statement to a method
  * called upon by the collision if statement.
  * */

public class BouncingBallGame extends GraphicsProgram {
  //instance variables
  private GOval ball, bumper, center;
  private GRect court, goal;
  private GPoint point;
  private int score = 1;
  private double x, y,    
    speed, xSpeed = -5, ySpeed = 2; 
  private GLabel label, goalLabel, youLose;
  private RandomGenerator rand = new RandomGenerator();
  
  //setting window size
  public static int 
    APPLICATION_WIDTH = 600,
    APPLICATION_HEIGHT = 585;
  
  //setting ball size, bumper size, delay time, and wall width
  private static final double
    BALL = 30,
    BUMPER = 70,
    DELAY = 14,
    WALL_W= 20;
  
  //setting initial variables  
  public void init() {
    double width = getWidth();
    double height = getHeight();
    
    //setting window, court, and bottom
    setBackground(Color.BLACK);
    
    court = new GRect (WALL_W, WALL_W,
                       width-2*WALL_W, height-WALL_W);
    court.setFilled(true);
    court.setColor(Color.WHITE);
    add(court);
    
    //draws the bottom end
    GRect bottom = new GRect (0 + WALL_W, 580, 600 - (2 * WALL_W), WALL_W);
    bottom.setFilled(true);
    bottom.setColor(Color.RED);
    add(bottom);
    
    //Draws green goal
    goal = new GRect (295, 0, 20, WALL_W);
    goal.setFilled(true);
    goal.setColor(Color.GREEN);
    add(goal);
    
    //start directions
    label = new GLabel ("Click to Begin (if you dare!)", width/2, 40);
    label.setFont(new Font("Sanserif", Font.BOLD, 20));
    add(label);
    
    //setting bouncing ball parameters
    ball = new GOval (WALL_W + 5, WALL_W + 5, BALL, BALL);
    ball.setFilled(true);
    ball.setFillColor(Color.ORANGE);
    add(ball);
    
    //setting bumper parameters
    bumper = new GOval (width/2-BUMPER/2, 510, BUMPER, BUMPER);
    bumper.setFilled(true);
    bumper.setFillColor(Color.YELLOW);
    add(bumper);
  }
  
  public void run() {
    waitForClick(); //wait for click of the mouse to begin game
    //use a while-loop to control animatation
   
    while (true) {
      oneTimeStep(); //perform oneTimeStep operation
      pause (DELAY);
    }
  }
  
  //sets random color of court at each collision
  private void setRandomColor (GRect court) {
    court.setColor(rand.nextColor());
    court.setFillColor(rand.nextColor());
  }
  
  //udates score label
  public void updateScore() { 
    label.setLabel ("Score:" + score++);
  }
  
  //allows the bumper to be controlled by the mouse
  public void mouseMoved (GPoint point) {
    double newX = (point.getX() - bumper.getWidth() / 2);
    
    if (newX < WALL_W) newX = WALL_W;
   
    //if the curser exits the court but stays in the window,
    //the bumper stays up against the wall
    if (newX > 600 - WALL_W - bumper.getWidth())
      newX = 600 - (WALL_W + bumper.getWidth()); 
   
    //moves the bumper to the mouse's x value
    bumper.setLocation (newX, 510);  
  }
  
  private void oneTimeStep() {
    
    //move the ball and bouce in each time step
    x = ball.getX();
    y = ball.getY();
    
    //if the ball hits the left/right wall, reverse xSpeed
    if (x < WALL_W || x + BALL > getWidth()-WALL_W){
      xSpeed = -xSpeed;}
    
    //if the ball hits top wall/bumper, reverse the y-speed
    if (y < WALL_W ) { 
      ySpeed = -ySpeed;}
    
    //calculates the distance between the ball and the bumper
    double dist = GMath.distance (
                                  ball.getX()   + ball.getWidth()    / 2,                          
                                  ball.getY()   + ball.getHeight()   / 2,                           
                                  bumper.getX() + bumper.getWidth()  / 2,                             
                                  bumper.getY() + bumper.getHeight() / 2);
    double offset = 50 - dist;
    
    if (offset > 0) {
      
      //updates the score
      score = score;
      updateScore();
      
      //udates the random color
      setRandomColor(court);
      
      //sets the ball speed and direction after the collision
      speed = Math.sqrt(Math.pow (xSpeed, 2) + 
                        Math.pow (ySpeed, 2));
      xSpeed = (speed * (ball.getX()+ ball.getWidth() /2 - 
                         bumper.getX() - 
                         bumper.getWidth()/2
                        ) / dist);
      ySpeed = (speed * (ball.getY()+ ball.getHeight()/2 - 
                         bumper.getY() -
                         bumper.getWidth()/2
                        ) / dist);  
    }
    //applies the collision calculations to the movement animation of the ball
    ball.move(xSpeed, ySpeed);
    
    //if the ball hits the goal, the player wins the game
    if (ball.getY() < WALL_W) {
      if (ball.getX() + ball.getWidth() / 2 < 310) {
        if (ball.getX() + ball.getHeight() / 2 > 290){
          
          xSpeed = 0; 
          ySpeed = 0;
          youLose = new GLabel ("YOU WIN", 175, 300);
          youLose.setFont(new Font ("Sanserif", Font.BOLD, 50));
          youLose.setColor(Color.GREEN);
          add(youLose); 
        }
       }
      }
   
    //if the ball hits the red bottom, game over
    if (ball.getY() > 550) {
          
      youLose = new GLabel ("Fatality", 200, 300);
      youLose.setFont(new Font ("Sanserif", Font.BOLD, 50));
      youLose.setColor(Color.RED);
      add(youLose); 
    }
  }
}

