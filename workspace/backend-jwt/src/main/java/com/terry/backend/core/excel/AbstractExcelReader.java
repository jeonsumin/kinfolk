package com.terry.backend.core.excel;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;

public abstract class AbstractExcelReader implements ExcelReader {

    private static final int EXCEL_ROW_CACHE_SIZE = 100;
    private static final int EXCEL_BUFFER_SIZE    = 4096;

    private InputStream inputStream     = null;
    private Workbook    workbook        = null;
    private String      remoteIpAddress = null;


    @Override
    public void beforeRead() throws Exception {

    }

    @Override
    public void afterRead() throws Exception {

    }

    @Override
    public void openWorkbook(InputStream inputStream) throws Exception {
        this.inputStream = inputStream;
        workbook = StreamingReader
                .builder()
                .rowCacheSize(EXCEL_ROW_CACHE_SIZE)
                .bufferSize(EXCEL_BUFFER_SIZE)
                .open(inputStream);
    }

    @Override
    public Workbook getWorkbook() {
        return workbook;
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
    public String getRemoteAddr() {
        return remoteIpAddress;
    }

    @Override
    public void setRemoteAddr(String remoteAddr) {
        this.remoteIpAddress = remoteAddr;
    }


}
