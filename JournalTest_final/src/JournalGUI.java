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
private static String origTotal;	//总期刊列表路径
private static String annual;		//年度期刊列表路径
private static String outFolder;	//输出文件夹路径

private static JTextField annualExcel;	//输入年度期刊列表文件/文件夹控件
private static JTextField OrigExcel;	//输入总期刊列表文件控件
private static JTextField OutputFiles;	//获取输出目录的控件
private static  JFrame app;		//生成的程序控制面板

private static JButton btnOpen_1;	//打开年度期刊列表选择对话框
private static JButton btnOpen_2 ;	//打开总期刊列表选择对话框
private static JButton btnOpen_3 ;	//打开输出目录选择对话框

private static JButton btnCombine;	//进行合并按钮控件
private static JButton btnExit;		//退出程序按钮控件

private static JToolBar toolBar;	//输出工具条控件
private static JLabel lbLabel_result;	//输出结果标签
private static JRadioButton rdBtn_One;	//控制是否是单文件合并
private static JRadioButton rdBtn_Batch;//控制是否是批文件处理

private JournalGUI() {
	/**
	 * @function: JournalGUI构造函数
	 * @function: 初始化并创建控件响应，调用合并程序并输出结果
	 */
	initilization();	//调用初始化，创建窗口控件
	
btnOpen_1.addActionListener(new ActionListener(){
    //添加打开年度列表文件/文件夹程序
    public void actionPerformed(ActionEvent e) {
    	if(rdBtn_Batch.isSelected()) {		//批文件处理模式下，选择年度期刊列表文件夹
        	JFileChooser chooser = new JFileChooser();
	        chooser.setCurrentDirectory(new File("."));
        	chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	        int result =chooser.showDialog(chooser,"选择目标文件夹");	
	        if(result==JFileChooser.APPROVE_OPTION){
	            annual=chooser.getSelectedFile().getPath();
	            annualExcel.setText(annual);
	        }
        }
        else if(rdBtn_One.isSelected()) {	//单文件处理模式下，选择单个年度期刊列表文件
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
        else	//尚未选择处理方式，提示需要先进行模式选择
        	JOptionPane.showMessageDialog(app,"请选择合并文件形式！\n批处理/单文件合并", "Error", JOptionPane.ERROR_MESSAGE);
    }
});
btnOpen_2.addActionListener(new ActionListener(){
	///打开总期刊列表文件
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
	///打开期刊列表合并结果储存位置
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
	///完成合并参数的传递与处理结果的显示
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
	        {  ///保证程序开始的程序显示
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
			//设置进度条的最大值和最小值,  
		        progressBar.setMinimum(0);   
		        //以总任务量作为进度条的最大值  
		        progressBar.setMaximum(100);  
		}
		timer0.start();	//显示运行过程
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
		new Thread(jTest).start();	//开始执行jTest.run()程序并以新的线程不间断的运行下去
		timer0.stop();	//停止“开始处理”显示
		final Timer timer1 = new Timer(180000, new ActionListener(){
			///进行目标是否为空的显示，增加时间延迟防止出现误判
			public void actionPerformed(ActionEvent e) {
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
				timer1.stop();//合并成功，则停止判断是否失败
			}
			else {
				timer1.start();//延迟判断合并是否失败
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
app.setSize(400,320);	//指定窗口大小
Dimension screensize=Toolkit.getDefaultToolkit().getScreenSize();//获取屏幕参数
int pos_x = (int)(screensize.getHeight()/2)- 200;		//设置横向位置
int pos_y = (int)(screensize.getWidth() /2)- 160;		//设置纵向位置
app.setLocation(pos_y, pos_x);	//保证窗口起始位置为屏幕中央
app.setVisible(true);
}
private static void initilization(){
	/**
	 * @function: 窗口初始化，创建窗口控件
	 * @function: 构建一些确定性控件并确定其位置构造
	 */
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
	lbLabel_result = new JLabel("");
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
	///主程序入口
	///调用窗口JournalGUI类，生成合并窗口实例
	JournalGUI window =new JournalGUI();
	window.getClass();	//对主程序运行无影响，避免程序报错
}
}
