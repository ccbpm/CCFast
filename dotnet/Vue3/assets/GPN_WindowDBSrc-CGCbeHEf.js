var u=Object.defineProperty;var F=(r,t,e)=>t in r?u(r,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):r[t]=e;var s=(r,t,e)=>F(r,typeof t!="symbol"?t+"":t,e);var i=(r,t,e)=>new Promise((n,N)=>{var l=a=>{try{o(e.next(a))}catch(p){N(p)}},c=a=>{try{o(e.throw(a))}catch(p){N(p)}},o=a=>a.done?n(a.value):Promise.resolve(a.value).then(l,c);o((e=e.apply(r,t)).next())});import{WinDocModel as m}from"./WinDocModel-BUMXT_mN.js";import{WindowTemplate as y}from"./WindowTemplate-DdOTsEqk.js";import{b5 as D,G as E,l as S}from"./entry/index-C6uBgOW5-1730430676707.js";import{GloComm as f}from"./GloComm-CmAl8MpM.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";class C extends D{constructor(){super("GPN_WindowDBSrc");s(this,"WebApi",`
  #### 帮助
  - 执行定义的过程.
  - 系统将把表单数据主表从表，都会写入到接口里面去.
`);s(this,"SFProcedure",`
  #### 帮助
  - 执行定义的过程.
  - 系统将把表单数据主表从表，都会写入到接口里面去.
`);s(this,"Docs0",`
  #### 帮助
  - 在执行内容里填写一个存储过程名称，注意表达式支持变量。如： EXEC YourProName @OID
  #### 运行图例
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapData/FrmEvent/Img/StoreProcedure.png "屏幕截图.png")  
`);s(this,"BuessUnit",`
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
`);s(this,"Docs4",`
#### 帮助
- 返回一行数据的json格式的数据源.
- 在执行内容里设置一个http://myserver/Do.aspx?DoType=aaaaa，创建一个Do.aspx 根据DoType 标记这不同的内容处理。
- 如果顺利处理了就返回空，出现异常一定要返回: Error+”异常信息。”
- 处理返回值用: this.Response.Write("Error:"+msg); 方法.
- Ccform 处理的机制是，使用 HttpWebRequest 类静默的执行URL ,然后获取返回的内容。如果检查到前几个字符是Error 就认为是异常ccform 就会抛出异常。
#### 系统参数：
- 您定义的url比如为 /App/DoUrl.aspx?ABC=123 , 系统会在之后增加一些参数，这些参数叫系统参数。实际执行的url为。
- http://yourserver/App/DoUrl.aspx?ABC=123&UserNo=xy&SID=xxxxx&FK_Dept=1010&FK_Unit=10&EntityName=ND101&EntityPK=OID&EntityPKVal=12333& FK_Event=xxxxxx
`);this.PageTitle="数据源向导"}Init(){return i(this,null,function*(){this.AddGroup("A","新建窗体"),this.SelectItemsByList("SelectType","选择数据源",this.BuessUnit,!1,this.selectChartType()),this.SelectItemsByList("SelectType.Table","表",this.BuessUnit,!1,this.selectChartType()),this.SelectItemsByList("SelectType.Table.ChartType","选择图表类型",this.BuessUnit,!1,this.selectChartType());const e=`SELECT KeyOfEn as No,Name FROM Sys_MapAttr WHERE MyDataType IN (2,3,5,8) AND LGType=0 AND FK_MapData='${this.RequestVal("FrmID")}'`;this.SelectItemsByList("SelectType.NumFiled","分析数据",this.BuessUnit,!1,e);const n=`SELECT KeyOfEn as No,Name FROM Sys_MapAttr WHERE MyDataType IN (2,1) AND LGType in (1,2) AND FK_MapData='${this.RequestVal("FrmID")}'`;this.SelectItemsByList("SelectType.NumFiled.Group","分析内容",this.BuessUnit,!1,n)})}selectChartType(){return JSON.stringify([{No:m.ChartLine,Name:"折线图"},{No:m.ChartPie,Name:"饼状图"},{No:m.ChartZZT,Name:"柱状图"},{No:m.ChartRate,Name:"百分比扇形图"},{No:m.ChartRing,Name:"环形图"}])}Save_TextBox_X(e,n,N,l,c){return i(this,null,function*(){if(e!="SelectType.NumFiled.Group")return;this.RequestVal("RefPKVal");const o=new y;return o.SetPara("EnName",enName),yield o.Insert(),new E(S.GoToUrl,f.UrlEn(enName,o.MyPK))})}GenerSorts(){return i(this,null,function*(){return this.RefMainEnName==="TS.WF.Template.FlowExt"?Promise.resolve([{No:"FlowOnCreateWorkID",Name:"创建工作ID后"},{No:"FlowOverBefore",Name:"流程结束前"},{No:"FlowOverAfter",Name:"流程结束后"},{No:"BeforeFlowDel",Name:"流程删除前"},{No:"AfterFlowDel",Name:"流程删除后"}]):this.RefMainEnName==="TS.WF.Template.NodeExt"?Promise.resolve([{No:"WorkArrive",Name:"工作到达"},{No:"SendWhen",Name:"当节点发送前"},{No:"SendSuccess",Name:"节点发送成功时"},{No:"SendError",Name:"节点发送失败时"},{No:"ReturnBefore",Name:"当节点退回前"},{No:"ReturnAfter",Name:"退回后"},{No:"UndoneBefore",Name:"当节点撤销发送前"},{No:"UndoneAfter",Name:"当节点撤销发送后"},{No:"WhenReadWork",Name:"工作打开后"}]):this.RefMainEnName==="TS.Frm.MapFrmFool"?Promise.resolve([{No:"FrmLoadBefore",Name:"表单载入前"},{No:"FrmLoadAfter",Name:"节点表单载入后"},{No:"SaveBefore",Name:"当表单保存前"},{No:"SaveAfter",Name:"当表单保存后"},{No:"FrmLoadAfter",Name:"节点表单载入后"},{No:"DeleteBefore",Name:"当表单删除前"},{No:"DeleteAfter",Name:"当表单删除后"}]):Promise.resolve([])})}}export{C as GPN_WindowDBSrc};
