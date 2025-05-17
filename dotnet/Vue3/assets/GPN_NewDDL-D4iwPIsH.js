var F=Object.defineProperty;var N=(c,a,t)=>a in c?F(c,a,{enumerable:!0,configurable:!0,writable:!0,value:t}):c[a]=t;var s=(c,a,t)=>N(c,typeof a!="symbol"?a+"":a,t);var D=(c,a,t)=>new Promise((u,i)=>{var l=r=>{try{m(t.next(r))}catch(e){i(e)}},T=r=>{try{m(t.throw(r))}catch(e){i(e)}},m=r=>r.done?u(r.value):Promise.resolve(r.value).then(l,T);m((t=t.apply(c,a)).next())});import{GloComm as o}from"./GloComm-DZ1gELjv.js";import{b9 as b,aM as B,bk as A,W as M,cq as _,aB as S,aC as d,aL as h,aG as p,aE as R}from"./entry/index-M8VErHPE-1727507756861.js";import{SysEnumMain as q}from"./SysEnumMain-Bn9Li5_w.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./SFColumn-Q_PoS_2g.js";import"./SysEnum-B89JeOjj.js";const I=class I extends b{constructor(){super("GPN_NewDDL");s(this,"HelpAdminEnum",`
  #### 帮助
  - 枚举库维护, 进入枚举库后，在工具栏点击新建按钮.
  `);s(this,"HelpDBSrc",`
  #### 帮助
  - 数据源分为关系数据库类型的数据源与web服务的数据源.
  - 首先需要维护数据源，然后在数据源上创建字典.
  `);s(this,"HelpDict",`
  #### 帮助
  - 字典表管理,在数据源上创建字典表.
  - 如果没有数据源，请首先创建数据源.
  `);s(this,"Blank",`
  #### 帮助
  - 空白的字段: 用于加载表单的时候，他的数据源是通过，由参数的字典获得的.
  - 比如：表单里由，片区、省份、地市、区县四个下拉框字段. 当表单加载的时候，在没有确定片区其他的三个字段都无法确定值。
  - 省份、地区、区县就需要绑定空白数据源外键.
  - 使用级联关系，把其他的字段数据实现数据级联查询.
  `);s(this,"Docs1",`
  #### 帮助 
  - 待完善.
  `);s(this,"SelectedDict_Name",`
  #### 帮助
  - 请输入要生成的字段名, 格式:英文字母开头下划线数字.
  - 该名称将会是数据库的真实字段.
  `);s(this,"SelectedDict",`
  #### 帮助
  - 请选择一个外键字典表，如果没有请在左侧创建一个字典表.
  - 创建外键字段,必须选定外键的字典表.
  - 列出来的都是无参数的字典.

  #### 关于外键字典表
  - 具有编码，名称数据我们称为字典表，比如：片区、省份、税种、税目、部门.
  - 创建外键字典表必须依托一个数据源.
  - 从其他系统获得的数据, 
  - 外键字典表可以链接到

  #### 字典数据来源类型
  - SQL语句：从关系数据库中获得来的.
  - WebApi: 通过web服务获得.
  - Javascript: 通过脚本的function方法获得.
  - ccform内置的数据维护,维护在ccbpm系统中的字典.

  #### 两种格式数据格式
  - 编号名称格式: 比如片区、省份.
  - 树结构模式: 比如部门、产品目录树.
  #### 数据源的类型
  - 关系数据库
  - web服务.

  `);s(this,"NewStrEnum",`
  #### 帮助
   - 填写格式: 枚举值=枚举标签; 
   - 例如: ty=团员,dy=党员,qz=群众
   - 系统解析为: ty是团员, dy是党员, qz是群众.

  #### 数据存储.
   - string类型的枚举也称为标记枚举,字母存储一个列,标签存储一个列.
   - 在表单里字段是abc,那系统就会自动创建一个影子字段 abcT.
   - abc字段存储的是标记, abcT存储的是标签.
   - 这一点与外部数据源存储一致.
    `);s(this,"NewIntEnum",`
  #### 帮助
   - 填写格式1: 团员,党员,群众
   - 系统解析为: 0是团员， 1是党员，2是群众.
   - 填写格式2: @0=团员@1=党员@2=群众
   - 系统解析为: 10是团员， 20是党员，30是群众，这样就可以自己定义枚举值.
  #### 数据存储
   - int类型的枚举值是常用的数据类型，ccfrom是格式化的存储到数据表里.
   - 创建一个int类型的字段，用于存储枚举的数据.
    `);s(this,"Docs2",`

  #### 帮助
   - 依托富文本编辑器,实现对表单的编辑.
   - 优点:格式灵活,展现效果随心所欲.
   - 缺点:业务人员入手需要一定的学习成本.
   - 适用于:效果
    
  `);this.PageTitle="新建枚举/外键字段"}static SelectedEnum_FieldName(t,u,i,l,T,m){throw new Error("Method not implemented.")}Init(){this.AddGroup("A","枚举字段");const t=B.SQLEnumMain;this.SelectItemsByList("SelectedEnum","新建枚举字段",I.SelectedEnum,!1,t),this.TextBox1_Name("SelectedEnum.FieldName","输入字段ID",this.SelectedDict_Name,"字段ID",()=>A.CCBPMRunModel===0?this.RequestVal("tb1","SelectedEnum"):this.RequestVal("tb1","SelectedEnum").replace(M.OrgNo+"_",""),"英文字母或者下划线开头."),this.AddGoToUrl("NewEnum","新建枚举",o.UrlGPN("GPN_Enum","","")),this.AddGoToUrl("AdminEnum","枚举库维护",o.UrlSearch("TS.FrmUI.SysEnumMain")),this.AddGoToUrl("AdminEnum1","枚举库维护(GL)",o.UrlGenerList("GL_NewEnum")),this.AddGroup("B","外部数据源(外键)字段"),this.SelectItemsByList("SelectedDict","新建外键字段",this.SelectedDict,!1," SELECT No, Name, DBSrcType as GroupNo FROM Sys_SFTable WHERE No!='Blank' "),this.TextBox1_Name("SelectedDict.Name","输入字段ID",this.SelectedDict_Name,"字段ID",()=>this.RequestVal("tb1","SelectedDict"),"英文字母或者下划线开头."),this.AddGoToUrl("AdminDict","字典库维护",o.UrlSearch("TS.FrmUI.SFTable")),this.AddGoToUrl("AdminDBSrc","数据源维护",o.UrlSearch("TS.Sys.SFDBSrc"))}GenerSorts(){return D(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,u,i,l,T){return D(this,null,function*(){if(t!="SelectedEnum"){if(t=="Blank")return yield _.Init_Blank(),this.InitDDL(l,i,"Blank");if(t==="AdminEnum.ToUrl"||t==="AdminEnum")return new S(d.GoToUrl,o.UrlSearch("TS.FrmUI.SysEnumMain"));if(t==="AdminDict.ToUrl"||t==="AdminDict")return new S(d.GoToUrl,o.UrlSearch("TS.FrmUI.SFTable"));if(t==="AdminDBSrc")return new S(d.GoToUrl,o.UrlSearch("TS.Sys.SFDBSrc"));if(t==="SelectedEnum.FieldName"){const m=this.RequestVal("FrmID"),r=this.RequestVal("GroupField"),e=this.RequestVal("CtrlType");if(!i)return;const y=this.RequestVal("tb1","SelectedEnum"),G=this.RequestVal("tb2","SelectedEnum"),E=new q(y);E.No=y,yield E.Retrieve(),E.EnumKey===""&&(E.EnumKey=i);const n=new h;if(n.MyPK=m+"_"+i,yield n.IsExits())return new S(d.Error,"字段在表单已经存在"+i);n.Name=G,n.KeyOfEn=i,n.FK_MapData=m,n.UIVisible=1,n.UIIsEnable=1,n.GroupID=r,n.DefVal=0,n.LGType=1,n.MyDataType=e==="2"?1:E.EnumType===0?2:1,n.SetPara("RBShowModel",3),n.UIContralType=parseInt(e)||p.DDL,n.UIBindKey=E.EnumKey,yield n.Insert();const w="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrEnum&PKVal="+n.MyPK;return new S(d.GoToUrl,w)}if(t==="SelectedDict.Name"){const m=this.RequestVal("tb1","SelectedDict");return this.InitDDL(i,"",m)}}})}InitDDL(t,u,i){return D(this,null,function*(){const l=this.RequestVal("FrmID"),T=this.RequestVal("GroupField");if(!t)return;const r=new R(i);r.No=i,yield r.Retrieve();const e=new h;if(e.MyPK=l+"_"+t,(yield e.IsExits())==!0)return new S(d.Error,"字段在表单已经存在"+t);u?e.Name=u:e.Name=r.Name,e.KeyOfEn=t,e.FK_MapData=l,e.UIVisible=1,e.UIIsEnable=1,e.GroupID=T,e.LGType=0,e.MyDataType=1,e.UIContralType=p.DDL,e.UIBindKey=r.No,e.SetPara("SrcType",r.DBSrcType),yield e.Insert();const U=e.MyPK;e.UIVisible=0,e.MyPK=l+"_"+t+"T",e.KeyOfEn=t+"T",e.Name=e.Name+"T",e.UIContralType=p.TB,yield e.Insert();const y="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrSFSQL&PKVal="+U;return new S(d.GoToUrl,y)})}};s(I,"SelectedEnum",`
  #### 帮助 
   - 选择枚举库的枚举.
   - 如果没有该数据，请点击枚举库管理，新建枚举.
  #### 定义.
   - 枚举分为int类型的枚举与string类型的枚举.
   - Int类型的枚举:政治面貌, 0=群众1=团员,2=党员.
   - String类型的枚举:政治面貌, qz=群众 ty=团员, dy=党员 .
   - 格式为(Item用逗号分开): 事假,病假,婚假,其它

  #### 枚举的存储
   - 枚举主表: Sys_EnumMain 
   - 枚举从表: Sys_Enum
  `);let f=I;export{f as GPN_NewDDL};
