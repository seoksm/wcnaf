package com.winitech.common.library;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.jxls.common.Context;
import org.jxls.transform.poi.PoiTransformer;
import org.jxls.util.JxlsHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * 엑셀내보내기 관련 유틸리티 클래스입니다.
 * <pre>
 * com.winitech.common.library
 * └ WiniExcel.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-06 10:21
 **/
@Slf4j
@Component
public class WiniExcel {
	@Getter
	private static String excelTemplateDir;

	@Value("${winitech.excel.template-dir}")
	private void setExcelTemplateDir(String excelTemplateDir) {
		excelTemplateDir = excelTemplateDir.replace('\\', '/');
		if (! excelTemplateDir.endsWith("/")) {
			excelTemplateDir += "/";
		}

		WiniExcel.excelTemplateDir = excelTemplateDir;
	}

	/**
	 * 주어진 컬럼 리스트와 데이터 리스트를 사용하여 엑셀 워크북을 생성합니다.<br/>
	 * 생성된 엑셀 워크북은 바로 다운로드하거나 가공하여 다운로드 할 수 있습니다.
	 * <pre><code>
	 *     // 사용예제
	 *     WiniExcel.WiniExcelOptions options = WiniExcel.WiniExcelOptions.builder()
	 *         .title("엑셀 제목")									// 상단 제목
	 *         .useRowNumber(true)									// 행번호 컬럼 사용여부
	 *         .useSheetProtect(true)								// 시트 보호 사용여부
	 *         .sheetProtectPasswd("비밀번호")						// 시트 보호 비밀번호
	 *         .headerList(Arrays.asList("컬럼1", "컬럼2", "컬럼3"))	// 컬럼 이름 목록
	 *         .widthList(Arrays.asList(20, 30, 40))			// 컬럼너비 목록
	 *         .sheetProtectList(Arrays.asList("컬럼1", "컬럼2"))	// 시트 보호시 잠금 컬럼 목록
	 *         .build();
	 *     
	 *     List&lt;String> columnList = Arrays.asList("a", "b", "c");
	 *     List&lt;Map&lt;String, Object>> dataList = new ArrayList&lt;>();
	 *     dataList.add(new HashMap&lt;String, Object>() {{
	 *        put("컬럼4", "데이터4");
	 *        put("컬럼6", "데이터6");
	 *        put("a", "데이터1");
	 *        put("b", 123);
	 *        put("c", 45.67);
	 *        put("컬럼5", "데이터5");
	 *        put("컬럼7", "데이터7");
	 *        put("컬럼8", "데이터8");
	 *        put("컬럼9", "데이터9");
	 *        put("컬럼10", "데이터10");
	 *     }});
	 *     dataList.add(new HashMap&lt;String, Object>() {{
	 *        put("a", "데이터2");
	 *        put("b", 456);
	 *        put("c", 78.90);
	 *     }});
	 *     
	 *     Workbook workbook = WiniExcel.createExcelWorkbook(options, columnList, dataList);
	 *     workbook.write(new FileOutputStream("output.xlsx"));
	 *     // => 엑셀제목
	 *     //    	컬럼1 	컬럼2 	컬럼3
	 *     //    1  데이터1	123		45.67
	 *     //    2  데이터2	456		78.90
	 * </code></pre>
	 * @param options 엑셀 옵션
	 * @param columnList 엑셀 컬럼 리스트. 여기서 컬럼은 데이터 리스트의 키와 매칭됩니다.
	 * @param dataList Map&lt;String, Object> 형식의 엑셀 데이터 리스트
	 * @return
	 */
	public static Workbook createExcelWorkbook(WiniExcelOptions options, List<String> columnList, List<Map<String, Object>> dataList) {
		List<String> tempColumnList = new ArrayList<>();
		tempColumnList.addAll(columnList);
		columnList = tempColumnList;
		
		Set<String> sheetProtectSet = new HashSet<>();
		
		if (options.getSheetProtectList() != null && options.getSheetProtectList().size() > 0) {
			sheetProtectSet.addAll(options.getSheetProtectList());
		}
		
		XSSFWorkbook workbook = new XSSFWorkbook();

		if (options.isUseRowNumber()) {
			// 순번 추가
			columnList.add("NO");
		}

		HorizontalAlignment hlAlign = HorizontalAlignment.LEFT;
		HorizontalAlignment hcAlign = HorizontalAlignment.CENTER;
		HorizontalAlignment hrAlign = HorizontalAlignment.RIGHT;
		VerticalAlignment vAlign = VerticalAlignment.CENTER;
		BorderStyle border = BorderStyle.THIN;

		XSSFDataFormat df = (XSSFDataFormat) workbook.createDataFormat();

		//----- Header Style 정의 ------------------------------//
		XSSFFont font = workbook.createFont();
		//font.setFontName("ARIAL");
		font.setFontHeightInPoints((short) 10);

		XSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
		headerStyle.setFillBackgroundColor(IndexedColors.PALE_BLUE.getIndex());

		headerStyle.setAlignment(hcAlign);
		headerStyle.setVerticalAlignment(vAlign);

		headerStyle.setBorderBottom(border);
		headerStyle.setBorderTop(border);
		headerStyle.setBorderRight(border);
		headerStyle.setBorderLeft(border);
		headerStyle.setWrapText(true);

		headerStyle.setFont(font);

		headerStyle.setLocked(true);
		//headerStyle.setHidden(true);
		//--------------------------------------------------//


		//----- VARCHAR Body Style 정의 ------------------------------//
		XSSFCellStyle strStyle = workbook.createCellStyle();
		//vStyle.setFillPattern(XSSFCellStyle.FINE_DOTS );

		strStyle.setAlignment(hcAlign);
		strStyle.setVerticalAlignment(vAlign);

		strStyle.setBorderBottom(border);
		strStyle.setBorderTop(border);
		strStyle.setBorderRight(border);
		strStyle.setBorderLeft(border);

		strStyle.setLocked(false);

		strStyle.setFont(font);
		strStyle.setDataFormat(df.getFormat("@"));

		// 잠김설정
		XSSFCellStyle strProtectStyle = workbook.createCellStyle();

		strProtectStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		strProtectStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		strProtectStyle.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

		strProtectStyle.setAlignment(hcAlign);
		strProtectStyle.setVerticalAlignment(vAlign);

		strProtectStyle.setBorderBottom(border);
		strProtectStyle.setBorderTop(border);
		strProtectStyle.setBorderRight(border);
		strProtectStyle.setBorderLeft(border);

		strProtectStyle.setLocked(true);

		strProtectStyle.setFont(font);
		strStyle.setDataFormat(df.getFormat("@"));
		//--------------------------------------------------//


		//----- NUMERIC Body Style 정의 ------------------------------//
		XSSFCellStyle numStyle = workbook.createCellStyle();
		//vStyle.setFillPattern(XSSFCellStyle.FINE_DOTS );

		numStyle.setAlignment(hrAlign);
		numStyle.setVerticalAlignment(vAlign);

		numStyle.setBorderBottom(border);
		numStyle.setBorderTop(border);
		numStyle.setBorderRight(border);
		numStyle.setBorderLeft(border);

		numStyle.setLocked(false);
		numStyle.setFont(font);
		numStyle.setDataFormat(df.getFormat("#,##0"));


		// 잠김설정
		XSSFCellStyle numProtectStyle = workbook.createCellStyle();

		numProtectStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		numProtectStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		numProtectStyle.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

		numProtectStyle.setAlignment(hrAlign);
		numProtectStyle.setVerticalAlignment(vAlign);

		numProtectStyle.setBorderBottom(border);
		numProtectStyle.setBorderTop(border);
		numProtectStyle.setBorderRight(border);
		numProtectStyle.setBorderLeft(border);

		numProtectStyle.setLocked(true);
		numProtectStyle.setFont(font);
		numProtectStyle.setDataFormat(df.getFormat("#,##0"));
		//--------------------------------------------------//

		//----- DOUBLE Body Style 정의 ------------------------------//
		XSSFCellStyle dobStyle = workbook.createCellStyle();

		dobStyle.setAlignment(hrAlign);
		dobStyle.setVerticalAlignment(vAlign);

		dobStyle.setBorderBottom(border);
		dobStyle.setBorderTop(border);
		dobStyle.setBorderRight(border);
		dobStyle.setBorderLeft(border);

		dobStyle.setLocked(false);
		dobStyle.setFont(font);
		df = (XSSFDataFormat) workbook.createDataFormat();
		dobStyle.setDataFormat(df.getFormat("#,##0.00"));

		// 잠김설정
		XSSFCellStyle dobProtectStyle = workbook.createCellStyle();

		dobProtectStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		dobProtectStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		dobProtectStyle.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

		dobProtectStyle.setAlignment(hrAlign);
		dobProtectStyle.setVerticalAlignment(vAlign);

		dobProtectStyle.setBorderBottom(border);
		dobProtectStyle.setBorderTop(border);
		dobProtectStyle.setBorderRight(border);
		dobProtectStyle.setBorderLeft(border);

		dobProtectStyle.setLocked(true);
		dobProtectStyle.setFont(font);
		dobProtectStyle.setDataFormat(df.getFormat("#,##0.00"));
		//--------------------------------------------------//


		//----- Title Style 정의 ------------------------------//
		XSSFFont titleFont = workbook.createFont();
		//titleFont.setFontName("ARIAL");
		titleFont.setFontHeightInPoints((short) 20);
		titleFont.setBold(true);

		XSSFCellStyle titleStyle = workbook.createCellStyle();
		titleStyle.setAlignment(hcAlign);
		titleStyle.setFont(titleFont);
		//--------------------------------------------------//

		int dataRow = 0;
		int seq = 1;

		int dataSize = dataList.size();
		int startRow = 0;

		int addRowNum = 0;

		String numStr = "";

		// 데이터가 없을 때 Math.ceil(0/30000)=0이 되면서 시트가 하나도 생성되지 않아 엑셀 파일이 손상됩니다.
		// 업로드 템플릿처럼 헤더만 있는 경우에도 최소 1개 시트는 필요합니다.
		int sheetCount = Math.max(1, (int) Math.ceil((double) dataSize / 30000));

		for (int sheetNum = 0; sheetNum < sheetCount; sheetNum++) {

			// sheet생성
			XSSFSheet sheet = workbook.createSheet("Sheet" + (sheetNum + 1));

			// 엑셀의 행
			XSSFRow xRow = null;
			// 엑셀의 셀
			XSSFCell xCell = null;

			int rowPos = 0;

			//----- 타이틀 설정 ------------------------------//
			if (options.getTitle() != null && !"".equals(options.getTitle())) {
				xRow = sheet.createRow(0);
				//병합 영역 설정
				if (options.isUseRowNumber()) {
					sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnList.size() - 1));
				} else {
					sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnList.size() - 1));
				}

				xCell = xRow.createCell(0);
				xCell.setCellValue(options.getTitle());
				xCell.setCellStyle(titleStyle);

				rowPos = 2;
			}
			//--------------------------------------------------//


			//----- Header Row 생성 ------------------------------//
			xRow = sheet.createRow(rowPos);
			rowPos++;
			//row.setRowStyle(style);

			//cell.setCellStyle(style.get("header"));

			int colCnt = 0;
			if (options.getHeaderList() != null && options.getHeaderList().size() > 0) {
				String key = "";

				//----- 순번추가 ---------------
				if (options.isUseRowNumber()) {
					xCell = xRow.createCell(colCnt);
					xCell.setCellValue("NO");
					xCell.setCellStyle(headerStyle);
					colCnt++;
				}
				//------------------------------

				for (int i = 0; i < options.getHeaderList().size(); i++) {
					key = WiniCom.nvl(options.getHeaderList().get(i), "");
					key = key.replaceAll("<br>", " ").replaceAll("<br/>", " ").replaceAll("<br />", " ");

					xCell = xRow.createCell(colCnt);

					// Header Row 생성
					xCell.setCellValue(key);
					xCell.setCellStyle(headerStyle);

					//row.createCell(i).setCellStyle(style);
					colCnt++;
				}
			}
			//--------------------------------------------------//


			//----- DB데이터 설정 ------------------------------//
			if (dataList != null && dataSize > 0) {
				Map dataMap = new HashMap();
				String[] columnArr = null;

				for (int row = startRow; row < 30000 * (sheetNum + 1); row++) {
					xRow = ((XSSFSheet) sheet).createRow((short) (rowPos++));

					dataMap = dataList.get(row);

					if (columnList != null && columnList.size() > 0) {
						// 순번추가
						if (options.isUseRowNumber()) {
							xCell = xRow.createCell(0);
							xCell.setCellValue(row + 1);
							xCell.setCellStyle(strStyle);

							addRowNum = 1;
						}

						for (int j = 0; j < columnList.size() - addRowNum; j++) {
							// 생성된 row에 컬럼을 생성한다
							xCell = xRow.createCell(j + addRowNum);
							//xCell.setCellType(xCell.CELL_TYPE_NUMERIC);

							// map에 담긴 데이터를 가져와 cell에 add한다
							//xCell.setCellValue(nvl(String.valueOf(dataMap.get(columnList.get(j))), ""));
							//xCell.setCellValue(nvl("" + dataMap.get(columnList.get(j)), ""));
							//xCell.setCellValue(EgovStringUtil.null2void((String) dataMap.get(columnList.get(j))));

							try {
								columnArr = columnList.get(j).toString().split(":");

								// 시트보호 컬럼에 따른 스타일 변경
								String protectYn = "N";
								if (sheetProtectSet.contains(columnArr[0])) {
									protectYn = "Y";
									break;
								}

								if (columnArr.length > 1) {
									if ("N".equals(columnArr[1].toUpperCase()) || "D".equals(columnArr[1].toUpperCase())) {
										numStr = String.valueOf(dataMap.get(columnArr[0])).replaceAll(",", "");

										try {
											xCell.setCellType(CellType.NUMERIC);
											if (numStr != null && !"".equals(numStr) && !"null".equals(numStr)) {
												xCell.setCellValue(Double.parseDouble(numStr));
											}
											//xCell.setCellValue(Double.parseDouble(String.valueOf(dataMap.get(columnArr[0])).replaceAll(",",  "")));
										} catch (NumberFormatException nfe) {
											log.error(nfe.toString());

											//xCell.setCellType(xCell.CELL_TYPE_STRING);
											//xCell.setCellValue(nvl(String.valueOf(dataMap.get(columnArr[0])), ""));
											xCell.setCellValue(WiniCom.nvl(numStr, ""));
										} catch (Exception e) {
											log.error(e.toString());

											//xCell.setCellValue(WiniCom.nvl(String.valueOf(dataMap.get(columnArr[0])), ""));
											xCell.setCellValue(WiniCom.nvl(numStr, ""));
										}

										if ("N".equals(columnArr[1].toUpperCase())) {
											if ("Y".equals(protectYn)) {
												xCell.setCellStyle(numProtectStyle);
											} else {
												xCell.setCellStyle(numStyle);
											}
										} else {
											if ("Y".equals(protectYn)) {
												xCell.setCellStyle(dobProtectStyle);
											} else {
												xCell.setCellStyle(dobStyle);
											}
										}
									} else {
										xCell.setCellValue(WiniCom.nvl(String.valueOf(dataMap.get(columnArr[0])), ""));
										if ("Y".equals(protectYn)) {
											xCell.setCellStyle(strProtectStyle);
										} else {
											xCell.setCellStyle(strStyle);
										}
									}
								} else {
									//xCell.setCellValue(EgovStringUtil.null2void((String) dataMap.get(columnList.get(j))));
									xCell.setCellValue(WiniCom.nvl(String.valueOf(dataMap.get(columnList.get(j))), ""));

									if ("Y".equals(protectYn)) {
										xCell.setCellStyle(strProtectStyle);
									} else {
										xCell.setCellStyle(strStyle);
									}

								}
							} catch (ClassCastException cce) {
								log.error(cce.toString());

								xCell.setCellType(CellType.NUMERIC);
								xCell.setCellValue(Double.parseDouble(String.valueOf(dataMap.get(columnList.get(j)))));

								xCell.setCellStyle(numStyle);
							} catch (Exception e) {
								log.error(e.toString());

								xCell.setCellValue(WiniCom.nvl(String.valueOf(dataMap.get(columnList.get(j))), ""));

								xCell.setCellStyle(strStyle);
							}
									
									/*try {
										xCell.setCellValue(EgovStringUtil.null2void((String) dataMap.get(columnList.get(j))));
									} catch (ClassCastException cce) {
										xCell.setCellType(xCell.CELL_TYPE_NUMERIC);
										xCell.setCellValue(Double.parseDouble(String.valueOf(dataMap.get(columnList.get(j)))));
									} catch (Exception e) {
										xCell.setCellValue(WiniCom.nvl(String.valueOf(dataMap.get(columnList.get(j))), ""));
									}*/

							//xCell.setCellStyle(bodyStyle);
						}
					}

					startRow++;

					if (dataSize == startRow) {
						break;
					}
				}
			}
			//--------------------------------------------------//


			//----- 컬럼 width 설정 ------------------------------//
			for (int i = 0; i < colCnt; i++) {
				sheet.autoSizeColumn((short) i);
				//sheet.setColumnWidth(i, (sheet.getColumnWidth(i)) + 512);  // 윗줄만으로는 컬럼의 width 가 부족하여 더 늘려야 함.
				sheet.setColumnWidth(i, Math.min(255 * 256, sheet.getColumnWidth(i) + 1500));
			}

			if (options.getWidthList() != null) {
				int iColOffset = 0;
				if (options.isUseRowNumber()) {
					iColOffset = 1;
				}

				for (int iCol = 0; iCol < options.getWidthList().size(); iCol++) {
					Integer width = options.getWidthList().get(iCol);

					if (width != null) {
						sheet.setColumnWidth(iCol + iColOffset, width * 256);
					}
				}
			}

			//--------------------------------------------------//
			//response.setContentType(getContentType());

			//startRow = 30000 + 1;

			// 시트보호여부
			if (options.isUseSheetProtect()) {
				sheet.protectSheet(options.getSheetProtectPasswd());
			}
		}

		return workbook;
	}

	/**
	 * 주어진 정보를 사용하여 엑셀 파일을 생성하고 다운 받습니다.
	 * <pre><code>
	 *     // 사용예제
	 *     WiniExcel.WiniExcelOptions options = WiniExcel.WiniExcelOptions.builder()
	 *         .title("엑셀 제목")									// 상단 제목
	 *         .useRowNumber(true)									// 행번호 컬럼 사용여부
	 *         .useSheetProtect(true)								// 시트 보호 사용여부
	 *         .sheetProtectPasswd("비밀번호")						// 시트 보호 비밀번호
	 *         .headerList(Arrays.asList("컬럼1", "컬럼2", "컬럼3"))	// 컬럼 이름 목록
	 *         .widthList(Arrays.asList(20, 30, 40))			// 컬럼너비 목록
	 *         .sheetProtectList(Arrays.asList("컬럼1", "컬럼2"))	// 시트 보호시 잠금 컬럼 목록
	 *         .build();
	 *
	 *     List&lt;String> columnList = Arrays.asList("a", "b", "c");
	 *     List&lt;Map&lt;String, Object>> dataList = new ArrayList&lt;>();
	 *     dataList.add(new HashMap&lt;String, Object>() {{
	 *        put("컬럼4", "데이터4");
	 *        put("컬럼6", "데이터6");
	 *        put("a", "데이터1");
	 *        put("b", 123);
	 *        put("c", 45.67);
	 *        put("컬럼5", "데이터5");
	 *        put("컬럼7", "데이터7");
	 *        put("컬럼8", "데이터8");
	 *        put("컬럼9", "데이터9");
	 *        put("컬럼10", "데이터10");
	 *     }});
	 *     dataList.add(new HashMap&lt;String, Object>() {{
	 *        put("a", "데이터2");
	 *        put("b", 456);
	 *        put("c", 78.90);
	 *     }});
	 *
	 *     WiniExcel.downloadExcel(options, "파일명", columnList, dataList, req, res);
	 *     // => // 다음 내용의 엑셀파일이 파일명.xlsx로 다운로드 됩니다.
	 *     //	 엑셀제목
	 *     //    	컬럼1 	컬럼2 	컬럼3
	 *     //    1  데이터1	123		45.67
	 *     //    2  데이터2	456		78.90
	 * </code></pre>
	 * @param options 엑셀 옵션
	 * @param fileName 다운로드 파일 명
	 * @param columnList 엑셀 컬럼 리스트. 여기서 컬럼은 데이터 리스트의 키와 매칭됩니다.
	 * @param dataList Map&lt;String, Object> 형식의 엑셀 데이터 리스트
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 */
	public static void downloadExcel(WiniExcelOptions options, String fileName, List<String> columnList, List<Map<String, Object>> dataList, HttpServletRequest req, HttpServletResponse res) {
		// 언어가져오기
		//Locale locales = LocaleContextHolder.getLocale();
		//String localesNm = locales.toString();
		//log.debug("localesNm----->" + localesNm);

		if (WiniFile.getFileExtension(fileName).isEmpty()) {
			fileName += ".xlsx";
		}

		try (Workbook workbook = createExcelWorkbook(options, columnList, dataList)) {
			WiniFile.setDisposition(fileName, req, res);
			res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

			try (ServletOutputStream out = res.getOutputStream()) {
				workbook.write(out);
				out.flush();
			}
		} catch (IOException e) {
			log.error(e.toString());
			throw new RuntimeException(e);
		}
	}

	/**
	 * 주어진 정보를 사용하여 엑셀 파일을 outputStream으로 생성하여 보냅니다.
	 * <pre><code>
	 *     // 사용예제
	 *     WiniExcel.WiniExcelOptions options = WiniExcel.WiniExcelOptions.builder()
	 *         .title("엑셀 제목")									// 상단 제목
	 *         .useRowNumber(true)									// 행번호 컬럼 사용여부
	 *         .useSheetProtect(true)								// 시트 보호 사용여부
	 *         .sheetProtectPasswd("비밀번호")						// 시트 보호 비밀번호
	 *         .headerList(Arrays.asList("컬럼1", "컬럼2", "컬럼3"))	// 컬럼 이름 목록
	 *         .widthList(Arrays.asList(20, 30, 40))			// 컬럼너비 목록
	 *         .sheetProtectList(Arrays.asList("컬럼1", "컬럼2"))	// 시트 보호시 잠금 컬럼 목록
	 *         .build();
	 *
	 *     List&lt;String> columnList = Arrays.asList("a", "b", "c");
	 *     List&lt;Map&lt;String, Object>> dataList = new ArrayList&lt;>();
	 *     dataList.add(new HashMap&lt;String, Object>() {{
	 *        put("컬럼4", "데이터4");
	 *        put("컬럼6", "데이터6");
	 *        put("a", "데이터1");
	 *        put("b", 123);
	 *        put("c", 45.67);
	 *     }});
	 *     dataList.add(new HashMap&lt;String, Object>() {{
	 *        put("a", "데이터2");
	 *        put("b", 456);
	 *        put("c", 78.90);
	 *     }});
	 *
	 *     Workbook workbook = WiniExcel.saveExcel(options, columnList, dataList, outputStream);
	 *     // => // 다음 내용의 엑셀파일이 outputStream으로 보냅니다.
	 *     //	 엑셀제목
	 *     //    	컬럼1 	컬럼2 	컬럼3
	 *     //    1  데이터1	123		45.67
	 *     //    2  데이터2	456		78.90
	 * </code></pre>
	 * @param options 엑셀 옵션
	 * @param columnList 엑셀 컬럼 리스트. 여기서 컬럼은 데이터 리스트의 키와 매칭됩니다.
	 * @param dataList Map&lt;String, Object> 형식의 엑셀 데이터 리스트
	 * @param outputStream 엑셀 파일 내용이 출력될 outputStream
	 */
	public static void saveExcel(WiniExcelOptions options, List<String> columnList, List<Map<String, Object>> dataList, OutputStream outputStream) {
		// 언어가져오기
		//Locale locales = LocaleContextHolder.getLocale();
		//String localesNm = locales.toString();
		//log.debug("localesNm----->" + localesNm);

		try (Workbook workbook = createExcelWorkbook(options, columnList, dataList)) {
			workbook.write(outputStream);
			outputStream.flush();
		} catch (IOException e) {
			log.error(e.toString());
			throw new RuntimeException(e);
		}
	}

	/**
	 * 주어진 정보를 사용하여 엑셀 파일을 filePath에 생성합니다.
	 * <pre><code>
	 *     // 사용예제
	 *     WiniExcel.WiniExcelOptions options = new WiniExcel.WiniExcelOptions();
	 *     options.setTitle("엑셀 제목");									// 상단 제목 
	 *     options.setUseRowNumber(true);									// 행번호 컬럼 사용여부
	 *     options.setUseSheetProtect(true);								// 시트 보호 사용여부
	 *     options.setSheetProtectPasswd("비밀번호");						// 시트 보호 비밀번호
	 *     options.setHeaderList(Arrays.asList("컬럼1", "컬럼2", "컬럼3"));	// 컬럼 이름 목록
	 *     options.setWidthList(Arrays.asList(20, 30, 40));					// 컬럼너비 목록
	 *     options.setSheetProtectList(Arrays.asList("컬럼1", "컬럼2"));	// 시트 보호시 잠금 컬럼 목록
	 *
	 *     List&lt;String> columnList = Arrays.asList("a", "b", "c");
	 *     List&lt;Map&lt;String, Object>> dataList = new ArrayList&lt;>();
	 *     dataList.add(new HashMap&lt;String, Object>() {{
	 *        put("컬럼4", "데이터4");
	 *        put("컬럼6", "데이터6");
	 *        put("a", "데이터1");
	 *        put("b", 123);
	 *        put("c", 45.67);
	 *     }});
	 *     dataList.add(new HashMap&lt;String, Object>() {{
	 *        put("a", "데이터2");
	 *        put("b", 456);
	 *        put("c", 78.90);
	 *     }});
	 *
	 *     WiniExcel.saveExcel(options, columnList, dataList, "/tmp/output.xlsx");
	 *     // => // 다음 내용의 엑셀파일이 /tmp/output.xlsx에 생성됩니다.
	 *     //	 엑셀제목
	 *     //    	컬럼1 	컬럼2 	컬럼3
	 *     //    1  데이터1	123		45.67
	 *     //    2  데이터2	456		78.90
	 * </code></pre>
	 * @param options 엑셀 옵션
	 * @param columnList 엑셀 컬럼 리스트. 여기서 컬럼은 데이터 리스트의 키와 매칭됩니다.
	 * @param dataList Map&lt;String, Object> 형식의 엑셀 데이터 리스트
	 * @param filePath 엑셀 파일이 생성될 경로
	 */
	public static void saveExcel(WiniExcelOptions options, List<String> columnList, List<Map<String, Object>> dataList, String filePath) {
		try (Workbook workbook = createExcelWorkbook(options, columnList, dataList)) {
			try (OutputStream out = new FileOutputStream(WiniFile.filePathBlackList(filePath))) {
				workbook.write(out);
				out.flush();
			}
		} catch (IOException e) {
			log.error(e.toString());
			throw new RuntimeException(e);
		}
	}

	/**
	 * 템플릿 엑셀 파일을 사용하여<br/>
	 * 엑셀 파일을 생성하고 다운로드합니다.<br/>
	 * 엑셀 템플릿 양식은 https://jxls.sourceforge.net/reference/xls_area.html 문서를 참고하세요.
	 * <pre><code>
	 * // 사용예제
	 * WiniExcel.downloadExcelTemplate("template.xlsx", "파일명.xlsx", data, req, res);
	 * // => template.xlsx 파일을 가공하여 파일명.xlsx로 다운로드 합니다.
	 * </code></pre>
	 * @param templateName 엑셀 템플릿 파일명
	 * @param fileName 다운로드 파일명
	 * @param data 엑셀 템플릿에 적용할 데이터
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 */
	public static void downloadExcelTemplate(String templateName, String fileName, Map<String, Object> data, HttpServletRequest req, HttpServletResponse res) {
		// 템플릿 양식은 다음 문서를 참고하세요. https://jxls.sourceforge.net/reference/xls_area.html
		
		if (WiniFile.getFileExtension(fileName).isEmpty()) {
			fileName += ".xlsx";
		}

		try (OutputStream outputStream = res.getOutputStream()) {
			WiniFile.setDisposition(fileName, req, res);
			res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

			saveExcelTemplate(templateName, data, outputStream);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 템플릿 엑셀 파일을 사용하여<br/>
	 * 엑셀 파일을 생성하고 outputStream으로 전달합니다.<br/>
	 * 엑셀 템플릿 양식은 https://jxls.sourceforge.net/reference/xls_area.html 문서를 참고하세요.
	 * <pre><code>
	 * // 사용예제
	 * WiniExcel.saveExcelTemplate("template.xlsx", data, outputStream);
	 * // => template.xlsx 파일을 가공하여 outputStream으로 보냅니다.
	 * </code></pre>
	 * @param templateName 엑셀 템플릿 파일명
	 * @param data 엑셀 템플릿에 적용할 데이터
	 * @param outputStream 엑셀 파일 내용이 출력될 outputStream
	 */
	public static void saveExcelTemplate(String templateName, Map<String, Object> data, OutputStream outputStream) {
		// 템플릿 양식은 다음 문서를 참고하세요. https://jxls.sourceforge.net/reference/xls_area.html

		String templateFilePath = WiniFile.filePathBlackList(getExcelTemplateDir() + templateName);

		try (FileInputStream fis = new FileInputStream(templateFilePath);
			 InputStream is = new BufferedInputStream(fis)) {
			try (Workbook workbook = WorkbookFactory.create(is)) {
				PoiTransformer transformer = PoiTransformer.createTransformer(workbook);
				transformer.setOutputStream(outputStream);
				// 템플릿에 데이터를 적용

				Context context = new Context(data);
				JxlsHelper.getInstance().processTemplate(context, transformer);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 템플릿 엑셀 파일을 사용하여<br/>
	 * 엑셀 파일을 filePath에 지정된 경로에 생성합니다.<br/>
	 * 엑셀 템플릿 양식은 https://jxls.sourceforge.net/reference/xls_area.html 문서를 참고하세요.
	 * <pre><code>
	 * // 사용예제
	 * WiniExcel.saveExcelTemplate("template.xlsx", data, "/tmp/output.xlsx");
	 * // => template.xlsx 파일을 가공하여 /tmp/output.xlsx에 파일을 생성합니다.
	 * </code></pre>
	 * @param templateName 엑셀 템플릿 파일명
	 * @param data 엑셀 템플릿에 적용할 데이터
	 * @param filePath 엑셀 파일이 생성될 파일명이 포함된 경로
	 */
	public static void saveExcelTemplate(String templateName, Map<String, Object> data, String filePath) {
		// 템플릿 양식은 다음 문서를 참고하세요. https://jxls.sourceforge.net/reference/xls_area.html

		try (OutputStream outputStream = new FileOutputStream(WiniFile.filePathBlackList(filePath))) {
			saveExcelTemplate(templateName, data, outputStream);
			outputStream.flush();
		} catch (IOException e) {
			log.error(e.toString());
			throw new RuntimeException(e);
		}
	}

	/**
	 * 엑셀 내보내기시 사용되는 옵션을 설정하는 클래스입니다.
	 */
	@Getter
	@Setter
	public static class WiniExcelOptions {
		private String title;
		private boolean useRowNumber;
		private boolean useSheetProtect;
		private String sheetProtectPasswd;
		private List<String> headerList;
		private List<Integer> widthList;
		private List<String> sheetProtectList;

		/**
		 * 엑셀 내보내기시 사용되는 옵션을 설정하는 클래스입니다.
		 * <pre><code>
		 *     // 사용예제
		 *     WiniExcel.WiniExcelOptions options = new WiniExcel.WiniExcelOptions();
		 *     options.setTitle("엑셀 제목");									// 상단 제목 
		 *     options.setUseRowNumber(true);									// 행번호 컬럼 사용여부
		 *     options.setUseSheetProtect(true);								// 시트 보호 사용여부
		 *     options.setSheetProtectPasswd("비밀번호");						// 시트 보호 비밀번호
		 *     options.setHeaderList(Arrays.asList("컬럼1", "컬럼2", "컬럼3"));	// 컬럼 이름 목록
		 *     options.setWidthList(Arrays.asList(20, 30, 40));					// 컬럼너비 목록
		 *     options.setSheetProtectList(Arrays.asList("컬럼1", "컬럼2"));	// 시트 보호시 잠금 컬럼 목록
		 * </code></pre>
		 */
		public WiniExcelOptions() {
			this.title = "";
			this.useRowNumber = false;
			this.useSheetProtect = false;
			this.sheetProtectPasswd = "";
			this.headerList = new ArrayList<>();
			this.widthList = new ArrayList<>();
			this.sheetProtectList = new ArrayList<>();
		}

		/**
		 * 엑셀 내보내기시 사용되는 옵션을 설정하는 클래스입니다.
		 * <pre><code>
		 *     // 사용예제
		 *     WiniExcel.WiniExcelOptions options = WiniExcel.WiniExcelOptions.builder()
		 *         .title("엑셀 제목")									// 상단 제목
		 *         .useRowNumber(true)									// 행번호 컬럼 사용여부
		 *         .useSheetProtect(true)								// 시트 보호 사용여부
		 *         .sheetProtectPasswd("비밀번호")						// 시트 보호 비밀번호
		 *         .headerList(Arrays.asList("컬럼1", "컬럼2", "컬럼3"))	// 컬럼 이름 목록
		 *         .widthList(Arrays.asList(20, 30, 40))			// 컬럼너비 목록
		 *         .sheetProtectList(Arrays.asList("컬럼1", "컬럼2"))	// 시트 보호시 잠금 컬럼 목록
		 *         .build();
		 * </code></pre>
		 * @param title
		 * @param headerList
		 * @param widthList
		 * @param useRowNumber
		 * @param useSheetProtect
		 * @param sheetProtectPasswd
		 */
		@Builder
		public WiniExcelOptions(String title, List<String> headerList, List<Integer> widthList, boolean useRowNumber, boolean useSheetProtect, String sheetProtectPasswd, List<String> sheetProtectList) {
			this.title = title;
			this.useRowNumber = useRowNumber;
			this.useSheetProtect = useSheetProtect;
			this.sheetProtectPasswd = sheetProtectPasswd;

			this.headerList = new ArrayList<>();
			if (headerList != null) {
				this.headerList.addAll(headerList);
			}
			
			this.widthList = new ArrayList<>();
			if (widthList != null) {
				this.widthList.addAll(widthList);
			}
			
			this.sheetProtectList = new ArrayList<>();
			if (sheetProtectList != null) {
				this.sheetProtectList.addAll(sheetProtectList);
			}
		}
	}
}
