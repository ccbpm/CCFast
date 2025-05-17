var h=Object.defineProperty;var g=(t,i,e)=>i in t?h(t,i,{enumerable:!0,configurable:!0,writable:!0,value:e}):t[i]=e;var a=(t,i,e)=>g(t,typeof i!="symbol"?i+"":i,e);var r=(t,i,e)=>new Promise((o,m)=>{var n=l=>{try{p(e.next(l))}catch(s){m(s)}},c=l=>{try{p(e.throw(l))}catch(s){m(s)}},p=l=>l.done?o(l.value):Promise.resolve(l.value).then(n,c);p((e=e.apply(t,i)).next())});import{bb as u,ba as M,cq as S}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as E}from"./PageBaseGroupEdit-JIgqoTiq.js";import{SysEnumMains as C}from"./SysEnumMain-Bn9Li5_w.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";import"./SysEnum-B89JeOjj.js";class T extends E{constructor(){super("GPE_MultipleChoiceSmall");a(this,"Desc0",`
  #### 帮助
   - 就是用户录入相对固定的文本时，在文本框里提前输入相关的选项，可以直接进行多选操作。
  #### 应用场景
   - 比如，请假类型，出行方式等
  #### 效果图
  - 按文本输入的值
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSmall/Img/MultipleChoiceSmall1.png "屏幕截图.png")
  - 按系统外键表计算
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSmall/Img/MultipleChoiceSmall.png "屏幕截图.png")

  `);a(this,"Desc1",`
  #### 说明
  值用逗号分开,比如: 飞机,火车,轮船,火箭,其他 
  #### 配置图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSmall/Img/MultipleChoiceSmallPeizhi.png "屏幕截图.png")
  #### 效果图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSmall/Img/MultipleChoiceSmall1.png "屏幕截图.png")
  
  `);a(this,"Desc2",`
  #### 说明
  系统用枚举值作为该字段的多选.
  #### 效果图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSmall/Img/MultipleChoiceSmall.png "屏幕截图.png")
  
  `);a(this,"Desc3",`
  #### 说明
  系统用系统外键作为该字段的多选.
  #### 效果图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSmall/Img/MultipleChoiceSmall.png "屏幕截图.png")
  
  `);this.PageTitle="小范围多选"}Init(){return r(this,null,function*(){this.entity=new u,this.KeyOfEn=M.DoWay,yield this.entity.InitDataForMapAttr("MultipleChoiceSmall",this.GetRequestVal("PKVal")),this.AddGroup("A","小范围多选"),this.Blank("0","不设置",this.Desc0),this.SingleTB("1","按文本输入的值","Tag1",this.Desc1,""),this.SingleDDLEntities("2","按照枚举值","Tag1",this.Desc2,new C,!1),this.SingleDDLEntities("3","按照系统外键表计算","Tag1",this.Desc3,new S,!1),this.SingleTBSQL("4","按照SQL计算","Tag4","查询sql返回No,Name两列")})}AfterSave(e,o){if(e==o)throw new Error("Method not implemented.")}BtnClick(e,o,m){if(e==o||e===m)throw new Error("Method not implemented.")}}export{T as GPE_MultipleChoiceSmall};
