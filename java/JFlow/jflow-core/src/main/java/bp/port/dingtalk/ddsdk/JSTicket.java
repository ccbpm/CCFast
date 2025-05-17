package bp.port.dingtalk.ddsdk;

/**
 * JSAPI时用的票据
 */
public class JSTicket extends ResultPackage {
    private String ticket;

    public final String getTicket() {
        return ticket;
    }

    public final void setTicket(String value) {
        ticket = value;
    }

    private int expires_in;

    public final int getExpiresIn() {
        return expires_in;
    }

    public final void setExpiresIn(int value) {
        expires_in = value;
    }
}
