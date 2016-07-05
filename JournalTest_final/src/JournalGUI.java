// GUI design

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.*;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.JRadioButton;



public class JournalGUI {
private static String origTotal;
private static String annual;
private static String outFolder;

private static JTextField annualExcel;
private static JTextField OrigExcel;
private static JTextField OutputFiles;
private static  JFrame app;

private static JButton btnOpen_1;
private static JButton btnOpen_2 ;
private static JButton btnOpen_3 ;

private static JButton btnCombine;
private static JButton btnExit;

private static JToolBar toolBar;
private static JLabel lbLabel_result;
private static JRadioButton rdBtn_One;
private static  JRadioButton rdBtn_Batch;

private JournalGUI() {
	initilization();
btnOpen_1.addActionListener(new ActionListener(){
    //
    public void actionPerformed(ActionEvent e) {
    	if(rdBtn_Batch.isSelected()) {
        	JFileChooser chooser = new JFileChooser();
	        chooser.setCurrentDirectory(new File("."));
        	chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        	
	        int result =chooser.showDialog(chooser,"选择目标文件夹");
	        if(result==JFileChooser.APPROVE_OPTION){
	            annual=chooser.getSelectedFile().getPath();
	            annualExcel.setText(annual);
	        }
        }
        else if(rdBtn_One.isSelected()) {
        	JFileChooser chooser = new JFileChooser();
	        chooser.setCurrentDirectory(new File("."));
	        chooser.setFileFilter(new FileNameExtensionFilter("Excel files", "xls", "xlsx"));
	        chooser.setAcceptAllFileFilterUsed(false);
	        int result =chooser.showDialog(chooser,"选择目标文件夹");
	        if(result==JFileChooser.APPROVE_OPTION){
	            annual=chooser.getSelectedFile().getPath();
	            annualExcel.setText(annual);
	        }
	        
        }
        else
        	JOptionPane.showMessageDialog(app,"请选择合并文件形式！\n批处理/单文件合并", "Error", JOptionPane.ERROR_MESSAGE);
    }
});
btnOpen_2.addActionListener(new ActionListener(){
    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.setFileFilter(new FileNameExtensionFilter("Excel files", "xls", "xlsx"));
        chooser.setAcceptAllFileFilterUsed(false);
        int result =chooser.showDialog(chooser,"选择总期刊列表");
        if(result==JFileChooser.APPROVE_OPTION){
            origTotal=chooser.getSelectedFile().getPath();
            OrigExcel.setText(origTotal);
        }
    }
});

btnOpen_3.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
	    	chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    	//chooser.setAcceptAllFileFilterUsed(false);
		int result =chooser.showDialog(chooser,"选择目标文件夹");
		if(result==JFileChooser.APPROVE_OPTION){
		    outFolder=chooser.getSelectedFile().getPath();
		    OutputFiles.setText(outFolder);
        }
	}
});

btnCombine.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
		if(annual == null || origTotal == null) {
			JOptionPane.showMessageDialog(app,"请输入期刊列表！\n至少应输入年度期刊列表及总期刊列表", "Error", JOptionPane.ERROR_MESSAGE);
		}
		btnCombine.setEnabled(false);
		final JProgressBar progressBar;
		progressBar= new JProgressBar();
		progressBar.setForeground(Color.GREEN);
		progressBar.setStringPainted(true);
		progressBar.setEnabled(false);
		progressBar.setBorderPainted(false);
		Timer timer0 = new Timer(0 , new ActionListener()  
        {  
            public void actionPerformed(ActionEvent e)  
            {  
                //以任务的当前完成量设置进度条的value  
                if(rdBtn_Batch.isSelected())
                	progressBar.setValue(0);  
                lbLabel_result.setText("正在进行文件合并……");
            }
        });  
		if(rdBtn_Batch.isSelected()) {
			progressBar.setEnabled(true);
			progressBar.setBorderPainted(true);
			toolBar.add(progressBar);
			//final JournalCombine target = new JournalCombine();  
	        
	        //new Thread(target).start();  
			
	        //设置进度条的最大值和最小值,  
	        progressBar.setMinimum(0);   
	        //以总任务量作为进度条的最大值  
	        progressBar.setMaximum(100);  
	        
	        timer0.start();
	        
		}
		
		final JournalCombine jTest;
		String[] args = new String[4];
		if(rdBtn_One.isSelected()) {
			args[0] = "0";	//mode selection for JournalCombine: 0-- one file combine
			args[1] = annual;
			args[2] = origTotal;
			
			if(annual != null && origTotal != null && outFolder == null) {
				String[] tmp = annual.split("/");
				outFolder = annual.substring(0, annual.indexOf(tmp[tmp.length-1]));
				OutputFiles.setText(outFolder);
				args[3] = outFolder+"/";
			}
			else {
				//String[] splitFile = files[i].split("/");
				args[3] = outFolder+"/";//+ "/"+ splitFile[splitFile.length-1].substring(0, splitFile[splitFile.length-1].length()-5) +"_result.xlsx";
			}
		}
		else {
			args[0] = "1";	//mode selection for JournalCombine: 1-- Batch files combine
			args[1] =  annual+"/";
			args[2] = origTotal;
			if(annual != null && origTotal != null && outFolder == null) {
				outFolder = annual;
				OutputFiles.setText(outFolder);
				args[3] = outFolder+"/";
			}
			else {
				args[3] = outFolder+"/";
			}
		}
		
		try {
			jTest = new JournalCombine(args);
		} catch (Exception err) {
			JOptionPane.showMessageDialog(app,"无法打开文件！", "Error", JOptionPane.ERROR_MESSAGE);
			err.printStackTrace();
			return;
		}
		//以启动一条线程的方式来执行一个耗时的任务  
		new Thread(jTest).start();
		timer0.stop();
		final Timer timer1 = new Timer(180000, new ActionListener(){
			public void actionPerformed(ActionEvent e)  
        	{
				String file = jTest.getCurrentProg();
				if(!jTest.getCurrentStat()){//jTest.current() == 0 || jTest.current()== 100) {
					JOptionPane.showMessageDialog(app,"无法合并文件！", "Error", JOptionPane.ERROR_MESSAGE);
					lbLabel_result.setText(file + "合并失败");
					if(rdBtn_Batch.isSelected()) {
						progressBar.setValue(0);
					}
					btnCombine.setEnabled(true);
				}
			}
		});
		final Timer timer2 = new Timer(1000, new ActionListener(){
			public void actionPerformed(ActionEvent e)  
        	{
			String file = jTest.getCurrentProg();
			if(jTest.getCurrentStat()){
				lbLabel_result.setText(file +"合并成功！");
				if(rdBtn_Batch.isSelected()) {
		                //以任务的当前完成量设置进度条的value
		            	progressBar.setValue(jTest.current());
		        }
				timer1.stop();
			}
			else {
				timer1.start();
			}
			
        }
			
        });  
        timer2.start();
        final Timer timer3 = new Timer(1000, new ActionListener() {
        	public void actionPerformed(ActionEvent e)  
        	{
        		if(jTest.current()==100 && jTest.getCurrentStat()) {
        			lbLabel_result.setText("合并成功！");
        			if(rdBtn_Batch.isSelected())
        				progressBar.setValue(100);
        			btnCombine.setEnabled(true);
        			timer1.stop();
        			timer2.stop();
        		}
        	}
        	
        });
        timer3.start();
	}
});

btnExit.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
		System.exit(0);
	}
});
app.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}
});
app.setSize(400,320);
Dimension screensize=Toolkit.getDefaultToolkit().getScreenSize();
int pos_x = (int)(screensize.getHeight()/2)- 200;
int pos_y = (int)(screensize.getWidth() /2)- 160;
//app.setLocation(pos_y, pos_x);
app.setVisible(true);
}
private static void initilization(){
app = new JFrame("期刊列表合并演示");
	
	JLabel lblNewLabel = new JLabel("年度期刊列表文件夹");
	
	annualExcel = new JTextField();
	annualExcel.setColumns(14);
	
	btnOpen_1 = new JButton("...");
	
	JLabel lblNewLabel_1 = new JLabel("总期刊列表");
	
	OrigExcel = new JTextField();
	OrigExcel.setColumns(14);

	btnOpen_2 = new JButton("...");
	
	JLabel lblNewLabel_2 = new JLabel("输出期刊列表位置");
	
	OutputFiles = new JTextField();
	OutputFiles.setColumns(14);
	
	btnOpen_3 = new JButton("...");
	
	btnCombine = new JButton("合并");
	
	btnExit = new JButton("退出");
	
	toolBar = new JToolBar();
	toolBar.setFloatable(false);
	lbLabel_result = new JLabel("                         ");
	toolBar.add(lbLabel_result);
	
	JPanel panel = new JPanel();
	
	GroupLayout groupLayout = new GroupLayout(app.getContentPane());
	groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(toolBar, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(30)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(2)
							.addComponent(lblNewLabel)
							.addGap(10)
							.addComponent(annualExcel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(5)
							.addComponent(btnOpen_1))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(2)
							.addComponent(lblNewLabel_1)
							.addGap(62)
							.addComponent(OrigExcel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(5)
							.addComponent(btnOpen_2))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNewLabel_2)
							.addGap(5)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(btnCombine)
									.addGap(5)
									.addComponent(btnExit))
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(21)
									.addComponent(OutputFiles, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGap(5)
									.addComponent(btnOpen_3)))))
					.addContainerGap(31, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(66)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 261, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(85, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(45, Short.MAX_VALUE)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(5)
							.addComponent(lblNewLabel))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(3)
							.addComponent(annualExcel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnOpen_1))
					.addGap(5)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(5)
							.addComponent(lblNewLabel_1))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(3)
							.addComponent(OrigExcel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnOpen_2))
					.addGap(5)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(5)
							.addComponent(lblNewLabel_2))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(3)
							.addComponent(OutputFiles, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnOpen_3))
					.addGap(27)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnCombine)
						.addComponent(btnExit))
					.addGap(65)
					.addComponent(toolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
	
	rdBtn_One = new JRadioButton("单文件合并");
	panel.add(rdBtn_One);
	
	rdBtn_Batch = new JRadioButton("批量文件合并");
	panel.add(rdBtn_Batch);
	app.getContentPane().setLayout(groupLayout);
	ButtonGroup group = new ButtonGroup();
	group.add(rdBtn_Batch);
	group.add(rdBtn_One);
}
// Main function
public static void main(String args[]) throws Exception {
	JournalGUI window =new JournalGUI();
	window.getClass();
}
}
