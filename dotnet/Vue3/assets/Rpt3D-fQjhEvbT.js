import{I as s,K as a,U as u,L as l}from"./entry/index-M8VErHPE-1727507756861.js";import{MenuAttr as t}from"./Menu-CUL0m0Wx.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./PCenter-CGZJ3ajQ.js";import"./PowerCenter-B1QbxnUu.js";class o extends s{constructor(e){super("TS.CCFast.Rpt3D"),e&&(this.No=e)}get HisUAC(){const e=new u;return e.IsDelete=!0,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new l("GPM_Menu","三维报表");e.AddTBStringPK(t.No,null,"编号",!1,!1,1,90,50),e.AddTBString(t.Icon,null,"Icon",!0,!1,0,50,50),e.AddTBString(t.Name,null,"菜单名称",!0,!1,0,200,200),e.AddTBString(t.Title,null,"报表标题",!0,!1,0,200,200,!0),e.AddTBString(t.Tag4,null,"分析项目名称",!0,!1,0,200,200),e.AddDDLSysEnum(t.ListModel,0,"维度显示格式",!0,!0,"RptModel","@0=左边@1=顶部"),e.AddDDLSysEnum(t.TagInt1,0,"合计位置?",!0,!0,"Rpt3SumModel","@0=不显示@1=底部@2=头部"),e.AddTBStringDoc(t.Tag0,null,"数据源SQL",!0,!1,!0);let n="编写说明";return n+=`	
 1. 该数据源一般是一个分组统计语句, 比如： SELECT D1,D2,D3,SUM(XX) AS Num FROM MyTable WHERE 1=2 GROUP BY D1,D2,D3  `,n+=`	
 2. 对应的数据列分别是 如下数据源的列数据，列的顺序不要改变。 `,n+=`	
 3. 每个维度都是返回的No,Name两个列的数据。 `,n+=`	
 3，DEMO `,n+=`	
 数据源：SELECT FK_BanJi,XB,ZZMM, COUNT(*) as Num from Demo_Student GROUP BY FK_BanJi,XB,ZZMM`,n+=`	
 维度1：SELECT No,Name FROM demo_banji `,n+=`	
 维度2：SELECT IntKey as No, Lab as Name FROM sys_enum WHERE EnumKey='XB' `,n+=`	
 维度3：SELECT IntKey as No, Lab as Name FROM sys_enum WHERE EnumKey='ZZMM'`,e.SetHelperAlert(t.Tag0,n),e.AddTBStringDoc(t.Tag1,null,"维度1SQL",!0,!1,!0),e.AddTBStringDoc(t.Tag2,null,"维度2SQL",!0,!1,!0),e.AddTBStringDoc(t.Tag3,null,"维度3SQL",!0,!1,!0),this._enMap=e,this._enMap}}class D extends a{get GetNewEntity(){return new o}constructor(){super()}}export{o as Rpt3D,D as Rpt3Ds};
