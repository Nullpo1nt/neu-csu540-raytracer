package raytracer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.*;


/**
 * Simple Swing window to allow manipulation of a ray tracer.
 * 
 * @author Bryant Martin
 */
@SuppressWarnings("serial")
public class Main extends JFrame implements ActionListener {
	static final String APP_TITLE = "CSU540 - Ray Tracer";
	public static final int WINDOW_WIDTH = 600;
	
	Raytracer r = new Raytracer();
	Scene scene;
	File sceneFile;
	Image image;
	
	JPanel renderWindow;
	//JScrollPane sp = new JScrollPane();
	
	
	JMenuBar mb = new JMenuBar();
	
	JMenu file = new JMenu("File");
	JMenuItem loadScene = new JMenuItem("Load Scene");
	JMenuItem reloadScene = new JMenuItem("Reload");
	JMenuItem saveScreen = new JMenuItem("Save Screen");
	JMenuItem exit = new JMenuItem("Exit");
	
	JMenu settings = new JMenu("Settings");
		
	JMenu subSample = new JMenu("Subsampling");
	JMenuItem ss0 = new JCheckBoxMenuItem("None",true);
	JMenuItem ss2 = new JCheckBoxMenuItem("2x2 Regular");
	JMenuItem ss4 = new JCheckBoxMenuItem("4x4 Regular");
	
	JMenu reflection = new JMenu("Recursion Limit");
	JMenuItem rl1  = new JCheckBoxMenuItem("1");
	JMenuItem rl10 = new JCheckBoxMenuItem("10");
	JMenuItem rl30 = new JCheckBoxMenuItem("30", true);
	
	JButton render = new JButton("Render");
	
	
	public Main() {
		super(APP_TITLE);
				
		initWindow();
	}
	
	
	public void initWindow() {
		addWindowListener(new WindowAdapter(){
			public void windowClosing(final WindowEvent e) { System.exit(0); }
		});
		
		setSize(WINDOW_WIDTH, WINDOW_WIDTH + 44);
		renderWindow = new JPanel() {
			public void paint(Graphics g) {
				if (image != null) {
					((Graphics2D)g).drawImage(image,0,0,null);
				} else {
					g.setColor(Color.GRAY);
					g.fillRect(0,0,getWidth(),getHeight());
					g.setColor(Color.RED);
					g.drawLine(0,0,getWidth(),getHeight());
					g.drawLine(getWidth(),0,0,getHeight());
				}
			}
		};
		
		//Create and set up the window.
		loadScene.addActionListener(this);
		reloadScene.addActionListener(this);
		saveScreen.addActionListener(this);
		saveScreen.setEnabled(false);
		exit.addActionListener(this);

		file.add(loadScene);
		file.add(reloadScene);
		file.add(new JSeparator());
		file.add(saveScreen);
		file.add(new JSeparator());
		file.add(exit);

		ss0.addActionListener(this);
		ss2.addActionListener(this);
		ss4.addActionListener(this);
		subSample.add(ss0);
		subSample.add(ss2);
		subSample.add(ss4);
		
		rl1.addActionListener(this);
		rl10.addActionListener(this);
		rl30.addActionListener(this);
		reflection.add(rl1);
		reflection.add(rl10);
		reflection.add(rl30);
		
		settings.add(reflection);
		settings.add(subSample);
		
		render.addActionListener(this);
		render.setEnabled(false);
		
		mb.add(file);
		mb.add(settings);
		mb.add(render);
		
		renderWindow.setSize(WINDOW_WIDTH, WINDOW_WIDTH);
		
		//Create and set up the content pane.	
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		c.add(mb, BorderLayout.NORTH);
		c.add(renderWindow, BorderLayout.CENTER);
		
		//Display the window.
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		if (source == exit) {
			System.exit(0);
		}
		
		if (source == loadScene) {
			JFileChooser chooser = new JFileChooser(".");
			
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				sceneFile = chooser.getSelectedFile();
			}
		}
		
		if(source == loadScene || source == reloadScene) {
			if (sceneFile != null) {
				try {
					scene = new Scene(sceneFile);
					
					System.out.println(scene);
					
					setTitle(APP_TITLE + " - " + sceneFile.getName());
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					scene = null;
					sceneFile = null;
					setTitle(APP_TITLE);
				}
				
				image = null;
				renderWindow.repaint();
				render.setEnabled(scene != null);
			}
			
		}
		
		if (source == render) {
			image = new BufferedImage(renderWindow.getWidth(), renderWindow.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			
			Thread t = new Thread(new Runnable() {
				public void run() {
					r.render(image, scene);
					renderWindow.repaint();
				}
			});

			t.start();
		}
		
		if (source == ss0) {
			r.setSubsample(1);
			
			ss0.setSelected(true);
			ss2.setSelected(false);
			ss4.setSelected(false);
		}
		
		if (source == ss2) {
			r.setSubsample(2);
			
			ss0.setSelected(false);
			ss2.setSelected(true);
			ss4.setSelected(false);
		}
		
		if (source == ss4) {
			r.setSubsample(4);
			
			ss0.setSelected(false);
			ss2.setSelected(false);
			ss4.setSelected(true);
		}
		
		if (source == rl1) {
			r.setRecursionLimit(1);
			
			rl1.setSelected(true);
			rl10.setSelected(false);
			rl30.setSelected(false);
		}
		
		if (source == rl10) {
			r.setRecursionLimit(10);
			
			rl1.setSelected(false);
			rl10.setSelected(true);
			rl30.setSelected(false);
		}
		
		if (source == rl30) {
			r.setRecursionLimit(30);
			
			rl1.setSelected(false);
			rl10.setSelected(false);
			rl30.setSelected(true);
		}
	}
	
	public static void main(final String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            setDefaultLookAndFeelDecorated(true);
        } catch(Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
           public void run() {
               new Main();
           }
        });
	}
}

