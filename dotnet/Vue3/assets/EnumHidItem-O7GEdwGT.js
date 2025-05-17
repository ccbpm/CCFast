var n=Object.defineProperty;var p=(s,e,t)=>e in s?n(s,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):s[e]=t;var r=(s,e,t)=>p(s,typeof e!="symbol"?e+"":e,t);import{j as i,U as u,h as m}from"./entry/index-C6uBgOW5-1730430676707.js";import{a}from"./MapExt-DtQWKcAY.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";class I extends i{constructor(t){super("TS.MapExt.EnumHidItem");r(this,"Desc1",`
  #### 说明
   - 设置格式
   - @0=1,2,3
   - @1=4,5
   - 标识当选项是0的时候,枚举值隐藏1,2,3 当选项=1的时候，隐藏4,5.
   #### 枚举值
   - 请打开枚举库查看, 对应的IntKey.
   - 也可以通过,select * from sys_enum WHERE EnumKey='xxxxx' 查看 枚举的键值.
  `);this.RefEnName="TS.Sys.MapExt",t&&(this.MyPK=t)}get HisUAC(){const t=new u;return t.IsDelete=!0,t.IsUpdate=!0,t.IsInsert=!0,t}get EnMap(){const t=new m("Sys_MapExt","点击事件隐藏选项");return t.AddMyPK(),t.AddTBString(a.FK_MapData,null,"表单ID",!0,!0,100),t.AddDDLSQL(a.Tag,null,"联动的控件",`
    SELECT KeyOfEn as No, Name FROM Sys_MapAttr 
    WHERE LGType=1 AND UIVisible=1 AND UIIsEnable=1 AND FK_MapData='@FK_MapData' AND KeyOfEn!='@AttrKey'`,!0,null,!1,100),t.AddTBStringDoc(a.Tag4,null,"配置项",!0,!1,!0,this.Desc1),this._enMap=t,this._enMap}}export{I as EnumHidItem};
