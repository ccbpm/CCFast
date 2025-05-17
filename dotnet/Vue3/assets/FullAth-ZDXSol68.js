var f=(r,e,l)=>new Promise((a,i)=>{var o=s=>{try{n(l.next(s))}catch(u){i(u)}},d=s=>{try{n(l.throw(s))}catch(u){i(u)}},n=s=>s.done?a(s.value):Promise.resolve(s.value).then(o,d);n((l=l.apply(r,e)).next())});import{N as c,ba as t,a0 as p,O as S,U as A,L as m}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class y extends c{constructor(e){super("TS.MapExt.FullAth"),e&&(this.MyPK=e)}get HisUAC(){const e=new A;return e.IsDelete=!0,e.IsUpdate=!0,e.IsInsert=!0,e}get EnMap(){const e=new m("Sys_MapExt","填充附件");e.AddMyPK(),e.AddTBString(t.FK_MapData,null,"表单ID",!1,!1,0,10,50,!0),e.AddTBString(t.RefPKVal,null,"RefPKVal",!1,!1,0,10,50,!0),e.AddTBString(t.ExtType,null,"ExtType",!1,!1,0,10,50,!0);const l=`
    #### 帮助
    - 目前仅仅支持从ccform的表单里获取附件数据并填充.
    - 当前填充关联的表单ID, 以及附件ID.
    `,a=`
    #### 帮助
    - 至少返回两个列(有先后顺序)FileName,FileFullName（有要填充源数据的Sort,在返回的列后加上）
    - SQL示例：select FileName,FileFullName FROM sys_frmattachmentdb where RefPKVal='@Key' and NoOfObj='Ath1'.
    `;return e.AddTBString(t.Tag1,null,"附件NoOfObj",!0,!1,0,150,150,!1,l),e.AddTBString(t.Tag2,null,"附件名称",!0,!1,0,150,150,!1,l),e.AddDDLSysEnum(t.DBType,0,"数据源类型",!0,!0,"DBType","@0=执行SQL@1=执行url返回JSON@2=执行CCFromRef.js返回JSON",null,!1),e.AddDDLEntities(t.FK_DBSrc,"local","数据源",new p,!0,null,!1),e.AddTBStringDoc(t.Doc,null,"表达式",!0,!1,!0,a),this._enMap=e,this._enMap}beforeInsert(){return f(this,null,function*(){return Promise.resolve(!0)})}}class g extends S{get GetNewEntity(){return new y}constructor(){super()}}export{y as FullAth,g as FullAths};
