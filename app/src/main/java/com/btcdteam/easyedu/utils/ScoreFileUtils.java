package com.btcdteam.easyedu.utils;

import android.net.Uri;

import com.btcdteam.easyedu.models.StudentDetail;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.Callable;

public class ScoreFileUtils {
    private TaskRunner taskRunner;
    private String filePath;
    private List<StudentDetail> list;
    private addOnCompleteListener addOnCompleteListener;
    private static final String TAG = "ScoreFileUtils";

    public interface addOnCompleteListener {
        void onComplete(Uri export);

        void onError(Exception e);
    }

    public ScoreFileUtils(String filePath, List<StudentDetail> list, ScoreFileUtils.addOnCompleteListener addOnCompleteListener) {
        this.filePath = filePath;
        this.list = list;
        this.addOnCompleteListener = addOnCompleteListener;
    }

    public void exportScoreInputFile() {
        taskRunner = new TaskRunner();
        taskRunner.execute(new Callable<Uri>() {
            @Override
            public Uri call() throws Exception {
                String[] columns = {"ID", "Mã HS", "Mã lớp", "Họ và tên", "Điểm thường xuyên 1", "Điểm thường xuyên 2", "Điểm thường xuyên 3", "Điểm giữa kỳ", "Điểm cuối kỳ"};
                Workbook workbook = new XSSFWorkbook();
                Sheet semesterSheet1 = workbook.createSheet("Học kỳ 1");
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) 14);
                headerFont.setColor(IndexedColors.BLUE.index);
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFont(headerFont);
                Row headerRow = semesterSheet1.createRow(0);
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns[i]);
                    cell.setCellStyle(headerCellStyle);
                }
                int rowNum = 1;
                for (StudentDetail detail : list) {
                    if (detail.getSemester() == 1) {
                        Row row = semesterSheet1.createRow(rowNum++);
                        row.createCell(0).setCellValue(detail.getId());
                        row.createCell(1).setCellValue(detail.getStudentId());
                        row.createCell(2, CellType.NUMERIC).setCellValue(detail.getClassroomId());
                        row.createCell(3).setCellValue(detail.getName());
                        row.createCell(4, CellType.NUMERIC).setBlank();
                        row.createCell(5, CellType.NUMERIC).setBlank();
                        row.createCell(6, CellType.NUMERIC).setBlank();
                        row.createCell(7, CellType.NUMERIC).setBlank();
                        row.createCell(8, CellType.NUMERIC).setBlank();
                    }
                }
                Sheet semesterSheet2 = workbook.createSheet("Học kỳ 2");

                Row headerRow2 = semesterSheet2.createRow(0);
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = headerRow2.createCell(i);
                    cell.setCellValue(columns[i]);
                    cell.setCellStyle(headerCellStyle);
                }
                int rowNum2 = 1;
                for (StudentDetail detail : list) {
                    if (detail.getSemester() == 2) {
                        Row row = semesterSheet2.createRow(rowNum2++);
                        row.createCell(0).setCellValue(detail.getId());
                        row.createCell(1).setCellValue(detail.getStudentId());
                        row.createCell(2, CellType.NUMERIC).setCellValue(detail.getClassroomId());
                        row.createCell(3).setCellValue(detail.getName());
                        row.createCell(4, CellType.NUMERIC).setBlank();
                        row.createCell(5, CellType.NUMERIC).setBlank();
                        row.createCell(6, CellType.NUMERIC).setBlank();
                        row.createCell(7, CellType.NUMERIC).setBlank();
                        row.createCell(8, CellType.NUMERIC).setBlank();
                    }
                }
                semesterSheet1.setColumnWidth(3,24 * 256);
                semesterSheet1.setColumnWidth(4, 24 * 256);
                semesterSheet1.setColumnWidth(5, 24 * 256);
                semesterSheet1.setColumnWidth(6, 24 * 256);
                semesterSheet1.setColumnWidth(7, 15 * 256);
                semesterSheet1.setColumnWidth(8, 15 * 256);
                semesterSheet2.setColumnWidth(3,24 * 256);
                semesterSheet2.setColumnWidth(4, 24 * 256);
                semesterSheet2.setColumnWidth(5, 24 * 256);
                semesterSheet2.setColumnWidth(6, 24 * 256);
                semesterSheet2.setColumnWidth(7, 15 * 256);
                semesterSheet2.setColumnWidth(8, 15 * 256);
                semesterSheet1.setColumnHidden(0 ,true);
                semesterSheet1.setColumnHidden(1, true);
                semesterSheet1.setColumnHidden(2, true);
                semesterSheet2.setColumnHidden(0, true);
                semesterSheet2.setColumnHidden(1, true);
                semesterSheet2.setColumnHidden(2, true);
                FileOutputStream fileOut = new FileOutputStream(filePath);
                workbook.write(fileOut);
                fileOut.close();
                workbook.close();
                File file = new File(filePath);
                if (!file.exists()) {
                    throw new Exception("File not found");
                }

                return Uri.fromFile(file);
            }
        }, new TaskRunner.Callback<Uri>() {
            @Override
            public void onComplete(Uri result) {
                addOnCompleteListener.onComplete(result);
            }

            @Override
            public void onError(Exception e) {
                addOnCompleteListener.onError(e);
            }
        });
    }
}
