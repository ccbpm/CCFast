var m=Object.defineProperty;var c=(o,e,t)=>e in o?m(o,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):o[e]=t;var d=(o,e,t)=>c(o,typeof e!="symbol"?e+"":e,t);var p=(o,e,t)=>new Promise((l,i)=>{var n=s=>{try{r(t.next(s))}catch(a){i(a)}},u=s=>{try{r(t.throw(s))}catch(a){i(a)}},r=s=>s.done?l(s.value):Promise.resolve(s.value).then(n,u);r((t=t.apply(o,e)).next())});import{aM as S,U as f,h}from"./entry/index-C6uBgOW5-1730430676707.js";import{EntityNodeID as N}from"./EntityNodeID-BCBbJNH2.js";import{GPE_ShenFenModel as y}from"./GPE_ShenFenModel-CSm9b2hc.js";import{NodeStations as I,NodeStation as D}from"./NodeStation-BssN0CY9.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./PageBaseGroupEdit-IicyYiex.js";import"./Help-D0bDMZWg.js";import"./Node-DKGUVcK1.js";class T extends N{constructor(t){super("TS.WF.AccepterRoleBindStation");d(this,"Help1",`
  1. 岗位切片-严谨模式：如果配置了严谨模式，效果是有部分人员绑定了这几个岗位，这几个岗位必须每个岗位下都有人被绑定，如果有一个岗位下没查到对应岗位的人员，就算所有岗位没查到人员数据。
  2. 岗位切片-宽泛模式：如果配置了宽泛模式，效果是有部分人员绑定了这几个岗位，不深究每个岗位是否有人员被绑定，查找这几个岗位下人员数据的并集，最终查到的人员数据最少一条。如果在这几个岗位下都没有人员被绑定，也就是没有查到人员，会给前台一个提示。

这里的岗位等同于角色，不同的业务场景叫法不同，意义一样。
  `);d(this,"Help2",`
  #### 帮助 -岗位找人规则.
  - 寻找人的路径, 在树结构里面.
  #### 岗位找人规则 
  - 递归父级,父级平级寻找.
  - 递归父级寻找.
 `);t&&(this.NodeID=t)}get HisUAC(){const t=new f;return t.IsDelete=!1,t.IsUpdate=!0,t.IsInsert=!1,t}get EnMap(){const t=new h("WF_Node","绑定角色");return t.AddTBIntPK("NodeID",0,"节点ID",!0),t.AddTBInt("ShenFenModel",0,"身份规则",!1,!1),t.AddTBString("NodeStations",null,"角色",!0,!1,0,100,100,!0),t.SetPopGroupList("NodeStations",S.srcStationTypes,S.srcStations,!0,"300px","500px","选择角色","icon-people","1"),t.AddDDLSysEnum("StationReqEmpsWay",0,"岗位计算方式",!0,!0,"StationReqEmpsWay","@0=角色集合模式@1=岗位切片-严谨模式@2=岗位切片-宽泛模式",this.Help1,!1),t.AddDDLSysEnum("StationFindWay",0,"岗位找人规则",!0,!0,"StationFindWay","@0=递归父级,父级平级寻找@1=递归父级直线寻找@2=向下级寻找",this.Help2,!1),t.AddTBAtParas(4e3),t.ParaFields=",ShenFenModel,StationReqEmpsWay,StationFindWay,",t.AddRM_GPE(new y,"icon-drop"),this._enMap=t,this._enMap}afterUpdate(){return p(this,null,function*(){return yield new I().Delete("FK_Node",this.NodeID),typeof this.NodeStations!="string"||this.NodeStations.split(",").forEach(i=>p(this,null,function*(){const n=new D;n.FK_Node=this.NodeID,n.FK_Station=i,n.MyPK=this.NodeID+"_"+i,yield n.Insert()})),Promise.resolve(!0)})}}export{T as AccepterRoleBindStation};
