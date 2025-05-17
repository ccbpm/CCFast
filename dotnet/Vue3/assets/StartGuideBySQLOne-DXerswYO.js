var o=Object.defineProperty;var N=(t,a,e)=>a in t?o(t,a,{enumerable:!0,configurable:!0,writable:!0,value:e}):t[a]=e;var s=(t,a,e)=>N(t,typeof a!="symbol"?a+"":a,e);import{I as m,a0 as E,U as l,L as i}from"./entry/index-M8VErHPE-1727507756861.js";import{FlowAttr as r}from"./Flow-D1QXg1UO.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class F extends m{constructor(e){super("TS.AttrFlow.StartGuideBySQLOne");s(this,"DescTag1",`
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


  `);s(this,"DescTag3",`
  #### 帮助
   - 比如:SELECT No as EmpNo,Name as EmpName ,Email FROM WF_Emp WHERE No='@Key'
   - 该数据源返回的列名大小写与开始节点表单的字段名匹配， 只有匹配成功的才能赋值.


  `);e&&this.setPKVal(e)}get HisUAC(){const e=new l;return e.IsDelete=!0,e.IsUpdate=!0,e.IsInsert=!0,e}get EnMap(){const e=new i("WF_Flow","按设置的SQL-单条模式");return e.AddTBStringPK(r.No,null,"流程编号",!0,!0,0,10,100,!1),e.AddTBString(r.Name,null,"名称",!0,!0,0,50,200,!1),e.AddDDLEntities("FK_DBSrc",null,"数据源",new E,!0,null,!0),e.AddTBString(r.StartGuidePara1,null,"查询参数",!0,!1,0,50,200,!0,this.DescTag1),e.AddTBString(r.StartGuidePara2,null,"初始化列表参数",!0,!1,0,50,200,!0,this.DescTag2),e.AddTBString(r.StartGuidePara3,null,"装载一行的数据SQL",!0,!1,0,50,200,!0,this.DescTag3),this._enMap=e,this._enMap}}export{F as StartGuideBySQLOne};
