var A=Object.defineProperty;var U=(b,E,o)=>E in b?A(b,E,{enumerable:!0,configurable:!0,writable:!0,value:o}):b[E]=o;var D=(b,E,o)=>U(b,typeof E!="symbol"?E+"":E,o);var f=(b,E,o)=>new Promise((N,R)=>{var I=n=>{try{i(o.next(n))}catch(T){R(T)}},d=n=>{try{i(o.throw(n))}catch(T){R(T)}},i=n=>n.done?N(n.value):Promise.resolve(n.value).then(I,d);i((o=o.apply(b,E)).next())});import{WinDocModel as B}from"./WinDocModel-BUMXT_mN.js";import{WindowTemplate as G}from"./WindowTemplate-BSgIc8PU.js";import{b9 as v,aL as L,aB as w,aC as M}from"./entry/index-M8VErHPE-1727507756861.js";import{MapData as W}from"./MapData-lfC2UY9r.js";import{GloComm as P}from"./GloComm-DZ1gELjv.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./EnumLab-CzismWql.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";class J extends v{constructor(){super("GPN_WindowFrm");D(this,"Table1",`
  #### 帮助 -2维表格 
  - 样式
  - ![输入图片说明](/resource/CCBill/RptTabl1.png "1维度表格")  
`);D(this,"Table2",`
#### 帮助 -2维表格 
- 样式
- ![输入图片说明](/resource/CCBill/RptTabl2.png "1维度表格")  
`);D(this,"Table3",`
#### 帮助 -2维表格 
- 样式
- ![输入图片说明](/resource/CCBill/RptTabl3.png "1维度表格")  
`);D(this,"SFProcedure",`
  #### 帮助
  - 执行定义的过程.
  - 系统将把表单数据主表从表，都会写入到接口里面去.
`);D(this,"Docs0",`
  #### 帮助
  - 在执行内容里填写一个存储过程名称，注意表达式支持变量。如： EXEC YourProName @OID
  #### 运行图例
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapData/FrmEvent/Img/StoreProcedure.png "屏幕截图.png")  
`);D(this,"BuessUnit",`
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
`);D(this,"Docs4",`
#### 帮助
- 返回一行数据的json格式的数据源.
- 在执行内容里设置一个http://myserver/Do.aspx?DoType=aaaaa，创建一个Do.aspx 根据DoType 标记这不同的内容处理。
- 如果顺利处理了就返回空，出现异常一定要返回: Error+”异常信息。”
- 处理返回值用: this.Response.Write("Error:"+msg); 方法.
- Ccform 处理的机制是，使用 HttpWebRequest 类静默的执行URL ,然后获取返回的内容。如果检查到前几个字符是Error 就认为是异常ccform 就会抛出异常。
#### 系统参数：
- 您定义的url比如为 /App/DoUrl.aspx?ABC=123 , 系统会在之后增加一些参数，这些参数叫系统参数。实际执行的url为。
- http://yourserver/App/DoUrl.aspx?ABC=123&UserNo=xy&SID=xxxxx&FK_Dept=1010&FK_Unit=10&EntityName=ND101&EntityPK=OID&EntityPKVal=12333& FK_Event=xxxxxx
`);this.PageTitle="表单向导"}Init(){return f(this,null,function*(){this.AddGroup("A","新建窗体"),this.SelectItemsByList("SelectType","图表分析",this.BuessUnit,!1,this.selectChartType());const o=`SELECT KeyOfEn as No,Name FROM Sys_MapAttr WHERE MyDataType IN (2,3,5,8) 
    AND LGType=0 AND FK_MapData='${this.RequestVal("FrmID")}'
     AND KeyOfEn NOT IN ('OID','FID','PWorkID','BillState','WFState','WFSta')
     UNION
     SELECT 'MyCountNum' as No, '条数' as Name FROM WF_Emp WHERE No='admin' 
     `,N=`SELECT KeyOfEn as No,Name FROM Sys_MapAttr WHERE MyDataType IN (2,1) 
     AND UIContralType in (1,3) AND FK_MapData='${this.RequestVal("FrmID")}' `;this.SelectItemsByList("SelectType.Group","分析内容",this.BuessUnit,!1,N),this.SelectItemsByList("SelectType.Group.NumField","分析数据",this.BuessUnit,!0,o),this.AddBlank("ChartPie","饼状图",this.BuessUnit),this.SelectItemsByList("ChartPie.Group","分析内容",this.BuessUnit,!1,N),this.SelectItemsByList("ChartPie.Group.NumField","分析数据",this.BuessUnit,!1,o),this.AddBlank("Table1","1维表格",this.Table1),this.SelectItemsByList("Table1.Group","分析内容",this.Table1,!1,N),this.SelectItemsByList("Table1.Group.NumField","分析数据",this.Table1,!0,o),this.AddBlank("Table2","二维表格",this.Table2),this.SelectItemsByList("Table1.Group","分析内容",this.Table2,!1,N),this.SelectItemsByList("Table1.Group.NumField","分析数据",this.Table2,!0,o),this.AddBlank("Table3","三维表格",this.Table3),this.SelectItemsByList("Table3.Group1","分析内容维度1",this.Table3,!1,N),this.SelectItemsByList("Table3.Group1.Group2","分析内容维度2",this.Table3,!1,N),this.SelectItemsByList("Table3.Group1.Group2.NumField","分析数据",this.Table3,!1,o)})}selectChartType(){return JSON.stringify([{No:B.ChartLine,Name:"折线图"},{No:B.ChartZZT,Name:"柱状图"}])}GenerEn(o,N,R){return f(this,null,function*(){const I=new G,d=o.split(",");for(let i=0;i<d.length;i++){const n=d[i];if(n==""||n==null)continue;const T=N+"_"+n,u=new L(T);if(yield u.Retrieve(),u.LGType==1){const F="C"+i+"Ens",m="SELECT IntKey as No, Lab as Name FROM Sys_Enum WHERE EnumKey= ~"+u.UIBindKey+"~ ";m.replace("~","'"),I.SetValByKey(F,m);continue}R=R.replaceAll(n+",",n+"T,")}return I.Docs=R,I})}Save_TextBox_X(o,N,R,I,d){return f(this,null,function*(){o=="SelectType.Group.NumField";const i=this.RequestVal("FrmID"),n=this.RequestVal("PageID"),T=new W(i);if(yield T.RetrieveFromDBSources(),o=="Table2.Group1.Group2.NumField"){const u=this.RequestVal("tb1","Table2.Group1.Group2.NumField"),F="分析:"+this.RequestVal("tb2","Table2.Group1")+","+this.RequestVal("tb2","Table2.Group1.Group2"),m=this.RequestVal("tb1","Table2.Group1")+","+this.RequestVal("tb1","Table2.Group1.Group2");let r=`SELECT ${m} , ${u.split(",").map(e=>"sum("+e+") as "+e).join(",")} FROM  ${T.PTable} WHERE 1=1 GROUP BY ${m} `;r=r.replace("sum(MyCountNum) as MyCountNum","count(*) as MyNum");const t=new G;t.PageID=n,t.Name=F,t.WinDocModel="Table",t.Icon="icon-grid";const l=new W(i);yield l.RetrieveFromDBSources();const a=m.split(",");for(let e=0;e<a.length;e++){const c=a[e];if(c==""||c==null)return;const p=i+"_"+c,S=new L(p);if(yield S.Retrieve(),S.LGType==1){const h="C"+e+"Ens",g="SELECT IntKey as No, Lab as Name FROM Sys_Enum WHERE EnumKey= ~"+S.UIBindKey+"~ ";g.replace("~","'"),t.SetValByKey(h,g);continue}r=r.replaceAll(c+",",c+"T,");const C="C"+e+"Ens";t.SetValByKey(C,` SELECT DISTINCT ${c} as No, ${c}T as Name FROM  ${l.PTable}  WHERE ${c}!='' `)}t.Docs=r;let y=this.RequestVal("tb2","Table2.Group1");y+=","+this.RequestVal("tb2","Table2.Group1.Group2"),y+=","+this.RequestVal("tb2","Table2.Group1.Group2.NumField"),t.Tag1=y,yield t.Insert();const s=P.UrlEn("TS.CCFast.Windows.WinTable",t.No);return new w(M.GoToUrl,s)}if(o=="Table1.Group.NumField"){const u=this.RequestVal("tb1","Table1.Group.NumField"),F="分析:"+this.RequestVal("tb2","Table1.Group.NumField"),m=this.RequestVal("tb1","Table1.Group");let r=`SELECT ${m} , ${u.split(",").map(s=>"sum("+s+") as "+s).join(",")} FROM  ${T.PTable} WHERE 1=1 GROUP BY ${m} `;r=r.replace("sum(MyCountNum) as MyCountNum","count(*) as MyNum");const t=new G;t.PageID=n,t.Name=F,t.WinDocModel="Table",t.Icon="icon-grid";const l=new W(i);yield l.RetrieveFromDBSources();const a=m.split(",");for(let s=0;s<a.length;s++){const e=a[s];if(e==""||e==null)return;const c=i+"_"+e,p=new L(c);if(yield p.Retrieve(),p.LGType==1){const C="C"+s+"Ens",h="SELECT IntKey as No, Lab as Name FROM Sys_Enum WHERE EnumKey= ~"+p.UIBindKey+"~ ";h.replace("~","'"),t.SetValByKey(C,h);continue}r=r.replaceAll(e+",",e+"T,"),r=r.replaceAll(e+",",e+"T,");const S="C"+s+"Ens";t.SetValByKey(S,"SELECT DISTINCT "+e+" as No,"+e+"T as Name FROM "+l.PTable+" WHERE 1=1 AND length("+e+") >1")}t.Docs=r,t.Tag1=this.RequestVal("tb2","Table1.Group")+","+this.RequestVal("tb2","Table1.Group.NumField"),yield t.Insert();const y=P.UrlEn("TS.CCFast.Windows.WinTable",t.No);return new w(M.GoToUrl,y)}if(o=="SelectType.Group.NumField"){const u=this.RequestVal("tb1","SelectType"),F=this.RequestVal("tb1","SelectType.Group.NumField"),m="分析:"+this.RequestVal("tb2","SelectType.Group"),r=this.RequestVal("tb1","SelectType.Group");let t=`SELECT ${r} , ${F.split(",").map(s=>"sum("+s+") as "+s).join(",")} FROM  ${T.PTable} WHERE 1=1 GROUP BY ${r} `;t=t.replace("sum(MyCountNum) as MyCountNum","count(*) as MyNum");const l=new W(i);yield l.RetrieveFromDBSources();const a=new G,y=r.split(",");for(let s=0;s<y.length;s++){const e=y[s];if(e==""||e==null)continue;const c=i+"_"+e,p=new L(c);if(yield p.Retrieve(),p.LGType==1){const C="C"+s+"Ens",h="SELECT IntKey as No, Lab as Name FROM Sys_Enum WHERE EnumKey= ~"+p.UIBindKey+"~ ";h.replace("~","'"),a.SetValByKey(C,h);continue}t=t.replaceAll(e+",",e+"T,");const S="C"+s+"Ens";a.SetValByKey(S,"SELECT DISTINCT "+e+" as No,"+e+"T as Name FROM "+l.PTable+" WHERE 1=1 AND length("+e+") >1")}if(a.Docs=t,u===B.ChartLine||u===B.ChartZZT){a.PageID=n,a.Name=m,a.WinDocModel=u,a.Icon="icon-fire",a.Docs=t,yield a.Insert();const s=P.UrlEn("TS.CCFast.Windows."+u,a.No);return new w(M.GoToUrl,s)}if(u===B.ChartPie){a.PageID=n,a.Name=m,a.WinDocModel=u,a.Icon="icon-fire",a.Docs=t,yield a.Insert();const s=P.UrlEn("TS.CCFast.Windows."+u,a.No);return new w(M.GoToUrl,s)}}if(o=="ChartPie.Group.NumField"){const u=this.RequestVal("tb1","ChartPie.Group.NumField"),F="分析:"+this.RequestVal("tb2","ChartPie.Group"),m=this.RequestVal("tb1","ChartPie.Group");let r=`SELECT ${m} , ${u.split(",").map(s=>"sum("+s+") as "+s).join(",")} FROM  ${T.PTable} WHERE 1=1 GROUP BY ${m} `;r=r.replace("sum(MyCountNum) as MyCountNum","count(*) as MyNum");const t=new W(i);yield t.RetrieveFromDBSources();const l=new G,a=m.split(",");for(let s=0;s<a.length;s++){const e=a[s];if(e==""||e==null)continue;const c=i+"_"+e,p=new L(c);if(yield p.Retrieve(),p.LGType==1){const C="C"+s+"Ens",h="SELECT IntKey as No, Lab as Name FROM Sys_Enum WHERE EnumKey= ~"+p.UIBindKey+"~ ";h.replace("~","'"),l.SetValByKey(C,h);continue}r=r.replaceAll(e+",",e+"T,");const S="C"+s+"Ens";l.SetValByKey(S,"SELECT DISTINCT "+e+" as No,"+e+"T as Name FROM "+t.PTable+" WHERE 1=1 AND length("+e+") >1")}l.Docs=r,l.PageID=n,l.Name=F,l.WinDocModel="ChartPie",l.Icon="icon-fire",l.Docs=r,yield l.Insert();const y=P.UrlEn("TS.CCFast.Windows.ChartPie",l.No);return new w(M.GoToUrl,y)}})}GenerSorts(){return f(this,null,function*(){return this.RefMainEnName==="TS.WF.Template.FlowExt"?Promise.resolve([{No:"FlowOnCreateWorkID",Name:"创建工作ID后"},{No:"FlowOverBefore",Name:"流程结束前"},{No:"FlowOverAfter",Name:"流程结束后"},{No:"BeforeFlowDel",Name:"流程删除前"},{No:"AfterFlowDel",Name:"流程删除后"}]):this.RefMainEnName==="TS.WF.Template.NodeExt"?Promise.resolve([{No:"WorkArrive",Name:"工作到达"},{No:"SendWhen",Name:"当节点发送前"},{No:"SendSuccess",Name:"节点发送成功时"},{No:"SendError",Name:"节点发送失败时"},{No:"ReturnBefore",Name:"当节点退回前"},{No:"ReturnAfter",Name:"退回后"},{No:"UndoneBefore",Name:"当节点撤销发送前"},{No:"UndoneAfter",Name:"当节点撤销发送后"},{No:"WhenReadWork",Name:"工作打开后"}]):this.RefMainEnName==="TS.Frm.MapFrmFool"?Promise.resolve([{No:"FrmLoadBefore",Name:"表单载入前"},{No:"FrmLoadAfter",Name:"节点表单载入后"},{No:"SaveBefore",Name:"当表单保存前"},{No:"SaveAfter",Name:"当表单保存后"},{No:"FrmLoadAfter",Name:"节点表单载入后"},{No:"DeleteBefore",Name:"当表单删除前"},{No:"DeleteAfter",Name:"当表单删除后"}]):Promise.resolve([])})}}export{J as GPN_WindowFrm};
