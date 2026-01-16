package logic;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DiscordWebhook {

    private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/1451057706835378344/wzeFzpek6mZyns6Kuxm3SRq2WEsjFaTvrZGAGHmHN_k50RqGlXQuLC7MI9HAHQc9NTJM"; 

    public static boolean guiThongBao(String tieuDe, String noiDung) {
        if (WEBHOOK_URL.contains("...")) {
            System.err.println("Chưa cấu hình URL Webhook!");
            return false; 
        }

        HttpURLConnection conn = null;

        try {
            if (tieuDe == null) tieuDe = "Thông báo hệ thống";
            if (noiDung == null) noiDung = "Không có nội dung";
            
            String safeTieuDe = escapeJson(tieuDe);
            String safeNoiDung = escapeJson(noiDung);

            String jsonPayload = "{\"content\": \"🚨 **BÁO LỖI HỆ THỐNG**\\n**Tiêu đề:** " + safeTieuDe + "\\n**Chi tiết:** " + safeNoiDung + "\"}";

            URL url = new URL(WEBHOOK_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", "Java-Webhook-Bot");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            
            if (responseCode >= 200 && responseCode < 300) {
                return true; 
            } else {
                System.err.println("Gửi thất bại. Mã lỗi: " + responseCode);
                return false; 
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String escapeJson(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "");
    }
}	