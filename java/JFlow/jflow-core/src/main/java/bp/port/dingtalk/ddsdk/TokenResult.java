package bp.port.dingtalk.ddsdk;

public class TokenResult extends ResultPackage {
    private String AccessToken;

    public final String getAccessToken() {
        return AccessToken;
    }

    public final void setAccessToken(String value) {
        AccessToken = value;
    }
}
