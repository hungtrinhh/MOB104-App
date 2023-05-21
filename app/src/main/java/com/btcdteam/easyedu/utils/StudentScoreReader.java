package com.btcdteam.easyedu.utils;

import android.util.Log;

import com.btcdteam.easyedu.models.StudentDetail;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

public class StudentScoreReader {
    private static final String TAG = "StudentScoreReader";
    private String filePath;
    private HandleOnComplete handleOnComplete;
    private TaskRunner taskRunner;

    public interface HandleOnComplete {
        void onComplete(List<StudentDetail> studentDetails);

        void onError(Exception e);
    }

    public StudentScoreReader(String filePath, HandleOnComplete handleOnComplete) {
        this.filePath = filePath;
        this.handleOnComplete = handleOnComplete;
        taskRunner = new TaskRunner();
    }


    public void start() {
        List<StudentDetail> list = new ArrayList<>();
        taskRunner = new TaskRunner();
        taskRunner.execute(new Callable<List<StudentDetail>>() {
            @Override
            public List<StudentDetail> call() throws Exception {

                FileInputStream file = new FileInputStream(filePath);
                XSSFWorkbook workbook = new XSSFWorkbook(file);
                for (int i = 0; i <= 1; i++) {
                    XSSFSheet sheet = workbook.getSheetAt(i); //Chọn sheet
                    Iterator<Row> rowIterator = sheet.rowIterator();
                    while (rowIterator.hasNext()) {
                        StudentDetail studentDetail = new StudentDetail();
                        studentDetail.setSemester(i + 1);
                        Row row = rowIterator.next();
                        // Bỏ qua dòng đầu tiên là tiêu đề
                        if (row.getRowNum() == 0) {
                            continue;
                        }
                        Iterator<Cell> cellIterator = row.cellIterator();
                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();

                                switch (cell.getColumnIndex()) {
                                    case 0: // Cột 1 ID
                                        studentDetail.setId((int) cell.getNumericCellValue());
                                        break;
                                    case 1: // Cột 2 mã học sinh
                                        studentDetail.setStudentId(cell.getStringCellValue());
                                        break;
                                    case 2: // Cột 3 mã lớp
                                        studentDetail.setClassroomId((int) cell.getNumericCellValue());
                                        break;
                                    case 3: // Cột 4 họ tên
                                        studentDetail.setName(cell.getStringCellValue());
                                        break;
                                    case 4: // Cột 5 điểm tx 1
                                        studentDetail.setRegularScore1((float) cell.getNumericCellValue());
                                        break;
                                    case 5: // Cột 6 điểm tx 2
                                        studentDetail.setRegularScore2((float) cell.getNumericCellValue());
                                        break;
                                    case 6: // Cột 7 điểm tx 3
                                        studentDetail.setRegularScore3((float) cell.getNumericCellValue());
                                        break;
                                    case 7: // Cột 8 điểm giữa kỳ
                                        studentDetail.setMidtermScore((float) cell.getNumericCellValue());
                                        break;
                                    case 8: // Cột 9 điểm cuối kỳ
                                        studentDetail.setFinalScore((float) cell.getNumericCellValue());
                                        break;
                                    default:
                                        Log.d(TAG, "Other data");
                                        break;
                                }
                               // Log.d(TAG, cell.getColumnIndex() + " :" + cell.getStringCellValue());
                            }

                        list.add(studentDetail);
                        Log.d(TAG, "End Row");
                    }
                }
                return list;

            }
        }, new TaskRunner.Callback<List<StudentDetail>>() {
            @Override
            public void onComplete(List<StudentDetail> result) {
                handleOnComplete.onComplete(result);
            }

            @Override
            public void onError(Exception e) {
                handleOnComplete.onError(e);
            }
        });
    }
}
