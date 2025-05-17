package bp.difference.handler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bp.da.DataType;
import bp.difference.ContextHolderUtils;
import bp.tools.StringHelper;

public abstract class HttpHandlerBase
{
	/**
	 * 获取 "Handler业务处理类"的Type
	 * 注意： "Handler业务处理类"必须继承自bp.wf.httphandler.WebContralBase</p>
	*/
	public abstract java.lang.Class getCtrlType();

	public final boolean getIsReusable()throws Exception
	{
		return false;
	}

	public void ProcessRequestPost(Object mycontext)
	{
		PrintWriter out =null;
		//创建 ctrl 对象.
		Object tempVar = mycontext;

		DirectoryPageBase ctrl = (DirectoryPageBase)((tempVar instanceof DirectoryPageBase) ? tempVar : null);


		String mName=ctrl.GetRequestVal("DoMethod");
		String msg="";
		if (mName==null || mName=="null")
			msg ="方法[" + ctrl.getDoType() + "]类[" + this.getCtrlType().toString() + "]";
		else
			msg ="方法[" + mName+ "]类[" + this.getCtrlType().toString() + "]，";

		bp.da.Log.DebugWriteInfo(msg);
	//	out.write(msg);


		try
		{
			//执行方法返回json.
			String data = ctrl.DoMethod(ctrl, ctrl.getDoType());
            if(DataType.IsNullOrEmpty(data))
                data="";
			//返回执行的结果.
			//ctrl.context.Response.Write(data);
			this.getResponse().setHeader("content-type", "text/html;charset=UTF-8");
			this.getResponse().setCharacterEncoding("UTF-8");
			out = this.getResponse().getWriter();
            /*if(SystemConfig.getIsDebug()==false && data.toUpperCase().indexOf("@SQL:")!=-1 && data.toUpperCase().indexOf("@SQL_")==-1){
                data = data.substring(0,data.toUpperCase().indexOf("@SQL"));
            }*/
			out.write(data);
		}
		catch (Exception ex)
		{

			String err=ex.getMessage();

			String paras="";

			paras+= ctrl.GetRequestVal("DoMethod");

			//返回执行错误的结果.
			if (ex.getCause() != null)
			{
				err+="err@在执行类[" + this.getCtrlType().toString() + "]，方法[" + ctrl.getDoType() + "]["+paras+"]错误 \t\n @" + ex.getCause().getMessage() + " \t\n @技术信息:" + ex.getStackTrace();
			}
			else
			{
				err+="err@在执行类[" + this.getCtrlType().toString() + "]，方法[" + ctrl.getDoType() + "]["+paras+"]错误 \t\n @" + ex.getMessage() + " \t\n @技术信息:" + ex.getStackTrace();
			}

			bp.da.Log.DebugWriteError(err);

			out.write(err);


		}finally{
			if(null !=out){
				out.close();
			}
		}
	}

	// BaseController

    public HttpServletRequest getRequest() {
        return ContextHolderUtils.getRequest();
    }

    public HttpServletResponse getResponse() {
        return ContextHolderUtils.getResponse();
    }

    public String getParamter(String key){
        return getRequest().getParameter(key);
    }

    /**
     * 增加列的数量。
     */
    public final int getaddRowNum() {
        try {
            int i = Integer.parseInt(ContextHolderUtils.getRequest()
                    .getParameter("addRowNum"));
            if (ContextHolderUtils.getRequest().getParameter("IsCut") == null) {
                return i;
            } else {
                return i;
            }
        } catch (java.lang.Exception e) {
            return 0;
        }
    }

    public final int getIsWap() {
        if(ContextHolderUtils.getRequest().getParameter("IsWap")==null)
            return 0;
        if (ContextHolderUtils.getRequest().getParameter("IsWap").equals("1")) {
            return 1;
        }
        return 0;
    }

    public String getRefPKVal() {
        String str = ContextHolderUtils.getRequest().getParameter("RefPKVal");
        if (str == null) {
            return "1";
        }
        return str;
    }
    public final String getSta() {
        String str = ContextHolderUtils.getRequest().getParameter("Sta");

        return str;
    }
    public int getPageIdx() {
        String str = ContextHolderUtils.getRequest().getParameter("PageIdx");
        if (str == null || str.equals("") || str.equals("null"))
            return 1;
        return Integer.parseInt(str);
        // set
        // {
        // ViewState["PageIdx",value;
        // }
    }

    public int getPageSize() {
        String str = ContextHolderUtils.getRequest().getParameter("PageSize");
        if (str == null || str.equals("") || str.equals("null"))
            return 10;
        return Integer.parseInt(str);
        // set
        // {
        // ViewState["PageIdx",value;
        // }
    }
    public String getFK_MapExt() {
        return ContextHolderUtils.getRequest().getParameter("FK_MapExt");
    }

    public String getFK_MapDtl(){
        return ContextHolderUtils.getRequest().getParameter("FK_MapDtl");
    }

    public final String getKey() {
        return ContextHolderUtils.getRequest().getParameter("Key");
    }

    public final String getActionType()
    {
        String s = ContextHolderUtils.getRequest().getParameter("ActionType");
        if (s == null)
        {
            s = ContextHolderUtils.getRequest().getParameter("DoType");
        }

        return s;
    }

    public String GetRequestVal(String key)
    {
        return ContextHolderUtils.getRequest().getParameter(key);
    }

    public final Long getOID()
    {
        String str = this.GetRequestVal("RefOID"); // context.Request.QueryString["RefOID");
        if (DataType.IsNullOrEmpty(str) == true)
            str = this.GetRequestVal("OID");  //context.Request.QueryString["OID");

        if (DataType.IsNullOrEmpty(str) == true)
            str="0";

        return Long.parseLong(str);

    }
    public String getFK_Flow()
    {
        return GetRequestVal("FK_Flow");
    }
    public String getEnName()
    {
        return GetRequestVal("EnName");
    }
    public String getRefNo()
    {
        return GetRequestVal("RefNo");
    }
    public String getEnsName()
    {
        return GetRequestVal("EnsName");
    }
    public final String getFK_Emp()
    {
        return GetRequestVal("FK_Emp");
    }
    public final String getPageID()
    {
        return GetRequestVal("PageID");
    }
    public long getWorkID()
    {
        return Long.parseLong(StringHelper.isEmpty(getParamter("WorkID"), "0"));
    }
    public boolean getIsCC()
    {
        String s = ContextHolderUtils.getRequest().getParameter("Paras");
        if (s == null)
        {
            return false;
        }

        if (s.contains("IsCC"))
        {
            return true;
        }
        return false;
    }
    public String getDoType()
    {
        return ContextHolderUtils.getRequest().getParameter("DoType");
    }
    public final int getallRowCount() {
        int i = 0;
        try {
            i = Integer.parseInt(ContextHolderUtils.getRequest().getParameter(
                    "rowCount"));
        } catch (java.lang.Exception e) {
            return 0;
        }
        return i;
    }
    public final String getTB_Doc()
    {
        return GetRequestVal("TB_Doc");
    }

    public String getSID()
    {
        return GetRequestVal("SID");
    }
    public int getRefOID()
    {
        String s = GetRequestVal("RefOID");
        if (s == null || s.equals("")||s.equals("null"))
            s = GetRequestVal("OID");
        if (s == null || s.equals("")||s.equals("null"))
            return 0;
        return Integer.valueOf(s);
    }
    //public String FK_Node;
    //public String FID;
    //public String WorkID;
    //public String FK_Flow;
    //public String MyPK;



    public String getMyPK() {
        String s = GetRequestVal("MyPK");
        return s;
    }

//	public void setMyPK(String myPK) {
//		MyPK = myPK;
//	}

//	@PostConstruct
//	public void init(){
//		HttpServletRequest request = ContextHolderUtils.getRequest();
//		//setNodeID(request.getParameter("FK_Node"));
//		//setFID(request.getParameter("FID"));
//		//setWorkID(request.getParameter("WorkID"));
//		//setFK_Flow(request.getParameter("FK_Flow"));
//		//setMyPK(request.getParameter("MyPK"));
//	}


    public int getFK_Node() {
        String str = ContextHolderUtils.getRequest().getParameter("FK_Node");
        if (str == null || str.equals("")||str.equals("null"))
            return 1;
        return Integer.parseInt(str);
    }


    public long getFID() {
        String str = ContextHolderUtils.getRequest().getParameter("FID");
        if (str == null || str.equals("")||str.equals("null"))
            return 0;
        return Long.valueOf(str);
    }

}
