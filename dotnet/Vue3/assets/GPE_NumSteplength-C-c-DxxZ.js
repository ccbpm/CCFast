var u=Object.defineProperty;var c=(r,e,t)=>e in r?u(r,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):r[e]=t;var h=(r,e,t)=>c(r,typeof e!="symbol"?e+"":e,t);var g=(r,e,t)=>new Promise((s,n)=>{var p=i=>{try{m(t.next(i))}catch(o){n(o)}},a=i=>{try{m(t.throw(i))}catch(o){n(o)}},m=i=>i.done?s(i.value):Promise.resolve(i.value).then(p,a);m((t=t.apply(r,e)).next())});import{bb as S,ba as l,aL as f}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as A}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class M extends A{constructor(){super("GPE_NumSteplength");h(this,"Desc1",` 
  #### 帮助
   - 定义: 为了方便用户数据采集，使用+ - 符号来完成文本框的数值数据录入, 点击+ - 一次的增量或者减量数据.
   - 比如: 设置步长为10，没点击一次+ 文本框的数值就在原来的数据上增加10.
   - 禁用步长： 文本框不是使用 + - 图标 不启用.
   #### 效果图
   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/NumSteplength/Img/NumSteplength.png "屏幕截图.png")
  `);h(this,"Desc2",` 
  #### 帮助
  - 请输入步长参数,不能为0或负数.
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/NumSteplength/Img/NumSteplength.png "屏幕截图.png")

....`);this.PageTitle="步长输入"}Init(){this.entity=new S,this.KeyOfEn=l.DoWay,this.AddGroup("A","数值输入值限制"),this.Blank("0","禁用步长",this.Desc1),this.SingleTB("1","启用步长输入",l.Tag,this.Desc2,"格式:0.5")}BtnClick(t,s,n){if(t==s||t===n)throw new Error("Method not implemented.")}AfterSave(t,s){return g(this,null,function*(){var a;if(this.entity==null)return;const n=(a=this.entity)==null?void 0:a.AttrOfOper,p=new f(n);yield p.Retrieve(),t==="0"?p.SetPara("NumSteplength",""):p.SetPara("NumSteplength",this.entity.Tag)})}}export{M as GPE_NumSteplength};
