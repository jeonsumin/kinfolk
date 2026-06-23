package com.terry.backend.core.security.util;

import com.terry.backend.core.messages.util.MessageSourceUtils;
import com.terry.backend.core.security.dto.UserDTO;
import com.terry.backend.thirdparty.JWT.provider.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component // Spring Bean으로 등록하여 의존성 주입이 가능하게 함
public class SessionUtils {

    private static final String[] SYSTEM_IDS = new String[]{"SYSTEM"};
    private static final Map<String, String> USER_CACHE_MAP = new LinkedHashMap<>(); // Diamond Operator 사용

    private static TokenProvider tokenProviderInstance; // static 인스턴스를 통해 접근
    // Spring이 이 클래스를 빈으로 만들 때 TokenProvider를 주입할 수 있도록 생성자 주입 사용
    public SessionUtils(TokenProvider tokenProvider) {
        SessionUtils.tokenProviderInstance = tokenProvider;
    }

    private static final String[] IP_HEADER_CANDIDATES = {"X-Forwarded-For", "Proxy-Client-IP",
            "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"};


    public static HttpServletRequest getCurrentRequest() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes) {
                return ((ServletRequestAttributes) requestAttributes).getRequest();
            }
        } catch (IllegalStateException e) {
            log.warn("Cannot get current HttpServletRequest outside of a request scope.");
        }
        return null;
    }

    /**
     * Authorization 헤더에서 토큰을 추출합니다.
     * @return 토큰 문자열 또는 null (토큰이 없거나 형식이 잘못된 경우)
     */
    public static String getToken() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            log.warn("HttpServletRequest is null, cannot get token.");
            return null;
        }
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7); // "Bearer " 다음부터 추출
        }
        return null;
    }

    /**
     * 토큰에서 사용자 이름을 추출합니다.
     * @return 사용자 이름 또는 "anonymousUser"
     */
    public static String getUsername() {
        String token = getToken();
        if (token == null || tokenProviderInstance == null) { // tokenProviderInstance null 체크 추가
            return "anonymousUser";
        }
        try {
            return tokenProviderInstance.getUsername(token);
        } catch (Exception e) {
            log.error("Error getting username from token: {}", e.getMessage());
            return "anonymousUser";
        }
    }

    /**
     * 토큰에서 사용자 ID를 추출합니다.
     * @return 사용자 ID 또는 null (토큰이 유효하지 않거나 tokenProvider가 null인 경우)
     */
    public static String getUserId() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.info("Principal is : {}", principal);
            if (principal instanceof String) {
                return (String) principal;
            } else if (principal instanceof UserDTO) {
                return ((UserDTO) principal).getId();
            }
        } catch (Exception e) {
        }
        return "anonymousUser";
    }

    /**
     * 클라이언트의 실제 IP 주소를 가져옵니다.
     * @return 클라이언트 IP 주소 또는 "0.0.0.0" (요청 정보가 없을 경우)
     */
    public static String getRemoteAddr() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return "0.0.0.0";
        }
        return getRemoteAddr(request); // 오버로드된 메서드 호출
    }

    /**
     * HttpServletRequest에서 클라이언트의 실제 IP 주소를 가져옵니다.
     * @param request HttpServletRequest 객체
     * @return 클라이언트 IP 주소
     */
    public static String getRemoteAddr(HttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ipList = request.getHeader(header);
            if (StringUtils.hasText(ipList) && !"unknown".equalsIgnoreCase(ipList)) {
                String ip = ipList.split(",")[0].trim(); // 공백 제거
                // 포트 번호 제거 및 유효한 IPv4 형식만 반환 (정규식 수정)
                if (ip.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}(:\\d+)?$")) {
                    return ip.replaceAll("^(.*?)(:\\d+)?$", "$1");
                }
            }
        }
        return request.getRemoteAddr();
    }

    /**
     * ID에 해당하는 메타 사용자 이름을 가져옵니다. (주석 처리된 securityService 부분은 외부에서 주입받아 사용해야 함)
     * @param id 사용자 ID
     * @return 메타 사용자 이름
     */
    public static String getMetaUsername(String id) {
        if (!StringUtils.hasText(id)) { // null 또는 빈 문자열 체크
            return "";
        }

        for (String s_id : SYSTEM_IDS) {
            if (s_id.equalsIgnoreCase(id)) {
                return MessageSourceUtils.getMessage(s_id);
            }
        }

        // USER_CACHE_MAP에서 조회
        String cachedName = USER_CACHE_MAP.get(id);
        if (cachedName != null) {
            return cachedName;
        }

        // 캐시에 없는 경우, 실제 사용자 정보를 조회하는 로직 필요
        // 현재는 주석 처리되어 있으므로 "손님" 반환
        // UserDTO dto = securityService.findByUserId(id);
        // if (dto != null) {
        //     String name = dto.getUsername();
        //     if (!StringUtils.hasText(name)) {
        //         name = dto.getLoginId();
        //     }
        //     String convertedName = convertName(name);
        //     USER_CACHE_MAP.put(id, convertedName);
        //     return convertedName;
        // }
        return "손님";
    }

    /**
     * 사용자 캐시 맵을 업데이트합니다.
     * @param id 사용자 ID
     * @param name 사용자 이름 (실제 이름)
     * @param loginId 사용자 로그인 ID
     */
    public static void updateUserCacheMap(String id, String name, String loginId) {
        String effectiveName = StringUtils.hasText(name) ? name : loginId;
        USER_CACHE_MAP.put(id, convertName(effectiveName));
    }

    /**
     * 이름을 특정 형식으로 변환합니다. (예: 뒷부분을 *로 마스킹)
     * @param value 원본 이름
     * @return 변환된 이름
     */
    private static String convertName(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        // 예: "홍길동" -> "홍길*" 또는 "abcde" -> "ab***"
        String prefix = value.replaceAll("(.+?)(.{0,3})$", "$1"); // 마지막 3글자 제외한 앞부분
        String appendix = value.replaceAll("(.+?)(.{0,3})$", "$2"); // 마지막 3글자

        // appendix가 3글자 미만일 경우 전체를 마스킹하지 않고 0~3글자 범위 내에서 마스킹
        if (!appendix.isEmpty()) {
            return prefix + String.join("", Collections.nCopies(appendix.length(), "*"));
        } else {
            return prefix; // 뒷부분이 없으면 마스킹할 것도 없음
        }
    }
}
