import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.sound.sampled.*;

public class Window extends JFrame {
	private JLabel score_label, high_score_label;
	private boolean sound_flag = true;
	private Clip clip;
	private Game tetris;
	
	public Window() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		try {
		    UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch(Exception e) {
		    e.printStackTrace();
		}
		
        setSize(560, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocation(300, 100);
        
        setLayout(new BorderLayout());
        
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(3, 1));
        
        score_label = new JLabel("SCORE: 0");
        score_label.setFont(new Font("Calibri", Font.BOLD, 20));
        score_label.setForeground(Color.WHITE);
        score_label.setOpaque(true);
        score_label.setBackground(Color.BLACK);
        
        high_score_label = new JLabel("HIGH SCORE: ");
        high_score_label.setFont(new Font("Calibri", Font.BOLD, 20));
        high_score_label.setForeground(Color.WHITE);
        high_score_label.setOpaque(true);
        high_score_label.setBackground(Color.BLACK);
        
        JLabel title_label = new JLabel("MY TETRIS");
        title_label.setFont(new Font("Calibri", Font.BOLD, 20));
        JPanel title_panel = new JPanel();
        title_panel.add(title_label);
        
        labelPanel.add(score_label);
        labelPanel.add(high_score_label);
        labelPanel.add(title_panel);
        
        
        JLabel nextOne_label = new JLabel("NEXT TETROMINO");
        nextOne_label.setFont(new Font("Calibri", Font.BOLD, 15));
        nextOne_label.setForeground(Color.WHITE);
        nextOne_label.setOpaque(true);
        nextOne_label.setBackground(Color.BLACK);
        
        //////////////////////SOUNDTRACK////////////////////////
        File file = new File("Tetris.wav");
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
        clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
        
        tetris = new Game(score_label, high_score_label, clip);
        addKeyListener(tetris);
        tetris.updateHighScore();
        
        JButton sound_button = new JButton("AUDIO ON/OFF");
        sound_button.setFont(new Font("Calibri", Font.BOLD, 15));
        sound_button.setFocusable(false);
        sound_button.setForeground(Color.WHITE);
        sound_button.setContentAreaFilled(false);
        sound_button.setOpaque(true);
        sound_button.setBackground(Color.BLACK);
        sound_button.addActionListener(new ActionListener() {
        	
			@Override
			public void actionPerformed(ActionEvent e) {
			    
				if(sound_flag == true) {
					clip.stop();
					sound_flag = false;
				}
				else {
			        clip.loop(Clip.LOOP_CONTINUOUSLY);
					clip.start();
					sound_flag = true;
				}

			}
			
        });
        
        JButton restart_button = new JButton("RESTART");
        restart_button.setFont(new Font("Calibri", Font.BOLD, 15));
        restart_button.setFocusable(false);
        restart_button.setForeground(Color.WHITE);
        restart_button.setContentAreaFilled(false);
        restart_button.setOpaque(true);
        restart_button.setBackground(Color.BLACK);
        restart_button.addActionListener(new ActionListener() {
        	
			@Override
			public void actionPerformed(ActionEvent e) {
			   tetris.restart();
			   if(sound_flag == true) {
				   clip.stop();
				   clip.loop(Clip.LOOP_CONTINUOUSLY);
				   clip.setMicrosecondPosition(0);
			   }
			}
        });
        
        JPanel right_panel = new JPanel();
        right_panel.setLayout(new GridLayout(3, 1));
        
        right_panel.add(sound_button);
        right_panel.add(nextOne_label);   
        right_panel.add(restart_button);
        
        add(tetris, BorderLayout.CENTER);
        add(labelPanel, BorderLayout.SOUTH);
        add(right_panel, BorderLayout.EAST);

        
        setVisible(true);
        

    }
	
	public static void main(String[] args) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
         Window my_tetris = new Window();
	}
}
