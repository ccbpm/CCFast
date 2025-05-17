var x=Object.defineProperty;var y=(c,s,o)=>s in c?x(c,s,{enumerable:!0,configurable:!0,writable:!0,value:o}):c[s]=o;var n=(c,s,o)=>y(c,typeof s!="symbol"?s+"":s,o);var D=(c,s,o)=>new Promise((N,i)=>{var m=e=>{try{t(o.next(e))}catch(l){i(l)}},f=e=>{try{t(o.throw(e))}catch(l){i(l)}},t=e=>e.done?N(e.value):Promise.resolve(e.value).then(m,f);t((o=o.apply(c,s)).next())});import{b9 as T,aB as w,aC as F,W as B,J as U,aQ as A,H as M}from"./entry/index-M8VErHPE-1727507756861.js";import{Menus as R}from"./Menu-CUL0m0Wx.js";import{Collection as P}from"./Collection-C1EQ_otd.js";import{D as I}from"./DBAccess-CzjFzLoq.js";import{F as _}from"./FrmBill-BBwTGzvc.js";import{Methods as k}from"./Method-D3vubAhY.js";import{GloComm as u}from"./GloComm-DZ1gELjv.js";import{FlowSort as G,FlowSorts as L}from"./FlowSort-Cod5vT3a.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./PCenter-CGZJ3ajQ.js";import"./PowerCenter-B1QbxnUu.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./FrmAdm-DEdgJPii.js";import"./MapData-lfC2UY9r.js";import"./EnumLab-CzismWql.js";import"./SysEvent-BDHAFjMW.js";import"./PG_Group2Method-C4uAx4td.js";import"./PageBasePanelGroup-BlJ5oMyj.js";import"./GroupMethod-Cc_Vx1Kw.js";import"./FrmTrack-0uAZQ3B_.js";import"./PageBaseGroupEdit-JIgqoTiq.js";import"./Help-D0bDMZWg.js";import"./SearchFKEnum-BLDDK4zg.js";import"./GPE_ActiveDDL-BSvw0lCs.js";import"./GPEActiveDDLSFTable-CDs1bctA.js";import"./GPEActiveDDLSelfSetting-cWbBg1lv.js";import"./GPE_AutoFullDLL-kKOYGZbv.js";import"./GPEAutoFullDLL-BTgpBioK.js";import"./GPEAutoFullDDLSFTable-MXOvWgx8.js";import"./DBRole-BthbZKHy.js";import"./GPE_FrmType-DMY15Fbs.js";import"./Flow-D1QXg1UO.js";import"./SelfCheck-QnMztK45.js";import"./ByEmpNo-B1iso2da.js";import"./FlowAdm-CDKA5xU7.js";import"./Sort-DL2UOyFV.js";class r{}n(r,"SearchCond","SearchCond"),n(r,"Link","Link"),n(r,"QRCodeAddDict","QRCodeAddDict"),n(r,"FlowNewEntity","FlowNewEntity"),n(r,"Func","Func"),n(r,"Bill","Bill"),n(r,"LinkCollection","LinkCollection"),n(r,"FlowEntityBatchStart","FlowEntityBatchStart");class Et extends T{constructor(){super("GPN_Collection");n(this,"DocsLink","暂未开放");n(this,"FlowNewEntity",`
  #### 帮助
  - 比如：xxx登记、供应商申请、xx申请、入党申请、材料入库申请
  - 流程运行完毕后，就写入该条数据到实体列表中.
  #### 开发说明
  - 点击确定后，系统自动创建一个流程，并且开流程为绑定表单库的表单模式模式。
  - 该流程绑定的表单就是该实体表单
  - 用户发起流程实例就是直接在该表单上增加一笔记录，流程结束后，或者指定的节点结束后，该记录变为提交状态。
  `);n(this,"Docs0",`
  
  #### 帮助
   - 该模式的表单定义是自由的,每个节点上都可以定义不同的表单方案. 
   - 每个节点上都可以灵活定义个性化的表单,而不需要统一管理. 
        
        `);n(this,"Desc100","暂未开放");n(this,"DocSelfUrl",`
  #### 帮助
   - 自定义URL菜单， 您可以使用右上角的下拉框选择自己要定义的菜单类型. 
   
   - 菜单连接： http://ccbpm.cn/MyUrl.htm  
   - 菜单连接： http://ccbpm.cn/MyUrl.htm  
   - 链接： /WF/Comm/Search.htm?EnsName=TS.ZS.Projcets 查询
   - 链接： /WF/Comm/Group.htm?EnsName=TS.ZS.Projcets  分析
   - 链接： /WF/MyFlow.htm?FK_Flow=001 发起指定的流程. 
   
   -  可以使用相对路径，也可以使用绝对路径。
   -  用户输入的Url:  http://ccbpm.cn/MyUrl.htm
   -  打开的Url : http://ccbpm.cn/MyUrl.htm?UserNo=xxxx&Token=xxxx。
   -  SID就类似于token, UserNo就是当前登录用户的编号。
   -  <img src="SelfUrl.png" class="HelpImg" />
  `);n(this,"DescQRCodeAddDict",`
      扫码在手机上新建.
        `);n(this,"Docs1",`
  
   #### 帮助
    - 该表单是固定格式的表单,可以展现4列6列展现. 
    - 使用批量设置审核组件的状态,来满足不同的审批需要,审核组件有启用禁用只读三个状态. 
    - 用于简单的表单审批场景,第1个节点填写表单,第2个节点之后表单都是只读的,使用审核组件填写审核意见. 
    - 优点:开发效率高,展现简洁,学习成本低,业务人员可以入手. 
    - 缺点:展示样式固定.
        `);n(this,"Docs2",`
  
   #### 帮助
    - 该流程所有的节点都禁用了审核组件,审核信息写入到了审核分组里的字段里.
    - 流程在运动过程中,每个节点的人员都在当前节点上填写一些信息,走到最后一个节点才是完整的表单,所以整个表单就像累加起来的一样.
    - 我们把符合整个特征的流程,称为累加表单流程.
    
  `);n(this,"Docs3",`
   #### 帮助
   
    - 第三方软件向特定的表 WF_Task 中写入数据，每写入一条数据系统就会自动发起一条流程。
    - ccBPM就会读取这张表来完成流程的发起,发起成功后就把这条记录设置成已经发起的状态。
    - 详见设置以及该表的结构参考操作手册.
    
  `);this.PageTitle="新建列表组件",this.ForEntityClassID="TS.CCBill.Collection"}Init(){return D(this,null,function*(){const o=this.RefPKVal,N=new _(o);yield N.Retrieve(),this.AddGroup("A","无需集合支持"),this.TextBox2_NameNo(r.Link,"自定义链接",this.DocsLink,"","链接名称","URL链接","我的链接"),this.TextBox1_Name(r.QRCodeAddDict,"扫码填报",this.DescQRCodeAddDict,"标签","扫码填报");const i=`新建${N.Name}流程`;this.TextBox1_Name(r.FlowNewEntity,"注册/新增实体类流程",this.FlowNewEntity,"流程名称",i),this.AddGroup("B","需要集合支持");const m=`SELECT No,Name FROM Frm_Method  WHERE MethodModel='Func' AND FrmID='${this.RefPKVal}' ORDER BY Idx  `;yield new k().Retrieve("MethodModel","Func","FrmID",this.RefPKVal),this.SelectItemsByList(r.Func,"实体方法",this.Desc100,!1,m),this.TextBox1_Name(r.Bill,"单据:批量发起(未解析)",this.Docs0,"单据名称","出入证明"),this.TextBox2_NameNo(r.LinkCollection,"自定义链接",this.Docs0,"","链接标签","URL链接","我的链接");const t=`批量发起:${N.Name}流程`;this.TextBox1_Name(r.FlowEntityBatchStart,"批量发起流程",this.Docs0,"流程名称",t)})}GenerSorts(o){return Promise.resolve([])}Save_TextBox_X(o,N,i,m,f){return D(this,null,function*(){if(o===r.Link){const t=new P;t.FrmID=this.RefPKVal,t.MethodID="Link",t.Mark="Link",t.Name=i,t.MethodModel="Link",t.UrlExt=m,t.Tag1=m,t.Icon="icon-drop",t.SetPara("EnName","TS.CCBill.CollectionLink"),t.No=i+"_"+this.RefPKVal,t.Idx=100,yield t.Insert();const e=u.UrlEn(t.GetParaString("EnName",""),t.No);return new w(F.GoToUrl,e)}if(o===r.QRCodeAddDict){const t=new P;t.FrmID=this.RefPKVal,t.MethodID=o,t.Mark=o,t.Name=i,t.MethodModel=o,t.SetPara("EnName","TS.CCBill.CollectionQRCodeAddDict"),t.Icon="icon-drop",t.Idx=100,t.No=i+"_"+this.RefPKVal,yield t.Insert();const e=u.UrlEn(t.GetParaString("EnName",""),t.No);return new w(F.GoToUrl,e)}if(o===r.FlowNewEntity){const t=this.RefPKVal,e=new R;let l=yield e.Retrieve("FrmID",t,"MenuModel","Dict");if(l==0){alert("没有查询到菜单.");return}const h=e[0],d=new G(h.No);if(l=yield d.RetrieveFromDBSources(),l==0){let S="0";if(B.CCBPMRunModel!=U.Single)S=B.OrgNo;else{const E=new L;if(l=yield E.Retrieve("ParentNo","0"),l==0){alert("没有查询到流程根目录.");return}S=E[0].No}d.No=h.No,d.Name=h.Name,d.ParentNo=S,d.Insert()}const a=new M("BP.CCBill.WF_CCBill_Admin_Collection");a.AddPara("FlowName",i),a.AddPara("Name",i),a.AddPara("FrmID",t),a.AddPara("FlowDevModel",1),a.AddPara("ModuleNo",h.ModuleNo),a.AddPara("SortNo",d.No);const C=yield a.DoMethodReturnString("FlowNewEntity_Save"),p=u.UrlEn("TS.CCBill.CollectionFlowNewEntity",C);return new w(F.GoToUrl,p)}if(o===r.FlowEntityBatchStart){const t=this.RefPKVal,e=new R;if((yield e.Retrieve("FrmID",t,"MenuModel","Dict"))==0){alert("没有查询到菜单.");return}const h=e[0],d=new A("BP.Sys.MapData",t),a=new M("BP.CCBill.WF_CCBill_Admin_Collection");a.AddPara("SortNo",d.FK_FormTree),a.AddPara("FlowName",i),a.AddPara("Name",i),a.AddPara("FrmID",t),a.AddPara("FlowDevModel",1),a.AddPara("ModuleNo",h.ModuleNo),a.AddPara("IsCanBatch",1);const C=yield a.DoMethodReturnString("FlowEntityBatchStart_Save");if(C.indexOf("err@")==0){alert(C);return}const p=u.UrlEn("TS.CCBill.CollectionFlowNewEntity",C);return new w(F.GoToUrl,p)}if(o===r.Func){const t=new P;t.FrmID=this.RefPKVal,t.MethodID=i,t.Mark=o,t.Name=m,t.MethodModel=o,t.SetPara("EnName","TS.CCBill.CollectionFunc"),t.Icon="icon-energy",t.Idx=100,t.No=i+"_"+this.RefPKVal,yield t.Insert();const e=u.UrlEn(t.GetParaString("EnName",""),t.No);return new w(F.GoToUrl,e)}if(o===r.LinkCollection){const t=new P;t.FrmID=this.RefPKVal,t.MethodID=i,t.Mark=o,t.Name=m,t.MethodModel=o,t.SetPara("EnName","TS.CCBill.CollectionLink"),t.Icon="icon-energy",t.Idx=100,t.No=I.GenerGUID(),yield t.Insert();const e=u.UrlEn(t.GetParaString("EnName",""),t.No);return new w(F.GoToUrl,e)}alert("没有判断的PageID:"+o)})}}export{r as CollectionModel,Et as GPN_Collection};
