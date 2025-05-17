package bp.difference.handler;

import bp.wf.httphandler.WF_Comm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/WF/Export")
@ResponseBody
public class ExcelExportController extends HttpHandlerBase{
    @Override
    public Class getCtrlType() {
        return null;
    }
    @RequestMapping(value = "/Search_Exp")
    public final void Search_Exp(HttpServletRequest request, String frmID, String type)throws Exception{
        //实体、单据导出
        if(type.equals("bill")==true || type.equals("dict")==true){
            bp.ccbill.WF_CCBill ccbill = new bp.ccbill.WF_CCBill();
            ccbill.Search_Exp();
            return;
        }
        if(type.equals("search")==true || type.equals("searchDtl")==true){
            WF_Comm comm = new WF_Comm();
            //通用查询页面导出
            if(type.equals("search")==true)
                comm.Search_Exp();
            //从表导出
            if(type.equals("searchDtl")==true)
                comm.SearchDtl_Exp();
            return;
        }

        //
        if(type.equals("flowSearch")==true || type.equals("flowGroupDtl")==true){
            bp.wf.httphandler.WF_RptDfine rptDfine = new bp.wf.httphandler.WF_RptDfine();
            if(type.equals("flowSearch")==true)
                rptDfine.FlowSearch_Exp();
            if(type.equals("flowGroupDtl")==true)
                rptDfine.FlowGroupDtl_Exp();
        }

    }

}
