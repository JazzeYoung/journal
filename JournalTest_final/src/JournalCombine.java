/**
 * 
 */

/**
 * @author Jazze
 * Library test file: .XLS .XLSXfiles
 * Task: take the corresponding field of the files, create a joint xls file
 * Field: Journal Title to JTitle
 * 
 */
import java.util.List;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;
import javax.swing.Timer;

public class JournalCombine implements Runnable {
	
	private String output;
	private String annual;
	private String total;
	private CExcel annExcel;
	private CExcel totExcel;
	private String[] files;
	private int index = 0;
	private int FileNum;
    private boolean[] isSuccess;

	private static boolean Compare(String s1, String s2) {
		// 比较函数（确认期刊列表中的期刊名称与总期刊列表相同）
		if(s1 == null || s2 == null) return false;
		if(s1.length() != s2.length()) return false;
		if(s1.toLowerCase().compareTo(s2.toLowerCase())==0) return true;
    	return false;
    }
	
	
	JournalCombine() { }
	
    JournalCombine(String[] args)throws Exception{
     /**
	 * @param args
	 * @param(in) files(year-SCIE/SSCI-Journalxxx.xls)
	 * @param(in) file(original library copy file: .xls)
	 * @JournalCombine构造函数
	 */
    	if(args.length < 3) {
        	throw(new IOException());
        }
    	this.total = args[2];
    	this.annual = args[1];
    	switch(args[0].compareTo("0")) {
    	case 0:
    		this.files = new String[1];
    		String[] tmp = this.annual.split("/");
    		this.files[0] = tmp[tmp.length-1];
    		this.FileNum = 1;
    		this.annual = this.annual.substring(0, this.annual.indexOf(files[0]));
    		if( args.length == 3){
            	//JournalCombine
    			this.output = args[1].substring(0, args[1].length()-5) + "_result.xlsx";
            } else {
            	this.output = args[3];
            	this.output += this.files[0].substring(0, this.files[0].length()-5) + "_result.xlsx";
            }
    		
    		break;
    	case 1:
    		File tmplist = new File(annual);
    		this.files = tmplist.list();
    		this.FileNum = this.files.length;
    		if(args.length == 3) {
    			this.output = this.annual;
    		} else {
    			this.output = args[3];
    		}
    		break;
    	default:
    		break;	
    	}
    	try {
    		this.totExcel = new CExcel(this.total);
    	}catch(Exception e) {
    		System.out.println("Error in READING excel workbook: ");
			throw(new IOException(e));
    	}
        this.isSuccess = new boolean[this.FileNum];
    }
    
    private boolean combine() throws Exception {
	 /* @function: 建立JournalCombine类，实现两个列表的合并
	 */
        List<String[]> getAll1 = this.annExcel.getAllBookData();
        this.totExcel.getAllBookData();
        
        int i = 0;
        for(int j = 0; j < getAll1.size(); j++) {
        	String[] getT = getAll1.get(j);
        	i = 0;
        	while(i< getT.length && getT[i].compareTo("Full Journal Title") != 0) i++;
        	if(i < getT.length) break; 
        }
        
        //int num = getAll2.size();
        
    	//获取该列数据并建立比对表
    	String[] getColumnI = this.annExcel.getBookColumnData(i);

    	String[] getColumn0 = this.totExcel.getBookColumnData(0);
    	
    	ArrayList<ArrayList<Integer>> match = new ArrayList<ArrayList<Integer>>();
    	
    	for(i = 2; i < getColumnI.length; i++) {
    		ArrayList<Integer>  corr = new ArrayList<Integer>();
    		for(int j = 1; j < getColumn0.length; j++) {
    			if(Compare(getColumnI[i], getColumn0[j])){
    				corr.add(j);
    				getColumn0[j]="";
    			}
    		}
    		match.add(corr);
    		
    	}
    	getColumn0 = this.totExcel.getBookColumnData(1);
    	
    	String[] str = getAll1.get(1);
		String[] str2 = new String[7];
		
		//设置表格表头
		str2[0] = str[0];
		str2[1] = "ISSN";
		str2[2] = str[1];
		str2[3] = str[2];
		str2[4] = str[4];
		str2[5] = str[5];
		str2[6] = "备注";
    	List<String[]> DataResult=new ArrayList<String[]>();
    	
    	DataResult.add(str2);
    	int len = annExcel.getBookRowNum();
    	
    	//将数据查询得到的ISSN全部存入annExcel_result中
    	for(i = 2; i  <  len - 1; i++) { //最后一行需要舍弃
    		str2 = new String[7];
    		str = getAll1.get(i);
    		int[] list = {1, 2, 4, 5};
    		str2[0] = str[0];
    		for(int j = 0; j <= 3; j++) {
    			str2[j+2] = str[list[j]];
    		}
    		//String[] matchStr = match.get(i).split(",");
    		int numISSN = match.get(i-2).size();
    		if(numISSN==0) 
    			str2[1]  =  "";
    		else
    			str2[1] = getColumn0[match.get(i-2).get(0)];
    		
    		for(int j = 1; j < numISSN; j ++) {
    			str2[1] = str2[1] + ";" + getColumn0[match.get(i-2).get(j)];
    		}
    		str2[6] = (numISSN>1)? "多个ISSN："+ String.valueOf(numISSN) : (numISSN==0 ?"没有对应ISSN":"");
    		if(str2[1].length()> 32760) 
    			System.out.println(i);
    		DataResult.add(str2);
    	}
    	
    	if(!DataResult.isEmpty()) {
    		String outpath = this.output + this.files[this.index].substring(0, this.files[this.index].length()-5) + "_result.xlsx";
    		try {
    			this.annExcel.createNewExcelFile(DataResult, outpath);
    		}
    		catch(Exception e) {
    			throw(new IOException(e));
    		}
    		return true;
    	}
    	return false;
    }


    public boolean getCurrentStat() {
    	if(this.isSuccess[this.i_old]) return true;
    	return false;
    }
    
    public int current() {
    	return (int)((this.index * 100.)/this.FileNum);
    }
    
    public String getCurrentProg() {
    	return this.files[this.i_old];
    }
    
    private int i_old;
	@Override
	public void run(){
		//run();
		while(this.index < this.FileNum) {
			
			if(this.files[this.index].toLowerCase().endsWith(".xlsx")) {
				try {
					this.annExcel = new CExcel(this.annual + this.files[index]);
		    	}
		    	catch(Exception e) {
		    		System.out.println("Error in READING excel workbook: ");
		    		this.index = this.FileNum;
		    		return;
		    	}
					
				this.isSuccess[this.index] = false;
				try {
					this.combine();
				} catch (Exception e) {
					System.out.println("Combination error!");
					e.printStackTrace();
					this.index = this.FileNum;
					return;
				}
				this.isSuccess[this.index] = true;
				i_old = this.index;
				++ this.index;
			
			}
		}
	}
    
    

}
