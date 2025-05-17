var g=Object.defineProperty;var B=(o,n,r)=>n in o?g(o,n,{enumerable:!0,configurable:!0,writable:!0,value:r}):o[n]=r;var p=(o,n,r)=>B(o,typeof n!="symbol"?n+"":n,r);var T=(o,n,r)=>new Promise((f,S)=>{var I=e=>{try{i(r.next(e))}catch(t){S(t)}},N=e=>{try{i(r.throw(e))}catch(t){S(t)}},i=e=>e.done?f(e.value):Promise.resolve(e.value).then(I,N);i((r=r.apply(o,n)).next())});import{SysEnumMain as h}from"./SysEnumMain-CBhfewK0.js";import{b5 as K,W as d,C as M,G as l,l as m,aH as C,D as x}from"./entry/index-C6uBgOW5-1730430676707.js";import{b as O}from"./antd-Dd9L3uAF.js";import"./SysEnum-DlgPT0C2.js";import"./vue-BXIlYw1E.js";const c=class c extends K{constructor(){super("GPN_Enum"),this.PageTitle="新建枚举",this.ForEntityClassID="TS.FrmUI.SysEnumMain"}Init(){this.AddGroup("B","新建枚举"),this.TextBox3_NameNoNote("NewIntEnum","新建int类型枚举",c.NewIntEnum,"","枚举ID","枚举名称","请输入枚举值",""),this.TextBox3_NameNoNote("NewStrEnum","新建String类型枚举",c.NewStrEnum,"","枚举ID","枚举名称","请输入枚举值","")}GenerSorts(){return T(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(n,r,f,S,I){return T(this,null,function*(){const N=f,i=S;let e=I.trim();const t=new h;if(d.CCBPMRunModel==M.Single?t.No=i:t.No=d.OrgNo+"_"+i,t.EnumKey=i,(yield t.IsExits())===!0){O.warning("枚举值已经存在"+i);return}if(t.Name=N,t.OrgNo=d.OrgNo,e=e.replaceAll("，",","),e=e.replaceAll("＠","@"),e=e.replaceAll("＝","="),n==="NewIntEnum"){if(!e.includes(",")&&!e.includes("@"))return new l(m.Error,"多个枚举值使用 , 或 @ 符号分开.");for(let a=0;a<20;a++)t.SetValByKey("Idx"+a,a);e.indexOf("@")==-1?e.split(",").forEach((u,s)=>{t.SetValByKey("Idx"+s,s),t.SetValByKey("Val"+s,u)}):C(e).forEach((u,s)=>{const[E,y]=u.split("=");t.SetValByKey("Idx"+s,E),t.SetValByKey("Val"+s,y)}),t.EnumType=0,t.CfgVal=e,t.SetPara("EnName","TS.FrmUI.SysEnumMainInt");try{yield t.Insert()}catch(a){yield t.Insert()}yield t.SaveDtls();const w="/@/WF/Comm/En.vue?EnName=TS.FrmUI.SysEnumMainInt&PKVal="+t.No;return new l(m.GoToUrl,w)}if(n==="NewStrEnum"){if(e.indexOf(",")==-1)return new l(m.Error,"多个枚举值使用逗号分开.");if(e.indexOf("@")==-1){const a=e.split(",");let u="",s=-1;a.forEach(E=>{if(s++,E.indexOf("=")==-1)return new l(m.Error,"枚举键和枚举值使用等号连接.");const y=E.split("=");if(x.IsNullOrEmpty(y[0]))return new l(m.Error,"请填写枚举键.");if(x.IsNullOrEmpty(y[1]))return new l(m.Error,"请填写枚举值.");u+="@"+E,t.SetValByKey("Idx"+s,y[0]),t.SetValByKey("Val"+s,y[1])}),e=u}t.EnumType=1,t.EnumKey=i,t.Name=N,t.CfgVal=e,t.SetPara("EnName","TS.FrmUI.SysEnumMainString");try{yield t.Insert()}catch(a){yield t.Insert()}yield t.SaveDtls();const w="/@/WF/Comm/En.vue?EnName=TS.FrmUI.SysEnumMainString&PKVal="+t.No;return new l(m.GoToUrl,w)}})}};p(c,"NewStrEnum",`
  #### 帮助
  - 填写格式: 枚举值=枚举标签;
  - 例如1: ty=团员,dy=党员,qz=群众
  - 例如2: shijia=事假,bingjia=病假,hunjia=婚假
  #### 数据存储.
  - string类型的枚举也称为标记枚举,字母存储一个列,标签存储一个列.
  - 在表单里字段是abc,那系统就会自动创建一个影子字段 abcT.
  - abc字段存储的是标记, abcT存储的是标签.
  - 这一点与外部数据源存储一致.
  `),p(c,"NewIntEnum",`
  #### 帮助
  - 填写格式0: 事假,病假,其它
  - 填写格式1: 团员,党员,群众
  - 填写格式2: @0=团员@1=党员@2=群众
  #### 数据存储
  - int类型的枚举值是常用的数据类型，ccfrom是格式化的存储到数据表里.
  - 创建一个int类型的字段，用于存储枚举的数据.
  `);let V=c;export{V as GPN_Enum};
