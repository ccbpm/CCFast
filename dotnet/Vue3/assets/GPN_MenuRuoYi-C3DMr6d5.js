var W=Object.defineProperty;var v=(h,C,n)=>C in h?W(h,C,{enumerable:!0,configurable:!0,writable:!0,value:n}):h[C]=n;var m=(h,C,n)=>v(h,typeof C!="symbol"?C+"":C,n);var _=(h,C,n)=>new Promise((c,r)=>{var N=F=>{try{e(n.next(F))}catch(p){r(p)}},E=F=>{try{e(n.throw(F))}catch(p){r(p)}},e=F=>F.done?c(F.value):Promise.resolve(F.value).then(N,E);e((n=n.apply(h,C)).next())});import{b9 as V,cr as i,aM as R,b0 as k,aB as s,aC as l,aE as g,H as f}from"./entry/index-M8VErHPE-1727507756861.js";import{Menu as U}from"./Menu-CUL0m0Wx.js";import{Func as H}from"./Func-BhM92BNX.js";import{useClassFactoryLoader as w}from"./useClassFactoryLoader-DnOCYN87.js";import{D as B}from"./DBAccess-CzjFzLoq.js";import{GenerListEn as $}from"./GenerListEn-Co36_k1t.js";import{GloComm as M}from"./GloComm-DZ1gELjv.js";import{MapData as y}from"./MapData-lfC2UY9r.js";import{Flow as K}from"./Flow-D1QXg1UO.js";import{DictDtlView as I}from"./DictDtlView-DNxw-42U.js";import{FlowAdm as L}from"./FlowAdm-CDKA5xU7.js";import{FlowDtlView as O}from"./FlowDtlView-Ba0bcUsZ.js";import{buildShortUUID as q}from"./uuid-CODpppBC.js";import{GPN_Menu as D}from"./GPN_Menu-Q4Ebbzy0.js";import{RuoYiMenu as b}from"./RuoYiMenu-BySRn2ua.js";import{e as d}from"./antd-DkiF_jXA.js";import"./vue-DGeTOT5N.js";import"./PCenter-CGZJ3ajQ.js";import"./PowerCenter-B1QbxnUu.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./FrmAdm-DEdgJPii.js";import"./EnumLab-CzismWql.js";import"./PageBaseGenerList-Be8VFIt5.js";import"./FrmTrack-0uAZQ3B_.js";import"./GL_VSTOFrm-pu_TS543.js";import"./MapDtl-Bo8WkjPG.js";import"./Module-E11Ygwx4.js";class Me extends V{constructor(){super("GPN_MenuRuoYi");m(this,"Components",`
  #### 帮助.
  1. xxxx
  2. xxxx
  #### 效果图.
  
  `);m(this,"Search",`
  #### 帮助.
  1. 查询实体:就是把Entity实体,放入查询组件/Comm/Search.vue 上实现数据的增删改查.
  1. 可以对实体进行，把关键字，日期时间范围，枚举外键进行查询.
  #### 效果图.
  1. xxxx
  1. xxxx
  #### 步骤1. 创建实体.
  1. 创建一个实体子类,根据实体特征确定继承路径, 请参考 BP.Demo.* /src/bp/demo/*.*
  1. 注意命名空间不要与系统的命名空间重复.
  1. 查询条件,隐藏条件的设置与bp架构的一样.
  #### 步骤2. 注册到ClassFactory.
  1. 把改实体注册到 /src/bp/da/ClassFactory  类里面.
  1. 注意Ens, En 都要注册.
  1. 请参考:BP.Demo.Student 的写法.
  #### 步骤3. 按照向导创建菜单.
  1. 新建菜单，选择模块，按照向导创建菜单.
  2. 测试.
  `);m(this,"Search_Ens",`
    #### 帮助.
    1. 选择一个实体类.
    #### 为会没有有找到?
    1. 没有注册到 /src/bp/da/ClassFactory.ts里面去.
    2. 注册进去了，使用了系统命名空间.
    `);m(this,"Search_Ens_Paras",`
  #### 帮助.
  1. 输入参数,可以作为条件的参数,改参数可以为空.
  1. 比如: &FK_Dept=@WebUser.DeptNo&SortNo=xxxxx 
  1. 更多的参数请参考Search.vue的设计说明.
  `);m(this,"GPN",`
  #### 帮助.
  1. 暂无
  #### 效果图.
  1. 暂无
  `);m(this,"GPN_Ens",`
    #### 帮助.
    1. 暂无
    #### 效果图.
    1. 暂无
    `);m(this,"GPN_Ens_Paras",`
  #### 帮助.
  1. 暂无
  #### 效果图.
  1. 暂无
  `);m(this,"TreeEns",`
  #### 帮助.
  1. 暂无
  #### 效果图.
  1. 暂无
  `);m(this,"TreeEns_Ens",`
  #### 帮助.
  1. 暂无
  #### 效果图.
  1. 暂无
    `);m(this,"Desc100","暂未开放");m(this,"DocSelfUrl",`
#### 帮助
1. 可以使用相对路径，也可以使用绝对路径。
1. 用户输入的Url:  http://ccbpm.cn/MyUrl.htm
1. 打开的Url : http://ccbpm.cn/MyUrl.htm?UserNo=xxxx&Token=xxxx。
1. SID就类似于token, UserNo就是当前登录用户的编号。


#### 自定义URL菜单 For H5
1. 菜单连接： http://ccbpm.cn/MyUrl.htm   
1. 菜单连接： http://ccbpm.cn/MyUrl.htm  
1. H5链接： /WF/Comm/Search.htm?EnsName=TS.ZS.Projcets 查询
1. H5链接： /WF/Comm/Group.htm?EnsName=TS.ZS.Projcets  分析
1. H5链接： /WF/MyFlow.htm?FK_Flow=001 发起指定的流程. 

#### 自定义URL菜单 For Vue3.x

- <strong>vue3菜单支持<span style="color:red">两种</span>配置</strong>
- <span style="color:red">配置后需要刷新页面</span>
- <strong>方式1：配置打开的vue文件和参数</strong>

  - 此方式本质就是构建vue的路由， 需要填写指向文件和链接
  - 指向文件是系统存在的vue文件， 表示下方链接实际由这个文件来处理
    - /src/WF/Comm/Search.vue
  - URL表示： 通过哪个路径访问上述的vue文件 可自定义，
    - 例如：需要通过上面的Search.vue 实现一个系统字典管理的链接 
    - 需要自定义个路径 /DictManage + 参数EnName=TS.FrmUI.SysEnumMain
    - 完整路径为 /DictManage?EnName=TS.FrmUI.SysEnumMain
    

- <strong>方式2： 配置url链接</strong>
  - <span style="color:red">如果配置url，指向文件填写一个空格即可</span>
  - url链接可以配置外链，以http:// 或者 https:// 开头
    外链可以为任意互联网地址 - 例如ccflow官网: http://ccflow.org
  - url链接也可以配置系统内部的链接， 以self://开头
    系统内部链接可以配置系统工作地址 - 例如打开编号001的流程: self://WF/Flow?FK_Flow=001 

- 常用vue文件地址如下：
  - /src/WF/Comm/Search.vue 查询
  - /src/WF/Comm/Group.vue  分组

- 常用的url如下：
  - self://WF/MyFlow?FK_Flow=xxx 发起id为xxx的流程
        
#### 效果图
![输入图片说明](/src/CCFast/GPM/CCMenu/Img/SelfUrl.png "屏幕截图.png")
  `);m(this,"DocAloneflow",`
####  创建独立运行的流程 
  - 创建流程后，系统自动对该流程的相关操作创建到菜单上去。 
  - 比如：发起流程，流程查询、分析。 
 
  `);m(this,"DocRptwhite",`
####  信息窗/大屏(白色风格) 
  - 信息窗支持数据的图形展示，比如折线图、柱状图、饼图。
  - 支持变量文本输出，支持自定义HTLM代码的输出。
  - 是多种形式的统计分析展示功能。
#### 效果图
![输入图片说明](/src/CCFast/GPM/CCMenu/Img/Windows.png "屏幕截图.png")

  `);m(this,"DocRptblue",`
  
#### 信息窗/大屏(蓝色风格) 
  
  - 支持拖拽形式组装大屏窗口
  - 大屏组件丰富:各种图表,信息,列表,小组件,图标,图片等
  - 数据支持静态文件上传及接口动态获取的形式
#### 效果图
![输入图片说明](/src/CCFast/GPM/CCMenu/Img/blueRPT.jpg "屏幕截图.png")
  
  `);m(this,"DocTab",`
  
  #### Tabs页面容器 
   -  定义：每个tab下面都有一个自定义的url。
  #### 效果图
   - ![输入图片说明](/src/CCFast/GPM/CCMenu/Img/Tabs.png "屏幕截图.png")
   `);m(this,"DocRpt3d",`
  
  #### 三维报表 
     
   - 定义：三维报表是需要指定三个数据源，通过三维关系显示数据。
  #### 事例
    
   - 数据源：SELECT FK_Flow,RunModel, FWCSta ,count(*) AS Num FROM wf_node GROUP BY FK_Flow,RunModel,FWCSta
   - 维度1：SELECT No,Name FROM WF_Flow ;
   - 维度2：SELECT IntKey AS No, Lab as Name FROM sys_enum WHERE EnumKey='RunModel';
   - 维度3：SELECT IntKey AS No, Lab as Name FROM sys_enum WHERE EnumKey='FWCSta';
   
  #### 效果图
  ![输入图片说明](/src/CCFast/GPM/CCMenu/Img/Rpt3D.png "屏幕截图.png")
   
    `);m(this,"DocDict",`
  
  #### 创建实体 
      
   - 定义: 实体也叫台账或者列表，就是存储管理的对象，具有编号与名称两个必要的属性
   - 比如: 学生台账、固定资产台账、员工台帐
   - 该功能，提供了对实体的增删改查，对列表的操作，对单行记录的操作。

  #### 效果图1
  ![输入图片说明](/src/CCFast/GPM/CCMenu/Img/DictEn.png "屏幕截图.png")
  #### 效果图2
  ![输入图片说明](/src/CCFast/GPM/CCMenu/Img/Dict.png "屏幕截图.png")
     `);m(this,"DBList",`
  
  #### 数据源实体 
   -  定义：数据源实体就是视图，不能对数据执行，增加，删除，修改操作。
   -  具备实体的其他的功能，可以当作查询所用。
   `);m(this,"DictCopy",`
   #### 复制实体
   - 定义：自动启动工作流程，一个流程的开始节点的填写与发起是在特定规则的设置下自动发起的流程。
   - 解释：通常模式下的流程启动是手工的启动，就是用户从一个发起列表，点击流程名字，就启动了该流程。但是有的时候，是系统自动发起该流程。
   - 应用场景：
      1 周例会流程，用户希望每个周都要启动例会通知流程这个启动是让系统自动发起而非人工发起。
   `);m(this,"Bill",this.DictCopy);m(this,"DictRef","");m(this,"DictTable",`
  
  #### 字典表 
   -  定义：只具有编号,名称两个属性的字典实体，比如：角色类型、系统类别、税种、税目、省份、片区
 
  #### 效果图
  ![输入图片说明](/src/CCFast/GPM/CCMenu/Img/DictTable.png "屏幕截图.png")
   
   `);m(this,"DictTableTree",`
  
  #### 树结构字典表 
   -  定义：只具有编号,名称,父节点，三个属性的字典实体，比如：部门
   `);m(this,"Task",`
  #### 任务 
   -  定义：记录任务参与人，任务时间，紧急程度等事件的记事本。
  #### 效果图
  ![输入图片说明](/src/CCFast/GPM/CCMenu/Img/Task.png "屏幕截图.png")
   `);m(this,"Info",`
  
  #### 信息发布 
    
   -  定义：可以编辑发布信息，信息以列表的形式展示在页面上。 
   -  也可以定义信息发布类形，比如：会议记要，工作进度，文件传达等 
  
  #### 信息列表图
  ![输入图片说明](/src/CCFast/GPM/CCMenu/Img/Info2.png "屏幕截图.png") 
  #### 信息编辑
  ![输入图片说明](/src/CCFast/GPM/CCMenu/Img/Info.png "屏幕截图.png") 
  #### 信息类型
  ![输入图片说明](/src/CCFast/GPM/CCMenu/Img/Info3.png "屏幕截图.png") 
   
   `);m(this,"Calendar",`
   
     
    -  定义：可以在日历上编辑记事，工作提醒等。
  #### 效果图
  ![输入图片说明](/src/CCFast/GPM/CCMenu/Img/Calendar.png "屏幕截图.png")
    
    `);m(this,"Notepad",`
    
  #### 记事本 
      
     -  定义：是一款在线记事本，可以记录生活，工作，事件。
  #### 效果图
  ![输入图片说明](/src/CCFast/GPM/CCMenu/Img/Notepad.png "屏幕截图.png") 
 
     `);m(this,"KnowledgeManagement",`
   
  #### 知识库 
     
   -  定义：各种知识的集合，方便了解和查询

  #### 效果图
  ![输入图片说明](/src/CCFast/GPM/CCMenu/Img/KnowledgeManagement.png "屏幕截图.png") 
  
 
    
    `);m(this,"WorkRec",`
   
  #### 工作日志 
     
   -  定义：记录工作的日志
  #### 效果图
  ![输入图片说明](/src/CCFast/GPM/CCMenu/Img/WorkRec.png "屏幕截图.png") 
   
    `);this.PageTitle="新建菜单"}GenerSorts(n){return Promise.resolve([])}Init(){return _(this,null,function*(){this.AddGroup("B","实体单据"),this.TextBox2_NameNo(i.Dict,"创建实体",this.DocDict,"Dict_","编号","名称","学生台账"),this.TextBox2_NameNo(i.Bill,"创建单据",this.DocDict,"Bill_","编号","名称","学生台账"),this.AddIcon("GenerListEn","icon-wallet"),this.AddGroup("Z","视图"),this.TextBox2_NameNo("View","自定义",this.HelpTodo,"View_","输入ID","输入名称","我的视图"),this.TextBox3_NameNoNote("DictDtlView","实体从表",this.HelpTodo,"ViewFrmDtl_","请输入视图ID","输入视图名称","请输入表单ID","实体从表视图"),this.SelectItemsByList("DictDtlView.SelectFrmDtl","选择从表",this.HelpTodo,!1,()=>`SELECT No, Name FROM Sys_MapDtl WHERE FK_MapData='${this.RequestVal("tb3","DictDtlView")}' `),this.AddIcon("DictDtlView","icon-grid"),this.TextBox3_NameNoNote("FlowDtlView","流程从表",this.HelpTodo,"ViewFrmDtl_","请输入视图ID","输入视图名称","输入流程编号","流程从表视图"),this.SelectItemsByList("FlowDtlView.SelectFlowDtl","选择从表",this.HelpTodo,!1,()=>`SELECT No, Name FROM Sys_MapDtl WHERE FK_MapData='${"ND"+Number.parseInt(this.RequestVal("tb3","FlowDtlView"))+"01"}' `),this.AddIcon("FlowDtlView","icon-grid"),this.AddGroup("E","系统实体"),this.AddBlank("Entity","Entity数据实体",this.Search),this.SelectItemsByList("Entity.EnName","选择实体",this.Search_Ens,!1,yield D.GenerEnsList("Entity"),!0),this.SelectItemsByList("Entity.EnName.componentName","组件类型",this.Search_Ens,!1,this.GenerEntitySort()),this.TextBox2_NameNo("Entity.EnName.componentName.Paras","路径及参数",this.Search_Ens_Paras,"","参数","URL定义",""),this.AddBlank("GPN","GPN新建组件",this.GPN),this.SelectItemsByList("GPN.Ens","选择实体",this.GPN_Ens,!1,yield D.GenerEnsList("GPN")),this.TextBox1_Name("GPN.Ens.Paras","可选参数",this.GPN_Ens_Paras,"参数","&1=1"),this.AddBlank("TreeEns","TreeEns树干叶子组件",this.TreeEns),this.SelectItemsByList("TreeEns.Ens","选择实体",this.TreeEns_Ens,!1,yield D.GenerEnsList("TreeEns")),this.TextBox1_Name("TreeEns.Ens.Paras","可选参数",this.Search_Ens,"参数","&1=1"),this.AddBlank("GL","GL通用列表组件",this.TreeEns),this.SelectItemsByList("GL.Ens","选择实体",this.TreeEns_Ens,!1,yield D.GenerEnsList("GL")),this.TextBox1_Name("GL.Ens.Paras","可选参数",this.Search_Ens,"参数","&1=1"),this.AddBlank("PG","PG实体分组展示组件",this.TreeEns),this.SelectItemsByList("PG.Ens","选择实体",this.TreeEns_Ens,!1,yield D.GenerEnsList("PG")),this.TextBox1_Name("PG.Ens.Paras","可选参数",this.Search_Ens,"参数","&1=1"),this.AddGroup("A","通用功能"),this.TextBox1_Name(i.RptWhite,"信息窗/大屏(白色风格)",this.DocRptwhite,"页面名称","统计分析"),this.TextBox1_Name(i.RptBlue,"信息窗/大屏(蓝色风格)",this.DocRptblue,"大屏名称","大屏展示"),this.AddGroup("F","页面引用"),this.SelectItemsByList("LinkFlow","流程功能",this.HelpTodo,!1,this.GenerFlowFunc()),this.SelectItemsByGroupList("LinkFlow.FlowNo","选择流程",this.HelpTodo,!1,R.srcFlowSorts,R.srcFlows,!1),this.SelectItemsByList("DictLink","实体功能",this.HelpTodo,!1,this.GenerDictFunc()),this.TextBox1_Name("DictLink.DictID","输入实体ID",this.HelpTodo,"实体ID","","在表单设计器,实体属性里查看实体ID."),this.AddIcon("icon-link","book-open"),this.AddIcon("icon-heart","Entity"),this.AddIcon("icon-docs","GPN"),this.AddIcon("icon-control-pause","TreeEns"),this.AddIcon("icon-cup","GL"),this.AddIcon("icon-layers","PG"),this.AddIcon("icon-link","SelfUrl"),this.AddIcon("icon-doc","RptWhite"),this.AddIcon("icon-drop","Tabs"),this.AddIcon("icon-docs","Rpt3D"),this.AddIcon("icon-docs","Info"),this.AddIcon("icon-doc","Notepad"),this.AddIcon("icon-layers","KnowledgeManagement"),this.AddIcon("icon-film","WorkRec")})}GenerDictFunc(){return JSON.stringify([{No:"Home",Name:"实体主页:通过En组件链接到列表、分析、报表、大屏链接."},{No:"Search",Name:"列表:链接到流程实例分析."},{No:"Group",Name:"分析:分组分析数据."},{No:"Rpt",Name:"报表:2维,3维报表"},{No:"BS",Name:"大屏:BS可以定制化分析内容."}])}GenerFlowFunc(){return JSON.stringify([{No:"FlowSearch",Name:"查询Search,链接到流程实例查询"},{No:"FlowGroup",Name:"分析Group:链接到流程实例分析."},{No:"Start",Name:"发起Start:发起指定的流程."},{No:"Todolist",Name:"待办Todolist:查看指定流程的待办."},{No:"Runing",Name:"在途Runing:查看指定流程的在途."},{No:"Complete",Name:"已完成GL_Complete:查看指定流程的已经完成的."},{No:"Home",Name:"流程主页Home:查看流程主页."}])}GenerEntitySort(){return JSON.stringify([{No:"Search",Name:"查询组件Search"},{No:"Batch",Name:"批处理组件Batch"},{No:"Ens",Name:"批量修改组件Ens"}])}static GenerEnsList(n){return _(this,null,function*(){if(n==="GPN")return yield(yield w("ClassFactoryOfGroupPageNew")).toJSON([]);if(n==="TreeEns")return(yield w("ClassFactoryOfPageBaseTreeEns")).toJSON([]);if(n==="GL")return yield(yield w("ClassFactoryOfGenerList")).toJSON([]);if(n==="PG")return(yield w("ClassFactoryOfPanelGroup")).toJSON([]);if(n==="Entity")return yield(yield w("ClassFactory")).toJSON([]);const c=new k;return yield c.RetrieveAll(),JSON.stringify(c)})}Save_TextBox_X(n,c,r,N,E){return _(this,null,function*(){var x,A;const e=new b;if(e.Icon="icon-user",e.parent_id=this.RequestVal("parent_id"),n=="DBList"){const t=M.UrlGPN("GPN_DBList","");return new s(l.GoToUrl,t)}if(n=="DictDtlView"){const t=new I(r);if((yield t.IsExits())==!0)return new s(l.Message,"菜单ID:["+r+"]已经存在");if(t.setPKVal(E),(yield t.RetrieveFromDBSources())==0)return new s(l.Message,"表单ID:["+E+"]不存在.")}if(n=="DictDtlView.SelectFrmDtl"){const t=new I;t.No=this.RequestVal("tb2","DictDtlView"),t.Name=this.RequestVal("tb1","DictDtlView");const o=this.RequestVal("tb3","DictDtlView"),a=new y(o);yield a.RetrieveFromDBSources(),t.DictID=a.No,t.DictName=a.Name,t.DictDtlID=r,t.DictDtlName=N,yield t.Insert(),e.MenuModel="DictDtlView",e.Name=t.Name,e.FrmID=t.No,e.No=t.No,e.Icon="icon-wallet",e.SetPara("EnName","TS.CCBill.DictDtlView"),e.Tag1=N,e.Alias="DictDtlView_"+t.No,e.UrlPath="/@/CCFast/CCBill/SearchDict.vue",e.UrlExt="/DictDtlView_"+t.No+"?EnName="+t.No,yield e.Insert();const u=M.UrlEn("TS.CCBill.DictDtlView",t.No);return new s(l.GoToUrl,u,"属性")}if(n=="FlowDtlView"){if((yield new I(r).IsExits())==!0)return new s(l.Message,"菜单ID:["+r+"]已经存在");if((yield new L(E).IsExits())==!1)return new s(l.Message,"流程编号错误:"+E)}if(n=="FlowDtlView.SelectFlowDtl"){const t=new O;t.No=this.RequestVal("tb2","FlowDtlView"),t.Name=this.RequestVal("tb1","FlowDtlView");const o=this.RequestVal("tb3","FlowDtlView"),a=new L(o);yield a.RetrieveFromDBSources(),t.FlowNo=a.No,t.DictID="ND"+Number.parseInt(a.No+"01"),t.DictName=a.Name,t.DictDtlID=r,t.DictDtlName=N,yield t.Insert(),e.MenuModel="FlowDtlView",e.Name=t.Name,e.FrmID=t.No,e.No=t.No,e.Icon="icon-wallet",e.SetPara("EnName","TS.CCBill.FlowDtlView"),e.Tag1=N,e.Alias="FlowDtlView_"+t.No,e.Name=t.Name,e.UrlExt="/"+t.No+"?displayMode=table&FrmID="+t.No,e.UrlPath="/src/CCFast/CCBill/SearchDict.vue",e.FrmID=t.No,e.ListModel=0,e.WorkType="0",yield e.Insert();const u=M.UrlEn("TS.CCBill.FlowDtlView",t.No);return new s(l.GoToUrl,u,"属性")}if(n=="View"){const t=new y;if(t.No=N,(yield t.IsExits())==!0)return new s(l.Message,"ID:"+N+"已经存在,请重命名.");const o=new f("BP.WF.HttpHandler.WF_Admin_CCFormDesigner");o.AddPara("TB_Name",r),o.AddPara("TB_No",N),o.AddPara("DDL_DBSrc","local"),o.AddPara("FK_FrmSort",c),o.AddPara("EntityType",100);const a=yield o.DoMethodReturnString("NewFrmGuide_Create_DBList");if(a.includes("err@")==!0)return new s(l.Message,a);e.MenuModel=n,e.Name=r,e.FrmID=N,e.No=N,e.Icon="icon-wallet",e.SetPara("EnName","TS.CCBill.DBEntity"),e.Tag1=N,e.Alias="View_"+N,e.UrlPath="/@/WF/views/Search.vue",e.UrlExt="/WF/Comm/Search?EnName="+N,yield e.Insert();const u=M.UrlEn("TS.CCBill.DBEntity",N);return new s(l.GoToUrl,u,"属性")}if(n=="GenerListEn"){e.MenuModel=n,e.Name=r;const t=new $;t.No=B.GenerGUID(),t.Name=r,yield t.Insert(),e.No=t.No,e.Icon="icon-wallet",e.SetPara("EnName","TS.CCBill.GenerListEn"),e.Tag1=t.No,e.Alias=c+"_"+t.No,e.UrlPath="/@/WF/views/GenerList.vue",e.UrlExt="/WF/Comm/GenerList?EnName=GL_GLEn&EnID="+t.No,yield e.Insert();const o=M.UrlEn("TS.CCBill.GenerListEn",t.No);return new s(l.GoToUrl,o,"设计通用列表")}const F=["Entity.EnName.CompentType.Paras","Search.EnName.Paras","GPN.Ens.Paras","TreeEns.Ens.Paras","GL.Ens.Paras","PG.Ens.Paras"],p=new Map([["Search",{file:"/@/WF/Comm/Search.vue",urlPrefix:"/WF/Comm/Search",factory:yield w("ClassFactory")}],["GPN",{file:"/@/WF/Comm/UIEntity/GroupPageNew.vue",urlPrefix:"/WF/Comm/GroupPageNew",factory:yield w("ClassFactoryOfGroupPageNew")}],["TreeEns",{file:"/@/WF/Comm/TreeEns.vue",urlPrefix:"/WF/Comm/TreeEns",factory:yield w("ClassFactoryOfPageBaseTreeEns")}],["GL",{file:"/@/WF/views/GenerList.vue",urlPrefix:"/WF/Comm/GenerList",factory:yield w("ClassFactoryOfGenerList")}],["PG",{file:"/@/WF/Comm/PanelGroup.vue",urlPrefix:"/WF/Comm/PanelGroup",factory:yield w("ClassFactoryOfPanelGroup")}]]);if(F.includes(n)){const t=n.split(".")[0],o=n.split(".")[1],a=p.get(t);if(!a)return d.error("抱歉，你输入的类型不存在"),new s(l.DoNothing,null);const{file:u,urlPrefix:S,factory:T}=a,G=this.RequestVal("tb1",t+"."+o),P=yield T.GetEn(G);return e.ModuleNo=c,e.Name=((x=P==null?void 0:P._enMap)==null?void 0:x.EnDesc)||P.PageTitle,e.Alias=c+"_"+G,e.UrlPath=u,e.UrlExt=S+"?EnName="+G+"&"+r,e.SystemNo=this.SystemNo,e.MenuModel=i.FixedUrl,e.IsEnable=1,yield e.Insert(),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null)}if(n==="Entity.EnName.componentName.Paras"){const t="/@/WF/Comm/",o=this.RequestVal("tb1","Entity.EnName"),a=this.RequestVal("tb1","Entity.EnName.componentName"),S=yield(yield w("ClassFactory")).GetEn(o);if(e.ModuleNo=c,e.Name=((A=S==null?void 0:S._enMap)==null?void 0:A.EnDesc)||S.PageTitle,e.Alias=c+"_"+o,e.UrlPath=t+a+".vue",r+="",r=r.trim(),r.length===0){const T=o.lastIndexOf(".");r=o.substring(T)+a}return e.UrlExt=r+"?EnName="+o,typeof N=="string"&&N.trim().length>0&&(e.UrlExt=r+"?EnName="+o+"&"+N.replace(/\?/g,"").replace(/^&+/g,"")),e.SystemNo=this.SystemNo,e.MenuModel=i.FixedUrl,e.IsEnable=1,yield e.Insert(),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null)}if(n===i.SelfUrl){e.ModuleNo=c,e.Name=r,e.UrlPath=N,e.UrlExt=E,e.SystemNo=this.SystemNo,e.MenuModel=i.SelfUrl,e.IsEnable=1;const t=new f("BP.WF.HttpHandler.WF_Admin_FoolFormDesigner");return t.AddPara("name",r),t.AddPara("flag",!0),e.Alias=yield t.DoMethodReturnString("ParseStringToPinyin"),yield e.Insert(),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null)}if(n==="LinkFlow.FlowNo"){const t=this.RequestVal("tb1","LinkFlow"),o=r,a=new K(o);return yield a.Retrieve(),t=="Home"&&(e.Name="主页:"+a.Name,e.Alias=c+"_GL_CC"+a.No,e.UrlPath="/src/WF/Comm/En.vue",e.UrlExt=`/Flow_Home_${a.No}?EnName=TS.TSClass.FlowOneSetting&PKVal=${a.No}`),t=="FlowSearch"&&(e.Name=a.Name+"查询",e.Alias="SearchFlow"+a.No,e.UrlPath="/src/WF/Rpt/SearchFlow.vue?FlowNo="+a.No,e.UrlExt=`/SearchFlow_${a.No}?FlowNo=${a.No}`),t=="FlowGroup"&&(e.Name=a.Name+"分析",e.Icon="icon-chart",e.Alias="FlowGroup"+a.No,e.UrlPath="/src/WF/Rpt/GroupFlow.vue?FlowNo="+a.No,e.UrlExt=`/GroupFlow_${a.No}?FlowNo=${a.No}`),t=="Start"&&(e.Alias=c+"_"+o,e.UrlPath="",e.UrlExt="self://WF/MyFlow?FlowNo="+o,e.Name="发起:"+a.Name,e.MobileUrlExt=`self://CCMobile/MyFlow?FlowNo=${o}&Title=待办&FK_Flow=${a.No}`),t=="Todolist"&&(e.Name="待办:"+a.Name,e.Alias=c+"_GL_Todolist"+a.No,e.UrlPath="/@/WF/views/GenerList.vue",e.UrlExt=`/GL_Todolist_${o}?EnName=GL_Todolist&FlowNo=${a.No}`,e.MobileUrlExt=`self://CCMobile/GenerList?EnName=GL_Todolist&FlowNo=${o}&Title=待办&FK_Flow=${a.No}`),t=="Runing"&&(e.Name="在途:"+a.Name,e.Alias=c+"_GL_Runing"+a.No,e.UrlPath="/@/WF/views/GenerList.vue",e.UrlExt=`/GL_Running_${o}?EnName=GL_Runing&FlowNo=${a.No}`,e.MobileUrlExt=`self://CCMobile/GenerList?EnName=GL_Running&FlowNo=${o}&Title=在途&FK_Flow=${a.No}`),t=="Complete"&&(e.Name="已完成:"+a.Name,e.Alias=c+"_GL_Complete"+a.No,e.UrlPath="/@/WF/views/GenerList.vue",e.UrlExt=`/GL_Complete_${o}?EnName=GL_Complete&FlowNo=${a.No}`,e.MobileUrlExt=`self://CCMobile/GenerList?EnName=GL_Complete&FlowNo=${o}&Title=已完成&FK_Flow=${a.No}`),t=="CC"&&(e.Name="抄送:"+a.Name,e.Alias=c+"GL_CC"+a.No,e.UrlPath="/@/WF/views/GenerList.vue",e.UrlExt=`/GL_CC_${o}?EnName=GL_CC&FlowNo=${a.No}`,e.MobileUrlExt=`self://CCMobile/GenerList?EnName=GL_CC&FlowNo=${o}&Title=抄送&FK_Flow=${a.No}`),e.ModuleNo=c,e.SystemNo=this.SystemNo,e.MenuModel=i.SelfUrl,e.IsEnable=1,yield e.Insert(),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null)}if(n==="DictLink.DictID"){const t=this.RequestVal("tb1","DictLink"),o=r,a=new y(o);return yield a.Retrieve(),t=="Home"&&(e.Name="主页:"+a.Name,e.Alias=c+"_Dict_Home_"+a.No,e.UrlPath="/@/WF/Comm/En.vue",e.UrlExt=`/Dict_Home_${a.No}?EnName=TS.CCBill.DictSettingOne&PKVal=${a.No}`),t=="Search"&&(e.Name="列表:"+a.Name,e.Alias=c+"_List_"+a.No,e.UrlPath="/@/CCFast/CCBill/SearchDict.vue",e.UrlExt=`/Dict_List_${a.No}?FrmID=${a.No}`),t=="Group"&&(e.Name="分析:"+a.Name,e.Alias=c+"_Analy_"+a.No,e.UrlPath="/@/CCFast/CCBill/SearchDict.vue",e.UrlExt=`/Dict_Analy_${a.No}?FrmID=${a.No}&displayMode=group`),t=="Rpt"&&(e.Name="报表:"+a.Name,e.Alias=c+"_Rpt_"+a.No,e.UrlPath="/@/CCFast/CCBill/SearchDict.vue",e.UrlExt=`/Dict_Rpt_${a.No}?FrmID=${a.No}&displayMode=rpt`),t=="BS"&&(e.Name="大屏:"+a.Name,e.Alias=c+"_BigScreen_"+a.No,e.UrlPath="/src/CCFast/Views/RptWhiteMain.vue",e.UrlExt=`/Dict_BigScreen_${a.No}?PageID=${a.No}`),e.ModuleNo=c,e.SystemNo=this.SystemNo,e.MenuModel=i.SelfUrl,e.IsEnable=1,yield e.Insert(),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null)}if(n===i.StandAloneFlow){const t=new f("BP.WF.HttpHandler.WF_GPM_CreateMenu");t.AddPara("SortNo",c),t.AddPara("FlowName",r),t.AddPara("FlowDevModel",0),t.AddPara("ModuleNo",c);const o=yield t.DoMethodReturnString("StandAloneFlow_Save");if(typeof o=="string"&&o.startsWith("err@")){alert(o);return}return e.ModuleNo=c,e.Name=r,e.UrlExt=N,e.MenuModel=i.StandAloneFlow,yield e.Insert(),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null)}if(n===i.RptWhite)return e.Name=r,e.MenuModel=i.RptWhite,e.IsEnable=1,e.Icon="icon-screen-desktop",e.SetPara("EnName","TS.CCFast.Rpt3D"),e.UrlPath="/src/CCFast/Views/RptWhiteMain.vue",yield e.Insert(),e.UrlExt="/RptWhite"+e.No.substring(0,6)+"?PageID="+e.No,yield e.Update(),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null);if(n===i.RptBlue){e.Name=r,e.Tag1=q(),e.MenuModel=i.RptBlue,e.IsEnable=1,e.Icon="icon-screen-desktop",e.SetPara("EnName","TS.CCFast.RptBlue"),e.UrlPath="/src/CCFast/Views/RptBlueMain.vue",e.path="#/chart/home/"+e.Tag1,yield e.Insert(),e.UrlExt="/RptBlue"+e.No.substring(0,6)+"?PageID="+e.No,yield e.Update();const t=new f("bp.wf.httphandler.third.Third_GoView");return t.AddPara("myPk",e.Tag1),yield t.DoMethodReturnString("CreateProject"),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null)}if(n===i.Tabs)return e.Name=r,e.MenuModel=i.Tabs,e.IsEnable=1,yield e.Insert(),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null);if(n===i.Rpt3D)return e.Name=r,e.Icon="icon-screen-desktop",e.MenuModel=i.Rpt3D,e.SetPara("EnName","TS.CCFast.Rpt3D"),yield e.Insert(),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null);if(n===i.FlowUrl){alert("未实现");return}if(n===i.Func){const t=new H;return t.Name=r,t.FuncID=N,yield t.Insert(),e.Name=t.Name,e.MenuModel=i.Func,e.Icon="icon-energy",e.UrlExt=t.No,e.SetPara("EnName","TS.CCFast.Func"),e.SetPara("EnPKVal",t.No),e.No=t.No,yield e.Insert(),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null)}if(n===i.Dict){const t=N,o=r,a=new f("BP.WF.HttpHandler.WF_Admin_CCFormDesigner");return a.AddPara("TB_No",t),a.AddPara("TB_Name",o),a.AddPara("TB_PTable",t),a.AddPara("FK_FrmSort",this.SystemNo),a.AddPara("EntityType",2),yield a.DoMethodReturnString("NewFrmGuide_Create"),e.Name=o,e.UrlExt=t+"?displayMode=table&FrmID="+t,e.UrlPath="/src/CCFast/CCBill/SearchDict.vue",e.FrmID=t,e.MenuModel=i.Dict,e.ListModel=0,e.WorkType="0",e.SetPara("EnName","TS.CCBill.FrmDict"),e.SetPara("EnPKVal",t),yield e.Insert(),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null)}if(n===i.DBList){const t=N,o=r,a=new f("BP.WF.HttpHandler.WF_Admin_CCFormDesigner");a.AddPara("TB_No",t),a.AddPara("TB_Name",o),a.AddPara("TB_PTable",t),a.AddPara("FK_FrmSort",this.SystemNo),a.AddPara("EntityType",2);const u=yield a.DoMethodReturnString("NewFrmGuide_Create_DBList");if(typeof u=="string"&&u.startsWith("err@")){d.error(u);return}return e.Name=o,e.UrlExt=t+"?FrmID="+t,e.UrlPath="/src/CCFast/CCBill/SearchDBList.vue",e.MenuModel=i.DBList,e.ListModel=0,e.WorkType="0",e.Icon="icon-book-open",yield e.Insert(),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null)}if(n===i.DictCopy){alert("未翻译..");return}if(n===i.Bill){const t=N,o=r,a=new f("BP.WF.HttpHandler.WF_Admin_CCFormDesigner");return a.AddPara("TB_No",t),a.AddPara("TB_Name",o),a.AddPara("TB_PTable",t),a.AddPara("FK_FrmSort",this.SystemNo),a.AddPara("EntityType",1),yield a.DoMethodReturnString("NewFrmGuide_Create"),e.Name=o,e.UrlExt=t+"?displayMode=table&FrmID="+t,e.UrlPath="/src/CCFast/CCBill/SearchBill.vue",e.FrmID=t,e.MenuModel=i.Bill,e.ListModel=0,e.WorkType="0",e.SetPara("EnName","TS.CCBill.FrmBill"),e.SetPara("EnPKVal",t),yield e.Insert(),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null)}if(n===i.DictRef){alert("未翻译..");return}if(n===i.DictTable){const t=new g;if(t.No=N,t.Name=r,t.No==r||t.Name==""){alert("err@名称与编号不能为空.");return}if(yield t.IsExits()){d.warning("编号已经存在["+t.No+"]请使用其他的编号.");return}return t.DBSrcType="SysDict",t.CodeStruct=0,yield t.Insert(),e.Name=t.Name,e.UrlExt=t.No,e.MenuModel=i.DictTable,e.SystemNo=this.SystemNo,e.SetPara("CodeStruct",0),yield e.Insert(),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null)}if(n===i.DictTableTree){const t=new g;if(t.No=N,t.Name=r,t.No==r||t.Name==""){alert("err@名称与编号不能为空.");return}if(yield t.IsExits()){alert("编号已经存在["+t.No+"]请使用其他的编号.");return}return t.SrcType="SysDict",t.CodeStruct=1,e.SetPara("CodeStruct",1),yield t.Insert(),e.Name=t.Name,e.UrlExt=t.No,e.MenuModel=i.DictTableTree,yield e.Insert(),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null)}if(n===i.Task)return e.Name=r,e.UrlExt="/src/CCFast/Task/Task.htm",e.MenuModel="Task",e.Mark="Task",e.Icon="icon-note",e.SetPara("EnName","TS.GPM.MenuExt"),yield e.Insert(),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null);if(n===i.Info){const t=new U;t.ModuleNo=c,t.SystemNo=this.SystemNo,t.Name=r,t.UrlExt="/src/WF/Comm/Search.vue?EnsName=TS.CCOA.CCInfo.Info",t.MenuModel="Info",t.Mark="Info",t.Icon="icon-note",t.Insert();const o=new U;return o.ModuleNo=c,o.SystemNo=this.SystemNo,o.Name=r+"类型",o.UrlExt="/src/WF/Comm/Ens.vue?EnsName=TS.CCOA.CCInfo.InfoType",o.MenuModel="Info",o.Mark="Info",o.Icon="icon-note",o.Insert(),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null)}if(n===i.Notepad)return e.Name=r,e.UrlExt="/src/CCFast/Notepad/Notepad.htm",e.MenuModel="Notepad",e.Mark="Notepad",e.Icon="icon-note",e.Insert(),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null);if(n===i.KnowledgeManagement)return e.Name=r,e.UrlExt="/src/CCFast/KnowledgeManagement/Default.htm",e.MenuModel="KnowledgeManagement",e.Mark="KnowledgeManagement",e.Icon="icon-eyeglass",e.Insert(),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null);if(n===i.WorkRec)return e.ModuleNo=c,e.Name=r,e.UrlPath="/@/WF/Comm/Search.vue",e.MobileUrlExt="/@/CCMobile/Comm/Search.vue",e.UrlExt="/WF/Comm/Search?EnName=TS.CCOA.WorkLog.WorkRec&1=1",e.SystemNo=this.SystemNo,e.MenuModel="WorkRec",e.IsEnable=1,e.Alias=B.GenerGUID(),yield e.Insert(),d.info("创建成功，您可以在在菜单里执行高级编辑与授权."),new s(l.CloseAndReload,null)})}}export{Me as GPN_MenuRuoYi};
