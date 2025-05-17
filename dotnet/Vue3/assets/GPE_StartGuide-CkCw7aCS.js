var n=Object.defineProperty;var a=(r,i,e)=>i in r?n(r,i,{enumerable:!0,configurable:!0,writable:!0,value:e}):r[i]=e;var t=(r,i,e)=>a(r,typeof i!="symbol"?i+"":i,e);import{StarGuideBySQLMulti as o}from"./StarGuideBySQLMulti-pZdUskVx.js";import{StartGuideBySQLOne as h}from"./StartGuideBySQLOne-DXerswYO.js";import{PageBaseGroupEdit as S}from"./PageBaseGroupEdit-JIgqoTiq.js";import{Flow as l,FlowAttr as u}from"./Flow-D1QXg1UO.js";import"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class E extends S{constructor(){super("GPE_StartGuide");t(this,"Desc10",`
  #### 帮助
   
   - 发起之前需要提示的Html文本信息.
   - 比如:许可协议、注意事项、办理流程、办理程序说明等等.
   - 用户点击后,就进入发起页面.
   
    `);t(this,"Desc0",`
  #### 帮助
   
   - 发起前置导航，就是流程在启动之前需要有一个前置页面。
   - 在这个页面里，可以根据不同的应用场景设置不同的界面。
   - 不使用 ，不启动前置页面。
   
    `);t(this,"Desc1",`
  #### 帮助
   
   - 发起前置导航，就是流程在启动之前需要有一个前置页面。
   - 在这个页面里，可以根据不同的应用场景设置不同的界面。

   #### 应用场景
   - 启动一个订单要选择一个客户.
   - 对客户的选择是一个单选,只能选择一个记录.
   - 这里要配置查询条件,列表数据源,选择一笔数据根据主键查询记录.
   #### 配置图
   ![输入图片说明](/resource/WF/Admin/AttrFlow/StartGuide/Img/StartGuidSetting.png "屏幕截图.png")
   #### 效果图
   - 运行图 
   点击一条记录发起导航
   ![输入图片说明](/resource/WF/Admin/AttrFlow/StartGuide/Img/StartGuide.png "屏幕截图.png")
   #### 效果图
   - 运行流程，把导航数据导入到表单中，点击发送可以发起流程
   ![输入图片说明](/resource/WF/Admin/AttrFlow/StartGuide/Img/StartGuide2.png "屏幕截图.png")
    `);t(this,"Desc2",`
  #### 帮助

   - 应用前置导航可以对数据列表里批量发起子流程。
  #### 应用场景
   - 三好学生评定、分包商审核等。下面以三好学生评定以例进行说明。
    1.打开学生的实体类列表。
    2.在实体类列表里，选择多名学生（选择多行）。
    3.发起三好学生评定流程。
  
    `);t(this,"Desc3",`
  #### 帮助
   
   - 用户希望出现一个历史发起的流程列表，选择一条流程并把该流程的数据copy到新建的流程上。
   - 您需要在这里配置一个SQL, 并且该SQL必须有一个OID列。
   - 比如：SELECT Title ,OID FROM WF_GenerWorkFlow WHERE Title LIKE '%@Key%' AND FK_Flow='001' AND WFState=3
  

    `);t(this,"Desc4",`
  #### 帮助
   - 发起该流程之前，需要选择一个已经发起的流程实例作为父流程。
   - 启动该流程的时候，需要启动 /WF/WorkOpt/StartGuideParentFlowModel.htm 的页面，让用户选择一个父流程。
  #### 应用场景
   - 在执行财务报销的时候，要选择一个出差申请父流程，并把申请信息带入到报销单里面。
   - 在执行采购入库的时候，要选择一个采购申请单作为父流程。
  
  
    `);t(this,"Desc5",`
  #### 帮助
   - 在流程运行过程中，选择多个已完成的流程做为该流程的子流程，批量的发起。
  #### 应用场景
   - 多标段的路桥建设中，每一段的建设进度都为一个流程。
   - 在报总部审批时，可以把每一段流程做为一个子流程，多个子流程一起审批。
   - 注意，各子流程必须为同一类别。具有相同的工作。
   
    
      `);t(this,"Desc6",`
  #### 帮助
     
   - 此模式仅仅限于开始节点是表单树的模式。
   - 当开始节点绑定多个表单的时候，流程发起需要选择一个活的多个表单启动流程。

    
      `);t(this,"Desc7",`
  #### 帮助
      
   - 请设置URL在文本框里。
   - 该URL是一个列表，在每一行的数据里有一个连接链接到工作处理器上（/WF/MyFlow.htm）
   - 连接到工作处理器（ WF/MyFlow.htm）必须有2个参数FK_Flow=xxx&IsCheckGuide=1
   - 您可以打开Demo: /SDKFlowDemo/TestCase/StartGuideSelfUrl.htm 详细的说明了该功能如何开发。
    
    `);t(this,"Desc8",`
  #### 帮助
   - 二维码：适用于移动端，扫二维码获得一些信息，进行一些操作。
   - 条码：适用于移动端，扫条码获得一些信息，进行一些操作。
  #### 场景
   - 利用扫描枪或手机扫描功能，把带有识别码的物品信息录入到系统中。
   - 比如：维修工具归还流程中，可以利用外置扫描枪，扫描工具上的识别码（二维码或条码）。
   - 把工具的编号，名称等信息自动录入到表格中，利用发起前置导航功能，选中这些工具，发起归还流程。
    
  
    `);this.PageTitle="发起前置导航"}Init(){this.entity=new l,this.KeyOfEn=u.StartGuideWay,this.AddGroup("A","数据源配置模式"),this.Blank("0","不使用",this.Desc0),this.AddEntity("1","按设置的SQL-单条模式",new h,this.Desc1),this.AddEntity("2","按设置的SQL-多条模式(用于批量发起)",new o,this.Desc2),this.AddGroup("B","流程数据获取模式"),this.SingleTBSQL("3","从历史发起的流程Copy数据(查询历史记录)","StartGuidePara1",this.Desc3),this.SingleTBSQL("4","父子流程模","StartGuidePara1",this.Desc4),this.AddEntity("5","子流程实例列表模式-多条)",new o,this.Desc5),this.AddGroup("C","扫码录入"),this.Blank("8","二维码",this.Desc8),this.Blank("9","条码",this.Desc8),this.AddGroup("D","其它模式"),this.SingleEnumDDL("6","开始节点绑定的独立表单列表","StartGuidePara1",this.Desc6,"@0=单选模式@1=多选模式"),this.SingleTB("7","按自定义的Url ","StartGuidePara1",this.Desc7,"请输入url."),this.SingleRichTxt("10","发起说明HTML文本","StartGuidePara1","请输入内容",this.Desc10)}AfterSave(e,s){if(e==s)throw new Error("Method not implemented.")}BtnClick(e,s,d){if(e==s||e===d)throw new Error("Method not implemented.")}}export{E as GPE_StartGuide};
