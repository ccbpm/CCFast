package bp.port.dingtalk.dts;

import bp.en.*;
import bp.port.dingtalk.*;

/**
 * 钉钉组织结构同步
 */
public class OrgInit_Ding extends Method {
    /**
     * 钉钉组织结构同步
     */
    public OrgInit_Ding() {
        this.Title = "同步钉钉通讯录到CCGPM";
        this.Help = "本功能将首先<b style='color:red;'>清空组织结构</b>，然后同步钉钉通讯录。<br> 钉钉相关配置写入jflow.properties，配置正确才可以被执行";
    }

    /**
     * 设置执行变量
     *
     * @return
     */
    @Override
    public void Init() {
    }

    /**
     * 当前的操纵员是否可以执行这个方法
     */
    @Override
    public boolean getIsCanDo() {
        if (bp.wf.Glo.isEnableDingDing() == true) {
            return true;
        }
        return false;
    }

    /**
     * 执行
     *
     * @return 返回执行结果
     */
    @Override
    public Object Do() {
        DingDing ding = new DingDing();
        boolean result = ding.AnsyOrgToCCGPM();
        if (result == true) {
            return "执行成功...";
        } else {
            return "执行失败...";
        }
    }
}
