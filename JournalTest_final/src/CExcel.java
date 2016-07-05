import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class CExcel {
	    
	private String excelPath = ".";

	private Workbook wb = null;
	private List<String[]> dataList = new ArrayList<String[]>();

	CExcel(String excel) throws Exception {
		/**
		 * @param(in): excel file name
		 * @CExcel构造函数，初始化
		 * @return  CExcel类
		 */
		excelPath = excel;
	
		boolean isE2007 = false;    //判断是否是excel2007格式  
        if(excelPath.endsWith("xlsx"))  
            isE2007 = true;  
        try {
            FileInputStream input = new FileInputStream(excelPath);  //建立输入流  
            //根据文件格式(2003或者2007)来初始化  
            if(isE2007)  
                wb = new XSSFWorkbook(input);  
            else
                wb = new HSSFWorkbook(input);
        } catch (IOException e) {  
        	e.printStackTrace(); 
        }
	}

	public List<String[]> getAllBookData() {
		dataList.clear();
		for(int i = 0; i < wb.getNumberOfSheets(); i++)
			getAllData(i);
		
		return dataList;
	}
	
	private List<String[]> getAllData(int sheetIndex){
		/**
		 * @param(in) sheetIndex(sheet index in excel)
		 * @  取Excel第sheetIndex个sheet的所有数据，包含header
		 * @return  List<String[]>
		 */
		int columnNum = 0;
		Sheet sheet = wb.getSheetAt(sheetIndex);		//获取Sheet类
		if(sheet.getRow(0)!=null){
			columnNum = sheet.getRow(0).getLastCellNum()-sheet.getRow(0).getFirstCellNum();
		}
		if(columnNum>0){
			for(Row row:sheet){ 
				String[] singleRow = new String[columnNum];
				int n = 0;
				for(int i=0;i<columnNum;i++){
					Cell cell = row.getCell(i, Row.CREATE_NULL_AS_BLANK);
					switch(cell.getCellType()){
					case Cell.CELL_TYPE_BLANK:					//单元格为空
						singleRow[n] = "";
						break;
					case Cell.CELL_TYPE_BOOLEAN:				//读取Boolean类型值
						singleRow[n] = Boolean.toString(cell.getBooleanCellValue());
						break;
						
					case Cell.CELL_TYPE_NUMERIC:               //读取数值
						if(DateUtil.isCellDateFormatted(cell)){
							singleRow[n] = String.valueOf(cell.getDateCellValue());
						}else{ 
							cell.setCellType(Cell.CELL_TYPE_STRING);
							String temp = cell.getStringCellValue();
							//判断是否包含小数点，如果不含小数点，则以字符串读取，如果含小数点，则转换为Double类型的字符串
							//if(temp.indexOf(".")>-1){
								//singleRow[n] = String.valueOf(new Double(temp)).trim();
							//}else{
								//singleRow[n] = temp.trim();
							//}
							singleRow[n] = temp;
						}
						break;
					case Cell.CELL_TYPE_STRING:					//读取字符串
						singleRow[n] = cell.getStringCellValue().trim();
						break;
					case Cell.CELL_TYPE_ERROR:					//读取错误，输出为空
						singleRow[n] = "";
						break;  
					case Cell.CELL_TYPE_FORMULA:				//读取公式类型
						cell.setCellType(Cell.CELL_TYPE_STRING);
						singleRow[n] = cell.getStringCellValue();
						if(singleRow[n]!=null){
							singleRow[n] = singleRow[n].replaceAll("#N/A","").trim();
						}
						break;  
					default:
						singleRow[n] = "";
						break;
					}
					n++;
				} 
				if("".equals(singleRow[0])){continue;}		//如果该行为空，跳过
				dataList.add(singleRow);
			}
		}
		return dataList;
	}
	
	public int getBookRowNum() {
		return dataList.size();
	}
	
	private int getRowNum(int sheetIndex){
		/**
	 * @param(in) sheetIndex：Excel的Sheet指标
	 * @返回Excel的第sheetIndex个Sheet的最大行index值，实际行数要加1
	 * @return
	 */
		Sheet sheet = wb.getSheetAt(sheetIndex);
		return sheet.getLastRowNum();
	}
	
	public int getBookColumnNum() {
		return dataList.get(0).length;
	}
	
	private int getColumnNum(int sheetIndex){
		/**
		 *@param(in) sheetIndex：Excel的Sheet指标
		 * @返回的第sheetIndex个Sheet的列数
		 * @return 
		 */
		Sheet sheet = wb.getSheetAt(sheetIndex);
		Row row = sheet.getRow(0);
		if(row!=null&&row.getLastCellNum()>0){
			return row.getLastCellNum();
		}
		return 0;
	}
	
	public String[] getBookRowData(int rowIndex) {
		if(rowIndex < getBookRowNum())
			return this.dataList.get(rowIndex);
		else
			return null;
	}

	private String[] getRowData(int sheetIndex,int rowIndex){
		/**
		 * @param rowIndex 计数从0开始，rowIndex为0代表header行
		 * @获取sheetIndex表的第rowIndex行数据
		 *  @return
		 */
		String[] dataArray = null;
		if(rowIndex>this.getColumnNum(sheetIndex)){
			return dataArray;
		}else{
			dataArray = new String[this.getColumnNum(sheetIndex)];
			return this.dataList.get(rowIndex);
		}

	}
	

	public String[] getBookColumnData(int colIndex) {
		//getAllBookData();
		String[] dataArray = null;
		if(getBookColumnNum() > colIndex)
			dataArray = new String[getBookRowNum()];
		else
			return dataArray;
		int idx = 0;
		for(int i = 0; i < wb.getNumberOfSheets(); i++ ) {
			String[] tmp = getColumnData(i, colIndex);
			for(String a:tmp) {
				dataArray[idx++] = a;
			}
		}
		return dataArray;
	}

	private String[] getColumnData(int sheetIndex,int colIndex){
		/**
		 * @param(in): sheetIndex,colIndex：表，列数
		 * @获取sheetIndex表的第colIndex列数据
		 * @return
		 */
		String[] dataArray = null;
		if(colIndex>this.getColumnNum(sheetIndex)){ 
			return dataArray;
		}else{   
			if(this.dataList!=null&&this.dataList.size()>0){
				dataArray = new String[this.getRowNum(sheetIndex)];
				int index = 0;
				for(String[] rowData:dataList){
					if(rowData!=null && index < dataArray.length){
						dataArray[index] = rowData[colIndex];
						index++;
					}
				}
			}
		}
		return dataArray;

	}
	
	public boolean createNewExcelFile(List<String[]> result, String path) throws Exception {
		/***
		 * @param(in): result, path：输出数据，存储路径
		 * @将输出结果存储到结果excel表中
		 * @return:存储成功/失败
		 */
		boolean isCreateSuccess = false;
		Workbook workbook = null;
		try {
			workbook = new XSSFWorkbook();//HSSFWorkbook();//WorkbookFactory.create(inputStream);
			// XSSFWork used for .xlsx (>= 2007), HSSWorkbook for 03 .xls
		}catch(Exception e) {
			System.out.println("It cause Error on CREATING excel workbook: ");
			e.printStackTrace();
		}
		if(workbook != null) {
			Sheet sheet = workbook.createSheet("resultdata");
			int rows = result.size();
			int cols = result.get(0).length;
			Row row0 = sheet.createRow(0);
			for(int i = 0; i < cols; i++) {
				Cell cell = row0.createCell(i, Cell.CELL_TYPE_STRING);
				CellStyle style = getStyle(workbook);
				cell.setCellStyle(style);
				cell.setCellValue(result.get(0)[i]);
				sheet.autoSizeColumn(i);
			}
			for (int rowNum = 1; rowNum < rows; rowNum++) {
				Row row = sheet.createRow(rowNum);
				for(int i = 0; i < cols; i++) {
					Cell cell = row.createCell(i, Cell.CELL_TYPE_STRING);
					cell.setCellValue(result.get(rowNum)[i]);
				}
			}
			try {
				FileOutputStream outputStream = new FileOutputStream(path);
				workbook.write(outputStream);
				outputStream.flush();
				outputStream.close();
				isCreateSuccess = true;
			} catch (Exception e) {
				System.out.println("Error in WRITTING excel workbook: ");
				e.printStackTrace();
			}
		}
		//File savNew = new File(path);
		//System.out.println(sss.getAbsolutePath());
		return isCreateSuccess;
	}
	private CellStyle getStyle(Workbook workbook){
		CellStyle style = workbook.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER); 
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		// 设置单元格字体
		Font headerFont = workbook.createFont(); // 字体
		headerFont.setFontHeightInPoints((short)16);
		headerFont.setColor(HSSFColor.BLACK.index);
		headerFont.setFontName("宋体");
		style.setFont(headerFont);
		style.setWrapText(true);

		// 设置单元格边框及颜色
		style.setBorderBottom((short)1);
		style.setBorderLeft((short)1);
		style.setBorderRight((short)1);
		style.setBorderTop((short)1);
		style.setWrapText(true);
		return style;
	}
}
