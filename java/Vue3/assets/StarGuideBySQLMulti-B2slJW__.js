var o=Object.defineProperty;var N=(t,a,e)=>a in t?o(t,a,{enumerable:!0,configurable:!0,writable:!0,value:e}):t[a]=e;var s=(t,a,e)=>N(t,typeof a!="symbol"?a+"":a,e);import{E as m,U as E,h as i}from"./entry/index-C6uBgOW5-1730430676707.js";import{FlowAttr as r}from"./Flow-D4nUES4A.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";class W extends m{constructor(e){super("TS.AttrFlow.StarGuideBySQLMulti");s(this,"DescTag1",`
  #### 帮助
   - 比如:SELECT No, Name, No as EmpNo,Name as EmpName,Email FROM WF_Emp WHERE No LIKE '%@key%'
   - 初始化列表参数，该查询语句必须有No,Name两个列，注意显示数量限制。
   - 很多场合下需要用到父子流程，在启动子流程的时候需要选择一个父流程。
   - 实例:SELECT a.WorkID as No, a.Title as Name, a.Starter, a.WorkID As PWorkID, '011' as PFlowNo, a.FK_Node as PNodeID FROM WF_GenerWorkflow a, WF_GenerWorkerlist b WHERE A.WorkID=b.WorkID AND B.FK_Emp='@WebUser.No' AND B.IsPass=0 AND A.FK_Flow='011' AND a.Title Like '%@Key%'


   
  `);s(this,"DescTag2",`
  #### 帮助
   - 比如:SELECT top 15 No,Name ,No as EmpNo,Name as EmpName ,Email FROM WF_Emp
   - 或者:SELECT No,Name ,No as EmpNo,Name as EmpName ,Email FROM WF_Emp WHERE ROWID < 15
   - 该数据源必须有No,Name两个列, 其他的列要与开始节点表单字段对应。
   - 注意查询的数量，避免太多影响效率。


  `);e&&this.setPKVal(e)}get HisUAC(){const e=new E;return e.IsDelete=!0,e.IsUpdate=!0,e.IsInsert=!0,e}get EnMap(){const e=new i("WF_Flow","按设置的SQL-单条模式");return e.AddTBStringPK(r.No,null,"流程编号",!0,!0,0,10,100,!1),e.AddTBString(r.Name,null,"名称",!0,!0,0,50,200,!1),e.AddTBString(r.StartGuidePara1,null,"查询参数",!0,!1,0,50,200,!0,this.DescTag1),e.AddTBString(r.StartGuidePara2,null,"初始化列表参数",!0,!1,0,50,200,!0,this.DescTag2),this._enMap=e,this._enMap}}export{W as StarGuideBySQLMulti};
