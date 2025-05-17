var c=Object.defineProperty;var N=(s,t,r)=>t in s?c(s,t,{enumerable:!0,configurable:!0,writable:!0,value:r}):s[t]=r;var l=(s,t,r)=>N(s,typeof t!="symbol"?t+"":t,r);var n=(s,t,r)=>new Promise((p,u)=>{var i=e=>{try{o(r.next(e))}catch(a){u(a)}},h=e=>{try{o(r.throw(e))}catch(a){u(a)}},o=e=>e.done?p(e.value):Promise.resolve(e.value).then(i,h);o((r=r.apply(s,t)).next())});import{Auth as y}from"./Auth-BHgKMLO6.js";import{b9 as A,aM as m,W as D,aD as P}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class I extends A{constructor(){super("GPN_Auther");l(this,"Docs0",`
  #### 帮助
  - 按照节点表单的字段作为抄送人.
  - 通常是在节点表单上加一个字段,这个字段存储的是人员账号，多个人员使用逗号分开.
  #### 运行图例
  - @liang.

`);l(this,"Docs1",`
  #### 帮助
  - 自动抄送给要绑定的人员.
`);l(this,"Docs2",`
  #### 帮助
  - 按照绑定的部角色下的人员集合作为抄送人.
  - 有一个规则
  
`);this.PageTitle="新增授权人",this.ForEntityClassID="TS.Port.Auth"}Init(){this.AddGroup("A","请选择规则"),this.SelectItemsByTreeEns("1","选择授权人",this.Docs1,!1,m.srcDeptLazily,m.srcDeptRoot,m.srcEmpLazily,"@No=账号@Name=名称@Tel=电话")}GenerSorts(){return n(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(r,p,u,i,h){return n(this,null,function*(){let o="";o=this.RequestVal("RefPKVal");const e=new y;if(e.MyPK=D.No+"_"+u,(yield e.IsExits())==!0){alert("人员["+i+"]已经被选择.");return}e.Auther=o,e.AuthType=0,e.AutherToEmpNo=u,e.AutherToEmpName=i,e.RDT=P.CurrentDate,yield e.Insert();let a="";return a="/@/WF/Comm/EnOnly.vue?EnName=TS.Port.Auth&PKVal="+e.MyPK,"url@"+a})}}export{I as GPN_Auther};
