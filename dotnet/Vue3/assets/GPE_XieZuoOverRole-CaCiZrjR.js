var g=Object.defineProperty;var d=(i,t,e)=>t in i?g(i,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):i[t]=e;var s=(i,t,e)=>d(i,typeof t!="symbol"?t+"":t,e);var m=(i,t,e)=>new Promise((r,a)=>{var l=o=>{try{n(e.next(o))}catch(c){a(c)}},h=o=>{try{n(e.throw(o))}catch(c){a(c)}},n=o=>o.done?r(o.value):Promise.resolve(o.value).then(l,h);n((e=e.apply(i,t)).next())});import{PageBaseGroupEdit as u}from"./PageBaseGroupEdit-IicyYiex.js";import{Node as D}from"./Node-DKGUVcK1.js";import{D as p,G as _,l as w}from"./entry/index-C6uBgOW5-1730430676707.js";import{GloComm as v}from"./GloComm-CmAl8MpM.js";import"./Help-D0bDMZWg.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./EntityNodeID-BCBbJNH2.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";class I extends u{constructor(){super("GPE_XieZuoOverRole");s(this,"Desc0",`
  
  #### 通用规则(默认)
     - 该配置项对协作模式下有效.
     - 默认规则是按照目前的协作模式计算，最后一个人发送到下一个节点上去，这是默认的规则。
     - 协作完成规则：是在什么条件下向下运动，提前结束当前节点的工作，用于投票、征求意见节点。
     - 是阻塞规则的扩展，可以删除掉符合规则条件下的指定的人员待办。
     - 用途是什么时间发送到下一个节点，默认的情况是所有人都完成了才发送到下一个节点。
  #### 帮助
  https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=11123807&doc_id=31094
  `);s(this,"Desc1",`
  #### 审核组件的立场-按照完成率.
     -  在审核组件里设置立场表达式，一般设置为@0=不同意@1=同意 ,  第1个选项为0,  第2个选项为1.
     -  率值有完成正确率与完成错误率两个，两个加在一起是100。 选项是1的是正确率。
      - 完成正确率 = 该节点的处理人数 / 当前完成工作的人数中表达是1的人数. 
      - 完成错误率 = 该节点的处理人数 / 当前完成工作的人数中表达是0的人数. 
     -  比如：有10个人协作处理，阈值设置60，60就是完成正确率.  当出现4个不同意或者6个同意的时候，就运动到下一个节点。
        #### 帮助
  https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=11123807&doc_id=31094
  `);s(this,"Desc2",`
  https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=11123807&doc_id=31094
  `);this.PageTitle="协作完成规则",this.Btns=[{pageNo:"1",list:["设置审核组件"]},{pageNo:"2",list:["设置审核组件"]}]}Init(){this.entity=new D,this.KeyOfEn="XieZuoOverRole",this.AddGroup("A","按数量计算"),this.Blank("0","通用规则(默认)",this.Desc0),this.SingleTB("1","审核组件的立场-完成率.","XieZuoOverRate",this.Desc1,"输入百分比阈值,比如80就是80%.",p.AppFloat),this.SingleTB("2","按照完成数","XieZuoOverRate",this.Desc2,"输入阈值",p.AppFloat),this.AddGroup("B","按照审核人员账号计算"),this.SingleTB("3","出现指定的任意一位人员账号.","XieZuoOverRate",this.Desc1,"多个人员用逗号分隔：比如：zhangsan,lisi,",p.AppFloat),this.SingleTB("4","出现指定的所有的人员账号.","XieZuoOverRate",this.Desc1,"多个人员用逗号分隔：比如：zhangsan,lisi,",p.AppFloat)}AfterSave(e,r){return m(this,null,function*(){})}BtnClick(e,r,a){const l=v.UrlEnOnly("TS.WF.Template.NodeWorkCheck",this.RefPKVal);return new _(w.OpenUrlByDrawer90,l)}}export{I as GPE_XieZuoOverRole};
