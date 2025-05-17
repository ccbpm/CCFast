var g=Object.defineProperty;var B=(o,n,r)=>n in o?g(o,n,{enumerable:!0,configurable:!0,writable:!0,value:r}):o[n]=r;var p=(o,n,r)=>B(o,typeof n!="symbol"?n+"":n,r);var T=(o,n,r)=>new Promise((I,S)=>{var f=t=>{try{i(r.next(t))}catch(e){S(e)}},N=t=>{try{i(r.throw(t))}catch(e){S(e)}},i=t=>t.done?I(t.value):Promise.resolve(t.value).then(f,N);i((r=r.apply(o,n)).next())});import{SysEnumMain as h}from"./SysEnumMain-Bn9Li5_w.js";import{b9 as K,W as d,J as M,aB as l,aC as m,aI as C,aD as x}from"./entry/index-M8VErHPE-1727507756861.js";import{e as O}from"./antd-DkiF_jXA.js";import"./SysEnum-B89JeOjj.js";import"./vue-DGeTOT5N.js";const c=class c extends K{constructor(){super("GPN_Enum"),this.PageTitle="新建枚举",this.ForEntityClassID="TS.FrmUI.SysEnumMain"}Init(){this.AddGroup("B","新建枚举"),this.TextBox3_NameNoNote("NewIntEnum","新建int类型枚举",c.NewIntEnum,"","枚举ID","枚举名称","请输入枚举值",""),this.TextBox3_NameNoNote("NewStrEnum","新建String类型枚举",c.NewStrEnum,"","枚举ID","枚举名称","请输入枚举值","")}GenerSorts(){return T(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(n,r,I,S,f){return T(this,null,function*(){const N=I,i=S;let t=f.trim();const e=new h;if(d.CCBPMRunModel==M.Single?e.No=i:e.No=d.OrgNo+"_"+i,e.EnumKey=i,(yield e.IsExits())===!0){O.warning("枚举值已经存在"+i);return}if(e.Name=N,e.OrgNo=d.OrgNo,t=t.replaceAll("，",","),t=t.replaceAll("＠","@"),t=t.replaceAll("＝","="),n==="NewIntEnum"){if(!t.includes(",")&&!t.includes("@"))return new l(m.Error,"多个枚举值使用 , 或 @ 符号分开.");for(let a=0;a<20;a++)e.SetValByKey("Idx"+a,a);t.indexOf("@")==-1?t.split(",").forEach((u,s)=>{e.SetValByKey("Idx"+s,s),e.SetValByKey("Val"+s,u)}):C(t).forEach((u,s)=>{const[E,y]=u.split("=");e.SetValByKey("Idx"+s,E),e.SetValByKey("Val"+s,y)}),e.EnumType=0,e.CfgVal=t,e.SetPara("EnName","TS.FrmUI.SysEnumMainInt");try{yield e.Insert()}catch(a){yield e.Insert()}yield e.SaveDtls();const w="/@/WF/Comm/En.vue?EnName=TS.FrmUI.SysEnumMainInt&PKVal="+e.No;return new l(m.GoToUrl,w)}if(n==="NewStrEnum"){if(t.indexOf(",")==-1)return new l(m.Error,"多个枚举值使用逗号分开.");if(t.indexOf("@")==-1){const a=t.split(",");let u="",s=-1;a.forEach(E=>{if(s++,E.indexOf("=")==-1)return new l(m.Error,"枚举键和枚举值使用等号连接.");const y=E.split("=");if(x.IsNullOrEmpty(y[0]))return new l(m.Error,"请填写枚举键.");if(x.IsNullOrEmpty(y[1]))return new l(m.Error,"请填写枚举值.");u+="@"+E,e.SetValByKey("Idx"+s,y[0]),e.SetValByKey("Val"+s,y[1])}),t=u}e.EnumType=1,e.EnumKey=i,e.Name=N,e.CfgVal=t,e.SetPara("EnName","TS.FrmUI.SysEnumMainString");try{yield e.Insert()}catch(a){yield e.Insert()}yield e.SaveDtls();const w="/@/WF/Comm/En.vue?EnName=TS.FrmUI.SysEnumMainString&PKVal="+e.No;return new l(m.GoToUrl,w)}})}};p(c,"NewStrEnum",`
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
