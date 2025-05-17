package bp.port.dingtalk.ddsdk;

/**
 * 签名包
 */
public class SignPackage {
    private String agentId;

    public final String getAgentId() {
        return agentId;
    }

    public final void setAgentId(String value) {
        agentId = value;
    }

    private String corpId;

    public final String getCorpId() {
        return corpId;
    }

    public final void setCorpId(String value) {
        corpId = value;
    }

    private String timeStamp;

    public final String getTimeStamp() {
        return timeStamp;
    }

    public final void setTimeStamp(String value) {
        timeStamp = value;
    }

    private String nonceStr;

    public final String getNonceStr() {
        return nonceStr;
    }

    public final void setNonceStr(String value) {
        nonceStr = value;
    }

    private String signature;

    public final String getSignature() {
        return signature;
    }

    public final void setSignature(String value) {
        signature = value;
    }

    private String url;

    public final String getUrl() {
        return url;
    }

    public final void setUrl(String value) {
        url = value;
    }

    private String rawstring;

    public final String getRawstring() {
        return rawstring;
    }

    public final void setRawstring(String value) {
        rawstring = value;
    }

    private String jsticket;

    public final String getJsticket() {
        return jsticket;
    }

    public final void setJsticket(String value) {
        jsticket = value;
    }
}
