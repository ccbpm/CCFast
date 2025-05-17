var m=Object.defineProperty;var a=(e,t,i)=>t in e?m(e,t,{enumerable:!0,configurable:!0,writable:!0,value:i}):e[t]=i;var r=(e,t,i)=>a(e,typeof t!="symbol"?t+"":t,i);import{PageBaseGroupEdit as o}from"./PageBaseGroupEdit-JIgqoTiq.js";import{Flow as n}from"./Flow-D1QXg1UO.js";import"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class E extends o{constructor(){super("GPE_Limit");r(this,"Desc0",`
  #### 帮助
  - 定义: 发起限制规则就是如何限制发起的流程的规则 
  - 不使用规则,不设置发起限制条件,默认为不限制.

  #### 应用场景.
  - 指定的字段不能重复场景: 对纳税人进行执行注销流程, 如果一个纳税人编号已经启动了注销,就不能在启动注销了.
  - 按时间规则计算场景: 周例会流程, 每个周的周1发起例会流程,不能重复发起两次.
  - 其它的请参考说明.
  `);r(this,"Desc1",`
  #### 帮助 
   - 用来限制该流程可以在什么时间段内发起。
   - 例如:按照每人每天一次设置时间范围，规则参数：@08:30-09:00@18:00-18:30，解释：该流程只能在08:30-09:00与18:00-18:30两个时间段发起且只能发起一次。
  #### 配置图
  ![输入图片说明](/resource/WF/Admin/AttrFlow/Limit/Img/LimitSetting.png "屏幕截图.png")
  
    `);r(this,"Desc7",`
   #### 帮助
   - 例如：SELECT COUNT(*) AS Num FROM TABLE1 WHERE NAME='@MyFieldName'. 
   - 解释：编写一个sql语句返回一行一列，如果信息是0，就是可以启动，非0就不可以启动。
   - 该参数支持ccbpm的表达式。比如:@WebUser.No,@WebUser.Name,@WebUser.DeptNo,@WebUser.OrgNo
    `);r(this,"Desc6",`
   #### 帮助 
   
    
   - 设置一个列允许重复，比如：NSRBH
   - 设置多个列的时候，需要用逗号分开，比如：field1,field2
   - 流程在发起的时候如果发现，该列是重复的，就抛出异常，阻止流程发起。
   - 比如：纳税人注销流程，一个纳税人只能发起一次注销，就要配置纳税人字段，让其不能重复。
   
   `);r(this,"Desc9",`
   #### 帮助  
    
   - 多个子流程用逗号分开。
   - 比如:001,003
   
    `);this.PageTitle="发起限制规则"}Init(){this.entity=new n,this.KeyOfEn="StartLimitRole",this.AddGroup("A","限制规则"),this.Blank("0","不限制",this.Desc0),this.SingleTB("1","按时间规则计算","StartLimitPara",this.Desc1,"请输入参数.."),this.SingleTB("6","按照发起字段不能重复规则","StartLimitPara",this.Desc6,"请输入参数.."),this.SingleTBSQL("7","按SQL","StartLimitPara",this.Desc7),this.SingleTB("9","为子流程时仅仅只能被调用1次","StartLimitPara",this.Desc9,"请输入参数..")}AfterSave(i,s){if(i==s)throw new Error("Method not implemented.")}BtnClick(i,s,c){}}export{E as GPE_Limit};
