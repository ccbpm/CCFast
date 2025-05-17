var I=Object.defineProperty;var p=(F,d,i)=>d in F?I(F,d,{enumerable:!0,configurable:!0,writable:!0,value:i}):F[d]=i;var o=(F,d,i)=>p(F,typeof d!="symbol"?d+"":d,i);var S=(F,d,i)=>new Promise((c,n)=>{var C=r=>{try{t(i.next(r))}catch(l){n(l)}},T=r=>{try{t(i.throw(r))}catch(l){n(l)}},t=r=>r.done?c(r.value):Promise.resolve(r.value).then(C,T);t((i=i.apply(F,d)).next())});import{b9 as A,aM as f,aB as h,aC as D,aQ as R,H as w}from"./entry/index-M8VErHPE-1727507756861.js";import{GroupMethods as x,GroupMethodAttr as G}from"./GroupMethod-Cc_Vx1Kw.js";import{Method as E}from"./Method-D3vubAhY.js";import{D as P}from"./DBAccess-CzjFzLoq.js";import{GloComm as u}from"./GloComm-DZ1gELjv.js";import{MapData as _}from"./MapData-lfC2UY9r.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./PCenter-CGZJ3ajQ.js";import"./PowerCenter-B1QbxnUu.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./FrmTrack-0uAZQ3B_.js";import"./EnumLab-CzismWql.js";class e{}o(e,"Link","Link"),o(e,"Func","Func"),o(e,"Bill","Bill"),o(e,"FrmBBS","FrmBBS"),o(e,"DataVer","DataVer"),o(e,"DictLog","DictLog"),o(e,"QRCode","QRCode"),o(e,"DBList","DBList"),o(e,"Toolbar","Toolbar"),o(e,"ImpFromFile","ImpFromFile"),o(e,"PrintRTF","PrintRTF"),o(e,"PrintHtml","PrintHtml"),o(e,"PrintPDF","PrintPDF"),o(e,"PrintZip","PrintZip"),o(e,"FlowBaseData","FlowBaseData"),o(e,"FlowEtc","FlowEtc"),o(e,"FlowNewEntity","FlowNewEntity"),o(e,"SingleDictGenerWorkFlows","SingleDictGenerWorkFlows");class Z extends A{constructor(){super("GPN_Method");o(this,"SingleDictGenerWorkFlows",`
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
  - 
    
  `);o(this,"Docs10",`
   
  #### 帮助
  - 
    
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
    
  `);this.PageTitle="新建实体方法",this.ForEntityClassID="TS.CCBill.Method"}Init(){this.AddGroup("A","常规组件","icon-doc"),this.TextBox2_NameNo(e.Link,"自定义链接",this.Docs0,"","URL链接","链接名称","我的链接"),this.TextBox2_NameNo(e.Func,"方法",this.Docs1,"Func_","方法ID","方法名称","缴纳班费"),this.TextBox1_Name(e.FrmBBS,"BBS/评论/日志组件",this.Docs3,"名称","评论"),this.TextBox1_Name(e.DataVer,"数据快照",this.Docs4,"名称","数据快照"),this.TextBox1_Name(e.DictLog,"操作日志",this.Docs5,"名称","操作日志"),this.TextBox1_Name(e.QRCode,"二维码",this.Docs6,"名称","二维码"),this.TextBox1_Name(e.DBList,"数据列表",this.Docs6,"名称","数据列表"),this.TextBox1_Name(e.PrintRTF,"RTF模板打印",this.Docs7,"名称","RTF模板打印"),this.AddGroup("D","关联单据","icon-doc"),this.TextBox2_NameNo("NewBill","新建单据",this.HelpTodo,"Bill_","编号","名称","维修单");const i=this.RequestVal("FrmID"),c=`SELECT OID as No, Lab as Name FROM Sys_GroupField WHERE FrmID='${i}' AND CtrlID='' `,n=` SELECT MyPK AS No, Name, GroupID FROM Sys_MapAttr WHERE FK_MapData='${i}' 
    AND UIContralType <=4 AND KeyOfEn NOT IN ('OID','Rec','RDT','FID','Title','BillNo','BillState','FlowStarter',
    'FlowEmps',
    'FlowStartRDT','WFState','Emps')
    AND UIVisible=1 ORDER BY GroupID,Idx
    `;this.SelectItemsByGroupList("NewBill.SelectAttrs","选择字段",this.HelpUn,!0,c,n),this.SelectItemsByList("NewBill.SelectAttrs.Group","选择目录",this.HelpUn,!1,f.srcFrmTree),this.SelectItemsByGroupList("RefBill","关联单据",this.Desc100,!1,f.srcFrmTree,f.srcFrmListBill),this.SelectItemsByList("RefBill.DictID","选择关联字段ID",this.HelpUn,!1,this.RefBillAttrs),this.SelectItemsByList("RefBill.DictID.DictName","选择关联字段Name",this.HelpUn,!1,this.RefBillAttrs),this.AddGroup("C","流程类","icon-doc"),this.TextBox1_Name(e.FlowBaseData,"基础数据变更流程",this.Docs11,"流程名称","基础数据变更流程"),this.TextBox1_Name(e.FlowEtc,"业务流程",this.Docs12,"流程名称",""),this.TextBox1_Name(e.SingleDictGenerWorkFlows,"实体流程汇总列表(综合流程列表)",this.SingleDictGenerWorkFlows,"流程名称","流程列表"),this.AddIcon("icon-link","Link"),this.AddIcon("icon-film","Func"),this.AddIcon("icon-bubbles","FrmBBS"),this.AddIcon("icon-docs","DataVer"),this.AddIcon("icon-film","DictLog"),this.AddIcon("icon-frame","QRCode"),this.AddIcon("icon-list","DBList"),this.AddIcon("icon-layers","PrintRTF"),this.AddIcon("icon-doc","NewBill"),this.AddIcon("icon-doc","RefBill"),this.AddIcon("icon-grid","FlowBaseData"),this.AddIcon("icon-grid","FlowEtc"),this.AddIcon("icon-grid","SingleDictGenerWorkFlows")}RefBillAttrs(){return S(this,null,function*(){const i=this.RequestVal("tb1","RefBill"),c=new w("BP.CCBill.WF_CCBill_Admin_Method");return c.AddPara("FrmID",i),yield c.DoMethodReturnString("GPN_Menthd_RefBill_BillAttrs")})}GenerSorts(){return S(this,null,function*(){const i=new x;return yield i.Retrieve(G.FrmID,this.PKVal,"Idx"),i})}Save_TextBox_X(i,c,n,C,T){return S(this,null,function*(){const t=new E;if(t.GroupID=c,t.GroupIDT=this.GetSortName(c),t.FrmID=this.PKVal,t.Icon=this.GetPageIcon(i),t.IsEnable=!0,t.Idx=100,t.Name=n,t.MethodModel=i,i===e.Link){t.Name=n,t.Docs=C,t.No=P.GenerGUID(),t.SetPara("EnName","TS.CCBill.MethodLink"),yield t.Insert();const r=u.UrlEn("TS.CCBill.MethodLink",t.No);return new h(D.GoToUrl,r)}if(i===e.Func){t.Name=n,t.Docs=C,t.No=P.GenerGUID(),t.SetPara("EnName","TS.CCBill.MethodFunc"),t.MethodID=C,yield t.Insert();const r=u.UrlEn("TS.CCBill.MethodFunc",t.No);return new h(D.GoToUrl,r)}if(i==="NewBill"){const r=new _;if(r.No=C,(yield r.RetrieveFromDBSources())!=0)return new h(D.Error,"表单ID["+n+"]已经存在.")}if(i=="RefBill.DictID.DictName"){const r=this.RequestVal("FrmID"),l=this.RequestVal("tb2","RefBill"),B=this.RequestVal("tb1","RefBill"),m=this.RequestVal("tb1","RefBill.DictID"),s=this.RequestVal("tb1","RefBill.DictID.DictName");t.Name=B,t.MethodModel="DictRefBill",t.No=r+"_"+l,t.Tag1=l,t.Tag2=B,t.SetPara("EnName","TS.CCBill.MethodDictRefBill"),t.SetPara("RefDictNo",m),t.SetPara("RefDictName",s),t.MethodID="DictRefBill",yield t.Insert();const a=u.UrlEn("TS.CCBill.MethodDictRefBill",t.No);return new h(D.GoToUrl,a)}if(i=="NewBill.SelectAttrs.Group"){const r=this.RequestVal("FrmID"),l=this.RequestVal("tb2","NewBill"),B=this.RequestVal("tb1","NewBill"),m=this.RequestVal("tb3","NewBill"),s=this.RequestVal("tb1","NewBill.SelectAttrs.Group"),a=new w("BP.WF.HttpHandler.WF_Admin_CCFormDesigner");a.AddUrlData(),a.AddPara("FK_FrmSort",s),a.AddPara("TB_No",l),a.AddPara("TB_Name",B),a.AddPara("TB_PTable",m),a.AddPara("DDL_PTableModel",0),a.AddPara("EntityType",1),a.AddPara("SelectAttrs",this.RequestVal("tb1","NewBill.SelectAttrs")),a.AddPara("DictFrmID",r),yield a.DoMethodReturnString("NewFrmGuide_Create"),t.Name=B,t.MethodModel="DictRefBill",t.No=r+"_"+l,t.Tag1=l,t.Tag2=B,t.SetPara("EnName","TS.CCBill.MethodDictRefBill"),t.SetPara("RefDictNo",r+"No"),t.SetPara("RefDictName",r+"Name"),t.MethodID="DictRefBill",yield t.Insert();const N=u.UrlEn("TS.CCBill.MethodDictRefBill",t.No);return new h(D.GoToUrl,N)}if(i===e.FrmBBS||i===e.DBList||i===e.DictLog||i===e.QRCode||i==e.DataVer){if(t.No=this.PKVal+"_"+i,(yield t.IsExits())==!0){if(i!=e.DBList){alert("该组件已经存在,不可重复添加.");return}t.No=P.GenerGUID(),t.MethodID=i}t.Name=n,i===e.FrmBBS&&(t.Icon="icon-film"),i===e.DictLog&&(t.Icon="icon-eye"),i===e.QRCode&&(t.Icon="icon-frame"),i===e.DataVer&&(t.Icon="icon-camera"),i===e.DBList&&(t.Icon="icon-drop"),t.SetPara("EnName","TS.CCBill.Method"+i),yield t.Insert();const r=u.UrlEn(t.GetParaString("EnName",""),t.No);return new h(D.GoToUrl,r)}if(i===e.PrintHtml||i===e.PrintPDF||i===e.PrintRTF||i==e.PrintZip){if(t.No=this.PKVal+"_"+i,i===e.PrintRTF&&(yield t.IsExits())==!0)t.No=P.GenerGUID();else if((yield t.IsExits())==!0){alert("该组件已经存在,不可重复添加.");return}t.Name=n,i===e.PrintHtml&&(t.Icon="icon-printer"),i===e.PrintPDF&&(t.Icon="icon-printer"),i===e.PrintRTF&&(t.Icon="icon-printer"),i===e.PrintZip&&(t.Icon="icon-cloud-download"),t.Tag1=i,i=="PrintRTF"?t.SetPara("EnName","TS.CCBill.MethodPrintRTF"):t.SetPara("EnName","TS.CCBill.MethodPrint"),yield t.Insert();const r="/src/WF/Comm/En.vue?EnName=TS.CCBill.MethodFlowBaseData&PKVal="+t.No;return new h(D.GoToUrl,r)}if(i===e.FlowBaseData){const r=n;let l=this.RefPKVal;(l==null||l==null)&&(l=this.RequestVal("FrmID"));const B=c;(l==null||l=="")&&(l=this.PKVal);const m=new R("BP.Sys.MapData",l);m.No=l,yield m.Retrieve();const s=new w("BP.CCBill.WF_CCBill_Admin_Method");s.AddPara("SortNo",m.data.FK_FormTree),s.AddPara("FlowName",r),s.AddPara("Name",r),s.AddPara("FrmID",l),s.AddPara("FlowDevModel",1),s.AddPara("GroupID",B),s.AddPara("ModuleNo","");const a=yield s.DoMethodReturnString("FlowBaseData_Save"),N=u.UrlEn("TS.CCBill.MethodFlowBaseData",a);return new h(D.GoToUrl,N)}if(i===e.FlowEtc){const r=n;let l=this.RefPKVal;(l==null||l==null)&&(l=this.RequestVal("FrmID"));const B=c;(l==null||l=="")&&(l=this.PKVal);const m=new R("BP.Sys.MapData",l);m.No=l,yield m.Retrieve();const s=new w("BP.CCBill.WF_CCBill_Admin_Method");s.AddPara("SortNo",m.FK_FormTree),s.AddPara("FlowName",r),s.AddPara("Name",r),s.AddPara("FrmID",l),s.AddPara("FlowDevModel",1),s.AddPara("GroupID",B),s.AddPara("ModuleNo","");const a=yield s.DoMethodReturnString("FlowEtc_Save"),N=u.UrlEn("TS.CCBill.MethodFlowEtc",a);return new h(D.GoToUrl,N)}if(i===e.SingleDictGenerWorkFlows){if(t.No=this.PKVal+"_"+i,(yield t.IsExits())==!0){alert("该组件已经存在,不可重复添加.");return}t.Name=n,t.MethodID=e.SingleDictGenerWorkFlows,t.MethodModel=e.SingleDictGenerWorkFlows,t.RefMethodType=1,t.Icon="icon-drop",t.SetPara("EnName","TS.CCBill.MethodSingleDictGenerWorkFlow"),t.Insert();const r="/src/WF/Comm/En.vue?EnName=TS.CCBill.MethodSingleDictGenerWorkFlow&PKVal="+t.No;return new h(D.GoToUrl,r)}})}}export{Z as GPN_Method,e as MethodModel};
