var q=Object.defineProperty;var X=(i,e,t)=>e in i?q(i,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):i[e]=t;var s=(i,e,t)=>X(i,typeof e!="symbol"?e+"":e,t);var f=(i,e,t)=>new Promise((l,n)=>{var r=D=>{try{u(t.next(D))}catch(A){n(A)}},T=D=>{try{u(t.throw(D))}catch(A){n(A)}},u=D=>D.done?l(D.value):Promise.resolve(D.value).then(r,T);u((t=t.apply(i,e)).next())});import{D as N,E as I,f as P,a_ as G,B as b,g as K,U as W,h as O,i as L,G as w,l as F,H as Y}from"./entry/index-C6uBgOW5-1730430676707.js";import{FrmAttr as S}from"./FrmAdm-DqTHICqI.js";import{MapDataAttr as a}from"./MapData-Ccsy8tbB.js";import{SysEvents as U,SysEventAttr as y}from"./SysEvent-D4PfKXg3.js";import{Collections as k,CollectionAttr as v}from"./Collection-C0bGHKA7.js";import{PG_Group2Method as x}from"./PG_Group2Method-C5y-BSQj.js";import{PageBaseGroupEdit as p}from"./PageBaseGroupEdit-IicyYiex.js";import{SearchFKEnums as H,SearchFKEnumAttr as V}from"./SearchFKEnum-CidaowCH.js";import{DBRoles as d}from"./DBRole-BA4YAefA.js";import{GPE_FrmType as j}from"./GPE_FrmType-BPAZNZeD.js";import{FlowDevModel as ee}from"./EnumLab-CzismWql.js";import{Flow as E}from"./Flow-D4nUES4A.js";import{SelfCheck as te}from"./SelfCheck-Dde0SsBc.js";import{ByEmpNo as re}from"./ByEmpNo-Bq18vxbq.js";import{GloComm as le}from"./GloComm-CmAl8MpM.js";import{b as ne}from"./antd-Dd9L3uAF.js";import{MapAttrs as z}from"./MapAttr-B1mxD3vP.js";class M extends p{constructor(){super("GPE_DTSearchWay");s(this,"Desc0",`
  #### 帮助
  - 按照选择的日期型字段进行查询。
  - 在报表中根据 like 日期字段值 进行查询。
  
  #### 效果图
  ![输入图片说明](/resource/CCBill/SearchCond/SearchKeyData.png "屏幕截图.png")  
 `);s(this,"Desc1",`
  #### 帮助
  - 按照选择的日期型字段进行查询。
  - 在报表中根据 like 日期时间值 进行查询。
  `);this.PageTitle="日期查询"}AfterSave(t,l){if(t==l)return null}BtnClick(t,l,n){}Init(){this.entity=new m,this.KeyOfEn="DTSearchWay",this.AddGroup("A","日期查询"),this.Blank("0","不启用",this.Desc0);const t=`SELECT KeyOfEn as No,Name FROM Sys_MapAttr 
    WHERE FK_MapData='${this.PKVal}' AND UIVisible=1 
     AND (MyDataType=6 or MyDataType=7)  AND UIContralType=0 `;this.SelectItemsByList("1","按日期查询",this.Desc0,!0,t,"DTSearchKey",""),this.SelectItemsByList("2","按日期时间查询",this.Desc1,!0,t,"DTSearchKey","")}}const Me=Object.freeze(Object.defineProperty({__proto__:null,GPE_DTSearchWay:M},Symbol.toStringTag,{value:"Module"}));class C extends p{constructor(){super("GPE_SearchHiden");s(this,"Desc0",`
  #### 帮助
  - 隐藏字段查询条件:即是利用隐藏字段的查询条件对表单数据进行过滤。
  - 显示出符合条件的数据列表。
  - 不启用：不启用隐藏字段查询。
  
  #### 效果图
  ![输入图片说明](/src/resource/CCBill/SearchCond/SearchHiden.png "屏幕截图.png") 
 `);s(this,"Desc1",`
  #### 帮助
  - 利用隐藏字段条件，来过滤表单数据。查询出符合条件的数据。
  1.数据源类型按SQL查询：
  WFState=1 多个条件： WFState=1 AND Starter='__WebUser.No'
  支持_+表达式变量.
  2.数据源类型按URL请求查询(仅支持Post请求的方式)；
  隐藏的条件可以增加 &UserNo=_WebUser.No&WFState=1
  3.数据源类型按存储过程请求查询：
  WFState=1 多个条件： Starter='__WebUser.No',WFState=1
  支持_+表达式变量
  #### 配置图
  ![输入图片说明](/resource/CCBill/SearchCond/SearchHidenSetting.png "屏幕截图.png")  
  #### 效果图
  ![输入图片说明](/resource/CCBill/SearchCond/SearchHiden.png "屏幕截图.png")  
  `);this.PageTitle="隐藏查询条件"}AfterSave(t,l){if(t==l)return null}BtnClick(t,l,n){}Init(){this.entity=new m,this.KeyOfEn="HidenWay",this.AddGroup("A","隐藏查询条件"),this.Blank("0","不启用",this.Desc0),this.SingleTB("1","启用隐藏字段查询条件.","HidenField",this.Desc1,"输入隐藏查询条件",N.AppString)}}const Ce=Object.freeze(Object.defineProperty({__proto__:null,GPE_SearchHiden:C},Symbol.toStringTag,{value:"Module"}));class _ extends p{constructor(){super("GPE_ListShowWay");s(this,"Desc0",`
  #### 帮助
  - 展现方式,也称为数据的呈现方式.
  - 系统提供两种展现方式，经典列表模式与树干叶子模式.
  - 
 `);s(this,"Desc1",`
  #### 帮助
  - 请选择列表字段, 只有外键或者枚举，外部数据源字段才能出现在列表中.
  `);this.PageTitle="展现方式"}AfterSave(t,l){if(t==l)return null}BtnClick(t,l,n){}Init(){this.entity=new m,this.KeyOfEn="ListShowWay",this.AddGroup("A","展现方式"),this.Blank("0","经典列表模式",this.Desc0);const t=`SELECT KeyOfEn as No,Name FROM Sys_MapAttr 
    WHERE FK_MapData='${this.PKVal}' AND UIVisible=1 
     AND  UIContralType=1`;this.SelectItemsByList("1","树干叶子模式",this.Desc1,!1,t,"ListShowKey",""),this.Blank("2","级联模式(未解析)",this.Desc0)}}const _e=Object.freeze(Object.defineProperty({__proto__:null,GPE_ListShowWay:_},Symbol.toStringTag,{value:"Module"}));class J extends p{constructor(){super("GPE_TableStyle");s(this,"Desc0",`
  #### 帮助
  - 不设置：表格内容换行显示。
  #### 效果图
  ![输入图片说明](/resource/CCBill/SearchCond/TableNoSetting.png "屏幕截图.png") 
 `);s(this,"Desc1",`
  #### 帮助
  - 表格内容提示框显示:表格内容不进行换行,溢出隐藏,鼠标移入提示框显示表格内隐藏的内容.
  #### 效果图
  ![输入图片说明](/resource/CCBill/SearchCond/TableSetting.png "屏幕截图.png")  
  `);this.PageTitle="表格内容展示形式"}AfterSave(t,l){if(t==l)return null}BtnClick(t,l,n){}Init(){this.entity=new m,this.KeyOfEn="TableStyle",this.AddGroup("A","表格内容展示类型"),this.Blank("0","不设置",this.Desc0),this.Blank("1","表格内容提示框显示",this.Desc1)}}const Re=Object.freeze(Object.defineProperty({__proto__:null,GPE_TableStyle:J},Symbol.toStringTag,{value:"Module"}));class $ extends p{constructor(){super("GPE_SearchDictTreeModel");s(this,"Desc0",`
  #### 帮助
   - 显示所有的列.
   - 这些列是按照实体的Attr是否可见与顺序决定的.
`);s(this,"Desc1",`
  #### 帮助
  - 选择要显示的列.
  - 系统列出的是所有可以显示的列.
  `);this.PageTitle="树结构"}Init(){return f(this,null,function*(){this.entity=new m,this.KeyOfEn="ShowColTree",this.AddGroup("A","树结构显示"),this.Blank("0","不显示",this.Desc0),this.SelectItemsByList("1","显示指定的外键字段",this.Desc1,!0,yield this.GenerAttrs(),"ColTree")})}GenerAttrs(){return f(this,null,function*(){const t=new z;yield t.Retrieve("FK_MapData",this.PKVal,"UIContralType",1);const l=t.filter(n=>!!n.UIVisible).map(n=>({Name:n.Name,No:n.KeyOfEn}));return JSON.stringify(l)})}AfterSave(t,l){if(t==l)throw new Error("Method not implemented.")}BtnClick(t,l,n){if(t==l||t===n)throw new Error("Method not implemented.")}}const Ee=Object.freeze(Object.defineProperty({__proto__:null,GPE_SearchDictTreeModel:$},Symbol.toStringTag,{value:"Module"}));class c extends S{}s(c,"WZ","WZ"),s(c,"SortBy","SortBy");class m extends I{constructor(e){super("TS.CCBill.FrmDict"),e&&(this.No=e)}get HisUAC(){const e=new W;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new O("Sys_MapData","实体表单");e.GroupBarShowModel=1,e.AddGroupAttr("基本信息"),e.AddTBStringPK(a.No,null,"表单编号",!0,!0,1,190,20),e.SetHelperAlert(a.No,"也叫表单ID,系统唯一."),e.AddDDLSysEnum(a.FrmType,0,"表单类型",!0,!0,"BillFrmType","@0=经典表单@1=自由表单@8=开发者表单@10=章节表单@6=VSTO表单"),e.AddTBString(a.PTable,null,"存储表",!0,!1,0,500,20,!0),e.SetHelperAlert(a.PTable,"存储的表名,如果您修改一个不存在的系统将会自动创建一个表."),e.AddTBString(a.Name,null,"表单名称",!0,!1,0,200,20,!0),e.AddGroupAttr("列表属性"),e.AddDDLSysEnum(S.RowOpenModel,2,"行记录打开模式",!0,!0,"RowOpenMode","@0=新窗口打开@1=在本窗口打开@2=弹出窗口打开,关闭后不刷新列表@3=弹出窗口打开,关闭后刷新列表");let t="@0=MyDictFrameWork.htm 实体与实体相关功能编辑器";t+="@1=MyDict.htm 实体编辑器",t+="@2=MyBill.htm 单据编辑器",t+="@9=自定义URL",e.AddDDLSysEnum("SearchDictOpenType",0,"双击行打开内容",!0,!0,"SearchDictOpenType",t),e.AddBoolean("IsSelectMore",!0,"是否下拉查询条件多选?",!0,!0),e.AddDDLSysEnum("ListDtlShowWay",0,"从表展现",!0,!0,"ListDtlShowWay","@0=不展现@1=展现"),e.AddTBString("UrlExt",null,"要打开的Url",!0,!1,0,500,60,!0),e.AddTBInt(S.PopHeight,500,"弹窗高度",!0,!1),e.AddTBInt(S.PopWidth,760,"弹窗宽度",!0,!1),e.AddDDLSysEnum(a.TableCol,0,"表单显示列数",!1,!1,"TableCol","@0=4列@1=6列"),e.AddTBString(o.SortColumns,null,"排序字段",!0,!1,0,100,20,!1),e.AddDDLStringEnum(o.SortBy,"ASC","排序方式","@ASC=正序@DESC=倒序",!0),e.AddTBString(o.ColorSet,null,"表格列颜色设置",!0,!1,0,100,20,!0);let l="对字段的颜色处理";l+=`	
 对于数值字段: @Age:From=0,To=18,Color=green;From=19,To=30,Color=red`,l+=`	
 对于枚举字段: @XB:From=0,To=0,Color=green;From=1,To=1,Color=red`,e.SetHelperAlert(o.ColorSet,l),e.AddTBString(o.RowColorSet,null,"表格行颜色设置",!0,!1,0,100,20,!0),e.SetHelperAlert(o.RowColorSet,"按照指定字段存储的颜色设置表格行的背景色,比如:MyField字段"),e.AddTBString(o.FieldSet,null,"字段求和求平均设置",!0,!1,0,100,20,!0),e.AddTBString("ForamtFunc",null,"字段格式化函数",!0,!1,0,200,60,!0);let n="对字段的显示使用函数进行处理";n+=`	
 1. 对于字段内容需要处理后在输出出来.`,n+=`	
 2. 比如：原字段内容 @zhangsa,张三@lisi,李四 显示的内容为 张三,李四`,n+=`	
 3. 配置格式: 字段名@函数名; 比如:  FlowEmps@DealFlowEmps; `,n+=`	
 4. 函数写入到 /DataUser/JSLibData/SearchSelf.js`,e.SetHelperAlert("ForamtFunc",n),e.AddGroupAttr("实体表单"),e.AddDDLSysEnum(c.EntityType,0,"业务类型",!0,!1,c.EntityType,"@0=独立表单@1=单据@2=编号名称实体@3=树结构实体@5=实体EntityNoName"),e.SetHelperAlert(c.EntityType,"该实体的类型,@0=单据@1=编号名称实体@2=树结构实体@5=实体EntityNoName"),e.AddTBString(c.BillNoFormat,null,"实体编号规则",!0,!1,0,10,20,!0),e.SetHelperAlert(c.BillNoFormat,`	
实体编号规则: 	
 2标识:01,02,03等, 3标识:001,002,003,等..`),e.AddGroupAttr("设计者信息"),e.AddTBString(a.Designer,null,"设计者",!0,!1,0,500,20),e.AddTBString(a.DesignerContact,null,"联系方式",!0,!1,0,500,20),e.AddTBString(a.DesignerUnit,null,"单位",!0,!1,0,500,20,!0),e.AddTBString(a.GUID,null,"GUID",!0,!0,0,128,20,!1),e.AddTBString(a.Ver,null,"版本号",!0,!0,0,30,20),e.AddTBStringDoc(a.Note,null,"备注",!0,!1,!0),e.AddTBInt(a.Idx,100,"序号",!1,!1),e.AddTBAtParas(3e3),e.AddTBString(c.Tag0,null,"Tag0",!1,!1,0,500,20),e.AddTBString(c.Tag1,null,"Tag1",!1,!1,0,4e3,20),e.AddTBString(c.Tag2,null,"Tag2",!1,!1,0,500,20),e.AddTBInt(S.ShowColModel,0,"ShowColModel",!1,!1),e.AddTBString(c.ShowCols,null,"ShowCols",!1,!1,0,500,20),e.AddTBInt("ShowColTree",0,"是否树模式?",!1,!1),e.AddTBString("ColTree",null,"树字段",!1,!1,0,50,20),e.AddTBInt(S.IsSearchKey,0,"IsSearchKey",!1,!1),e.AddTBString("StringSearchKeys",null,"StringSearchKeys",!1,!1,0,500,20),e.AddTBString("TBSearchKeyPlaceholder",null,"查询关键字",!1,!1,0,500,20),e.AddTBInt("DTSearchWay",0,"DTSearchWay",!1,!1),e.AddTBInt("DTShowWay",0,"DTShowWay",!1,!1),e.AddTBString("DTSearchKey",null,"DTSearchKey",!1,!1,0,500,20),e.AddTBInt("DDLShowWays",0,"DDLShowWays",!1,!1),e.AddTBInt("ListShowWay",0,"列表展现",!1,!1),e.AddTBString("ListShowKey",null,"字段",!1,!1,0,50,20),e.AddTBString("RptSearchKeys",null,"RptSearchKeys",!1,!1,0,50,20),e.AddTBInt("HidenWay",0,"是否启用隐藏条件?",!1,!1),e.AddTBString("HidenField",null,"隐藏查询字段",!1,!1,0,500,20),e.AddTBInt("TableStyle",0,"表格内容展示类型",!1,!1),e.ParaFields=",ShowColTree,ColTree,HidenWay,IsSearchKey,StringSearchKeys,RptSearchKeys,DTSearchWay,DTSearchKey,DDLShowWays,TBSearchKeyPlaceholder,HidenField,",e.AddGroupMethod("单记录"),e.AddRM_PG(new x,"icon-drop"),e.AddRM_EnOnly("常规按钮","TS.CCBill.FrmDictBtn","@No","icon-drop"),e.AddRM_GPE(new j,"icon-note"),e.AddRM_DtlSearch("表单事件",new U,y.RefPKVal,"","",y.ShowAttrs,"icon-energy");const r=new L;return r.Title="转成EntityNoName",r.ClassMethod="Turn2FrmEntityNoName",r.RefMethodType=P.Func,r.Warning="目前的实体是主键是OID,要兼容高代码，把实体设置为No 主键的实体吗？",r.IsCanBatch=!1,r.IsForEns=!1,e.AddRefMethod(r),e.AddGroupMethod("列表(查询)"),e.AddRM_DtlSearch("列表功能",new k,v.FrmID,"","","Name,IsEnable,MethodModel,Icon,IsZD,","icon-film",!0,"",G.Left),e.AddRM_GPE(new _,"icon-grid"),e.AddRM_UrlTabOpen("多表头","/@/WF/views/Comm/MultiTitle.vue?DoType=Dict"),e.AddRM_GPE(new R),e.AddRM_GPE(new $),e.AddGroupMethod("查询条件"),e.AddRM_DtlSearch("外键枚举查询条件",new H,V.FrmID,"","","","icon-drop",!0),e.AddRM_GPE(new M,"icon-drop"),e.AddRM_GPE(new C,"icon-drop"),e.AddGroupMethod("表格内容"),e.AddRM_GPE(new J,"icon-drop"),e.AddGroupMethod("编程-翻译中"),e.AddRM_Func("功能页面接口","CCFormAPI1","","icon-user"),e.AddRM_Func("JS接口","CCFormAPI2","","icon-user"),e.AddRM_Func("后台接口","CCFormAPI3","","icon-user"),e.AddGroupMethod("数据权限"),e.AddRM_DtlSearch("列表权限",new d,"FrmID","","","","icon-settings",!1,"&DBRole=DBList"),e.AddRM_DtlSearch("新建-Button",new d,"FrmID","","","","icon-settings",!1,"&DBRole=RecNew"),e.AddRM_DtlSearch("删除-Button",new d,"FrmID","","","","icon-settings",!1,"&DBRole=RecDelete"),e.AddRM_DtlSearch("保存-Button",new d,"FrmID","","","","icon-settings",!1,"&DBRole=RecSave"),e.AddRM_DtlSearch("归档-Button",new d,"FrmID","","","","icon-settings",!1,"&DBRole=RecFiling"),e.AddRM_DtlSearch("导入-Button",new d,"FrmID","","","","icon-settings",!1,"&DBRole=EnsImp"),e.AddRM_DtlSearch("导出-Button",new d,"FrmID","","","","icon-settings",!1,"&DBRole=ExpExcel"),e.AddMapLoader(()=>{e.AddGroupMethod("大屏设计"),e.AddRM_UrlTabOpen("大屏设计","/src/CCFast/Views/RptWhiteEdit.vue?FrmID="+this.No+"&PageID=FrmDict"+this.No,"icon-film")}),this._enMap=e,this._enMap}Turn2FrmEntityNoName(){return f(this,null,function*(){const e=new b("BP.Sys.MapData",this.No);return e.setPK(this.No),yield e.RetrieveFromDBSources(),e.DoMethodReturnJSON("Turn2EntityNoName")})}CCFormAPI(){return"tabOpen@"+`
    #### 帮助
    -  ccform提供两个类的接口， 功能页面调用与
    #### 新建接口
    - 新建一实体记录的链接. 
    - /WF/Port.vue?DoWhat=NewFrmDictRec&FrmID=xxxx
    - 打开一实体记录的链接. 
    - /WF/Port.vue?DoWhat=OpenFrmDictRec&FrmID=xxxx&OID=xxxx
    `}}class oe extends K{get GetNewEntity(){return new m}constructor(){super()}}const Ne=Object.freeze(Object.defineProperty({__proto__:null,FrmDict:m,FrmDictAttr:c,FrmDicts:oe},Symbol.toStringTag,{value:"Module"}));class R extends p{constructor(){super("GPE_SearchDictShowCol");s(this,"Desc0",`
  #### 帮助
   - 显示所有的列.
   - 这些列是按照实体的Attr是否可见与顺序决定的.
`);s(this,"Desc1",`
  #### 帮助
  - 选择要显示的列.
  - 系统列出的是所有可以显示的列.
  `);this.PageTitle="显示列"}Init(){return f(this,null,function*(){this.entity=new m,this.KeyOfEn=c.ShowColModel,this.AddGroup("A","显示列"),this.Blank("0","所有的列",this.Desc0),this.SelectItemsByList("1","指定的选择列",this.Desc1,!0,yield this.GenerAttrs(),c.ShowCols)})}GenerAttrs(){return f(this,null,function*(){const t=new z;yield t.Retrieve("FK_MapData",this.PKVal);const l=t.filter(n=>!!n.UIVisible).map(n=>({Name:n.Name,No:n.KeyOfEn}));return JSON.stringify(l)})}AfterSave(t,l){if(t==l)throw new Error("Method not implemented.")}BtnClick(t,l,n){if(t==l||t===n)throw new Error("Method not implemented.")}}const Ie=Object.freeze(Object.defineProperty({__proto__:null,GPE_SearchDictShowCol:R},Symbol.toStringTag,{value:"Module"}));class Q extends p{constructor(){super("GPE_BillCheckModel");s(this,"ByFlowNo",`  
  #### 帮助
  - 如果要绑定的流程为空，请按照要求设计流程，阅读<被启动的流程设计注意事项>
  - 使用流程审批单据, 列表上点新建按钮，就启动流程.
  #### 被启动的流程设计注意事项.
  1. 该流程每个节点都绑定当前单据,使用绑定表单库的表单.
  2. 设置流程业务数据存储表，与当前单据的存储表保持一致.
  3. 该流程单据编号生成规则与单据编号规则一致.
  #### 应用描述
  1. 操作员在列表上点新建，就创建流程,并启动流程, 这个时间单据的状态是BillState, CheckStart=3 开始审核状态.
  1. 如果发送下去: 该BillState状态 Checking =4 审批状态.
  1. 审批完毕后,就是归档状态.Over=100
  `);s(this,"SelfCheck",`  
  #### 帮助
  - 自定义审核: 是单据的创建人，自定义审核人的路径.
  `);s(this,"Desc0",`  
  #### 帮助
  - 不审核，仅仅实现单据的增删改查,统计分析,登记所用.
  - 比如： 出门单，出库单，不需要审核.
  - 单据的状态: 0=空白，1=草稿，2=编辑中, 100=归档.
  `);s(this,"ByEmpNos",`
  #### 帮助
  - 按照设置人员账号的顺序进行审核.
  `);s(this,"BySQL",`
  #### 帮助
  - 按照SQL语句获得人员顺序进行审核.
  `);this.PageTitle="单据审核模式"}Init(){this.entity=new g,this.KeyOfEn="BillCheckModel",this.Btns=[{pageNo:"ByFlowNo",list:["设计流程"]}],this.AddGroup("A","简易模式"),this.Blank("None","不审核",this.Desc0),this.AddEntity("SelfCheck","自定义审核",new te,this.SelfCheck,""),this.AddEntity("BySettingEmpNos","固定人员审核",new re,this.ByEmpNos,""),this.AddGroup("B","二开模式"),this.SingleTB("BySQL","按照SQL设置审核人员","BillCheckTag",this.BySQL,"请输入SQL表达式，返回No,Name两个列的人员集合.",N.AppString),this.Blank("ByAPI","外部程序调用",this.SelfCheck),this.AddGroup("Z","流程模式");const t=`SELECT No,Name,FK_FlowSort GroupNo FROM WF_Flow  WHERE FrmUrl='${this.RefPKVal}' Order By Idx`;this.SelectItemsByList("ByFlowNo","绑定流程审核",this.ByFlowNo,!0,t,"BillCheckTag","BillCheckTagT")}BtnClick(t,l,n){return f(this,null,function*(){if(n=="设计流程"){const r=new g;if(r.No=this.RefPKVal,(yield r.RetrieveFromDBSources())==0){alert("错误，没有查询到编号:"+r.No+"的表单.");return}if(r.BillCheckModel!="ByFlowNo"&&(r.BillCheckModel="ByFlowNo",yield r.Update()),r.BillCheckTag.includes(",")==!0){const h=le.UrlGPN("GPN_BillCheckEditFlow","&FrmID="+r.No);return new w(F.OpenUrlByDrawer30,h)}const u=new E;if(u.No=r.BillCheckTag,(yield u.RetrieveFromDBSources())==0){if(window.confirm("目前没有绑定流程编号，您想创建一个新流程吗?")==!1)return}else{u.SetValByKey("PTable",r.PTable),u.FrmUrl=r.No,u.BillNoFormat=r.BillNoFormat,u.IsCanStart=0,yield u.Update();const h="/#/WF/Designer/EditFlow?FlowNo="+u.No+"&FK_Flow="+u.No;return new w(F.GoToUrl,h)}const A=new Y("BP.WF.HttpHandler.WF_Admin_CCBPMDesigner_FlowDevModel");A.AddPara("SortNo",""),A.AddPara("FlowName",r.Name),A.AddPara("FlowDevModel",ee.RefOneFrmTree),A.AddPara("FrmUrl",r.No),A.AddPara("FrmPK",r.No);const B=yield A.DoMethodReturnString("FlowDevModel_Save");if(B==null||B==null)return ne.info("创建失败:"+B),null;{alert("该单据的审核流程已经创建,模板编号为:"+B);const h=new E;h.No=B,yield h.RetrieveFromDBSources(),h.SetValByKey("PTable",r.PTable),h.FrmUrl=r.No,h.BillNoFormat=r.BillNoFormat,h.IsCanStart=0,yield h.Update(),r.BillCheckModel="ByFlowNo",r.BillCheckTag=B,yield r.Update()}const Z="/#/WF/Designer/EditFlow?FlowNo="+B+"&FK_Flow="+B;return new w(F.GoToUrl,Z)}})}AfterSave(t,l){return f(this,null,function*(){})}}const Pe=Object.freeze(Object.defineProperty({__proto__:null,GPE_BillCheckModel:Q},Symbol.toStringTag,{value:"Module"}));class o extends S{}s(o,"RefDict","RefDict"),s(o,"SortBy","SortBy");class g extends I{constructor(e){super("TS.CCBill.FrmBill"),e&&(this.No=e)}get HisUAC(){const e=new W;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new O("Sys_MapData","单据表单");e.GroupBarShowModel=1,e.AddGroupAttr("基本信息"),e.AddTBStringPK(a.No,null,"表单编号",!0,!0,1,190,20),e.SetHelperAlert(a.No,"也叫表单ID,系统唯一."),e.AddTBString(a.PTable,null,"存储表",!0,!1,0,500,20,!0),e.SetHelperAlert(a.PTable,"存储的表名,如果您修改一个不存在的系统将会自动创建一个表."),e.AddTBString(a.Name,null,"表单名称",!0,!1,0,200,20,!0),e.AddGroupAttr("列表属性"),e.AddDDLSysEnum(S.RowOpenModel,2,"行记录打开模式",!0,!0,"RowOpenMode","@0=新窗口打开@1=在本窗口打开@2=弹出窗口打开,关闭后不刷新列表@3=弹出窗口打开,关闭后刷新列表");let t="@0=MyDictFrameWork.htm 单据与单据相关功能编辑器";t+="@1=MyDict.htm 单据编辑器",t+="@2=MyBill.htm 单据编辑器",t+="@9=自定义URL",e.AddDDLSysEnum("SearchDictOpenType",0,"双击行打开内容",!0,!0,"SearchDictOpenType",t),e.AddBoolean("IsSelectMore",!0,"是否下拉查询条件多选?",!0,!0),e.AddTBString("UrlExt",null,"要打开的Url",!0,!1,0,500,60,!0),e.AddTBInt(S.PopHeight,500,"弹窗高度",!0,!1),e.AddTBInt(S.PopWidth,760,"弹窗宽度",!0,!1),e.AddDDLSysEnum(a.TableCol,0,"表单显示列数",!1,!1,"TableCol","@0=4列@1=6列"),e.AddTBString(o.SortColumns,null,"排序字段",!0,!1,0,100,20,!1),e.AddDDLStringEnum(o.SortBy,"ASC","排序方式","@ASC=正序@DESC=倒序",!0),e.AddTBString(o.ColorSet,null,"表格列颜色设置",!0,!1,0,100,20,!0);let l="对字段的颜色处理";l+=`	
 @Age:From=0,To=18,Color=green;From=19,To=30,Color=red`,e.SetHelperAlert(o.ColorSet,l),e.AddTBString(o.RowColorSet,null,"表格行颜色设置",!0,!1,0,100,20,!0),e.SetHelperAlert(o.RowColorSet,"按照指定字段存储的颜色设置表格行的背景色"),e.AddTBString(o.FieldSet,null,"字段求和求平均设置",!0,!1,0,100,20,!0),e.AddTBString("ForamtFunc",null,"字段格式化函数",!0,!1,0,200,60,!0);let n="对字段的显示使用函数进行处理";n+=`	
 1. 对于字段内容需要处理后在输出出来.`,n+=`	
 2. 比如：原字段内容 @zhangsa,张三@lisi,李四 显示的内容为 张三,李四`,n+=`	
 3. 配置格式: 字段名@函数名; 比如:  FlowEmps@DealFlowEmps; `,n+=`	
 4. 函数写入到 /DataUser/JSLibData/SearchSelf.js`,e.SetHelperAlert("ForamtFunc",n),e.AddGroupAttr("单据属性"),e.AddDDLSysEnum(a.FrmType,0,"表单类型",!0,!0,"BillFrmType","@0=经典表单@1=自由表单@8=开发者表单@10=章节表单@6=VSTO表单"),e.AddTBString(o.BillNoFormat,null,"单号规则",!0,!1,0,100,20,!0),e.SetHelperUrl("BillNoFormat","https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3953012&doc_id=31094"),e.AddTBString(o.TitleRole,null,"标题生成规则",!0,!1,0,100,20,!0),e.SetHelperUrl("TitleRole","https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661872&doc_id=31094"),e.AddTBString(o.SortColumns,null,"排序字段",!0,!1,0,100,20,!0),e.AddTBString(o.FieldSet,null,"字段求和求平均设置",!0,!1,0,100,20,!0),e.AddTBString(o.RefDict,null,"单据关联的单据",!1,!0,0,190,20,!0),e.AddTBString(o.BtnRefBill,"关联单据","关联单据",!0,!1,0,50,20),e.AddDDLSysEnum(S.RefBillRole,0,"关联单据工作模式",!0,!0,"RefBillRole","@0=不启用@1=非必须选择关联单据@2=必须选择关联单据"),e.AddTBString(o.RefBill,null,"关联单据ID",!0,!1,0,100,20,!0),e.SetHelperAlert(o.RefBill,`请输入单据编号,多个单据编号用逗号分开.	
比如:Bill_Sale,Bill_QingJia`),e.AddTBString("BillCheckModel","None","审核模式",!1,!1,0,20,10,!0,null),e.AddTBString("BillCheckTag",null,"审核内容",!1,!1,0,300,10,!0,null),e.AddGroupAttr("设计者信息"),e.AddTBString(a.Designer,null,"设计者",!0,!1,0,500,20),e.AddTBString(a.DesignerContact,null,"联系方式",!0,!1,0,500,20),e.AddTBString(a.DesignerUnit,null,"单位",!0,!1,0,500,20,!0),e.AddTBString(a.GUID,null,"GUID",!0,!0,0,128,20,!1),e.AddTBString(a.Ver,null,"版本号",!0,!0,0,30,20),e.AddTBStringDoc(a.Note,null,"备注",!0,!1,!0),e.AddTBInt(a.Idx,100,"序号",!1,!1),e.AddTBAtParas(3e3),e.AddTBString(o.Tag0,null,"Tag0",!1,!1,0,500,20),e.AddTBString(o.Tag1,null,"Tag1",!1,!1,0,4e3,20),e.AddTBString(o.Tag2,null,"Tag2",!1,!1,0,500,20),e.AddTBInt(S.ShowColModel,0,"ShowColModel",!1,!1),e.AddTBString(o.ShowCols,null,"ShowCols",!1,!1,0,500,20),e.AddTBInt(S.IsSearchKey,0,"IsSearchKey",!1,!1),e.AddTBString("StringSearchKeys",null,"StringSearchKeys",!1,!1,0,500,20),e.AddTBString("TBSearchKeyPlaceholder",null,"查询关键字",!1,!1,0,500,20),e.AddTBInt("DTSearchWay",0,"DTSearchWay",!1,!1),e.AddTBInt("DTShowWay",0,"DTShowWay",!1,!1),e.AddTBString("DTSearchKey",null,"DTSearchKey",!1,!1,0,500,20),e.AddTBInt("DDLShowWays",0,"DDLShowWays",!1,!1),e.AddTBInt("ListShowWayKey",0,"展现方式",!1,!1),e.AddTBString("RptSearchKeys",null,"RptSearchKeys",!1,!1,0,50,20),e.AddTBInt("HidenWay",0,"是否启用隐藏条件?",!1,!1),e.AddTBString("HidenField",null,"隐藏查询字段",!1,!1,0,500,20),e.ParaFields=",HidenWay,IsSearchKey,StringSearchKeys,RptSearchKeys,DTSearchWay,DTSearchKey,DDLShowWays,TBSearchKeyPlaceholder,",e.AddGroupMethod("单记录"),e.AddRM_PG(new x,"icon-drop"),e.AddRM_EnOnly("常规按钮","TS.CCBill.FrmDictBtn","@No","icon-drop"),e.AddRM_GPE(new j,"icon-note"),e.AddRM_GPE(new Q,"icon-check"),e.AddRM_DtlSearch("表单事件",new U,y.RefPKVal,"","",y.ShowAttrs,"icon-energy");const r=new L;return r.Title="FrmBill转成EntityNoName",r.ClassMethod="FrmBillTurn2FrmEntityNoName",r.RefMethodType=P.Func,r.Warning="目前的单据是主键是OID,要兼容高代码,把单据设置为No主键的实体吗？",r.IsCanBatch=!1,r.IsForEns=!1,e.AddRefMethod(r),e.AddGroupMethod("列表(查询)"),e.AddRM_DtlSearch("列表功能",new k,v.FrmID,"","","Name,IsEnable,MethodModel,Icon,IsZD,","icon-film",!0,"",G.Left),e.AddRM_GPE(new _,"icon-grid"),e.AddRM_UrlTabOpen("多表头","/@/WF/views/Comm/MultiTitle.vue?DoType=Dict"),e.AddRM_GPE(new R),e.AddGroupMethod("查询条件"),e.AddRM_DtlSearch("外键枚举查询条件",new H,V.FrmID,"","","","icon-drop",!0),e.AddRM_GPE(new M,"icon-drop"),e.AddRM_GPE(new C,"icon-drop"),e.AddGroupMethod("数据&按钮权限"),e.AddRM_DtlSearch("列表权限",new d,"FrmID","","","","icon-settings",!1,"&DBRole=DBList"),e.AddRM_DtlSearch("新建-Button",new d,"FrmID","","","","icon-settings",!1,"&DBRole=RecNew"),e.AddRM_DtlSearch("删除-Button",new d,"FrmID","","","","icon-settings",!1,"&DBRole=RecDelete"),e.AddRM_DtlSearch("保存-Button",new d,"FrmID","","","","icon-settings",!1,"&DBRole=RecSave"),e.AddRM_DtlSearch("归档-Button",new d,"FrmID","","","","icon-settings",!1,"&DBRole=RecFiling"),e.AddRM_DtlSearch("提交审核-Button",new d,"FrmID","","","","icon-settings",!1,"&DBRole=SubmitCheck"),e.AddRM_DtlSearch("导入-Button",new d,"FrmID","","","","icon-settings",!1,"&DBRole=EnsImp"),e.AddRM_DtlSearch("导出-Button",new d,"FrmID","","","","icon-settings",!1,"&DBRole=ExpExcel"),e.AddGroupMethod("编程-翻译中"),e.AddRM_Func("功能页面接口","CCFormAPI1","","icon-user"),e.AddRM_Func("JS接口","CCFormAPI2","","icon-user"),e.AddRM_Func("后台接口","CCFormAPI3","","icon-user"),this._enMap=e,this._enMap}FrmBillTurn2FrmEntityNoName(){return f(this,null,function*(){const e=new b("BP.Sys.MapData",this.No);return e.setPK(this.No),yield e.RetrieveFromDBSources(),e.DoMethodReturnJSON("FrmBillTurn2EntityNoName")})}CCFormAPI(){return"tabOpen@"+`
    #### 帮助
    -  ccform提供两个类的接口， 功能页面调用与
    #### 新建接口
    - 新建一单据记录的链接. 
    - /WF/Port.vue?DoWhat=NewFrmBillRec&FrmID=xxxx
    - 打开一单据记录的链接. 
    - /WF/Port.vue?DoWhat=OpenFrmBillRec&FrmID=xxxx&OID=xxxx
    `}}class ae extends K{get GetNewEntity(){return new g}constructor(){super()}}const Ge=Object.freeze(Object.defineProperty({__proto__:null,FrmBill:g,FrmBillAttr:o,FrmBills:ae},Symbol.toStringTag,{value:"Module"}));export{m as F,M as G,C as a,o as b,g as c,_ as d,R as e,$ as f,Me as g,Ce as h,_e as i,Re as j,Ee as k,Ne as l,Ie as m,Pe as n,Ge as o};
