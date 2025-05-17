var R=Object.defineProperty;var B=(n,a,t)=>a in n?R(n,a,{enumerable:!0,configurable:!0,writable:!0,value:t}):n[a]=t;var g=(n,a,t)=>B(n,typeof a!="symbol"?a+"":a,t);var p=(n,a,t)=>new Promise((e,o)=>{var i=s=>{try{r(t.next(s))}catch(c){o(c)}},d=s=>{try{r(t.throw(s))}catch(c){o(c)}},r=s=>s.done?e(s.value):Promise.resolve(s.value).then(i,d);r((t=t.apply(n,a)).next())});import{bb as A,ba as m,aL as E,bc as F,aY as l,i as M}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as u}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class L extends u{constructor(){super("GPE_RadioBtns");g(this,"Desc0",`
  #### 帮助
   - 不启用：不对其它控件进行控制。
   - 选项联动控件，就是选择一个Item,对其他控件实现隐藏，显示，设置特定的值的功能。
   - 比如: 请假表单的请假类型，选择病假，让其上传病例，填写所在医院，请事假就不需要显示附件上传与所在医院控件。
  #### 选择病假-效果图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/RadioBtns/Img/RadioBtns.png "屏幕截图.png") 
  #### 选择事假-效果图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/RadioBtns/Img/RadioBtns1.png "屏幕截图.png") 
  `);g(this,"Desc1",`

  #### 帮助
  - 就是选择一个Item,对其他控件实现隐藏，显示，设置特定的值的功能。
  - 点击设置进入详细设置.
  - 配置图例1
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/RadioBtns/Img/RadioBtnsSetting.png "屏幕截图.png") 
  - 配置图例2
   ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/RadioBtns/Img/RadioBtnsSetting1.png "屏幕截图.png") 
  - 运行图例
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/RadioBtns/Img/RadioBtns.png "屏幕截图.png") 
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/RadioBtns/Img/RadioBtns1.png "屏幕截图.png") 

  `);this.PageTitle="选项联动控件"}Init(){return p(this,null,function*(){const{loadComponent:t}=M();this.entity=new A,this.KeyOfEn=m.DoWay,yield this.entity.InitDataForMapAttr("RBAction",this.GetRequestVal("PKVal")),this.AddGroup("A","选项联动控件"),this.Blank("0","不启用",this.Desc0),this.SelfComponent("1","启用设置",t("/@/WF/Admin/FrmLogic/Views/RadioBtns.vue"),{EnClassID:this.EnClassID,PKVal:this.PKVal,enable:!1})})}BtnClick(t,e,o){return p(this,null,function*(){if(t=="0"||e=="0"||o=="0")return;const i=new E(this.PKVal);yield i.Retrieve(),new F().Retrieve("FK_MapData",i.FK_MapData),new l().Retrieve(m.FK_MapData,i.FK_MapData,m.ExtModel,"RBAction",m.ExtType,"RBActionAttr")})}AfterSave(t,e){return p(this,null,function*(){})}}export{L as GPE_RadioBtns};
