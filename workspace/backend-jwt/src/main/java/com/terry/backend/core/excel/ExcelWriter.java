package com.terry.backend.core.excel;

import com.terry.backend.core.dto.BaseSearchParam;
import com.terry.backend.core.excel.config.ExcelConfig;
import com.terry.backend.core.excel.mapper.ExcelMapper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public interface ExcelWriter<P extends BaseSearchParam> {

    /**
     * 엑셀 파일 쓰기 전 실행 메소드
     *
     * @throws Exception 실행 오류
     */
    void beforeWrite() throws Exception;

    /**
     * 엑셀 파일 쓰기 후 실행 메소드
     *
     * @throws Exception 실행 오류
     */
    void afterWrite() throws Exception;

    /**
     * 엑셀 파일에 데이터를 씁니다.
     *
     * @param  param     파라미터
     * @throws Exception 엑셀 파일 쓰기 실패
     */
    void write(P param) throws Exception;

    /**
     * 엑셀 파일을 엽니다.
     *
     * @throws Exception 엑셀 파일을 여는데 실패함
     */
    void openWorkbook() throws Exception;

    /**
     * 열린 엑셀 파일을 가져온다
     *
     * @return 엑셀 워크북
     */
    XSSFWorkbook getWorkbook();

    /**
     * 엑셀 파일을 다운로드 위치에 저장합니다.
     *
     * @return           저장된 파일 위치
     * @throws Exception 저장 실패
     */
    String saveWorkbook() throws Exception;

    /**
     * 엑셀 파일을 닫습니다.
     */
    void closeWorkbook();

    /**
     * 엑셀 파일 위치(명)을 가져옵니다.
     *
     * @return 엑셀 파일 위치/명
     */
    String getFilename();

    /**
     * 엑셀 파일의 다운로드 명을 가져옵니다
     *
     * @return 엑셀 파일 다운로드 명
     */
    String getExportName();

    void setExcelConfig(ExcelConfig config);

    void setExcelMapper(ExcelMapper excelMapper);

}
