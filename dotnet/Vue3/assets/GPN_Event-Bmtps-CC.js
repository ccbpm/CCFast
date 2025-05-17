var E=Object.defineProperty;var d=(a,r,t)=>r in a?E(a,r,{enumerable:!0,configurable:!0,writable:!0,value:t}):a[r]=t;var c=(a,r,t)=>d(a,typeof r!="symbol"?r+"":r,t);var N=(a,r,t)=>new Promise((s,i)=>{var m=e=>{try{l(t.next(e))}catch(o){i(o)}},F=e=>{try{l(t.throw(e))}catch(o){i(o)}},l=e=>e.done?s(e.value):Promise.resolve(e.value).then(m,F);l((t=t.apply(a,r)).next())});import{SysEvent as u}from"./SysEvent-D4PfKXg3.js";import{b5 as v,G as D,l as S,H as f}from"./entry/index-C6uBgOW5-1730430676707.js";import{D as B}from"./DBAccess-sLO0RM-h.js";import{GloComm as p}from"./GloComm-CmAl8MpM.js";import{Node as h}from"./Node-DKGUVcK1.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./FrmTrack-BAfWiAdt.js";import"./EntityNodeID-BCBbJNH2.js";class L extends v{constructor(){super("GPN_Event");c(this,"WebApi",`
  #### 帮助
  - 执行定义的过程.
  - 系统将把表单数据主表从表，都会写入到接口里面去.
  #### Demo
  - 设置的: http://192.168.10:1000/xx?JinE=@JinE
  - 系统执行的: http://192.168.10:1000/xx?JinE=1001&WrokID=1000&FlowNo=009&NodeID=905&Token=xxxxxx
  - 说明：系统就会自动补充上系统参数. WorkID,FlowNo,NodeID,Token 数据.
  -  如果配置了@+字段名,系统就会自动替换下来.
  #### 返回值约定
  - err@xxxxxx 错误信息，抛出的异常信息.
  - info@xxxxx 执行成功的信息.
  - 其他的信息，系统也会提示出来按照info@计算。
`);c(this,"SFProcedure",`
  #### 帮助
  - 执行定义的过程.
  - 系统将把表单数据主表从表，都会写入到接口里面去.
`);c(this,"Docs0",`
  #### 帮助
  - 在执行内容里填写一个存储过程名称，注意表达式支持变量。如： EXEC YourProName @OID
  #### 运行图例
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapData/FrmEvent/Img/StoreProcedure.png "屏幕截图.png")  
`);c(this,"BuessUnit",`
#### 帮助
- ccbpm提供了一个让后台开发人员使用的代码表达业务逻辑实现的方式,业务单元是其中的一种.
- 定义: 处理一段业务逻辑脚本, 我们称为业务单元,比如:付款,出库. 
- 这个业务模块有通用性,可以被很多流程所调用,我们把它封装为一个业务单元.
- 这个代码块从一个基类上继承下来（BP.Sys.BuessUnitBase）. 按照要求重写方法. 
- 在配置的时候，ccbpm通过基类的反射功能，放入到下拉框，方便流程设计人员进行选择配置.
#### DEMO.
- 定义一个子类，如下图:
![输入图片说明](/resource/WF/Admin/FrmLogic/MapData/FrmEvent/Img/UnitDemo.png "屏幕截图.png")  
- 在BP类里定义一个业务单元类 如下图中的 出库信息初始化 BuessUnitFrmND1407 ，继承自 BP.Sys.BuessUnitBase
![输入图片说明](/resource/WF/Admin/FrmLogic/MapData/FrmEvent/Img/BuessUnitBase.Java.png "屏幕截图.png")  
- 在后台选择这个类配置到表单事件中。

#### 配置图例
![输入图片说明](/resource/WF/Admin/FrmLogic/MapData/FrmEvent/Img/Event.png "屏幕截图.png")  
#### 配置图例
![输入图片说明](/resource/WF/Admin/FrmLogic/MapData/FrmEvent/Img/Event1.png "屏幕截图.png")  
#### 事件存储
 所有的事件配置信息都存储在Sys_FrmEvent表里。
![输入图片说明](/resource/WF/Admin/FrmLogic/MapData/FrmEvent/Img/Eventcc.png "屏幕截图.png")  
`);c(this,"Docs4",`
#### 帮助
- 返回一行数据的json格式的数据源.
- 在执行内容里设置一个http://myserver/Do.aspx?DoType=aaaaa，创建一个Do.aspx 根据DoType 标记这不同的内容处理。
- 如果顺利处理了就返回空，出现异常一定要返回: Error+”异常信息。”
- 处理返回值用: this.Response.Write("Error:"+msg); 方法.
- Ccform 处理的机制是，使用 HttpWebRequest 类静默的执行URL ,然后获取返回的内容。如果检查到前几个字符是Error 就认为是异常ccform 就会抛出异常。
#### 系统参数：
- 您定义的url比如为 /App/DoUrl.aspx?ABC=123 , 系统会在之后增加一些参数，这些参数叫系统参数。实际执行的url为。
- http://yourserver/App/DoUrl.aspx?ABC=123&UserNo=xy&SID=xxxxx&FK_Dept=1010&FK_Unit=10&EntityName=ND101&EntityPK=OID&EntityPKVal=12333& FK_Event=xxxxxx
`);this.ForEntityClassID="TS.Sys.SysEvent",this.PageTitle="新建事件"}Init(){return N(this,null,function*(){this.AddGroup("A","新建事件"),this.TextSQL("2","数据源模式",this.Docs0,"输入过程名称:","","比如:excec mypro"),this.SelectItemsByList("7","业务单元-BuessUnit",this.BuessUnit,!1,yield this.GenerBuessUnit()),this.SelectItemsByList("9","执行自定义过程",this.SFProcedure,!1,"SELECT No,Name FROM Sys_SFProcedure "),this.SelectItemsByList("6","事件类-EventBase",this.BuessUnit,!1,yield this.GenerEventBase()),this.TextBox1_Name("8","执行WebApi",this.WebApi,"请输入url","http://"),this.AddGroup("B","数据源管理"),this.AddFunction("ToUrl","数据源维护ToUrl",this.AdminDBSrc)})}GenerBuessUnit(){return N(this,null,function*(){const s=yield new f("BP.WF.HttpHandler.WF_Admin_AttrNode").DoMethodReturnJson("ActionDtl_Init");return JSON.stringify(s)})}GenerEventBase(){return N(this,null,function*(){const s=yield new f("BP.WF.HttpHandler.WF_Admin_AttrNode").DoMethodReturnJson("ActionEventBase_Init");return JSON.stringify(s)})}AdminDBSrc(){return N(this,null,function*(){const t=p.UrlSearch("TS.Sys.SFDBSrc");return new D(S.GoToUrl,t)})}Save_TextBox_X(t,s,i,m,F){return N(this,null,function*(){const l=this.RequestVal("RefPKVal"),e=new u;e.FK_DBSrc="无",e.EventSource=1,e.RefPKVal=l,e.EventID=s,e.EventName=yield this.GetSortName(s),this.RefMainEnName.includes("TS.WF.Template.Frm")&&(e.EventSource=0),this.RefMainEnName.includes("TS.CCBill.FrmDict")&&(e.EventSource=0),this.RefMainEnName.includes("TS.CCBill.FrmEntityNoName")&&(e.EventSource=0),this.RefMainEnName.includes("TS.CCBill.FrmBill")&&(e.EventSource=0),this.RefMainEnName==="TS.WF.Template.NodeExt"&&(e.EventSource=1),this.RefMainEnName==="TS.WF.Template.FlowExt"&&(e.EventSource=2),e.EventDoType=t,e.EventDoTypeT=this.GetPageName(t),e.DoDoc=m,e.MyPK=B.GenerGUID();let o="TS.Sys.SysEventDBSrc";if(t=="2"&&(e.FK_DBSrc=i),t=="7"&&(o="TS.Sys.SysEventBuessUnit",e.DoDoc=i,e.FK_DBSrc=m),t=="6"){o="TS.Sys.SysEventEventBase";const n=i.split("@")[0];e.DoDoc=n,e.DoDocT=m,e.EventDoTypeT=m}if(t=="9"&&(o="TS.Sys.SysEventSFProcedure",e.DoDoc=i,e.DoDocT=m),t=="8"&&(o="TS.Sys.SysEventSFWebApi",e.DoDoc=i),e.EventSource==0&&(e.FrmID=this.RefPKVal),e.EventSource==1){const n=new h(this.RefPKVal);yield n.Retrieve(),n.NodeFrmID===""||n.NodeFrmID.lastIndexOf("ND")==0?e.FrmID="ND"+parseInt(n.FK_Flow)+"Rpt":e.FrmID=n.NodeFrmID,e.FK_Node=n.NodeID,e.FK_Flow=n.FK_Flow,e.RefFlowNo=n.FK_Flow}return e.EventSource==2&&(e.FrmID="ND"+parseInt(this.RefPKVal)+"Rpt",e.FK_Flow=this.RefPKVal,e.RefFlowNo=this.RefPKVal),e.SetPara("EnName",o),yield e.Insert(),new D(S.GoToUrl,p.UrlEn(o,e.MyPK))})}GenerSorts(){return N(this,null,function*(){return this.RefMainEnName==="TS.WF.Template.FlowExt"?Promise.resolve([{No:"FlowOnCreateWorkID",Name:"创建工作ID后"},{No:"FlowOverBefore",Name:"流程结束前"},{No:"FlowOverAfter",Name:"流程结束后"},{No:"BeforeFlowDel",Name:"流程删除前"},{No:"AfterFlowDel",Name:"流程删除后"}]):this.RefMainEnName==="TS.WF.Template.NodeExt"?Promise.resolve([{No:"WorkArrive",Name:"工作到达"},{No:"SendWhen",Name:"当节点发送前"},{No:"SendSuccess",Name:"节点发送成功时"},{No:"SendError",Name:"节点发送失败时"},{No:"ReturnBefore",Name:"当节点退回前"},{No:"ReturnAfter",Name:"退回后"},{No:"UndoneBefore",Name:"当节点撤销发送前"},{No:"UndoneAfter",Name:"当节点撤销发送后"},{No:"WhenReadWork",Name:"工作打开后"}]):this.RefMainEnName==="TS.CCBill.FrmBill"?Promise.resolve([{No:"OverBefore",Name:"归档前"},{No:"OverAfter",Name:"归档后"},{No:"CheckStart",Name:"启动审核"},{No:"CheckOver",Name:"审核结束后"},{No:"UnSend",Name:"撤销审核"},{No:"Reback",Name:"回滚前"},{No:"FrmLoadBefore",Name:"表单载入前"},{No:"FrmLoadAfter",Name:"节点表单载入后"},{No:"SaveBefore",Name:"当表单保存前"},{No:"SaveAfter",Name:"当表单保存后"},{No:"FrmLoadAfter",Name:"节点表单载入后"},{No:"DeleteBefore",Name:"当表单删除前"},{No:"DeleteAfter",Name:"当表单删除后"}]):Promise.resolve([{No:"FrmLoadBefore",Name:"表单载入前"},{No:"FrmLoadAfter",Name:"节点表单载入后"},{No:"SaveBefore",Name:"当表单保存前"},{No:"SaveAfter",Name:"当表单保存后"},{No:"FrmLoadAfter",Name:"节点表单载入后"},{No:"DeleteBefore",Name:"当表单删除前"},{No:"DeleteAfter",Name:"当表单删除后"}])})}}export{L as GPN_Event};
