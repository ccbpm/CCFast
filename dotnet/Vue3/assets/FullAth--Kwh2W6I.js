var i=(s,e,r)=>new Promise((a,o)=>{var f=l=>{try{n(r.next(l))}catch(u){o(u)}},p=l=>{try{n(r.throw(l))}catch(u){o(u)}},n=l=>l.done?a(l.value):Promise.resolve(l.value).then(f,p);n((r=r.apply(s,e)).next())});import{j as d,k as m,U as c,h as S}from"./entry/index-C6uBgOW5-1730430676707.js";import{a as t}from"./MapExt-DtQWKcAY.js";import{SFDBSrc as A}from"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";class y extends d{constructor(e){super("TS.MapExt.FullAth"),e&&(this.MyPK=e)}get HisUAC(){const e=new c;return e.IsDelete=!0,e.IsUpdate=!0,e.IsInsert=!0,e}get EnMap(){const e=new S("Sys_MapExt","填充附件");e.AddMyPK(),e.AddTBString(t.FK_MapData,null,"表单ID",!1,!1,0,10,50,!0),e.AddTBString(t.RefPKVal,null,"RefPKVal",!1,!1,0,10,50,!0),e.AddTBString(t.ExtType,null,"ExtType",!1,!1,0,10,50,!0);const r=`
    #### 帮助
    - 目前仅仅支持从ccform的表单里获取附件数据并填充.
    - 当前填充关联的表单ID, 以及附件ID.
    `,a=`
    #### 帮助
    - 返回三个列(有先后顺序)FileName,FileFullName,Sort
    - SQL示例：select FileName,FileFullName,Sort FROM sys_frmattachmentdb where RefPKVal='@Key' and NoOfObj='Ath1'.
    `;return e.AddTBString(t.Tag1,null,"附件NoOfObj",!0,!1,0,150,150,!1,r),e.AddTBString(t.Tag2,null,"附件名称",!0,!1,0,150,150,!1,r),e.AddDDLSysEnum(t.DBType,0,"数据源类型",!0,!0,"DBType","@0=执行SQL@1=执行url返回JSON@2=执行CCFromRef.js返回JSON",null,!1),e.AddDDLEntities(t.FK_DBSrc,"local","数据源",new A,!0,null,!1),e.AddTBStringDoc(t.Doc,null,"表达式",!0,!1,!0,a),this._enMap=e,this._enMap}beforeInsert(){return i(this,null,function*(){return Promise.resolve(!0)})}}class N extends m{get GetNewEntity(){return new y}constructor(){super()}}export{y as FullAth,N as FullAths};
