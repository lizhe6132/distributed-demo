package com.ronglian.bms.commons.file;

import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
/**
 * 文件处理工具类
 * 
 * @author Administrator
 */
public class FileUtils {

	private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

	/**
	 * 把字符串写入文件
	 * 
	 * @param content
	 */
	public static boolean writeFile(String content, String filePath, String fileName) {

		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			File dic = new File(filePath);
			if (!dic.exists()) {
				if (!dic.mkdir()) {
					logger.warn("创建目录[" + filePath + "]失败！");
					return false;
				}
			}
			File file = new File(filePath + fileName);
			if (!file.exists()) {
				if (!file.createNewFile()) {
					logger.warn("创建文件[" + filePath + fileName + "]失败！");
					return false;
				}
			}
			String[] infos = StringUtils.split(content.toString(), "SPLIT");
			if (null != infos && infos.length > 0) {
				fw = new FileWriter(file);
				bw = new BufferedWriter(fw);
				for (String temp : infos) {
					if (StringUtils.isNotEmpty(temp)) {
						bw.write(StringUtils.trim(temp));
						bw.newLine();
						if (logger.isDebugEnabled()) {
							logger.debug("文件数据：" + temp);
						}
					}
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("写入文件错误...");
				logger.error(e.toString());
				return false;
			}
		} finally {
			try {
				if (null != bw) {
					bw.close();
				}
				if (null != fw) {
					fw.close();
				}
			} catch (IOException e) {
				if (logger.isErrorEnabled()) {
					logger.error(e.toString());
				}
			}
		}
		return true;
	}

	/**
	 * 创建文件夹
	 * 
	 * @param path
	 * @return boolean
	 */
	public static boolean createDir(String path) {
		boolean creadok = true;
		File dirFile = new File(path);
		if (!(dirFile.exists()) && !(dirFile.isDirectory())) {
			creadok = dirFile.mkdirs();
			if (!creadok) {
				if (logger.isInfoEnabled()) {
					logger.info("dir create failure!");
				}
			} else {
				creadok = true;
			}
		}
		return creadok;
	}

	/**
	 * 创建文件夹
	 * 
	 * @param path
	 * @return boolean
	 */
	public static boolean createFile(String path) {
		boolean creadok = true;
		try {
			File dirFile = new File(path);
			if (!dirFile.exists()) {
				creadok = dirFile.createNewFile();
				if (!creadok) {
					if (logger.isInfoEnabled()) {
						logger.info("file create failure!");
					}
				} else {
					creadok = true;
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("创建文件错误...");
				e.printStackTrace();
				return false;
			}
		}
		return creadok;
	}

	/**
	 * 获取文件类型
	 * 
	 * @param filename
	 * @return
	 */
	public static String getFileType(String filename) {
		String suffixFileName = "";
		int beginIndex = filename.lastIndexOf(".") + 1;// 取得文件名中最后.的下标
		suffixFileName = filename.substring(beginIndex);// 截取子字符串
		return suffixFileName;
	}

	/**
	 * 保存文件
	 * 
	 * @param filename
	 * @param filedir
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static boolean saveFile(String filename, String filedir, File file) throws Exception {
		// 循环保存文件
		if (FileUtils.createDir(filedir)) {
			InputStream is = new FileInputStream(file);
			File deskFile = new File(filedir, filename);
			OutputStream os = new FileOutputStream(deskFile);
			byte[] bytefer = new byte[400];
			int length = 0;
			while ((length = is.read(bytefer)) > 0) {
				os.write(bytefer, 0, length);
			}
			os.close();
			is.close();
		}
		return true;
	}

	/**
	 * 删除文件
	 * 
	 * @param fileFullName
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean delFile(String fileFullName) throws Exception {
		File file = new File(fileFullName);
		if (file.exists()) {
			try {
				file.delete();
			} catch (Exception e) {
				throw new Exception("删除文件失败！" + e.getMessage());
			}
		}
		return true;
	}

	/**
	 * 删除目录及子目录
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(File dir) throws Exception {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param fileFullName
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean isFileExist(String fileFullName) throws Exception {
		boolean result = true;
		File file = new File(fileFullName);
		if (!file.exists()) {
			result = false;
		}
		return result;
	}

	/**
	 * 根据文件名获取文件的MIMI类型
	 * 
	 * @param filename
	 * @return String
	 */
	public static String getFileMime(String filename) {
		String filetype = getFileType(filename).toLowerCase();// 文件后最转换为小写
		String mime = "";
		if ("doc".equals(filetype)) {
			mime = "application/msword";
		}
		if ("excel".equals(filetype)) {
			mime = "application/msexcel";
		} else if ("pdf".equals(filetype)) {
			mime = "application/pdf";
		} else if ("ppt".equals(filetype)) {
			mime = "appication/powerpoint";
		} else if ("rtf".equals(filetype)) {
			mime = "appication/rtf";
		} else if ("z".equals(filetype)) {
			mime = "appication/x-compress";
		} else if ("gz".equals(filetype)) {
			mime = "application/x-gzip";
		} else if ("gtar".equals(filetype)) {
			mime = "application/x-gtar";
		} else if ("swf".equals(filetype)) {
			mime = "application/x-shockwave-flash";
		} else if ("tar".equals(filetype)) {
			mime = "application/x-tar";
		} else if ("zip".equals(filetype)) {
			mime = "application/zip";
		} else if ("rar".equals(filetype)) {
			mime = "application/x-rar-compressed";
		} else if ("mpeg".equals(filetype) || "mp2".equals(filetype)) {
			mime = "audio/mpeg";
		} else if ("mid".equals(filetype) || "midi".equals(filetype) || "rmf".equals(filetype)) {
			mime = "audio/x-aiff";
		} else if ("rpm".equals(filetype)) {
			mime = "audio/x-pn-realaudio-plugin";
		} else if ("wav".equals(filetype)) {
			mime = "audio/x-wav";
		} else if ("gif".equals(filetype)) {
			mime = "image/gif";
		} else if ("jpeg".equals(filetype) || "jpg".equals(filetype) || "jpe".equals(filetype)) {
			mime = "image/jpeg";
		} else if ("png".equals(filetype)) {
			mime = "image/png";
		} else if ("txt".equals(filetype)) {
			mime = "text/plain";
		} else if ("xml".equals(filetype)) {
			mime = "text/xml";
		} else if ("json".equals(filetype)) {
			mime = "text/json";
		} else if ("exe".equals(filetype)) {
			mime = "application/octet-stream";
		}
		return mime;
	}

	/**
	 * 文件下载
	 * 
	 * @param fileFullPath
	 * @param filename
	 * @param response
	 * @throws Exception
	 */
	public static void filedownload(String fileFullPath, String filename, HttpServletResponse response)
			throws FileNotFoundException, UnsupportedEncodingException, IOException {
		File filedown = new File(fileFullPath);
		if (filedown.exists()) {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filedown));
			byte[] buffer = new byte[1024];
			String strFileName = filedown.getName();
			strFileName = java.net.URLEncoder.encode(strFileName, "UTF-8");// 处理中文文件名的问题
			strFileName = new String(strFileName.getBytes("UTF-8"), "GBK");// 处理中文文件名的问题
			response.reset();
			response.setCharacterEncoding("UTF-8");
			response.setContentType(FileUtils.getFileMime(filename));
			response.setHeader("Content-Disposition", "attachment; filename=" + strFileName);
			OutputStream os = response.getOutputStream();
			while (bis.read(buffer) > 0) {
				os.write(buffer);
			}
			bis.close();
			os.close();
		}
	}

	/**
	 * 输出文件流
	 * 
	 * @param fileFullPath
	 * @param filename
	 * @param response
	 * @throws Exception
	 */
	public static void outputFile(String fileFullPath, HttpServletResponse response) throws FileNotFoundException,
			UnsupportedEncodingException, IOException {

		File filedown = new File(fileFullPath);
		BufferedInputStream bis = null;
		OutputStream os = null;
		try {
			if (filedown.exists()) {
				bis = new BufferedInputStream(new FileInputStream(filedown));
				os = response.getOutputStream();
				byte[] buffer = new byte[1024];
				while (bis.read(buffer) > 0) {
					os.write(buffer);
				}
				bis.close();
				os.close();
			}
		} catch (Exception e) {
			logger.error("输出文件流错误");
			e.printStackTrace();
		} finally {
			if (null != bis) {
				bis.close();
			}
			if (null != os) {
				os.close();
			}
		}
	}

	/**
     * 默认解析excel文件方法:解析除表头外所有表内容，表内容从第一行开始
     * @param file
     * @return List<List<Object>>集合
     */
    public static List<List<Object>> defaultProcessExcel(File file) {
        Assert.notNull(file, "文件不能为空");
        //判断file是否为excel文件
        String fileName = file.getName();
        if (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx")) {
            throw new IllegalArgumentException("解析excel失败，不是标准的excel文件");
        }
        InputStream input = null;
        HSSFWorkbook wb = null; 
        try {
            input = new FileInputStream(file);
            wb = new HSSFWorkbook(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (wb != null) {
            //获取工作表
            HSSFSheet sheet = wb.getSheetAt(0);
            return processExcel(sheet, 1, 0, sheet.getRow(0).getPhysicalNumberOfCells());
        }
        return null;
    }
    /**
     * 解析指定excel文件，并指定从第几行开始，从第几列开始，至第几列结束
     * @param file 要解析的文件
     * @param rowBegin 开始行
     * @param colBegin 开始列
     * @param colEnd 结束列
     * @return
     */
    public static List<List<Object>> processExcelByRowAndCol(File file, int rowBegin, int colBegin, int colEnd) {
        Assert.notNull(file, "文件不能为空");
        //判断file是否为excel文件
        String fileName = file.getName();
        if (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx")) {
            throw new IllegalArgumentException("解析excel失败，不是标准的excel文件");
        }
        InputStream input = null;
        HSSFWorkbook wb = null; 
        try {
            input = new FileInputStream(file);
            wb = new HSSFWorkbook(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (wb != null) {
            //获取工作表
            HSSFSheet sheet = wb.getSheetAt(0);
            return processExcel(sheet, rowBegin, colBegin, colEnd);
        }
        return null;
    }
    /**
     * 解析指定excel的xlsx文件，并指定从第几行开始，从第几列开始，至第几列结束
     * @param file 要解析的文件
     * @param rowBegin 开始行
     * @param colBegin 开始列
     * @param colEnd 结束列
     * @return
     */
    public static List<List<Object>> processExcelXlsxByRowAndColByInputStream(InputStream input, int rowBegin, int colBegin, int colEnd) {
        Assert.notNull(input, "文件流不能为空");
        XSSFWorkbook wb = null; 
        try {
            wb = new XSSFWorkbook(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (wb != null) {
            //获取工作表
        	XSSFSheet sheet = wb.getSheetAt(0);
            return processExcel(sheet, rowBegin, colBegin, colEnd);
        }
        return null;
    }
    
    /**
     * 解析指定excel的xls文件，并指定从第几行开始，从第几列开始，至第几列结束
     * @param file 要解析的文件
     * @param rowBegin 开始行
     * @param colBegin 开始列
     * @param colEnd 结束列
     * @return
     */
    public static List<List<Object>> processExcelXlsByRowAndColByInputStream(InputStream input, int rowBegin, int colBegin, int colEnd) {
        Assert.notNull(input, "文件流不能为空");
        HSSFWorkbook wb = null; 
        try {
            wb = new HSSFWorkbook(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (wb != null) {
            //获取工作表
        	HSSFSheet sheet = wb.getSheetAt(0);
            return processExcel(sheet, rowBegin, colBegin, colEnd);
        }
        return null;
    }
    
    /**
     * 解析excel的xls文件
     * @param sheet  HSSFSheet对象
     * @param rowBegin 读取文件开始行
     * @param colBegin 读取文件开始列
     * @param colEnd 读取文件结束列(指定结束列若大于总列数，则为最后一列)
     * @return List<List<Object>>集合
     */
    public static List<List<Object>> processExcel(HSSFSheet sheet, int rowBegin, int colBegin, int colEnd) {
        Assert.notNull(sheet, "表不能为空");
        List<List<Object>> list = null;
        list = new ArrayList<List<Object>>();
        int columns = sheet.getRow(rowBegin).getPhysicalNumberOfCells();
        //指定列大于表总列数，重新给解析列数赋值
        if (colEnd > columns) {
            colEnd = columns;
        }
        DecimalFormat df = new DecimalFormat("#.##");
        //循环读取行,列
        for (int rowCount = rowBegin; rowCount <= sheet.getLastRowNum(); rowCount++) {
            Row row = sheet.getRow(rowCount);
            //跳过空行(第一列为null（不存在）认为改行是空行)
            if (row.getCell(0) == null) {
                continue;
            }
            List<Object> childList = new ArrayList<Object>(colEnd);
            for (int cellCount = colBegin; cellCount < colEnd; cellCount++) {
                Cell cell = row.getCell(cellCount);
                //跳过空单元格
                if (cell == null) {
                    childList.add("");
                    continue;
                }
                switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    childList.add(cell.getStringCellValue() != null ? cell.getStringCellValue().trim() : "");
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    childList.add(df.format(cell.getNumericCellValue()));
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    //如果是公式获取公式的计算的值而不是公式
                    childList.add(df.format(cell.getNumericCellValue()));
                    //childList.add(cell.getCellFormula() != null ? evaluator.evaluateFormulaCell(cell) : "");
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    childList.add(cell.getBooleanCellValue());
                    break;
                default:
                    childList.add("");
                }
                cell = null;
            }
            list.add(childList);
            childList = null;
        }
        return list;
    }
    
    /**
     * 解析excel的xls文件
     * @param sheet  HSSFSheet对象
     * @param rowBegin 读取文件开始行
     * @param colBegin 读取文件开始列
     * @param colEnd 读取文件结束列(指定结束列若大于总列数，则为最后一列)
     * @return List<List<Object>>集合
     */
    public static List<List<Object>> processExcel(XSSFSheet sheet, int rowBegin, int colBegin, int colEnd) {
        Assert.notNull(sheet, "表不能为空");
        List<List<Object>> list = null;
        list = new ArrayList<List<Object>>();
        int columns = sheet.getRow(rowBegin).getPhysicalNumberOfCells();
        //指定列大于表总列数，重新给解析列数赋值
        if (colEnd > columns) {
            colEnd = columns;
        }
        DecimalFormat df = new DecimalFormat("#.##");
        //循环读取行,列
        for (int rowCount = rowBegin; rowCount <= sheet.getLastRowNum(); rowCount++) {
            Row row = sheet.getRow(rowCount);
            //跳过空行(第一列为null（不存在）认为改行是空行)
            if (row.getCell(0) == null) {
                continue;
            }
            List<Object> childList = new ArrayList<Object>(colEnd);
            for (int cellCount = colBegin; cellCount < colEnd; cellCount++) {
                Cell cell = row.getCell(cellCount);
                //跳过空单元格
                if (cell == null) {
                    childList.add("");
                    continue;
                }
                switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    childList.add(cell.getStringCellValue() != null ? cell.getStringCellValue().trim() : "");
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    childList.add(df.format(cell.getNumericCellValue()));
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    //如果是公式获取公式的计算的值而不是公式
                    childList.add(df.format(cell.getNumericCellValue()));
                    //childList.add(cell.getCellFormula() != null ? evaluator.evaluateFormulaCell(cell) : "");
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    childList.add(cell.getBooleanCellValue());
                    break;
                default:
                    childList.add("");
                }
                cell = null;
            }
            list.add(childList);
            childList = null;
        }
        return list;
    }
    
	public static List<String> getFilesList(String filePath) {
		List<String> filesList = new ArrayList<String>();
		File file = new File(filePath);
		File[] lf = file.listFiles();
		if (lf != null && lf.length > 0) {
			for (int i = 0; i < lf.length; i++) {
				if (lf[i].isFile()) {
					filesList.add(lf[i].getName());
				}
			}
		}

		return filesList;
	}

	public static List<String> getFilesList(String fullPath, String suffixStr) {
		List<String> filesList = new ArrayList<String>();
		File file = new File(fullPath);
		File[] filesArray = file.listFiles();
		if (filesArray != null && filesArray.length > 0) {
			for (int i = 0; i < filesArray.length; i++) {
				File tempFile = filesArray[i];
				if (tempFile.isFile() && StringUtils.indexOf(tempFile.getName(), suffixStr) > 0) {
					filesList.add(tempFile.getName());
				}
			}
		}
		return filesList;
	}

	//复制文件
	public static void copyFile(File sourceFile, File targetFile) throws IOException {
		// 新建文件输入流并对它进行缓冲
		FileInputStream input = new FileInputStream(sourceFile);
		BufferedInputStream inBuff = new BufferedInputStream(input);

		// 新建文件输出流并对它进行缓冲
		FileOutputStream output = new FileOutputStream(targetFile);
		BufferedOutputStream outBuff = new BufferedOutputStream(output);

		// 缓冲数组
		byte[] b = new byte[1024 * 5];
		int len;
		while ((len = inBuff.read(b)) != -1) {
			outBuff.write(b, 0, len);
		}
		// 刷新此缓冲的输出流
		outBuff.flush();

		// 关闭流
		inBuff.close();
		outBuff.close();
		output.close();
		input.close();
	}

	// 复制文件夹
	public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {
		// 新建目标目录
		(new File(targetDir)).mkdirs();
		// 获取源文件夹当前下的文件或目录
		File[] file = (new File(sourceDir)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// 源文件
				File sourceFile = file[i];
				// 目标文件
				File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
				copyFile(sourceFile, targetFile);
			}
			if (file[i].isDirectory()) {
				// 准备复制的源文件夹
				String dir1 = sourceDir + "/" + file[i].getName();
				// 准备复制的目标文件夹
				String dir2 = targetDir + "/" + file[i].getName();
				copyDirectiory(dir1, dir2);
			}
		}
	}

	/**
	 * 文件输出
	 * @param content 文件内容
	 * @param url 路径
	 * @param fileName 文件名称
	 * @param charset 字符集
     * @return
     */
	public static boolean outFile(String content, String url, String fileName, String charset) {
		boolean isPass = false;
		File file = new File(url+fileName);
		FileOutputStream outf = null;
		BufferedOutputStream bufferout = null;
		//创建附件存储位置
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdir();
		}
//		if(file.exists()){
//			file.delete();
//		}
		try {
			outf = new FileOutputStream(file);
			bufferout = new BufferedOutputStream(outf);
			bufferout.write(content.getBytes(charset));
			isPass = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bufferout.flush();
				bufferout.close();
				outf.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return isPass;
	}
	/**
     * 生成通用excel数据并下载，有边框，无背景色，默认字体
     * @param list          数据集合
     * @param columnMap     列名字段对应关系
     * @param fileName      下载文件名称
     * @param response      输出
     */
    public static void createAndDownExcelForNormal(List list, Map<String, String> columnMap, String fileName, HttpServletResponse response){
        OutputStream os = null;
        try{
            response.addHeader("Content-Disposition", "attachment;");
            response.setContentType("application/octet-stream");
            os = response.getOutputStream();
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet();
            ExportExcelUtil excelUtil = new ExportExcelUtil(workbook, sheet);
            String[] titleArr = columnMap.values().toArray(new String[columnMap.values().size()]);
            String[] attrArr = columnMap.keySet().toArray(new String[columnMap.values().size()]);
            excelUtil.createExcelRow(workbook, sheet, 0, 10, columnMap.values().size(), fileName);
            int rowNO = 1;
            excelUtil.createColumnHeader(sheet, rowNO, 300, titleArr);
            rowNO ++;
            String[][] columnData = new String[list.size()][titleArr.length];
            if(null != list && list.size() > 0){
                Object obj = null;
                for(int i = 0; i < list.size(); i ++){
                    obj = list.get(i);
                    for(int j = 0; j < attrArr.length; j ++){
                        columnData[i][j] = getObjectFieldValue(attrArr[j], obj);
                    }
                }
            }
            sheet = excelUtil.createColumnData(sheet, rowNO, columnData, 20000);
            workbook.write(os);
        }catch(Exception e){
            logger.error("生成excel文件错误：" + e);
        }finally{
            try{
                if(null != os){
                    os.close();
                }
            }catch(Exception e){
                logger.error("关闭输出流错误" + e);
            }
        }
    }
    /**
     * 利用反射机制，根据对象和属性名称调用get方法获取属性值
     * @param fieldName
     * @param obj
     * @return
     */
    public static String getObjectFieldValue(String fieldName, Object obj){
        try {
            PropertyDescriptor pd = new PropertyDescriptor(fieldName, obj.getClass());
            Method getMethod = pd.getReadMethod();//获得get方法
            Object o = getMethod.invoke(obj);//执行get方法返回一个Object
            if(null != o){
                return o.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		 * String fileFullName = new
		 * String("E:\\EtcFiles\\downfile\\t_crd_det.txt"); List<String[]> lists
		 * = FileDealUtils.readFromFile(fileFullName);
		 */
		String fullPath = "D:\\apache-tomcat-7.0.69\\webapps\\CDMP";
		List<String> fileList = FileUtils.getFilesList(fullPath, "xml");
		for (String fileName : fileList) {
			System.out.println(fileName);

			System.out.println(StringUtils.indexOf(fileName, "CEB311Message"));
		}
	}
	/**
	 * 解析excel文件的表头
	 * @param input
	 * @param rowBegin
	 * @param colBegin
	 * @param colEnd
	 * @return
	 * List<List<Object>>
	 */
	public static List<String> processExcelByRowAndColByInputStream(InputStream input, int rowBegin, int colBegin, int colEnd, String filePath) {
        Assert.notNull(input, "文件流不能为空");
        Workbook wb = null; 
        try {
        	if (filePath.endsWith(".xls")) {
        		wb = new HSSFWorkbook(input);
			}else {
				wb = new XSSFWorkbook(input);
			}
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (wb != null) {
            //获取工作表
        	Sheet sheet = wb.getSheetAt(0);
            return processExcelTitle(sheet, rowBegin, colBegin, colEnd);
        }
        return null;
    }
    /**
     * 解析表(标题)
     * @param sheet
     * @return
     */
    public static List<String> processExcelTitle(Sheet sheet, int rowBegin, int colBegin, int colEnd) {
        Assert.notNull(sheet, "表不能为空");
        int columns = sheet.getRow(rowBegin).getPhysicalNumberOfCells();
        Row row = sheet.getRow(rowBegin);
        List<String> childList = new ArrayList<String>(colEnd);
        //循环读取列
        for (int cellCount = 0; cellCount < colEnd; cellCount++) {
            Cell cell = row.getCell(cellCount);
            //跳过空单元格
            if (cell == null) {
                childList.add("");
                continue;
            }
            switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                childList.add(cell.getStringCellValue() != null ? cell.getStringCellValue().trim() : "");
                break;
            default:
                childList.add("");
            }
            cell = null;
        }
        return childList;
    }

}
