var c=Object.defineProperty;var E=(t,i,e)=>i in t?c(t,i,{enumerable:!0,configurable:!0,writable:!0,value:e}):t[i]=e;var p=(t,i,e)=>E(t,typeof i!="symbol"?i+"":i,e);var M=(t,i,e)=>new Promise((r,n)=>{var D=a=>{try{s(e.next(a))}catch(o){n(o)}},g=a=>{try{s(e.throw(a))}catch(o){n(o)}},s=a=>a.done?r(a.value):Promise.resolve(a.value).then(D,g);s((e=e.apply(t,i)).next())});import{bb as B,ba as m}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as R}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class y extends R{constructor(){super("GPE_RMBDaXie");p(this,"Desc0",`
  #### 帮助
   - 定义：当前输入金额类型的数据，要实现对其他只读的文本框进行大写输出（在一个金额类型或者数值类型的字段输入时自动在另外一个文本框上显示该输入值的人民币大写.）。
   - 设置过程: 1.创建一个大写输出的文本框;2.设置只读状态;3.在需要转大小写的字段上点击属性;4.设置人民币大写.
   - 比如: **合同付款金额**需要转人民币大写，就可以在**合同付款金额**的字段属性中 => 基本设置 => 人民币大写 => 选择只读的人民币大写字段，选择输出人民币大写的字段点击保存即可。
   - 应用场景：银行、单位和个人在填写各种票据和结算凭证时必须遵守严格的标准和规范.
   #### 效果图
   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/RMBDaXie/Img/RMBDaXieyanshi.png "屏幕截图.png") 

   ### 配置图
   - 配置图例1
   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/RMBDaXie/Img/RMBDaXie2.png "屏幕截图.png") 
   - 配置图例2
   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/RMBDaXie/Img/RMBDaXie1.png "屏幕截图.png") 
   - 配置图例3
   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/RMBDaXie/Img/RMBDaXie.png "屏幕截图.png") 
   - 运行图例
   ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/RMBDaXie/Img/RMBDaXieyanshi.png "屏幕截图.png") 
 
   `);p(this,"Desc1",`
  #### 帮助
   - 不启用：不对其他文本框实现大写转换.
   - 人民币大写：当前输入金额类型的数据，要实现对其他只读的文本框进行大写输出。
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/RMBDaXie/Img/RMBDaXieyanshi.png "屏幕截图.png") 
  `);this.PageTitle="人民币大写"}Init(){return M(this,null,function*(){this.entity=new B,this.KeyOfEn=m.DoWay,yield this.entity.InitDataForMapAttr("RMBDaXie",this.GetRequestVal("PKVal")),this.AddGroup("A","人民币大写"),this.Blank("0","不启用",this.Desc0),this.SingleDDLSQL("1","选择只读的人民币大写字段",m.Tag,this.Desc0,"SELECT KeyOfEn as No, Name FROM Sys_MapAttr WHERE FK_MapData='@FK_MapData' AND UIVisible=1 AND UIIsEnable = 0 AND MyDataType=1",!1)})}AfterSave(e,r){if(e==r)throw new Error("Method not implemented.")}BtnClick(e,r,n){if(e==r||e===n)throw new Error("Method not implemented.")}}export{y as GPE_RMBDaXie};
