var l=(n,t,s)=>new Promise((i,o)=>{var p=r=>{try{u(s.next(r))}catch(a){o(a)}},d=r=>{try{u(s.throw(r))}catch(a){o(a)}},u=r=>r.done?i(r.value):Promise.resolve(r.value).then(p,d);u((s=s.apply(n,t)).next())});import{j as g,k as c,U as m,h as A}from"./entry/index-C6uBgOW5-1730430676707.js";import{a as e}from"./MapExt-DtQWKcAY.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";class M extends g{constructor(t){super("TS.MapExt.RegularExpression"),t&&(this.MyPK=t)}get HisUAC(){const t=new m;return t.IsDelete=!0,t.IsUpdate=!0,t.IsInsert=!0,t}get EnMap(){const t=new A("Sys_MapExt","正则表达式");return t.AddMyPK(),t.AddTBString(e.FK_MapData,null,"表单ID",!0,!0,0,10,100),t.AddTBString(e.AttrOfOper,null,"字段ID",!0,!0,0,10,100),t.AddTBString(e.Tag6,null,"模式名称",!0,!0,0,10,100,!0),t.AddTBString(e.Tag,null,"事件类型",!0,!0,0,10,100,!1),t.AddTBString(e.Tag1,null,"事件名称",!0,!0,0,10,100,!1),t.AddTBString(e.Tag2,null,"提示信息",!0,!1,0,100,200,!0,`
    #### 帮助
    - 当验证不通过的时，提示的信息.
    - 提示信息不要有特殊字符.
    - 比如：电话号码输入不正确.
    `),t.AddTBStringDoc(e.Doc,null,"表达式",!0,!1,!0,`
    #### 帮助
    - 输入正则表达式内容.
    - 格式: xxewssssss
    `),this._enMap=t,this._enMap}beforeInsert(){return l(this,null,function*(){return Promise.resolve(!0)})}}class D extends c{get GetNewEntity(){return new M}constructor(){super()}}export{M as RegularExpression,D as RegularExpressions};
