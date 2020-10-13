/**
 * @(#)HeightMapGUI.java
 *
 *
 * @author 
 * @version 1.00 2018/4/19
 */
import java.text.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.swing.*;
import javax.swing.event.*;


public class HeightMapGUI extends JFrame implements ActionListener, ChangeListener{
        
    /**
     * Creates a new instance of <code>HeightMapGUI</code>.
     */
     
    JPanel panel = new JPanel();

    JButton genButton = new JButton("Generate"); 
    JSlider volSlider = new JSlider(JSlider.HORIZONTAL,0,100,50);
    JLabel volLabel = new JLabel("Volatility: 1",JLabel.CENTER);
    JLabel sizeLabel = new JLabel("Size (2^value+1): ",JLabel.CENTER);
    JTextField sizeTF = new JTextField(7);
    
    MapPanel mapPanel = new MapPanel();
    
    int[][] map;
    double[][] normalMap;
    int[][][] colMap;
    int[][][] colMat;
    Color[][] colBuff;

    double volatility = 0.0;
    
    final DecimalFormat df = new DecimalFormat("#.##");
    final Color lime = new Color(50,205,50);
    
    public HeightMapGUI() {
    	super("Heightmap GUI");
    	setBounds(280,100,1000,700);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setResizable(false);
    	Container cont = this.getContentPane();
    	cont.add(panel,BorderLayout.SOUTH);
    	cont.add(mapPanel,BorderLayout.CENTER);
    	volLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    	volLabel.setHorizontalAlignment(JLabel.CENTER);
    	volLabel.setVerticalAlignment(JLabel.TOP);
    	sizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    	sizeLabel.setHorizontalAlignment(JLabel.CENTER);
    	sizeLabel.setVerticalAlignment(JLabel.TOP);
    	volSlider.addChangeListener(this);
    	genButton.addActionListener(this);
    	sizeTF.addActionListener(this);
    	panel.add(sizeLabel);
    	panel.add(sizeTF);
    	panel.add(volLabel);
    	panel.add(volSlider);
    	panel.add(genButton);	
    	setVisible(true);
    	run();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new HeightMapGUI();
    }
    
    public void run(){
    	try {
    		while(true){
    			if (map != null){
		        	mapPanel.repaint();
		        	//Thread.sleep(16);
    			}
    		}
    	}catch(Exception e){
    		
    	}
    }
    
    public double sliderLog(int n){
    	if (n <= 50){
    		return (0.0+n)/50.0;
    	} else if (n<=60){
    		return 1.0+(n-50.0)/10.0;
    	} else if (n == 100) {
    		return 300;
    	} else {
    		return (2)*Math.exp((n-60.0)/8.0);
    	}
    }
    
    public double[][] normalize(int[][] map){
    	double[][] nmap = new double[map.length][map.length];
    	int largest = Integer.MIN_VALUE;
    	int smallest = Integer.MAX_VALUE;
    	for(int r = 0; r < map.length; r++){
    		for(int c = 0; c < map.length; c++){
    			if (map[r][c] > largest){
    				largest = map[r][c];
    			}
    		}
    	}
    	for(int r = 0; r < map.length; r++){
    		for(int c = 0; c < map.length; c++){
    			if (map[r][c] < smallest){
    				smallest = map[r][c];
    			}
    		}
    	}
    	int diff = largest-smallest;
    	if (largest > 0) {
	    	for(int r = 0; r < map.length; r++){
	    		for(int c = 0; c < map.length; c++){
	    			nmap[r][c] = ((double)(map[r][c]-smallest))/((double)diff);
	    		}
	    	}
    	}
    	return nmap;
    }
    
    public void printMap(int[][] map){
    	int largest = 0;
    	for(int r = 0; r < map.length; r++){
    		for(int c = 0; c < map.length; c++){
    			if (map[r][c] > largest){
    				largest = map[r][c];
    			}
    		}
    	}
    	int maxdigits = 1;
    	maxdigits = (int)(Math.log10(largest)+1);
    	for(int r = 0; r < map.length; r++){
    		for(int c = 0; c < map.length; c++){
    			int test = 1;
    			if (map[r][c] > 0){
    				test = (int)(Math.log10(map[r][c])+1);
    			}
    			System.out.print(map[r][c]);
    			while (test < maxdigits+1){
    				System.out.print(" ");
    				test++;
    			}
    		}
    		System.out.println();
    	}
    }
    
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == genButton){
				Heightmap hmap; 
				try{
				hmap = new Heightmap((int)(Math.pow(2,Integer.parseInt(sizeTF.getText())))+1,1000,sliderLog(volSlider.getValue()));
				//hmap.printMap(hmap.getMap());
				map = hmap.getMap();
				normalMap = normalize(map);
				} catch (OutOfMemoryError ee){
					hmap = null;
					map = null;
					normalMap = null;
					System.out.println("Size too large.");
				}
				/*for(int i = 0; i < colMap.length; i++){
					colMap.get(i).clear();
				}*/	
				colMap = null;
		}
	}
	public void stateChanged(ChangeEvent e) {
		JSlider src = (JSlider)e.getSource();
		if (!src.getValueIsAdjusting()) {
			volatility = sliderLog(src.getValue());
			volLabel.setText("Volatility: "+df.format(volatility));
		}
	}
	
	
	class MapPanel extends JPanel implements MouseListener, MouseMotionListener {
		
		private BufferedImage canvas;
   
    	int viewX = 0;
		int viewY = 0;
		
		boolean clicked = false;
		int clickX = 0;
		int clickY = 0;
		
		public MapPanel()
		{
			super();
			addMouseListener(this);
			addMouseMotionListener(this);
			canvas = new BufferedImage(1000,700,BufferedImage.TYPE_INT_ARGB);
			initCanvas(Color.DARK_GRAY);
			repaint();
		}
		
		@Override
		public void update(Graphics g){
			paintComponent(g);	
		}
		
		@Override
	    public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;
			
			genColMap();
			
			if (clicked){
				viewX = clickX-(int)MouseInfo.getPointerInfo().getLocation().getX()+HeightMapGUI.this.getX()+4;
				viewY = clickY-(int)MouseInfo.getPointerInfo().getLocation().getY()+HeightMapGUI.this.getY()+26;
			}
			
			g2.drawImage(canvas,null,null);
		}
		
		public void genColMap(){
			try{
				if (normalMap != null){
					if (colMap == null){
					colMap = new int[(int)(Math.pow(2,Integer.parseInt(sizeTF.getText())))+1][(int)(Math.pow(2,Integer.parseInt(sizeTF.getText())))+1][3];
						for(int r = 0; r < normalMap.length; r++){
							for(int c = 0; c < normalMap.length; c++){
								if (normalMap[r][c] < 0.3){
									int[] col = {(int)(250*Math.pow(normalMap[r][c]/.3,2)*.3),(int)(80+150*Math.pow(normalMap[r][c]/.3,2)*.3),255};
									colMap[r][c] = col;
								} else if (normalMap[r][c] >= 0.3 && normalMap[r][c] < 0.305){
									/*if (r != 0 && r != normalMap.length-1 && c != 0 && c != normalMap.length-1){
										if (normalMap[r][c-1] > 0.3045 || normalMap[r-1][c] > 0.3045 || normalMap[r][c+1] > 0.3045 || normalMap[r+1][c] > 0.3045){
											int[] col = {(int)(255-500*(normalMap[r][c]-.3)),(int)(250-500*(normalMap[r][c]-.3)),(int)(200-1000*(normalMap[r][c]-.3))};
											colMap[r][c] = col;
										}
										else{
											int[] col = {(int)(250*.3),(int)(80+150*.3),255};
											colMap[r][c] = col;
										}
									} else {
										int[] col = {(int)(250*.3),(int)(80+150*.3),255};
										colMap[r][c] = col;
									}*/	
									if (r != 0 && r != normalMap.length-1 && c != 0 && c != normalMap.length-1){
										int nearLand = 0;
										if (normalMap[r-1][c-1] >= 0.305){
											nearLand++;
										}
										if (normalMap[r][c-1] >= 0.305){
											nearLand++;
										}
										if (normalMap[r+1][c-1] >= 0.305){
											nearLand++;
										}
										if (normalMap[r-1][c] >= 0.305){
											nearLand++;
										}
										if (normalMap[r+1][c] >= 0.305){
											nearLand++;
										}
										if (normalMap[r-1][c+1] >= 0.305){
											nearLand++;
										}
										if (normalMap[r][c+1] >= 0.305){
											nearLand++;
										}
										if (normalMap[r+1][c+1] >= 0.305){
											nearLand++;
										}
										if (Math.random()*(nearLand) > 1){
											int[] col = {(int)(255-500*(normalMap[r][c]-.3)),(int)(250-500*(normalMap[r][c]-.3)),(int)(200-1000*(normalMap[r][c]-.3))};
											colMap[r][c] = col;
										}
										else{
											int[] col = {(int)(250*.3),(int)(80+150*.3),255};
											colMap[r][c] = col;
										}
									}
									else{
										int[] col = {(int)(250*.3),(int)(80+150*.3),255};
										colMap[r][c] = col;
									}
								} else if (normalMap[r][c] >= 0.305 && normalMap[r][c] < 0.83){
									if (r != 0 && r != normalMap.length-1 && c != 0 && c != normalMap.length-1){
										if (normalMap[r][c-1] > 0.302 || normalMap[r-1][c] > 0.302 || normalMap[r][c+1] > 0.302 || normalMap[r+1][c] > 0.302){
											int[] col = {0,(int)(255-155*(1-Math.pow(1-((normalMap[r][c]-.305)/.525),2))),(int)(72-42*(1-Math.pow(1-((normalMap[r][c]-.305)/.525),2)))};
											colMap[r][c] = col;
										}
										else{
											int[] col = {165,165,165};
											colMap[r][c] = col;
										}
									}
									else {
										int[] col = {0,(int)(255-155*(1-Math.pow(1-((normalMap[r][c]-.305)/.525),2))),(int)(72-42*(1-Math.pow(1-((normalMap[r][c]-.305)/.525),2)))};
										colMap[r][c] = col;
									}
								} else if (normalMap[r][c] >= 0.83 && normalMap[r][c] < 0.88){
									int[] col = {80+(int)((normalMap[r][c]-.83)*2000),(int)(100+(normalMap[r][c]-.83)*1500),80+(int)((normalMap[r][c]-.83)*2000)};
									colMap[r][c] = col;
								} else if (normalMap[r][c] >= 0.88 && normalMap[r][c] < 0.9){
									int[] col = {165,165,165};
									colMap[r][c] = col;
								} else if (normalMap[r][c] >= 0.9){
									int[] col = {(int)(245+10*(normalMap[r][c]-.9)),(int)(245+10*(normalMap[r][c]-.9)),(int)(245+10*(normalMap[r][c]-.9))};
									colMap[r][c] = col;
								}
							}
						}
					}
					/*if (colMap != null){
						if (colMap.length >= 1){
							for(int y = viewY; y < viewY+700; y++){
								for(int x = viewX; x < viewX+1000; x++){
									if (y >= colMap.length || x >= colMap.length){
										g.setColor(Color.DARK_GRAY);
									}
									else if (y >= 0 && x >= 0){
										int[] col = colMap[y][x];
										g.setColor(new Color(col[0],col[1],col[2]));
									} else {
										g.setColor(Color.DARK_GRAY);
									}
									g.fillRect(x-viewX,y-viewY,1,1);
								}
							}
						}
					}*/
					if (colMap != null){
						if (colMap.length >= 1){
							for(int y = viewY; y < viewY+700; y++){
								for(int x = viewX; x < viewX+1000; x++){
									if (y >= colMap.length || x >= colMap.length){
										canvas.setRGB(x-viewX,y-viewY,(Color.DARK_GRAY).getRGB());
									}
									else if (y >= 0 && x >= 0){
										int[] col = colMap[y][x];
										canvas.setRGB(x-viewX,y-viewY,(new Color(col[0],col[1],col[2])).getRGB());
									} else {
										canvas.setRGB(x-viewX,y-viewY,(Color.DARK_GRAY).getRGB());
									}
								}
							}
						}
					}
				}
					
			} catch (OutOfMemoryError ee){
				map = null;
				normalMap = null;
				/*for(int i = 0; i < colMap.size(); i++){
					colMap.get(i).clear();
				}*/	
				colMap = null;
				System.out.println("Size too large");
			}
		}
		
		public void initCanvas(Color c){
			for(int i = 0; i < 700; i++){
				for(int j = 0; j < 1000; j++){
					canvas.setRGB(j,i,c.getRGB());
				}
			}
		}
		
		public void mouseClicked(MouseEvent e){
			
		}
		public void mousePressed(MouseEvent e){
			clicked = true;
			clickX = viewX+e.getX();
			clickY = viewY+e.getY();
		}
		public void mouseReleased(MouseEvent e){
			clicked = false;
		}
		public void mouseEntered(MouseEvent e){
			
		}
		public void mouseExited(MouseEvent e){
			
		}
		public void mouseMoved(MouseEvent e){
			
		}
		public void mouseDragged(MouseEvent e){
			
		}
	}
}

