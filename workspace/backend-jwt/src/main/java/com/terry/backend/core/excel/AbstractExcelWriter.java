package com.terry.backend.core.excel;

import com.terry.backend.core.dto.BaseSearchParam;
import com.terry.backend.core.excel.config.ExcelConfig;
import com.terry.backend.core.excel.dto.ExcelDTO;
import com.terry.backend.core.excel.mapper.ExcelMapper;
import com.terry.backend.core.serial.config.AbstractStringSerialConfiguration;
import com.terry.backend.core.serial.config.SerialConfiguration;
import com.terry.backend.core.serial.util.SerialUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

@Slf4j
public abstract class AbstractExcelWriter<P extends BaseSearchParam> implements ExcelWriter<P>{
    private static final SerialConfiguration<String> ID_STRATEGY = new AbstractStringSerialConfiguration() {

        @Override
        public String getFormatString() {
            return "EX%s%%04d";
        }

        @Override
        public String getDateFormat() {
            return "yyyyMMddHHmm";
        }

        @Override
        public String getCompareDateFormat() {
            return "yyyyMMddHHmm";
        }

    };

    private ExcelConfig       config;
    private ExcelMapper       excelMapper;
    private InputStream       inputStream = null;
    private Workbook          workbook    = null;

    @Override
    public void beforeWrite() throws Exception {

    }

    @Override
    public void afterWrite() throws Exception {

    }


    @Override
    public void openWorkbook() throws Exception {

        closeWorkbook();

        String filename = config.getBasePath();

        filename = filename.replaceAll("\\\\", "/");
        if (!filename.endsWith("/")) {
            filename = filename + "/";
        }

        String filePath = getFilename();

        filePath = filePath.replaceAll("\\\\", "/");
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1, filePath.length());
        }

        filename = filename + filePath;

        if (log.isDebugEnabled())
            log.debug(String.format("Read excel file: %s", filename));

        /**
         * May be classpath resource
         */
        if (filename.matches("^classpath:.*")) {
            filename = filename.replace("classpath:", "");
            inputStream = new ClassPathResource(filename).getInputStream();
        }
        /**
         * May be a file
         */
        else {
            inputStream = new FileInputStream(filename);
        }

        /**
         * Open workbook
         */
        workbook = new XSSFWorkbook(inputStream);
    }

    @Override
    public XSSFWorkbook getWorkbook() {
        return (XSSFWorkbook) workbook;
    }

    @Override
    public String saveWorkbook() throws Exception {

        final String id = SerialUtil.<String>get("SWC_EXCEL_FILE", ID_STRATEGY);

        String base = config.getTemporaryPath();
        base.replaceAll("\\\\", "/");
        if (!base.endsWith("/")) {
            base = base + "/";
        }

        File dir = new File(base);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }

        String filepath = String.format("%s%s.xlsx", base, id);

        OutputStream os = new FileOutputStream(filepath);

        workbook.write(os);

        os.close();

        excelMapper
                .insert(ExcelDTO
                        .builder()
                        .fileId(id)
                        .fileName(String.format("%s.xlsx", getExportName()))
                        .fileSize(getFileSize(filepath))
                        .fileType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        .filePath(filepath)
                        .build());

        return id;
    }
    private Long getFileSize(String filepath) {
        File file = new File(filepath);
        return file.length();
    }

    @Override
    public void closeWorkbook() {
        try {
            inputStream.close();
        } catch (Exception e) {
        } finally {
            inputStream = null;
        }

        try {
            workbook.close();
        } catch (Exception e) {
        } finally {
            workbook = null;
        }
    }


    @Override
    public void setExcelConfig(ExcelConfig config) {
        this.config = config;
    }

    @Override
    public void setExcelMapper(ExcelMapper excelMapper) {
        this.excelMapper = excelMapper;
    }

}
