var r=Object.defineProperty;var n=(s,t,e)=>t in s?r(s,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):s[t]=e;var i=(s,t,e)=>n(s,typeof t!="symbol"?t+"":t,e);import{PageBaseGroupEdit as l}from"./PageBaseGroupEdit-IicyYiex.js";import{Node as h,NodeAttr as p}from"./Node-DKGUVcK1.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./Help-D0bDMZWg.js";import"./EntityNodeID-BCBbJNH2.js";class x extends l{constructor(){super("GPE_BlockModel");i(this,"Desc0",`
  #### 帮助
   - 默认模式，不阻塞。
   - 在发送的的时候，符合什么样的条件，才不让它运行下去。
`);i(this,"Desc1",`
  #### 帮助
   - 配置一个SQL，该SQL返回一行一列的数值类型的值。
   - 如果该值大于0，则阻塞发送, 反之不阻塞.
   - 配置的参数支持ccbpm表达式。
`);i(this,"Desc2",`
 #### 帮助
  - 配置一个表达式, 比如: @MyFieldName = 1 该表达式成立的时候，系统就不能向下发送。 
  - 表达式：@+字段名+空格+运算符+空格+值；字段名就是节点表单的所有字段，空格为英文状态下空格，运算符包含=、!=、>、>=、<、<=、LIKE 
  - 注意: 仅仅支持一个表达式 . 比如: @JinE > 10000 
 
 `);i(this,"Desc3",`
 #### 帮助
   - 当前节点吊起了子流程，并且有未完成的子流程时就不能向下运动。
   - 所有的在此节点上启动的子流程，都完成后，该节点才能发送。
 
 `);i(this,"Desc4",`
 #### 帮助
  - 当该节点向下运动时，要检查指定的历史节点曾经启动的指定的子流程全部完成，作为条件。
  - 例如：在D节点上，要检查曾经在C节点上启动的甲子流程是否全部完成，如果完成就不阻塞。
  - 配置格式：@指定的节点1=子流程编号1@指定的节点n=子流程编号n。

 `);i(this,"Desc5",`
 #### 帮助
  - 当该节点向下运动时，要检查指定的子流程是否运行过指定的节点，作为条件。
  - 例如：在D节点上，要检查启动的子流程是否全部运行到指定的节点，如果完成就不阻塞。
  - 配置格式：@指定的节点1=子流程节点编号1@指定的节点n=子流程节点编号n。。
 
 `);i(this,"Desc6",`
 #### 帮助
  - 当该节点向下运动时，要检查指定平级子流程是否运行过指定的节点，作为条件。
  - 例如：在D节点上，要检查启动的平级子流程是否全部运行到指定的节点，如果完成就不阻塞。
  - 配置格式：402，503。

 `);this.PageTitle="发送阻塞"}Init(){this.entity=new h,this.KeyOfEn=p.BlockModel,this.AddGroup("A","+通用阻塞"),this.Blank("0","不阻塞",this.Desc0),this.SingleTBSQL("3","按照SQL/URL阻塞","BlockExp",this.Desc1,"请按照帮助设置SQL"),this.SingleTB("4","按照表达式阻塞","BlockExp",this.Desc2,"请输入表达式..."),this.AddGroup("B","+父子流程规则"),this.Blank("1","当前节点有未完成的子流程时.",this.Desc3),this.SingleTB("2","按约定格式阻塞未完成子流程","BlockExp",this.Desc4,"输入流程编号，多个用逗号分来，比如:001,002"),this.SingleTB("5","是否启用为父流程时，子流程未运行到指定的节点.","BlockExp",this.Desc5,"输入流程编号，多个用逗号分来，比如:001,002"),this.SingleTB("6","是否启用为平级子流程时，子流程未运行到指定的节点","BlockExp",this.Desc6,"输入流程编号，多个用逗号分来，比如:001,002")}AfterSave(e,o){if(e==o)throw new Error("Method not implemented.")}BtnClick(e,o,c){if(e==o||e===c)throw new Error("Method not implemented.")}}export{x as GPE_BlockModel};
