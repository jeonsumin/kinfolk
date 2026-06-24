package com.terry.backend.api.planner.service;

import com.terry.backend.api.planner.dto.PlacePreviewDTO;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PlacePreviewService {

    private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https");

    public PlacePreviewDTO resolvePreview(String url) {
        validateUrl(url);
        try {
            org.jsoup.Connection.Response response = org.jsoup.Jsoup.connect(url)
                    .followRedirects(true)
                    .timeout(5000)
                    .userAgent("Mozilla/5.0 (compatible; KinfolkBot/1.0)")
                    .execute();
            // SSRF: resolve final URL after redirects and check IP
            String finalUrl = response.url().toString();
            validateUrl(finalUrl);

            org.jsoup.nodes.Document doc = response.parse();
            String title = getOgOrFallback(doc, "og:title", "title");
            String description = getOgOrMeta(doc, "og:description", "description");
            String image = getOgContent(doc, "og:image");
            String category = getOgContent(doc, "og:type");

            return PlacePreviewDTO.builder()
                    .sourceUrl(url)
                    .thumbnailUrl(image != null ? image : "")
                    .title(title != null ? title : "")
                    .description(description != null ? description : "")
                    .category(category != null ? category : "장소")
                    .build();
        } catch (java.io.IOException e) {
            throw new IllegalArgumentException("URL에서 메타데이터를 가져올 수 없습니다: " + e.getMessage());
        }
    }

    private void validateUrl(String url) {
        try {
            java.net.URL parsed = new java.net.URL(url);
            if (!ALLOWED_SCHEMES.contains(parsed.getProtocol().toLowerCase())) {
                throw new IllegalArgumentException("허용되지 않는 URL 스킴입니다.");
            }
            // Resolve hostname to IP for SSRF check
            java.net.InetAddress address = java.net.InetAddress.getByName(parsed.getHost());
            if (isPrivateAddress(address)) {
                throw new IllegalArgumentException("내부 네트워크 URL은 허용되지 않습니다.");
            }
        } catch (java.net.MalformedURLException e) {
            throw new IllegalArgumentException("잘못된 URL 형식입니다.");
        } catch (java.net.UnknownHostException e) {
            throw new IllegalArgumentException("URL 호스트를 확인할 수 없습니다.");
        }
    }

    private boolean isPrivateAddress(java.net.InetAddress address) {
        return address.isLoopbackAddress()
                || address.isSiteLocalAddress()
                || address.isLinkLocalAddress()
                || address.isAnyLocalAddress();
    }

    private String getOgContent(org.jsoup.nodes.Document doc, String property) {
        org.jsoup.nodes.Element el = doc.selectFirst("meta[property=" + property + "]");
        return el != null ? el.attr("content") : null;
    }

    private String getOgOrFallback(org.jsoup.nodes.Document doc, String ogProp, String fallbackTag) {
        String og = getOgContent(doc, ogProp);
        if (og != null && !og.isBlank()) return og;
        org.jsoup.nodes.Element el = doc.selectFirst(fallbackTag);
        return el != null ? el.text() : null;
    }

    private String getOgOrMeta(org.jsoup.nodes.Document doc, String ogProp, String metaName) {
        String og = getOgContent(doc, ogProp);
        if (og != null && !og.isBlank()) return og;
        org.jsoup.nodes.Element el = doc.selectFirst("meta[name=" + metaName + "]");
        return el != null ? el.attr("content") : null;
    }
}
