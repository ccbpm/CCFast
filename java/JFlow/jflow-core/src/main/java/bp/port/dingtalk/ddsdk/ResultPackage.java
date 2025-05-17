package bp.port.dingtalk.ddsdk;

public class ResultPackage {
    /**
     * 错误码
     */
    private ErrCodeEnum ErrCode = ErrCodeEnum.values()[0];

    public final ErrCodeEnum getErrCode() {
        return ErrCode;
    }

    public final void setErrCode(ErrCodeEnum value) {
        ErrCode = value;
    }

    /**
     * 错误消息
     */
    private String ErrMsg;

    public final String getErrMsg() {
        return ErrMsg;
    }

    public final void setErrMsg(String value) {
        ErrMsg = value;
    }

    /**
     * 结果的json形式
     */
    private String Json;

    public final String getJson() {
        return Json;
    }

    public final void setJson(String value) {
        Json = value;
    }


    ///#region IsOK Function
    public final boolean ItIsOK() {
        return getErrCode() == ErrCodeEnum.OK;
    }
    ///#endregion

    ///#region ToString
    @Override
    public String toString() {
        String info = "{\"ErrCode\":" + this.getErrCode() + ",\"ErrMsg\":" + this.getErrMsg() + "}";
        return info;
    }
    ///#endregion
}
