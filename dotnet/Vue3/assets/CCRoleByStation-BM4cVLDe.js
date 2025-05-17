import{N as s,aM as r,U as o,L as a}from"./entry/index-M8VErHPE-1727507756861.js";import{CCRoleAttr as e}from"./CCRole-BYc1Lkgq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";class m extends s{constructor(t){super("TS.MapExt.CCRoleByStation"),t&&(this.MyPK=t)}get HisUAC(){const t=new o;return t.IsDelete=!0,t.IsUpdate=!0,t.IsInsert=!1,t}get EnMap(){const t=new a("WF_CCRole","按角色抄送");return t.AddMyPK(),t.AddTBString(e.FlowNo,null,"流程编号",!1,!1,0,10,50,!0),t.AddDDLSysEnum(e.CCStaWay,0,"角色计算规则",!0,!0,e.CCStaWay,`
     @0=仅按角色计算
     @1=按角色智能计算(当前节点)
     @2=按角色智能计算(发送到节点)
     @3=按角色与部门的交集
     @4=按直线上级部门找角色下的人员(当前节点)
     @5=按直线上级部门找角色下的人员(接受节点)
     `),t.AddTBStringDoc(e.EnIDs,null,"角色",!0,!1,!0),t.SetPopGroupList(e.EnIDs,r.srcStationTypes,r.srcStations,!0),t.AddTBStringDoc(e.Tag2,null,"部门",!0,!1,!0,"只有选择按角色与部门的交集时配置部门信息"),t.SetPopTree(e.Tag2,r.srcDepts,r.srcDeptRoot,!0,"300px","500px","选择部门","icon-people"),t.AddTBAtParas(),this._enMap=t,this._enMap}}export{m as CCRoleByStation};
