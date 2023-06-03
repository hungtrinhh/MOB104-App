package com.btcdteam.easyedu.utils;

import android.util.Log;

import com.btcdteam.easyedu.models.Parent;
import com.btcdteam.easyedu.models.Student;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class StudentListReader {
    private static final String TAG = "StudentReader";
    private String filePath;
    private HandleOnComplete handleOnComplete;
    private TaskRunner taskRunner;
    private Map<String, Parent> parentMap = new LinkedHashMap<>();
    private Map<String, Parent> parentMapWithId = new LinkedHashMap<>();

    public interface HandleOnComplete {
        void onComplete(List<Student> students, Map<String, Parent> parentMap);

        void onError(Exception e);
    }

    public StudentListReader(String filePath, HandleOnComplete handleOnComplete) {
        this.filePath = filePath;
        this.handleOnComplete = handleOnComplete;
        taskRunner = new TaskRunner();
    }


    public void start() {
        List<Student> list = new ArrayList<>();
        taskRunner = new TaskRunner();
        taskRunner.execute(new Callable<List<Student>>() {
            @Override
            public List<Student> call() throws Exception {

                FileInputStream file = new FileInputStream(filePath);
                XSSFWorkbook workbook = new XSSFWorkbook(file);
                XSSFSheet sheet = workbook.getSheetAt(0); //Chọn sheet
                Iterator<Row> rowIterator = sheet.rowIterator();
                while (rowIterator.hasNext()) {
                    Parent parent = new Parent();
                    Student student = new Student();
                    Row row = rowIterator.next();
                    // Bỏ qua dòng đầu tiên là tiêu đề
                    if (row.getRowNum() == 0) {
                        continue;
                    }
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        if (cell.getCellType() == CellType.STRING) { // Nếu cell đó là chuỗi thì lưu vào student
                            switch (cell.getColumnIndex()) {
                                case 1: // Cột 1 Họ tên
                                    student.setName(cell.getStringCellValue());
                                    break;
                                case 2: // Cột 2 Ngày sinh
                                    student.setDob(cell.getStringCellValue());
                                    break;
                                case 3: // Cột 3 Giới tính
                                    student.setGender(cell.getStringCellValue());
                                    break;
                                case 4: // Cột 4 là tên phụ huynh
                                    parent.setName(cell.getStringCellValue());
                                    break;
                                case 5:
                                    if (!parentMap.containsKey(cell.getStringCellValue())) {
                                        parent.setPhone(cell.getStringCellValue());
                                        parent.makeId();
                                        parentMap.put(parent.getPhone(), parent);
                                    } else {
                                        parent.setPhone(parentMap.get(cell.getStringCellValue()).getPhone());
                                        parent.setId(parentMap.get(cell.getStringCellValue()).getId());
                                    }
                                    student.setParentPhone(parent.getPhone());
                                    break;
                                default:
                                    Log.d(TAG, "Other data");
                                    break;
                            }
                            Log.d(TAG, cell.getColumnIndex() + " :" + cell.getStringCellValue());
                        }
                    }
                    parentMapWithId.put(parent.getId(), parent);
                    student.setParentId(parent.getId());
                    list.add(student);
                    Log.d(TAG, "End Row");
                }
                return list;

            }
        }, new TaskRunner.Callback<List<Student>>() {
            @Override
            public void onComplete(List<Student> result) {
                handleOnComplete.onComplete(result, parentMapWithId);
            }

            @Override
            public void onError(Exception e) {
                handleOnComplete.onError(e);
            }
        });
    }
}
