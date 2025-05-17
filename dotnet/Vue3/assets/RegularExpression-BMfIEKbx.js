var o=(n,t,s)=>new Promise((l,i)=>{var d=e=>{try{u(s.next(e))}catch(a){i(a)}},p=e=>{try{u(s.throw(e))}catch(a){i(a)}},u=e=>e.done?l(e.value):Promise.resolve(e.value).then(d,p);u((s=s.apply(n,t)).next())});import{N as g,ba as r,O as c,U as A,L as M}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class T extends g{constructor(t){super("TS.MapExt.RegularExpression"),t&&(this.MyPK=t)}get HisUAC(){const t=new A;return t.IsDelete=!0,t.IsUpdate=!0,t.IsInsert=!0,t}get EnMap(){const t=new M("Sys_MapExt","正则表达式");return t.AddMyPK(),t.AddTBString(r.FK_MapData,null,"表单ID",!0,!0,0,10,100),t.AddTBString(r.AttrOfOper,null,"字段ID",!0,!0,0,10,100),t.AddTBString(r.Tag6,null,"模式名称",!0,!0,0,10,100,!0),t.AddTBString(r.Tag,null,"事件类型",!0,!0,0,10,100,!1),t.AddTBString(r.Tag1,null,"事件名称",!0,!0,0,10,100,!1),t.AddTBString(r.Tag2,null,"提示信息",!0,!1,0,10,200,!0,`
    #### 帮助
    - 当验证不通过的时，提示的信息.
    - 提示信息不要有特殊字符.
    - 比如：电话号码输入不正确.
    `),t.AddTBStringDoc(r.Doc,null,"表达式",!0,!1,!0,`
    #### 帮助
    - 输入正则表达式内容.
    - 格式: xxewssssss
    `),this._enMap=t,this._enMap}beforeInsert(){return o(this,null,function*(){return Promise.resolve(!0)})}}class y extends c{get GetNewEntity(){return new T}constructor(){super()}}export{T as RegularExpression,y as RegularExpressions};
