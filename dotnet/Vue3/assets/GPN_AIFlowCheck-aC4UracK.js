var N=Object.defineProperty;var h=(o,t,e)=>t in o?N(o,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):o[t]=e;var d=(o,t,e)=>h(o,typeof t!="symbol"?t+"":t,e);var s=(o,t,e)=>new Promise((i,n)=>{var y=r=>{try{a(e.next(r))}catch(l){n(l)}},c=r=>{try{a(e.throw(r))}catch(l){n(l)}},a=r=>r.done?i(r.value):Promise.resolve(r.value).then(y,c);a((e=e.apply(o,t)).next())});import{b5 as p,G as u,l as w}from"./entry/index-C6uBgOW5-1730430676707.js";import A from"./HttpHandler-DNmkgeyq.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./Events-D9tOL1Ad.js";class G extends p{constructor(){super("GPN_AIFlowCheck");d(this,"DeliveryWay1",`
    #### 说明
    - 接收人规则是指，谁可以处理指定节点的工作.
    - ccbpm提供了30多种接收人规则,是应用不同的场景,比如:绑定接收人，绑定部门，绑定岗位等.
    - AI可以帮助我们把每个节点的接收人规则给与最大的匹配与建议.
    `);d(this,"DeliveryWay2",`
    #### 说明
    - 接收人规则是指，谁可以处理指定节点的工作.
    - ccbpm提供了30多种接收人规则,是应用不同的场景,比如:绑定接收人，绑定部门，绑定岗位等.
    - AI可以帮助我们把每个节点的接收人规则给与最大的匹配与建议.
    `);this.PageTitle="AI流程检查"}Init(){return s(this,null,function*(){this.AddGroup("A","AI流程检查"),this.AddBlank("DeliveryWay","接受人规则",this.DeliveryWay1),this.Table("DeliveryWay.Nodes","内容输出",this.DeliveryWay2,!0,this.GenerNodes)})}GenerNodes(){return s(this,null,function*(){const e=new A("BP.WF.HttpHandler.WF_Admin_AI");e.AddPara("FlowNo",this.RequestVal("FlowNo"));const i=yield e.DoMethodReturnJson("AiFlow_NodesDeliveryWayGener");return JSON.stringify(i)})}GenerSorts(){return s(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,i,n,y,c){return s(this,null,function*(){if(e=="DeliveryWay.Nodes"){const a=new A("BP.WF.HttpHandler.WF_Admin_AI");a.AddPara("FlowNo",this.RequestVal("FlowNo"));const r=yield a.DoMethodReturnString("AiFlow_NodesDeliveryWaySave");return new u(w.Message,r)}})}}export{G as GPN_AIFlowCheck};
