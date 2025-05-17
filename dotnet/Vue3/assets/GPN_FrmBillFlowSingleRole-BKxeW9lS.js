var w=Object.defineProperty;var R=(o,l,e)=>l in o?w(o,l,{enumerable:!0,configurable:!0,writable:!0,value:e}):o[l]=e;var a=(o,l,e)=>R(o,typeof l!="symbol"?l+"":l,e);var m=(o,l,e)=>new Promise((p,s)=>{var S=t=>{try{i(e.next(t))}catch(n){s(n)}},d=t=>{try{i(e.throw(t))}catch(n){s(n)}},i=t=>t.done?p(t.value):Promise.resolve(t.value).then(S,d);i((e=e.apply(o,l)).next())});import{FrmBillFlowSingleRole as h}from"./FrmBillFlowSingleRole-CpHAYf90.js";import{b5 as B,G as F,l as c}from"./entry/index-C6uBgOW5-1730430676707.js";import{MethodFlowSingle as f}from"./MethodFlowSingle-BOCOVakp.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./Method-CoRs5kcO.js";import"./GroupMethod-CUWbZDLV.js";import"./DBAccess-sLO0RM-h.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./PCenter-g1qHGjKQ.js";import"./PowerCenter-xD33lI2C.js";class b extends B{constructor(){super("GPN_FrmBillFlowSingleRole");a(this,"OnlyStartSelfCreateRec",`
  #### 帮助
  - 只能发起自己创建的记录.
  - 
`);a(this,"RegularExpressionSelf",`
  #### 帮助
  - 自定义正则表达式, 请在文本框输入正则表达式，然后执行创建。
  - 请正确的选择事件然.
`);a(this,"SelfFunc",`
  #### 帮助
  - 自定义函数，就是在服务器上创建一个js文件，写入一个函数.
  -  
`);a(this,"JSBody",`
#### 帮助
-  输入函数的脚本.
-  系统就会执行这些脚本，在您指定的事件里.
`);this.ForEntityClassID="TS.MapExt.FrmBillFlowSingleRole",this.PageTitle="单次流程规则"}Init(){this.AddGroup("SingleRole","单次流程规则"),this.AddBlank("AfterOverCanStartFlow","只能在归档后发起流程.",this.HelpUn,"icon-drop"),this.AddBlank("FlowOverUpdateFrmBillData","流程结束后,更新主表数据.",this.HelpUn,"icon-drop"),this.AddBlank("FlowOverDeleteFrmBillData","流程删除后删除单据数据.",this.HelpUn,"icon-drop"),this.AddBlank("FlowReturnStartNodeDeleteFlowData","流程退回到开始节点是删除流程.",this.HelpUn,"icon-drop"),this.AddGroup("StartLimit","发起限制规则"),this.AddBlank("OnlyStartSelfCreateRec","只能发起自己创建的数据.",this.OnlyStartSelfCreateRec,"icon-drop"),this.AddBlank("UnOverFlow","如果改流程没有完成则不能发起.",this.OnlyStartSelfCreateRec,"icon-drop")}GenerSorts(){return m(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,p,s,S,d){return m(this,null,function*(){const i=this.RequestVal("RefPKVal"),t=new f(i);yield t.Retrieve();const n=t.FlowNo+"_"+t.FrmID+"_"+e,r=new h(n);return(yield r.IsExits())==!0?new F(c.Message,"改项目已经存在."):(r.MyPK=n,e=="OnlyStartSelfCreateRec"||e=="UnOverFlow"?r.DBRole="StartLimit":r.DBRole="SingleRole",r.MarkID=e,r.MarkName=this.GetPageName(e),r.FrmID=t.FrmID,r.Docs=t.FlowNo,r.RefPKVal=i,r.SetPara("EnName","TS.MapExt.FrmBillFlowSingleRole"),yield r.Insert(),new F(c.Message,"增加成功."))})}}export{b as GPN_FrmBillFlowSingleRole};
