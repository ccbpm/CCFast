var O=Object.defineProperty;var D=(S,d,o)=>d in S?O(S,d,{enumerable:!0,configurable:!0,writable:!0,value:o}):S[d]=o;var n=(S,d,o)=>D(S,typeof d!="symbol"?d+"":d,o);var T=(S,d,o)=>new Promise((u,i)=>{var s=a=>{try{p(o.next(a))}catch(m){i(m)}},f=a=>{try{p(o.throw(a))}catch(m){i(m)}},p=a=>a.done?u(a.value):Promise.resolve(a.value).then(s,f);p((o=o.apply(S,d)).next())});import{b5 as I,aM as C,G as N,l as c,bd as x,C as K,W}from"./entry/index-C6uBgOW5-1730430676707.js";import{Cond as A}from"./Cond-Ca-U73Si.js";import{Direction as R}from"./Direction-B015Y8Pg.js";import{GloComm as y}from"./GloComm-CmAl8MpM.js";import{Node as M}from"./Node-DKGUVcK1.js";import{CCRole as w}from"./CCRole-G1BkUVVW.js";import{MapAttr as L}from"./MapAttr-B1mxD3vP.js";import{a as P}from"./NodeExt-BCOk6gzz.js";import{SysEnumMain as _}from"./SysEnumMain-CBhfewK0.js";import{b as F}from"./antd-Dd9L3uAF.js";import"./vue-BXIlYw1E.js";import"./DBAccess-sLO0RM-h.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./Events-D9tOL1Ad.js";import"./EntityNodeID-BCBbJNH2.js";import"./FrmTrack-BAfWiAdt.js";import"./BtnLab-BTiMwJb0.js";import"./EnumLab-CLN2gIih.js";import"./PageBaseGroupEdit-IicyYiex.js";import"./Help-D0bDMZWg.js";import"./DeliveryWay-BAsDbr-9.js";import"./AccepterRoleBindStation-CEITKGnx.js";import"./GPE_ShenFenModel-CSm9b2hc.js";import"./NodeStation-BssN0CY9.js";import"./AccepterRoleBindEmp-DQKLnjLB.js";import"./NodeEmp-CN5SErX3.js";import"./AccepterRoleBindDeptStation-CTkDOR_h.js";import"./NodeDept-DLibq-tF.js";import"./AccepterRoleBindDept-CgdW7OqF.js";import"./AccepterRoleBindDeptOrg-Pd3Ji8_C.js";import"./AccepterRoleBindSFTable-DIu3xImw.js";import"./ARBindWebApi-xvrolXsb.js";import"./ARBindStationSpecDept-CY7LzFqG.js";import"./GPE_ARStaModel-_B7lsqpX.js";import"./ARStation-YvfO3CLZ.js";import"./AR501-RrRAl7X5.js";import"./AccepterRoleMLeader-BRQpKWHc.js";import"./ARWebAPI-Czfk0n86.js";import"./RouteAttr2Emp-k06_lrYd.js";import"./GPE_TurnTo-CGRQb3uY.js";import"./GPE_TodolistModel-COXqeGTz.js";import"./GPE_TeamleaderConfirm-DKddBPNI.js";import"./GPE_XieZuoOverRole-CaCiZrjR.js";import"./NodeToolbar-A9ZMbD-I.js";import"./EntityOID-DvdPWQGp.js";import"./SubFlow-VinOXzJF.js";import"./GPE_OvertimeRole-Pdfqwy2u.js";import"./FrmNodeExt-DymiwDmL.js";import"./FrmNode-C2BSQEvr.js";import"./GPE_FrmNodeEnableRole-7ITrN3WK.js";import"./GPE_FrmCtrlSln-C6rnEHuy.js";import"./FrmNodeBatch-da6VVbq0.js";import"./GPE_FrmTransfer-DlxiFcAW.js";import"./FrmTransferCustom-NtBPN4_I.js";import"./SysEvent-D4PfKXg3.js";import"./PushMsg-IncdEGNL.js";import"./GPE_BlockModel-BXsQjRK3.js";import"./GPE_CCWriteRole-xk6AuPdj.js";import"./MapData-Ccsy8tbB.js";import"./EnumLab-CzismWql.js";import"./HttpHandler-DNmkgeyq.js";import"./SysEnum-DlgPT0C2.js";class r{}n(r,"CondByFrm","0"),n(r,"StandAloneFrm","1"),n(r,"CondStation","2"),n(r,"CondDept","3"),n(r,"CondBySQL","4"),n(r,"CondBySQLTemplate","5"),n(r,"CondByPara","6"),n(r,"CondByUrl","7"),n(r,"CondByWebApi","8"),n(r,"CondByWorkCheck","9"),n(r,"CondLeaderOfDept","10"),n(r,"CondEmp","11"),n(r,"Operator","100");class Je extends I{constructor(){super("GPN_Cond");n(this,"CondByWorkCheck",`
  #### 帮助
  - 审核组件立场.

  `);n(this,"ByFrm",`
  #### 帮助
  - 按照表单的字段值来计算,是常用的一个方向条件,前提是您不是采用sdk模式开发的,是使用ccbpm内置的表单.
  - 场景: 请假流程的请假天数转向条件, 合同审批流程的合同金额,作为转向条件.
  - 请选择一个表单,点击创建按钮.
  #### 配置实例图
  - 当有多个条件的时这些才有用.
  `);n(this,"ByCond100",`
  #### 帮助
  - 条件表达式包括 ( 、) 、AND、 OR 四个类型.
  - 用于链接条件, 只有正确的配置好条件表达式条件才可以工作.
  - 我们提供检查功能，来帮助您检查条件表达式是否正确.
  - 您可以使用拖动的，来调整位置.
  #### 其它
  - 当有多个条件的时这些才有用.
  `);n(this,"BySQL",`
  #### 帮助
  - 按SQL表达式计算.
  - 设置一个查询SQL, 返回一行一列，获取一个数值，如果大于 0条件=true, 否则 =false.
  - 比如: SELECT count(*) from port_emp WHERE FK_Dept='@WebUser.DeptNo' or xxxx=@MyFieldName
  - 支持ccbpm表达式, 可以获取当前登录人员的信息变量,也可以获取表单字段变量.
  `);n(this,"sqlTemplate",`
  #### 帮助
  - 按照SQL模板计算，与按SQL计算类似. 我们把常用的SQL放入sql模板库存储起来.
  - 这里只是一个引用，不是copy. 就是说当sql模板的配置信息变化后，这里跟着变化.
  - 支持ccbpm表达式, 可以获取当前登录人员的信息变量,也可以获取表单字段变量.
  #### 其它
  - 请在系统管理里维护SQL模板.
  `);n(this,"CondByPara",`
  #### 帮助
  - 所谓的开发者参数，就是开发人员在执行流程过程中(发送、退回),向接口传入的参数作为条件.
  - 比如: 发送方法
 #### DEMO
  //组织参数.
  Hashtable ht = new Hashtable();
  ht.Add("PrjNo", "项目编号"); 
  ht.Add("PrjName", "项目名称"); 
  ht.Add("JinE", 500000.00); //项目金额, 根据项目金额大小自动转向.
  //调用发送接口.
    BP.WF.Dev2Interface.Node_SendWork("001", 1002, ht, null);
  
  `);n(this,"CondByUrl",`
  #### 帮助
  - URL就是通过http协议,运行一个url,返回数据，根据数据内容作为条件的一种方式.
  - 返回值是 err@开头的字符串，说明系统是有异常。
  - 返回值 >0 条件 =true,  否则为false.
  #### 配置参数
  - 配置格式: http://ccflow.org/xxx.jsp
  - 系统解析格式: http://ccflow.org/xxx.do?WorkID=xxxx&FK_Flow=001&FK_Node=101&UserNo=zhangsan&Token=xxx-xx-xxx
  - 系统会自动把当前环境已知的参数加里面去, 开发人员可以通过 WorkID获取流程的实例的其它数据,可以通过UserNo, Token来校验合法性.
  `);n(this,"WebApi",`
  #### 帮助
   - 返回值说明,"false"是不通过，"true"是通过。
   - WebAPI的输入格式：http://demo.ccflow.org/DataUser/GetEmps?id=51184
   - 接口地址支持固定参数，或者ccbpm内置参数，或者流程表单参数，比如:http://demo.ccflow.org/DataUser/GetEmps?id=@FK_Node
   #### 其它
   - 返回值是 err@开头的字符串，说明系统是有异常。
   - 返回值 >0 条件 =true,  否则为false.
  `);n(this,"ByStation",`
  #### 帮助
   - 按角色计算: 就是指定身份的人员拥有的角色是否与条件设置的角色集合是否有交集来判断条件的一种方式. 
   - 有交集=true,无交集=false.
   - 该方式使用比较广泛, 比如,请假人是中层角色，走那个路线，高层角色走那个路线.
   - 配置方式:选择角色集合,点创建按钮.
  ##### 人员身份
   - 就是按那个操作员的身份计算,确定人员身份求他的角色集合.
   - 默认为当前操作员的身份计算.
  `);n(this,"ByLeaderOfDept",`
  #### 帮助
  - 按部门计算: 就是指定身份的人员,是不是设置部门集合的负责人.
 #### 人员身份
  - 就是按那个操作员的身份计算,确定人员身份求他的部门集合.
  - 默认为当前操作员的身份计算.
#### 解释
 -  锁定一个人员,判断该人员是否是部门负责人.
  `);n(this,"ByEmp",`
  #### 帮助
  - 选择的人员可以走这个路径.
  - 就是按那个操作员的身份计算,确定人员身份求他的部门集合.
  - 默认为当前操作员的身份计算.
  `);n(this,"ByDept",`
  #### 帮助
  - 按部门计算: 就是指定身份的人员拥有的部门是否与条件设置的部门集合是否有交集来判断条件的一种方式. 
  - 有交集=true,无交集=false.
  - 该方式使用比较广泛, 比如：什么角色的人走那个路线.
  - 配置方式:选择部门集合,点创建按钮.
 ##### 人员身份
  - 就是按那个操作员的身份计算,确定人员身份求他的部门集合.
  - 默认为当前操作员的身份计算.
  `);this.PageTitle="新建条件/表达式",this.ForEntityClassID="TS.WF.Cond"}Init(){return T(this,null,function*(){this.AddGroup("A","条件表达式"),this.AddBlank("Left","左括号",this.ByCond100),this.AddBlank("Right","右括号",this.ByCond100),this.AddBlank("AND","AND",this.ByCond100),this.AddBlank("OR","OR",this.ByCond100),this.AddGroup("B","内置表单条件");let o="001",u=101;if(this.RefMainEnName.includes("CCRole")==!0){const a=new w;a.MyPK=this.RefPKVal,a.RetrieveFromDBSources(),u=a.NodeID,o=a.FlowNo}else if(this.RefMainEnName.includes("NodeExt")==!0){u=this.RefPKVal;const a=new P(u);yield a.Retrieve(),o=a.FK_Flow}else{const a=new R;a.MyPK=this.RefPKVal,yield a.RetrieveFromDBSources(),u=a.Node,o=a.FK_Flow}const i="ND"+Number(o)+"Rpt",s=`
      SELECT  No, Name  FROM Sys_MapData  WHERE No='ND${u}'
     UNION
    SELECT A.FK_Frm as No, B.Name as Name FROM WF_FrmNode A,Sys_MapData B WHERE A.FK_Node=${u} AND A.FK_Frm=B.No
     UNION
    SELECT '${i}' as No, '${i}流程业务表' as Name FROM Port_Emp where No='admin' 
    `;this.SelectItemsByList("CondByFrm","表单字段条件",this.ByFrm,!1,s),this.SelectItemsByGroupList("CondByFrm.SelectField","选择字段",this.ByFrm,!1,()=>`
        SELECT OID as No, Lab Name FROM Sys_GroupField WHERE FrmID='${this.RequestVal("tb1","CondByFrm")}' AND 1=1 
        UNION
        SELECT 0 AS No, '无分组' as Name FROM Port_Emp WHERE No='admin'
        `,()=>{const a=this.RequestVal("tb1","CondByFrm"),m="'OID','FID','AtPara','GUID','WFState','WFSta'";return`
        SELECT MyPK as No, Name, GroupID FROM Sys_MapAttr WHERE FK_MapData='${a}'
         AND KeyOfEn NOT IN (${m}) AND GroupID  IN (select OID from Sys_GroupField where FrmID='${a}')
         UNION
         SELECT MyPK as No, Name, 0 as GroupID FROM Sys_MapAttr WHERE FK_MapData='${a}'
         AND KeyOfEn NOT IN (${m}) AND GroupID Not IN (select OID from Sys_GroupField where FrmID='${a}')
      `}),this.TextBox1_Name(r.CondByWorkCheck,"审核组件的立场",this.CondByWorkCheck,"输入立场中文名称","同意","在审核组件中的立场配置."),this.AddGroup("C","组织结构条件"),this.SelectItemsByGroupList(r.CondStation,"按角色计算",this.ByCond100,!0,C.srcStationTypes,C.srcStations),this.SelectItemsByTree(r.CondDept,"按部门计算",this.ByDept,!0,C.srcDepts,C.srcDeptRoot),this.SelectItemsByTree(r.CondLeaderOfDept,"判断部门负责人",this.ByDept,!0,C.srcDepts,C.srcDeptRoot),this.SelectItemsByTreeEns(r.CondEmp,"按人员计算",this.ByEmp,!0,C.srcDeptLazily,"0",C.srcEmpLazily,"@No=账号@Name=名称@Tel=电话"),this.AddGroup("D","开发接口条件"),this.TextSQL(r.CondBySQL,"按SQL表达式计算",this.BySQL,"SQL表达式"," SELECT count(*) AS NUM FROM MyTable WHERE MyField='@WebUser.No' ","请设置一个SQL语句,返回是number"),this.SelectItemsByList(r.CondBySQLTemplate,"按SQL模板条件计算",this.sqlTemplate,!1,"SELECT No as No,Name as Name FROM WF_SQLTemplate WHERE SQLType=0 "),this.TextBox1_Name(r.CondByPara,"按开发者参数计算",this.CondByPara,"参数","","请输入表达式,比如:Jine > 1000 "),this.TextBox1_Name(r.CondByUrl,"按Url条件计算",this.CondByUrl,"URL","http://","请输入url地址."),this.TextBox3_NameNoNote(r.CondByWebApi,"按WebApi返回值计算",this.WebApi,"","请输入webapi接口地址 ","判断值","备注(不为空)","")})}Save_TextBox_X(o,u,i,s,f){return T(this,null,function*(){const p=this.RequestVal("RefPKVal");let a="",m=0,B=0;if(this.RefMainEnName.includes("CCRole")==!0){const t=new w(p);yield t.Init(),yield t.Retrieve(),a=t.FlowNo,m=t.NodeID,B=t.NodeID}else if(this.RefMainEnName.includes("NodeExt")==!0){const t=new P(p);yield t.Init(),yield t.Retrieve(),a=t.FK_Flow,m=t.NodeID,B=t.NodeID}else{const t=new R(p);yield t.Init(),yield t.Retrieve();const l=new M(t.Node);yield l.RetrieveFromDBSources(),l.CondModel!=0&&(l.CondModel=0,yield l.Update());const E=new R(p);yield E.Init(),yield E.Retrieve(),a=E.FK_Flow,m=E.Node,B=E.ToNode}if(o=="CondByFrm")return;const e=new A;if(yield e.Init(),e.FK_Flow=a,e.FK_Node=m,e.ToNodeID=B,e.CondType=2,this.RefMainEnName.includes("CCRole")==!0&&(e.CondType=4),this.RefMainEnName.includes("NodeExt")==!0&&(e.CondType=1),e.DataFrom=o,e.DataFromText=this.GetPageName(o),e.Idx=100,e.RefPKVal=p,o==="Left"||o==="Right"||o==="AND"||o==="OR")return e.DataFrom=100,e.DataFromText="运算符",e.Note=o,o==="Left"&&(e.Note="("),o==="Right"&&(e.Note=")"),e.FK_Operator=e.Note,e.OperatorValue=e.Note,e.SetPara("EnName","TS.WF.Cond100"),yield e.Insert(),F.info("保存成功!!!"),new N(c.CloseAndReload);if(o==="CondByFrm.SelectField"){const t=new L(i);t.MyPK=i,yield t.Retrieve();let l="TS.WF.CondFrmString";if(e.Tag1=t.UIBindKey,t.LGType==1){l="TS.WF.CondFrmEnum";const h=new _;x.CCBPMRunModel==K.SAAS?h.No=W.OrgNo+"_"+t.UIBindKey:h.No=t.UIBindKey,(yield h.RetrieveFromDBSources())==1&&h.EnumType==1&&(l="TS.WF.CondFrmEnumString")}t.LGType>=2&&(l="TS.WF.CondFrmString"),t.LGType==0&&t.IsNum&&(l="TS.WF.CondFrmNum"),e.Note="",t.FK_MapData.includes("ND")&&t.FK_MapData.includes("Rpt")?e.DataFrom=r.CondByFrm:e.DataFrom=r.StandAloneFrm,e.FK_Attr=i,e.AttrKey=t.KeyOfEn,e.AttrName=s,e.FK_Operator="=",e.FK_OperatorT="等于",e.OperatorValue="0",e.OperatorValueT="未设置",e.Idx=100,e.SetPara("EnName",l),alert(l),e.FrmID=this.RequestVal("tb1","CondByFrm"),e.FrmID.startsWith("ND")==!1&&(e.DataFrom=r.StandAloneFrm),e.FrmName=this.RequestVal("tb2","CondByFrm"),yield e.Insert();const E=y.UrlEn(l,e.MyPK);return new N(c.GoToUrl,E)}if(o===r.CondByWorkCheck){e.Note="当立场=["+i+"]时.",e.FK_Operator=o,e.OperatorValue=i,e.Idx=100,e.SetPara("EnName","TS.WF.CondWorkCheckSelected"),e.AttrKey="",yield e.Insert(),F.info("保存成功");const t=y.UrlEn(e.GetParaString("EnName"),e.MyPK);return new N(c.GoToUrl,t)}if(o===r.CondBySQL){e.Note=f,e.FK_Operator=o,e.OperatorValue=s,e.Idx=100,e.SetPara("EnName","TS.WF.CondSQL"),e.FK_DBSrc=i,yield e.Insert(),F.info("保存成功");const t=y.UrlEn(e.GetParaString("EnName"),e.MyPK);return new N(c.GoToUrl,t)}if(o===r.CondByPara){if(!i){F.error("参数条件不能为空");return}if(i.split(" ").length<3){F.error("请检查设置的条件是否正确,操作符前后必须有空格");return}e.Note=i,e.FK_Operator=o,e.OperatorValue=i,e.Idx=100,e.SetPara("EnName","TS.WF.CondParas"),yield e.Insert(),F.info("保存成功");const t=y.UrlEn(e.GetParaString("EnName"),e.MyPK);return new N(c.GoToUrl,t)}if(o===r.CondByUrl){e.Note=i,e.FK_Operator=o,e.OperatorValue=i,e.Idx=100,e.SetPara("EnName","TS.WF.CondWebApi"),yield e.Insert(),F.info("保存成功");const t=y.UrlEn(e.GetParaString("EnName"),e.MyPK);return new N(c.GoToUrl,t)}if(o===r.CondByWebApi){e.Note=f,e.OperatorValue=i,e.Idx=100,e.SetPara("EnName","TS.WF.CondWebApi"),yield e.Insert();const t=y.UrlEn(e.GetParaString("EnName"),e.MyPK);return new N(c.GoToUrl,t)}if(o===r.CondStation){e.OperatorValue=i,e.OperatorValueT=s,e.Note=s,e.Idx=100,e.SetPara("EnName","TS.WF.CondStation"),yield e.Insert();const t=y.UrlEn(e.GetParaString("EnName"),e.MyPK);return new N(c.GoToUrl,t)}if(o===r.CondDept||o===r.CondLeaderOfDept){e.OperatorValue=i,e.OperatorValueT=s,e.Note=s,e.Idx=100,e.SetPara("EnName","TS.WF.CondDept"),yield e.Insert(),F.info("保存成功");const t=y.UrlEn(e.GetParaString("EnName"),e.MyPK);return new N(c.GoToUrl,t)}if(o===r.CondEmp){e.OperatorValue=i,e.OperatorValueT=s,e.Note=s,e.Idx=100,e.SetPara("EnName","TS.WF.CondEmp"),yield e.Insert();const t=y.UrlEn(e.GetParaString("EnName"),e.MyPK);return new N(c.GoToUrl,t)}alert("没有判断的类型:"+e.DataFromText)})}GenerSorts(){return Promise.resolve([])}}export{r as DataFrom,Je as GPN_Cond};
