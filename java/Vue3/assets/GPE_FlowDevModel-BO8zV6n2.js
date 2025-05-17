var a=Object.defineProperty;var l=(i,r,e)=>r in i?a(i,r,{enumerable:!0,configurable:!0,writable:!0,value:e}):i[r]=e;var t=(i,r,e)=>l(i,typeof r!="symbol"?r+"":r,e);import{aM as s,D as n}from"./entry/index-C6uBgOW5-1730430676707.js";import{PageBaseGroupEdit as F}from"./PageBaseGroupEdit-IicyYiex.js";import{Flow as p,FlowAttr as o}from"./Flow-D4nUES4A.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./Help-D0bDMZWg.js";class g extends F{constructor(){super("GPE_FlowDevModel");t(this,"Profession",`
  #### 帮助
   - 专业模式也是2020年以前版本模式，如果2020年以前设置的流程都是专业模式。
   - 专业模式就是任何节点的表单方案都可单独定制，它适应与更复杂的环境配置，但是也给配置人员提高了复杂难度。
   - 对于老客户，如果您已经习惯了，熟悉了2020年以前的版本设置，建议使用专业模式。
   - 我们改造这些模式的目的，就是为了简化操作，降低使用者的门槛。
   - 
  
  `);t(this,"JiJian",`
  #### 帮助 
    
   - 极简模式是采用经典表单+审核组件实现流程审核的一种模式。
   - 开始节点填写申请，以后的节点都是审批，我们把这样的模式称为极简模式。
   - 在极简模式下，每个节点右键上有一个审核组件状态（启用，禁用，只读）。  #### 配置图
  ![输入图片说明](/resource/WF/Admin/AttrFlow/Limit/Img/LimitSetting.png "屏幕截图.png")
    `);t(this,"RefFrm",`
   #### 帮助
   - 表单库的表单称为独立表单，每个表单都可以与任何流程的任何节点绑定。
   - 一个表单类似于一个车厢，停留在仓库里，只有他在被绑定到节点上才可以使用。
   - 每个节点就类似于火车头，一个火车头可以挂多个车厢，也可以挂一个车厢。
   - 一个节点挂一个表单，我们称为单表单流程，多个表单称为表单树流程。
   - 表单的权限控制: 一个节点挂接一个表单有权限控制，控制整体表单只读、可见、可编辑，可控制每个表单元素的状态特征。

    `);t(this,"FrmTree",`
   #### 帮助 
   - 表单库的表单称为独立表单，每个表单都可以与任何流程的任何节点绑定。
   - 一个表单类似于一个车厢，停留在仓库里，只有他在被绑定到节点上才可以使用。
   - 每个节点就类似于火车头，一个火车头可以挂多个车厢，也可以挂一个车厢。
   - 一个节点挂一个表单，我们称为单表单流程，多个表单称为表单树流程。
   - 表单的权限控制: 一个节点挂接一个表单有权限控制，控制整体表单只读、可见、可编辑，可控制每个表单元素的状态特征。
   
   `);t(this,"SDKFrm",`
   #### 帮助  
   - SDK表单就是ccbpm把界面的展现完全交给了开发人员处理,开发人员只要设计一个表单,增加一个发送按钮,调用ccbpm的发送API就可以完成。
   - 表单的渲染，都是由开发人员完成，对流程的操作调用不同的接口即可。
   - 这种模式适应于比较复杂的表单但是ccform又满足不了用户的要求的情况下使用sdk表单。
   - 关于该表单的调用接口可以参考：流程属性\\开发接口。
   
    `);t(this,"QianRuFrm",`
   #### 帮助  
     
   - 您可以定义一个页面，绑定到该节点上。
   - 该页面里面有一个Save() 的 function ，当用户点击框架外面的工具栏上的【保存】按钮或者【发送】按钮，就会触发这个函数。
   - 您需要在Save()的function里完成数据完整性效验与数据保存。
   - 如果保存成功就return true, 保存失败就return false. 比如:当用户执行发送的时候，首先执行保存，保存成功后在执行发送，保存失败后，就阻止发送。
   - 您输入的Url可以有参数，但是系统会把所有的参数附件到该url后面。
   - 例如:/SDKFlowDemo/QingJia/SDKQianRuFangShiForm.htm 。
   - 比如:您配置的url为 http://xxxx:222:/abc.htm 系统实际的Url为 http://xxxx:222:/abc.htm?FK_Flow=xxx&FK_Node=xxx&WorkID=xxx&UserNo=xxx&Token=xxx
   - 系统会把当前流程环境中的变量与参数都传递到您的自定义页面上来，您可以根据这些参数来展示，保存数据，控制数据只读，可编辑。
   - 如果使用绝对路径可以使用ccbpm的全局变量@SDKFromServHost ，比如: @SDKFromServHost/MyFile.htm
    
     `);this.PageTitle="设计模式"}Init(){this.entity=new p,this.KeyOfEn=o.DevModelType,this.AddGroup("A","内置表单模式"),this.Blank("Profession","专业模式",this.Profession),this.Blank("JiJian","极简模式",this.JiJian),this.AddGroup("B","绑定表单库模式"),this.SelectItemsByGroupList("RefFrm","绑表单库的表单",this.RefFrm,!1,s.srcFrmTree,s.srcFrmList,o.DevModelPara),this.SelectItemsByGroupList("FrmTree","表单树",this.FrmTree,!0,s.srcFrmTree,s.srcFrmList,o.DevModelPara),this.AddGroup("C","自定义表单模式"),this.SingleTB("SDKFrm","SDK表单",o.DevModelPara,this.SDKFrm,"请输入表单URL",n.AppString),this.SingleTB("QianRuFrm","嵌入式表单",o.DevModelPara,this.QianRuFrm,"请输入表单URL",n.AppString)}AfterSave(e,m){if(e==m)throw new Error("Method not implemented.")}BtnClick(e,m,h){}}export{g as GPE_FlowDevModel};
