var I=Object.defineProperty;var p=(F,d,e)=>d in F?I(F,d,{enumerable:!0,configurable:!0,writable:!0,value:e}):F[d]=e;var o=(F,d,e)=>p(F,typeof d!="symbol"?d+"":d,e);var C=(F,d,e)=>new Promise((c,n)=>{var w=r=>{try{t(e.next(r))}catch(l){n(l)}},R=r=>{try{t(e.throw(r))}catch(l){n(l)}},t=r=>r.done?c(r.value):Promise.resolve(r.value).then(w,R);t((e=e.apply(F,d)).next())});import{b5 as A,aM as f,G as h,l as B,B as T,H as S}from"./entry/index-C6uBgOW5-1730430676707.js";import{GroupMethods as x,GroupMethodAttr as G}from"./GroupMethod-CUWbZDLV.js";import{Method as E}from"./Method-CoRs5kcO.js";import{D as P}from"./DBAccess-sLO0RM-h.js";import{GloComm as N}from"./GloComm-CmAl8MpM.js";import{MapData as _}from"./MapData-Ccsy8tbB.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./PCenter-g1qHGjKQ.js";import"./PowerCenter-xD33lI2C.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./FrmTrack-BAfWiAdt.js";import"./EnumLab-CzismWql.js";class i{}o(i,"Link","Link"),o(i,"Func","Func"),o(i,"Bill","Bill"),o(i,"FrmBBS","FrmBBS"),o(i,"DataVer","DataVer"),o(i,"DictLog","DictLog"),o(i,"QRCode","QRCode"),o(i,"DBList","DBList"),o(i,"Toolbar","Toolbar"),o(i,"ImpFromFile","ImpFromFile"),o(i,"PrintRTF","PrintRTF"),o(i,"PrintHtml","PrintHtml"),o(i,"PrintPDF","PrintPDF"),o(i,"PrintZip","PrintZip"),o(i,"FlowBaseData","FlowBaseData"),o(i,"FlowEtc","FlowEtc"),o(i,"FlowSingle","FlowSingle"),o(i,"FlowNewEntity","FlowNewEntity"),o(i,"SingleDictGenerWorkFlows","SingleDictGenerWorkFlows");class $ extends A{constructor(){super("GPN_Method");o(this,"SingleDictGenerWorkFlows",`
  #### 帮助
  - 一个实体发起的所有流程
  - 比如：在一个学生身上发起的，请假流程、入党申请流程、基本资料变更流程.
  - 比如：在一个固定资产身发起的：领用流程、维修流程、折旧流程、移交流程.
  - 所有的流程都组合一个表显示出来
  `);o(this,"Desc100","暂未开放");o(this,"DocSelfUrl",`
 
 自定义URL菜单， 您可以使用右上角的下拉框选择自己要定义的菜单类型.
  #### 帮助
   - 菜单连接： http://ccbpm.cn/MyUrl.htm  
   - 菜单连接： http://ccbpm.cn/MyUrl.htm  
   - 链接： /WF/Comm/Search.htm?EnsName=TS.ZS.Projcets 查询
   - 链接： /WF/Comm/Group.htm?EnsName=TS.ZS.Projcets  分析
   - 链接： /WF/MyFlow.htm?FK_Flow=001 发起指定的流程. 
  #### 帮助
   -  可以使用相对路径，也可以使用绝对路径。
   -  用户输入的Url:  http://ccbpm.cn/MyUrl.htm
   -  打开的Url : http://ccbpm.cn/MyUrl.htm?UserNo=xxxx&Token=xxxx。
   -  SID就类似于token, UserNo就是当前登录用户的编号。
   -  <img src="SelfUrl.png" class="HelpImg" />
</fieldset>

  `);o(this,"Docs0",`
  
  #### 帮助
   - 用于解决不能实现的对实体的操作个性化较强的功能。
   - 比如：您输入的url为外部链接: http://ccbpm.cn/MyUrl.htm
   - 比如：您需要打开系统内部的一个vue文件: /src/WF/Comm/En.vue 这个文件必须位于项目内部
   - 系统将解析为: http://ccbpm.cn/MyUrl.htm?WorkID=xxxx&FrmID=xxxx&UserNo=xxxx&Token=xxxx
   - 该链接显示在查询工具栏上。
  #### 效果图
  ![输入图片说明](/resource/CCFast/CCBill/Method/Img/Link.png "屏幕截图.png")      
        `);o(this,"Docs1",`
  #### 帮助
  - 对一个实体记录，执行相关的操作。
  - 执行一段SQL, Javascript, Url, 类。
  #### 无参数的方法效果
  ![输入图片说明](/resource/CCFast/CCBill/Method/Img/Func.png "屏幕截图.png")    
  #### 有参数的方法效果
  ![输入图片说明](/resource/CCFast/CCBill/Method/Img/Func2.png "屏幕截图.png")    
        `);o(this,"Docs2",`
  
  #### 帮助
   - 单据就是依赖与实体存在流水性质的记账凭证。
   - 比如：出门证、介绍信、证明函。
   - 比如：出库单、入库单。
   - 创建单据后，不需要审批，或者简单的审批就可以设置为入库状态的数据。
    
  `);o(this,"Docs3",`
   
  #### 帮助
  - 一个实体里只有一个该组件菜单
  - 应用场景：填写客户跟踪信息、实体跟踪记录、实体留言记录、多个人对一个实体的操作记录。
  #### 效果图
  ![输入图片说明](/resource/CCFast/CCBill/Method/Img/FrmBBS.png "屏幕截图.png")     
  `);o(this,"Docs4",`
   
  #### 帮助
  - 类似与数据库的备份
  - 可以在一定的事件对当前的实体进行数据备份，可以恢复到数据到指定的数据备份。
  #### 效果图
  ![输入图片说明](/resource/CCFast/CCBill/Method/Img/DataVer.png "屏幕截图.png") 
    
  `);o(this,"Docs5",`
   
  #### 帮助
  - 操作日志，留存操作痕迹。
  #### 效果图
  ![输入图片说明](/resource/CCFast/CCBill/Method/Img/DictLog.png "屏幕截图.png") 
    
  `);o(this,"Docs6",`
   
  #### 帮助
  - 该二维码是一个用于数据扫描查看的二维码。
  -  用户扫一扫就可以在手机上查看该表单的信息。
  -  如果您需要填报二维码，请在菜单新建【表单填报二维码】。
  #### 效果图
  ![输入图片说明](/resource/CCFast/CCBill/Method/Img/QRCode.png "屏幕截图.png") 
    
  `);o(this,"Docs7",`
   
  #### 帮助
  - 第工具栏按钮权限属于实体组件的一部分。
  - 按照节点属性的设置习惯，我们把其放入了实体属性里设置。
  #### 运行图
  ![输入图片说明](/resource/CCFast/CCBill/Method/Img/ToolbarRuning.png "屏幕截图.png") 
  #### 效果图
  ![输入图片说明](/resource/CCFast/CCBill/Method/Img/ToolbarSetting.png "屏幕截图.png")    
  `);o(this,"Docs8",`
  #### 帮助
  -
  `);o(this,"Docs9",`
  #### 帮助
  `);o(this,"Docs10",`
  #### 帮助
  `);o(this,"Docs11",`
   
  #### 帮助
  - 比如:基础资料变更、法人变更、企业变更、状态变更。
  - 就是对当前按选择一行（一个实体的基础数据变更)。
  - 流程结束后，系统就会把这些字段同步到实体中去。
  - <a href=https://www.bilibili.com/video/BV12P4y1p74h/>流程与实体的关系</a>
  #### 开发说明
  - 点击确定后，系统自动创建一个流程，并且开流程为极简模式。
  - 此流程的表单，是从当前实体表单中复制而来的。
  - 您可以根据自己的需要新增与删除字段。
  - 可以在方法属性里，设置数据同步方式与同步内容。
  #### 效果图
  ![输入图片说明](/resource/CCFast/CCBill/Method/Img/FlowBaseData.png "屏幕截图.png")    
  `);o(this,"Docs12",`
   
  #### 帮助
  - 比如：物业费缴纳、维修流程、派车流程、处罚流程、嘉奖流程、三好学生评定。
  - 流程运行完毕后，就作为业务查询数据。
  #### 开发说明
  - 点击确定后，系统自动创建一个流程，并且开流程为极简模式。
  - 此流程的表单，是从当前实体表单中复制而来的。
  - 您可以根据自己的需要新增与删除字段。
  - <a href=https://www.bilibili.com/video/BV12P4y1p74h/>流程与实体的关系</a>
    
  `);o(this,"DocsFlowSingle",`
   
  #### 帮助
  - 在一个单据中，仅仅发起一次的流程，我们成为单次流程。
  - 比如：合同，发起审核流程，注销流程。
  - 创建单次流程系统为当前表单设置为如下字段,其中XXX是流程编号.
  - WFStateXXXX, 状态.
  - FlowStarterXXXX, 发起人名称.
  - FlowStartRDT, 发起日期.
  - FlowEndNodeIDXXX, 结束节点.
  - FlowEndNodeNameXXX, 节点名称.
  - WorkIDXXX, 工作ID.
  - 流程在运行过程中会更新这些字段.
  #### 1.数据引用模式
  - 创建的流程类型为绑定单表单流程，绑定的表单就是当前的表单.
  - 把当前的低代码开发的表单绑定到每个节点上.
  - 表单与节点的关系属性里设置, WhoIsPK 为 父流程节点ID或者单据OID.
   #### 2.数据复制模式
  - 创建的是极简模式的流程，开始节点表单的内容(字段)是从当前实体或者单据复制过去的.
  - 启动流程的时候，把当前的表单数据复制到开始节点表单上去,流程运转的数据与当前实体或者单据无关系.
  #### 开发说明
  - <a href=https://www.bilibili.com/video/BV12P4y1p74h/>流程与实体的关系</a>
  `);this.PageTitle="新建实体方法",this.ForEntityClassID="TS.CCBill.Method"}Init(){this.AddGroup("A","常规组件","icon-doc"),this.TextBox2_NameNo(i.Link,"自定义链接",this.Docs0,"","URL链接","链接名称","我的链接"),this.TextBox2_NameNo(i.Func,"方法",this.Docs1,"Func_","方法ID","方法名称","缴纳班费"),this.TextBox1_Name(i.FrmBBS,"BBS/评论/日志组件",this.Docs3,"名称","评论"),this.TextBox1_Name(i.DataVer,"数据快照",this.Docs4,"名称","数据快照"),this.TextBox1_Name(i.DictLog,"操作日志",this.Docs5,"名称","操作日志"),this.TextBox1_Name(i.QRCode,"二维码",this.Docs6,"名称","二维码"),this.TextBox1_Name(i.DBList,"数据列表",this.Docs6,"名称","数据列表"),this.TextBox1_Name(i.PrintRTF,"RTF模板打印",this.Docs7,"名称","RTF模板打印"),this.AddGroup("D","关联单据","icon-doc"),this.TextBox2_NameNo("NewBill","新建单据",this.HelpTodo,"Bill_","编号","名称","维修单");const e=this.RequestVal("FrmID"),c=`SELECT OID as No, Lab as Name FROM Sys_GroupField WHERE FrmID='${e}' AND CtrlID='' `,n=` SELECT MyPK AS No, Name, GroupID FROM Sys_MapAttr WHERE FK_MapData='${e}' 
    AND UIContralType <=4 AND KeyOfEn NOT IN ('OID','Rec','RDT','FID','Title','BillNo','BillState','FlowStarter',
    'FlowEmps',
    'FlowStartRDT','WFState','Emps')
    AND UIVisible=1 ORDER BY GroupID,Idx
    `;this.SelectItemsByGroupList("NewBill.SelectAttrs","选择字段",this.HelpUn,!0,c,n),this.SelectItemsByList("NewBill.SelectAttrs.Group","选择目录",this.HelpUn,!1,f.srcFrmTree),this.SelectItemsByGroupList("RefBill","关联单据",this.Desc100,!1,f.srcFrmTree,f.srcFrmListBill),this.SelectItemsByList("RefBill.DictID","选择关联字段ID",this.HelpUn,!1,this.RefBillAttrs),this.SelectItemsByList("RefBill.DictID.DictName","选择关联字段Name",this.HelpUn,!1,this.RefBillAttrs),this.AddGroup("C","流程类","icon-plane"),this.TextBox1_Name(i.FlowBaseData,"基础数据变更流程",this.Docs11,"流程名称","基础数据变更流程"),this.TextBox1_Name(i.FlowEtc,"多次业务流程",this.Docs12,"流程名称",""),this.TextBox1_Name("FlowSingleRefData","单次流程-引用数据模式",this.Docs12,"流程名称","单次流程-引用数据模式"),this.TextBox1_Name("FlowSingleCopyData","单次流程-复制数据模式",this.Docs12,"流程名称","单次流程-复制数据模式"),this.TextBox1_Name(i.SingleDictGenerWorkFlows,"实体流程汇总列表(综合流程列表)",this.SingleDictGenerWorkFlows,"流程名称","流程列表"),this.AddIcon("icon-link","Link"),this.AddIcon("icon-film","Func"),this.AddIcon("icon-bubbles","FrmBBS"),this.AddIcon("icon-docs","DataVer"),this.AddIcon("icon-film","DictLog"),this.AddIcon("icon-frame","QRCode"),this.AddIcon("icon-list","DBList"),this.AddIcon("icon-layers","PrintRTF"),this.AddIcon("icon-doc","NewBill"),this.AddIcon("icon-doc","RefBill"),this.AddIcon("icon-plane","FlowBaseData"),this.AddIcon("icon-plane","FlowEtc"),this.AddIcon("icon-plane","FlowSingle"),this.AddIcon("icon-plane","SingleDictGenerWorkFlows")}FlowFrmModel(){return C(this,null,function*(){return JSON.stringify([{No:"RefBill",Name:"实体表单引用模式"},{No:"DataCopy",Name:"实体数据复制模式"}])})}RefBillAttrs(){return C(this,null,function*(){const e=this.RequestVal("tb1","RefBill"),c=new S("BP.CCBill.WF_CCBill_Admin_Method");return c.AddPara("FrmID",e),yield c.DoMethodReturnString("GPN_Menthd_RefBill_BillAttrs")})}GenerSorts(){return C(this,null,function*(){const e=new x;return yield e.Retrieve(G.FrmID,this.PKVal,"Idx"),e})}Save_TextBox_X(e,c,n,w,R){return C(this,null,function*(){const t=new E;if(t.GroupID=c,t.GroupIDT=this.GetSortName(c),t.FrmID=this.PKVal,t.Icon=this.GetPageIcon(e),t.IsEnable=!0,t.Idx=100,t.Name=n,t.MethodModel=e,e===i.Link){t.Name=n,t.Docs=w,t.No=P.GenerGUID(),t.SetPara("EnName","TS.CCBill.MethodLink"),yield t.Insert();const r=N.UrlEn("TS.CCBill.MethodLink",t.No);return new h(B.GoToUrl,r)}if(e===i.Func){t.Name=n,t.Docs=w,t.No=P.GenerGUID(),t.SetPara("EnName","TS.CCBill.MethodFunc"),t.MethodID=w,yield t.Insert();const r=N.UrlEn("TS.CCBill.MethodFunc",t.No);return new h(B.GoToUrl,r)}if(e==="NewBill"){const r=new _;if(r.No=w,(yield r.RetrieveFromDBSources())!=0)return new h(B.Error,"表单ID["+n+"]已经存在.")}if(e=="RefBill.DictID.DictName"){const r=this.RequestVal("FrmID"),l=this.RequestVal("tb2","RefBill"),D=this.RequestVal("tb1","RefBill"),m=this.RequestVal("tb1","RefBill.DictID"),a=this.RequestVal("tb1","RefBill.DictID.DictName");t.Name=D,t.MethodModel="DictRefBill",t.No=r+"_"+l,t.Tag1=l,t.Tag2=D,t.SetPara("EnName","TS.CCBill.MethodDictRefBill"),t.SetPara("RefDictNo",m),t.SetPara("RefDictName",a),t.MethodID="DictRefBill",yield t.Insert();const s=N.UrlEn("TS.CCBill.MethodDictRefBill",t.No);return new h(B.GoToUrl,s)}if(e=="NewBill.SelectAttrs.Group"){const r=this.RequestVal("FrmID"),l=this.RequestVal("tb2","NewBill"),D=this.RequestVal("tb1","NewBill"),m=this.RequestVal("tb3","NewBill"),a=this.RequestVal("tb1","NewBill.SelectAttrs.Group"),s=new S("BP.WF.HttpHandler.WF_Admin_CCFormDesigner");s.AddUrlData(),s.AddPara("FK_FrmSort",a),s.AddPara("TB_No",l),s.AddPara("TB_Name",D),s.AddPara("TB_PTable",m),s.AddPara("DDL_PTableModel",0),s.AddPara("EntityType",1),s.AddPara("SelectAttrs",this.RequestVal("tb1","NewBill.SelectAttrs")),s.AddPara("DictFrmID",r),yield s.DoMethodReturnString("NewFrmGuide_Create"),t.Name=D,t.MethodModel="DictRefBill",t.No=r+"_"+l,t.Tag1=l,t.Tag2=D,t.SetPara("EnName","TS.CCBill.MethodDictRefBill"),t.SetPara("RefDictNo",r+"No"),t.SetPara("RefDictName",r+"Name"),t.MethodID="DictRefBill",yield t.Insert();const u=N.UrlEn("TS.CCBill.MethodDictRefBill",t.No);return new h(B.GoToUrl,u)}if(e===i.FrmBBS||e===i.DBList||e===i.DictLog||e===i.QRCode||e==i.DataVer){if(t.No=this.PKVal+"_"+e,(yield t.IsExits())==!0){if(e!=i.DBList){alert("该组件已经存在,不可重复添加.");return}t.No=P.GenerGUID(),t.MethodID=e}t.Name=n,e===i.FrmBBS&&(t.Icon="icon-film"),e===i.DictLog&&(t.Icon="icon-eye"),e===i.QRCode&&(t.Icon="icon-frame"),e===i.DataVer&&(t.Icon="icon-camera"),e===i.DBList&&(t.Icon="icon-drop"),t.SetPara("EnName","TS.CCBill.Method"+e),yield t.Insert();const r=N.UrlEn(t.GetParaString("EnName",""),t.No);return new h(B.GoToUrl,r)}if(e===i.PrintHtml||e===i.PrintPDF||e===i.PrintRTF||e==i.PrintZip){if(t.No=this.PKVal+"_"+e,e===i.PrintRTF&&(yield t.IsExits())==!0)t.No=P.GenerGUID();else if((yield t.IsExits())==!0){alert("该组件已经存在,不可重复添加.");return}t.Name=n,e===i.PrintHtml&&(t.Icon="icon-printer"),e===i.PrintPDF&&(t.Icon="icon-printer"),e===i.PrintRTF&&(t.Icon="icon-printer"),e===i.PrintZip&&(t.Icon="icon-cloud-download"),t.Tag1=e,e=="PrintRTF"?t.SetPara("EnName","TS.CCBill.MethodPrintRTF"):t.SetPara("EnName","TS.CCBill.MethodPrint"),yield t.Insert();const r="/src/WF/Comm/En.vue?EnName=TS.CCBill.MethodFlowBaseData&PKVal="+t.No;return new h(B.GoToUrl,r)}if(e===i.FlowBaseData){const r=n;let l=this.RefPKVal;(l==null||l==null)&&(l=this.RequestVal("FrmID"));const D=c;(l==null||l=="")&&(l=this.PKVal);const m=new T("BP.Sys.MapData",l);m.No=l,yield m.Retrieve();const a=new S("BP.CCBill.WF_CCBill_Admin_Method");a.AddPara("SortNo",m.data.FK_FormTree),a.AddPara("FlowName",r),a.AddPara("Name",r),a.AddPara("FrmID",l),a.AddPara("FlowDevModel",1),a.AddPara("GroupID",D),a.AddPara("ModuleNo","");const s=yield a.DoMethodReturnString("FlowBaseData_Save"),u=N.UrlEn("TS.CCBill.MethodFlowBaseData",s);return new h(B.GoToUrl,u)}if(e===i.FlowEtc||e==="FlowSingleRefData"||e==="FlowSingleCopyData"){const r=n;let l=this.RefPKVal;(l==null||l==null)&&(l=this.RequestVal("FrmID"));const D=c;(l==null||l=="")&&(l=this.PKVal);const m=new T("BP.Sys.MapData",l);m.No=l,yield m.Retrieve();const a=new S("BP.CCBill.WF_CCBill_Admin_Method");a.AddPara("SortNo",m.FK_FormTree),a.AddPara("FlowName",r),a.AddPara("Name",r),a.AddPara("FrmID",l),a.AddPara("FlowDevModel",1),a.AddPara("GroupID",D),a.AddPara("ModuleNo",""),a.AddPara("FlowModel",e);const s=yield a.DoMethodReturnString("FlowEtc_Save"),u=N.UrlEn("TS.CCBill.Method"+e,s);return new h(B.GoToUrl,u)}if(e===i.SingleDictGenerWorkFlows){if(t.No=this.PKVal+"_"+e,(yield t.IsExits())==!0){alert("该组件已经存在,不可重复添加.");return}t.Name=n,t.MethodID=i.SingleDictGenerWorkFlows,t.MethodModel=i.SingleDictGenerWorkFlows,t.RefMethodType=1,t.Icon="icon-drop",t.SetPara("EnName","TS.CCBill.MethodSingleDictGenerWorkFlow"),t.Insert();const r="/src/WF/Comm/En.vue?EnName=TS.CCBill.MethodSingleDictGenerWorkFlow&PKVal="+t.No;return new h(B.GoToUrl,r)}})}}export{$ as GPN_Method,i as MethodModel};
