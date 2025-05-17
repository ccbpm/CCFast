var P=Object.defineProperty;var d=(r,t,a)=>t in r?P(r,t,{enumerable:!0,configurable:!0,writable:!0,value:a}):r[t]=a;var D=(r,t,a)=>d(r,typeof t!="symbol"?t+"":t,a);var i=(r,t,a)=>new Promise((p,n)=>{var c=e=>{try{s(a.next(e))}catch(o){n(o)}},u=e=>{try{s(a.throw(e))}catch(o){n(o)}},s=e=>e.done?p(e.value):Promise.resolve(e.value).then(c,u);s((a=a.apply(r,t)).next())});import{b9 as l,aQ as m,aB as h,aC as w}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class y extends l{constructor(){super("GPN_MapDataVer");D(this,"Docs0",`
  #### 帮助
  - 表单版本.
  - 记录表单目前的版本号。包括创建人，创新时间等信息。
  - 当表单增加了字段后，可以创建新的版本，而再运行此表单时，所运行的版本号就为新的版本号。
  - 在一个时间内只有一个运行的版本，所运行的版本不一定是最新的版本。
  
  #### 应用场景
  - 比如出库流程，当运行一段时间后，需要增加出库商品批号。当在表单中增加这个字段后，就可以在表单属性中增加一个新的版本号。
  - 那么再进行出库操作时，就运行新版本的表单。

  #### 数据结构

  - 新增数据版本后，数据信息存入到AtPara字段里.

`);this.PageTitle="新建表单版本",this.ForEntityClassID="TS.FrmUI.MapDataVer"}Init(){this.AddGroup("A","新建表单版本"),this.AddBlank("0","新建表单版本",this.Docs0)}GenerSorts(){return i(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(a,p,n,c,u){return i(this,null,function*(){if(window.confirm("您确定要创建新版本吗？")==!1)return;const s=this.RequestVal("RefPKVal"),e=new m("BP.Sys.MapData",s);yield e.Init();const o=yield e.DoMethodReturnString("CreateMapDataVer");return new h(w.Message,o)})}}export{y as GPN_MapDataVer};
