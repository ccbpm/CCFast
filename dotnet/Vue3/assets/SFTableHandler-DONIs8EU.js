import{I as s,cx as t,a0 as a,K as n,U as l,L as d}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class u extends s{constructor(e){super("TS.FrmUI.SFTableHandler"),e&&this.setPKVal(e)}get HisUAC(){const e=new l;return e.IsDelete=!0,e.IsUpdate=!0,e}get EnMap(){const e=new d("Sys_SFTable","Handler字典表");return e.AddTBStringPK(t.No,null,"编号",!0,!0,1,200,20),e.AddTBString(t.Name,null,"名称",!0,!1,0,200,20),e.AddDDLEntities(t.FK_SFDBSrc,"local","数据源",new a,!0),e.AddDDLSysEnum(t.CodeStruct,0,"字典表类型",!0,!0,t.CodeStruct,"@0=编号名称类型@1=树结构类型"),e.AddTBString(t.SelectStatement,null,"地址",!0,!1,0,1e3,600,!0,`
        #### 帮助
         -  WebAPI的输入格式：@WebApiHost/DataUser/GetEmps?id=51184
         - 此处只支持用户定义好的固定参数，比如：id=51184
        #### 其它
        - 访问ccfrom提供的内置的handler,开发人员进行重写返回数据.
        - 表单在运行的时候,通过访问这个服务，携带设置的参数，解析返回的数据，展现在表单的控件上.
        `),e.AddTBString(t.RootVal,null,"根节点值",!0,!1,0,1e3,600,!0,` 
        #### 帮助
        - 对树形结构的字段有效.
        - 根目录的parentNo数据.
        `),this._enMap=e,this._enMap}DoEdit(){}}class m extends n{get GetNewEntity(){return new u}constructor(){super()}}export{u as SFTableHandler,m as SFTableHandlers};
