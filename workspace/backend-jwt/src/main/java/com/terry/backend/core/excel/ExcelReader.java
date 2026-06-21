package com.terry.backend.core.excel;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;

public interface ExcelReader {

    /**
     * 엑셀 파일 읽기 전 실행 메소드
     *
     * @throws Exception 실행 실패
     */
    void beforeRead() throws Exception;

    /**
     * 엑셀 파일 읽기 후 실행 메소드
     *
     * @throws Exception 실행 실패
     */
    void afterRead() throws Exception;

    /**
     * 엑셀 읽기
     *
     * @throws Exception 읽기 실패
     */
    void read() throws Exception;

    /**
     * 엑셀 파일 열기
     *
     * @param  inputStream FileInputStream
     * @throws Exception   열기 실패
     */
    void openWorkbook(InputStream inputStream) throws Exception;

    /**
     * 엑셀 파일을 받아온다
     *
     * @return 엑셀 워크북
     */
    Workbook getWorkbook();

    /**
     * 엑셀 파일을 닫는다
     */
    void closeWorkbook();

    /**
     * 원격 IP 주소를 받아온다
     *
     * @return IP 주소
     */
    String getRemoteAddr();

    /**
     * 원격 IP 주소를 설정한다
     *
     * @param remoteAddr IP 주소
     */
    void setRemoteAddr(String remoteAddr);

}
