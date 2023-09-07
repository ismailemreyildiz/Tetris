import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

import javax.swing.Timer;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Game extends JPanel implements KeyListener{

	private final int SIZE = 30;
	private final int NEXT_SIZE = 25;
	private final int WIDTH = 10;
	private final int HEIGHT = 20;
	private Color[][] board = new Color[20][10];
	private Color[][] erased_board = new Color[20][10];
	private Color[][] shape;
	private Color[][] next_shape;
	private Color color;
	private Color next_color;
	private int shape_x = 120;
	private int shape_y = 0;
	private int next_shape_x;
	private int next_shape_y = 275; //240
	private boolean shape_end_flag = true;
	private boolean board_erased_flag = false;
	private Timer timer;
	private int delay = 700;
	private long currentTime;	
	private int score = 0;
	private int high_score = 0;
	private JLabel score_label;
	private JLabel high_score_label;
	private Clip clip;
	
	public Game(JLabel l, JLabel hl, Clip c) {
		score_label = l;
		high_score_label = hl;
		clip = c;
		
		timer = new Timer(1, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(delay <= currentTime) {
					repaint();
					shape_y += SIZE;
					currentTime = 0;
				}
				currentTime++;
			}
			
		});
		timer.start();
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Color.BLACK);
		
		///////////////////CHECKING SHAPE//////////////////////		
		checkShape();
		
		///////////////////CREATING SHAPE///////////////////////
		if(shape_end_flag == true) 
			makeShape();
		
		///////////////////DRAWING BOARD///////////////////////
		if(board_erased_flag == true)
			paintErased(g);
		else
		for(int i = 0; i < board.length; i++) 
			for(int j = 0; j< board[0].length; j++) 
				if(board[i][j] != null) {
					g.setColor(board[i][j]);
					g.fillRect(j * SIZE, i * SIZE, SIZE, SIZE);
				}
		
		////////////////////DRAWING SHAPE//////////////////////
		int a = 0;
		for(int i = 0; i < shape.length; i++) {
			for(int j = 0; j< shape[0].length; j++) {
				if(shape[i][j] != null) {
					g.setColor(shape[i][j]);
					g.fillRect(shape_x, shape_y, SIZE, SIZE);
				}
				shape_x += SIZE;
			}
			shape_x -= SIZE * shape[0].length;
			shape_y += SIZE;
			a++;
		}
		
		shape_y -= SIZE * a;
		
		////////////////////DRAWING NEXT SHAPE/////////////////
		if(next_shape[0].length == 3)
			next_shape_x = 320;
		else if(next_shape[0].length == 1)
			next_shape_x = 345;
		else
			next_shape_x = 330;
		
		int b = 0;
		for(int i = 0; i < next_shape.length; i++) {
			for(int j = 0; j< next_shape[0].length; j++) {
				if(next_shape[i][j] != null) {
					g.setColor(next_shape[i][j]);
					g.fillRect(next_shape_x, next_shape_y, NEXT_SIZE, NEXT_SIZE);
				}
				next_shape_x += NEXT_SIZE;
			}
			next_shape_x -= NEXT_SIZE * next_shape[0].length;
			next_shape_y += NEXT_SIZE;
			b++;
		}
		next_shape_y -= NEXT_SIZE * b;
		
		////////////////////DRAWING LINES OF NEXT SHAPE/////////
		g.setColor(Color.WHITE);
		
		
		int c = 0;
		for(int i = 0; i < next_shape.length; i++) {
			for(int j = 0; j< next_shape[0].length; j++) {
				if(next_shape[i][j] != null) {
					g.drawLine(next_shape_x, next_shape_y, next_shape_x + NEXT_SIZE, next_shape_y);
					g.drawLine(next_shape_x, next_shape_y + NEXT_SIZE, next_shape_x + NEXT_SIZE, next_shape_y + NEXT_SIZE);
					g.drawLine(next_shape_x, next_shape_y, next_shape_x, next_shape_y + NEXT_SIZE);
					g.drawLine(next_shape_x + NEXT_SIZE, next_shape_y, next_shape_x + NEXT_SIZE, next_shape_y + NEXT_SIZE);
				}
				next_shape_x += NEXT_SIZE;
			}
			next_shape_x -= NEXT_SIZE * next_shape[0].length;
			next_shape_y += NEXT_SIZE;
			c++;
		}
		next_shape_y -= NEXT_SIZE * c;
		
		////////////////////DRAWING LINES//////////////////////
		//DRAWING ROWS 
		for(int i = 0; i <= HEIGHT; i++)
			g.drawLine(0, i * SIZE, SIZE * WIDTH, i * SIZE);
		//DRAWING COLLUMNS
		for(int i = 0; i <= WIDTH; i++)
			g.drawLine(i * SIZE, 0, i * SIZE, SIZE * HEIGHT);
		
	}
	
	/////////////////////DRAWING ERASED SHAPE FOR ON/////////////////
	private void paintErased(Graphics g) {
		for(int i = 0; i < erased_board.length; i++) 
			for(int j = 0; j< erased_board[0].length; j++) 
				if(erased_board[i][j] != null) {
					g.setColor(erased_board[i][j]);
					g.fillRect(j * SIZE, i * SIZE, SIZE, SIZE);
				}
		board_erased_flag = false;
	}
	
	private void checkBoard() {
		int row_counter = 0;
		for(int i = 0; i < board.length; i++) {
			//////////////////LOOKING FOR IF THERE IS FULL ROW////////////////////
			for(int j = 0; j < board[0].length; j++) {
				if(board[i][j] != null)
					row_counter++;
			}
			/////////////////ERASING THE ROW//////////////////////////////////////
			if(row_counter == 10) {
				for(int j = 0; j < board[0].length; j++) 
					board[i][j] = null;
				cloneBoard();
				rearangeBoard(i);
			}
			row_counter = 0;
		}
	}
	
	private void cloneBoard() {
		for(int i = 0; i < board.length; i++)
			for(int j = 0; j < board[0].length; j++)
				erased_board[i][j] = board[i][j];
		board_erased_flag = true;
	}
	
	private void rearangeBoard(int r) {
		r--;
		while(r >= 0) {
			for(int j = 0; j < board[0].length; j++) {
				if(board[r][j] != null) {
					board[r + 1][j] = board[r][j];
					board[r][j] = null;
				}
			}
			r--;
		}
	}
	
	private void checkShape() {
		
		if(shape != null) {
			////////////////CHECKING FOR IF IT IS AT THE BOTTOM OF THE BOARD////////////////
			if(shape_y + shape.length * SIZE >= 600) {	
				fillBoard();
				shape_end_flag = true;
				shape_y = 0;
				shape_x = 120;
				return;
			}
						
			////////////////CHECKING FOR IF IT IS TOP AT A TETROMINO////////////////////////
			for(int i = 0; i < shape.length; i++)
				for(int j = 0; j < shape[0].length; j++) 
					if(shape[i][j] != null) 
						if(board[shape_y / SIZE + i + 1][shape_x / SIZE + j] != null) {
							if(shape_y - SIZE <= SIZE) {
								timer.stop();
								new PopUp();
								updateHighScore();
							}
							fillBoard();
							shape_end_flag = true;
							shape_y = 0;
							shape_x = 120;
							break;
						}
		}
	}
	
	private void fillBoard() {
		for(int i = 0; i < shape.length; i++)
			for(int j = 0; j < shape[0].length; j++)
				if(shape[i][j] != null)
					board[shape_y / SIZE + i][shape_x / SIZE + j] = color;
		updateScore();
		checkBoard();
		
	}
	
	private void updateScore() {
		score++;
		score_label.setText("SCORE: " + score );
	}
	
 public void updateHighScore() {
		
		File f = new File("highscore.txt");
		
		if(f.exists() && !f.isDirectory()) { 
		    try {
				Scanner scan = new Scanner(new File("highscore.txt"));
				high_score = scan.nextInt();
				scan.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		if(score > high_score) {
			high_score = score + 1;
			high_score_label.setText("HIGH SCORE: " + high_score);
			
			try {
				PrintWriter writer = new PrintWriter(new FileOutputStream("highscore.txt"));
				writer.println(high_score);
				writer.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		else {
			high_score_label.setText("HIGH SCORE: " + high_score);
		}
	}
/*	----
	----
	----
	----*/
	
	private void makeShape() {
		Random selector = new Random();
		Color color = null;
		
		////////////////CHOSING COLOR/////////////////////////
		int number = selector.nextInt(6);
		if(number == 0)
			color = Color.BLUE;
		if(number == 1)
			color = Color.RED;
		if(number == 2)
			color = Color.GREEN;
		if(number == 3)
			color = Color.YELLOW;
		if(number == 4)
			color = Color.PINK;
		if(number == 5)
			color = Color.ORANGE;
		
		this.color = color;
		
		///////////////FILLING SHAPE//////////////////////////
		number = selector.nextInt(7);
		// _|_
		if(number == 0) {
			shape = new Color[2][3];
			shape[0][1] = color;
			shape[1][0] = color;
			shape[1][1] = color;
			shape[1][2] = color;
		}
		
		// _|-
		if(number == 1) {
			shape = new Color[2][3];
			shape[0][1] = color;
			shape[0][2] = color;
			shape[1][1] = color;
			shape[1][0] = color;
		}
		
		// -|_
		if(number == 2) {
			shape = new Color[2][3];
			shape[0][1] = color;
			shape[0][0] = color;
			shape[1][1] = color;
			shape[1][2] = color;
		}
		
		// |__
		if(number == 3) {
			shape = new Color[3][2];
			shape[0][0] = color;
			shape[1][0] = color;
			shape[2][0] = color;
			shape[2][1] = color;
		}
		
		// __|
		if(number == 4) {
			shape = new Color[3][2];
			shape[0][1] = color;
			shape[1][1] = color;
			shape[2][1] = color;
			shape[2][0] = color;
		}
		
		// ___
		if(number == 5) {
			shape = new Color[4][1];
			shape[0][0] = color;
			shape[1][0] = color;
			shape[2][0] = color;
			shape[3][0] = color;
		}
		
		// #
		if(number == 6) {
			shape = new Color[2][2];
			shape[0][0] = color;
			shape[1][1] = color;
			shape[0][1] = color;
			shape[1][0] = color;
		}
		
		shape_end_flag = false;
		
		if(next_shape == null) {
			next_shape = shape;
			next_color = color;
			makeShape();
		}
		else {
			Color[][] temp = shape;
			shape = next_shape;
			next_shape = temp;
			Color tempc = this.color;
			this.color = next_color;
			next_color = tempc;
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			delay = 70;
		}
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			boolean condition = true;
			while(condition) {
				if(shape != null) {
					////////////////PUTTING AT THE BOTTOM OF THE BOARD////////////////
					if(shape_y + shape.length * SIZE >= 600) {
						shape_y = 600 - shape.length * SIZE;
						repaint();
						break;
					}

					////////////////PUTTING AT A TOP OF THE TERMINO////////////////////////
					for(int i = 0; i < shape.length; i++)
						for(int j = 0; j < shape[0].length; j++) 
							if(shape[i][j] != null) 
								if(board[shape_y / SIZE + i + 1][shape_x / SIZE + j] != null) {
									shape_y -= SIZE;
									repaint();
									condition = false;
								}
					shape_y += SIZE;
				}
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			if(shape_x > 0) {
				///////////////////CHECKING IF THERE IS A TETROMINO AT THE LEFT//////////////
				int x = shape_x;
				int y = shape_y;
				boolean condition = true;
				for(int i = 0; i < shape.length; i++) {
					for(int j = 0; j < shape[0].length; j++) {
						if(shape[i][j] != null)
							if(board[y / SIZE][x / SIZE - 1] != null) 
								condition = false;
						x += SIZE;
					}
					x -= SIZE * shape[0].length;
					y += SIZE;
				}
				
				if(condition == true) {
					shape_x -= 30;
					repaint();
				}
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if(shape_x + shape[0].length * SIZE < 300) {
				///////////////////CHECKING IF THERE IS A TETROMINO AT THE RIGHT//////////////
				int x = shape_x;
				int y = shape_y;
				boolean condition = true;
				for(int i = 0; i < shape.length; i++) {
					for(int j = 0; j < shape[0].length; j++) {
						if(shape[i][j] != null)
							if(board[y / SIZE][x / SIZE + 1] != null) 
								condition = false;
						x += SIZE;
					}
					x -= SIZE * shape[0].length;
					y += SIZE;
				}
				
				if(condition == true) {
					shape_x += 30;
					repaint();
				}
			}
		}
		//////////////////////ROTATING TETROMINOS//////////////
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			
			///////////////////CREATING TEMPERORY SHAPE/////////////
			Color[][] temp_shape = new Color[shape.length][shape[0].length];
			for(int i = 0; i < shape.length; i++)
				for(int j = 0; j < shape[0].length; j++)
					if(shape[i][j] != null) 
						temp_shape[i][j] = shape[i][j];
			
			shape = null;
			shape = new  Color[temp_shape[0].length][temp_shape.length];
			
			///////////////////PASSING ELEMNTS TO NEW SHAPE//////////////
			for(int i = 0; i < temp_shape.length; i++)
				for(int j = 0; j < temp_shape[0].length; j++)
					if(temp_shape[i][j] != null) 
						shape[j][shape[0].length - i - 1] = temp_shape[i][j];
			
			if(shape_x + shape[0].length * SIZE > 300) {
				shape_x = 300 - shape[0].length * SIZE;
			}
			
			repaint();
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			delay = 700;
		}
	}
	
	public void restart() {
		board = new Color[20][10];
		shape_y = - SIZE;
		shape_x = 120;
		shape_end_flag = true;
		score = -1;
		updateScore();
		timer.start();
	}
	
	public int getScore() {
		return score;
	}
	
	class PopUp extends JFrame implements ActionListener{
		private boolean music_state = false;
		
		public PopUp() {
			setSize(500, 100);
			
			JLabel end_label = new JLabel("GAME OVER", SwingConstants.CENTER);
			end_label.setFont(new Font("Calibri", Font.BOLD, 20));
			setLayout(new BorderLayout());
			JPanel buttons = new JPanel();
			buttons.setLayout(new FlowLayout());
			JButton exit = new JButton("exit");
			JButton restart = new JButton("restart");
			exit.addActionListener(this);
			restart.addActionListener(this);
			buttons.add(exit);
			buttons.add(restart);
			add(end_label,BorderLayout.CENTER);
			add(buttons,BorderLayout.SOUTH);
			
			if(clip.isRunning()) {
				clip.stop();
				music_state = true;
			}
			
			setVisible(true);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("exit")) {
				System.exit(0);
			}
			else if(e.getActionCommand().equals("restart")){
				dispose();
				restart();
				if(music_state == true) {
					   clip.stop();
					   clip.loop(Clip.LOOP_CONTINUOUSLY);
					   clip.setMicrosecondPosition(0);
					   music_state = false;
				}
			}
			else {
				dispose();
			}
		}
	}
}
