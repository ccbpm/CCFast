var O=Object.defineProperty;var D=(S,l,o)=>l in S?O(S,l,{enumerable:!0,configurable:!0,writable:!0,value:o}):S[l]=o;var n=(S,l,o)=>D(S,typeof l!="symbol"?l+"":l,o);var T=(S,l,o)=>new Promise((y,i)=>{var s=a=>{try{p(o.next(a))}catch(m){i(m)}},f=a=>{try{p(o.throw(a))}catch(m){i(m)}},p=a=>a.done?y(a.value):Promise.resolve(a.value).then(s,f);p((o=o.apply(S,l)).next())});import{b9 as I,aM as u,aB as N,aC as c,aL as x,bk as K,J as W,W as A}from"./entry/index-M8VErHPE-1727507756861.js";import{Cond as L}from"./Cond-U5EvNC-p.js";import{Direction as R}from"./Direction-B8hdj6IA.js";import{GloComm as C}from"./GloComm-DZ1gELjv.js";import{Node as M}from"./Node-B6HRFhwD.js";import{CCRole as w}from"./CCRole-BYc1Lkgq.js";import{a as P}from"./AccepterRoleByEmpsFrmDtl-BemdGE2P.js";import{SysEnumMain as _}from"./SysEnumMain-Bn9Li5_w.js";import{e as E}from"./antd-DkiF_jXA.js";import"./vue-DGeTOT5N.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./EntityNodeID-De9k9loD.js";import"./FrmTrack-0uAZQ3B_.js";import"./BtnLab-DDtmliJP.js";import"./EnumLab-CLN2gIih.js";import"./FrmNode-ICauEUJT.js";import"./MapData-lfC2UY9r.js";import"./EnumLab-CzismWql.js";import"./PageBaseGroupEdit-JIgqoTiq.js";import"./Help-D0bDMZWg.js";import"./HttpHandler-Ebi1068_.js";import"./DeliveryWay-CVCcKNk8.js";import"./AccepterRoleBindStation-DdRTXWKY.js";import"./GPE_ShenFenModel-ByWjF4SH.js";import"./NodeStation-Dp7o-G35.js";import"./AccepterRoleBindEmp-CRh2yorG.js";import"./NodeEmp-CdqFQpX3.js";import"./AccepterRoleBindDeptStation-CyQkDE1t.js";import"./NodeDept-DTUyT_ab.js";import"./AccepterRoleBindDept-U-Revlbh.js";import"./AccepterRoleBindSFTable-FI4LbJ26.js";import"./ARBindWebApi-ubOfOuM4.js";import"./ARBindStationSpecDept-Bk9VZi_1.js";import"./ARBindStationSpecSta-Q-Yj_XTA.js";import"./ARStation-B0hXohoy.js";import"./AR501-Brrbs0MN.js";import"./AccepterRoleMLeader-qHjJR2qD.js";import"./ARWebAPI-D8evxd8e.js";import"./RouteAttr2Emp-D2pGOYjX.js";import"./GPE_TurnTo-CYU8FR2B.js";import"./GPE_TodolistModel-DEAZhHvC.js";import"./GPE_TeamleaderConfirm-BfBzWTvZ.js";import"./GPE_XieZuoOverRole-BWfWAaqD.js";import"./NodeToolbar-CfeKGY2_.js";import"./EntityOID-BVVq-i_P.js";import"./SubFlow-DqNaKCzf.js";import"./GPE_OvertimeRole-mTyCgJu8.js";import"./FrmNodeExt-D_BvSa8V.js";import"./GPE_FrmNodeEnableRole-CZYy8pVi.js";import"./GPE_FrmCtrlSln-7B6VHwKd.js";import"./FrmNodeBatch-BEv_VoAb.js";import"./GPE_FrmTransfer-BHqi9DsM.js";import"./FrmTransferCustom-CLYjrv9F.js";import"./SysEvent-BDHAFjMW.js";import"./PushMsg-Bf2jY7mr.js";import"./GPE_BlockModel-Cw3lBpWx.js";import"./GPE_CCWriteRole-BpSRGZtU.js";import"./SysEnum-B89JeOjj.js";class r{}n(r,"CondByFrm","0"),n(r,"StandAloneFrm","1"),n(r,"CondStation","2"),n(r,"CondDept","3"),n(r,"CondBySQL","4"),n(r,"CondBySQLTemplate","5"),n(r,"CondByPara","6"),n(r,"CondByUrl","7"),n(r,"CondByWebApi","8"),n(r,"CondByWorkCheck","9"),n(r,"CondLeaderOfDept","10"),n(r,"CondEmp","11"),n(r,"Operator","100");class $e extends I{constructor(){super("GPN_Cond");n(this,"CondByWorkCheck",`
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
  `);this.PageTitle="新建条件/表达式",this.ForEntityClassID="TS.WF.Cond"}Init(){return T(this,null,function*(){this.AddGroup("A","条件表达式"),this.AddBlank("Left","左括号",this.ByCond100),this.AddBlank("Right","右括号",this.ByCond100),this.AddBlank("AND","AND",this.ByCond100),this.AddBlank("OR","OR",this.ByCond100),this.AddGroup("B","内置表单条件");let o="001",y=101;if(this.RefMainEnName.includes("CCRole")==!0){const a=new w;a.MyPK=this.RefPKVal,a.RetrieveFromDBSources(),y=a.NodeID,o=a.FlowNo}else if(this.RefMainEnName.includes("NodeExt")==!0){y=this.RefPKVal;const a=new P(y);yield a.Retrieve(),o=a.FK_Flow}else{const a=new R;a.MyPK=this.RefPKVal,yield a.RetrieveFromDBSources(),y=a.Node,o=a.FK_Flow}const i="ND"+Number(o)+"Rpt",s=`
      SELECT  No, Name  FROM Sys_MapData  WHERE No='ND${y}'
     UNION
    SELECT A.FK_Frm as No, B.Name as Name FROM WF_FrmNode A,Sys_MapData B WHERE A.FK_Node=${y} AND A.FK_Frm=B.No
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
      `}),this.TextBox1_Name(r.CondByWorkCheck,"审核组件的立场",this.CondByWorkCheck,"输入立场中文名称","同意","在审核组件中的立场配置."),this.AddGroup("C","组织结构条件"),this.SelectItemsByGroupList(r.CondStation,"按角色计算",this.ByCond100,!0,u.srcStationTypes,u.srcStations),this.SelectItemsByTree(r.CondDept,"按部门计算",this.ByDept,!0,u.srcDepts,u.srcDeptRoot),this.SelectItemsByTree(r.CondLeaderOfDept,"判断部门负责人",this.ByDept,!0,u.srcDepts,u.srcDeptRoot),this.SelectItemsByTreeEns(r.CondEmp,"按人员计算",this.ByEmp,!0,u.srcDeptLazily,"0",u.srcEmpLazily,"@No=账号@Name=名称@Tel=电话"),this.AddGroup("D","开发接口条件"),this.TextSQL(r.CondBySQL,"按SQL表达式计算",this.BySQL,"SQL表达式"," SELECT count(*) AS NUM FROM MyTable WHERE MyField='@WebUser.No' ","请设置一个SQL语句,返回是number"),this.SelectItemsByList(r.CondBySQLTemplate,"按SQL模板条件计算",this.sqlTemplate,!1,"SELECT No as No,Name as Name FROM WF_SQLTemplate WHERE SQLType=0 "),this.TextBox1_Name(r.CondByPara,"按开发者参数计算",this.CondByPara,"参数","","请输入表达式,比如:Jine > 1000 "),this.TextBox1_Name(r.CondByUrl,"按Url条件计算",this.CondByUrl,"URL","http://","请输入url地址."),this.TextBox3_NameNoNote(r.CondByWebApi,"按WebApi返回值计算",this.WebApi,"","请输入webapi接口地址 ","判断值","备注(不为空)","")})}Save_TextBox_X(o,y,i,s,f){return T(this,null,function*(){const p=this.RequestVal("RefPKVal");let a="",m=0,B=0;if(this.RefMainEnName.includes("CCRole")==!0){const t=new w(p);yield t.Init(),yield t.Retrieve(),a=t.FlowNo,m=t.NodeID,B=t.NodeID}else if(this.RefMainEnName.includes("NodeExt")==!0){const t=new P(p);yield t.Init(),yield t.Retrieve(),a=t.FK_Flow,m=t.NodeID,B=t.NodeID}else{const t=new R(p);yield t.Init(),yield t.Retrieve();const d=new M(t.Node);yield d.RetrieveFromDBSources(),d.CondModel!=0&&(d.CondModel=0,yield d.Update());const F=new R(p);yield F.Init(),yield F.Retrieve(),a=F.FK_Flow,m=F.Node,B=F.ToNode}if(o=="CondByFrm")return;const e=new L;if(yield e.Init(),e.FK_Flow=a,e.FK_Node=m,e.ToNodeID=B,e.CondType=2,this.RefMainEnName.includes("CCRole")==!0&&(e.CondType=4),this.RefMainEnName.includes("NodeExt")==!0&&(e.CondType=1),e.DataFrom=o,e.DataFromText=this.GetPageName(o),e.Idx=100,e.RefPKVal=p,o==="Left"||o==="Right"||o==="AND"||o==="OR")return e.DataFrom=100,e.DataFromText="运算符",e.Note=o,o==="Left"&&(e.Note="("),o==="Right"&&(e.Note=")"),e.FK_Operator=e.Note,e.OperatorValue=e.Note,e.SetPara("EnName","TS.WF.Cond100"),yield e.Insert(),E.info("保存成功!!!"),new N(c.CloseAndReload);if(o==="CondByFrm.SelectField"){const t=new x(i);t.MyPK=i,yield t.Retrieve();let d="TS.WF.CondFrmString";if(e.Tag1=t.UIBindKey,t.LGType==1){d="TS.WF.CondFrmEnum";const h=new _;K.CCBPMRunModel==W.SAAS?h.No=A.OrgNo+"_"+t.UIBindKey:h.No=t.UIBindKey,(yield h.RetrieveFromDBSources())==1&&h.EnumType==1&&(d="TS.WF.CondFrmEnumString")}t.LGType>=2&&(d="TS.WF.CondFrmString"),t.LGType==0&&t.IsNum&&(d="TS.WF.CondFrmNum"),e.Note="",t.FK_MapData.includes("ND")&&t.FK_MapData.includes("Rpt")?e.DataFrom=r.CondByFrm:e.DataFrom=r.StandAloneFrm,e.FK_Attr=i,e.AttrKey=t.KeyOfEn,e.AttrName=s,e.FK_Operator="=",e.FK_OperatorT="等于",e.OperatorValue="0",e.OperatorValueT="未设置",e.Idx=100,e.SetPara("EnName",d),alert(d),e.FrmID=this.RequestVal("tb1","CondByFrm"),e.FrmID.startsWith("ND")==!1&&(e.DataFrom=r.StandAloneFrm),e.FrmName=this.RequestVal("tb2","CondByFrm"),yield e.Insert();const F=C.UrlEn(d,e.MyPK);return new N(c.GoToUrl,F)}if(o===r.CondByWorkCheck){e.Note="当立场=["+i+"]时.",e.FK_Operator=o,e.OperatorValue=i,e.Idx=100,e.SetPara("EnName","TS.WF.CondWorkCheckSelected"),e.AttrKey="",yield e.Insert(),E.info("保存成功");const t=C.UrlEn(e.GetParaString("EnName"),e.MyPK);return new N(c.GoToUrl,t)}if(o===r.CondBySQL){e.Note=f,e.FK_Operator=o,e.OperatorValue=s,e.Idx=100,e.SetPara("EnName","TS.WF.CondSQL"),e.FK_DBSrc=i,yield e.Insert(),E.info("保存成功");const t=C.UrlEn(e.GetParaString("EnName"),e.MyPK);return new N(c.GoToUrl,t)}if(o===r.CondByPara){e.Note=i,e.FK_Operator=o,e.OperatorValue=i,e.Idx=100,e.SetPara("EnName","TS.WF.CondParas"),yield e.Insert(),E.info("保存成功");const t=C.UrlEn(e.GetParaString("EnName"),e.MyPK);return new N(c.GoToUrl,t)}if(o===r.CondByUrl){e.Note=i,e.FK_Operator=o,e.OperatorValue=i,e.Idx=100,e.SetPara("EnName","TS.WF.CondWebApi"),yield e.Insert(),E.info("保存成功");const t=C.UrlEn(e.GetParaString("EnName"),e.MyPK);return new N(c.GoToUrl,t)}if(o===r.CondByWebApi){e.Note=f,e.OperatorValue=i,e.Idx=100,e.SetPara("EnName","TS.WF.CondWebApi"),yield e.Insert();const t=C.UrlEn(e.GetParaString("EnName"),e.MyPK);return new N(c.GoToUrl,t)}if(o===r.CondStation){e.OperatorValue=i,e.OperatorValueT=s,e.Note=s,e.Idx=100,e.SetPara("EnName","TS.WF.CondStation"),yield e.Insert();const t=C.UrlEn(e.GetParaString("EnName"),e.MyPK);return new N(c.GoToUrl,t)}if(o===r.CondDept||o===r.CondLeaderOfDept){e.OperatorValue=i,e.OperatorValueT=s,e.Note=s,e.Idx=100,e.SetPara("EnName","TS.WF.CondDept"),yield e.Insert(),E.info("保存成功");const t=C.UrlEn(e.GetParaString("EnName"),e.MyPK);return new N(c.GoToUrl,t)}if(o===r.CondEmp){e.OperatorValue=i,e.OperatorValueT=s,e.Note=s,e.Idx=100,e.SetPara("EnName","TS.WF.CondEmp"),yield e.Insert();const t=C.UrlEn(e.GetParaString("EnName"),e.MyPK);return new N(c.GoToUrl,t)}alert("没有判断的类型:"+e.DataFromText)})}GenerSorts(){return Promise.resolve([])}}export{r as DataFrom,$e as GPN_Cond};
