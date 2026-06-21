package com.terry.backend.core.excel.util;

import com.terry.backend.core.dto.BaseSearchParam;
import com.terry.backend.core.excel.ExcelReader;
import com.terry.backend.core.excel.ExcelWriter;
import com.terry.backend.core.excel.config.ExcelConfig;
import com.terry.backend.core.excel.dto.ExcelHeader;
import com.terry.backend.core.excel.exception.ExcelJobAlreadyRunning;
import com.terry.backend.core.excel.mapper.ExcelMapper;
import com.terry.backend.core.excption.SystemException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public class ExcelUtils  implements InitializingBean {

    private static Map<String, Thread> excelWriterThread = new LinkedHashMap<>();
    private static Map<String, Thread> excelReaderThread = new LinkedHashMap<>();

    private static ExcelConfig config;

    private static ExcelMapper excelMapper;


    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
    public static void upload(final String id, final ExcelReader excelReader,
                              final MultipartHttpServletRequest request) throws Exception {
        if (excelReaderThread.containsKey(id)) {
            throw new ExcelJobAlreadyRunning();
        }

        String base = config.getTemporaryPath();
        base = base.replaceAll("\\\\", "/");
        if (!base.endsWith("/")) {
            base = base + "/";
        }

        File dir = new File(base);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }

        Iterator<String> filenames = request.getFileNames();

        excelReader.setRemoteAddr(request.getRemoteAddr());

        while (filenames.hasNext()) {
            MultipartFile file = request.getFile(filenames.next());

            excelReader.openWorkbook(file.getInputStream());
            excelReader.beforeRead();
            excelReader.read();
            excelReader.afterRead();
            excelReader.closeWorkbook();
        }
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
    public static String download(final String id, final ExcelWriter excelWriter, final Object param,
                                  final Class<? extends BaseSearchParam> clzz) throws Exception {
        if (excelWriterThread.containsKey(id)) {
            throw new ExcelJobAlreadyRunning();
        }

        Assert.notNull(excelWriter, "Excel writer is null");
        Assert.hasText(excelWriter.getFilename(), "Excel filename is not exists");

        excelWriter.setExcelConfig(config);

        excelWriter.setExcelMapper(excelMapper);

        excelWriter.openWorkbook();

        excelWriter.beforeWrite();

        if (param == null || clzz == null) {
            excelWriter.write(null);
        } else {
            excelWriter.write(clzz.cast(param));
        }

        excelWriter.afterWrite();

        final String downloadUri =
                String.format("%s/download/%s", config.getContextPath(), excelWriter.saveWorkbook());

        excelWriter.closeWorkbook();

        return downloadUri;
    }

    @SuppressWarnings("rawtypes")
    public static String download(final String id, final ExcelWriter excelWriter) throws Exception {
        return download(id, excelWriter, null, null);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(ExcelUtils.config, "Excel config cannot be null");
        Assert.notNull(ExcelUtils.excelMapper, "Excel mapper cannot be null");
    }

    public static CellCopyPolicy.Builder getCellCopyPolicyBuilder() {
        CellCopyPolicy policy = new CellCopyPolicy();
        return policy.createBuilder();
    }

    public static void setString(final XSSFRow row, final String column, final String value) {
        setString(row, parseColumnIndex(column), value);
    }

    public static void setInteger(final XSSFRow row, final int columnIndex, final Integer value) {
        try {
            XSSFCell cell = getCell(row, columnIndex);

            if (value != null) {
                cell.setCellValue(value);
            }
        } catch (Exception e) {
            // Hello...we got error here...
        }
    }

    public static void setInteger(final XSSFRow row, final String column, final Integer value) {
        setInteger(row, parseColumnIndex(column), value);
    }

    public static void setDouble(final XSSFRow row, final int columnIndex, final Double value) {
        try {
            XSSFCell cell = getCell(row, columnIndex);

            if (value != null) {
                cell.setCellValue(value);
            }
        } catch (Exception e) {
            // Hello...we got error here...
        }
    }

    public static void setDouble(final XSSFRow row, final String column, final Double value) {
        setDouble(row, parseColumnIndex(column), value);
    }

    public static String getString(final Row row, final int columnIndex) {
        try {
            Cell cell = row.getCell(columnIndex);
            String value = cell.getStringCellValue();

            if (StringUtils.hasText(value)) {
                return value;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static String getString(final Row row, final String column) {
        return getString(row, parseColumnIndex(column));
    }

    public static Integer getInteger(final Row row, final int columnIndex) {
        try {
            Cell cell = row.getCell(columnIndex);
            Integer value = new Double(cell.getNumericCellValue()).intValue();
            return value;
        } catch (Exception e) {
        }
        return null;
    }

    public static Integer getInteger(final Row row, final String column) {
        return getInteger(row, parseColumnIndex(column));
    }

    public static Double getDouble(final Row row, final int columnIndex) {
        try {
            Cell cell = row.getCell(columnIndex);
            Double value = cell.getNumericCellValue();
            return value;
        } catch (Exception e) {
        }
        return null;
    }

    public static Double getDouble(final Row row, final String column) {
        return getDouble(row, parseColumnIndex(column));
    }

    private static XSSFCell getCell(XSSFRow row, int columnIndex) throws Exception {
        XSSFCell cell = row.getCell(columnIndex);

        if (cell == null) {
            cell = row.createCell(columnIndex);
        }

        return cell;
    }

    public static int parseColumnIndex(String pos) {
        final ColumnAdder adder = new ColumnAdder();

        List<String> array = Arrays.asList(pos.split(""));
        Collections.reverse(array);

        array.stream().forEach(x -> {
            adder.add(x);
            adder.raise();
        });

        return adder.get();
    }

    public void setConfig(ExcelConfig config) {
        ExcelUtils.config = config;
    }

    public void setMapper(ExcelMapper excelMapper) {
        ExcelUtils.excelMapper = excelMapper;
    }

    private static class ColumnAdder {
        private int value = 0;
        private int base = 0;

        public void add(String alphabet) {
            this.value += (getAlphabetNumber(alphabet) * (int) Math.pow(26.0, (double) base));
        }

        public void raise() {
            base++;
        }

        public int get() {
            return value - 1;
        }

        private int getAlphabetNumber(String alphabet) {
            Character character = alphabet.toUpperCase().charAt(0);
            Character base = "A".charAt(0);
            return character - base + 1;
        }
    }

    public static void readRow(Row row, List<ExcelHeader> headers, Object obj, Class<?> clzz)
            throws Exception {
        for (ExcelHeader header : headers) {
            Field field = clzz.getDeclaredField(header.getField());
            boolean acc = field.isAccessible();
            // Set accessible to true
            field.setAccessible(true);

            Cell cell = row.getCell(header.getColumnIndex());

            switch (cell.getCellTypeEnum()) {
                case _NONE:
                case BLANK:
                case ERROR:
                    field.set(obj, null);
                    break;
                case BOOLEAN:
                    Boolean v1 = cell.getBooleanCellValue();
                    if (field.getType() == String.class) {
                        field.set(obj, v1.toString());
                    } else {
                        field.set(obj, v1);
                    }
                    break;
                case FORMULA:
                    // TODO::공식일 때 개발하기
                    break;
                case NUMERIC:
                    Double v2 = cell.getNumericCellValue();
                    if (field.getType() == String.class) {
                        field.set(obj, v2.toString());
                    } else if (field.getType() == Integer.class) {
                        field.set(obj, v2.intValue());
                    } else {
                        field.set(obj, v2);
                    }
                    break;
                case STRING:
                    String v3 = cell.getStringCellValue();
                    field.set(obj, v3);
                    break;
            }

            if (header.isKey() && field.get(obj) == null)
                throw new SystemException(String.format("%s은(는) 빈 값일 수 없습니다.", header.getLabel()));

            // Restore original
            field.setAccessible(acc);
        }
    }

    public static void writeRow(Row row, List<ExcelHeader> headers, Object obj, Class<?> clzz)
            throws Exception {
        for (ExcelHeader header : headers) {
            Field field = clzz.getDeclaredField(header.getField());
            boolean acc = field.isAccessible();

            Cell cell = row.getCell(header.getColumnIndex());

            Class<?> fz = field.getClass();

            try {
                if (fz == String.class) {
                    cell.setCellValue(field.get(obj).toString());
                } else if (fz == Integer.class || fz == Double.class) {
                    String value = field.get(obj).toString();
                    cell.setCellValue(new Double(value));
                }
            } catch (Exception e) {

            }

            // Restore original
            field.setAccessible(acc);
        }
    }
    public static void setString(final XSSFRow row, final int columnIndex, final String value) {
        try {
            XSSFCell cell = getCell(row, columnIndex);

            if (StringUtils.hasText(value)) {
                cell.setCellValue(value);
            }
        } catch (Exception e) {
            // Hello...we got error here...
        }
    }

}
