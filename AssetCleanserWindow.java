import java.awt.Color;
import java.awt.EventQueue;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AssetCleanserWindow {

	private JFrame frmAssetCleanser;

	/**
	 * Launch the application.
	 */
	public static String text;
	public static int flag=1;
	static PropertyChangeSupport pcs;
	public static String errtext;
	public static int errflag=1;
	static PropertyChangeSupport errpcs;
	
	public static void RunBatch(){
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				try{
					RunPowerShellScript rsp=new RunPowerShellScript();
					rsp.start();
					}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AssetCleanserWindow window = new AssetCleanserWindow();
					window.frmAssetCleanser.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AssetCleanserWindow() {
		initialize();
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmAssetCleanser = new JFrame();
		frmAssetCleanser.setResizable(false);
		frmAssetCleanser.getContentPane().setBackground(SystemColor.controlHighlight);
		frmAssetCleanser.setTitle("Asset Cleanser");
		frmAssetCleanser.setBounds(100, 100, 688, 404);
		frmAssetCleanser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAssetCleanser.getContentPane().setLayout(null);
		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 177, 662, 187);
		frmAssetCleanser.getContentPane().add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		textArea.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				// TODO Auto-generated method stub
				if(arg0.getPropertyName().equals(textArea)){
					textArea.append('\n'+ String.valueOf(arg0.getNewValue()));
				}
				
			}
		});
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setForeground(Color.RED);
		lblNewLabel_1.setBounds(10, 89, 662, 77);
		frmAssetCleanser.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setForeground(Color.BLACK);
		lblNewLabel.setBackground(Color.WHITE);
		lblNewLabel.setBounds(10, 56, 402, 22);
		frmAssetCleanser.getContentPane().add(lblNewLabel);
	
		JButton btnNewButton_2 = new JButton("View Log File");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Process pfolder = Runtime.getRuntime().exec("explorer .\\Config");
					pfolder.wait();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		btnNewButton_2.setForeground(Color.BLACK);
		btnNewButton_2.setBackground(Color.LIGHT_GRAY);
		btnNewButton_2.setBounds(170, 11, 150, 35);
		frmAssetCleanser.getContentPane().add(btnNewButton_2);
		JButton btnNewButton_1 = new JButton("Run Script");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pcs=new PropertyChangeSupport(this);
				errpcs= new PropertyChangeSupport(this);
				System.out.println("ERR TEXT is " + errtext);
				pcs.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						// TODO Auto-generated method stub
						textArea.append(text+'\n');
						if(text.equals("EndOfTask")){
							lblNewLabel.setText("Task was completed successfully !!");
						}
						if(text.equals("ERRORFOUND")){
							lblNewLabel.setText("Task was UNSUCCESSFUL !!");
						}
						
					}
				});
				
				errpcs.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						lblNewLabel_1.setText(errtext+'\n');
						//textArea.append(text+'\n');
						if(!errtext.equals("NULL")){
							lblNewLabel_1.setText("Something Went Wrong, Please Check Manually Once");
						}
					}
				});
				RunBatch();
				textArea.append("\nStarted");
			}
		});
		btnNewButton_1.setForeground(Color.BLACK);
		btnNewButton_1.setBackground(Color.LIGHT_GRAY);
		
		btnNewButton_1.setBounds(10, 11, 150, 35);
		frmAssetCleanser.getContentPane().add(btnNewButton_1);
	}
}
class RunPowerShellScript extends Thread {
	   private Thread t;
	   public static String textArea ="";
	   public static String label1 ="";
	   public static String label ="";
	   public static Process p;
	   public static boolean flag=true;
	  
	   public RunPowerShellScript() {
		
	   }
	   public void run() {
	      System.out.println("Running Thread");
	      try {
	    	   p = Runtime.getRuntime().exec("PowerShell -ExecutionPolicy Bypass ./Cleanser.ps1");
				BufferedReader br= new BufferedReader(new InputStreamReader(p.getInputStream()));
				BufferedReader br1= new BufferedReader(new InputStreamReader(p.getErrorStream()));
				System.out.println("Script is Executed");
				DataFetcher df=new DataFetcher(br, p);
				df.start();
				ErrorFetcher ef=new ErrorFetcher(br1, p);
				ef.start();
				
				if(!p.waitFor(5, TimeUnit.MINUTES)){
					p.destroy();
					
				}
			  
	         } catch (InterruptedException | IOException e) {
	         System.out.println("Thread " + " interrupted.");
	     }
	     System.out.println("Thread " + " exiting.");
	     flag=false;
	   }
	   public void start ()
	   {
	      System.out.println("Starting");
	      if (t == null)
	      {
	         t = new Thread (this);
	         t.start ();
	      }
	   }
	}

class DataFetcher extends Thread {
	   private Thread t;
	   BufferedReader br; 
	   Process p;
	   static String textArea;
	   public DataFetcher(BufferedReader brr, Process pp) {
		br=brr;
		p=pp;
		t=new Thread(this);
	   }
	    public void run() {
	      System.out.println("Running Thread At Data Fetcher");
	      
	      //lblNewLabel.setText("some"+some);
		System.out.println("somthin");
		while(p.isAlive()){
			try {
				textArea=br.readLine();
				if(textArea!=null){
					System.out.println("Producing"+textArea);
					AssetCleanserWindow.text=textArea;
					AssetCleanserWindow.pcs.firePropertyChange(textArea, null, "hello");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				AssetCleanserWindow.text="ERRORFOUND";
				AssetCleanserWindow.pcs.firePropertyChange(textArea, null, "hello");
				e.printStackTrace();
			}
		}
	      System.out.println("Thread " + " exiting.");
			 } 
	   public void start ()
	   {
	      System.out.println("Starting");
	      
	         t.start ();
	   }
	}

class ErrorFetcher extends Thread {
	   private Thread t;
	   BufferedReader br; 
	   Process p;
	   static String textArea;
	   public ErrorFetcher(BufferedReader brr, Process pp) {
		br=brr;
		p=pp;
		t=new Thread(this);
	   }
	    public void run() {
	      System.out.println("Running Thread At Error Fetcher");
	     	while(p.isAlive()){
			try {
				textArea=br.readLine();
				if(textArea!=null){
					System.out.println("Producing Error At"+textArea);
					AssetCleanserWindow.errtext=textArea;
					AssetCleanserWindow.errpcs.firePropertyChange(textArea, null, "hello");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	      System.out.println("Thread "+"Exiting");
			 } 
	   public void start ()
	   {
	      System.out.println("Starting");
	      t.start ();
	   }
	}
